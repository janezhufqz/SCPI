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
package com.sap.hybris.crm.sapcrmcategoryschema.converters.populator;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.converters.Populator;

import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorizationCategoryModel;

/**
 *
 */
public class CategorizationCategoryPopulator implements Populator<CategorizationCategoryModel, CategoryData> {

    @Override
    public void populate(final CategorizationCategoryModel source, final CategoryData target) {

        target.setCode(source.getCode());
        target.setGuid(source.getCategorizationGuid());
        target.setName(source.getName());
    }
}
