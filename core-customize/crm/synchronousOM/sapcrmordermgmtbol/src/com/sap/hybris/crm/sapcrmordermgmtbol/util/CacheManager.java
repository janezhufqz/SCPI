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
package com.sap.hybris.crm.sapcrmordermgmtbol.util;



/**
 * @author C5230713
 *
 */
public interface CacheManager
{

	/**
	 * @param key
	 * @param value
	 */
	public void addToCache(String key, Object value);

	/**
	 * @param key
	 * @return
	 */
	public Object getFromCache(String key);

	/**
	 * @param key
	 */
	public void removeFromCache(String key);

}
