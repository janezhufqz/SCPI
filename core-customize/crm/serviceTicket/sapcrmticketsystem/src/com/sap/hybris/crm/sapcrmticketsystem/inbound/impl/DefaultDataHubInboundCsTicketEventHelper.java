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
package com.sap.hybris.crm.sapcrmticketsystem.inbound.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.crm.sapcrmticketsystem.inbound.DataHubInboundCsTicketEventHelper;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.ticket.dao.TicketDao;
import de.hybris.platform.ticket.enums.CsEventReason;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.strategies.TicketEventEmailStrategy;
import de.hybris.platform.ticket.strategies.TicketEventStrategy;

/**
 * Default Data Hub Inbound helper for triggering CsTicketEvent on creation of
 * ticket from CRM and to save the notes
 *
 * @author C5229484
 */
public class DefaultDataHubInboundCsTicketEventHelper implements DataHubInboundCsTicketEventHelper {

    /**
     * Logger for class DefaultDataHubInboundCustomerHelper
     */
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDataHubInboundCsTicketEventHelper.class);
    private ModelService modelService;
    private TicketEventEmailStrategy ticketEventEmailStrategy;
    private TicketEventStrategy ticketEventStrategy;
    private TicketDao ticketDao;

    /**
     * This method creates a new ticket change event for the ticket coming from
     * DataHub
     *
     * @param notes
     *            representing ticketNotes
     * @param ticketID
     *            ticket for which change event is to be processed
     */
    @Override
    public void createTicketChangeEvent(final String notes, final String ticketID, final CsTicketPriority priority,
            final CsTicketCategory category, final String headline) {
        LOG.debug(" Trying to trigger ticket change event for ticket with ticket id:" + ticketID);

        CsTicketModel ticket = getTicketById(ticketID);
        if (ticket != null) {
            addNoteToExistingTicket(notes, ticket);
        } else {
            ticket = new CsTicketModel();
            ticket.setTicketID(ticketID);
            ticket.setCategory(category);
            ticket.setPriority(priority);
            ticket.setState(CsTicketState.NEW);
            ticket.setHeadline(headline);
            final CsCustomerEventModel creationEvent = this.ticketEventStrategy.createCreationEventForTicket(ticket,
                    CsEventReason.FIRSTCONTACT, CsInterventionType.CALL, notes);
            this.ticketEventStrategy.ensureTicketEventSetupForCreationEvent(ticket, creationEvent);

            this.modelService.saveAll(new Object[] { ticket, creationEvent });
            this.modelService.refresh(ticket);

            this.ticketEventEmailStrategy.sendEmailsForEvent(ticket, creationEvent);
        }
    }

    /**
     * Add note to existing ticket and trigger email for that ticket
     *
     * @param notes
     * @param ticket
     */
    private void addNoteToExistingTicket(final String notes, final CsTicketModel ticket) {
        boolean isNotesExisting = false;
        final Pattern regex = Pattern.compile("[_]{2}\\d+[_]{2}");
        final Matcher matcher = regex.matcher(notes);
        if (notes != null && (matcher.find() || isSameNoteExisting(notes, ticket))) {
            isNotesExisting = true;
        }
        if (!isNotesExisting) {
            final CsCustomerEventModel ret = this.ticketEventStrategy.createNoteForTicket(ticket,
                    CsInterventionType.CALL, CsEventReason.FIRSTCONTACT, notes, null);
            this.modelService.save(ret);
            this.ticketEventEmailStrategy.sendEmailsForEvent(ticket, ret);
        }
    }

    /**
     * Method to find if same note already exist.
     *
     * @param ticket
     * @param notes
     *
     * @return true if same note already exist.
     */
    @SuppressWarnings("deprecation")
    private boolean isSameNoteExisting(final String notes, final CsTicketModel ticket) {
        boolean isExist = false;
        for (final CsTicketEventModel note : ticket.getEvents()) {
            if (note.getText().equals(notes)) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    /**
     * Return the ticket based upon the ticket id
     *
     * @param ticketID
     * @return CsTicketModel
     */
    private CsTicketModel getTicketById(final String ticketID) {
        final List<CsTicketModel> ticketList = this.ticketDao.findTicketsById(ticketID);
        CsTicketModel ticket = null;
        if (null != ticketList && !ticketList.isEmpty()) {
            ticket = ticketList.get(0);
        }
        return ticket;
    }

    /**
     * @param modelService
     *            the modelService to set
     */
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * @param ticketEventEmailStrategy
     *            the ticketEventEmailStrategy to set
     */
    public void setTicketEventEmailStrategy(final TicketEventEmailStrategy ticketEventEmailStrategy) {
        this.ticketEventEmailStrategy = ticketEventEmailStrategy;
    }

    /**
     * @param ticketEventStrategy
     *            the ticketEventStrategy to set
     */
    public void setTicketEventStrategy(final TicketEventStrategy ticketEventStrategy) {
        this.ticketEventStrategy = ticketEventStrategy;
    }

    /**
     * @param ticketDao
     *            the ticketDao to set
     */
    public void setTicketDao(final TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

}
