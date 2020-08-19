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
package com.sap.hybris.crm.sapcrmproductregistrationbol.businessobject;

import java.util.List;
import java.util.Map;

import de.hybris.platform.productregistrationfacades.data.ProductRegistrationData;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;

/**
 * @author C5230710
 *
 */
public interface ProductRegistrationBO {

    /**
     * This method triggers the JCO call to SAP backend system to craete request
     * for product registration
     *
     * @param createRegistrationParameters
     *            product guid to be registered
     * @param partnerNo
     *            customer for which request is initiated
     * @param backendType
     * @throws BackendException
     */
    void createProductregistration(final Map<String, String> createRegistrationParameters, final String partnerNo,
            final String backendType) throws BackendException;

    /**
     * Determine if the backend connection is available to send registration
     * request
     *
     * @return true if backend is up
     */
    boolean isBackendDown();

    /**
     * Get list of registered products for customer
     *
     * @param customerID
     * @param backendType
     */
    List<ProductRegistrationData> getRegisteredProducts(String customerID, String backendType) throws BackendException;

    /**
     * Get the product registration details by guid
     *
     * @param registrationNumber
     * @param customerID
     * @return ProductRegistrationData
     */
    ProductRegistrationData getRegisteredProductByGuid(String registrationNumber, String backendType, String customerID)
            throws BackendException;

}
