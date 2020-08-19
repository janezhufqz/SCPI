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
package com.sap.hybris.crm.sapcrmcategoryschema.facade;

import de.hybris.platform.commercefacades.product.data.CategoryData;

import java.util.Date;
import java.util.List;

/**
 *
 */
public interface SapCrmCategoriesFacade {
    /**
     * Get the list of subject categories based on the catgeory level and object
     * creation date
     *
     * @param level
     * @param schemaType
     * @param creationDate
     * @return CategoryData
     */
    List<CategoryData> getDefaultCategories(final int level, String schemaType, final Date creationDate);

    /**
     * Get the list of all sub(child) categories for the category code
     *
     * @param categoryCode
     * @return CategoryData
     */
    List<CategoryData> getSubCategoriesForCode(String categoryCode, final String schemaId, Date creationDate);
}
