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

import de.hybris.platform.catalog.ClassificationUtils;
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
public class SapCpiCrmInsertProductFeaturesPersistenceHook implements PrePersistHook
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiCrmInsertProductFeaturesPersistenceHook.class);

	private ModelService modelService;

	@Override
	public Optional<ItemModel> execute(final ItemModel item)
	{
		LOG.info("Executing pre persistence hook for item {}", item);

		if (item instanceof ProductModel)
		{
			return setClassificationsAttributes(item);
		}

		LOG.info("Item will be persisted");
		return Optional.of(item);

	}

	/**
	 *
	 */
	private Optional<ItemModel> setClassificationsAttributes(final ItemModel item)
	{
		LOG.info("The persistence hook SapCpiCrmInsertProductFeaturesPersistenceHook is called!");

		final ProductModel productModel = (ProductModel) item;
		LOG.info("********Product Model Object **********");
		LOG.info("Product Model catalog ID : {}", productModel.getCatalogVersion().getCatalog().getId());
		LOG.info("Product Model catalog Version : {}", productModel.getCatalogVersion().getVersion());
		LOG.info("Product ID : {}", productModel.getCode());


		LOG.info("Get the Product Features from product.......");
		final List<ProductFeatureModel> productFeatures = productModel.getFeatures();
		final int productFeaturesSize = productFeatures.size();
		LOG.info("ProductFeatures Size before Insert or Update : {} ", productFeaturesSize);
		int valuePosition = 0;
		for (final ProductFeatureModel pf : productFeatures)
		{

			if (pf.getValue() == null)
			{
				pf.setFeaturePosition(valuePosition);
				pf.setValuePosition(valuePosition);
				valuePosition = valuePosition + 1;
				if (pf.getValueDetails() == null || pf.getValueDetails().isEmpty())
				{
					pf.setValue("N/A");
				}
				else
				{
					pf.setValue(pf.getValueDetails());
				}
				if (pf.getQualifier().isEmpty() && pf.getClassificationAttributeAssignment() != null)
				{
					pf.setQualifier(ClassificationUtils.createFeatureQualifier(pf.getClassificationAttributeAssignment()));
				}
				pf.setUnit(null);
				pf.setLanguage(null);
				pf.setValueDetails(null);

			}


		}

		productModel.setFeatures(productFeatures);
		getModelService().save(productModel);
		getModelService().refresh(productModel);
		LOG.info("ProductFeatures are Inserted or Updated Sucessfully for Product : {}", productModel.getCode());
		return Optional.empty();
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
