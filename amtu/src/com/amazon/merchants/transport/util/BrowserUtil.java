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

package com.amazon.merchants.transport.util;

import com.amazon.merchants.Messages;
import com.amazon.merchants.transport.model.MainModel;

public class BrowserUtil {

    private static void askUserToOpenWebBrowser(String url) {
        String title = Messages.BrowserUtil_0.toString();
        String message = Messages.BrowserUtil_1.toString() + "\n" + url; //$NON-NLS-1$
        MainModel.getInstance().displayInfoMessage(title, message, true);
    }


    public static boolean openURLInBrowser(String url) {
        if (!java.awt.Desktop.isDesktopSupported()) {
            askUserToOpenWebBrowser(url);
            return false;
        }

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
            askUserToOpenWebBrowser(url);
            return false;
        }

        try {

            java.net.URI uri = new java.net.URI(url);
            desktop.browse(uri);
        }
        catch (Exception e) {
            askUserToOpenWebBrowser(url);
            return false;
        }

        return true;
    }
}