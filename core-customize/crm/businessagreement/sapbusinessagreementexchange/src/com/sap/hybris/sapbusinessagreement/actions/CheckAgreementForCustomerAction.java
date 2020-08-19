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

import de.hybris.platform.agreement.model.AgreementApprovalProcessModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.task.RetryLaterException;

import org.apache.log4j.Logger;

/**
 *
 */
public class CheckAgreementForCustomerAction extends
        AbstractSimpleDecisionAction<AgreementApprovalProcessModel> {

    private static final Logger LOG = Logger
            .getLogger(CheckAgreementReplicationAction.class);

    @Override
    public Transition executeAction(
            final AgreementApprovalProcessModel agreementProcess)
            throws RetryLaterException, Exception {

        LOG.info("Entered Customer agreements check class.......");

        final PaymentInfoModel paymentInfo = agreementProcess.getOrder()
                .getPaymentInfo();
        final CustomerModel customer = (CustomerModel) agreementProcess
                .getOrder().getUser();

        if (paymentInfo instanceof CreditCardPaymentInfoModel
                && customer.getType().equals(CustomerType.REGISTERED)
                && customer.getBusinessagreement().isEmpty()) {
            return Transition.NOK;
        }
        return Transition.OK;
    }

}
