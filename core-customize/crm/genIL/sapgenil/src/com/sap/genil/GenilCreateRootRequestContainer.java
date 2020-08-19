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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.sap.genil.exception.GenilBackendException;


/**
 * This class models a request to create a root object in the GenIL.<br>
 * If you want to create a root object together with dependent objects you have first to create a root request using
 * this class. Afterwards you create a root by calling the corresponding Proxy method and with this class as parameter,
 * which will return a root data container. You can then add the dependent objects to the root object and send a modify.<br>
 *
 * @see com.sap.genil.IGenilProxy
 * @author SAP
 * @version 1.0
 *
 * @author C5230248 -- Modified by Removing WCFLocation
 */
public class GenilCreateRootRequestContainer
{

	/**
	 * Default Number
	 */
	public static final int DEFAULT_NUMBER = 1;

	/**
	 * GenIL name of the root Object
	 */
	private final String rootName;
	/**
	 * List of attributes for the root
	 */
	private final Map<String, GenilAttribute> createParameters;
	/**
	 * The GenIL Proxy this container is connected to.
	 */
	private final GenilProxy proxy;

	/**
	 * Number of Objects to be created. Default =1
	 */
	private int number;

	/**
	 * Standard constructor. <br>
	 *
	 * @param rootName
	 *           Name of the root object to be created.
	 * @throws GenilBackendException
	 *            if the rootObject is unknown to the model
	 */
	protected GenilCreateRootRequestContainer(final String rootName, final GenilProxy proxy) throws GenilBackendException
	{
		super();
		if (proxy == null)
		{
			final String msg = "Proxy must be provided";
			final GenilBackendException gnlBkndExc = new GenilBackendException(msg);
			throw gnlBkndExc;
		}
		this.proxy = proxy;
		this.rootName = rootName;
		if (getModelObject() == null)
		{
			final String msg = "The object " + rootName + " is unknown in Model";
			final GenilBackendException gnlBkndExc = new GenilBackendException(msg);
			throw gnlBkndExc;
		}
		this.createParameters = new HashMap<String, GenilAttribute>();
		init();
	}

	/**
	 * Adds an GeniLAttribute to the container. Should be used to build the container after read.<br>
	 *
	 * @param attr
	 *           GenilAttribute to add
	 */
	public void addCreateParameter(final GenilAttribute attr)
	{
		final String name = attr.getName();
		createParameters.put(name, attr);
	}

	/**
	 * Adds the given name value pair as an GeniLAttribute to the container.<br>
	 *
	 * @param name
	 *           GenIL name of the Attribute
	 * @param value
	 *           String value of the attribute *
	 */
	public void addCreateParameter(final String name, final String value)
	{

		GenilAttribute attr = createParameters.get(name);
		if (attr == null)
		{
			attr = new GenilAttribute(name, value);
			addCreateParameter(attr);
		}
		else
		{
			attr.setValue(value);

		}
	}

	/**
	 * returns an unmodifiable list of all attributes, which is connected to this container.
	 *
	 * @return Returns a list of all attributes
	 */
	public Collection<GenilAttribute> getAllCreateParameters()
	{
		return Collections.unmodifiableCollection(createParameters.values());
	}

	/**
	 * Get a GenIL Attribute from the list.<br>
	 *
	 * @param name
	 *           of attribute to get
	 * @return GenILAttribute
	 */
	public GenilAttribute getCreateParameter(final String name)
	{
		return createParameters.get(name);
	}

	/**
	 * Utility Method to obtain the GenIL Model Object this container is associated to. This object provides design time
	 * information from GenIL Model.<br>
	 *
	 * @return GenilModelObject this container is associated to
	 */
	public GenilModelObject getModelObject()
	{
		return proxy.getModel().getObject(rootName);
	}

	/**
	 * Getter-Method for property {@link #number}. <br>
	 *
	 * @return Returns the {@link #number}.
	 */
	public int getNumber()
	{
		return number;
	}

	/**
	 * Getter-Method for property {@link #rootName}. <br>
	 *
	 * @return Returns the {@link #rootName}.
	 */
	public String getRootName()
	{
		return rootName;
	}

	/**
	 * resets to default values
	 */
	public void init()
	{
		createParameters.clear();
		this.number = DEFAULT_NUMBER;
	}

	/**
	 * Remove a GenIL Attribute from the list.<br>
	 *
	 * @param name
	 *           of attribute to remove
	 */
	public void removeCreateParameter(final String name)
	{
		createParameters.remove(name);
	}

	/**
	 * Setter-Method for property {@link #number}. <br>
	 * If number < 1, 1 is set
	 *
	 * @param number
	 *           The {@link #number} to set.
	 */
	public void setNumber(final int number)
	{
		this.number = number < 1 ? 1 : number;
	}

	/**
	 * Returns a textual presentation of this create root request,
	 *
	 * @return String describing this create root request
	 */
	@Override
	public String toString()
	{
		final StringBuilder str = new StringBuilder("Create root '");
		str.append(rootName);
		str.append("' with attributes: name = value\n");
		str.append(createParameters.values().toString());
		return str.toString();
	}

}
