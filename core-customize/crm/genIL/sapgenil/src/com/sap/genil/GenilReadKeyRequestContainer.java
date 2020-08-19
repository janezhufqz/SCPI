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

import java.util.Map;

import com.sap.genil.exception.GenilBackendException;
import com.sap.genil.exception.GenilBackendRuntimeException;



/**
 * This class models a read request for a GenIL object with a given runtime GUID. <br>
 * You can not instantiate this class. To build a read request create an instance of the
 * <code>GenilReadRootRequestContainer</code>. If you add request for a specific child, by providing a runtime GUID and
 * the child relation name, to any request container you will receive an instance of this class.
 *
 * @see com.sap.wec.tc.core.backend.genil.GenilReadRootRequestContainer
 * @author SAP
 * @version 1.0
 *
 * @author C5230248 -- Modified by Removing WCFLocation
 */
public class GenilReadKeyRequestContainer extends GenilReadAllRequestContainer
{


	/**
	 * Runtime GUID to be read.
	 */
	private final String rtGuid;

	/**
	 * Standard constructor. <br>
	 *
	 * @param parentCont
	 *           reference to the parent container
	 * @param parentRel
	 *           GenIL relation from the parent
	 * @param rtGuid
	 *           runtime GUID of the child to be read
	 * @throws GenilBackendException
	 */
	protected GenilReadKeyRequestContainer(final GenilReadAllRequestContainer parentCont, final String parentRel,
			final String rtGuid, final GenilProxy proxy) throws GenilBackendException
	{
		super(parentCont, parentRel, proxy);
		this.rtGuid = rtGuid;
		this.setKeySet(true);
	}

	/**
	 * This method adds the request for a specific child to the container. This child is identified by the relation from
	 * the parent that points to this child and the runtime GUID of this child.<br>
	 * The returned RequestConatiner can be used for further processing, eg. to add a request to read another child of
	 * the requested child.
	 *
	 * @param relName
	 *           GenIL name of the relation to read
	 * @param rtGuid
	 *           RuntimeGUID of the related Object
	 * @return RequestContainer representing the specified child request
	 */
	public GenilReadKeyRequestContainer addChildRel(final String relName, final String rtGuid)
	{
		// check if relation is valid
		if (isRelationInModel(relName))
		{
			// check if such an child already exists first guid then name
			final Map<String, GenilReadAllRequestContainer> children = getChilds();
			if (children.containsKey(rtGuid + relName))
			{
				final String msg = "Request to read childs via Relation " + relName + " already exists unknown";
				final GenilBackendRuntimeException gnlBkndExc = new GenilBackendRuntimeException(msg);
				throw gnlBkndExc;
			}
			GenilReadKeyRequestContainer child;
			try
			{
				child = new GenilReadKeyRequestContainer(this, relName, rtGuid, proxy);
			}
			catch (final GenilBackendException e)
			{
				final String msg = "Error determining child for add [relName = " + relName + ", rtGuid = " + rtGuid + "]";
				final GenilBackendRuntimeException gnlBkndRunExc = new GenilBackendRuntimeException(msg, e);
				throw gnlBkndRunExc;
			}
			addChild(child, rtGuid);
			return child;
		}
		else
		{
			final String msg = "Relation " + relName + " is unknown in Model for Object " + getEntityName();
			final GenilBackendRuntimeException gnlBkndExc = new GenilBackendRuntimeException(msg);
			throw gnlBkndExc;
		}
	}

	/**
	 * This methods returns the path from the root object as a StringBuffer, using the following syntax<br>
	 * <code>RootObjectName[RuntimeGUID]\\RealationName:ObjectName[RuntimeGUID]</code>
	 *
	 * @return The Path from the root object as StringBuffer
	 */
	@Override
	public StringBuilder getPathAsString()
	{
		final StringBuilder path = getParent().getPathAsString();
		path.append("\\");
		path.append(getParentRelName());
		path.append(":");
		path.append(getEntityName());
		path.append("[");
		path.append(rtGuid);
		path.append("]");
		return path;
	}

	/**
	 * Return the property {@link #rtGuid}. <br>
	 *
	 * @return Returns the {@link #rtGuid}.
	 */
	public String getRtGuid()
	{
		return rtGuid;
	}

	/**
	 * Overwrites the super implementation.<br>
	 * Returns a User-friendly String representation of this container<br>
	 *
	 * @return User-Friendly textual representation of this object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuilder string = new StringBuilder();
		string.append("Request for object '");
		string.append(getEntityName());
		string.append("' with rtGUID: ");
		string.append(getRtGuid());
		string.append(" | Lookup path from root: \n");
		string.append(getPathAsString());
		return string.toString();

	}

}