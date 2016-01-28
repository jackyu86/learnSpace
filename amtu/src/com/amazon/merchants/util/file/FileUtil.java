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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.Validate;

import com.amazon.merchants.Messages;

public class FileUtil {
    public static String getStringFromFile(String fileName) throws IOException {
        Validate.notEmpty(fileName);
        return getStringFromFile(new File(fileName));
    }


    public static String getStringFromFile(File inputFile) throws IOException {
        FileInputStream fis = null;
        String result = null;

        Validate.notNull(inputFile);

        Validate.isTrue(inputFile.exists());

        try {
            fis = new FileInputStream(inputFile);
            result = IO.toString(fis);
        }
        finally {
            IO.closeSilently(fis);
        }
        return result;
    }


    public static String[] getStringArrayFromFile(File inputFile)
            throws IOException {
        BufferedReader br = null;
        ArrayList<String> list = new ArrayList<String>();
        try {
            br = new BufferedReader(new FileReader(inputFile));
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                list.add(line);
            }
        }
        finally {
            IO.closeSilently(br);
        }

        return list.toArray(new String[0]);
    }


    public static boolean areFilesDifferent(File file1, File file2)
            throws IOException {
        FileInputStream fis1 = null, fis2 = null;
        byte[] file1Data, file2Data;

        Validate.isTrue(file1.exists());

        Validate.isTrue(file2.exists());

        try {
            fis1 = new FileInputStream(file1);
            fis2 = new FileInputStream(file2);

            file1Data = IO.toByteArray(fis1);
            file2Data = IO.toByteArray(fis2);
        }
        finally {
            IO.closeSilently(fis1);
            IO.closeSilently(fis2);
        }

        return !Arrays.equals(file1Data, file2Data);
    }


    public static File createTempDirectory() throws IOException {
        File tempDirectory = File.createTempFile(
                "FileUtilTempDirectory", ".tmp"); //$NON-NLS-1$ //$NON-NLS-2$
        tempDirectory.delete();
        if (!tempDirectory.mkdirs()) {
            throw new IOException(Messages.FileUtil_0.toString()
                    + " [" + tempDirectory.getAbsolutePath() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return tempDirectory;
    }


    public static void deleteTempDirectory(File tempDirectory) {
        if (tempDirectory != null && tempDirectory.isDirectory()) {
            File[] files = tempDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        FileUtil.deleteTempDirectory(file);
                    }
                    file.delete();
                }
            }
            tempDirectory.delete();
        }
    }


    public static String getExtension(String fileOrUrl) {
        Validate.notEmpty(fileOrUrl);
        String extension = ""; //$NON-NLS-1$
        try {
            extension = getFileExtension(new URL(fileOrUrl).getPath());
        }
        catch (MalformedURLException e) {
            // Not a URL, so try parsing it as a file
            extension = getFileExtension(fileOrUrl);
        }
        return extension;
    }


    public static void writeStringToFile(String stringToWrite, String filename)
            throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(filename);
            os.write(stringToWrite.getBytes());
        }
        finally {
            IO.closeSilently(os);
        }

    }


    private static String getFileExtension(String file) {
        String extension = ""; //$NON-NLS-1$
        String fileName = new File(file).getName();
        int idxOfDot = fileName.lastIndexOf('.');
        if (idxOfDot != -1) {
            extension = fileName.substring(idxOfDot + 1);
        }

        return extension;
    }


    public static String getSeparator() {
        return System.getProperty("file.separator"); //$NON-NLS-1$
    }
}
