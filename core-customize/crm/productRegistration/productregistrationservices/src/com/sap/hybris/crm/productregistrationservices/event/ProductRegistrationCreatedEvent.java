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

package com.sap.hybris.crm.productregistrationservices.event;

import de.hybris.platform.productregistrationfacades.data.ProductRegistrationData;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

/**
 * Product Registration Created Event implementation of event triggered when a
 * product is registered
 */
public class ProductRegistrationCreatedEvent extends AbstractEvent {
    private ProductRegistrationData registrationData;

    /**
     * Default constructor
     */
    public ProductRegistrationCreatedEvent() {
        super();
    }

    /**
     * Parameterized Constructor
     *
     * @param registrationData
     */
    public ProductRegistrationCreatedEvent(final ProductRegistrationData registrationData) {
        super();
        this.registrationData = registrationData;
    }

    public ProductRegistrationData getRegistrationData() {
        return registrationData;
    }

    public void setRegistrationData(final ProductRegistrationData registrationData) {
        this.registrationData = registrationData;
    }

}
