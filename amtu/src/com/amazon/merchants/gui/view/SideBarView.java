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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.amazon.merchants.Messages;
import com.amazon.merchants.account.AMTUAccount;
import com.amazon.merchants.account.MerchantSiteGroup;
import com.amazon.merchants.gui.model.TransportGuiModel;
import com.amazon.merchants.transport.model.MWSEndpoint;
import com.amazon.merchants.util.file.DirectoryUtil;

public class SideBarView extends JPanel implements UpdateViewInterface {
    private static final long serialVersionUID = -8035840023772008570L;

    private TransportGuiModel model;

    private Icon logo = null;
    private JLabel lblLogo = null;

    private DefaultComboBoxModel cboAccountModel;
    private JComboBox cboAccounts;
    private DefaultComboBoxModel cboSiteGroupModel;
    private JComboBox cboSiteGroups;
    private JLabel lblAccounts;
    private JLabel lblSites;
    private JLabel lblDetails;
    private JLabel lblEndpoint;
    private JLabel lblEndpointValue;
    private JLabel lblDocumentTransport;
    private JTextArea lblDocumentTransportValue;
    private JLabel lblStatus;
    private JLabel lblStatusValue;
    private JLabel lblLastConnection;
    private JLabel lblLastConnectionValue;

    private boolean ignoreEvents = false;


    public SideBarView(TransportGuiModel model) {
        this.model = model;

        try {
            logo = new ImageIcon(getClass().getResource(
                    DirectoryUtil.getCompanyNameImagePath()));
            lblLogo = new JLabel(logo);
        }
        catch (Exception e) {
            // ensure that lblLogo is never null
            lblLogo = new JLabel();
        }

        init();
        registerComponents();

        this.model.addView(this);
    }


    private void init() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        cboAccountModel = new DefaultComboBoxModel();
        cboAccounts = new JComboBox(cboAccountModel);
        cboAccounts.setPreferredSize(TransportGuiFactory.COMBOBOXNORMAL);

        cboSiteGroupModel = new DefaultComboBoxModel();
        cboSiteGroups = new JComboBox(cboSiteGroupModel);
        cboSiteGroups.setPreferredSize(TransportGuiFactory.COMBOBOXNORMAL);

        lblAccounts = new JLabel(Messages.SideBarView_0.toString());
        lblSites = new JLabel(Messages.SideBarView_11.toString());
        lblDetails = new JLabel(Messages.SideBarView_1.toString());
        lblEndpoint = new JLabel(Messages.SideBarView_2.toString());
        lblEndpointValue = new JLabel();
        lblDocumentTransport = new JLabel(Messages.SideBarView_3.toString());
        lblDocumentTransportValue = new JTextArea();
        lblStatus = new JLabel(Messages.SideBarView_4.toString());
        lblStatusValue = new JLabel();
        lblLastConnection = new JLabel(Messages.SideBarView_5.toString());
        lblLastConnectionValue = new JLabel();

        lblAccounts.setPreferredSize(TransportGuiFactory.LABELLARGE);
        lblAccounts.setOpaque(true);
        lblAccounts.setBackground(TransportGuiFactory.AMTUGRAY);

        lblSites.setPreferredSize(TransportGuiFactory.LABELLARGE);
        lblSites.setOpaque(true);
        lblSites.setBackground(TransportGuiFactory.AMTUGRAY);

        lblDetails.setPreferredSize(TransportGuiFactory.LABELLARGE);
        lblDetails.setOpaque(true);
        lblDetails.setBackground(TransportGuiFactory.AMTUGRAY);
        lblEndpointValue.setFont(TransportGuiFactory.PLAINTEXT);
        lblEndpoint.setPreferredSize(TransportGuiFactory.LABELLARGE);
        lblDocumentTransport.setPreferredSize(TransportGuiFactory.LABELLARGE);
        lblDocumentTransportValue.setFont(TransportGuiFactory.PLAINTEXT);
        lblDocumentTransportValue
                .setPreferredSize(TransportGuiFactory.TEXTBOXMULTI);
        lblDocumentTransportValue.setLineWrap(true);
        lblDocumentTransportValue.setWrapStyleWord(true);
        lblDocumentTransportValue.setEditable(false);
        lblLastConnectionValue.setFont(TransportGuiFactory.PLAINTEXT);

