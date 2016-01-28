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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import com.amazon.merchants.account.MerchantSiteGroup;
import com.amazon.merchants.database.Database;
import com.amazon.merchants.database.DatabaseException;
import com.amazonaws.mws.model.ReportInfo;

public class MerchantReport implements Comparable<MerchantReport> {
    public static enum SQL_COLUMNS {
        merchant_report_id, file_name, report_id, report_type, acknowledged,
        report_size_kb, date_created, date_modified
    };

    private int merchantReportId = -1;
    private MerchantSiteGroup siteGroup = null;

    private File file = null;
    private String reportId = null;
    private String reportType = null;
    private boolean acknowledged = false;
    private long reportSize = 0;
    private Date dateReceived = new Date();
    private Date dateModified = new Date();

    private XMLGregorianCalendar availDate = null;
    private XMLGregorianCalendar ackDate = null;

    protected File temporaryFile = null;

    private static Database db = Database.getInstance();


    private static MerchantReport parseReportDetails(
            MerchantSiteGroup siteGroup, Map<String, Object> details) {
        MerchantReport merchReport = new MerchantReport();
        merchReport.siteGroup = siteGroup;
        merchReport.merchantReportId = (Integer) details
            .get(SQL_COLUMNS.merchant_report_id.toString());
        merchReport.file = new File((String) details.get(SQL_COLUMNS.file_name
            .toString()));
        merchReport.reportId = (String) details.get(SQL_COLUMNS.report_id
            .toString());
        merchReport.reportType = (String) details.get(SQL_COLUMNS.report_type
            .toString());
        merchReport.acknowledged = "Y".equals(details.get(SQL_COLUMNS.acknowledged.toString())); //$NON-NLS-1$
        merchReport.reportSize = (Integer) details
            .get(SQL_COLUMNS.report_size_kb.toString());
        merchReport.dateReceived = (Date) details.get(SQL_COLUMNS.date_created
            .toString());
        merchReport.dateModified = (Date) details.get(SQL_COLUMNS.date_modified
            .toString());
        return merchReport;
    }


    public static MerchantReport pullReportById(MerchantSiteGroup siteGroup,
        Integer merchantReportId) throws DatabaseException, SQLException {
        if (merchantReportId == null) {
            return null;
        }

        Map<String, Object> details = db.pullReportDetailsById(siteGroup
            .getParentAccount().getAccountId(), siteGroup
            .getSiteGroupId(), merchantReportId);
        if (details.isEmpty()) {
            return null;
        }

        return parseReportDetails(siteGroup, details);
    }


    public static List<MerchantReport> pullMerchantReportsForDateRange(
            MerchantSiteGroup siteGroup, Date fromDate, Date toDate)
        throws DatabaseException, SQLException {
        List<MerchantReport> reportList = new ArrayList<MerchantReport>();

        List<HashMap<String, Object>> reportInfoList = db
            .listMerchantReportsForDateRange(siteGroup.getParentAccount()
                .getAccountId(), siteGroup.getSiteGroupId(),
                fromDate, toDate);
        for (HashMap<String, Object> details : reportInfoList) {
            reportList.add(parseReportDetails(siteGroup, details));
        }

        return reportList;
    }


    public MerchantReport() {
    }


    public MerchantReport(MerchantSiteGroup siteGroup, File file,
        File temporaryFile) {
        this.file = file;
        this.temporaryFile = temporaryFile;
        this.siteGroup = siteGroup;
    }


    public MerchantReport(MerchantSiteGroup siteGroup, File file,
        File temporaryFile, ReportInfo info) {
        this.siteGroup = siteGroup;
        this.file = file;
        this.temporaryFile = temporaryFile;
        availDate = info.getAvailableDate();
        reportId = info.getReportId();
        reportType = info.getReportType();
        ackDate = info.getAcknowledgedDate();
    }


    public synchronized void save(Connection conn) throws SQLException {
        if (merchantReportId < 0) {
            merchantReportId = db.insertMerchantReport(conn, siteGroup
                .getParentAccount().getAccountId(), siteGroup
                .getSiteGroupId(), file.getName(), reportId, reportType,
                acknowledged, reportSize, dateReceived, dateModified);
        }
        else {
            db.updateMerchantReport(conn, siteGroup.getParentAccount()
                .getAccountId(), siteGroup.getSiteGroupId(),
                merchantReportId, file.getName(), reportId, reportType,
                acknowledged, reportSize, dateReceived, dateModified);
        }
    }


    public synchronized void delete(Connection conn) throws SQLException {
        db.deleteMerchantReport(conn, siteGroup.getParentAccount()
            .getAccountId(), siteGroup.getSiteGroupId(),
            merchantReportId);
    }


    @Override
    public int compareTo(MerchantReport report) {
        return availDate.compare(report.getAvailableDate());
    }


    public Date getDateReceived() {
        return dateReceived;
    }


    public void setDateReceived() {
        dateReceived = new Date();
    }


    public void setInfo(ReportInfo info) {
        availDate = info.getAvailableDate();
        reportId = info.getReportId();
        reportType = info.getReportType();
        ackDate = info.getAcknowledgedDate();
    }


    public XMLGregorianCalendar getAvailableDate() {
        return availDate;
    }


    public String getReportId() {
        return reportId;
    }


    public String getReportType() {
        return reportType;
    }


    public boolean getAcknowledged() {
        return acknowledged;
    }


    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }


    public XMLGregorianCalendar getAcknowledgedDate() {
        return ackDate;
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


    public long getReportSize() {
        return reportSize;
    }


    public void setReportSize(long reportSize) {
        this.reportSize = reportSize;
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


    public int getMerchantReportId() {
        return merchantReportId;
    }


    public void setReportId(String reportId) {
        this.reportId = reportId;
    }


    public void setReportType(String reportType) {
        this.reportType = reportType;
    }


    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }


    @Override
    public String toString() {
        return new StringBuilder()
            .append("FileName: " + file.getAbsolutePath()) //$NON-NLS-1$
            .append("\nDateReceived: " + dateReceived) //$NON-NLS-1$
            .append("\nReportType: " + getReportType()) //$NON-NLS-1$
            .append("\nReportId: " + getReportId()) //$NON-NLS-1$
            .append("\nDateAvailable: " + getAvailableDate()) //$NON-NLS-1$
            .append("\nDateAcknowledged: " + getAcknowledgedDate()) //$NON-NLS-1$
            .append("\n").toString(); //$NON-NLS-1$
    }
}
