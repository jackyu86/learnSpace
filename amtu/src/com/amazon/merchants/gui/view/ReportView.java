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
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.amazon.merchants.Messages;
import com.amazon.merchants.gui.model.DateRange;
import com.amazon.merchants.gui.model.TransportGuiModel;
import com.amazon.merchants.logger.TransportLogger;

public class ReportView extends JPanel implements UpdateViewInterface {
    private static final long serialVersionUID = 8413552957170881636L;

    private TransportGuiModel model;

    private JPanel pnlReports = new JPanel();
    private JTable tblReports = new JTable();
    private JPanel pnlDate = new JPanel();
    private JLabel lblDate = new JLabel(Messages.ReportView_0.toString());
    private JScrollPane scrFeeds = new JScrollPane(tblReports);
    private DefaultComboBoxModel cboModel = new DefaultComboBoxModel();
    private JComboBox cboDate = new JComboBox(cboModel);


    public ReportView(TransportGuiModel model) {
        this.model = model;

        init();
        registerComponents();

        this.model.addView(this);
    }


    private void init() {
        setLayout(new BorderLayout());

        for (DateRange dr : DateRange.values()) {
            cboModel.addElement(dr);
        }
        cboDate.setPreferredSize(new Dimension(200, 20));

        tblReports.setModel(model.getReportModel());

        pnlDate.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnlDate.add(lblDate);
        pnlDate.add(cboDate);

        pnlReports.setLayout(new BorderLayout());
        pnlReports.setBorder(BorderFactory
            .createTitledBorder(Messages.ReportView_1.toString()));
        pnlReports.add(scrFeeds, BorderLayout.CENTER);
        pnlReports.add(pnlDate, BorderLayout.NORTH);

        this.add(pnlReports, BorderLayout.CENTER);

        model.updateReports();

    }


    private void registerComponents() {
        cboDate.addActionListener(new DateSelectorListener());
    }


    public void updateFeeds() {

    }


    public void updateLog(Object msg) {

    }


    public void updateReports() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tblReports.updateUI();
            }
        });
    }


    public void updateView() {

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


    private class DateSelectorListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    DateRange dr = (DateRange) cboDate.getSelectedItem();
                    try {
                        model.updateReportsDateRange(dr);
                    }
                    catch (SQLException e) {
                        TransportLogger.getSysErrorLogger().fatal(
                            Messages.ReportView_3.toString(), e);
                        JOptionPane.showMessageDialog(ReportView.this,
                            e.getLocalizedMessage(),
                            Messages.ReportView_2.toString(),
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
            t.start();
        }
    }
}