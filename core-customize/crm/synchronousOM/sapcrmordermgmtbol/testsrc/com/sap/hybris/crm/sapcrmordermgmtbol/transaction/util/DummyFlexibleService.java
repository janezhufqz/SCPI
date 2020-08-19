/**
 * 
 */
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.util;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.RelationQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.TranslationResult;

import java.util.List;
import java.util.Map;

/**
 * @author C5230727
 *
 */
public class DummyFlexibleService implements FlexibleSearchService
{

	/* (non-Javadoc)
	 * @see de.hybris.platform.servicelayer.search.FlexibleSearchService#getModelByExample(java.lang.Object)
	 */
	@Override
	public <T> T getModelByExample(T paramT)
	{
		// YTODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.hybris.platform.servicelayer.search.FlexibleSearchService#getModelsByExample(java.lang.Object)
	 */
	@Override
	public <T> List<T> getModelsByExample(T paramT)
	{
		// YTODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.hybris.platform.servicelayer.search.FlexibleSearchService#search(de.hybris.platform.servicelayer.search.FlexibleSearchQuery)
	 */
	@Override
	public <T> SearchResult<T> search(FlexibleSearchQuery paramFlexibleSearchQuery)
	{
		// YTODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.hybris.platform.servicelayer.search.FlexibleSearchService#search(java.lang.String)
	 */
	@Override
	public <T> SearchResult<T> search(String paramString)
	{
		// YTODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.hybris.platform.servicelayer.search.FlexibleSearchService#search(java.lang.String, java.util.Map)
	 */
	@Override
	public <T> SearchResult<T> search(String paramString, Map<String, ? extends Object> paramMap)
	{
		// YTODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.hybris.platform.servicelayer.search.FlexibleSearchService#searchRelation(de.hybris.platform.core.model.ItemModel, java.lang.String, int, int)
	 */
	@Override
	public <T> SearchResult<T> searchRelation(ItemModel paramItemModel, String paramString, int paramInt1, int paramInt2)
	{
		// YTODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.hybris.platform.servicelayer.search.FlexibleSearchService#searchRelation(de.hybris.platform.servicelayer.search.RelationQuery)
	 */
	@Override
	public <T> SearchResult<T> searchRelation(RelationQuery paramRelationQuery)
	{
		// YTODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.hybris.platform.servicelayer.search.FlexibleSearchService#searchUnique(de.hybris.platform.servicelayer.search.FlexibleSearchQuery)
	 */
	@Override
	public <T> T searchUnique(FlexibleSearchQuery paramFlexibleSearchQuery)
	{
		// YTODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.hybris.platform.servicelayer.search.FlexibleSearchService#translate(de.hybris.platform.servicelayer.search.FlexibleSearchQuery)
	 */
	@Override
	public TranslationResult translate(FlexibleSearchQuery paramFlexibleSearchQuery)
	{
		// YTODO Auto-generated method stub
		return null;
	}

}