        lblStatus.setPreferredSize(TransportGuiFactory.LABELLARGE);
        lblStatusValue.setFont(TransportGuiFactory.PLAINTEXT);

        this.add(Box.createRigidArea(TransportGuiFactory.AREASMALL));
        this.add(lblLogo);
        this.add(Box.createRigidArea(TransportGuiFactory.AREASMALL));
        this.add(lblAccounts);
        this.add(cboAccounts);
        this.add(Box.createRigidArea(TransportGuiFactory.AREASMALL));
        this.add(lblSites);
        this.add(cboSiteGroups);
        this.add(Box.createRigidArea(TransportGuiFactory.AREASMALL));
        this.add(lblDetails);
        this.add(lblEndpoint);
        this.add(lblEndpointValue);
        this.add(lblDocumentTransport);
        this.add(lblDocumentTransportValue);
        this.add(lblStatus);
        this.add(lblStatusValue);
        this.add(lblLastConnection);
        this.add(lblLastConnectionValue);

        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(215, (int) getPreferredSize()
                .getHeight()));

        fillAccountList();
    }


    private void fillAccountList() {
        // Fill in account drop down list
        cboAccountModel.removeAllElements();
        for (AMTUAccount account : model.getAccountList()) {
            cboAccountModel.addElement(account);
        }

        fillSiteGroupList();
    }


    private void fillSiteGroupList() {
        cboSiteGroupModel.removeAllElements();
        for (MerchantSiteGroup siteGroup : model.getCurrentAccount()
                .getSiteGroups()) {
            cboSiteGroupModel.addElement(siteGroup);
        }
    }


    private void refreshAccountList() {
        ignoreEvents = true;

        fillAccountList();

        cboAccounts.setSelectedIndex(model.getCurrentAccountIndex());

        cboSiteGroups.setSelectedIndex(model.getCurrentSiteGroupIndex());

        ignoreEvents = false;
    }


    private void refreshSiteList() {
        ignoreEvents = true;

        fillSiteGroupList();

        cboSiteGroups.setSelectedIndex(model.getCurrentSiteGroupIndex());

        ignoreEvents = false;
    }


    private void registerComponents() {
        cboAccounts.addActionListener(new AccountSelectorListener());
        cboSiteGroups.addActionListener(new SiteSelectorListener());
    }


    @Override
    public void updateView() {
        int feedsInQueue = model.getCurrentAccount().getUnsubmittedFeeds();
        String feedsInQueueMsg = feedsInQueue > 1 ? Messages.SideBarView_7
                .toString() : Messages.SideBarView_8.toString();
        lblStatusValue
                .setText(model.getCurrentAccount().getUnsubmittedFeeds() == 0 ? Messages.SideBarView_9
                        .toString() : String.format(feedsInQueueMsg, feedsInQueue));
        lblLastConnectionValue.setText(model.getCurrentAccount()
                .getLastConnection() == null ? Messages.SideBarView_10
                .toString() : model.getCurrentAccount().getLastConnection()
                .toString());
    }


    @Override
    public void updateFeeds() {
    }


    @Override
    public void updateLog(Object msg) {
    }


    @Override
    public void updateReports() {
    }


    @Override
    public void updateAccount() {
        refreshAccountList();

        MWSEndpoint endpoint = model.getCurrentAccount().getMwsEndpoint();
        lblEndpointValue.setText(endpoint.toString());
        lblDocumentTransportValue.setText(model.getCurrentAccount()
                .getDocumentTransport().getAbsolutePath());

        updateView();
    }
    
    @Override
    public void updateProxy() {
    }


    @Override
    public void updateSiteGroup() {
        refreshSiteList();
    }

    private class AccountSelectorListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (!ignoreEvents) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int index = cboAccounts.getSelectedIndex();
                        if (index > -1) {
                            model.setCurrentAccountIndex(index);
                        }
                    }
                });
                t.start();
            }
        }
    }

    private class SiteSelectorListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (!ignoreEvents) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int index = cboSiteGroups.getSelectedIndex();
                        if (index > -1) {
                            model.setCurrentSiteGroupIndex(index);
                        }
                    }
                });
                t.start();
            }
        }
    }
}