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

import com.sap.genil.GenilConst;


/**
 * Implements a Name Value pair
 *
 * @author SAP
 * @version 1.0
 */
public class NameValuePair
{
	/**
	 * Name of the Name Value pair
	 */
	private final String name;

	/**
	 * Value of the Name Value Pair
	 */
	private Object value;

	/**
	 * Standard constructor
	 *
	 * @param name
	 * @param value
	 */
	public NameValuePair(final String name, final Object value)
	{
		this.name = name;
		this.value = value;
	}

	public void setValue(final Object value)
	{
		this.value = value;
	}

	/**
	 * Getter-Method for property {@link #name}. <br>
	 *
	 * @return Returns the {@link #name}.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Getter-Method for property {@link #value}. <br>
	 *
	 * @return returns the string representation of the value, if the object is character based or GenilConst.ABAP_TRUE
	 *         or GenilConst.ABAP_FALSE if the object is boolean or the object itself in all other cases
	 */
	public Object getValue()
	{
		if (value instanceof Character)
		{
			return String.valueOf(value);
		}
		else if (value instanceof Boolean)
		{
			return (Boolean) value ? GenilConst.ABAP_TRUE : GenilConst.ABAP_FALSE;
		}
		return value;
	}

	public boolean compareToNVP(final String name, final Object value)
	{
		if (value instanceof Character)
		{
			return name.compareTo(this.name) == 0 && String.valueOf(value).contentEquals(String.valueOf(this.value));
		}
		else if (value instanceof Boolean)
		{
			return name.compareTo(this.name) == 0 && (Boolean) value == (Boolean) this.value;
		}
		else
		{
			return name.compareTo(this.name) == 0 && value == this.value;
		}
	}

	@Override
	public String toString()
	{
		return name + "=" + value;
	}
}
