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
package com.sap.hybris.sapbusinessagreement.actions;

import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.task.RetryLaterException;

import org.apache.log4j.Logger;

import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementProcessModel;
import com.sap.hybris.sapbusinessagreement.outbound.DefaultCRMBusinessAgreementExportService;

/**
 *
 */
public class BusinessAgreementPublishAction extends
        AbstractSimpleDecisionAction<BusinessAgreementProcessModel> {
    private static final Logger LOGGER = Logger
            .getLogger(com.sap.hybris.sapbusinessagreement.actions.BusinessAgreementPublishAction.class
                    .getName());
    private DefaultCRMBusinessAgreementExportService defaultCRMBusinessAgreementExportService;

    /**
     * @return the defaultCRMBusinessAgreementExportService
     */
    public DefaultCRMBusinessAgreementExportService getDefaultCRMBusinessAgreementExportService() {
        return defaultCRMBusinessAgreementExportService;
    }

    /**
     * @param defaultCRMBusinessAgreementExportService
     *            the defaultCRMBusinessAgreementExportService to set
     */
    public void setDefaultCRMBusinessAgreementExportService(
            final DefaultCRMBusinessAgreementExportService defaultCRMBusinessAgreementExportService) {
        this.defaultCRMBusinessAgreementExportService = defaultCRMBusinessAgreementExportService;
    }

    @Override
    public Transition executeAction(
            final BusinessAgreementProcessModel businessAgreementProcessModel)
            throws RetryLaterException, Exception {
        if (businessAgreementProcessModel.getCreditCardPaymentInfo()
                .getSapIsReplicated().booleanValue()
                && businessAgreementProcessModel.getBusinessAgreement() != null) {
            LOGGER.info("business agreement sent to datahub------------------"
                    + businessAgreementProcessModel.getBusinessAgreement()
                    + "---------------------------"
                    + businessAgreementProcessModel.getCreditCardPaymentInfo()
                            .getCardInc());
            defaultCRMBusinessAgreementExportService
                    .updateBusinessAgreement(businessAgreementProcessModel
                            .getBusinessAgreement());

            return Transition.OK;

        } else {
            return Transition.NOK;
        }
    }
}
