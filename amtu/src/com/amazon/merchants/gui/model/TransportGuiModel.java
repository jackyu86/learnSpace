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

package com.amazon.merchants.gui.model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.amazon.merchants.Messages;
import com.amazon.merchants.account.AMTUAccount;
import com.amazon.merchants.account.MerchantAccountException;
import com.amazon.merchants.account.MerchantAccountManager;
import com.amazon.merchants.account.MerchantSiteGroup;
import com.amazon.merchants.database.DatabaseException;
import com.amazon.merchants.gui.view.AccountWizardFrame;
import com.amazon.merchants.gui.view.AddRegisteredSiteFrame;
import com.amazon.merchants.gui.view.UpdateViewInterface;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.system.ProxyConfig;
import com.amazon.merchants.system.ProxyException;
import com.amazon.merchants.transport.model.MainModel;
import com.amazon.merchants.transport.model.MerchantFeed;
import com.amazon.merchants.transport.model.MerchantReport;
import com.amazonaws.mws.MarketplaceWebServiceException;

public class TransportGuiModel {
    private static TransportGuiModel instance = new TransportGuiModel();
    private List<UpdateViewInterface> views = new ArrayList<UpdateViewInterface>();

    private List<AMTUAccount> accounts = MerchantAccountManager
        .getAccountList();
    private int currentAccountIndex = 0;
    private int currentSiteGroupIndex = 0;
    private List<MerchantFeed> feeds = new ArrayList<MerchantFeed>();
    private List<MerchantReport> reports = new ArrayList<MerchantReport>();

    private TableModel feedModel = new AbstractTableModel() {
        private static final long serialVersionUID = -6035697600683319639L;
        private String[] titles = new String[] {
            Messages.TransportGuiModel_0.toString(),
            Messages.TransportGuiModel_1.toString(),
            Messages.TransportGuiModel_2.toString(),
            Messages.TransportGuiModel_3.toString(),
            Messages.TransportGuiModel_4.toString() };


        @Override
        public String getColumnName(int col) {
            return titles[col];
        }


        public int getColumnCount() {
            return titles.length;
        }


        public int getRowCount() {
            return feeds.size();
        }


        public Object getValueAt(int row, int col) {
            Object val = null;
            MerchantFeed feed = null;
            if (row >= 0 && row < feeds.size()) {
                feed = feeds.get(row);
            }

            if (feed != null) {
                switch (col) {
                    case 0:
                        if (feed.getFile() != null) {
                            val = feed.getFile().getName();
                        }
                        break;
                    case 1:
                        val = feed.getFeedSubmissionId();
                        break;
                    case 2:
                        val = feed.getFeedProcessingStatus();
                        break;
                    case 3:
                        val = feed.getDateCreated();
                        break;
                    case 4:
                        MerchantReport report = feed.getProcessingReport();
                        if (feed.feedCancelled()) {
                            val = feed.getFeedProcessingStatus();
                        }
                        else {
                            val = report != null ? Messages.TransportGuiModel_5
                                .toString() : Messages.TransportGuiModel_6
                                .toString();
                        }
                        break;
                }
            }
            if (val == null) {
                return Messages.TransportGuiModel_7.toString();
            }
            return val;
        }
    };

    private TableModel reportModel = new AbstractTableModel() {
        private static final long serialVersionUID = 1381163043931439332L;
        private String[] titles = new String[] {
            Messages.TransportGuiModel_8.toString(),
            Messages.TransportGuiModel_9.toString(),
            Messages.TransportGuiModel_10.toString(),
            Messages.TransportGuiModel_11.toString() };


        @Override
        public String getColumnName(int col) {
            return titles[col];
        }


        public int getColumnCount() {
            return titles.length;
        }


        public int getRowCount() {
            return reports.size();
        }


        public Object getValueAt(int row, int col) {
            Object val = null;
            MerchantReport report = null;
            if (row >= 0 && row < reports.size()) {
                report = reports.get(row);
            }

            if (report != null) {
                switch (col) {
                    case 0:
                        if (report.getFile() != null) {
                            val = report.getFile().getName();
                        }
                        break;
                    case 1:
                        val = report.getReportId();
                        break;
                    case 2:
                        val = report.getReportType();
                        break;
                    case 3:
                        val = report.getDateReceived();
                        break;
                }
            }
            if (val == null) {
                return Messages.TransportGuiModel_12.toString();
            }
            return val;
        }
    };

    private DateRange feedDateRange = DateRange.TODAY;
    private DateRange reportDateRange = DateRange.TODAY;


    private TransportGuiModel() {
    }


    public static TransportGuiModel getInstance() {
        return instance;
    }


    /**
     * Add a view to this model
     *
     * @param view
     */
    public void addView(UpdateViewInterface view) {
        views.add(view);
        view.updateView();
        view.updateFeeds();
        view.updateReports();
        view.updateAccount();
        view.updateProxy();
        view.updateSiteGroup();
    }


