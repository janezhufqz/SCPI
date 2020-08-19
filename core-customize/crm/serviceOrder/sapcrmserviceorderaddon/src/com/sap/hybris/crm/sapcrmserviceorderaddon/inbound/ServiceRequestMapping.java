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

package com.sap.hybris.crm.sapcrmserviceorderaddon.inbound;

import org.apache.log4j.Logger;

import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;

import org.springframework.integration.support.MessageBuilder;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import de.hybris.platform.core.Registry;

/**
 * This class includes the translator for B2BUnit address deletion notification
 */
public class ServiceRequestMapping extends AbstractSpecialValueTranslator {
    private static final org.apache.log4j.Logger LOG = Logger.getLogger(ServiceRequestMapping.class.getName());
    private MessageChannel serviceTicketLinkChannel;

    @Override
    public void performImport(final String transformationExpression, final Item processedItem) throws ImpExException {
        if (transformationExpression != null && transformationExpression.contains(":")) {
            final String payLoadServiceRequestMapping = "ticketid:" + transformationExpression.split(":")[0]
                    + "|objectid:" + transformationExpression.split(":")[1] + "|objectType:serviceorderid";
            try {
                final ConfigurableApplicationContext appCtx = (ConfigurableApplicationContext) Registry
                        .getApplicationContext();
                setServiceTicketLinkChannel((MessageChannel) appCtx.getBean("serviceTicketLinkChannel"));
                final Message<String> msg = MessageBuilder.withPayload(payLoadServiceRequestMapping).build();
                getServiceTicketLinkChannel().send(msg);
                LOG.debug("Service order details related to Service Request is pushed to the channel");
            } catch (Exception e) {
                LOG.debug("Channel Not Found to push service order info OR Service ticket module not installed..",e);
            }

        } else {
            LOG.debug(" No service request id found related to the service order");
        }
    }

    @Override
    public String performExport(final Item paramItem) throws ImpExException {
        return null;
    }

    public MessageChannel getServiceTicketLinkChannel() {
        return serviceTicketLinkChannel;
    }

    public void setServiceTicketLinkChannel(MessageChannel serviceTicketLinkChannel) {
        this.serviceTicketLinkChannel = serviceTicketLinkChannel;
    }

}
