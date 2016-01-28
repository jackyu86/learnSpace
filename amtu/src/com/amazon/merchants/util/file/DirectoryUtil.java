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

package com.amazon.merchants.util.file;

import java.io.File;

public class DirectoryUtil {
    private static final String AMTU_WORKING_DIR = System
            .getProperty("user.home") + File.separator + "amtu2";
    private static final String SYSLOG_DIR = AMTU_WORKING_DIR + File.separator
            + "syslogs";
    private static final String COMPANY_ICON_FILENAME = "AS_coname.gif";

    private static final String IMAGE_DIR = "/img";


    static public String getSyslogDir() {
        return SYSLOG_DIR;
    }


    static public String getAMTUWorkingDir() {
        return AMTU_WORKING_DIR;
    }


    static public String makeSyslogPath(String filename) {
        return SYSLOG_DIR + File.separator + filename;
    }


    static public String makeAMTUWorkingPath(String filename) {
        return AMTU_WORKING_DIR + File.separator + filename;
    }


    static public String getImageDir() {
        return IMAGE_DIR;
    }


    static public String makeImagePath(String filename) {
        return getImageDir() + "/" + filename;
    }


    static public String getCompanyNameImagePath() {
        return makeImagePath(COMPANY_ICON_FILENAME);
    }


    static public String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }


    static public File makeTempFile(String filename) {
        return new File(getTempDir() + File.separator + filename);
    }
}
