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

package com.amazon.merchants.transport.dispatcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.Validate;

import com.amazon.merchants.Messages;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.transport.model.FeedTypeEnum;
import com.amazon.merchants.transport.model.UnrecognizedFeedException;
import com.amazon.merchants.util.file.FileUtil;
import com.amazon.merchants.util.file.IO;

public class FeedContentIdentifier {
    private static final int DEPTH = 2048;


    /**
     * Gets the file type based on the extension of the file and its contents
     *
     * @param file File to analyze
     * @return Feed file type
     * @throws UnrecognizedFeedException If file could not be matched to a known feed type
     */
    public static String getFileType(File file) throws UnrecognizedFeedException {
        Validate.notNull(file);
        Validate.isTrue(file.canRead(), Messages.FeedContentIdentifier_0.toString() + " " + file.getAbsolutePath());
        String ext = FileUtil.getExtension(file.getName());
        String type = guess(getHeader(file, ext), ext);
        return type;
    }


    /**
     * Extracts the first DEPTH characters of the file for analysis. Method has been upgraded to properly handle UTF-16
     * encoded files
     *
     * @param file File to pull header
     * @param ext Extension of file
     * @return First DEPTH characters of file
     */
    public static String getHeader(File file, String ext) {
        String header = null;
        BufferedReader br = null;
        FileInputStream fin = null;
        GZIPInputStream gzis = null;
        InputStreamReader xover = null;
        FileReader freader = null;
        boolean isUTF16 = false;

        InputStream in = null;

        try {
            // support gzip feeds
            if (ext.toLowerCase().equals("gz")) {
                fin = new FileInputStream(file);
                gzis = new GZIPInputStream(fin);
                isUTF16 = isUniCode16(gzis);
                gzis.close();
                fin.close();

                // New InputStream has to be opened because these streams do not support mark and reset
                fin = new FileInputStream(file);
                in = new GZIPInputStream(fin);
            }
            else {
                fin = new FileInputStream(file);
                isUTF16 = isUniCode16(fin);
                fin.close();

                // New InputStream has to be opened because these streams do not support mark and reset
                in = new FileInputStream(file);
            }

            if (isUTF16) {
                xover = new InputStreamReader(in, "UTF-16");
            }
            else {
                // Revert to system-default encoding if not recognized as UTF-16
                xover = new InputStreamReader(in);
            }
            br = new BufferedReader(xover);

            char[] buffer = new char[DEPTH];
            br.read(buffer);
            header = new String(buffer);
        }
        catch (IOException e) {
            TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);
        }
        finally {
            IO.closeSilently(freader);
            IO.closeSilently(fin);
            IO.closeSilently(gzis);
            IO.closeSilently(xover);
            IO.closeSilently(br);
            IO.closeSilently(in);
        }

        return header;
    }


    /**
     * Attempts to determine the feed type based on the header string and file extension provided
     *
     * @param header File header
     * @param ext File extension
     * @return Feed type
     * @throws UnrecognizedFeedException If header and extension could not be matched to a known feed type
     */
    private static String guess(String header, String ext) throws UnrecognizedFeedException {
        for (FeedTypeEnum type : FeedTypeEnum.values()) {
            if (type.getRule().execute(header, ext)) {
                return type.getEnumeration();
            }
        }

        // Couldn't match - error!
        TransportLogger.getSysErrorLogger().error(Messages.FeedContentIdentifier_1.toString());
        throw new UnrecognizedFeedException(Messages.FeedContentIdentifier_1.toString());
    }


    /**
     * Determines whether the input stream is UTF-16 encoded
     *
     * @param is Input stream to check
     * @return Whether input stream is UTF-16
     * @throws IOException If error reading file
     */
    private static boolean isUniCode16(InputStream is) throws IOException {
        // create a wrapper in stream which supports mark and reset
        int bytesRead = 0;
        byte[] testBytes = new byte[10];
        while (bytesRead < 10) {
            int tempBytesRead = is.read(testBytes, bytesRead, testBytes.length - bytesRead);
            bytesRead += tempBytesRead;
        }

        // get the consecutive 3 zero bytes
        int pos1 = indexOfZeroByte(testBytes);
        int pos2 = indexOfZeroByte(testBytes, pos1 + 1);
        int pos3 = indexOfZeroByte(testBytes, pos2 + 1);

        // check whether the zero bytes follows UTF-16 pattern
        if (pos1 + 2 == pos2 && pos2 + 2 == pos3) {
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Returns the index of the first zero byte from the given byte array
     *
     * @param bytes Byte array to read
     * @return Index of first zero byte found
     */
    private static int indexOfZeroByte(byte[] bytes) {
        return indexOfZeroByte(bytes, 0);
    }


    /**
     * Returns the index of the first zero byte from the given byte array, starting (inclusive) from offset
     *
     * @param bytes Byte array to read
     * @param offSet Offset at which to start the search
     * @return Index of first zero byte found
     */
    private static int indexOfZeroByte(byte[] bytes, int offset) {
        for (int i = offset; i < bytes.length; ++i) {
            if (bytes[i] == 0) {
                return i;
            }
        }
        return -1;
    }
}