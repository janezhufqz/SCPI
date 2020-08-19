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
package com.sap.hybris.sapbusinessagreement.inbound.events;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.CSVCellDecorator;

import java.util.Map;

import com.sap.hybris.sapbusinessagreement.constants.SapbusinessagreementexchangeConstants;
import com.sap.hybris.sapbusinessagreement.model.CRMBusinessAgreementClassModel;

/**
 *
 */
public class DefaultBusinessAgreementPaymentMethodDecorator implements
        CSVCellDecorator {

    @Override
    public String decorate(final int position,
            final Map<Integer, String> srcLine) {

        final String parsedValue = srcLine.get(Integer.valueOf(position));
        if (parsedValue != null && !parsedValue.isEmpty()) {
            final String paymentMethodIncValues[] = parsedValue.split(":");

            final String paymethodIncCode = paymentMethodIncValues[0];
            String countryCode = readCountryCode(paymentMethodIncValues[1]);
            if (countryCode == null) {
                countryCode = SapbusinessagreementexchangeConstants.DEFAULT_COUNTRY_CODE;
            }
            return paymethodIncCode + ":" + countryCode;
        }
        return "";
    }

    /**
     *
     */
    private String readCountryCode(final String businessagreementclass) {
        String countryCode = null;
        final FlexibleSearchService flexibleSearchService = (FlexibleSearchService) Registry
                .getApplicationContext().getBean("flexibleSearchService");
        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
                "SELECT {o:pk} FROM {CRMBusinessAgreementClass AS o} WHERE  {o.code} =?businessagreementclass");
        flexibleSearchQuery.addQueryParameter(
                SapbusinessagreementexchangeConstants.BUSINESS_AGREEMENT_CLASS,
                businessagreementclass);
        final SearchResult<CRMBusinessAgreementClassModel> result = flexibleSearchService
                .search(flexibleSearchQuery);
        if (result.getResult().isEmpty()) {
            countryCode = businessagreementclass;
        } else {
            final CRMBusinessAgreementClassModel agreementClassModel = result
                    .getResult().get(0);
            countryCode = agreementClassModel.getCountry();
        }

        return countryCode;
    }
}
