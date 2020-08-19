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

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the GenIL attribute which belongs to GenIL object. <br>
 * 
 * @author SAP
 * @version 1.0
 */
public class GenilModelAttr {

	/**
	 * GenIL name of the attribute
	 */
	private String name;

	/**
	 * GenIL property of the attribute
	 */
	private String property;

	/**
	 * Index of the attribute in the object structure
	 */
	private int structIndex;

	/**
	 * Reference to object which attribute belongs to
	 */
	private GenilModelObject object;
	private Map<String, GenilModelAttrOption> attributeOptions;

	protected GenilModelAttr() {
		super();
		this.attributeOptions = new HashMap<String, GenilModelAttrOption>();
	}

	public String getName() {
		return name;
	}

	public GenilModelObject getObject() {
		return object;
	}

	public String getProperty() {
		return property;
	}

	/**
	 * @return
	 */
	public int getStructIndex() {
		return structIndex;
	}

	/**
	 * Setter-Method for property {@link #name}. <br>
	 * 
	 * @param name
	 *            Sets the {@link #name}.
	 */
	protected void setName(String name) {
		this.name = name;
	}

	/**
	 * Setter-Method for property {@link #object}. <br>
	 * 
	 * @param object
	 *            Sets the {@link #object}.
	 */
	protected void setObject(GenilModelObject object) {
		this.object = object;
	}

	/**
	 * Setter-Method for property {@link #property}. <br>
	 * 
	 * @param property
	 *            Sets the {@link #property}.
	 */
	protected void setProperty(String property) {
		this.property = property;
	}

	/**
	 * Setter-Method for property {@link #structIndex}. <br>
	 * 
	 * @param structIndex
	 *            Sets the {@link #structIndex}.
	 */
	protected void setStructIndex(int structIndex) {
		this.structIndex = structIndex;
	}

	/**
	 * returns the attribute options for the Attribute. Is empty for all objects
	 * <> dynamicQuery for dynamicQuery objects it either returns the attribute
	 * options for this attribute, or if they are not available, the default
	 * attributes for the object
	 * 
	 * @return
	 */
	public Map<String, GenilModelAttrOption> getAttributeOptions() {
		if (attributeOptions.isEmpty()) {
			if (object.getAttributeDefaultOptions().isEmpty()) {
				return null;
			} else {
				return object.getAttributeDefaultOptions();
			}
		} else {
			return attributeOptions;
		}
	}

	public boolean attributeOptionAllowed(char sign, String option) {
		GenilModelAttrOption defaultOption = null;
		String key = sign + option;

		if (attributeOptions.isEmpty()) {
			if (object.getAttributeDefaultOptions().isEmpty()) {
				return false;
			} else {
				defaultOption = object.getAttributeDefaultOptions().get(key);
			}
		} else {
			defaultOption = attributeOptions.get(key);
		}
		return !(defaultOption == null);
	}

	/**
	 * Add an attribute option to the attribute
	 * 
	 * @param attributeSign
	 * @param attributeOption
	 */
	protected void addAttributeOption(char attributeSign, String attributeOption) {
		String attibuteKey = attributeSign + attributeOption;
		GenilModelAttrOption attributeDefaultOption = new GenilModelAttrOption(
				attributeSign, attributeOption);
		this.attributeOptions.put(attibuteKey, attributeDefaultOption);
	}

	/**
	 * Add an attribute option object to the attribute
	 * 
	 * @param attributeDefaultOption
	 */
	protected void addAttributeOption(
			GenilModelAttrOption attributeDefaultOption) {
		String attibuteKey = attributeDefaultOption.getModelAttrSign()
				+ attributeDefaultOption.getModelAttrOption();
		this.attributeOptions.put(attibuteKey, attributeDefaultOption);
	}
}
