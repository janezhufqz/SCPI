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

package com.sap.genil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.sap.genil.exception.GenilBackendException;
import com.sap.genil.exception.GenilBackendRuntimeException;



/**
 * This class models Query based Request to GenIL.<br>
 * The request parameters are stored as a list of GenilQueryParameter
 *
 * @see com.sap.genil.GenilQueryParameter
 * @author SAP
 * @version 1.0
 *
 * @author C5230248 -- Modified by Removing WCFLocation
 */
public class GenilQueryRequestContainer
{

	/**
	 * GenIL match type All (default)
	 */
	public static final char ALL = 'A';
	/**
	 * GenIL match type One
	 */
	public static final char ONE = 'B';
	/**
	 * Default Max hit number
	 */
	public static final int DEFAULT_MAX_HITS = 100;

	/**
	 * The GenIL Proxy this container is connected to.
	 */
	private final GenilProxy proxy;
	/**
	 * GenIL name of the query Object
	 */
	private final String queryObjectName;
	/**
	 * Max hits
	 */
	private int maxHits;
	/**
	 * List to store the request parameters.
	 */
	private final List<GenilQueryParameter> parameters;

	/**
	 * GenIL matchtype
	 */
	private char matchType;

	/**
	 * Used for reading related objects to the search result
	 */
	private GenilReadRootRequestContainer requestContainter;

	/**
	 * Standard constructor. <br>
	 *
	 * @param queryObjectName
	 *           GenIL name of the query Object
	 * @throws GenilBackendException
	 *            if the rootObject is unknown to the model
	 */
	protected GenilQueryRequestContainer(final String queryObjectName, final GenilProxy proxy) throws GenilBackendException
	{
		super();
		if (proxy == null)
		{
			final String msg = "Proxy must be provided";

			final GenilBackendException gnlBkndExc = new GenilBackendException(msg);
			throw gnlBkndExc;
		}
		this.proxy = proxy;
		this.queryObjectName = queryObjectName;
		this.parameters = new ArrayList<GenilQueryParameter>();
		if (getModelObject() == null)
		{
			final String msg = "QueryObject " + queryObjectName + " unknown in Model";

			final GenilBackendException gnlBkndExc = new GenilBackendException(msg);

			throw gnlBkndExc;
		}
		init();
	}

	/**
	 * Adds the given parameter to the list.<br>
	 *
	 * @param param
	 *           Parameter to set
	 * @throws GenilBackendException
	 *            if param is not valid according to the model
	 */
	public void addQueryParam(final GenilQueryParameter param)
	{
		checkParam(param.getParameterName());
		parameters.add(param);
	}

	/**
	 * Shortcut method to add a parameter, same as constructing a new param and calling the ad method with it:
	 * <code>addQueryParam(GenilQueryParameter(name, sign, option, low, high);
	 *
	 * @param name
	 *           name of the GenIL parameter, not allowed to be empty or null
	 * @param sign
	 *           sign has to be valid, use corresponding constants
	 * @param option
	 *           option has to be valid, use corresponding constants
	 * @param low
	 *           low value, may be null or empty
	 * @throws GenilBackendException
	 *            if the parameter is not valid
	 * @see com.sap.wec.tc.core.backend.genil.GenilQueryParameter#GenilQueryParameter(String, char, String, String,
	 *      String)
	 */
	public void addQueryParam(final String name, final char sign, final String option, final String low)
			throws GenilBackendException
	{
		try
		{
			checkDQueryOptions(name, sign, option);
		}
		catch (final GenilBackendRuntimeException e)
		{
			final String msg = "Error in Query Parameter [name = " + name + ", sign = " + sign + ", option = " + option + ", low = "
					+ low + "]";
			final GenilBackendException gnlBkndExc = new GenilBackendException(msg, e);
			throw gnlBkndExc;
		}
		parameters.add(new GenilQueryParameter(name, sign, option, low));
	}

