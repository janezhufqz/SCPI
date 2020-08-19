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
package com.sap.hybris.productregistrationservices.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sap.hybris.crm.productregistrationservices.event.ProductRegistrationCreatedEvent;
import com.sap.hybris.productregistrationservices.constants.ProductregistrationservicesConstants;
import com.sap.hybris.productregistrationservices.dao.ProductRegistrationDao;
import com.sap.hybris.productregistrationservices.service.ProductRegistrationBOFactory;
import com.sap.hybris.productregistrationservices.service.ProductRegistrationService;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.productregistrationfacades.data.ProductRegistrationData;
import de.hybris.platform.sap.core.configuration.SAPConfigurationService;
import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapmodel.model.SAPProductSalesAreaToCatalogMappingModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.store.services.BaseStoreService;

/**
 * @author C5229484
 *
 */
public class DefaultProductRegistrationService implements ProductRegistrationService {

    private static final Logger LOG = Logger.getLogger(DefaultProductRegistrationService.class);

    private ProductRegistrationDao productRegistrationDao;
    private SAPConfigurationService sapCoreConfigurationService;
    private ProductRegistrationBOFactory productRegistrationBOFactory;
    private Converter<ProductModel, ProductData> productConverter;
    private BaseStoreService baseStoreService;
    private EventService eventService;

    /**
     * Get list of all the registered products along with pagination details for
     * the user
     *
     * @param userID
     *            current logged in user
     * @return SearchPageData
     * @throws BackendException
     */
    @Override
    public List<ProductRegistrationData> getRegisteredProducts(final String userID) throws BackendException {
        LOG.debug("Trying to fetch the list of registered products");
        List<ProductRegistrationData> results = new ArrayList<ProductRegistrationData>();
        validateParameterNotNull(userID, "User must not be null");
        LOG.debug("");
        results = this.productRegistrationBOFactory.getProductRegistrationBO().getRegisteredProducts(userID,
                getBackendType());
        for (final ProductRegistrationData data : results) {
            data.setProduct(populateProductData(data.getProductCode()));
        }
        return results;

    }

    /**
     * Register a new product
     *
     * @param productGuid
     * @param customerID
     */
    @Override
    public void createProductRegistration(final String productGuid, final String customerID) throws BackendException {
        LOG.debug("Processing request for registering a product");
        validateParameterNotNull(customerID, "User must not be null");
        validateParameterNotNull(productGuid, "Product Guid must not be null");
        final ProductModel product = getProductByGuid(productGuid);
        final SAPProductSalesAreaToCatalogMappingModel catalogMapping = getSalesAreaMappingFromCatalog(product);
        final Map<String, String> createRegistrationParameters = new HashMap<String, String>();
        createRegistrationParameters.put(ProductregistrationservicesConstants.SALES_ORG,
                catalogMapping.getSalesOrganization());
        createRegistrationParameters.put(ProductregistrationservicesConstants.DIS_CHANNEL,
                catalogMapping.getDistributionChannel());
        createRegistrationParameters.put(ProductregistrationservicesConstants.PRODUCT_GUID_PARAM, productGuid);
        this.productRegistrationBOFactory.getProductRegistrationBO()
                .createProductregistration(createRegistrationParameters, customerID, getBackendType());
        this.eventService.publishEvent(new ProductRegistrationCreatedEvent(initializeEvent(productGuid)));
    }

    /**
     * Get the product registration details by guid
     *
     * @param registrationNumber
     * @return ProductRegistrationData
     * @throws BackendException
     */
    @Override
    public ProductRegistrationData getRegisteredProductByGuid(final String registrationNumber, final String customerID)
            throws BackendException {
        LOG.debug("Getting the details of product registration");
        ProductRegistrationData data = null;
        validateParameterNotNull(registrationNumber, "Registration number must not be null");
        data = this.productRegistrationBOFactory.getProductRegistrationBO()
                .getRegisteredProductByGuid(registrationNumber, getBackendType(), customerID);
        data.setProduct(populateProductData(data.getProductCode()));
        return data;
    }

    @Override
    public ProductModel getProductByGuid(final String guid) {
        return this.productRegistrationDao.getProductByGuid(guid);
    }

    /**
     * @param product
     * @return SAPProductSalesAreaToCatalogMappingModel
     */
    protected SAPProductSalesAreaToCatalogMappingModel getSalesAreaMappingFromCatalog(final ProductModel product) {
        final CatalogVersionModel catalogVersion = product.getCatalogVersion();
        final CatalogModel parentCatalog = catalogVersion.getCatalog();
        final Set<CatalogVersionModel> catalogVersions = parentCatalog.getCatalogVersions();
        for (final CatalogVersionModel version : catalogVersions) {
            final SAPProductSalesAreaToCatalogMappingModel catalogMapping = this.productRegistrationDao
                    .getProductMappingByCatalogVersion(version);
            if (null != catalogMapping && null != catalogMapping.getSalesOrganization()) {
                return catalogMapping;
            }
        }
        return new SAPProductSalesAreaToCatalogMappingModel();
    }

    /**
     * Get backend type from product registration specific to current base
     * store.If there is no RFC destination specific to Registration scenario in
     * Registration tab in hmc then pick the one from common settings
     *
     * @return backendType
     */
    protected String getBackendType() {
        String backendType;
        final SAPRFCDestinationModel defaultDestination = this.baseStoreService.getCurrentBaseStore()
                .getSAPConfiguration().getProductRegistrationRfcDestination();
        if (null != defaultDestination) {
            backendType = defaultDestination.getBackendType().getCode();
        } else {
            backendType = this.sapCoreConfigurationService.getBackendType();
        }

        return backendType;
    }

    protected ProductRegistrationData initializeEvent(final String productGuid) {
        final ProductRegistrationData data = new ProductRegistrationData();
        data.setProduct(populateProductData(productGuid));
        return data;
    }

    /**
     * Populate product dto from product model
     *
     * @param productGuid
     */
    private ProductData populateProductData(final String productGuid) {
        ProductData data = new ProductData();
        final ProductModel product = this.productRegistrationDao.getProductByGuid(productGuid);
        if (null != product) {
            data = this.productConverter.convert(product);
        }
        return data;
    }

    /**
     * @param productRegistrationDao
     *            the productRegistrationDao to set
     */
    public void setProductRegistrationDao(final ProductRegistrationDao productRegistrationDao) {
        this.productRegistrationDao = productRegistrationDao;
    }

    /**
     * @param sapCoreConfigurationService
     *            the sapCoreConfigurationService to set
     */
    public void setSapCoreConfigurationService(final SAPConfigurationService sapCoreConfigurationService) {
        this.sapCoreConfigurationService = sapCoreConfigurationService;
    }

    /**
     * @param productRegistrationBOFactory
     *            the productRegistrationBOFactory to set
     */
    public void setProductRegistrationBOFactory(final ProductRegistrationBOFactory productRegistrationBOFactory) {
        this.productRegistrationBOFactory = productRegistrationBOFactory;
    }

    /**
     * @param productConverter
     *            the productConverter to set
     */
    public void setProductConverter(final Converter<ProductModel, ProductData> productConverter) {
        this.productConverter = productConverter;
    }

    /**
     * @param baseStoreService
     *            the baseStoreService to set
     */
    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public void setEventService(final EventService eventService) {
        this.eventService = eventService;
    }

}
