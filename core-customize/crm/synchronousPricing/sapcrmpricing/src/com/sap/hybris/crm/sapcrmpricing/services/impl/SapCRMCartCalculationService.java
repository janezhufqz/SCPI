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

/**
 *
 */
package com.sap.hybris.crm.sapcrmpricing.services.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.sap.sappricing.services.impl.SapCartCalculationService;


public class SapCRMCartCalculationService extends SapCartCalculationService
{

	@Override
	public void recalculate(final AbstractOrderEntryModel entry) throws CalculationException
	{
		resetAllValues(entry);
		super.calculateTotals(entry, true);
	}


	@Override
	protected void resetAllValues(final AbstractOrderEntryModel entry) throws CalculationException
	{
		if (!getSapPricingEnablementService().isCartPricingEnabled())
		{
			super.resetAllValues(entry);
		}
	}

	@Override
	public void calculate(final AbstractOrderModel order) throws CalculationException
	{
		if (getSapPricingEnablementService().isCartPricingEnabled())
		{
			getSapPricingCartService().getPriceInformationForCart(order);
		}
		super.calculate(order);
	}
}
