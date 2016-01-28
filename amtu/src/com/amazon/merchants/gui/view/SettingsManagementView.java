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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import com.amazon.merchants.Messages;
import com.amazon.merchants.account.AMTUAccount;
import com.amazon.merchants.account.MerchantAccountException;
import com.amazon.merchants.gui.model.TransportGuiModel;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.system.ProxyConfig;
import com.amazon.merchants.system.ProxyException;
import com.amazonaws.mws.MarketplaceWebServiceException;

public class SettingsManagementView extends JPanel implements UpdateViewInterface {
    private static final long serialVersionUID = 3455217954000244641L;

    private TransportGuiModel model;

    private JButton btnCancelModifyProxySetting;
    private JButton btnDeleteProxySetting;
    private JButton btnModifyProxySetting;
    private JButton btnSaveProxySetting;
    private JButton btnTestProxy;
    private JLabel lblProxyHost;
    private JLabel lblProxyUsername;
    private JLabel lblProxyPassword;
    private JLabel lblProxyPort;
    private JPanel pnlAccountSettings;
    private JPanel pnlProxySettings;
    private JTextField txtProxyHost;
    private JTextField txtProxyUsername;
    private JPasswordField txtProxyPassword;
    private JTextField txtProxyPort;

    private enum EditMode {
        RO, EDIT;
    }

    private EditMode accountMode = EditMode.RO;

    public SettingsManagementView(TransportGuiModel model) {
        this.model = model;

        initComponents();
        registerComponents();

        this.model.addView(this);
    }


