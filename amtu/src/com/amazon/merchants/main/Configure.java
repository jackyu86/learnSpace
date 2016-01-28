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

package com.amazon.merchants.main;

import com.amazon.merchants.daemon.model.ConfigureModel;
import com.amazon.merchants.daemon.util.TransportConfigureUtil;
import com.amazon.merchants.logger.TransportLogger;

public class Configure {
    private static final ConfigureModel model = ConfigureModel.getInstance();


    public static void main(String[] args) {
        // Check number of parameters is correct
        if (args.length > 2 || args.length < 1) {
            TransportConfigureUtil.printManual(System.out);
            return;
        }

        String action = args[0].toUpperCase();
        String filename = args.length == 2 ? args[1] : null;

        try {
            model.start(action, filename);
        }
        catch (IllegalArgumentException e) {
            TransportLogger.getSysAuditLogger().info(e.getLocalizedMessage());
            TransportConfigureUtil.printManual(System.out);
        }
    }
}
