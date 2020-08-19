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
package com.sap.hybris.crm.sapbusinessagreementservices.dao.impl;

import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.hybris.crm.sapbusinessagreementservices.constants.SapbusinessagreementservicesConstants;
import com.sap.hybris.crm.sapbusinessagreementservices.dao.BusinessAgreementDao;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;

public class DefaultBusinessAgreementDao extends AbstractItemDao implements BusinessAgreementDao {

    private static final String FIND_AGREEMENTS_BY_B2C_CUSTOMER_QUERY = SapbusinessagreementservicesConstants.SELECT
            + BusinessAgreementModel.PK + SapbusinessagreementservicesConstants.FROM + BusinessAgreementModel._TYPECODE
            + SapbusinessagreementservicesConstants.WHERE + BusinessAgreementModel.CUSTOMER + "} = ?customer ";

    private static final String SORT_AGREEMENTS_BY_CODE = " ORDER BY {" + BusinessAgreementModel.ID + "} DESC, {"
            + BusinessAgreementModel.PK + "}";

    private static final String FIND_BUSINESS_AGREEMENT_BY_ID = SapbusinessagreementservicesConstants.SELECT
            + BusinessAgreementModel.PK + SapbusinessagreementservicesConstants.FROM + BusinessAgreementModel._TYPECODE
            + SapbusinessagreementservicesConstants.WHERE + BusinessAgreementModel.ID + "} = ?id ";
    private PagedFlexibleSearchService pagedFlexibleSearchService;

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

    @Override
    public SearchPageData<BusinessAgreementModel> findBusinessAgreementsForCustomer(final CustomerModel customerModel,
            final PageableData pageableData) {

        final Map<String, Object> queryParams = new HashMap<String, Object>();

        queryParams.put(SapbusinessagreementservicesConstants.CUSTOMER, customerModel);

        final List<SortQueryData> sortQueries = Arrays.asList(createSortQueryData(
                SapbusinessagreementservicesConstants.SORT_CODE, createQuery(FIND_AGREEMENTS_BY_B2C_CUSTOMER_QUERY,
                        SapbusinessagreementservicesConstants.FILTER_CLAUSE, SORT_AGREEMENTS_BY_CODE)));

        return getPagedFlexibleSearchService().search(sortQueries, SapbusinessagreementservicesConstants.SORT_CODE,
                queryParams, pageableData);
    }

    protected SortQueryData createSortQueryData(final String sortCode, final String query) {
        final SortQueryData result = new SortQueryData();
        result.setSortCode(sortCode);
        result.setQuery(query);
        return result;
    }

    protected String createQuery(final String... queryClauses) {
        final StringBuilder queryBuilder = new StringBuilder();

        for (final String queryClause : queryClauses) {
            queryBuilder.append(queryClause);
        }

        return queryBuilder.toString();
    }

    @Override
    public BusinessAgreementModel getBusinessAggreementDetails(final String businessAgreementId) {
        final Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(SapbusinessagreementservicesConstants.ID, businessAgreementId);

        final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(FIND_BUSINESS_AGREEMENT_BY_ID);

        fQuery.addQueryParameters(queryParams);

        final List<BusinessAgreementModel> businessAgreement = getFlexibleSearchService()
                .<BusinessAgreementModel> search(fQuery).getResult();

        return businessAgreement.get(0);
    }
}
