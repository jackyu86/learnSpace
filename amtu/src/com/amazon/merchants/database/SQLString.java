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

package com.amazon.merchants.database;

public class SQLString {
    protected static final int SCHEMA_VERSION = 5;

    // Schema Definition
    protected static final String CreateSchemaAMTU = "CREATE SCHEMA AMTU AUTHORIZATION SA";
    protected static final String CreateTableSchemaVersion = "CREATE TABLE AMTU.SCHEMA_VERSION "
        + "(VERSION_NUM INT NOT NULL, "
        + "DATE_CREATED TIMESTAMP, "
        + "DATE_UPDATED TIMESTAMP) ";
    protected static final String CreateTableAmtuAccount = "CREATE TABLE AMTU.AMTU_ACCOUNT "
        + "(AMTU_ACCOUNT_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT AMTU_ACCOUNT_PK PRIMARY KEY, "
        + "MERCHANT_ALIAS VARCHAR(64) NOT NULL, "
        + "MWS_ACCESS_KEY CHAR(20) NOT NULL, "
        + "MWS_SECRET_KEY CHAR(40) NOT NULL, "
        + "MERCHANT_ID VARCHAR(32) NOT NULL, "
        + "MWS_ENDPOINT VARCHAR(6) NOT NULL, "
        + "DOCUMENT_TRANSPORT VARCHAR(1024) NOT NULL)";
    protected static final String CreateTableAccountConfig = "CREATE TABLE AMTU.ACCOUNT_CONFIG "
        + "(AMTU_ACCOUNT_ID INT NOT NULL REFERENCES AMTU.AMTU_ACCOUNT(AMTU_ACCOUNT_ID) ON DELETE CASCADE, "
        + "CONFIG_KEY VARCHAR(1024) NOT NULL, "
        + "CONFIG_VALUE VARCHAR(1024))";
    protected static final String CreateTableMerchantSiteGroup = "CREATE TABLE AMTU.MERCHANT_SITE_GROUP "
        + "(SITE_GROUP_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT SITE_GROUP_PK PRIMARY KEY, "
        + "AMTU_ACCOUNT_ID INT NOT NULL REFERENCES AMTU.AMTU_ACCOUNT(AMTU_ACCOUNT_ID) ON DELETE CASCADE, "
        + "SITE_GROUP_DTD VARCHAR(30), "
        + "MERCHANT_ALIAS VARCHAR(64))";
    protected static final String CreateTableMerchantSite = "CREATE TABLE AMTU.MERCHANT_SITE "
        + "(SITE_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT MERCHANT_SITE_PK PRIMARY KEY, "
        + "SITE_GROUP_ID INT NOT NULL REFERENCES AMTU.MERCHANT_SITE_GROUP(SITE_GROUP_ID) ON DELETE CASCADE, "
        + "MARKETPLACE_ID VARCHAR(32) NOT NULL, "
        + "MERCHANT_ALIAS VARCHAR(64) NOT NULL)";

    protected static final String CreateTableMerchantFeed = "CREATE TABLE AMTU.MERCHANT_FEED "
        + "(MERCHANT_FEED_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT MERCHANT_FEED_PK PRIMARY KEY, "
        + "AMTU_ACCOUNT_ID INT NOT NULL REFERENCES AMTU.AMTU_ACCOUNT(AMTU_ACCOUNT_ID) ON DELETE CASCADE, "
        + "MERCHANT_REPORT_ID INT REFERENCES AMTU.MERCHANT_REPORT(MERCHANT_REPORT_ID) ON DELETE CASCADE, "
        + "SITE_GROUP_ID INT NOT NULL REFERENCES AMTU.MERCHANT_SITE_GROUP(SITE_GROUP_ID) ON DELETE CASCADE, "
        + "FILE_NAME VARCHAR(1024) NOT NULL, "
        + "FEED_SUBMISSION_ID VARCHAR(32) NOT NULL, "
        + "FEED_TYPE VARCHAR(128) NOT NULL, "
        + "FEED_PROCESSING_STATUS VARCHAR(32) NOT NULL, "
        + "FEED_SUBMITTED_DATE VARCHAR(32) NOT NULL, "
        + "FEED_SIZE_KB INT NOT NULL, "
        + "DATE_CREATED TIMESTAMP, "
        + "DATE_UPDATED TIMESTAMP)";
    protected static final String CreateTableMerchantFeedSubmissionQueue = "CREATE TABLE AMTU.MERCHANT_FEED_QUEUE "
        + "(AMTU_ACCOUNT_ID INT NOT NULL REFERENCES AMTU.AMTU_ACCOUNT(AMTU_ACCOUNT_ID) ON DELETE CASCADE, "
        + "FEED_SUBMISSION_ID VARCHAR(32) NOT NULL, "
        + "SUBMISSION_TIME TIMESTAMP NOT NULL)";

