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

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import com.sap.hybris.crm.sapcrmibasecore.model.IBaseComponentModel;


/**
 *
 */
public class StructureDao extends AbstractItemDao
{
	public IBaseComponentModel getValidComponentGUID(final String objectGUID)
	{
		final FlexibleSearchQuery componentSearch = new FlexibleSearchQuery(
				"Select {pk} from {IBaseComponent} where {objectGUID}=?objectGUID and {validTo}>CURDATE()");
		componentSearch.addQueryParameter("objectGUID", objectGUID);
		final SearchResult<ItemModel> result = search(componentSearch);

		return (IBaseComponentModel) processResult(result);
	}

	/**
	 * @return ItemModel from SearchResult only if a result is found.
	 */
	private ItemModel processResult(final SearchResult<ItemModel> result)
	{
		if (result.getCount() >= 1)
		{
			return result.getResult().get(0);
		}
		else
		{
			return null;
		}
	}
}
