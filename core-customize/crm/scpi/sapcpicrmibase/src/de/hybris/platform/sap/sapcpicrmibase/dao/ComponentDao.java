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

import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import com.sap.hybris.crm.sapcrmibasecore.model.IBaseModel;
import com.sap.hybris.crm.sapcrmibasecore.model.ObjectTypeModel;
import com.sap.hybris.crm.sapcrmibasecore.model.ProductObjectModel;


/**
 *
 */
public class ComponentDao extends AbstractItemDao
{
	public ObjectTypeModel getObjectType(final String objectTypeCode)
	{
		final FlexibleSearchQuery objectTypeFlexibleSearchQuery = new FlexibleSearchQuery(
				"SELECT {pk} FROM {ObjectType} WHERE  {code}=?code");
		objectTypeFlexibleSearchQuery.addQueryParameter("code", objectTypeCode);

		return searchUnique(objectTypeFlexibleSearchQuery);
	}

	public ProductObjectModel getProductObject(final String productObjectCode)
	{
		final FlexibleSearchQuery productObjectFlexibleSearchQuery = new FlexibleSearchQuery(
				"SELECT {pk} FROM {ProductObject} WHERE  {code}=?code");
		productObjectFlexibleSearchQuery.addQueryParameter("code", productObjectCode);

		return searchUnique(productObjectFlexibleSearchQuery);
	}

	public IBaseModel getIBase(final String ibaseGuid)
	{
		final FlexibleSearchQuery ibaseFlexibleSearchQuery = new FlexibleSearchQuery(
				"SELECT {pk} FROM {IBase} WHERE  {guid}=?guid");
		ibaseFlexibleSearchQuery.addQueryParameter("guid", ibaseGuid);

		return searchUnique(ibaseFlexibleSearchQuery);
	}
}