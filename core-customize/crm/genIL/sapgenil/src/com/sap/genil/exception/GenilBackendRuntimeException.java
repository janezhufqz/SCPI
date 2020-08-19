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

package com.sap.genil.exception;

import de.hybris.platform.sap.core.jco.exceptions.BackendRuntimeException;


/**
 * Thrown in the GenIL Connectivity Framework. <br>
 *
 * @author SAP
 * @version 1.0
 */
@java.lang.SuppressWarnings("squid:S2166")
public class GenilBackendRuntimeException extends BackendRuntimeException
{

	private static final long serialVersionUID = 5751461721006397925L;

	/**
	 * Standard constructor. <br>
	 * 
	 * @param msg
	 */
	public GenilBackendRuntimeException(final String msg)
	{
		super(msg);
	}

	/**
	 * Standard constructor. <br>
	 * 
	 * @param msg
	 * @param rootCause
	 */
	public GenilBackendRuntimeException(final String msg, final Throwable rootCause)
	{
		super(msg, rootCause);
	}

}