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

package com.amazon.merchants.daemon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amazon.merchants.Messages;
import com.amazon.merchants.account.AMTUAccount;
import com.amazon.merchants.account.MerchantAccountException;
import com.amazon.merchants.account.MerchantAccountManager;
import com.amazon.merchants.exception.MerchantException;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.system.ProxyConfig;
import com.amazon.merchants.system.ProxyException;
import com.amazon.merchants.util.file.IO;
import com.amazonaws.mws.MarketplaceWebServiceException;

public class TransportConfigureUtil {
    private static final String ROOT = AMTUAccount.XML_ELEMENTS.accounts
            .toString();


    private static List<AMTUAccount> parse(String filename)
            throws ParserConfigurationException, SAXException, IOException,
            MerchantException, MarketplaceWebServiceException {
        if (filename == null) {
            throw new IllegalArgumentException(
                    Messages.TransportConfigureUtil_5.toString());
        }
        File file = new File(filename);
        if (!file.exists() && !file.isFile()) {
            throw new MerchantException(
                    Messages.TransportConfigureUtil_2.toString());
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);

        NodeList list = doc
                .getElementsByTagName(AMTUAccount.XML_ELEMENTS.account
                        .toString());

        List<AMTUAccount> accounts = new ArrayList<AMTUAccount>();
        for (int i = 0; i < list.getLength(); i++) {
            Element element = (Element) list.item(i);

            AMTUAccount account = null;
            try {
                account = AMTUAccount.fromXML(element);
            }
            catch (Exception e) {
                throw new MerchantAccountException(String.format(
                        Messages.TransportConfigureUtil_9.toString(), (i + 1))
                        + " " + e.getLocalizedMessage()); //$NON-NLS-1$
            }

            if (account == null) {
                TransportLogger.getSysAuditLogger().warn(
                        String.format(Messages.TransportConfigureUtil_10
                                .toString(), element
                                .getAttribute(AMTUAccount.XML_ELEMENTS.name
                                        .toString())));
            }
            else {
                accounts.add(account);
            }
        }

        return accounts;
    }


    public static void setupMerchantAccounts(String filename)
            throws ParserConfigurationException, SAXException, IOException,
            MerchantException, MarketplaceWebServiceException, SQLException {

        ProxyConfig proxy = ProxyConfig.pullProxyConfiguration();

        List<AMTUAccount> list = parse(filename);
        int count = 0;
        for (AMTUAccount account : list) {
            Object[] messageArguments = { new Integer(++count) };
            MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
            formatter
                    .applyPattern(Messages.TransportConfigureUtil_6.toString());
            TransportLogger.getSysAuditLogger().info(
                    formatter.format(messageArguments));

            if (account.readyToDelete()) {
                deleteMerchantAccount(account);
            }
            else {
                saveMerchantAccount(account, proxy);
            }
        }
    }


    private static void saveMerchantAccount(AMTUAccount account, ProxyConfig proxy)
            throws MerchantAccountException, MarketplaceWebServiceException {
        account.validateAccount();
        account.validateCredentials(proxy);
        account.save();
    }


    private static void deleteMerchantAccount(AMTUAccount account)
            throws MerchantAccountException {
        account.delete();
    }


    /**
     * Write manual content to a given output stream
     *
     * @param out
     */
    public static void printManual(OutputStream out) {
        final String filename = "xml" + File.separator + "configure.txt"; //$NON-NLS-1$ //$NON-NLS-2$

        OutputStreamWriter writer = new OutputStreamWriter(out);
        File manual = new File(filename);
        if (manual.exists() && manual.isFile()) {
            try {
                BufferedReader input = new BufferedReader(
                        new FileReader(manual));
                try {
                    String line = null;
                    while ((line = input.readLine()) != null) {
                        writer.write(line + '\n');
                    }
                    writer.flush(); // Flush content to output stream
                }
                finally {
                    input.close();
                }
            }
            catch (IOException e) {
                TransportLogger.getSysErrorLogger().error(
                        Messages.TransportConfigureUtil_7.toString(), e);
            }
        }
        else {
            try {
                writer.write(Messages.TransportConfigureUtil_4.toString());
                writer.flush(); // Flush content to output stream
            }
            catch (IOException e) {
                TransportLogger.getSysErrorLogger().error(
                        Messages.TransportConfigureUtil_7.toString(), e);
            }
        }
    }


