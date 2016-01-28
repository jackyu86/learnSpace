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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;

import com.amazon.merchants.Messages;
import com.amazon.merchants.account.AMTUAccount;
import com.amazon.merchants.account.MerchantSiteGroup;
import com.amazon.merchants.database.Database;
import com.amazon.merchants.database.DatabaseException;
import com.amazon.merchants.gui.model.TransportGuiModel;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.transport.model.MerchantReport;
import com.amazon.merchants.util.file.IO;
import com.amazonaws.mws.MarketplaceWebService;
import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.GetReportRequest;
import com.amazonaws.mws.model.GetReportResponse;
import com.amazonaws.mws.model.IdList;
import com.amazonaws.mws.model.ResponseMetadata;
import com.amazonaws.mws.model.UpdateReportAcknowledgementsRequest;
import com.amazonaws.mws.model.UpdateReportAcknowledgementsResponse;

public class ReportRetrieverTask implements Runnable {
    private AMTUAccount account;


    public ReportRetrieverTask(AMTUAccount account) {
        this.account = account;
    }


    @Override
    public void run() {
        synchronized (account.RETRIEVE_LOCK) {
            account.setUnderRetrieval(true);

            while (account.hasReportsToRetrieve()) {
                MerchantReport report = account.getReportForRetrieval();
                retrieveReport(report);

                account.updateLastReportRetrieval();
                TransportGuiModel.getInstance().updateAllViews();
            }
            account.setUnderRetrieval(false);
        }
    }


    private void retrieveReport(MerchantReport report) {
        MerchantSiteGroup siteGroup = report.getSiteGroup();
        AMTUAccount account = siteGroup.getParentAccount();

        MarketplaceWebService mws = account.getMWSClient();

        Object[] messageArguments = { report.getFile().getName() };
        MessageFormat formatter = new MessageFormat("");
        formatter.applyPattern(Messages.ReportRetrieverTask_0.toString());
        TransportLogger.getAcctAuditLogger(account).info(
            "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                + formatter.format(messageArguments));

        final String merchantId = account.getMerchantId();

        String requestId = null;
        OutputStream out = null;
        try {
            // Check whether this report has successfully downloaded already
            if (report.getFile().exists()) {
                // This may indicate the file downloaded but didn't ACK. If this is seen, investigate and improve
                throw new IOException(report.getFile().getAbsolutePath());
            }

            // Ensure a new temporary file can be created
            if (report.getTemporaryFile().exists()) {
                report.getTemporaryFile().delete();
            }
            if (!report.getTemporaryFile().createNewFile()) {
                throw new IOException(report.getTemporaryFile().getAbsolutePath());
            }

            GetReportRequest request = new GetReportRequest();
            request.setMerchant(merchantId);
            request.setReportId(report.getReportId());

            out = new FileOutputStream(report.getTemporaryFile());

            // Define output stream to the file that was previously created
            request.setReportOutputStream(out);

            GetReportResponse response = mws.getReport(request);

            report.setDateReceived();
            if (response.isSetResponseMetadata()) {
                ResponseMetadata responseMetadata = response.getResponseMetadata();
                if (responseMetadata.isSetRequestId()) {
                    requestId = responseMetadata.getRequestId();
                }
            }

            out.close();
            // Move the temporary file to the REPORT directory upon successful MD5 test
            if (!report.getTemporaryFile().renameTo(report.getFile())) {
                throw new IOException(report.getTemporaryFile().getAbsolutePath());
            }

            report.setReportSize(report.getFile().length() / 1024);

            // Write success to audit logger
            messageArguments = new Object[] { report.getFile().getName() };
            formatter = new MessageFormat("");
            formatter.applyPattern(Messages.ReportRetrieverTask_1.toString());
            TransportLogger.getAcctAuditLogger(account).info(
                "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                    + formatter.format(messageArguments) + ", requestId=" + requestId);
        }
        catch (IOException e) {
            messageArguments = new Object[] { e.getLocalizedMessage() };
            formatter = new MessageFormat("");
            formatter.applyPattern(Messages.ReportRetrieverTask_2.toString());
            TransportLogger.getAcctErrorLogger(account).error(
                "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                    + formatter.format(messageArguments), e);
            return;
        }
        catch (MarketplaceWebServiceException e) {
            messageArguments = new Object[] { report.getReportId() };
            formatter = new MessageFormat("");
            formatter.applyPattern(Messages.ReportRetrieverTask_4.toString());
            TransportLogger.getAcctErrorLogger(account).error(
                "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                    + formatter.format(messageArguments));
            TransportLogger.getAcctErrorLogger(account).error(
                String.format(Messages.MarketplaceWebServiceException_0.toString(), e.getLocalizedMessage()), e);
            return;
        }
        finally {
            IO.closeSilently(out);
            report.getTemporaryFile().delete();
        }

        // Report acknowledgment
        try {
            IdList list = new IdList();
            list.getId().add(report.getReportId());

            UpdateReportAcknowledgementsRequest request = new UpdateReportAcknowledgementsRequest();
            request.setMerchant(merchantId);
            request.setReportIdList(list);

            UpdateReportAcknowledgementsResponse response = mws.updateReportAcknowledgements(request);

            if (response.isSetUpdateReportAcknowledgementsResult()) {
                if (response.isSetResponseMetadata()) {
                    ResponseMetadata responseMetadata = response.getResponseMetadata();
                    if (responseMetadata.isSetRequestId()) {
                        requestId = responseMetadata.getRequestId();
                    }
                }
            }

            report.setAcknowledged(true);

            messageArguments = new Object[] { report.getFile().getName() };
            formatter = new MessageFormat("");
            formatter.applyPattern(Messages.ReportRetrieverTask_5.toString());
            TransportLogger.getAcctAuditLogger(account).info(
                "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                    + formatter.format(messageArguments) + ", requestId=" + requestId);
        }
        catch (MarketplaceWebServiceException e) {
            messageArguments = new Object[] { report.getReportId() };
            formatter = new MessageFormat("");
            formatter.applyPattern(Messages.ReportRetrieverTask_6.toString());
            TransportLogger.getAcctErrorLogger(account).error(
                "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                    + formatter.format(messageArguments));
            TransportLogger.getAcctErrorLogger(account).error(
                String.format(Messages.MarketplaceWebServiceException_0.toString(), e.getLocalizedMessage()), e);
            return;
        }

        // Store report meta data into database
        try {
            Connection conn = null;
            try {
                conn = Database.getUnmanagedConnection();
                conn.setAutoCommit(false);
                try {
                    report.save(conn);
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

            messageArguments = new Object[] { report.getFile().getName() };
            formatter = new MessageFormat("");
            formatter.applyPattern(Messages.ReportRetrieverTask_7.toString());
            TransportLogger.getAcctAuditLogger(account).info(
                "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                    + formatter.format(messageArguments));
        }
        catch (SQLException e) {
            messageArguments = new Object[] { report.getReportId() };
            formatter = new MessageFormat("");
            formatter.applyPattern(Messages.ReportRetrieverTask_8.toString());
            TransportLogger.getAcctErrorLogger(account).error(
                "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                    + formatter.format(messageArguments), e);
            return;
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
            return;
        }

        // Notify GUI to update
        TransportGuiModel.getInstance().updateReports();
    }
}