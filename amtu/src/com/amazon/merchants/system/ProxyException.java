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

package com.amazon.merchants.system;


public class ProxyException extends Exception {

    /**
     * Generated Serial ID
     */
    private static final long serialVersionUID = 2190789437320638794L;

    /**
     *
     */
    public ProxyException() {
        super();
    }


    /**
     * @param arg0
     */
    public ProxyException(String arg0) {
        super(arg0);
    }
    
    /**
     * @param arg0
     */
    public ProxyException(Throwable arg0) {
        super(arg0);
    }
	
    /**
     * @param arg0
     * @param arg1
     */
    public ProxyException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
