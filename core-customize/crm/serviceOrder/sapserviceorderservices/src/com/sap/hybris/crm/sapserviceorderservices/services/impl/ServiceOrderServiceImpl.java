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
package com.sap.hybris.crm.sapserviceorderservices.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.crm.sapserviceorderservices.daos.ServiceOrderDao;
import com.sap.hybris.crm.sapserviceorderservices.data.ServiceOrderFilterData;
import com.sap.hybris.crm.sapserviceorderservices.model.ServiceOrderModel;
import com.sap.hybris.crm.sapserviceorderservices.services.ServiceOrderService;

/**
 * @author SAP
 *
 */
public class ServiceOrderServiceImpl implements ServiceOrderService {

    private ServiceOrderDao serviceOrderDao;

    /**
     * @return the serviceOrderDao
     */
    public ServiceOrderDao getServiceOrderDao() {
        return serviceOrderDao;
    }

    /**
     * @param serviceOrderDao
     *            the serviceOrderDao to set
     */
    @Required
    public void setServiceOrderDao(final ServiceOrderDao serviceOrderDao) {
        this.serviceOrderDao = serviceOrderDao;
    }

    @Override
    public ServiceOrderModel getOrderDtails(final String code) {
        validateParameterNotNull(code, "code cannot be null");
        return getServiceOrderDao().findServiceOrderByCode(code);
    }

    @Override
    public SearchPageData<ServiceOrderModel> getOrderList(final String sortCode, final boolean sortOrder,
            final PageableData pageableData, final List<ServiceOrderFilterData> filters) {
        return getServiceOrderDao().findOrders(pageableData, sortCode, sortOrder, filters);
    }

}