    protected static final String CreateTableMerchantReport = "CREATE TABLE AMTU.MERCHANT_REPORT "
        + "(MERCHANT_REPORT_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT MERCHANT_REPORT_PK PRIMARY KEY, "
        + "AMTU_ACCOUNT_ID INT NOT NULL REFERENCES AMTU.AMTU_ACCOUNT(AMTU_ACCOUNT_ID) ON DELETE CASCADE, "
        + "SITE_GROUP_ID INT NOT NULL REFERENCES AMTU.MERCHANT_SITE_GROUP(SITE_GROUP_ID) ON DELETE CASCADE, "
        + "FILE_NAME VARCHAR(1024) NOT NULL, "
        + "REPORT_ID VARCHAR(32) NOT NULL, "
        + "REPORT_TYPE VARCHAR(128) NOT NULL, "
        + "ACKNOWLEDGED CHAR(1) NOT NULL, "
        + "REPORT_SIZE_KB INT NOT NULL, "
        + "DATE_CREATED TIMESTAMP, "
        + "DATE_UPDATED TIMESTAMP)";
    
    protected static final String CreateTableProxyConfig = "CREATE TABLE AMTU.PROXY_CONFIG "
        + "(PROXY_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT PROXY_CONFIG_PK PRIMARY KEY, "
        + "PROXY_HOST VARCHAR(64) NOT NULL, "
        + "PROXY_PORT INT NOT NULL, "
        + "PROXY_USER VARCHAR(64), "
        + "PROXY_PASS VARCHAR(64))";

    // Operation
    protected static final String InitializeSchemaVersion = "INSERT INTO AMTU.SCHEMA_VERSION "
        + "(VERSION_NUM, DATE_CREATED, DATE_UPDATED) "
        + "VALUES(?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

    // For AMTUAccount.java
    protected static final String SelectAllAmtuAccounts = "select amtu_account_id from amtu.amtu_account";
    protected static final String SelectAmtuAccountIdByAlias = "select amtu_account_id from amtu.amtu_account where merchant_alias = ?";
    protected static final String InsertAmtuAccount = "insert into amtu.amtu_account("
        + "merchant_alias, mws_access_key, mws_secret_key, merchant_id, "
        + "mws_endpoint, document_transport) values(?, ?, ?, ?, ?, ?)";
    protected static final String UpdateAmtuAccount = "update amtu.amtu_account set merchant_alias = ?, "
        + "mws_access_key = ?, mws_secret_key = ?, merchant_id = ?, "
        + "mws_endpoint = ?, document_transport = ? where amtu_account_id = ?";
    protected static final String DeleteAmtuAccount = "delete from amtu.amtu_account where amtu_account_id = ?";
    protected static final String SelectAccountForMerchantId = "select amtu_account_id from amtu.amtu_account where merchant_id = trim(?)";
    protected static final String SelectAccountForDocumentTransport = "select amtu_account_id from amtu.amtu_account where document_transport = trim(?)";
    protected static final String SelectAccountForMwsAccessKeyAndMerchantId = "select amtu_account_id from amtu.amtu_account where mws_access_key = trim(?) and merchant_id = trim(?)";
    protected static final String SelectAmtuAccountById = "select merchant_alias, "
        + "mws_access_key, mws_secret_key, merchant_id, mws_endpoint, "
        + "document_transport from amtu.amtu_account where amtu_account_id = ?";

