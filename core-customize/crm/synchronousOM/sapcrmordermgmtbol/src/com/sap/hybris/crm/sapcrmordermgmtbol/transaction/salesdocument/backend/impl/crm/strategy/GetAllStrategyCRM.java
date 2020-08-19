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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm.strategy;

import de.hybris.platform.sap.core.bol.backend.jco.JCoHelper;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionTools;
import de.hybris.platform.sap.sapmodel.constants.SapmodelConstants;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.EStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.OverallStatusOrder;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShippingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult.Result;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.genil.GenilAttribute;
import com.sap.genil.GenilMessageContainer;
import com.sap.genil.GenilProxyFactory;
import com.sap.genil.GenilQueryRequestContainer;
import com.sap.genil.GenilRootDataContainer;
import com.sap.genil.IGenilProxy;
import com.sap.genil.exception.GenilBackendException;
import com.sap.hybris.crm.sapcrmordermgmtbol.constants.SapcrmordermgmtbolConstants;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.BackendState;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.CRMConstants;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.ItemBuffer;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.strategy.GetAllStrategy;
import com.sap.hybris.crm.sapcrmordermgmtbol.util.CacheManager;

/**
 * Strategy for function module CRM_LORD_GET_ALL.
 */
public class GetAllStrategyCRM extends BaseStrategyCRM implements
		GetAllStrategy, CRMConstants {

	protected static final String BTQ_SLS_ORD = "BTQSlsOrd";
	protected static final String BT = "BT";
	protected static final char SIGN = 'I';
	protected static final String EQ = "EQ";
	@Resource
	protected CommonI18NService commonI18NService;
	protected ModuleConfigurationAccess moduleConfigurationAccess;
	static List<GenilRootDataContainer> searchResult;
	protected TransactionConfiguration shop;
	protected HeaderMapper headMapper;
	protected ItemMapper itemMapper;
	protected PartnerMapper partnerMapper;

	protected CacheManager cacheManager;

	/**
	 * reference to SAP logging API
	 */
	public static final Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(GetAllStrategyCRM.class.getName());

	/**
	 * Empty string
	 */
	protected static final String EMPTY_STRING = "";

	/**
	 * Attribute for LO-API: No conversion intended (conversion exits not called
	 * in SAP back end)
	 */
	protected static final String FIELD_NO_CONVERSION = "NO_CONVERSION";

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

	protected void determineStatus(final Header head, final JCoTable etStatus) {
		if (etStatus != null) {
			for (int i = 0; i < etStatus.getNumRows(); i++) {
				etStatus.setRow(i);
				final String partnerFunction = etStatus.getString("REF_KIND");
				updateHeaderStatus(partnerFunction, etStatus, head);

			}
		}
	}

	/**
	 * @param partnerFunction
	 * @param etStatus
	 * @param head
	 */
	protected void updateHeaderStatus(final String partnerFunction,
			final JCoTable etStatus, final Header head) {

		if ("A".equals(partnerFunction)) {
			final String procStatus = JCoHelper.getString(etStatus,
					"OVERALL_STATUS").length() == 0 ? " " : JCoHelper
					.getString(etStatus, "OVERALL_STATUS");

			final String shipingStatus = JCoHelper.getString(etStatus,
					"SHIPPING_STATUS").length() == 0 ? " " : JCoHelper
					.getString(etStatus, "SHIPPING_STATUS");

			final String billingStatus = JCoHelper.getString(etStatus,
					"BILLING_STATUS").length() == 0 ? " " : JCoHelper
					.getString(etStatus, "BILLING_STATUS");

			final String rjHeaderStatus = JCoHelper.getString(etStatus,
					"RELEASE_STATUS").length() == 0 ? " " : JCoHelper
					.getString(etStatus, "RELEASE_STATUS");

			final char rjIStatus = rjHeaderStatus != null ? rjHeaderStatus
					.charAt(0) : '\0';
			final char prcStatus = procStatus != null ? procStatus.charAt(0)
					: '\0';

			final ShippingStatus shipStatus = getShippingStatusBean();

			shipStatus.init(EStatus
					.getStatusType(shipingStatus != null ? shipingStatus
							.charAt(0) : '\0'));

			head.setShippingStatus(shipStatus);

			final BillingStatus billStatus = getBillingStatusBean();
			billStatus.init(EStatus
					.getStatusType(billingStatus != null ? billingStatus
							.charAt(0) : '\0'));

			final OverallStatusOrder overallStatus = getOverallStatusBean();
			overallStatus.init(EStatus.getStatusType(prcStatus), shipStatus,
					billStatus, EStatus.getStatusType(rjIStatus));

			head.setOverallStatus(overallStatus);

		}

	}

	/**
	 * Strategy for CRM_LORD_GET_ALL. Reads all relevant data for header and
	 * item.
	 *
	 * @param cn
	 *            JCO connection to use
	 * @return Object containing messages of call and (if present) the return
	 *         code generated by the function module.
	 */

	@Override
	public BackendCallResult execute(final BackendState backendState,
			final SalesDocument salesDocument, final ItemBuffer itemBuffer,
			final JCoConnection cn) throws BackendException {
		final String methodName = "execute()";
		sapLogger.entering(methodName);

		initializeMapper();

		final BackendCallResult retVal = new BackendCallResult();

		final JCoFunction function = cn
				.getFunction(getOrderDetailFunctionModule());

		final String orderGuid = getOrderGUID(salesDocument.getHeader()
				.getTechKey().toString(), cn);

		setImportParams(function, orderGuid);

		setTableParams(function);

		executeRfc(cn, function);
		final JCoParameterList tableParams = function.getTableParameterList();
		final JCoTable etMessages = tableParams
				.getTable(getErrorMessagesTable());
		final JCoStructure esHeader = function.getExportParameterList()
				.getStructure("ES_HEADER");
		final boolean isError = checkError(etMessages, salesDocument, esHeader);
		if (isError) {
			return new BackendCallResult(Result.failure);
		}

		processResult(function, salesDocument, backendState, orderGuid);

		sapLogger.exiting();
		return retVal;
	}

	/**
	 * @param function
	 * @param orderGuid
	 */
	protected void setImportParams(final JCoFunction function,
			final String orderGuid) {
		final JCoParameterList importParams = function.getImportParameterList();
		importParams.setValue("IV_GUID", orderGuid.trim());
		final JCoStructure headControl = importParams
				.getStructure("IS_CONTROL");
		headControl.setValue("LANGUAGE", commonI18NService.getCurrentLanguage()
				.getIsocode().toString());
		headControl.setValue("MODE", CRMConstants.ORDER_READ_MODE);
		importParams.setValue("IS_CONTROL", headControl);
	}

	/**
	 * @param function
	 * @param salesDocument
	 * @param backendState
	 * @param readParams
	 * @param orderGuid
	 */
	protected void processResult(final JCoFunction function,
			final SalesDocument salesDocument, final BackendState backendState,
			final String orderGuid) {
		final JCoParameterList exportParams = function.getExportParameterList();
		final JCoStructure esHeader = exportParams.getStructure("ES_HEADER");
		final JCoParameterList tableParams = function.getTableParameterList();
		final JCoTable etItem = tableParams.getTable("ET_ITEM");
		final JCoTable etScheduleLines = tableParams
				.getTable("ET_SCHEDULELINES");
		final JCoTable esStatus = tableParams.getTable("ET_STATUS_SYST");
		final JCoTable etSuccessor = tableParams.getTable("ET_SUCCESSOR");

		final Header header = salesDocument.getHeader();

		for (int i = 0; i < etSuccessor.getNumRows(); i++) {
			etSuccessor.setRow(i);
			final ConnectedDocument conDoc = header.createConnectedDocument();
			if (StringUtils.isNotBlank(etSuccessor.getString("OBJECT_ID"))
					&& StringUtils
							.isNotBlank(etSuccessor.getString("REF_GUID"))
					&& StringUtils.equals(orderGuid,
							etSuccessor.getString("REF_GUID"))) {
				conDoc.setDocNumber(ConversionTools.cutOffZeros(etSuccessor
						.getString("OBJECT_ID")));
				conDoc.setBusObjectType(etSuccessor.getString("OBJTYPE"));
				header.addSuccessor(conDoc);
			}
		}
		headMapper.read(esHeader, backendState, salesDocument, header);
		itemMapper.read(etItem, etScheduleLines, shop, backendState,
				salesDocument);
		final JCoTable partners = function.getTableParameterList().getTable(
				"ET_PARTNER");
		partnerMapper.read(partners, salesDocument, backendState, header);

		if (StringUtils.isNotBlank(esHeader.getString("GUID"))) {
			salesDocument.setTechKey(new TechKey(esHeader.getString("GUID")));
		}
		determineStatus(header, esStatus);
		addOrderGUIDToCache(esHeader);
	}

	/**
	 * @param esHeader
	 *            Adding order guid to cache to avoid GENIL call in subsequent
	 *            requests.
	 */
	protected void addOrderGUIDToCache(final JCoStructure esHeader) {
		Map<String, String> fromCache = null;

		final String orderId = String.valueOf(new BigInteger(esHeader
				.getString("OBJECT_ID")));

		if (cacheManager.getFromCache("order_guids") != null) {
			fromCache = new HashMap<String, String>();
			fromCache.putAll((Map<String, String>) cacheManager
					.getFromCache("order_guids"));

			if (!fromCache.containsKey(orderId)) {
				fromCache.put(orderId, esHeader.getString("GUID"));
			}
		} else {
			fromCache = new HashMap<String, String>();
			fromCache.put(orderId, esHeader.getString("GUID"));
		}
		cacheManager.addToCache("order_guids", fromCache);
	}

	/**
	 * @param function
	 */
	protected void setTableParams(final JCoFunction function) {
		final JCoParameterList importTables = function.getTableParameterList();
		final JCoTable table = importTables.getTable("IT_PARAMETERS");
		table.appendRow();
		table.setValue("PARAMETER", "ES_HEADER");
		table.appendRow();
		table.setValue("PARAMETER", "ET_ITEM");
		table.appendRow();
		table.setValue("PARAMETER", "ET_PARTNER");
		table.appendRow();
		table.setValue("PARAMETER", "ET_SCHEDULELINES");
		table.appendRow();
		table.setValue("PARAMETER", "ET_STATUS_SYST");
		table.appendRow();
		table.setValue("PARAMETER", "ET_SUCCESSOR");
		importTables.setValue("IT_PARAMETERS", table);
	}

	/**
	 * gets orderGuid from session service, If not available in sessionService
	 * then get it by making Genil Call
	 *
	 * @param orderId
	 * @param jcoConnection
	 * @return guid for give order id
	 */
	public String getOrderGUID(final String orderId,
			final JCoConnection jcoConnection) {
		String orderGuid = null;
		if (orderId.length() > 20) {
			return orderId;
		}
		final Object fromCache = cacheManager.getFromCache("order_guids");
		if (fromCache != null && fromCache instanceof Map) {
			final Map<String, String> keys = (Map<String, String>) fromCache;
			if (!CollectionUtils.isEmpty(keys) && keys.containsKey(orderId)) {
				orderGuid = keys.get(orderId);
			}
		}
		if (StringUtils.isBlank(orderGuid)) {
			orderGuid = getOrderDetails(jcoConnection, orderId);
			final Map<String, String> newKeys = new HashMap<String, String>();
			newKeys.put(orderId, orderGuid);
			cacheManager.addToCache("order_guids", newKeys);
		}
		return orderGuid;
	}

	protected String getOrderDetails(final JCoConnection cn,
			final String orderId) {
		sapLogger
				.getLogger()
				.info("**** Enter in the Genil Order History getOrderDetails method *******");
		if (orderId.length() > 20) {
			return orderId;
		}
		Object guid = null;

		try {
			// gets Order Type which is configured in HMC
			final String processType = getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_TRANSACTION_TYPE);
			// Getting GenilProxy and execute
			IGenilProxy genilproxy;

			genilproxy = GenilProxyFactory.getProxy(cn, BT);
			genilproxy.isInitialized();

			GenilQueryRequestContainer queryReqCont;
			queryReqCont = genilproxy.getQueryRequestContainer(BTQ_SLS_ORD);
			queryReqCont.addQueryParam("OBJECT_ID", SIGN, EQ, orderId, "");
			queryReqCont.addQueryParam("PROCESS_TYPE", SIGN, EQ, processType,
					"");
			// default is 100
			queryReqCont
					.setMaxHits(Integer
							.parseInt(getProperty(SapcrmordermgmtbolConstants.CONFIGURATION_PROPERTY_MAX_HIT)));

			searchResult = genilproxy.search(queryReqCont);
			final GenilAttribute attribute = searchResult.get(0).getAttribute(
					"GUID");
			guid = (attribute != null) ? attribute.getValue() : null;
			final GenilMessageContainer msgCont = genilproxy
					.getGlobalMessageContainer();
			msgCont.getMessages();
		} catch (final GenilBackendException e) {
			sapLogger.getLogger().error(e);
		} catch (final JCoException e) {
			sapLogger.getLogger().error(e);
		} catch (final Exception ex) {
			sapLogger.getLogger().error(ex);
		}
		return (guid != null) ? guid.toString() : null;

	}

	void initializeMapper() {
		shop = genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BO_TRANSACTION_CONFIGURATION);
		headMapper = (HeaderMapper) genericFactory
				.getBean(SapcrmordermgmtbolConstants.ALIAS_BEAN_HEADER_MAPPER);
		itemMapper = (ItemMapper) genericFactory
				.getBean(SapcrmordermgmtbolConstants.ALIAS_BEAN_ITEM_MAPPER);
		partnerMapper = (PartnerMapper) genericFactory
				.getBean(SapcrmordermgmtbolConstants.ALIAS_BEAN_PARTNER_MAPPER);
	}

	/**
	 * Wrapper for the remote function call. This can be used for performance
	 * measurement instrumentation, additional logging a.o.
	 */
	@Override
	public void executeRfc(final JCoConnection connection,
			final JCoFunction function) throws BackendException {
		sapLogger.entering("executeRfc");
		try {
			connection.execute(function);
		} finally {
			sapLogger.exiting();
		}
	}

	ShippingStatus getShippingStatusBean() {
		return (ShippingStatus) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_SHIPPING_STATUS);
	}

	OverallStatusOrder getOverallStatusBean() {
		return (OverallStatusOrder) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_OVERALL_STATUS_ORDER);
	}

	BillingStatus getBillingStatusBean() {
		return (BillingStatus) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_BILLING_HEADER_STATUS);
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
	 * @return the moduleConfigurationAccess
	 */
	public ModuleConfigurationAccess getModuleConfigurationAccess() {
		return moduleConfigurationAccess;
	}

	/**
	 * @param moduleConfigurationAccess
	 *            the moduleConfigurationAccess to set
	 */
	public void setModuleConfigurationAccess(
			final ModuleConfigurationAccess moduleConfigurationAccess) {
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

	/**
	 * @return
	 */
	protected String getErrorMessagesTable() {
		return "ET_MESSAGES";
	}
}
