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

package com.amazon.merchants.transport.retriever;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.PriorityBlockingQueue;

import com.amazon.merchants.Messages;
import com.amazon.merchants.account.AMTUAccount;
import com.amazon.merchants.database.Database;
import com.amazon.merchants.database.DatabaseException;
import com.amazon.merchants.gui.model.TransportGuiModel;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.transport.model.MerchantFeed;
import com.amazon.merchants.transport.model.MerchantReport;
import com.amazon.merchants.util.file.DirectoryEnum;
import com.amazon.merchants.util.file.IO;
import com.amazon.merchants.util.file.TransportUtil;
import com.amazonaws.mws.MarketplaceWebService;
import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.GetFeedSubmissionResultRequest;
import com.amazonaws.mws.model.GetFeedSubmissionResultResponse;

public class ProcReportRetrieverTask implements Runnable {
    private AMTUAccount account;

    private final String PROCESSING_REPORT_PREFIX = "PROCESSING";


    public ProcReportRetrieverTask(AMTUAccount account) {
        this.account = account;
    }


    @Override
    public void run() {
        synchronized (account.PROC_RETRIEVE_LOCK) {
            try {
                PriorityBlockingQueue<MerchantFeed> queue = account.feedsForProcessingReportRetrieval();
                account.setUnderProcRetrieval(true);

                while (account.hasProcessingReportRetrievalSpotsAvailable() && !queue.isEmpty()) {
                    MerchantFeed feed = queue.poll();
                    retrieveProcessingReport(feed);
                }
            }
            catch (SQLException e) {
                TransportLogger.getAcctErrorLogger(account).error(
                    "[" + account.getMerchantAlias() + "] " + Messages.StatusUpdaterService_0.toString(), e);
            }
            catch (DatabaseException e) {
                TransportLogger.getSysErrorLogger().error(
                    String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
            }
            finally {
                account.setUnderProcRetrieval(false);
            }
        }
    }


    private void retrieveProcessingReport(MerchantFeed feed) {
        String feedSubmissionId = feed.getFeedSubmissionId();
        TransportLogger.getAcctAuditLogger(account).info(
            "[" + account.getMerchantAlias() + "] [" + feed.getSiteGroup().getMerchantAlias() + "] "
                + Messages.ProcReportRetrieverTask_0.toString() + ", batch_id=" + feedSubmissionId);

        MarketplaceWebService mws = account.getMWSClient();
        final String merchantId = account.getMerchantId();

        try {
            OutputStream out = null;
            String requestId = null;
            MerchantReport report = null;
            try {
                final File PROCREPORTS = TransportUtil.getDirectory(feed.getSiteGroup().getDocumentTransportFolder(),
                    DirectoryEnum.PROCESSINGREPORTS);
                final File TEMP = TransportUtil.getDirectory(feed.getSiteGroup().getDocumentTransportFolder(),
                    DirectoryEnum.TEMP);

                String ext = feed.getFeedType().contains("_FLAT_FILE_") ? ".txt" : ".xml";

                String tempPath = TEMP.getAbsolutePath() + File.separator + PROCESSING_REPORT_PREFIX + feedSubmissionId
                    + ext;
                String path = PROCREPORTS.getAbsolutePath() + File.separator + PROCESSING_REPORT_PREFIX
                    + feedSubmissionId + ext;
                File tempFile = new File(tempPath);
                File file = new File(path);
                report = new MerchantReport(feed.getSiteGroup(), file, tempFile);

                // Check whether this report has successfully downloaded already
                if (report.getFile().exists()) {
                    // This may indicate the file downloaded but didn't ACK. If this is seen, investigate and improve
                    report.getFile().delete();
                }

                // Ensure a new temporary file can be created
                if (report.getTemporaryFile().exists()) {
                    report.getTemporaryFile().delete();
                }
                if (!report.getTemporaryFile().createNewFile()) {
                    throw new IOException(report.getTemporaryFile().getAbsolutePath());
                }

                out = new FileOutputStream(report.getTemporaryFile());

                GetFeedSubmissionResultRequest request = new GetFeedSubmissionResultRequest();
                request.setMerchant(merchantId);
                request.setFeedSubmissionId(feedSubmissionId);
                request.setFeedSubmissionResultOutputStream(out);

                GetFeedSubmissionResultResponse response = mws.getFeedSubmissionResult(request);

                if (response.isSetResponseMetadata()) {
                    requestId = response.getResponseMetadata().getRequestId();
                }

                out.close();

                account.addFeedResultRequest(feed.getFeedSubmissionId(), new Date());

                // Move the temporary file to the PROCESSINGREPORTS directory upon successful MD5 test
                if (!report.getTemporaryFile().renameTo(report.getFile())) {
                    throw new IOException();
                }

                report.setSiteGroup(feed.getSiteGroup());
                report.setReportSize(report.getFile().length() / 1024);
                report.setReportType("_GET_PROCESSING_REPORT_DATA_");
                report.setReportId(feedSubmissionId);
                report.setAcknowledged(true);

                Connection conn = null;
                try {
                    conn = Database.getUnmanagedConnection();
                    conn.setAutoCommit(false);
                    try {
                        report.save(conn);
                        feed.setProcessingReport(report);
                        feed.save(conn);
                        conn.commit();
                    }
                    catch (SQLException e) {
                        conn.rollback();
                        throw e;
                    }
                }
                finally {
                    if (conn != null) {
                        try {
                            conn.close();
                        }
                        catch (Exception e) {
                            // ignore
                        }
                    }
                }

                account.updateLastProcessingReportRetrieval();

                // Log success in audit logger
                Object[] messageArguments = { report.getFile().getName() };
                MessageFormat formatter = new MessageFormat("");
                formatter.applyPattern(Messages.ProcReportRetrieverTask_1.toString());
                TransportLogger.getAcctAuditLogger(account).info(
                    "[" + account.getMerchantAlias() + "] [" + feed.getSiteGroup().getMerchantAlias() + "] "
                        + formatter.format(messageArguments) + ", batch_id: " + feedSubmissionId + ", request_id="
                        + requestId);
            }
            catch (SQLException e) {
                TransportLogger.getAcctErrorLogger(account).error(
                    "[" + account.getMerchantAlias() + "] [" + feed.getSiteGroup().getMerchantAlias() + "] "
                        + Messages.ProcReportRetrieverTask_2.toString(), e);
            }
            catch (MarketplaceWebServiceException e) {
                TransportLogger.getAcctErrorLogger(account).error(
                    "[" + account.getMerchantAlias() + "] [" + feed.getSiteGroup().getMerchantAlias() + "] "
                        + Messages.ProcReportRetrieverTask_3.toString());
                TransportLogger.getAcctErrorLogger(account).error(
                    String.format(Messages.MarketplaceWebServiceException_0.toString(), e.getLocalizedMessage()), e);
            }
            catch (DatabaseException e) {
                TransportLogger.getSysErrorLogger().error(
                    String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
            }
            finally {
                IO.closeSilently(out);
                if (report != null) {
                    report.getTemporaryFile().delete();
                }
            }
        }
        catch (IOException e) {
            TransportLogger.getAcctErrorLogger(account).error(
                "[" + account.getMerchantAlias() + "] [" + feed.getSiteGroup().getMerchantAlias() + "] "
                    + Messages.ProcReportRetrieverTask_5.toString(), e);
        }

        // Notify GUI to update
        TransportGuiModel.getInstance().updateFeeds();
        TransportGuiModel.getInstance().updateReports();
    }
}