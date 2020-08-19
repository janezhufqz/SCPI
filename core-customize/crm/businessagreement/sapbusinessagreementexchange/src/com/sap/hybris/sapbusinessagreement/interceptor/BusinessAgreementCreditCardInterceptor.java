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

import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import org.apache.log4j.Logger;

import com.sap.hybris.sapbusinessagreement.constants.SapbusinessagreementexchangeConstants;

/**
 *
 */
public class BusinessAgreementCreditCardInterceptor implements
        ValidateInterceptor<CreditCardPaymentInfoModel> {
    private static final Logger LOGGER = Logger
            .getLogger(com.sap.hybris.sapbusinessagreement.interceptor.BusinessAgreementCreditCardInterceptor.class
                    .getName());

    private BusinessProcessService businessProcessService;

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
    public void setBusinessProcessService(
            final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    @Override
    public void onValidate(
            final CreditCardPaymentInfoModel creditCardPaymentInfoModel,
            final InterceptorContext ctx) throws InterceptorException {
        if (ctx.isModified(creditCardPaymentInfoModel,
                CreditCardPaymentInfoModel.SAPISREPLICATED)
                && !ctx.isNew(creditCardPaymentInfoModel)
                && creditCardPaymentInfoModel.isSaved()
                && creditCardPaymentInfoModel.getSapIsReplicated()
                        .booleanValue()) {

            final CustomerModel customerModel = (CustomerModel) creditCardPaymentInfoModel
                    .getUser();
            LOGGER.info("credit card notification  ------------------"
                    + creditCardPaymentInfoModel.getCardInc());

            getBusinessProcessService()
                    .triggerEvent(
                            SapbusinessagreementexchangeConstants.BUSINESS_AGREEMENT_EVENT
                                    + "_"
                                    + customerModel.getCustomerID()
                                    + "_"
                                    + creditCardPaymentInfoModel.getCardInc());
        }
    }

}
