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

package com.amazon.merchants.logger;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import com.amazon.merchants.gui.model.TransportGuiModel;

public class TransportGuiAppender extends AppenderSkeleton {
	private TransportGuiModel model;
	private Layout layout;

	public TransportGuiAppender (Layout layout, TransportGuiModel model) {
		this.layout = layout;
		this.model = model;
	}

	@Override
	protected void append(LoggingEvent event) {
		model.log(layout.format(event));
	}

	@Override
	public void close() {}

	@Override
	public boolean requiresLayout() {
		return true;
	}

}
