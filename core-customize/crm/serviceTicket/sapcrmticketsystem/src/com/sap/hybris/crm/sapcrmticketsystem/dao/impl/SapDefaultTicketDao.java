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
package com.sap.hybris.crm.sapcrmticketsystem.dao.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.hybris.crm.sapcrmticketsystem.dao.SapTicketDao;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.ticket.dao.impl.DefaultTicketDao;
import de.hybris.platform.ticket.model.CsTicketModel;

/**
 * @author C5229484
 *
 */
public class SapDefaultTicketDao extends DefaultTicketDao implements SapTicketDao {

    private static final String FIND_TICKETS_BY_IBASE_ID = "SELECT {cs.pk} FROM {CsTicket AS cs JOIN CsTicketRelatedObject AS obj ON {cs:pk}={obj.csticket}}  WHERE {obj.objectId} = ?objectid AND {obj.ObjectType} = ?objecttype AND {cs.customer} = ?user";

    /**
     * Override the default ticket fetching mechanism by removing the base site
     * from the query.
     */
    @Override
    public SearchPageData<CsTicketModel> findTicketsByCustomerOrderByModifiedTime(final UserModel user,
            final BaseSiteModel baseSite, final PageableData pageableData) {
        ServicesUtil.validateParameterNotNull(user, "Customer must not be null");
        ServicesUtil.validateParameterNotNull(baseSite, "Store must not be null");

        final Map queryParams = new HashMap();
        queryParams.put("user", user);
        final List sortQueries = Arrays.asList(new SortQueryData[] {
                createSortQueryData("byDate",
                        "SELECT {pk} FROM {CsTicket} WHERE {customer} = ?user  ORDER BY {modifiedtime} DESC"),
                createSortQueryData("byTicketId",
                        "SELECT {pk} FROM {CsTicket} WHERE {customer} = ?user  ORDER BY {ticketID} DESC"),
                createSortQueryData("byCategory",
                        "SELECT {pk} FROM {CsTicket} WHERE {customer} = ?user  ORDER BY {category} DESC") });

        return getPagedFlexibleSearchService().search(sortQueries, "byDate", queryParams, pageableData);
    }

    /**
     * Query to find list of tickets for specific ibase.
     *
     * @param customer
     * @param iBaseID
     * @param objectType
     * @return List of csticket models
     */
    @Override
    public List<CsTicketModel> findTicketsByIbaseId(final UserModel customer, final String iBaseID,
            final String objectType) {
        if (customer == null) {
            throw new IllegalArgumentException("customer must not be null");
        }
        final Map queryParams = new HashMap();
        queryParams.put("user", customer);
        queryParams.put("objectid", iBaseID);
        queryParams.put("objecttype", objectType);

        final SearchResult<CsTicketModel> result = getFlexibleSearchService().search(FIND_TICKETS_BY_IBASE_ID,
                queryParams);
        return result.getResult();
    }
}
