/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package sap.sapcpicrmb2breturnorder.service.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

import java.util.List;
import java.util.Map;

import sap.sapcpicrmreturnorder.model.SAPCpiOutboundReturnOrderModel;
import sap.sapcpicrmreturnorder.service.impl.SapCpiReturnOrderConversionServiceImpl;

/**
 *
 */
public class SapCpiB2BReturnOrderConversionServiceImpl extends SapCpiReturnOrderConversionServiceImpl
{
	@Override
	public SAPCpiOutboundReturnOrderModel convertReturnOrderSapCpiReturnOrder(final List<Map<String, Object>> rowsAsNameValuePairs,
			final ReturnRequestModel pReturnOrder)
	{
		final SAPCpiOutboundReturnOrderModel convertReturnOrderSapCpiReturnOrder = super.convertReturnOrderSapCpiReturnOrder(
				rowsAsNameValuePairs, pReturnOrder);

		if (pReturnOrder.getOrder().getUser() instanceof B2BCustomerModel)
		{
			convertReturnOrderSapCpiReturnOrder.setB2bUnit(convertReturnOrderSapCpiReturnOrder.getCustomerId());
			convertReturnOrderSapCpiReturnOrder
					.setCustomerId(((B2BCustomerModel) pReturnOrder.getOrder().getUser()).getCustomerID());
		}

		return convertReturnOrderSapCpiReturnOrder;
	}
}
