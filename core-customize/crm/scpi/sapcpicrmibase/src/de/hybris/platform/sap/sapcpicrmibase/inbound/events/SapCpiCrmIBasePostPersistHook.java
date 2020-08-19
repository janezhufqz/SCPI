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

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook;
import de.hybris.platform.sap.sapcpicrmibase.dao.StructureDao;
import de.hybris.platform.servicelayer.model.ModelService;

import com.sap.hybris.crm.sapcrmibasecore.model.IBaseComponentModel;
import com.sap.hybris.crm.sapcrmibasecore.model.IBaseModel;
import com.sap.hybris.crm.sapcrmibasecore.model.IBaseStructureModel;


/**
 *
 */
public class SapCpiCrmIBasePostPersistHook implements PostPersistHook
{
	private StructureDao structureDao;
	private ModelService modelService;

	@Override
	public void execute(final ItemModel item)
	{
		if (item instanceof IBaseModel)
		{
			final IBaseModel ibaseItem = (IBaseModel) item;
			handleStructureRawGUIDs(ibaseItem);
		}
	}


	/**
	 * Sets the respective valid IBaseComponent for each reference based on the provided IBaseComponent:objectGUID
	 */
	private void handleStructureRawGUIDs(final IBaseModel ibaseItem)
	{
		for (final IBaseStructureModel ibaseStructureItem : ibaseItem.getStructures())
		{
			final String concatenatedGUIDs = ibaseStructureItem.getRawObjectGuids();

			if (concatenatedGUIDs != null && !concatenatedGUIDs.equals(""))
			{
				ibaseStructureItem.setRawObjectGuids("");

				final String[] guids = concatenatedGUIDs.split("[|]");
				handleStructureComponents(ibaseStructureItem, guids);

				getModelService().save(ibaseStructureItem);
			}
		}
	}


	/**
	 * Looks up and sets IBaseComponent references in the IBaseStructure with the given GUIDs.
	 */
	private void handleStructureComponents(final IBaseStructureModel ibaseStructureItem, final String[] guids)
	{
		IBaseComponentModel validComponent = null;

		if (guids.length >= 1 && !guids[0].equals(""))
		{
			validComponent = getStructureDao().getValidComponentGUID(guids[0]);
			ibaseStructureItem.setObjectGuid(validComponent);
		}

		if (guids.length >= 2 && !guids[1].equals(""))
		{
			validComponent = getStructureDao().getValidComponentGUID(guids[1]);
			ibaseStructureItem.setParentObjectGuid(validComponent);
		}

		if (guids.length == 3 && !guids[2].equals(""))
		{
			validComponent = getStructureDao().getValidComponentGUID(guids[2]);
			ibaseStructureItem.setRootObjectGuid(validComponent);
		}
	}

	/**
	 * @return the structureDao
	 */
	public StructureDao getStructureDao()
	{
		return structureDao;
	}

	/**
	 * @param structureDao
	 *           the structureDao to set
	 */
	public void setStructureDao(final StructureDao structureDao)
	{
		this.structureDao = structureDao;
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
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
