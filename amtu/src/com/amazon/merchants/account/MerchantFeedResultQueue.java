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

package com.amazon.merchants.account;

public class MerchantFeedResultQueue extends MerchantSubmissionQueue {
    private static final int MAX_QUEUE_SIZE = 15;
    private static final int QUOTA_RESTORE_RATE = 1 * 60 * 1000;
    private static final int QUOTA_VACATE_TIME = 15 * 60 * 1000;


    @Override
    public int queueSpotsAvailable() {
        return queueSpotsAvailable(MAX_QUEUE_SIZE);
    }


    @Override
    public void pruneQueue() {
        pruneQueue(QUOTA_RESTORE_RATE, QUOTA_VACATE_TIME);
    }


    @Override
    public void vacateOldQueueEntries() {
        vacateOldQueueEntries(QUOTA_VACATE_TIME);
    }
}