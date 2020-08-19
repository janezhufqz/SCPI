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

import com.sap.genil.exception.GenilBackendRuntimeException;


/**
 * This class represents a GenIL Attribute, which is identified via an attribute name. It has a value, that can be
 * changed on Java Side. The GenIL property, is read-only, it contains technical information about the field which is
 * set by GenIL.
 *
 * @author SAP
 * @version 1.0
 *
 * @author C5230248 -- Modified by Removing WCFLocation
 */
public class GenilAttribute
{
	public static final char READ_ONLY = 'R';
	public static final char CHANGEABLE = 'C';
	public static final char BLANK = 'I';
	public static final char NOT_DEFINED = 'N';
	public static final char HIDDEN = 'H';
	public static final char REQUIRED = 'M';
	public static final char CHANGED = 'A';
	public static final char TECHNICAL_FIELD = 'T';
	public static final char DEACTIVATED = 'D';


	/**
	 * GenIL name of the attribute, can not be changed
	 */
	private String name;
	/**
	 * Attribute value
	 */
	private String value;
	/**
	 * GenIL property of the Attribute
	 */
	private char property;
	/**
	 * Indicates if the attribute was updated
	 */
	private boolean updated;

	/**
	 * Standard constructor. <br>
	 *
	 * @param name
	 *           GenIL name
	 * @param value
	 *           attribute value
	 */
	protected GenilAttribute(final String name, final String value)
	{
		this(name, value, NOT_DEFINED);
	}

	/**
	 * Standard constructor. <br>
	 *
	 * @param name
	 *           GenIL name
	 * @param value
	 *           attribute value
	 * @param property
	 *           GenIL property, has to correspond to one of the constants
	 */
	@java.lang.SuppressWarnings({"squid:S1067","squid:MethodCyclomaticComplexity"})
	protected GenilAttribute(final String name, final String value, final char property)
	{
		super();
		this.name = name;
		this.value = value;
		this.property = property;
		this.updated = false;

		if (property == READ_ONLY || property == CHANGEABLE || property == BLANK || property == NOT_DEFINED || property == HIDDEN
				|| property == REQUIRED || property == CHANGED || property == TECHNICAL_FIELD || property == DEACTIVATED)
		{
			return;
		}
		final String msg = "Property " + property + " of Attribute " + name + " not supported";
		final GenilBackendRuntimeException gnlBkndExc = new GenilBackendRuntimeException(msg);
		throw gnlBkndExc;
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
	 * Getter-Method for property {@link #property}. <br>
	 *
	 * @return Returns the {@link #property}.
	 */
	public char getProperty()
	{
		return property;
	}



	/**
	 * Getter-Method for property {@link #value}. <br>
	 *
	 * @return Returns the {@link #value}.
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * This method checks if attribute can be changed based on the attribute properties. However it takes not into
	 * consideration if the container this attribute belongs to is locked. For this purpose the container itself provides
	 * a method.<br>
	 * In particular <code>true</code> is only returned if <code>property != READ_ONLY</code>
	 *
	 * @see com.sap.genil.GenilDataContainer#isLocked()
	 * @return </code>true<code> if the attribute can be changed
	 */
	public boolean isChangeable()
	{
		return property != READ_ONLY ? true : false;
	}

	/**
	 * Getter-Method for property {@link #updated}. <br>
	 *
	 * @return Returns the {@link #updated}.
	 */
	public boolean isUpdated()
	{
		return updated;
	}

	/**
	 * Setter-Method for property {@link #updated}. <br>
	 *
	 * @param updated
	 *           The {@link #updated} to set.
	 */
	protected void setUpdated(final boolean updated)
	{
		this.updated = updated;
	}

	/**
	 * Setter-Method for property {@link #value}. <br>
	 * does not set the updates flag to True
	 *
	 * @param value
	 *           The {@link #value} to set.
	 */
	protected void setPropertyIntern(final char property)
	{
		if (this.property != property)
		{
			this.property = property;
		}
	}

	/**
	 * Setter-Method for property {@link #}. <br>
	 * does not set the updates flag to True
	 *
	 * @param value
	 *           The {@link #value} to set.
	 */
	protected void setValueIntern(final String value)
	{
		if (this.value == null || !this.value.equals(value))
		{
			this.value = value;
		}
	}

	/**
	 * Setter-Method for property {@link #}. <br>
	 *
	 * @param value
	 *           The {@link #value} to set.
	 */
	protected void setValue(final String value)
	{
		if (!this.value.equals(value))
		{
			this.value = value;
			this.updated = true;
		}
	}

	/**
	 * Returns a textual presentation of this attribute, using the following template.<br>
	 * <code>(name = value)</code>
	 *
	 * @return String describing this attribute
	 */
	@Override
	public String toString()
	{
		final StringBuilder str = new StringBuilder("( ");
		str.append(name);
		str.append(" = ");
		str.append(value);
		str.append(" )");
		return str.toString();
	}

}
