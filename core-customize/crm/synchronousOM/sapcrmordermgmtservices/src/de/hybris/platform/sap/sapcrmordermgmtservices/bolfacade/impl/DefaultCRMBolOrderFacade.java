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

package de.hybris.platform.sap.sapcrmordermgmtservices.bolfacade.impl;

import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.impl.DefaultBolOrderFacade;

import java.util.Map;

import com.sap.hybris.crm.sapcrmordermgmtbol.util.CacheManager;

/**
 * @author SAP
 *
 */
public class DefaultCRMBolOrderFacade extends DefaultBolOrderFacade
{
	protected CacheManager cacheManager;

	/**
	 * @return the cacheManager
	 */
	public CacheManager getCacheManager()
	{
		return cacheManager;
	}

	/**
	 * @param cacheManager
	 *           the cacheManager to set
	 */
	public void setCacheManager(final CacheManager cacheManager)
	{
		this.cacheManager = cacheManager;
	}

	/*
	 * (non-Javadoc) overriding this method to set orderGuid in TechKey.
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtservices.bolfacade.impl.DefaultBolOrderFacade#getSavedOrder(java.lang.String)
	 */
	@Override
	public Order getSavedOrder(final String orderId)
	{
		final Order order = getSavedOrder();
		order.setTechKey(new TechKey(getOrderGuid(orderId)));

		try
		{
			order.read();
			final boolean soldToMatches = getSapCheckDocumentValid().hasPermissions(order,
					getSapPartnerService().getCurrentSapCustomerId());
			final boolean documentTypeMatches = getSapCheckDocumentValid().isDocumentSupported(order, DocumentType.ORDER);
			if ((!soldToMatches) || (!documentTypeMatches))
			{
				throw new ApplicationBaseRuntimeException("You are not allowed to see Order: " + orderId);
			}
		}
		catch (final CommunicationException e)
		{
			throw new ApplicationBaseRuntimeException("Could not read order for code: " + orderId + " with error details :", e);
		}
		return order;
	}

	/**
	 * @param orderId
	 * @return orderGuid
	 */
	protected String getOrderGuid(final String orderId)
	{
		Map<String, String> fromCache = null;

		if (cacheManager.getFromCache("order_guids") != null)
		{
			fromCache = (Map<String, String>) cacheManager.getFromCache("order_guids");

			if (fromCache.containsKey(orderId))
			{
				return fromCache.get(orderId);
			}
		}
		return orderId;
	}

	/**
	 * @return
	 */
	protected Order getSavedOrder()
	{
		return (Order) getGenericFactory().getBean(SapordermgmtbolConstants.ALIAS_BO_ORDER_HISTORY);
	}
}
