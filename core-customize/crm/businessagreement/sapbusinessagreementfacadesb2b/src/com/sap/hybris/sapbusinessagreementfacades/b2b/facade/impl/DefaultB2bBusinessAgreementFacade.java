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
package com.sap.hybris.sapbusinessagreementfacades.b2b.facade.impl;

import de.hybris.platform.servicelayer.dto.converter.Converter;

import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;
import com.sap.hybris.sapbusinessagreementfacades.data.BusinessAgreementDetailsData;
import com.sap.hybris.sapbusinessagreementfacades.facade.impl.DefaultBusinessAgreementFacade;

/**
 *
 */
public class DefaultB2bBusinessAgreementFacade extends DefaultBusinessAgreementFacade {

    private Converter<BusinessAgreementModel, BusinessAgreementDetailsData> businessAgreementB2BConverter;

    /**
     * @return the businessAgreementB2BConverter
     */
    public Converter<BusinessAgreementModel, BusinessAgreementDetailsData> getBusinessAgreementB2BConverter() {
        return businessAgreementB2BConverter;
    }

    /**
     * @param businessAgreementB2BConverter
     *            the businessAgreementB2BConverter to set
     */
    public void setBusinessAgreementB2BConverter(
            final Converter<BusinessAgreementModel, BusinessAgreementDetailsData> businessAgreementB2BConverter) {
        this.businessAgreementB2BConverter = businessAgreementB2BConverter;
    }

    @Override
    public BusinessAgreementDetailsData getBusinessAggreementDetails(final String businessAgreementId) {
        final BusinessAgreementModel businessAgreementModel = getBusinessAgreementService()
                .getBusinessAggreementDetails(businessAgreementId);

        BusinessAgreementDetailsData businessAgreementDetailsData = null;

        if (businessAgreementModel.getPartner() != null) {

            businessAgreementDetailsData = getBusinessAgreementB2BConverter().convert(businessAgreementModel);
        } else if (businessAgreementModel.getCustomer() != null) {
            businessAgreementDetailsData = getBusinessAgreementB2CConverter().convert(businessAgreementModel);
        }

        return businessAgreementDetailsData;
    }

}
