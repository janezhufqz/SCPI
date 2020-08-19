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

/**
 *
 */
package com.sap.hybris.crm.sapcrmproductregistrationbol.backend.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.hybris.crm.sapcrmmodel.util.XSSFilterUtil;
import com.sap.hybris.crm.sapcrmproductregistrationbol.backend.ProductRegistrationBackend;
import com.sap.hybris.crm.sapcrmproductregistrationbol.constants.SapcrmproductregistrationbolConstants;

import de.hybris.platform.productregistrationfacades.data.ProductRegistrationData;
import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.backend.jco.BackendBusinessObjectBaseJCo;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.bol.logging.LogSeverity;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionFactory;
import de.hybris.platform.sap.core.jco.connection.JCoStateful;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;

@BackendType("CRM")
public class DefaultProductRegistrationBackend extends BackendBusinessObjectBaseJCo
        implements ProductRegistrationBackend {

    private static final Log4JWrapper LOG = Log4JWrapper.getInstance(DefaultProductRegistrationBackend.class.getName());

    @Resource(name = "sapCoreJCoManagedConnectionFactory")
    protected JCoManagedConnectionFactory managedConnectionFactory;

    private String jcoFunctionCreate;
    private String jcoFunctionFetch;
    private String jcoFunctionDetails;

    protected void closeConnection(final JCoConnection connection) {
        try {
            if (connection != null) {
                ((JCoStateful) connection).destroy();
            }
        } catch (final BackendException ex) {

            LOG.log(LogSeverity.ERROR, LogCategories.APPS_COMMON_RESOURCES,
                    "Error during JCoStateful connection close! " + ex);

        }
    }

    /**
     * This method triggers JCO call to backend system to create product
     * registration request and then create an instance in Hybris from the
     * result of the call
     *
     * @param createRegParams
     *            contains product to be registered ,distribution channel sales
     *            org
     * @param partnerNo
     *            customer requesting the registration
     * @throws BackendException
     */
    @Override
    public void createProductregistration(final Map<String, String> createRegParams, final String partnerNo)
            throws BackendException {

        try {
            LOG.debug("***** Triggrring JCO call to backend system for product registration with product guid: "
                    + createRegParams.get(SapcrmproductregistrationbolConstants.PRODUCT_GUID_PARAM));
            final JCoConnection connection = getDefaultJCoConnection();
            final JCoFunction function = connection.getFunction(this.jcoFunctionCreate);
            prepareCreateRegRequest(createRegParams, partnerNo, function);

            // IT_ATTRIBUTES
            connection.execute(function);

            // determine if there are any errors in the result
            final JCoTable errors = function.getTableParameterList()
                    .getTable(SapcrmproductregistrationbolConstants.ERROR_TABLE);
            for (int i = 0; i < errors.getNumRows(); i++) {
                errors.setRow(i);
                if (errors.getString(SapcrmproductregistrationbolConstants.ERROR_CODE_ATTRIBUTE)
                        .equalsIgnoreCase(SapcrmproductregistrationbolConstants.ERROR_CODE)) {
                    LOG.log(LogSeverity.ERROR, LogCategories.APPS_COMMON_RESOURCES,
                            errors.getString(SapcrmproductregistrationbolConstants.MESSAGE));
                    throw new BackendException("Error while submitting the product registration request ");
                }
            }
        } catch (final Exception e) {
            LOG.getLogger().error("Could not process product registration request ::", e);
            throw new BackendException("Error creating product registration ", e);
        }
    }

    /**
     * Get list of registered products for customer
     *
     * @param customerID
     * @throws BackendException
     */
    @Override
    public List<ProductRegistrationData> getRegisteredProducts(final String customerID) throws BackendException {
        final List<ProductRegistrationData> productRegistrations = new ArrayList<ProductRegistrationData>();
        try {
            LOG.debug("***** Triggrring JCO call to backend system for fetching product registrations for customer: "
                    + customerID);
            final JCoConnection connection = getDefaultJCoConnection();
            final JCoFunction function = connection.getFunction(this.jcoFunctionFetch);
            final JCoParameterList parameterList = function.getImportParameterList();
            final JCoStructure buStructure = parameterList
                    .getStructure(SapcrmproductregistrationbolConstants.IS_PARTNER_TABLE);

            buStructure.setValue(SapcrmproductregistrationbolConstants.PARTNER_NUMBER, customerID);
            buStructure.setValue(SapcrmproductregistrationbolConstants.PARTNER_ROLE,
                    SapcrmproductregistrationbolConstants.PARTNER_ROLE_VALUE);
            connection.execute(function);

            // get result of fumction execution
            final JCoParameterList exportParameterList = function.getTableParameterList();
            final JCoTable outputTable = exportParameterList.getTable("ET_IO_LIST");

            // split
            for (int i = 0; i < outputTable.getNumRows(); i++) {
                getProductRegistrationListDto(productRegistrations, outputTable, i);
            }
        } catch (final Exception e) {
            LOG.getLogger().error("Could not get product registrations  ::", e);
            throw new BackendException("Could not fetch list of registered product", e);
        }
        return productRegistrations;
    }

    /**
     * Get details of registered products for customer by product registration
     * guid
     *
     * @param registrationGuid
     * @throws BackendException
     */
    @Override
    public ProductRegistrationData getRegisteredProductByGuid(final String registrationGuid, final String customerID)
            throws BackendException {
        final ProductRegistrationData productRegistration = new ProductRegistrationData();
        try {
            LOG.debug(
                    "***** Triggrring JCO call to backend system for fetching product registrations dsetail for customer: "
                            + registrationGuid);
            final JCoConnection connection = getDefaultJCoConnection();

            final JCoFunction function = connection.getFunction(this.jcoFunctionDetails);
            final JCoParameterList importParameters = function.getImportParameterList();
            importParameters.setValue(SapcrmproductregistrationbolConstants.REGISTRATION_GUID_PARAM, registrationGuid);

            final JCoParameterList parameterList = function.getImportParameterList();
            final JCoStructure buStructure = parameterList
                    .getStructure(SapcrmproductregistrationbolConstants.IS_PARTNER_TABLE);

            // IT_ATTRIBUTES
            buStructure.setValue(SapcrmproductregistrationbolConstants.PARTNER_NUMBER, customerID);
            buStructure.setValue(SapcrmproductregistrationbolConstants.PARTNER_ROLE,
                    SapcrmproductregistrationbolConstants.PARTNER_ROLE_VALUE);
            connection.execute(function);

            // get result of function execution
            final JCoParameterList exportParameterList = function.getExportParameterList();
            populateProductRegistrationData(productRegistration, exportParameterList);
        } catch (final Exception e) {
            LOG.getLogger().error("Could not get product registrations", e);
            throw new BackendException("Could not fetch product registration details from Backend System", e);
        }
        return productRegistration;
    }

    @Override
    public boolean isBackendDown() {
        try {
            return getDefaultJCoConnection().isBackendOffline();
        } catch (final BackendException e) {
            throw new ApplicationBaseRuntimeException("Cannot determine backend availability", e);

        }
    }

    /**
     * Populate product registration data
     *
     * @param productRegistration
     * @param exportParameterList
     */
    protected void populateProductRegistrationData(final ProductRegistrationData productRegistration,
            final JCoParameterList exportParameterList) {
        final JCoStructure header = exportParameterList
                .getStructure(SapcrmproductregistrationbolConstants.OBJECT_INFO_STRUCTURE);
        productRegistration
                .setRegistrationGuid(header.getString(SapcrmproductregistrationbolConstants.REGISTRATION_GUID));
        productRegistration.setRegistrationNumber(
                formatRegistrationNo(header.getString(SapcrmproductregistrationbolConstants.REGISTRATION_NUMBER)));
        productRegistration.setProductCode(header.getString(SapcrmproductregistrationbolConstants.PRODUCT_CODE));
        productRegistration
                .setRegistrationDate(formatDate(header.getString(SapcrmproductregistrationbolConstants.VALID_FROM)));
        productRegistration.setValidTo(formatDate(header.getString(SapcrmproductregistrationbolConstants.VALID_TO)));
        productRegistration
                .setWarrantyDescription(header.getString(SapcrmproductregistrationbolConstants.WARRANTY_DESCRIPTION));
        productRegistration.setWarrantyBasis(header.getString(SapcrmproductregistrationbolConstants.WARRANTY_BASIS));
        productRegistration.setWarrantyStart(
                formatDate(header.getString(SapcrmproductregistrationbolConstants.WARRANTY_START_DATE)));
        productRegistration
                .setWarrantyEnd(formatDate(header.getString(SapcrmproductregistrationbolConstants.WARRANTY_END_DATE)));
    }

    /**
     * This method forms the import parameters list and the JCO table used to
     * pass as arguments to backend system to create registration request.
     *
     * @param createRegParams
     * @param partnerNo
     * @param function
     */
    protected void prepareCreateRegRequest(final Map<String, String> createRegParams, final String partnerNo,
            final JCoFunction function) {
        final JCoParameterList importParameters = function.getImportParameterList();
        importParameters.setValue(SapcrmproductregistrationbolConstants.PRODUCT_GUID_PARAM,
                createRegParams.get(SapcrmproductregistrationbolConstants.PRODUCT_GUID_PARAM));
        importParameters.setValue(SapcrmproductregistrationbolConstants.SALES_ORG,
                createRegParams.get(SapcrmproductregistrationbolConstants.SALES_ORG));
        importParameters.setValue(SapcrmproductregistrationbolConstants.DIS_CHANNEL,
                createRegParams.get(SapcrmproductregistrationbolConstants.DIS_CHANNEL));
        final JCoParameterList tableList = function.getTableParameterList();
        final JCoTable buTable = tableList.getTable(SapcrmproductregistrationbolConstants.IT_PARTNER_TABLE);

        buTable.appendRow();

        buTable.setValue(SapcrmproductregistrationbolConstants.PARTNER_NUMBER, partnerNo);
        buTable.setValue(SapcrmproductregistrationbolConstants.PARTNER_ROLE,
                SapcrmproductregistrationbolConstants.PARTNER_ROLE_VALUE);
    }

    /**
     * Populate list of product registrations from backend system
     *
     * @param productRegistrations
     * @param outputTable
     * @param i
     */
    protected void getProductRegistrationListDto(final List<ProductRegistrationData> productRegistrations,
            final JCoTable outputTable, final int i) {
        final ProductRegistrationData registeredProduct = new ProductRegistrationData();
        outputTable.setRow(i);
        registeredProduct.setProductCode(XSSFilterUtil.filter(outputTable
                .getString(SapcrmproductregistrationbolConstants.PRODUCT_CODE)));
        registeredProduct.setRegistrationGuid(XSSFilterUtil.filter(outputTable
                .getString(SapcrmproductregistrationbolConstants.REGISTRATION_GUID)));
        registeredProduct.setRegistrationNumber(formatRegistrationNo(XSSFilterUtil.filter(outputTable
                .getString(SapcrmproductregistrationbolConstants.REGISTRATION_NUMBER))));
        registeredProduct.setRegistrationDate(formatDate(XSSFilterUtil.filter(outputTable
                .getString(SapcrmproductregistrationbolConstants.VALID_FROM))));
        registeredProduct.setValidTo(formatDate(XSSFilterUtil.filter(outputTable
                .getString(SapcrmproductregistrationbolConstants.VALID_TO))));
        productRegistrations.add(registeredProduct);
    }

    /**
     * @param registrationNo
     * @return registrationNo
     */
    protected String formatRegistrationNo(final String registrationNo) {
        return registrationNo.substring(30, registrationNo.length());
    }

    /**
     * Format date
     *
     * @param purchaseDate
     * @return Date
     */
    protected Date formatDate(final String purchaseDate) {
        if (null != purchaseDate) {
            DateFormat formatter = null;
            formatter = new SimpleDateFormat(SapcrmproductregistrationbolConstants.DATE_FORMAT);
            try {
                return formatter.parse(purchaseDate);
            } catch (final ParseException e) {
                LOG.debug("Could not format date .Reason" + e);
            }
        }
        return null;

    }

    /**
     * @param jcoFunctionCreate
     *            the jcoFunctionCreate to set
     */
    public void setJcoFunctionCreate(final String jcoFunctionCreate) {
        this.jcoFunctionCreate = jcoFunctionCreate;
    }

    /**
     * @param jcoFunctionFetch
     *            the jcoFunctionFetch to set
     */
    public void setJcoFunctionFetch(final String jcoFunctionFetch) {
        this.jcoFunctionFetch = jcoFunctionFetch;
    }

    /**
     * @param jcoFunctionDetails
     *            the jcoFunctionDetails to set
     */
    public void setJcoFunctionDetails(final String jcoFunctionDetails) {
        this.jcoFunctionDetails = jcoFunctionDetails;
    }

}
