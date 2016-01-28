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

import java.util.ArrayList;
import java.util.List;

import com.amazon.merchants.Messages;

public class AmazonMarketplaceSite {
    private String siteName = "";
    private String alias = "";
    private String shortCode = "";
    private String marketplaceId = "";

    private static List<AmazonMarketplaceSite> amazonSites = new ArrayList<AmazonMarketplaceSite>();
    static {
        amazonSites.add(new AmazonMarketplaceSite(
            Messages.AmazonMarketplaceSite_0.toString(),
            Messages.MWSEndpoint_1.toString(), "CA",
            "A2EUQ1WTGCTBG2"
        )); // CA

        amazonSites.add(new AmazonMarketplaceSite(
            Messages.AmazonMarketplaceSite_1.toString(),
            Messages.MWSEndpoint_2.toString(), "CN",
            "AAHKV2X7AFYLW"
        )); // CN

        amazonSites.add(new AmazonMarketplaceSite(
            Messages.AmazonMarketplaceSite_2.toString(),
            Messages.MWSEndpoint_3.toString(), "FR",
            "A13V1IB3VIYZZH"
        )); // FR

        amazonSites.add(new AmazonMarketplaceSite(
            Messages.AmazonMarketplaceSite_3.toString(),
            Messages.MWSEndpoint_4.toString(), "DE",
            "A1PA6795UKMFR9"
        )); // DE

        amazonSites.add(new AmazonMarketplaceSite(
            Messages.AmazonMarketplaceSite_4.toString(),
            Messages.MWSEndpoint_8.toString(), "IT",
            "APJ6JRA9NG5V4"
        )); // IT

        amazonSites.add(new AmazonMarketplaceSite(
            Messages.AmazonMarketplaceSite_5.toString(),
            Messages.MWSEndpoint_5.toString(), "JP",
            "A1VC38T7YXB528"
        )); // JP

        amazonSites.add(new AmazonMarketplaceSite(
            Messages.AmazonMarketplaceSite_8.toString(),
            Messages.MWSEndpoint_9.toString(), "ES",
            "A1RKKUPIHCS9HS"
        )); // ES

        amazonSites.add(new AmazonMarketplaceSite(
                Messages.AmazonMarketplaceSite_9.toString(),
                Messages.MWSEndpoint_10.toString(), "IN",
                "A21TJRUUN4KGV"
            )); // IN
        
        amazonSites.add(new AmazonMarketplaceSite(
            Messages.AmazonMarketplaceSite_6.toString(),
            Messages.MWSEndpoint_6.toString(), "GB",
            "A1F83G8C2ARO7P"
        )); // UK

        amazonSites.add(new AmazonMarketplaceSite(
            Messages.AmazonMarketplaceSite_7.toString(),
            Messages.MWSEndpoint_7.toString(), "US",
            "ATVPDKIKX0DER"
        )); // US
    }


    public static List<AmazonMarketplaceSite> getAmazonSites() {
        return amazonSites;
    }


    /**
     * Creates a new Amazon marketplace site
     *
     * @param siteName Descriptive name of Amazon marketplace site
     * @param alias Country name alias for site
     * @param shortCode Country short-code for DTD naming suggestion
     * @param marketplaceId Marketplace ID for site
     */
    public AmazonMarketplaceSite(String siteName, String alias, String shortCode, String marketplaceId) {
        this.siteName = siteName;
        this.alias = alias;
        this.shortCode = shortCode;
        this.marketplaceId = marketplaceId;
    }


    @Override
    public String toString() {
        return siteName;
    }


    public String getSiteName() {
        return siteName;
    }


    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }


    public String getAlias() {
        return alias;
    }


    public void setAlias(String alias) {
        this.alias = alias;
    }


    public String getShortCode() {
        return shortCode;
    }


    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }


    public String getMarketplaceId() {
        return marketplaceId;
    }


    public void setMarketplaceId(String marketplaceId) {
        this.marketplaceId = marketplaceId;
    }
}
