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
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.commons.lang.Validate;

import com.amazon.merchants.Messages;
import com.amazon.merchants.account.MerchantAccountException;

public class TransportUtil {
    private static final String ENVIRONMENT = "production"; //$NON-NLS-1$


    public static File getDirectory(File root, DirectoryEnum de) {
        Validate.notNull(root);
        Validate.notNull(de);
        File dir = new File(root.getAbsolutePath() + File.separator
                + de.getKey());
        Validate.notNull(dir);
        Validate.isTrue(dir.isDirectory());
        return dir;
    }


    public static File getLogFile(File root, FileEnum fe) {
        Validate.notNull(root);
        Validate.notNull(fe);
        File file = new File(getDirectory(root, DirectoryEnum.LOGS)
                + File.separator + fe.getKey());
        Validate.notNull(file);
        return file;
    }


    /**
     * Given the root document transport directory Create the sub directory
     * structure
     *
     * @param root
     * @throws IOException
     */
    public static void createMerchantDirectoryStructure(File root)
            throws IOException {
        // Check if root directory is valid
        Validate.notNull(root);
        if (!root.exists()) {
            createDirectory(root);
        }
        Validate.isTrue(root.exists() && root.isDirectory());

        // Setup 2nd level directories
        File logs = new File(root.getAbsoluteFile() + File.separator
                + DirectoryEnum.LOGS);
        File production = new File(root.getAbsoluteFile() + File.separator
                + ENVIRONMENT);
        if (!logs.exists()) {
            createDirectory(logs);
        }
        if (!production.exists()) {
            createDirectory(production);
        }

        createSiteGroupDirectoryStructure(production);

        // Production root gets Reports directory, site groups do not
        File reports = new File(production.getAbsoluteFile() + File.separator
                + DirectoryEnum.REPORTS);
        if (!reports.exists()) {
            createDirectory(reports);
        }
    }


    public static void createSiteGroupDirectoryStructure(File siteGroupRoot)
            throws IOException {
        // Check if root directory is valid
        Validate.notNull(siteGroupRoot);
        if (!siteGroupRoot.exists()) {
            createDirectory(siteGroupRoot);
        }
        Validate.isTrue(siteGroupRoot.exists() && siteGroupRoot.isDirectory());

        File failed = new File(siteGroupRoot.getAbsoluteFile() + File.separator
                + DirectoryEnum.FAILED);
        if (!failed.exists()) {
            createDirectory(failed);
        }
        File outgoing = new File(siteGroupRoot.getAbsoluteFile()
                + File.separator + DirectoryEnum.OUTGOING);
        if (!outgoing.exists()) {
            createDirectory(outgoing);
        }
        File processingreports = new File(siteGroupRoot.getAbsoluteFile()
                + File.separator + DirectoryEnum.PROCESSINGREPORTS);
        if (!processingreports.exists()) {
            createDirectory(processingreports);
        }
        File sent = new File(siteGroupRoot.getAbsoluteFile() + File.separator
                + DirectoryEnum.SENT);
        if (!sent.exists()) {
            createDirectory(sent);
        }
        File temp = new File(siteGroupRoot.getAbsoluteFile() + File.separator
                + DirectoryEnum.TEMP);
        if (!temp.exists()) {
            createDirectory(temp);
        }
    }


    public static void checkDirectoryStructure(File root)
            throws MerchantAccountException {
        /* Check if root directory is valid */
        Validate.notNull(root);
        if (!root.exists()) {
            throw new MerchantAccountException(""); //$NON-NLS-1$
        }
    }


    private static void createDirectory(File file) throws IOException {
        if (!file.mkdir()) {
            throw new IOException();
        }
    }


    public static File moveFile(File fromFile, File toDir) throws IOException {
        return moveFile(fromFile, toDir, null);
    }


    public static File moveFile(File fromFile, File toDir, String suffix)
            throws IOException {
        File fullPath = null;
        Validate.notNull(fromFile);
        Validate.notNull(toDir);
        if (!fromFile.isFile()) {
            Object[] messageArguments = { fromFile.getName() };
            MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
            formatter.applyPattern(Messages.TransportUtil_0.toString());
            throw new IOException(formatter.format(messageArguments));
        }
        if (!toDir.isDirectory()) {
            Object[] messageArguments = { toDir.getName() };
            MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
            formatter.applyPattern(Messages.TransportUtil_1.toString());
            throw new IOException(formatter.format(messageArguments));
        }
        String path = new StringBuffer(toDir.getAbsolutePath())
                .append(File.separator).append(fromFile.getName()).toString();
        String ext = new StringBuffer(suffix == null ? "" : "." + suffix).toString(); //$NON-NLS-1$ //$NON-NLS-2$
        fullPath = new File(path + ext);

        // Updating this to help stop stuck feeds
        if (fullPath.exists()) {
            fullPath.delete();
        }

        if (!fromFile.renameTo(fullPath)) {
            Object[] messageArguments = { fromFile, fullPath };
            MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
            formatter.applyPattern(Messages.TransportUtil_2.toString());
            throw new IOException(formatter.format(messageArguments));
        }
        return fullPath;
    }
}
