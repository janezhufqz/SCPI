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
package com.sap.hybris.crm.sapservicecontractservices.service;

import java.util.List;

import com.sap.hybris.crm.contract.model.SAPStatusModel;
import com.sap.hybris.crm.contract.model.ServiceContractModel;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.SearchResultList;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractSearchResult;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;

/**
 * Services to get contracts from Hybris backend
 * 
 * @author C5229488
 *
 */
public interface ContractService {
	/**
	 * get All contracts for giver customer asynchronously
	 *
	 * @param customer
	 * @param pageableData
	 * @return List of ServiceContractModel to be displayed on UI
	 */
	public SearchPageData<ServiceContractModel> getAllContracts(CustomerModel customer,
			final PageableData pageableData);

	/**
	 * Get List of Service contracts for customer
	 */
	public List<ServiceContractModel> getContracts(CustomerModel customer);

	/**
	 * Get UID for customer depending on wether it is a b2b/b2c customer
	 *
	 * @param customer
	 * @return String UID
	 */
	public String getUid(final CustomerModel customer);

	/**
	 * Sort options which are stored in the session. The UI might not always
	 * send the sort options, so we need a session storage
	 *
	 * @return Current sort options
	 */
	List<SortData> getSortOptionsFromBol();

	/**
	 * Return whether backend connection is available or not
	 *
	 * @return true or false
	 */
	boolean checkBackendDown();

	/**
	 * Get all service Contracts From CRM backend for current customer
	 *
	 * @param UID
	 * @param pageableData
	 * @return ServiceContractSearchResultList
	 */
	SearchResultList getServiceContractsForCustomerFromBol(PageableData pageableData, final boolean reload,
			final CustomerModel customer);

	/**
	 * Get details for given Contract
	 *
	 * @param contractId
	 * @param userId
	 * @return ServiceContractData
	 */
	ServiceContractSearchResult getServiceContractDetailsFromBol(String contractId, final CustomerModel customer);

	/**
	 * renew service contract for given contract guid
	 */
	String renewContractFromBol(String guid) throws ApplicationBaseRuntimeException;

	/**
	 * Terminate the contract item with given contract guid and item guid
	 */
	String terminateContractFromBol(final String contractGuid, final String itemGuid);

	/**
	 * Renew an item from given contract
	 * 
	 * @param contractGuid
	 * @param itemGuid
	 * @return
	 */
	String renewContractItemfromBol(final String contractGuid, final String itemGuid)
			throws ApplicationBaseRuntimeException;

	/**
	 * Method to find service contracts by ibase id.
	 * 
	 * @param iBaseID
	 * @param currentCustomer
	 * @return service contract search list
	 */
	SearchResultList getServiceContractsForIBaseFromBol(String iBaseID, CustomerModel currentCustomer);

	/**
	 * Method to find and fetch the status with given code and profile
	 * 
	 * @param code
	 * @param profile
	 * @return
	 */
	SAPStatusModel getStatusForCodeAndProfile(final String code, final String profile);

	/**
	 * To get all the SAP Statuses imported from the SAP Backend
	 * 
	 * @return
	 */
	List<SAPStatusModel> getAllSAPStatuses();
}
