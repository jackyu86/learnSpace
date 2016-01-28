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
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 *
 * @author jpaquin
 */
public class FileLockManager
{

    private FileChannel lockFileChannel = null;
    private RandomAccessFile lockFile = null;
    private FileLock lockFileLock = null;

    public boolean isLocked()
    {
        return lockFileLock != null;
    }

    public boolean isUnlocked()
    {
        return !isLocked();
    }

    private void closeFileChannel()
    {
        IO.closeSilently(lockFileChannel);
        IO.closeSilently(lockFile);

        lockFileChannel = null;
        lockFile = null;
    }

    public boolean lock(String tmpName) throws FileLockException
    {
        if (isUnlocked())
        {
            try
            {
                File tmpFile =DirectoryUtil.makeTempFile(tmpName);
                lockFile = new RandomAccessFile(tmpFile, "rw");
                lockFileChannel = lockFile.getChannel();
                lockFileLock = lockFileChannel.tryLock();
            } catch (Exception e)
            {
                FileLockException fle = new FileLockException();
                fle.initCause(e);

                closeFileChannel();

                throw fle;
            }

            if (lockFileLock == null) {
                closeFileChannel();
            }
        }

        return isLocked();
    }

    public void unlock()
    {
        if (isLocked())
        {
            try
            {
                lockFileLock.release();
            }
            catch (Exception e)
            {
            }
            finally
            {
                closeFileChannel();
                lockFileLock = null;
            }
        }
    }
}
