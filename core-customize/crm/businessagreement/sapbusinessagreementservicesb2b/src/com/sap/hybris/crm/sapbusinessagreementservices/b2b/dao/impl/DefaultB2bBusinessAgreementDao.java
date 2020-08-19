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
package com.sap.hybris.crm.sapbusinessagreementservices.b2b.dao.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.hybris.crm.sapbusinessagreementservices.b2b.dao.B2bBusinessAgreementDao;
import com.sap.hybris.crm.sapbusinessagreementservices.constants.SapbusinessagreementservicesConstants;
import com.sap.hybris.crm.sapbusinessagreementservices.dao.impl.DefaultBusinessAgreementDao;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;

public class DefaultB2bBusinessAgreementDao extends DefaultBusinessAgreementDao implements B2bBusinessAgreementDao {

    private static final String FIND_AGREEMENTS_BY_CUSTOMER_QUERY = SapbusinessagreementservicesConstants.SELECT
            + BusinessAgreementModel.PK + SapbusinessagreementservicesConstants.FROM + BusinessAgreementModel._TYPECODE
            + SapbusinessagreementservicesConstants.WHERE + BusinessAgreementModel.PARTNER + "} = ?customer ";

    private static final String SORT_AGREEMENTS_BY_CODE = " ORDER BY {" + BusinessAgreementModel.ID + "} DESC, {"
            + BusinessAgreementModel.PK + "}";

    @Override
    public SearchPageData<BusinessAgreementModel> findBusinessAgreementsForUnit(final B2BUnitModel b2bUnitModel,
            final PageableData pageableData) {

        final Map<String, Object> queryParams = new HashMap<>();

        queryParams.put(SapbusinessagreementservicesConstants.CUSTOMER, b2bUnitModel);

        final List<SortQueryData> sortQueries = Arrays.asList(createSortQueryData(
                SapbusinessagreementservicesConstants.SORT_CODE, createQuery(FIND_AGREEMENTS_BY_CUSTOMER_QUERY,
                        SapbusinessagreementservicesConstants.FILTER_CLAUSE, SORT_AGREEMENTS_BY_CODE)));

        return getPagedFlexibleSearchService().search(sortQueries, SapbusinessagreementservicesConstants.SORT_CODE,
                queryParams, pageableData);
    }
}
