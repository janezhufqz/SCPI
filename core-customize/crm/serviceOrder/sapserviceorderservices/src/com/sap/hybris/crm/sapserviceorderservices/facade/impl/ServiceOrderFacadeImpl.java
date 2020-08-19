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
package com.sap.hybris.crm.sapserviceorderservices.facade.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.crm.sapserviceorderservices.ServiceOrderData;
import com.sap.hybris.crm.sapserviceorderservices.ServiceOrderHistoryData;
import com.sap.hybris.crm.sapserviceorderservices.constants.SapserviceorderservicesConstants;
import com.sap.hybris.crm.sapserviceorderservices.data.ServiceOrderFilterData;
import com.sap.hybris.crm.sapserviceorderservices.data.ServiceOrderRelObjData;
import com.sap.hybris.crm.sapserviceorderservices.facade.ServiceOrderFacade;
import com.sap.hybris.crm.sapserviceorderservices.model.ServiceOrderModel;
import com.sap.hybris.crm.sapserviceorderservices.providers.ServiceOrderSortProvider;
import com.sap.hybris.crm.sapserviceorderservices.services.ServiceOrderService;

/**
 * @author SAP
 *
 */

public class ServiceOrderFacadeImpl implements ServiceOrderFacade {
    private static final Logger LOG = Logger.getLogger(ServiceOrderFacadeImpl.class.getName());
    private static final String ORDER_NOT_FOUND_FOR_CODE = "Order with code %s not found";
    private ServiceOrderService seriveOrderService;
    private Converter<ServiceOrderModel, ServiceOrderHistoryData> serviceOrderHistoryConverter;
    private Converter<ServiceOrderModel, ServiceOrderData> serviceOrderDetailsConverter;
    private ServiceOrderSortProvider sortValueProvider;
    private ConfigurationService configurationService;

    @Override
    public ServiceOrderData getServiceOrderDetails(final String orderCode) {
        ServiceOrderModel orderModel = null;
        try {
            orderModel = getSeriveOrderService().getOrderDtails(orderCode);
        }

        catch (final ModelNotFoundException e) {
            LOG.error(ORDER_NOT_FOUND_FOR_CODE, e);
            throw new UnknownIdentifierException(String.format(ORDER_NOT_FOUND_FOR_CODE, orderCode));
        }

        if (orderModel == null) {
            throw new UnknownIdentifierException(String.format(ORDER_NOT_FOUND_FOR_CODE, orderCode));
        }
        return getServiceOrderDetailsConverter().convert(orderModel);

    }

    @Override
    public SearchPageData<ServiceOrderHistoryData> getPagedOrderHistory(final PageableData pageableData,
            final String sortCode, final boolean sortOrder, final List<ServiceOrderFilterData> filters) {
        String sortFiledIntable = null;
        if (sortCode != null) {
            sortFiledIntable = sortValueProvider.getTypeAttributeForSortCode(sortCode);
        }
        final SearchPageData<ServiceOrderModel> orderResults = getSeriveOrderService().getOrderList(sortFiledIntable,
                sortOrder, pageableData, filters);
        return orderResults != null ? convertPageData(orderResults, sortCode, getServiceOrderHistoryConverter()) : null;

    }

    protected <S, T> SearchPageData<T> convertPageData(final SearchPageData<S> source, final String sortCode,
            final Converter<S, T> converter) {
        final SearchPageData<T> result = new SearchPageData<T>();
        final List<SortData> sorts = sortValueProvider.getAvailableSortWithSlected(sortCode);
        if (source.getPagination() != null) {
            source.getPagination().setSort(sortCode);
        }
        result.setPagination(source.getPagination());
        result.setSorts(sorts);
        result.setResults(Converters.convertAll(source.getResults(), converter));
        return result;
    }

    @Override
    public ServiceOrderData populateRelatedObject(ServiceOrderData serviceOrderData) {
        List<ServiceOrderRelObjData> relObjects = new ArrayList<ServiceOrderRelObjData>();
        final String requestId = serviceOrderData.getServiceRequestId();
        final String contractId = serviceOrderData.getServiceContractId();
        
        if (!StringUtils.isEmpty(requestId)) {
            relObjects.add(getRelatedObject(requestId, SapserviceorderservicesConstants.SRVICE_TICKET_TYPE));
        }
        
        if (!StringUtils.isEmpty(contractId)) {
            relObjects.add(getRelatedObject(contractId,SapserviceorderservicesConstants.SRVICE_CONTRACT_TYPE ));
        }
        serviceOrderData.setRelatedObjects(relObjects);
        return serviceOrderData;
    }

    protected ServiceOrderRelObjData getRelatedObject(final String objectId,String relObjType) {
       
        final String objectType = (String) getConfigurationService().getConfiguration().getProperty(
                relObjType);
        final String baseURL = (String) configurationService.getConfiguration().getProperty(
                SapserviceorderservicesConstants.PREFIX_URL + "." + objectType.toLowerCase());
    	
    	if (!StringUtils.isEmpty(objectId) && !StringUtils.isEmpty(baseURL) && !StringUtils.isEmpty(objectType)) {
            ServiceOrderRelObjData relServiceTicket = new ServiceOrderRelObjData();
            relServiceTicket.setObjectid(objectId);
            relServiceTicket.setObjectType(objectType);
            relServiceTicket.setObjecturl(baseURL);
            return relServiceTicket;
        }
        return null;
    }

    public ServiceOrderService getSeriveOrderService() {
        return seriveOrderService;
    }

    @Required
    public void setSeriveOrderService(final ServiceOrderService seriveOrderService) {
        this.seriveOrderService = seriveOrderService;
    }

    public Converter<ServiceOrderModel, ServiceOrderHistoryData> getServiceOrderHistoryConverter() {
        return serviceOrderHistoryConverter;
    }

    @Required
    public void setServiceOrderHistoryConverter(
            final Converter<ServiceOrderModel, ServiceOrderHistoryData> serviceOrderHistoryConverter) {
        this.serviceOrderHistoryConverter = serviceOrderHistoryConverter;
    }

    public Converter<ServiceOrderModel, ServiceOrderData> getServiceOrderDetailsConverter() {
        return serviceOrderDetailsConverter;
    }

    @Required
    public void setServiceOrderDetailsConverter(
            final Converter<ServiceOrderModel, ServiceOrderData> serviceOrderDetailsConverter) {
        this.serviceOrderDetailsConverter = serviceOrderDetailsConverter;
    }

    public ServiceOrderSortProvider getSortValueProvider() {
        return sortValueProvider;
    }

    @Required
    public void setSortValueProvider(final ServiceOrderSortProvider sortValueProvider) {
        this.sortValueProvider = sortValueProvider;
    }

    protected PaginationData createPaginationData() {
        return new PaginationData();
    }

    /**
     * @return the configurationService
     */
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    /**
     * @param configurationService
     *            the configurationService to set
     */
    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
