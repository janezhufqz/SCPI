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

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm.BackendExceptionECOCRM;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.BackendMessageCRMMapper;


/**
 * Base class for strategy implementations dealing with CRM LO-API calls
 */
public class BaseStrategyCRM
{

	private static final String MSGPARAM = "VDATU";
	private static final String REC_MEG_NUM = "018";
	private static final String CRM_HYB_ORDER = "CRM_HYB_ORDER";
	private static final String ERROR_GUID = "00000000000000000000000000000000";

	private String orderSetFunctionModule;

	private String orderDetailFunctionModule;

	private String orderSaveFunctionModule;

	/**
	 * @return the orderSetFunctionModule
	 */
	public String getOrderSetFunctionModule()
	{
		return orderSetFunctionModule;
	}

	/**
	 * @param orderSetFunctionModule
	 *           the orderSetFunctionModule to set
	 */
	public void setOrderSetFunctionModule(final String orderSetFunctionModule)
	{
		this.orderSetFunctionModule = orderSetFunctionModule;
	}

	/**
	 * @return the orderDetailFunctionModule
	 */
	public String getOrderDetailFunctionModule()
	{
		return orderDetailFunctionModule;
	}

	/**
	 * @param orderDetailFunctionModule
	 *           the orderDetailFunctionModule to set
	 */
	public void setOrderDetailFunctionModule(final String orderDetailFunctionModule)
	{
		this.orderDetailFunctionModule = orderDetailFunctionModule;
	}

	/**
	 * @return the orderSaveFunctionModule
	 */
	public String getOrderSaveFunctionModule()
	{
		return orderSaveFunctionModule;
	}

	/**
	 * @param orderSaveFunctionModule
	 *           the orderSaveFunctionModule to set
	 */
	public void setOrderSaveFunctionModule(final String orderSaveFunctionModule)
	{
		this.orderSaveFunctionModule = orderSaveFunctionModule;
	}

	/**
	 * Factory to access SAP session beans
	 */
	protected GenericFactory genericFactory;

	/**
	 * @return the genericFactory
	 */
	public GenericFactory getGenericFactory()
	{
		return genericFactory;
	}

	/**
	 * @param genericFactory
	 *           the genericFactory to set
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}


	/**
	 * Logging instance
	 */
	protected static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(BaseStrategyCRM.class.getName());
	protected static final String FALSE = "F";

	private static final String DIRECTION_IN = " - IN: ";
	private static final String DIRECTION_OUT = " - OUT: ";
	private BackendMessageCRMMapper messageMapper;

	/**
	 * @return the messageMapper
	 */
	public BackendMessageCRMMapper getMessageMapper()
	{
		return messageMapper;
	}



	/**
	 * Represents the data returned by a call to the function module.
	 *
	 * @version 1.0
	 */
	public static class ReturnValue
	{
		/**
		 * Return code of a FM call
		 */
		protected String myReturnCode;

		protected JCoTable messageRead;

		protected JCoStructure messageStruct;

		/**
		 * Creates a new instance
		 *
		 * @param messages
		 *           Table containing the messages returned from the backend
		 * @param returnCode
		 *           The return code returned from the backend
		 */
		public ReturnValue(final JCoTable messages, final String returnCode)
		{

			myReturnCode = returnCode;
			messageRead = messages;
		}

		/**
		 * Creates a new instance
		 *
		 * @param message
		 *           Structure containing only one message (Structure can be different as for the messages table)
		 * @param messages
		 *           Table containing the messages returned from the backend
		 * @param returnCode
		 *           The return code returned from the backend
		 */
		public ReturnValue(final JCoTable messages, final JCoStructure message, final String returnCode)
		{

			myReturnCode = returnCode;
			messageRead = messages;
			messageStruct = message;
		}

		/**
		 * Creates a new instance
		 *
		 * @param returnCode
		 *           The return code returned from the backend
		 */
		public ReturnValue(final String returnCode)
		{

			myReturnCode = returnCode;
		}

		/**
		 * @return Return code from a JCO FM call
		 */
		public String getReturnCode()
		{
			return myReturnCode;
		}

	}

	/**
	 * Checks if an attribute is initial and throws an exception if this is the case.
	 *
	 * @param attribute
	 *           if it is a string, emptyness is checked in addition
	 * @param attributeName
	 * @throws BackendExceptionECOCRM
	 */
	protected static void checkAttributeEmpty(final Object attribute, final String attributeName) throws BackendExceptionECOCRM
	{
		boolean throwMessage = false;
		if (attribute == null)
		{
			throwMessage = true;
		}
		else if (attribute instanceof String)
		{
			final String attributeAsString = (String) attribute;
			if (attributeAsString.isEmpty())
			{
				throwMessage = true;
			}
		}
		else if (attribute instanceof TechKey)
		{
			final TechKey attributeAsKey = (TechKey) attribute;
			if (attributeAsKey.isInitial())
			{
				throwMessage = true;
			}
		}
		if (throwMessage)
		{
			final Message message = new Message(Message.ERROR, "sapsalestransactions.crm.missingattr");
			throw new BackendExceptionECOCRM(message.getMessageText(LocaleUtil.getLocale()) + ": " + attributeName);
		}

	}

