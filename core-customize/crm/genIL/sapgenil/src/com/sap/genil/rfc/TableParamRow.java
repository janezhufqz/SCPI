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

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a single table parameter row. A parameter row contains
 * a list of name value pairs
 * 
 * @author SAP
 * @version 1.0
 */
public class TableParamRow {

	/**
	 * row as a list of name value pairs
	 */
	private List<NameValuePair> row;

	/**
	 * Standard constructor
	 */
	public TableParamRow() {
		row = new ArrayList<NameValuePair>();
	}

	/**
	 * adds a name value pair object
	 * 
	 * @param nvp
	 *            Name value Pair object to add
	 */
	public void add(NameValuePair nvp) {
		row.add(nvp);
	}

	/**
	 * Adds a name value pair
	 * 
	 * @param name
	 *            name of the name value pair
	 * @param value
	 *            value of the name value pair
	 */
	public void add(String name, Object value) {
		row.add(new NameValuePair(name, value));
	}

	/**
	 * returns the name value pair at a special position in the list
	 * 
	 * @param i
	 *            Position of the name value pair
	 * @return returns the name value pair
	 */
	public NameValuePair get(int i) {
		if (i > row.size() - 1 || i < 0) {
			return null;
		}
		return row.get(i);
	}

	/**
	 * returns the name value pair by name
	 * 
	 * @param name
	 *            name of the name value pair
	 * @return returns the name value pair
	 */
	public NameValuePair get(String name) {
		for (NameValuePair nvp : row) {
			if (nvp.getName().compareTo(name) == 0) {
				return nvp;
			}
		}
		return null;
	}

	/**
	 * returns the TableParamRow
	 * 
	 * @return returns the table parameter row
	 */
	public List<NameValuePair> getRow() {
		return row;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("[");
		for (NameValuePair nvp : row) {
			result.append("( ");
			result.append(nvp);
			result.append(" )");
		}
		result.append("]");
		return result.toString();
	}
}
