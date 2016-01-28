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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazon.merchants.database.Database;
import com.amazon.merchants.database.DatabaseException;

public class MerchantSite {
    public static enum SQL_COLUMNS {
        site_id, marketplace_id, merchant_alias
    };

    private MerchantSiteGroup parentSiteGroup = null;
    private int siteId = -1;
    private String marketplaceId = null;
    private String merchantAlias = null;

    private static Database db = Database.getInstance();


    public static MerchantSite loadMerchantSite(MerchantSiteGroup siteGroup,
            int siteId) throws DatabaseException, SQLException {
        if (siteId < 0) {
            return null;
        }

        Map<String, String> siteDetails = db.pullMerchantSiteDetails(
                siteGroup.getSiteGroupId(), siteId);
        if (siteDetails == null) {
            return null;
        }

        MerchantSite site = new MerchantSite();
        site.parentSiteGroup = siteGroup;
        site.siteId = siteId;
        site.marketplaceId = siteDetails.get(SQL_COLUMNS.marketplace_id
                .toString());
        site.merchantAlias = siteDetails.get(SQL_COLUMNS.merchant_alias
                .toString());

        return site;
    }


    public static List<MerchantSite> loadAllForSiteGroup(
            MerchantSiteGroup siteGroup) throws DatabaseException, SQLException {
        List<MerchantSite> siteList = new ArrayList<MerchantSite>();

        List<Integer> siteIds = db.listAllSitesBySiteGroup(siteGroup
                .getSiteGroupId());
        for (int siteId : siteIds) {
            MerchantSite site = loadMerchantSite(siteGroup, siteId);
            if (site != null) {
                siteList.add(site);
            }
        }

        return siteList;
    }


    public MerchantSite() {

    }


    public MerchantSite(MerchantSiteGroup siteGroup) {
        parentSiteGroup = siteGroup;
    }


    public synchronized void save(Connection conn) throws SQLException {
        if (siteId < 0) {
            siteId = db.createMerchantSite(conn,
                    parentSiteGroup.getSiteGroupId(), merchantAlias,
                    marketplaceId);
        }
        else {
            db.updateMerchantSite(conn, parentSiteGroup.getSiteGroupId(),
                    siteId, merchantAlias, marketplaceId);
        }
    }


    public synchronized void delete(Connection conn) throws SQLException {
        if (parentSiteGroup == null || parentSiteGroup.getSiteGroupId() < 0) {
            // parent not yet saved, nothing to delete
            return;
        }

        if (siteId < 0) {
            // site not yet saved, nothing to delete
            return;
        }

        db.deleteMerchantSite(conn, parentSiteGroup.getSiteGroupId(), siteId);
    }


    // Getters and setters
    public MerchantSiteGroup getParentSiteGroup() {
        return parentSiteGroup;
    }


    public int getSiteId() {
        return siteId;
    }


    public String getMarketplaceId() {
        return marketplaceId;
    }


    public void setMarketplaceId(String marketplaceId) {
        this.marketplaceId = marketplaceId;
    }


    public String getMerchantAlias() {
        return merchantAlias;
    }


    public void setMerchantAlias(String merchantAlias) {
        this.merchantAlias = merchantAlias;
    }
}