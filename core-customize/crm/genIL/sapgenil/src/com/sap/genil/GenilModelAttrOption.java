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

/**
 * This class represents the GenIL attribute which belongs to GenIL object. <br>
 * 
 * @author SAP
 * @version 1.0
 */
public class GenilModelAttrOption {

	private char modelAttrSign;
	private String modelAttrOption;

	protected GenilModelAttrOption(char modelAttrSign, String modelAttrOption) {
		super();
		this.modelAttrSign = modelAttrSign;
		this.modelAttrOption = modelAttrOption;
	}

	public char getModelAttrSign() {
		return modelAttrSign;
	}

	public String getModelAttrOption() {
		return modelAttrOption;
	}
@Override
	public String toString() {
		return modelAttrSign + " - " + modelAttrOption;
	}
}
