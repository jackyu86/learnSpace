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

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.amazon.merchants.transport.model.MerchantQueueSubmission;

abstract class MerchantSubmissionQueue {
    protected Queue<MerchantQueueSubmission> queue = null;

    protected Date lastCheckTime = null;


    public synchronized Date lastRequest() {
        if (isEmpty()) {
            return null;
        }
        Object[] list = queue.toArray();
        return ((MerchantQueueSubmission) list[list.length - 1])
            .getSubmissionTime();
    }


    public synchronized boolean isEmpty() {
        return queue == null || queue.isEmpty();
    }


    public synchronized int size() {
        if (isEmpty()) {
            return 0;
        }

        return queue.size();
    }


    public synchronized Date peek() {
        if (isEmpty()) {
            return null;
        }
        return queue.peek().getSubmissionTime();
    }


    public synchronized Date poll() {
        if (isEmpty()) {
            return null;
        }

        MerchantQueueSubmission submission = queue.poll();

        return submission.getSubmissionTime();
    }


    public synchronized void offer(String submissionId, Date submissionTime) {
        if (queue == null) {
            queue = new ConcurrentLinkedQueue<MerchantQueueSubmission>();
        }

        if (isEmpty() || lastCheckTime == null) {
            lastCheckTime = new Date();
        }

        MerchantQueueSubmission submission = new MerchantQueueSubmission();
        submission.setSubmissionId(submissionId);
        submission.setSubmissionTime((Date) submissionTime.clone());

        queue.offer(submission);
    }


    public synchronized boolean queueFull() {
        return queueSpotsAvailable() == 0;
    }


    abstract public int queueSpotsAvailable();


    protected synchronized int queueSpotsAvailable(int maxQueueSize) {
        if (isEmpty()) {
            return maxQueueSize;
        }

        return maxQueueSize - queue.size();
    }


    abstract public void pruneQueue();


    protected synchronized void pruneQueue(long restoreRate,
        long quotaVacateTime) {
        long queueExpiration = new Date().getTime() - restoreRate;

        if (lastCheckTime == null) {
            //can't prune until something's been added to the queue
            return;
        }

        if (!isEmpty() && lastCheckTime.getTime() < queueExpiration) {
            poll();
            lastCheckTime = new Date();
        }

        vacateOldQueueEntries(quotaVacateTime);
    }


    abstract public void vacateOldQueueEntries();


    protected synchronized void vacateOldQueueEntries(long quotaVacateTime) {
        long queueVacate = new Date().getTime() - quotaVacateTime;
        do {
            Date queuePeek = peek();
            if (queuePeek == null || queuePeek.getTime() > queueVacate) {
                return;
            }
            poll();
        }
        while (!isEmpty());
    }
}