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

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import org.apache.log4j.Logger;

import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementPartnersModel;
import com.sap.hybris.sapbusinessagreement.outbound.DefaultCRMBusinessAgreementExportService;

/**
 *
 */
public class DefaultCRMBusinessAgreementPartnersInterceptor
        implements ValidateInterceptor<BusinessAgreementPartnersModel> {
    private DefaultCRMBusinessAgreementExportService defaultCRMBusinessAgreementExportService;
    private static final Logger LOGGER = Logger.getLogger(
            com.sap.hybris.sapbusinessagreement.interceptor.DefaultCRMBusinessAgreementPartnersInterceptor.class
                    .getName());

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
    public void onValidate(final BusinessAgreementPartnersModel businessAgreementPartnersModel,
            final InterceptorContext partnerctx) throws InterceptorException {

        if (businessAgreementPartnersModel.getCustomerguid() == null) {
            throw new InterceptorException(" Consumer does not  exist in hybris");
        }

        if (!partnerctx.isModified(businessAgreementPartnersModel,
                BusinessAgreementPartnersModel.REPLICATIONNUMBERPARTNERS)) {
            defaultCRMBusinessAgreementExportService.updateBusinessAgreementPartners(businessAgreementPartnersModel);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Business Agreement "
                        + businessAgreementPartnersModel.getBusinessagreementheader().getBusinessagreement().getId()
                        + " sent to Datahub");
            }

        }

    }
}
