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
package com.sap.hybris.sapcrmcustomerb2c.outbound.events;

import de.hybris.platform.servicelayer.event.ClusterAwareEvent;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import java.util.List;
import java.util.Map;

public class CRMCustomerReplicationEvent extends AbstractEvent implements
        ClusterAwareEvent {

    private static final long serialVersionUID = -3839203917371356971L;

    private final List<Map<String, Object>> customerDetailsAndAddressDataList;
    private final List<Map<String, Object>> customerCreditCardDetailsDataList;

    public CRMCustomerReplicationEvent(
            final List<Map<String, Object>> customerDetailsAndAddressDataList,
            final List<Map<String, Object>> customerCreditCardDetailsDataList) {
        this.customerDetailsAndAddressDataList = customerDetailsAndAddressDataList;
        this.customerCreditCardDetailsDataList = customerCreditCardDetailsDataList;
    }

    /**
     * @return the customerDetailsAndAddressDataList
     */
    public List<Map<String, Object>> getCustomerDetailsAndAddressDataList() {
        return customerDetailsAndAddressDataList;
    }

    /**
     * @param customerDetailsAndAddressDataList
     *            the customerDetailsAndAddressDataList to set
     */

    /**
     * @return the customerCreditCardDetailsDataList
     */
    public List<Map<String, Object>> getCustomerCreditCardDetailsDataList() {
        return customerCreditCardDetailsDataList;
    }

    @Override
    public boolean publish(final int sourceNodeId, final int targetNodeId) {
        return true;
    }
}
