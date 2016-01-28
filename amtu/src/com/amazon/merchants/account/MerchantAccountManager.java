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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.amazon.merchants.Messages;
import com.amazon.merchants.database.DatabaseException;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.util.file.TransportUtil;

public class MerchantAccountManager {

    private static List<AMTUAccount> accounts = Collections
            .synchronizedList(new ArrayList<AMTUAccount>());


    public static void setupMerchantAccounts() throws MerchantAccountException,
            IOException {
        try {
            synchronized (accounts) {
                accounts.clear();
                accounts.addAll(AMTUAccount.loadAllAccounts());
                for (AMTUAccount account : accounts) {
                    TransportUtil.createMerchantDirectoryStructure(account
                            .getDocumentTransport());

                    if (account.getSiteGroups() != null) {
                        for (MerchantSiteGroup siteGroup : account
                                .getSiteGroups()) {
                            TransportUtil
                                    .createSiteGroupDirectoryStructure(siteGroup
                                            .getDocumentTransportFolder());
                        }
                    }
                }
            }
            if (accounts.size() == 0) {
                // Main will catch this exception and trigger application
                // initial setup
                throw new MerchantAccountException(
                        Messages.MerchantAccountManager_0.toString());
            }
        }
        catch (SQLException e) {
            TransportLogger.getSysErrorLogger().fatal(
                    Messages.MerchantAccountManager_18.toString(), e);
        }
        catch (DatabaseException e) {
            TransportLogger.getSysErrorLogger().error(
                    String.format(Messages.Database_10.toString(),
                            e.getLocalizedMessage()), e);
        }
    }


    public static List<AMTUAccount> getAccountList() {
        Validate.notNull(accounts);
        return accounts;
    }
}