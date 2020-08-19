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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a table parameter. A table parameter contains a list of
 * table parameter rows
 * 
 * @author SAP
 * @version 1.0
 */
public class TablesParam {

	/**
	 * name of the table parameter
	 */
	private String tableName;
	private boolean duplicate = false;

	/**
	 * list of table parameter rows
	 */
	protected List<TableParamRow> rows;
	protected Map<String, String> rowsKeyMap;

	/**
	 * standard constructor
	 * 
	 * @param tableName
	 *            name of the table parameter
	 */
	public TablesParam(String tableName) {
		this.tableName = tableName;
		rows = new ArrayList<TableParamRow>();
	}

	/**
	 *  constructor
	 * 
	 * @param tableName
	 *            name of the table parameter
	 * @param duplicate
	 *            indicates if key in the table are unique or not
	 */
	public TablesParam(String tableName, boolean duplicate) {
		this.tableName = tableName;
		this.duplicate = duplicate;
		if (duplicate) {
			rows = new ArrayList<TableParamRow>();
			rowsKeyMap = new HashMap<String, String>();
		} else {
			rows = new ArrayList<TableParamRow>();

		}
	}

	/**
	 * Ads a table parameter row to a table parameter
	 * 
	 * @param row
	 *            table parameter row to add
	 */

	public void addRow(TableParamRow row) {
		// check if duplicate are allowed
		if (duplicate) {
			// Assuming that the first NameValuePair represents the key
			NameValuePair nvp = row.get(0);
			if (nvp != null && rowsKeyMap.get(nvp.getValue()) == null) {
		
					rowsKeyMap.put(nvp.getValue().toString(), nvp.getValue()
							.toString());
					rows.add(row);
	
			}
		} else {
			rows.add(row);
		}
	}

	/**
	 * Returns a special table parameter row from the table parameter
	 * 
	 * @param i
	 *            position of the table parameter row to get
	 * @return returns the table parameter row
	 */
	public TableParamRow getRow(int i) {
		if (i > rows.size() - 1 || i < 0) {
			return null;
		}
		return rows.get(i);
	}

	/**
	 * Getter-Method for property rows. <br>
	 * returns the complete table parameter collection of table parameter rows
	 * 
	 * @return returns the table parameter rows collection
	 */
	public Collection<TableParamRow> getRows() {
		return rows;
	}

	/**
	 * Getter-Method for property {@link #tableName}. <br>
	 * 
	 * @return Returns the {@link #tableName}.
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Inserts a table parameter row row into the table parameter
	 * 
	 * @param row
	 *            table parameter row to be inserted
	 * @param i
	 *            Position to insert the row
	 */
	public void insertRow(TableParamRow row, int i) {
		rows.add(i, row);
	}

	/**
	 * Sort the Table parameter rows
	 * 
	 * @param deleteDuplicates
	 *            if true, duplicates will be deleted
	 */
	public void sort(boolean deleteDuplicates) {
		List<String> keys = new ArrayList<String>();
		Map<String, Integer> keyMap = new HashMap<String, Integer>();
		int i = 0;
		for (TableParamRow tpr : rows) {
			// Assuming that the first NameValuePair represents the key
			NameValuePair nvp = tpr.get(0);
			if (nvp != null) {
				if (deleteDuplicates && keyMap.get(nvp.getValue()) != null) {
					continue;
				}
				keys.add(nvp.getValue().toString());
				keyMap.put(nvp.getValue().toString(), i);
			}
			i++;
		}
		Collections.sort(keys);
		List<TableParamRow> newRows = new ArrayList<TableParamRow>();
		for (String key : keys) {
			int oldIndex = keyMap.get(key);
			TableParamRow tpr = rows.get(oldIndex);
			newRows.add(tpr);
		}
		rows.clear();
		rows.addAll(newRows);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(tableName);
		int i = 0;
		for (TableParamRow tpr : rows) {
			result.append("\n");
			result.append(i);
			result.append(": ");
			result.append(tpr);
			i++;
		}
		return result.toString();
	}
}
