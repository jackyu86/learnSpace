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

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.amazon.merchants.account.MerchantSiteGroup;
import com.amazon.merchants.database.Database;
import com.amazon.merchants.database.DatabaseException;

public class MerchantFeed implements Comparable<MerchantFeed> {
    public static enum SQL_COLUMNS {
        merchant_feed_id, file_name, feed_submission_id, feed_type,
        feed_processing_status, feed_submitted_date, feed_size_kb,
        date_created, date_updated, merchant_report_id
    };

    private int merchantFeedId = -1;
    private MerchantReport processingReport = null;
    private MerchantSiteGroup siteGroup = null;

    protected File file = null;
    private String feedSubmissionId = null;
    private String feedType = null;
    private String feedProcessingStatus = null;
    private String submittedDate = null;
    private long feedSize = -1;

    private Date dateCreated = new Date();
    private Date dateModified = new Date();

    protected File temporaryFile = null;

    private static Database db = Database.getInstance();

    public static final String FEED_DONE_STATUS = "_DONE_";
    public static final String FEED_CANCELLED_STATUS = "_CANCELLED_";


    private static MerchantFeed parseFeedInfo(MerchantSiteGroup siteGroup,
        HashMap<String, Object> feedInfo) throws DatabaseException,
        SQLException {
        MerchantFeed feed = new MerchantFeed();
        feed.siteGroup = siteGroup;
        feed.merchantFeedId = (Integer) feedInfo
            .get(SQL_COLUMNS.merchant_feed_id.toString());
        feed.file = new File((String) feedInfo.get(SQL_COLUMNS.file_name
            .toString()));
        feed.feedSubmissionId = (String) feedInfo
            .get(SQL_COLUMNS.feed_submission_id.toString());
        feed.feedType = (String) feedInfo.get(SQL_COLUMNS.feed_type.toString());
        feed.feedProcessingStatus = (String) feedInfo
            .get(SQL_COLUMNS.feed_processing_status.toString());
        feed.submittedDate = (String) feedInfo
            .get(SQL_COLUMNS.feed_submitted_date.toString());
        feed.feedSize = (Integer) feedInfo.get(SQL_COLUMNS.feed_size_kb
            .toString());
        feed.dateCreated = (Timestamp) feedInfo.get(SQL_COLUMNS.date_created
            .toString());
        feed.dateModified = (Timestamp) feedInfo.get(SQL_COLUMNS.date_updated
            .toString());

        feed.processingReport = MerchantReport.pullReportById(siteGroup,
            (Integer) feedInfo.get(SQL_COLUMNS.merchant_report_id.toString()));

        return feed;
    }


    public static List<MerchantFeed> pullMerchantFeedsForDateRange(
            MerchantSiteGroup siteGroup, Date fromDate, Date toDate)
        throws DatabaseException, SQLException {
        List<MerchantFeed> feedList = new ArrayList<MerchantFeed>();

        List<HashMap<String, Object>> pulledFeeds = db
            .listMerchantFeedsForDateRange(siteGroup.getParentAccount()
                .getAccountId(), siteGroup.getSiteGroupId(),
                fromDate, toDate);

        for (HashMap<String, Object> feedInfo : pulledFeeds) {
            feedList.add(parseFeedInfo(siteGroup, feedInfo));
        }

        return feedList;
    }


    public static MerchantFeed pullMerchantFeedById(MerchantSiteGroup siteGroup,
        int merchantFeedId) throws DatabaseException, SQLException {
        return parseFeedInfo(siteGroup, db.pullFeedDetailsById(siteGroup
            .getParentAccount().getAccountId(), siteGroup
            .getSiteGroupId(), merchantFeedId));
    }


    public static List<MerchantFeed> listIncompleteFeedsForSiteGroup(
            MerchantSiteGroup siteGroup) throws DatabaseException, SQLException {
        List<Integer> incompleteList = db.listIncompleteFeedsForSiteGroup(
            siteGroup.getParentAccount().getAccountId(),
            siteGroup.getSiteGroupId());

        List<MerchantFeed> incompleteFeeds = new ArrayList<MerchantFeed>();
        for (int id : incompleteList) {
            incompleteFeeds.add(pullMerchantFeedById(siteGroup, id));
        }
        return incompleteFeeds;
    }


    public static List<MerchantFeed> listCompleteFeedsWithoutProcessingReportForSiteGroup(
            MerchantSiteGroup siteGroup) throws DatabaseException, SQLException {
        List<Integer> incompleteList = db
            .listCompleteFeedsWithoutProcessingReportForSiteGroup(siteGroup
                .getParentAccount().getAccountId(), siteGroup
                .getSiteGroupId());

        List<MerchantFeed> awaitingProcessingReport = new ArrayList<MerchantFeed>();
        for (int id : incompleteList) {
            awaitingProcessingReport.add(pullMerchantFeedById(siteGroup, id));
        }
        return awaitingProcessingReport;
    }


    public MerchantFeed() {
    }


    public MerchantFeed(MerchantSiteGroup siteGroup, File feedFile) {
        this.siteGroup = siteGroup;
        file = feedFile;
    }


