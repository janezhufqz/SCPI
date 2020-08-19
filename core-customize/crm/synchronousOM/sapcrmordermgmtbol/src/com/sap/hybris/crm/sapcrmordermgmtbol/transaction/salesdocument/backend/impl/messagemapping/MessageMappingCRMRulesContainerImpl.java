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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.xml.sax.SAXException;

import com.sap.hybris.crm.sapcrmordermgmtbol.constants.SapcrmordermgmtbolConstants;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCRMCallbackProcessor;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCRMRulesContainer;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCRMRulesLoader;
import com.sap.tc.logging.Severity;

import de.hybris.platform.sap.core.bol.cache.CacheAccess;
import de.hybris.platform.sap.core.bol.cache.exceptions.SAPHybrisCacheException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.core.jco.exceptions.BackendRuntimeException;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.impl.TransactionConfigurationBase;

/**
 * Contains all rules for message mapping. Refer to messages.xml
 */
public class MessageMappingCRMRulesContainerImpl implements MessageMappingCRMRulesContainer {

    /**
     * Allows to access configuration settings
     */
    protected ModuleConfigurationAccess moduleConfigurationAccess;
    /**
     * Loads call backs (change of messages on application side)
     */
    protected MessageMappingCRMCallbackLoader messageMappingCallbackLoader;

    /**
     * Key Level 1. It is used to match message class, number and severity into
     * a hashMap.
     */
    public static class Key {
        /**
         * Message ID
         */
        protected final String id;
        /**
         * Message number
         */
        protected final String number;
        /**
         * Severity
         */
        protected final String severity;

        /**
         * @param id
         * @param num
         * @param sev
         */
        public Key(final String id, final String num, final String sev) {
            this.id = id;
            this.number = num;
            this.severity = sev;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }

            if (!(obj instanceof Key)) {
                return false;
            }

            final Key o = (Key) obj;
            return MessageMappingCRMRule.equalsField(id, o.id) //
                    && MessageMappingCRMRule.equalsField(number, o.number) //
                    && MessageMappingCRMRule.equalsField(severity, o.severity);
        }

        @Override
        public int hashCode() {
            return 1 + MessageMappingCRMRule.hashCodeField(id) * 0x1F + MessageMappingCRMRule.hashCodeField(number)
                    + 0x7 * MessageMappingCRMRule.hashCodeField(severity);
        }

