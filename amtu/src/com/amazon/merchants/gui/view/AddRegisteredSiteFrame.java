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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.amazon.merchants.Messages;
import com.amazon.merchants.account.AMTUAccount;
import com.amazon.merchants.account.MerchantAccountException;
import com.amazon.merchants.account.MerchantSite;
import com.amazon.merchants.account.MerchantSiteGroup;
import com.amazon.merchants.gui.model.TransportGuiModel;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.transport.model.AmazonMarketplaceSite;

public class AddRegisteredSiteFrame extends JDialog {
    private static final long serialVersionUID = 5539005802023170812L;

    private TransportGuiModel model = null;

    private JButton btnCancel;
    private JButton btnSave;
    private JLabel lblDirectoryName;
    private JLabel lblMarketplaceId;
    private JLabel lblSiteName;
    private JList lstAmazonSiteList;
    private ButtonGroup marketplaceSiteSelect;
    private JPanel pnlSellerSite;
    private JRadioButton rdoExistingMarketplaceSite;
    private JRadioButton rdoSellerSpecificSite;
    private JScrollPane scrlAmazonSiteList;
    private JTextField txtDirectoryName;
    private JTextField txtMarketplaceId;
    private JTextField txtSiteName;
    private DefaultListModel modelAmazonSiteList;

    private MerchantSiteGroup siteGroup = null;
    private MerchantSite merchantSite = null;

    private static final String HTML_FORMATTED_STRING = "<html>%s</html>";

    private boolean defaultDtdChanged = false;

    private List<String> siteDirectoryNames = null;


    public AddRegisteredSiteFrame(TransportGuiModel model) {
        setTitle(Messages.AddRegisteredSiteFrame_0.toString());
        setModalityType(ModalityType.APPLICATION_MODAL);
        this.model = model;

        siteDirectoryNames = model.getCurrentAccountSiteGroupDirectories();

        initComponents();
        registerComponents();
        setVisible(true);
    }

    private static final String EXISTING = "existing"; //$NON-NLS-1$
    private static final String NEW = "new"; //$NON-NLS-1$


