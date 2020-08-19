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
package com.sap.hybris.crm.sapcrmcategoryschema.facade.impl;

import de.hybris.platform.commercefacades.product.data.CategoryData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.core.convert.converter.Converter;

import com.sap.hybris.crm.sapcrmcategoryschema.facade.SapCrmCategoriesFacade;
import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorizationCategoryModel;
import com.sap.hybris.crm.sapcrmcategoryschema.service.SapCrmCategoriesService;

/**
 *
 */
public class DefaultSapCrmCategoriesFacade implements SapCrmCategoriesFacade {
    private Converter<CategorizationCategoryModel, CategoryData> categorizationCategoryConverter;
    private SapCrmCategoriesService sapCategoriesService;

    /**
     * Get the list of subject categories based on the catgeory level and object
     * creation date
     *
     * @param level
     * @param creationDate
     * @return CategoryData
     */
    @Override
    public List<CategoryData> getDefaultCategories(final int level, final String schemaId, final Date creationDate) {

        final List<CategorizationCategoryModel> categoryList = this.sapCategoriesService.getDefaultCategoriesByLevel(
                level, schemaId, creationDate);
        final List<CategoryData> subjectCategoryList = new ArrayList<CategoryData>();
        for (final CategorizationCategoryModel category : categoryList) {
            subjectCategoryList.add(categorizationCategoryConverter.convert(category));
        }
        return subjectCategoryList;
    }

    /**
     * Get the list of all sub(child) categories for the category code
     *
     * @param categoryGuid
     * @return CategoryData
     */
    @Override
    public List<CategoryData> getSubCategoriesForCode(final String categoryGuid, final String schemaId,
            final Date creationDate) {
        final List<CategoryData> subCategoryList = new ArrayList<CategoryData>();
        final List<CategorizationCategoryModel> categoryModelList = this.sapCategoriesService.getRelatedCategories(
                this.sapCategoriesService.getCategoryByGuid(categoryGuid), schemaId, creationDate);
        for (final CategorizationCategoryModel category : categoryModelList) {
            subCategoryList.add(categorizationCategoryConverter.convert(category));
        }
        return subCategoryList;

    }

    /**
     * @param sapCategoriesService
     *            the sapCategoriesService to set
     */
    public void setSapCategoriesService(final SapCrmCategoriesService sapCategoriesService) {
        this.sapCategoriesService = sapCategoriesService;
    }

    public Converter<CategorizationCategoryModel, CategoryData> getCategorizationCategoryConverter() {
        return categorizationCategoryConverter;
    }

    public void setCategorizationCategoryConverter(
            final Converter<CategorizationCategoryModel, CategoryData> categorizationCategoryConverter) {
        this.categorizationCategoryConverter = categorizationCategoryConverter;
    }

}