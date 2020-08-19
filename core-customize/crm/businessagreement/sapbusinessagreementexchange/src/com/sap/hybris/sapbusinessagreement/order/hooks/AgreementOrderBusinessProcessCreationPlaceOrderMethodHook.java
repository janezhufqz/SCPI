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
package com.sap.hybris.sapbusinessagreement.order.hooks;

import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.InvalidCartException;

import com.sap.hybris.sapbusinessagreement.strategies.AgreementProcessStrategy;


/**
 *
 */
public class AgreementOrderBusinessProcessCreationPlaceOrderMethodHook implements CommercePlaceOrderMethodHook
{

	private AgreementProcessStrategy agreementOrderProcessStrategy;

	@Override
	public void afterPlaceOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult orderModel)
			throws InvalidCartException
	{
		if (this.isB2CContext(orderModel.getOrder()))
		{

			this.agreementOrderProcessStrategy.createAgreementOrderProcess(orderModel.getOrder());
		}

	}

	protected boolean isB2CContext(final AbstractOrderModel order)
	{
		return order != null && order.getUser() != null ? order.getUser() instanceof CustomerModel : false;
	}

	@Override
	public void beforePlaceOrder(final CommerceCheckoutParameter parameter) throws InvalidCartException
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void beforeSubmitOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{
		// YTODO Auto-generated method stub

	}

	/**
	 * @return the agreementOrderProcessStrategy
	 */
	public AgreementProcessStrategy getAgreementOrderProcessStrategy()
	{
		return this.agreementOrderProcessStrategy;
	}

	/**
	 * @param agreementOrderProcessStrategy
	 *           the agreementOrderProcessStrategy to set
	 */

	public void setAgreementOrderProcessStrategy(final AgreementProcessStrategy agreementOrderProcessStrategy)
	{
		this.agreementOrderProcessStrategy = agreementOrderProcessStrategy;
	}

}
