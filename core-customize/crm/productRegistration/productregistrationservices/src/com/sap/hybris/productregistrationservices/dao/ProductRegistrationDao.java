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
package com.sap.hybris.productregistrationservices.dao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.sapmodel.model.SAPProductSalesAreaToCatalogMappingModel;

/**
 * @author C5229484
 *
 */
public interface ProductRegistrationDao {

    /**
     * @param guid
     * @return ProductModel
     */
    ProductModel getProductByGuid(String guid);

    /**
     *
     */
    SAPProductSalesAreaToCatalogMappingModel getProductMappingByCatalogVersion(CatalogVersionModel catalogVersionModel);

}
