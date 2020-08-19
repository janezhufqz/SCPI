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

package com.sap.hybris.crm.sapserviceorderservices.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.exceptions.FlexibleSearchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.crm.sapserviceorderservices.constants.SapserviceorderservicesConstants;
import com.sap.hybris.crm.sapserviceorderservices.daos.ServiceOrderDao;
import com.sap.hybris.crm.sapserviceorderservices.data.ServiceOrderFilterData;
import com.sap.hybris.crm.sapserviceorderservices.model.ServiceOrderModel;

/**
 * @author SAP
 *
 */
public class ServiceOrderDaoImpl extends AbstractItemDao implements ServiceOrderDao {
    private static final Logger LOG = Logger.getLogger(ServiceOrderDaoImpl.class.getName());
    private PagedFlexibleSearchService pagedFlexibleSearchService;

    private static final String DESC = "DESC";
    private static final String ASC = "ASC";
    private static final String REGEX_FOR_ORDER_CODE = "[^\\d]";

    private static final String SERVICE_ORDER_COMMON_QUERY = "SELECT {" + ServiceOrderModel.PK + "}, {"
            + ServiceOrderModel.CREATIONTIME + "}, {" + ServiceOrderModel.CODE + "} FROM {"
            + ServiceOrderModel._TYPECODE + "} WHERE";

    private static final String FIND_SERVICEORDERS_BY_CODE_QUERY = SERVICE_ORDER_COMMON_QUERY + " {"
            + ServiceOrderModel.CODE + "} = ?code AND {" + ServiceOrderModel.VERSIONID + "} IS NULL";

    private static final String FIND_SERVICEORDERS_QUERY = SERVICE_ORDER_COMMON_QUERY + " {"
            + ServiceOrderModel.VERSIONID + "} IS NULL";

    @Override
    public ServiceOrderModel findServiceOrderByCode(final String orderCode) {
        validateParameterNotNull(orderCode, "Code must not be null");
        final String serviceOrderCode = orderCode.replaceAll(REGEX_FOR_ORDER_CODE, "");
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("code", serviceOrderCode);
        return getFlexibleSearchService().searchUnique(
                new FlexibleSearchQuery(FIND_SERVICEORDERS_BY_CODE_QUERY, queryParams));

    }

    @Override
    public SearchPageData<ServiceOrderModel> findOrders(final PageableData pageableData, final String sortCode,
            final boolean sortOrder, final List<ServiceOrderFilterData> filters) {
        SearchPageData<ServiceOrderModel> searchdata = null;
        StringBuilder query = new StringBuilder(FIND_SERVICEORDERS_QUERY);

        try {
            query = query.append(getQuery(sortCode, sortOrder));
            // appending filters into query
            if (filters != null) {
                query.append(getQueryApplyingFilter(filters));
            }
            // if short query null query will not consider for shorting
            searchdata = getPagedFlexibleSearchService().search(query.toString(), null, pageableData);
            searchdata.getPagination().setSort(sortCode);
            return searchdata;
        } catch (final FlexibleSearchException e) {
            return handelException(e, query.toString(), searchdata);

        }

    }

    /**
     * @param filters
     * @return String returns the filter query after appending all filters
     */
    protected String getQueryApplyingFilter(final List<ServiceOrderFilterData> filters) {
        final StringBuilder queryFilter = new StringBuilder("");
        for (final ServiceOrderFilterData searchfilter : filters) {
            if (searchfilter.getFilterKey() != null && searchfilter.getFilterOperator() != null
                    && searchfilter.getFilterValue() != null) {
                if (searchfilter.getFilterValue().equals(SapserviceorderservicesConstants.NULL_FILTER)) {
                    queryFilter.append(" AND  {" + searchfilter.getFilterKey() + "} IS NULL ");
                } else {
                    queryFilter.append(" AND  {" + searchfilter.getFilterKey() + "} "
                            + searchfilter.getFilterOperator() + " '" + searchfilter.getFilterValue() + "' ");
                }
            }

        }
        return queryFilter.toString();
    }

    /**
     * @param sortCode
     * @param sortOrder
     * @return String returns the sort query.
     */
    protected String getQuery(final String sortCode, final boolean sortOrder) {
        if (StringUtils.isEmpty(sortCode)) {
            return "";
        }
        final String sortOrderToAppend = sortOrder ? ASC : DESC;
        return " ORDER BY {" + sortCode + "} " + sortOrderToAppend + "";
    }

    protected SearchPageData<ServiceOrderModel> handelException(final Exception e, final String query,
            final SearchPageData<ServiceOrderModel> searchresultData) {
        LOG.error("Service Order Histroy Query : [" + query + "] not correct", e);
        return searchresultData;
    }

    @Required
    public void setPagedFlexibleSearchService(final PagedFlexibleSearchService pagedFlexibleSearchService) {
        this.pagedFlexibleSearchService = pagedFlexibleSearchService;
    }

    protected PagedFlexibleSearchService getPagedFlexibleSearchService() {
        return pagedFlexibleSearchService;
    }

}
