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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sap.genil.GenilConst.GenilRFC;
import com.sap.genil.exception.GenilBackendRuntimeException;


/**
 * Input parameters for RFC call. The class provides several functions to define the input parameters and the RFC
 * function module to be called
 *
 * @author SAP
 * @version 1.0
 *
 * @author C5230248 -- Modified by Removing WCFLocation
 */
public class RFCInput extends RFCInOutput<TablesParam>
{

	private List<String> requestedParameters;

	private List<String> requestedTables;

	/**
	 * Standard constructor, genilRFC is mandatory and must not be null
	 *
	 * @param genilRFC
	 */
	public RFCInput(final GenilRFC genilRFC)
	{
		super(genilRFC);
		if (this.genilRFC == null)
		{
			final String msg = "RFC Function must be specified";
			final GenilBackendRuntimeException gnlBkndExc = new GenilBackendRuntimeException(msg);

			throw gnlBkndExc;
		}
	}

	/**
	 * Adds a name value pair as import parameter
	 *
	 * @param name
	 *           name of the name value pair
	 * @param value
	 *           value of the name value pair
	 */
	public void addImportParameter(final String name, final Object value)
	{
		super.addParameter(name, value);
	}

	/**
	 * Adds a requested parameter to the object. These parameters should be delivered by the RFC call
	 *
	 * @param name
	 *           Name of the requested parameter
	 */
	public void addRequestedParameter(final String name)
	{
		if (requestedParameters == null)
		{
			requestedParameters = new ArrayList<String>();
		}
		requestedParameters.add(name);
	}

	/**
	 * Adds a requested table to the object. These tables should be delivered by the RFC call
	 *
	 * @param name
	 *           Name of the requested table
	 */
	public void addRequestedTables(final String name)
	{
		if (requestedTables == null)
		{
			requestedTables = new ArrayList<String>();
		}
		requestedTables.add(name);
	}

	/**
	 * Adds a name value pair to an table parameter
	 *
	 * @param tableName
	 *           Table where the parameter should be added. if the table parameter does not exist, the table parameter
	 *           will be created
	 * @param row
	 *           row on which the parameters should be changed. If TableParameterRow does not exist on this position, a
	 *           new one will be breted
	 * @param name
	 *           Name of the name value pair
	 * @param value
	 *           Value of the name value pair
	 */
	public void addTablesParameter(final String tableName, final int row, final String name, final Object value)
	{
		TablesParam tp = getTablesParameter(tableName);
		if (tp == null)
		{
			tp = new TablesParam(tableName);
			addTablesParameter(tableName, tp);
		}

		if (row != -1)
		{
			TableParamRow tpr = tp.getRow(row);
			if (tpr == null)
			{
				tpr = new TableParamRow();
				tp.insertRow(tpr, row);
			}

			tpr.add(name, value);
		}
	}

	/**
	 * Getter-Method for property ImportParameters. <br>
	 *
	 * @return Returns the ImportParameters of the object}.
	 */
	public Map<String, NameValuePair> getImportParameters()
	{
		return super.getParameters();
	}

	/**
	 * Getter-Method for property requestedParameters. <br>
	 *
	 * @return Returns the requested Parameters
	 */
	public List<String> getRequestedParameters()
	{
		return requestedParameters;
	}

	/**
	 * Getter-Method for property requestedTables. <br>
	 *
	 * @return Returns the requested Tables
	 */
	public List<String> getRequestedTables()
	{
		return requestedTables;
	}

	/**
	 * Creates a new RFCOutput object
	 *
	 * @return
	 */
	public RFCOutput prepareOutput()
	{
		return new RFCOutput(genilRFC);
	}

	/**
	 * Sorts the rows of the Table Parameters with without deleting duplicates
	 *
	 * @param tableName
	 *           Table name of table to be sorted
	 */
	public void sortTablesParameter(final String tableName)
	{
		sortTablesParameter(tableName, false);
	}

	/**
	 * Sorts the rows of the Table Parameters with without deleting duplicates
	 *
	 * @param tableName
	 *           Table name of table to be sorted
	 * @param deleteDuplicates
	 *           if true, the duplicate entries will be deleted
	 */
	public void sortTablesParameter(final String tableName, final boolean deleteDuplicates)
	{
		final TablesParam tp = getTablesParameter(tableName);
		if (tp != null)
		{
			tp.sort(deleteDuplicates);
		}
	}
}
