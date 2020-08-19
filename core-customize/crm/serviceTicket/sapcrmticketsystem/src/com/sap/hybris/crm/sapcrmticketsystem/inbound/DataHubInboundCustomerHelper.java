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

import de.hybris.platform.jalo.user.User;

/**
 * Data Hub Inbound Helper for Customer
 */
public interface DataHubInboundCustomerHelper {

    /**
     * This method gets the Customer corresponding to the customer ID
     *
     * @param customerID
     * @return Jalo user since impex engine runs on JAlo layer
     */
    User getCustomerFromID(String customerID);

}
