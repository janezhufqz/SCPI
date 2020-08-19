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

import com.sap.genil.exception.GenilBackendException;



/**
 * This class models a read request for an GenIL root Object.<br>
 * GenIL objects are organized in a tree. If you want to read any object from GenIL, you have always to start at the
 * root. So to generate a new read request create an instance of this class.<br>
 * By calling the <code>addChild()</code> method you can extend the request to also read the specified children.<br>
 * To create an instance you need to provide a runtimeGUID. Typically you obtain this GUID by querying for the root
 * object using the <code>GenilQueryRequestContainer</code>.<br>
 *
 * @see com.sap.genil.GenilQueryRequestContainer
 * @author SAP
 * @version 1.0
 *
 * @author C5230248 -- Modified by Removing WCFLocation
 */
public class GenilReadRootRequestContainer extends GenilReadKeyRequestContainer
{


	/**
	 * Standard constructor. <br>
	 *
	 * @param rootName
	 *           GenIL name of the root object
	 * @param rtGuid
	 *           runtime GUID of the root object
	 * @throws GenilBackendException
	 *            f the rootObject is unknown to the model
	 */
	protected GenilReadRootRequestContainer(final String rootName, final String rtGuid, final GenilProxy proxy)
			throws GenilBackendException
	{
		super(null, null, rtGuid, proxy);
		this.setIsRoot(true);
		this.setEntityName(rootName);
		if (getModelObject() == null)
		{
			final String msg = "The object " + getEntityName() + " is unknown in Model";

			final GenilBackendException gnlBkndExc = new GenilBackendException(msg);

			throw gnlBkndExc;
		}
	}

	/**
	 * Overwrites the super implementation.<br>
	 * Root specific implementation. The returned StringBuffer has the form <code>RootObjectName[RuntimeGuid]</code>.<br>
	 *
	 * @return A StringBuffer containing root object name and runtime GUID
	 * @see com.sap.genil.GenilDataContainer#getPathAsString()
	 */
	@Override
	public StringBuilder getPathAsString()
	{
		final StringBuilder path = new StringBuilder();
		path.append(getEntityName());
		path.append("[");
		path.append(getRtGuid());
		path.append("]");
		return path;
	}

	/**
	 * Returns the RootdataContainer that is the root of this DataContainer
	 *
	 * @return Returns the RootdataContainer that is the root of this DataContainer
	 */
	@Override
	public GenilReadRootRequestContainer getRoot()
	{
		return this;
	}
}
