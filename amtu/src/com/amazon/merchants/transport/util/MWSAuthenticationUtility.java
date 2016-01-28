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

import com.amazon.merchants.mws.MWSClient;
import com.amazon.merchants.system.ProxyConfig;
import com.amazon.merchants.transport.model.MWSEndpoint;
import com.amazonaws.mws.MarketplaceWebServiceClient;
import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.GetFeedSubmissionCountRequest;

public class MWSAuthenticationUtility {

    /**
     * Attempt to authenticate Marketplace Web Service
     *
     * @param accessKeyId
     * @param secretAccessKey
     * @param merchantId
     * @param endpoint
     * @throws MarketplaceWebServiceException Authentication failed
     */
    public static void authenticate(String accessKeyId, String secretAccessKey, String merchantId, MWSEndpoint endpoint, ProxyConfig proxy)
        throws MarketplaceWebServiceException {

        MarketplaceWebServiceClient mws = MWSClient.createMWSClient(accessKeyId, secretAccessKey,
            System.getProperty("app.name"), System.getProperty("app.version"), endpoint.getEndpointUrl(), 0, proxy);
        
        GetFeedSubmissionCountRequest request = new GetFeedSubmissionCountRequest();
        request.setMerchant(merchantId);

        mws.getFeedSubmissionCount(request);
    }
}