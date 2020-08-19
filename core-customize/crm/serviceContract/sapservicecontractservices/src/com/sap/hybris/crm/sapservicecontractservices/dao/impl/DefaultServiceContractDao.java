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
package com.sap.hybris.crm.sapservicecontractservices.dao.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.sap.hybris.crm.contract.model.SAPStatusModel;
import com.sap.hybris.crm.contract.model.ServiceContractModel;
import com.sap.hybris.crm.sapservicecontractservices.dao.ServiceContractDao;

import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * DAO layer to fetch results from backend using FlexibleSearchService.
 *
 * @author C5229488
 *
 */
public class DefaultServiceContractDao extends DefaultGenericDao<ServiceContractModel> implements ServiceContractDao {

    private PagedFlexibleSearchService pagedFlexibleSearchService;

    private static final String FIND_CONTRACTS_FOR_CUSTOMER_QUERY = "SELECT {" + ServiceContractModel._TYPECODE + ":"
            + ServiceContractModel.PK + "} FROM {" + ServiceContractModel._TYPECODE + " as "
            + ServiceContractModel._TYPECODE + "} WHERE {" + ServiceContractModel._TYPECODE + ":"
            + ServiceContractModel.CUSTOMER + "} = ?customer";

    private static final String FIND_SAP_STATUS_FOR_CODE_AND_PROFILE_QUERY = "SELECT {" + SAPStatusModel._TYPECODE + ":"
            + SAPStatusModel.PK + "} FROM {" + SAPStatusModel._TYPECODE + " as " + SAPStatusModel._TYPECODE
            + "} WHERE {" + SAPStatusModel._TYPECODE + ":" + SAPStatusModel.STATUSPROFILE + "} = ?statusProfile AND {"
            + SAPStatusModel._TYPECODE + ":" + SAPStatusModel.CODE + "} = ?code";

    private static final String FIND_ALL_SAP_STATUS_QUERY = "SELECT {" + SAPStatusModel._TYPECODE + ":"
            + SAPStatusModel.PK + "} FROM {" + SAPStatusModel._TYPECODE + " as " + SAPStatusModel._TYPECODE + "}";

    protected static final String BY_DATE = "byDate";

    protected static final String SORT_CONTRACTS_BY_DATE = " ORDER BY {" + ServiceContractModel._TYPECODE + ":"
            + ServiceContractModel.STARTDATE + "} DESC, {" + ServiceContractModel._TYPECODE + ":"
            + ServiceContractModel.PK + "}";

    /**
     * default constructor
     */
    public DefaultServiceContractDao() {
        super(ServiceContractModel._TYPECODE);
    }

    /**
     * @return the pagedFlexibleSearchService
     */
    public PagedFlexibleSearchService getPagedFlexibleSearchService() {
        return pagedFlexibleSearchService;
    }

    /**
     * @param pagedFlexibleSearchService
     *            the pagedFlexibleSearchService to set
     */
    public void setPagedFlexibleSearchService(final PagedFlexibleSearchService pagedFlexibleSearchService) {
        this.pagedFlexibleSearchService = pagedFlexibleSearchService;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sap.hybris.sapservicecontractfacades.dao.ServiceContractsDao#
     * getServiceContractsforCustomer(de.hybris.
     * platform.core.model.user.CustomerModel)
     */
    @Override
    public List<ServiceContractModel> getContractsByCustomer(final CustomerModel customer) {
        final Map<String, Object> queryParams = new HashMap<String, Object>(1);
        queryParams.put("customer", customer);
        final FlexibleSearchQuery query = new FlexibleSearchQuery(
                FIND_CONTRACTS_FOR_CUSTOMER_QUERY + SORT_CONTRACTS_BY_DATE);
        query.getQueryParameters().putAll(queryParams);
        final SearchResult<ServiceContractModel> result = this.getFlexibleSearchService().search(query);
        return result.getResult();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sap.hybris.sapservicecontractfacades.dao.ServiceContractsDao#
     * getAllContractsForCustomer(de.hybris.platform.
     * core.model.user.CustomerModel,
     * de.hybris.platform.commerceservices.search.pagedata.PageableData)
     */
    @Override
    public SearchPageData<ServiceContractModel> getAllContractsForCustomer(final CustomerModel customer,
            final PageableData pageableData) {
        final Map<String, Object> queryParams = new HashMap<String, Object>(1);
        queryParams.put("customer", customer);

        final List<SortQueryData> sortQueries = Arrays
                .asList(createSortQueryData(BY_DATE, FIND_CONTRACTS_FOR_CUSTOMER_QUERY + SORT_CONTRACTS_BY_DATE));

        return getPagedFlexibleSearchService().search(sortQueries, BY_DATE, queryParams, pageableData);
    }

    protected SortQueryData createSortQueryData(final String sortCode, final String query) {
        final SortQueryData result = new SortQueryData();
        result.setSortCode(sortCode);
        result.setQuery(query);
        return result;
    }

    @Override
    public SAPStatusModel getSAPStatusForCodeAndProfile(String code, String profile) {
        final Map<String, Object> queryParams = new HashMap<String, Object>(1);
        queryParams.put("code", code);
        queryParams.put("statusProfile", profile);
        final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_SAP_STATUS_FOR_CODE_AND_PROFILE_QUERY);
        query.getQueryParameters().putAll(queryParams);
        final SearchResult<SAPStatusModel> result = this.getFlexibleSearchService().search(query);
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult().get(0) : null;
    }

    @Override
    public List<SAPStatusModel> getAllSAPStatuses() {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ALL_SAP_STATUS_QUERY);
        final SearchResult<SAPStatusModel> result = this.getFlexibleSearchService().search(query);
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult() : null;

    }

}