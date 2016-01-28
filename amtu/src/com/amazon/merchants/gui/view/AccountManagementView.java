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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;

import com.amazon.merchants.Messages;
import com.amazon.merchants.account.AMTUAccount;
import com.amazon.merchants.account.AccountConfig;
import com.amazon.merchants.account.MerchantAccountException;
import com.amazon.merchants.account.MerchantSite;
import com.amazon.merchants.account.MerchantSiteGroup;
import com.amazon.merchants.gui.model.TransportGuiModel;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.transport.util.BrowserUtil;
import com.amazonaws.mws.MarketplaceWebServiceException;

public class AccountManagementView extends JPanel implements UpdateViewInterface {
    private static final long serialVersionUID = -7335720889418627870L;

    private TransportGuiModel model;

    private JButton btnAddAccount;
    private JButton btnAddSite;
    private JButton btnCancelModifyAccount;
    private JButton btnCancelModifySite;
    private JButton btnDeleteAccount;
    private JButton btnDeleteSite;
    private JButton btnDocumentTransport;
    private JButton btnMWSRegistration;
    private JButton btnModifyAccount;
    private JButton btnModifySite;
    private JButton btnSaveAccount;
    private JButton btnSaveSite;
    private JButton btnTestMWS;
    private JCheckBox chkReportsDisabled;
    private JLabel lblAccessKey;
    private JLabel lblDocumentTransport;
    private JLabel lblProcessingReportDownloadInterval;
    private JLabel lblFeedUploadInterval;
    private JLabel lblMWSRegistrationHelp;
    private JLabel lblMarketplaceId;
    private JLabel lblMerchantId;
    private JLabel lblReportDownloadInterval;
    private JLabel lblSecretKey;
    private JLabel lblSiteDirectory;
    private JLabel lblSiteName;
    private JPanel pnlAccountSettings;
    private JPanel pnlConnectionIntervals;
    private JPanel pnlMWSAuth;
    private JPanel pnlSiteInformation;
    private JTextField txtAccessKey;
    private JTextField txtDocumentTransport;
    private JFormattedTextField txtProcessingReportDownloadInterval;
    private JFormattedTextField txtFeedUploadInterval;
    private JTextField txtMarketplaceId;
    private JTextField txtMerchantId;
    private JFormattedTextField txtReportDownloadInterval;
    private JPasswordField txtSecretKey;
    private JTextField txtSiteDirectory;
    private JTextField txtSiteName;

    private JFileChooser fcDocumentTransport;

    private static final String HTML_FORMATTED_STRING = "<html>%s</html>";

    private enum EditMode {
        RO, EDIT, ADD;
    }

    private EditMode accountMode = EditMode.RO;
    private EditMode siteMode = EditMode.RO;


    public AccountManagementView(TransportGuiModel model) {
        this.model = model;

        initComponents();
        registerComponents();

        this.model.addView(this);
    }


