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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.amazon.merchants.Messages;
import com.amazon.merchants.gui.model.TransportGuiModel;

public class ConsoleView extends JPanel implements UpdateViewInterface {
    private static final long serialVersionUID = -2600380350949896195L;

    private TransportGuiModel model;

    private JPanel pnlConsole = new JPanel();
    private JPanel pnlButton = new JPanel();
    private JTextArea txtConsole = new JTextArea();
    private JScrollPane scrConsole = new JScrollPane(txtConsole);
    private JButton btnClear = new JButton(Messages.ConsoleView_0.toString());

    private static final int consoleBuffer = 20 * 1024;


    public ConsoleView(TransportGuiModel model) {
        super();
        this.model = model;

        init();
        registerComponents();

        this.model.addView(this);
    }


    private void init() {
        setLayout(new BorderLayout());

        txtConsole.setEditable(false);
        txtConsole.setLineWrap(true);
        txtConsole.setWrapStyleWord(true);
        scrConsole.getVerticalScrollBar().setValue(100);

        pnlConsole.setLayout(new BorderLayout());
        pnlConsole.add(scrConsole, BorderLayout.CENTER);

        Dimension btnLargeDimension = new Dimension(125, 20);
        btnClear.setPreferredSize(btnLargeDimension);
        pnlButton.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnlButton.add(btnClear);

        this.add(pnlConsole, BorderLayout.CENTER);
        this.add(pnlButton, BorderLayout.SOUTH);

    }


    private void registerComponents() {
        btnClear.addActionListener(new ClearButtonListener());
    }


    @Override
    public void updateView() {
    }


    @Override
    public void updateFeeds() {
    }

    
    @Override
    public void updateAccount() {
    }

    
    @Override
    public void updateProxy() {
    }


    @Override
    public void updateSiteGroup() {
    }


    @Override
    public void updateLog(Object msg) {
        if (txtConsole.getText().length() > consoleBuffer) {
            txtConsole.replaceRange(null, 0,
                txtConsole.getText().length() - consoleBuffer);
        }
        txtConsole.append(msg.toString());
        txtConsole.setCaretPosition(txtConsole.getText().length());
    }


    @Override
    public void updateReports() {
    }


    private class ClearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            txtConsole.setText(""); //$NON-NLS-1$
        }
    }
}