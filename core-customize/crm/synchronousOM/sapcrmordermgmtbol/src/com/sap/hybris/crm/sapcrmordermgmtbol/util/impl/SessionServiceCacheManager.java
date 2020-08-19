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
package com.sap.hybris.crm.sapcrmordermgmtbol.util.impl;

import de.hybris.platform.servicelayer.session.SessionService;

import org.springframework.beans.factory.annotation.Autowired;

import com.sap.hybris.crm.sapcrmordermgmtbol.util.CacheManager;


/**
 * @author C5230713
 *
 */
public class SessionServiceCacheManager implements CacheManager
{

	@Autowired
	private SessionService sessionService;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.crm.sapcrmordermgmtbol.util.CacheManager#addToCache(java.lang.String, java.util.Collection)
	 */
	@Override
	public void addToCache(final String key, final Object value)
	{
		sessionService.setAttribute(key, value);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.crm.sapcrmordermgmtbol.util.CacheManager#getFromCache(java.lang.String)
	 */
	@Override
	public Object getFromCache(final String key)
	{
		return sessionService.getAttribute(key);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.crm.sapcrmordermgmtbol.util.CacheManager#removeFromCache(java.lang.String)
	 */
	@Override
	public void removeFromCache(final String key)
	{
		sessionService.removeAttribute(key);

	}
}
