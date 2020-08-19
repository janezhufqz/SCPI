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
package de.hybris.platform.sap.sapcpicrmserviceorder.inbound.events;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.sap.sapcpicrmserviceorder.dao.ServiceOrderB2BUnitDao;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Optional;

import com.sap.hybris.crm.sapserviceorderservices.model.ServiceOrderModel;


/**
 *
 */
public class SapCpiCrmServiceOrderPrePersistHook implements PrePersistHook
{
	private ServiceOrderB2BUnitDao serviceOrderB2BUnitDao;
	private static ModelService modelService;

	@Override
	public Optional<ItemModel> execute(final ItemModel item)
	{
		if (item instanceof ServiceOrderModel)
		{
			handleB2BUnit((ServiceOrderModel) item);
			handleAddressOwner((ServiceOrderModel) item);
		}

		return Optional.of(item);
	}

	/**
	 * Sets the owner for Payment and Delivery addresses
	 */
	private void handleAddressOwner(final ServiceOrderModel serviceOrderitem)
	{
		setAddressOwner(serviceOrderitem.getDeliveryAddress(), serviceOrderitem);
		setAddressOwner(serviceOrderitem.getPaymentAddress(), serviceOrderitem);
	}

	/**
	 * Sets Owner for newly created 'address' as 'parent'
	 */
	private void setAddressOwner(final AddressModel address, final ItemModel parent)
	{
		if (address != null && getModelService().isNew(address))
		{
			address.setOwner(parent);
		}
	}

	/**
	 * sets unit if a B2BUnit exists with the given b2bUnitID
	 */
	private void handleB2BUnit(final ServiceOrderModel serviceOrderItem)
	{
		final String b2bUnitID = serviceOrderItem.getB2bUnitID();
		if (!b2bUnitID.equals(""))
		{
			final B2BUnitModel b2bUnitItem = getServiceOrderB2BUnitDao().getB2BUnit(b2bUnitID);
			if (b2bUnitItem != null)
			{
				serviceOrderItem.setUnit(b2bUnitItem);
			}

			serviceOrderItem.setB2bUnitID(null);
		}
	}

	/**
	 * @return the serviceOrderB2BUnitDao
	 */
	public ServiceOrderB2BUnitDao getServiceOrderB2BUnitDao()
	{
		return serviceOrderB2BUnitDao;
	}

	/**
	 * @param serviceOrderB2BUnitDao
	 *           the serviceOrderB2BUnitDao to set
	 */
	public void setServiceOrderB2BUnitDao(final ServiceOrderB2BUnitDao serviceOrderB2BUnitDao)
	{
		this.serviceOrderB2BUnitDao = serviceOrderB2BUnitDao;
	}

	/**
	 * @return the modelService
	 */
	public static ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public static void setModelService(final ModelService modelService)
	{
		SapCpiCrmServiceOrderPrePersistHook.modelService = modelService;
	}
}
