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
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public enum FeedTypeEnum {
    // IMPORTANT: executes in the order of declaration

    BOOKLOADER("_POST_FLAT_FILE_BOOKLOADER_DATA_",
            new BookLoaderFeedRule(),
            false),
    PRICEANDQUANTITY("_POST_FLAT_FILE_PRICEANDQUANTITYONLY_UPDATE_DATA_",
            new PriceAndQuantityFeedRule(),
            false),
    INVLOADER("_POST_FLAT_FILE_INVLOADER_DATA_",
            new InvLoaderFeedRule(),
            false),

    // XML Format Feed Types
    PRODUCT("_POST_PRODUCT_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.XML,
                    "<MessageType>Product</MessageType>"),
            false),
    TEST_ORDER("_POST_TEST_ORDERS_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.XML,
                    "<MessageType>TestOrderRequest</MessageType>"),
            false),
    PAYMENT_ADJUSTMENT("_POST_PAYMENT_ADJUSTMENT_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.XML,
                    "<MessageType>OrderAdjustment</MessageType>"),
            false),
    ORDER_FULFILLMENT("_POST_ORDER_FULFILLMENT_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.XML,
                    "<MessageType>OrderFulfillment</MessageType>"),
            false),
    PRODUCT_RELATIONSHIP("_POST_PRODUCT_RELATIONSHIP_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.XML,
                    "<MessageType>Relationship</MessageType>"),
            false),
    PRODUCT_OVERRIDE("_POST_PRODUCT_OVERRIDES_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.XML,
                    "<MessageType>Override</MessageType>"),
            false),
    IMAGE("_POST_PRODUCT_IMAGE_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.XML,
                    "<MessageType>ProductImage</MessageType>"),
            false),
    PRICING("_POST_PRODUCT_PRICING_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.XML,
                    "<MessageType>Price</MessageType>"),
            false),
    INVENTORY("_POST_INVENTORY_AVAILABILITY_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.XML,
                    "<MessageType>Inventory</MessageType>"),
            false),
    ACES("_POST_ACES_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.XML,
                    "<MessageType>ACES</MessageType>"),
            false),
    PIES("_POST_PIES_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.XML,
                    "<MessageType>PIES</MessageType>"),
            false),
    ORDER_ACKNOWLEDGEMENT("_POST_ORDER_ACKNOWLEDGEMENT_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.XML,
                    "<MessageType>OrderAcknowledgement</MessageType>"),
            false),
    SINGLE_FORMAT_ITEM("_POST_ITEM_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.XML,
                    "<MessageType>Item</MessageType>"),
            false),
    STD_ACES("_POST_STD_ACES_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.XML,
                    "<ACES"),
            false),
    STD_PIES("_POST_STD_PIES_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.XML,
                    "<PartInformation"),
            false),

    // FLAT FILE FEED TYPES
    FLAT_INVOICE_CONFIRMATION("_POST_FLAT_FILE_INVOICE_CONFIRMATION_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.TXT,
                    "TemplateType=InvoiceConfirm"),
            false),
    FLAT_ORDER_FULFILLMENT("_POST_FLAT_FILE_FULFILLMENT_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.TXT,
                    "id	order-item-id	quantity"),
            false),
    FLAT_PAYMENT_ADJUSTMENT("_POST_FLAT_FILE_PAYMENT_ADJUSTMENT_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.TXT,
                    "order-id	order-item-id	adjustment-reason-code"),
            false),
    FLAT_ORDER_ACKNOWLEDGEMENT("_POST_FLAT_FILE_ORDER_ACKNOWLEDGEMENT_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.TXT,
                    "TemplateType=OrderCancellation"),
            false),
    FLAT_TEST_ORDER("_POST_FLAT_FILE_TEST_ORDER_DATA_",
            new GenericFeedRule(
                    FeedExtensionEnum.TXT,
                    "TemplateType=TestOrder"),
            false),
    SHOPZILLA("_POST_FLAT_FILE_SHOPZILLA_DATA_",
            new FlatFileShopzillaFeedRule("Category,Title,Link,SKU,Price"),
            false),
    FLAT_PRODUCT("_POST_FLAT_FILE_LISTINGS_DATA_",
            new FlatFileProductFeedRule(),
            false);

    private String enumeration;
    private IRule rule;
    private boolean global;


    private FeedTypeEnum(String enumeration, IRule rule, boolean global) {
        this.enumeration = enumeration;
        this.rule = rule;
        this.global = global;
    }


    public static boolean isExtensionSupported(File file) {
        for (FeedExtensionEnum ext : FeedExtensionEnum.values()) {
            // Make sure capitalized extensions are also supported
            if (file.isFile() && file.getName().toLowerCase().endsWith("." + ext.getExtension())) {
                return true;
            }
        }
        return false;
    }


    public String getEnumeration() {
        return enumeration;
    }


    public IRule getRule() {
        return rule;
    }


    public boolean isGlobal() {
        return global;
    }


    public static boolean isFlatFileExtension(String ext) {
        return ext.equals(FeedExtensionEnum.TXT.getExtension());
    }
}

enum FeedExtensionEnum {
    XML("xml"), TXT("txt"), GZ("gz");

    private String extension;


    FeedExtensionEnum(String extension) {
        this.extension = extension;
    }


