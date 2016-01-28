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

package com.amazon.merchants.system;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.amazon.merchants.Messages;
import com.amazon.merchants.account.MerchantAccountException;
import com.amazon.merchants.database.Database;
import com.amazon.merchants.database.DatabaseException;
import com.amazon.merchants.logger.TransportLogger;

/**
 * Encapsulates data needed for manipulating and storing proxy settings
 */
public class ProxyConfig {
    public static enum SQL_COLUMNS {
        proxy_id, proxy_host, proxy_port, proxy_user, proxy_pass
    };
    
    public static enum XML_ELEMENTS {
        proxy, delete, host, port, username, password
    };
    
    private int id = -1;
    private String host = null;
    private int port = -1;
    private String username = null;
    private String password = null;
    
    private static Database db = Database.getInstance();
    
    /**
     * Parses the proxy information queried from the database
     * 
     * @param details Map of SQL query results for proxy info
     * @return ProxyConfig object with data from SQL query
     */
    private static ProxyConfig parseProxyDetails(Map<String, Object> details) {
        ProxyConfig proxy = new ProxyConfig();
        proxy.id = (Integer) details.get(SQL_COLUMNS.proxy_id.toString());
        proxy.host = (String) details.get(SQL_COLUMNS.proxy_host.toString());
        proxy.port = (Integer) details.get(SQL_COLUMNS.proxy_port.toString());
        proxy.username = (String) details.get(SQL_COLUMNS.proxy_user.toString());
        proxy.password = (String) details.get(SQL_COLUMNS.proxy_pass.toString());
        
        return proxy;
    }
    
    /**
     * Queries the database for proxy settings
     * 
     * @return ProxyConfig object from SQL query
     * @throws DatabaseException
     * @throws SQLException
     */
    public static ProxyConfig pullProxyConfiguration() throws DatabaseException, SQLException {
        Map<String, Object> details = db.pullProxyInfo();
        if(!details.isEmpty()) {
            return parseProxyDetails(details);
        } else
            return null;
    }
    
    /**
     * Saves the proxy configuration to the database
     * 
     * @throws MerchantAccountException
     */
    public synchronized void save() throws ProxyException {
        Connection conn = null;
        try {
            conn = Database.getUnmanagedConnection();
            conn.setAutoCommit(false);
            if(db.pullProxyCount() < 1) {
                try {
                    db.createProxyConfig(conn, host, port, username, password);
                    conn.commit();
                }
                catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            } else {
                try {
                    db.updateProxyConfig(conn, host, port, username, password, pullProxyConfiguration().id);
                    conn.commit();
                    
                    TransportLogger.getSysAuditLogger().info(
                        Messages.ProxyConfig_3.toString());
                }
                catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            }

        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                String.format(Messages.Database_10.toString(), e.getLocalizedMessage()), e);
        }
        catch (SQLException e) {
            e.printStackTrace();
            TransportLogger.getSysErrorLogger().fatal(Messages.ProxyConfig_0.toString(), e);
            throw new ProxyException(Messages.ProxyConfig_0.toString());
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
     * Deletes the current proxy configuration
     * 
     * @throws MerchantAccountException
     */
    public static synchronized void delete() throws ProxyException {
        Connection conn = null;
        try {
            conn = Database.getUnmanagedConnection();
            conn.setAutoCommit(false);

            try {
                if(db.pullProxyCount() > 0) {
                    db.deleteProxyConfig(conn);
                    conn.commit();

                    TransportLogger.getSysAuditLogger().info(
                        Messages.ProxyConfig_2.toString());

                    return;
                }
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
            TransportLogger.getSysErrorLogger().fatal(Messages.ProxyConfig_1.toString(), e);
            throw new ProxyException(Messages.ProxyConfig_1.toString());
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
    
    public ProxyConfig() {
    }
    
    public ProxyConfig(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
    
    public ProxyConfig(String host, int port) {
        this.host = host;
        this.port = port;
        this.username = null;
        this.password = null;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