        @Override
        public String toString() {
            return MessageFormat.format("key {0}/{1}/{2} ", new Object[] { id, number, severity });
        }

    }

    /**
     * Compares degrees of search patterns
     */
    public static class PatternDegreeDescComparatorCRM implements Comparator<MessageMappingCRMRule> {
        @Override
        public int compare(final MessageMappingCRMRule a1, final MessageMappingCRMRule a2) {
            // (minus) reverse order
            return -(a1.getPattern().attrDergee() - a2.getPattern().attrDergee());
        }
    }

    /**
     * Cache access for storing the rules which were passed from messages.xml
     */
    @Resource(name = SapordermgmtbolConstants.BEAN_ID_CACHE_MESSAGE_MAPPING)
    protected CacheAccess messageMappingCacheAccess;

    /**
     * Access to SAP session beans
     */
    protected GenericFactory genericFactory;

    private static final String CACHEKEY_MESSAGE_MAPPING_RULES = "MESSAGE_MAPPING_RULES";

    private static Log4JWrapper sapLogger = Log4JWrapper
            .getInstance(MessageMappingCRMRulesContainerImpl.class.getName());

    /**
     * Do we hide warning and info messages?
     */
    protected boolean hideNonErrorMsg = false;

    /**
     * Map of rules
     */
    // null indicated uninitialized
    protected Map<Key, List<MessageMappingCRMRule>> rules = null;
    /**
     * Map of callbacks
     */
    protected Map<String, MessageMappingCRMCallbackProcessor> callbacks = null;

    /**
     * Allows to load message mapping rules
     */
    protected MessageMappingCRMRulesLoader messageMappingRulesLoader = null;

    /**
     * Initialization of container
     */
    public void init() {
        getMessageMappingRulesLoader();
        initMessageMappingRulesContainer();

        callbacks = messageMappingCallbackLoader.loadCallbacks();
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    protected void initMessageMappingRulesContainer() {

        // the config name identifies uniquely the application
        final String rfcMsgRulesCacheKey = CACHEKEY_MESSAGE_MAPPING_RULES
                + moduleConfigurationAccess.getSAPConfigurationName();

        // sync access to cache
        synchronized (TransactionConfigurationBase.class) {

            final Object obj = messageMappingCacheAccess.get(rfcMsgRulesCacheKey);

            if (obj != null) {
                rules = (Map<Key, List<MessageMappingCRMRule>>) obj;
            } else {
                try {
                    rules = messageMappingRulesLoader.loadRules();

                } catch (BackendRuntimeException | SAXException | IOException e1) {
                    throw new BackendRuntimeException("Cannot load message mapping rules from file.:::" + e1);
                }

                try {
                    messageMappingCacheAccess.put(rfcMsgRulesCacheKey, rules);
                } catch (final SAPHybrisCacheException e) {
                    throw new BackendRuntimeException("Issue during cache access.::::" + e);
                }

            }
            hideNonErrorMsg = messageMappingRulesLoader.isHideNonErrorMsg();
        }

    }

    /**
     * @return Message mapping rules loader
     */
    protected MessageMappingCRMRulesLoader getMessageMappingRulesLoader() {
        if (messageMappingRulesLoader == null) {
            messageMappingRulesLoader = genericFactory
                    .getBean(SapcrmordermgmtbolConstants.ALIAS_BEAN_MESSAGE_MAPPING_RULES_LOADER);
        }
        return messageMappingRulesLoader;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.
     * impl.messagemapping. MessgeMappingRulesContainer #isHideNonErrorMsg()
     */
    @Override
    public boolean isHideNonErrorMsg() {
        return hideNonErrorMsg;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.
     * impl.messagemapping. MessgeMappingRulesContainer #
     * mostNarrow(de.hybris.platform.sap.sapordermgmtbol.transaction.
     * salesdocument.backend.impl .messagemapping.MessageMappingRule .Pattern)
     */
    @Override
    public MessageMappingCRMRule mostNarrow(final MessageMappingCRMRule.Pattern beMes) {
        sapLogger.entering("mostNarrow(String beClass, String beNumber, String beSeverity)");

        if (rules == null) {
            final IllegalStateException ex = new IllegalStateException("Not loaded");
            sapLogger.throwing(ex);
            throw ex;
        }

        // find solution starting from the most specific rule
        final Key[] patternSequence = {
                // specific
                new Key(beMes.getBeClass(), beMes.getBeNumber(), beMes.getBeSeverity()),
                // any severity
                new Key(beMes.getBeClass(), beMes.getBeNumber(), null),
                // any number or severity
                new Key(beMes.getBeClass(), null, null) };

        for (final Key pattern : patternSequence) {
            final List<MessageMappingCRMRule> rulesList = rules.get(pattern);
            if (rulesList != null) {
                return getRule(beMes, rulesList);
            } // else continue
        }

        sapLogger.log(Severity.INFO, LogCategories.APPLICATIONS,
                "Found no message mapping rule for backend message: " + beMes.getBeClass() + " , " + beMes.getBeNumber()
                        + " , " + beMes.getBeSeverity() + ", " + beMes.getBeV1() + ", " + beMes.getBeV2() + ", "
                        + beMes.getBeV3());

        sapLogger.exiting();
        return null;
    }

    private MessageMappingCRMRule getRule(final MessageMappingCRMRule.Pattern beMes,
            final List<MessageMappingCRMRule> rulesList) {
        MessageMappingCRMRule tempRule = null;
        for (final MessageMappingCRMRule rule : rulesList) {
            if (rule.getPattern().match(beMes.getBeClass(), beMes.getBeNumber(), beMes.getBeSeverity(), beMes.getBeV1(),
                    beMes.getBeV2(), beMes.getBeV3(), beMes.getBeV4())) {

                if (sapLogger.isDebugEnabled()) {
                    sapLogger.debug("found rule " + rule + " for " + beMes.getBeClass() + " , " + beMes.getBeNumber()
                            + " , " + beMes.getBeSeverity());
                }
                sapLogger.exiting();
                tempRule = rule;
                return tempRule;
            }
        }
        return tempRule;
    }

    /**
     * @param messageMappingRulesLoader
     */
    public void setMessageMappingRulesLoader(final MessageMappingCRMRulesLoader messageMappingRulesLoader) {
        this.messageMappingRulesLoader = messageMappingRulesLoader;
    }

    /**
     * @param genericFactory
     *            Factory to access SAP session beans
     */
    public void setGenericFactory(final GenericFactory genericFactory) {
        this.genericFactory = genericFactory;
    }

    /**
     * @param moduleConfigurationAccess
     *            the moduleConfigurationAccess to set
     */
    public void setModuleConfigurationAccess(final ModuleConfigurationAccess moduleConfigurationAccess) {
        this.moduleConfigurationAccess = moduleConfigurationAccess;
    }

    /**
     * @param messageMappingCallbackLoader
     *            the messageMappingCallbackLoader to set
     */
    public void setMessageMappingCallbackLoader(final MessageMappingCRMCallbackLoader messageMappingCallbackLoader) {
        this.messageMappingCallbackLoader = messageMappingCallbackLoader;
    }

    /**
     * @return the callbacks
     */
    @Override
    public Map<String, MessageMappingCRMCallbackProcessor> getCallbacks() {
        return callbacks;
    }

}
