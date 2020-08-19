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

package de.hybris.platform.sap.sapcrmorderexchange.populator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.commercefacades.order.converters.populator.OrderPopulator;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.session.SessionService;

public class DefaultOrderPopulatorCRM extends OrderPopulator {
    @Autowired
    private SessionService sessionService;

    @Override
    public void populate(final OrderModel source, final OrderData target) {
        super.populate(source, target);
        Map<String, String> billingDocNumbers = new HashMap<String, String>();
        if (source.getInvoiceNumber() != null) {
            billingDocNumbers.put(Integer.toString(source.getInvoiceNumber()), "VBRK");
            sessionService.setAttribute(Integer.toString(source.getInvoiceNumber()) + "_data", source.getCode());
            target.setAsyncBillingDocNumbers(billingDocNumbers);
        }

    }
}
