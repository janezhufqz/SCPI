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
package com.sap.hybris.crm.sapcrmproductregistrationbol.backend;

import java.util.List;
import java.util.Map;

import de.hybris.platform.productregistrationfacades.data.ProductRegistrationData;
import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;

/**
 * @author C5229484
 *
 */
public interface ProductRegistrationBackend extends BackendBusinessObject {

    /**
     * This method triggers JCO call to backend system to create product
     * registration request and then create an instance in Hybris from the
     * result of the call
     *
     * @param registrationParameters
     *            product to be registered
     * @param partnerNo
     *            customer requesting the registration
     * @throws BackendException
     */
    public void createProductregistration(final Map<String, String> registrationParameters, final String partnerNo)
            throws BackendException;

    /**
     * Check if backend connectivity is available for product registration
     *
     * @return true if backend available
     */
    public boolean isBackendDown();

    /**
     * Get list of registered products for customer
     *
     * @param customerID
     */
    public List<ProductRegistrationData> getRegisteredProducts(String customerID) throws BackendException;

    /**
     * Get the details of product registration by Guid
     *
     * @param registrationGuid
     * @param customerID
     * @return ProductRegistrationData
     * @throws BackendException
     */
    ProductRegistrationData getRegisteredProductByGuid(String registrationGuid, String customerID)
            throws BackendException;

}
