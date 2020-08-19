/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.cpi.crm.sapcpicrmorderexchange.service.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.impl.SapCpiOmmOrderOutboundConversionService;


/**
 *
 */
public class SapCpiCrmOrderOutboundConversionService extends SapCpiOmmOrderOutboundConversionService
{

	@Override
	public SAPCpiOutboundOrderModel convertOrderToSapCpiOrder(final OrderModel orderModel)
	{
		return super.convertOrderToSapCpiOrder(orderModel);
	}
}
