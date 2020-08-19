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
package com.sap.hybris.crm.sapcpicrmcustomerexchange.service;

import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;

import java.util.List;
import java.util.Map;


/**
 *
 */
public interface SapCpiCrmCustomerConversionService
{

	public SAPCpiOutboundCustomerModel convertB2CCustomerToSapCpiCustomer(final List<Map<String, Object>> b2cCustomerAddData,
			final List<Map<String, Object>> b2cCustomerCreditData);

}
