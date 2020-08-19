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
package com.sap.hybris.crm.sapcrmcategoryschema.service.impl;

import de.hybris.platform.sap.core.configuration.SAPConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.sap.hybris.crm.sapcrmcategoryschema.dao.SapCategoriesDao;
import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorizationCategoryModel;
import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorySchemaModel;
import com.sap.hybris.crm.sapcrmcategoryschema.service.SapCrmCategoriesService;

/**
 *
 */
public class DefaultSapCrmCategoriesService implements SapCrmCategoriesService {

    private SapCategoriesDao sapCategoriesDao;

    private SAPConfigurationService sapCoreConfigurationService;


    /**
     * Get the list of subject categories based on the catgeory level and object
     * creation date
     *
     * @param level
     * @param creationDate
     * @return CategoryModel
     */
    @Override
    public List<CategorizationCategoryModel> getDefaultCategoriesByLevel(final int level, final String schemaId,
            Date creationDate) {
        if (null == creationDate) {
            creationDate = getCurrentDate();
        }
        final List<CategorySchemaModel> schemaList = this.sapCategoriesDao.getReasonCategorySchema(level,
                getSchemaIDForAttribute(schemaId), creationDate);
        final List<CategorizationCategoryModel> categoriesAtLevel = new ArrayList<CategorizationCategoryModel>();
        if (null != schemaList && !schemaList.isEmpty()) {
            for (final CategorySchemaModel schema : schemaList) {
                categoriesAtLevel.add(schema.getSourceCategory());
            }
        }
        return categoriesAtLevel;
    }

    /**
     * This method fetches the list of sub categories for a given category code
     *
     * @param categoryModel
     *            parent category code
     * @param creationDate
     *            validity lies between this date
     */
    @Override
    public List<CategorizationCategoryModel> getRelatedCategories(final CategorizationCategoryModel categoryModel,
            final String schemaAttribute, Date creationDate) {
        final List<CategorizationCategoryModel> categoriesAtLevel = new ArrayList<CategorizationCategoryModel>();
        
        if (categoryModel != null) {
            if (null == creationDate) {
                creationDate = getCurrentDate();
            }
            final List<CategorySchemaModel> schemaList = this.sapCategoriesDao.getRelatedCategories(categoryModel,
                    getSchemaIDForAttribute(schemaAttribute), creationDate);
            if (null != schemaList && !schemaList.isEmpty()) {
                for (final CategorySchemaModel schema : schemaList) {
                    categoriesAtLevel.add(schema.getSourceCategory());
                }
            }
        }
        return categoriesAtLevel;
    }

    @Override
    public List<CategorizationCategoryModel> getCategoriesByCode(final String reasonCatCode) {
        return this.sapCategoriesDao.getCategoriesByCode(reasonCatCode);
    }

    @Override
    public CategorySchemaModel getModelByCategoryAndSchema(final String schemaID,
            final List<CategorizationCategoryModel> category, final String catalogName) {
        final CategorySchemaModel categorySchema = this.sapCategoriesDao.getModelByCategoryAndSchema(schemaID,
                category, catalogName);
        if (null == categorySchema) {
            throw new UnknownIdentifierException("Could not found category");
        }
        return categorySchema;
    }

    @Override
    public CategorySchemaModel getSchemaByCategoryCode(final CategorizationCategoryModel category,
            final String schemaAttribute, Date date) {
        if (null == date) {
            date = getCurrentDate();
        }
        return this.sapCategoriesDao.getSchemaByCategoryCode(getSchemaIDForAttribute(schemaAttribute), category, date);
    }

    /**
     * Get active schema ID for service request or complaint from base store
     * configuration.
     *
     * @param schemaAttribute
     *            name of the attribute whose value is to be retrieved
     * @return schema
     */
    @Override
    public String getSchemaIDForAttribute(final String schemaAttribute) {
        final String schemaID = this.sapCoreConfigurationService.getProperty(schemaAttribute);
        if (null == schemaID) {
            throw new UnknownIdentifierException("Schema ID can not be null");
        }
        return schemaID;
    }

    @Override
    public CategorizationCategoryModel getCategoryByGuid(final String guid) {
        final CategorizationCategoryModel category = this.sapCategoriesDao.getCategoryByGuid(guid);
        return category;
    }

    /**
     *
     */
    protected Date getCurrentDate() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * @param sapCategoriesDao
     *            the sapCategoriesDao to set
     */
    public void setSapCategoriesDao(final SapCategoriesDao sapCategoriesDao) {
        this.sapCategoriesDao = sapCategoriesDao;
    }

    /**
     * @param sapCoreConfigurationService
     *            the sapCoreConfigurationService to set
     */
    public void setSapCoreConfigurationService(final SAPConfigurationService sapCoreConfigurationService) {
        this.sapCoreConfigurationService = sapCoreConfigurationService;
    }

}
