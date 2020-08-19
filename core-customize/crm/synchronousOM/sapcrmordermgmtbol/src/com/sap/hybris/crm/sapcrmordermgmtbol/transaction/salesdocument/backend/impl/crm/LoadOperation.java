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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm;

/**
 * Class to control the Load operation provided to CRM LORD API (FM CRM_LORD_LOAD)
 */
public class LoadOperation
{

	/** Create document */
	final static public String CREATE = "H";
	/** Load document in display mode */
	final static public String DISPLAY = "A";
	/** Load document in edit mode */
	final static public String EDIT = "V";

	private String op = null;
	private boolean changeable = false;

	/**
	 * @return the current Load operation
	 */
	public String getLoadOperation()
	{
		return this.op;
	}

	/**
	 * @param operation
	 *           The operation to use
	 */
	public void setLoadOperation(final String operation)
	{
		this.op = operation;
		if (operation.equals(LoadOperation.CREATE) || operation.equals(LoadOperation.EDIT))
		{
			this.changeable = true;
		}
		else
		{
			this.changeable = false;
		}
	}

	/**
	 * @return Depending on the set operation, a document is changeable (create or edit mode) or not (display mode)
	 */
	public boolean isChangeable()
	{
		return changeable;
	}

}
