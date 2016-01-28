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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import com.amazon.merchants.logger.TransportLogger;

public class QAUtility {
    private static final File file = new File(
        DirectoryUtil
            .makeAMTUWorkingPath("amzn_qa_preferences_091124.properties"));


    private static boolean isPresent() {
        return file.canRead();
    }


    public static Properties getPreferences() {
        Properties props = new Properties();
        FileInputStream istream = null;
        if (isPresent()) {
            try {
                istream = new FileInputStream(file);
                props.load(istream);
            }
            catch (IOException e) {
                TransportLogger.getSysErrorLogger().fatal(
                    e.getLocalizedMessage(), e);
            }
            finally {
                IO.closeSilently(istream);
            }
        }

        return props;
    }


    public static boolean isDevoIncluded() {
        String val = getPreferences().getProperty("conf_show_devo");
        return val == null ? false : val.equalsIgnoreCase("true");
    }


    public static int getFeedDelay() {
        String val = getPreferences().getProperty("conf_fd_delay_in_min");
        return val == null ? 2 : Integer.parseInt(val);
    }


    public static int getProcessingReportDelay() {
        String val = getPreferences().getProperty("conf_pc_delay_in_min");
        return val == null ? 2 : Integer.parseInt(val);
    }


    public static int getReportDelay() {
        String val = getPreferences().getProperty("conf_rt_delay_in_min");
        return val == null ? 5 : Integer.parseInt(val);
    }


    public static int getUpdateCheckDelay() {
        String val = getPreferences().getProperty("conf_uc_delay_in_hours");
        return val == null ? 24 : Integer.parseInt(val);
    }


    public static int getUpdateCheckHour() {
        int result;

        String val = getPreferences().getProperty("conf_uc_check_hour");

        if ("now".equalsIgnoreCase(val)) {
            result = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        }
        else {
            result = val == null ? 3 : Integer.parseInt(val);
        }

        return result;
    }


    public static void main(String[] args) {
        System.out.println(getProcessingReportDelay());
    }
}
