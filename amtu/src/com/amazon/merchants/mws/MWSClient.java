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

package com.amazon.merchants.mws;

import java.sql.SQLException;

import com.amazon.merchants.Messages;
import com.amazon.merchants.database.DatabaseException;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.system.ProxyConfig;
import com.amazon.merchants.transport.model.MainModel;
import com.amazonaws.mws.MarketplaceWebServiceClient;
import com.amazonaws.mws.MarketplaceWebServiceConfig;

public class MWSClient {
    public static final int DEFAULT_TIMEOUT = 50000;


    /**
     * Encapsulates creating a new MWS Client so we get consistent user agent
     * strings for all AMTU requests
     *
     * @param accessKeyId MWS Access Key
     * @param secretAccessKey MWS Secret Key
     * @param appName App Name
     * @param appVersion App Version
     * @param endpoint MWS Endpoint
     * @return MWS Client instance
     */
    public static MarketplaceWebServiceClient createMWSClient(String accessKeyId, String secretAccessKey,
        String appName, String appVersion, String endpoint, int timeout, ProxyConfig tempProxy) {
        MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();

        if (timeout > DEFAULT_TIMEOUT) {
            config.setSoTimeout(timeout);
        }
        
        // Setting proxy settings for MWS config
        ProxyConfig proxy;
        if(tempProxy == null) { 
            try {
                proxy = ProxyConfig.pullProxyConfiguration();
            } catch (DatabaseException e) {
                proxy = null;
                TransportLogger.getSysAuditLogger().info(
                    Messages.ProxyConfig_4.toString());
            } catch (SQLException e) {
                proxy = null;
                TransportLogger.getSysAuditLogger().info(
                    Messages.ProxyConfig_4.toString());
            }
        } else {
            proxy = tempProxy;
        }
        
        if(proxy != null) {
            String proxyHost = proxy.getHost();
            int proxyPort = proxy.getPort();
            String proxyUser = proxy.getUsername();
            String proxyPass = proxy.getPassword();
        
            if (proxyHost != null && !proxyHost.equals("")) {
                config.setProxyHost(proxyHost);
            }
            if (proxyPort != -1) {
                try {
                    config.setProxyPort(proxyPort);
                }
                catch (NumberFormatException e) {
                    TransportLogger.getSysAuditLogger().warn("Proxy port invalid: " + proxyPort);
                }
            }
            if (proxyUser != null && !proxyUser.equals("")) {
                config.setProxyUsername(proxyUser);
            }
            if (proxyPass != null && !proxyPass.equals("")) {
                config.setProxyPassword(proxyPass);
            }
        } else {
            // If there's no defined value, try to grab the system proxy settings
            String proxyHost = System.getProperty("https.proxyHost");
            String proxyPort = System.getProperty("https.proxyPort");
            
            if(proxyHost != null && !proxyHost.equals("")) {
                config.setProxyHost(proxyHost);
            }
            if(proxyPort != null && !proxyPort.equals("")) {
                config.setProxyPort(Integer.parseInt(proxyPort));
            }
        }
        
        config.setServiceURL(endpoint);
        config.setUserAgent(
            System.getProperty("app.name"),

            System.getProperty("app.version"),

            "Java/" + System.getProperty("java.version") + "/" + System.getProperty("java.class.version") + "/"
                + System.getProperty("java.vendor"),

            "Platform",
            System.getProperty("os.name") + "/" + System.getProperty("os.arch") + "/"
                + System.getProperty("os.version"),

            "MWSClientVersion", "2011-06-01",

            "AMTURunConfig", MainModel.getInstance().isGUI() ? "GUI" : "Headless");

        return new MarketplaceWebServiceClient(accessKeyId, secretAccessKey, System.getProperty("app.name"),
            System.getProperty("app.version"), config);
    }
}
