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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.genil.exception.GenilBackendException;
import com.sap.genil.exception.GenilBackendRuntimeException;



/**
 * This class models a read request for all children of GenIL object with a the child relation name.<br>
 * You can not instantiate this class. To build a read request create an instance of the
 * <code>GenilReadRootRequestContainer</code>. If you add request for all children, by providing the child relation
 * name, to any request container you will receive an instance of this class.
 *
 * @see com.sap.wec.tc.core.backend.genil.GenilReadRootRequestContainer
 * @author SAP
 * @version 1.0
 *
 * @author C5230248 -- Modified by Removing WCFLocation
 */
public class GenilReadAllRequestContainer
{

	/**
	 * GenIL name of the relation that points from the parent to this container
	 */
	private final String parentRelName;
	/**
	 * GenIL object name of the entity to read
	 */
	private String entityName;
	/**
	 * Flag if this is a root request container
	 */
	private boolean isRoot;
	/**
	 * Flag if attributes should be read
	 */
	private boolean attrRequested;
	/**
	 * Flag if a key is set
	 */
	private boolean keySet;
	/**
	 * reference to the parent container
	 */
	private final GenilReadAllRequestContainer parent;

	/**
	 * List of children to be read
	 */
	private final Map<String, GenilReadAllRequestContainer> children;


	/**
	 * List of single requetsed attributes
	 */
	private final List<GenilModelAttr> requestedAttributes;

	protected GenilProxy proxy;

	/**
	 * Standard constructor. <br>
	 *
	 * @param parentCont
	 *           reference to the parent container
	 * @param parentRel
	 *           GenIL name of the relation that points from the parent to this container
	 * @throws GenilBackendException
	 */
	protected GenilReadAllRequestContainer(final GenilReadAllRequestContainer parentCont, final String parentRel,
			final GenilProxy proxy) throws GenilBackendException
	{

		children = new HashMap<String, GenilReadAllRequestContainer>();
		requestedAttributes = new ArrayList<GenilModelAttr>();
		this.parent = parentCont;
		this.parentRelName = parentRel;
		this.keySet = false;
		this.attrRequested = false;
		if (proxy == null)
		{
			final String msg = "Proxy must be provided";
			final GenilBackendException gnlBkndExc = new GenilBackendException(msg);
			throw gnlBkndExc;
		}
		this.proxy = proxy;

		// get Object name in case of root, parent is null, but then objectName
		// is set in constructor of GenilRootDataContainer
		if (parent != null)
		{
			entityName = parent.getModelObject().getRelation(parentRel).getObjectB().getObjectName();
		}
	}

	/**
	 * Adds the given request container as child to this container.<br>
	 *
	 * @param cont
	 *           container to add
	 */
	protected void addChild(final GenilReadAllRequestContainer cont)
	{
		final GenilReadAllRequestContainer parentreq = cont.parent;
		final GenilReadRootRequestContainer root = parentreq.getRoot();
		children.put(root.getRtGuid() + cont.getParentRelName(), cont);
	}

	/**
	 * Adds the given request container as child to this container.<br>
	 *
	 * @param cont
	 *           container to add
	 */
	protected void addChild(final GenilReadAllRequestContainer cont, final String rtGuid)
	{
		children.put(rtGuid + cont.getParentRelName(), cont);
	}

	/**
	 * This method adds the request for all children of given relation to the container. The children are identified by
	 * the relation from the parent that points to them.<br>
	 * The returned RequestConatiner can be used for further processing, eg. to add a request to read again children of
	 * the requested children.<br>
	 * Note that this method returns a container of type <code>GenILReadAllRequestContainer</code>. To a container of
	 * this kind you can only add request to read all children for a given relation name, but not to read a child with a
	 * specific runtimeG UID. If you want to read a specific child with a GUID all key of the parents have to be known.
	 *
	 * @param relName
	 *           GenIL name of the relation to read
	 * @return RequestContainer representing the specified child request
	 * @throws GenilBackendException
	 *            if the given relation name is not valid according to the model, or if a child request container with
	 *            such an relation already exists
	 */
	public GenilReadAllRequestContainer addChildRel(final String relName)
	{
		if (isRelationInModel(relName))
		{
			// check if such an child already exists
			if (!children.containsKey(this.getRoot().getRtGuid() + relName))
			{
				GenilReadAllRequestContainer child;
				try
				{
					child = new GenilReadAllRequestContainer(this, relName, proxy);
				}
				catch (final GenilBackendException e)
				{
					final String msg = "Error adding child relation [" + relName + "]";
					final GenilBackendRuntimeException gnlBkndRunExc = new GenilBackendRuntimeException(msg, e);
					throw gnlBkndRunExc;

				}
				addChild(child);
				return child;
			}
			else
			{
				final String msg = "Request to read childs via Relation " + relName + " already exists";

				final GenilBackendRuntimeException gnlBkndExc = new GenilBackendRuntimeException(msg);
				throw gnlBkndExc;
			}
		}
		else
		{
			final String msg = "Relation " + relName + " is unknown in Model for Object " + entityName;
			final GenilBackendRuntimeException gnlBkndExc = new GenilBackendRuntimeException(msg);
			throw gnlBkndExc;
		}
	}

