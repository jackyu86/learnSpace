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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;

import javax.swing.*;

import com.amazon.merchants.Messages;
import com.amazon.merchants.account.AMTUAccount;
import com.amazon.merchants.account.AccountConfig;
import com.amazon.merchants.account.MerchantAccountException;
import com.amazon.merchants.database.DatabaseException;
import com.amazon.merchants.gui.model.RegionUtil;
import com.amazon.merchants.gui.model.TransportGuiModel;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.system.ProxyConfig;
import com.amazon.merchants.system.ProxyException;
import com.amazon.merchants.transport.model.MWSEndpoint;
import com.amazon.merchants.transport.util.BrowserUtil;
import com.amazon.merchants.util.file.DirectoryUtil;
import com.amazonaws.mws.MarketplaceWebServiceException;

public class AccountWizardFrame extends JDialog {
    private static final long serialVersionUID = 7686022691808108435L;
    private static final int ROWS_PER_AUTH_GROUP = 3;
    private TransportGuiModel model;

    private CardLayout layout = new CardLayout();
    private JPanel pnlCenter = new JPanel();
    private JPanel pnlLogo = new JPanel();
    private JPanel pnlControl = new JPanel();
    private JPanel pnlWelcome = new JPanel();
    private JPanel pnlAuthen = new JPanel();
    private JPanel pnlValidate = new JPanel();
    private JPanel pnlIntervals = new JPanel();
    private JPanel pnlProxy = new JPanel();
    private JPanel pnlCurrent = pnlWelcome;

    private JPanel pnlFolderButton = new JPanel();

    private Icon logo = new ImageIcon(DirectoryUtil.getCompanyNameImagePath());
    private JLabel lblLogo = new JLabel(logo);
    private JLabel lblSteps = new JLabel(" " + Messages.AccountWizardFrame_0.toString());
    private JLabel lblStep1 = new JLabel("  " + Messages.AccountWizardFrame_1.toString());
    private JLabel lblStep2 = new JLabel("  " + Messages.AccountWizardFrame_2.toString());
    private JLabel lblStep3 = new JLabel("  " + Messages.AccountWizardFrame_3.toString());

    private JButton btnBack = new JButton(Messages.AccountWizardFrame_4.toString());
    private JButton btnNext = new JButton(Messages.AccountWizardFrame_5.toString());
    private JButton btnCancel = new JButton(Messages.AccountWizardFrame_6.toString());
    private JButton btnAdd = new JButton(Messages.AccountWizardFrame_7.toString());
    private JButton btnAddRegisteredSite = new JButton(Messages.AccountWizardFrame_100.toString());

    private JTextField txtAlias = new JTextField();
    private JLabel lblAlias = new JLabel(Messages.AccountWizardFrame_8.toString());
    private JLabel lblAliasHelp = new JLabel(String.format("<html>%s<br>%s<br> </html>",
        Messages.AccountWizardFrame_10.toString(), Messages.AccountWizardFrame_11.toString()));

    private JLabel lblEndpoint = new JLabel(Messages.AccountWizardFrame_12.toString());
    private JLabel lblEndpointHelp = new JLabel(String.format("<html>%s<br></html>",
        Messages.AccountWizardFrame_14.toString()));
    private DefaultComboBoxModel cboEndpointModel = new DefaultComboBoxModel();
    private JComboBox cboEndpoint = new JComboBox(cboEndpointModel);

    private JLabel lblDocumentTransport = new JLabel(Messages.AccountWizardFrame_15.toString());
    private JTextField txtDocumentTransport = new JTextField();
    private JButton btnDocumentTransport = new JButton(Messages.AccountWizardFrame_16.toString());
    private JLabel lblDocumentTransportHelp = new JLabel(String.format("<html>%s<br></html>",
        Messages.AccountWizardFrame_18.toString()));
    private JFileChooser fcDocumentTransport = new JFileChooser();

    private JLabel lblMwsAccessKey = new JLabel(Messages.AccountWizardFrame_19.toString());
    private JLabel lblMwsSecretKey = new JLabel(Messages.AccountWizardFrame_20.toString());
    private JLabel lblMerchantId = new JLabel(Messages.AccountWizardFrame_21.toString());
    private JTextField txtMwsAccessKey = new JTextField();
    private JPasswordField txtMwsSecretKey = new JPasswordField();
    private JTextField txtMerchantId = new JTextField();
    private JLabel lblMwsAccessKeyHelp = new JLabel(String.format("<html>%s<br>%s EXAMPLEPFT7VN2WYGMG2.</html>",
        Messages.AccountWizardFrame_23.toString(), Messages.AccountWizardFrame_24.toString()));
    private JLabel lblMwsSecretKeyHelp = new JLabel(String.format(
        "<html>%s<br>%s eXamPlE6k5XXqZu5sXCxYFURNB2BiF2SM1Hx/ilw</html>", Messages.AccountWizardFrame_26.toString(),
        Messages.AccountWizardFrame_24.toString()));
    private JLabel lblMerchantIdHelp = new JLabel(String.format("<html>%s<br>%s EXAMPLE9AAD8OM</html>",
        Messages.AccountWizardFrame_29.toString(), Messages.AccountWizardFrame_24.toString()));
    
