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

import de.hybris.platform.sap.core.jco.exceptions.BackendException;

import java.util.HashMap;
import java.util.Map;

import com.sap.genil.exception.GenilBackendException;




/**
 * The GenilRopotDataCopntainer extends the GenilDataContainer. <br>
 * By definition it has no parent.
 *
 * @see com.sap.genil.GenilDataContainer
 * @author SAP
 * @version 1.0
 *
 * @author C5230248 -- Modified by Removing WCFLocation
 */
public class GenilRootDataContainer extends GenilDataContainer
{


	/**
	 * contains all objects in the object tree
	 */
	private Map<String, GenilDataContainer> allObjectsByRTGuid;

	/**
	 * Indicates if the root is locked in GenIL
	 */
	private boolean locked;

	/**
	 * The GenIL Proxy this container is connected to.
	 */
	private GenilProxy proxy;

	protected Map<String, GenilDataContainer> getAllObjectsByRTGuid()
	{
		return allObjectsByRTGuid;
	}

	// contains the last used root request container. This is required to
	// ensure that for reRead the same filters are used then for the
	// corresponding read
	private GenilReadRootRequestContainer lastGenilReadRootRequest;

	protected void resetDataContainer()
	{
		this.allObjectsByRTGuid = new HashMap<String, GenilDataContainer>();

	}

	protected void setAllObjectsByRTGuid(final Map<String, GenilDataContainer> allObjectsByRTGuid)
	{
		this.allObjectsByRTGuid = allObjectsByRTGuid;
	}

	/**
	 * This will create a new Root Container with delta Flag set to created.<br>
	 * To create a new Root Object either directly instantiate a <code>GenilCreateRootRequestContainer</code> or use the
	 * <code>getCreateRootRequest()</code> method to obtain a <code>GenilCreateRootRequestContainer</code> based on this
	 * container.
	 *
	 * @param rootObjectName
	 * @throws GenilBackendException
	 *            if the rootObject is unknown to the model
	 */
	protected GenilRootDataContainer(final String rootObjectName, final GenilProxy proxy) throws GenilBackendException
	{
		super(null, null);
		init(rootObjectName, proxy);

	}

	/**
	 * Standard constructor. <br>
	 *
	 * @param rootObjectName
	 *           GenIL Name of the RootObject
	 * @throws GenilBackendException
	 *            if the rootObject is unknown to the model
	 */
	protected GenilRootDataContainer(final String rootObjectName, final String rtGuid, final GenilProxy proxy,
			final boolean createNew) throws GenilBackendException
	{
		super(null, null, rtGuid);
		init(rootObjectName, proxy);
		if (createNew)
		{
			this.locked = true;
		}
	}

	/**
	 * Standard constructor. <br>
	 *
	 * @param rootObjectName
	 *           GenIL Name of the RootObject
	 * @throws GenilBackendException
	 *            if the rootObject is unknown to the model
	 */
	protected GenilRootDataContainer(final String rootObjectName, final String rtGuid, final GenilProxy proxy)
			throws GenilBackendException
	{
		super(null, null, rtGuid);
		init(rootObjectName, proxy);
	}

	/**
	 * Standard constructor. <br>
	 *
	 * @param rootObjectName
	 *           GenIL Name of the RootObject
	 * @param numberOfAttr
	 *           Expected number of Attributes
	 * @param numberOfRelTyps
	 *           Expected number of relation types
	 * @throws GenilBackendException
	 *            if the rootObject is unknown to the model
	 */
	protected GenilRootDataContainer(final String rootObjectName, final String rtGuid, final int numberOfAttr,
			final int numberOfRelTyps, final GenilProxy proxy) throws GenilBackendException
	{
		super(null, null, rtGuid, numberOfAttr, numberOfRelTyps);
		init(rootObjectName, proxy);
	}

	/**
	 * This method will construct a request, that creates exactly this root object.<br>
	 *
	 * @return a request that creates exactly this root object
	 * @throws GenilBackendException
	 */
	public GenilCreateRootRequestContainer getCreateRootRequest() throws GenilBackendException
	{

		final GenilCreateRootRequestContainer req = new GenilCreateRootRequestContainer(getObjectName(), proxy);
		return req;
	}

