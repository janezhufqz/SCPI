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
package com.sap.hybris.crm.sapcrmcategoryschema.service;

import de.hybris.platform.category.model.CategoryModel;

import java.util.Date;
import java.util.List;

import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorizationCategoryModel;
import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorySchemaModel;

/**
 *
 */
public interface SapCrmCategoriesService {

    /**
     * Get the list of subject categories based on the catgeory level and ticket
     * creation date
     *
     * @param level
     * @param schemaId
     * @param creationDate
     * @return CategoryModel
     */
    List<CategorizationCategoryModel> getDefaultCategoriesByLevel(final int level, String schemaId, Date creationDate);

    /**
     * This method fetches the list of subcategories for a given category code
     *
     * @param categoryModel
     *            parent category code
     * @param creationDate
     *            validity lies between this date
     */
    List<CategorizationCategoryModel> getRelatedCategories(CategorizationCategoryModel categoryModel, final String schemaAttribute,
            Date creationDate);

    /**
     * Get Category schema ID configured in Base Store Configuration
     */
    String getSchemaIDForAttribute(final String schemaAttribute);

    CategorySchemaModel getSchemaByCategoryCode(CategorizationCategoryModel category, final String schemaAttribute, Date date);

    /**
     * Get the category model by guid
     */
    CategorizationCategoryModel getCategoryByGuid(String guid);

    /**
     * This method fetches the category schema model for a given category
     * code,schema ID and catalog name
     *
     * @param categoriesList
     *            list of categories
     * @param schemaID
     *            get only categories where schema ID is schemaID
     * @param catalogName
     *            validity lies between this date
     */
    CategorySchemaModel getModelByCategoryAndSchema(String schemaID, List<CategorizationCategoryModel> categoriesList,
            String catalogName);

    /**
     *
     */
    List<CategorizationCategoryModel> getCategoriesByCode(String reasonCatCode);

}
