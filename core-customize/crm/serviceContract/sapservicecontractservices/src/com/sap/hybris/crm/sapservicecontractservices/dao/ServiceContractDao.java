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
package com.sap.hybris.crm.sapservicecontractservices.dao;

import java.util.List;

import com.sap.hybris.crm.contract.model.SAPStatusModel;
import com.sap.hybris.crm.contract.model.ServiceContractModel;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;

/**
 * Dao Layer to fetch Contracts using Flexible Search
 * 
 * @author C5229488
 *
 */
public interface ServiceContractDao {
	/**
	 * Get Contracts for given customer
	 *
	 * @param customer
	 * @return List<ServiceContractModel>
	 */
	List<ServiceContractModel> getContractsByCustomer(final CustomerModel customer);

	/**
	 * Get contracts for customer with SearchPageData Information
	 *
	 * @param customer
	 * @param pageableData
	 * @return SearchPageData<ServiceContractModel>
	 */
	SearchPageData<ServiceContractModel> getAllContractsForCustomer(final CustomerModel customer,
			final PageableData pageableData);

	/**
	 * Find and fetch SAP status with given code and profile
	 *
	 * @param code
	 * @param profile
	 * @return
	 */
	SAPStatusModel getSAPStatusForCodeAndProfile(final String code, final String profile);

	/**
	 * Fetch the list of all SAP Statuses from Backend
	 *
	 * @return
	 */
	List<SAPStatusModel> getAllSAPStatuses();

}
