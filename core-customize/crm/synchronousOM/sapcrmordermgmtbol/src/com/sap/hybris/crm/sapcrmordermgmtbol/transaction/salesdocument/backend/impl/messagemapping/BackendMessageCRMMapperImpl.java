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

import java.util.Iterator;

import javax.annotation.Resource;

import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoTable;
import com.sap.hybris.crm.sapcrmordermgmtbol.constants.SapcrmordermgmtbolConstants;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.BackendMessageCRMMapper;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCRMCallbackProcessor;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCRMRulesContainer;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import com.sap.tc.logging.Severity;

/**
 * Maps backend messages to BOL messages (with a replaced text or a replaced
 * severity). Refer to messages.xml
 */
public class BackendMessageCRMMapperImpl implements BackendMessageCRMMapper {

    /**
     * Logging instance
     */
    protected static final Log4JWrapper sapLogger = Log4JWrapper
            .getInstance(BackendMessageCRMMapperImpl.class.getName());

    /**
     * Factory to access SAP session beans
     */
    protected GenericFactory genericFactory;

    /**
     * Indicates that message reflects an error
     */
    protected static final char BAPI_RETURN_ERROR = 'E';
    /**
     * Indicates that message reflects a warning
     */
    protected static final char BAPI_RETURN_WARNING = 'W';
    /**
     * Indicates that message reflects an information
     */
    protected static final char BAPI_RETURN_INFO = 'I';
    /**
     * Indicates that message reflects a fatal error
     */
    protected static final char BAPI_RETURN_ABORT = 'A';
    /**
     * Indicates that message reflects a success information
     */
    protected static final char BAPI_RETURN_SUCCESS = 'S';

    /**
     * Convert external message type to internal. Returns '0' if the message
     * could not being converted.
     *
     * @param msgType
     *            message type external
     * @return message type internal
     */
    protected static int msgTypeBe2Java(final String msgType) {
        final char beType = msgType.charAt(0);
        switch (beType) {
        case BAPI_RETURN_SUCCESS:
            return Message.SUCCESS;
        case BAPI_RETURN_WARNING:
            return Message.WARNING;
        case BAPI_RETURN_INFO:
            return Message.INFO;
        case BAPI_RETURN_ERROR:
        case BAPI_RETURN_ABORT:
            return Message.ERROR;
        default:
            return Message.INITIAL;
        }
    }

    /**
     * Container for message mapping rules, parsed from messages.xml
     */
    @Resource(name = SapcrmordermgmtbolConstants.ALIAS_BEAN_MESSAGE_MAPPING_RULES_CONTAINER)
    protected MessageMappingCRMRulesContainer messageMappingRulesContainer;

    /**
     * Default constructor
     */
    public BackendMessageCRMMapperImpl() {
        sapLogger.entering("CrmMessageMapper.CrmMessageMapper(TransactionConfiguration transConf )");
        sapLogger.exiting();
    }

