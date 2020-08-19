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
package com.sap.hybris.crm.sapbusinessagreementfacades.b2b.converters.populator;

import de.hybris.platform.converters.Populator;

import com.sap.hybris.sapcrmcustomerb2b.model.SAPCCPaymentInfoModel;
import com.sap.hybris.sapbusinessagreementfacades.data.CreditCardData;


/**
 * populate credit card details of business agreement
 */
public class DefaultBusinessAgreementCreditCardPopulator implements Populator<SAPCCPaymentInfoModel, CreditCardData>
{
	@Override
	public void populate(final SAPCCPaymentInfoModel source, final CreditCardData target)
	{
		if (source != null)
		{
			target.setCardtype(source.getCardType().getCode().toString());
			target.setValidTo(source.getValidTo());
			target.setCardnumber(source.getCardNumber());
			target.setHoldername(source.getCcOwner());
		}
	}
}
