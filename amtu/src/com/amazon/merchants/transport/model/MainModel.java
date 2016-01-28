/*
Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License"). You may
not use this file except in compliance with the License. A copy of the
License is located at

    http://aws.amazon.com/apache2.0/

or in the "license" file accompanying this file. This file is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.
 */

package com.amazon.merchants.transport.model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.amazon.merchants.Messages;
import com.amazon.merchants.Properties;
import com.amazon.merchants.account.MerchantAccountException;
import com.amazon.merchants.account.MerchantAccountManager;
import com.amazon.merchants.database.Database;
import com.amazon.merchants.database.DatabaseException;
import com.amazon.merchants.executor.ExecutorFactory;
import com.amazon.merchants.executor.TransportScheduler;
import com.amazon.merchants.gui.model.TransportGuiModel;
import com.amazon.merchants.gui.view.AccountWizardFrame;
import com.amazon.merchants.gui.view.AddRegisteredSiteFrame;
import com.amazon.merchants.gui.view.TransportGuiFrame;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.services.DispatcherService;
import com.amazon.merchants.services.RetrieverService;
import com.amazon.merchants.services.StatusUpdaterService;
import com.amazon.merchants.transport.util.OSXAdapter;
import com.amazon.merchants.util.file.FileLockException;
import com.amazon.merchants.util.file.FileLockManager;
import com.amazon.merchants.util.file.QAUtility;

public class MainModel {
    private static MainModel instance = new MainModel();

    // Application Meta Data
    public static final String APP_NAME = "Amazon Merchant Transport Utility"; //$NON-NLS-1$
    public static final String APP_NAME_DISPLAY = Messages.MainModel_0
            .toString();
    public static final String APP_VERSION = Properties
            .getString("buildVersion"); //$NON-NLS-1$
    public static final String LOCK_FILE_NAME = "AMTUSysLockFile.dat"; //$NON-NLS-1$

    private Database db = Database.getInstance();
    private TransportScheduler ts = TransportScheduler.getInstance();
    private ThreadPoolExecutor de = ExecutorFactory.getDispatcherExecutor();
    private ThreadPoolExecutor re = ExecutorFactory.getRetrieveExecutor();
    private TransportGuiModel guiModel = TransportGuiModel.getInstance();
    private final int NUMSCHEDULEDSERVICES = 4;
    private int option;
    private FileLockManager lockFile = new FileLockManager();

    private JFrame frame = null;
    private JDialog registeredSites = null;

    private AtomicBoolean shuttingDown = new AtomicBoolean();


    private MainModel() {
    }

    public static final int GUI = 0;
    public static final int DAEMON = 1;


    public static MainModel getInstance() {
        return instance;
    }


    public boolean isGUI() {
        return option == GUI;
    }


