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
package com.sap.hybris.crm.sapcrmcategoryschema.dao;

import java.util.Date;
import java.util.List;

import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorizationCategoryModel;
import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorySchemaModel;

/**
 *
 */
public interface SapCategoriesDao {
    /**
     * Get the subject categories based on level and validity with respect to
     * ticket creation date
     *
     * @param level
     * @param schemaID
     * @param creationDate
     * @return CategorySchemaModel
     */
    List<CategorySchemaModel> getReasonCategorySchema(final int level, String schemaID, Date creationDate);

    /**
     * This method fetches the list of subcategories for a given category code
     *
     * @param categoryModel
     *            parent category code
     * @param schemaID
     *            get only categories where schema ID is schemaID
     * @param creationDate
     *            validity lies between this date
     */
    List<CategorySchemaModel> getRelatedCategories(CategorizationCategoryModel categoryModel, String schemaID,
            Date creationDate);

    /**
     * This method fetches the category schema model for a given category
     * code,schema ID and date
     *
     * @param category
     *            parent category code
     * @param schemaID
     *            get only categories where schema ID is schemaID
     * @param date
     *            validity lies between this date
     */
    CategorySchemaModel getSchemaByCategoryCode(String schemaID, CategorizationCategoryModel category, Date date);

    /**
     * Get CategorizationCategoryModel by guid
     *
     * @param activeVersion
     */
    CategorizationCategoryModel getCategoryByGuid(String guid);

    /**
     * This method fetches the category schema model for a given category
     * code,schema ID and catalog name
     *
     * @param category
     *            parent category code
     * @param schemaID
     *            get only categories where schema ID is schemaID
     * @param catalogName
     *            validity lies between this date
     */
    CategorySchemaModel getModelByCategoryAndSchema(String schemaID, List<CategorizationCategoryModel> category,
            String catalogName);

    /**
     * This method fetches the list of all the categories where category code is
     * given
     *
     * @param catCode
     *            given category code
     */
    List<CategorizationCategoryModel> getCategoriesByCode(String catCode);

}
