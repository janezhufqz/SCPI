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
 * Interface for Genil Attributes from and to string
 * 
 * @author SAP
 * @version 1.0
 */
public interface GenilAttributeConverter {

    public Object convertFromString(String valueAsString);

    public String convertToString(Object value);

}