    private JLabel lblProxyHost = new JLabel(Messages.AccountWizardFrame_102.toString());
    private JLabel lblProxyPort = new JLabel(Messages.AccountWizardFrame_103.toString());
    private JLabel lblProxyUser = new JLabel(Messages.AccountWizardFrame_104.toString());
    private JLabel lblProxyPass = new JLabel(Messages.AccountWizardFrame_105.toString());
    private JTextField txtProxyHost = new JTextField();
    private JTextField txtProxyPort = new JTextField();
    private JTextField txtProxyUser = new JTextField();
    private JPasswordField txtProxyPass = new JPasswordField();
    private JLabel lblProxyHostHelp = new JLabel(String.format("<html>%s<br>%s proxyhost.example.com.</html>",
        Messages.AccountWizardFrame_110.toString(), Messages.AccountWizardFrame_24.toString()));
    private JLabel lblProxyPortHelp = new JLabel(String.format(
        "<html>%s<br>%s 443</html>", Messages.AccountWizardFrame_111.toString(),
        Messages.AccountWizardFrame_24.toString()));

    private JLabel lblDispatchInterval = new JLabel(Messages.AccountWizardFrame_31.toString());
    private JLabel lblDispatchIntervalHelp = new JLabel(String.format("<html>%s</html>",
        Messages.AccountWizardFrame_33.toString()));
    private JLabel lblProcInterval = new JLabel(Messages.AccountWizardFrame_34.toString());
    private JLabel lblProcIntervalHelp = new JLabel(String.format("<html>%s</html>",
        Messages.AccountWizardFrame_36.toString()));
    private JLabel lblReportInterval = new JLabel(Messages.AccountWizardFrame_37.toString());
    private JLabel lblReportIntervalHelp = new JLabel(String.format("<html>%s</html>",
        Messages.AccountWizardFrame_39.toString()));

    private JFormattedTextField txtDispatchInterval = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private JFormattedTextField txtProcInterval = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private JFormattedTextField txtReportInterval = new JFormattedTextField(NumberFormat.getIntegerInstance());

    private JCheckBox chkReportsDisabled = new JCheckBox(Messages.AccountWizardFrame_99.toString());

    private JLabel lblMWSRegHelp = new JLabel(String.format("<html>%s</html>",
        Messages.AccountWizardFrame_41.toString()));
    private JButton btnMWSReg = new JButton(Messages.AccountWizardFrame_42.toString());

    private JTextArea txtValidate = new JTextArea();
    private JScrollPane scrValidate = new JScrollPane(txtValidate);

    private AMTUAccount account = null;
    private ProxyConfig proxy = null;


    public AccountWizardFrame(TransportGuiModel model) {
        setTitle(Messages.AccountWizardFrame_43.toString());
        setModalityType(ModalityType.APPLICATION_MODAL);
        this.model = model;

        init();
        registerComponents();
        setVisible(true);
    }