	/**
	 * Logs a RFC-call into the log file of the application.
	 *
	 * @param functionName
	 *           the name of the JCo function that was executed
	 * @param input
	 *           input data for the function module. You may set this parameter to <code>null</code> if you don't have
	 *           any data to be logged.
	 * @param output
	 *           Log4 * output data for the function module. You may set this parameter to <code>null</code> if you don't
	 *           have any data to be logged.
	 * @param log
	 *           the logging context to be used
	 */
	public static void logCall(final String functionName, final JCoRecord input, final JCoRecord output, final Log4JWrapper log)
	{

		creatRecordLogEntry(functionName, input, DIRECTION_IN, log);
		creatRecordLogEntry(functionName, output, DIRECTION_OUT, log);

	}

	static void creatRecordLogEntry(final String functionName, final JCoRecord record, final String direction,
			final Log4JWrapper log)
	{
		final StringBuilder buffer = new StringBuilder();
		createBufferForLogging(functionName, record, direction, buffer);

		log.debug(buffer.toString());

	}

	/**
	 * @param functionName
	 * @param record
	 * @param direction
	 * @param buffer
	 */
	static void createBufferForLogging(final String functionName, final JCoRecord record, final String direction,
			final StringBuilder buffer)
	{
		boolean recordOK = true;

		if (record instanceof JCoTable)
		{
			final JCoTable inputTable = (JCoTable) record;
			if (inputTable.getNumRows() <= 0)
			{
				recordOK = false;
			}
		}

		if ((record != null) && recordOK)
		{

			buffer.append("::").append(functionName).append("::").append(direction).append(record.getMetaData().getName())
					.append(" * ");
			final JCoFieldIterator iterator = record.getFieldIterator();
			while (iterator.hasNextField())
			{
				final JCoField field = iterator.nextField();
				buffer.append(field.getName()).append("='").append(field.getString()).append("' ");
			}

		}
	}

	protected void invalidateSalesDocument(final SalesDocument doc)
	{
		doc.setInitialized(false);
	}

	/**
	 * Traces a function module call
	 *
	 * @param functionName
	 *           Name of RFC function
	 * @param input
	 *           Import attributes
	 * @param output
	 *           Export attributes
	 */
	protected static void logCall(final String functionName, final JCoRecord input, final JCoRecord output)
	{
		logCall(functionName, input, output, sapLogger);
	}

	/**
	 * Can we recover from this error situation?
	 *
	 * @param esError
	 *           Error structure of type TDS_ERROR
	 * @return true if we can proceed after LO-API LOAD
	 */
	protected boolean isRecoverableHeaderError(final JCoStructure esError)
	{
		//This Method can be used to determine error recoverable case.
		final String messageId = esError.getString("ID");
		final String messageNumber = esError.getString("NUMBER");
		final String firstParameter = esError.getString("MESSAGE_V1");
		// CRM_HYB_ORDER / 018 / VDATU: Required delivery date must be specified. Can
		// be corrected
		final boolean isRecoverable = CRM_HYB_ORDER.equals(messageId) && REC_MEG_NUM.equals(messageNumber)
				&& MSGPARAM.equals(firstParameter);
		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("Error message during load can be handled: " + isRecoverable);
		}
		return isRecoverable;
	}

	/**
	 * Logs and attaches messages to a given document.
	 *
	 * @param bob
	 *           Target business object the messages must be applied to
	 *
	 * @param msgTable
	 *           Table with messages
	 * @param singleMsg
	 *           Structure containing one single message
	 * @throws BackendException
	 */

	protected void dispatchMessages(final BusinessObject bob, final JCoTable msgTable, final JCoStructure singleMsg)
			throws BackendException
	{

		sapLogger.entering("dispatchMessages");
		messageMapper.map(bob, singleMsg, msgTable);
		sapLogger.exiting();
	}




	/**
	 * @param messageMapper
	 *           Maps backend message to BOL messages (refer to messages.xml)
	 */
	public void setMessageMapper(final BackendMessageCRMMapper messageMapper)
	{
		this.messageMapper = messageMapper;
	}

	/**
	 * @param esError
	 */


	/**
	 * @param etMessages
	 * @param salesDoc
	 * @param esHeader
	 * @throws BackendException
	 */
	protected boolean checkError(final JCoTable etMessages, final SalesDocument salesDoc, final JCoStructure esHeader)
			throws BackendException
	{
		//Error Case with High Priority
		boolean isError = false;

		if (ERROR_GUID.equalsIgnoreCase(esHeader.getString("GUID")))
		{
			dispatchMessages(salesDoc, etMessages, null);
			isError = true;

		}
		//Normal Error Case:
		dispatchMessages(salesDoc, etMessages, null);
		return isError;

	}
}