    /**
     * @param genericFactory
     *            Factory to access SAP session beans
     */
    public void setGenericFactory(final GenericFactory genericFactory) {
        this.genericFactory = genericFactory;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend
     * .impl.messagemapping.BackendMessageMapper #map
     * (com.sap.wec.tc.core.businessobject.boi.BusinessObject,
     * com.sap.conn.jco.JCoRecord, com.sap.conn.jco.JCoTable)
     */
    @Override
    public void map(final BusinessObject rootBo, //
            final JCoRecord singleMsg, final JCoTable msgTable) throws BackendException {

        if (singleMsg != null) {
            map(rootBo, singleMsg);
        }

        if (msgTable != null) {
            int cnt = msgTable.getNumRows();
            msgTable.firstRow();
            while (cnt > 0) {
                map(rootBo, msgTable);
                --cnt;
                msgTable.nextRow();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend
     * .impl.messagemapping.BackendMessageMapper #map
     * (com.sap.wec.tc.core.businessobject.boi.BusinessObject,
     * com.sap.conn.jco.JCoRecord)
     */
    @Override
    public Message map(final BusinessObject rootBo, final JCoRecord struct) throws BackendException {
        sapLogger.entering("map(BusinessObject rootBo, JCoRecord struct)");

        final BackendMessageCRM beMes = createBackendMessage(struct);

        if (beMes.isEmpty()) {
            sapLogger.log(Severity.INFO, LogCategories.APPLICATIONS, "Message " + beMes + " is empty");
            sapLogger.exiting();
            return null;

        } else if (beMes.getBeSeverity().charAt(0) == 'A') {
            final BackendException ex = new BackendException("Abort BE message: " + beMes.toString());
            sapLogger.throwing(ex);
            throw ex;

        } else {
            final String severity = beMes.getBeSeverity();
            final MessageMappingCRMRule.Pattern patrn = new MessageMappingCRMRule.Pattern(beMes.getBeClass(),
                    beMes.getBeNumber(), severity, beMes.getVars()[0], beMes.getVars()[1], beMes.getVars()[2],
                    beMes.getVars()[3]);

            // find the matching rule
            final MessageMappingCRMRule rule = messageMappingRulesContainer.mostNarrow(patrn);

            // decide action base on rule
            final Message msg = getMessage(rootBo, beMes, severity, rule);

            sapLogger.exiting();
            return msg;
        }
    }

    private Message getMessage(final BusinessObject rootBo, final BackendMessageCRM beMes, final String severity,
            final MessageMappingCRMRule rule) {
        Message msg;
        if (rule != null) {
            msg = transformMessageByRule(beMes, rule);
        } else {
            if (messageMappingRulesContainer.isHideNonErrorMsg()
                    && severityScale(severity.charAt(0)) < severityScale(BAPI_RETURN_ERROR)) {
                msg = null;
            } else {
                msg = passMessage(beMes);
            }
        }

        if (msg != null) {
            final BusinessObject bob = findReferredBO(rootBo, beMes);
            bob.addMessage(msg);
            sapLogger.log(Severity.INFO, LogCategories.APPLICATIONS,
                    "Message " + beMes + " mapped by rule " + rule + " to " + msg + " and attched to BO " + bob);
        } else {
            sapLogger.log(Severity.INFO, LogCategories.APPLICATIONS,
                    "Message " + beMes + " mapped by rule " + rule + " to nothing");
        }

        sapLogger.exiting();
        return msg;
    }

    /**
     * Creates new message based on a JCO structure
     *
     * @param struct
     *            JCO structure
     * @return Backend message
     */
    protected BackendMessageCRM createBackendMessage(final JCoRecord struct) {
        final BackendMessageCRM beMes = new BackendMessageCRM(struct);
        return beMes;
    }

    /**
     * Transforms a message from backend applying the rules from messages.xml
     *
     * @param beMsg
     *            Backend message
     * @param rule
     *            Rule
     * @return Transformed message
     */
    protected OrderMgmtMessageCRM transformMessageByRule(final BackendMessageCRM beMsg,
            final MessageMappingCRMRule rule) {

        if (rule.getResult().isHide()) {
            return null;
        } else {
            if (!processCallbacks(rule, beMsg)) {
                return null;
            }

            final MessageMappingCRMRule.Result result = rule.getResult();

            String beSeverity;
            if (result.getSeverity() != null) {
                beSeverity = String.valueOf(result.getSeverity());
            } else {
                beSeverity = beMsg.getBeSeverity();
            }
            final OrderMgmtMessageCRM msg = new OrderMgmtMessageCRM(msgTypeBe2Java(beSeverity));

            takeOverAllwaysPassedAttributes(beMsg, msg);

            if (result.getResourceKey() != null) {
                msg.setResourceKey(result.getResourceKey());
            } else {
                msg.setDescription(beMsg.getMessageText());
            }

            // set process step
            msg.setProcessStep(rule.result.processStep);

            return msg;
        }
    }

    /**
     * @param rule
     * @param beMsg
     */
    private boolean processCallbacks(final MessageMappingCRMRule rule, final BackendMessageCRM beMsg) {
        if (messageMappingRulesContainer.getCallbacks() != null) {
            final MessageMappingCRMCallbackProcessor callback = messageMappingRulesContainer.getCallbacks()
                    .get(rule.result.getCallbackId());
            if (callback != null) {
                return callback.process(beMsg);
            }
        }
        return true;

    }

    /**
     * Parses message
     *
     * @param beMsg
     * @return Transformed message
     */
    protected OrderMgmtMessageCRM passMessage(final BackendMessageCRM beMsg) {
        final OrderMgmtMessageCRM msg = new OrderMgmtMessageCRM(msgTypeBe2Java(beMsg.getBeSeverity()));
        takeOverAllwaysPassedAttributes(beMsg, msg);
        msg.setDescription(beMsg.getMessageText());
        return msg;
    }

    /**
     * Transfers attributes from a backend message to a transformed one
     * (reference key, parameters)
     *
     * @param beMsg
     *            Backend message
     * @param msg
     *            Transformed message
     */
    protected void takeOverAllwaysPassedAttributes(final BackendMessageCRM beMsg, final OrderMgmtMessageCRM msg) {
        msg.setRefTechKey(beMsg.getRefTechKey());
        msg.setResourceArgs(beMsg.getVars());
        msg.setTechKey(new TechKey(beMsg.getBeClass() + ' ' + beMsg.getBeNumber()));
    }

    /**
     * Does message refer to the root BO?
     *
     * @param bo
     *            Business object which might contain childs
     * @param msg
     *            Backend message
     * @return Belongs to root?
     */
    protected boolean isMessageReferRootBO(final BusinessObject bo, final BackendMessageCRM msg) {
        final TechKey key = msg.getRefTechKey();

        if (key == null || key.isInitial()) {
            // nothing can be found.
            if (sapLogger.isDebugEnabled()) {
                sapLogger.debug("message " + msg + "have no tech key; force to refer to root BO " + bo);
            }
            return true;

        } else {
            return key.equals(bo.getTechKey()) //
                    || "HEAD".equals(key.toString()) //
                    || key.toString().equals(bo.getHandle());
        }
    }

    /**
     * Does message belong to a child BO?
     *
     * @param bo
     *            Business object, might contain childs
     * @param msg
     *            Backend message
     * @return Belongs to a child?
     */
    protected boolean isMessageReferChildBO(final BusinessObject bo, final BackendMessageCRM msg) {
        return bo.getTechKey() != null && bo.getTechKey().equals(msg.getRefTechKey());
    }

    /**
     * Find BO a message belongs to
     *
     * @param rootBo
     *            Business object, might contain childs
     * @param msg
     *            Backend message
     * @return The BO a message belongs to (either root or a child)
     */
    protected BusinessObject findReferredBO(final BusinessObject rootBo, final BackendMessageCRM msg) {
        sapLogger.entering("findReferencedBO(BusinessObject rootBo, ErpMessage mes)");

        if (isMessageReferRootBO(rootBo, msg)) {
            if (sapLogger.isDebugEnabled()) {
                sapLogger.debug("Message " + msg + " belongs to header " + rootBo);
            }
            sapLogger.exiting();
            return rootBo;
        } else {
            // check shields
            @SuppressWarnings("unchecked")
            final Iterator<BusinessObjectBase> it = rootBo.getSubObjectIterator();
            while (it.hasNext()) {
                // must be BusinessObject
                final BusinessObject childBo = it.next();
                if (isMessageReferChildBO(childBo, msg)) {
                    logChildBO(msg, childBo);
                    sapLogger.exiting();
                    return childBo;
                }
            }
            if (sapLogger.isDebugEnabled()) {
                sapLogger.debug("Message " + msg + " do not belongs to " + rootBo
                        + " or its chields, but forced to refer to it");
            }
            sapLogger.exiting();
            return rootBo;
        }
    }

    private void logChildBO(final BackendMessageCRM msg, final BusinessObject childBo) {
        if (sapLogger.isDebugEnabled()) {
            sapLogger.debug("Message " + msg + " belongs to child BO " + childBo);
        }
    }

    /**
     * Returns a relative scale of severity.
     * <p>
     * Concrete value is unimportant. Defined for severity comparison.
     *
     * @param beSeverity
     *            Severity
     * @return Scale
     */
    protected static int severityScale(final char beSeverity) {
        switch (beSeverity) {
        case BAPI_RETURN_INFO:
            return 10;
        case BAPI_RETURN_SUCCESS:
            return 20;
        case BAPI_RETURN_WARNING:
            return 30;
        case BAPI_RETURN_ERROR:
            return 40;
        case BAPI_RETURN_ABORT:
            return 50;
        default:
            return 0;
        }
    }

}
