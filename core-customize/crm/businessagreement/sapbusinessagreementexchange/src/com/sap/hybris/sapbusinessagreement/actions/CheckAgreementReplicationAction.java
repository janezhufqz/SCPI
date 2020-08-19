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

import java.util.Set;

import org.apache.log4j.Logger;

import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementHeaderModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;

/**
 *
 */
public class CheckAgreementReplicationAction extends AbstractSimpleDecisionAction<AgreementApprovalProcessModel> {

    private static final Logger LOG = Logger.getLogger(CheckAgreementReplicationAction.class);

    @Override
    public Transition executeAction(final AgreementApprovalProcessModel agreementProcess)
            throws RetryLaterException, Exception {

        LOG.info("Entered Agreement replication check class.......");
        final CustomerModel customer = (CustomerModel) agreementProcess.getOrder().getUser();

        String ccinc = null;
        final Set<BusinessAgreementModel> agreements = customer.getBusinessagreement();
        final PaymentInfoModel paymentInfo = agreementProcess.getOrder().getPaymentInfo();

        if (paymentInfo instanceof CreditCardPaymentInfoModel && customer.getType().equals(CustomerType.REGISTERED)) {

            ccinc = ((CreditCardPaymentInfoModel) paymentInfo).getCardInc();

            for (final BusinessAgreementModel ba : agreements) {
                if (checkCardIncInHeader(ba, ccinc)) {
                    return Transition.OK;
                }
            }

        } else {

            LOG.info("Order payment is of not of type: " + CreditCardPaymentInfoModel._TYPECODE
                    + "or customer is Guest.Bypassing agreement replication.");
            return Transition.OK;
        }

        return Transition.NOK;
    }

    /**
     *
     */
    private boolean checkCardIncInHeader(final BusinessAgreementModel ba, final String ccinc) {

        for (final BusinessAgreementHeaderModel bh : ba.getBusinessagreementheader()) {
            if (ccinc.equalsIgnoreCase(bh.getCreditcardinc())
                    && bh.getBusinessagreement().getSapIsReplicated().equals(Boolean.TRUE)) {
                return true;
            }
        }
        return false;
    }

}
