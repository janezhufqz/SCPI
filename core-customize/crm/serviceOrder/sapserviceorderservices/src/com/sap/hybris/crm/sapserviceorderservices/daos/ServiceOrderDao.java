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
package com.sap.hybris.crm.sapserviceorderservices.daos;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.List;

import com.sap.hybris.crm.sapserviceorderservices.data.ServiceOrderFilterData;
import com.sap.hybris.crm.sapserviceorderservices.model.ServiceOrderModel;

/**
 * @author SAP
 *
 */
public interface ServiceOrderDao extends Dao {

    /**
     * Finds service order for code
     *
     * @param code
     *            the code
     * @return ServiceOrderModel associate with current customer
     */
    ServiceOrderModel findServiceOrderByCode(String code);

    /**
     *@param pageableData
     *            pageableData contains page information
     * @param sortCode
     *            sort code
     * @param sortOrder
     *            Sort Order
     * @param filters
     *            filter by order attributes
     * @return The list of Service orders
     */
    SearchPageData<ServiceOrderModel> findOrders(PageableData pageableData, String sortCode, boolean sortOrder,
            final List<ServiceOrderFilterData> filters);

}
