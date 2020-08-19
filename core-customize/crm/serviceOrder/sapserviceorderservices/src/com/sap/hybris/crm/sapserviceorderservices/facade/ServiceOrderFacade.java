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
package com.sap.hybris.crm.sapserviceorderservices.facade;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.List;

import com.sap.hybris.crm.sapserviceorderservices.ServiceOrderData;
import com.sap.hybris.crm.sapserviceorderservices.ServiceOrderHistoryData;
import com.sap.hybris.crm.sapserviceorderservices.data.ServiceOrderFilterData;

/**
 * @author SAP
 *
 */
public interface ServiceOrderFacade {

    /**
     * Returns the order history of the current user or unit.
     *
     * @param pageableData
     *            paging information
     * @param sortCode
     *            sort code
     * @param sortOrder
     *            Sort Order
     * @param filters
     *            filter by order attributes
     *            
     * @return The service order history of the current user.
     */
    SearchPageData<ServiceOrderHistoryData> getPagedOrderHistory(PageableData pageableData, String sortCode,
            boolean assending, List<ServiceOrderFilterData> filters);

    /**
     * @param orderCode
     * @return ServiceOrderData
     */
    ServiceOrderData getServiceOrderDetails(String orderCode);
    
    /**
     * populates the service order's related object link
     * @param serviceOrderData
     * @return serviceOrderData
     */
    ServiceOrderData populateRelatedObject(ServiceOrderData serviceOrderData);
    

}
