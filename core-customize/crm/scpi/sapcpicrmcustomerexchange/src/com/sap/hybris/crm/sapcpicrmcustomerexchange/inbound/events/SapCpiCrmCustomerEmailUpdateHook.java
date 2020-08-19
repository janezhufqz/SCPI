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
package com.sap.hybris.crm.sapcpicrmcustomerexchange.inbound.events;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 *
 */
public class SapCpiCrmCustomerEmailUpdateHook implements PrePersistHook
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiCrmCustomerEmailUpdateHook.class);

	private ModelService modelService;
	private FlexibleSearchService flexibleSearchService;

	@Override
	public Optional<ItemModel> execute(ItemModel item)
	{
		if (item instanceof CustomerModel)
		{
			LOG.info("The persistence hook SapCpiCrmCustomerEmailUpdateHook is called!");
			CustomerModel inboundCustomerModel = (CustomerModel) item;

			CustomerModel databaseCustomer = readInDatabaseCustomer(inboundCustomerModel.getCustomerID());

			if (databaseCustomer != null && !databaseCustomer.getUid().equals(inboundCustomerModel.getUid()))
			{
				LOG.debug("Updating email of customer '{}' from '{}' to '{}", databaseCustomer.getCustomerID(),
						databaseCustomer.getUid(), inboundCustomerModel.getUid());
				databaseCustomer.setUid(inboundCustomerModel.getUid());
				getModelService().save(databaseCustomer);
			}

			return Optional.empty();
		}
		return Optional.of(item);
	}

	protected CustomerModel readInDatabaseCustomer(final String customerID)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				"SELECT {c:pk} FROM {Customer AS c} WHERE  {c.customerID} = ?customerID");
		flexibleSearchQuery.addQueryParameter("customerID", customerID);

		final SearchResult<CustomerModel> result = getFlexibleSearchService().search(flexibleSearchQuery);
		if (result.getCount() == 1)
		{
			return result.getResult().get(0);
		}
		else
		{
			return null;
		}
	}


	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}



	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}



	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}



	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}


}
