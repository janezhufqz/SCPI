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
package com.sap.hybris.productregistrationservices.service;

import java.util.List;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.productregistrationfacades.data.ProductRegistrationData;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;

/**
 * @author C5229484
 *
 */
public interface ProductRegistrationService {

    /**
     * Get list of all the registered products along with pagination details for
     * the user
     *
     * @param user
     *            current logged in user
     * @return SearchPageData
     */
    List<ProductRegistrationData> getRegisteredProducts(String user) throws BackendException;

    /**
     * @param guid
     * @return ProductModel
     */
    ProductModel getProductByGuid(String guid);

    /**
     * Register a new product
     *
     * @param guid
     * @param customerID
     * @throws BackendException
     */
    void createProductRegistration(String guid, String customerID) throws BackendException;

    /**
     * Get the product registration details by guid
     *
     * @param registrationNumber
     * @param customerID
     *            the customer ID for which registration is to be fetched
     * @return ProductRegistrationData
     */
    ProductRegistrationData getRegisteredProductByGuid(String registrationNumber, String customerID)
            throws BackendException;
}