    /**
     * Notify all views to update their contents
     */
    public void updateAllViews() {
        for (UpdateViewInterface view : views) {
            view.updateView();
        }
    }


    /**
     * Notify all views to update their logs
     */
    public void updateAllLogs(Object msg) {
        for (UpdateViewInterface view : views) {
            view.updateLog(msg);
        }
    }


    /**
     * Notify all views to update their feeds
     */
    public void updateAllFeeds() {
        for (UpdateViewInterface view : views) {
            view.updateFeeds();
        }
    }


    /**
     * Notify all views to update their reports
     */
    public void updateAllReports() {
        for (UpdateViewInterface view : views) {
            view.updateReports();
        }
    }


    /**
     * Notify all views to update their accounts
     */
    public void updateAllAccounts() {
        for (UpdateViewInterface view : views) {
            view.updateAccount();
            view.updateSiteGroup();
        }
    }


    /**
     * Get all available merchant accounts
     *
     * @return accounts
     */
    public List<AMTUAccount> getAccountList() {
        return accounts;
    }


    /**
     * Set the currently selected merchant account index
     *
     * @param index
     */
    public void setCurrentAccountIndex(int index) {
        if (currentAccountIndex != index) {
            currentAccountIndex = index;

            // force site group reset
            currentSiteGroupIndex = -1;
            setCurrentSiteGroupIndex(0);
        }
    }


    public void setCurrentAccount(AMTUAccount account) {
        for (int i = 0; i < accounts.size(); i++) {
            if (account.getMerchantId().equals(accounts.get(i).getMerchantId())) {
                setCurrentAccountIndex(i);
                return;
            }
        }
        setCurrentAccountIndex(0);
    }


    /**
     * Get the currently selected merchant account index
     *
     * @return currentAccountIndex
     */
    public int getCurrentAccountIndex() {
        return currentAccountIndex;
    }


    public void setCurrentSiteGroupIndex(int index) {
        if (currentSiteGroupIndex != index) {
            currentSiteGroupIndex = index;
            updateFeeds();
            updateReports();

            updateAllAccounts();
        }
    }


    public int getCurrentSiteGroupIndex() {
        return currentSiteGroupIndex;
    }


    /**
     * Get the currently selected merchant account
     *
     * @return currentAccount
     */
    public AMTUAccount getCurrentAccount() {
        return accounts.get(currentAccountIndex);
    }


    public MerchantSiteGroup getCurrentSiteGroup() {
        return getCurrentAccount().getSiteGroups().get(currentSiteGroupIndex);
    }


    public List<String> getCurrentAccountSiteGroupDirectories() {
        AMTUAccount account = getCurrentAccount();
        try {
            return MerchantSiteGroup
                    .getAllSiteGroupDirectoriesForAccount(account);
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().error(
                    String.format(Messages.Database_10.toString(),
                            e.getLocalizedMessage()), e);
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                    String.format(Messages.Database_10.toString(),
                            e.getLocalizedMessage()), e);
        }

