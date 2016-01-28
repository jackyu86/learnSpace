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

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.amazon.merchants.Messages;
import com.amazon.merchants.account.AMTUAccount;
import com.amazon.merchants.account.AccountConfig;
import com.amazon.merchants.account.MerchantFeedQueue;
import com.amazon.merchants.account.MerchantSite;
import com.amazon.merchants.account.MerchantSiteGroup;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.transport.model.MerchantFeed;
import com.amazon.merchants.transport.model.MerchantQueueSubmission;
import com.amazon.merchants.transport.model.MerchantReport;
import com.amazon.merchants.util.file.DirectoryUtil;

public class Database {
    public static enum SQL_COLUMNS {
        version_num, count
    };

    private static Database instance = new Database();

    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver"; //$NON-NLS-1$
    private static final String DBNAME = DirectoryUtil.getAMTUWorkingDir()
            + File.separator + "db"; //$NON-NLS-1$
    private static final String DBLOGNAME = DirectoryUtil
            .makeSyslogPath("derby.log"); //$NON-NLS-1$

    private static final String CONNECTIONURL = "jdbc:derby:" + DBNAME + ";create=true"; //$NON-NLS-1$ //$NON-NLS-2$
    private Connection conn = null;


    private Database() {
    }


    public static Database getInstance() {
        return instance;
    }


    /**
     * Start up the database and load the Derby driver. When the embedded Driver
     * is used this action start the Derby engine. Also start a connection to
     * the database
     *
     * @throws DatabaseException
     */
    public void start() throws DatabaseException {
        try {
            // put the log file in with the other system logs
            System.setProperty("derby.stream.error.file", DBLOGNAME); //$NON-NLS-1$

            Class.forName(DRIVER);
            // Create database connection after starting up
            conn = DriverManager.getConnection(CONNECTIONURL);
            TransportLogger.getSysAuditLogger().info(
                    Messages.Database_7.toString() + DRIVER);

            // Create database schema is it has not been created
            if (!isSchemaCreated()) {
                createSchema();
            }
            else {
                updateSchema();
            }
        }
        catch (ClassNotFoundException e) {
            TransportLogger.getSysErrorLogger().fatal(
                    Messages.Database_0.toString(), e);
            throw new DatabaseException(e);
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().fatal(
                    Messages.Database_3.toString(), e);
            throw new DatabaseException(e);
        }
    }


    /**
     * Shutdown the database. In embedded mode, an application should shut down
     * Derby. Shutdown throws the XJ015 exception to confirm success
     *
     * @throws SQLException
     *         unsuccessful database shutdown
     */
    public void shutdown() throws DatabaseException {
        if (DRIVER.equals("org.apache.derby.jdbc.EmbeddedDriver")) { //$NON-NLS-1$
            try {
                // Close database connect before shutdown
                conn.close();
                DriverManager.getConnection("jdbc:derby:;shutdown=true"); //$NON-NLS-1$
            }
            catch (SQLException e) {
                if (!e.getSQLState().equals("XJ015")) { //$NON-NLS-1$
                    TransportLogger.getSysErrorLogger().fatal(
                            Messages.Database_1.toString(), e);
                    throw new DatabaseException(e);
                }
            }

            TransportLogger.getSysAuditLogger().info(
                    Messages.Database_4.toString());
        }
    }


    /**
     * Gets the cached database connection
     *
     * @return Database connection
     * @throws DatabaseException
     *         If database connection is not set
     */
    private Connection getConnection() throws DatabaseException {
        if (conn == null) {
            TransportLogger.getSysErrorLogger().fatal(
                    Messages.Database_2.toString());
            throw new DatabaseException();
        }
        return conn;
    }


