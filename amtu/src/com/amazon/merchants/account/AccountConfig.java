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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.amazon.merchants.Messages;
import com.amazon.merchants.database.Database;
import com.amazon.merchants.database.DatabaseException;

public class AccountConfig {
    public static enum SQL_COLUMNS {
        config_key, config_value
    };


    public static enum XML_ELEMENTS {
        accountconfig, config, key
    }


    public enum ValidKeys {
        FEED_DISPATCH_INTERVAL, PROCESSING_REPORT_RETRIEVAL_INTERVAL,
        REPORT_RETRIEVAL_INTERVAL, REPORTS_DISABLED
    }
    public static final String BOOLEAN_TRUE = "Y";
    public static final String BOOLEAN_FALSE = "N";

    public static final String FEED_DISPATCH_INTERVAL = "FEED_DISPATCH_INTERVAL";
    public static final String PROCESSING_REPORT_RETRIEVAL_INTERVAL = "PROCESSING_REPORT_RETRIEVAL_INTERVAL";
    public static final String REPORT_RETRIEVAL_INTERVAL = "REPORT_RETRIEVAL_INTERVAL";
    public static final String REPORTS_DISABLED = "REPORTS_DISABLED";

    public static final int MINIMUM_FEED_DISPATCH_INTERVAL = 2;
    public static final int MINIMUM_PROCESSING_REPORT_RETRIEVAL_INTERVAL = 2;
    public static final int MINIMUM_REPORT_RETRIEVAL_INTERVAL = 5;

    private static final Map<String, String> DEFAULT_CONFIG = new HashMap<String, String>();
    static {
        DEFAULT_CONFIG.put(FEED_DISPATCH_INTERVAL,
            Integer.toString(MINIMUM_FEED_DISPATCH_INTERVAL));
        DEFAULT_CONFIG.put(PROCESSING_REPORT_RETRIEVAL_INTERVAL,
            Integer.toString(MINIMUM_PROCESSING_REPORT_RETRIEVAL_INTERVAL));
        DEFAULT_CONFIG.put(REPORT_RETRIEVAL_INTERVAL,
            Integer.toString(MINIMUM_REPORT_RETRIEVAL_INTERVAL));
        DEFAULT_CONFIG.put(REPORTS_DISABLED, BOOLEAN_FALSE);
    }


    private static Map<String, String> copyDefaultConfig() {
        Map<String, String> copy = new HashMap<String, String>();
        for (Entry<String, String> entry : DEFAULT_CONFIG.entrySet()) {
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;
    }


    public static AccountConfig getDefaultConfig(AMTUAccount parentAccount) {
        AccountConfig config = new AccountConfig(parentAccount);
        config.configMap = copyDefaultConfig();

        return config;
    }

    private AMTUAccount parentAccount = null;
    private Map<String, String> configMap = null;
    private Set<String> deletedKeys = null;

    private static Database db = Database.getInstance();


    public static AccountConfig loadConfigForAccount(AMTUAccount account)
        throws DatabaseException, SQLException {
        AccountConfig config = new AccountConfig(account);
        config.configMap = db.pullAccountConfig(account.getAccountId());
        return config;
    }


    public AccountConfig() {
    }


    public AccountConfig(AMTUAccount parentAccount) {
        this.parentAccount = parentAccount;
    }


    public String get(String key) {
        if (configMap == null) {
            configMap = copyDefaultConfig();
        }

        return configMap.get(key);
    }


    public void put(String key, String value) {
        if (configMap == null) {
            configMap = copyDefaultConfig();
        }

        if (deletedKeys != null && deletedKeys.contains(key)) {
            deletedKeys.remove(key);

            if (deletedKeys.isEmpty()) {
                deletedKeys = null;
            }
        }

        configMap.put(key, value);
    }


    public void remove(String key) {
        if (configMap == null) {
            return;
        }

        if (configMap.containsKey(key)) {
            if (deletedKeys == null) {
                deletedKeys = new HashSet<String>();
            }

            deletedKeys.add(key);
            configMap.remove(key);
        }
    }


    public Element toXML(Document doc) {
        Element accountConfig = doc.createElement(XML_ELEMENTS.accountconfig
            .toString());
        for (Entry<String, String> entry : configMap.entrySet()) {
            Element config = doc.createElement(XML_ELEMENTS.config.toString());
            config.setAttribute(XML_ELEMENTS.key.toString(), entry.getKey());
            config.setTextContent(entry.getValue());
            accountConfig.appendChild(config);
        }
        return accountConfig;
    }


    protected void fromXML(AMTUAccount account, Element config)
        throws MerchantAccountException {
        String key = config.getAttribute(XML_ELEMENTS.key.toString());
        String value = config.getTextContent();

        if (key == null || key.isEmpty()) {
            AMTUAccount.throwXMLException(String.format(
                Messages.AMTUAccount_9.toString(),
                AMTUAccount.elementToString(config)));
        }
        try {
            ValidKeys keyEnum = ValidKeys.valueOf(key.trim());
            if (keyEnum == null) {
                throw new Exception();
            }
        }
        catch (Exception e) {
            AMTUAccount.throwXMLException(String.format(
                Messages.AMTUAccount_10.toString(), key));
        }

        if (value == null || value.trim().isEmpty()) {
            remove(key.trim());
        }
        else {
            put(key.trim(), value.trim());
        }
    }


    protected synchronized void save(Connection conn) throws SQLException {
        // if (parentAccount == null || parentAccount.getAccountId() < 0) {
        // throw new Exception("Parent account not yet saved");
        // }

        if (deletedKeys != null) {
            for (String key : deletedKeys) {
                db.deleteAccountConfigByKey(conn, parentAccount.getAccountId(),
                    key);
            }
        }

        if (configMap != null) {
            for (Entry<String, String> entry : configMap.entrySet()) {
                if (db.updateAccountConfigByKey(conn,
                    parentAccount.getAccountId(), entry.getKey(),
                    entry.getValue()) <= 0) {
                    db.insertAccountConfigByKey(conn,
                        parentAccount.getAccountId(), entry.getKey(),
                        entry.getValue());
                }
            }
        }
    }


    protected synchronized void delete(Connection conn) throws SQLException {
        if (parentAccount == null || parentAccount.getAccountId() < 0) {
            // parent not yet saved, nothing to delete
            return;
        }

        db.deleteAccountConfig(conn, parentAccount.getAccountId());
    }


    // Getters and setters
    public AMTUAccount getParentAccount() {
        return parentAccount;
    }


    public void setParentAccount(AMTUAccount parentAccount) {
        this.parentAccount = parentAccount;
    }
}