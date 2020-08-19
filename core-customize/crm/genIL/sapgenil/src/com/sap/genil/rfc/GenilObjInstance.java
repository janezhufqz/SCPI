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

/**
 * Representation of an Genil object in Java. For each root object read or created a GenilObjInstance will be created
 * which held its objectName, ID and whether the ID is an handle or not
 *
 * @author SAP
 * @version 1.0
 */
public class GenilObjInstance
{

	/**
	 * Name of the object instance
	 */
	private String objectName;

	/**
	 * ID of the object instance
	 */
	private String objectId;

	/**
	 * describes if the object is an handle or not
	 */
	private boolean idIsHandle = false;

	/**
	 * Standard constructor
	 * 
	 * @param objectName
	 * @param objectId
	 */
	public GenilObjInstance(final String objectName, final String objectId)
	{
		super();
		this.objectName = objectName;
		this.objectId = objectId;
	}


	/**
	 * Getter-Method for property {@link #objectId}. <br>
	 * 
	 * @return Returns the {@link #objectId}.
	 */
	public String getObjectId()
	{
		return objectId;
	}

	/**
	 * Getter-Method for property {@link #objectName}. <br>
	 * 
	 * @return Returns the {@link #objectName}.
	 */
	public String getObjectName()
	{
		return objectName;
	}

	/**
	 * Getter-Method for property IsHandle. <br>
	 * 
	 * @return Returns whether the ID is an handle or not.
	 */
	public boolean idIsHandle()
	{
		return idIsHandle;
	}

	/**
	 * Setter-Method for property isHandle. <br>
	 * 
	 * @param idIsHandle
	 *           Sets whether ID is an Handle or not.
	 */
	public void setIdIsHandle(final boolean idIsHandle)
	{
		this.idIsHandle = idIsHandle;
	}

	/**
	 * Setter-Method for property {@link #objectId}. <br>
	 * 
	 * @param idIsHandle
	 *           The {@link #objectId} to set.
	 */
	public void setObjectId(final String objectId)
	{
		this.objectId = objectId;
	}

	/**
	 * Setter-Method for property {@link #objectName}. <br>
	 * 
	 * @param idIsHandle
	 *           The {@link #objectName} to set.
	 */
	public void setObjectName(final String objectName)
	{
		this.objectName = objectName;
	}

	@Override
	public String toString()
	{
		return "[" + objectName + ": " + objectId + "]";
	}

}
