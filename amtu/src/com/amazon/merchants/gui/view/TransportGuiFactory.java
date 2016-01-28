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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;

public class TransportGuiFactory {
	public static final Font HELPTEXT = new Font(Font.SANS_SERIF, Font.ITALIC, 10);
	public static final Font PLAINTEXT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

	public static final Dimension BUTTONSMALL = new Dimension(80, 20);
	public static final Dimension BUTTONNORMAL = new Dimension(100, 20);
	public static final Dimension BUTTONLARGE = new Dimension(125, 20);
	public static final Dimension BUTTONXLARGE = new Dimension(175, 20);

	public static final Dimension TEXTBOXLARGE = new Dimension(300, 20);
	public static final Dimension TEXTBOXNORMAL = new Dimension(250, 20);

	public static final Dimension TEXTBOXMULTI = new Dimension(200, 35);

	public static final Dimension LABELXSMALL = new Dimension(80, 20);
	public static final Dimension LABELSMALL = new Dimension(125, 20);
	public static final Dimension LABELNORMAL = new Dimension(150, 20);
	public static final Dimension LABELLARGE = new Dimension(200, 20);
	public static final Dimension LABELXLARGE = new Dimension(250, 20);
	public static final Dimension LABELXXLARGE = new Dimension(300, 20);

	public static final Dimension COMBOBOXNORMAL = new Dimension(200, 25);

	public static final Dimension AREASMALL = new Dimension(200, 5);

	public static final int SMALLSPACERHEIGHT = 10;
	public static final int LARGESPACERHEIGHT = 40;

	public static final Color AMTUGRAY = new Color(0.9f, 0.9f, 0.9f);

	public JLabel createJLabel(Dimension dim, String str) {
		JLabel temp = new JLabel(str);
		temp.setPreferredSize(dim);
		return temp;
	}
}
