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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.amazon.merchants.Messages;
import com.amazon.merchants.util.file.IO;

public class ContentMD5HeaderCalculator {
    /**
     * Consume the stream and return its Base-64 encoded MD5 checksum.
     */
    public static String computeContentMD5Header(InputStream inputStream) {

        // Consume the stream to compute the MD5 as a side effect.
        DigestInputStream s;
        try {
            s = new DigestInputStream(inputStream,
                MessageDigest.getInstance("MD5")); //$NON-NLS-1$

            // drain the buffer, as the digest is computed as a side-effect
            byte[] buffer = new byte[8192];
            while (s.read(buffer) > 0) {
                // loop through buffer
            }

            String md5 = new String(
                org.apache.commons.codec.binary.Base64.encodeBase64(s
                    .getMessageDigest().digest()), "UTF-8"); //$NON-NLS-1$
            return md5;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void cksum(File file, String serverCheckSum)
        throws MD5CheckSumException, IOException {

        FileInputStream in = null;

        try {
            in = new FileInputStream(file);

            String clientCheckSum = ContentMD5HeaderCalculator
                .computeContentMD5Header(in);

            if (!clientCheckSum.equals(serverCheckSum)) {
                throw new MD5CheckSumException(String.format(
                    Messages.ContentMD5HeaderCalculator_0.toString(),
                    file.getName()));
            }

        }
        finally {
            IO.closeSilently(in);
        }
    }
}