        return null;
    }


    /**
     * Log message on the console view
     *
     * @param message
     */
    public void log(Object msg) {
        updateAllLogs(msg);
    }


    /**
     * Submitted feed table model
     *
     * @return feedModel
     */
    public TableModel getFeedModel() {
        return feedModel;
    }


    /**
     * Retrieved report table model
     *
     * @return reportModel
     */
    public TableModel getReportModel() {
        return reportModel;
    }


    /**
     * Get updated feed information from database
     *
     * @throws SQLException
     */
    public void updateFeeds() {
        try {
            feeds = MerchantFeed.pullMerchantFeedsForDateRange(
                getCurrentSiteGroup(), feedDateRange.getFromDate(),
                feedDateRange.getToDate());
            updateAllFeeds();
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().warn(
                Messages.TransportGuiModel_13.toString(), e);
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                String.format(Messages.Database_10.toString(),
                    e.getLocalizedMessage()), e);
        }
    }


    /**
     * Get updated feed information from database with a specific date range
     *
     * @throws SQLException
     */
    public void updateFeedsDateRange(DateRange feedDateRange)
        throws SQLException {
        this.feedDateRange = feedDateRange;
        updateFeeds();
    }


    /**
     * Get updated report information from database
     *
     * @throws SQLException
     */
    public void updateReports() {
        try {
            reports = MerchantReport.pullMerchantReportsForDateRange(
                getCurrentSiteGroup(), reportDateRange.getFromDate(),
                reportDateRange.getToDate());
            updateAllReports();
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().warn(
                Messages.TransportGuiModel_13.toString(), e);
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                String.format(Messages.Database_10.toString(),
                    e.getLocalizedMessage()), e);
        }
    }


    /**
     * Get updated report information from database with a specific date range
     *
     * @throws SQLException
     */
    public void updateReportsDateRange(DateRange reportDateRange)
        throws SQLException {
        this.reportDateRange = reportDateRange;
        updateReports();
    }


    /**
     * Saves a merchant account
     *
     * @param account
     * @throws MerchantAccountException
     * @throws IOException
     */
    public void saveMerchantAccount(AMTUAccount account)
        throws MerchantAccountException, IOException {

        account.save();

        MerchantAccountManager.setupMerchantAccounts();

        TransportLogger.register(account);

        currentAccountIndex = -1;
        setCurrentAccount(account);
    }


    /**
     * Delete an existing merchant account
     *
     * @param accountId
     * @throws MerchantAccountException
     */
    public void deleteMerchantAccount(AMTUAccount account)
        throws MerchantAccountException {
        if (account != null) {
            account.delete();
        }

        try {
            MerchantAccountManager.setupMerchantAccounts();
        }
        catch (IOException e) {
            TransportLogger.getSysErrorLogger().fatal(
                Messages.MerchantAccountManager_4.toString(), e);
            throw new MerchantAccountException(
                Messages.MerchantAccountManager_4.toString());
        }

        currentAccountIndex = -1;
        setCurrentAccountIndex(0);
    }


    public void deleteMerchantSiteGroup(MerchantSiteGroup siteGroup)
        throws MerchantAccountException {
        if (siteGroup != null) {
            AMTUAccount account = siteGroup.getParentAccount();
            account.deleteSiteGroup(siteGroup);
            account.save();
        }

        try {
            MerchantAccountManager.setupMerchantAccounts();
        }
        catch (IOException e) {
            TransportLogger.getSysErrorLogger().fatal(
                Messages.MerchantAccountManager_4.toString(), e);
            throw new MerchantAccountException(
                Messages.MerchantAccountManager_4.toString());
        }

        currentSiteGroupIndex = -1;
        setCurrentSiteGroupIndex(0);
    }


    /**
     * Validate given account information to ensure no duplicated merchant id,
     * alias or document transport directories are chosen
     *
     * @throws MerchantAccountException
     */
    public void validateAccount(AMTUAccount account)
        throws MerchantAccountException {
        account.validateAccount();
    }


    /**
     * Validate given mws credentials are correct and unique locally
     *
     * @param ma
     * @param proxy
     * 
     * @throws MarketplaceWebServiceException
     * @throws MerchantAccountException
     */
    public void validateCredentials(AMTUAccount account, ProxyConfig proxy)
        throws MarketplaceWebServiceException, MerchantAccountException {
        account.validateCredentialsNotInUse();
        account.validateCredentialsAuthenticate(proxy);
    }
    
    /**
     * Delete an existing proxy configuration
     *
     * @param accountId
     * @throws ProxyException
     */
    public void deleteProxyConfig()
        throws ProxyException {
        ProxyConfig.delete();
    }
    
    /**
     * Retrieves the current proxy settings
     * 
     * @return
     * @throws ProxyException 
     */
    public ProxyConfig getCurrentProxy() 
        throws ProxyException {
        try {
            return ProxyConfig.pullProxyConfiguration();
            
        } catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().fatal(
                    Messages.Database_10.toString(), e);
                throw new ProxyException(
                    Messages.Database_10.toString());
        } catch (SQLException e) {
            TransportLogger.getSysErrorLogger().fatal(
                    Messages.Database_10.toString(), e);
                throw new ProxyException(
                    Messages.Database_10.toString());
        }
    }


    /**
     * Show setup wizard
     */
    public void showAccountSetupWizard() {
        new AccountWizardFrame(this);
    }


    public void showAddRegisteredSiteWizard() {
        if (MainModel.getInstance().getRegisteredSitesFrame() != null) {
            MainModel.getInstance().getRegisteredSitesFrame().dispose();
        }
        MainModel.getInstance().setRegisteredSitesFrame(
            new AddRegisteredSiteFrame(this));
    }


    /**
     * Start the GUI frame through GUI
     */
    public void startFrame() {
        MainModel.getInstance().startFrame();
    }


    /**
     * Shutdown the application through GUI
     */
    public void shutdown() {
        MainModel.getInstance().shutdown();
    }


    /**
     * Shutdown the application through GUI
     */
    public void shutdown(boolean forced) {
        MainModel.getInstance().shutdown(forced);
    }


    /**
     * Shutdown the account setup wizard
     */
    public void shutdownWizard() {
        MainModel.getInstance().shutdownWizard();
    }


    /**
     * Shutdown frame
     */
    public void shutdownFrame() {
        MainModel.getInstance().getFrame().dispose();
        MainModel.getInstance().setFrame(null);
    }
}