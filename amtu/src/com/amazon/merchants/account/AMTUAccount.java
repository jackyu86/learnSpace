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

import java.io.File;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.PriorityBlockingQueue;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.amazon.merchants.Messages;
import com.amazon.merchants.database.Database;
import com.amazon.merchants.database.DatabaseException;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.mws.MWSClient;
import com.amazon.merchants.system.ProxyConfig;
import com.amazon.merchants.transport.model.MWSEndpoint;
import com.amazon.merchants.transport.model.MerchantFeed;
import com.amazon.merchants.transport.model.MerchantReport;
import com.amazon.merchants.transport.util.MWSAuthenticationUtility;
import com.amazonaws.mws.MarketplaceWebService;
import com.amazonaws.mws.MarketplaceWebServiceException;

/**
 * Encapsulates all the functions and data storage related to an AMTU account
 */
public class AMTUAccount {
    public static enum SQL_COLUMNS {
        amtu_account_id, merchant_alias, merchant_id, mws_access_key, mws_secret_key, mws_endpoint, document_transport
    };

    public static enum XML_ELEMENTS {
        accounts, account, name, delete, mwsaccesskey, mwssecretkey, merchantid, mwsendpoint, documenttransport
    };

    private int accountId = -1;
    private String merchantAlias = null;
    private String merchantId = null;
    private String mwsAccessKey = null;
    private String mwsSecretKey = null;
    private MWSEndpoint mwsEndpoint = null;
    private File documentTransport = null;

    private List<MerchantSiteGroup> siteGroups = null;
    private List<MerchantSiteGroup> deletedSiteGroups = null;
    private AccountConfig config = null;
    private MerchantFeedQueue submissionQueue = null;
    private MerchantFeedResultQueue feedResultQueue = null;

    private Date lastDispatch = null;
    private Date lastReportRetrieval = null;
    private Date lastProcessingReportRetrieval = null;
    private Date lastStatusUpdate = null;
    private Date lastConnection = null;
    private int unsubmittedFeeds = 0;

    public Object DISPATCH_LOCK;
    public Object STATUS_UPDATE_LOCK;
    public Object PROC_RETRIEVE_LOCK;
    public Object REPORT_STATUS_UPDATE_LOCK;
    public Object RETRIEVE_LOCK;

    private boolean underDispatch = false;
    private boolean underProcRetrieval = false;
    private boolean underRetrieval = false;

    private boolean toDelete = false;

    private static Database db = Database.getInstance();

    private TreeMap<String, MerchantReport> reportsToRetrieve;


    /**
     * Loads an AMTU account by ID
     *
     * @param accountId AMTU account ID
     * @return AMTU account referenced by the given ID; null if account ID not found
     * @throws SQLException Error in SQL
     * @throws DatabaseException Error communicating with database
     */
    public static AMTUAccount loadAccount(int accountId) throws SQLException, DatabaseException {
        if (accountId < 0) {
            return null;
        }

        Map<String, String> accountDetails = db.pullAmtuAccountDetailsById(accountId);

        // if no record, return null
        if (accountDetails == null) {
            return null;
        }

        AMTUAccount account = new AMTUAccount();
        account.accountId = accountId;
        account.merchantAlias = accountDetails.get(SQL_COLUMNS.merchant_alias.toString());
        account.merchantId = accountDetails.get(SQL_COLUMNS.merchant_id.toString());
        account.mwsAccessKey = accountDetails.get(SQL_COLUMNS.mws_access_key.toString());
        account.mwsSecretKey = accountDetails.get(SQL_COLUMNS.mws_secret_key.toString());
        account.mwsEndpoint = MWSEndpoint.getEndpointMap().get(accountDetails.get(SQL_COLUMNS.mws_endpoint.toString()));
        account.documentTransport = new File(accountDetails.get(SQL_COLUMNS.document_transport.toString()));

        account.siteGroups = MerchantSiteGroup.loadAllForAccount(account);
        account.config = AccountConfig.loadConfigForAccount(account);
        account.submissionQueue = MerchantFeedQueue.loadQueueForAccount(account);

        account.submissionQueue.vacateOldQueueEntries();

        return account;
    }


    /**
     * Loads an AMTU account by alias
     *
     * @param merchantAlias Merchant alias
     * @return AMTU account referenced by the given alias; null of alias not found
     * @throws SQLException Error in SQL
     * @throws DatabaseException Error communicating with database
     */
    public static AMTUAccount loadAccount(String merchantAlias) throws SQLException, DatabaseException {
        Integer accountId = db.pullAmtuAccountIdByAlias(merchantAlias);
        if (accountId == null) {
            return null;
        }
        return loadAccount(accountId);
    }


    /**
     * Loads all AMTU accounts stored in the database
     *
     * @return List of all AMTU accounts in application
     * @throws SQLException Error in SQL
     * @throws DatabaseException Error communicating with database
     */
    public static List<AMTUAccount> loadAllAccounts() throws SQLException, DatabaseException {
        List<AMTUAccount> accountList = new ArrayList<AMTUAccount>();
        try {
            List<Integer> accountIds = db.listAllAmtuAccountIds();
            if (accountIds != null) {
                for (Integer id : accountIds) {
                    accountList.add(loadAccount(id));
                }
            }
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().fatal(Messages.MerchantAccountManager_18.toString(), e);
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
        }
        return accountList;
    }


    /**
     * Returns the AMTU account ID using a given Amazon merchant ID
     *
     * @param merchantId Amazon Merchant ID
     * @return AMTU account ID; -1 if merchant ID not in use
     * @throws SQLException Error in SQL
     * @throws DatabaseException Error communicating with database
     */
    public static int merchantIdInUse(String merchantId) throws SQLException, DatabaseException {
        return db.getAccountIdForMerchantId(merchantId);
    }


