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

package com.amazon.merchants.transport.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazon.merchants.Messages;

public class MWSEndpoint {
    private String code = "";
    private String desc = "";
    private String endpointURL = "";
    private String mwsRegistrationURL = "";
    private List<String> aliases = null;

    private static Map<String, MWSEndpoint> endpoints = new HashMap<String, MWSEndpoint>();
    static {
        endpoints.put("CA",
            new MWSEndpoint("CA", Messages.MWSEndpoint_1.toString(),
                "https://mws.amazonservices.ca",
                "https://sellercentral.amazon.ca/gp/mws/registration/register.html"
            )); // CA

        endpoints.put("CN",
            new MWSEndpoint("CN", Messages.MWSEndpoint_2.toString(),
                "https://mws.amazonservices.com.cn",
                "https://mai.amazon.cn/gp/mws/registration/register.html"
            )); // CN

        endpoints.put("FR",
            new MWSEndpoint("FR", Messages.MWSEndpoint_3.toString(),
                "https://mws.amazonservices.fr",
                "https://sellercentral.amazon.fr/gp/mws/registration/register.html"
            )); // FR

        endpoints.put("DE",
            new MWSEndpoint("DE", Messages.MWSEndpoint_4.toString(),
                "https://mws.amazonservices.de",
                "https://sellercentral.amazon.de/gp/mws/registration/register.html"
            )); // DE

        endpoints.put("IT",
            new MWSEndpoint("IT", Messages.MWSEndpoint_8.toString(),
                    "https://mws.amazonservices.it",
                    "https://sellercentral.amazon.it/gp/mws/registration/register.html"
            )); // IT

        endpoints.put("JP",
            new MWSEndpoint("JP", Messages.MWSEndpoint_5.toString(),
                "https://mws.amazonservices.jp",
                "https://sellercentral-japan.amazon.com/gp/mws/registration/register.html"
            )); // JP

        endpoints.put("ES",
            new MWSEndpoint("ES", Messages.MWSEndpoint_9.toString(),
                "https://mws.amazonservices.es",
                "https://sellercentral.amazon.es/gp/mws/registration/register.html"
            )); // ES

        endpoints.put("IN",
                new MWSEndpoint("IN", Messages.MWSEndpoint_10.toString(),
                    "https://mws.amazonservices.in",
                    "https://sellercentral.amazon.in/gp/mws/registration/register.html"
                )); // IN

        endpoints.put("GB",
            new MWSEndpoint("GB", Messages.MWSEndpoint_6.toString(),
                "https://mws.amazonservices.co.uk",
                "https://sellercentral.amazon.co.uk/gp/mws/registration/register.html",
                //Aliases:
                "UK"
            )); // UK

        endpoints.put("US",
            new MWSEndpoint("US", Messages.MWSEndpoint_7.toString(),
                "https://mws.amazonservices.com",
                "https://sellercentral.amazon.com/gp/mws/registration/register.html"
            )); // US
    }


    public static Map<String, MWSEndpoint> getEndpointMap() {
        return endpoints;
    }


    public static List<MWSEndpoint> getEndpointList() {
        ArrayList<MWSEndpoint> endpointList = new ArrayList<MWSEndpoint>(endpoints.values());
        Collections.sort(endpointList, new Comparator<MWSEndpoint>() {
            @Override
            public int compare(MWSEndpoint endpoint1, MWSEndpoint endpoint2) {
                return endpoint1.toString().compareTo(endpoint2.toString());
            }
        });
        return endpointList;
    }


    public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }


    /**
     * Creates a new MWSEndpoint
     *
     * @param code Short code to reference endpoint in DB
     * @param desc Descriptive name of endpoint
     * @param endpoint URL for endpoint
     * @param website Registration URL for endpoint
     */
    public MWSEndpoint(String code, String desc, String endpoint, String registrationURL, String... aliases) {
        this.code = code;
        this.desc = desc;
        endpointURL = endpoint;
        mwsRegistrationURL = registrationURL;

        this.aliases = new ArrayList<String>();
        if (aliases != null) {
            this.aliases.addAll(java.util.Arrays.asList(aliases));
        }
    }


    @Override
    public String toString() {
        return desc;
    }


    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }


    public String getDescription() {
        return desc;
    }


    public void setDescription(String desc) {
        this.desc = desc;
    }


    public String getEndpointUrl() {
        return endpointURL;
    }


    public void setEndpointUrl(String endpoint) {
        endpointURL = endpoint;
    }


    public String getMwsRegistrationURL() {
        return mwsRegistrationURL;
    }


    public void setMwsRegistrationURL(String mwsRegistrationURL) {
        this.mwsRegistrationURL = mwsRegistrationURL;
    }


    public List<String> getAliases() {
        return aliases;
    }


    public static MWSEndpoint getEndpoint(String code) {
        MWSEndpoint endpoint = endpoints.get(code);

        if (endpoint != null) {
            return endpoint;
        }

        for (MWSEndpoint chk : getEndpointList()) {
            if (chk.getAliases().contains(code)) {
                return chk;
            }
        }

        return null;
    }
}
