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

package com.amazon.merchants.gui.view;


public interface UpdateViewInterface {
	/**
	 * Method called by model to refresh the view
	 */
	public void updateView();

	/**
	 * Method called by model to log message to console gui
	 */
	public void updateLog(Object msg);

	/**
	 * Method called by model to update feeds
	 */
	public void updateFeeds();

	/**
	 * Method called by model to update reports
	 */
	public void updateReports();

	/**
	 * Method called by model to update accounts
	 */
	public void updateAccount();
	
	/**
	 * Method called by model to update proxy
	 */
	public void updateProxy();

	/**
	 * Method called by model to update sites
	 */
	public void updateSiteGroup();
}