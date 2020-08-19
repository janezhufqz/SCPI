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

import java.util.Locale;

import com.sap.genil.exception.GenilBackendRuntimeException;


/**
 * This class models the parameter structure used to specify a single parameter for a GenILQuery.<br>
 * While parameter name, low and high value are strings without any restrictions, sign and options can only have certain
 * values. This class provides constants to uses as sign and options. If you specify an invalid sign or option this will
 * lead to a runtime exception.
 *
 * @author SAP
 * @version 1.0
 *
 * @author C5230248 -- Modified by Removing WCFLocation
 */
public class GenilQueryParameter
{
	/**
	 * Result including condition
	 */
	public static final char INCLUDE = 'I';
	/**
	 * Result excluding condition
	 */
	public static final char EXCLUDE = 'E';
	/**
	 * equals to low value
	 */
	public static final String EQUAL = "EQ";
	/**
	 * not equals to low value
	 */
	public static final String NOT_EQUAL = "NE";
	/**
	 * is between low and high value
	 */
	public static final String BETWEEN = "BT";
	/**
	 * is outside low or high value (not between low and high value)
	 */
	public static final String OUTSIDE = "NB";
	/**
	 * contains pattern
	 */
	public static final String CONTAINS_PATTERN = "CP";
	/**
	 * not contains pattern
	 */
	public static final String NOT_CONTAINS_PATTERN = "NP";
	/**
	 * is less than low value
	 */
	public static final String LESS = "LT";
	/**
	 * is less or equal than low value
	 */
	public static final String LESS_EQUAL = "LE";
	/**
	 * is greater than low value
	 */
	public static final String GREATER = "GT";
	/**
	 * is greater or equal than low value
	 */
	public static final String GREATER_EQUALS = "GE";
	/**
	 * Empty value (low and high)
	 */
	public static final String EMPTY_VALUE = "";

	private static final String SEP = ", ";

	/**
	 * GenIL name of the parameter
	 */
	private String parameterName;
	/**
	 * GenIL query sign (either include or exclude)
	 */
	private char sign;
	/**
	 * GenIL query option
	 */
	private String option;
	/**
	 * low value
	 */
	private String low;

	/**
	 * high value
	 */
	private String high;

	/**
	 * Standard constructor. Will set default values for sign and option, so that <code>sign = INCLUDE</code> and
	 * <code>option = CONTAINS_PATTERN</code>.<br>
	 *
	 * @param parameterName
	 *           name of the GenIL parameter, not allowed to be empty or null
	 */
	public GenilQueryParameter(final String parameterName)
	{
		super();
		this.sign = INCLUDE;
		this.option = CONTAINS_PATTERN;
		setParameterName(parameterName);
	}

	/**
	 * Constructor. <br>
	 *
	 * @param parameterName
	 *           name of the GenIL parameter, not allowed to be empty or null
	 * @param sign
	 *           sign has to be valid, use corresponding constants
	 * @param option
	 *           option has to be valid, use corresponding constants
	 * @param low
	 *           low value, may be null or empty
	 */
	public GenilQueryParameter(final String parameterName, final char sign, final String option, final String low)
	{
		this(parameterName, low);
		setOption(option);
		setSign(sign);
	}

	/**
	 * Constructor. <br>
	 *
	 * @param parameterName
	 *           name of the GenIL parameter, not allowed to be empty or null
	 * @param sign
	 *           sign has to be valid, use corresponding constants
	 * @param option
	 *           option has to be valid, use corresponding constants
	 * @param low
	 *           low value, may be null or empty
	 * @param high
	 *           high value, may be null or empty
	 */
	public GenilQueryParameter(final String parameterName, final char sign, final String option, final String low,
			final String high)
	{
		this(parameterName, sign, option, low);
		this.high = high;

	}

	/**
	 * Constructor. Will set default values for sign and option, so that <code>sign = INCLUDE</code> and
	 * <code>option = CONTAINS_PATTERN</code>.<br>
	 *
	 * @param parameterName
	 *           name of the GenIL parameter, not allowed to be empty or null
	 * @param low
	 *           low value, may be null or empty
	 */
	public GenilQueryParameter(final String parameterName, final String low)
	{
		this(parameterName);
		this.low = low;
	}

	/**
	 * Getter-Method for property {@link #high}. <br>
	 *
	 * @return Returns the {@link #high}.
	 */
	public String getHigh()
	{
		return high == null ? EMPTY_VALUE : high;
	}

