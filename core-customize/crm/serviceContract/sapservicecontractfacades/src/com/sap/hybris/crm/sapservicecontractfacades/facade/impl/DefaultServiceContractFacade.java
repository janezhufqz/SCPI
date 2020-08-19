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
package com.sap.hybris.crm.sapservicecontractfacades.facade.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.sap.hybris.crm.contract.model.ServiceContractModel;
import com.sap.hybris.crm.sapservicecontract.data.ServiceContractData;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.SearchResultList;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractItemResult;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractSearchResult;
import com.sap.hybris.crm.sapservicecontractfacades.constants.SapservicecontractfacadesConstants;
import com.sap.hybris.crm.sapservicecontractfacades.facade.ServiceContractFacade;
import com.sap.hybris.crm.sapservicecontractservices.service.ContractService;

/**
 * Default implementation of ServiceContractFacade
 *
 * @author C5229488
 *
 */
public class DefaultServiceContractFacade implements ServiceContractFacade {

    private static final String BACKEND_DOWN = "Backend Down";
    private UserService userService;
    private ContractService contractService;
    private Converter<ServiceContractModel, ServiceContractData> serviceContractConverter;
    private Converter<ServiceContractSearchResult, ServiceContractData> serviceContractSearchResultConverter;
    private TypeService typeService;

    /**
     * @return the typeService
     */
    public TypeService getTypeService() {
        return typeService;
    }

    /**
     * @param typeService
     *            the typeService to set
     */
    public void setTypeService(final TypeService typeService) {
        this.typeService = typeService;
    }

    /**
     * @return the serviceContractSearchResultConverter
     */
    public Converter<ServiceContractSearchResult, ServiceContractData> getServiceContractSearchResultConverter() {
        return serviceContractSearchResultConverter;
    }

