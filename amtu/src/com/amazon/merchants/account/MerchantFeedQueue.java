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

package com.amazon.merchants.account;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.amazon.merchants.Messages;
import com.amazon.merchants.database.Database;
import com.amazon.merchants.database.DatabaseException;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.transport.model.MerchantQueueSubmission;

public class MerchantFeedQueue extends MerchantSubmissionQueue {
    public static enum SQL_COLUMNS {
        feed_submission_id, submission_time
    };

    protected int MAX_QUEUE_SIZE = 10;
    protected int QUOTA_RESTORE_RATE = 2 * 60 * 1000;
    protected int QUOTA_VACATE_TIME = 30 * 60 * 1000;

    private AMTUAccount parentAccount = null;

    private static Database db = Database.getInstance();


    public static MerchantFeedQueue loadQueueForAccount(AMTUAccount account) throws DatabaseException, SQLException {
        MerchantFeedQueue feedQueue = new MerchantFeedQueue(account);
        feedQueue.queue = db.pullSubmissionQueueForMerchant(account.getAccountId());
        if (!feedQueue.isEmpty()) {
            feedQueue.lastCheckTime = new Date();
        }
        return feedQueue;
    }


    public MerchantFeedQueue(AMTUAccount parentAccount) {
        this.parentAccount = parentAccount;

        int reportInterval = AccountConfig.MINIMUM_FEED_DISPATCH_INTERVAL;
        String feedConfig = parentAccount.getConfigValue(AccountConfig.FEED_DISPATCH_INTERVAL);
        if (feedConfig != null && !feedConfig.isEmpty()) {
            reportInterval = Integer.parseInt(feedConfig);
        }

        QUOTA_RESTORE_RATE = reportInterval * 60 * 1000;
    }


    @Override
    public synchronized Date poll() {
        if (isEmpty()) {
            return null;
        }

        MerchantQueueSubmission submission = queue.poll();

        try {
            db.deleteFromFeedQueueForMerchant(parentAccount.getAccountId(), submission.getSubmissionId(),
                submission.getSubmissionTime());
        }
        catch (Exception e) {
            TransportLogger.getAcctErrorLogger(parentAccount).error(Messages.MerchantFeedQueue_0.toString());
        }

        return submission.getSubmissionTime();
    }


    @Override
    public synchronized void offer(String submissionId, Date submissionTime) {
        if (queue == null) {
            queue = new ConcurrentLinkedQueue<MerchantQueueSubmission>();
        }

        if (isEmpty() || lastCheckTime == null) {
            lastCheckTime = new Date();
        }

        MerchantQueueSubmission submission = new MerchantQueueSubmission();
        submission.setSubmissionId(submissionId);
        submission.setSubmissionTime((Date) submissionTime.clone());

        queue.offer(submission);

        try {
            db.addFeedToMerchantQueue(parentAccount.getAccountId(), submissionId, submissionTime);
        }
        catch (Exception e) {
            TransportLogger.getAcctErrorLogger(parentAccount).warn(Messages.MerchantFeedQueue_1.toString());
        }
    }


    public synchronized void save(Connection conn) throws SQLException {
        db.deleteAllFromMerchantFeedQueue(conn, parentAccount.getAccountId());

        if (isEmpty()) {
            return;
        }

        Object[] submissionList = queue.toArray();
        for (Object submission : submissionList) {
            MerchantQueueSubmission sub = (MerchantQueueSubmission) submission;
            db.addFeedToMerchantQueue(conn, parentAccount.getAccountId(), sub.getSubmissionId(),
                sub.getSubmissionTime());
        }
    }


    protected synchronized void delete(Connection conn) throws SQLException {
        if (parentAccount == null || parentAccount.getAccountId() < 0) {
            // parent not yet saved, nothing to delete
            return;
        }

        if (isEmpty()) {
            // not yet saved, nothing to delete
            return;
        }

        db.deleteAllFromMerchantFeedQueue(conn, parentAccount.getAccountId());

        queue = null;
    }


    @Override
    public int queueSpotsAvailable() {
        return queueSpotsAvailable(MAX_QUEUE_SIZE);
    }


    @Override
    public void pruneQueue() {
        pruneQueue(QUOTA_RESTORE_RATE, QUOTA_VACATE_TIME);
    }


    @Override
    public void vacateOldQueueEntries() {
        vacateOldQueueEntries(QUOTA_VACATE_TIME);
    }
}