    public String getExtension() {
        return extension;
    }


    @Override
    public String toString() {
        return extension;
    }
}

class GenericFeedRule implements IRule {
    private FeedExtensionEnum format;
    private String key;


    public GenericFeedRule(FeedExtensionEnum format, String key) {
        setFormat(format);
        this.key = key;
    }


    @Override
    public boolean execute(String header, String ext) {
        return key != null && header.toLowerCase().indexOf(key.toLowerCase()) != -1;
    }


    public void setFormat(FeedExtensionEnum format) {
        this.format = format;
    }


    public FeedExtensionEnum getFormat() {
        return format;
    }
}

class FlatFileProductFeedRule implements IRule {

    public FlatFileProductFeedRule() {
    }


    @Override
    public boolean execute(String header, String ext) {
        return header.toLowerCase().indexOf("TemplateType=".toLowerCase()) == 0
            && header.toLowerCase().indexOf("TemplateType=Shopzilla".toLowerCase()) != 0;
    }
}

/**
 * Shopzilla strategy - algorithm taken from the previous version
 */
class FlatFileShopzillaFeedRule implements IRule {
    private String key;


    public FlatFileShopzillaFeedRule(String key) {
        this.key = key;
    }


    @Override
    public boolean execute(String header, String ext) {
        if (header.toLowerCase().indexOf("TemplateType=Shopzilla".toLowerCase()) == 0) {
            return true;
        }

        String[] shopzillaRequiredColumms = StringUtils.stripAll(StringUtils.split(key, ","));

        if (shopzillaRequiredColumms == null || shopzillaRequiredColumms.length == 0) {
            return false;
        }

        for (int i = 0; i < shopzillaRequiredColumms.length; i++) {
            if (!StringUtils.contains(header, shopzillaRequiredColumms[i])) {
                return false;
            }
        }
        return true;
    }
}

/**
 * Book Loader strategy
 */
class BookLoaderFeedRule implements IRule {
    // List of book loader's requirement columns
    private final String[] cols = new String[] { "sku", "author", "title" };


    @Override
    public boolean execute(String header, String ext) {
        String headerSearch = header.toLowerCase();
        int indexOfNewLineChar = headerSearch.indexOf('\n');
        if (indexOfNewLineChar != -1) {
            headerSearch = headerSearch.substring(0, indexOfNewLineChar);
        }
        headerSearch = headerSearch.replaceAll("\r", "");

        // Reject if XML found
        if (headerSearch.indexOf('<') >= 0) {
            return false;
        }

        // See if all required column names exist in the header
        for (String c : cols) {
            if (!headerSearch.contains(c)) {
                return false;
            }
        }
        return true;
    }
}

/**
 * Inventory Loader strategy
 */
class InvLoaderFeedRule implements IRule {
    // List of inventory loader's requirement columns
    private final String[] colsReq = new String[] { "sku" };
    // List of inventory loader's optional columns, need to have at least one
    private final String[] colsOpt = new String[] { "quantity", "price" };


    @Override
    public boolean execute(String header, String ext) {
        // Handle the "upgraded" flat file type
        if (header.indexOf("TemplateType=InventoryLoader") == 0) {
            return true;
        }

        String headerSearch = header.toLowerCase();

        int indexOfNewLineChar = headerSearch.indexOf('\n');
        if (indexOfNewLineChar != -1) {
            headerSearch = headerSearch.substring(0, indexOfNewLineChar);
        }
        headerSearch = headerSearch.replaceAll("\r", "");

        // Reject if XML found
        if (headerSearch.indexOf('<') >= 0) {
            return false;
        }

        // Check if all required column names exist in the header
        for (String req : colsReq) {
            if (!headerSearch.contains(req)) {
                return false;
            }
        }

        // Check if header has a least one optional columns
        for (String opt : colsOpt) {
            if (headerSearch.contains(opt)) {
                return true;
            }
        }

        // Return false if the header does not have a least one optional columns
        return false;
    }
}

class PriceAndQuantityFeedRule implements IRule {
    // List of P&Q's requirement columns
    private final String[] colsReq = new String[] { "sku" };
    // List of columns that are allowed to appear in the P&Q feed
    private final List<String> colsLimit = Arrays.asList(new String[] { "sku", "quantity", "price", "leadtime-to-ship" });


    @Override
    public boolean execute(String header, String ext) {
        // Handle the "upgraded" flat file type
        if (header.indexOf("TemplateType=PriceInventory") == 0) {
            return true;
        }

        String headerSearch = header.toLowerCase();

        int indexOfNewLineChar = headerSearch.indexOf('\n');
        if (indexOfNewLineChar != -1) {
            headerSearch = headerSearch.substring(0, indexOfNewLineChar);
        }
        headerSearch = headerSearch.replaceAll("\r", "");

        // Reject if XML found
        if (headerSearch.indexOf('<') >= 0) {
            return false;
        }

        // Check if all required column names exist in the header
        for (String req : colsReq) {
            if (!headerSearch.contains(req)) {
                return false;
            }
        }

        // Check that only the allowed columns appear
        List<String> headerColumns = Arrays.asList(headerSearch.split("\t"));
        return headerColumns.size() <= colsLimit.size() && colsLimit.containsAll(headerColumns);
    }
}
