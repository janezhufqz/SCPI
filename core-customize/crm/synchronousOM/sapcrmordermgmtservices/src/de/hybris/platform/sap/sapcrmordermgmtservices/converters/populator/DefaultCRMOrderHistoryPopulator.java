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
package de.hybris.platform.sap.sapcrmordermgmtservices.converters.populator;

import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultOrderHistoryPopulator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.math.BigInteger;
import java.util.Map;

import com.sap.hybris.crm.sapcrmordermgmtbol.util.CacheManager;


/**
 * @author C5230713
 *
 */
public class DefaultCRMOrderHistoryPopulator extends DefaultOrderHistoryPopulator
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
	public void setCacheManager(CacheManager cacheManager)
	{
		this.cacheManager = cacheManager;
	}

	/*
	 * (non-Javadoc)overriding this method to set OrderId on target(OrderHistoryData)
	 * 
	 * @see DefaultOrderHistoryPopulator#populate(SearchResult, OrderHistoryData)
	 */
	@Override
	public void populate(final SearchResult source, final OrderHistoryData target) throws ConversionException
	{
		super.populate(source, target);
		target.setCode(getExternalFormat(getOrderIdForGUID(source.getKey().getIdAsString())));
	}

	/**
	 * @param orderGUID
	 * @return orderID
	 */
	private String getOrderIdForGUID(String orderGUID)
	{
		Map<String, String> fromCache = null;
		String orderID = null;

		if (cacheManager.getFromCache("order_guids") != null)
		{
			fromCache = (Map<String, String>) cacheManager.getFromCache("order_guids");
			for (Map.Entry<String, String> entry : fromCache.entrySet())
			{
				if (entry.getValue().equals(orderGUID))
				{
					orderID = entry.getKey();
					break;
				}
			}
		}
		return orderID;
	}

	/**
	 * Compiles external format of SAP order ID by just cutting leading zeros in case ID is purely numerical
	 *
	 * @param orderId
	 *           Technical SAP order ID
	 * @return Order ID in external format
	 */
	protected String getExternalFormat(final String orderId)
	{
		try
		{
			final BigInteger orderIdNumeric = new BigInteger(orderId);
			return orderIdNumeric.toString();
		}
		catch (final NumberFormatException ex)
		{
			return orderId;
		}
	}
}