    /**
     * Returns the AMTU account ID using a given DTD
     *
     * @param documentTransport Full path to document transport directory
     * @return AMTU account ID; -1 if DTD not in use
     * @throws SQLException Error in SQL
     * @throws DatabaseException Error communicating with database
     */
    public static int documentTransportInUse(String documentTransport) throws SQLException, DatabaseException {
        return db.getAccountIdForDocumentTransport(documentTransport);
    }


    /**
     * Returns the AMTU account ID using a given MWS access key and Merchant ID
     *
     * @param mwsAccessKey MWS Access Key
     * @param merchantId Amazon Merchant ID
     * @return AMTU account ID; -1 if access key / merchant ID not in use
     * @throws SQLException Error in SQL
     * @throws DatabaseException Error communicating with database
     */
    public static int credentialsInUse(String mwsAccessKey, String merchantId) throws SQLException, DatabaseException {
        return db.getAccountIdForMwsAccessKeyAndMerchantId(mwsAccessKey, merchantId);
    }


    /**
     * Throws a formatted exception related to XML processing
     *
     * @param exceptionDetail Exception string to format
     * @throws MerchantAccountException
     */
    protected static void throwXMLException(String exceptionDetail) throws MerchantAccountException {
        String fullMessage = Messages.AMTUAccount_1.toString() + " " + exceptionDetail;

        throw new MerchantAccountException(fullMessage);
    }