    public MerchantFeed(MerchantSiteGroup siteGroup, File feedFile,
        String feedSubmissionId, String feedType, String feedProcessingStatus,
        Date dateCreated, MerchantReport processingReport) {
        this.siteGroup = siteGroup;
        file = feedFile;
        this.feedSubmissionId = feedSubmissionId;
        this.feedType = feedType;
        this.feedProcessingStatus = feedProcessingStatus;
        this.dateCreated = dateCreated;
        this.processingReport = processingReport;
    }


    public synchronized void save(Connection conn) throws SQLException {
        if (merchantFeedId < 0) {
            merchantFeedId = db.insertMerchantFeed(
                conn,
                siteGroup.getParentAccount().getAccountId(),
                siteGroup.getSiteGroupId(),
                (processingReport != null ? processingReport
                    .getMerchantReportId() : null), file.getName(),
                feedSubmissionId, feedType, feedProcessingStatus,
                submittedDate, feedSize, dateCreated, dateModified);
        }
        else {
            // update
            db.updateMerchantFeed(
                conn,
                siteGroup.getParentAccount().getAccountId(),
                siteGroup.getSiteGroupId(),
                merchantFeedId,
                (processingReport != null ? processingReport
                    .getMerchantReportId() : null), file.getName(),
                feedSubmissionId, feedType, feedProcessingStatus,
                submittedDate, feedSize, dateCreated, dateModified);
        }
    }


    public synchronized void delete(Connection conn) throws SQLException {
        if (siteGroup == null || siteGroup.getSiteGroupId() < 0) {
            // parent not yet saved, nothing to delete
            return;
        }

        if (merchantFeedId < 0) {
            // not yet saved, don't need to delete
            return;
        }

        db.deleteMerchantFeed(conn, siteGroup.getParentAccount()
            .getAccountId(), siteGroup.getSiteGroupId(),
            merchantFeedId);
    }


    @Override
    public int compareTo(MerchantFeed feed) {
        return new Long(file.lastModified())
            .compareTo(feed.file.lastModified());
    }


    @Override
    public boolean equals(Object value) {
        if (value instanceof MerchantFeed) {
            MerchantFeed feed = (MerchantFeed) value;
            if (feedSubmissionId != null) {
                return feedSubmissionId.equals(feed.feedSubmissionId);
            }
            else {
                return file.equals(feed.file);
            }
        }
        return false;
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }


    public int getMerchantFeedId() {
        return merchantFeedId;
    }


    public void setMerchantFeedId(int merchantFeedId) {
        this.merchantFeedId = merchantFeedId;
    }


    public MerchantReport getProcessingReport() {
        return processingReport;
    }


    public void setProcessingReport(MerchantReport processingReport) {
        this.processingReport = processingReport;
    }


    public MerchantSiteGroup getSiteGroup() {
        return siteGroup;
    }


    public void setSiteGroup(MerchantSiteGroup siteGroup) {
        this.siteGroup = siteGroup;
    }


    public File getFile() {
        return file;
    }


    public void setFile(File file) {
        this.file = file;
    }


    public String getFeedSubmissionId() {
        return feedSubmissionId;
    }


    public void setFeedSubmissionId(String feedSubmissionId) {
        this.feedSubmissionId = feedSubmissionId;
    }


    public String getFeedType() {
        return feedType;
    }


    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }


    public boolean feedProcessingComplete() {
        return FEED_DONE_STATUS.equals(feedProcessingStatus);
    }


    public boolean feedCancelled() {
        return FEED_CANCELLED_STATUS.equals(feedProcessingStatus);
    }


    public String getFeedProcessingStatus() {
        return feedProcessingStatus;
    }


    public void setFeedProcessingStatus(String feedProcessingStatus) {
        this.feedProcessingStatus = feedProcessingStatus;
    }


    public String getSubmittedDate() {
        return submittedDate;
    }


    public void setSubmittedDate(String submittedDate) {
        this.submittedDate = submittedDate;
    }


    public long getFeedSize() {
        return feedSize;
    }


    public void setFeedSize(long feedSize) {
        this.feedSize = feedSize;
    }


    public Date getDateCreated() {
        return dateCreated;
    }


    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }


    public Date getDateModified() {
        return dateModified;
    }


    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }


    public File getTemporaryFile() {
        return temporaryFile;
    }


    public void setTemporaryFile(File temporaryFile) {
        this.temporaryFile = temporaryFile;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FileName: " + file.getAbsolutePath() + "\n") //$NON-NLS-1$ //$NON-NLS-2$
            .append("LastModified: " + dateModified + "\n") //$NON-NLS-1$ //$NON-NLS-2$
            .append("FeedType(Local): " + feedType + "\n") //$NON-NLS-1$ //$NON-NLS-2$
            .append("FeedSubmissionId: " + feedSubmissionId + "\n") //$NON-NLS-1$ //$NON-NLS-2$
            .append("FeedType(Remote): " + feedType + "\n") //$NON-NLS-1$ //$NON-NLS-2$
            .append("FeedProcessingStatus: " + feedProcessingStatus + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
        return builder.toString();
    }
}
