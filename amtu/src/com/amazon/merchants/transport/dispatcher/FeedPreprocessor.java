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

import java.util.concurrent.PriorityBlockingQueue;

import com.amazon.merchants.account.AMTUAccount;
import com.amazon.merchants.gui.model.TransportGuiModel;
import com.amazon.merchants.transport.model.MerchantFeed;

public class FeedPreprocessor {
    private static FeedPreprocessor instance;


    public static FeedPreprocessor getInstance() {
        if (instance == null) {
            instance = new FeedPreprocessor();
        }
        return instance;
    }


    /**
     * Returns a queue of the feeds ready for submit
     *
     * @param account AMTU account
     * @return Queue of feeds ready for submit
     */
    public PriorityBlockingQueue<MerchantFeed> process(AMTUAccount account) {
        PriorityBlockingQueue<MerchantFeed> queue = account.feedsForDispatch();

        TransportGuiModel.getInstance().updateAllViews();

        return queue;
    }
}