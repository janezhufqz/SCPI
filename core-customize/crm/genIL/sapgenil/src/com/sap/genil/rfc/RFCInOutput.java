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

import java.util.HashMap;
import java.util.Map;

import com.sap.genil.GenilConst.GenilRFC;


/**
 * Base container class for input/output operations via RFC. The class contains the RFC to be called, single and table
 * parameters for the RFC call
 *
 * @author SAP
 * @version 1.0
 */
public abstract class RFCInOutput<V>
{

	/**
	 * RFC function module to be called
	 */
	protected GenilRFC genilRFC;

	/**
	 * Hash map with single name value pairs
	 */
	private Map<String, NameValuePair> parameters;

	/**
	 * Hash map with name value pair tables
	 */
	private Map<String, V> tables;

	/**
	 * Standard constructor
	 * 
	 * @param genilRFC
	 *           RFC function Module name
	 */
	public RFCInOutput(final GenilRFC genilRFC)
	{
		super();
		this.genilRFC = genilRFC;
	}

	/**
	 * Adds a single name Value pair to the object
	 * 
	 * @param name
	 *           Name of the Parameter
	 * @param value
	 *           Value of the Parameter
	 */
	protected void addParameter(final String name, final Object value)
	{
		if (parameters == null)
		{
			parameters = new HashMap<String, NameValuePair>();
		}
		parameters.put(name, new NameValuePair(name, value));
	}

	/**
	 * Adds a table Parameter to the the Object
	 * 
	 * @param tableName
	 *           Name of the Table Parameter
	 * @param value
	 *           Table of Name Value pairs
	 */
	public void addTablesParameter(final String tableName, final V value)
	{
		if (tables == null)
		{
			tables = new HashMap<String, V>();
		}
		tables.put(tableName, value);
	}

	/**
	 * Getter-Method for property {@link #genilRFC}. <br>
	 * 
	 * @return Returns the {@link #genilRFC}.
	 */
	public GenilRFC getGenilRFC()
	{
		return genilRFC;
	}

	protected Map<String, NameValuePair> getParameters()
	{
		return parameters;
	}


	/**
	 * Getter-Method for property {@link #tables}. <br>
	 * Returns a single table by name
	 * 
	 * @param tableName
	 *           Table name of the Table to get
	 * @return Returns the {@link #tables}.
	 */
	public V getTablesParameter(final String tableName)
	{
		if (tables == null)
		{
			return null;
		}
		return tables.get(tableName);
	}



	/**
	 * Getter-Method for property {@link #tables}. <br>
	 * 
	 * @return Returns the {@link #tables}.
	 */
	public Map<String, V> getTablesParameters()
	{
		return tables;
	}
}
