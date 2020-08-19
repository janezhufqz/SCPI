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
package com.sap.hybris.crm.sapcrmproductregistrationbol.businessobject.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.hybris.crm.sapcrmproductregistrationbol.backend.ProductRegistrationBackend;
import com.sap.hybris.crm.sapcrmproductregistrationbol.businessobject.ProductRegistrationBO;

import de.hybris.platform.productregistrationfacades.data.ProductRegistrationData;
import de.hybris.platform.sap.core.bol.businessobject.BackendInterface;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.store.services.BaseStoreService;

@BackendInterface(ProductRegistrationBackend.class)
public class DefaultProductRegistrationBO extends BusinessObjectBase implements ProductRegistrationBO {

    private static final Logger LOG = Logger.getLogger(DefaultProductRegistrationBO.class);

    private static final String PREFIX = "productRegistrationBackendType";

    private BaseStoreService baseStoreService;

    /**
     * This method triggers the JCO call to SAP backend system to create request
     * for product registration
     *
     * @param registrationParameters
     *            contain parameters like product guid to be registered,and
     *            sales area data
     * @param partnerNo
     *            customer for which request is initiated
     * @param backendType
     * @throws BackendException
     */
    @Override
    public void createProductregistration(final Map<String, String> registrationParameters, final String partnerNo,
            final String backendType) throws BackendException {
        try {
            if (!isBackendDown() && null != backendType && !backendType.isEmpty()) {

                final ProductRegistrationBackend sapProductRegistrationBackend = (ProductRegistrationBackend) genericFactory
                        .getBean(PREFIX.concat(backendType));

                sapProductRegistrationBackend.createProductregistration(registrationParameters, partnerNo);

            }
        } catch (ApplicationBaseRuntimeException | BackendException e) {
            LOG.debug("Could not submit the product registration request. Reason" + e);
            throw e;
        }
    }

    /**
     * Get list of registered products for customer
     *
     * @param customerID
     * @return List<ProductRegistrationData>
     * @throws BackendException
     */
    @Override
    public List<ProductRegistrationData> getRegisteredProducts(final String customerID, final String backendType)
            throws BackendException {
        List<ProductRegistrationData> registeredProducts = null;
        if (!isBackendDown() && null != backendType && !backendType.isEmpty()) {
            final ProductRegistrationBackend sapProductRegistrationBackend = (ProductRegistrationBackend) genericFactory
                    .getBean(PREFIX.concat(backendType));

            registeredProducts = sapProductRegistrationBackend.getRegisteredProducts(customerID);
        }
        return registeredProducts;
    }

    /**
     * Get the product registration details by guid
     *
     * @param registrationNumber
     * @return ProductRegistrationData
     */
    @Override
    public ProductRegistrationData getRegisteredProductByGuid(final String registrationNumber, final String backendType,
            final String customerID) throws BackendException {
        ProductRegistrationData registeredProducts = null;
        if (!isBackendDown() && null != backendType && !backendType.isEmpty()) {

            final ProductRegistrationBackend sapProductRegistrationBackend = (ProductRegistrationBackend) genericFactory
                    .getBean(PREFIX.concat(backendType));

            registeredProducts = sapProductRegistrationBackend.getRegisteredProductByGuid(registrationNumber,
                    customerID);
        }
        return registeredProducts;
    }

    /**
     * Determine if the backend connection is available to send registration
     * request
     *
     * @return true if backend is up
     */
    @Override
    public boolean isBackendDown() {
        boolean currentlyDown = false;
        try {
            currentlyDown = getProductRegistrationBackendBusinessObject().isBackendDown();
        } catch (final BackendException e) {
            throw new ApplicationBaseRuntimeException("Cannot determine backend availability", e);
        }
        return currentlyDown;
    }

    /**
     * Get the current backend type.
     *
     * @return backend type
     */
    @Override
    public String getBackendType() {
        final SAPRFCDestinationModel defaultDestination = this.baseStoreService.getCurrentBaseStore()
                .getSAPConfiguration().getProductRegistrationRfcDestination();

        if (null != defaultDestination) {
            backendType = defaultDestination.getBackendType().getCode();
        }
        if (backendType == null) {
            backendType = moduleConfigurationAccess.getBackendType();
        }
        if (backendType == null || backendType.length() == 0) {
            LOG.debug("BackendType is empty");
        } else {
            LOG.debug("BackendType is " + backendType.toString());
        }
        return backendType;
    }

    @Override
    public boolean equals(final Object object) {
        final boolean result = false;
        if (null != object) {
            super.equals(object);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return techKey.hashCode();
    }

    /**
     * Returns ProductRegistrationBackend object
     *
     * @return ProductRegistrationBackend object
     * @throws BackendException
     */
    public ProductRegistrationBackend getProductRegistrationBackendBusinessObject() throws BackendException {
        return (ProductRegistrationBackend) getBackendBusinessObject();
    }

    /**
     * @param baseStoreService
     *            the baseStoreService to set
     */
    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

}
