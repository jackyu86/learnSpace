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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.amazon.merchants.Messages;
import com.amazon.merchants.database.Database;
import com.amazon.merchants.database.DatabaseException;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.transport.model.FeedTypeEnum;
import com.amazon.merchants.transport.model.MerchantFeed;
import com.amazon.merchants.util.file.DirectoryEnum;
import com.amazon.merchants.util.file.TransportUtil;

public class MerchantSiteGroup {
    public static final String DEFAULT_SITE_GROUP_ALIAS = "DEFAULT"; //$NON-NLS-1$
    public static final String DEFAULT_SITE_GROUP_DTD = "production"; //$NON-NLS-1$

    public static enum SQL_COLUMNS {
        site_group_id, site_group_dtd, merchant_alias
    };

    public static enum XML_ELEMENTS {
        site_groups, site_group, name, delete, directory, site, marketplaceid
    };

    private AMTUAccount parentAccount = null;
    private int siteGroupId = -1;
    private String siteGroupDocumentTransport = null;
    private String merchantAlias = null;

    private List<MerchantSite> siteList = null;
    private Set<Integer> deletedSites = null;

    private static Database db = Database.getInstance();


    public static MerchantSiteGroup getDefaultSiteGroup(AMTUAccount account) {
        MerchantSiteGroup siteGroup = new MerchantSiteGroup();
        siteGroup.parentAccount = account;
        siteGroup.siteGroupDocumentTransport = DEFAULT_SITE_GROUP_DTD;
        siteGroup.merchantAlias = DEFAULT_SITE_GROUP_ALIAS;
        return siteGroup;
    }


    public static MerchantSiteGroup loadSiteGroup(AMTUAccount account,
            int siteGroupId) throws DatabaseException, SQLException {
        if (siteGroupId < 0) {
            return null;
        }

        Map<String, String> siteGroupDetails = db.pullSiteGroupDetails(
                account.getAccountId(), siteGroupId);
        if (siteGroupDetails == null) {
            return null;
        }

        MerchantSiteGroup siteGroup = new MerchantSiteGroup();
        siteGroup.parentAccount = account;
        siteGroup.siteGroupId = siteGroupId;

        siteGroup.siteGroupDocumentTransport = siteGroupDetails
                .get(SQL_COLUMNS.site_group_dtd.toString());
        siteGroup.merchantAlias = siteGroupDetails
                .get(SQL_COLUMNS.merchant_alias.toString());

        siteGroup.siteList = MerchantSite.loadAllForSiteGroup(siteGroup);

        return siteGroup;
    }


    public static List<MerchantSiteGroup> loadAllForAccount(AMTUAccount account)
            throws DatabaseException, SQLException {
        List<MerchantSiteGroup> siteGroupList = new ArrayList<MerchantSiteGroup>();

        List<Integer> siteGroupIds = db.listAllSiteGroupsByAmtuAccount(account
                .getAccountId());
        for (int siteGroupId : siteGroupIds) {
            MerchantSiteGroup siteGroup = loadSiteGroup(account, siteGroupId);
            if (siteGroup != null) {
                siteGroupList.add(siteGroup);
            }
        }

        return siteGroupList;
    }


    public static List<String> getAllSiteGroupDirectoriesForAccount(
            AMTUAccount account) throws DatabaseException, SQLException {
        return db.getSiteGroupDirectoriesForAccount(account);
    }


    public MerchantSiteGroup() {
    }


    public MerchantSiteGroup(AMTUAccount parentAccount) {
        this.parentAccount = parentAccount;
    }


    @Override
    public String toString() {
        return merchantAlias;
    }


    public void addMerchantSite(MerchantSite merchantSite) {
        if (siteList == null) {
            siteList = new ArrayList<MerchantSite>();
        }

        if (deletedSites != null
                && deletedSites.contains(merchantSite.getSiteId())) {
            deletedSites.remove(merchantSite.getSiteId());
        }

        if (siteList.contains(merchantSite)) {
            return;
        }

        siteList.add(merchantSite);
    }


    public void deleteMerchantSite(MerchantSite site) {
        deleteMerchantSite(site.getSiteId());
    }


    public void deleteMerchantSite(int siteId) {
        for (MerchantSite site : siteList) {
            if (site.getSiteId() == siteId) {
                siteList.remove(site);
                deletedSites.add(siteId);
                return;
            }
        }
    }


