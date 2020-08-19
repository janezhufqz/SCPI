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
package com.sap.hybris.crm.sapservicecontractbol.backendbusinessobject;

import java.util.List;

import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.SearchResultList;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractSearchResult;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;

/**
 * Service Contract implementation for business object .
 *
 * @author C5229488
 *
 */
public interface SapServiceContractBackendBusinessObject {

    /**
     * Get all service Contracts From CRM backend for current customer
     *
     * @param UID
     * @param pageableData
     * @return ServiceContractSearchResultList
     */
    SearchResultList getServiceContractsForCustomer(String uID, PageableData pageableData, final boolean reload);

    /**
     * Get details for given Contract
     *
     * @param contractId
     * @return ServiceContractData
     */
    ServiceContractSearchResult getServiceContractDetails(final String userId, String contractId);

    /**
     * Sets state to dirty, i.e. the next search call needs to access the back
     * end
     *
     * @param b
     *            State is dirty?
     */
    void setDirty(boolean b);

    /**
     * Is state dirty, i.e. do we need to access the back end for the next
     * search call?
     *
     * @return Dirty state
     */
    boolean isDirty();

    /**
     * Sort options which are stored in the session. The UI might not always
     * send the sort options, so we need a session storage
     *
     * @return Current sort options
     */
    List<SortData> getSortOptions();

    /**
     * Return whether backend connection is available or not
     *
     * @return true or false
     */
    boolean isBackendDown();

    /**
     * renew service contract for given contract guid
     */
    String renewContract(String guid) throws ApplicationBaseRuntimeException;

    /**
     * Terminate the contract
     */
    String terminateContractItem(final String contractGuid, final String itemGuid);

    /**
     * Renew a particular item with given item guid and contract guid
     */
    String renewItem(final String contractGuid, final String itemGuid) throws ApplicationBaseRuntimeException;

    /**
     * Method to get list of service contracts for ibase.
     *
     * @param uid
     * @param iBaseID
     * @return list of contracts for ibase
     */
    SearchResultList getServiceContractsForIBase(String uid, String iBaseID);

}
