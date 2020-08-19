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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * This exception is meant to handle critical errors which must lead to an abortion of the program flow. It should not
 * occur in productive environments (i.e. does not indicate communication or resource issues) but reflects problems with
 * the call of the ECommerce CRM backend layer.
 *
 */
public class BackendExceptionECOCRM extends BackendException
{

	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 5173890011152490823L;

	/**
	 * Meant to handle critical errors which must lead to an abortion of the program flow. It should not occur in
	 * productive environments (i.e. does not indicate communication or resource issues) but reflects problems with the
	 * call of the ECommerce CRM backend layer
	 *
	 * @param msg
	 *           Message for the Exception
	 */
	public BackendExceptionECOCRM(final String msg)
	{
		super(msg);
	}

}
