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
package com.sap.hybris.sapbusinessagreement.b2b.service.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;

import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;
import com.sap.hybris.sapbusinessagreement.service.impl.DefaultBusinessAgreementCreateService;

/**
 *
 */
public class DefaultB2bBusinessAgreementCreateService extends DefaultBusinessAgreementCreateService {

    @Override
    protected BusinessAgreementModel createBusinessAgreementModel(
            final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final String businessAgreementGuid) {
        final BusinessAgreementModel businessAgreementModel = getModelService().create(BusinessAgreementModel.class);

        businessAgreementModel.setGuid(businessAgreementGuid);
        businessAgreementModel.setId(getBusinessAgreementCreateServiceUtility().generateBusinessAgreementId());

        final UserModel userModel = creditCardPaymentInfoModel.getUser();
        if (userModel instanceof B2BCustomerModel) {
            final B2BCustomerModel b2BCustomerModel = (B2BCustomerModel) userModel;
            businessAgreementModel.setPartner(b2BCustomerModel.getDefaultB2BUnit());

        } else if (userModel instanceof CustomerModel) {
            final CustomerModel customerModel = (CustomerModel) userModel;
            businessAgreementModel.setCustomer(customerModel);
        }

        return businessAgreementModel;
    }

}
