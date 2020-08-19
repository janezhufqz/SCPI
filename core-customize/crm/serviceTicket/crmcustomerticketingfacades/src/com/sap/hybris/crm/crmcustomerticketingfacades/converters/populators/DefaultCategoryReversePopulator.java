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

/**
 *
 */
package com.sap.hybris.crm.crmcustomerticketingfacades.converters.populators;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Convert category data to category model
 */
public class DefaultCategoryReversePopulator implements Populator<CategoryData, CategoryModel> {

    @Override
    public void populate(final CategoryData source, final CategoryModel target) throws ConversionException {

        target.setCode(source.getCode());
        target.setName(source.getName());
    }

}
