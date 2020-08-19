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
package sap.sapcpicrmreturnorder.service;

import de.hybris.platform.returns.model.ReturnRequestModel;

import java.util.List;
import java.util.Map;

import sap.sapcpicrmreturnorder.model.SAPCpiOutboundReturnOrderModel;


/**
 *
 */
public interface SapCpiReturnOrderConversionService
{

	public SAPCpiOutboundReturnOrderModel convertReturnOrderSapCpiReturnOrder(List<Map<String, Object>> rowsAsNameValuePairs,
			ReturnRequestModel pReturnOrder);

}
