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
package com.sap.hybris.crm.sapservicecontractservices.service.impl;

import java.util.List;

import com.sap.hybris.crm.contract.model.SAPStatusModel;
import com.sap.hybris.crm.contract.model.ServiceContractModel;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.SearchResultList;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractSearchResult;
import com.sap.hybris.crm.sapservicecontractbol.service.factory.SapServiceContractFactory;
import com.sap.hybris.crm.sapservicecontractservices.dao.ServiceContractDao;
import com.sap.hybris.crm.sapservicecontractservices.service.ContractService;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;

/**
 * Default Implementation of Contract Service
 *
 * @author C5229488
 *
 */
public class DefaultContractService extends AbstractBusinessService implements ContractService {
	private transient ServiceContractDao serviceContractDao;
	private transient SapServiceContractFactory sapServiceContractFactory;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.hybris.sapservicecontractfacades.service.CustomerAccountService#
	 * getAllContracts(de.hybris.platform.core. model.user.CustomerModel)
	 */
	@Override
	public SearchPageData<ServiceContractModel> getAllContracts(final CustomerModel customer,
			final PageableData pageableData) {

		return getServiceContractDao().getAllContractsForCustomer(customer, pageableData);
	}

	/**
	 * @return the serviceContractDao
	 */
	public ServiceContractDao getServiceContractDao() {
		return serviceContractDao;
	}

	/**
	 * @param serviceContractDao
	 *            the serviceContractDao to set
	 */
	public void setServiceContractDao(final ServiceContractDao serviceContractDao) {
		this.serviceContractDao = serviceContractDao;
	}

	/**
	 * @return the sapServiceContractFactory
	 */
	public SapServiceContractFactory getSapServiceContractFactory() {
		return sapServiceContractFactory;
	}

	/**
	 * @param sapServiceContractFactory
	 *            the sapServiceContractFactory to set
	 */
	public void setSapServiceContractFactory(final SapServiceContractFactory sapServiceContractFactory) {
		this.sapServiceContractFactory = sapServiceContractFactory;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.hybris.sapservicecontractfacades.service.CustomerAccountService#
	 * getContracts(de.hybris.platform.core. model.user.CustomerModel)
	 */
	@Override
	public List<ServiceContractModel> getContracts(final CustomerModel customer) {
		return getServiceContractDao().getContractsByCustomer(customer);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.hybris.sapservicecontractfacades.service.ContractService#getUid(
	 * de.hybris.platform.core.model.user. CustomerModel)
	 */
	@Override
	public String getUid(final CustomerModel customer) {
		return customer.getCustomerID();
	}

	@Override
	public List<SortData> getSortOptionsFromBol() {
		return getSapServiceContractFactory().getSapServiceContractBackendBusinessObject().getSortOptions();
	}

	@Override
	public boolean checkBackendDown() {
		return getSapServiceContractFactory().getSapServiceContractBackendBusinessObject().isBackendDown();
	}

	@Override
	public SearchResultList getServiceContractsForCustomerFromBol(PageableData pageableData, boolean reload,
			final CustomerModel customer) {
		return getSapServiceContractFactory().getSapServiceContractBackendBusinessObject()
				.getServiceContractsForCustomer(getUid(customer), pageableData, reload);
	}

	@Override
	public ServiceContractSearchResult getServiceContractDetailsFromBol(String contractId,
			final CustomerModel customer) {
		return getSapServiceContractFactory().getSapServiceContractBackendBusinessObject()
				.getServiceContractDetails(getUid(customer), contractId);
	}

	@Override
	public String renewContractFromBol(String guid) throws ApplicationBaseRuntimeException {
		return getSapServiceContractFactory().getSapServiceContractBackendBusinessObject().renewContract(guid);
	}

	@Override
	public String terminateContractFromBol(String contractGuid, final String itemGuid) {
		return getSapServiceContractFactory().getSapServiceContractBackendBusinessObject()
				.terminateContractItem(contractGuid, itemGuid);
	}

	@Override
	public String renewContractItemfromBol(String contractGuid, String itemGuid)
			throws ApplicationBaseRuntimeException {
		return getSapServiceContractFactory().getSapServiceContractBackendBusinessObject().renewItem(contractGuid,
				itemGuid);
	}

	@Override
	public SearchResultList getServiceContractsForIBaseFromBol(String iBaseID, CustomerModel customer) {
		return getSapServiceContractFactory().getSapServiceContractBackendBusinessObject()
				.getServiceContractsForIBase(getUid(customer), iBaseID);
	}

	@Override
	public SAPStatusModel getStatusForCodeAndProfile(String code, String profile) {
		return getServiceContractDao().getSAPStatusForCodeAndProfile(code, profile);
	}

	@Override
	public List<SAPStatusModel> getAllSAPStatuses() {
		return getServiceContractDao().getAllSAPStatuses();
	}

}
