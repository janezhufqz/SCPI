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

import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorizationCategoryModel;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Convert category data to category model
 */
public class DefaultCrmCategoryUrlPopulator {
    public void populate(final CategorizationCategoryModel source, final CategoryData target)
            throws ConversionException {
        target.setGuid(source.getCategorizationGuid());
    }
}