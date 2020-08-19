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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.sap.genil.exception.GenilBackendException;


/**
 * container for Genil execute_bo_message call. provided by genilProxy.getExecBoMethodRequestContainer. contains all
 * information reqired for genil_bo_excecute_methode
 *
 * @author SAP
 *
 *
 * @author C5230248 -- Modified by Removing WCFLocation
 */

public class GenilExecuteRequestContainer
{

	private final GenilDataContainer cont;
	private final String methodName;
	private final GenilProxy proxy;

	private Map<String, String> parameters;

	/*
	 * Standard constructor *
	 */

	GenilExecuteRequestContainer(final GenilDataContainer cont, final String methodName, final GenilProxy proxy)
			throws GenilBackendException
	{
		this.cont = cont;
		this.methodName = methodName;
		if (this.cont == null || this.methodName == null)
		{
			final String msg = "Invalid Parameters";
			final GenilBackendException gnlBkndExc = new GenilBackendException(msg);
			throw gnlBkndExc;
		}

		if (proxy == null)
		{
			final String msg = "Proxy must be provided";
			final GenilBackendException gnlBkndExc = new GenilBackendException(msg);
			throw gnlBkndExc;
		}
		this.proxy = proxy;

		final GenilModelObject modelObject = getModelObject();
		if (modelObject == null || modelObject.getMethod(methodName) == null)
		{
			final String msg = "Method not supported for object";
			final GenilBackendException gnlBkndExc = new GenilBackendException(msg);
			throw gnlBkndExc;
		}
	}

	/**
	 * Add a execute parameter (name value pair) to the execute container.
	 *
	 * @param name
	 *           Name of the Parameter forwarded to execute_bo_methode
	 * @param value
	 *           vlau of the parameter
	 */
	public void addParameters(final String name, final String value)
	{
		if (parameters == null)
		{
			parameters = new HashMap<String, String>();
		}
		parameters.put(name, value);
	}

	/**
	 * returns the genil data container
	 *
	 * @return
	 */
	public GenilDataContainer getGenilDataContainer()
	{
		return cont;
	}

	/**
	 * returnss the methode name
	 *
	 * @return
	 */
	public String getMethodName()
	{
		return methodName;
	}

	/**
	 * returns the the object model
	 *
	 * @return
	 */
	public GenilModelObject getModelObject()
	{
		return proxy.getModel().getObject(cont.getObjectName());
	}

	/**
	 * returns a collection of execute parameters
	 *
	 * @return
	 */
	public Map<String, String> getParameters()
	{
		if (parameters == null)
		{
			return Collections.emptyMap();
		}
		return parameters;
	}

}
