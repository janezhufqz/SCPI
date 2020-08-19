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
package com.sap.hybris.crm.sapservicecontractbol.service.backend;

import java.util.List;

import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.SearchResultList;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractSearchResult;

import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;

/**
 * Backend class to search and read contracts for customer
 *
 *
 */
public interface SapCRMServiceContractBackend extends BackendBusinessObject {
    /**
     * Get Contracts for given customer ID
     *
     * @param uId
     *            SOLD-TO_PARTY/CONTRACTID
     * @return SearchResult for contracts
     */
    SearchResultList getContracts(final String uId);

    /**
     * GET details for given contractID
     *
     * @param contractId
     * @return ServiveContractModel
     */
    ServiceContractSearchResult getContractforContractId(final String userId, final String contractId);

    /**
     * check if Backend is down or not
     *
     * @return true / false
     */
    boolean isBackendDown();

    /**
     * Get the sort options for the pageable data
     *
     * @return list of sort data
     */
    public List<SortData> getSearchSort();

    /**
     * method to renew service contract for given contract guid
     */
    String renewContract(String guid) throws ApplicationBaseRuntimeException;

    /**
     * Terminate the contract item with given contractGuid and Item Guid
     */
    public String terminateContractItem(final String contractGuid, final String itemGuid);

    /**
     * method to renew a particular item in a given contract
     */
    public String renewItem(final String contractGuid, final String itemGuid);

    /**
     * Get Contracts for given ibase
     *
     * @param uid
     *            BU_PARTNER
     * @param iBaseID
     *            Installed base id
     * @return SearchResult for contracts
     */
    SearchResultList getContractsForIBase(final String uid, final String iBaseID);

}
