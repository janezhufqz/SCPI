/*
 *
 *  [y] hybris Platform
 *
 *  Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 *  This software is the confidential and proprietary information of SAP
 *  ("Confidential Information"). You shall not disclose such Confidential
 *  Information and shall use it only in accordance with the terms of the
 *  license agreement you entered into with SAP.
 * /
 */
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingCRMRulesContainerImpl.Key;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingCRMRulesContainerImpl.PatternDegreeDescComparatorCRM;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCRMRulesLoader;
import com.sap.tc.logging.Severity;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.bol.logging.LogSeverity;
import de.hybris.platform.sap.core.jco.exceptions.BackendRuntimeException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.SapXMLReaderFactory;

/**
 *
 */
public class MessageMappingCRMRulesLoaderImpl implements MessageMappingCRMRulesLoader {

    /** Registration id for RFC messages */
    protected static final String RFC_MESSAGES = "rfc_messages";

    private final static Log4JWrapper sapLogger = Log4JWrapper
            .getInstance(MessageMappingCRMRulesLoaderImpl.class.getName());
    private boolean hideNonErrorMsg;

    private InputStream messageMappingFileInputStream;

    @Override
    public Map<Key, List<MessageMappingCRMRule>> loadRules() throws SAXException, IOException, BackendRuntimeException {
        sapLogger.entering("loadRules(InputStream is)");

        if (messageMappingFileInputStream == null) {
            messageMappingFileInputStream = loadMessageMappingRulesContainerFile();
        }

        Map<Key, List<MessageMappingCRMRule>> rules = parseMessageMappingRules(messageMappingFileInputStream);

        messageMappingFileInputStream = loadAdditionalMessageMappingRulesContainerFile();

        if (messageMappingFileInputStream != null) {
            Map<Key, List<MessageMappingCRMRule>> additionalMappingRules = parseMessageMappingRules(
                    messageMappingFileInputStream);

            rules = mergeMappingRules(rules, additionalMappingRules);
        }
        return rules;
    }

    /**
     * @param rules
     * @param additionalMappingRules
     * @return additionalMappingRules
     */
    protected Map<Key, List<MessageMappingCRMRule>> mergeMappingRules(Map<Key, List<MessageMappingCRMRule>> rules,
            Map<Key, List<MessageMappingCRMRule>> additionalMappingRules) {
        for (Map.Entry<Key, List<MessageMappingCRMRule>> rule : rules.entrySet()) {
            additionalMappingRules.putIfAbsent(rule.getKey(), rule.getValue());
        }
        sapLogger.exiting();
        return additionalMappingRules;
    }

    /**
     * @return rules
     * @throws SAXException
     * @throws IOException
     */
    protected Map<Key, List<MessageMappingCRMRule>> parseMessageMappingRules(InputStream messageMappingFileInputStream)
            throws SAXException, IOException {
        Map<Key, List<MessageMappingCRMRule>> rules;

        final XMLReader parser = SapXMLReaderFactory.createXMLReader();

        final MessageMappingCRMRulesParserImpl msgHandler = getMessageMappingRulesParser();
        parser.setContentHandler(msgHandler);
        parser.parse(new InputSource(messageMappingFileInputStream));

        sapLogger.log(Severity.DEBUG, LogCategories.APPS_BUSINESS_LOGIC,
                MessageFormat.format("Message Mapping Rules: {0}", new Object[] { msgHandler }));

        final StringBuilder errorsCollector = new StringBuilder();

        hideNonErrorMsg = msgHandler.hideNonErrorMsg;

        final MessageMappingCRMRule[] tmpRules = msgHandler.rulesList
                .toArray(new MessageMappingCRMRule[msgHandler.rulesList.size()]);

        /*
         * Arrange rules by attribute degree, so they are added into list by
         * degree. Most specific will be tested first. <p> Stable sort.
         */
        Arrays.sort(tmpRules, new PatternDegreeDescComparatorCRM());

        rules = new HashMap<Key, List<MessageMappingCRMRule>>(tmpRules.length);
        for (final MessageMappingCRMRule rule : tmpRules) {
            final MessageMappingCRMRule.Pattern pattern = rule.getPattern();

            final Key keyL1 = new Key(pattern.getBeClass(), pattern.getBeNumber(), pattern.getBeSeverity());
            List<MessageMappingCRMRule> rulesList = rules.get(keyL1);
            if (rulesList == null) {
                rulesList = new ArrayList<MessageMappingCRMRule>(1);
                rules.put(keyL1, rulesList);
            }

            // check for duplicate patterns
            for (final MessageMappingCRMRule dublicated : rulesList) {
                if (rule.getPattern().equals(dublicated.getPattern())) {
                    final String MSG_PATTERN = "Rule {0} has the same pattern as rule {1}, it will be ignored";
                    final Object[] args = new Object[] { rule, dublicated };

                    sapLogger.trace(LogSeverity.WARNING, MSG_PATTERN, args);

                    final String errMsg = MessageFormat.format(MSG_PATTERN, args);
                    errorsCollector.append(errMsg).append('\n');
                }
            }

            rulesList.add(rule);
        }

        if (errorsCollector.length() > 0) {
            final BackendRuntimeException ex = new BackendRuntimeException(errorsCollector.toString());
            sapLogger.throwing(ex);
            throw ex;
        }

        sapLogger.exiting();
        return rules;
    }

