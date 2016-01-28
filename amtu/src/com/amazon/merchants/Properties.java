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

package com.amazon.merchants;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Properties {
  private static final String BUNDLE_NAME = "com.amazon.merchants.app"; //$NON-NLS-1$

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
      .getBundle(BUNDLE_NAME);

  private Properties() {
  }

  public static String getString(String key) {
    try {
      return RESOURCE_BUNDLE.getString(key);
    }
    catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }
}