	/**
	 * To query only selective attributes of an object
	 */
	public void addRequestedAttribute(final String name) throws GenilBackendRuntimeException
	{
		final GenilModelAttr attr = getModelObject().getAttrByName(name);
		if (attr == null)
		{
			final String msg = "Attribute " + name + " unknown in Model";
			final GenilBackendRuntimeException gnlBkndExc = new GenilBackendRuntimeException(msg);
			throw gnlBkndExc;
		}

		setAttrRequested(true);
		requestedAttributes.add(attr);
	}


	public Map<String, GenilReadAllRequestContainer> getChilds()
	{
		return Collections.unmodifiableMap(children);
	}

	// old version *more performant implementation (organize childs)*


	/**
	 * Return the property {@link #entityName}. <br>
	 *
	 * @return Returns the {@link #entityName}.
	 */
	public String getEntityName()
	{
		return entityName;
	}

	/**
	 * Utility Method to obtain the GenIL Model Object this container is associated to. This object provides design time
	 * information from GenIL Model.<br>
	 *
	 * @return GenilModelObject this container is associated to
	 */
	public GenilModelObject getModelObject()
	{
		return proxy.getModel().getObject(entityName);
	}

	/**
	 * Return the property {@link #parent}. <br>
	 *
	 * @return Returns the {@link #parent}.
	 */
	public GenilReadAllRequestContainer getParent()
	{
		return parent;
	}

	/**
	 * Getter-Method for property {@link #parentRelName}. <br>
	 *
	 * @return Returns the {@link #parentRelName}.
	 */
	public String getParentRelName()
	{
		return parentRelName;
	}

	/**
	 * This methods returns the path from the root object as a StringBuffer, using the following syntax<br>
	 * <code>RootObjectName[RuntimeGUID]\\RealationName:ObjectName[ALL]</code>
	 *
	 * @return The Path from the root object as StringBuffer
	 */
	public StringBuilder getPathAsString()
	{
		final StringBuilder path = parent.getPathAsString();
		path.append("\\");
		path.append(parentRelName);
		path.append(":");
		path.append(entityName);
		path.append("[ALL]");
		return path;
	}

	public List<GenilModelAttr> getRequestedAttributes()
	{
		return Collections.unmodifiableList(requestedAttributes);
	}

	/**
	 * Returns the RootReadRequestContainer that is the root of this ReadRequestContainer
	 *
	 * @return Returns the RootReadRequestContainer that is the root of this DataContainer
	 */
	public GenilReadRootRequestContainer getRoot()
	{
		return parent.getRoot();
	}

	/**
	 * Return the property {@link #attrRequested}. <br>
	 *
	 * @return Returns the {@link #attrRequested}.
	 */
	public boolean isAttrRequested()
	{
		return attrRequested;
	}

	/**
	 * Getter-Method for property {@link #keySet}. <br>
	 *
	 * @return Returns the {@link #keySet}.
	 */
	public boolean isKeySet()
	{
		return keySet;
	}

	public String isNoRelsRequestedStr()
	{
		if (isRelsRequested())
		{
			return GenilConst.ABAP_FALSE;
		}
		else
		{
			return GenilConst.ABAP_TRUE;
		}
	}

	/**
	 * Checks in the GenIL Model if there exits such an relation for the GenIL object this container is associated to.<br>
	 *
	 * @param relationName
	 *           Name of the Relation to check
	 * @return <code>true</code> only if the given relation is valid
	 */
	public boolean isRelationInModel(final String relationName)
	{
		if (getModelObject().getRelation(relationName) == null)
		{
			return false;
		}
		return true;
	}

	public boolean isRelsRequested()
	{
		return children.isEmpty();
	}

	/**
	 * Return the property {@link #isRoot}. <br>
	 *
	 * @return Returns the {@link #isRoot}.
	 */
	public boolean isRoot()
	{
		return isRoot;
	}

	/**
	 * Set the property {@link #attrRequested}. <br>
	 *
	 * @param attrRequested
	 *           The {@link #attrRequested} to set.
	 */
	public void setAttrRequested(final boolean attrRequested)
	{
		this.attrRequested = attrRequested;
	}

	/**
	 * Set the property {@link #entityName}. <br>
	 *
	 * @param entityName
	 *           The {@link #entityName} to set.
	 */
	protected void setEntityName(final String entityName)
	{
		this.entityName = entityName;
	}

	/**
	 * Set the property {@link #isRoot}. <br>
	 *
	 * @param isRoot
	 *           The {@link #isRoot} to set.
	 */
	protected void setIsRoot(final boolean isRoot)
	{
		this.isRoot = isRoot;
	}

	/**
	 * Setter-Method for property {@link #keySet}. <br>
	 *
	 * @param keySet
	 *           The {@link #keySet} to set.
	 */
	protected void setKeySet(final boolean keySet)
	{
		this.keySet = keySet;
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
		string.append("Request for all objects '");
		string.append(entityName);
		string.append("' | Lookup path from root: \n");
		string.append(getPathAsString());
		return string.toString();

	}
}