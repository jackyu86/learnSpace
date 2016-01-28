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

package com.amazon.merchants.daemon.model;

import java.text.MessageFormat;

import com.amazon.merchants.Messages;
import com.amazon.merchants.daemon.util.TransportConfigureUtil;
import com.amazon.merchants.database.Database;
import com.amazon.merchants.database.DatabaseException;
import com.amazon.merchants.gui.model.TransportGuiModel;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.transport.model.MainModel;

public class ConfigureModel {
    private static final String SETUP_COMMAND = "setup"; //$NON-NLS-1$
    private static final String EXPORT_COMMAND = "export"; //$NON-NLS-1$
    private static final String HELP_COMMAND = "help"; //$NON-NLS-1$
    private static final String PROXY_COMMAND = "proxy"; //$NON-NLS-1$

    private static final ConfigureModel instance = new ConfigureModel();

    private Database db = Database.getInstance();
    private TransportGuiModel guiModel = TransportGuiModel.getInstance();


    private ConfigureModel() {
    }


    public static ConfigureModel getInstance() {
        return instance;
    }


    public void start(String action, String filename) {
        // Application Information
        System.setProperty("app.name", MainModel.APP_NAME); //$NON-NLS-1$
        System.setProperty("app.name.display", MainModel.APP_NAME_DISPLAY); //$NON-NLS-1$
        System.setProperty("app.version", MainModel.APP_VERSION); //$NON-NLS-1$

        boolean error = false;
        try {
            // Initialize global loggers
            TransportLogger.init(guiModel);

            // Initialize with command line option
            TransportLogger.init();

            // Start database instance
            db.start();

            Object[] messageArguments = {
                    System.getProperty("app.name.display"), //$NON-NLS-1$
                    System.getProperty("app.version") //$NON-NLS-1$
            };
            MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
            formatter.applyPattern(Messages.ConfigureModel_0.toString());
            TransportLogger.getSysAuditLogger().info(
                    formatter.format(messageArguments));

            // Assign action based on parameter names
            if (action.equalsIgnoreCase(SETUP_COMMAND)) {
                TransportConfigureUtil.setupMerchantAccounts(filename);
            }
            else if (action.equalsIgnoreCase(EXPORT_COMMAND)) {
                TransportConfigureUtil.exportMerchantAccounts(filename);
            }
            else if (action.equalsIgnoreCase(HELP_COMMAND)) {
                TransportConfigureUtil.printManual(System.out);
            }
            else if (action.equalsIgnoreCase(PROXY_COMMAND)) {
            	TransportConfigureUtil.setupProxyConfiguration(filename);
            }
            else {
                // Exception if name is not recognized
                throw new IllegalArgumentException(
                        Messages.ConfigureModel_1.toString() + action);
            }

            db.shutdown();
        }
        catch (DatabaseException e) {
            error = true;
            TransportLogger.getSysErrorLogger().fatal(
                    e.getLocalizedMessage()
                            + System.getProperty("line.separator") + //$NON-NLS-1$
                            Messages.ConfigureModel_2.toString());
        }
        catch (Exception e) {
            error = true;
            TransportLogger.getSysErrorLogger().fatal(e.getLocalizedMessage());
        }
        finally {
            if (error) {
                TransportLogger.getSysAuditLogger().info(
                        Messages.ConfigureModel_3.toString());
            }
            else {
                TransportLogger.getSysAuditLogger().info(
                        Messages.ConfigureModel_4.toString());
            }
        }
    }
}