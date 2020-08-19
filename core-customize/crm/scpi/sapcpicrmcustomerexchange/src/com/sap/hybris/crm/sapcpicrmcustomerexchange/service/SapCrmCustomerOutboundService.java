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

import java.util.List;
import java.util.Map;


/**
 *
 */
public interface SapCrmCustomerOutboundService
{
	
	public void sendB2CCustomerToScpi(final List<Map<String, Object>> b2cCustomerAddressData,final List<Map<String, Object>> b2cCustomerCreditCardData);

}
