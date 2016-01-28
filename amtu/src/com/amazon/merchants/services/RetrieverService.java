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

package com.amazon.merchants.services;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.amazon.merchants.Messages;
import com.amazon.merchants.account.AMTUAccount;
import com.amazon.merchants.account.MerchantAccountManager;
import com.amazon.merchants.account.MerchantSiteGroup;
import com.amazon.merchants.executor.ExecutorFactory;
import com.amazon.merchants.gui.model.TransportGuiModel;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.mws.MWSClient;
import com.amazon.merchants.transport.model.MerchantReport;
import com.amazon.merchants.transport.model.ReportTypeEnum;
import com.amazon.merchants.transport.retriever.ReportRetrieverTask;
import com.amazon.merchants.util.file.DirectoryEnum;
import com.amazon.merchants.util.file.TransportUtil;
import com.amazonaws.mws.MarketplaceWebService;
import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.GetReportListRequest;
import com.amazonaws.mws.model.GetReportListResponse;
import com.amazonaws.mws.model.GetReportListResult;
import com.amazonaws.mws.model.ReportInfo;
import com.amazonaws.mws.model.ResponseMetadata;
import com.amazonaws.mws.model.TypeList;

public class RetrieverService extends TransportService {
    private static final int MAX_ATTEMPTS = 3;
    private static final int RETRY_SLEEP = 15000;


    public RetrieverService() {
        super(TransportServiceEnum.RETRIEVE);
    }


    @Override
    public void run() {
        List<AMTUAccount> accountList = MerchantAccountManager.getAccountList();
        for (AMTUAccount account : accountList) {
            synchronized (account.REPORT_STATUS_UPDATE_LOCK) {
                processReportCheck(account);

                processReportRetrieval(account);
            }
        }

        TransportGuiModel.getInstance().updateAllViews();
    }


    private void processReportCheck(AMTUAccount account) {
        // skip if we're within the minimum retrieval interval and that we're not already downloading
        if (!account.isEligibleForReportRetrieval() || account.isUnderRetrieval()) {
            return;
        }

        MarketplaceWebService mws = account.getMWSClient();

        MerchantSiteGroup defaultSiteGroup = account.getDefaultSiteGroup();

        final File REPORTS = TransportUtil.getDirectory(defaultSiteGroup.getDocumentTransportFolder(),
            DirectoryEnum.REPORTS);
        final File TEMP = TransportUtil.getDirectory(defaultSiteGroup.getDocumentTransportFolder(),
            DirectoryEnum.TEMP);

        TypeList rtl = new TypeList();
        for (ReportTypeEnum t : ReportTypeEnum.values()) {
            rtl.getType().add(t.getEnumeration());
        }

        int attempts = 0;
        boolean retry = false;
        do {
            retry = false;
            ++attempts;

            GetReportListRequest request = null;
            GetReportListResponse response = null;
            try {
                request = new GetReportListRequest();
                request.setMerchant(account.getMerchantId());
                // Detect only those that have not been acknowledged
                request.setAcknowledged(Boolean.FALSE);
                // Detect only types that are scheduled
                request.setReportTypeList(rtl);

                response = mws.getReportList(request);

                List<ReportInfo> reportInfoList = new ArrayList<ReportInfo>();
                String requestId = null;

                if (response.isSetGetReportListResult()) {
                    GetReportListResult getReportListResult = response.getGetReportListResult();
                    reportInfoList.addAll(getReportListResult.getReportInfoList());
                }
                if (response.isSetResponseMetadata()) {
                    ResponseMetadata responseMetadata = response.getResponseMetadata();
                    if (responseMetadata.isSetRequestId()) {
                        requestId = responseMetadata.getRequestId();
                    }
                }

                int pendingReports = reportInfoList.size();
                Object[] messageArguments = { new Integer(pendingReports) };
                MessageFormat formatter = new MessageFormat("");
                formatter.applyPattern(pendingReports == 1 ? Messages.RetrieverService_0.toString()
                    : Messages.RetrieverService_1.toString());
                TransportLogger.getAcctAuditLogger(account).debug(
                    "[" + account.getMerchantAlias() + "] [" + defaultSiteGroup.getMerchantAlias() + "] "
                        + formatter.format(messageArguments) + ", requestId=" + requestId);

                for (ReportInfo info : reportInfoList) {
                    if (account.reportQueuedForDownload(info.getReportId())) {
                        continue;
                    }
                    String prefix = ReportTypeEnum.getReportNameFromType(info.getReportType());
                    String ext = ReportTypeEnum.getExtensionFromType(info.getReportType());

                    // Build file paths to use
                    String tempPath = TEMP.getAbsolutePath() + File.separator + prefix + info.getReportId() + "."
                        + ext;
                    String path = REPORTS.getAbsolutePath() + File.separator + prefix + info.getReportId() + "."
                        + ext;
                    File temporaryFile = new File(tempPath);
                    File file = new File(path);

                    // Queue report for retrieval
                    account.queueReportForRetrieval(info.getReportId(),
                        new MerchantReport(account.getDefaultSiteGroup(), file, temporaryFile, info));
                }

                // Update account last connection
                account.updateLastConnection();
                TransportGuiModel.getInstance().updateAllViews();
            }
            catch (MarketplaceWebServiceException e) {
                String mwsRequestId = "MWS Request ID unknown";
                if (response != null) {
                    if (response.isSetResponseMetadata()) {
                        mwsRequestId = response.getResponseMetadata().getRequestId();
                    }
                }
                retry = true;
                TransportLogger.getAcctErrorLogger(account).error(
                    Messages.RetrieverService_2.toString() + " - " + mwsRequestId);
                TransportLogger
                    .getAcctErrorLogger(account)
                    .error(
                        String
                            .format(Messages.MarketplaceWebServiceException_0.toString(), e.getLocalizedMessage()),
                        e);

                if (attempts < MAX_ATTEMPTS) {
                    mws = account.getMWSClient(MWSClient.DEFAULT_TIMEOUT * (attempts + 1));
                }

                try {
                    // Sleep before next submission attempt
                    Thread.sleep(RETRY_SLEEP);
                }
                catch (InterruptedException ie) {
                    TransportLogger.getSysErrorLogger().error(
                        String.format(Messages.FeedDispatcherTask_7.toString()), ie);
                }
            }
        }
        while (retry && attempts < MAX_ATTEMPTS);
    }


    private void processReportRetrieval(AMTUAccount account) {
        ThreadPoolExecutor re = ExecutorFactory.getRetrieveExecutor();

        int reportsToRetrieve = account.getNumberOfReportsToRetrieve();

        if (account.isUnderRetrieval()) {
            return;
        }

        if (reportsToRetrieve == 0) {
            return;
        }

        re.submit(new ReportRetrieverTask(account));
    }
}