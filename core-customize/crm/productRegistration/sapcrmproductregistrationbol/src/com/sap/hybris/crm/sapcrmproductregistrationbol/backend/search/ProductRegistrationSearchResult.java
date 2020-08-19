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
package com.sap.hybris.crm.sapcrmproductregistrationbol.backend.search;

import de.hybris.platform.productregistrationfacades.data.ProductRegistrationData;

/**
 * BOL representation of an product registration search result.
 *
 * @author C5229484
 *
 */
public interface ProductRegistrationSearchResult {
    /**
     * get search result from backend
     *
     * @return result
     */
    ProductRegistrationData getSearchResult();

    /**
     * Setter for search result
     *
     * @param registration
     */
    void setSearchResult(final ProductRegistrationData registration);

}
