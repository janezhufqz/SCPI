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
package com.sap.hybris.sapbusinessagreement.inbound.impl;

import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapbusinessagreement.constants.SapbusinessagreementexchangeConstants;
import com.sap.hybris.sapbusinessagreement.inbound.BusinessAgreementNotificationHelper;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementHeaderModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;

/**
 *
 */
public class DefaultBusinessAgreementNotificationHelper implements
        BusinessAgreementNotificationHelper {

    private BusinessProcessService businessProcessService;
    private FlexibleSearchService flexibleSearchService;

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
    @Required
    public void setFlexibleSearchService(
            final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    /**
     * @return the businessProcessService
     */
    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    /**
     * @param businessProcessService
     *            the businessProcessService to set
     */
    @Required
    public void setBusinessProcessService(
            final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    protected BusinessAgreementModel readBusinessAgreement(
            final String businessAgreementGUID) {

        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
                "SELECT {o:pk} FROM {BusinessAgreement AS o} WHERE  {o.guid} = ?guid");

        flexibleSearchQuery.addQueryParameter(
                SapbusinessagreementexchangeConstants.GUID,
                businessAgreementGUID);

        final BusinessAgreementModel businessAgreementModel = getFlexibleSearchService()
                .searchUnique(flexibleSearchQuery);

        if (businessAgreementModel == null) {

            throw new IllegalArgumentException(
                    String.format(
                            "Error while processing inbound IDoc with business Agreement code [%s] that does not exist!",
                            businessAgreementGUID));
        }

        return businessAgreementModel;
    }

    @Override
    public void processBusinessAgreementConfirmationFromHub(
            final String businessAgreementGUID) {
        final BusinessAgreementModel businessAgreementModel = readBusinessAgreement(businessAgreementGUID);
        if (businessAgreementModel.getCustomer() != null) {
            final String customerId = businessAgreementModel.getCustomer()
                    .getCustomerID();

            final String cardInc = getBusinessAgreementHeaderModel(
                    businessAgreementModel).getCreditcardinc();

            final String eventName = SapbusinessagreementexchangeConstants.BUSINESS_AGREEMENT_REPLICATION_EVENT
                    + "_" + customerId + "_" + cardInc;

            getBusinessProcessService().triggerEvent(eventName);
        }
    }

    private BusinessAgreementHeaderModel getBusinessAgreementHeaderModel(
            final BusinessAgreementModel source) {
        BusinessAgreementHeaderModel businessAgreementHeaderModel = null;
        if (source.getBusinessagreementheader() != null) {
            final Set<BusinessAgreementHeaderModel> businessAgreementHeaderSet = source
                    .getBusinessagreementheader();

            if (!businessAgreementHeaderSet.isEmpty()) {
                businessAgreementHeaderModel = businessAgreementHeaderSet
                        .iterator().next();
            }
        }
        return businessAgreementHeaderModel;
    }

}
