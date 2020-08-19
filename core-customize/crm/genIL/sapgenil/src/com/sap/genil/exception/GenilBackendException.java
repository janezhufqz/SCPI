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

import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * Thrown if something in the GenIL Connectivity Framework goes wrong.<br>
 * Typically indicates a violation of the framework interfaces. Please refer to the Java Doc of throwing class/interface
 * for details.
 *
 * @author SAP
 * @version 1.0
 */
@java.lang.SuppressWarnings("squid:S2166")
public class GenilBackendException extends BackendException
{

	private static final long serialVersionUID = 7332681956070526453L;

	/**
	 * Standard constructor. <br>
	 *
	 * @param msg
	 */
	public GenilBackendException(final String msg)
	{
		super(msg);
	}

	/**
	 * Standard constructor. <br>
	 *
	 * @param msg
	 * @param rootCause
	 */
	public GenilBackendException(final String msg, final Throwable rootCause)
	{
		super(msg, rootCause);
	}

}
