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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm.strategy;

import de.hybris.platform.sap.core.bol.backend.jco.JCoHelper;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionHelper;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.CustomizingHelper;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm.LoadOperation;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.BackendState;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.CRMConstants;
import com.sap.tc.logging.Severity;

/**
 * Responsible to map header attributes from the LO-API response to the header
 * BO representation. Used for reading and writing data to CRM.
 */
public class HeaderMapper extends BaseMapper {

    private final String CRM_DATE_FORMAT = "yyyyMMddHHmmss";
    /** LO-API ID of header segment. */
    public static final String OBJECT_ID_HEAD = "HEAD";

    /** The Constant PARAMETER_FIELDNAME. */
    public static final String PARAMETER_FIELDNAME = "FIELDNAME";

    /** Logging instance. */
    public static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(HeaderMapper.class.getName());

    /** Converter BO. */
    protected Converter converter;

    /** The common i18 n service. */
    CommonI18NService commonI18NService;

    /** The configuration service. */
    @Resource
    ConfigurationService configurationService;

    /** The module configuration access. */
    protected ModuleConfigurationAccess moduleConfigurationAccess;

    /**
     * Gets the module configuration access.
     *
     * @return the module configuration access
     */
    public ModuleConfigurationAccess getModuleConfigurationAccess() {
        return moduleConfigurationAccess;
    }

    /**
     * Sets the module configuration access.
     *
     * @param moduleConfigurationAccess
     *            the new module configuration access
     */
    public void setModuleConfigurationAccess(final ModuleConfigurationAccess moduleConfigurationAccess) {
        this.moduleConfigurationAccess = moduleConfigurationAccess;
    }

    /**
     * Sets the converter.
     *
     * @param converter
     *            the new converter
     */
    public void setConverter(final Converter converter) {
        this.converter = converter;
    }

    /**
     * Gets the common i18 n service.
     *
     * @return the commonI18NService
     */
    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    /**
     * Sets the common i18 n service.
     *
     * @param commonI18NService
     *            the commonI18NService to set
     */
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend
     * .impl.crm.strategy.BaseMapper#init()
     */
    @Override
    public void init() {
        /* nothing to initialize */
    }

    /**
     * @param date
     *            date
     * @return date
     */
    protected Date convertString2date(final String date) {
        final SimpleDateFormat sdf = new SimpleDateFormat(CRM_DATE_FORMAT);
        Date convertedDate = null;
        if (null != date) {
            try {
                convertedDate = sdf.parse(date);
            } catch (final ParseException e) {
                sapLogger.log(Severity.ERROR, LogCategories.APPLICATIONS, "Date not parsable in HeaderMapper");
            }
        }
        return convertedDate;
    }

    /**
     * Read.
     *
     * @param esHeader
     *            the es header
     * @param backendState
     *            the backend state
     * @param salesDocument
     *            the sales document
     * @param header
     *            the header
     */
    public void read(final JCoStructure esHeader, final BackendState backendState, final SalesDocument salesDocument,
            final Header header) {
        header.setChangeable(backendState.getLoadState().isChangeable());
        header.setHandle(JCoHelper.getString(esHeader, CRMConstants.FIELD_HANDLE));
        header.setProcessType(JCoHelper.getString(esHeader, "PROCESS_TYPE"));
        header.setSalesDocNumber(JCoHelper.getString(esHeader, "OBJECT_ID"));
        header.setSalesDocumentsOrigin(""); // ERP only know ERP!
        header.setPurchaseOrderExt(JCoHelper.getString(esHeader, "PO_NUMBER_SOLD"));
        header.setShipCond(JCoHelper.getString(esHeader, "SHIP_COND"));
        header.setIncoTerms1(JCoHelper.getString(esHeader, "INCOTERMS1"));
        header.setIncoTerms2(JCoHelper.getString(esHeader, "INCOTERMS2"));
        header.setPaymentTerms(JCoHelper.getString(esHeader, "PMNTTRMS"));
        header.setProcessTypeDesc(JCoHelper.getString(esHeader, "PROC_TYPE_DESC"));
        header.setCreatedAt(convertString2date(esHeader.getString("CREATED_AT")));
        header.setPaymentTermsDesc(JCoHelper.getString(esHeader, "PMNTTRMS_DESC"));
        final String currency = JCoHelper.getString(esHeader, "CURRENCY");
        header.setCurrency(currency);
        final int numberOfDecimals = CustomizingHelper.getNumberOfDecimals(converter, currency);

        BigDecimal netValue = esHeader.getBigDecimal("NET_VALUE");
        netValue = ConversionHelper.adjustCurrencyDecimalPoint(netValue, numberOfDecimals);
        header.setNetValue(netValue);

        BigDecimal taxValue = esHeader.getBigDecimal("TAX_VALUE");
        taxValue = ConversionHelper.adjustCurrencyDecimalPoint(taxValue, numberOfDecimals);
        header.setTaxValue(taxValue);

        BigDecimal grossValue = esHeader.getBigDecimal("GROSS_VALUE");
        grossValue = ConversionHelper.adjustCurrencyDecimalPoint(grossValue, numberOfDecimals);
        header.setGrossValue(grossValue);

        if (esHeader.getBigDecimal("FREIGHT_VALUE") != null) {
            BigDecimal freightValue = esHeader.getBigDecimal("FREIGHT_VALUE");
            freightValue = ConversionHelper.adjustCurrencyDecimalPoint(freightValue, numberOfDecimals);
            header.setFreightValue(freightValue);
        } else {
            header.setFreightValue(BigDecimal.ZERO);
        }

        final DocumentType docType = DocumentTypeMapping
                .getDocumentTypeByTransactionGroup(JCoHelper.getString(esHeader, "PROCESS_TYPE"));

        if (docType != null) {
            header.setDocumentType(docType);
        } else {
            header.setDocumentType(DocumentType.UNKNOWN);
            // no doctype recognized -log error
            sapLogger.debug("No Document type could be determined for transaction group: " + docType);
        }
        calculateValuesWOFreight(header);
    }

