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
package com.sap.hybris.productregistrationfacades.registration.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.productregistrationfacades.data.ProductRegistrationData;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.sap.hybris.productregistrationfacades.constants.ProductregistrationfacadesConstants;
import com.sap.hybris.productregistrationfacades.registration.ProductRegistrationFacade;
import com.sap.hybris.productregistrationservices.service.ProductRegistrationService;

/**
 * @author C5229484
 *
 */
public class DefaultProductRegistrationFacade implements ProductRegistrationFacade {
    private static final Logger LOG = Logger.getLogger(DefaultProductRegistrationFacade.class);

    private UserService userService;
    private ProductRegistrationService productRegistrationService;
    private ProductService productService;

    @Override
    public SearchPageData<ProductRegistrationData> getRegisteredProducts(final PageableData pageableData)
            throws BackendException {
        LOG.debug("Trying to fetch the list of registered products");
        List<ProductRegistrationData> registeredProducts;
        final SearchPageData<ProductRegistrationData> pagedResults = new SearchPageData<>();
        registeredProducts = this.productRegistrationService
                .getRegisteredProducts(getCustomerID().get(ProductregistrationfacadesConstants.CUSTOMER_ID));
        if (CollectionUtils.isNotEmpty(registeredProducts)) {
            pagedResults.setResults(registeredProducts);
            // Set PaginData

            final int totalNumber = getSearchResultsTotalNumber(registeredProducts);
            final PaginationData pagination = new PaginationData();
            pagination.setNumberOfPages(calculateNumberOfPages(totalNumber, pageableData.getPageSize()));
            pagination.setCurrentPage(
                    Math.max(0, Math.min(pagination.getNumberOfPages(), pageableData.getCurrentPage())));
            pagination.setPageSize(pageableData.getPageSize());
            pagination.setTotalNumberOfResults(totalNumber);
            pagination.setSort("registrationNumber");
            pagedResults.setPagination(pagination);
            pagedResults.setSorts(getValidSortOptions());
        } else {
            setDefualtResult(pageableData, pagedResults);
        }
        return pagedResults;
    }

    /**
     * Create a new product registration for the user
     *
     * @param productRegistrationData
     *            dto containing registration request
     */
    @Override
    public ProductRegistrationData createProductRegistration(final ProductRegistrationData productRegistrationData)
            throws BackendException {
        LOG.debug("Processing request for registering a product");

        final ProductModel product = this.productService.getProduct(productRegistrationData.getProductCode());
        this.productRegistrationService.createProductRegistration(product.getGuid(),
                getCustomerID().get(ProductregistrationfacadesConstants.CUSTOMER_ID));

        return productRegistrationData;
    }

    /**
     * Get the product registration details by guid
     *
     * @param registrationNumber
     * @return ProductRegistrationData
     */
    @Override
    public ProductRegistrationData getRegisteredProductByGuid(final String registrationNumber) throws BackendException {
        LOG.debug("Getting the details of product registration");

        return this.productRegistrationService.getRegisteredProductByGuid(registrationNumber,
                getCustomerID().get(ProductregistrationfacadesConstants.CUSTOMER_ID));
    }

    /**
     * @param pageableData
     * @param result
     */
    protected void setDefualtResult(final PageableData pageableData,
            final SearchPageData<ProductRegistrationData> result) {
        result.setResults(new ArrayList<ProductRegistrationData>());
        // Set PaginData
        final PaginationData pagination = new PaginationData();
        pagination.setCurrentPage(pageableData.getCurrentPage());
        pagination.setPageSize(pageableData.getPageSize());
        pagination.setTotalNumberOfResults(0);
        pagination.setNumberOfPages(0);
        result.setPagination(pagination);
    }

    /**
     * Get partner number
     *
     * @return partner number
     */
    protected Map<String, String> getCustomerID() {
        final Map<String, String> customerDetails = new HashMap<String, String>();
        final UserModel user = this.userService.getCurrentUser();

        if (user.getClass() == CustomerModel.class) {
            customerDetails.put(ProductregistrationfacadesConstants.USER_ROLE,
                    ProductregistrationfacadesConstants.B2B_USER_ROLE);
        } else {
            customerDetails.put(ProductregistrationfacadesConstants.USER_ROLE,
                    ProductregistrationfacadesConstants.B2C_USER_ROLE);
        }
        final CustomerModel customer = (CustomerModel) user;
        customerDetails.put(ProductregistrationfacadesConstants.CUSTOMER_ID, customer.getCustomerID());
        return customerDetails;
    }

    protected int getSearchResultsTotalNumber(final List<ProductRegistrationData> searchResultList) {
        if (searchResultList == null) {
            return 0;
        } else {
            return searchResultList.size();
        }
    }

    /**
     * Calculates number of pages for a search result
     *
     * @param totalNumberOfResults
     *            Total number
     * @param pageSize
     *            Page size
     * @return Number of pages needed
     */
    protected int calculateNumberOfPages(final int totalNumberOfResults, final int pageSize) {
        final double totalNumber = totalNumberOfResults;
        final double page = pageSize;
        final double result = Math.ceil(totalNumber / page);
        return (int) result;

    }

    protected List<SortData> getValidSortOptions() {
        final List<SortData> sortOptionsList = new ArrayList<SortData>();

        SortData sortData = new SortData();
        sortData.setCode("productCode");
        sortOptionsList.add(sortData);

        sortData = new SortData();
        sortData.setCode("registrationNumber");
        sortOptionsList.add(sortData);
        return sortOptionsList;
    }

    /**
     * @param userService
     *            the userService to set
     */
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    /**
     * @param productRegistrationService
     *            the productRegistrationService to set
     */
    public void setProductRegistrationService(final ProductRegistrationService productRegistrationService) {
        this.productRegistrationService = productRegistrationService;
    }

    /**
     * @param productService
     *            the productService to set
     */
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

}