	/**
	 * Shortcut method to add a parameter, same as constructing a new param and calling the ad method with it:
	 * <code>addQueryParam(GenilQueryParameter(name, sign, option, low, high);
	 *
	 * @param name
	 *           name of the GenIL parameter, not allowed to be empty or null
	 * @param sign
	 *           sign has to be valid, use corresponding constants
	 * @param option
	 *           option has to be valid, use corresponding constants
	 * @param low
	 *           low value, may be null or empty
	 * @param high
	 *           high value, may be null or empty
	 * @throws GenilBackendException
	 *            if the parameter is not valid
	 * @see com.sap.wec.tc.core.backend.genil.GenilQueryParameter#GenilQueryParameter(String, char, String, String,
	 *      String)
	 */
	public void addQueryParam(final String name, final char sign, final String option, final String low, final String high)
			throws GenilBackendException
	{

		try
		{
			checkDQueryOptions(name, sign, option);
		}
		catch (final GenilBackendRuntimeException e)
		{
			final String msg = "Error in Query Parameter [name = " + name + ", sign = " + sign + ", option = " + option + ", low = "
					+ low + ", high" + high + "]";
			final GenilBackendException gnlBkndExc = new GenilBackendException(msg, e);
			throw gnlBkndExc;
		}
		parameters.add(new GenilQueryParameter(name, sign, option, low, high));
	}

	/**
	 * Shortcut method to add a parameter, same as constructing a new param and calling the ad method with it:
	 * <code>addQueryParam(GenilQueryParameter(name, low_value);
	 *
	 * @param name
	 *           parameter name
	 * @param low
	 *           parameter value
	 * @throws GenilBackendException
	 *            if the parameter is not valid
	 * @see com.sap.wec.tc.core.backend.genil.GenilQueryParameter#GenilQueryParameter(String, String)
	 */
	public void addQueryParam(final String name, final String low) throws GenilBackendException
	{
		try
		{
			checkParam(name);
		}
		catch (final GenilBackendRuntimeException e)
		{
			final String msg = "Error in Query Parameter [name = " + name + ", low = " + low + "]";
			final GenilBackendException gnlBkndExc = new GenilBackendException(msg, e);
			throw gnlBkndExc;
		}
		parameters.add(new GenilQueryParameter(name, low));
	}

	/**
	 * Checks if the Parameter is valid..<br>
	 *
	 * @param name
	 *           name of the parameter to check
	 * @throws GenilBackendException
	 *            if the parameter is not valid
	 */
	private void checkParam(final String name)
	{
		if (!isParamInModel(name))
		{
			final String msg = "Invalid Parameter " + name + " for QueryObject " + queryObjectName;
			final GenilBackendRuntimeException gnlBkndExc = new GenilBackendRuntimeException(msg);
			throw gnlBkndExc;
		}
	}

	/**
	 * Checks if the Parameter is valid..<br>
	 *
	 * @param name
	 *           name of the parameter to check
	 * @throws GenilBackendException
	 *            if the parameter is not valid
	 */
	private void checkDQueryOptions(final String name, final char sign, final String option) throws GenilBackendRuntimeException
	{
		checkParam(name);
		if (!getModelObject().getAttrByName(name).attributeOptionAllowed(sign, option))
		{
			final String msg = "Invalid Attribute Option " + sign + "," + option + " for Attribute " + name + " of QueryObject "
					+ queryObjectName;
			final GenilBackendRuntimeException gnlBkndExc = new GenilBackendRuntimeException(msg);
			throw gnlBkndExc;

		}
	}

	public GenilReadRootRequestContainer createRequestContainer(final String searchResultObejctName) throws GenilBackendException
	{
		requestContainter = proxy.getReadRequestContainer(searchResultObejctName, null);
		return requestContainter;
	}

	/**
	 * Getter-Method for property {@link #matchType}. <br>
	 *
	 * @return Returns the {@link #matchType}.
	 */
	public char getMatchType()
	{
		return matchType;
	}

