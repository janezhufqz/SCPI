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

import com.sap.genil.GenilConst.GenilMessageType;


public class GenilMessageRequestContainer
{

	private boolean globalMessages;
	private boolean objectMessages;

	private GenilMessageType msgType;
	private char msgLevel;

	private boolean forDisplay;

	protected GenilMessageRequestContainer()
	{
		super();
	}

	public String getMsgLevel()
	{
		final char[] data = new char[1];
		data[0] = this.msgLevel;
		return new String(data);
	}

	public GenilMessageType getMsgType()
	{
		return msgType;
	}

	public boolean isForDisplay()
	{
		return forDisplay;
	}

	public boolean isGlobalMessages()
	{
		return globalMessages;
	}

	public boolean isObjectMessages()
	{
		return objectMessages;
	}

	public void setForDisplay(final boolean forDisplay)
	{
		this.forDisplay = forDisplay;
	}

	public void setGlobalMessages(final boolean globalMessages)
	{
		this.globalMessages = globalMessages;
	}

	public void setMsgLevel(final char msgLevel)
	{
		this.msgLevel = msgLevel;
	}

	public void setMsgType(final GenilMessageType msgType)
	{
		this.msgType = msgType;
	}

	public void setObjectMessages(final boolean objectMessages)
	{
		this.objectMessages = objectMessages;
	}

}
