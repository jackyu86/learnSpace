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

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.Validate;


/**
 * Based on org.strange.core.utilities.File.IO
 */
public class IO
{
	public static int BUFFER_SIZE = 4 * 1024;

	public static void copyStream(InputStream input, OutputStream output) throws IOException
	{
		copyStream(input, output, true);
	}

	public static void copyStream(InputStream input, OutputStream output, boolean closeStreams)
		throws IOException
	{
		Validate.notNull(input, "InputStream cannot be null");
		Validate.notNull(output, "OutputStream cannot be null");

		try
		{
			byte[] buffer = new byte[BUFFER_SIZE];
			int numBytesRead;

			while ((numBytesRead = input.read(buffer)) != -1)
			{
				output.write(buffer, 0, numBytesRead);
			}
		} finally
		{
			if (closeStreams)
			{
				closeSilently(input);
				closeSilently(output);
			}
		}
	}

	public static byte[] toByteArray(InputStream input) throws IOException
	{
		return toByteArray(input, true);
	}

	public static byte[] toByteArray(InputStream input, boolean closeStream) throws IOException
	{
		return copyToByteArray(input, closeStream).toByteArray();
	}

	public static String toString(InputStream input) throws IOException
	{
		return copyToByteArray(input, true).toString();
	}

	public static <T extends Closeable> void closeSilently(T value)
	{
        if (value != null) {
            try
            {
                value.close();
            } catch (IOException ignored)
            {
            }
        }
	}

	private static ByteArrayOutputStream copyToByteArray(
		InputStream input,
		boolean closeInputStream)
		throws IOException
	{
		Validate.notNull(input, "InputStream cannot be null");

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try
		{
			copyStream(input, buffer, closeInputStream);
		} finally
		{
			closeSilently(buffer);
		}

		return buffer;
	}
}