    /**
     * Initialize and setup all form elements and their values
     */
    private void init() {
        setLayout(new BorderLayout());

        lblSteps.setPreferredSize(TransportGuiFactory.LABELLARGE);
        lblSteps.setOpaque(true);
        lblSteps.setBackground(TransportGuiFactory.AMTUGRAY);
        pnlLogo.setBorder(BorderFactory.createEtchedBorder());
        pnlLogo.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnlLogo.add(Box.createRigidArea(TransportGuiFactory.AREASMALL));
        pnlLogo.add(lblLogo);
        pnlLogo.add(Box.createRigidArea(TransportGuiFactory.AREASMALL));
        pnlLogo.add(lblSteps);
        pnlLogo.add(lblStep1);
        pnlLogo.add(lblStep2);
        pnlLogo.add(lblStep3);
        pnlLogo.setBackground(Color.WHITE);
        pnlLogo.setPreferredSize(new Dimension(215, (int) getPreferredSize().getHeight()));

        btnBack.setPreferredSize(TransportGuiFactory.BUTTONNORMAL);
        btnNext.setPreferredSize(TransportGuiFactory.BUTTONNORMAL);
        btnCancel.setPreferredSize(TransportGuiFactory.BUTTONNORMAL);
        btnAdd.setPreferredSize(TransportGuiFactory.BUTTONXLARGE);
        btnAddRegisteredSite.setPreferredSize(new Dimension(300, 20));

        pnlControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnlControl.add(btnAddRegisteredSite);
        pnlControl.add(Box.createHorizontalStrut(10));
        pnlControl.add(btnAdd);
        pnlControl.add(Box.createHorizontalStrut(10));
        pnlControl.add(btnBack);
        pnlControl.add(btnNext);
        pnlControl.add(Box.createHorizontalStrut(10));
        pnlControl.add(btnCancel);
        pnlControl.add(Box.createHorizontalStrut(3));

        // *********************************
        // set up Welcome panel
        // *********************************

        /* Find unused directory under user.home */
        String strDocumentDirectory = DirectoryUtil.makeAMTUWorkingPath("DocumentTransport");
        File fileDocumentTransport = new File(strDocumentDirectory);
        int count = 1;
        while (fileDocumentTransport.exists()) {
            fileDocumentTransport = new File(strDocumentDirectory + count++);
        }

        RegionUtil.fillComboBoxModel(cboEndpointModel, true);

        txtAlias.setPreferredSize(TransportGuiFactory.TEXTBOXLARGE);
        txtAlias.setMaximumSize(TransportGuiFactory.TEXTBOXLARGE);
        lblAliasHelp.setFont(TransportGuiFactory.HELPTEXT);
        txtDocumentTransport.setPreferredSize(TransportGuiFactory.TEXTBOXLARGE);
        txtDocumentTransport.setMaximumSize(TransportGuiFactory.TEXTBOXLARGE);
        txtDocumentTransport.setEditable(false);
        txtDocumentTransport.setText(fileDocumentTransport.getAbsolutePath());
        btnDocumentTransport.setPreferredSize(TransportGuiFactory.BUTTONSMALL);
        lblDocumentTransportHelp.setFont(TransportGuiFactory.HELPTEXT);
        fcDocumentTransport.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        cboEndpoint.setMaximumSize(TransportGuiFactory.COMBOBOXNORMAL);
        txtDispatchInterval.setValue(Long.valueOf(AccountConfig.MINIMUM_FEED_DISPATCH_INTERVAL));
        txtReportInterval.setValue(Long.valueOf(AccountConfig.MINIMUM_REPORT_RETRIEVAL_INTERVAL));
        txtProcInterval.setValue(Long.valueOf(AccountConfig.MINIMUM_PROCESSING_REPORT_RETRIEVAL_INTERVAL));
        chkReportsDisabled.setSelected(false);

        txtAlias.setAlignmentX(Component.LEFT_ALIGNMENT);
        cboEndpoint.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlFolderButton.setLayout(new BoxLayout(pnlFolderButton, BoxLayout.X_AXIS));
        pnlFolderButton.add(txtDocumentTransport);
        pnlFolderButton.add(btnDocumentTransport);
        pnlFolderButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlWelcome.setBorder(BorderFactory.createTitledBorder(Messages.AccountWizardFrame_45.toString()));
        BoxLayout bl = new BoxLayout(pnlWelcome, BoxLayout.Y_AXIS);
        pnlWelcome.setLayout(bl);
        pnlWelcome.add(lblAlias);
        pnlWelcome.add(lblAliasHelp);
        pnlWelcome.add(Box.createRigidArea(new Dimension(0, 5)));
        pnlWelcome.add(txtAlias);
        pnlWelcome.add(Box.createVerticalGlue());
        pnlWelcome.add(lblEndpoint);
        pnlWelcome.add(lblEndpointHelp);
        pnlWelcome.add(Box.createRigidArea(new Dimension(0, 5)));
        pnlWelcome.add(cboEndpoint);
        pnlWelcome.add(Box.createVerticalGlue());
        pnlWelcome.add(lblDocumentTransport);
        pnlWelcome.add(lblDocumentTransportHelp);
        pnlWelcome.add(Box.createRigidArea(new Dimension(0, 5)));
        pnlWelcome.add(pnlFolderButton);
        pnlWelcome.add(Box.createVerticalGlue());

        // *********************************
        // set up Authentication panel
        // *********************************

        pnlAuthen.setBorder(BorderFactory.createTitledBorder(Messages.AccountWizardFrame_46.toString()));
        pnlAuthen.setLayout(new GridBagLayout());

        lblMwsAccessKey.setPreferredSize(TransportGuiFactory.LABELSMALL);
        lblMwsSecretKey.setPreferredSize(TransportGuiFactory.LABELSMALL);
        lblMerchantId.setPreferredSize(TransportGuiFactory.LABELSMALL);
        txtMwsAccessKey.setPreferredSize(TransportGuiFactory.TEXTBOXNORMAL);
        txtMwsSecretKey.setPreferredSize(TransportGuiFactory.TEXTBOXNORMAL);
        txtMerchantId.setPreferredSize(TransportGuiFactory.TEXTBOXNORMAL);
        lblMwsAccessKeyHelp.setFont(TransportGuiFactory.HELPTEXT);
        lblMwsSecretKeyHelp.setFont(TransportGuiFactory.HELPTEXT);
        lblMerchantIdHelp.setFont(TransportGuiFactory.HELPTEXT);
        lblEndpointHelp.setFont(TransportGuiFactory.HELPTEXT);
        lblMWSRegHelp.setFont(TransportGuiFactory.HELPTEXT);

        // row will keep track of which row of the grid we're currently on.
        int row = 0;

        addUIGroup(pnlAuthen, row, lblMwsAccessKey, txtMwsAccessKey, lblMwsAccessKeyHelp,
            TransportGuiFactory.SMALLSPACERHEIGHT);
        row += ROWS_PER_AUTH_GROUP;

        addUIGroup(pnlAuthen, row, lblMwsSecretKey, txtMwsSecretKey, lblMwsSecretKeyHelp,
            TransportGuiFactory.SMALLSPACERHEIGHT);
        row += ROWS_PER_AUTH_GROUP;

        addUIGroup(pnlAuthen, row, lblMerchantId, txtMerchantId, lblMerchantIdHelp,
            TransportGuiFactory.LARGESPACERHEIGHT);
        row += ROWS_PER_AUTH_GROUP;

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        pnlAuthen.add(lblMWSRegHelp, c);

        c.gridx = 1;
        c.gridy = row + 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        pnlAuthen.add(btnMWSReg, c);
        
        // set up proxy panel
        
        pnlProxy.setBorder(BorderFactory.createTitledBorder(Messages.AccountWizardFrame_106.toString()));
        pnlProxy.setLayout(new GridBagLayout());

        lblProxyHost.setPreferredSize(TransportGuiFactory.LABELSMALL);
        lblProxyPort.setPreferredSize(TransportGuiFactory.LABELSMALL);
        lblProxyUser.setPreferredSize(TransportGuiFactory.LABELSMALL);
        lblProxyPass.setPreferredSize(TransportGuiFactory.LABELSMALL);
        txtProxyHost.setPreferredSize(TransportGuiFactory.TEXTBOXNORMAL);
        txtProxyPort.setPreferredSize(TransportGuiFactory.TEXTBOXNORMAL);
        txtProxyUser.setPreferredSize(TransportGuiFactory.TEXTBOXNORMAL);
        txtProxyPass.setPreferredSize(TransportGuiFactory.TEXTBOXNORMAL);
        lblProxyHostHelp.setFont(TransportGuiFactory.HELPTEXT);
        lblProxyPortHelp.setFont(TransportGuiFactory.HELPTEXT);

        // row will keep track of which row of the grid we're currently on.
        row = 0;

        addUIGroup(pnlAuthen, row, lblProxyHost, txtProxyHost, lblProxyHostHelp,
                TransportGuiFactory.SMALLSPACERHEIGHT);
            row += ROWS_PER_AUTH_GROUP;
            
        addUIGroup(pnlProxy, row, lblProxyHost, txtProxyHost, lblProxyHostHelp,
            TransportGuiFactory.SMALLSPACERHEIGHT);
        row += ROWS_PER_AUTH_GROUP;

        addUIGroup(pnlProxy, row, lblProxyPort, txtProxyPort, lblProxyPortHelp,
            TransportGuiFactory.SMALLSPACERHEIGHT);
        row += ROWS_PER_AUTH_GROUP;

        addUIGroup(pnlProxy, row, lblProxyUser, txtProxyUser, null,
            TransportGuiFactory.SMALLSPACERHEIGHT);
        row += ROWS_PER_AUTH_GROUP;
        
        addUIGroup(pnlProxy, row, lblProxyPass, txtProxyPass, null,
                TransportGuiFactory.SMALLSPACERHEIGHT);
            row += ROWS_PER_AUTH_GROUP;

        // set up the interval panel

        pnlIntervals.setBorder(BorderFactory.createTitledBorder(Messages.AccountWizardFrame_47.toString()));
        pnlIntervals.setLayout(new GridBagLayout());

        lblDispatchInterval.setPreferredSize(TransportGuiFactory.LABELSMALL);
        lblReportInterval.setPreferredSize(TransportGuiFactory.LABELSMALL);
        lblProcInterval.setPreferredSize(TransportGuiFactory.LABELSMALL);
        txtDispatchInterval.setPreferredSize(TransportGuiFactory.TEXTBOXNORMAL);
        txtReportInterval.setPreferredSize(TransportGuiFactory.TEXTBOXNORMAL);
        txtProcInterval.setPreferredSize(TransportGuiFactory.TEXTBOXNORMAL);
        lblDispatchIntervalHelp.setFont(TransportGuiFactory.HELPTEXT);
        lblReportIntervalHelp.setFont(TransportGuiFactory.HELPTEXT);
        lblProcIntervalHelp.setFont(TransportGuiFactory.HELPTEXT);

        row = 0;

        addUIGroup(pnlIntervals, row, lblDispatchInterval, txtDispatchInterval, lblDispatchIntervalHelp,
            TransportGuiFactory.SMALLSPACERHEIGHT);
        row += ROWS_PER_AUTH_GROUP;

        addUIGroup(pnlIntervals, row, lblProcInterval, txtProcInterval, lblProcIntervalHelp,
            TransportGuiFactory.SMALLSPACERHEIGHT);
        row += ROWS_PER_AUTH_GROUP;

        addUIGroup(pnlIntervals, row, chkReportsDisabled, null, null, TransportGuiFactory.SMALLSPACERHEIGHT);
        row += ROWS_PER_AUTH_GROUP;

        addUIGroup(pnlIntervals, row, lblReportInterval, txtReportInterval, lblReportIntervalHelp,
            TransportGuiFactory.LARGESPACERHEIGHT * 2);
        row += ROWS_PER_AUTH_GROUP;

        // finish setting up the main panel

        txtValidate.setEditable(false);
        pnlValidate.setBorder(BorderFactory.createTitledBorder(Messages.AccountWizardFrame_48.toString()));
        pnlValidate.setLayout(new BorderLayout());
        pnlValidate.add(scrValidate, BorderLayout.CENTER);

        pnlCenter.setLayout(layout);
        pnlCenter.add(pnlWelcome, Messages.AccountWizardFrame_49.toString());
        pnlCenter.add(pnlAuthen, Messages.AccountWizardFrame_50.toString());
        pnlCenter.add(pnlIntervals, Messages.AccountWizardFrame_51.toString());
        pnlCenter.add(pnlValidate, Messages.AccountWizardFrame_52.toString());
        pnlCenter.add(pnlProxy, Messages.AccountWizardFrame_107.toString());
        this.add(pnlLogo, BorderLayout.WEST);
        this.add(pnlCenter, BorderLayout.CENTER);
        this.add(pnlControl, BorderLayout.SOUTH);

        setResizable(false);
        setSize(new Dimension(44 * 16, 40 * 9));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                model.shutdownWizard();
            }
        });

        setButtonEnabled();

        /*
         * When the X button is clicked, trigger the click event but do not
         * dispose the frame
         */
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }


    /**
     * Register listeners with form elements
     */
    private void registerComponents() {
        btnNext.addActionListener(new NextButtonListener());
        btnBack.addActionListener(new BackButtonListener());
        btnCancel.addActionListener(new CancelButtonListener());
        btnDocumentTransport.addActionListener(new DocumentTransportButtonListener());
        btnAdd.addActionListener(new AddButtonListener());
        btnAddRegisteredSite.addActionListener(new AddSiteGroupListener());
        btnMWSReg.addActionListener(new MWSButtonListener());
        chkReportsDisabled.addItemListener(new ReportsDisabledListener());

        txtAlias.addFocusListener(new TextFieldFocusListener());
        txtMwsAccessKey.addFocusListener(new TextFieldFocusListener());
        txtMwsSecretKey.addFocusListener(new TextFieldFocusListener());
        txtMerchantId.addFocusListener(new TextFieldFocusListener());
        
        txtProxyHost.addFocusListener(new TextFieldFocusListener());
        txtProxyPort.addFocusListener(new TextFieldFocusListener());
        txtProxyUser.addFocusListener(new TextFieldFocusListener());
        txtProxyPass.addFocusListener(new TextFieldFocusListener());

        txtDispatchInterval.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        txtDispatchInterval.selectAll();
                    }
                });
            }
        });
        txtProcInterval.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        txtProcInterval.selectAll();
                    }
                });
            }
        });
        txtReportInterval.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        txtReportInterval.selectAll();
                    }
                });
            }
        });
    }


    private void addUIGroup(JPanel panel, int row, JComponent label, JComponent txt, JComponent help, int spacerHeight) {
        GridBagConstraints c = new GridBagConstraints();
        // access key
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1;
        if (label != null) {
            panel.add(label, c);
        }

        c.gridx = 1;
        c.weightx = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        if (txt != null) {
            panel.add(txt, c);
        }

        c.gridy = row + 1;
        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 0;
        if (help != null) {
            panel.add(help, c);
        }

        c.gridx = 0;
        c.gridy = row + 2;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        panel.add(Box.createRigidArea(new Dimension(10, spacerHeight)), c);
    }


    private void next() {
        Thread t;
        if (pnlCurrent.equals(pnlWelcome)) {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    btnNext.setEnabled(false);
                    btnBack.setEnabled(false);
                    btnNext.setText(Messages.AccountWizardFrame_53.toString());
                    try {
                        AMTUAccount account = new AMTUAccount();
                        account.setMerchantAlias(txtAlias.getText());
                        account.setMerchantId("-1");
                        account.setDocumentTransport(txtDocumentTransport.getText());
                        account.setMwsEndpoint((MWSEndpoint) cboEndpoint.getSelectedItem());
                        account.validateAccount();
                        account = null;

                        pnlCurrent = pnlIntervals;
                    }
                    catch (MerchantAccountException e) {
                        TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);
                        JOptionPane.showMessageDialog(AccountWizardFrame.this, e.getLocalizedMessage(),
                            Messages.AccountWizardFrame_55.toString(), JOptionPane.INFORMATION_MESSAGE);
                    }
                    btnNext.setEnabled(true);
                    btnBack.setEnabled(true);
                    btnNext.setText(Messages.AccountWizardFrame_5.toString());
                    setButtonEnabled();
                }
            });
            t.start();
        }
        else if (pnlCurrent.equals(pnlIntervals)) {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    btnNext.setEnabled(false);
                    btnBack.setEnabled(false);
                    btnNext.setText(Messages.AccountWizardFrame_53.toString());
                    try {
                        int dispatchInterval = ((Long) txtDispatchInterval.getValue()).intValue();
                        int reportInterval = ((Long) txtReportInterval.getValue()).intValue();
                        boolean reportsDisabled = chkReportsDisabled.isSelected();
                        int procInterval = ((Long) txtProcInterval.getValue()).intValue();

                        AMTUAccount account = new AMTUAccount();
                        account.setMerchantAlias(txtAlias.getText());
                        account.setMerchantId("-1");
                        account.setDocumentTransport(txtDocumentTransport.getText());
                        account.setMwsEndpoint((MWSEndpoint) cboEndpoint.getSelectedItem());
                        account.setConfigValue(AccountConfig.FEED_DISPATCH_INTERVAL, Integer.toString(dispatchInterval));
                        account.setConfigValue(AccountConfig.REPORT_RETRIEVAL_INTERVAL,
                            Integer.toString(reportInterval));
                        account.setConfigValue(AccountConfig.PROCESSING_REPORT_RETRIEVAL_INTERVAL,
                            Integer.toString(procInterval));
                        account.setConfigValue(AccountConfig.REPORTS_DISABLED,
                            reportsDisabled ? AccountConfig.BOOLEAN_TRUE : AccountConfig.BOOLEAN_FALSE);
                        account.validateAccount();
                        account = null;

                        pnlCurrent = pnlAuthen;
                    }
                    catch (MerchantAccountException e) {
                        TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);
                        JOptionPane.showMessageDialog(AccountWizardFrame.this, e.getLocalizedMessage(),
                            Messages.AccountWizardFrame_55.toString(), JOptionPane.INFORMATION_MESSAGE);
                    }
                    btnNext.setEnabled(true);
                    btnBack.setEnabled(true);
                    btnNext.setText(Messages.AccountWizardFrame_5.toString());
                    setButtonEnabled();
                }
            });
            t.start();
        }
        else if (pnlCurrent.equals(pnlAuthen)) {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    btnNext.setEnabled(false);
                    btnBack.setEnabled(false);
                    btnNext.setText(Messages.AccountWizardFrame_53.toString());
                    
                    try {
                        int dispatchInterval = ((Long) txtDispatchInterval.getValue()).intValue();
                        int reportInterval = ((Long) txtReportInterval.getValue()).intValue();
                        boolean reportsDisabled = chkReportsDisabled.isSelected();
                        int procInterval = ((Long) txtProcInterval.getValue()).intValue();
                        
                        proxy = null;
                        
                        // Check to see if there are existing proxy configurations
                        try {
                            proxy = ProxyConfig.pullProxyConfiguration();
                        } catch (DatabaseException e1) {
                            proxy = null;
                            TransportLogger.getSysAuditLogger().info(
                                Messages.ProxyConfig_4.toString());
                        } catch (SQLException e1) {
                            proxy = null;
                            TransportLogger.getSysAuditLogger().info(
                                    Messages.ProxyConfig_4.toString());
                        }

                        if(proxy == null) {
                            try {
                                proxy = new ProxyConfig(System.getProperty("https.proxyHost").toString(), 
                                        Integer.valueOf(System.getProperty("https.proxyPort").toString()));
                            } catch(Exception e) {
                                proxy = null;
                            }
                        }
                        
                        account = new AMTUAccount();
                        account.setMerchantAlias(txtAlias.getText());
                        account.setDocumentTransport(txtDocumentTransport.getText());
                        account.setMwsEndpoint((MWSEndpoint) cboEndpoint.getSelectedItem());
                        account.setConfigValue(AccountConfig.FEED_DISPATCH_INTERVAL, Integer.toString(dispatchInterval));
                        account.setConfigValue(AccountConfig.REPORT_RETRIEVAL_INTERVAL,
                            Integer.toString(reportInterval));
                        account.setConfigValue(AccountConfig.PROCESSING_REPORT_RETRIEVAL_INTERVAL,
                            Integer.toString(procInterval));
                        account.setConfigValue(AccountConfig.REPORTS_DISABLED,
                            reportsDisabled ? AccountConfig.BOOLEAN_TRUE : AccountConfig.BOOLEAN_FALSE);
                        account.setMwsAccessKey(txtMwsAccessKey.getText().trim().toUpperCase());
                        account.setMwsSecretKey(new String(txtMwsSecretKey.getPassword()).trim());
                        account.setMerchantId(txtMerchantId.getText().trim().toUpperCase());

                        account.validateAccount();
                        account.validateCredentials(proxy);
                        account = null;
                        pnlCurrent = pnlValidate;
                    }
                    catch (MarketplaceWebServiceException e) {
                        if(e.getStatusCode() != -1) {
                            TransportLogger.getSysErrorLogger()
                                .error(
                                    String.format(Messages.MarketplaceWebServiceException_0.toString(),
                                        e.getLocalizedMessage()), e);
                            JOptionPane.showMessageDialog(AccountWizardFrame.this, String.format(
                                Messages.MarketplaceWebServiceException_0.toString(), e.getLocalizedMessage()),
                                Messages.AccountWizardFrame_55.toString(), JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            TransportLogger.getSysErrorLogger()
                            .error(
                                String.format(Messages.MarketplaceWebServiceException_0.toString(),
                                    e.getLocalizedMessage()), e);
                            int setupProxy = JOptionPane.showConfirmDialog(AccountWizardFrame.this, String.format(
                                Messages.MarketplaceWebServiceException_0.toString(), e.getLocalizedMessage() + 
                                "\n" + Messages.AccountWizardFrame_101.toString()),
                                Messages.AccountWizardFrame_55.toString(), JOptionPane.YES_NO_OPTION);
                            
                            if(setupProxy == JOptionPane.YES_OPTION) {
                                pnlCurrent = pnlProxy;
                            }
                        }
                    }
                    catch (MerchantAccountException e) {
                        TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);
                        JOptionPane.showMessageDialog(AccountWizardFrame.this, e.getLocalizedMessage(),
                            Messages.AccountWizardFrame_55.toString(), JOptionPane.INFORMATION_MESSAGE);
                    }
                    btnNext.setEnabled(true);
                    btnBack.setEnabled(true);
                    btnNext.setText(Messages.AccountWizardFrame_5.toString());
                    setButtonEnabled();
                }
            });
            t.start();
        }
        else if(pnlCurrent.equals(pnlProxy)) {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    btnNext.setEnabled(false);
                    btnBack.setEnabled(false);
                    btnNext.setText(Messages.AccountWizardFrame_53.toString());

                    try {
                        proxy = new ProxyConfig();
                        proxy.setHost(txtProxyHost.getText().trim());
                        try {
                            proxy.setPort(Integer.valueOf(txtProxyPort.getText().trim()));
                        } catch(NumberFormatException e) {
                            throw new ProxyException(Messages.AccountWizardFrame_116.toString());
                        }
                        proxy.setUsername(txtProxyUser.getText().trim());
                        proxy.setPassword((new String(txtProxyPass.getPassword())).trim());

                        account.validateAccount();
                        account.validateCredentials(proxy);
                        account = null;

                        pnlCurrent = pnlValidate;
                    }
                    catch (ProxyException e) {
                        JOptionPane.showMessageDialog(AccountWizardFrame.this, e.getLocalizedMessage(),
                            Messages.AccountWizardFrame_55.toString(), JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (MarketplaceWebServiceException e) {
                        if(e.getStatusCode() != -1) {
                            TransportLogger.getSysErrorLogger()
                                .error(
                                    String.format(Messages.MarketplaceWebServiceException_0.toString(),
                                        e.getLocalizedMessage()), e);
                            JOptionPane.showMessageDialog(AccountWizardFrame.this, String.format(
                                Messages.MarketplaceWebServiceException_0.toString(), e.getLocalizedMessage()),
                                Messages.AccountWizardFrame_55.toString(), JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            TransportLogger.getSysErrorLogger()
                            .error(
                                String.format(Messages.MarketplaceWebServiceException_0.toString(),
                                    e.getLocalizedMessage()), e);
                            JOptionPane.showMessageDialog(AccountWizardFrame.this, String.format(
                                Messages.MarketplaceWebServiceException_0.toString(), e.getLocalizedMessage() + 
                                "\n" + Messages.AccountWizardFrame_108.toString()),
                                Messages.AccountWizardFrame_55.toString(), JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    catch (MerchantAccountException e) {
                        TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);
                        JOptionPane.showMessageDialog(AccountWizardFrame.this, e.getLocalizedMessage(),
                            Messages.AccountWizardFrame_55.toString(), JOptionPane.INFORMATION_MESSAGE);
                    }
                    btnNext.setEnabled(true);
                    btnBack.setEnabled(true);
                    btnNext.setText(Messages.AccountWizardFrame_5.toString());
                    setButtonEnabled();
                }
            });
            t.start();
        }
    }


    private void back() {
        if (pnlCurrent.equals(pnlIntervals)) {
            pnlCurrent = pnlWelcome;
        }
        else if (pnlCurrent.equals(pnlAuthen)) {
            pnlCurrent = pnlIntervals;
        }
        else if (pnlCurrent.equals(pnlValidate)) {
            pnlCurrent = pnlAuthen;
        }
        else if (pnlCurrent.equals(pnlProxy)) {
            pnlCurrent = pnlAuthen;
        }
        setButtonEnabled();
    }


    private void setButtonEnabled() {
        if (pnlCurrent.equals(pnlWelcome)) {
            btnNext.setEnabled(true);
            btnBack.setEnabled(false);
            btnAdd.setEnabled(false);
            btnAdd.setVisible(false);
            btnAddRegisteredSite.setEnabled(false);
            btnAddRegisteredSite.setVisible(false);
            layout.show(pnlCenter, Messages.AccountWizardFrame_49.toString());
        }
        else if (pnlCurrent.equals(pnlIntervals)) {
            btnNext.setEnabled(true);
            btnBack.setEnabled(true);
            btnAdd.setEnabled(false);
            btnAdd.setVisible(false);
            btnAddRegisteredSite.setEnabled(false);
            btnAddRegisteredSite.setVisible(false);
            layout.show(pnlCenter, Messages.AccountWizardFrame_51.toString());
        }
        else if (pnlCurrent.equals(pnlAuthen)) {
            btnNext.setEnabled(true);
            btnBack.setEnabled(true);
            btnAdd.setEnabled(false);
            btnAdd.setVisible(false);
            btnAddRegisteredSite.setEnabled(false);
            btnAddRegisteredSite.setVisible(false);
            layout.show(pnlCenter, Messages.AccountWizardFrame_50.toString());
        }
        else if (pnlCurrent.equals(pnlProxy)) {
            btnNext.setEnabled(true);
            btnBack.setEnabled(true);
            btnAdd.setEnabled(false);
            btnAdd.setVisible(false);
            btnAddRegisteredSite.setEnabled(false);
            btnAddRegisteredSite.setVisible(false);
            layout.show(pnlCenter, Messages.AccountWizardFrame_107.toString());
        }
        else {
            btnNext.setEnabled(false);
            btnBack.setEnabled(false);
            btnNext.setVisible(false);
            btnBack.setVisible(false);
            btnAdd.setEnabled(false);
            btnAdd.setVisible(true);
            btnAddRegisteredSite.setEnabled(false);
            btnAddRegisteredSite.setVisible(true);
            layout.show(pnlCenter, Messages.AccountWizardFrame_52.toString());
            Thread t = new Thread(new Runnable() {
                public void run() {
                    txtValidate.setText("");
                    txtValidate.append(Messages.AccountWizardFrame_72.toString() + "\n");

                    try {
                        int dispatchInterval = ((Long) txtDispatchInterval.getValue()).intValue();
                        int reportInterval = ((Long) txtReportInterval.getValue()).intValue();
                        boolean reportsDisabled = chkReportsDisabled.isSelected();
                        int procInterval = ((Long) txtProcInterval.getValue()).intValue();

                        account = new AMTUAccount();
                        account.setMerchantAlias(txtAlias.getText());
                        account.setDocumentTransport(txtDocumentTransport.getText());
                        account.setMwsEndpoint((MWSEndpoint) cboEndpoint.getSelectedItem());
                        account.setConfigValue(AccountConfig.FEED_DISPATCH_INTERVAL, Integer.toString(dispatchInterval));
                        account.setConfigValue(AccountConfig.REPORT_RETRIEVAL_INTERVAL,
                            Integer.toString(reportInterval));
                        account.setConfigValue(AccountConfig.PROCESSING_REPORT_RETRIEVAL_INTERVAL,
                            Integer.toString(procInterval));
                        account.setConfigValue(AccountConfig.REPORTS_DISABLED,
                            reportsDisabled ? AccountConfig.BOOLEAN_TRUE : AccountConfig.BOOLEAN_FALSE);
                        account.setMwsAccessKey(txtMwsAccessKey.getText().trim().toUpperCase());
                        account.setMwsSecretKey(new String(txtMwsSecretKey.getPassword()).trim());
                        account.setMerchantId(txtMerchantId.getText().trim().toUpperCase());
                        account.validateAccount();
                        account.validateCredentials(proxy);

                        txtValidate.append(Messages.AccountWizardFrame_77.toString() + "\n");

                        // Save merchant account

                        model.saveMerchantAccount(account);
                        
                        // Save proxy
                        if(proxy != null)
                            proxy.save();
                        
                        txtValidate.append(Messages.AccountWizardFrame_80.toString() + "\n");
                        txtValidate.append(Messages.AccountWizardFrame_82.toString() + "\n");

                        /* Change button text */
                        btnCancel.setText(Messages.AccountWizardFrame_84.toString());
                        btnAdd.setEnabled(true);
                        btnAddRegisteredSite.setEnabled(true);
                    }
                    catch (MarketplaceWebServiceException e) {
                        TransportLogger.getSysErrorLogger()
                            .error(
                                String.format(Messages.MarketplaceWebServiceException_0.toString(),
                                    e.getLocalizedMessage()), e);
                        txtValidate.append(Messages.AccountWizardFrame_88.toString() + "\n" + e.getLocalizedMessage());
                        TransportLogger.getSysErrorLogger().error(Messages.AccountWizardFrame_88.toString(), e);
                    }
                    catch (MerchantAccountException e) {
                        TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);
                        txtValidate.append(Messages.AccountWizardFrame_91.toString() + "\n");
                        TransportLogger.getSysErrorLogger().error(Messages.AccountWizardFrame_91.toString(), e);
                    }
                    catch (IOException e) {
                        TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);
                        txtValidate.append(Messages.AccountWizardFrame_94.toString() + "\n");
                        TransportLogger.getSysErrorLogger().error(Messages.AccountWizardFrame_94.toString(), e);
                    }
                    catch (ProxyException e) {
                        TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);
                        txtValidate.append(Messages.AccountWizardFrame_109.toString() + "\n");
                        TransportLogger.getSysErrorLogger().error(Messages.AccountWizardFrame_109.toString(), e);
                    }
                }
            });
            t.start();
        }
    }

    private class ReportsDisabledListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            txtReportInterval.setEnabled(!chkReportsDisabled.isSelected());
        }
    }

    private class NextButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            next();
        }
    }

    private class BackButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            back();
        }
    }

    private class CancelButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            if (model.getAccountList() != null && model.getAccountList().size() > 0) {
                model.startFrame();
                dispose();
                model.shutdownWizard();
            }
            else {
                JOptionPane.showMessageDialog(AccountWizardFrame.this, Messages.AccountManagementView_29.toString(),
                    Messages.AccountManagementView_30.toString(), JOptionPane.INFORMATION_MESSAGE);
                // Forced shutdown
                model.shutdown(true);
            }
        }
    }

    private class AddButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            model.startFrame();
            dispose();
            new AccountWizardFrame(model);
        }
    }

    private class AddSiteGroupListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            model.showAddRegisteredSiteWizard();
        }
    }

    private class MWSButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            MWSEndpoint endpoint = (MWSEndpoint) cboEndpointModel.getSelectedItem();
            BrowserUtil.openURLInBrowser(endpoint.getMwsRegistrationURL());
        }
    }

    private class DocumentTransportButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            int returnVal = fcDocumentTransport.showOpenDialog(AccountWizardFrame.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                if (fcDocumentTransport.getSelectedFile().isDirectory()) {
                    txtDocumentTransport.setText(fcDocumentTransport.getSelectedFile().getAbsolutePath());
                }
                else {
                    JOptionPane.showMessageDialog(AccountWizardFrame.this, Messages.AccountWizardFrame_98.toString(),
                        Messages.AccountWizardFrame_55.toString(), JOptionPane.INFORMATION_MESSAGE);
                }
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
}