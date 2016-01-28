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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang.Validate;

import com.amazon.merchants.Messages;
import com.amazon.merchants.account.AMTUAccount;
import com.amazon.merchants.account.MerchantAccountManager;
import com.amazon.merchants.database.Database;
import com.amazon.merchants.database.DatabaseException;
import com.amazon.merchants.executor.ExecutorFactory;
import com.amazon.merchants.gui.model.TransportGuiModel;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.mws.MWSClient;
import com.amazon.merchants.transport.model.MerchantFeed;
import com.amazon.merchants.transport.retriever.ProcReportRetrieverTask;
import com.amazonaws.mws.MarketplaceWebService;
import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.FeedSubmissionInfo;
import com.amazonaws.mws.model.GetFeedSubmissionListRequest;
import com.amazonaws.mws.model.GetFeedSubmissionListResponse;
import com.amazonaws.mws.model.GetFeedSubmissionListResult;
import com.amazonaws.mws.model.IdList;

public class StatusUpdaterService extends TransportService {
    private static final int MAX_ATTEMPTS = 3;
    private static final int RETRY_SLEEP = 15000;


    public StatusUpdaterService() {
        super(TransportServiceEnum.STATUS_UPDATER);
    }


    @Override
    public void run() {
        List<AMTUAccount> accountList = MerchantAccountManager.getAccountList();

        for (AMTUAccount account : accountList) {
            synchronized (account.STATUS_UPDATE_LOCK) {
                processStatusUpdate(account);

                processProcessingReportRetrieval(account);
            }
        }

        TransportGuiModel.getInstance().updateAllViews();
    }


    private void processStatusUpdate(AMTUAccount account) {
        // skip if we're within the minimum update time for this merchant
        if (!account.isEligibleForStatusUpdate()) {
            return;
        }

        // Build FeedSubmissionId list to update feed status
        IdList list = new IdList();

        HashMap<String, MerchantFeed> incompleteFeedList = new HashMap<String, MerchantFeed>();
        try {
            incompleteFeedList = account.incompleteFeedsForMerchant();
            list.setId(new ArrayList<String>(incompleteFeedList.keySet()));
        }
        catch (SQLException e) {
            TransportLogger.getAcctErrorLogger(account).error(
                "[" + account.getMerchantAlias() + "] " + Messages.StatusUpdaterService_0.toString()
                    + e.getLocalizedMessage(), e);
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
        }

        // Do not proceed if all feeds are in the _DONE_ state
        if (incompleteFeedList.isEmpty()) {
            return;
        }

        MarketplaceWebService mws = account.getMWSClient();

        String feedSubmissionId = null;
        String feedProcessingStatus = null;
        String requestId = null;
        int attempts = 0;
        boolean retry = false;
        do {
            retry = false;
            ++attempts;

            GetFeedSubmissionListRequest request = null;
            GetFeedSubmissionListResponse response = null;
            try {
                // Create request and set parameters
                request = new GetFeedSubmissionListRequest();
                request.setMerchant(account.getMerchantId());
                request.setFeedSubmissionIdList(list);

                response = mws.getFeedSubmissionList(request);

                if (response.isSetResponseMetadata()) {
                    requestId = response.getResponseMetadata().getRequestId();
                }

                if (response.isSetGetFeedSubmissionListResult()) {
                    GetFeedSubmissionListResult getFeedSubmissionListResult = response
                        .getGetFeedSubmissionListResult();

                    List<FeedSubmissionInfo> feedSubmissionInfoList = getFeedSubmissionListResult
                        .getFeedSubmissionInfoList();
                    for (FeedSubmissionInfo feedSubmissionInfo : feedSubmissionInfoList) {
                        if (feedSubmissionInfo.isSetFeedSubmissionId()) {
                            feedSubmissionId = feedSubmissionInfo.getFeedSubmissionId();
                        }
                        if (feedSubmissionInfo.isSetFeedProcessingStatus()) {
                            feedProcessingStatus = feedSubmissionInfo.getFeedProcessingStatus();
                        }
                        Validate.notNull(feedProcessingStatus);

                        // Update database with new feed processing statuses
                        MerchantFeed feed = incompleteFeedList.get(feedSubmissionId);
                        if (!feed.getFeedProcessingStatus().equals(feedProcessingStatus)) {
                            feed.setFeedProcessingStatus(feedProcessingStatus);

                            Connection conn = null;
                            try {
                                conn = Database.getUnmanagedConnection();
                                conn.setAutoCommit(false);
                                try {
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

                            if (feed.feedProcessingComplete()) {
                                // Get processing report
                                TransportLogger.getAcctAuditLogger(account).info(
                                    "[" + account.getMerchantAlias() + "] "
                                        + Messages.StatusUpdaterService_1.toString() + ", batch_id="
                                        + feedSubmissionId + ", request_id=" + requestId);
                            }
                        }
                    }
                }

                // Update account last connection
                account.updateLastStatusUpdate();
                account.updateLastConnection();
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
                    "[" + account.getMerchantAlias() + "] " + Messages.StatusUpdaterService_2.toString() + " - "
                        + mwsRequestId);
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
            catch (SQLException e) {
                TransportLogger.getAcctErrorLogger(account).error(
                    "[" + account.getMerchantAlias() + "] " + Messages.StatusUpdaterService_3.toString(), e);
            }
            catch (DatabaseException e) {
                TransportLogger.getSysErrorLogger().error(
                    String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
            }
        }
        while (retry && attempts < MAX_ATTEMPTS);
    }


    private void processProcessingReportRetrieval(AMTUAccount account) {
        ThreadPoolExecutor re = ExecutorFactory.getRetrieveExecutor();
        try {
            PriorityBlockingQueue<MerchantFeed> queue = account.feedsForProcessingReportRetrieval();

            if (account.isUnderProcRetrieval() || !account.hasProcessingReportRetrievalSpotsAvailable()) {
                return;
            }

            if (queue == null || queue.isEmpty()) {
                return;
            }

            re.submit(new ProcReportRetrieverTask(account));
        }
        catch (SQLException e) {
            TransportLogger.getAcctErrorLogger(account).error(
                "[" + account.getMerchantAlias() + "] " + Messages.StatusUpdaterService_0.toString(), e);
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
        }
    }
}
