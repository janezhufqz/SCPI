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

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser for the file registered in the metadata file under the type
 * rfc_messages
 *
 * @see MessageMappingCRMRulesContainerImpl
 */

public class MessageMappingCRMRulesParserImpl extends DefaultHandler {

    /**
     * Represents a tag
     */
    public static final class TAG {

        static final String //
        GLOBAL = "global_config", //
                MESSAGE = "message", //
                SOURCE = "source", //
                DESTINATION = "destination", //
                HIDE = "hide";

        private TAG() {

        }
    }

    /**
     * Represents an attribute
     */
    public static final class ATTRIBUTE {
        static final String //
        HIDE_NON_ERROR_MESSAGES = "hide_non_error_messages", //
                ID = "id", //
                NUMBER = "number", //
                MSGV1 = "MESSAGE_V1", MSGV2 = "MESSAGE_V1", MSGV3 = "MESSAGE_V1", MSGV4 = "MESSAGE_V1",
                SEVERITY = "type", //
                RES_KEY_SHORT = "resource_key", CALLBACKID = "callbackId"; //
        /**
         * Process step (Checkout?)
         */
        public static final String PROCESSSTEP = "process_step";

        private ATTRIBUTE() {

        }
    }

    /**
     * Are we processing a message?
     */
    protected boolean inMessage = false;

    /**
     * Hide info or warning messages?
     */
    boolean hideNonErrorMsg = false;
    /**
     * List of rules as result of the parse process
     */
    List<MessageMappingCRMRule> rulesList = new ArrayList<MessageMappingCRMRule>();

    /**
     * Search pattern
     */
    protected MessageMappingCRMRule.Pattern pattern;
    /**
     * Result of mapping process
     */
    protected MessageMappingCRMRule.Result result;

    /**
     * Error message ID: tag inside a tag
     */
    protected static final String ERROR__TAG_OUTSIDE_MESAGE_TAG = "Tag not inside tag " + TAG.MESSAGE;

    @Override
    public void startElement(final String nsURI, final String stripName, final String tagName, final Attributes attrs)
            throws SAXException {

        if (stripName.equals(TAG.GLOBAL)) {
            hideNonErrorMsg = Boolean.parseBoolean(attrs.getValue(ATTRIBUTE.HIDE_NON_ERROR_MESSAGES));
        } else if (stripName.equals(TAG.MESSAGE)) {
            inMessage = true;
        } else if (stripName.equals(TAG.SOURCE)) {
            handleSourceTag(attrs);
        } else if (stripName.equals(TAG.DESTINATION)) {
            handleDestinationTag(attrs);
        } else if (stripName.equals(TAG.HIDE)) {
            handleHideTag();
        }
    }

    protected void handleSourceTag(final Attributes attrs) throws SAXException {
        if (!inMessage) {
            throw new SAXException(ERROR__TAG_OUTSIDE_MESAGE_TAG);
        }
        pattern = buildPattern(attrs);
    }

    protected void handleHideTag() throws SAXException {
        if (!inMessage) {
            throw new SAXException(ERROR__TAG_OUTSIDE_MESAGE_TAG);
        }
        result = buildHideResult();
    }

    protected void handleDestinationTag(final Attributes attrs) throws SAXException {
        if (!inMessage) {
            throw new SAXException(ERROR__TAG_OUTSIDE_MESAGE_TAG);
        }
        result = buildMapResult(attrs);
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (localName.equals(TAG.MESSAGE)) {
            inMessage = false;
            if (pattern == null) {
                throw new SAXException("No pattern in rule");
            }
            if (result == null) {
                throw new SAXException("No result in rule " + pattern);
            }
            rulesList.add(buildRule());
        }
    }

    /**
     * Create a pattern
     *
     * @param attrs
     * @return Search pattern
     */
    protected MessageMappingCRMRule.Pattern buildPattern(final Attributes attrs) {
        return new MessageMappingCRMRule.Pattern(attrs.getValue(ATTRIBUTE.ID), attrs.getValue(ATTRIBUTE.NUMBER),
                attrs.getValue(ATTRIBUTE.SEVERITY), attrs.getValue(ATTRIBUTE.MSGV1), attrs.getValue(ATTRIBUTE.MSGV2),
                attrs.getValue(ATTRIBUTE.MSGV3), attrs.getValue(ATTRIBUTE.MSGV4));

    }

    /**
     * Create a mapping result expression
     *
     * @param attrs
     * @return Mapping result expression
     */
    protected MessageMappingCRMRule.Result buildMapResult(final Attributes attrs) {
        final String severity = attrs.getValue(ATTRIBUTE.SEVERITY);
        final String callbackId = attrs.getValue(ATTRIBUTE.CALLBACKID);
        final String processStep = attrs.getValue(ATTRIBUTE.PROCESSSTEP);
        final Character charSeverity = severity != null && !severity.isEmpty() ? new Character(severity.charAt(0))
                : null;

        return new MessageMappingCRMRule.Result(charSeverity, attrs.getValue(ATTRIBUTE.RES_KEY_SHORT), callbackId,
                processStep);
    }

    /**
     * Create a mapping rule
     *
     * @return Mapping rule (pattern, result expression)
     */
    protected MessageMappingCRMRule buildRule() {
        return new MessageMappingCRMRule(pattern, result);
    }

    /**
     * Create a mapping rule result which states that message is hidden
     *
     * @return Result expression
     */
    protected MessageMappingCRMRule.Result buildHideResult() {
        return new MessageMappingCRMRule.Result(Boolean.TRUE);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append(ATTRIBUTE.HIDE_NON_ERROR_MESSAGES).append(" = ").append(hideNonErrorMsg).append('\n');

        for (final MessageMappingCRMRule rule : rulesList) {
            sb.append(rule).append('\n');
        }
        return sb.toString();
    }

}