    public static void exportMerchantAccounts(String filename)
            throws MerchantAccountException, IOException,
            ParserConfigurationException, TransformerException {
        boolean needToCloseOS = false;
        OutputStream os = null;

        try {
            MerchantAccountManager.setupMerchantAccounts();
            List<AMTUAccount> accountList = MerchantAccountManager
                    .getAccountList();

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory
                    .newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement(ROOT);
            document.appendChild(rootElement);
            for (AMTUAccount account : accountList) {
                rootElement.appendChild(account.toXML(document));
            }

            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);

            if (filename == null) {
                os = System.out;
            }
            else {
                needToCloseOS = true;
                File outFile = new File(filename);
                if (outFile.exists()) {
                    TransportLogger.getSysAuditLogger().info(
                            String.format(Messages.TransportConfigureUtil_8
                                    .toString(), filename));
                    return;
                }
                os = new FileOutputStream(outFile);
            }

            StreamResult result = new StreamResult(os);
            transformer.transform(source, result);
        }
        finally {
            if (needToCloseOS) {
                IO.closeSilently(os);
            }
        }
    }
    
    /**
     * Returns the value of a given node with a given name
     * 
     * @param tag name of the given XML element
     * @param element DOM object
     * @return string representation of the value at the given tag
     * @throws Exception for any error while trying to retrieve the value
     */
    private static String getTagValue(String tag, Element element) throws Exception {
        NodeList list = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node value = (Node) list.item(0);
        return value.getNodeValue();
    }
    
    /**
     * Parses the XML file for proxy details
     * 
     * @param filename XML proxy file
     * @return ProxyConfig object with parameters from XML file
     * @throws IOException
     * @throws MerchantException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws ProxyException 
     */
    public static ProxyConfig parseProxy(String filename) 
            throws IOException, MerchantException, ParserConfigurationException, SAXException, ProxyException {
        if (filename == null) {
            throw new IllegalArgumentException(
                    Messages.TransportConfigureUtil_5.toString());
        }
        File file = new File(filename);
        if (!file.exists() && !file.isFile()) {
            throw new ProxyException(
                    Messages.TransportConfigureUtil_2.toString());
        }
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        
        NodeList list = doc.getElementsByTagName("proxy");
        
        ProxyConfig proxy = null;
        
        for (int i = 0; i < list.getLength(); i++) {
            Element element = (Element) list.item(i);
            String elementName = element.getNodeName();
            if (elementName.equals("proxy")) {
                if (element.hasAttribute("delete") && element.getAttribute("delete").equalsIgnoreCase("true")) {
                    return null;
                }
            }
            
            proxy = new ProxyConfig();
            try {
                proxy.setHost(getTagValue("host", element));
                proxy.setPort(Integer.valueOf(getTagValue("port", element)));
                try {
                    proxy.setUsername(getTagValue("username", element));
                } catch(Exception e) {
                    proxy.setUsername(null);
                }
                try {
                    proxy.setPassword(getTagValue("password", element));
                } catch(Exception e) {
                    proxy.setPassword(null);
                }
            } catch(NumberFormatException e) {
                proxy = null;
                throw new ProxyException(Messages.TransportConfigureUtil_12.toString());
            } catch(Exception e) {
                proxy = null;
                throw new ProxyException(Messages.TransportConfigureUtil_11.toString());
            }
        }
        
        return proxy;
    }

    /**
     * Configures proxy settings provided by the client in XML format
     * 
     * @param filename XML proxy file
     * @throws IOException
     * @throws MerchantException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static void setupProxyConfiguration(String filename) 
            throws IOException, MerchantException, ParserConfigurationException, SAXException, ProxyException {
        ProxyConfig proxy;
        proxy = parseProxy(filename);
        
        if(proxy != null) {
            proxy.save();
        } else { 
            ProxyConfig.delete();
        }
    }
}