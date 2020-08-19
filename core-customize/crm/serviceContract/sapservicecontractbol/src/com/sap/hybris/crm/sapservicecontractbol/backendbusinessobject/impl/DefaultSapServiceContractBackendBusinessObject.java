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
package com.sap.hybris.crm.sapservicecontractbol.backendbusinessobject.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.sap.core.bol.businessobject.BackendDeterminationRuntimeException;
import de.hybris.platform.sap.core.bol.businessobject.BackendInterface;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.bol.logging.LogSeverity;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.exceptions.CoreBaseRuntimeException;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.sap.hybris.crm.sapservicecontractbol.backendbusinessobject.SapServiceContractBackendBusinessObject;
import com.sap.hybris.crm.sapservicecontractbol.constants.SapservicecontractbolConstants;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.SapCRMServiceContractBackend;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.SearchResultList;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractSearchResult;


/**
 * Default Implementation of SapServiceContractBackendBusinessObject.
 *
 *
 */
@BackendInterface(SapCRMServiceContractBackend.class)
public class DefaultSapServiceContractBackendBusinessObject extends BusinessObjectBase
		implements SapServiceContractBackendBusinessObject
{

	private static final Log4JWrapper LOGGER = Log4JWrapper
			.getInstance(DefaultSapServiceContractBackendBusinessObject.class.getName());


	private boolean dirty = true;
	private boolean backendWasUp = false;
	private boolean backendWasDown = false;
	private List<SortData> sortOptions = getValidSortOptions();
	private SessionService sessionService;
	private SearchResultList searchResultList;
	private TypeService typeService;

	/**
	 * @return the searchResultList
	 */
	public SearchResultList getSearchResultList()
	{
		return searchResultList;
	}

	/**
	 * @param searchResultList
	 *           the searchResultList to set
	 */
	public void setSearchResultList(final SearchResultList searchResultList)
	{
		this.searchResultList = searchResultList;
	}

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * Returns Backendbusiness object
	 *
	 * @return backendbusiness object
	 * @throws BackendException
	 */
	public SapCRMServiceContractBackend getSapServiceContractBackendBusinessObject() throws BackendException
	{

		return (SapCRMServiceContractBackend) getBackendBusinessObject();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.sapservicecontractfacades.backendbusinessobject. SapServiceContractBackendBusinessObject#
	 * getServiceContractsForCustomer(java.lang.String)
	 */
	@Override
	public SearchResultList getServiceContractsForCustomer(final String uID, final PageableData pageableData, final boolean reload)
	{
		if (isDirty() || reload)
		{
			try
			{
				LOGGER.debug(
						"**********************************  SapServiceContractfacades :: getSAPServiceContractsBackentBusinessObject Called");
				searchResultList = getSapServiceContractBackendBusinessObject().getContracts(uID);
				if (searchResultList != null && CollectionUtils.isNotEmpty(searchResultList.getResults()))
				{
					removeCompletedContractFromList(searchResultList.getResults());
				}
				setDirty(false);
			}
			catch (final BackendException e)
			{
				throw new ApplicationBaseRuntimeException(SapservicecontractbolConstants.BACKEND_ERROR_MSG, e);
			}
		}
		if (searchResultList != null)
		{
			searchResultList.setPageableData(pageableData);
			setSortOptions(getCheckedSortOption(pageableData));
		}
		return searchResultList;
	}

	/**
	 * method to remove service contract from result list which are in completed status
	 *
	 * @param searchResult
	 */
	protected void removeCompletedContractFromList(final List<ServiceContractSearchResult> searchResult)
	{

		final Iterator<ServiceContractSearchResult> iterator = searchResult.iterator();
		while (iterator.hasNext())
		{
			final ServiceContractSearchResult result = iterator.next();
			if (result.getStatusCode() == null && result.getContractSysStatus() == null)
			{
				LOGGER.log(LogSeverity.ERROR, LogCategories.APPS_COMMON_RESOURCES,
						"Contract Header status is null for contract id : " + result.getContractId() + " " + "It cannot be null.");
			}
			else if (SapservicecontractbolConstants.I_STATUS_COMPLETED.equalsIgnoreCase(result.getContractSysStatus())
					|| SapservicecontractbolConstants.E_STATUS_COMPLETED.equalsIgnoreCase(result.getStatusCode()))
			{
				iterator.remove();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.sapservicecontractfacades.backendbusinessobject. SapServiceContractBackendBusinessObject#
	 * getServiceContractDetails(java.lang.String)
	 */
	@Override
	public ServiceContractSearchResult getServiceContractDetails(final String userId, final String contractId)
	{
		ServiceContractSearchResult result = null;
		try
		{
			LOGGER.debug(
					"**********************************  SapServiceContractfacades :: getSAPServiceContractsBackentBusinessObject Called");
			result = getSapServiceContractBackendBusinessObject().getContractforContractId(userId, contractId);
		}
		catch (final BackendException e)
		{
			LOGGER.debug("Error in connection via RFC", e);
		}

		return result;
	}

	protected List<SortData> getValidSortOptions()
	{
		final List<SortData> sortOptionsList = new ArrayList<SortData>();

		SortData sortData = new SortData();
		sortData.setCode("startDate");
		sortOptionsList.add(sortData);

		sortData = new SortData();
		sortData.setCode("endDate");
		sortOptionsList.add(sortData);

		sortData = new SortData();
		sortData.setCode("netValue");
		sortOptionsList.add(sortData);

		sortData = new SortData();
		sortData.setCode("status");
		sortOptionsList.add(sortData);

		sortData = new SortData();
		sortData.setCode("contractId");
		sortOptionsList.add(sortData);

		return sortOptionsList;
	}

	List<SortData> getCheckedSortOption(final PageableData pageableData)
	{
		final List<SortData> sortOptionsList = getSortOptions();

		final String sort = pageableData.getSort();
		if (sort != null && (!sort.isEmpty()))
		{
			for (final SortData sortOption : sortOptionsList)
			{
				sortOption.setSelected(sort.equals(sortOption.getCode()));
			}
		}
		else
		{
			for (final SortData sortOption : sortOptionsList)
			{
				if (sortOption.isSelected())
				{
					pageableData.setSort(sortOption.getCode());
				}
			}
		}

		return sortOptionsList;
	}

	/**
	 * @param sortData
	 */
	protected void setSortOptions(final List<SortData> sortData)
	{
		this.sortOptions = sortData;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.sapservicecontractfacades.backendbusinessobject.
	 * SapServiceContractBackendBusinessObject#setDirty( boolean)
	 */
	@Override
	public void setDirty(final boolean b)
	{
		this.dirty = b;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.sapservicecontractfacades.backendbusinessobject.
	 * SapServiceContractBackendBusinessObject#isDirty()
	 */
	@Override
	public boolean isDirty()
	{
		return dirty;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.sapservicecontractfacades.backendbusinessobject. SapServiceContractBackendBusinessObject#
	 * getSortOptions()
	 */
	@Override
	public List<SortData> getSortOptions()
	{
		return sortOptions;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.sapservicecontractfacades.backendbusinessobject. SapServiceContractBackendBusinessObject#
	 * isBackendDown()
	 */
	@Override
	public boolean isBackendDown()
	{
		if (isBackendWasDown())
		{
			return checkBackendNeverWasUp();
		}

		try
		{
			final boolean currentlyDown = getSapServiceContractBackendBusinessObject().isBackendDown();
			if (!currentlyDown)
			{
				setBackendWasUp(true);
				return false;
			}
			else
			{
				setBackendWasDown(true);
				return checkBackendNeverWasUp();
			}
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Cannot determine backend availability", e);
		}
	}

	/**
	 * Checks whether backend was up previously and thus a runtime exception needs to be raised
	 *
	 * @return true if backend was never up before
	 */
	boolean checkBackendNeverWasUp()
	{
		if (isBackendWasUp())
		{
			getSessionService().closeCurrentSession();
			throw new ApplicationBaseRuntimeException("Back end went down, session needs to be terminated");
		}
		else
		{
			return true;
		}
	}

	void setBackendWasUp(final boolean b)
	{
		this.backendWasUp = b;
	}

	boolean isBackendWasUp()
	{
		return this.backendWasUp;
	}

	void setBackendWasDown(final boolean b)
	{
		this.backendWasDown = b;
	}

	boolean isBackendWasDown()
	{
		return this.backendWasDown;
	}

	@Override
	public String renewContract(final String guid) throws ApplicationBaseRuntimeException
	{
		try
		{
			return getSapServiceContractBackendBusinessObject().renewContract(guid);
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Error while connecting to backend", e);
		}
	}

	@Override
	public String terminateContractItem(final String contractGuid, final String itemGuid)
	{
		String status = null;
		try
		{
			status = getSapServiceContractBackendBusinessObject().terminateContractItem(contractGuid, itemGuid);
		}
		catch (final BackendException e)
		{
			LOGGER.debug("Error in connection via RFC during the termination of contract item", e);
			throw new ApplicationBaseRuntimeException("Back end went down, session needs to be terminated");
		}
		return status;
	}

	@Override
	public String renewItem(final String contractGuid, final String itemGuid) throws ApplicationBaseRuntimeException
	{
		try
		{
			return getSapServiceContractBackendBusinessObject().renewItem(contractGuid, itemGuid);
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Error while connecting to backend", e);
		}
	}

	@Override
	protected void determineBackendObject(final boolean initialize) throws ApplicationBaseRuntimeException
	{
		try
		{
			super.determineBackendObject(initialize);
		}
		catch (final BackendDeterminationRuntimeException e)
		{
			final String exceptionMessage = "Invalid backend type for RFC Destination is configured in backoffice. Please update the same to CRM!!!";
			LOGGER.log(LogSeverity.ERROR, LogCategories.APPS_COMMON_CORE, exceptionMessage);
			throw new CoreBaseRuntimeException(exceptionMessage, e);
		}



	}

	/**
	 * @return the typeService
	 */
	public TypeService getTypeService()
	{
		return typeService;
	}

	/**
	 * @param typeService
	 *           the typeService to set
	 */
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	@Override
	public SearchResultList getServiceContractsForIBase(final String uid, final String iBaseID)
	{
		try
		{
			LOGGER.debug(
					"**********************************  DefaultSapServiceContractBackendBusinessObject :: getServiceContractsForIBase Called");
			searchResultList = getSapServiceContractBackendBusinessObject().getContractsForIBase(uid, iBaseID);
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException(SapservicecontractbolConstants.BACKEND_ERROR_MSG, e);
		}
		return searchResultList;
	}

}
