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
package com.sap.hybris.productregistrationfacades.registration;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.productregistrationfacades.data.ProductRegistrationData;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;

/**
 * @author C5229484
 *
 */
public interface ProductRegistrationFacade {

    /**
     * Get the list of registered products for the logged in customer
     *
     * @param pageableData
     * @return SearchPageData
     */
    SearchPageData<ProductRegistrationData> getRegisteredProducts(PageableData pageableData) throws BackendException;

    /**
     * Create a new product registration
     *
     * @param populateProductRegistrationData
     * @return ProductRegistrationData
     * @throws BackendException
     */
    ProductRegistrationData createProductRegistration(ProductRegistrationData populateProductRegistrationData)
            throws BackendException;

    /**
     * Get the product registration details by guid
     *
     * @param encodeHTML
     * @return ProductRegistrationData
     */
    ProductRegistrationData getRegisteredProductByGuid(String encodeHTML) throws BackendException;
}
