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
package de.hybris.platform.sap.sapcpicrmserviceorder.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 *
 */
public class ServiceOrderB2BUnitDao extends AbstractItemDao
{
	/*
	 * @return B2BUnitModel with uid: b2bUnitID, if exists.
	 */
	public B2BUnitModel getB2BUnit(final String b2bUnitID)
	{
		final FlexibleSearchQuery b2bSearch = new FlexibleSearchQuery("Select {pk} from {B2BUnit} where {uid}=?uid");
		b2bSearch.addQueryParameter("uid", b2bUnitID);

		final SearchResult<ItemModel> result = search(b2bSearch);
		if (result.getCount() > 0)
		{
			return (B2BUnitModel) result.getResult().get(0);
		}
		else
		{
			return null;
		}
	}
}
