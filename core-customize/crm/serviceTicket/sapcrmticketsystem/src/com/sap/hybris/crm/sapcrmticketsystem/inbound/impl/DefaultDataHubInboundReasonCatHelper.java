/*
 *
 *  [y] hybris Platform
 *
 *  Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 *  This software is the confidential and proprietary information of SAP
 *  ("Confidential Information"). You shall not disclose such Confidential
 *  Information and shall use it only in accordance with the terms of the
 *  license agreement you entered into with SAP.
 * /
 */

package com.sap.hybris.crm.sapcrmticketsystem.inbound.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.crm.sapcrmcategoryschema.jalo.CategorizationCategory;
import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorizationCategoryModel;
import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorySchemaModel;
import com.sap.hybris.crm.sapcrmcategoryschema.service.SapCrmCategoriesService;
import com.sap.hybris.crm.sapcrmticketsystem.inbound.DataHubInboundReasonCatHelper;

import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Default Data Hub Inbound helper for getting customer
 *
 * @author C5229484
 */
public class DefaultDataHubInboundReasonCatHelper implements DataHubInboundReasonCatHelper {

    /**
     * Logger for class DefaultDataHubInboundCustomerHelper
     */
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDataHubInboundReasonCatHelper.class);
    private SapCrmCategoriesService sapCategoriesService;
    private ModelService modelService;

	@Override
	public CategorizationCategory getCategoryFromSchema(final String reasonCatCode, final String catalogType,
			final String catalogName)
	{
		LOG.debug("Trying to resolve the reason category for code :" + reasonCatCode);
		final List<CategorizationCategoryModel> categoriesList = this.sapCategoriesService.getCategoriesByCode(reasonCatCode);
		CategorizationCategory category = null;
		if (categoriesList != null && !categoriesList.isEmpty())
		{
			final CategorySchemaModel catSchema = sapCategoriesService.getModelByCategoryAndSchema(catalogName, categoriesList,
					catalogType);
			if (null != catSchema)
			{
				category = this.modelService.getSource(catSchema.getSourceCategory());
			}
		}
		return category;
	}

    /**
     * @param sapCategoriesService
     *            the sapCategoriesService to set
     */
    public void setSapCategoriesService(final SapCrmCategoriesService sapCategoriesService) {
        this.sapCategoriesService = sapCategoriesService;
    }

    /**
     * @param modelService
     *            the modelService to set
     */
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

}
