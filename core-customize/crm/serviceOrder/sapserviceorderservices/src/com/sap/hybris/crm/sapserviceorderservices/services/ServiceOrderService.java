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
package com.sap.hybris.crm.sapserviceorderservices.services;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.List;

import com.sap.hybris.crm.sapserviceorderservices.data.ServiceOrderFilterData;
import com.sap.hybris.crm.sapserviceorderservices.model.ServiceOrderModel;

/**
 * @author SAP
 *
 */
public interface ServiceOrderService {
    /**
     * @param sortCode
     *            sort code
     * @param sortOrder
     *            Sort Order
     * @param pageableData
     * @param filters
     * @return SearchPageData<ServiceOrderModel>
     */
    SearchPageData<ServiceOrderModel> getOrderList(String sortCode, boolean sortOrder, final PageableData pageableData,
            final List<ServiceOrderFilterData> filters);

    /**
     * @param code
     * @return ServiceOrderModel
     */
    ServiceOrderModel getOrderDtails(String code);

}
