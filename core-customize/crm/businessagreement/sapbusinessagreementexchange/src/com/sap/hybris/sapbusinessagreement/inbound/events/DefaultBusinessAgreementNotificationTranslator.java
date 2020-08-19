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
package com.sap.hybris.sapbusinessagreement.inbound.events;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.sap.orderexchange.inbound.events.DataHubTranslator;

import com.sap.hybris.sapbusinessagreement.constants.SapbusinessagreementexchangeConstants;
import com.sap.hybris.sapbusinessagreement.inbound.BusinessAgreementNotificationHelper;

/**
 *
 */
public class DefaultBusinessAgreementNotificationTranslator extends
        DataHubTranslator<BusinessAgreementNotificationHelper> {
    public static final String HELPER_BEAN = "businessAgreementNotificationHelper";

    public DefaultBusinessAgreementNotificationTranslator() {
        super(HELPER_BEAN);
    }

    @Override
    public void performImport(final String transformationExpression,
            final Item processedItem) throws ImpExException {
        final String businessAgreementGUID = getBusinessAgreementGUID(processedItem);
        getInboundHelper().processBusinessAgreementConfirmationFromHub(
                businessAgreementGUID);
    }

    private String getBusinessAgreementGUID(final Item processedItem)
            throws ImpExException {
        String businessAgreementGUID = null;
        try {
            businessAgreementGUID = processedItem
                    .getAttribute(
                            SapbusinessagreementexchangeConstants.BUSINESS_AGREEMENT_RULE_HEADER_GUID)
                    .toString();
            final String[] businessAgreementRuleGUID = businessAgreementGUID
                    .split("_");
            businessAgreementGUID = businessAgreementRuleGUID[0];
        } catch (final JaloSecurityException e) {
            throw new ImpExException(e);
        }
        return businessAgreementGUID;
    }
}
