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

package com.amazon.merchants.gui.model;

import com.amazon.merchants.exception.MerchantException;

public class TransportGuiModelException extends MerchantException {
	/**
	 *
	 */
	private static final long serialVersionUID = -289225222932498375L;

	/**
	 * @param exceptionText
	 */
	public TransportGuiModelException(String exceptionText)
	{
		super(exceptionText);
	}

	/**
	 * @param rootException
	 */
	public TransportGuiModelException(Throwable rootException)
	{
		super(rootException);
	}

	/**
	 * @param exceptionText
	 * @param rootException
	 */
	public TransportGuiModelException(
		String exceptionText,
		Throwable rootException)
	{
		super(exceptionText, rootException);
	}

	/**
	 *
	 */
	public TransportGuiModelException()
	{
		super();
	}
}