    public File getDocumentTransportFolder() {
        if (parentAccount == null
                || parentAccount.getDocumentTransport() == null) {
            return null;
        }

        return new File(parentAccount.getDocumentTransport().toURI()
                .resolve(siteGroupDocumentTransport));
    }


    public List<MerchantFeed> getFeedsForDispatch() {
        final File siteGroupDtd = getDocumentTransportFolder();

        final File FAILED = TransportUtil.getDirectory(siteGroupDtd,
                DirectoryEnum.FAILED);
        final File OUTGOING = TransportUtil.getDirectory(siteGroupDtd,
                DirectoryEnum.OUTGOING);
        File[] files = OUTGOING.listFiles();

        List<MerchantFeed> feedList = new ArrayList<MerchantFeed>();

        for (File file : files) {
            if (FeedTypeEnum.isExtensionSupported(file)) {
                MerchantFeed feed = new MerchantFeed(this, file);
                feedList.add(feed);
            }
            else {
                // Move files with non-supported extensions to failed
                // directory and log
                try {
                    file = TransportUtil.moveFile(file, FAILED,
                            new Date().getTime() + ""); //$NON-NLS-1$
                }
                catch (IOException e) {
                    Object[] messageArguments = { file.getName() };
                    MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
                    formatter.applyPattern(Messages.FeedPreprocessor_0
                            .toString());
                    TransportLogger
                            .getAcctErrorLogger(getParentAccount())
                            .error("[" + getParentAccount().getMerchantAlias() + "] " //$NON-NLS-1$ //$NON-NLS-2$
                                    + formatter.format(messageArguments), e);
                }

                Object[] messageArguments = { file.getName() };
                MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
                formatter.applyPattern(Messages.FeedPreprocessor_1.toString());
                TransportLogger.getAcctAuditLogger(getParentAccount()).warn(
                        "[" + getParentAccount().getMerchantAlias() + "] " //$NON-NLS-1$ //$NON-NLS-2$
                                + formatter.format(messageArguments) + "\n" //$NON-NLS-1$
                                + Messages.FeedPreprocessor_2.toString());
            }
        }

        return feedList;
    }


    public Element toXML(Document doc) {
        if (DEFAULT_SITE_GROUP_ALIAS.equals(merchantAlias)) {
            return null;
        }

        Element siteGroup = doc.createElement(XML_ELEMENTS.site_group
                .toString());
        siteGroup.setAttribute(XML_ELEMENTS.name.toString(), merchantAlias);

        Element siteGroupDtd = doc.createElement(XML_ELEMENTS.directory
                .toString());
        siteGroupDtd.setTextContent(siteGroupDocumentTransport);
        siteGroup.appendChild(siteGroupDtd);

        if (siteList != null && !siteList.isEmpty()) {
            MerchantSite mplace = siteList.get(0);

            Element merchantSite = doc.createElement(XML_ELEMENTS.site
                    .toString());

            merchantSite.setAttribute(XML_ELEMENTS.name.toString(),
                    mplace.getMerchantAlias());
            merchantSite.setAttribute(XML_ELEMENTS.marketplaceid.toString(),
                    mplace.getMarketplaceId());

            siteGroup.appendChild(merchantSite);
        }

        return siteGroup;
    }


    protected static void fromXML(AMTUAccount account, Element siteGroupInfo)
            throws MerchantAccountException, XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        String siteGroupName = siteGroupInfo.getAttribute(XML_ELEMENTS.name
                .toString());
        boolean deleteSiteGroup = "true".equalsIgnoreCase(siteGroupInfo.getAttribute(XML_ELEMENTS.delete.toString()).trim()); //$NON-NLS-1$
        String siteGroupDtd = xpath.evaluate(XML_ELEMENTS.directory.toString()
                + "/text()", //$NON-NLS-1$
                siteGroupInfo);
        String siteName = xpath.evaluate(XML_ELEMENTS.site.toString()
                + "/@" + XML_ELEMENTS.name.toString(), siteGroupInfo); //$NON-NLS-1$
        String marketplaceId = xpath.evaluate(XML_ELEMENTS.site.toString()
                + "/@" + XML_ELEMENTS.marketplaceid.toString(), siteGroupInfo); //$NON-NLS-1$

        if (siteGroupName == null || siteGroupName.isEmpty()) {
            AMTUAccount.throwXMLException(String.format(
                    Messages.AMTUAccount_14.toString(),
                    AMTUAccount.elementToString(siteGroupInfo)));
        }

