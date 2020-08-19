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
import java.util.Collection;
import java.util.List;

import com.sap.genil.GenilConst.GenilMessageType;


/**
 * This class models a GenIL message. It is internally organised in the same way as GenIL.<br>
 * It provides methods to create and access Messages similar to CRM BOL
 *
 * @author SAP
 * @version 1.0
 */

public class GenilMessage
{

	/**
	 * Message Type
	 */
	private GenilMessageType type;
	/**
	 * Message ID
	 */
	private String id;
	/**
	 * Message Number
	 */
	private int number;
	/**
	 * Message Text
	 */
	private String message;
	/**
	 * Message Attribute 1
	 */
	private final String msgVar1;
	/**
	 * Message Attribute 2
	 */
	private final String msgVar2;
	/**
	 * Message Attribute 3
	 */
	private final String msgVar3;
	/**
	 * Message Attribute 4
	 */
	private final String msgVar4;
	/**
	 * Show message only once
	 */
	private boolean showOnce;
	/**
	 * Message Level
	 */
	private char msgLevel;
	/**
	 * Object name
	 */
	private String objectName;
	/**
	 * Attribute Name
	 */
	private String attrName;
	/**
	 * Object GUID
	 */
	private String rtGUID;

	/**
	 * Standard constructor. <br>
	 *
	 * @param type
	 *           Message type
	 * @param id
	 *           Message ID
	 * @param number
	 *           Message Number
	 * @param message
	 *           Message Text
	 * @param msgVars
	 *           Array list of Message Parameters
	 * @param showOnce
	 *           Message only displayed once
	 * @param msgLevel
	 *           Message Level
	 */
	public GenilMessage(final GenilMessageType type, final String id, final int number, final String message,
			final List<String> msgVars, final boolean showOnce, final char msgLevel)
	{
		this(type, id, number, message, msgVars.get(0), msgVars.get(1), msgVars.get(2), msgVars.get(3), showOnce, msgLevel, null,
				null, null);
	}

	/**
	 * Standard constructor. <br>
	 *
	 * @param type
	 *           Message type
	 * @param id
	 *           Message ID
	 * @param number
	 *           Message Number
	 * @param message
	 *           Message Text
	 * @param msgVars
	 *           Array list of Message Parameters
	 * @param showOnce
	 *           Message only displayed once
	 * @param msgLevel
	 *           Message Level
	 * @param objectName
	 *           Name of the Object
	 * @param attrName
	 *           Name of the Attribute
	 * @param rtGUID
	 *           Guid of the Object
	 */
	@java.lang.SuppressWarnings("squid:S00107")
	public GenilMessage(final GenilMessageType type, final String id, final int number, final String message,
			final List<String> msgVars, final boolean showOnce, final char msgLevel, final String objectName, final String attrName,
			final String rtGUID)
	{
		this(type, id, number, message, msgVars.get(0), msgVars.get(1), msgVars.get(2), msgVars.get(3), showOnce, msgLevel,
				objectName, attrName, rtGUID);
	}

	/**
	 * Standard constructor. <br>
	 *
	 * @param type
	 *           Message type
	 * @param id
	 *           Message ID
	 * @param number
	 *           Message Number
	 * @param message
	 *           Message Text
	 * @param msgVar1
	 *           Message Parameters 1
	 * @param msgVar2
	 *           Message Parameters 2
	 * @param msgVar3
	 *           Message Parameters 3
	 * @param msgVar4
	 *           Message Parameters 4
	 * @param showOnce
	 *           Message only displayed once
	 * @param msgLevel
	 *           Message Level
	 */
	@java.lang.SuppressWarnings("squid:S00107")
	public GenilMessage(final GenilMessageType type, final String id, final int number, final String message,
			final String msgVar1, final String msgVar2, final String msgVar3, final String msgVar4, final boolean showOnce,
			final char msgLevel)
	{
		this(type, id, number, message, msgVar1, msgVar2, msgVar3, msgVar4, showOnce, msgLevel, null, null, null);
	}

	/**
	 * Standard constructor. <br>
	 *
	 * @param type
	 *           Message type
	 * @param id
	 *           Message ID
	 * @param number
	 *           Message Number
	 * @param message
	 *           Message Text
	 * @param msgVar1
	 *           Message Parameters 1
	 * @param msgVar2
	 *           Message Parameters 2
	 * @param msgVar3
	 *           Message Parameters 3
	 * @param msgVar4
	 *           Message Parameters 4
	 * @param showOnce
	 *           Message only displayed once
	 * @param msgLevel
	 *           Message Level
	 * @param objectName
	 *           Name of the Object
	 * @param attrName
	 *           Name of the Attribute
	 * @param rtGUID
	 *           Guid of the Object
	 */
	@java.lang.SuppressWarnings("squid:S00107")
	public GenilMessage(final GenilMessageType type, final String id, final int number, final String message,
			final String msgVar1, final String msgVar2, final String msgVar3, final String msgVar4, final boolean showOnce,
			final char msgLevel, final String objectName, final String attrName, final String rtGUID)
	{
		super();
		this.type = type;
		this.id = id;
		this.number = number;
		this.message = message;
		this.msgVar1 = msgVar1;
		this.msgVar2 = msgVar2;
		this.msgVar3 = msgVar3;
		this.msgVar4 = msgVar4;
		this.showOnce = showOnce;
		this.msgLevel = msgLevel;
		this.objectName = objectName;
		this.attrName = attrName;
		this.rtGUID = rtGUID;
	}

