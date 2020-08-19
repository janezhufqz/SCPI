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

/**
 * 
 */
package com.sap.hybris.sapcrmcustomerb2b.outbound;

import java.util.List;
import java.util.Map;


/**
 * @author C5230256
 *
 */
public interface B2BContactExporter
{
    public void exportContact(List<Map<String, Object>> rawData);

    public void exportRelations(List<Map<String, Object>> rawData);
}
