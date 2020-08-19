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

import com.sap.hybris.crm.sapcrmmodel.util.XSSFilterUtil;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;
import com.sap.hybris.sapbusinessagreementfacades.data.BusinessAgreementData;

/**
 * populate all business agreements
 */
public class DefaultBusinessAgreementListPopulator implements
        Populator<BusinessAgreementModel, BusinessAgreementData> {

    @Override
    public void populate(final BusinessAgreementModel source,
            final BusinessAgreementData target) {

        target.setBusinessAgreementID(source.getId());
        target.setDescription(XSSFilterUtil.filter(source.getText()));
        if (!source.getBusinessagreementheader().isEmpty()) {
            target.setBusinessAgreementClass(source
                    .getBusinessagreementheader().iterator().next()
                    .getBusinessagreementclass().getText());
            target.setIsDefault(source.getBusinessagreementheader().iterator()
                    .next().getBusinessagreementdefault());
        }
    }

}