	public String getAttrName()
	{
		return this.attrName;
	}

	/**
	 * ID of the Message
	 *
	 * @return
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * Message Text of the Message
	 *
	 * @return
	 */
	public String getMessage()
	{
		return this.message;
	}

	/**
	 * Collection of Attributes of the Message
	 *
	 * @return
	 */
	public Collection<String> getMessageVariables()
	{
		final List<String> msgVars = new ArrayList<String>();
		msgVars.set(0, msgVar1);
		msgVars.set(1, msgVar2);
		msgVars.set(2, msgVar3);
		msgVars.set(3, msgVar4);
		return msgVars;
	}

	/**
	 * Getter-Method for property {@link #msgLevel}. <br>
	 *
	 * @return Returns the {@link #msgLevel}.
	 */
	public char getMsgLevel()
	{
		return this.msgLevel;
	}

	/**
	 * Getter-Method for property {@link #number}. <br>
	 *
	 * @return Returns the {@link #number}.
	 */
	public int getNumber()
	{
		return this.number;
	}

	/**
	 * Getter-Method for property {@link #objectName}. <br>
	 *
	 * @return Returns the {@link #objectName}.
	 */
	public String getObjectName()
	{
		return this.objectName;
	}

	/**
	 * Getter-Method for property {@link #rtGUID}. <br>
	 *
	 * @return Returns the {@link #rtGUID}.
	 */
	public String getRtGUID()
	{
		return this.rtGUID;
	}

	/**
	 * Getter-Method for property {@link #type}. <br>
	 *
	 * @return Returns the {@link #type}.
	 */
	public GenilMessageType getType()
	{
		return this.type;
	}

	/**
	 * Getter-Method for property {@link #showOnce}. <br>
	 *
	 * @return Returns the {@link #showOnce}.
	 */
	public boolean isShowOnce()
	{
		return this.showOnce;
	}

	/**
	 * Setter-Method for property {@link #attrName}. <br>
	 *
	 * @param attrName
	 *           Sets the {@link #attrName}.
	 */
	public void setAttrName(final String attrName)
	{
		this.attrName = attrName;
	}

	/**
	 * Setter-Method for property {@link #id}. <br>
	 *
	 * @param id
	 *           Sets the {@link #id}.
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * Setter-Method for property {@link #message}. <br>
	 *
	 * @param message
	 *           Sets the {@link #message}.
	 */
	public void setMessage(final String message)
	{
		this.message = message;
	}

	/**
	 * Setter-Method for property {@link #msgLevel}. <br>
	 *
	 * @param msgLevel
	 *           Sets the {@link #msgLevel}.
	 */
	public void setMsgLevel(final char msgLevel)
	{
		this.msgLevel = msgLevel;
	}

	/**
	 * Setter-Method for property {@link #number}. <br>
	 *
	 * @param number
	 *           Sets the {@link #number}.
	 */
	public void setNumber(final int number)
	{
		this.number = number;
	}

	/**
	 * Setter-Method for property {@link #objectName}. <br>
	 *
	 * @param objectName
	 *           Sets the {@link #objectName}.
	 */
	public void setObjectName(final String objectName)
	{
		this.objectName = objectName;
	}

	/**
	 * Setter-Method for property {@link #rtGUID}. <br>
	 *
	 * @param rtGUID
	 *           Sets the {@link #rtGUID}.
	 */
	public void setRtGUID(final String rtGUID)
	{
		this.rtGUID = rtGUID;
	}

	/**
	 * Setter-Method for property {@link #showOnce}. <br>
	 *
	 * @param showOnce
	 *           Sets the {@link #showOnce}.
	 */
	public void setShowOnce(final boolean showOnce)
	{
		this.showOnce = showOnce;
	}

	/**
	 * Setter-Method for property {@link #type}. <br>
	 *
	 * @param type
	 *           Sets the {@link #type}.
	 */
	public void setType(final GenilMessageType type)
	{
		this.type = type;
	}

	@Override
	public String toString()
	{
		return "[Msg: " + this.id + "," + this.message + "]\n";
	}

}