    /**
     * Returns an unmanaged connection that the calling function can use to
     * start a transaction in the database.
     *
     * @return Connection object
     * @throws DatabaseException
     *         If error communicating with database
     */
    public static Connection getUnmanagedConnection() throws DatabaseException {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(CONNECTIONURL);
        }
        catch (ClassNotFoundException e) {
            TransportLogger.getSysErrorLogger().fatal(
                    Messages.Database_0.toString(), e);
            throw new DatabaseException(e);
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().fatal(
                    Messages.Database_3.toString(), e);
            throw new DatabaseException(e);
        }
    }


    /**
     * Create the database schema
     *
     * @throws DatabaseException
     *         If error occurs creating schema
     */
    private void createSchema() throws DatabaseException {
        try {
            executeCommand(SQLString.CreateSchemaAMTU);
            executeCommand(SQLString.CreateTableSchemaVersion);
            executeCommand(SQLString.CreateTableAmtuAccount);
            executeCommand(SQLString.CreateTableAccountConfig);
            executeCommand(SQLString.CreateTableMerchantFeedSubmissionQueue);
            executeCommand(SQLString.CreateTableMerchantSiteGroup);
            executeCommand(SQLString.CreateTableMerchantSite);
            executeCommand(SQLString.CreateTableMerchantReport);
            executeCommand(SQLString.CreateTableMerchantFeed);
            executeCommand(SQLString.CreateTableProxyConfig);

            initializeSchema();
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().fatal(
                    Messages.Database_5.toString(), e);
            throw new DatabaseException(e);
        }
    }


    /**
     * Initializes the schema, setting the schema version
     *
     * @throws SQLException
     *         If error occurs updating schema version
     * @throws DatabaseException
     *         If error occurs communicating with database
     */
    private void initializeSchema() throws SQLException, DatabaseException {
        PreparedStatement ps = null;
        try {
            ps = getPreparedStatement(SQLString.InitializeSchemaVersion);
            ps.setInt(1, SQLString.SCHEMA_VERSION);
            ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Retrieves the current schema version
     *
     * @return Database schema version
     * @throws SQLException
     *         If error occurs pulling schema version
     * @throws DatabaseException
     *         If error occurs communicating with database
     */
    private int getDatabaseSchemaVersion() throws SQLException,
            DatabaseException {
        int schemaVersion = -1;

        Statement s = null;
        ResultSet rs = null;
        try {
            s = getConnection().createStatement();
            rs = s.executeQuery(SQLString.CheckCurrentSchemaVersion);
            if (rs.next()) {
                schemaVersion = rs.getInt(SQL_COLUMNS.version_num.toString());
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (s != null) {
                try {
                    s.close();
                }
                catch (Exception e) {
                }
            }
        }

        return schemaVersion;
    }


    /**
     * Attempts to update the schema version
     *
     * @throws SQLException
     *         If error occurs updating the AMTU schema
     * @throws DatabaseException
     *         If error occurs communicating with the database or if current
     *         AMTU schema is invalid
     */
    private void updateSchema() throws SQLException, DatabaseException {
        int currentVersion = getDatabaseSchemaVersion();

        if (currentVersion <= 0) {
            // invalid schema
            throw new DatabaseException(Messages.Database_8.toString());
        }
        else if (currentVersion > SQLString.SCHEMA_VERSION) {
            // database is more advanced than the AMTU installation
            // fail and request user update AMTU installation
            throw new DatabaseException(Messages.Database_9.toString());
        }

        if (currentVersion < SQLString.SCHEMA_VERSION) {
            backupDatabase();

            while (currentVersion < SQLString.SCHEMA_VERSION) {
                executeUpdateStep(++currentVersion);
            }
        }
    }


    /**
     * Backs up the current database to a separate folder in case of upgrade
     * failure
     *
     * @throws SQLException
     *         Error in backup call
     * @throws DatabaseException
     *         Error communicating with database
     */
    private void backupDatabase() throws SQLException, DatabaseException {
        DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String backupDirectory = DBNAME + ".back-"
                + formatter.format(new Date());
        CallableStatement cs = getConnection().prepareCall(
                "CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)");
        cs.setString(1, backupDirectory);
        cs.execute();
        cs.close();

        TransportLogger.getSysAuditLogger()
                .info(String.format(Messages.Database_11.toString(),
                        backupDirectory));
    }


    /**
     * Executes a specific schema upgrade step
     *
     * @param updateStep
     *        Schema upgrade step to execute
     * @throws SQLException
     *         If error occurs executing SQL
     * @throws DatabaseException
     *         If error occurs communicating with the database
     */
    private void executeUpdateStep(int updateStep) throws SQLException,
            DatabaseException {
        if (updateStep < 0 || updateStep >= SQLString.UPDATE_STEPS.length) {
            return;
        }

        Object updateStepObject = SQLString.UPDATE_STEPS[updateStep];

        if (updateStepObject == null) {
            return;
        }

        String[] updateSteps = (String[]) updateStepObject;

        for (String step : updateSteps) {
            executeCommand(step);
        }
    }


    /**
     * Check if the database schema has been created
     *
     * @return Whether the schema was created successfully
     * @throws DatabaseException
     *         If error occurs communicating with the database
     */
    private boolean isSchemaCreated() throws DatabaseException {
        Statement s = null;
        ResultSet rs = null;
        try {
            s = getConnection().createStatement();
            rs = s.executeQuery(SQLString.QueryIsSchemaCreated);
            while (rs.next()) {
                return rs.getInt(SQL_COLUMNS.count.toString()) == 1;
            }
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().error(
                    Messages.Database_6.toString(), e);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (s != null) {
                try {
                    s.close();
                }
                catch (Exception e) {
                }
            }
        }
        return false;
    }


    /**
     * Executes a SQL statement
     *
     * @param sql
     *        SQL statement to execute
     * @throws SQLException
     *         If error occurs executing SQL
     * @throws DatabaseException
     *         If error occurs communicating with the database
     */
    private void executeCommand(String sql) throws SQLException,
            DatabaseException {
        Statement s = null;
        try {
            s = getConnection().createStatement();
            s.execute(sql);
        }
        finally {
            if (s != null) {
                try {
                    s.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Gets a prepared statement for the given SQL
     *
     * @param sql
     *        SQL statement to prepare
     * @return Prepared statement from SQL
     * @throws SQLException
     *         If error occurs preparing SQL
     * @throws DatabaseException
     *         If error occurs communicating with the database
     */
    private PreparedStatement getPreparedStatement(String sql)
            throws SQLException, DatabaseException {
        return getConnection().prepareStatement(sql);
    }


    /**
     * Pulls the account details for the AMTU account specified by the account
     * ID
     *
     * @param accountId
     *        Account ID to pull
     * @return Map of account details
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public Map<String, String> pullAmtuAccountDetailsById(int accountId)
            throws SQLException, DatabaseException {
        Map<String, String> accountDetails = null;

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectAmtuAccountById);
            ps.setInt(1, accountId);
            rs = ps.executeQuery();
            if (rs.next()) {
                accountDetails = new HashMap<String, String>();

                ResultSetMetaData rsMeta = rs.getMetaData();
                int count = rsMeta.getColumnCount();
                for (int i = 1; i <= count; i++) {
                    accountDetails.put(rsMeta.getColumnName(i).toLowerCase(),
                            rs.getString(i));
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return accountDetails;
    }


    /**
     * Pulls a list of all AMTU account IDs registered in the application
     *
     * @return List of account IDs
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public List<Integer> listAllAmtuAccountIds() throws SQLException,
            DatabaseException {
        List<Integer> amtuAccountIds = new ArrayList<Integer>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectAllAmtuAccounts);
            rs = ps.executeQuery();
            while (rs.next()) {
                amtuAccountIds.add(rs
                        .getInt(AMTUAccount.SQL_COLUMNS.amtu_account_id
                                .toString()));
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return amtuAccountIds;
    }


    /**
     * Retrieves the AMTU account ID for the given account alias. If no account
     * is found with that alias, returns null
     *
     * @param alias
     *        AMTU account alias
     * @return Integer object with account ID, or null if no account found
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public Integer pullAmtuAccountIdByAlias(String alias) throws SQLException,
            DatabaseException {
        Integer accountId = null;

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectAmtuAccountIdByAlias);
            ps.setString(1, alias);
            rs = ps.executeQuery();
            if (rs.next()) {
                accountId = rs.getInt(AMTUAccount.SQL_COLUMNS.amtu_account_id
                        .toString());
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return accountId;
    }


    /**
     * Creates a new AMTU account
     *
     * @param con
     *        Database connection
     * @param alias
     *        Account alias
     * @param mwsAccessKey
     *        MWS access key
     * @param mwsSecretKey
     *        MWS secret key
     * @param merchantId
     *        Amazon Merchant ID
     * @param endpointCode
     *        2-character code representing MWS endpoint
     * @param documentTransport
     *        Full path of document transport directory
     * @return Account ID of new account, -1 if create failed
     * @throws SQLException
     *         Error in SQL
     */
    public int createAMTUAccount(Connection con, String alias,
            String mwsAccessKey, String mwsSecretKey, String merchantId,
            String endpointCode, String documentTransport) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQLString.InsertAmtuAccount,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, alias);
            ps.setString(2, mwsAccessKey);
            ps.setString(3, mwsSecretKey);
            ps.setString(4, merchantId);
            ps.setString(5, endpointCode);
            ps.setString(6, documentTransport);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            else {
                return -1;
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Updates an AMTU account
     *
     * @param con
     *        Database connection
     * @param alias
     *        Account alias
     * @param mwsAccessKey
     *        MWS Access Key
     * @param mwsSecretKey
     *        MWS Secret Key
     * @param merchantId
     *        Amazon Merchant ID
     * @param endpointCode
     *        2-character code representing MWS endpoint
     * @param documentTransport
     *        Full path of document transport directory
     * @param accountId
     *        AMTU account ID to update
     * @return Number of rows updated
     * @throws SQLException
     *         Error in SQL
     */
    public int updateAMTUAccount(Connection con, String alias,
            String mwsAccessKey, String mwsSecretKey, String merchantId,
            String endpointCode, String documentTransport, int accountId)
            throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.UpdateAmtuAccount);
            ps.setString(1, alias);
            ps.setString(2, mwsAccessKey);
            ps.setString(3, mwsSecretKey);
            ps.setString(4, merchantId);
            ps.setString(5, endpointCode);
            ps.setString(6, documentTransport);
            ps.setInt(7, accountId);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Deletes an AMTU account
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID to delete
     * @return Number of rows deleted
     * @throws SQLException
     *         Error in SQL
     */
    public int deleteAMTUAccount(Connection con, int accountId)
            throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.DeleteAmtuAccount);
            ps.setInt(1, accountId);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Pulls the account config information for the given AMTU account
     *
     * @param accountId
     *        AMTU account ID
     * @return Map of account configuration keys
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public Map<String, String> pullAccountConfig(int accountId)
            throws SQLException, DatabaseException {
        Map<String, String> config = new HashMap<String, String>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectAccountConfig);
            ps.setInt(1, accountId);
            rs = ps.executeQuery();
            while (rs.next()) {
                config.put(rs.getString(AccountConfig.SQL_COLUMNS.config_key
                        .toString()), rs
                        .getString(AccountConfig.SQL_COLUMNS.config_value
                                .toString()));
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return config;
    }


    /**
     * Deletes a specific account configuration key
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @param key
     *        Configuration key to delete
     * @return Number of rows deleted
     * @throws SQLException
     *         Error in SQL
     */
    public int deleteAccountConfigByKey(Connection con, int accountId,
            String key) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.DeleteAccountConfigKey);
            ps.setInt(1, accountId);
            ps.setString(2, key);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Updates the value of a specific account configuration key
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @param key
     *        Configuration key to update
     * @param value
     *        Value for configuration key
     * @return Number of rows updated
     * @throws SQLException
     *         Error in SQL
     */
    public int updateAccountConfigByKey(Connection con, int accountId,
            String key, String value) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.UpdateAccountConfigKey);
            ps.setString(1, value);
            ps.setInt(2, accountId);
            ps.setString(3, key);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Inserts a new configuration key
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @param key
     *        Configuration key to insert
     * @param value
     *        Value for configuration key
     * @return Number of rows inserted
     * @throws SQLException
     *         Error in SQL
     */
    public int insertAccountConfigByKey(Connection con, int accountId,
            String key, String value) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.InsertAccountConfigKey);
            ps.setInt(1, accountId);
            ps.setString(2, key);
            ps.setString(3, value);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Deletes all configuration for the given account
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @return Number of rows deleted
     * @throws SQLException
     *         Error in SQL
     */
    public int deleteAccountConfig(Connection con, int accountId)
            throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.DeleteAccountConfig);
            ps.setInt(1, accountId);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Pulls a list of all site group IDs for the given AMTU account
     *
     * @param accountId
     *        AMTU account ID
     * @return List of site group IDs
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public List<Integer> listAllSiteGroupsByAmtuAccount(int accountId)
            throws SQLException, DatabaseException {
        List<Integer> siteGroups = new ArrayList<Integer>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectAllSiteGroupsForAccount);
            ps.setInt(1, accountId);
            rs = ps.executeQuery();
            while (rs.next()) {
                siteGroups.add(rs
                        .getInt(MerchantSiteGroup.SQL_COLUMNS.site_group_id
                                .toString()));
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return siteGroups;
    }


    /**
     * Pulls the details for a given merchant site group
     *
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @return Map of site group details
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public Map<String, String> pullSiteGroupDetails(int accountId,
            int siteGroupId) throws SQLException, DatabaseException {
        Map<String, String> siteGroupDetails = null;

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectSiteGroupById);
            ps.setInt(1, accountId);
            ps.setInt(2, siteGroupId);
            rs = ps.executeQuery();
            if (rs.next()) {
                siteGroupDetails = new HashMap<String, String>();

                ResultSetMetaData rsMeta = rs.getMetaData();
                int count = rsMeta.getColumnCount();
                for (int i = 1; i <= count; i++) {
                    siteGroupDetails.put(rsMeta.getColumnName(i).toLowerCase(),
                            rs.getString(i));
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return siteGroupDetails;
    }


    /**
     * Updates a merchant site group
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @param merchantAlias
     *        Site Group alias
     * @param siteGroupDocumentTransport
     *        Site Group document transport directory
     * @return Number of rows updated
     * @throws SQLException
     *         Error in SQL
     */
    public int updateMerchantSiteGroup(Connection con, int accountId,
            int siteGroupId, String merchantAlias,
            String siteGroupDocumentTransport) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.UpdateMerchantSiteGroup);
            ps.setString(1, merchantAlias);
            ps.setString(2, siteGroupDocumentTransport);
            ps.setInt(3, accountId);
            ps.setInt(4, siteGroupId);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Creates a new merchant site group and returns the ID of the new site
     * group
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @param merchantAlias
     *        Site Group alias
     * @param siteGroupDocumentTransport
     *        Site Group document transport directory
     * @return ID of new site group, -1 if creation failed
     * @throws SQLException
     *         Error in SQL
     */
    public int createMerchantSiteGroup(Connection con, int accountId,
            String merchantAlias, String siteGroupDocumentTransport)
            throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQLString.InsertMerchantSiteGroup,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, accountId);
            ps.setString(2, merchantAlias);
            ps.setString(3, siteGroupDocumentTransport);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            else {
                return -1;
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Deletes a merchant site group
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @return Number of rows deleted
     * @throws SQLException
     *         Error in SQL
     */
    public int deleteMerchantSiteGroup(Connection con, int accountId,
            int siteGroupId) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.DeleteMerchantSiteGroup);
            ps.setInt(1, accountId);
            ps.setInt(2, siteGroupId);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Deletes all feeds for a given merchant site group
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @return Number of rows deleted
     * @throws SQLException
     *         Error in SQL
     */
    public int deleteMerchantSiteGroupFeeds(Connection con, int accountId,
            int siteGroupId) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.DeleteFeedsForMerchantSiteGroup);
            ps.setInt(1, accountId);
            ps.setInt(2, siteGroupId);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Deletes all reports for a given merchant site group
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @return Number of rows deleted
     * @throws SQLException
     *         Error in SQL
     */
    public int deleteMerchantSiteGroupReports(Connection con, int accountId,
            int siteGroupId) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.DeleteReportsForMerchantSiteGroup);
            ps.setInt(1, accountId);
            ps.setInt(2, siteGroupId);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Pulls a list of all sites in a given merchant site group
     *
     * @param siteGroupId
     *        Merchant Site Group ID
     * @return List of merchant site IDs
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public List<Integer> listAllSitesBySiteGroup(int siteGroupId)
            throws SQLException, DatabaseException {
        List<Integer> siteIds = new ArrayList<Integer>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectAllSitesForSiteGroup);
            ps.setInt(1, siteGroupId);
            rs = ps.executeQuery();
            while (rs.next()) {
                siteIds.add(rs.getInt(MerchantSite.SQL_COLUMNS.site_id
                        .toString()));
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return siteIds;
    }


    /**
     * Pulls the details for a specific merchant site
     *
     * @param siteGroupId
     *        Merchant Site Group ID
     * @param siteId
     *        Merchant Site ID
     * @return Map of site details
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public Map<String, String> pullMerchantSiteDetails(int siteGroupId,
            int siteId) throws SQLException, DatabaseException {
        Map<String, String> siteDetails = null;

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectMerchantSiteById);
            ps.setInt(1, siteGroupId);
            ps.setInt(2, siteId);
            rs = ps.executeQuery();
            if (rs.next()) {
                siteDetails = new HashMap<String, String>();

                ResultSetMetaData rsMeta = rs.getMetaData();
                int count = rsMeta.getColumnCount();
                for (int i = 1; i <= count; i++) {
                    siteDetails.put(rsMeta.getColumnName(i).toLowerCase(),
                            rs.getString(i));
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return siteDetails;
    }


    /**
     * Updates a merchant site
     *
     * @param con
     *        Database connection
     * @param siteGroupId
     *        Merchant Site Group ID
     * @param siteId
     *        Merchant Site ID
     * @param merchantAlias
     *        Merchant alias
     * @param marketplaceId
     *        Amazon Marketplace Site ID
     * @return Number of rows updated
     * @throws SQLException
     *         Error in SQL
     */
    public int updateMerchantSite(Connection con, int siteGroupId, int siteId,
            String merchantAlias, String marketplaceId) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.UpdateMerchantSite);
            ps.setString(1, marketplaceId);
            ps.setString(2, merchantAlias);
            ps.setInt(3, siteGroupId);
            ps.setInt(4, siteId);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Creates a new merchant site
     *
     * @param con
     *        Database connection
     * @param siteGroupId
     *        Merchant Site Group ID
     * @param merchantAlias
     *        Merchant alias
     * @param marketplaceId
     *        Amazon Marketplace Site ID
     * @return ID of new merchant site, or -1 if creation failed
     * @throws SQLException
     *         Error in SQL
     */
    public int createMerchantSite(Connection con, int siteGroupId,
            String merchantAlias, String marketplaceId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQLString.InsertMerchantSite,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, siteGroupId);
            ps.setString(2, marketplaceId);
            ps.setString(3, merchantAlias);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            else {
                return -1;
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Deletes a merchant site
     *
     * @param con
     *        Database connection
     * @param siteGroupId
     *        Merchant Site Group ID
     * @param siteId
     *        Merchant Site ID
     * @return Number of rows deleted
     * @throws SQLException
     *         Error in SQL
     */
    public int deleteMerchantSite(Connection con, int siteGroupId, int siteId)
            throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.DeleteMerchantSite);
            ps.setInt(1, siteGroupId);
            ps.setInt(2, siteId);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Pulls the feed submission queue for the AMTU account
     *
     * @param accountId
     *        AMTU Account ID
     * @return Feed submission queue
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public Queue<MerchantQueueSubmission> pullSubmissionQueueForMerchant(
            int accountId) throws SQLException, DatabaseException {
        Queue<MerchantQueueSubmission> queue = new ConcurrentLinkedQueue<MerchantQueueSubmission>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectFeedQueueForMerchant);
            ps.setInt(1, accountId);
            rs = ps.executeQuery();
            while (rs.next()) {
                MerchantQueueSubmission sub = new MerchantQueueSubmission();
                sub.setSubmissionId(rs
                        .getString(MerchantFeedQueue.SQL_COLUMNS.feed_submission_id
                                .toString()));
                sub.setSubmissionTime(rs
                        .getTimestamp(MerchantFeedQueue.SQL_COLUMNS.submission_time
                                .toString()));
                queue.offer(sub);
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return queue;
    }


    /**
     * Deletes a specific feed from the AMTU account's submission queue
     *
     * @param accountId
     *        AMTU account ID
     * @param feedSubmissionId
     *        Feed submission ID
     * @param submissionTime
     *        Feed submission Time
     * @return Number of rows deleted
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public int deleteFromFeedQueueForMerchant(int accountId,
            String feedSubmissionId, Date submissionTime) throws SQLException,
            DatabaseException {
        PreparedStatement ps = null;
        try {
            ps = getPreparedStatement(SQLString.DeleteFeedFromMerchantFeedQueue);
            ps.setInt(1, accountId);
            ps.setString(2, feedSubmissionId);
            ps.setTimestamp(3, new java.sql.Timestamp(submissionTime.getTime()));
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Adds a feed to the AMTU account's submission queue
     *
     * @param accountId
     *        AMTU account ID
     * @param feedSubmissionId
     *        Feed submission ID
     * @param submissionTime
     *        Feed submission time
     * @return Number of rows inserted
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public int addFeedToMerchantQueue(int accountId, String feedSubmissionId,
            Date submissionTime) throws SQLException, DatabaseException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.AddFeedToMerchantFeedQueue);
            ps.setInt(1, accountId);
            ps.setString(2, feedSubmissionId);
            ps.setTimestamp(3, new java.sql.Timestamp(submissionTime.getTime()));
            return ps.executeUpdate();
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Adds a feed to the AMTU account's submission queue using an unmanaged
     * connection
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @param feedSubmissionId
     *        Feed submission ID
     * @param submissionTime
     *        Feed submission time
     * @return Number of rows inserted
     * @throws SQLException
     *         Error in SQL
     */
    public int addFeedToMerchantQueue(Connection con, int accountId,
            String feedSubmissionId, Date submissionTime) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQLString.AddFeedToMerchantFeedQueue);
            ps.setInt(1, accountId);
            ps.setString(2, feedSubmissionId);
            ps.setTimestamp(3, new java.sql.Timestamp(submissionTime.getTime()));
            return ps.executeUpdate();
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Empties the AMTU account's feed submission queue
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @throws SQLException
     *         Error in SQL
     */
    public int deleteAllFromMerchantFeedQueue(Connection con, int accountId)
            throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.DeleteAllFromMerchantFeedQueue);
            ps.setInt(1, accountId);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Pulls a list of all merchant feeds for a given date range
     *
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @param fromDate
     *        Beginning of date range
     * @param endDate
     *        End of date range
     * @return List of merchant feed details for the given date range
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public List<HashMap<String, Object>> listMerchantFeedsForDateRange(
            int accountId, int siteGroupId, Date fromDate, Date endDate)
            throws SQLException, DatabaseException {
        List<HashMap<String, Object>> feedList = new ArrayList<HashMap<String, Object>>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectMerchantFeedsForSiteGroupInRange);
            ps.setInt(1, accountId);
            ps.setInt(2, siteGroupId);
            ps.setDate(3, new java.sql.Date(fromDate.getTime()));
            ps.setDate(4, new java.sql.Date(endDate.getTime()));
            rs = ps.executeQuery();

            ResultSetMetaData rsMeta = rs.getMetaData();
            int count = rsMeta.getColumnCount();
            while (rs.next()) {
                HashMap<String, Object> feed = new HashMap<String, Object>();

                for (int i = 1; i <= count; i++) {
                    feed.put(rsMeta.getColumnName(i).toLowerCase(),
                            rs.getObject(i));
                }

                feedList.add(feed);
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return feedList;
    }


    /**
     * Inserts a new merchant feed
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @param merchantReportId
     *        Merchant Report ID
     * @param fileName
     *        Feed file name
     * @param feedSubmissionId
     *        Feed submission ID
     * @param feedType
     *        Feed type
     * @param feedProcessingStatus
     *        Feed processing status
     * @param feedSubmittedDate
     *        Feed submitted date
     * @param feedSize
     *        Feed size in KB
     * @param dateCreated
     *        Date the feed record was created
     * @param dateUpdated
     *        Date the feed record was updated
     * @return ID of new merchant feed, or -1 if create failed
     * @throws SQLException
     *         Error in SQL
     */
    public int insertMerchantFeed(Connection con, int accountId,
            int siteGroupId, Integer merchantReportId, String fileName,
            String feedSubmissionId, String feedType,
            String feedProcessingStatus, String feedSubmittedDate,
            long feedSize, Date dateCreated, Date dateUpdated)
            throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQLString.InsertMerchantFeed,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, accountId);
            ps.setInt(2, siteGroupId);
            if (merchantReportId == null) {
                ps.setNull(3, Types.INTEGER);
            }
            else {
                ps.setInt(3, merchantReportId);
            }
            ps.setString(4, fileName);
            ps.setString(5, feedSubmissionId);
            ps.setString(6, feedType);
            ps.setString(7, feedProcessingStatus);
            ps.setString(8, feedSubmittedDate);
            ps.setLong(9, feedSize);
            ps.setTimestamp(10, new java.sql.Timestamp(dateCreated.getTime()));
            ps.setTimestamp(11, new java.sql.Timestamp(dateUpdated.getTime()));
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            else {
                return -1;
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Updates a merchant feed
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @param merchantFeedId
     *        Merchant Feed ID
     * @param reportId
     *        Merchant Report ID
     * @param fileName
     *        Feed file name
     * @param feedSubmissionId
     *        Feed submission ID
     * @param feedType
     *        Feed type
     * @param feedProcessingStatus
     *        Feed processing status
     * @param feedSubmittedDate
     *        Feed submitted date
     * @param feedSize
     *        Feed size in KB
     * @param dateCreated
     *        Date feed record was created
     * @param dateUpdated
     *        Date feed record was updated
     * @return Number of rows updated
     * @throws SQLException
     *         Error in SQL
     */
    public int updateMerchantFeed(Connection con, int accountId,
            int siteGroupId, int merchantFeedId, Integer reportId,
            String fileName, String feedSubmissionId, String feedType,
            String feedProcessingStatus, String feedSubmittedDate,
            long feedSize, Date dateCreated, Date dateUpdated)
            throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.UpdateMerchantFeed);
            if (reportId == null) {
                ps.setNull(1, Types.INTEGER);
            }
            else {
                ps.setInt(1, reportId);
            }
            ps.setString(2, fileName);
            ps.setString(3, feedSubmissionId);
            ps.setString(4, feedType);
            ps.setString(5, feedProcessingStatus);
            ps.setString(6, feedSubmittedDate);
            ps.setLong(7, feedSize);
            ps.setTimestamp(8, new java.sql.Timestamp(dateCreated.getTime()));
            ps.setTimestamp(9, new java.sql.Timestamp(dateUpdated.getTime()));
            ps.setInt(10, accountId);
            ps.setInt(11, siteGroupId);
            ps.setInt(12, merchantFeedId);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Deletes a merchant feed
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @param merchantFeedId
     *        Merchant Feed ID
     * @throws SQLException
     *         Error in SQL
     */
    public int deleteMerchantFeed(Connection con, int accountId,
            int siteGroupId, int merchantFeedId) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.DeleteMerchantFeed);
            ps.setInt(1, accountId);
            ps.setInt(2, siteGroupId);
            ps.setInt(3, merchantFeedId);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Pulls a list of IDs of incomplete feeds for a given merchant site group
     *
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @return List of merchant feed IDs
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public List<Integer> listIncompleteFeedsForSiteGroup(int accountId,
            int siteGroupId) throws SQLException, DatabaseException {
        List<Integer> incomplete = new ArrayList<Integer>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectIncompleteFeedsForSiteGroup);
            ps.setInt(1, accountId);
            ps.setInt(2, siteGroupId);
            rs = ps.executeQuery();
            while (rs.next()) {
                incomplete.add(rs
                        .getInt(MerchantFeed.SQL_COLUMNS.merchant_feed_id
                                .toString()));
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return incomplete;
    }


    /**
     * Pulls a list of IDs of completed feeds for a given merchant site group
     * that have not yet pulled processing reports
     *
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @return List of merchant feed IDs
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public List<Integer> listCompleteFeedsWithoutProcessingReportForSiteGroup(
            int accountId, int siteGroupId) throws SQLException,
            DatabaseException {
        List<Integer> needsReport = new ArrayList<Integer>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectCompleteFeedsWithoutProcessingReport);
            ps.setInt(1, accountId);
            ps.setInt(2, siteGroupId);
            rs = ps.executeQuery();
            while (rs.next()) {
                needsReport.add(rs
                        .getInt(MerchantFeed.SQL_COLUMNS.merchant_feed_id
                                .toString()));
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return needsReport;
    }


    /**
     * Pulls details of a merchant feed
     *
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @param merchantFeedId
     *        Merchant Feed ID
     * @return Map of feed details
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public HashMap<String, Object> pullFeedDetailsById(int accountId,
            int siteGroupId, int merchantFeedId) throws SQLException,
            DatabaseException {
        HashMap<String, Object> feed = new HashMap<String, Object>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectMerchantFeedById);
            ps.setInt(1, accountId);
            ps.setInt(2, siteGroupId);
            ps.setInt(3, merchantFeedId);
            rs = ps.executeQuery();
            if (rs.next()) {
                ResultSetMetaData rsMeta = rs.getMetaData();
                int count = rsMeta.getColumnCount();
                for (int i = 1; i <= count; i++) {
                    feed.put(rsMeta.getColumnName(i).toLowerCase(),
                            rs.getObject(i));
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return feed;
    }


    /**
     * Pulls details of a merchant report
     *
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @param merchantReportId
     *        Merchant Report ID
     * @return Map of report details
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public HashMap<String, Object> pullReportDetailsById(int accountId,
            int siteGroupId, int merchantReportId) throws SQLException,
            DatabaseException {
        HashMap<String, Object> details = new HashMap<String, Object>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectMerchantReportById);
            ps.setInt(1, accountId);
            ps.setInt(2, siteGroupId);
            ps.setInt(3, merchantReportId);
            rs = ps.executeQuery();
            if (rs.next()) {
                ResultSetMetaData rsMeta = rs.getMetaData();
                int count = rsMeta.getColumnCount();
                for (int i = 1; i <= count; i++) {
                    details.put(rsMeta.getColumnName(i).toLowerCase(),
                            rs.getObject(i));
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return details;
    }


    /**
     * Pulls a list of merchant report details for the given date range
     *
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @param fromDate
     *        Beginning of date range
     * @param endDate
     *        End of date range
     * @return List of report details
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public List<HashMap<String, Object>> listMerchantReportsForDateRange(
            int accountId, int siteGroupId, Date fromDate, Date endDate)
            throws SQLException, DatabaseException {
        List<HashMap<String, Object>> reportList = new ArrayList<HashMap<String, Object>>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectMerchantReportsForSiteGroupInRange);
            ps.setInt(1, accountId);
            ps.setInt(2, siteGroupId);
            ps.setTimestamp(3, new java.sql.Timestamp(fromDate.getTime()));
            ps.setTimestamp(4, new java.sql.Timestamp(endDate.getTime()));
            rs = ps.executeQuery();
            while (rs.next()) {
                int merchantReportId = rs
                        .getInt(MerchantReport.SQL_COLUMNS.merchant_report_id
                                .toString());
                HashMap<String, Object> details = pullReportDetailsById(
                        accountId, siteGroupId, merchantReportId);
                if (details != null && !details.isEmpty()) {
                    reportList.add(details);
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return reportList;
    }


    /**
     * Creates a new merchant report
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @param fileName
     *        Report file name
     * @param reportId
     *        Amazon Report ID
     * @param reportType
     *        Report type
     * @param acknowledged
     *        Whether report has been acknowledged
     * @param reportSize
     *        Report size in KB
     * @param dateReceived
     *        Date report received
     * @param dateModified
     *        Date report record last modified
     * @return ID of new merchant report; -1 if creation failed
     * @throws SQLException
     *         Error in SQL
     */
    public int insertMerchantReport(Connection con, int accountId,
            int siteGroupId, String fileName, String reportId,
            String reportType, boolean acknowledged, long reportSize,
            Date dateReceived, Date dateModified) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQLString.InsertMerchantReport,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, accountId);
            ps.setInt(2, siteGroupId);
            ps.setString(3, fileName);
            ps.setString(4, reportId);
            ps.setString(5, reportType);
            ps.setString(6, acknowledged ? AccountConfig.BOOLEAN_TRUE
                    : AccountConfig.BOOLEAN_FALSE);
            ps.setLong(7, reportSize);
            ps.setTimestamp(8, new java.sql.Timestamp(dateReceived.getTime()));
            ps.setTimestamp(9, new java.sql.Timestamp(dateModified.getTime()));
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            else {
                return -1;
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Updates a merchant report
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @param merchantReportId
     *        Merchant Report ID
     * @param fileName
     *        Report file name
     * @param reportId
     *        Amazon report ID
     * @param reportType
     *        Report type
     * @param acknowledged
     *        Whether the report has been acknowledged
     * @param reportSize
     *        Report size in KB
     * @param dateReceived
     *        Date report received
     * @param dateModified
     *        Date report record last modified
     * @return Number of rows updated
     * @throws SQLException
     *         Error in SQL
     */
    public int updateMerchantReport(Connection con, int accountId,
            int siteGroupId, int merchantReportId, String fileName,
            String reportId, String reportType, boolean acknowledged,
            long reportSize, Date dateReceived, Date dateModified)
            throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.UpdateMerchantReport);
            ps.setString(1, fileName);
            ps.setString(2, reportId);
            ps.setString(3, reportType);
            ps.setString(4, acknowledged ? AccountConfig.BOOLEAN_TRUE
                    : AccountConfig.BOOLEAN_FALSE);
            ps.setLong(5, reportSize);
            ps.setTimestamp(6, new java.sql.Timestamp(dateReceived.getTime()));
            ps.setTimestamp(7, new java.sql.Timestamp(dateModified.getTime()));
            ps.setInt(8, accountId);
            ps.setInt(9, siteGroupId);
            ps.setInt(10, merchantReportId);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Deletes a merchant report
     *
     * @param con
     *        Database connection
     * @param accountId
     *        AMTU account ID
     * @param siteGroupId
     *        Merchant Site Group ID
     * @param merchantReportId
     *        Merchant Report ID
     * @returns Number of rows deleted
     * @throws SQLException
     *         Error in SQL
     */
    public int deleteMerchantReport(Connection con, int accountId,
            int siteGroupId, int merchantReportId) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.DeleteMerchantReport);
            ps.setInt(1, accountId);
            ps.setInt(2, siteGroupId);
            ps.setInt(3, merchantReportId);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }


    /**
     * Pulls the AMTU account ID for a given Amazon Merchant ID
     *
     * @param merchantId
     *        Amazon Merchant ID
     * @return AMTU account ID; -1 if merchant ID not found
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public int getAccountIdForMerchantId(String merchantId)
            throws SQLException, DatabaseException {
        int accountId = -1;

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectAccountForMerchantId);
            ps.setString(1, merchantId);
            rs = ps.executeQuery();
            if (rs.next()) {
                accountId = rs.getInt(AMTUAccount.SQL_COLUMNS.amtu_account_id
                        .toString());
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return accountId;
    }


    /**
     * Pulls the AMTU account ID for a given document transport directory
     *
     * @param documentTransport
     *        Full path of document transport directory
     * @return AMTU account ID; -1 if DTD not found
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public int getAccountIdForDocumentTransport(String documentTransport)
            throws SQLException, DatabaseException {
        int accountId = -1;

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectAccountForDocumentTransport);
            ps.setString(1, documentTransport);
            rs = ps.executeQuery();
            if (rs.next()) {
                accountId = rs.getInt(AMTUAccount.SQL_COLUMNS.amtu_account_id
                        .toString());
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return accountId;
    }


    /**
     * Pulls the AMTU account ID for a given MWS access key and Amazon Merchant
     * ID
     *
     * @param mwsAccessKey
     *        MWS access key
     * @param merchantId
     *        Amazon Merchant ID
     * @return AMTU account ID; -1 if MWS access key not found with the merchant
     *         ID
     * @throws SQLException
     *         Error in SQL
     * @throws DatabaseException
     *         Error communicating with database
     */
    public int getAccountIdForMwsAccessKeyAndMerchantId(String mwsAccessKey,
            String merchantId) throws SQLException, DatabaseException {
        int accountId = -1;

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectAccountForMwsAccessKeyAndMerchantId);
            ps.setString(1, mwsAccessKey);
            ps.setString(2, merchantId);
            rs = ps.executeQuery();
            if (rs.next()) {
                accountId = rs.getInt(AMTUAccount.SQL_COLUMNS.amtu_account_id
                        .toString());
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return accountId;
    }


    public List<String> getSiteGroupDirectoriesForAccount(AMTUAccount account)
            throws SQLException, DatabaseException {
        List<String> siteDirectoryNames = new ArrayList<String>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectAllSiteGroupDirectoriesForAccount);
            ps.setInt(1, account.getAccountId());
            rs = ps.executeQuery();
            while (rs.next()) {
                siteDirectoryNames.add(rs
                        .getString(MerchantSiteGroup.SQL_COLUMNS.site_group_dtd
                                .toString()));
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return siteDirectoryNames;
    }
    
    /**
     * Pulls proxy configuration information based upon host and ID
     * 
     * @param proxyId
     *        AMTU system proxy ID
     * @param proxyHost
     * 	      Network proxy host
     * @return HashMap of query results of proxy info
     * @throws SQLException
     * @throws DatabaseException
     */
    public HashMap<String, Object> pullProxyInfo() throws SQLException,
            DatabaseException {
        HashMap<String, Object> details = new HashMap<String, Object>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectProxyConfig);
            rs = ps.executeQuery();
            if (rs.next()) {
                ResultSetMetaData rsMeta = rs.getMetaData();
                int count = rsMeta.getColumnCount();
                for (int i = 1; i <= count; i++) {
                    details.put(rsMeta.getColumnName(i).toLowerCase(),
                            rs.getObject(i));
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return details;
    }
    
    /**
     * Pulls the total number of proxy configurations
     * 
     * @return number of proxy configuratiosn
     * @throws SQLException
     * @throws DatabaseException
     */
    public int pullProxyCount() throws SQLException,
            DatabaseException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getPreparedStatement(SQLString.SelectProxyCount);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }

        return 0;
    }
    
    /**
     * 
     * @param con
     * 		  Database connection
     * @param proxyId
     * 		  AMTU System proxy ID
     * @param proxyHost
     * 		  Network proxy host
     * @param proxyPort
     * 		  Network proxy port number
     * @param proxyUser
     * 		  Network proxy username
     * @param proxyPass
     * 		  Network proxy password
     * @return
     * @throws SQLException
     */
    public int createProxyConfig(Connection con, String proxyHost, 
    		int proxyPort, String proxyUser, String proxyPass) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(SQLString.InsertProxyConfigHost,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, proxyHost);
            ps.setInt(2, proxyPort);
            ps.setString(3, proxyUser);
            ps.setString(4, proxyPass);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            else {
                return -1;
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }
    
    /**
     * Updates a proxy configuration
     * 
     * @param con
     * 		  Database connection
     * @param proxyId
     * 		  AMTU system proxy ID
     * @param proxyHost
     * 		  Network proxy host
     * @param proxyPort
     * 		  Network proxy port
     * @param proxyUser
     * 		  Network proxy username
     * @param proxyPass
     * 		  Network proxy password
     * @return
     * @throws SQLException
     */
    public int updateProxyConfig(Connection con, String proxyHost, int proxyPort, String proxyUser,
            String proxyPass, int proxyId)
            throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.UpdateProxyConfig);
            ps.setString(1, proxyHost);
            ps.setInt(2, proxyPort);
            ps.setString(3, proxyUser);
            ps.setString(4, proxyPass);
            ps.setInt(5, proxyId);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }
    
    /**
     * Deletes a proxy configuration
     *
     * @param con
     *        Database connection
     * @param proxyId
     *        AMTU system proxy ID
     * @param proxyHost
     *        Network proxy host
     * @throws SQLException
     *         Error in SQL
     */
    public int deleteProxyConfig(Connection con) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(SQLString.DeleteProxyConfig);
            return ps.executeUpdate();
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                }
            }
        }
    }
}