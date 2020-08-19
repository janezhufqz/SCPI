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

package com.sap.hybris.crm.sapcrmcomplaintexchange.service.impl;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;

import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.sap.hybris.crm.sapcrmcomplaintexchange.outbound.ComplaintExportService;
import com.sap.hybris.crm.sapcrmcomplaintexchange.service.SapComplaintBussinessService;

import de.hybris.platform.comments.model.CommentAttachmentModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.configuration.global.impl.SAPGlobalConfigurationServiceImpl;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.ticket.enums.CsEventReason;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.enums.CsResolutionType;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.events.model.CsTicketResolutionEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketException;
import de.hybris.platform.ticket.service.impl.DefaultTicketBusinessService;
import de.hybris.platform.ticket.strategies.TicketEventStrategy;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;

/**
 * The service overrides {@link DefaultTicketBusinessService} to inject trigger
 * to datahub whenever any operation is performed on complaint
 *
 * @author C5242879
 *
 */
@java.lang.SuppressWarnings("squid:S1948")
public class SapDefaultComplaintBussinessService extends DefaultTicketBusinessService
        implements SapComplaintBussinessService {

    /**
     * Logger for class
     */
    private static final Logger LOG = LoggerFactory.getLogger(SapDefaultComplaintBussinessService.class);

    private static final String FORMAT = "__";
    @Autowired
    private ComplaintExportService complaintExportService;
    @Autowired
    private SAPGlobalConfigurationServiceImpl sapCoreSAPGlobalConfigurationService;
    @Autowired
    private Converter<CsTicketParameter, CsTicketModel> ticketParameterConverter;

    @Autowired
    private TicketEventStrategy ticketEventStrategy;

    @Override
    public CsTicketModel createComplaint(final CsTicketParameter ticketParameter) {
        final CustomerModel customer = (CustomerModel) ticketParameter.getCustomer();
        final String notes = formatNote(ticketParameter.getCreationNotes(), customer.getCustomerID());
        ticketParameter.setCreationNotes(notes);

        final CsTicketModel ticket = this.ticketParameterConverter.convert(ticketParameter);
        final CsCustomerEventModel creationEvent = this.ticketEventStrategy.createCreationEventForTicket(ticket,
                ticketParameter.getReason(), ticketParameter.getInterventionType(), ticketParameter.getCreationNotes());

        if (CollectionUtils.isNotEmpty(ticketParameter.getAttachments())) {
            for (final MultipartFile file : ticketParameter.getAttachments()) {
                final CommentAttachmentModel attachmentModel = (CommentAttachmentModel) getModelService()
                        .create(CommentAttachmentModel.class);
                attachmentModel.setAbstractComment(creationEvent);
                try {
                    attachmentModel.setItem(getTicketAttachmentsService().createAttachment(file.getOriginalFilename(),
                            file.getContentType(), file.getBytes(), getUserService().getCurrentUser()));
                } catch (final IOException e) {
                    LOG.info("Exception occured " + e);
                    return null;
                }

                getModelService().save(attachmentModel);
            }
        }
        triggerExportToDataHub(ticket, notes);
        return createTicketInternal(ticket, creationEvent);
    }

    /**
     * Override the default updation of complaint . The ticket is exported to
     * datahub to be replicated in CRM after it is updated in Hybris.
     *
     * @param ticket
     *            the ticket to be created
     */
    @Override
    public CsTicketModel updateComplaint(final CsTicketModel ticket) throws TicketException {
        CsTicketModel updatedTicket = new CsTicketModel();
        final String emptyNote = new String();
        checkNullTicket(ticket);

        try {
            LOG.debug("SapDefaultComplaintBussinessService : Updating  ticket with ticket id:" + ticket.getTicketID());

            updatedTicket = super.updateTicket(ticket);
            triggerExportToDataHub(ticket, emptyNote);

        } catch (final ModelSavingException e) {
            LOG.error("SapDefaultComplaintBussinessService : An error occured while updating ticket "
                    + ticket.getTicketID() + " to datahub.Reason : ", e);
        }

        return updatedTicket;
    }

    @Override
    public CsCustomerEventModel addNoteComplaint(final CsTicketModel ticket, final CsInterventionType interventionType,
            final CsEventReason reason, final String note, final Collection<MediaModel> attachments) {
        CsCustomerEventModel customerEvent = null;
        checkNullTicket(ticket);
        try {
            LOG.debug("SapDefaultComplaintBussinessService : Adding note to ticket with ticket id:"
                    + ticket.getTicketID());
            final CustomerModel customer = (CustomerModel) ticket.getCustomer();
            final String formattedNote = formatNote(note, customer.getCustomerID());

            customerEvent = super.addNoteToTicket(ticket, interventionType, reason, formattedNote, attachments);
            triggerExportToDataHub(ticket, formattedNote);
        } catch (final ModelSavingException e) {
            LOG.error("An error occured while updating notes of ticket to datahub" + ticket.getTicketID(), e);
        }
        return customerEvent;
    }
    /**
     * This method overrides the default behaviour of ticket resolution by adding the trigger to export updated ticket to
     * data hub as and when the ticket is saved in Hybris.
     *
     * @param ticket
     *           the ticket to be updated
     * @param interventionType
     *           intervention type
     * @param resolutionType
     * @param note
     *           any comments associated with ticket
     */
    @Override
    public CsTicketResolutionEventModel resolveTicket(final CsTicketModel ticket, final CsInterventionType interventionType,
                    final CsResolutionType resolutionType, final String note, final Collection<MediaModel> attachments)
                    throws TicketException
    {
            CsTicketResolutionEventModel ticketResolution = null;
            checkNullTicket(ticket);

            try
            {
                    LOG.debug("SapDefaultComplaintBussinessService : Resolving  ticket with ticket id:" + ticket.getTicketID());
                    final CustomerModel customer = (CustomerModel) ticket.getCustomer();

                    final String formattedNote = formatNote(note, customer.getCustomerID());

                    ticketResolution = super.resolveTicket(ticket, interventionType, resolutionType, formattedNote, attachments);
                    triggerExportToDataHub(ticket, formattedNote);
            }
            catch (final ModelSavingException e)
            {
                    LOG.error("An error occured while sending resolved ticket" + ticket.getTicketID(), e);

            }

            return ticketResolution;
    }

    /**
     * @param customerID
     * @param note
     */
    public String formatNote(final String note, final String customerID) {
        return FORMAT + customerID + FORMAT + "\n  " + note + "  \n" + FORMAT + customerID + FORMAT;

    }

    /**
     * This method invokes the ticket export service with the ticket to be
     * exported.
     *
     * @param csTicket
     *            the ticket to be updated
     * @param notes
     */
    protected void triggerExportToDataHub(final CsTicketModel csTicket, final String notes) {
        try {
            if (null != this.sapCoreSAPGlobalConfigurationService) {
                LOG.debug("Trying to export updated Ticket with id  " + csTicket.getTicketID() + " to datahub");
                this.complaintExportService.sendComplaintData(csTicket, notes);
            }
        } catch (final DataHubCommunicationException e) {
            LOG.error(
                    "SapDefaultTicketBusinessService : An error occured while triggering export of ticket to datahub.Reason : ",
                    e);
        }
    }

    /**
     * Check for null ticket.
     *
     * @param ticket
     */
    protected void checkNullTicket(final CsTicketModel ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Cannot unresolve null ticket");
        }
    }

}
