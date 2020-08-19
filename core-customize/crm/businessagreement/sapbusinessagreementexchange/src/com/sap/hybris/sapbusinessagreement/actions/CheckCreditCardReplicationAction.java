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

import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.task.RetryLaterException;

import java.util.Set;

import com.google.common.collect.Sets;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementProcessModel;

/**
 *
 */
public class CheckCreditCardReplicationAction extends
        AbstractAction<BusinessAgreementProcessModel> {

    public enum Transition {
        OK, NOK, WAIT;
        public static Set<String> getStringValues() {
            final Set<String> res = Sets.newHashSet();

            for (final Transition transition : Transition.values()) {
                res.add(transition.toString());
            }
            return res;
        }
    }

    @Override
    public String execute(
            final BusinessAgreementProcessModel businessAgreementProcessModel)
            throws RetryLaterException, Exception {
        Transition transition;
        final CreditCardPaymentInfoModel creditCardPaymentInfoModel = businessAgreementProcessModel
                .getCreditCardPaymentInfo();
        if (creditCardPaymentInfoModel != null) {

            if (creditCardPaymentInfoModel.getSapIsReplicated().booleanValue()) {
                transition = Transition.OK;
            }

            else {
                transition = Transition.WAIT;

            }

        } else {
            transition = Transition.NOK;
        }
        return transition.toString();

    }

    @Override
    public Set<String> getTransitions() {
        return Transition.getStringValues();
    }

}
