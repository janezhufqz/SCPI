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
package com.sap.hybris.crm.sapservicecontractfacades.facade;

import java.util.List;

import com.sap.hybris.crm.sapservicecontract.data.ServiceContractData;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;

/**
 * To pass contract information from service layer to web UI
 *
 * @author C5229488
 *
 */
public interface ServiceContractFacade {
    /**
     * method to get contract data for a customer from hybris database
     *
     * @return ServiceContractData
     */
    SearchPageData<ServiceContractData> getPagedServiceContractData(final PageableData pageableData);

    /**
     * method to get service contracts for current customer via synchronized
     * call
     *
     * @return ServiceContractData
     */
    SearchPageData<ServiceContractData> getPagedSynchronizedContractData(final PageableData pageableData,
            final boolean reload);

    /**
     * method to get all the details for contract detail page.
     *
     * @param contractId
     * @return ServiceContractData
     */
    ServiceContractData getSynchronizedServiceContractDetails(final String contractId);

    /**
     * method to renew service contract
     *
     * @param guid
     * @param contractId
     * @return String
     * @throws ApplicationBaseRuntimeException
     */
    String renewContract(final String guid) throws ApplicationBaseRuntimeException;

    /**
     * Terminate contract item with given contractGuid and itemGuid
     *
     * @param contractGuid
     * @param itemGuid
     * @return String
     */
    String terminateContractItem(final String contractGuid, final String itemGuid);

    /**
     * Renew a item for given contract and item guid
     *
     * @param contractGuid
     * @param itemGuid
     * @return String
     * @throws ApplicationBaseRuntimeException
     */
    String renewItem(final String contractGuid, final String itemGuid) throws ApplicationBaseRuntimeException;

    /**
     * Method to get list of service contracts associated with ibase
     *
     * @param iBaseID
     * @return list of service contracts
     */
    List<ServiceContractData> getServiceContractDataForIBase(String iBaseID);

}