    // For AccountConfig.java
    protected static final String SelectAccountConfig = "select config_key, config_value from amtu.account_config where amtu_account_id = ?";
    protected static final String DeleteAccountConfigKey = "delete from amtu.account_config where amtu_account_id = ? and config_key = ?";
    protected static final String UpdateAccountConfigKey = "update amtu.account_config set config_value = ? where amtu_account_id = ? and config_key = ?";
    protected static final String InsertAccountConfigKey = "insert into amtu.account_config (amtu_account_id, config_key, config_value) values(?,?,?)";
    protected static final String DeleteAccountConfig = "delete from amtu.account_config where amtu_account_id = ?";

    // For MerchantSiteGroup.java
    protected static final String SelectAllSiteGroupsForAccount = "select site_group_id from amtu.merchant_site_group where amtu_account_id=? order by site_group_id";
    protected static final String SelectSiteGroupById = "select site_group_dtd, merchant_alias from amtu.merchant_site_group where amtu_account_id = ? and site_group_id = ?";
    protected static final String UpdateMerchantSiteGroup = "update amtu.merchant_site_group set merchant_alias = ?, site_group_dtd = ? where amtu_account_id = ? and site_group_id = ?";
    protected static final String InsertMerchantSiteGroup = "insert into amtu.merchant_site_group (amtu_account_id, merchant_alias, site_group_dtd) values(?, ?, ?)";
    protected static final String DeleteMerchantSiteGroup = "delete from amtu.merchant_site_group where amtu_account_id = ? and site_group_id = ?";
    protected static final String DeleteFeedsForMerchantSiteGroup = "delete from amtu.merchant_feed where amtu_account_id = ? and site_group_id = ?";
    protected static final String DeleteReportsForMerchantSiteGroup = "delete from amtu.merchant_report where amtu_account_id = ? and site_group_id = ?";
    protected static final String SelectAllSiteGroupDirectoriesForAccount = "select site_group_dtd from amtu.merchant_site_group where amtu_account_id = ?";

    // For MerchantSite.java
    protected static final String SelectAllSitesForSiteGroup = "select site_id from amtu.merchant_site where site_group_id = ?";
    protected static final String SelectMerchantSiteById = "select marketplace_id, merchant_alias from amtu.merchant_site where site_group_id = ? and site_id = ?";
    protected static final String UpdateMerchantSite = "update amtu.merchant_site set marketplace_id = ?, merchant_alias = ? where site_group_id = ? and site_id = ?";
    protected static final String InsertMerchantSite = "insert into amtu.merchant_site (site_group_id, marketplace_id, merchant_alias) values(?, ?, ?)";
    protected static final String DeleteMerchantSite = "delete from amtu.merchant_site where site_group_id = ? and site_id = ?";

    // For MerchantFeedQueue.java
    protected static final String SelectFeedQueueForMerchant = "select feed_submission_id, submission_time from amtu.merchant_feed_queue where amtu_account_id = ? order by submission_time asc";
    protected static final String DeleteFeedFromMerchantFeedQueue = "delete from amtu.merchant_feed_queue where amtu_account_id = ? and feed_submission_id = ? and submission_time = ?";
    protected static final String AddFeedToMerchantFeedQueue = "insert into amtu.merchant_feed_queue (amtu_account_id, feed_submission_id, submission_time) values(?, ?, ?)";
    protected static final String DeleteAllFromMerchantFeedQueue = "delete from amtu.merchant_feed_queue where amtu_account_id = ?";

