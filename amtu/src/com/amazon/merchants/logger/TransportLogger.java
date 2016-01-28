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

package com.amazon.merchants.logger;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.amazon.merchants.Messages;
import com.amazon.merchants.account.AMTUAccount;
import com.amazon.merchants.gui.model.TransportGuiModel;
import com.amazon.merchants.util.file.DirectoryUtil;
import com.amazon.merchants.util.file.FileEnum;
import com.amazon.merchants.util.file.TransportUtil;

public class TransportLogger {
    private static final Logger root = Logger.getRootLogger();
    private static final Logger transport = Logger.getLogger("transport");
    private static final Logger httpclient = Logger.getLogger("httpclient");
    private static final Logger mwslog = Logger.getLogger("com.amazonaws.mws.MarketplaceWebServiceClient");
    private static final Logger syserror = Logger.getLogger("transport.syserror");
    private static final Logger sysaudit = Logger.getLogger("transport.sysaudit");
    private static final PatternLayout pattern = new PatternLayout("%d [%t] %C - %m%n");
    private static final PatternLayout patternGui = new PatternLayout("%d{MM-dd-yyyy HH:mm:ss} - %m%n");
    private static final PatternLayout patternConsole = new PatternLayout("%m%n");
    private static final String SYS_ERROR_LOG_NAME = "sys_error.log";
    private static final String SYS_AUDIT_LOG_NAME = "sys_audit.log";
    private static final String MWS_ERROR_LOG_NAME = "mws_error.log";

    // Number of days before the log is deleted
    private static long maxBackupIndex = 14;


    /**
     * Initialize system loggers and appenders
     *
     * @param model
     * @throws IOException
     */
    public static void init(TransportGuiModel model) throws IOException {
        // Set logging level
        root.setLevel(Level.ERROR); // Suppress MWS Java Client DEBUG logs
        httpclient.setLevel(Level.ERROR); // Suppress HTTP Client DEBUG logs
        transport.setLevel(Level.INFO); // AMTU Logging Level
        mwslog.setLevel(Level.INFO);

        final File SYSERROR = new File(DirectoryUtil.makeSyslogPath(SYS_ERROR_LOG_NAME));
        final File SYSAUDIT = new File(DirectoryUtil.makeSyslogPath(SYS_AUDIT_LOG_NAME));
        final File MWSERROR = new File(DirectoryUtil.makeSyslogPath(MWS_ERROR_LOG_NAME));

        // Remove logs that are older than 14 days
        final File parent = SYSERROR.getParentFile();
        final File[] logs = parent.listFiles();

        if (parent.exists()) {
            long todayLong = new Date().getTime();
            long maxBackupIndexLong = maxBackupIndex * 24 * 60 * 60 * 1000;
            for (File log : logs) {
                if (todayLong - log.lastModified() > maxBackupIndexLong) {
                    if (!log.delete()) {
                        throw new IOException(Messages.TransportLogger_0.toString() + " "
                            + Messages.TransportLogger_1.toString() + log.getAbsolutePath());
                    }
                }
            }
        }

        // System log appenders
        DailyRollingFileAppender errorAppender = new DailyRollingFileAppender(pattern, SYSERROR.getAbsolutePath(),
            "'.'yyyy-MM-dd");
        DailyRollingFileAppender auditAppender = new DailyRollingFileAppender(pattern, SYSAUDIT.getAbsolutePath(),
            "'.'yyyy-MM-dd");
        DailyRollingFileAppender mwsAppender = new DailyRollingFileAppender(pattern, MWSERROR.getAbsolutePath(),
            "'.'yyyy-MM-dd");

        syserror.addAppender(errorAppender);
        sysaudit.addAppender(auditAppender);
        mwslog.addAppender(mwsAppender);

        // GUI component appender
        transport.addAppender(new TransportGuiAppender(patternGui, model));
    }


    /**
     * Initialize command line logging utility
     */
    public static void init() {
        // Console appender
        ConsoleAppender consoleAppender = new ConsoleAppender(patternConsole);

        sysaudit.addAppender(consoleAppender);
        syserror.addAppender(consoleAppender);
        mwslog.addAppender(consoleAppender);
    }


    /**
     * Register loggers and appenders for all initialized merchant accounts
     *
     * @param accounts
     * @throws IOException
     */
    public static void init(List<AMTUAccount> accounts) throws IOException {
        // Account specific appenders
        for (AMTUAccount account : accounts) {
            register(account);
        }
    }


    /**
     * Register loggers and appenders for a single merchant account
     *
     * @param account
     * @throws IOException
     */
    public static void register(AMTUAccount account) throws IOException {
        File ERROR = TransportUtil.getLogFile(account.getDocumentTransport(), FileEnum.ERROR);
        File AUDIT = TransportUtil.getLogFile(account.getDocumentTransport(), FileEnum.AUDIT);

        // Remove logs that are older than $maxBackupIndex days
        File parent = AUDIT.getParentFile();
        File[] logs = parent.listFiles();

        if (logs != null) {
            long todayLong = new Date().getTime();
            long maxBackupIndexLong = maxBackupIndex * 24 * 60 * 60 * 1000;

            for (File log : logs) {
                if (todayLong - log.lastModified() > maxBackupIndexLong) {
                    if (!log.delete()) {
                        throw new IOException(Messages.TransportLogger_0.toString() + " "
                            + Messages.TransportLogger_1.toString() + log.getAbsolutePath());
                    }
                }
            }
        }

        Logger audit = Logger.getLogger("transport.m" + getUniqueIdentifier(account) + "_audit");
        Logger error = Logger.getLogger("transport.m" + getUniqueIdentifier(account) + "_error");

        audit.removeAllAppenders();
        error.removeAllAppenders();

        DailyRollingFileAppender auditAppender = new DailyRollingFileAppender(pattern, AUDIT.getAbsolutePath(),
            "'.'yyyy-MM-dd");
        DailyRollingFileAppender errorAppender = new DailyRollingFileAppender(pattern, ERROR.getAbsolutePath(),
            "'.'yyyy-MM-dd");

        audit.addAppender(auditAppender);
        error.addAppender(errorAppender);
    }


    /**
     * Get system error logger
     *
     * @return
     */
    public static Logger getSysErrorLogger() {
        return syserror;
    }


    /**
     * Get system audit logger
     *
     * @return
     */
    public static Logger getSysAuditLogger() {
        return sysaudit;
    }


    /**
     * Get account audit logger
     *
     * @param account
     * @return
     */
    public static Logger getAcctAuditLogger(AMTUAccount account) {
        return Logger.getLogger("transport.m" + getUniqueIdentifier(account) + "_audit");
    }


    /**
     * Get account error logger
     *
     * @param account
     * @return
     */
    public static Logger getAcctErrorLogger(AMTUAccount account) {
        return Logger.getLogger("transport.m" + getUniqueIdentifier(account) + "_error");
    }


    private static String getUniqueIdentifier(AMTUAccount account) {
        return account.getMerchantId();
    }
}