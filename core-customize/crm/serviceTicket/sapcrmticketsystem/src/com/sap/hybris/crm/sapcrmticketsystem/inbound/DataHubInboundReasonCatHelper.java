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
package com.sap.hybris.crm.sapcrmticketsystem.inbound;

import com.sap.hybris.crm.sapcrmcategoryschema.jalo.CategorizationCategory;

/**
 * Data Hub Inbound Helper for reason category
 */
public interface DataHubInboundReasonCatHelper {
    /**
     * This method gets the Customer corresponding to the customer ID
     *
     * @param categoryCode
     * @param catalogName
     * @param catalogType
     * @return Jalo user since impex engine runs on JAlo layer
     */
    CategorizationCategory getCategoryFromSchema(String categoryCode, String catalogType, String catalogName);
}