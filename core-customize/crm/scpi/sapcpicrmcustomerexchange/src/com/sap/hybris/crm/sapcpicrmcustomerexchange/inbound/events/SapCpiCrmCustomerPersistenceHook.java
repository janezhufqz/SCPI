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

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 *
 */
public class SapCpiCrmCustomerPersistenceHook implements PrePersistHook
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiCrmCustomerPersistenceHook.class);

	private ModelService modelService;

	@Override
	public Optional<ItemModel> execute(ItemModel item)
	{
		if (item instanceof CustomerModel)
		{
			LOG.info("The persistence hook SapCpiCrmCustomerPersistenceHook is called!");
			CustomerModel customerModel = (CustomerModel) item;

			customerModel.setSapReplicationInfo(
					"Status change to 'IsReplicated = true' at: " + (new Timestamp(new Date().getTime())).toString());
			customerModel.setSapIsReplicated(true);
			getModelService().save(customerModel);
			return Optional.empty();
		}
		return Optional.of(item);
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


}
