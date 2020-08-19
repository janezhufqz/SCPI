/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.crm.platform.odata2services.odata.persistence.lookup;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.search.FlexibleSearchQueryBuilder;
import de.hybris.platform.integrationservices.search.NonUniqueItemFoundException;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.lookup.DefaultItemLookupStrategy;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class DefaultOrderItemLookupStrategy extends DefaultItemLookupStrategy
{

	@Override
	protected ItemModel lookupInternal(final ItemLookupRequest lookupRequest) throws EdmException
	{
		final FlexibleSearchQueryBuilder builder = queryBuilder(lookupRequest);
		final FlexibleSearchQuery searchQuery = buildQuery(builder);
		if (searchQuery.getQueryParameters().isEmpty())
		{
			throw new NonUniqueItemFoundException(String.format("No key properties defined for type [%s]", lookupRequest
					.getEntityType().getName()));
		}

		final SearchResult<ItemModel> result = search(searchQuery);

		if (result.getCount() == 0)
		{
			return null;
		}
		else if (result.getCount() >= 1)
		{
			return result.getResult().get(0);
		}
		throw new NonUniqueItemFoundException(String.format("No unique item found for %s: %s", lookupRequest.getEntityType()
				.getName(), searchQuery.getQueryParameters()));
	}
}