    // For MerchantFeed.java
    protected static final String SelectMerchantFeedsForSiteGroupInRange = "select merchant_feed_id, merchant_report_id, file_name, feed_submission_id, feed_type, feed_processing_status, feed_submitted_date, feed_size_kb, date_created, date_updated from amtu.merchant_feed where amtu_account_id=? and site_group_id=? and date_created between ? and ? order by feed_submission_id asc, date_created asc";
    protected static final String InsertMerchantFeed = "insert into amtu.merchant_feed (amtu_account_id, site_group_id, merchant_report_id, file_name, feed_submission_id, feed_type, feed_processing_status, feed_submitted_date, feed_size_kb, date_created, date_updated) values(?,?,?,?,?,?,?,?,?,?,?)";
    protected static final String UpdateMerchantFeed = "update amtu.merchant_feed set merchant_report_id=?, file_name=?, feed_submission_id=?, feed_type=?, feed_processing_status=?, feed_submitted_date=?, feed_size_kb=?, date_created=?, date_updated=? where amtu_account_id = ? and site_group_id = ? and merchant_feed_id = ?";
    protected static final String DeleteMerchantFeed = "delete from amtu.merchant_feed where amtu_account_id=? and site_group_id=? and merchant_feed_id = ?";
    protected static final String SelectIncompleteFeedsForSiteGroup = "select merchant_feed_id from amtu.merchant_feed where amtu_account_id = ? and site_group_id = ? and feed_processing_status != '_DONE_' and feed_processing_status != '_CANCELLED_'";
    protected static final String SelectCompleteFeedsWithoutProcessingReport = "select merchant_feed_id from amtu.merchant_feed where amtu_account_id=? and site_group_id = ? and feed_processing_status = '_DONE_' and (merchant_report_id is null or merchant_report_id <= 0)";
    protected static final String SelectMerchantFeedById = "select merchant_feed_id, merchant_report_id, file_name, feed_submission_id, feed_type, feed_processing_status, feed_submitted_date, feed_size_kb, date_created, date_updated from amtu.merchant_feed where amtu_account_id=? and site_group_id=? and merchant_feed_id = ?";

    // For MerchantReport.java
    protected static final String SelectMerchantReportById = "select merchant_report_id, file_name, report_id, report_type, acknowledged, report_size_kb, date_created, date_updated from amtu.merchant_report where amtu_account_id = ? and site_group_id = ? and merchant_report_id = ?";
    protected static final String SelectMerchantReportsForSiteGroupInRange = "select merchant_report_id, date_created from amtu.merchant_report where amtu_account_id = ? and site_group_id = ? and date_created between ? and ? order by date_created asc";
    protected static final String InsertMerchantReport = "insert into amtu.merchant_report (amtu_account_id, site_group_id, file_name, report_id, report_type, acknowledged, report_size_kb, date_created, date_updated) values(?,?,?,?,?,?,?,?,?)";
    protected static final String UpdateMerchantReport = "update amtu.merchant_report set file_name=?, report_id=?, report_type=?, acknowledged=?, report_size_kb=?, date_created=?, date_updated=? where amtu_account_id = ? and site_group_id = ? and merchant_report_id = ?";
    protected static final String DeleteMerchantReport = "delete from amtu.merchant_report where amtu_account_id = ? and site_group_id = ? and merchant_report_id = ?";

    // For ProxyConfig.java
    protected static final String SelectProxyConfig = "select proxy_id, proxy_host, proxy_port, proxy_user, proxy_pass from amtu.proxy_config";   
    protected static final String SelectProxyCount = "select count(*) from amtu.proxy_config as count";
    protected static final String DeleteProxyConfig = "delete from amtu.proxy_config";
    protected static final String InsertProxyConfigHost = "insert into amtu.proxy_config (proxy_host, proxy_port, proxy_user, proxy_pass) values(?, ?, ?, ?)";
    protected static final String UpdateProxyConfig = "update amtu.proxy_config set proxy_host=?, proxy_port=?, proxy_user=?, proxy_pass=? where proxy_id = ?";
    
    // Maintenance
    protected static final String CheckCurrentSchemaVersion = "SELECT VERSION_NUM "
        + "FROM AMTU.SCHEMA_VERSION ";

    protected static final String QueryIsSchemaCreated = "SELECT COUNT(*) COUNT "
        + "FROM SYS.SYSSCHEMAS " + "WHERE SCHEMANAME = 'AMTU'";

