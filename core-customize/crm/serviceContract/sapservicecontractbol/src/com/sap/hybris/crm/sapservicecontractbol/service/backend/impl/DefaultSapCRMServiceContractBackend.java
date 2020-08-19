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
package com.sap.hybris.crm.sapservicecontractbol.service.backend.impl;

import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.backend.jco.BackendBusinessObjectBaseJCo;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.bol.logging.LogSeverity;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionFactory;
import de.hybris.platform.sap.core.jco.connection.JCoStateful;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.genil.GenilAttribute;
import com.sap.genil.GenilConst;
import com.sap.genil.GenilConst.GenilMessageType;
import com.sap.genil.GenilDataContainer;
import com.sap.genil.GenilMessageContainer;
import com.sap.genil.GenilMessageRequestContainer;
import com.sap.genil.GenilProxyFactory;
import com.sap.genil.GenilQueryRequestContainer;
import com.sap.genil.GenilReadAllRequestContainer;
import com.sap.genil.GenilReadRootRequestContainer;
import com.sap.genil.GenilRootDataContainer;
import com.sap.genil.IGenilProxyStateAware;
import com.sap.genil.exception.GenilBackendException;
import com.sap.genil.exception.GenilBackendRuntimeException;
import com.sap.hybris.crm.sapcrmmodel.util.XSSFilterUtil;
import com.sap.hybris.crm.sapservicecontractbol.backendbusinessobject.SapServiceContractBackendBusinessObject;
import com.sap.hybris.crm.sapservicecontractbol.constants.SapservicecontractbolConstants;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.SapCRMServiceContractBackend;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.SearchResultList;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractGenilContainer;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractItemResult;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractSearchResult;
import com.sap.tc.logging.Severity;


/**
 * Default implementation of SapCRMServiceContractBackend
 *
 *
 */
@BackendType("CRM")
public class DefaultSapCRMServiceContractBackend extends BackendBusinessObjectBaseJCo implements SapCRMServiceContractBackend
{
	private static final Log4JWrapper Logger = Log4JWrapper.getInstance(DefaultSapCRMServiceContractBackend.class.getName());

	private I18NService i18nService;
	private UserService userService;
	private BaseStoreService baseStoreService;
	private static String E_STATUS_REGEX = "E";
	@Resource(name = "sapCoreJCoManagedConnectionFactory")
	protected JCoManagedConnectionFactory managedConnectionFactory;

	private String jcoFunctionTerminate;
	private String jcoFuntionRenew;
	private String jcoFuntionItemRenew;

	/**
	 * @return the jcoFuntionItemRenew
	 */
	public String getJcoFuntionItemRenew()
	{
		return jcoFuntionItemRenew;
	}

	/**
	 * @param jcoFuntionItemRenew
	 *           the jcoFuntionItemRenew to set
	 */
	public void setJcoFuntionItemRenew(final String jcoFuntionItemRenew)
	{
		this.jcoFuntionItemRenew = jcoFuntionItemRenew;
	}

	/**
	 * @return the jcoFunctionTerminate
	 */
	public String getJcoFunctionTerminate()
	{
		return jcoFunctionTerminate;
	}

	/**
	 * @param jcoFunctionTerminate
	 *           the jcoFunctionTerminate to set
	 */
	public void setJcoFunctionTerminate(final String jcoFunctionTerminate)
	{
		this.jcoFunctionTerminate = jcoFunctionTerminate;
	}

	/**
	 * @return the i18nService
	 */
	public I18NService getI18nService()
	{
		return i18nService;
	}

	/**
	 * @param i18nService
	 *           the i18nService to set
	 */
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the managedConnectionFactory
	 */
	public JCoManagedConnectionFactory getManagedConnectionFactory()
	{
		return managedConnectionFactory;
	}

