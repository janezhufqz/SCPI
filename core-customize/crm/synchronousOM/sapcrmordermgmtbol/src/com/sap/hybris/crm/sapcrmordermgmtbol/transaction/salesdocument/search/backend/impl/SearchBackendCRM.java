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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.search.backend.impl;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import com.sap.conn.jco.JCoException;
import com.sap.genil.GenilMessageContainer;
import com.sap.genil.GenilProxyFactory;
import com.sap.genil.GenilQueryRequestContainer;
import com.sap.genil.GenilRootDataContainer;
import com.sap.genil.IGenilProxy;
import com.sap.genil.exception.GenilBackendException;
import com.sap.hybris.crm.sapcrmordermgmtbol.constants.SapcrmordermgmtbolConstants;
import com.sap.hybris.crm.sapcrmordermgmtbol.util.CacheManager;
import com.sap.tc.logging.Severity;

import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.backend.jco.BackendBusinessObjectBaseJCo;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoStateful;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.backend.interf.SearchBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchFilter;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResultList;
import de.hybris.platform.sap.sapmodel.constants.SapmodelConstants;

/**
*
*/
@BackendType("CRM")
public class SearchBackendCRM extends BackendBusinessObjectBaseJCo implements
		SearchBackend {
	/**
     *
     */
	private static final String BTQ_SLS_ORD = "BTQSlsOrd";
	/**
     *
     */
	private static final String BT = "BT";
	/**
     *
     */
	private static final char SIGN = 'I';
	/**
     *
     */
	private static final String EQ = "EQ";

	private Integer maxHits;
	private Integer dateRangeInDays;
	private static final String CRM_DATE_FORMAT = "yyyyMMddHHmmss";// 20151207113828
	private static final String STATUS_INPROCESS = "In Process";
	private static final String STATUS_RELEASED = "Released";
	private static final String STATUS_COMPLETED = "Completed";
	private static final Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(SearchBackendCRM.class.getName());

	private ModuleConfigurationAccess moduleConfigurationAccess;

	private CacheManager cacheManager;

	/**
	 * @return the cacheManager
	 */
	public CacheManager getCacheManager() {
		return cacheManager;
	}

	/**
	 * @param cacheManager
	 *            the cacheManager to set
	 */
	public void setCacheManager(final CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public ModuleConfigurationAccess getModuleConfigurationAccess() {
		return moduleConfigurationAccess;
	}

	@Override
	@Required
	public void setModuleConfigurationAccess(
			final ModuleConfigurationAccess moduleConfigurationAccess) {
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

	@Override
	public int getDateRangeInDays() {
		if (dateRangeInDays == null) {
			dateRangeInDays = Integer
					.valueOf((String) moduleConfigurationAccess
							.getProperty(SapordermgmtbolConstants.CONFIGURATION_PROPERTY_SEARCH_DATE_RANGE));
		}
		return dateRangeInDays.intValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search
	 * .backend.interf.SearchBackend#getSearchResult ()
	 */
	@Override
	public SearchResultList getSearchResult(final SearchFilter searchFilter)
			throws BackendException {
		SearchResultList resultList = null;
		IGenilProxy genilproxy = null;
		GenilQueryRequestContainer queryReqCont = null;
		List<GenilRootDataContainer> searchResult;
		sapLogger
				.getLogger()
				.info("**** Enter in the Genil Order History getSearchResult method *******");
		try {
			genilproxy = getGENILProxy();
			queryReqCont = setQueryParams(genilproxy, searchFilter);
			resultList = createSearchResultList();

			searchResult = genilproxy.search(queryReqCont);

			if (searchResult == null || searchResult.isEmpty()) {
				return resultList;
			} else {
				resultList = processResults(searchResult);
			}
			final GenilMessageContainer msgCont = genilproxy
					.getGlobalMessageContainer();
			msgCont.getMessages();
		} catch (final GenilBackendException e) {
			sapLogger.getLogger().error(e);
		} catch (final JCoException e) {
			sapLogger.getLogger().error(e);
		}
		return resultList;
	}

	/**
	 * @param genilproxy
	 * @param queryReqCont
	 * @param searchFilter
	 * @throws GenilBackendException
	 */
	protected GenilQueryRequestContainer setQueryParams(
			final IGenilProxy genilproxy, final SearchFilter searchFilter)
			throws GenilBackendException {
		final GenilQueryRequestContainer queryReqCont = genilproxy
				.getQueryRequestContainer(BTQ_SLS_ORD);
		queryReqCont.addQueryParam("SOLD_TO_PARTY", SIGN, EQ,
				searchFilter.getSoldToId(), "");
		queryReqCont
				.addQueryParam(
						"PROCESS_TYPE",
						SIGN,
						EQ,
						getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_TRANSACTION_TYPE),
						"");
		// default is 100
		queryReqCont
				.setMaxHits(Integer
						.parseInt(getProperty(SapcrmordermgmtbolConstants.CONFIGURATION_PROPERTY_MAX_HIT)));

		return queryReqCont;
	}

	/**
	 * @param genilproxy
	 * @throws JCoException
	 * @throws GenilBackendException
	 */
	protected IGenilProxy getGENILProxy() throws GenilBackendException,
			JCoException {
		final JCoConnection connection = getDefaultJCoConnection();
		final IGenilProxy genilproxy = GenilProxyFactory.getProxy(connection,
				BT);
		genilproxy.isInitialized();
		return genilproxy;
	}

	protected void closeConnection(final JCoConnection connection)
			throws BackendException {
		if (connection != null) {
			((JCoStateful) connection).destroy();
		}
	}

	/**
	 * @param name
	 * @return
	 */
	protected String getProperty(final String name) {
		final Object propertyValue = getModuleConfigurationAccess()
				.getProperty(name);
		return (String) propertyValue;
	}

	/**
	 * Processes search result and converts it into the BOL search result
	 * representation
	 *
	 * @param genilSearchResult
	 *            JCO table of type ISALES_DOCLIST_ROW
	 * @return Search Result List
	 */

	protected SearchResultList processResults(
			final List<GenilRootDataContainer> genilSearchResult) {
		sapLogger.log(Severity.INFO, LogCategories.APPLICATIONS,
				"Trasforming all orders for single process type ");
		final SearchResultList resultList = createSearchResultList();
		final Map<String, String> orderGuidMap = new HashMap<>();
		String orderId;

		for (int i = 0; i < genilSearchResult.size(); i++) {
			final GenilRootDataContainer root1 = genilSearchResult.get(i);
			final SearchResult searchResultObj = createSearchResult();
			orderId = String.valueOf(new BigInteger(root1
					.getAttributeValue("OBJECT_ID")));
			orderGuidMap.put(orderId, root1.getAttributeValue("GUID"));
			searchResultObj
					.setKey(new TechKey(root1.getAttributeValue("GUID")));
			searchResultObj.setOverallStatus("CREATED");
			// Order Status Changes
			final String currentOrderStatus = root1
					.getAttributeValue("CONCATSTATUSER");
			if (!StringUtils.isEmpty(currentOrderStatus)
					&& (currentOrderStatus.contains(STATUS_INPROCESS) || currentOrderStatus.contains(STATUS_RELEASED)))
			{
				searchResultObj.setShippingStatus("B");
			}
			else if (!StringUtils.isEmpty(currentOrderStatus) && (currentOrderStatus.contains(STATUS_COMPLETED)))
			{
				searchResultObj.setShippingStatus("C");
			}
			else
			{
				// If order is in open status and cancelled
				searchResultObj.setShippingStatus("A");
			}
			
			searchResultObj.setPurchaseOrderNumber(root1
					.getAttributeValue("PO_NUMBER_SOLD"));
			final Date date = convertString2date(root1
					.getAttributeValue("CREATED_AT"));
			if (null != date) {
				searchResultObj.setCreationDate(date);
				resultList.add(searchResultObj);
			}
		}
		cacheManager.addToCache("order_guids", orderGuidMap);
		if (sapLogger.isDebugEnabled()) {
			final StringBuilder debugOutput = new StringBuilder(
					"ProcessResults, ");
			debugOutput.append("number of results: " + resultList.size());
			sapLogger.debug(debugOutput);
		}
		performLogging(resultList, getMaxHits());
		return resultList;
	}

	protected Date convertString2date(final String date) {
		final SimpleDateFormat sdf = new SimpleDateFormat(CRM_DATE_FORMAT);
		Date convertedDate = null;
		if (null != date) {
			try {
				convertedDate = sdf.parse(date);
			} catch (final ParseException e) {
				sapLogger.log(Severity.ERROR, LogCategories.APPLICATIONS,
						"Date not parsable SearchBackendCRM");
			}
		}
		return convertedDate;
	}

	/**
	 * @param resultList
	 * @param maxHits
	 */
	protected void performLogging(final SearchResultList resultList,
			final int maxHits) {
		if (isErrorStatus(resultList, maxHits)) {
			sapLogger.log(Severity.ERROR, LogCategories.APPLICATIONS,
					"Result list size exceeds maximum number of hits");
		} else if (isWarningStatus(resultList, maxHits)) {
			sapLogger
					.log(Severity.WARNING, LogCategories.APPLICATIONS,
							"Result list size exceeds 90% of the maximum number of hits");
		}
	}

	protected SearchResultList createSearchResultList() {
		return (SearchResultList) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_SEARCH_RESULT_LIST);
	}

	/**
	 * Creates a SearchResult instance
	 *
	 * @return SearchResult instance
	 */
	protected SearchResult createSearchResult() {
		return (SearchResult) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_SEARCH_RESULT_ENTRY);
	}

	/**
	 * @return the maxHits
	 */
	protected int getMaxHits() {
		if (maxHits == null) {
			maxHits = Integer
					.valueOf((String) moduleConfigurationAccess
							.getProperty(SapordermgmtbolConstants.CONFIGURATION_PROPERTY_SEARCH_MAX_HITS));
		}
		return maxHits.intValue();
	}

	/**
	 * This can be used for testing
	 *
	 * @param maxHits
	 *            the maxHits to set
	 */
	protected void setMaxHits(final int maxHits) {
		this.maxHits = Integer.valueOf(maxHits);
	}

	protected void setDateRangeInDays(final int dateRange) {
		this.dateRangeInDays = Integer.valueOf(dateRange);

	}

	protected boolean isWarningStatus(final SearchResultList results,
			final int maxHits) {
		final double threshold = 0.9;
		return exceedsThreshold(results, maxHits, threshold);
	}

	protected boolean isErrorStatus(final SearchResultList results,
			final int maxHits) {
		final double threshold = 1.0;
		return exceedsThreshold(results, maxHits, threshold);
	}

	private boolean exceedsThreshold(final SearchResultList results,
			final int maxHits, final double threshold) {
		if (maxHits <= 0) {
			throw new ApplicationBaseRuntimeException(
					"maxHits needs to be positive");
		}
		final double sizeOfList = results.size();
		final double max = maxHits;
		return (sizeOfList / max) > threshold;
	}
}
