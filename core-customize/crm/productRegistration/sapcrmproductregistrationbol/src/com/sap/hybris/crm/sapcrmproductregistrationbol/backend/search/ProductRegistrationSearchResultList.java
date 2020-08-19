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

import java.util.List;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.productregistrationfacades.data.ProductRegistrationData;

/**
 * BOL representation of product registration search.
 *
 * @author C5229484
 *
 */
public interface ProductRegistrationSearchResultList {

    /**
     * Adds a search result item
     *
     * @param result
     *            Search result
     */
    void add(ProductRegistrationData result);

    /**
     * Returns total number of search results
     *
     * @return Total number of search results
     */
    int size();

    /**
     * Clears search result list
     */
    void clear();

    /**
     * Sets paging data that we need for accessing the result list
     *
     * @param pageableData
     */
    void setPageableData(PageableData pageableData);

    /**
     * Returns the result list, taking the paging data into account. In case no
     * paging data is provided, a runtime exception is thrown.
     *
     * @return List<ProductRegistrationData> with search page data
     */
    List<ProductRegistrationData> getSearchResult();

    /**
     * return all the results
     *
     * @return List<ProductRegistrationData>
     */
    List<ProductRegistrationData> getResults();

}
