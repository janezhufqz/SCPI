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
package com.sap.hybris.crm.sapbusinessagreementfacades.converters.populator;

import de.hybris.platform.converters.Populator;

import com.sap.hybris.sapbusinessagreement.model.CRMBusinessAgreementPaymentMethodModel;
import com.sap.hybris.sapbusinessagreementfacades.data.BusinessAgreementPaymentMethod;

/**
 * populate payment method details of business agreement
 */
public class DefaultBusinessAgreementPaymentMethodPopulator
        implements
        Populator<CRMBusinessAgreementPaymentMethodModel, BusinessAgreementPaymentMethod> {

    @Override
    public void populate(final CRMBusinessAgreementPaymentMethodModel source,
            final BusinessAgreementPaymentMethod target) {
        if (source != null) {
            target.setPaymentMethod(source.getText());
        }
    }
}