    /**
     * @return Parser
     */
    protected MessageMappingCRMRulesParserImpl getMessageMappingRulesParser() {
        return new MessageMappingCRMRulesParserImpl();
    }

    /**
     * Load messages.xml file as input stream
     *
     * @return Rules in as messages.xml
     * @throws BackendRuntimeException
     */
    protected InputStream loadMessageMappingRulesContainerFile() throws BackendRuntimeException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("sapcrmmessagemapping/messages.xml");
        if (is != null) {
            return is;
        } else {
            throw new BackendRuntimeException("File \"" + RFC_MESSAGES + "\" can not be opened");
        }
    }

    /**
     * @param baseIs
     * @param additionalIs
     * @return
     */
    protected InputStream mergeMessageMappingRules(final InputStream baseIs, final InputStream additionalIs) {
        // per default in case something goes wrong we always pass back the
        // extensionIs
        InputStream mergedIs = additionalIs;
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder baseDocumentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document baseDocument = baseDocumentBuilder.parse(baseIs);
            final DocumentBuilder extDocumentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document extDocument = extDocumentBuilder.parse(additionalIs);

            final NodeList ndListExt = extDocument.getElementsByTagName("messages");
            if (null == ndListExt || null == ndListExt.item(0).getFirstChild()) {
                return baseIs;
            }

            populateNodeList(baseDocument, extDocument, ndListExt);

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final Source xmlSource = new DOMSource(extDocument);
            final Result outputTarget = new StreamResult(outputStream);
            TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
            mergedIs = new ByteArrayInputStream(outputStream.toByteArray());
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException
                | TransformerFactoryConfigurationError e) {
            sapLogger.getLogger().error(e);
        }
        return mergedIs;
    }

    protected void populateNodeList(final Document baseDocument, final Document extDocument, final NodeList ndListExt) {
        final Node firstChild = ndListExt.item(0).getFirstChild();

        final NodeList ndListBase = baseDocument.getElementsByTagName("message");
        for (int i = 0; i < ndListBase.getLength(); i++) {
            final Node node = ndListBase.item(i);
            final Node importedNode = extDocument.importNode(node, true);
            ndListExt.item(0).insertBefore(importedNode, firstChild);
        }
    }

    /**
     * @return
     * @throws BackendRuntimeException
     */
    protected InputStream loadAdditionalMessageMappingRulesContainerFile() throws BackendRuntimeException {
        return null;
    }

    /**
     * @param messageMappingFileInputStream
     */
    protected void setMessageMappingFileInputStream(final InputStream messageMappingFileInputStream) {
        this.messageMappingFileInputStream = messageMappingFileInputStream;
    }

    @Override
    public boolean isHideNonErrorMsg() {
        return hideNonErrorMsg;
    }
}