    /**
     * Calculate values wo freight.
     *
     * @param header
     *            the header
     */
    protected void calculateValuesWOFreight(final Header header) {

        header.setNetValueWOFreight(header.getNetValue().subtract(header.getFreightValue()));

    }

    /**
     * Writes header attributes to prepare the LO-API update call.
     *
     * @param salesDocHeader
     *            the sales doc header
     * @param salesDocR3Lrd
     *            the sales doc r3 lrd
     * @param headComV
     *            the head com v
     * @param headComX
     *            the head com x
     * @param config
     *            the config
     * @throws BackendException
     *             the backend exception
     */
    protected void write(final Header salesDocHeader, final BackendState salesDocR3Lrd, final JCoStructure headComV,
            final JCoTable headComX, final TransactionConfiguration config) throws BackendException {

        final Date reqDeliveryDate = salesDocHeader.getReqDeliveryDate();
        final String shipCond = salesDocHeader.getShipCond();
        final String currency = commonI18NService.getCurrentCurrency().getSapCode();
        printDebugOutput(salesDocHeader, currency, reqDeliveryDate, shipCond);

        headComV.setValue("GUID", salesDocHeader.getTechKey().getIdAsString());
        headComX.appendRow();
        headComX.setValue(PARAMETER_FIELDNAME, "GUID");

        headComV.setValue("HANDLE", salesDocHeader.getHandle());
        headComX.appendRow();
        headComX.setValue(PARAMETER_FIELDNAME, "HANDLE");

        if (StringUtils.isNotBlank(salesDocHeader.getPurchaseOrderExt())) {
            headComV.setValue("PO_NUMBER_SOLD", salesDocHeader.getPurchaseOrderExt());

            headComX.appendRow();
            headComX.setValue(PARAMETER_FIELDNAME, "PO_NUMBER_SOLD");
        }

        if (StringUtils.isNotBlank(currency)) {
            headComV.setValue("CURRENCY", currency);

            headComX.appendRow();
            headComX.setValue(PARAMETER_FIELDNAME, "CURRENCY");
        }

        if (StringUtils.isNotBlank(shipCond)) {
            headComV.setValue("SHIP_COND", shipCond);
            headComX.appendRow();
            headComX.setValue(PARAMETER_FIELDNAME, "SHIP_COND");
        }

        // Incoterms
        fillIncoTerms(salesDocHeader, headComV, headComX);

        // Only set the field in create mode
        if (salesDocR3Lrd.getLoadState().getLoadOperation().equals(LoadOperation.CREATE)
                && config.getDeliveryBlock() != null) {
            // Customer Purch Order Type
            headComV.setValue("ORDER_TYPE", config.getCustomerPurchOrderType());
            headComX.appendRow();
            headComX.setValue(PARAMETER_FIELDNAME, "ORDER_TYPE");
        }

    }

    /**
     * Fill inco terms.
     *
     * @param salesDocHeader
     *            the sales doc header
     * @param headComV
     *            the head com v
     * @param headComX
     *            the head com x
     * @param b2bModel
     *            the b2b model
     */
    protected void fillIncoTerms(final Header salesDocHeader, final JCoStructure headComV, final JCoTable headComX) {
        if (StringUtils.isNotBlank(salesDocHeader.getIncoTerms1())) {
            headComV.setValue("INCOTERMS1", salesDocHeader.getIncoTerms1());
            headComX.appendRow();
            headComX.setValue(PARAMETER_FIELDNAME, "INCOTERMS1");
        }

        if (StringUtils.isNotBlank(salesDocHeader.getIncoTerms2())) {
            headComV.setValue("INCOTERMS2", salesDocHeader.getIncoTerms2());
            headComX.appendRow();
            headComX.setValue(PARAMETER_FIELDNAME, "INCOTERMS2");
        }

    }

    /**
     * Prints the debug output.
     *
     * @param salesDocHeader
     *            the sales doc header
     * @param currency
     *            the currency
     * @param reqDeliveryDate
     *            the req delivery date
     * @param shipCond
     *            the ship cond
     */
    private void printDebugOutput(final Header salesDocHeader, final String currency, final Date reqDeliveryDate,
            final String shipCond) {
        if (sapLogger.isDebugEnabled()) {
            final StringBuilder debugOutput = new StringBuilder(75);
            debugOutput.append("Method fillHeader");
            debugOutput.append("\n ID         :         " + salesDocHeader.getTechKey());
            debugOutput.append("\n handle     :         " + salesDocHeader.getHandle());
            debugOutput.append("\n external ID:         " + salesDocHeader.getPurchaseOrderExt());
            debugOutput.append("\n shipping conditions: " + shipCond);
            debugOutput.append("\n req. delivery date : " + reqDeliveryDate);
            debugOutput.append("\n inco1:               " + salesDocHeader.getIncoTerms1());
            debugOutput.append("\n inco2:               " + salesDocHeader.getIncoTerms2());
            debugOutput.append("\n curr:                " + currency);
            sapLogger.debug(debugOutput);
        }

    }
}
