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

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import de.hybris.platform.sap.sapmodel.constants.SapmodelConstants;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.hybris.crm.sapcrmordermgmtbol.constants.SapcrmordermgmtbolConstants;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm.BackendExceptionECOCRM;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm.LoadOperation;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.BackendState;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.CRMConstants;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.strategy.GetAllStrategy;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.strategy.LrdActionsStrategy;

import de.hybris.platform.sap.core.bol.backend.jco.JCoHelper;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.PartnerFunctionData;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult.Result;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * CRM implementation of LrdActionsStrategy and ConstantsR3Lrd.
 *
 * @see LrdActionsStrategy
 * @see CRMConstants
 * @version 1.0
 */
public class LrdActionsStrategyCRM extends BaseStrategyCRM implements
		LrdActionsStrategy, CRMConstants {

	@Resource
	private CommonI18NService commonI18NService;
	private static final Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(LrdActionsStrategyCRM.class.getName());
	/**
	 * Allows access to configuration settings
	 */
	protected ModuleConfigurationAccess moduleConfigurationAccess;
	/**
	 * Strategy pattern to support various LO-API versions
	 */
	protected GetAllStrategy getAllStrategy;

	/**
	 * @return the getAllStrategy
	 */
	public GetAllStrategy getGetAllStrategy() {
		return getAllStrategy;
	}

	/**
	 * @param getAllStrategy
	 *            the getAllStrategy to set
	 */
	public void setGetAllStrategy(final GetAllStrategy getAllStrategy) {
		this.getAllStrategy = getAllStrategy;
	}

	/**
	 * @param moduleConfigurationAccess
	 *            Allows access to configuration settings
	 */
	public void setModuleConfigurationAccess(
			final ModuleConfigurationAccess moduleConfigurationAccess) {
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

	/**
	 * Standard constructor. <br>
	 */
	public LrdActionsStrategyCRM() {
		super();
		// Fill the active Fields list only once

	}

	@Override
	public BackendCallResult saveCrmOrder(final SalesDocument salesDoc,
			final boolean commit, final JCoConnection cn)
			throws BackendException {
		final String METHOD_NAME = "executeOrderSave()";
		sapLogger.entering(METHOD_NAME);
		try {

			final JCoFunction function = cn
					.getFunction(getOrderSaveFunctionModule());

			setOrderSaveImportParams(function, commit, salesDoc);

			executeRFC(cn, function);
			return handleOrderSaveResponse(function, salesDoc);
		} catch (final BackendException e) {
			invalidateSalesDocument(salesDoc);
			throw e;
		}
	}

	/**
	 * @param exportParams
	 */
	protected void logResponse(final JCoParameterList exportParams) {
		if (sapLogger.isDebugEnabled()) {
			final StringBuilder debugOutput = new StringBuilder(10);
			debugOutput.append("Result of CRM_HYBRIS_ORDER_SAVE: ");
			debugOutput.append("\n EV_OBJECT_NOT_SAVED      : "
					+ exportParams.getString("EV_OBJECT_NOT_SAVED"));
			sapLogger.debug(debugOutput);
		}
	}

	/**
	 * This method can be overridden to make any pre-processing and post
	 * processing for the RFC call.
	 *
	 * @param cn
	 * @param function
	 * @throws BackendException
	 */
	protected void executeRFC(final JCoConnection cn, final JCoFunction function)
			throws BackendException {
		cn.execute(function);
	}

	/**
	 * @param function
	 * @param commit
	 * @param salesDoc
	 */
	protected void setOrderSaveImportParams(final JCoFunction function,
			final boolean commit, final SalesDocument salesDoc) {
		final JCoParameterList importParams = function.getImportParameterList();
		importParams.setValue("IV_GUID", salesDoc.getTechKey().toString());
		if (commit) {
			// commit specific input parameters
			JCoHelper.setValue(importParams, "X", "IV_COMMIT");
			JCoHelper.setValue(importParams, "X", "IV_RELEASE_LOCK");
		}
	}

	protected BackendCallResult handleOrderSaveResponse(
			final JCoFunction function, final SalesDocument salesDoc)
			throws BackendException {
		BackendCallResult backendResult = new BackendCallResult(Result.success);
		// getting export parameters
		final JCoParameterList exportParams = function.getExportParameterList();

		logResponse(exportParams);

		final JCoTable etMessages = function.getTableParameterList().getTable(
				getErrorMessagesTable());
		if (Boolean.parseBoolean(exportParams.getString("EV_OBJECT_NOT_SAVED"))) {
			dispatchMessages(salesDoc, etMessages, null);
			backendResult = new BackendCallResult(Result.failure);

		}
		// Normal Error that will accepted by CRM to Save order
		dispatchMessages(salesDoc, etMessages, null);
		return backendResult;
	}

	/**
	 * Checks if following attributes have been provided: process type, soldTo,
	 * sales organisation, distribution channel, division (Mandatory fields for
	 * LOAD-call).
	 *
	 * @param posd
	 * @throws BackendExceptionECOCRM
	 */
	protected void checkAttributesLrdLoad(final SalesDocument posd)
			throws BackendExceptionECOCRM {

		checkAttributeEmpty(
				moduleConfigurationAccess
						.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_TRANSACTION_TYPE),
				"Transaction Type");
		checkAttributeEmpty(
				posd.getHeader().getPartnerKey(PartnerFunctionData.SOLDTO),
				"SoldTo");
		checkAttributeEmpty(
				moduleConfigurationAccess
						.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_SALES_ORG),
				"SalesOrg");
		checkAttributeEmpty(
				moduleConfigurationAccess
						.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DISTRIBUTION_CHANNEL),
				"DistrChan");
	}

	/**
	 * Fetch the order details after creation of order in the CRM
	 */
	@Override
	public BackendCallResult createBasket(final SalesDocument salesDoc,
			final BackendState crmDocument, final JCoConnection cn)
			throws BackendException {
		final String METHOD_NAME = "executeBasketCreate()";
		sapLogger.entering(METHOD_NAME);

		JCoParameterList exportParams = null;

		final JCoFunction function = setImportParamsForBasketCreate(salesDoc,
				cn, crmDocument);

		executeRFC(cn, function);

		exportParams = function.getExportParameterList();
		final JCoParameterList tableParams = function.getTableParameterList();
		final BackendCallResult result = processResultForBasketCreate(salesDoc,
				exportParams, tableParams);

		if (sapLogger.isDebugEnabled()) {
			logCall(getOrderSetFunctionModule(), null,
					exportParams.getStructure("ES_HEADER"));
		}
		sapLogger.exiting();
		return result;
	}

	/**
	 * @param salesDoc
	 * @param exportParams
	 * @param tableParams
	 * @return BackendCallResult
	 * @throws BackendException
	 */
	protected BackendCallResult processResultForBasketCreate(
			final SalesDocument salesDoc, final JCoParameterList exportParams,
			final JCoParameterList tableParams) throws BackendException {

		salesDoc.getHeader().setTechKey(
				new TechKey(exportParams.getStructure("ES_HEADER").getString(
						"GUID")));
		salesDoc.getHeader().setHandle(
				exportParams.getStructure("ES_HEADER").getString("HANDLE"));

		// VERIFY

		final JCoTable etMessages = tableParams
				.getTable(getErrorMessagesTable());
		final JCoStructure esHeader = exportParams.getStructure("ES_HEADER");
		final boolean isEror = checkError(etMessages, salesDoc, esHeader);
		if (!isEror) {
			return new BackendCallResult(BackendCallResult.Result.success);
		} else {
			return new BackendCallResult(BackendCallResult.Result.failure);
		}
	}

	/*
	 * @param salesDoc
	 *
	 * @param cn
	 *
	 * @param crmDocument
	 *
	 * @return JCoFunction
	 *
	 * @throws BackendException
	 */
	protected JCoFunction setImportParamsForBasketCreate(
			final SalesDocument salesDoc, final JCoConnection cn,
			final BackendState crmDocument) throws BackendException {
		JCoFunction function = null;
		JCoParameterList importParams = null;

		String processType = salesDoc.getHeader().getProcessType();
		if (processType == null || processType.length() == 0) {
			processType = (String) moduleConfigurationAccess
					.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_TRANSACTION_TYPE);
		}
		// Get CRM ORDER SET function
		function = cn.getFunction(getOrderSetFunctionModule());
		// Creating Basket
		crmDocument.setDocumentInitial(true);

		// First check on essential attributes
		checkAttributesLrdLoad(salesDoc);

		importParams = function.getImportParameterList();

		// Header
		final JCoStructure isHeader = importParams.getStructure("IS_HEADER");
		isHeader.setValue("HANDLE", CRMConstants.FIRST_HEAD_HANDLE);

		isHeader.setValue("PROCESS_TYPE", processType);

		setHeaderValues(isHeader);

		importParams.setValue("IS_HEADER", isHeader);

		final JCoTable headField = function.getTableParameterList().getTable(
				"IT_FIELDS_HEADER_SET");

		headField.appendRow();
		headField.setValue(CRMConstants.FIELD_NAME, "PROCESS_TYPE");

		setHeaderFields(headField);

		final JCoParameterList importTables = function.getTableParameterList();
		final JCoTable table = importTables.getTable("IT_PARAMETERS");
		table.appendRow();
		table.setValue("PARAMETER", "ES_HEADER");

		// Control Tables
		final JCoStructure headControl = importParams
				.getStructure("IS_CONTROL");
		headControl.setValue("LANGUAGE", commonI18NService.getCurrentLanguage()
				.getIsocode().toString());
		// Mode "A - Initialize"
		headControl.setValue("MODE", CRMConstants.BASKET_CREATE_MODE);

		importParams.setValue("IS_CONTROL", headControl);

		final boolean paytypeCOD = false;
		final PartnerMapper partnerMapper = (PartnerMapper) genericFactory
				.getBean(getPartnerMapperBean());
		partnerMapper.populatePartnerImportParams(salesDoc, function, null,
				paytypeCOD);
		if (sapLogger.isDebugEnabled()) {
			logCall(getOrderSetFunctionModule(), importParams, null);
		}

		return function;
	}

	/**
	 * @param headField
	 */
	protected void setHeaderFields(final JCoTable headField) {
		headField.appendRow();
		headField.setValue(CRMConstants.FIELD_NAME, "SALES_ORG");
		if (!StringUtils
				.isEmpty((String) moduleConfigurationAccess
						.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DIVISION))) {
			headField.appendRow();
			headField.setValue(CRMConstants.FIELD_NAME, "DIVISION");
		}
		headField.appendRow();
		headField.setValue(CRMConstants.FIELD_NAME, "DIS_CHANNEL");
		headField.appendRow();
		headField.setValue(CRMConstants.FIELD_NAME, "CURRENCY");
		headField.appendRow();
		headField.setValue(CRMConstants.FIELD_NAME, "SALES_ORG_RESP");
	}

	/**
	 * @param isHeader
	 */
	protected void setHeaderValues(final JCoStructure isHeader) {
		isHeader.setValue(
				"SALES_ORG",
				(String) moduleConfigurationAccess
						.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_SALES_ORG));
		if (!StringUtils
				.isEmpty((String) moduleConfigurationAccess
						.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DIVISION))) {
			isHeader.setValue(
					"DIVISION",
					(String) moduleConfigurationAccess
							.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DIVISION));
		}

		isHeader.setValue(
				"DIS_CHANNEL",
				(String) moduleConfigurationAccess
						.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DISTRIBUTION_CHANNEL));
		isHeader.setValue("CURRENCY", commonI18NService.getCurrentCurrency()
				.getIsocode());
		isHeader.setValue(
				"SALES_ORG_RESP",
				(String) moduleConfigurationAccess
						.getProperty(SapcrmordermgmtbolConstants.CONFIGURATION_PROPERTY_SALES_ORG_RESPONSIBLE));
	}

	@Override
	public BackendCallResult loadCrmOrderDetail(final SalesDocument posd,
			final BackendState crmDocument, final JCoConnection cn,
			final LoadOperation loadState) throws BackendException {
		final String METHOD_NAME = "executeCrmOrderLoad()";

		sapLogger.entering(METHOD_NAME);

		final BackendCallResult result = getAllStrategy.execute(crmDocument,
				posd, null, cn);

		sapLogger.exiting();

		return result;
	}

	/**
	 * @return
	 */
	protected String getErrorMessagesTable() {
		return "ET_MESSAGES";
	}

	/**
	 * @return
	 */
	protected String getPartnerMapperBean() {
		return SapcrmordermgmtbolConstants.ALIAS_BEAN_PARTNER_MAPPER;
	}
}