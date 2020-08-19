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
package com.sap.hybris.crm.sapserviceorderservices.populators;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.sap.sapmodel.model.SAPProductSalesAreaToCatalogMappingModel;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Populator for additional fields specific for the SAPProductCatalog
 */
public class SAPCRMProductCatalogMappingPopulator implements
        Populator<SAPProductSalesAreaToCatalogMappingModel, Map<String, Object>> {
    private static final Logger LOGGER = Logger.getLogger(SAPCRMProductCatalogMappingPopulator.class);

    @Override
    public void populate(final SAPProductSalesAreaToCatalogMappingModel source, final Map<String, Object> target) {
        final CountryModel countryModel = source.getTaxClassCountry();

        if (countryModel == null) {
            LOGGER.error("Missing customizing for Product to Catalog Mapping!");
            return;
        }
        final CatalogVersionModel catalogversion = source.getCatalogVersion();
        final CatalogModel catalog = catalogversion.getCatalog();
        target.put("taxClassCountry", source.getTaxClassCountry().getIsocode());
        target.put("catalogId", catalog.getId());

    }
}
