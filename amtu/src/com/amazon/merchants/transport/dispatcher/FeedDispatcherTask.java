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

package com.amazon.merchants.transport.dispatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import com.amazon.merchants.Messages;
import com.amazon.merchants.account.AMTUAccount;
import com.amazon.merchants.account.MerchantSite;
import com.amazon.merchants.account.MerchantSiteGroup;
import com.amazon.merchants.database.Database;
import com.amazon.merchants.database.DatabaseException;
import com.amazon.merchants.gui.model.TransportGuiModel;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.mws.MWSClient;
import com.amazon.merchants.transport.model.MerchantFeed;
import com.amazon.merchants.transport.model.UnrecognizedFeedException;
import com.amazon.merchants.transport.util.ContentMD5HeaderCalculator;
import com.amazon.merchants.util.file.DirectoryEnum;
import com.amazon.merchants.util.file.IO;
import com.amazon.merchants.util.file.TransportUtil;
import com.amazonaws.mws.MarketplaceWebService;
import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.IdList;
import com.amazonaws.mws.model.SubmitFeedRequest;
import com.amazonaws.mws.model.SubmitFeedResponse;
import com.amazonaws.mws.model.SubmitFeedResult;

public class FeedDispatcherTask implements Runnable {

    private static final int MAX_ATTEMPTS = 3;
    private static final int RETRY_SLEEP = 15000;

    private AMTUAccount account;

    private MarketplaceWebService mws;


    public FeedDispatcherTask(AMTUAccount account) {
        this.account = account;
    }


    public void run() {
        synchronized (account.DISPATCH_LOCK) {
            account.setUnderDispatch(true);
            PriorityBlockingQueue<MerchantFeed> feedList = account.feedsForDispatch();

            while (account.hasFeedDispatchSlotsAvailable() && !feedList.isEmpty()) {
                MerchantFeed feed = feedList.poll();
                submitFeed(feed);
            }
            account.setUnderDispatch(false);
        }
    }


