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

package com.sap.genil.rfc;

import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.genil.GenilConst.GenilRFC;
import com.sap.genil.exception.GenilBackendException;




/**
 * Provides some helper functions to execute the RFC Calls to the ABAP RFC function modules which wraps the Genil
 * functionality on APAB Side
 *
 * @author SAP
 * @version 1.0
 *
 * @author C5230248 -- Modified by Removing WCFLocation,ManagedConnectionBase,moduleId -- Modified by removing Genil
 *         JCoConnection and added Hybris JCoConnection
 */
public class GenilRFCWrapper
{

	private final JCoConnection connection;


	/**
	 * Sets the connection used for RFC calls
	 *
	 * @param connection
	 */
	public GenilRFCWrapper(final JCoConnection connection)
	{
		super();
		this.connection = connection;


	}

	/**
	 * Executes a single RFC directly
	 *
	 * @param rfc
	 *           RFC function to be executed
	 * @return
	 * @throws GenilBackendException
	 */
	public RFCOutput doRFC(final GenilRFC rfc) throws GenilBackendException
	{
		return doRFC(new RFCInput(rfc));
	}

	/**
	 * Execute a single RFC with parameters which are described in the input structure input
	 *
	 * @param input
	 *           Input structure which contains RFC to be called and its parameters
	 * @return
	 * @throws GenilBackendException
	 */

	public RFCOutput doRFC(final RFCInput input) throws GenilBackendException
	{

		final GenilRFC rfc = input.getGenilRFC();
		if (rfc == null)
		{
			final String msg = "Invalid Paramters for doRFC";
			final GenilBackendException genilBkndExc = new GenilBackendException(msg);

			throw genilBkndExc;
		}

		final RFCOutput result = input.prepareOutput();

		final String rfcStr = rfc.toString();
		try
		{
			final JCoFunction function = connection.getFunction(rfcStr);

			// Handle Import Paramteres
			handleImportTableparams(input, function);

			connection.execute(function);



			// Handle Export Paramters
			if (input.getRequestedParameters() != null && !input.getRequestedParameters().isEmpty())
			{
				final JCoParameterList exportParams = function.getExportParameterList();
				for (final String name : input.getRequestedParameters())
				{
					result.addExportParameter(name, exportParams.getValue(name));
				}
			}

			// Handle Tables Parameters
			getResultTableParameter(input, result, function);
			result.setSuccess(true);
		}
		catch (final BackendException e)
		{
			final String msg = "Error during execution of RFC " + rfcStr;
			final GenilBackendException gnlBkndExc = new GenilBackendException(msg, e);
			throw gnlBkndExc;
		}


		return result;
	}

	/**
	 * @param input
	 * @param function
	 */
	private void handleImportTableparams(final RFCInput input, final JCoFunction function)
	{
		if (input.getImportParameters() != null && !input.getImportParameters().isEmpty())
		{
			final JCoParameterList importParams = function.getImportParameterList();
			importParams.clear();

			setNvpAndValue(input, importParams);


		}

		// Handle Tables Parameters
		if (input.getTablesParameters() != null && !input.getTablesParameters().isEmpty())
		{
			final JCoParameterList tableParams = function.getTableParameterList();

			setNvpAndValues(input, tableParams);

		}
	}

	/**
	 * @param input
	 * @param result
	 * @param function
	 */
	private void getResultTableParameter(final RFCInput input, final RFCOutput result, final JCoFunction function)
	{
		if (input.getRequestedTables() != null && !input.getRequestedTables().isEmpty())
		{
			final JCoParameterList tableParams = function.getTableParameterList();
			for (final String table : input.getRequestedTables())
			{
				final JCoTable jcoTable = tableParams.getTable(table);
				if (jcoTable != null)
				{
					result.addTablesParameter(table, jcoTable);
				}
			}
		}
	}

	/**
	 * @param tableParams
	 *
	 */
	private void setNvpAndValues(final RFCInput input, final JCoParameterList tableParams)
	{
		for (final TablesParam tp : input.getTablesParameters().values())
		{
			final JCoTable jcoTable = tableParams.getTable(tp.getTableName());
			if (jcoTable != null)
			{
				jcoTable.clear();
				checkJcoTableName(tp, jcoTable);
			}
			tableParams.setValue(tp.getTableName(), jcoTable);
		}

	}