    /**
     * @param serviceContractSearchResultConverter
     *            the serviceContractSearchResultConverter to set
     */
    public void setServiceContractSearchResultConverter(
            final Converter<ServiceContractSearchResult, ServiceContractData> serviceContractSearchResultConverter) {
        this.serviceContractSearchResultConverter = serviceContractSearchResultConverter;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sap.hybris.sapservicecontractfacades.facade.ServiceContractFacade#
     * getPagedServiceContractData()
     */
    @Override
    public SearchPageData<ServiceContractData> getPagedServiceContractData(final PageableData pageableData) {
        final SearchPageData<ServiceContractModel> contracts = getContractService()
                .getAllContracts(getCurrentCustomer(), pageableData);
        return convertPageData(contracts, getServiceContractConverter());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sap.hybris.sapservicecontractfacades.facade.ServiceContractFacade#
     * getSynchronizedContractData()
     */
    @Override
    public SearchPageData<ServiceContractData> getPagedSynchronizedContractData(final PageableData pageableData,
            final boolean reload) {
        final SearchPageData<ServiceContractData> resultHybrisFormat = new SearchPageData<>();
        if (isBackendDown()) {
            throw new ApplicationBaseRuntimeException(BACKEND_DOWN);
        } else {
            final SearchResultList searchResultList = getSearchResultListFromBol(pageableData, reload);
            if (searchResultList != null && CollectionUtils.isNotEmpty(searchResultList.getResults())) {
                setAllContractsRenewableProperty(searchResultList.getSearchResult());
                final List<ServiceContractData> results = convertSearchResultToDTO(searchResultList.getSearchResult());
                populatePaginationInfo(results, searchResultList, pageableData, resultHybrisFormat);
            } else {
                setDefaultPaginationInfo(pageableData, resultHybrisFormat);
            }
        }
        return resultHybrisFormat;
    }

    /**
     * method to set renewable property for all service contract
     *
     * @param searchResult
     */
    protected void setAllContractsRenewableProperty(final List<ServiceContractSearchResult> searchResult) {
        final Iterator<ServiceContractSearchResult> iterator = searchResult.iterator();
        while (iterator.hasNext()) {
            final ServiceContractSearchResult result = iterator.next();
            setContractRenewableProperty(result);
        }
    }

    /**
     * method to set renewable property for service contract based on contract
     * items status
     *
     * @param result
     */
    protected void setContractRenewableProperty(final ServiceContractSearchResult result) {
        result.setIsRenewable(false);
        if (!CollectionUtils.isEmpty(result.getItems())) {
            for (final ServiceContractItemResult itemResult : result.getItems()) {
                if (StringUtils.isNotEmpty(itemResult.getItemSysStatus()) && SapservicecontractfacadesConstants.RELEASED
                        .equalsIgnoreCase(itemResult.getItemSysStatus())) {
                    result.setIsRenewable(true);

                } else {
                    result.setIsRenewable(false);
                    break;
                }
            }
        }
    }

    /**
     * @param results
     * @param searchResultList
     * @param pageableData
     * @param resultHybrisFormat
     */
    protected void populatePaginationInfo(final List<ServiceContractData> results,
            final SearchResultList searchResultList, final PageableData pageableData,
            final SearchPageData<ServiceContractData> resultHybrisFormat) {
        resultHybrisFormat.setResults(results);
        // Setting PaginationData
        final int totalNumber = searchResultList.getResults().size();
        final PaginationData pagination = new PaginationData();
        pagination.setCurrentPage(pageableData.getCurrentPage());
        pagination.setPageSize(pageableData.getPageSize());
        pagination.setTotalNumberOfResults(totalNumber);
        pagination.setNumberOfPages(calculateNumberOfPages(totalNumber, pageableData.getPageSize()));
        resultHybrisFormat.setPagination(pagination);
        resultHybrisFormat.setSorts(getContractService().getSortOptionsFromBol());

    }

    /**
     * @param searchResult
     * @return results
     */
    protected List<ServiceContractData> convertSearchResultToDTO(final List<ServiceContractSearchResult> searchResult) {
        final List<ServiceContractData> results = new ArrayList<>();
        for (final ServiceContractSearchResult contract : searchResult) {
            results.add(getServiceContractSearchResultConverter().convert(contract));
        }
        return results;
    }

    /**
     * @param reload
     * @param pageableData
     *
     */
    protected SearchResultList getSearchResultListFromBol(final PageableData pageableData, final boolean reload) {
        return getContractService().getServiceContractsForCustomerFromBol(pageableData, reload, getCurrentCustomer());

    }

    /**
     * @return CustomerModel
     */
    protected CustomerModel getCurrentCustomer() {
        return (CustomerModel) getUserService().getCurrentUser();
    }

    /**
     * @param pageableData
     * @param resultHybrisFormat
     */
    public void setDefaultPaginationInfo(final PageableData pageableData,
            final SearchPageData<ServiceContractData> resultHybrisFormat) {
        resultHybrisFormat.setResults(new ArrayList<ServiceContractData>());
        // Set Pagination Data
        final PaginationData pagination = new PaginationData();
        pagination.setCurrentPage(pageableData.getCurrentPage());
        pagination.setPageSize(pageableData.getPageSize());
        pagination.setTotalNumberOfResults(0);
        pagination.setNumberOfPages(0);
        resultHybrisFormat.setPagination(pagination);
    }

    public <S, T> SearchPageData<T> convertPageData(final SearchPageData<S> source, final Converter<S, T> converter) {
        final SearchPageData<T> result = new SearchPageData<>();
        result.setPagination(source.getPagination());
        result.setSorts(source.getSorts());
        result.setResults(Converters.convertAll(source.getResults(), converter));
        return result;
    }

    /**
     * @return the userService
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * @param userService
     *            the userService to set
     */
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    /**
     * @return the contractService
     */
    public ContractService getContractService() {
        return contractService;
    }

    /**
     * @param contractService
     *            the contractService to set
     */
    public void setContractService(final ContractService contractService) {
        this.contractService = contractService;
    }

    /**
     * @return the serviceContractConverter
     */
    public Converter<ServiceContractModel, ServiceContractData> getServiceContractConverter() {
        return serviceContractConverter;
    }

    /**
     * @param serviceContractConverter
     *            the serviceContractConverter to set
     */
    public void setServiceContractConverter(
            final Converter<ServiceContractModel, ServiceContractData> serviceContractConverter) {
        this.serviceContractConverter = serviceContractConverter;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sap.hybris.sapservicecontractfacades.facade.ServiceContractFacade#
     * getSynchronizedServiceContractDetails(java. lang.String)
     */
    @Override
    public ServiceContractData getSynchronizedServiceContractDetails(final String contractId) {
        ServiceContractData contract = null;
        if (contractId != null) {
            if (isBackendDown()) {
                throw new ApplicationBaseRuntimeException(BACKEND_DOWN);
            } else {
                final ServiceContractSearchResult contractSearchResult = getContractService()
                        .getServiceContractDetailsFromBol(contractId, getCurrentCustomer());
                if (contractSearchResult == null) {
                    throw new UnknownIdentifierException(String.format(
                            SapservicecontractfacadesConstants.CONTRACT_NOT_FOUND_FOR_USER_AND_BACKEND, contractId));
                }
                setContractRenewableProperty(contractSearchResult);
                contract = getServiceContractSearchResultConverter().convert(contractSearchResult);
            }

        }
        return contract;
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

    protected boolean isBackendDown() {
        return getContractService().checkBackendDown();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sap.hybris.sapservicecontractfacades.facade.ServiceContractFacade#
     * renewContract(java.lang.String)
     */
    @Override
    public String renewContract(final String guid) throws ApplicationBaseRuntimeException {
        return getContractService().renewContractFromBol(guid);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sap.hybris.sapservicecontractfacades.facade.ServiceContractFacade#
     * terminateContract(java.lang.String)
     */
    @Override
    public String terminateContractItem(final String contractGuid, final String itemGuid) {
        return getContractService().terminateContractFromBol(contractGuid, itemGuid);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sap.hybris.sapservicecontractfacades.facade.ServiceContractFacade#
     * renewItem(java.lang.String, java.lang.String)
     */
    @Override
    public String renewItem(final String contractGuid, final String itemGuid) throws ApplicationBaseRuntimeException {
        return getContractService().renewContractItemfromBol(contractGuid, itemGuid);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sap.hybris.crm.sapservicecontractfacades.facade.ServiceContractFacade
     * #getServiceContractDataForIBase(java.lang.String)
     */
    @Override
    public List<ServiceContractData> getServiceContractDataForIBase(final String iBaseID) {
        List<ServiceContractData> results = new ArrayList();
        if (isBackendDown()) {
            throw new ApplicationBaseRuntimeException(BACKEND_DOWN);
        } else {
            final SearchResultList searchResultList = getIBaseServiceContractListFromBol(iBaseID);
            if (searchResultList != null && CollectionUtils.isNotEmpty(searchResultList.getResults())) {
                results = convertSearchResultToDTO(searchResultList.getResults());
            }
        }
        return results;
    }

    /**
     * Method to get searchresultlist of service contracts associated with an
     * ibase from bol layer.
     *
     * @param iBaseID
     * @return search list
     */
    private SearchResultList getIBaseServiceContractListFromBol(final String iBaseID) {
        return getContractService().getServiceContractsForIBaseFromBol(iBaseID, getCurrentCustomer());
    }

}
