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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.misc.backend.impl.crm;

import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.cache.exceptions.SAPHybrisCacheException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.exceptions.BackendRuntimeException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.impl.TransactionConfigurationBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.CheckoutConfigurationBackendTools;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.util.StringUtils;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

/**
 * ERP specific implementation of TransactionConfigurationBackend
 */
@BackendType("CRM")
public class TransactionConfigurationCRM extends TransactionConfigurationBase {

    private static final Log4JWrapper sapLogger = Log4JWrapper
            .getInstance(TransactionConfigurationBase.class.getName());

    @Resource
    protected CommonI18NService commonI18NService;

    protected static final String CACHEKEY_TITLE_LIST = "TITLE_LIST";

    /** Name of cache region. */
    public static final String CACHE_NAME_FOR_CUSTOMIZING = "r3cust";

    CheckoutConfigurationBackendTools tools = new CheckoutConfigurationBackendTools();

    static final String CRM_ISA_BASKET_GETSHIPCOND = "CRM_ISA_BASKET_GETSHIPCOND";
    private static final String CACHEKEY_DELIVERY_TYPES = "DELIVERY_TYPES";

    @Override
    public Map<String, String> getAllowedDeliveryTypes() throws BackendException {
        return getDeliveryTypes();
    }

    @Override
    protected boolean isLanguageSwitchDelivCustomizingAvailable(final JCoConnection connection) {
        JCoConnection checkConnection = connection;
        if (checkConnection == null) {
            checkConnection = getDefaultJCoConnection();
        }
        try {
            return checkConnection.isFunctionAvailable(CRM_ISA_BASKET_GETSHIPCOND);
        } catch (final BackendException ex) {
            throw new ApplicationBaseRuntimeException("Could not determine FM availability", ex);
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, String> getDeliveryTypes() {
        // sync access to cache
        synchronized (TransactionConfigurationBase.class) {
            final String rfcCacheKey = CACHEKEY_DELIVERY_TYPES
                    + commonI18NService.getCurrentLanguage().getIsocode().toString().toUpperCase();

            deliveryTypes = (Map<String, String>) deliverTypeCacheAccess.get(rfcCacheKey);
            if (deliveryTypes == null) {
                try {
                    deliveryTypes = getDeliveryTypesFromBackend();
                    deliverTypeCacheAccess.put(rfcCacheKey, deliveryTypes);
                    if (sapLogger.isDebugEnabled()) {
                        sapLogger.debug("loaded from backend delivery types: {0}", new Object[] { deliveryTypes });
                    }
                } catch (final SAPHybrisCacheException e) {
                    throw new BackendRuntimeException("Issue during cache access.", e);
                }
            }
            return deliveryTypes;
        }

    }

    @Override
    protected Map<String, String> getDeliveryTypesFromBackend() {
        try {
            final JCoConnection connection = getDefaultJCoConnection();
            final JCoFunction jcoFunction = connection.getFunction(CRM_ISA_BASKET_GETSHIPCOND);

            final JCoParameterList importParams = jcoFunction.getImportParameterList();
            importParams.setValue("LANGUAGE", commonI18NService.getCurrentLanguage().getIsocode().toString().toUpperCase());
            connection.execute(jcoFunction);
            final JCoTable shipConditions = jcoFunction.getTableParameterList().getTable("SHIPPING_COND");
            final Map<String, String> result = putDeliveryTypes(shipConditions);
            return result;
        } catch (final BackendException e) {
            throw new ApplicationBaseRuntimeException("Could not fetch shipping conditons from back end", e);
        }
    }

    /**
     * @param table
     * @return result of type Map<String, String>
     */
    private Map<String, String> putDeliveryTypes(final JCoTable table) {
        final Map<String, String> result = new HashMap<String, String>();
        table.firstRow();
        do {
            if (!StringUtils.isEmpty(table.getString("SHIP_COND"))) {
                result.put(table.getString("SHIP_COND"), table.getString("DESCRIPTION"));
            }
        } while (table.nextRow());

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.sap.wec.app.ecom.module.checkout.backend.interf.
     * CheckoutConfigurationBackend#getAllowedDeliveryTypesFromBackend()
     */
    @Override
    public Map<String, String> getAllowedDeliveryTypesFromBackend() throws BackendException {
        sapLogger.entering("getAllowedDeliveryTypesFromBackend");

        sapLogger.exiting();
        return null;
    }

    /**
     * Traces a map.
     *
     * @param log
     *            the logger instance
     * @param map
     *            the map which we want to trace
     */
    public static void performDebugOutput(final Log4JWrapper log, final Map<String, String> map) {
        if (log.isDebugEnabled()) {
            final StringBuilder debugOutput = new StringBuilder(100);
            debugOutput.append("\n map content:");
            if (map != null && !map.isEmpty()) {
                final Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator();
                while (iter.hasNext()) {
                    debugOutput.append("\n" + iter.next());
                }
            }
            // toString is not necessary but it eases usage of EasyMock
            log.debug(debugOutput.toString());
        }
    }

    @Override
    protected String getCacheRegionForCustomizing() {
        return CACHE_NAME_FOR_CUSTOMIZING;
    }

}
