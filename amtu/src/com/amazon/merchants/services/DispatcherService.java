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

package com.amazon.merchants.services;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import com.amazon.merchants.account.AMTUAccount;
import com.amazon.merchants.account.MerchantAccountManager;
import com.amazon.merchants.executor.ExecutorFactory;
import com.amazon.merchants.transport.dispatcher.FeedDispatcherTask;
import com.amazon.merchants.transport.dispatcher.FeedPreprocessor;
import com.amazon.merchants.transport.model.MerchantFeed;

public class DispatcherService extends TransportService {
    private FeedPreprocessor p = FeedPreprocessor.getInstance();
    private ThreadPoolExecutor exec = ExecutorFactory.getDispatcherExecutor();


    public DispatcherService() {
        super(TransportServiceEnum.DISPATCH);
    }


    @Override
    public void run() {
        List<AMTUAccount> list = MerchantAccountManager.getAccountList();
        for (AMTUAccount account : list) {
            // Get the list of feeds to be submitted, updating the count of unsubmitted feeds
            PriorityBlockingQueue<MerchantFeed> feedList = p.process(account);

            // Skip if there are no feeds to dispatch
            if (feedList == null || feedList.isEmpty()) {
                continue;
            }

            // Skip if this account's submission queue is full or they are already submitting feeds
            if (account.isUnderDispatch() || !account.hasFeedDispatchSlotsAvailable()) {
                continue;
            }

            // Submitting the account containing a list of feeds to submit helps guarantee the feeds are executed in
            // the proper order, not by which ever feed's thread completes first
            exec.submit(new FeedDispatcherTask(account));
        }
    }
}