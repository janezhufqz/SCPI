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
package de.hybris.platform.sap.sapcpicrmibase.inbound.events;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.odata2services.odata.persistence.exception.MissingNavigationPropertyException;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.sap.sapcpicrmibase.dao.ComponentDao;
import de.hybris.platform.sap.sapcpicrmibase.dao.PartnerDao;

import java.util.Optional;

import com.sap.hybris.crm.sapcrmibasecore.model.IBaseComponentModel;
import com.sap.hybris.crm.sapcrmibasecore.model.IBaseModel;
import com.sap.hybris.crm.sapcrmibasecore.model.IBasePartnerModel;


/**
 *
 */
public class SapCpiCrmIBasePrePersistHook implements PrePersistHook
{
	private ComponentDao componentDao;
	private PartnerDao partnerDao;


	@Override
	public Optional<ItemModel> execute(final ItemModel item)
	{
		if (item instanceof IBaseModel)
		{
			final IBaseModel ibaseItem = (IBaseModel) item;
			handleComponentRawCodes(ibaseItem);
		}
		else if (item instanceof IBasePartnerModel)
		{
			final IBasePartnerModel ibasePartnerItem = (IBasePartnerModel) item;
			handlePartnerRawCode(ibasePartnerItem);
		}
		return Optional.of(item);
	}


	/**
	 * Sets ObjectType and ProductObject in all components by their corresponding codes sent from the iflow as strings.
	 */
	private void handleComponentRawCodes(final IBaseModel ibaseItem)
	{
		for (final IBaseComponentModel ibaseComponentItem : ibaseItem.getComponents())
		{
			//rawComponentData = ObjectType|ProductObject
			final String concatenatedCodes = ibaseComponentItem.getRawComponentData();
			if (concatenatedCodes != null)
			{
				ibaseComponentItem.setRawComponentData("");

				final String[] codes = concatenatedCodes.split("[|]");
				if (codes.length >= 1 && !codes[0].equals(""))
				{
					final String objectTypeCode = codes[0];
					ibaseComponentItem.setObjectType(getComponentDao().getObjectType(objectTypeCode));
				}

				if (codes.length >= 2 && !codes[1].equals(""))
				{
					final String productObjectCode = codes[1];
					ibaseComponentItem.setObject(getComponentDao().getProductObject(productObjectCode));
				}


				if (codes.length == 3 && !codes[2].equals(""))
				{
					final String componentIBaseGUID = codes[2];
					ibaseComponentItem.setComponentIBase(getComponentDao().getIBase(componentIBaseGUID));
				}
			}
		}
	}

	/**
	 * Map the partner number of each IBasePartner to B2BUnit, B2BCustomer, or Customer. Each IBasePartner has a
	 * rawPartnerID that could identify either a B2BUnit, a B2BCustomer, or a Customer
	 */
	private void handlePartnerRawCode(final IBasePartnerModel ibasePartnerItem)
	{
		final String rawPartnerId = ibasePartnerItem.getRawPartnerId();
		if (rawPartnerId != null && !rawPartnerId.equals(""))
		{
			ibasePartnerItem.setRawPartnerId("");

			final B2BUnitModel b2bUnitItem = getPartnerDao().getB2BUnit(rawPartnerId);
			if (b2bUnitItem != null)
			{
				ibasePartnerItem.setB2bUnit(b2bUnitItem);
				return;
			}

			final B2BCustomerModel b2bCustomerItem = getPartnerDao().getB2BCustomer(rawPartnerId);
			if (b2bCustomerItem != null)
			{
				ibasePartnerItem.setPartner(b2bCustomerItem);
				ibasePartnerItem.setB2bUnit(b2bCustomerItem.getDefaultB2BUnit());
				return;
			}

			final CustomerModel b2cCustomerItem = getPartnerDao().getB2CCustomer(rawPartnerId);
			if (b2cCustomerItem != null)
			{
				ibasePartnerItem.setPartner(b2cCustomerItem);
			}
			else
			{
				throw new MissingNavigationPropertyException("IBasePartner");
			}
		}
	}


	/**
	 * @return the componentDao
	 */
	public ComponentDao getComponentDao()
	{
		return componentDao;
	}


	/**
	 * @param componentDao
	 *           the componentDao to set
	 */
	public void setComponentDao(final ComponentDao componentDao)
	{
		this.componentDao = componentDao;
	}


	/**
	 * @return the partnerDao
	 */
	public PartnerDao getPartnerDao()
	{
		return partnerDao;
	}


	/**
	 * @param partnerDao
	 *           the partnerDao to set
	 */
	public void setPartnerDao(final PartnerDao partnerDao)
	{
		this.partnerDao = partnerDao;
	}
}