    private void initComponents() {
        pnlAccountSettings = new JPanel();
        pnlProxySettings = new JPanel();
        lblProxyHost = new JLabel();
        lblProxyPort = new JLabel();
        lblProxyUsername = new JLabel();
        lblProxyPassword = new JLabel();
        txtProxyHost = new JTextField();
        txtProxyPort = new JTextField();
        txtProxyUsername = new JTextField();
        txtProxyPassword = new JPasswordField();
        btnModifyProxySetting = new JButton();
        btnSaveProxySetting = new JButton();
        btnDeleteProxySetting = new JButton();
        btnCancelModifyProxySetting = new JButton();
        btnTestProxy = new JButton();

        setAutoscrolls(true);

        pnlAccountSettings.setBorder(BorderFactory.createTitledBorder(Messages.SettingsManagementView_0.toString()));

        pnlProxySettings.setBorder(BorderFactory.createTitledBorder(Messages.SettingsManagementView_1.toString()));

        lblProxyHost.setText(Messages.SettingsManagementView_2.toString());

        lblProxyPort.setText(Messages.SettingsManagementView_3.toString());

        lblProxyUsername.setText(Messages.SettingsManagementView_4.toString());
        
        lblProxyPassword.setText(Messages.SettingsManagementView_5.toString());

        txtProxyHost.setToolTipText(Messages.SettingsManagementView_6.toString());

        txtProxyPort.setToolTipText(Messages.SettingsManagementView_7.toString());

        txtProxyUsername.setToolTipText(Messages.SettingsManagementView_8.toString());
        
        txtProxyPassword.setToolTipText(Messages.SettingsManagementView_9.toString());

        GroupLayout pnlMWSAuthLayout = new GroupLayout(pnlProxySettings);
        pnlProxySettings.setLayout(pnlMWSAuthLayout);
        pnlMWSAuthLayout.setHorizontalGroup(
            pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlMWSAuthLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMWSAuthLayout.createSequentialGroup()
                        .addGroup(pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(lblProxyHost)
                            .addComponent(lblProxyPort)
                            .addComponent(lblProxyUsername)
                            .addComponent(lblProxyPassword))
                        .addGap(52, 52, 52)
                        .addGroup(pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(txtProxyPort, GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                            .addComponent(txtProxyHost, GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                            .addComponent(txtProxyUsername, GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                            .addComponent(txtProxyPassword, GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE))))
                .addContainerGap())
        );
        pnlMWSAuthLayout.setVerticalGroup(
            pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlMWSAuthLayout.createSequentialGroup()
                .addGroup(pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProxyHost, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProxyHost))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProxyPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProxyPort))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProxyUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProxyUsername))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMWSAuthLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProxyPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProxyPassword))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnSaveProxySetting.setText(Messages.SettingsManagementView_18.toString());
        btnSaveProxySetting.setToolTipText(Messages.SettingsManagementView_22.toString());

        btnModifyProxySetting.setText(Messages.SettingsManagementView_19.toString());
        btnModifyProxySetting.setToolTipText(Messages.SettingsManagementView_23.toString());

        btnDeleteProxySetting.setText(Messages.SettingsManagementView_20.toString());
        btnDeleteProxySetting.setToolTipText(Messages.SettingsManagementView_24.toString());

        btnCancelModifyProxySetting.setText(Messages.SettingsManagementView_21.toString());
        btnCancelModifyProxySetting.setToolTipText(Messages.SettingsManagementView_25.toString());

        btnTestProxy.setText(Messages.SettingsManagementView_16.toString());
        btnTestProxy.setToolTipText(Messages.SettingsManagementView_26.toString());

        GroupLayout pnlAccountSettingsLayout = new GroupLayout(pnlAccountSettings);
        pnlAccountSettings.setLayout(pnlAccountSettingsLayout);
        pnlAccountSettingsLayout.setHorizontalGroup(
            pnlAccountSettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlAccountSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAccountSettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(pnlProxySettings, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING, pnlAccountSettingsLayout.createSequentialGroup()
                        .addComponent(btnTestProxy, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelModifyProxySetting, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSaveProxySetting, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteProxySetting, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModifyProxySetting, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlAccountSettingsLayout.setVerticalGroup(
            pnlAccountSettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlAccountSettingsLayout.createSequentialGroup()
                .addGroup(pnlAccountSettingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlProxySettings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAccountSettingsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnModifyProxySetting)
                    .addComponent(btnSaveProxySetting)
                    .addComponent(btnDeleteProxySetting)
                    .addComponent(btnCancelModifyProxySetting)
                    .addComponent(btnTestProxy))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(pnlAccountSettings, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlAccountSettings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }


    private void registerComponents() {
        btnModifyProxySetting.addActionListener(new ModifyProxyButtonListener());
        btnSaveProxySetting.addActionListener(new SaveProxyButtonListener());
        btnTestProxy.addActionListener(new TestProxyButtonListener());
        btnDeleteProxySetting.addActionListener(new DeleteProxyButtonListener());
        btnCancelModifyProxySetting.addActionListener(new CancelModifyProxyButtonListener());
        txtProxyHost.addFocusListener(new TextFieldFocusListener());
        txtProxyUsername.addFocusListener(new TextFieldFocusListener());
        txtProxyPort.addFocusListener(new TextFieldFocusListener());
    }


    private void changeProxyMode(EditMode mode) {
        accountMode = mode;
        switch (accountMode) {
            case RO:
                txtProxyHost.setEnabled(false);
                txtProxyPort.setEnabled(false);
                txtProxyUsername.setEnabled(false);
                txtProxyPassword.setEnabled(false);

                btnTestProxy.setEnabled(false);
                btnTestProxy.setVisible(false);
                btnDeleteProxySetting.setEnabled(true);
                btnDeleteProxySetting.setVisible(true);
                btnSaveProxySetting.setEnabled(false);
                btnSaveProxySetting.setVisible(false);
                btnModifyProxySetting.setEnabled(true);
                btnModifyProxySetting.setVisible(true);
                btnCancelModifyProxySetting.setEnabled(false);
                btnCancelModifyProxySetting.setVisible(false);
                break;
            case EDIT:
                txtProxyHost.setEnabled(true);
                txtProxyPort.setEnabled(true);
                txtProxyUsername.setEnabled(true);
                txtProxyPassword.setEnabled(true);

                btnTestProxy.setEnabled(true);
                btnTestProxy.setVisible(true);
                btnDeleteProxySetting.setEnabled(false);
                btnDeleteProxySetting.setVisible(false);
                btnSaveProxySetting.setEnabled(true);
                btnSaveProxySetting.setVisible(true);
                btnModifyProxySetting.setEnabled(false);
                btnModifyProxySetting.setVisible(false);

                btnCancelModifyProxySetting.setEnabled(true);
                btnCancelModifyProxySetting.setVisible(true);

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
    }

    @Override
    public void updateProxy() {
        
        boolean hasProxy = true;
        ProxyConfig proxy = null;
        
        try {
            proxy = model.getCurrentProxy();
            if(proxy == null) 
                hasProxy = false;
        } catch (ProxyException e) {
            hasProxy = false;
        }
        
        if(hasProxy) {
            txtProxyHost.setText(proxy.getHost());
            txtProxyPort.setText(Integer.toString(proxy.getPort()));
            txtProxyUsername.setText(proxy.getUsername());
            txtProxyPassword.setText(proxy.getPassword());
        } else {
            txtProxyHost.setText("");
            txtProxyPort.setText("");
            txtProxyUsername.setText("");
            txtProxyPassword.setText("");
        }
        
        changeProxyMode(EditMode.RO);
    }

    private class ModifyProxyButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            changeProxyMode(EditMode.EDIT);
        }
    }

    private class CancelModifyProxyButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                        updateProxy();
                        btnSaveProxySetting.setText(Messages.SettingsManagementView_18.toString());
                        btnSaveProxySetting.setEnabled(true);
                }
            });
            t.start();
        }
    }

    private class SaveProxyButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        btnSaveProxySetting.setEnabled(false);
                        btnSaveProxySetting.setText(Messages.SettingsManagementView_17.toString());

                        try {
                            String proxyHost = txtProxyHost.getText().trim().toLowerCase();
                            if(!proxyHost.equals("")) {
                                int proxyPort = Integer.valueOf(txtProxyPort.getText().trim());
                                String proxyUser = txtProxyUsername.getText().trim();
                                String proxyPass = new String(txtProxyPassword.getPassword());
                                
                                ProxyConfig proxy = new ProxyConfig(proxyHost, proxyPort, proxyUser, proxyPass);
                                
                                // Save the proxy configuration to the database
                                if(proxy != null)
                                    proxy.save();
                                
                                // Gray out the form
                                changeProxyMode(EditMode.RO);
                            } else {
                                JOptionPane.showMessageDialog(SettingsManagementView.this,
                                    Messages.SettingsManagementView_29.toString(), Messages.SettingsManagementView_28.toString(),
                                    JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(SettingsManagementView.this,
                                Messages.SettingsManagementView_29.toString(), Messages.SettingsManagementView_28.toString(),
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    catch (ProxyException e) {
                        TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);
                        JOptionPane.showMessageDialog(SettingsManagementView.this, e.getLocalizedMessage(),
                            Messages.SettingsManagementView_15.toString(), JOptionPane.INFORMATION_MESSAGE);
                    }
                    finally {
                        btnSaveProxySetting.setText(Messages.SettingsManagementView_18.toString());
                        btnSaveProxySetting.setEnabled(true);
                    }
                }
            });
            t.start();
        }
    }

    private class TestProxyButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        btnTestProxy.setEnabled(false);
                        btnTestProxy.setText(Messages.SettingsManagementView_12.toString());

                        AMTUAccount account = model.getCurrentAccount();
                        
                        try {
                            String proxyHost = txtProxyHost.getText().trim().toLowerCase();
                            if(!proxyHost.equals("")) {
                                int proxyPort = Integer.valueOf(txtProxyPort.getText().trim());
                                String proxyUser = txtProxyUsername.getText().trim();
                                String proxyPass = new String(txtProxyPassword.getPassword());
                                ProxyConfig temp = new ProxyConfig(proxyHost, proxyPort, proxyUser, proxyPass);
                                
                                // Call MWS with given information
                                model.validateCredentials(account,temp);
                                
                                // Show success message
                                JOptionPane.showMessageDialog(SettingsManagementView.this,
                                    Messages.SettingsManagementView_13.toString(), Messages.SettingsManagementView_14.toString(),
                                    JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(SettingsManagementView.this,
                                    Messages.SettingsManagementView_29.toString(), Messages.SettingsManagementView_28.toString(),
                                    JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(SettingsManagementView.this,
                                Messages.SettingsManagementView_29.toString(), Messages.SettingsManagementView_28.toString(),
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    catch (MarketplaceWebServiceException e) {
                        TransportLogger.getSysErrorLogger()
                            .error(
                                String.format(Messages.MarketplaceWebServiceException_0.toString(),
                                    e.getLocalizedMessage()), e);
                        JOptionPane.showMessageDialog(SettingsManagementView.this, String.format(
                            Messages.MarketplaceWebServiceException_0.toString(), e.getLocalizedMessage()),
                            Messages.SettingsManagementView_15.toString(), JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (MerchantAccountException e) {
                        JOptionPane.showMessageDialog(SettingsManagementView.this, e.getLocalizedMessage(),
                            Messages.SettingsManagementView_15.toString(), JOptionPane.INFORMATION_MESSAGE);
                    }
                    finally {
                        btnTestProxy.setText(Messages.SettingsManagementView_16.toString());
                        btnTestProxy.setEnabled(true);
                    }
                }
            });
            t.start();
        }
    }

    private class DeleteProxyButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            try {
                String msg = Messages.SettingsManagementView_10.toString();
                String title = Messages.SettingsManagementView_11.toString();
                int ans = JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.YES_NO_OPTION);

                if (ans == JOptionPane.NO_OPTION) {
                    return;
                }

                model.deleteProxyConfig();
                updateProxy();
            }
            catch (ProxyException e) {
                TransportLogger.getSysErrorLogger().error(e.getLocalizedMessage(), e);

                // No proxy configuration has been set
                JOptionPane.showMessageDialog(SettingsManagementView.this, Messages.SettingsManagementView_27.toString(),
                    Messages.SettingsManagementView_28.toString(), JOptionPane.INFORMATION_MESSAGE);
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

    @Override
    public void updateSiteGroup() {
        // TODO Auto-generated method stub
        
    }
}