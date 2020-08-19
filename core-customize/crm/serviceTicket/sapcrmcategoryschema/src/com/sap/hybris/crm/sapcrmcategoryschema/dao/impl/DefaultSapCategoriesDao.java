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
package com.sap.hybris.crm.sapcrmcategoryschema.dao.impl;

import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.sap.hybris.crm.sapcrmcategoryschema.dao.SapCategoriesDao;
import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorizationCategoryModel;
import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorySchemaModel;

/**
 * Dao class for Sap CRM Category structure
 *
 * @author C5229484
 *
 */
public class DefaultSapCategoriesDao extends AbstractItemDao implements SapCategoriesDao {

    private static final String SELECT_CATEGORY_SCHEMA = "SELECT {pk} FROM {CategorySchema} WHERE {";
    private static final String CREATION_TIME_COND = " } > ?creationtime AND {";
    private static final String CODE_PARAM = "} = ?code";
    private static final String CREATION_TIME = "creationtime";
    private static final String FIND_CATEGORY_SCHEMA_DEFAULT = SELECT_CATEGORY_SCHEMA + CategorySchemaModel.LEVEL
            + "} = ?level AND {" + CategorySchemaModel.VALIDTO + CREATION_TIME_COND + CategorySchemaModel.CODE
            + CODE_PARAM;

    private static final String FIND_RELATED_CATEGORIES = SELECT_CATEGORY_SCHEMA + CategorySchemaModel.PARENTCATEGORY
            + "} = ?categoryCode AND {" + CategorySchemaModel.VALIDTO + CREATION_TIME_COND + CategorySchemaModel.CODE
            + CODE_PARAM;

    private static final String FIND_CATEGORIES = SELECT_CATEGORY_SCHEMA + CategorySchemaModel.SOURCECATEGORY
            + "} = ?categoryCode AND {" + CategorySchemaModel.VALIDTO + CREATION_TIME_COND + CategorySchemaModel.CODE
            + CODE_PARAM;

    private static final String GET_CATEGORY_BY_CATALOG = SELECT_CATEGORY_SCHEMA + CategorySchemaModel.CATALOGNAME
            + " } = ?catalogName AND {" + CategorySchemaModel.VALIDTO + CREATION_TIME_COND + CategorySchemaModel.CODE + "} = ?code AND {"
            + CategorySchemaModel.SOURCECATEGORY + "} IN (?category)";

    private static final String GET_CATEGORY_BY_GUID = "SELECT {pk} FROM {CategorizationCategory} WHERE {"
            + CategorizationCategoryModel.CATEGORIZATIONGUID + "} = ?guid";

    private static final String FIND_CATEGORIES_BY_CODE = "SELECT {pk} FROM {CategorizationCategory} WHERE {"
            + CategorizationCategoryModel.CODE + CODE_PARAM;

    /**
     * Get the subject categories based on level and validity with respect to
     * object creation date
     *
     * @param level
     * @return CategorySchemaModel
     */
    @Override
    public List<CategorySchemaModel> getReasonCategorySchema(final int level, final String schemaID,
            final Date creationDate) {
        ServicesUtil.validateParameterNotNull(new Integer(level), "Level must not be null");
        ServicesUtil.validateParameterNotNull(creationDate, "Date must not be null");
        ServicesUtil.validateParameterNotNull(schemaID, "Schema ID must not be null");

        final StringBuilder builder = new StringBuilder(FIND_CATEGORY_SCHEMA_DEFAULT);
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put(CREATION_TIME, creationDate);
        queryParams.put("level", new Integer(level));
        queryParams.put("code", schemaID);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
        query.addQueryParameters(queryParams);

        final SearchResult<CategorySchemaModel> result = getFlexibleSearchService().search(query);
        return result.getResult();

    }

    /**
     * This method fetches the list of subcategories for a given category code
     *
     * @param category
     *            parent category code
     * @param schemaID
     *            get only categories where schema ID is schemaID
     * @param creationDate
     *            validity lies between this date
     */

    @Override
    public List<CategorySchemaModel> getRelatedCategories(final CategorizationCategoryModel category,
            final String schemaID, final Date creationDate) {
        ServicesUtil.validateParameterNotNull(category, "Level must not be null");
        ServicesUtil.validateParameterNotNull(creationDate, "Date must not be null");
        ServicesUtil.validateParameterNotNull(schemaID, "Schema ID must not be null");

        final StringBuilder builder = new StringBuilder(FIND_RELATED_CATEGORIES);
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put(CREATION_TIME, creationDate);
        queryParams.put("categoryCode", category);
        queryParams.put("code", schemaID);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
        query.addQueryParameters(queryParams);

        final SearchResult<CategorySchemaModel> result = getFlexibleSearchService().search(query);
        return result.getResult();

    }

    @Override
    public CategorySchemaModel getSchemaByCategoryCode(final String schemaID,
            final CategorizationCategoryModel category, final Date date) {

        final StringBuilder builder = new StringBuilder(FIND_CATEGORIES);
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("code", schemaID);
        queryParams.put("categoryCode", category);
        queryParams.put(CREATION_TIME, date);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
        query.addQueryParameters(queryParams);

        final SearchResult<CategorySchemaModel> result = getFlexibleSearchService().search(query);
        return CollectionUtils.isEmpty(result.getResult()) ? null : result.getResult().iterator().next();
    }

    @Override
    public CategorySchemaModel getModelByCategoryAndSchema(final String schemaID,
            final List<CategorizationCategoryModel> category, final String catalogName) {

        final StringBuilder builder = new StringBuilder(GET_CATEGORY_BY_CATALOG);
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("code", schemaID);
        queryParams.put("category", category);
        queryParams.put("catalogName", catalogName);
        queryParams.put(CREATION_TIME, new Date());

        final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
        query.addQueryParameters(queryParams);

        final SearchResult<CategorySchemaModel> result = getFlexibleSearchService().search(query);
        return CollectionUtils.isEmpty(result.getResult()) ? null : result.getResult().iterator().next();
    }

    @Override
    public CategorizationCategoryModel getCategoryByGuid(final String guid) {
        final StringBuilder builder = new StringBuilder(GET_CATEGORY_BY_GUID);
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("guid", guid);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
        query.addQueryParameters(queryParams);

        final SearchResult<CategorizationCategoryModel> result = getFlexibleSearchService().search(query);
        return CollectionUtils.isEmpty(result.getResult()) ? null : result.getResult().iterator().next();
    }

    @Override
    public List<CategorizationCategoryModel> getCategoriesByCode(final String catCode) {
        ServicesUtil.validateParameterNotNull(catCode, "Category code must not be null");

        final StringBuilder builder = new StringBuilder(FIND_CATEGORIES_BY_CODE);
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("code", catCode);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
        query.addQueryParameters(queryParams);

        final SearchResult<CategorizationCategoryModel> result = getFlexibleSearchService().search(query);
        return result.getResult();
    }
}
