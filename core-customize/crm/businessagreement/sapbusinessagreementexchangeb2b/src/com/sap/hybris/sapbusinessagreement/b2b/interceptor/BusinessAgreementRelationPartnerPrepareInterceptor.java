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
package com.sap.hybris.sapbusinessagreement.b2b.interceptor;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementPartnersModel;

/**
 *
 */
public class BusinessAgreementRelationPartnerPrepareInterceptor
        implements PrepareInterceptor<BusinessAgreementPartnersModel> {

    private FlexibleSearchService flexibleSearchService;

    private static final String FIND_CUSTOMERS_BY_CUSTOMERID_QUERY = "SELECT {" + CustomerModel.PK + "} FROM {"
            + CustomerModel._TYPECODE + "} WHERE {" + CustomerModel.CUSTOMERID + "} = ?customerID ";

    private static final String FIND_B2BUNIT_BY_UID_QUERY = "SELECT {" + B2BUnitModel.PK + "} FROM {"
            + B2BUnitModel._TYPECODE + "} WHERE {" + B2BUnitModel.UID + "} = ?uid ";

    @Override
    public void onPrepare(final BusinessAgreementPartnersModel businessAgreementPartnersModel,
            final InterceptorContext interceptorContext) throws InterceptorException {
        if (businessAgreementPartnersModel.getCustomerguid() == null
                && businessAgreementPartnersModel.getPartnerguid2() == null) {
            final String partnerId = businessAgreementPartnersModel.getPartnerid2();
            FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_CUSTOMERS_BY_CUSTOMERID_QUERY);
            flexibleSearchQuery.addQueryParameter(CustomerModel.CUSTOMERID, partnerId);
            SearchResult<Object> result = flexibleSearchService.search(flexibleSearchQuery);
            if (result.getCount() > 0) {
                final CustomerModel customerModel = (CustomerModel) result.getResult().get(0);
                businessAgreementPartnersModel.setCustomerguid(customerModel);
            } else {
                flexibleSearchQuery = new FlexibleSearchQuery(FIND_B2BUNIT_BY_UID_QUERY);
                flexibleSearchQuery.addQueryParameter(B2BUnitModel.UID, partnerId);
                result = flexibleSearchService.search(flexibleSearchQuery);
                if (result.getCount() > 0) {
                    final B2BUnitModel b2bUnitModel = (B2BUnitModel) result.getResult().get(0);
                    businessAgreementPartnersModel.setPartnerguid2(b2bUnitModel);
                }
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
