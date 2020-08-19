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
package de.hybris.platform.sap.sapcpicrmibase.dao;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;


/**
 *
 */
public class PartnerDao extends AbstractItemDao
{
	public B2BUnitModel getB2BUnit(final String partnerId)
	{
		final FlexibleSearchQuery b2bSearch = new FlexibleSearchQuery("Select {pk} from {B2BUnit} where {uid}=?uid");
		b2bSearch.addQueryParameter("uid", partnerId);
		final SearchResult<ItemModel> result = search(b2bSearch);

		return (B2BUnitModel) processResult(result);
	}

	public B2BCustomerModel getB2BCustomer(final String partnerId)
	{
		final FlexibleSearchQuery b2bCustomerSearch = new FlexibleSearchQuery(
				"Select {pk} from {B2BCustomer} where {customerId}=?customerId");
		b2bCustomerSearch.addQueryParameter("customerId", partnerId);
		final SearchResult<ItemModel> result = search(b2bCustomerSearch);

		return (B2BCustomerModel) processResult(result);
	}

	public CustomerModel getB2CCustomer(final String partnerId)
	{
		final FlexibleSearchQuery b2cCustomerSearch = new FlexibleSearchQuery(
				"Select {pk} from {Customer} where {customerId}=?customerId");
		b2cCustomerSearch.addQueryParameter("customerId", partnerId);
		final SearchResult<ItemModel> result = search(b2cCustomerSearch);

		return (CustomerModel) processResult(result);
	}

	/**
	 * @return ItemModel from SearchResult only if a unique result is found.
	 */
	private ItemModel processResult(final SearchResult<ItemModel> result)
	{
		if (result.getCount() == 1)
		{
			return result.getResult().get(0);
		}
		else
		{
			return null;
		}
	}
}