    private void initComponents() {
        marketplaceSiteSelect = new ButtonGroup();
        rdoExistingMarketplaceSite = new JRadioButton();
        scrlAmazonSiteList = new JScrollPane();
        lstAmazonSiteList = new JList();
        rdoSellerSpecificSite = new JRadioButton();
        pnlSellerSite = new JPanel();
        lblSiteName = new JLabel();
        txtSiteName = new JTextField();
        lblMarketplaceId = new JLabel();
        txtMarketplaceId = new JTextField();
        lblDirectoryName = new JLabel();
        txtDirectoryName = new JTextField();
        btnCancel = new JButton();
        btnSave = new JButton();
        modelAmazonSiteList = new DefaultListModel();

        setMinimumSize(new Dimension(340, 335));
        setResizable(false);

        marketplaceSiteSelect.add(rdoExistingMarketplaceSite);
        rdoExistingMarketplaceSite.setSelected(true);
        rdoExistingMarketplaceSite.setText(Messages.AddRegisteredSiteFrame_1
                .toString());
        rdoExistingMarketplaceSite
                .setToolTipText(Messages.AddRegisteredSiteFrame_2.toString());
        rdoExistingMarketplaceSite.setActionCommand(EXISTING);

        modelAmazonSiteList.removeAllElements();
        for (AmazonMarketplaceSite marketplaceSite : AmazonMarketplaceSite
                .getAmazonSites()) {
            modelAmazonSiteList.addElement(marketplaceSite);
        }
        lstAmazonSiteList.setModel(modelAmazonSiteList);
        lstAmazonSiteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstAmazonSiteList.setToolTipText(Messages.AddRegisteredSiteFrame_3
                .toString());

        scrlAmazonSiteList.setViewportView(lstAmazonSiteList);

        marketplaceSiteSelect.add(rdoSellerSpecificSite);
        rdoSellerSpecificSite.setText(Messages.AddRegisteredSiteFrame_4
                .toString());
        rdoSellerSpecificSite.setToolTipText(Messages.AddRegisteredSiteFrame_5
                .toString());
        rdoSellerSpecificSite.setActionCommand(NEW);

        pnlSellerSite.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlSellerSite.setEnabled(false);

        lblSiteName.setText(Messages.AddRegisteredSiteFrame_6.toString());

        txtSiteName.setToolTipText(String.format(HTML_FORMATTED_STRING,
                Messages.AddRegisteredSiteFrame_7.toString()));

        lblMarketplaceId.setText(Messages.AddRegisteredSiteFrame_8.toString());

        txtMarketplaceId.setToolTipText(String.format(HTML_FORMATTED_STRING,
                Messages.AddRegisteredSiteFrame_9.toString()));

        GroupLayout pnlSellerSiteLayout = new GroupLayout(pnlSellerSite);
        pnlSellerSite.setLayout(pnlSellerSiteLayout);
        pnlSellerSiteLayout
                .setHorizontalGroup(pnlSellerSiteLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(
                                pnlSellerSiteLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                pnlSellerSiteLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.LEADING)
                                                        .addGroup(
                                                                pnlSellerSiteLayout
                                                                        .createSequentialGroup()
                                                                        .addGroup(
                                                                                pnlSellerSiteLayout
                                                                                        .createParallelGroup(
                                                                                                GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(
                                                                                                lblSiteName,
                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(
                                                                                                lblMarketplaceId,
                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                        .addGap(48,
                                                                                48,
                                                                                48))
                                                        .addGroup(
                                                                GroupLayout.Alignment.TRAILING,
                                                                pnlSellerSiteLayout
                                                                        .createSequentialGroup()
                                                                        .addGroup(
                                                                                pnlSellerSiteLayout
                                                                                        .createParallelGroup(
                                                                                                GroupLayout.Alignment.TRAILING)
                                                                                        .addComponent(
                                                                                                txtMarketplaceId,
                                                                                                GroupLayout.Alignment.LEADING,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                293,
                                                                                                Short.MAX_VALUE)
                                                                                        .addComponent(
                                                                                                txtSiteName,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                293,
                                                                                                Short.MAX_VALUE))
                                                                        .addContainerGap()))));
        pnlSellerSiteLayout.setVerticalGroup(pnlSellerSiteLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                        pnlSellerSiteLayout
                                .createSequentialGroup()
                                .addComponent(lblSiteName,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSiteName,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblMarketplaceId,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMarketplaceId,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)));

        lblDirectoryName.setText(Messages.AddRegisteredSiteFrame_10.toString());

        txtDirectoryName.setToolTipText(String.format(HTML_FORMATTED_STRING,
                Messages.AddRegisteredSiteFrame_11.toString()));

        btnCancel.setText(Messages.AddRegisteredSiteFrame_12.toString());
        btnCancel.setToolTipText(Messages.AddRegisteredSiteFrame_13.toString());

        btnSave.setText(Messages.AddRegisteredSiteFrame_14.toString());
        btnSave.setToolTipText(Messages.AddRegisteredSiteFrame_15.toString());

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                        layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.LEADING)
                                                .addComponent(
                                                        rdoExistingMarketplaceSite)
                                                .addComponent(
                                                        rdoSellerSpecificSite)
                                                .addComponent(
                                                        pnlSellerSite,
                                                        0,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        Short.MAX_VALUE)
                                                .addComponent(
                                                        scrlAmazonSiteList,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        321, Short.MAX_VALUE)
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addComponent(
                                                                        btnCancel,
                                                                        GroupLayout.PREFERRED_SIZE,
                                                                        150,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18,
                                                                        18)
                                                                .addComponent(
                                                                        btnSave,
                                                                        GroupLayout.DEFAULT_SIZE,
                                                                        153,
                                                                        Short.MAX_VALUE))
                                                .addComponent(
                                                        lblDirectoryName,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        321, Short.MAX_VALUE)
                                                .addComponent(
                                                        txtDirectoryName,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        321, Short.MAX_VALUE))
                                .addContainerGap()));
        layout.setVerticalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                        layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(rdoExistingMarketplaceSite)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrlAmazonSiteList,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rdoSellerSpecificSite)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlSellerSite,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblDirectoryName,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDirectoryName,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.BASELINE)
                                                .addComponent(
                                                        btnCancel,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addComponent(
                                                        btnSave,
                                                        GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)));

        pack();
        setLocationRelativeTo(null);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }


    private void registerComponents() {
        // TODO: Add code to provide a suggested default DTD
        lstAmazonSiteList.setEnabled(true);
        lstAmazonSiteList.setFocusable(true);

        ListSelectionModel listSelectionModel = lstAmazonSiteList
                .getSelectionModel();
        listSelectionModel
                .addListSelectionListener(new SiteListSelectionListener());

        txtSiteName.setEnabled(false);
        txtSiteName.setFocusable(false);
        txtMarketplaceId.setEnabled(false);
        txtMarketplaceId.setFocusable(false);

        btnCancel.addActionListener(new CancelButtonListener());
        btnSave.addActionListener(new SaveButtonListener());
        rdoExistingMarketplaceSite.addActionListener(new RadioButtonListener());
        rdoSellerSpecificSite.addActionListener(new RadioButtonListener());

        txtMarketplaceId.addFocusListener(new TextFieldFocusListener());
        txtSiteName.addFocusListener(new TextFieldFocusListener());
        txtSiteName.getDocument().addDocumentListener(new SiteNameChangeListener());
        txtDirectoryName.addFocusListener(new TextFieldFocusListener());
        txtDirectoryName.getDocument().addDocumentListener(
                new DTDChangeListener());
    }

    private class CancelButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            siteGroup = null;
            merchantSite = null;
            dispose();
        }
    }

    private class SaveButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    btnSave.setEnabled(false);
                    btnSave.setText(Messages.AccountManagementView_23
                            .toString());

                    String directoryName = txtDirectoryName.getText().trim();
                    String marketplaceId = txtMarketplaceId.getText().trim();
                    String siteName = txtSiteName.getText().trim();
                    // Temporarily will be the same as the site
                    String siteGroupName = siteName;

                    try {
                        // Verify a marketplace site is selected
                        String marketplaceSiteChoice = marketplaceSiteSelect
                                .getSelection().getActionCommand();
                        if (EXISTING.equals(marketplaceSiteChoice)) {
                            if (lstAmazonSiteList.getSelectedIndex() < 0) {
                                throw new MerchantAccountException(
                                        Messages.AddRegisteredSiteFrame_16
                                                .toString());
                            }
                            AmazonMarketplaceSite amazonSite = (AmazonMarketplaceSite) lstAmazonSiteList
                                    .getSelectedValue();
                            siteGroupName = amazonSite.getAlias();
                            siteName = amazonSite.getAlias();
                            marketplaceId = amazonSite.getMarketplaceId();
                        }

                        // Create site group object
                        AMTUAccount account = model.getCurrentAccount();
                        if (siteGroup == null) {
                            siteGroup = new MerchantSiteGroup(account);
                            merchantSite = new MerchantSite(siteGroup);
                        }

                        siteGroup.setMerchantAlias(siteGroupName);
                        siteGroup.setDocumentTransport(directoryName);

                        merchantSite.setMarketplaceId(marketplaceId);
                        merchantSite.setMerchantAlias(siteName);
                        siteGroup.addMerchantSite(merchantSite);

                        account.addSiteGroup(siteGroup);

                        // Validate and save
                        model.validateAccount(account);

                        model.saveMerchantAccount(account);

                        TransportLogger
                                .getAcctAuditLogger(account)
                                .info(String
                                        .format("[" + account.getMerchantAlias() + "] " //$NON-NLS-1$ //$NON-NLS-2$
                                                + Messages.AddRegisteredSiteFrame_17
                                                        .toString(), siteGroup
                                                .getMerchantAlias()));

                        dispose();
                    }
                    catch (MerchantAccountException e) {
                        TransportLogger.getSysErrorLogger().error(
                                e.getLocalizedMessage(), e);
                        JOptionPane.showMessageDialog(
                                AddRegisteredSiteFrame.this,
                                e.getLocalizedMessage(),
                                Messages.AccountManagementView_22.toString(),
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (IOException e) {
                        TransportLogger.getSysErrorLogger().error(
                                e.getLocalizedMessage(), e);
                        JOptionPane.showMessageDialog(
                                AddRegisteredSiteFrame.this,
                                e.getLocalizedMessage(),
                                Messages.AccountManagementView_22.toString(),
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    finally {
                        btnSave.setText(Messages.AddRegisteredSiteFrame_14
                                .toString());
                        btnSave.setEnabled(true);
                    }
                }

            });
            t.start();
        }
    }

    private class RadioButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            String marketplaceSiteChoice = marketplaceSiteSelect.getSelection()
                    .getActionCommand();
            if (EXISTING.equals(marketplaceSiteChoice)) {
                lstAmazonSiteList.setEnabled(true);
                lstAmazonSiteList.setFocusable(true);

                txtSiteName.setEnabled(false);
                txtSiteName.setFocusable(false);
                txtMarketplaceId.setEnabled(false);
                txtMarketplaceId.setFocusable(false);
            }
            else if (NEW.equals(marketplaceSiteChoice)) {
                lstAmazonSiteList.setEnabled(false);
                lstAmazonSiteList.setFocusable(false);

                txtSiteName.setEnabled(true);
                txtSiteName.setFocusable(true);
                txtMarketplaceId.setEnabled(true);
                txtMarketplaceId.setFocusable(true);
            }
        }
    }

    private class TextFieldFocusListener implements FocusListener {
        @Override
        public void focusGained(FocusEvent e) {
            Component c = e.getComponent();
            if (c instanceof JPasswordField) {
                ((JPasswordField) c).selectAll();
            }
            else if (c instanceof JTextField) {
                ((JTextField) c).selectAll();
            }
        }


        @Override
        public void focusLost(FocusEvent e) {
            // Do nothing
        }
    }

    private class SiteListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            String marketplaceSiteChoice = marketplaceSiteSelect.getSelection()
                    .getActionCommand();
            if (EXISTING.equals(marketplaceSiteChoice)) {
                AmazonMarketplaceSite amazonSite = (AmazonMarketplaceSite) lstAmazonSiteList
                        .getSelectedValue();

                // If the value hasn't already been changed, suggest a default
                if (!defaultDtdChanged) {
                    String directoryName = amazonSite.getShortCode();
                    if (siteDirectoryNames != null) {
                        int i = 1;
                        // Ensure the directory name is unique
                        while (siteDirectoryNames.contains(directoryName)) {
                            directoryName = amazonSite.getShortCode() + i;
                        }
                    }

                    txtDirectoryName.setText(directoryName);

                    // Reset to false since setText() changes the flag
                    defaultDtdChanged = false;
                }
            }
        }
    }

    private class DTDChangeListener implements DocumentListener {
        @Override
        public void changedUpdate(DocumentEvent e) {
            check(e);
        }


        @Override
        public void insertUpdate(DocumentEvent e) {
            check(e);
        }


        @Override
        public void removeUpdate(DocumentEvent e) {
            check(e);
        }


        public void check(DocumentEvent e) {
            defaultDtdChanged = !txtDirectoryName.getText().isEmpty();
        }
    }

    private class SiteNameChangeListener implements DocumentListener {
        @Override
        public void changedUpdate(DocumentEvent e) {
            check(e);
        }


        @Override
        public void insertUpdate(DocumentEvent e) {
            check(e);
        }


        @Override
        public void removeUpdate(DocumentEvent e) {
            check(e);
        }


        public void check(DocumentEvent e) {
            if (!defaultDtdChanged) {
                String dirName = txtSiteName.getText().replaceAll(
                        "[^_A-Za-z0-9]", "_");

                if (siteDirectoryNames != null) {
                    int i = 1;
                    // Ensure the directory name is unique
                    String tempDirName = dirName;
                    while (siteDirectoryNames.contains(tempDirName)) {
                        tempDirName = dirName + i;
                    }
                    dirName = tempDirName;
                }

                txtDirectoryName.setText(dirName);

                // Reset to false since setText() changes the flag
                defaultDtdChanged = false;
            }
        }
    }
}