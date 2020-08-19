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
package com.sap.hybris.crm.sapcrmordermgmtbol.backendobject.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.CRMConstants;

import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapcommonbol.common.backendobject.impl.ConverterBackendERPCRM;
import de.hybris.platform.sap.sapcommonbol.common.backendobject.impl.ConverterUtils;
import de.hybris.platform.sap.sapcommonbol.common.backendobject.impl.ErpWecCustF4Units;

/**
 * CRM backend functionality for conversion.<br>
 * Customizing for units and currencies need to be fetched from CRM
 *
 */
@BackendType("CRM")
public class ConverterBackendCRM extends ConverterBackendERPCRM {
    private static final String BAPI_CURRENCY_GETLIST = "BAPI_CURRENCY_GETLIST";
    private static final String TABLE_PARAM_CURRENCY_LIST = "CURRENCY_LIST";
    private static final Integer CURRENCY_DECIMAL = Integer.valueOf(2);

    @Autowired
    private UnitService unitService;

    @Override
    public UomLocalization loadUOMsByLanguageFromBackend(final String applicationID, final String language)
            throws BackendException {
        final Map<String, String> internalToExternalUOM = new HashMap<String, String>();
        final Map<String, String> externalToInternalUOM = new HashMap<String, String>();
        Map<String, String> uOMNamesAndDescriptions = null;
        Map<String, Integer> numberOfDecimalsForUOMs = null;
        /*
         * changes to get unit internal, external codes from hybris since it has
         * mismatch in external/display code which comes from RFC response so
         * populating internalToExternalUOM,externalToInternalUOM from hybris &
         * getting decimals from CRM since it is not available in hyb
         */
        final Set<UnitModel> hybUnits = unitService.getAllUnits();
        for (final UnitModel unitModel : hybUnits) {
            final String internalName = unitModel.getSapCode();
            final String externalName = unitModel.getCode();

            internalToExternalUOM.put(internalName, externalName);
            externalToInternalUOM.put(externalName, internalName);
        }
        // since decimals are not available in above hyb responce, getting it by
        // making CRM call
        final JCoFunction function = getDefaultJCoConnection().getFunction(CRMConstants.CRM_WEC_CUST_F4_UNITS);

        setImportParams(function, language);

        executeRFC(function);
        Map uOMDetails = processResult(function);
        uOMNamesAndDescriptions = (Map<String, String>) uOMDetails.get("uOMNamesAndDescriptions");
        numberOfDecimalsForUOMs = (Map<String, Integer>) uOMDetails.get("numberOfDecimalsForUOMs");

        // save with language as key
        return new UomLocalization(numberOfDecimalsForUOMs, internalToExternalUOM, externalToInternalUOM,
                uOMNamesAndDescriptions);
    }

    /**
     * @param function
     * @param uOMNamesAndDescriptions
     * @param numberOfDecimalsForUOMs
     */
    protected Map processResult(final JCoFunction function) {
        Map uOMDetails = new HashMap<String, Map<String, String>>();
        final JCoParameterList exportParameterList = function.getExportParameterList();
        final JCoTable f4Help = exportParameterList.getTable(ErpWecCustF4Units.et_physical_unit);
        Map<String, String> uOMNamesAndDescriptions = new HashMap<String, String>(f4Help.getNumRows());
        Map<String, Integer> numberOfDecimalsForUOMs = new HashMap<String, Integer>(f4Help.getNumRows());

        for (int i = 0; i < f4Help.getNumRows(); i++) {
            f4Help.setRow(i);
            final String internalName = ConverterUtils.getString(f4Help, ErpWecCustF4Units.internal_name);
            final String description = ConverterUtils.getString(f4Help, ErpWecCustF4Units.description);
            final int decimals = f4Help.getInt(ErpWecCustF4Units.decimals);
            // same for all languages
            numberOfDecimalsForUOMs.put(internalName, new Integer(decimals));
            uOMNamesAndDescriptions.put(internalName, description);
        }
        uOMDetails.put("uOMNamesAndDescriptions", uOMNamesAndDescriptions);
        uOMDetails.put("uOMNamesAndDescriptions", uOMNamesAndDescriptions);
        return uOMDetails;
    }

    /**
     * @param function
     * @throws BackendException
     */
    protected void executeRFC(final JCoFunction function) throws BackendException {
        getDefaultJCoConnection().execute(function);
    }

    /**
     * @param function
     * @param language
     */
    protected void setImportParams(final JCoFunction function, final String language) {
        final JCoParameterList importsCrmWecCustF4Units = function.getImportParameterList();
        importsCrmWecCustF4Units.setValue(ErpWecCustF4Units.iv_language, language.toUpperCase(Locale.ENGLISH));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.sap.sapcommonbol.common.backendobject.impl.
     * ConverterBackendERPCRM#
     * loadCurrenciesByLanguageFromBackend(java.lang.String, java.lang.String) *
     * To populate CurrencyLocalization it requires "CURRENCY",
     * "CURRENCY DESCRIPTION" AND "DECIMALS" But This RFC
     * "BAPI_CURRENCY_GETLIST" doesn't have decimals for currency & requires
     * additional back end call for each currency, so by default considering
     * decimal value as 2.
     */
    @Override
    public CurrencyLocalization loadCurrenciesByLanguageFromBackend(final String applicationID, final String language)
            throws BackendException {
        final JCoFunction function = getDefaultJCoConnection().getFunction(BAPI_CURRENCY_GETLIST);
        getDefaultJCoConnection().execute(function);
        final JCoTable currencyList = function.getTableParameterList().getTable(TABLE_PARAM_CURRENCY_LIST);
        final Map<String, Integer> numberOfDecimalsForCurrencies = new HashMap<String, Integer>(
                currencyList.getNumRows());
        final Map<String, String> currencyDescriptions = new HashMap<String, String>(currencyList.getNumRows());

        for (int i = 0; i < currencyList.getNumRows(); i++) {
            currencyList.setRow(i);
            numberOfDecimalsForCurrencies.put(currencyList.getString("CURRENCY"), CURRENCY_DECIMAL);
            currencyDescriptions.put(currencyList.getString("CURRENCY"), currencyList.getString("LONG_TEXT"));
        }
        return new CurrencyLocalization(numberOfDecimalsForCurrencies, currencyDescriptions);
    }
}