	/**
	 * @param managedConnectionFactory
	 *           the managedConnectionFactory to set
	 */
	public void setManagedConnectionFactory(final JCoManagedConnectionFactory managedConnectionFactory)
	{
		this.managedConnectionFactory = managedConnectionFactory;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.sapservicecontractfacades.service.backend.
	 * SapCRMServiceContractBackend#getContractsforCustomer(java .lang.String, java.lang.String)
	 */
	@Override
	public SearchResultList getContracts(final String uId)
	{
		Logger.entering("getContractsforCustomer(final String uId, final String queryParam)");
		List<GenilRootDataContainer> searchResult = null;
		JCoConnection connection = null;
		IGenilProxyStateAware genilProxy = null;
		SearchResultList resultList = null;
		try
		{
			// Get JCo connection
			connection = managedConnectionFactory.getManagedConnection(getDefaultConnectionName(), this.getClass().getName());
			genilProxy = (IGenilProxyStateAware) GenilProxyFactory.getProxy(connection, SapservicecontractbolConstants.BT);
			genilProxy.isInitialized();
			GenilQueryRequestContainer queryReqCont;
			queryReqCont = genilProxy.getQueryRequestContainer(SapservicecontractbolConstants.BTQ_SERVICE_CONTRACT);
			queryReqCont.addQueryParam(SapservicecontractbolConstants.SOLD_TO_PARTY_STRING, SapservicecontractbolConstants.SIGN,
					SapservicecontractbolConstants.EQ, uId);
			final String processType = getProperty(SapservicecontractbolConstants.CONFIGURATION_PROPERTY_CONTRACT_TYPE).toString();
			queryReqCont.addQueryParam(SapservicecontractbolConstants.PROCESS_TYPE, SapservicecontractbolConstants.SIGN,
					SapservicecontractbolConstants.EQ, processType);
			queryReqCont.setMaxHits(getMaxHits());
			searchResult = genilProxy.search(queryReqCont);
			if (searchResult != null && !searchResult.isEmpty())
			{
				resultList = processResult(searchResult, genilProxy);
			}
		}
		catch (final GenilBackendException e)
		{
			Logger.debug(SapservicecontractbolConstants.GENIL_ERROR_MSG, e);
			throw new ApplicationBaseRuntimeException(SapservicecontractbolConstants.BACKEND_ERROR_MSG);
		}
		catch (final JCoException e)
		{
			Logger.debug(SapservicecontractbolConstants.JCO_ERROR_MSG, e);
			throw new ApplicationBaseRuntimeException(SapservicecontractbolConstants.BACKEND_ERROR_MSG);
		}
		finally
		{
			closeConnection(connection);
		}
		return resultList;

	}

	/**
	 * Method to validate the maxHits for service contracts
	 */
	private Integer validateMaxHits()
	{
		final Object hits = getProperty(SapservicecontractbolConstants.MAX_HITS);
		return (int) hits;
	}

	/**
	 * Close the connection after completion of search and read operation
	 *
	 * @param connection
	 */
	protected void closeConnection(final JCoConnection connection)
	{
		try
		{

			if (connection != null)
			{
				((JCoStateful) connection).destroy();
			}
		}
		catch (final BackendException ex)
		{

			Logger.log(LogSeverity.ERROR, LogCategories.APPS_COMMON_RESOURCES, "Error during JCoStateful connection close! " + ex);
		}
	}

	/**
	 * Process the search result
	 *
	 * @param searchResult
	 * @param genilProxy
	 * @return ServiceContractSearchResultList
	 */
	protected SearchResultList processResult(final List<GenilRootDataContainer> searchResult,
			final IGenilProxyStateAware genilProxy)
	{
		final SearchResultList resultList = createSearchResultList();
		for (int i = 0; i < searchResult.size(); i++)
		{
			final GenilRootDataContainer root = searchResult.get(i);
			final ServiceContractSearchResult contractData = createSearchResult();
			try
			{
				contractData.setContractId(XSSFilterUtil.filter(root.getAttributeValue(SapservicecontractbolConstants.OBJECT_ID)));
				contractData.setDescription(XSSFilterUtil.filter(root.getAttributeValue(SapservicecontractbolConstants.DESCRIPTION)));
				contractData.setNetValue(XSSFilterUtil.filter(root.getAttributeValue(SapservicecontractbolConstants.NET_VALUE)));
				contractData.setStartDate(convertString2date(XSSFilterUtil.filter(root
						.getAttributeValue(SapservicecontractbolConstants.DATE_START))));
				contractData.setEndDate(convertString2date(XSSFilterUtil.filter(root
						.getAttributeValue(SapservicecontractbolConstants.DATE_END))));
				contractData.setCurrency(XSSFilterUtil.filter(root.getAttributeValue(SapservicecontractbolConstants.CURRENCY)));
				contractData.setContractConcatStatus(XSSFilterUtil.filter(root
						.getAttributeValue(SapservicecontractbolConstants.CONCATSTAT)));
				contractData.setContractGuid(XSSFilterUtil.filter(root.getAttributeValue(SapservicecontractbolConstants.GUID)));
				final GenilRootDataContainer genilRoot = readContractRelationsForStatus(root, genilProxy);
				final ServiceContractGenilContainer contractGenilContainer = getServiceContractGenilContainer(genilRoot);
				setContractHeaderStatus(contractData, contractGenilContainer);
				putContractItems(contractGenilContainer);
				final List<ServiceContractItemResult> items = new ArrayList<ServiceContractItemResult>();
				if (contractGenilContainer.getContractItemsAllList() != null)
				{
					createItemResultAndPopulateItemResult(contractGenilContainer, items);
				}
				contractData.setItems(items);
			}
			catch (final GenilBackendException e)
			{
				Logger.debug("Error in search via genil serach api", e);
				throw new ApplicationBaseRuntimeException("Back end went down, session needs to be terminated");
			}
			resultList.add(contractData);
		}
		if (Logger.isDebugEnabled())
		{
			final StringBuilder debugOutput = new StringBuilder("ProcessResults, ");
			debugOutput.append("number of results: " + resultList.size());
			Logger.debug(debugOutput);
		}
		performLogging(resultList.size(), getMaxHits());
		return resultList;

	}

	private void createItemResultAndPopulateItemResult(final ServiceContractGenilContainer contractGenilContainer,
			final List<ServiceContractItemResult> items)
	{
		ServiceContractItemResult product = null;
		for (final GenilDataContainer genilDataContainer : contractGenilContainer.getContractItemsAllList())
		{
			product = createItemResult();
			populateContractItemStatus(genilDataContainer, product, contractGenilContainer);
			items.add(product);
		}
	}

	/**
	 * read contract relations for contract header status and items status
	 *
	 * @param searchResult
	 * @param genilProxy
	 */
	private GenilRootDataContainer readContractRelationsForStatus(final GenilRootDataContainer searchResult,
			final IGenilProxyStateAware genilProxy) throws GenilBackendException
	{
		GenilRootDataContainer root;
		GenilReadRootRequestContainer rootReq;
		GenilReadAllRequestContainer childReq;
		GenilReadAllRequestContainer childReq1;
		GenilReadAllRequestContainer childReq2;
		GenilReadAllRequestContainer childReq3;
		GenilReadAllRequestContainer childReq10;
		GenilReadAllRequestContainer childReq11;
		GenilReadAllRequestContainer childReq12;
		GenilReadAllRequestContainer childReq13;


		root = searchResult;
		final String contractGuid = root.getRtGuid();
		rootReq = genilProxy.getReadRequestContainer(root.getObjectName(), contractGuid);
		rootReq.setAttrRequested(true);
		final GenilMessageContainer msgCont = genilProxy.getGlobalMessageContainer();
		msgCont.getMessages();
		final GenilMessageRequestContainer msgReqCont = genilProxy.getMessageRequestContainer();
		//reading relations
		childReq = addChildRelation(rootReq, SapservicecontractbolConstants.BT_ADVS_SRV_CON, msgReqCont);
		childReq1 = addChildRelation(childReq, SapservicecontractbolConstants.BT_ORDER_HEADER, msgReqCont);
		childReq2 = addChildRelation(childReq1, SapservicecontractbolConstants.BT_HEADER_ITEMS_EXT, msgReqCont);
		childReq3 = addChildRelation(childReq2, SapservicecontractbolConstants.BT_SRV_CNTRCT_ALL, msgReqCont);
		childReq10 = addChildRelation(childReq1, SapservicecontractbolConstants.BT_HEADER_STATUS_SET, msgReqCont);
		childReq11 = addChildRelation(childReq10, SapservicecontractbolConstants.BT_STATUS_H_ALL, msgReqCont);
		childReq12 = addChildRelation(childReq3, SapservicecontractbolConstants.BT_ITEM_STATUS_SET, msgReqCont);
		childReq13 = addChildRelation(childReq12, SapservicecontractbolConstants.BT_STATUS_I_ALL, msgReqCont);

		// reading attributes for user status
		addAttributeToRelation(childReq11, SapservicecontractbolConstants.STATUS_PROFILE);
		addAttributeToRelation(childReq11, SapservicecontractbolConstants.STATUS);
		addAttributeToRelation(childReq13, SapservicecontractbolConstants.STATUS_PROFILE);
		addAttributeToRelation(childReq13, SapservicecontractbolConstants.STATUS);


		root = genilProxy.read(rootReq, msgReqCont);
		return root;
	}

	private void addAttributeToRelation(final GenilReadAllRequestContainer childReq, final String attributeName)
	{
		if (childReq != null)
		{
			childReq.addRequestedAttribute(attributeName);
		}
	}



	/**
	 * Convert string into date
	 *
	 * @param date
	 * @return Date
	 */
	protected Date convertString2date(final String date)
	{
		final SimpleDateFormat sdf = new SimpleDateFormat(SapservicecontractbolConstants.CRM_DATE_FORMAT);
		Date convertedDate = null;
		if (null != date)
		{
			try
			{
				convertedDate = sdf.parse(date);
			}
			catch (final ParseException e)
			{
				Logger.log(Severity.ERROR, LogCategories.APPLICATIONS, "Date not parsable SearchBackendCRM");
			}
		}
		return convertedDate;
	}

	/**
	 * log the size of search
	 *
	 * @param size
	 * @param maxHits
	 */
	protected void performLogging(final Integer size, final int maxHits)
	{
		if (isErrorStatus(size, maxHits))
		{
			Logger.log(Severity.ERROR, LogCategories.APPLICATIONS, "Result list size exceeds maximum number of hits");
		}
		else if (isWarningStatus(size, maxHits))
		{
			Logger.log(Severity.WARNING, LogCategories.APPLICATIONS, "Result list size exceeds 90% of the maximum number of hits");
		}
	}

	/**
	 * threshold under warning status
	 *
	 * @param size
	 * @param maxHits
	 * @return boolean
	 */
	protected boolean isWarningStatus(final Integer size, final int maxHits)
	{
		final double threshold = 0.9;
		return exceedsThreshold(size, maxHits, threshold);
	}

	protected boolean isErrorStatus(final Integer size, final int maxHits)
	{
		final double threshold = 1.0;
		return exceedsThreshold(size, maxHits, threshold);
	}

	/**
	 * Check whether threshold exceeds or not
	 *
	 * @param size
	 * @param maxHits
	 * @param threshold
	 * @return boolean
	 */
	private boolean exceedsThreshold(final Integer size, final int maxHits, final double threshold)
	{
		if (maxHits <= 0)
		{
			throw new ApplicationBaseRuntimeException("maxHits needs to be positive");
		}
		final Double max = (double) maxHits;
		return size / max > threshold;
	}

	/**
	 * @return the maxHits
	 */
	protected int getMaxHits()
	{
		Integer maxHits = validateMaxHits();
		if (maxHits == null || maxHits <= 0)
		{
			maxHits = Config.getInt(SapservicecontractbolConstants.SERVICE_CONTRACTS_MAXHITS_KEY, 200);
		}
		return maxHits.intValue();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.sapservicecontractfacades.service.backend.
	 * SapCRMServiceContractBackend#getContractsforContractId( java.lang.String)
	 */
	@Override
	public ServiceContractSearchResult getContractforContractId(final String userId, final String contractId)
	{
		Logger.entering("getContractforContractId(final String contractId)");
		List<GenilRootDataContainer> searchResult = null;
		JCoConnection connection = null;
		ServiceContractSearchResult contract = null;
		IGenilProxyStateAware genilProxy = null;
		try
		{
			// Get JCo connection
			connection = managedConnectionFactory.getManagedConnection(getDefaultConnectionName(), this.getClass().getName());
			genilProxy = (IGenilProxyStateAware) GenilProxyFactory.getProxy(connection, SapservicecontractbolConstants.BT);
			genilProxy.isInitialized();
			GenilQueryRequestContainer queryReqCont;
			queryReqCont = genilProxy.getQueryRequestContainer(SapservicecontractbolConstants.BTQ_SERVICE_CONTRACT);
			queryReqCont.addQueryParam(SapservicecontractbolConstants.OBJECT_ID, SapservicecontractbolConstants.SIGN,
					SapservicecontractbolConstants.EQ, XSSFilterUtil.filter(contractId));
			queryReqCont.addQueryParam(SapservicecontractbolConstants.SOLD_TO_PARTY_STRING, SapservicecontractbolConstants.SIGN,
					SapservicecontractbolConstants.EQ, userId);
			final String processType = getProperty(SapservicecontractbolConstants.CONFIGURATION_PROPERTY_CONTRACT_TYPE).toString();
			queryReqCont.addQueryParam(SapservicecontractbolConstants.PROCESS_TYPE, SapservicecontractbolConstants.SIGN,
					SapservicecontractbolConstants.EQ, processType);
			queryReqCont.setMaxHits(getMaxHits());
			searchResult = genilProxy.search(queryReqCont);
			if (searchResult != null && !searchResult.isEmpty())
			{
				if (searchResult.size() > 1)
				{
					Logger.debug("Invalid Result.Only one contract should be returned for a contract ID");
					throw new ApplicationBaseRuntimeException(SapservicecontractbolConstants.BACKEND_ERROR_MSG);
				}
				contract = processContractData(searchResult.get(0), genilProxy);
			}
		}
		catch (final GenilBackendException e)
		{
			Logger.debug(SapservicecontractbolConstants.GENIL_ERROR_MSG, e);
			throw new ApplicationBaseRuntimeException(SapservicecontractbolConstants.BACKEND_ERROR_MSG);
		}
		catch (final JCoException e)
		{
			Logger.debug(SapservicecontractbolConstants.JCO_ERROR_MSG, e);
			throw new ApplicationBaseRuntimeException(SapservicecontractbolConstants.BACKEND_ERROR_MSG);
		}
		finally
		{
			closeConnection(connection);
		}
		return contract;

	}

	/**
	 * Process search result
	 *
	 * @param searchResult
	 * @param genilProxy
	 * @return ServiceContractData
	 * @throws JCoException
	 */
	protected ServiceContractSearchResult processContractData(final GenilRootDataContainer searchResult,
			final IGenilProxyStateAware genilProxy) throws JCoException
	{

		ServiceContractSearchResult contract = createSearchResult();

		final GenilRootDataContainer root = searchResult;
		contract.setContractId(XSSFilterUtil.filter(root.getAttributeValue(SapservicecontractbolConstants.OBJECT_ID)));
		contract.setDescription(XSSFilterUtil.filter(root.getAttributeValue(SapservicecontractbolConstants.DESCRIPTION)));
		contract.setNetValue(XSSFilterUtil.filter(root.getAttributeValue(SapservicecontractbolConstants.NET_VALUE)));
		contract.setStartDate(convertString2date(XSSFilterUtil.filter(root
				.getAttributeValue(SapservicecontractbolConstants.DATE_START))));
		contract
				.setEndDate(convertString2date(XSSFilterUtil.filter(root.getAttributeValue(SapservicecontractbolConstants.DATE_END))));
		contract.setCurrency(XSSFilterUtil.filter(root.getAttributeValue(SapservicecontractbolConstants.CURRENCY)));
		contract.setContractConcatStatus(root.getAttributeValue(SapservicecontractbolConstants.CONCATSTAT));
		contract.setContractGuid(XSSFilterUtil.filter(root.getAttributeValue(SapservicecontractbolConstants.GUID)));
		contract = getContractItems(contract, searchResult, genilProxy);
		if (Logger.isDebugEnabled())
		{
			final StringBuilder debugOutput = new StringBuilder("ProcessResults, ");
			debugOutput.append("number of results: " + contract.getItems().size());
			Logger.debug(debugOutput);
		}
		performLogging(contract.getItems().size(), getMaxHits());
		return contract;
	}

	/**
	 * Read all the attributes of requested contract
	 *
	 * @param contract
	 * @param searchResult
	 * @param genilProxy
	 * @return ServiceContractData
	 * @throws JCoException
	 */
	protected ServiceContractSearchResult getContractItems(final ServiceContractSearchResult contract,
			final GenilRootDataContainer searchResult, final IGenilProxyStateAware genilProxy) throws JCoException
	{
		final List<ServiceContractItemResult> items = new ArrayList<ServiceContractItemResult>();
		Logger.entering("getProductValue(final String uId, final String queryParam)");
		try
		{
			// Get JCo connection
			GenilRootDataContainer root;
			root = readContractRelations(searchResult, genilProxy);
			final ServiceContractGenilContainer contractGenilContainer = getServiceContractGenilContainer(root);
			getContractDetails(contract, contractGenilContainer);
			populateItems(contract, items, contractGenilContainer.getContractItemsAllList(), contractGenilContainer);
			contract.setItems(items);
		}
		catch (final GenilBackendException e)
		{
			Logger.debug("Error in search via genil serach api", e);
			throw new ApplicationBaseRuntimeException("Back end went down, session needs to be terminated");
		}
		catch (final ParseException e)
		{
			Logger.debug("Unable to parse the quantity", e);
			throw new ApplicationBaseRuntimeException("Unable to parse the data coming from backend.");
		}
		return contract;
	}

	/**
	 *
	 */
	protected ServiceContractGenilContainer getServiceContractGenilContainer(final GenilRootDataContainer root)
	{
		final ServiceContractGenilContainer contractGenilContainer = createServiceContractGenilContainer();
		if (!CollectionUtils.isEmpty(root.getChilds(SapservicecontractbolConstants.BT_ADVS_SRV_CON)))
		{
			contractGenilContainer.setContractInfo(root.getChilds(SapservicecontractbolConstants.BT_ADVS_SRV_CON).get(0));
		}
		if (!CollectionUtils.isEmpty(contractGenilContainer.getContractInfo().getChilds(
				SapservicecontractbolConstants.BT_ORDER_HEADER)))
		{
			contractGenilContainer.setContractHeader(contractGenilContainer.getContractInfo()
					.getChilds(SapservicecontractbolConstants.BT_ORDER_HEADER).get(0));
		}
		return contractGenilContainer;
	}

	/**
	 * Set contract details
	 *
	 * @param contract
	 */
	protected void getContractDetails(final ServiceContractSearchResult contract,
			final ServiceContractGenilContainer contractGenilContainer)
	{

		setContractHeaderStatus(contract, contractGenilContainer);

		if (!CollectionUtils.isEmpty(contractGenilContainer.getContractHeader().getChilds(
				SapservicecontractbolConstants.BT_HEADER_CUMULAT_EXT)))
		{
			contractGenilContainer.setContractCumulated(contractGenilContainer.getContractHeader()
					.getChilds(SapservicecontractbolConstants.BT_HEADER_CUMULAT_EXT).get(0));
			if (!CollectionUtils.isEmpty(contractGenilContainer.getContractCumulated().getAllAttribues()))
			{
				contract.setGrossValue(XSSFilterUtil.filter(contractGenilContainer.getContractCumulated().getAttributeValue(
						SapservicecontractbolConstants.GROSS_VALUE)));
			}
		}
		if (!CollectionUtils.isEmpty(contractGenilContainer.getContractHeader().getChilds(
				SapservicecontractbolConstants.BT_HEADER_TEXT_SET)))
		{
			contractGenilContainer.setContractTextSet(contractGenilContainer.getContractHeader()
					.getChilds(SapservicecontractbolConstants.BT_HEADER_TEXT_SET).get(0));
			if (!CollectionUtils.isEmpty(contractGenilContainer.getContractTextSet().getChilds(
					SapservicecontractbolConstants.BT_TEXT_H_ALL)))
			{
				contractGenilContainer.setContractTextAll(contractGenilContainer.getContractTextSet().getChilds(
						SapservicecontractbolConstants.BT_TEXT_H_ALL));

				if (!CollectionUtils.isEmpty(contractGenilContainer.getContractTextAll()))
				{
					getContractNotes(contract, contractGenilContainer.getContractTextAll());
				}
			}
		}

		putContractItems(contractGenilContainer);

		if (!CollectionUtils.isEmpty(contractGenilContainer.getContractHeader().getChilds(
				SapservicecontractbolConstants.BT_HEADER_BILL_PLAN_SET)))
		{
			contractGenilContainer.setContractBillingPlanSet(contractGenilContainer.getContractHeader()
					.getChilds(SapservicecontractbolConstants.BT_HEADER_BILL_PLAN_SET).get(0));
			if (!CollectionUtils.isEmpty(contractGenilContainer.getContractBillingPlanSet().getChilds(
					SapservicecontractbolConstants.BT_BILL_PLAN_DETAIL)))
			{
				contractGenilContainer.setContractBillingDetail(contractGenilContainer.getContractBillingPlanSet()
						.getChilds(SapservicecontractbolConstants.BT_BILL_PLAN_DETAIL).get(0));
				if (!CollectionUtils.isEmpty(contractGenilContainer.getContractBillingDetail().getAllAttribues()))
				{
					getContractBillingPlanDetails(contract, contractGenilContainer);
				}
			}
		}

	}

	/**
	 *
	 */
	protected void putContractItems(final ServiceContractGenilContainer contractGenilContainer)
	{
		if (!CollectionUtils.isEmpty(contractGenilContainer.getContractHeader().getChilds(
				SapservicecontractbolConstants.BT_HEADER_ITEMS_EXT)))
		{
			contractGenilContainer.setContractItems(contractGenilContainer.getContractHeader()
					.getChilds(SapservicecontractbolConstants.BT_HEADER_ITEMS_EXT).get(0));
			if (!CollectionUtils.isEmpty(contractGenilContainer.getContractItems().getChilds(
					SapservicecontractbolConstants.BT_SRV_CNTRCT_ALL)))
			{
				contractGenilContainer.setContractItemsAllList(contractGenilContainer.getContractItems().getChilds(
						SapservicecontractbolConstants.BT_SRV_CNTRCT_ALL));
			}
		}
	}

	/**
	 * method to set contract header status
	 */
	private void setContractHeaderStatus(final ServiceContractSearchResult contract,
			final ServiceContractGenilContainer contractGenilContainer)
	{
		if (!CollectionUtils.isEmpty(contractGenilContainer.getContractHeader().getChilds(
				SapservicecontractbolConstants.BT_HEADER_STATUS_SET)))
		{
			contractGenilContainer.setContractHeaderStatusSet(contractGenilContainer.getContractHeader()
					.getChilds(SapservicecontractbolConstants.BT_HEADER_STATUS_SET).get(0));

			if (!CollectionUtils.isEmpty(contractGenilContainer.getContractHeaderStatusSet().getChilds(
					SapservicecontractbolConstants.BT_STATUS_H_ALL)))
			{
				final List<GenilDataContainer> allHeaderStatuses = contractGenilContainer.getContractHeaderStatusSet().getChilds(
						SapservicecontractbolConstants.BT_STATUS_H_ALL);
				checkContractStatuses(contract, allHeaderStatuses);
			}
		}
	}

	/**
	 *
	 */
	private void checkContractStatuses(final ServiceContractSearchResult contract, final List<GenilDataContainer> allHeaderStatuses)
	{
		if (!CollectionUtils.isEmpty(allHeaderStatuses))
		{
			for (final GenilDataContainer statusContainer : allHeaderStatuses)
			{
				final Collection<GenilAttribute> attributes = statusContainer.getAllAttribues();
				checkContractHeaderStatus(contract, statusContainer, attributes);
			}
		}
	}

	/**
	 *
	 */
	private void checkContractHeaderStatus(final ServiceContractSearchResult contract, final GenilDataContainer statusContainer,
			final Collection<GenilAttribute> attributes)
	{
		if (!CollectionUtils.isEmpty(attributes))
		{
			final String status = statusContainer.getAttributeValue(SapservicecontractbolConstants.STATUS);
			checkContractStatusCode(contract, statusContainer, status);
		}
	}

	/**
	 *
	 */
	private void checkContractStatusCode(final ServiceContractSearchResult contract, final GenilDataContainer statusContainer,
			final String status)
	{
		if (StringUtils.isNotEmpty(status))
		{
			if (status.startsWith(E_STATUS_REGEX))
			{
				contract.setStatusCode(status);

				final String statusProfile = statusContainer.getAttributeValue(SapservicecontractbolConstants.STATUS_PROFILE);
				if (StringUtils.isNotEmpty(statusProfile))
				{
					contract.setStatusProfile(statusProfile);
				}
			}
			else
			{
				if (SapservicecontractbolConstants.OPEN_STATUS.equalsIgnoreCase(status)
						|| SapservicecontractbolConstants.IN_PROCESS_STATUS.equalsIgnoreCase(status)
						|| SapservicecontractbolConstants.RELEASED_STATUS.equalsIgnoreCase(status)
						|| SapservicecontractbolConstants.COMPLETED_STATUS.equalsIgnoreCase(status))
				{
					contract.setContractSysStatus(status);
				}
			}
		}
	}



	/**
	 * Read and Set billing plan information
	 *
	 * @param contract
	 */
	protected void getContractBillingPlanDetails(final ServiceContractSearchResult contract,
			final ServiceContractGenilContainer contractGenilContainer)
	{
		final String settlementPeriod = contractGenilContainer.getContractBillingDetail().getAttributeValue(
				SapservicecontractbolConstants.SETTLEMENT_PERIOD);
		if (settlementPeriod != null && !settlementPeriod.isEmpty())
		{
			contract.setSettlementPeriod(settlementPeriod);
		}
		final String billingDate = contractGenilContainer.getContractBillingDetail().getAttributeValue(
				SapservicecontractbolConstants.BILLING_DATE);
		if (billingDate != null && !billingDate.isEmpty())
		{
			contract.setBillingDate(billingDate);
		}
		final String invoiceCreationDate = contractGenilContainer.getContractBillingDetail().getAttributeValue(
				SapservicecontractbolConstants.INVOICE_DATE);
		if (invoiceCreationDate != null && !invoiceCreationDate.isEmpty())
		{
			contract.setInvoiceCreationDate(invoiceCreationDate);
		}

	}

	/**
	 * Read and Set notes for contract
	 *
	 * @param contract
	 */
	protected void getContractNotes(final ServiceContractSearchResult contract, final List<GenilDataContainer> contractTextFinal)
	{
		List<String> contractNotes = null;
		final String noteType = getProperty(SapservicecontractbolConstants.CONFIGURATION_PROPERTY_CONTRACT_NOTES_TYPE).toString();
		if (noteType != null && !noteType.isEmpty())
		{
			contractNotes = new ArrayList<String>();
			for (final GenilDataContainer genilDataContainer : contractTextFinal)
			{
				final String langCode = genilDataContainer.getAttributeValue(SapservicecontractbolConstants.LANG_CODE);
				if (langCode != null && !langCode.isEmpty()
						&& getUserService().getCurrentUser().getSessionLanguage().getIsocode().equalsIgnoreCase(langCode)
						&& genilDataContainer.getAttributeValue(SapservicecontractbolConstants.NOTE_TYPE).equals(noteType))
				{
					contractNotes.add(XSSFilterUtil.filter(genilDataContainer
							.getAttributeValue(SapservicecontractbolConstants.CONC_LINES)));
				}
			}
			if (!CollectionUtils.isEmpty(contractNotes))
			{
				final List<String> listOfNotes = new ArrayList<String>();
				for (final String note : contractNotes)
				{
					listOfNotes.add(note);
				}
				contract.setNotes(listOfNotes);
			}
		}
	}

	/**
	 * Read and Populate Items into contract
	 *
	 * @param contract
	 * @param items
	 * @param contractItemsAll
	 * @throws ParseException
	 */
	protected void populateItems(final ServiceContractSearchResult contract, final List<ServiceContractItemResult> items,
			final List<GenilDataContainer> contractItemsAll, final ServiceContractGenilContainer contractGenilContainer)
			throws ParseException
	{
		String productCode = null;
		ServiceContractItemResult product = null;
		if (!CollectionUtils.isEmpty(contractItemsAll))
		{
			for (final GenilDataContainer genilDataContainer : contractItemsAll)
			{
				product = createItemResult();
				product.setServiceContractId(contract.getContractId());
				if (!CollectionUtils.isEmpty(genilDataContainer.getAllAttribues()))
				{
					product.setItemNumber(XSSFilterUtil.filter(genilDataContainer
							.getAttributeValue(SapservicecontractbolConstants.ITEM_NUMBER)));
					productCode = genilDataContainer.getAttributeValue(SapservicecontractbolConstants.ORDERED_PROD);
					product
							.setItemGuid(XSSFilterUtil.filter(genilDataContainer.getAttributeValue(SapservicecontractbolConstants.GUID)));
					product.setProductId(XSSFilterUtil.filter(productCode));
					product.setDescription(XSSFilterUtil.filter(genilDataContainer
							.getAttributeValue(SapservicecontractbolConstants.DESCRIPTION)));
				}
				populatePriceValue(contractGenilContainer, product, genilDataContainer);
				populateQuantity(contractGenilContainer, product, genilDataContainer);
				populateItemDetails(genilDataContainer, product, contractGenilContainer);

				items.add(product);
			}
		}
	}

	/**
	 *
	 */
	private void populateQuantity(final ServiceContractGenilContainer contractGenilContainer,
			final ServiceContractItemResult product, final GenilDataContainer genilDataContainer)
	{
		if (!CollectionUtils.isEmpty(genilDataContainer.getChilds(SapservicecontractbolConstants.BT_ITEM_PRODUCT_EXT)))
		{
			contractGenilContainer.setContractItemProductExt(genilDataContainer.getChilds(
					SapservicecontractbolConstants.BT_ITEM_PRODUCT_EXT).get(0));
			if (!CollectionUtils.isEmpty(contractGenilContainer.getContractItemProductExt().getAllAttribues()))
			{
				product.setProductUnit(XSSFilterUtil.filter(contractGenilContainer.getContractItemProductExt().getAttributeValue(
						SapservicecontractbolConstants.PROCESS_QTY_UNIT)));

			}
		}
	}

	/**
	 *
	 */
	private void populatePriceValue(final ServiceContractGenilContainer contractGenilContainer,
			final ServiceContractItemResult product, final GenilDataContainer genilDataContainer)
	{
		if (!CollectionUtils.isEmpty(genilDataContainer.getChilds(SapservicecontractbolConstants.BT_ITEM_PRICING_EXT)))
		{
			contractGenilContainer.setContractItemPricingExt(genilDataContainer.getChilds(
					SapservicecontractbolConstants.BT_ITEM_PRICING_EXT).get(0));
			if (!CollectionUtils.isEmpty(contractGenilContainer.getContractItemPricingExt().getAllAttribues()))
			{
				product.setNetValue(XSSFilterUtil.filter(contractGenilContainer.getContractItemPricingExt().getAttributeValue(
						SapservicecontractbolConstants.NET_VALUE)));
				product.setExpectedValue(XSSFilterUtil.filter(contractGenilContainer.getContractItemPricingExt().getAttributeValue(
						SapservicecontractbolConstants.NET_VALUE_MAN)));
				product.setTargetValue(XSSFilterUtil.filter(contractGenilContainer.getContractItemPricingExt().getAttributeValue(
						SapservicecontractbolConstants.TARGET_VALUE)));
			}
		}
	}

	/**
	 * Read and Populate Item Details
	 *
	 * @param genilDataContainer
	 * @param product
	 * @throws ParseException
	 */
	protected void populateItemDetails(final GenilDataContainer genilDataContainer, final ServiceContractItemResult product,
			final ServiceContractGenilContainer contractGenilContainer) throws ParseException
	{
		if (!CollectionUtils.isEmpty(genilDataContainer.getChilds(SapservicecontractbolConstants.BT_ITEM_SCHED_LIN_FIRST)))
		{
			contractGenilContainer.setContractItemSchedlinExt(genilDataContainer.getChilds(
					SapservicecontractbolConstants.BT_ITEM_SCHED_LIN_FIRST).get(0));
			if (!CollectionUtils.isEmpty(contractGenilContainer.getContractItemSchedlinExt().getChilds(
					SapservicecontractbolConstants.BT_SCHED_LIN_FIRST)))
			{
				contractGenilContainer.setContractItemSchedlinFirst(contractGenilContainer.getContractItemSchedlinExt()
						.getChilds(SapservicecontractbolConstants.BT_SCHED_LIN_FIRST).get(0));
				if (!CollectionUtils.isEmpty(contractGenilContainer.getContractItemSchedlinFirst().getAllAttribues()))
				{
					product.setQuantity(XSSFilterUtil.filter(contractGenilContainer.getContractItemSchedlinFirst().getAttributeValue(
							SapservicecontractbolConstants.QUANTITY)));
				}
			}
		}

		populateContractItemStatus(genilDataContainer, product, contractGenilContainer);

		populateMoreItemDetails(genilDataContainer, product, contractGenilContainer);
	}

	/**
	  *
	  */
	private void populateContractItemStatus(final GenilDataContainer genilDataContainer, final ServiceContractItemResult product,
			final ServiceContractGenilContainer contractGenilContainer)
	{
		if (!CollectionUtils.isEmpty(genilDataContainer.getChilds(SapservicecontractbolConstants.BT_ITEM_STATUS_SET)))
		{
			contractGenilContainer.setContractItemStatusSet(genilDataContainer.getChilds(
					SapservicecontractbolConstants.BT_ITEM_STATUS_SET).get(0));
			final List<GenilDataContainer> allItemStatuses = contractGenilContainer.getContractItemStatusSet().getChilds(
					SapservicecontractbolConstants.BT_STATUS_I_ALL);
			if (!CollectionUtils.isEmpty(allItemStatuses))
			{
				checkAllStatusItems(product, allItemStatuses);
			}

		}

	}

	/**
	 *
	 */
	private void checkAllStatusItems(final ServiceContractItemResult product, final List<GenilDataContainer> allItemStatuses)
	{
		for (final GenilDataContainer statusContainer : allItemStatuses)
		{
			final Collection<GenilAttribute> attributes = statusContainer.getAllAttribues();
			if (!CollectionUtils.isEmpty(attributes))
			{
				final String status = statusContainer.getAttributeValue(SapservicecontractbolConstants.STATUS);
				if (StringUtils.isNotEmpty(status))
				{
					processStatus(product, statusContainer, status);
				}
			}
		}
	}

	/**
	 *
	 */
	private void processStatus(final ServiceContractItemResult product, final GenilDataContainer statusContainer,
			final String status)
	{
		if (status.startsWith(E_STATUS_REGEX))
		{
			product.setStatusCode(status);

			final String statusProfile = statusContainer.getAttributeValue(SapservicecontractbolConstants.STATUS_PROFILE);
			if (StringUtils.isNotEmpty(statusProfile))
			{
				product.setStatusProfile(statusProfile);
			}

		}
		else
		{
			if (SapservicecontractbolConstants.OPEN_STATUS.equalsIgnoreCase(status)
					|| SapservicecontractbolConstants.IN_PROCESS_STATUS.equalsIgnoreCase(status)
					|| SapservicecontractbolConstants.RELEASED_STATUS.equalsIgnoreCase(status)
					|| SapservicecontractbolConstants.COMPLETED_STATUS.equalsIgnoreCase(status))
			{
				product.setItemSysStatus(status);
			}
		}
	}



	/**
	 * Read and Populate more details about item
	 *
	 * @param genilDataContainer
	 * @param product
	 */
	protected void populateMoreItemDetails(final GenilDataContainer genilDataContainer, final ServiceContractItemResult product,
			final ServiceContractGenilContainer contractGenilContainer)
	{
		if (CollectionUtils.isEmpty(genilDataContainer.getChilds(SapservicecontractbolConstants.BT_ITEM_CUMULAT_I_SET)))
		{
			contractGenilContainer.setContractItemCummulateSet(genilDataContainer.getChilds(
					SapservicecontractbolConstants.BT_ITEM_CUMULAT_I_SET).get(0));
			if (!CollectionUtils.isEmpty(contractGenilContainer.getContractItemCummulateSet().getAllChilds()))
			{
				setReleasedValue(product, contractGenilContainer);
				setReleasedQuantity(product, contractGenilContainer);
			}
		}
		setItemCurrency(genilDataContainer, product, contractGenilContainer);
		getResponseAndServiceProfile(product, genilDataContainer, contractGenilContainer);
		setItemValidity(genilDataContainer, product, contractGenilContainer);
	}

	/**
	 * Read and Set Currency Info for Item
	 *
	 * @param genilDataContainer
	 * @param product
	 */
	private void setItemCurrency(final GenilDataContainer genilDataContainer, final ServiceContractItemResult product,
			final ServiceContractGenilContainer contractGenilContainer)
	{
		if (!CollectionUtils.isEmpty(genilDataContainer.getChilds(SapservicecontractbolConstants.BT_ITEM_PRICING_SET)))
		{
			contractGenilContainer.setContractItemPricingSet(genilDataContainer.getChilds(
					SapservicecontractbolConstants.BT_ITEM_PRICING_SET).get(0));
			if (!CollectionUtils.isEmpty(contractGenilContainer.getContractItemPricingSet().getAllAttribues()))
			{
				product.setCurrency(XSSFilterUtil.filter(contractGenilContainer.getContractItemPricingSet().getAttributeValue(
						SapservicecontractbolConstants.CURRENCY)));
			}
		}
	}

	/**
	 * Read and Set Released Quantity of Item
	 *
	 * @param product
	 */
	private void setReleasedQuantity(final ServiceContractItemResult product,
			final ServiceContractGenilContainer contractGenilContainer)
	{
		if (!CollectionUtils.isEmpty(contractGenilContainer.getContractItemCummulateSet().getChilds(
				SapservicecontractbolConstants.BT_CUMULAT_I_CALL_OFF_QUANTITY)))
		{
			contractGenilContainer.setContractItemCummulateQuantity(contractGenilContainer.getContractItemCummulateSet()
					.getChilds(SapservicecontractbolConstants.BT_CUMULAT_I_CALL_OFF_QUANTITY).get(0));
			if (!CollectionUtils.isEmpty(contractGenilContainer.getContractItemCummulateQuantity().getAllAttribues()))
			{
				product.setReleasedQuantity(XSSFilterUtil.filter(contractGenilContainer.getContractItemCummulateQuantity()
						.getAttributeValue(SapservicecontractbolConstants.QUANTITY)));
			}
		}
	}

	/**
	 * Read and Set Released Value of item
	 *
	 * @param product
	 */
	private void setReleasedValue(final ServiceContractItemResult product,
			final ServiceContractGenilContainer contractGenilContainer)
	{
		if (!CollectionUtils.isEmpty(contractGenilContainer.getContractItemCummulateSet().getChilds(
				SapservicecontractbolConstants.BT_CUMULAT_I_CALL_OFF_VALUE)))
		{
			contractGenilContainer.setContractItemCummulateValue(contractGenilContainer.getContractItemCummulateSet()
					.getChilds(SapservicecontractbolConstants.BT_CUMULAT_I_CALL_OFF_VALUE).get(0));
			if (!CollectionUtils.isEmpty(contractGenilContainer.getContractItemCummulateValue().getAllAttribues()))
			{
				product.setReleasedValue(XSSFilterUtil.filter(contractGenilContainer.getContractItemCummulateValue()
						.getAttributeValue(SapservicecontractbolConstants.VALUE)));
			}
		}
	}

	/**
	 * Read and Set validity of product
	 *
	 * @param genilDataContainer
	 * @param product
	 */
	private void setItemValidity(final GenilDataContainer genilDataContainer, final ServiceContractItemResult product,
			final ServiceContractGenilContainer contractGenilContainer)
	{
		if (!CollectionUtils.isEmpty(genilDataContainer.getChilds(SapservicecontractbolConstants.BT_ITEM_DATES_SET)))
		{
			contractGenilContainer.setContractItemDateSet(genilDataContainer.getChilds(
					SapservicecontractbolConstants.BT_ITEM_DATES_SET).get(0));
			filterDatesfromGenilContainer(product, contractGenilContainer);
		}
	}

	/**
	 *
	 */
	private void filterDatesfromGenilContainer(final ServiceContractItemResult product,
			final ServiceContractGenilContainer contractGenilContainer)
	{
		if (!CollectionUtils.isEmpty(contractGenilContainer.getContractItemDateSet().getChilds(
				SapservicecontractbolConstants.BT_DATES_ALL)))
		{
			contractGenilContainer.setContractItemDatesAll(contractGenilContainer.getContractItemDateSet().getChilds(
					SapservicecontractbolConstants.BT_DATES_ALL));
			if (!CollectionUtils.isEmpty(contractGenilContainer.getContractItemDatesAll()))
			{
				for (final GenilDataContainer dateContainer : contractGenilContainer.getContractItemDatesAll())
				{
					getProductValidity(product, dateContainer);
				}
			}
		}
	}

	/**
	 * Read and Set Response and Service Profile for Item
	 *
	 * @param product
	 * @param genilDataContainer
	 */
	private void getResponseAndServiceProfile(final ServiceContractItemResult product,
			final GenilDataContainer genilDataContainer, final ServiceContractGenilContainer contractGenilContainer)
	{
		if (!CollectionUtils.isEmpty(genilDataContainer.getChilds(SapservicecontractbolConstants.BT_ITEM_SERVICE_EXT)))
		{
			contractGenilContainer.setContractItemServiceExt(genilDataContainer.getChilds(
					SapservicecontractbolConstants.BT_ITEM_SERVICE_EXT).get(0));
			if (!CollectionUtils.isEmpty(contractGenilContainer.getContractItemServiceExt().getAllAttribues()))
			{
				final String responseCode = contractGenilContainer.getContractItemServiceExt().getAttributeValue(
						SapservicecontractbolConstants.RESPONSE_PROFILE);
				final String serviceCode = contractGenilContainer.getContractItemServiceExt().getAttributeValue(
						SapservicecontractbolConstants.SERVICE_PROFILE);

				if (StringUtils.isNotEmpty(serviceCode))
				{
					product.setServiceProfile(serviceCode);
				}

				if (StringUtils.isNotEmpty(responseCode))
				{
					product.setResponseProfile(responseCode);
				}
			}
		}
	}

	/**
	 * Read validity of item based on Application type
	 *
	 * @param product
	 * @param dateContainer
	 */
	private void getProductValidity(final ServiceContractItemResult product, final GenilDataContainer dateContainer)
	{
		String dateType = null;
		if (!CollectionUtils.isEmpty(dateContainer.getAllAttribues()))
		{
			dateType = dateContainer.getAttributeValue(SapservicecontractbolConstants.APPT_TYPE);
			if (dateType != null && !dateType.isEmpty())
			{
				getProductValidityDate(product, dateContainer, dateType);

			}
		}
	}

	/**
	 * Read and Set validity of item.
	 *
	 * @param product
	 * @param dateContainer
	 * @param dateType
	 */
	private void getProductValidityDate(final ServiceContractItemResult product, final GenilDataContainer dateContainer,
			final String dateType)
	{
		if (SapservicecontractbolConstants.VALIDITY_START_TYPE.equals(dateType))
		{
			product.setValidFrom(getProductValidityDate(dateContainer));
			product.setItemValidFrom(convertDate(XSSFilterUtil.filter(dateContainer
					.getAttributeValue(SapservicecontractbolConstants.FROM_DATE))));
		}
		else if (SapservicecontractbolConstants.VALIDITY_END_TYPE.equals(dateType))
		{
			product.setValidTo(getProductValidityDate(dateContainer));
			product.setItemValidTo(convertDate(XSSFilterUtil.filter(dateContainer
					.getAttributeValue(SapservicecontractbolConstants.TO_DATE))));
		}
	}

	/**
	 * Get date in desired time zone and format.
	 *
	 * @param dateContainer
	 */
	private Date getProductValidityDate(final GenilDataContainer dateContainer)
	{
		Date validDate = null;
		String timeZone = null;
		timeZone = XSSFilterUtil.filter(dateContainer.getAttributeValue(SapservicecontractbolConstants.TIMEZONE));
		if (timeZone != null && !timeZone.isEmpty())
		{
			final String date = XSSFilterUtil.filter(dateContainer.getAttributeValue(SapservicecontractbolConstants.TIMESTAMP));
			if (date != null && !date.isEmpty())
			{
				validDate = convertDateInTimeZone(date);
			}
		}
		return validDate;
	}

	/**
	 * Read all the relations for required attributes
	 *
	 * @param searchResult
	 * @param genilProxy
	 * @return GenilRootDataContainer
	 * @throws GenilBackendException
	 */
	protected GenilRootDataContainer readContractRelations(final GenilRootDataContainer searchResult,
			final IGenilProxyStateAware genilProxy) throws GenilBackendException
	{
		GenilReadRootRequestContainer rootReq;
		GenilRootDataContainer root;
		GenilReadAllRequestContainer childReq;
		GenilReadAllRequestContainer childReq1;
		GenilReadAllRequestContainer childReq2;
		GenilReadAllRequestContainer childReq3;
		GenilReadAllRequestContainer childReq4;
		GenilReadAllRequestContainer childReq5;
		GenilReadAllRequestContainer childReq6;
		GenilReadAllRequestContainer childReq7;
		GenilReadAllRequestContainer childReq8;
		GenilReadAllRequestContainer childReq9;
		GenilReadAllRequestContainer childReq10;
		GenilReadAllRequestContainer childReq11;
		GenilReadAllRequestContainer childReq12;
		GenilReadAllRequestContainer childReq13;
		GenilReadAllRequestContainer childReq14;
		GenilReadAllRequestContainer childReq15;
		GenilReadAllRequestContainer childReq16;
		GenilReadAllRequestContainer childReq17;
		GenilReadAllRequestContainer childReq18;
		GenilReadAllRequestContainer childReq19;
		GenilReadAllRequestContainer childReq20;
		GenilReadAllRequestContainer childReq21;
		GenilReadAllRequestContainer childReq22;
		GenilReadAllRequestContainer childReq23;

		// Request messages
		final GenilMessageContainer msgCont = genilProxy.getGlobalMessageContainer();
		msgCont.getMessages();
		root = searchResult;
		final String contractGuid = root.getRtGuid();
		rootReq = genilProxy.getReadRequestContainer(root.getObjectName(), contractGuid);
		rootReq.setAttrRequested(true);
		final GenilMessageRequestContainer msgReqCont = genilProxy.getMessageRequestContainer();
		childReq = addChildRelation(rootReq, SapservicecontractbolConstants.BT_ADVS_SRV_CON, msgReqCont);
		childReq1 = addChildRelation(childReq, SapservicecontractbolConstants.BT_ORDER_HEADER, msgReqCont);
		childReq2 = addChildRelation(childReq1, SapservicecontractbolConstants.BT_HEADER_ITEMS_EXT, msgReqCont);
		childReq5 = addChildRelation(childReq1, SapservicecontractbolConstants.BT_HEADER_TEXT_SET, msgReqCont);
		childReq6 = addChildRelation(childReq5, SapservicecontractbolConstants.BT_TEXT_H_ALL, msgReqCont);
		childReq9 = addChildRelation(childReq1, SapservicecontractbolConstants.BT_HEADER_CUMULAT_EXT, msgReqCont);
		childReq10 = addChildRelation(childReq1, SapservicecontractbolConstants.BT_HEADER_STATUS_SET, msgReqCont);
		childReq11 = addChildRelation(childReq10, SapservicecontractbolConstants.BT_STATUS_H_ALL, msgReqCont);
		childReq22 = addChildRelation(childReq1, SapservicecontractbolConstants.BT_HEADER_BILL_PLAN_SET, msgReqCont);
		childReq3 = addChildRelation(childReq2, SapservicecontractbolConstants.BT_SRV_CNTRCT_ALL, msgReqCont);
		childReq4 = addChildRelation(childReq3, SapservicecontractbolConstants.BT_ITEM_PRICING_EXT, msgReqCont);
		childReq7 = addChildRelation(childReq3, SapservicecontractbolConstants.BT_ITEM_SERVICE_EXT, msgReqCont);
		childReq8 = addChildRelation(childReq3, SapservicecontractbolConstants.BT_ITEM_PRODUCT_EXT, msgReqCont);
		childReq12 = addChildRelation(childReq3, SapservicecontractbolConstants.BT_ITEM_STATUS_SET, msgReqCont);
		childReq13 = addChildRelation(childReq12, SapservicecontractbolConstants.BT_STATUS_I_ALL, msgReqCont);
		childReq14 = addChildRelation(childReq3, SapservicecontractbolConstants.BT_ITEM_PRICING_SET, msgReqCont);
		childReq15 = addChildRelation(childReq3, SapservicecontractbolConstants.BT_ITEM_CUMULAT_I_SET, msgReqCont);
		childReq16 = addChildRelation(childReq3, SapservicecontractbolConstants.BT_ITEM_SCHED_LIN_FIRST, msgReqCont);
		childReq17 = addChildRelation(childReq16, SapservicecontractbolConstants.BT_SCHED_LIN_FIRST, msgReqCont);
		childReq18 = addChildRelation(childReq15, SapservicecontractbolConstants.BT_CUMULAT_I_CALL_OFF_QUANTITY, msgReqCont);
		childReq19 = addChildRelation(childReq15, SapservicecontractbolConstants.BT_CUMULAT_I_CALL_OFF_VALUE, msgReqCont);
		childReq20 = addChildRelation(childReq3, SapservicecontractbolConstants.BT_ITEM_DATES_SET, msgReqCont);
		childReq21 = addChildRelation(childReq20, SapservicecontractbolConstants.BT_DATES_ALL, msgReqCont);
		childReq23 = addChildRelation(childReq22, SapservicecontractbolConstants.BT_BILL_PLAN_DETAIL, msgReqCont);


		addAttributeToRelation(childReq11, SapservicecontractbolConstants.STATUS_PROFILE);
		addAttributeToRelation(childReq11, SapservicecontractbolConstants.STATUS);
		addAttributeToRelation(childReq13, SapservicecontractbolConstants.STATUS_PROFILE);
		addAttributeToRelation(childReq13, SapservicecontractbolConstants.STATUS);




		childReq9.addRequestedAttribute(SapservicecontractbolConstants.GROSS_VALUE);

		childReq14.addRequestedAttribute(SapservicecontractbolConstants.CURRENCY);// currency
		childReq8.addRequestedAttribute(SapservicecontractbolConstants.PROCESS_QTY_UNIT);// Unit
																													// of
																													// service
																													// product
		childReq17.addRequestedAttribute(SapservicecontractbolConstants.QUANTITY); // ordered
																											// quantity
		childReq18.addRequestedAttribute(SapservicecontractbolConstants.QUANTITY); // released
																											// quantity
		childReq18.addRequestedAttribute(SapservicecontractbolConstants.QUANTITY_UNIT);// unit
																												 // of
																												 // released
																												 // quantity
		childReq3.addRequestedAttribute(SapservicecontractbolConstants.ORDERED_PROD);// ordered
																											  // service
																											  // ID

		childReq3.addRequestedAttribute(SapservicecontractbolConstants.GUID);// GUID
																									// for
																									// ITEM
		childReq3.addRequestedAttribute(SapservicecontractbolConstants.DESCRIPTION);// description
																											 // of
																											 // service
																											 // item
		childReq3.addRequestedAttribute(SapservicecontractbolConstants.ITEM_NUMBER);// item
																											 // number
		childReq7.addRequestedAttribute(SapservicecontractbolConstants.RESPONSE_PROFILE); // response
																													 // profile
																													 // code
		childReq7.addRequestedAttribute(SapservicecontractbolConstants.SERVICE_PROFILE); // service
																													// profile
																													// code
		childReq6.addRequestedAttribute(SapservicecontractbolConstants.NOTE_TYPE); // code
																											// for
																											// note
																											// types
		childReq6.addRequestedAttribute(SapservicecontractbolConstants.LANG_CODE);// language
																										  // code
		childReq6.addRequestedAttribute(SapservicecontractbolConstants.CONC_LINES);// notes
		childReq4.addRequestedAttribute(SapservicecontractbolConstants.NET_VALUE); // net
																											// value
		childReq19.addRequestedAttribute(SapservicecontractbolConstants.CURRENCY); // currency
																											// with
																											// released
																											// value
		childReq19.addRequestedAttribute(SapservicecontractbolConstants.VALUE); // released
																										// value
		childReq4.addRequestedAttribute(SapservicecontractbolConstants.NET_VALUE_MAN); // expected
																												 // value
		childReq4.addRequestedAttribute(SapservicecontractbolConstants.GROSS_VALUE); // gross
																											  // value
		childReq4.addRequestedAttribute(SapservicecontractbolConstants.TARGET_VALUE); // target
																												// value
		childReq21.addRequestedAttribute(SapservicecontractbolConstants.APPT_TYPE);// date
																											// type
		childReq21.addRequestedAttribute(SapservicecontractbolConstants.TIMESTAMP); // time
																											 // stamp
		childReq21.addRequestedAttribute(SapservicecontractbolConstants.TIMEZONE); // timezone
		childReq23.addRequestedAttribute(SapservicecontractbolConstants.SETTLEMENT_PERIOD); // settlement
																														// period
		childReq23.addRequestedAttribute(SapservicecontractbolConstants.BILLING_DATE);// billing
																												// date
		childReq23.addRequestedAttribute(SapservicecontractbolConstants.INVOICE_DATE);// Invoice
		childReq21.addRequestedAttribute(SapservicecontractbolConstants.FROM_DATE); // creation
		childReq21.addRequestedAttribute(SapservicecontractbolConstants.TO_DATE); // date
		root = genilProxy.read(rootReq, msgReqCont);
		return root;
	}

	/**
	 * to add a relation in parent genil_data_container
	 *
	 * @param parent
	 * @param relationName
	 * @return GenilReadAllRequestContainer
	 */
	@SuppressWarnings("finally")
	private GenilReadAllRequestContainer addChildRelation(final GenilReadAllRequestContainer parent, final String relationName,
			final GenilMessageRequestContainer msgReqCont)
	{
		GenilReadAllRequestContainer childReq = null;
		try
		{
			childReq = parent.addChildRel(relationName);
			msgReqCont.setMsgType(GenilMessageType.ALL);
			msgReqCont.setMsgLevel(GenilConst.MESSAGE_LEVEL_TRACE);
			msgReqCont.setGlobalMessages(true);
			msgReqCont.setObjectMessages(true);
		}
		catch (final GenilBackendRuntimeException e)
		{
			Logger.debug("No relation with name: " + relationName + "found in GenilContainer " + parent + " skipping this......."
					+ e);
		}

		return childReq;

	}

	/**
	 * API to parse notes on new line character
	 *
	 * @param finalNotes
	 */
	private List<String> parseContractNotes(final String finalNotes)
	{
		final List<String> contractNotesList = new ArrayList<String>();
		if (finalNotes != null && !finalNotes.isEmpty())
		{
			final String[] notes = finalNotes.split(SapservicecontractbolConstants.NEWLINE_REGEX);
			for (final String s : notes)
			{
				contractNotesList.add(s);
			}
		}
		return contractNotesList;
	}

	@Override
	public boolean isBackendDown()
	{
		try
		{
			return getDefaultJCoConnection().isBackendOffline();
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Cannot determine backend availability", e);

		}
	}

	/**
	 * To create ServivecOntractSerachResultList
	 */
	protected SearchResultList createSearchResultList()
	{
		return (SearchResultList) genericFactory.getBean(SapservicecontractbolConstants.SERVICE_CONTRACT_SEARCH_RESULT_LIST_BEAN);
	}

	/**
	 * To create ServivecOntractSearchResult
	 */
	protected ServiceContractSearchResult createSearchResult()
	{
		return (ServiceContractSearchResult) genericFactory
				.getBean(SapservicecontractbolConstants.SERVICE_CONTRACT_SEARCH_RESULT_BEAN);
	}

	/**
	 * To create ServivecOntractItemResult
	 */
	protected ServiceContractItemResult createItemResult()
	{
		return (ServiceContractItemResult) genericFactory.getBean(SapservicecontractbolConstants.SERVICE_CONTRACT_ITEM_RESULT_BEAN);
	}

	/**
	 * To create ServivecOntractSerachResultList
	 */
	protected ServiceContractGenilContainer createServiceContractGenilContainer()
	{
		return (ServiceContractGenilContainer) genericFactory
				.getBean(SapservicecontractbolConstants.SERVICE_CONTRACT_GENIL_CONTAINER_BEAN);
	}

	@Override
	public List<SortData> getSearchSort()
	{
		final SapServiceContractBackendBusinessObject search = getSearch();
		return search.getSortOptions();
	}

	/**
	 * Get order search business object
	 *
	 * @return Order search BO implementation
	 */
	protected SapServiceContractBackendBusinessObject getSearch()
	{
		return genericFactory.getBean(SapservicecontractbolConstants.SERVICE_CONTRACT_CRM_BACKEND_BEAN);
	}

	/**
	 * API to convert date into a given time zone
	 *
	 * @param dateString
	 * @return Date
	 */
	private Date convertDateInTimeZone(final String dateString)
	{
		final SimpleDateFormat sdf = new SimpleDateFormat(SapservicecontractbolConstants.CRM_DATE_TIME_FORMAT);
		Date convertedDate = null;
		final TimeZone zone = getI18nService().getCurrentTimeZone();
		try
		{
			sdf.setTimeZone(zone);
			convertedDate = sdf.parse(dateString);
		}
		catch (final ParseException e)
		{
			Logger.log(Severity.ERROR, LogCategories.APPLICATIONS, "Error in parsing the date in given timezone");
		}
		return convertedDate;
	}

	private Date convertDate(final String dateString)
	{
		final SimpleDateFormat sdf = new SimpleDateFormat(SapservicecontractbolConstants.CRM_DATE_FORMAT);
		Date convertedDate = null;
		try
		{
			convertedDate = sdf.parse(dateString);
		}
		catch (final ParseException e)
		{
			Logger.log(Severity.ERROR, LogCategories.APPLICATIONS, "Error in parsing the date in given timezone");
		}
		return convertedDate;
	}

	/**
	 * Get Value for the constant
	 *
	 * @param name
	 * @return String value
	 */
	protected Object getProperty(final String name)
	{
		return getModuleConfigurationAccess().getProperty(name);
	}

	@Override
	public String renewContract(final String guid) throws ApplicationBaseRuntimeException
	{
		JCoFunction function = null;
		String quotationID = null;
		JCoConnection connection = null;
		try
		{
			connection = managedConnectionFactory.getManagedConnection(getDefaultConnectionName(), this.getClass().getName());
			function = connection.getFunction(getJcoFuntionRenew());
			final JCoParameterList importParameters = function.getImportParameterList();
			importParameters.setValue(SapservicecontractbolConstants.CONTRACT_GUID, XSSFilterUtil.filter(guid));
			connection.execute(function);
			final JCoParameterList exportParameters = function.getExportParameterList();
			exportParameters.getString(SapservicecontractbolConstants.QUOTATION_GUID);
			quotationID = exportParameters.getString(SapservicecontractbolConstants.QUOTATION_ID);
		}
		catch (final BackendException ex)
		{
			Logger.debug("Error in contract renewal.", ex);
			throw new ApplicationBaseRuntimeException(SapservicecontractbolConstants.BACKEND_ERROR_MSG);
		}
		catch (final Exception ex)
		{
			Logger.debug("Type conflict in method call.Error in contract renewal.", ex);
			throw new ApplicationBaseRuntimeException(SapservicecontractbolConstants.BACKEND_ERROR_MSG);
		}
		finally
		{
			closeConnection(connection);
		}
		return quotationID;
	}

	@Override
	public String terminateContractItem(final String contractGuid, final String itemGuid)
	{
		JCoConnection connection = null;
		String status = null;
		try
		{
			Logger.debug("***** Triggrring JCO call to backend system for termination of Contract with Contract guid: "
					+ contractGuid);
			connection = managedConnectionFactory.getManagedConnection(getDefaultConnectionName(), this.getClass().getName());
			final JCoFunction function = connection.getFunction(this.jcoFunctionTerminate);
			final JCoParameterList importParameters = function.getImportParameterList();
			importParameters.setValue(SapservicecontractbolConstants.CONTRACT_GUID, XSSFilterUtil.filter(contractGuid));
			importParameters.setValue(SapservicecontractbolConstants.ITEM_GUID, XSSFilterUtil.filter(itemGuid));
			connection.execute(function);
			final JCoParameterList tableList = function.getExportParameterList();
			status = tableList.getString(SapservicecontractbolConstants.STATUS_FLAG);
		}
		catch (final BackendException e)
		{
			Logger.log(Severity.ERROR, LogCategories.APPLICATIONS, "Error in terminating the contract");
			throw new ApplicationBaseRuntimeException("Could not complete the termination request.", e);
		}
		catch (final Exception ex)
		{
			Logger.debug("Type conflict in method call.Could not complete the termination request. ", ex);
			throw new ApplicationBaseRuntimeException(SapservicecontractbolConstants.BACKEND_ERROR_MSG);
		}
		finally
		{
			closeConnection(connection);
		}
		if (StringUtils.isEmpty(status))
		{
			status = "Error in Termination of contract.";
		}
		return status;
	}

	/**
	 * @return the jcoFuntionRenew
	 */
	public String getJcoFuntionRenew()
	{
		return jcoFuntionRenew;
	}

	/**
	 * @param jcoFuntionRenew
	 *           the jcoFuntionRenew to set
	 */
	public void setJcoFuntionRenew(final String jcoFuntionRenew)
	{
		this.jcoFuntionRenew = jcoFuntionRenew;
	}

	@Override
	public String renewItem(final String contractGuid, final String itemGuid)
	{
		JCoFunction function = null;
		String status = null;
		JCoConnection connection = null;
		try
		{
			connection = managedConnectionFactory.getManagedConnection(getDefaultConnectionName(), this.getClass().getName());
			function = connection.getFunction(getJcoFuntionItemRenew());
			final JCoParameterList importParameters = function.getImportParameterList();
			importParameters.setValue(SapservicecontractbolConstants.CONTRACT_GUID, XSSFilterUtil.filter(contractGuid));
			importParameters.setValue(SapservicecontractbolConstants.ITEM_GUID, XSSFilterUtil.filter(itemGuid));
			connection.execute(function);
			final JCoParameterList tableList = function.getExportParameterList();
			status = tableList.getString(SapservicecontractbolConstants.STATUS_FLAG);
		}
		catch (final BackendException ex)
		{
			Logger.debug("Error in contract item renewal ", ex);
			throw new ApplicationBaseRuntimeException(SapservicecontractbolConstants.BACKEND_ERROR_MSG);
		}
		catch (final Exception ex)
		{
			Logger.debug("Type conflict in method call.Error in contract item renewal ", ex);
			throw new ApplicationBaseRuntimeException(SapservicecontractbolConstants.BACKEND_ERROR_MSG);
		}
		finally
		{
			closeConnection(connection);
		}
		return status;
	}

	@Override
	public SearchResultList getContractsForIBase(final String uid, final String iBaseID)
	{
		Logger.entering("getContractsForIBase(final String uId, final String iBaseID)");
		List<GenilRootDataContainer> searchResult = null;
		JCoConnection connection = null;
		IGenilProxyStateAware genilProxy = null;
		SearchResultList resultList = null;
		try
		{
			// Get JCo connection
			connection = managedConnectionFactory.getManagedConnection(getDefaultConnectionName(), this.getClass().getName());
			genilProxy = (IGenilProxyStateAware) GenilProxyFactory.getProxy(connection, SapservicecontractbolConstants.BT);
			genilProxy.isInitialized();
			GenilQueryRequestContainer queryReqCont;
			queryReqCont = genilProxy.getQueryRequestContainer(SapservicecontractbolConstants.BTQ_SERVICE_CONTRACT_ITEM);
			queryReqCont.addQueryParam(SapservicecontractbolConstants.BU_PARTNER, SapservicecontractbolConstants.SIGN,
					SapservicecontractbolConstants.EQ, uid);
			queryReqCont.addQueryParam(SapservicecontractbolConstants.IBASE_HEADER, SapservicecontractbolConstants.SIGN,
					SapservicecontractbolConstants.EQ, iBaseID);
			final String processType = getProperty(SapservicecontractbolConstants.CONFIGURATION_PROPERTY_CONTRACT_TYPE).toString();
			queryReqCont.addQueryParam(SapservicecontractbolConstants.PROCESS_TYPE, SapservicecontractbolConstants.SIGN,
					SapservicecontractbolConstants.EQ, processType);
			queryReqCont.setMaxHits(getMaxHits());
			searchResult = genilProxy.search(queryReqCont);
			if (searchResult != null && !searchResult.isEmpty())
			{
				resultList = processResultForIBase(searchResult);
			}
		}
		catch (final GenilBackendException e)
		{
			Logger.debug(SapservicecontractbolConstants.GENIL_ERROR_MSG, e);
			throw new ApplicationBaseRuntimeException(SapservicecontractbolConstants.BACKEND_ERROR_MSG);
		}
		catch (final JCoException e)
		{
			Logger.debug(SapservicecontractbolConstants.JCO_ERROR_MSG, e);
			throw new ApplicationBaseRuntimeException(SapservicecontractbolConstants.BACKEND_ERROR_MSG);
		}
		finally
		{
			closeConnection(connection);
		}
		return resultList;
	}

	/**
	 * Method to populate servicecontract with result.
	 *
	 * @param searchResult
	 */
	protected SearchResultList processResultForIBase(final List<GenilRootDataContainer> searchResult)
	{
		final SearchResultList resultList = createSearchResultList();
		for (int i = 0; i < searchResult.size(); i++)
		{
			final GenilRootDataContainer root = searchResult.get(i);
			final ServiceContractSearchResult contractData = createSearchResult();
			contractData.setContractId(XSSFilterUtil.filter(root.getAttributeValue(SapservicecontractbolConstants.OBJECT_ID)));
			contractData.setDescription(XSSFilterUtil.filter(root.getAttributeValue(SapservicecontractbolConstants.DESCRIPTION)));
			contractData.setNetValue(XSSFilterUtil.filter(root.getAttributeValue(SapservicecontractbolConstants.NET_VALUE)));
			contractData.setStartDate(convertString2date(XSSFilterUtil.filter(root
					.getAttributeValue(SapservicecontractbolConstants.DATE_START))));
			contractData.setEndDate(convertString2date(XSSFilterUtil.filter(root
					.getAttributeValue(SapservicecontractbolConstants.DATE_END))));
			contractData.setCurrency(XSSFilterUtil.filter(root.getAttributeValue(SapservicecontractbolConstants.CURRENCY)));
			contractData.setContractConcatStatus(XSSFilterUtil.filter(root
					.getAttributeValue(SapservicecontractbolConstants.CONCATSTAT)));
			contractData.setContractGuid(XSSFilterUtil.filter(root.getAttributeValue(SapservicecontractbolConstants.GUID)));
			resultList.add(contractData);
		}
		if (Logger.isDebugEnabled())
		{
			final StringBuilder debugOutput = new StringBuilder("ProcessResults, ");
			debugOutput.append("number of results: " + resultList.size());
			Logger.debug(debugOutput);
		}
		performLogging(resultList.size(), getMaxHits());
		return resultList;
	}

}