	/**
	 * Getter-Method for property {@link #low}. <br>
	 *
	 * @return Returns the {@link #low}.
	 */
	public String getLow()
	{
		return low == null ? EMPTY_VALUE : low;
	}

	/**
	 * Getter-Method for property {@link #option}. <br>
	 *
	 * @return Returns the {@link #option}.
	 */
	public String getOption()
	{
		return option;
	}

	/**
	 * Getter-Method for property {@link #parameterName}. <br>
	 *
	 * @return Returns the {@link #parameterName}.
	 */
	public String getParameterName()
	{
		return parameterName == null ? "" : parameterName;
	}

	/**
	 * Getter-Method for property {@link #sign}. <br>
	 *
	 * @return Returns the {@link #sign}.
	 */
	public char getSign()
	{
		return sign;
	}

	/**
	 * checks if the option is valid.<br>
	 *
	 * @return true only if the option is valid
	 */
	@java.lang.SuppressWarnings({"squid:MethodCyclomaticComplexity","squid:S1067"})
	private boolean isOptionValid(final String option)
	{
		if (option.equals(BETWEEN) || option.equals(CONTAINS_PATTERN) || option.equals(EQUAL) || option.equals(GREATER)
				|| option.equals(GREATER_EQUALS) || option.equals(LESS) || option.equals(LESS_EQUAL)
				|| option.equals(NOT_CONTAINS_PATTERN) || option.equals(NOT_EQUAL) || option.equals(OUTSIDE))
		{
			return true;
		}
		return false;
	}

	/**
	 * checks if the sign is valid.<br>
	 *
	 * @return true only if the sign is valid
	 */
	private boolean isSignValid(final char sign)
	{
		return sign == INCLUDE || sign == EXCLUDE ? true : false;
	}

	/**
	 * Setter-Method for property {@link #high}. <br>
	 *
	 * @param high
	 *           The {@link #high} to set.
	 */
	public void setHigh(final String high)
	{
		this.high = high;
	}

	/**
	 * Setter-Method for property {@link #low}. <br>
	 *
	 * @param low
	 *           The {@link #low} to set.
	 */
	public void setLow(final String low)
	{
		this.low = low;
	}

	/**
	 * Setter-Method for property {@link #option}. <br>
	 *
	 * @param option
	 *           The {@link #option} to set.
	 */
	public void setOption(String option)
	{
		if (option != null)
		{
			this.option = option.toUpperCase(Locale.ENGLISH);
			if (isOptionValid(option))
			{
				this.option = option;
				return;
			}
		}
		final String msg = "Invalid Option " + option;
		final GenilBackendRuntimeException gnlBkndExc = new GenilBackendRuntimeException(msg);
		throw gnlBkndExc;

	}

	/**
	 * Setter-Method for property {@link #parameterName}. <br>
	 *
	 * @param parameterName
	 *           The {@link #parameterName} to set.
	 */
	private void setParameterName(final String parameterName)
	{

		if (parameterName != null && !parameterName.isEmpty())
		{
			this.parameterName = parameterName;
		}
		else
		{
			final String msg = "Invalid Paramter " + parameterName;
			final GenilBackendRuntimeException gnlBkndExc = new GenilBackendRuntimeException(msg);
			throw gnlBkndExc;
		}
	}

	/**
	 * Setter-Method for property {@link #sign}. <br>
	 *
	 * @param sign
	 *           The {@link #sign} to set.
	 */
	public void setSign(char sign)
	{
		this.sign = Character.toUpperCase(sign);
		if (isSignValid(sign))
		{
			this.sign = sign;
			return;
		}
		final String msg = "Invalid Sign " + sign;
		final GenilBackendRuntimeException gnlBkndExc = new GenilBackendRuntimeException(msg);
		throw gnlBkndExc;
	}

	/**
	 * Returns a textual presentation of this parameter, using the following template.<br>
	 * <code>(name, sign, option, low, high)</code>
	 *
	 * @return String describing this parameter
	 */
	@Override
	public String toString()
	{
		final StringBuilder str = new StringBuilder("(");
		str.append(parameterName);
		str.append(SEP);
		str.append(sign);
		str.append(SEP);
		str.append(option);
		str.append(SEP);
		str.append(low);
		str.append(SEP);
		str.append(high);
		str.append(")");
		return str.toString();
	}
}
