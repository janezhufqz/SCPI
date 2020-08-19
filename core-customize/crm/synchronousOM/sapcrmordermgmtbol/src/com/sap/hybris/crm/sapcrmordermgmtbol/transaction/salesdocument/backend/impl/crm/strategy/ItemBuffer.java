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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm.strategy;

/**
 * @author C5230711
 *
 */
public class ItemBuffer
{
	private String guid;
	private String handle;
	private String productID;

	/**
	 * @return the guid
	 */
	public String getGuid()
	{
		return guid;
	}

	/**
	 * @param guid
	 *           the guid to set
	 */
	public void setGuid(final String guid)
	{
		this.guid = guid;
	}

	/**
	 * @return the handle
	 */
	public String getHandle()
	{
		return handle;
	}

	/**
	 * @param handle
	 *           the handle to set
	 */
	public void setHandle(final String handle)
	{
		this.handle = handle;
	}

	/**
	 * @return the productID
	 */
	public String getProductID()
	{
		return productID;
	}

	/**
	 * @param productID
	 *           the productID to set
	 */
	public void setProductID(final String productID)
	{
		this.productID = productID;
	}
}