    // Update Steps
    protected static Object[] UPDATE_STEPS = new Object[SCHEMA_VERSION + 1];
    static {
        // Will be used later for schema updates between versions
        UPDATE_STEPS[0] = null;
        UPDATE_STEPS[1] = null;
        UPDATE_STEPS[2] = new String[] {
            // Schema version 2 adds "reports_disabled" capability
            "ALTER TABLE AMTU.AMTU_ACCOUNT ADD COLUMN REPORTS_DISABLED CHAR(1) NOT NULL DEFAULT 'N'",
            "UPDATE AMTU.SCHEMA_VERSION SET VERSION_NUM=2, DATE_UPDATED=CURRENT_TIMESTAMP" };
        UPDATE_STEPS[3] = new String[] {
            // Schema version 3 adds multi-site capability
            CreateTableMerchantSiteGroup,
            CreateTableMerchantSite,
            CreateTableAccountConfig,
            CreateTableMerchantFeedSubmissionQueue,

            "DROP TRIGGER MERCHANT_REPORT_AFTER_INSERT",

            "ALTER TABLE AMTU.AMTU_ACCOUNT ADD COLUMN MWS_ENDPOINT VARCHAR(6) NOT NULL DEFAULT 'US'",
            "UPDATE AMTU.AMTU_ACCOUNT SET MWS_ENDPOINT='CA' WHERE MARKETPLACE_ID='A2EUQ1WTGCTBG2'",
            "UPDATE AMTU.AMTU_ACCOUNT SET MWS_ENDPOINT='CN' WHERE MARKETPLACE_ID='AAHKV2X7AFYLW'",
            "UPDATE AMTU.AMTU_ACCOUNT SET MWS_ENDPOINT='FR' WHERE MARKETPLACE_ID='A13V1IB3VIYZZH'",
            "UPDATE AMTU.AMTU_ACCOUNT SET MWS_ENDPOINT='DE' WHERE MARKETPLACE_ID='A1PA6795UKMFR9'",
            "UPDATE AMTU.AMTU_ACCOUNT SET MWS_ENDPOINT='JP' WHERE MARKETPLACE_ID='A1VC38T7YXB528'",
            "UPDATE AMTU.AMTU_ACCOUNT SET MWS_ENDPOINT='GB' WHERE MARKETPLACE_ID='A1F83G8C2ARO7P'",
            "UPDATE AMTU.AMTU_ACCOUNT SET MWS_ENDPOINT='IT' WHERE MARKETPLACE_ID='APJ6JRA9NG5V4'",

            "ALTER TABLE AMTU.AMTU_ACCOUNT DROP COLUMN OS_USER_NAME",
            "ALTER TABLE AMTU.AMTU_ACCOUNT DROP COLUMN MARKETPLACE_ID",
            "ALTER TABLE AMTU.AMTU_ACCOUNT DROP COLUMN ENDPOINT_SC_URL",
            "ALTER TABLE AMTU.AMTU_ACCOUNT DROP COLUMN ENDPOINT_DESC",
            "ALTER TABLE AMTU.AMTU_ACCOUNT DROP COLUMN ENDPOINT",
            "ALTER TABLE AMTU.AMTU_ACCOUNT DROP COLUMN CONF_RT_TYPE_PREF",

            "RENAME COLUMN AMTU.AMTU_ACCOUNT.AWS_ACCESS_KEY TO MWS_ACCESS_KEY",
            "RENAME COLUMN AMTU.AMTU_ACCOUNT.AWS_SECRET_KEY TO MWS_SECRET_KEY",

            "INSERT INTO AMTU.ACCOUNT_CONFIG (AMTU_ACCOUNT_ID, CONFIG_KEY, CONFIG_VALUE) (SELECT AMTU_ACCOUNT_ID, 'FEED_DISPATCH_INTERVAL', TRIM(CHAR(CONF_FD_DISP_IN_MIN)) FROM AMTU.AMTU_ACCOUNT)",
            "INSERT INTO AMTU.ACCOUNT_CONFIG (AMTU_ACCOUNT_ID, CONFIG_KEY, CONFIG_VALUE) (SELECT AMTU_ACCOUNT_ID, 'REPORT_RETRIEVAL_INTERVAL', TRIM(CHAR(CONF_RT_RTRV_IN_MIN)) FROM AMTU.AMTU_ACCOUNT)",
            "INSERT INTO AMTU.ACCOUNT_CONFIG (AMTU_ACCOUNT_ID, CONFIG_KEY, CONFIG_VALUE) (SELECT AMTU_ACCOUNT_ID, 'PROCESSING_REPORT_RETRIEVAL_INTERVAL', TRIM(CHAR(CONF_PC_RT_RTRV_IN_MIN)) FROM AMTU.AMTU_ACCOUNT)",
            "INSERT INTO AMTU.ACCOUNT_CONFIG (AMTU_ACCOUNT_ID, CONFIG_KEY, CONFIG_VALUE) (SELECT AMTU_ACCOUNT_ID, 'REPORTS_DISABLED', REPORTS_DISABLED FROM AMTU.AMTU_ACCOUNT)",
            "ALTER TABLE AMTU.AMTU_ACCOUNT DROP COLUMN CONF_FD_DISP_IN_MIN",
            "ALTER TABLE AMTU.AMTU_ACCOUNT DROP COLUMN CONF_RT_RTRV_IN_MIN",
            "ALTER TABLE AMTU.AMTU_ACCOUNT DROP COLUMN CONF_PC_RT_RTRV_IN_MIN",
            "ALTER TABLE AMTU.AMTU_ACCOUNT DROP COLUMN REPORTS_DISABLED",
            "ALTER TABLE AMTU.AMTU_ACCOUNT DROP COLUMN ACT_IND",

            "INSERT INTO AMTU.MERCHANT_SITE_GROUP (AMTU_ACCOUNT_ID, MERCHANT_ALIAS, SITE_GROUP_DTD) (SELECT AMTU_ACCOUNT_ID, 'DEFAULT', 'production' FROM AMTU.AMTU_ACCOUNT)",

            "ALTER TABLE AMTU.MERCHANT_FEED ADD COLUMN SITE_GROUP_ID INT REFERENCES AMTU.MERCHANT_SITE_GROUP(SITE_GROUP_ID) ON DELETE CASCADE",
            "UPDATE AMTU.MERCHANT_FEED F SET SITE_GROUP_ID = (SELECT SITE_GROUP_ID FROM AMTU.MERCHANT_SITE_GROUP WHERE AMTU_ACCOUNT_ID = F.AMTU_ACCOUNT_ID)",
            "ALTER TABLE AMTU.MERCHANT_FEED ALTER COLUMN SITE_GROUP_ID NOT NULL",

            "ALTER TABLE AMTU.MERCHANT_REPORT ADD COLUMN SITE_GROUP_ID INT REFERENCES AMTU.MERCHANT_SITE_GROUP(SITE_GROUP_ID) ON DELETE CASCADE",
            "UPDATE AMTU.MERCHANT_REPORT R SET SITE_GROUP_ID = (SELECT SITE_GROUP_ID FROM AMTU.MERCHANT_SITE_GROUP WHERE AMTU_ACCOUNT_ID = R.AMTU_ACCOUNT_ID)",
            "ALTER TABLE AMTU.MERCHANT_REPORT ALTER COLUMN SITE_GROUP_ID NOT NULL",

            "UPDATE AMTU.SCHEMA_VERSION SET VERSION_NUM=3, DATE_UPDATED=CURRENT_TIMESTAMP" };
        UPDATE_STEPS[4] = new String[] {
            // Schema version 4 corrects a defect from version 3 that set the folder name to PRODUCTION rather than production
            "UPDATE AMTU.MERCHANT_SITE_GROUP SET SITE_GROUP_DTD='production' WHERE MERCHANT_ALIAS='DEFAULT'",
            "UPDATE AMTU.SCHEMA_VERSION SET VERSION_NUM=4, DATE_UPDATED=CURRENT_TIMESTAMP"
        };
        UPDATE_STEPS[5] = new String[] {
            // Schema version 5 adds a proxy configuration table for storing proxy information
            CreateTableProxyConfig,
            
            "UPDATE AMTU.SCHEMA_VERSION SET VERSION_NUM=5, DATE_UPDATED=CURRENT_TIMESTAMP"
        };
    }
}