        if (MerchantSiteGroup.DEFAULT_SITE_GROUP_ALIAS.equals(siteGroupName)) {
            AMTUAccount.throwXMLException(String.format(
                    Messages.AMTUAccount_15.toString(), siteGroupName));
        }

        if (siteGroupDtd != null
                && MerchantSiteGroup.DEFAULT_SITE_GROUP_DTD
                        .equalsIgnoreCase(siteGroupDtd.trim())
                || "logs".equalsIgnoreCase(siteGroupDtd.trim())) { //$NON-NLS-1$
            AMTUAccount.throwXMLException(String.format(
                    Messages.AMTUAccount_16.toString(), siteGroupDtd));
        }

        List<MerchantSiteGroup> accountSiteGroups = account.getSiteGroups();
        MerchantSiteGroup siteGroup = null;

        for (MerchantSiteGroup iter : accountSiteGroups) {
            if (iter.getMerchantAlias().equals(siteGroupName)) {
                siteGroup = iter;
                break;
            }
        }

        if (!deleteSiteGroup
                && (siteGroupDtd == null || siteGroupDtd.isEmpty()
                        || siteName == null || siteName.isEmpty()
                        || marketplaceId == null || marketplaceId.isEmpty())) {
            // If all are not missing, all must be present
            AMTUAccount.throwXMLException(String.format(
                    Messages.AMTUAccount_17.toString(), // TODO: Change string
                    AMTUAccount.elementToString(siteGroupInfo)));
        }

        if (siteGroup == null) {
            if (!deleteSiteGroup) {
                // New site group definition
                siteGroup = new MerchantSiteGroup(account);
                siteGroup.setMerchantAlias(siteGroupName);
                siteGroup.setDocumentTransport(siteGroupDtd);

                MerchantSite site = new MerchantSite(siteGroup);
                site.setMerchantAlias(siteName);
                site.setMarketplaceId(marketplaceId);

                siteGroup.addMerchantSite(site);

                account.addSiteGroup(siteGroup);
            }
        }
        else if (deleteSiteGroup) {
            account.deleteSiteGroup(siteGroup);
        }
        else {
            // Update site group
            siteGroup.setDocumentTransport(siteGroupDtd);

            MerchantSite site = siteGroup.getSiteList().get(0);
            site.setMerchantAlias(siteName);
            site.setMarketplaceId(marketplaceId);
        }
    }


    protected synchronized void save(Connection conn) throws SQLException {
        if (siteGroupId < 0) {
            siteGroupId = db.createMerchantSiteGroup(conn,
                    parentAccount.getAccountId(), merchantAlias,
                    siteGroupDocumentTransport);
        }
        else {
            db.updateMerchantSiteGroup(conn, parentAccount.getAccountId(),
                    siteGroupId, merchantAlias, siteGroupDocumentTransport);
        }

        if (siteList != null) {
            for (MerchantSite site : siteList) {
                site.save(conn);
            }
        }
    }


    protected synchronized void delete(Connection conn) throws SQLException {
        if (parentAccount == null || parentAccount.getAccountId() < 0) {
            // parent not yet saved, nothing to delete
            return;
        }

        if (siteGroupId < 0) {
            // not yet saved, nothing to delete
            return;
        }

        if (siteList != null) {
            for (MerchantSite site : siteList) {
                site.delete(conn);
            }
        }

        db.deleteMerchantSiteGroupReports(conn, parentAccount.getAccountId(),
                siteGroupId);

        db.deleteMerchantSiteGroupFeeds(conn, parentAccount.getAccountId(),
                siteGroupId);

        db.deleteMerchantSiteGroup(conn, parentAccount.getAccountId(),
                siteGroupId);
    }


    // Getters and setters
    public AMTUAccount getParentAccount() {
        return parentAccount;
    }


    public void setParentAccount(AMTUAccount account) {
        parentAccount = account;
    }


    public int getSiteGroupId() {
        return siteGroupId;
    }


    public String getDocumentTransport() {
        return siteGroupDocumentTransport;
    }


    public void setDocumentTransport(String documentTransport) {
        siteGroupDocumentTransport = documentTransport;
    }


    public String getMerchantAlias() {
        return merchantAlias;
    }


    public void setMerchantAlias(String merchantAlias) {
        this.merchantAlias = merchantAlias;
    }


    public List<MerchantSite> getSiteList() {
        return siteList;
    }


    public void setSiteList(List<MerchantSite> siteList) {
        this.siteList = siteList;
    }
}