	/**
	 * Getter-Method for property {@link #maxHits}. <br>
	 *
	 * @return Returns the {@link #maxHits}.
	 */
	public int getMaxHits()
	{
		return maxHits;
	}

	/**
	 * Utility Method to obtain the GenIL Model Object this container is associated to. This object provides design time
	 * information from GenIL Model.<br>
	 *
	 * @return GenilModelObject this container is associated to
	 */
	public GenilModelObject getModelObject()
	{
		return proxy.getModel().getObject(queryObjectName);
	}

	/**
	 * Returns an unmodifiable list of all parameters. <br>
	 *
	 * @return List of all parameters.
	 */
	public List<GenilQueryParameter> getParameters()
	{
		return Collections.unmodifiableList(parameters);
	}

	/**
	 * Returns an unmodifiable list of parameters that all have the same name as the given name ignoring case.<br>
	 *
	 * @param name
	 *           Parameter name to search for
	 * @return List of parameters with the given name
	 */
	public List<GenilQueryParameter> getParameters(final String name)
	{
		final List<GenilQueryParameter> all = new ArrayList<GenilQueryParameter>();
		for (final GenilQueryParameter param : parameters)
		{
			if (param.getParameterName().equalsIgnoreCase(name))
			{
				all.add(param);
			}
		}
		return Collections.unmodifiableList(all);
	}

	/**
	 * Getter-Method for property {@link #queryObjectName}. <br>
	 *
	 * @return Returns the {@link #queryObjectName}.
	 */
	public String getQueryObjectName()
	{
		return queryObjectName;
	}

	public GenilReadRootRequestContainer getRequestContainter()
	{
		return requestContainter;
	}

	/**
	 * resets to default values
	 */
	public void init()
	{
		parameters.clear();
		this.maxHits = DEFAULT_MAX_HITS;
		this.matchType = ALL;
	}

	/**
	 * Checks in the GenIL Model if there exits such an attribute for the GenIL object this container is associated to.<br>
	 *
	 * @param paramName
	 *           Name of the Relation to check
	 * @return <code>true</code> only if the given attribute is valid
	 */
	public boolean isParamInModel(final String paramName)
	{
		if (getModelObject().getAttrByName(paramName) == null)
		{
			return false;
		}
		return true;
	}

	/**
	 * Removes all parameters that have the given name ignoring case.<br>
	 *
	 * @param name
	 *           name of the parameters to remove
	 */
	public void removeParams(final String name)
	{
		if (name == null)
		{
			return;
		}
		final Iterator<GenilQueryParameter> itr = parameters.iterator();
		while (itr.hasNext())
		{
			if (itr.next().getParameterName().equalsIgnoreCase(name))
			{
				itr.remove();
			}
		}
	}

	/**
	 * Setter-Method for property {@link #matchType}. <br>
	 *
	 * @param matchType
	 *           The {@link #matchType} to set.
	 */
	public void setMatchType(final char matchType)
	{
		if (matchType == ALL || matchType == ONE)
		{
			this.matchType = matchType;
		}
		else
		{
			final String msg = "Invalid Matchtype " + matchType + " for QueryObject " + queryObjectName;
			final GenilBackendRuntimeException gnlBkndExc = new GenilBackendRuntimeException(msg);
			throw gnlBkndExc;
		}
	}

	/**
	 * Setter-Method for property {@link #maxHits}. <br>
	 *
	 * @param max_hits
	 *           The {@link #maxHits} to set.
	 */
	public void setMaxHits(final int max_hits)
	{
		this.maxHits = max_hits < 0 ? 0 : max_hits;
	}

	/**
	 * Returns a textual presentation of this query,
	 *
	 * @return String describing this query
	 */
	@Override
	public String toString()
	{
		final StringBuilder str = new StringBuilder();
		str.append(queryObjectName);
		str.append(" with max hits: ");
		str.append(maxHits);
		str.append(" | Query Params: (name, sign, option, low, high)\n");
		str.append(parameters.toString());
		return str.toString();
	}
}