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

import org.apache.log4j.Logger;

import de.hybris.platform.productregistrationfacades.data.ProductRegistrationData;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

/**
 * Listener for customer registration events.
 */
public class ProductRegistrationCreatedEventListener extends AbstractEventListener<ProductRegistrationCreatedEvent> {

    private static final Logger LOG = Logger.getLogger(ProductRegistrationCreatedEventListener.class);

    @Override
    protected void onEvent(final ProductRegistrationCreatedEvent event) {

        final ProductRegistrationData data = event.getRegistrationData();

        LOG.debug("Product with product code : " + data.getProduct().getCode()
                + " registered successfully. Triggering event for it.");
    }

}
