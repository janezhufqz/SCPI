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
package com.sap.hybris.sapcrmcustomerb2b.inbound;



/**
 * 
 */
public interface InboundB2BCustomerHelper
{

	/**
	 * @param b2bCustomerId
	 * @return Return B2BUnit Id for B2BCustomer if present otherwise return default SAP B2BUnit
	 */
	String getB2BUnitForCustomer(String b2bCustomerId);
}