	/**
	 * Overwrites the super implementation.<br>
	 * Root specific implementation. The returned StringBuffer has the form <code>RootObjectName[RuntimeGuid]</code>.<br>
	 *
	 * @return A StringBuffer containing root object name and runtime GUID
	 * @see com.sap.wec.tc.core.backend.genil.GenilDataContainer#getPathAsString()
	 */
	@Override
	public StringBuilder getPathAsString()
	{
		final StringBuilder path = new StringBuilder();
		path.append(getObjectName());
		path.append("[");
		path.append(getRtGuid());
		path.append("]");
		return path;
	}

	/**
	 * Getter-Method for property {@link #proxy}. <br>
	 *
	 * @return Returns the {@link #proxy}.
	 */
	protected GenilProxy getProxy()
	{
		return proxy;
	}


	/**
	 * Getter-Method for property lastGenilReadRootRequest. <br>
	 *
	 * @return Returns the GenilReadRootRequestContainer used for this Rott data request.
	 */
	protected GenilReadRootRequestContainer getLastGenilReadRootRequest()
	{
		return this.lastGenilReadRootRequest;
	}

	/**
	 * Setter-Method for property lastGenilReadRootRequest. <br>
	 * During read the last used Root request container is set. This is required to allow a reRead with the same read
	 * attributes
	 */
	protected void setLastGenilReadRootRequest(final GenilReadRootRequestContainer lastGenilReadRootRequest)
	{
		this.lastGenilReadRootRequest = lastGenilReadRootRequest;
	}


	/**
	 * Overwrites the super implementation.<br>
	 * This method will create a requestContainer structure that models a request to re-read this root object with this
	 * GUID.<br>
	 * If you also want to re-read the attributes of this object, you have to set the flag attrRequested manually.<br>
	 *
	 * @return Request Container for re-read
	 * @throws GenilBackendException
	 *            if the container can not be constructed
	 * @see com.sap.wec.tc.core.backend.genil.GenilDataContainer#getRequestContainer()
	 */
	@Override
	public GenilReadRootRequestContainer getRequestContainer() throws BackendException
	{
		GenilReadRootRequestContainer req = null;
		req = proxy.getReadRequestContainer(getObjectName(), getRtGuid());
		return req;
	}

	/**
	 * A root can not have a parent, so <code>this</code> is returned.
	 *
	 * @return this
	 */
	@Override
	public GenilRootDataContainer getRoot()
	{
		return this;
	}

	/**
	 * Initializes this container<br>
	 *
	 * @param rootObjectName
	 *           GenIL Name of the RootObject
	 * @throws GenilBackendException
	 *            if the rootObject is unknown to the model
	 */
	private void init(final String rootObjectName, final GenilProxy proxy) throws GenilBackendException
	{
		if (proxy == null)
		{
			final String msg = "Proxy must be provided";

			final GenilBackendException gnlBkndExc = new GenilBackendException(msg);
			throw gnlBkndExc;
		}
		this.proxy = proxy;
		this.setIsRoot(true);
		this.setObjectName(rootObjectName);

		this.allObjectsByRTGuid = new HashMap<String, GenilDataContainer>();

		if (getModelObject() == null)
		{
			final String msg = "The object " + rootObjectName + " is unknown in Model";

			final GenilBackendException gnlBkndExc = new GenilBackendException(msg);
			throw gnlBkndExc;
		}
		locked = false;
	}

	/**
	 * Getter-Method for property {@link #locked}. <br>
	 *
	 * @return Returns the {@link #locked}.
	 */
	@Override
	public boolean isLocked()
	{
		return locked;
	}

	@Override
	protected void remove()
	{
		// just disable super impl.
	}

	/**
	 * Setter-Method for property {@link #locked}. <br>
	 *
	 * @param locked
	 *           The {@link #locked} to set.
	 */
	protected void setLocked(final boolean locked)
	{
		this.locked = locked;
	}
}
