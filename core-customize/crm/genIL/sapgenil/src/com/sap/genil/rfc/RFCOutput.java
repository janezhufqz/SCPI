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

import java.util.Map;

import com.sap.conn.jco.JCoTable;
import com.sap.genil.GenilConst.GenilRFC;


/**
 * Input parameters for RFC call. The class provides several functions to define the output parameters requested of an
 * RFC function module call
 *
 * @author SAP
 * @version 1.0
 */

public class RFCOutput extends RFCInOutput<JCoTable>
{
	/**
	 * flag that indicates if the RFC call was successful
	 */
	private boolean success;

	/**
	 * Standard constructor, genilRFC is mandatory and must not be null
	 * 
	 * @param genilRFC
	 */
	public RFCOutput(final GenilRFC genilRFC)
	{
		super(genilRFC);
	}

	/**
	 * Adds a name value pair as export parameter
	 * 
	 * @param name
	 *           name of the name value pair
	 * @param value
	 *           value of the name value pair
	 */
	public void addExportParameter(final String name, final Object value)
	{
		super.addParameter(name, value);
	}

	/**
	 * Getter-Method for export parameters map
	 * 
	 * @return Returns the export parameters.
	 */
	public Map<String, NameValuePair> getExportParameters()
	{
		return super.getParameters();
	}

	/**
	 * Getter-Method for property {@link #objecsuccesstId}. <br>
	 * 
	 * @return Returns the {@link #success}.
	 */
	public boolean isSuccess()
	{
		return success;
	}


	/**
	 * Setter-Method for property success. <br>
	 * 
	 * @param success
	 *           sets the property success
	 */
	public void setSuccess(final boolean success)
	{
		this.success = success;
	}
}
