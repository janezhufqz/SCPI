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
package com.sap.hybris.crm.sapservicecontractservices.b2b.dao.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.hybris.crm.contract.model.ServiceContractModel;
import com.sap.hybris.crm.sapservicecontractservices.b2b.dao.B2BServiceContractDao;
import com.sap.hybris.crm.sapservicecontractservices.dao.impl.DefaultServiceContractDao;

/**
 *
 */
public class DefaultB2BServiceContractDao extends DefaultServiceContractDao implements B2BServiceContractDao {

    protected static final String FIND_CONTRACTS_FOR_UNIT_QUERY = "SELECT {" + ServiceContractModel._TYPECODE + ":"
            + ServiceContractModel.PK + "} FROM {" + ServiceContractModel._TYPECODE + " as "
            + ServiceContractModel._TYPECODE + "} WHERE {" + ServiceContractModel._TYPECODE + ":"
            + ServiceContractModel.UNIT + "} = ?unit";

    /*
     * (non-Javadoc)
     *
     * @see com.sap.hybris.sapservicecontractfacades.dao.ServiceContractsDao#
     * getServiceContractsforUnit(de.hybris.platform. b2b.model.B2BUnitModel)
     */
    @Override
    public List<ServiceContractModel> getServiceContractsforUnit(final B2BUnitModel unit) {
        final Map<String, Object> queryParams = new HashMap<>(1);
        queryParams.put("unit", unit);
        final FlexibleSearchQuery query = new FlexibleSearchQuery(
                FIND_CONTRACTS_FOR_UNIT_QUERY + SORT_CONTRACTS_BY_DATE);
        query.getQueryParameters().putAll(queryParams);
        final SearchResult<ServiceContractModel> result = this.getFlexibleSearchService().search(query);
        return result.getResult();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sap.hybris.sapservicecontractfacades.dao.ServiceContractsDao#
     * getAllContractsForUnit(de.hybris.platform.b2b. model.B2BUnitModel,
     * de.hybris.platform.commerceservices.search.pagedata.PageableData)
     */
    @Override
    public SearchPageData<ServiceContractModel> getAllContractsForUnit(final B2BUnitModel unit,
            final PageableData pageableData) {
        final Map<String, Object> queryParams = new HashMap<>(1);
        queryParams.put("unit", unit);

        final List<SortQueryData> sortQueries = Arrays
                .asList(createSortQueryData(BY_DATE, FIND_CONTRACTS_FOR_UNIT_QUERY + SORT_CONTRACTS_BY_DATE));

        getPagedFlexibleSearchService().search(sortQueries, BY_DATE, queryParams, pageableData);

        return null;
    }

}