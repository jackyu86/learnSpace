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

import com.amazon.merchants.Messages;

public enum ReportTypeEnum {
	SCHEDULED_XML_ORDER("ORDER", //$NON-NLS-1$
	        "_GET_ORDERS_DATA_", //$NON-NLS-1$
	        ReportExtensionEnum.XML),
	SCHEDULED_FLAT_FILE_ORDER("ORDER", //$NON-NLS-1$
	        "_GET_FLAT_FILE_ORDERS_DATA_", //$NON-NLS-1$
	        ReportExtensionEnum.TXT),
	SCHEDULED_XML_SETTLEMENT("SETTLEMENT", //$NON-NLS-1$
	        "_GET_PAYMENT_SETTLEMENT_DATA_", //$NON-NLS-1$
	        ReportExtensionEnum.XML),
	SCHEDULED_FLAT_FILE_SETTLEMENT("SETTLEMENT", //$NON-NLS-1$
	        "_GET_FLAT_FILE_PAYMENT_SETTLEMENT_DATA_", //$NON-NLS-1$
	        ReportExtensionEnum.TXT),
	SCHEDULED_ALT_FLAT_FILE_SETTLEMENT("SETTLEMENT", //$NON-NLS-1$
	        "_GET_ALT_FLAT_FILE_PAYMENT_SETTLEMENT_DATA_", //$NON-NLS-1$
	        ReportExtensionEnum.TXT);

	public enum ReportExtensionEnum {
		TXT("txt", Messages.ReportTypeEnum_0.toString()), //$NON-NLS-1$
		XML("xml", Messages.ReportTypeEnum_1.toString()); //$NON-NLS-1$


		private String extension;
		private String desc;

		ReportExtensionEnum (String extension, String desc) {
			this.extension = extension;
			this.desc = desc;
		}

		public String getExtension() {
			return extension;
		}

		public String getDescription() {
			return desc;
		}

		@Override
        public String toString() {
			return desc + " (." + extension + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		public static ReportExtensionEnum getReportExtensionEnum(String ext) {
			for (ReportExtensionEnum enumeration : ReportExtensionEnum.values()) {
				if (enumeration.getExtension().equals(ext)) {
                    return enumeration;
                }
			}
			return null;
		}
	}

	private String enumeration;
	private ReportExtensionEnum format;
	private String reportName;

	ReportTypeEnum (String reportName, String enumeration, ReportExtensionEnum extension) {
	    this.reportName = reportName;
		this.enumeration = enumeration;
		format = extension;
	}

	public String getReportName() {
	    return reportName;
	}

	public String getEnumeration() {
		return enumeration;
	}

	public String getFormat() {
		return format.getExtension();
	}

	public static String getExtensionFromType (String enumeration) {
		for (ReportTypeEnum t : ReportTypeEnum.values()) {
			if (enumeration.equals(t.getEnumeration())) {
				return t.getFormat();
			}
		}
		return null;
	}

	public static String getReportNameFromType (String enumeration) {
	    for (ReportTypeEnum t : ReportTypeEnum.values()) {
            if (enumeration.equals(t.getEnumeration())) {
                return t.getReportName();
            }
        }
        return null;
	}

	public static ReportExtensionEnum[] getReportExtensions() {
		return ReportExtensionEnum.values();
	}
}