	/**
	 * @param tp
	 * @param jcoTable
	 */
	private void checkJcoTableName(final TablesParam tp, final JCoTable jcoTable)
	{
		for (final TableParamRow tpr : tp.getRows())
		{
			jcoTable.appendRow();
			for (final NameValuePair nvp : tpr.getRow())
			{
				jcoTable.setValue(nvp.getName(), nvp.getValue());
			}
		}
	}

	/**
	 * @param importParams
	 *
	 */
	private void setNvpAndValue(final RFCInput input, final JCoParameterList importParams)
	{
		for (final NameValuePair nvp : input.getImportParameters().values())
		{
			if (nvp.getValue() instanceof ImportStructure)
			{
				final ImportStructure is = (ImportStructure) nvp.getValue();
				final JCoStructure iStruct = importParams.getStructure(is.getName());
				for (final NameValuePair nvpIS : is.getValues())
				{
					iStruct.setValue(nvpIS.getName(), nvpIS.getValue());
				}
			}
			else
			{
				importParams.setValue(nvp.getName(), nvp.getValue());
			}
		}

	}

	// trace the complete Genil RFC parameters to have a base for error
	// searching
	private void traceJcoCallParameters(final JCoFunction function, final String heading)
	{
		final JCoParameterList importParameters = function.getImportParameterList();
		final JCoParameterList exportParameters = function.getExportParameterList();
		final JCoParameterList changingParameters = function.getChangingParameterList();
		final JCoParameterList tableParameters = function.getTableParameterList();

		final StringBuilder completeText = new StringBuilder();
		final StringBuilder emptyValues = new StringBuilder();

		completeText
				.append("************************************************************************************************************************************************");
		completeText.append("\n");
		completeText.append(heading);
		completeText.append(function.getName());
		completeText.append("\n");

		traceJCoParameter(importParameters, completeText, emptyValues, "Import");
		traceJCoParameter(exportParameters, completeText, emptyValues, "Export");
		traceJCoParameter(changingParameters, completeText, emptyValues, "Changing");
		traceJCoParameter(tableParameters, completeText, emptyValues, "Table");

		if (emptyValues.length() > 0)
		{
			completeText.append("Empty Parameters: ");
			completeText.append(emptyValues);
			completeText.append("\n");
		}
		completeText
				.append("************************************************************************************************************************************************");
		completeText.append("\n");


	}

	private void traceJCoParameter(final JCoParameterList importParameters, final StringBuilder completeText,
			final StringBuilder emptyValues, final String typeName)
	{
		if (importParameters != null)
		{
			final StringBuilder buffer = new StringBuilder();
			getJcoFieldName(importParameters, emptyValues, buffer);
			if (buffer.length() > 0)
			{
				completeText.append("\n");
				completeText.append("with " + typeName + " parameters: ");
				completeText.append("\n");
				completeText.append(buffer);
			}

		}
	}

	/**
	 * @param importParameters
	 * @param emptyValues
	 * @param buffer
	 */
	private void getJcoFieldName(final JCoParameterList importParameters, final StringBuilder emptyValues,
			final StringBuilder buffer)
	{
		for (final JCoField name : importParameters)
		{
			if (name.getValue() != null)
			{
				checkNameIsTable(buffer, name);
			}
			else
			{
				emptyValues.append(name.getName());
				emptyValues.append(" ");
			}
		}
	}

	/**
	 * @param buffer
	 * @param name
	 */
	private void checkNameIsTable(final StringBuilder buffer,
			final JCoField name) {
		if(  (!name.isTable() && !name.getValue().toString().isEmpty()) || (name.isTable() && !name.getTable().isEmpty()) ){					
			buffer.append("Name=");
			buffer.append(name.getName());
			if (name.getRecordMetaData() != null)
			{
				buffer.append(", Table=");
				buffer.append("\n");
			}
			else
			{
				buffer.append(", Value=");
			}
			buffer.append(name.getValue());
			buffer.append("\n");
		}
	}

}