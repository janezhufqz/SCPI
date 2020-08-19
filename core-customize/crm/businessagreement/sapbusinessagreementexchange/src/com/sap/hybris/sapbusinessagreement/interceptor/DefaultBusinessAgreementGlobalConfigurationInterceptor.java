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

import de.hybris.platform.sap.core.configuration.model.SAPGlobalConfigurationModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.util.localization.Localization;

import org.apache.log4j.Logger;

import com.sap.hybris.sapbusinessagreement.constants.SapbusinessagreementexchangeConstants;
import com.sap.hybris.sapbusinessagreement.model.CRMBusinessAgreementClassModel;
import com.sap.hybris.sapbusinessagreement.model.CRMBusinessAgreementPaymentMethodModel;

/**
 *
 */
public class DefaultBusinessAgreementGlobalConfigurationInterceptor implements
        ValidateInterceptor<SAPGlobalConfigurationModel> {

    private static final Logger LOGGER = Logger
            .getLogger(com.sap.hybris.sapbusinessagreement.interceptor.DefaultBusinessAgreementGlobalConfigurationInterceptor.class
                    .getName());

    @Override
    public void onValidate(
            final SAPGlobalConfigurationModel sapGlobalConfigurationModel,
            final InterceptorContext ctx) throws InterceptorException {

        final CRMBusinessAgreementPaymentMethodModel crmBusinessAgreementPaymentMethodModel = sapGlobalConfigurationModel
                .getPaymentmethod();
        final CRMBusinessAgreementClassModel crmBusinessAgreementClassModel = sapGlobalConfigurationModel
                .getBusinessagreementclass();
        if (crmBusinessAgreementPaymentMethodModel != null) {
            if (crmBusinessAgreementClassModel != null) {

                if (crmBusinessAgreementPaymentMethodModel.getCountry()
                        .equalsIgnoreCase(
                                crmBusinessAgreementClassModel.getCountry())
                        && crmBusinessAgreementPaymentMethodModel
                                .getPaymentuasge() == null
                        && crmBusinessAgreementPaymentMethodModel
                                .getActvbankdetail()
                                .equalsIgnoreCase(
                                        SapbusinessagreementexchangeConstants.ACTV_BANK_DETAIL)) {
                    LOGGER.info("Global configuration attributes are saved for Business Agreement");
                }

                else {

                    throw new InterceptorException(
                            Localization
                                    .getLocalizedString(
                                            "validation.SAPGlobalConfiguration.BusinessAgreementPaymentMethod.GlobalConfigurationMissing",
                                            new Object[] { SapbusinessagreementexchangeConstants.ACTV_BANK_DETAIL }));

                }
            } else {
                throw new InterceptorException(
                        Localization
                                .getLocalizedString(
                                        "validation.SAPGlobalConfiguration.BusinessAgreementClass.GlobalConfigurationMissing",
                                        new Object[] { SapbusinessagreementexchangeConstants.ACTV_BANK_DETAIL }));
            }
        }
    }
}