    private void initComponents() {
        pnlAccountSettings = new JPanel();
        lblDocumentTransport = new JLabel();
        txtDocumentTransport = new JTextField();
        btnDocumentTransport = new JButton();
        pnlMWSAuth = new JPanel();
        lblAccessKey = new JLabel();
        lblSecretKey = new JLabel();
        lblMerchantId = new JLabel();
        txtAccessKey = new JTextField();
        txtSecretKey = new JPasswordField();
        txtMerchantId = new JTextField();
        lblMWSRegistrationHelp = new JLabel();
        btnMWSRegistration = new JButton();
        pnlConnectionIntervals = new JPanel();
        lblFeedUploadInterval = new JLabel();
        txtFeedUploadInterval = new JFormattedTextField(NumberFormat.getInstance());
        lblProcessingReportDownloadInterval = new JLabel();
        txtProcessingReportDownloadInterval = new JFormattedTextField(NumberFormat.getInstance());
        chkReportsDisabled = new JCheckBox();
        lblReportDownloadInterval = new JLabel();
        txtReportDownloadInterval = new JFormattedTextField(NumberFormat.getInstance());
        btnModifyAccount = new JButton();
        btnSaveAccount = new JButton();
        btnDeleteAccount = new JButton();
        btnCancelModifyAccount = new JButton();
        btnTestMWS = new JButton();
        btnAddAccount = new JButton();
        pnlSiteInformation = new JPanel();
        lblSiteName = new JLabel();
        txtSiteName = new JTextField();
        lblSiteDirectory = new JLabel();
        txtSiteDirectory = new JTextField();
        lblMarketplaceId = new JLabel();
        txtMarketplaceId = new JTextField();
        btnModifySite = new JButton();
        btnSaveSite = new JButton();
        btnDeleteSite = new JButton();
        btnCancelModifySite = new JButton();
        btnAddSite = new JButton();

        setAutoscrolls(true);

        pnlAccountSettings.setBorder(BorderFactory.createTitledBorder(Messages.AccountManagementView_19.toString()));

        lblDocumentTransport.setText(String.format(HTML_FORMATTED_STRING, Messages.AccountManagementView_8.toString()));

        txtDocumentTransport.setEditable(false);
        txtDocumentTransport.setToolTipText(Messages.AccountManagementView_32.toString());
        txtDocumentTransport.setEnabled(false);
        txtDocumentTransport.setFocusable(false);

        btnDocumentTransport.setText(Messages.AccountManagementView_9.toString());
        btnDocumentTransport.setToolTipText(Messages.AccountManagementView_33.toString());

        pnlMWSAuth.setBorder(BorderFactory.createTitledBorder(Messages.AccountManagementView_18.toString()));

        lblAccessKey.setText(Messages.AccountManagementView_0.toString());

        lblSecretKey.setText(Messages.AccountManagementView_1.toString());

        lblMerchantId.setText(Messages.AccountManagementView_2.toString());

        txtAccessKey.setToolTipText(Messages.AccountManagementView_34.toString());

        txtSecretKey.setToolTipText(Messages.AccountManagementView_35.toString());

        txtMerchantId.setToolTipText(Messages.AccountManagementView_36.toString());

        lblMWSRegistrationHelp.setText(String.format(HTML_FORMATTED_STRING, Messages.AccountManagementView_16.toString()));

        btnMWSRegistration.setText(Messages.AccountManagementView_17.toString());
        btnMWSRegistration.setToolTipText(Messages.AccountManagementView_37.toString());

        GroupLayout pnlMWSAuthLayout = new GroupLayout(pnlMWSAuth);
        pnlMWSAuth.setLayout(pnlMWSAuthLayout);
        pnlMWSAuthLayout.setHorizontalGroup(
            pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlMWSAuthLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblMWSRegistrationHelp, GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
                    .addGroup(pnlMWSAuthLayout.createSequentialGroup()
                        .addGroup(pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(lblAccessKey)
                            .addComponent(lblSecretKey)
                            .addComponent(lblMerchantId))
                        .addGap(52, 52, 52)
                        .addGroup(pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(txtSecretKey, GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                            .addComponent(txtAccessKey, GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                            .addComponent(txtMerchantId, GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)))
                    .addComponent(btnMWSRegistration, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pnlMWSAuthLayout.setVerticalGroup(
            pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlMWSAuthLayout.createSequentialGroup()
                .addGroup(pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAccessKey, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAccessKey))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSecretKey, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSecretKey))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMerchantId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMerchantId))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblMWSRegistrationHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMWSRegistration)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlConnectionIntervals.setBorder(BorderFactory.createTitledBorder(Messages.AccountManagementView_20.toString()));

        lblFeedUploadInterval.setText(Messages.AccountManagementView_3.toString());
        txtFeedUploadInterval.setToolTipText(Messages.AccountManagementView_38.toString());

        lblProcessingReportDownloadInterval.setText(Messages.AccountManagementView_4.toString());
        txtProcessingReportDownloadInterval.setToolTipText(Messages.AccountManagementView_39.toString());

        chkReportsDisabled.setText(Messages.AccountManagementView_31.toString());
        chkReportsDisabled.setToolTipText(Messages.AccountManagementView_40.toString());

