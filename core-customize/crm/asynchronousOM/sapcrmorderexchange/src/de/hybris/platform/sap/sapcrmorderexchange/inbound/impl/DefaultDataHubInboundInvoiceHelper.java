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

package de.hybris.platform.sap.sapcrmorderexchange.inbound.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.datahub.inbound.impl.AbstractDataHubInboundHelper;
import de.hybris.platform.sap.sapcrmorderexchange.inbound.DataHubInboundInvoiceHelper;

public class DefaultDataHubInboundInvoiceHelper extends AbstractDataHubInboundHelper implements DataHubInboundInvoiceHelper
{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDataHubInboundInvoiceHelper.class);
	
	@Override
	public void processInvoiceNumberInOrder(String orderCode, String invoiceNumber) {
		
		final OrderModel order = readOrder(orderCode);
		
		if(invoiceNumber != null){
			order.setInvoiceNumber(Integer.parseInt(invoiceNumber));
			LOGGER.debug("setting invoice number : " + invoiceNumber + " for order Id : " + orderCode);
			getModelService().save(order);
		} else{
			LOGGER.debug("cannot set invoice number : " + " for order Id : " + orderCode + "as invoice number is null..!!");
		}
	}

}
