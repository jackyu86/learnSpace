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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import com.amazon.merchants.Messages;
import com.amazon.merchants.gui.model.TransportGuiModel;

public class TransportGuiFrame extends JFrame {
    private static final long serialVersionUID = 3553280622505530412L;
    private TransportGuiModel model;

    private ConsoleView console;
    private JTabbedPane pane;
    private FeedView feed;
    private ReportView report;
    private SideBarView sidebar;
    private AccountManagementView accounts;
    private SettingsManagementView settings;


    public TransportGuiFrame(TransportGuiModel model) {
        super(System.getProperty("app.name.display") + " v" + //$NON-NLS-1$ //$NON-NLS-2$
                System.getProperty("app.version")); //$NON-NLS-1$
        this.model = model;
        init();
    }


    private void init() {
        feed = new FeedView(model);
        report = new ReportView(model);
        console = new ConsoleView(model);
        sidebar = new SideBarView(model);
        accounts = new AccountManagementView(model);
        settings = new SettingsManagementView(model);
        pane = new JTabbedPane();

        setLayout(new BorderLayout());

        pane.addTab(Messages.TransportGuiFrame_0.toString(), console);
        pane.addTab(Messages.TransportGuiFrame_1.toString(), feed);
        pane.addTab(Messages.TransportGuiFrame_2.toString(), report);
        pane.addTab(Messages.TransportGuiFrame_3.toString(), accounts);
        pane.addTab(Messages.TransportGuiFrame_4.toString(), settings);

        this.add(sidebar, BorderLayout.WEST);
        this.add(pane, BorderLayout.CENTER);

        setState(Frame.NORMAL);
        this.setSize(new Dimension(930, 690));
        setLocationRelativeTo(null);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                model.shutdown();
            }
        });

        /*
         * When the X button is clicked, trigger the click event but do not
         * dispose the frame
         */
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }
}