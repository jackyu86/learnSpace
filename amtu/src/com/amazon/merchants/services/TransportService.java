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

public abstract class TransportService implements Runnable {

    public TransportServiceEnum service;


    public TransportService(TransportServiceEnum service) {
        this.service = service;
    }


    /**
     * Method called by the scheduler when quantum expires
     */
    public abstract void run();


    @Override
    public boolean equals(Object value) {
        boolean result = false;

        if (value instanceof TransportService) {

            TransportService service = (TransportService) value;
            result = this.service == service.service;
        }

        return result;
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }


    @Override
    public String toString() {
        return service.toString();
    }
}