    /**
     * Converts an XML element into a string
     *
     * @param element XML element
     * @return Element represented by string
     */
    protected static String elementToString(Element element) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.transform(new DOMSource(element), new StreamResult(sw));
        }
        catch (TransformerException e) {
            return "";
        }
        return sw.toString();

    }


    /**
     * Prepares an AMTU account object from XML
     *
     * @param element XML element
     * @return AMTU account
     * @throws MerchantAccountException Error in XML or error related to business rules
     */
    public static AMTUAccount fromXML(Element element) throws MerchantAccountException {
        String alias = element.getAttribute(XML_ELEMENTS.name.toString());
        if (alias == null || alias.isEmpty()) {
            throwXMLException(Messages.AMTUAccount_2.toString());
        }
        boolean deleteAccount = "true".equalsIgnoreCase(element.getAttribute(XML_ELEMENTS.delete.toString()).trim());

        AMTUAccount account = null;
        try {
            account = AMTUAccount.loadAccount(alias.trim());
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().fatal(Messages.MerchantAccountManager_18.toString(), e);
            throwXMLException(Messages.MerchantAccountManager_18.toString());
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
            return null;
        }

        if (deleteAccount) {
            if (account != null) {
                account.toDelete = true;
            }
            return account;
        }

        if (account == null) {
            account = new AMTUAccount();
            account.setMerchantAlias(alias.trim());

            // set up default site group
            account.getDefaultSiteGroup();
        }

        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            String mwsAccessKey = xpath.evaluate(XML_ELEMENTS.mwsaccesskey.toString() + "/text()", element);
            if (mwsAccessKey == null || mwsAccessKey.isEmpty()) {
                throwXMLException(Messages.AMTUAccount_3.toString());
            }
            account.setMwsAccessKey(mwsAccessKey.trim());

            String mwsSecretKey = xpath.evaluate(XML_ELEMENTS.mwssecretkey.toString() + "/text()", element);
            if (mwsSecretKey == null || mwsSecretKey.isEmpty()) {
                throwXMLException(Messages.AMTUAccount_4.toString());
            }
            account.setMwsSecretKey(mwsSecretKey.trim());

            String merchantId = xpath.evaluate(XML_ELEMENTS.merchantid.toString() + "/text()", element);
            if (merchantId == null || merchantId.isEmpty()) {
                throwXMLException(Messages.AMTUAccount_5.toString());
            }
            account.setMerchantId(merchantId.trim());

            String endpoint = xpath.evaluate(XML_ELEMENTS.mwsendpoint.toString() + "/text()", element);
            if (endpoint == null || endpoint.isEmpty()) {
                throwXMLException(Messages.AMTUAccount_6.toString());
            }
            MWSEndpoint mwsEndpoint = MWSEndpoint.getEndpoint(endpoint.trim());
            if (mwsEndpoint == null) {
                throwXMLException(String.format(Messages.AMTUAccount_7.toString(), endpoint));
            }
            account.setMwsEndpoint(mwsEndpoint);

            String documentTransport = xpath.evaluate(XML_ELEMENTS.documenttransport.toString() + "/text()", element);
            if (documentTransport == null || documentTransport.isEmpty()) {
                throwXMLException(Messages.AMTUAccount_8.toString());
            }
            account.setDocumentTransport(documentTransport);
            try {
                account.validateDocumentTransportDirectory();
            }
            catch (Exception e) {
                throwXMLException(e.getLocalizedMessage());
            }

            NodeList accountConfigRoot = element.getElementsByTagName(AccountConfig.XML_ELEMENTS.accountconfig
                .toString());
            if (accountConfigRoot != null && accountConfigRoot.getLength() > 0) {
                NodeList configList = ((Element) accountConfigRoot.item(0))
                    .getElementsByTagName(AccountConfig.XML_ELEMENTS.config.toString());

                if (configList != null) {
                    for (int i = 0; i < configList.getLength(); i++) {
                        Element config = (Element) configList.item(i);

                        account.config.fromXML(account, config);
                    }
                }
            }

            try {
                account.validateConfig();
            }
            catch (Exception e) {
                throwXMLException(e.getLocalizedMessage());
            }

            NodeList siteGroupsRoot = element.getElementsByTagName(MerchantSiteGroup.XML_ELEMENTS.site_groups
                .toString());
            if (siteGroupsRoot != null && siteGroupsRoot.getLength() > 0) {
                NodeList siteGroupList = ((Element) siteGroupsRoot.item(0))
                    .getElementsByTagName(MerchantSiteGroup.XML_ELEMENTS.site_group.toString());

                if (siteGroupList != null) {
                    for (int i = 0; i < siteGroupList.getLength(); i++) {
                        Element siteGroupInfo = (Element) siteGroupList.item(i);

                        MerchantSiteGroup.fromXML(account, siteGroupInfo);
                    }
                }
            }
            try {
                account.validateSiteGroups();
            }
            catch (Exception e) {
                throwXMLException(e.getLocalizedMessage());
            }
        }
        catch (XPathExpressionException e) {
            throwXMLException(String.format(Messages.AMTUAccount_18.toString(), e.getLocalizedMessage()));
        }

        return account;
    }


    public AMTUAccount() {
        DISPATCH_LOCK = new Object();
        STATUS_UPDATE_LOCK = new Object();
        PROC_RETRIEVE_LOCK = new Object();
        REPORT_STATUS_UPDATE_LOCK = new Object();
        RETRIEVE_LOCK = new Object();

        config = AccountConfig.getDefaultConfig(this);
        siteGroups = new ArrayList<MerchantSiteGroup>();
        feedResultQueue = new MerchantFeedResultQueue();
        reportsToRetrieve = new TreeMap<String, MerchantReport>();
    }


    @Override
    public String toString() {
        return merchantAlias;
    }


    public MerchantSiteGroup getDefaultSiteGroup() {
        if (siteGroups == null) {
            siteGroups = new ArrayList<MerchantSiteGroup>();
        }

        for (MerchantSiteGroup siteGroup : siteGroups) {
            List<MerchantSite> list = siteGroup.getSiteList();
            if (list == null || list.isEmpty()) {
                return siteGroup;
            }
        }

        MerchantSiteGroup siteGroup = MerchantSiteGroup.getDefaultSiteGroup(this);

        addSiteGroup(siteGroup);

        return siteGroup;
    }


    public void deleteSiteGroup(MerchantSiteGroup siteGroup) {
        if (siteGroup.getSiteGroupId() < 0) {
            return;
        }

        if (deletedSiteGroups == null) {
            deletedSiteGroups = new ArrayList<MerchantSiteGroup>();
        }

        if (deletedSiteGroups.contains(siteGroup)) {
            return;
        }

        deletedSiteGroups.add(siteGroup);

        if (siteGroups != null) {
            siteGroups.remove(siteGroup);
        }
    }


    public void addSiteGroup(MerchantSiteGroup siteGroup) {
        if (deletedSiteGroups != null && deletedSiteGroups.contains(siteGroup)) {
            deletedSiteGroups.remove(siteGroup);
        }

        if (siteGroups == null) {
            siteGroups = new ArrayList<MerchantSiteGroup>();
        }

        siteGroup.setParentAccount(this);

        if (siteGroups.contains(siteGroup)) {
            return;
        }

        siteGroups.add(siteGroup);
    }


    public MerchantSiteGroup getSiteGroup(String siteGroupAlias) {
        if (siteGroupAlias == null || siteGroups == null) {
            return null;
        }

        for (MerchantSiteGroup siteGroup : siteGroups) {
            if (siteGroupAlias.equals(siteGroup.getMerchantAlias())) {
                return siteGroup;
            }
        }

        return null;
    }


    public Element toXML(Document doc) {
        Element accountRoot = doc.createElement(XML_ELEMENTS.account.toString());
        accountRoot.setAttribute(XML_ELEMENTS.name.toString(), merchantAlias);

        Element mwsAccessKey = doc.createElement(XML_ELEMENTS.mwsaccesskey.toString());
        mwsAccessKey.setTextContent(this.mwsAccessKey);
        accountRoot.appendChild(mwsAccessKey);

        Element mwsSecretKey = doc.createElement(XML_ELEMENTS.mwssecretkey.toString());
        mwsSecretKey.setTextContent(this.mwsSecretKey);
        accountRoot.appendChild(mwsSecretKey);

        Element merchantId = doc.createElement(XML_ELEMENTS.merchantid.toString());
        merchantId.setTextContent(this.merchantId);
        accountRoot.appendChild(merchantId);

        Element mwsEndpoint = doc.createElement(XML_ELEMENTS.mwsendpoint.toString());
        mwsEndpoint.setTextContent(this.mwsEndpoint.getCode());
        accountRoot.appendChild(mwsEndpoint);

        Element documentTransport = doc.createElement(XML_ELEMENTS.documenttransport.toString());
        documentTransport.setTextContent(this.documentTransport.getAbsolutePath());
        accountRoot.appendChild(documentTransport);

        accountRoot.appendChild(config.toXML(doc));

        Element siteGroupsXML = doc.createElement(MerchantSiteGroup.XML_ELEMENTS.site_groups.toString());

        for (MerchantSiteGroup siteGroup : siteGroups) {
            Element siteGroupXML = siteGroup.toXML(doc);
            if (siteGroupXML != null) {
                siteGroupsXML.appendChild(siteGroupXML);
            }
        }

        accountRoot.appendChild(siteGroupsXML);

        return accountRoot;
    }


    /**
     * Returns whether this account has been saved to the database
     *
     * @return Whether this account has been saved
     */
    private boolean isNew() {
        return accountId < 0;
    }


    /**
     * Calls for the submission queue to execute a prune of old submissions
     */
    private void updateSubmissionQueue() {
        submissionQueue.pruneQueue();
    }


    /**
     * Adds a feed submission to the queue
     *
     * @param submissionId Feed submission ID
     * @param submissionTime Feed submission time
     */
    public void addFeedSubmission(String submissionId, Date submissionTime) {
        submissionQueue.offer(submissionId, submissionTime);
    }


    /**
     * Returns how many feed submission queue spots are available for immediate dispatch
     *
     * @return How many feeds can be dispatched
     */
    public int spotsAvailableForDispatch() {
        return submissionQueue.queueSpotsAvailable();
    }


    /**
     * Returns whether the account has feed dispatch slots available
     *
     * @return Whether account has slots available
     */
    public boolean hasFeedDispatchSlotsAvailable() {
        updateSubmissionQueue();

        return spotsAvailableForDispatch() > 0;
    }


    /**
     * Determines if the account is ready for a status update
     *
     * @return Whether account is ready for status update
     */
    public boolean isEligibleForStatusUpdate() {
        if (lastStatusUpdate == null) {
            return true;
        }

        int reportInterval = AccountConfig.MINIMUM_PROCESSING_REPORT_RETRIEVAL_INTERVAL;
        String reportConfig = config.get(AccountConfig.PROCESSING_REPORT_RETRIEVAL_INTERVAL);
        if (reportConfig != null && !reportConfig.isEmpty()) {
            reportInterval = Integer.parseInt(reportConfig);
        }

        return new Date().getTime() - lastStatusUpdate.getTime() > reportInterval * 60 * 1000;
    }


    /**
     * Returns a prioritized queue of all feeds ready to dispatch for this account. It brings together all feeds from
     * all site groups and prioritizes them based on the date the file was last modified (oldest first).
     *
     * @return Prioritized queue of feeds
     */
    public PriorityBlockingQueue<MerchantFeed> feedsForDispatch() {
        PriorityBlockingQueue<MerchantFeed> feedQueue = new PriorityBlockingQueue<MerchantFeed>();
        for (MerchantSiteGroup siteGroup : siteGroups) {
            feedQueue.addAll(siteGroup.getFeedsForDispatch());
        }
        unsubmittedFeeds = feedQueue.size();
        return feedQueue;
    }


    /**
     * Returns a map of all submitted feeds that have not been marked as complete
     *
     * @return Map of merchant feeds
     * @throws SQLException Error in SQL
     * @throws DatabaseException Error communicating with database
     */
    public HashMap<String, MerchantFeed> incompleteFeedsForMerchant() throws SQLException, DatabaseException {
        HashMap<String, MerchantFeed> incompleteFeedList = new HashMap<String, MerchantFeed>();
        for (MerchantSiteGroup siteGroup : siteGroups) {
            List<MerchantFeed> siteGroupIncompleteList = MerchantFeed.listIncompleteFeedsForSiteGroup(siteGroup);
            for (MerchantFeed feed : siteGroupIncompleteList) {
                incompleteFeedList.put(feed.getFeedSubmissionId(), feed);
            }
        }
        return incompleteFeedList;
    }


    /**
     * Returns a prioritized queue of all feeds awaiting processing report retrieval
     *
     * @return Prioritized queue of merchant feeds
     * @throws SQLException Error in SQL
     * @throws DatabaseException Error communicating with database
     */
    public PriorityBlockingQueue<MerchantFeed> feedsForProcessingReportRetrieval() throws SQLException,
        DatabaseException {
        PriorityBlockingQueue<MerchantFeed> feedQueue = new PriorityBlockingQueue<MerchantFeed>();
        for (MerchantSiteGroup siteGroup : siteGroups) {
            feedQueue.addAll(MerchantFeed.listCompleteFeedsWithoutProcessingReportForSiteGroup(siteGroup));
        }
        return feedQueue;
    }


    public void setUnderDispatch(boolean underDispatch) {
        this.underDispatch = underDispatch;
    }


    public boolean isUnderDispatch() {
        return underDispatch;
    }


    public void updateLastDispatch() {
        lastDispatch = new Date();
    }


    public Date getLastDispatch() {
        return lastDispatch;
    }


    public void updateLastConnection() {
        lastConnection = new Date();
    }


    public Date getLastConnection() {
        return lastConnection;
    }


    public void updateLastStatusUpdate() {
        lastStatusUpdate = new Date();
    }


    public Date getLastStatusUpdate() {
        return lastStatusUpdate;
    }


    public void updateLastReportRetrieval() {
        lastReportRetrieval = new Date();
    }


    public Date getLastReportRetrieval() {
        return lastReportRetrieval;
    }


    public void updateLastProcessingReportRetrieval() {
        lastProcessingReportRetrieval = new Date();
    }


    public Date getLastProcessingReportRetrieval() {
        return lastProcessingReportRetrieval;
    }


    public void updateProcessingReportRetrievalQueue() {
        feedResultQueue.pruneQueue();
    }


    public int spotsAvailableForProcessingReportRetrieval() {
        return feedResultQueue.queueSpotsAvailable();
    }


    public boolean hasProcessingReportRetrievalSpotsAvailable() {
        updateProcessingReportRetrievalQueue();

        return spotsAvailableForProcessingReportRetrieval() > 0;
    }


    public void setUnderProcRetrieval(boolean underProcRetrieval) {
        this.underProcRetrieval = underProcRetrieval;
    }


    public boolean isUnderProcRetrieval() {
        return underProcRetrieval;
    }


    public boolean isEligibleForReportRetrieval() {
        if (AccountConfig.BOOLEAN_TRUE.equalsIgnoreCase(config.get(AccountConfig.REPORTS_DISABLED))) {
            return false;
        }

        if (lastReportRetrieval == null) {
            return true;
        }

        int reportInterval = AccountConfig.MINIMUM_REPORT_RETRIEVAL_INTERVAL;
        String reportConfig = config.get(AccountConfig.REPORT_RETRIEVAL_INTERVAL);
        if (reportConfig != null && !reportConfig.isEmpty()) {
            reportInterval = Integer.parseInt(reportConfig);
        }

        return new Date().getTime() - lastReportRetrieval.getTime() > reportInterval * 60 * 1000;
    }


    public void setUnderRetrieval(boolean underRetrieval) {
        this.underRetrieval = underRetrieval;
    }


    public boolean isUnderRetrieval() {
        return underRetrieval;
    }


    public void queueReportForRetrieval(String reportId, MerchantReport report) {
        synchronized(reportsToRetrieve) {
            reportsToRetrieve.put(reportId, report);
        }
    }


    public boolean reportQueuedForDownload(String id) {
        synchronized(reportsToRetrieve) {
            return reportsToRetrieve.containsKey(id);
        }
    }


    public MerchantReport getReportForRetrieval() {
        synchronized(reportsToRetrieve) {
            if (reportsToRetrieve.isEmpty()) {
                return null;
            }
            String id = reportsToRetrieve.keySet().iterator().next();
            MerchantReport report = reportsToRetrieve.get(id);
            reportsToRetrieve.remove(id);
            return report;
        }
    }


    public boolean hasReportsToRetrieve() {
        synchronized (reportsToRetrieve) {
            return !reportsToRetrieve.isEmpty();
        }
    }


    public int getNumberOfReportsToRetrieve() {
        synchronized (reportsToRetrieve) {
            return reportsToRetrieve.size();
        }
    }


    /**
     * Saves the AMTU account to the database
     *
     * @param conn Database connection
     * @throws MerchantAccountException If the information in the account is invalid
     * @throws SQLException Error in SQL
     */
    protected synchronized void save(Connection conn) throws MerchantAccountException, SQLException {
        validateAccount();

        getDefaultSiteGroup();

        boolean isNew = isNew();
        try {
            try {
                if (isNew) {
                    accountId = db.createAMTUAccount(conn, merchantAlias, mwsAccessKey, mwsSecretKey, merchantId,
                        mwsEndpoint.getCode(), documentTransport.getAbsolutePath());
                }
                else {
                    db.updateAMTUAccount(conn, merchantAlias, mwsAccessKey, mwsSecretKey, merchantId,
                        mwsEndpoint.getCode(), documentTransport.getAbsolutePath(), accountId);
                }

                config.save(conn);
                for (MerchantSiteGroup siteGroup : siteGroups) {
                    siteGroup.save(conn);
                }
                if (deletedSiteGroups != null) {
                    for (MerchantSiteGroup siteGroup : deletedSiteGroups) {
                        siteGroup.delete(conn);
                    }
                }
                if (submissionQueue != null) {
                    submissionQueue.save(conn);
                }

                String encryptedSecretAccessKey = mwsSecretKey.substring(0, 5) + "****************************";
                if (isNew) {
                    TransportLogger.getSysAuditLogger().info(
                        Messages.MerchantAccountManager_19.toString() + " alias=" + merchantAlias + ", "
                            + "accessKeyId=" + mwsAccessKey + ", secretAccessKey=" + encryptedSecretAccessKey
                            + ", merchantId=" + merchantId + ", endpoint=" + mwsEndpoint.getDescription()
                            + ", documentTransport=" + documentTransport.getAbsolutePath());
                }
                else {
                    TransportLogger.getSysAuditLogger().info(
                        Messages.MerchantAccountManager_20.toString() + " alias=" + merchantAlias + ", "
                            + "accessKeyId=" + mwsAccessKey + ", secretAccessKey=" + encryptedSecretAccessKey
                            + ", documentTransport=" + documentTransport.getAbsolutePath());
                }
            }
            catch (SQLException e) {
                if (isNew) {
                    accountId = -1;
                }

                throw e;
            }
        }
        catch (SQLException e) {
            if (isNew) {
                TransportLogger.getSysErrorLogger().fatal(Messages.MerchantAccountManager_1.toString(), e);
                throw new MerchantAccountException(Messages.MerchantAccountManager_1.toString());
            }
            else {
                TransportLogger.getSysErrorLogger().fatal(Messages.MerchantAccountManager_3.toString(), e);
                throw new MerchantAccountException(Messages.MerchantAccountManager_3.toString());
            }
        }
        return;
    }


    /**
     * Saves the AMTU account to the database
     *
     * @throws MerchantAccountException If the information in the account is invalid
     */
    public synchronized void save() throws MerchantAccountException {
        Connection conn = null;
        try {
            conn = Database.getUnmanagedConnection();
            conn.setAutoCommit(false);
            try {
                save(conn);
                conn.commit();
            }
            catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().fatal(Messages.MerchantAccountManager_3.toString(), e);
            throw new MerchantAccountException(Messages.MerchantAccountManager_3.toString());
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
    }


    /**
     * Reloads the account information from the database
     *
     * @throws MerchantAccountException If the information cannot be retrieved from the database
     */
    public synchronized void reload() throws MerchantAccountException {
        if (isNew()) {
            // Can't reload if it's never been saved to the database
            return;
        }

        try {
            Map<String, String> accountDetails = db.pullAmtuAccountDetailsById(accountId);
            merchantAlias = accountDetails.get(SQL_COLUMNS.merchant_alias.toString());
            merchantId = accountDetails.get(SQL_COLUMNS.merchant_id.toString());
            mwsAccessKey = accountDetails.get(SQL_COLUMNS.mws_access_key.toString());
            mwsSecretKey = accountDetails.get(SQL_COLUMNS.mws_secret_key.toString());
            mwsEndpoint = MWSEndpoint.getEndpointMap().get(accountDetails.get(SQL_COLUMNS.mws_endpoint.toString()));
            documentTransport = new File(accountDetails.get(SQL_COLUMNS.document_transport.toString()));

            siteGroups = MerchantSiteGroup.loadAllForAccount(this);
            config = AccountConfig.loadConfigForAccount(this);
            submissionQueue = MerchantFeedQueue.loadQueueForAccount(this);
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().fatal(Messages.AMTUAccount_0.toString(), e);
            throw new MerchantAccountException(Messages.AMTUAccount_0.toString());
        }
    }


    /**
     * Deletes an AMTU account
     *
     * @throws MerchantAccountException If the account cannot be deleted
     */
    public synchronized void delete() throws MerchantAccountException {
        if (accountId < 0) {
            // not yet saved, nothing to delete
            return;
        }

        Connection conn = null;
        try {
            conn = Database.getUnmanagedConnection();
            conn.setAutoCommit(false);

            try {
                config.delete(conn);
                for (MerchantSiteGroup siteGroup : siteGroups) {
                    siteGroup.delete(conn);
                }
                submissionQueue.delete(conn);

                db.deleteAMTUAccount(conn, accountId);

                conn.commit();

                TransportLogger.getSysAuditLogger().info(
                    Messages.MerchantAccountManager_21.toString() + ", alias=" + merchantAlias);

                return;
            }
            catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().fatal(Messages.MerchantAccountManager_5.toString(), e);
            throw new MerchantAccountException(Messages.MerchantAccountManager_5.toString());
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
    }


    /**
     * Retrieves a reference to this account's MWS client
     *
     * @return MarketplaceWebService client
     */
    public MarketplaceWebService getMWSClient() {
        return getMWSClient(0);
    }


    /**
     * Retrieves a reference to this account's MWS client with the given socket timeout
     *
     * @param timeout Socket timeout
     * @return MarketplaceWebService client
     */
    public MarketplaceWebService getMWSClient(int timeout) {
        return MWSClient.createMWSClient(mwsAccessKey, mwsSecretKey, System.getProperty("app.name"),
            System.getProperty("app.version"), mwsEndpoint.getEndpointUrl(), timeout, null);
    }


    // Getters and setters
    public int getAccountId() {
        return accountId;
    }


    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }


    public String getMerchantAlias() {
        return merchantAlias;
    }


    public void setMerchantAlias(String merchantAlias) {
        this.merchantAlias = merchantAlias;
    }


    public String getMerchantId() {
        return merchantId;
    }


    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }


    public String getMwsAccessKey() {
        return mwsAccessKey;
    }


    public void setMwsAccessKey(String mwsAccessKey) {
        this.mwsAccessKey = mwsAccessKey;
    }


    public String getMwsSecretKey() {
        return mwsSecretKey;
    }


    public void setMwsSecretKey(String mwsSecretKey) {
        this.mwsSecretKey = mwsSecretKey;
    }


    public MWSEndpoint getMwsEndpoint() {
        return mwsEndpoint;
    }


    public void setMwsEndpoint(MWSEndpoint mwsEndpoint) {
        this.mwsEndpoint = mwsEndpoint;
    }


    public File getDocumentTransport() {
        return documentTransport;
    }


    public void setDocumentTransport(File documentTransport) {
        this.documentTransport = documentTransport;
    }


    public void setDocumentTransport(String documentTransport) {
        this.documentTransport = new File(documentTransport);
    }


    public List<MerchantSiteGroup> getSiteGroups() {
        return siteGroups;
    }


    public String getConfigValue(String key) {
        return config.get(key);
    }


    public void setConfigValue(String key, String value) {
        config.put(key, value);
    }


    public void addFeedResultRequest(String feedSubmissionId, Date requestTime) {
        feedResultQueue.offer(feedSubmissionId, requestTime);
    }


    public int getUnsubmittedFeeds() {
        return unsubmittedFeeds;
    }


    public boolean readyToDelete() {
        return toDelete;
    }


    // validation routines

    /**
     * Validates all the non-credential information in the account
     *
     * @throws MerchantAccountException If some piece of account information fails to validate
     */
    public void validateAccount() throws MerchantAccountException {
        validateAlias();
        validateMerchantId();
        validateDocumentTransportDirectory();
        validateDocumentTransportDirectoryInUse();
        validateEndpoint();
        validateConfig();
        validateSiteGroups();
    }


    /**
     * Ensures the alias is valid and is not in use by another account
     *
     * @throws MerchantAccountException If the alias is invalid or is in use elsewhere
     */
    public void validateAlias() throws MerchantAccountException {
        String alias = getMerchantAlias();

        if (alias == null || alias.isEmpty()) {
            throw new MerchantAccountException(Messages.MerchantAccountManager_6.toString());
        }
        else if (alias.length() > 64) {
            throw new MerchantAccountException(Messages.MerchantAccountManager_7.toString());
        }

        try {
            if (isNew()) {
                AMTUAccount alreadyExists = AMTUAccount.loadAccount(alias);
                if (alreadyExists != null) {
                    throw new MerchantAccountException(Messages.MerchantAccountManager_14.toString());
                }
            }
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().error(Messages.MerchantAccountManager_22.toString(), e);
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
        }
    }


    /**
     * Ensures the merchant ID is valid and is not in use by another account
     *
     * @throws MerchantAccountException If the merchant ID is invalid or is in use elsewhere
     */
    public void validateMerchantId() throws MerchantAccountException {
        String merchantId = getMerchantId();

        if (merchantId.isEmpty()) {
            throw new MerchantAccountException(Messages.MerchantAccountManager_25.toString());
        }

        try {
            int inUseBy = AMTUAccount.merchantIdInUse(merchantId);
            if (inUseBy > 0 && (isNew() || getAccountId() != inUseBy)) {
                throw new MerchantAccountException(Messages.MerchantAccountManager_24.toString());
            }
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().error(Messages.MerchantAccountManager_22.toString(), e);
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
        }
    }


    /**
     * Ensures the DTD is a valid directory
     *
     * @throws MerchantAccountException If the DTD is a valid directory
     */
    public void validateDocumentTransportDirectory() throws MerchantAccountException {
        File dtd = getDocumentTransport();
        if (dtd == null || dtd.getAbsolutePath().isEmpty()) {
            throw new MerchantAccountException(Messages.MerchantAccountManager_9.toString());
        }
        else if (dtd.getAbsolutePath().length() > 1024) {
            throw new MerchantAccountException(Messages.MerchantAccountManager_10.toString());
        }
        else if (dtd.exists() && !dtd.isDirectory()) {
            throw new MerchantAccountException(Messages.MerchantAccountManager_26.toString());
        }
    }


    /**
     * Ensures the DTD is not in use by another account
     *
     * @throws MerchantAccountException If the DTD is in use elsewhere
     */
    public void validateDocumentTransportDirectoryInUse() throws MerchantAccountException {
        File dtd = getDocumentTransport();

        try {
            int inUseBy = AMTUAccount.documentTransportInUse(dtd.getAbsolutePath());
            if (inUseBy > 0 && (isNew() || getAccountId() != inUseBy)) {
                throw new MerchantAccountException(Messages.MerchantAccountManager_15.toString());
            }
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().error(Messages.MerchantAccountManager_22.toString(), e);
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
        }
    }


    /**
     * Validates the account has an endpoint set
     *
     * @throws MerchantAccountException If no endpoint is set
     */
    public void validateEndpoint() throws MerchantAccountException {
        if (getMwsEndpoint() == null) {
            throw new MerchantAccountException(Messages.MerchantAccountManager_8.toString());
        }
    }


    /**
     * Validates the account configuration details
     *
     * @throws MerchantAccountException If the account configuration is invalid
     */
    public void validateConfig() throws MerchantAccountException {
        validateDispatchInterval();
        validateProcessingReportInterval();
        validateReportDownloadInterval();
    }


    /**
     * Ensures the feed dispatch interval is an integer and is at least as great as the minimum
     *
     * @throws MerchantAccountException If the feed dispatch interval is not an integer or is less than the minimum
     */
    public void validateDispatchInterval() throws MerchantAccountException {
        String interval = config.get(AccountConfig.FEED_DISPATCH_INTERVAL);
        if (interval != null) {
            try {
                int dispatchInterval = Integer.parseInt(interval);
                if (dispatchInterval < AccountConfig.MINIMUM_FEED_DISPATCH_INTERVAL) {
                    throw new Exception();
                }
            }
            catch (NumberFormatException e) {
                throw new MerchantAccountException(String.format(Messages.AMTUAccount_11.toString(),
                    AccountConfig.MINIMUM_FEED_DISPATCH_INTERVAL));
            }
            catch (Exception e) {
                Object[] messageArguments = { AccountConfig.MINIMUM_FEED_DISPATCH_INTERVAL };
                MessageFormat formatter = new MessageFormat("");
                formatter.applyPattern(Messages.MerchantAccountManager_11.toString());
                throw new MerchantAccountException(formatter.format(messageArguments));
            }
        }
    }


    /**
     * Ensures the processing report retrieval interval is an integer and is at least as great as the minimum
     *
     * @throws MerchantAccountException If the processing report retrieval interval is not an integer or is less than
     *         the minimum
     */
    public void validateProcessingReportInterval() throws MerchantAccountException {
        String interval = config.get(AccountConfig.PROCESSING_REPORT_RETRIEVAL_INTERVAL);
        if (interval != null) {
            try {
                int dispatchInterval = Integer.parseInt(interval);
                if (dispatchInterval < AccountConfig.MINIMUM_PROCESSING_REPORT_RETRIEVAL_INTERVAL) {
                    throw new Exception();
                }
            }
            catch (NumberFormatException e) {
                throw new MerchantAccountException(String.format(Messages.AMTUAccount_13.toString(),
                    AccountConfig.MINIMUM_PROCESSING_REPORT_RETRIEVAL_INTERVAL));
            }
            catch (Exception e) {
                Object[] messageArguments = { AccountConfig.MINIMUM_PROCESSING_REPORT_RETRIEVAL_INTERVAL };
                MessageFormat formatter = new MessageFormat("");
                formatter.applyPattern(Messages.MerchantAccountManager_13.toString());
                throw new MerchantAccountException(formatter.format(messageArguments));
            }
        }
    }


    /**
     * Ensures the report download interval is an integer and is at least as great as the minimum
     *
     * @throws MerchantAccountException If the report download interval is not an integer or is less than the minimum
     */
    public void validateReportDownloadInterval() throws MerchantAccountException {
        String reportsDisabled = config.get(AccountConfig.REPORTS_DISABLED);
        if (reportsDisabled == null || !AccountConfig.BOOLEAN_TRUE.equalsIgnoreCase(reportsDisabled)) {
            String interval = config.get(AccountConfig.REPORT_RETRIEVAL_INTERVAL);
            if (interval != null) {
                try {
                    int dispatchInterval = Integer.parseInt(interval);
                    if (dispatchInterval < AccountConfig.MINIMUM_REPORT_RETRIEVAL_INTERVAL) {
                        throw new Exception();
                    }
                }
                catch (NumberFormatException e) {
                    throw new MerchantAccountException(String.format(Messages.AMTUAccount_12.toString(),
                        AccountConfig.MINIMUM_REPORT_RETRIEVAL_INTERVAL));
                }
                catch (Exception e) {
                    Object[] messageArguments = { AccountConfig.MINIMUM_REPORT_RETRIEVAL_INTERVAL };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.applyPattern(Messages.MerchantAccountManager_12.toString());
                    throw new MerchantAccountException(formatter.format(messageArguments));
                }
            }
        }
    }


    /**
     * Validates the user account credentials are unique and are accepted by MWS
     *
     * @throws MarketplaceWebServiceException If account credentials do not authenticate
     * @throws MerchantAccountException If the account credentials are invalid
     */
    public void validateCredentials(ProxyConfig proxy) throws MarketplaceWebServiceException, MerchantAccountException {
        validateCredentialsNotInUse();
        validateCredentialsAuthenticate(proxy);
    }


    /**
     * Ensures the account credentials are not already in use by a different account
     *
     * @throws MerchantAccountException If the credentials are already in use elsewhere
     */
    public void validateCredentialsNotInUse() throws MerchantAccountException {
        try {
            int inUseBy = AMTUAccount.credentialsInUse(getMwsAccessKey(), getMerchantId());
            if (inUseBy > 0 && (isNew() || getAccountId() != inUseBy)) {
                throw new MerchantAccountException(Messages.MerchantAccountManager_17.toString());
            }
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().error(Messages.MerchantAccountManager_23.toString(), e);
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
        }
    }


    /**
     * Ensures the account credentials authenticate with MWS
     *
     * @throws MarketplaceWebServiceException If the credentials do not authenticate
     * @throws MerchantAccountException If the credentials are incomplete
     */
    public void validateCredentialsAuthenticate(ProxyConfig proxy) throws MarketplaceWebServiceException, MerchantAccountException {
        if (getMwsAccessKey() == null || getMwsAccessKey().isEmpty() || getMwsSecretKey() == null
            || getMwsSecretKey().isEmpty() || getMerchantId() == null || getMerchantId().isEmpty()
            || getMwsEndpoint() == null) {
            throw new MerchantAccountException(Messages.MerchantAccountManager_16.toString());
        }
        
        MWSAuthenticationUtility.authenticate(getMwsAccessKey(), getMwsSecretKey(), getMerchantId(), getMwsEndpoint(), proxy);
    }


    /**
     * Validates all the merchant site groups for this account. Ensures that no aliases or site group DTDs are
     * duplicated. Also ensures no site aliases or marketplace IDs are duplicated.
     *
     * @throws MerchantAccountException If site group details are invalid
     */
    public void validateSiteGroups() throws MerchantAccountException {
        HashMap<String, Integer> nameMap = new HashMap<String, Integer>();
        HashMap<String, Integer> directoryMap = new HashMap<String, Integer>();
        HashMap<String, Integer> siteNameMap = new HashMap<String, Integer>();
        HashMap<String, Integer> marketplaceIdMap = new HashMap<String, Integer>();

        if (siteGroups != null) {
            for (MerchantSiteGroup siteGroup : siteGroups) {
                String siteGroupAlias = siteGroup.getMerchantAlias();
                int siteGroupId = siteGroup.getSiteGroupId();

                if (siteGroupAlias == null || siteGroupAlias.isEmpty()) {
                    throw new MerchantAccountException(Messages.AMTUAccount_19.toString());
                }
                if (nameMap.containsKey(siteGroupAlias)) {
                    throw new MerchantAccountException(
                        String.format(Messages.AMTUAccount_20.toString(), siteGroupAlias));
                }
                if (siteGroupAlias.length() > 64) {
                    throw new MerchantAccountException(Messages.AMTUAccount_29.toString());
                }
                nameMap.put(siteGroupAlias, siteGroupId);

                String siteGroupDirectory = siteGroup.getDocumentTransport();
                if (siteGroupDirectory == null || siteGroupDirectory.isEmpty()) {
                    throw new MerchantAccountException(Messages.AMTUAccount_21.toString());
                }
                if (directoryMap.containsKey(siteGroupDirectory)) {
                    throw new MerchantAccountException(String.format(Messages.AMTUAccount_22.toString(),
                        siteGroupDirectory));
                }
                if (!siteGroupDirectory.matches("[_A-Za-z0-9]+")) {
                    throw new MerchantAccountException(String.format(Messages.AMTUAccount_23.toString(),
                        siteGroupDirectory));
                }
                if (siteGroupDirectory.length() > 30) {
                    throw new MerchantAccountException(Messages.AMTUAccount_28.toString());
                }
                directoryMap.put(siteGroupDirectory, siteGroupId);

                // Check this group's sites
                List<MerchantSite> siteList = siteGroup.getSiteList();
                if (siteList != null && !siteList.isEmpty()) {
                    HashMap<String, Integer> groupSites = new HashMap<String, Integer>();

                    for (MerchantSite site : siteList) {
                        String siteAlias = site.getMerchantAlias();
                        int siteId = site.getSiteId();

                        if (siteAlias == null || siteAlias.isEmpty()) {
                            throw new MerchantAccountException(Messages.AMTUAccount_24.toString());
                        }
                        if (groupSites.containsKey(siteAlias)) {
                            throw new MerchantAccountException(String.format(Messages.AMTUAccount_25.toString(),
                                siteAlias));
                        }
                        if (siteAlias.length() > 64) {
                            throw new MerchantAccountException(Messages.AMTUAccount_30.toString());
                        }
                        groupSites.put(siteAlias, siteId);

                        if (siteNameMap.containsKey(siteAlias)) {
                            if (siteId != siteNameMap.get(siteAlias)) {
                                throw new MerchantAccountException(String.format(Messages.AMTUAccount_25.toString(),
                                    siteAlias));
                            }
                        }
                        siteNameMap.put(siteAlias, siteId);

                        String marketplaceId = site.getMarketplaceId();
                        if (marketplaceId == null || marketplaceId.isEmpty()) {
                            throw new MerchantAccountException(Messages.AMTUAccount_26.toString());
                        }
                        if (marketplaceIdMap.containsKey(marketplaceId)) {
                            if (siteId != marketplaceIdMap.get(marketplaceId)) {
                                throw new MerchantAccountException(String.format(Messages.AMTUAccount_27.toString(),
                                    marketplaceId));
                            }
                        }
                        if (marketplaceId.length() > 32) {
                            throw new MerchantAccountException(Messages.AMTUAccount_31.toString());
                        }
                        marketplaceIdMap.put(marketplaceId, siteId);
                    }
                }
            }
        }
    }
}