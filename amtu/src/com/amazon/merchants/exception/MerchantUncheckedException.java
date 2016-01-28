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

package com.amazon.merchants.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

public class MerchantUncheckedException extends RuntimeException {
    /**
     * Generated Serial ID
     */
    private static final long serialVersionUID = -4517614151772054604L;
    private Throwable chain;


    public MerchantUncheckedException() {
        super();
    }


    /**
     * @param arg0
     */
    public MerchantUncheckedException(String arg0) {
        super(arg0);
    }


    /**
     * @param arg0
     */
    public MerchantUncheckedException(Throwable arg0) {
        super(arg0);
        chain = arg0;
    }


    /**
     * @param arg0
     * @param arg1
     */
    public MerchantUncheckedException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        chain = arg1;
    }


    /**
     * @see java.lang.Throwable#printStackTrace()
     */
    @Override
    public void printStackTrace() {
        super.printStackTrace();
        if (chain != null) {
            chain.printStackTrace();
        }
    }


    /**
     * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
     */
    @Override
    public void printStackTrace(PrintStream arg0) {
        super.printStackTrace(arg0);
        if (chain != null) {
            chain.printStackTrace(arg0);
        }
    }


    /**
     * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
     */
    @Override
    public void printStackTrace(PrintWriter arg0) {
        super.printStackTrace(arg0);
        if (chain != null) {
            chain.printStackTrace(arg0);
        }
    }


    @Override
    public String getMessage() {
        return super.getMessage();
    }


    /**
     * Chain traversing function - gets to the bottom of an exception chain
     */
    public String getRootCauseMessage() {

        if (chain == null || !(chain instanceof MerchantException)) {
            return getMessage();
        }
        else if (!(chain instanceof MerchantException)) {
            return chain.getMessage();
        }
        else {
            return ((MerchantException) chain).getRootCauseMessage();
        }
    }


    /**
     * Chain traversing function - gets to the bottom of an exception chain
     */
    public Throwable getRootCauseException() {
        if (chain == null) {
            return this;
        }
        else if (!(chain instanceof MerchantException)) {
            return chain;
        }
        else {
            return ((MerchantException) chain).getRootCauseException();
        }
    }
}