        lblReportDownloadInterval.setText(Messages.AccountManagementView_5.toString());
        txtReportDownloadInterval.setToolTipText(Messages.AccountManagementView_41.toString());

        GroupLayout pnlConnectionIntervalsLayout = new GroupLayout(pnlConnectionIntervals);
        pnlConnectionIntervals.setLayout(pnlConnectionIntervalsLayout);
        pnlConnectionIntervalsLayout.setHorizontalGroup(
            pnlConnectionIntervalsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlConnectionIntervalsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlConnectionIntervalsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(chkReportsDisabled)
                    .addGroup(GroupLayout.Alignment.TRAILING, pnlConnectionIntervalsLayout.createSequentialGroup()
                        .addGroup(pnlConnectionIntervalsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(lblFeedUploadInterval)
                            .addComponent(lblProcessingReportDownloadInterval)
                            .addComponent(lblReportDownloadInterval))
                        .addGap(38, 38, 38)))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlConnectionIntervalsLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(txtProcessingReportDownloadInterval, GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                    .addComponent(txtReportDownloadInterval, GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                    .addComponent(txtFeedUploadInterval, GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlConnectionIntervalsLayout.setVerticalGroup(
            pnlConnectionIntervalsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlConnectionIntervalsLayout.createSequentialGroup()
                .addGroup(pnlConnectionIntervalsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFeedUploadInterval)
                    .addComponent(txtFeedUploadInterval, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlConnectionIntervalsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProcessingReportDownloadInterval)
                    .addComponent(txtProcessingReportDownloadInterval, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkReportsDisabled)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlConnectionIntervalsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblReportDownloadInterval)
                    .addComponent(txtReportDownloadInterval, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnSaveAccount.setText(Messages.AccountManagementView_10.toString());
        btnSaveAccount.setToolTipText(Messages.AccountManagementView_43.toString());

        btnModifyAccount.setText(Messages.AccountManagementView_11.toString());
        btnModifyAccount.setToolTipText(Messages.AccountManagementView_42.toString());

        btnDeleteAccount.setText(Messages.AccountManagementView_14.toString());
        btnDeleteAccount.setToolTipText(Messages.AccountManagementView_44.toString());

        btnCancelModifyAccount.setText(Messages.AccountManagementView_15.toString());
        btnCancelModifyAccount.setToolTipText(Messages.AccountManagementView_45.toString());

        btnTestMWS.setText(Messages.AccountManagementView_13.toString());
        btnTestMWS.setToolTipText(Messages.AccountManagementView_46.toString());

        GroupLayout pnlAccountSettingsLayout = new GroupLayout(pnlAccountSettings);
        pnlAccountSettings.setLayout(pnlAccountSettingsLayout);
        pnlAccountSettingsLayout.setHorizontalGroup(
            pnlAccountSettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlAccountSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAccountSettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(pnlMWSAuth, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlAccountSettingsLayout.createSequentialGroup()
                        .addComponent(lblDocumentTransport, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDocumentTransport, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDocumentTransport, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlConnectionIntervals, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING, pnlAccountSettingsLayout.createSequentialGroup()
                        .addComponent(btnTestMWS, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelModifyAccount, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSaveAccount, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteAccount, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModifyAccount, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlAccountSettingsLayout.setVerticalGroup(
            pnlAccountSettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlAccountSettingsLayout.createSequentialGroup()
                .addGroup(pnlAccountSettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAccountSettingsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(txtDocumentTransport, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDocumentTransport))
                    .addComponent(lblDocumentTransport, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlMWSAuth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlConnectionIntervals, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAccountSettingsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnModifyAccount)
                    .addComponent(btnSaveAccount)
                    .addComponent(btnDeleteAccount)
                    .addComponent(btnCancelModifyAccount)
                    .addComponent(btnTestMWS))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnAddAccount.setText(Messages.AccountManagementView_12.toString());
        btnAddAccount.setToolTipText(Messages.AccountManagementView_67.toString());

        pnlSiteInformation.setBorder(BorderFactory.createTitledBorder(Messages.AccountManagementView_52.toString()));

        lblSiteName.setText(Messages.AccountManagementView_53.toString());
        txtSiteName.setEditable(false);
        txtSiteName.setToolTipText(Messages.AccountManagementView_54.toString());
        txtSiteName.setEnabled(false);
        txtSiteName.setFocusable(false);

        lblSiteDirectory.setText(Messages.AccountManagementView_55.toString());
        txtSiteDirectory.setToolTipText(String.format(HTML_FORMATTED_STRING, Messages.AccountManagementView_56.toString()));

        lblMarketplaceId.setText(Messages.AccountManagementView_59.toString());

        txtMarketplaceId.setEditable(false);
        txtMarketplaceId.setToolTipText(Messages.AccountManagementView_60.toString());
        txtMarketplaceId.setEnabled(false);
        txtMarketplaceId.setFocusable(false);

        btnSaveSite.setText(Messages.AccountManagementView_10.toString());
        btnSaveSite.setToolTipText(Messages.AccountManagementView_62.toString());

        btnModifySite.setText(Messages.AccountManagementView_11.toString());
        btnModifySite.setToolTipText(Messages.AccountManagementView_61.toString());

        btnDeleteSite.setText(Messages.AccountManagementView_14.toString());
        btnDeleteSite.setToolTipText(Messages.AccountManagementView_63.toString());

        btnCancelModifySite.setText(Messages.AccountManagementView_15.toString());
        btnCancelModifySite.setToolTipText(Messages.AccountManagementView_64.toString());

        GroupLayout pnlSiteInformationLayout = new GroupLayout(pnlSiteInformation);
        pnlSiteInformation.setLayout(pnlSiteInformationLayout);
        pnlSiteInformationLayout.setHorizontalGroup(
            pnlSiteInformationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlSiteInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSiteInformationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSiteInformationLayout.createSequentialGroup()
                        .addGroup(pnlSiteInformationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(lblSiteName)
                            .addComponent(lblSiteDirectory)
                            .addComponent(lblMarketplaceId))
                        .addGap(19, 19, 19)
                        .addGroup(pnlSiteInformationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(txtMarketplaceId, GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                            .addComponent(txtSiteName, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                            .addComponent(txtSiteDirectory, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)))
                    .addGroup(GroupLayout.Alignment.TRAILING, pnlSiteInformationLayout.createSequentialGroup()
                        .addComponent(btnCancelModifySite, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSaveSite, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteSite, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModifySite, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlSiteInformationLayout.setVerticalGroup(
            pnlSiteInformationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlSiteInformationLayout.createSequentialGroup()
                .addGroup(pnlSiteInformationLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSiteName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSiteName))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlSiteInformationLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSiteDirectory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSiteDirectory))
                .addGap(6, 6, 6)
                .addGroup(pnlSiteInformationLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMarketplaceId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMarketplaceId))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlSiteInformationLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelModifySite)
                    .addComponent(btnDeleteSite)
                    .addComponent(btnSaveSite)
                    .addComponent(btnModifySite))
                .addContainerGap())
        );

        btnAddSite.setText(Messages.AccountManagementView_65.toString());
        btnAddSite.setToolTipText(Messages.AccountManagementView_66.toString());

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(pnlAccountSettings, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlSiteInformation, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnAddSite, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddAccount, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlAccountSettings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlSiteInformation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddAccount)
                    .addComponent(btnAddSite))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }


    private void registerComponents() {
        btnDocumentTransport.addActionListener(new DocumentTransportButtonListener());
        btnModifyAccount.addActionListener(new ModifyAccountButtonListener());
        btnSaveAccount.addActionListener(new SaveAccountButtonListener());
        btnTestMWS.addActionListener(new TestMWSButtonListener());
        btnAddAccount.addActionListener(new AddAccountButtonListener());
        btnDeleteAccount.addActionListener(new DeleteAccountButtonListener());
        btnMWSRegistration.addActionListener(new MWSButtonListener());
        btnCancelModifyAccount.addActionListener(new CancelModifyAccountButtonListener());
        chkReportsDisabled.addItemListener(new ReportsDisabledListener());

        txtAccessKey.addFocusListener(new TextFieldFocusListener());
        txtMerchantId.addFocusListener(new TextFieldFocusListener());
        txtSecretKey.addFocusListener(new TextFieldFocusListener());

        txtFeedUploadInterval.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        txtFeedUploadInterval.selectAll();
                    }
                });
            }
        });
        txtProcessingReportDownloadInterval.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        txtProcessingReportDownloadInterval.selectAll();
                    }
                });
            }
        });
        txtReportDownloadInterval.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        txtReportDownloadInterval.selectAll();
                    }
                });
            }
        });

        txtSiteDirectory.addFocusListener(new TextFieldFocusListener());

        btnModifySite.addActionListener(new ModifySiteButtonListener());
        btnCancelModifySite.addActionListener(new CancelModifySiteButtonListener());
        btnSaveSite.addActionListener(new SaveSiteButtonListener());
        btnAddSite.addActionListener(new AddSiteButtonListener());
        btnDeleteSite.addActionListener(new DeleteSiteButtonListener());
    }


    private void changeAccountMode(EditMode mode) {
        accountMode = mode;
        switch (accountMode) {
            case RO:
                txtDocumentTransport.setEnabled(false);
                txtAccessKey.setEnabled(false);
                txtSecretKey.setEnabled(false);
                txtMerchantId.setEnabled(false);
                txtFeedUploadInterval.setEnabled(false);
                txtReportDownloadInterval.setEnabled(false);
                txtProcessingReportDownloadInterval.setEnabled(false);
                btnDocumentTransport.setEnabled(false);
                chkReportsDisabled.setEnabled(false);

                btnTestMWS.setEnabled(false);
                btnTestMWS.setVisible(false);
                btnDeleteAccount.setEnabled(true);
                btnDeleteAccount.setVisible(true);
                btnSaveAccount.setEnabled(false);
                btnSaveAccount.setVisible(false);
                btnModifyAccount.setEnabled(true);
                btnModifyAccount.setVisible(true);
                btnCancelModifyAccount.setEnabled(false);
                btnCancelModifyAccount.setVisible(false);
                break;
            case EDIT:
                txtDocumentTransport.setEnabled(true);
                txtAccessKey.setEnabled(true);
                txtSecretKey.setEnabled(true);
                txtMerchantId.setEnabled(true);
                btnDocumentTransport.setEnabled(true);
                btnTestMWS.setEnabled(true);
                txtFeedUploadInterval.setEnabled(true);
                txtReportDownloadInterval.setEnabled(!chkReportsDisabled.isSelected());
                txtProcessingReportDownloadInterval.setEnabled(true);
                chkReportsDisabled.setEnabled(true);

                btnTestMWS.setEnabled(true);
                btnTestMWS.setVisible(true);
                btnDeleteAccount.setEnabled(false);
                btnDeleteAccount.setVisible(false);
                btnSaveAccount.setEnabled(true);
                btnSaveAccount.setVisible(true);
                btnModifyAccount.setEnabled(false);
                btnModifyAccount.setVisible(false);

                btnCancelModifyAccount.setEnabled(true);
                btnCancelModifyAccount.setVisible(true);

                break;
        }
    }


    private void changeSiteMode(EditMode mode) {
        siteMode = mode;
        switch (siteMode) {
            case RO:
                btnDeleteSite.setVisible(true);
                btnModifySite.setVisible(true);

                boolean defaultSiteGroup = MerchantSiteGroup.DEFAULT_SITE_GROUP_ALIAS.equals(model
                    .getCurrentSiteGroup().getMerchantAlias());
                // Delete and Modify are not available for default site group
                btnDeleteSite.setEnabled(!defaultSiteGroup);
                btnModifySite.setEnabled(!defaultSiteGroup);

                btnCancelModifySite.setEnabled(false);
                btnCancelModifySite.setVisible(false);
                btnSaveSite.setEnabled(false);
                btnSaveSite.setVisible(false);

                txtSiteDirectory.setEnabled(false);
                break;
            case EDIT:
                btnDeleteSite.setEnabled(false);
                btnDeleteSite.setVisible(false);
                btnModifySite.setEnabled(false);
                btnModifySite.setVisible(false);
                btnCancelModifySite.setEnabled(true);
                btnCancelModifySite.setVisible(true);
                btnSaveSite.setEnabled(true);
                btnSaveSite.setVisible(true);

                txtSiteDirectory.setEnabled(true);
                break;
        }
    }


    @Override
    public void updateView() {
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
        AMTUAccount currentAccount = model.getCurrentAccount();

        txtAccessKey.setText(currentAccount.getMwsAccessKey());
        txtSecretKey.setText(currentAccount.getMwsSecretKey());
        txtMerchantId.setText(currentAccount.getMerchantId());
        txtDocumentTransport.setText(currentAccount.getDocumentTransport().getAbsolutePath());

        txtFeedUploadInterval
            .setValue(Long.valueOf(currentAccount.getConfigValue(AccountConfig.FEED_DISPATCH_INTERVAL)));
        txtReportDownloadInterval.setValue(Long.valueOf(currentAccount
            .getConfigValue(AccountConfig.REPORT_RETRIEVAL_INTERVAL)));
        txtProcessingReportDownloadInterval.setValue(Long.valueOf(currentAccount
            .getConfigValue(AccountConfig.PROCESSING_REPORT_RETRIEVAL_INTERVAL)));

        chkReportsDisabled.setSelected(AccountConfig.BOOLEAN_TRUE.equals(currentAccount
            .getConfigValue(AccountConfig.REPORTS_DISABLED)));

        changeAccountMode(EditMode.RO);
    }
    
    @Override
    public void updateProxy() {
    }


    @Override
    public void updateSiteGroup() {
        MerchantSiteGroup currentSiteGroup = model.getCurrentSiteGroup();

        txtSiteName.setText(currentSiteGroup.getMerchantAlias());
        txtSiteDirectory.setText(currentSiteGroup.getDocumentTransport());

        List<MerchantSite> siteList = currentSiteGroup.getSiteList();
        String marketplaceId = "";
        if (siteList != null && !siteList.isEmpty()) {
            MerchantSite site = siteList.get(0);
            marketplaceId = site.getMarketplaceId();
        }
        txtMarketplaceId.setText(marketplaceId);

        changeSiteMode(EditMode.RO);
    }

    private class DocumentTransportButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            int returnVal = fcDocumentTransport.showOpenDialog(AccountManagementView.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                if (fcDocumentTransport.getSelectedFile().isDirectory()) {
                    txtDocumentTransport.setText(fcDocumentTransport.getSelectedFile().getAbsolutePath());
                }
                else {
                    JOptionPane.showMessageDialog(AccountManagementView.this,
                        Messages.AccountManagementView_21.toString(), Messages.AccountManagementView_22.toString(),
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    private class ReportsDisabledListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            txtReportDownloadInterval.setEnabled(!chkReportsDisabled.isSelected());
        }
    }

    private class ModifyAccountButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            changeAccountMode(EditMode.EDIT);
        }
    }

    private class CancelModifyAccountButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        AMTUAccount account = model.getCurrentAccount();
                        account.reload();
                        updateAccount();
                    }
                    catch (MerchantAccountException e) {
                        TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);
                        JOptionPane.showMessageDialog(AccountManagementView.this, e.getLocalizedMessage(),
                            Messages.AccountManagementView_22.toString(), JOptionPane.INFORMATION_MESSAGE);
                    }
                    finally {
                        btnSaveAccount.setText(Messages.AccountManagementView_10.toString());
                        btnSaveAccount.setEnabled(true);
                    }
                }
            });
            t.start();
        }
    }

    private class SaveAccountButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        btnSaveAccount.setEnabled(false);
                        btnSaveAccount.setText(Messages.AccountManagementView_23.toString());

                        // Check Alias and Document Transport directory settings
                        AMTUAccount account = model.getCurrentAccount();

                        account.setDocumentTransport(txtDocumentTransport.getText());
                        account.setConfigValue(AccountConfig.FEED_DISPATCH_INTERVAL, txtFeedUploadInterval.getValue()
                            .toString());
                        account.setConfigValue(AccountConfig.REPORT_RETRIEVAL_INTERVAL, txtReportDownloadInterval
                            .getValue().toString());
                        account.setConfigValue(AccountConfig.PROCESSING_REPORT_RETRIEVAL_INTERVAL,
                            txtProcessingReportDownloadInterval.getValue().toString());
                        account.setConfigValue(AccountConfig.REPORTS_DISABLED,
                            chkReportsDisabled.isSelected() ? AccountConfig.BOOLEAN_TRUE : AccountConfig.BOOLEAN_FALSE);

                        model.validateAccount(account);

                        String accessKey = txtAccessKey.getText().trim().toUpperCase();
                        String secretKey = new String(txtSecretKey.getPassword()).trim();
                        String merchantId = txtMerchantId.getText().trim().toUpperCase();

                        if (!accessKey.equals(account.getMwsAccessKey())
                            || !secretKey.equals(account.getMwsSecretKey())
                            || !merchantId.equals(account.getMerchantId())) {
                            account.setMwsAccessKey(accessKey);
                            account.setMwsSecretKey(secretKey);
                            account.setMerchantId(merchantId);
                            
                            model.validateCredentials(account, null);
                        }

                        // Store to database
                        model.saveMerchantAccount(account);

                        // Gray out the form
                        changeAccountMode(EditMode.RO);

                    }
                    catch (MarketplaceWebServiceException e) {
                        TransportLogger.getSysErrorLogger()
                            .error(
                                String.format(Messages.MarketplaceWebServiceException_0.toString(),
                                    e.getLocalizedMessage()), e);
                        JOptionPane.showMessageDialog(AccountManagementView.this, String.format(
                            Messages.MarketplaceWebServiceException_0.toString(), e.getLocalizedMessage()),
                            Messages.AccountManagementView_22.toString(), JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (MerchantAccountException e) {
                        TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);
                        JOptionPane.showMessageDialog(AccountManagementView.this, e.getLocalizedMessage(),
                            Messages.AccountManagementView_22.toString(), JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (IOException e) {
                        TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);
                        JOptionPane.showMessageDialog(AccountManagementView.this, e.getLocalizedMessage(),
                            Messages.AccountManagementView_22.toString(), JOptionPane.INFORMATION_MESSAGE);
                    }
                    finally {
                        btnSaveAccount.setText(Messages.AccountManagementView_10.toString());
                        btnSaveAccount.setEnabled(true);
                    }
                }
            });
            t.start();
        }
    }

    private class TestMWSButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        btnTestMWS.setEnabled(false);
                        btnTestMWS.setText(Messages.AccountManagementView_24.toString());

                        AMTUAccount account = model.getCurrentAccount();

                        // Call MWS with given information
                        account.setMwsAccessKey(txtAccessKey.getText().trim().toUpperCase());
                        account.setMwsSecretKey(new String(txtSecretKey.getPassword()).trim());
                        account.setMerchantId(txtMerchantId.getText().trim().toUpperCase());
                        
                        model.validateCredentials(account, null);

                        // Show success message
                        JOptionPane.showMessageDialog(AccountManagementView.this,
                            Messages.AccountManagementView_25.toString(), Messages.AccountManagementView_26.toString(),
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (MarketplaceWebServiceException e) {
                        TransportLogger.getSysErrorLogger()
                            .error(
                                String.format(Messages.MarketplaceWebServiceException_0.toString(),
                                    e.getLocalizedMessage()), e);
                        JOptionPane.showMessageDialog(AccountManagementView.this, String.format(
                            Messages.MarketplaceWebServiceException_0.toString(), e.getLocalizedMessage()),
                            Messages.AccountManagementView_22.toString(), JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (MerchantAccountException e) {
                        JOptionPane.showMessageDialog(AccountManagementView.this, e.getLocalizedMessage(),
                            Messages.AccountManagementView_22.toString(), JOptionPane.INFORMATION_MESSAGE);
                    }
                    finally {
                        btnTestMWS.setText(Messages.AccountManagementView_13.toString());
                        btnTestMWS.setEnabled(true);
                    }
                }
            });
            t.start();
        }
    }

    private class AddAccountButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            model.showAccountSetupWizard();
        }
    }

    private class MWSButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            BrowserUtil.openURLInBrowser(model.getCurrentAccount().getMwsEndpoint().getMwsRegistrationURL());
        }
    }

    private class DeleteAccountButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            try {
                String msg = Messages.AccountManagementView_27.toString();
                String title = Messages.AccountManagementView_28.toString();
                int ans = JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.YES_NO_OPTION);

                if (ans == JOptionPane.NO_OPTION) {
                    return;
                }

                model.deleteMerchantAccount(model.getCurrentAccount());

                model.setCurrentAccountIndex(0);
            }
            catch (MerchantAccountException e) {
                TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);

                // No more account stored for this user, shutdown application
                JOptionPane.showMessageDialog(AccountManagementView.this, Messages.AccountManagementView_29.toString(),
                    Messages.AccountManagementView_30.toString(), JOptionPane.INFORMATION_MESSAGE);
                // Forced shutdown
                model.shutdown(true);
            }
        }
    }

    private class ModifySiteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            changeSiteMode(EditMode.EDIT);
        }
    }

    private class CancelModifySiteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        updateSiteGroup();
                    }
                    finally {
                        btnSaveSite.setText(Messages.AccountManagementView_10.toString());
                        btnSaveSite.setEnabled(true);
                    }
                }
            });
            t.start();
        }
    }

    private class SaveSiteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        btnSaveSite.setEnabled(false);
                        btnSaveSite.setText(Messages.AccountManagementView_23.toString());

                        AMTUAccount account = model.getCurrentAccount();
                        MerchantSiteGroup siteGroup = account.getSiteGroup(model.getCurrentSiteGroup()
                            .getMerchantAlias());

                        siteGroup.setDocumentTransport(txtSiteDirectory.getText().trim());

                        model.validateAccount(account);

                        // Store to database
                        model.saveMerchantAccount(account);

                        // Gray out the form
                        changeSiteMode(EditMode.RO);

                        TransportLogger.getAcctAuditLogger(account).info(
                            "[" + account.getMerchantAlias() + "] " + Messages.MerchantSiteGroup_0.toString()
                                + " alias=" + siteGroup.getMerchantAlias() + ", " + "documentTransport="
                                + siteGroup.getDocumentTransport());
                    }
                    catch (MerchantAccountException e) {
                        TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);
                        JOptionPane.showMessageDialog(AccountManagementView.this, e.getLocalizedMessage(),
                            Messages.AccountManagementView_22.toString(), JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (IOException e) {
                        TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);
                        JOptionPane.showMessageDialog(AccountManagementView.this, e.getLocalizedMessage(),
                            Messages.AccountManagementView_22.toString(), JOptionPane.INFORMATION_MESSAGE);
                    }
                    finally {
                        btnSaveSite.setText(Messages.AccountManagementView_10.toString());
                        btnSaveSite.setEnabled(true);
                    }
                }
            });
            t.start();
        }
    }

    private class DeleteSiteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String msg = Messages.AccountManagementView_68.toString();
                        String title = Messages.AccountManagementView_28.toString();
                        int ans = JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.YES_NO_OPTION);

                        if (ans == JOptionPane.NO_OPTION) {
                            return;
                        }

                        String siteGroupAlias = model.getCurrentSiteGroup().getMerchantAlias();

                        model.deleteMerchantSiteGroup(model.getCurrentSiteGroup());

                        TransportLogger.getAcctAuditLogger(model.getCurrentAccount()).info(
                            "[" + model.getCurrentAccount().getMerchantAlias() + "] "
                                + Messages.MerchantSiteGroup_1.toString() + " alias=" + siteGroupAlias);
                    }
                    catch (MerchantAccountException e) {
                        TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);
                    }
                }
            });
            t.start();
        }
    }

    private class AddSiteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            model.showAddRegisteredSiteWizard();
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
}