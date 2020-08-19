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
package com.sap.hybris.sapbusinessagreement.interceptor;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;

/**
 *
 */
public class BusinessAgreementPartnerPrepareInterceptor implements PrepareInterceptor<BusinessAgreementModel> {

    private FlexibleSearchService flexibleSearchService;

    private static final String FIND_CUSTOMERS_BY_CUSTOMERID_QUERY = "SELECT {" + CustomerModel.PK + "} FROM {"
            + CustomerModel._TYPECODE + "} WHERE {" + CustomerModel.CUSTOMERID + "} = ?customerID ";

    @Override
    public void onPrepare(final BusinessAgreementModel businessAgreementModel,
            final InterceptorContext interceptorContext) throws InterceptorException {
        if (businessAgreementModel.getCustomer() == null) {
            final String partnerId = businessAgreementModel.getPartnerid();
            final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_CUSTOMERS_BY_CUSTOMERID_QUERY);
            flexibleSearchQuery.addQueryParameter(CustomerModel.CUSTOMERID, partnerId);
            final SearchResult<Object> result = flexibleSearchService.search(flexibleSearchQuery);
            if (result.getCount() > 0) {
                final CustomerModel customerModel = (CustomerModel) result.getResult().get(0);
                businessAgreementModel.setCustomer(customerModel);
            }
        }
    }

    /**
     * @return the flexibleSearchService
     */
    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    /**
     * @param flexibleSearchService
     *            the flexibleSearchService to set
     */
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

}
