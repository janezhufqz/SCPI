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
package com.sap.hybris.crm.sapcrmcomplaintexchange.interceptors;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.interceptors.CsTicketPrepareInterceptor;
import de.hybris.platform.ticket.model.CsTicketModel;

/**
 * @author C5242879
 */
public class SapCsTicketPrepareInterceptor extends CsTicketPrepareInterceptor {
    private CsTicketState initialTicketState;

    private KeyGenerator complaintKeyGenerator;

    @Override
    public void onPrepare(final Object model, final InterceptorContext ctx) throws InterceptorException {
        if (model instanceof CsTicketModel) {
            final CsTicketModel ticket = (CsTicketModel) model;
            if (ticket.getTicketID() == null) {
                if (CsTicketCategory.COMPLAINT.equals(ticket.getCategory())) {
                    ticket.setTicketID(createComplaintId());
                } else {
                    ticket.setTicketID(createTicketId());
                }
            }

            if (ticket.getState() == null) {
                ticket.setState(initialTicketState);
            }
        }
    }

    protected String createComplaintId() {
        return complaintKeyGenerator.generate().toString();
    }

    @Required
    public void setComplaintKeyGenerator(final KeyGenerator complaintKeyGenerator) {
        this.complaintKeyGenerator = complaintKeyGenerator;
    }

    @Override
    @Required
    public void setInitialTicketState(final CsTicketState initialTicketState) {
        this.initialTicketState = initialTicketState;
    }
}
