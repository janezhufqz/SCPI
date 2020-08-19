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
package com.sap.hybris.productregistrationservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.HashMap;
import java.util.Map;

import com.sap.hybris.productregistrationservices.dao.ProductRegistrationDao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.sapmodel.model.SAPProductSalesAreaToCatalogMappingModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * @author C5229484
 *
 */
public class DefaultProductRegistrationDao extends AbstractItemDao implements ProductRegistrationDao {

    private static final String PRODUCT_BY_GUID = "SELECT {pk} FROM {" + ProductModel._TYPECODE + "} WHERE {"
            + ProductModel.GUID + "} = ?guid";

    private static final String PRODUCT_SALES_BY_CATALOG = "SELECT {pk} FROM {"
            + SAPProductSalesAreaToCatalogMappingModel._TYPECODE + "} WHERE {"
            + SAPProductSalesAreaToCatalogMappingModel.CATALOGVERSION + "} = ?catalogVersion";

    /**
     * Get list of registered products for the user
     */
    @Override
    public ProductModel getProductByGuid(final String guid) {
        ProductModel product = null;
        validateParameterNotNull(guid, "Product Guid must not be null");
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("guid", guid);
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(PRODUCT_BY_GUID);
        searchQuery.addQueryParameters(params);
        final SearchResult searchResult = getFlexibleSearchService().search(searchQuery);
        if (null != searchResult && null != searchResult.getResult() && !searchResult.getResult().isEmpty()) {
            product = (ProductModel) searchResult.getResult().get(0);
        }

        return product;
    }

    /**
     * Get product sales data mapping model
     */
    @Override
    public SAPProductSalesAreaToCatalogMappingModel getProductMappingByCatalogVersion(
            final CatalogVersionModel catalogVersion) {
        SAPProductSalesAreaToCatalogMappingModel product = null;
        validateParameterNotNull(catalogVersion, "Product Guid must not be null");
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("catalogVersion", catalogVersion);
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(PRODUCT_SALES_BY_CATALOG);
        searchQuery.addQueryParameters(params);
        final SearchResult searchResult = getFlexibleSearchService().search(searchQuery);
        if (null != searchResult && null != searchResult.getResult() && !searchResult.getResult().isEmpty()) {
            product = (SAPProductSalesAreaToCatalogMappingModel) searchResult.getResult().get(0);
        }

        return product;

    }

}