    public void start(int option) {
        this.option = option;

        // Application Information
        System.setProperty("app.name", APP_NAME); //$NON-NLS-1$
        System.setProperty("app.name.display", APP_NAME_DISPLAY); //$NON-NLS-1$
        System.setProperty("app.version", APP_VERSION); //$NON-NLS-1$

        exitIfSecondInstanceOfAMTU();

        try {
            // init shutdown thread
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    prepareForShutdown();
                }
            });
            // Initialize global loggers
            TransportLogger.init(guiModel);
            // Start database instance
            db.start();
            // Start Scheduler & Executors
            ts.start(NUMSCHEDULEDSERVICES);
            int pc_delay_in_min = QAUtility.getProcessingReportDelay();
            int rt_delay_in_min = QAUtility.getReportDelay();

            // Schedule recurring services
            ts.schedule(new DispatcherService(), 15, TimeUnit.SECONDS);
            ts.schedule(new StatusUpdaterService(), pc_delay_in_min,
                    TimeUnit.MINUTES);
            ts.schedule(new RetrieverService(), rt_delay_in_min,
                    TimeUnit.MINUTES);

            // Account Manager
            MerchantAccountManager.setupMerchantAccounts();
            // Initialize Global and Account Specific Loggers
            TransportLogger.init(MerchantAccountManager.getAccountList());

            checkForProxy();

            // Start GUI or command line
            if (isGUI()) {
                startFrame();
                setOSXCloseCommand();
            }
            else {
                startCommandLine();
            }
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().fatal(e.getLocalizedMessage(),
                    e);
            if (isGUI()) {
                // Display error message to user if there is a database error
                JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
                        Messages.Database_8.toString(),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (MerchantAccountException e) {
            // Start the account setup wizard
            if (isGUI()) {
                new AccountWizardFrame(TransportGuiModel.getInstance());
            }
            else {
                TransportLogger.getSysErrorLogger().error(
                        Messages.MainModel_1.toString(), e);
                System.err.println(Messages.MainModel_1.toString());

                // Shutdown application if no merchant account is found
                shutdown();
            }
        }
        catch (IOException e) {
            TransportLogger.getSysErrorLogger().fatal(e.getLocalizedMessage(),
                    e);
        }
    }


    public void setRegisteredSitesFrame(AddRegisteredSiteFrame registeredSites) {
        this.registeredSites = registeredSites;
    }


    public JDialog getRegisteredSitesFrame() {
        return registeredSites;
    }


    public void setFrame(TransportGuiFrame frame) {
        this.frame = frame;
    }


    public JFrame getFrame() {
        return frame;
    }


    /**
     * Show GUI Frame
     */
    public void startFrame() {
        if (frame == null) {
            frame = new TransportGuiFrame(guiModel);
            Object[] messageArguments = {
                    System.getProperty("app.name.display"), //$NON-NLS-1$
                    System.getProperty("app.version") }; //$NON-NLS-1$
            MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
            formatter.applyPattern(Messages.MainModel_12.toString());
            TransportLogger.getSysAuditLogger().info(
                    formatter.format(messageArguments));
        }
    }


    /**
     * Start command line utility
     */
    public void startCommandLine() {
        Object[] messageArguments = { System.getProperty("app.name.display"), //$NON-NLS-1$
                System.getProperty("app.version") }; //$NON-NLS-1$
        MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
        formatter.applyPattern(Messages.MainModel_13.toString());
        TransportLogger.getSysAuditLogger().info(
                formatter.format(messageArguments));
    }


    /**
     * Shutdown account setup wizard
     */
    public void shutdownWizard() {
        if (frame == null) {
            shutdown();
        }
    }


    /**
     * regular shutdown
     */
    public void shutdown() {
        shutdown(false);
    }


    private void showShuttingDownDialog() {
        if (isGUI()) {
            displayInfoMessage(Messages.MainModel_2.toString(),
                    Messages.MainModel_3.toString(), false);
        }
    }


    /**
     * Wait for processing to stop, then shuts down the database, and releases
     * the lockfile
     *
     * @return true: if this call to prepareForShutdown() drove the shutdown.
     *         false: if this method had already been called (perhaps on another
     *         thread)
     */

    public boolean lockForShutdown() {
        if (shuttingDown.compareAndSet(false, true)) {
            return true;
        }

        return false;
    }


    public boolean prepareForShutdown() {
        // make sure this only gets called once across all threads

        if (!lockForShutdown()) {
            return false;
        }

        int waitCount = 0;
        TransportLogger.getSysAuditLogger().info(
                Messages.MainModel_14.toString());
        if (frame != null) {
            frame.dispose();
        }
        try {
            ts.shutdown();
            re.shutdown();
            de.shutdown();

            // Ensure all tasks have been completed before shutting down the
            // database instance
            while (!(re.isTerminated() && de.isTerminated())) {
                waitCount++;
                if (waitCount == 2) {
                    showShuttingDownDialog();
                }
                Thread.sleep(5000); // Retry after 5 seconds
            }

            db.shutdown();
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().fatal(
                    Messages.MainModel_15.toString(), e);
        }
        catch (InterruptedException e) {
            TransportLogger.getSysErrorLogger().fatal(
                    Messages.MainModel_15.toString(), e);
        }
        Object[] messageArguments = { System.getProperty("app.name.display"), //$NON-NLS-1$
                System.getProperty("app.version") }; //$NON-NLS-1$
        MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
        formatter.applyPattern(Messages.MainModel_16.toString());
        TransportLogger.getSysAuditLogger().info(
                formatter.format(messageArguments));

        lockFile.unlock();

        return true;
    }


    /**
     * Shutdown the application
     *
     * Allow all tasks to finish before shutting down the executors and the
     * database
     */

    // ask the user if they really want to quit
    public boolean promptUserWantsShutdown() {
        boolean result = true;

        if (isGUI()) {
            String msg = Messages.MainModel_4.toString();
            String title = Messages.MainModel_5.toString();
            int ans = JOptionPane.showConfirmDialog(null, msg, title,
                    JOptionPane.OK_CANCEL_OPTION);

            if (ans == JOptionPane.CANCEL_OPTION) {
                result = false;
            }
        }

        return result;

    }


    public void shutdown(boolean forced) {
        // see if user really wants to shutdown
        if (forced == false && promptUserWantsShutdown() == false) {
            return;
        }

        // if prepare for shutdown returns false, it means another thread is
        // already shutting us down, so we don't need to

        if (prepareForShutdown()) {
            System.exit(0);
        }
    }


    // this is called when the user presses ctrl-q on a mac, or chooses the mac
    // exit menu option
    public boolean macShutdown() {
        boolean result = promptUserWantsShutdown();

        if (result) {
            prepareForShutdown();
        }

        return result;
    }


    // check file lock to see if another instance is running.
    // if another instance is running, then exit immediately

    private void exitIfSecondInstanceOfAMTU() {
        // if another version is running, stop immediately
        String exceptionMessage = null;

        try {
            lockFile.lock(LOCK_FILE_NAME);
        }
        catch (FileLockException fle) {
            exceptionMessage = Messages.MainModel_6.toString()
                    + fle.getCause().toString();
        }

        // if we didn't get the lock, then we have to exit
        if (lockFile.isUnlocked()) {
            String dialogMessage;
            String dialogTitle = Messages.MainModel_7.toString()
                    + APP_NAME_DISPLAY;

            if (exceptionMessage != null) {
                dialogMessage = exceptionMessage;
            }
            else {
                dialogMessage = Messages.MainModel_8.toString();
            }

            displayErrorMessage(dialogTitle, dialogMessage, true);

            System.exit(exceptionMessage == null ? 100 : 200);
        }

    }


    /**
     * Checks whether there is a system-defined proxy for HTTPS connections. If
     * one is found, it will use that found proxy for MWS and update
     * connections.
     */
    private void checkForProxy() {
        System.setProperty("java.net.useSystemProxies", "true"); //$NON-NLS-1$ //$NON-NLS-2$
        TransportLogger.getSysAuditLogger().debug("Detecting system proxies"); //$NON-NLS-1$
        List<Proxy> l = null;
        try {
            l = ProxySelector.getDefault().select(
                    new URI("https://mws.amazonservices.com")); //$NON-NLS-1$
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (l != null) {
            for (Proxy proxy : l) {
                InetSocketAddress addr = (InetSocketAddress) proxy.address();

                if (addr == null) {
                    TransportLogger.getSysAuditLogger().debug(
                            "No system proxies found"); //$NON-NLS-1$
                }
                else {
                    TransportLogger.getSysAuditLogger().info(
                            "Proxy hostname: " + addr.getHostName()); //$NON-NLS-1$
                    if(System.getProperty("https.proxyHost") == null) 
                        System.setProperty("https.proxyHost", addr.getHostName()); //$NON-NLS-1$
                    TransportLogger.getSysAuditLogger().info(
                            "Proxy Port: " + addr.getPort()); //$NON-NLS-1$
                    if(System.getProperty("https.proxyPort") == null)
                        System.setProperty("https.proxyPort", //$NON-NLS-1$
                            Integer.toString(addr.getPort()));
                    break;
                }
            }
        }
    }


    // utilities for writing user message to output

    private void displayMessage(final String title, final String message,
            final int type, boolean blocking) {
        if (!blocking) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, message, title, type);
                }
            };
            thread.start();
        }
        else {
            JOptionPane.showMessageDialog(null, message, title, type);
        }

    }


    public void displayErrorMessage(String title, String message,
            boolean blocking) {
        if (isGUI()) {
            displayMessage(title, message, JOptionPane.ERROR_MESSAGE, blocking);
        }
        else {
            System.err.println(Messages.MainModel_9.toString() + title);
            System.err.println(message);
        }
    }


    public void displayInfoMessage(String title, String message,
            boolean blocking) {
        if (isGUI()) {
            displayMessage(title, message, JOptionPane.INFORMATION_MESSAGE,
                    blocking);
        }
        else {
            System.out.println(Messages.MainModel_10.toString() + title);
            System.out.println(message);
        }
    }


    /**
     * sets up the method to call when a mac user presses ctrl-q or chooses quit
     * from the menu
     */
    private void setOSXCloseCommand() {
        if (OSXAdapter.isMacOSX()) {
            try {
                OSXAdapter.setQuitHandler(
                        this,
                        getClass().getDeclaredMethod(
                                "macShutdown", (Class[]) null)); //$NON-NLS-1$
            }
            catch (Exception e) {
                System.err.println(Messages.MainModel_11.toString());
                e.printStackTrace();
            }
        }
    }
}