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
package com.sap.hybris.crm.sapcpicrmproductexchange.inbound.events;

import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class SapCpiCrmRemoveProductFeaturesPersistenceHook implements PrePersistHook
{
	private static final Logger LOG = LoggerFactory.getLogger(SapCpiCrmRemoveProductFeaturesPersistenceHook.class);

	private ModelService modelService;

	@Override
	public Optional<ItemModel> execute(final ItemModel item)
	{
		LOG.info("Executing pre persistence hook for item {}", item);

		if (item instanceof ProductModel)
		{
			LOG.info("The persistence hook SapCpiCrmRemoveProductFeaturesPersistenceHook is called!");

			final ProductModel productModel = (ProductModel) item;
			LOG.info("********Product Model Object **********");
			LOG.info("Product Model catalog ID : " + productModel.getCatalogVersion().getCatalog().getId());
			LOG.info("Product Model catalog Version : " + productModel.getCatalogVersion().getVersion());
			LOG.info("Product ID : " + productModel.getCode());

			final List<ProductFeatureModel> productFeatures = productModel.getFeatures();
			final int listSize = productFeatures.size();
			LOG.info("ProductFeatures Size before Removing : {}  ", listSize);
			if (!productFeatures.isEmpty())
			{
				productModel.setFeatures(null);
			}
			getModelService().save(productModel);
			getModelService().refresh(productModel);
			LOG.info("ProductFeatures removed Sucessfully from Product : {} ", productModel.getCode());
			return Optional.empty();
		}


		LOG.info("Item will be persisted");
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
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}



}