    private void submitFeed(MerchantFeed feed) {
        MerchantSiteGroup siteGroup = feed.getSiteGroup();
        File siteGroupDTD = siteGroup.getDocumentTransportFolder();

        try {
            // Move the candidate feed to the temp folder
            final File TEMP = TransportUtil.getDirectory(siteGroupDTD, DirectoryEnum.TEMP);
            feed.setFile(TransportUtil.moveFile(feed.getFile(), TEMP));

            final File FAILED = TransportUtil.getDirectory(siteGroupDTD, DirectoryEnum.FAILED);
            final File SENT = TransportUtil.getDirectory(siteGroupDTD, DirectoryEnum.SENT);

            Object[] messageArguments = { feed.getFile().getName() };
            MessageFormat formatter = new MessageFormat("");
            formatter.applyPattern(Messages.FeedDispatcherTask_1.toString());
            TransportLogger.getAcctAuditLogger(account).info(
                "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                    + formatter.format(messageArguments));

            // Recognize the feed type
            try {
                String type = FeedContentIdentifier.getFileType(feed.getFile());
                feed.setFeedType(type);
            }
            catch (UnrecognizedFeedException e) {
                feed.setFile(TransportUtil.moveFile(feed.getFile(), FAILED, new Date().getTime() + ""));
                TransportLogger.getAcctAuditLogger(account).warn(
                    "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                        + feed.getFile().getName() + " " + e.getLocalizedMessage(), e);
                return;
            }

            // Dispatching feed
            FileInputStream in = null;
            FileInputStream md5in = null;
            String requestId = null;
            boolean failedSend;
            int attempts = 0;
            do {
                failedSend = false;
                ++attempts;

                SubmitFeedRequest request = null;
                SubmitFeedResponse response = null;
                try {
                    request = new SubmitFeedRequest();
                    request.setMerchant(account.getMerchantId());

                    List<MerchantSite> siteList = siteGroup.getSiteList();
                    if (!siteList.isEmpty()) {
                        IdList list = new IdList();
                        for (MerchantSite site : siteList) {
                            list.withId(site.getMarketplaceId());
                        }

                        request.setMarketplaceIdList(list);
                    }

                    // Compute Content MD5 Header information
                    md5in = new FileInputStream(feed.getFile());
                    request.setContentMD5(ContentMD5HeaderCalculator.computeContentMD5Header(md5in));
                    IO.closeSilently(md5in);
                    md5in = null;

                    // Load file content
                    in = new FileInputStream(feed.getFile());
                    request.setFeedContent(in);
                    request.setFeedType(feed.getFeedType());

                    if (mws == null) {
                        // Initialize client
                        mws = account.getMWSClient();
                    }
                    response = mws.submitFeed(request);

                    account.updateLastDispatch();

                    if (response.isSetSubmitFeedResult()) {
                        SubmitFeedResult submitFeedResult = response.getSubmitFeedResult();
                        if (submitFeedResult.isSetFeedSubmissionInfo()) {
                            feed.setFeedType(submitFeedResult.getFeedSubmissionInfo().getFeedType());
                            feed.setFeedProcessingStatus(submitFeedResult.getFeedSubmissionInfo()
                                .getFeedProcessingStatus());
                            feed.setFeedSubmissionId(submitFeedResult.getFeedSubmissionInfo().getFeedSubmissionId());
                            feed.setSubmittedDate(submitFeedResult.getFeedSubmissionInfo().getSubmittedDate());
                            feed.setFeedSize(feed.getFile().length() / 1024);
                        }
                    }

                    account.addFeedSubmission("" + feed.getFeedSubmissionId(), new Date());

                    if (response.isSetResponseMetadata()) {
                        requestId = response.getResponseMetadata().getRequestId();
                    }

                    // Update account last connection value
                    account.updateLastConnection();
                }
                catch (MarketplaceWebServiceException e) {
                    String mwsRequestId = "MWS Request ID unknown";
                    if (response != null) {
                        if (response.isSetResponseMetadata()) {
                            mwsRequestId = response.getResponseMetadata().getRequestId();
                        }
                    }

                    failedSend = true;

                    // Add to feed queue anyway to prevent throttling
                    account.addFeedSubmission(Long.toString(new Date().getTime()), new Date());

                    messageArguments = new Object[] { feed.getFile().getName() };
                    formatter = new MessageFormat("");
                    formatter.applyPattern(Messages.FeedDispatcherTask_2.toString());
                    TransportLogger.getAcctErrorLogger(account).error(
                        "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                            + formatter.format(messageArguments) + " - " + mwsRequestId);

                    String mwsError = String.format(Messages.MarketplaceWebServiceException_0.toString(),
                        e.getLocalizedMessage());
                    TransportLogger.getAcctErrorLogger(account).error(mwsError, e);

                    if (attempts < MAX_ATTEMPTS) {
                        // Bring up the timeout in case that's causing the failure
                        mws = account.getMWSClient(MWSClient.DEFAULT_TIMEOUT * (attempts + 1));

                        String retryNotice = String.format(Messages.FeedDispatcherTask_6.toString(), feed.getFile()
                            .getName(), attempts, MAX_ATTEMPTS - 1);
                        TransportLogger.getAcctAuditLogger(account).info(
                            "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                                + retryNotice);

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
                finally {
                    IO.closeSilently(in);
                    IO.closeSilently(md5in);
                }
            }
            while (failedSend && attempts < MAX_ATTEMPTS);

            if (failedSend) {
                // Fell out of the loop above without a successful feed submission
                String errorString = "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                    + Messages.FeedDispatcherTask_8.toString();
                TransportLogger.getAcctErrorLogger(account).error(
                    String.format(errorString, feed.getFile().getName(), FAILED.getAbsolutePath()));

                feed.setFile(TransportUtil.moveFile(feed.getFile(), FAILED, new Date().getTime() + ""));
                return;
            }

            // Move successfully submitted feeds to the sent directory
            feed.setFile(TransportUtil.moveFile(feed.getFile(), SENT, feed.getFeedSubmissionId()));

            messageArguments = new Object[] { feed.getFile().getName() };
            formatter = new MessageFormat("");
            formatter.applyPattern(Messages.FeedDispatcherTask_3.toString());
            TransportLogger.getAcctAuditLogger(account).info(
                "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                    + formatter.format(messageArguments) + ", batch_id=" + feed.getFeedSubmissionId() + ", request_id="
                    + requestId);

        }
        catch (IOException e) {
            Object[] messageArguments = { feed.getFile().getName() };
            MessageFormat formatter = new MessageFormat("");
            formatter.applyPattern(Messages.FeedDispatcherTask_4.toString());
            TransportLogger.getAcctErrorLogger(account).error(
                "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                    + formatter.format(messageArguments) + " " + e.getLocalizedMessage(), e);
            return;
        }

        // Store in the database
        Connection conn = null;
        try {
            conn = Database.getUnmanagedConnection();
            conn.setAutoCommit(false);
            try {
                feed.save(conn);
                conn.commit();

                Object[] messageArguments = { feed.getFile().getName() };
                MessageFormat formatter = new MessageFormat("");
                formatter.applyPattern(Messages.FeedDispatcherTask_5.toString());
                TransportLogger.getAcctAuditLogger(account).info(
                    "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                        + formatter.format(messageArguments) + ", batch_id=" + feed.getFeedSubmissionId());
            }
            catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
        catch (SQLException e) {
            TransportLogger.getAcctErrorLogger(account).error(
                "[" + account.getMerchantAlias() + "] [" + siteGroup.getMerchantAlias() + "] "
                    + feed.getFile().getName() + " " + e.getLocalizedMessage(), e);
            return;
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
            return;
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

        // Notify GUI to update
        TransportGuiModel.getInstance().updateFeeds();
    }
}