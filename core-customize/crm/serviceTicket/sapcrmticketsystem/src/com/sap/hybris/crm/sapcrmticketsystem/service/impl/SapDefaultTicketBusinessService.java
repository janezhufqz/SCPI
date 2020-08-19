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

/**
 *
 */
package com.sap.hybris.crm.sapcrmticketsystem.service.impl;

import de.hybris.platform.comments.model.CommentAttachmentModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.core.configuration.global.impl.SAPGlobalConfigurationServiceImpl;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.ticket.enums.CsEventReason;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.enums.CsResolutionType;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.events.model.CsTicketResolutionEventModel;
import de.hybris.platform.ticket.model.CsAgentGroupModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketException;
import de.hybris.platform.ticket.service.impl.DefaultTicketBusinessService;
import de.hybris.platform.ticket.strategies.TicketEventEmailStrategy;
import de.hybris.platform.ticket.strategies.TicketEventStrategy;
import de.hybris.platform.ticket.strategies.TicketUpdateStrategy;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;

import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.sap.hybris.crm.sapcrmticketsystem.outbound.CsTicketExportService;
import com.sap.hybris.crm.sapcrmticketsystem.service.SapTicketBusinessService;


/**
 * The service overrides {@link DefaultTicketBusinessService} to inject trigger to datahub whenever any operation is
 * performed on ticket
 *
 * @author C5229484
 *
 */
public class SapDefaultTicketBusinessService extends DefaultTicketBusinessService implements SapTicketBusinessService
{
	/**
	 * Logger for class
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SapDefaultTicketBusinessService.class);

	/**
	 * Ticket export service.It prepares data to be sent to datahub
	 */
	private CsTicketExportService ticketExportService;
	private SAPGlobalConfigurationServiceImpl sapCoreSAPGlobalConfigurationService;

	@Resource(name = "ticketEventStrategy")
	private TicketEventStrategy ticketEventStrategy;

	@Resource(name = "ticketEventEmailStrategy")
	private TicketEventEmailStrategy ticketEventEmailStrategy;

	/**
	 * Ticket update strategy
	 */
	@Resource(name = "ticketUpdateStrategy")
	private TicketUpdateStrategy ticketUpdateStrategy;
	private String format;
	@Autowired
	private Converter<CsTicketParameter, CsTicketModel> ticketParameterConverter;

	@Override
	public CsTicketModel createTicket(final CsTicketParameter ticketParameter)
	{
		final CustomerModel customer = (CustomerModel) ticketParameter.getCustomer();
		final String notes = formatNote(ticketParameter.getCreationNotes(), customer.getCustomerID());
		ticketParameter.setCreationNotes(notes);

		final CsTicketModel ticket = this.ticketParameterConverter.convert(ticketParameter);
		final CsCustomerEventModel creationEvent = this.ticketEventStrategy.createCreationEventForTicket(ticket,
				ticketParameter.getReason(), ticketParameter.getInterventionType(), ticketParameter.getCreationNotes());

		if (CollectionUtils.isNotEmpty(ticketParameter.getAttachments()))
		{
			for (final MultipartFile file : ticketParameter.getAttachments())
			{
				final CommentAttachmentModel attachmentModel = (CommentAttachmentModel) getModelService()
						.create(CommentAttachmentModel.class);
				attachmentModel.setAbstractComment(creationEvent);
				try
				{
					attachmentModel.setItem(getTicketAttachmentsService().createAttachment(file.getOriginalFilename(),
							file.getContentType(), file.getBytes(), getUserService().getCurrentUser()));
				}
				catch (final IOException e)
				{
					LOG.error(e.getMessage(), e);
					return null;
				}

				getModelService().save(attachmentModel);
			}
		}
		triggerExportToDataHub(ticket, notes);

		return createTicketInternal(ticket, creationEvent);
	}

	/**
	 * Override the default creation of ticket . The ticket is exported to datahub to be replicated in CRM after it is
	 * saved in Hybris.
	 *
	 * @param ticket
	 *           the ticket to be created
	 * @param creationEvent
	 */
	@Override
	public CsTicketModel createTicket(final CsTicketModel ticket, final CsCustomerEventModel creationEvent)
	{
		checkNullTicket(ticket);

		try
		{
			LOG.debug("SapDefaultTicketBusinessService : Creating  ticket with ticket id:" + ticket.getTicketID());
			final CustomerModel customer = (CustomerModel) ticket.getCustomer();
			creationEvent.setText(formatNote(creationEvent.getText(), customer.getCustomerID()));
			final CsTicketModel savedTicket = super.createTicket(ticket, creationEvent);
			triggerExportToDataHub(savedTicket, creationEvent.getText());
		}
		catch (final ModelSavingException | NullPointerException e)
		{
			LOG.error("SapDefaultTicketBusinessService : An error occured while creating ticket  ", e);
		}

		return ticket;
	}

	/**
	 * Create ticket when an order is associated with the customer
	 */
	@java.lang.SuppressWarnings("squid:S00107")
	public CsTicketModel createTicket(final UserModel customer, final OrderModel order, final CsTicketCategory category,
			final CsTicketPriority priority, final EmployeeModel assignedAgent, final CsAgentGroupModel assignedGroup,
			final String headline, final CsInterventionType interventionType, final CsEventReason reason, final String creationNotes)
	{

		final CsTicketModel ticket = populateTicketDetails(customer, order, category, priority, assignedAgent, assignedGroup,
				headline);
		final CsCustomerEventModel creationEvent = createAndTriggerTicket(customer, interventionType, reason, creationNotes,
				ticket);

		return createTicketInternal(ticket, creationEvent);
	}

	/**
	 * Create ticket when a cart is associated with the customer
	 */
	@java.lang.SuppressWarnings("squid:S00107")
	public CsTicketModel createTicket(final UserModel customer, final CartModel cart, final CsTicketCategory category,
			final CsTicketPriority priority, final EmployeeModel assignedAgent, final CsAgentGroupModel assignedGroup,
			final String headline, final CsInterventionType interventionType, final CsEventReason reason, final String creationNotes)
	{
		final CsTicketModel ticket = populateTicketDetails(customer, cart, category, priority, assignedAgent, assignedGroup,
				headline);
		final CsCustomerEventModel creationEvent = createAndTriggerTicket(customer, interventionType, reason, creationNotes,
				ticket);

		return createTicketInternal(ticket, creationEvent);
	}

	@java.lang.SuppressWarnings("squid:S00107")
	public CsTicketModel createTicket(final UserModel customer, final CsTicketCategory category, final CsTicketPriority priority,
			final EmployeeModel assignedAgent, final CsAgentGroupModel assignedGroup, final String headline,
			final CsInterventionType interventionType, final CsEventReason reason, final String creationNotes)
	{
		final CsTicketModel ticket = populateTicketDetails(customer, null, category, priority, assignedAgent, assignedGroup,
				headline);
		final CsCustomerEventModel creationEvent = createAndTriggerTicket(customer, interventionType, reason, creationNotes,
				ticket);

		return createTicketInternal(ticket, creationEvent);
	}

	/**
	 * Override the default updation of ticket . The ticket is exported to datahub to be replicated in CRM after it is
	 * updated in Hybris.
	 *
	 * @param ticket
	 *           the ticket to be created
	 */
	@Override
	public CsTicketModel updateTicket(final CsTicketModel ticket) throws TicketException
	{
		CsTicketModel updatedTicket = new CsTicketModel();
		final String emptyNote = new String();
		checkNullTicket(ticket);

		try
		{
			LOG.debug("SapDefaultTicketBusinessService : Updating  ticket with ticket id:" + ticket.getTicketID());

			updatedTicket = super.updateTicket(ticket);
			triggerExportToDataHub(ticket, emptyNote);

		}
		catch (final ModelSavingException | NullPointerException e)
		{
			LOG.error("SapDefaultTicketBusinessService : An error occured while updating ticket " + ticket.getTicketID()
					+ " to datahub.Reason : ", e);
		}

		return updatedTicket;
	}

	/**
	 * Override the default updation of ticket . The ticket is exported to datahub to be replicated in CRM after it is
	 * updated in Hybris.
	 *
	 * @param ticket
	 *           the ticket to be created
	 * @param note
	 *           note associated with ticket
	 */
	@Override
	public CsTicketModel updateTicket(final CsTicketModel ticket, final String note) throws TicketException
	{
		CsTicketModel updatedTicket = null;
		checkNullTicket(ticket);
		try
		{
			LOG.debug("SapDefaultTicketBusinessService : Updating  ticket with ticket id:" + ticket.getTicketID());
			final CustomerModel customer = (CustomerModel) ticket.getCustomer();

			final String formattedNote = formatNote(note, customer.getCustomerID());

			updatedTicket = super.updateTicket(ticket, formattedNote);
			triggerExportToDataHub(ticket, formattedNote);

		}
		catch (final ModelSavingException | NullPointerException e)
		{
			LOG.error("An error occured while sending updated ticket " + ticket.getTicketID(), e);
		}
		return updatedTicket;
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
			LOG.debug("SapDefaultTicketBusinessService : Resolving  ticket with ticket id:" + ticket.getTicketID());
			final CustomerModel customer = (CustomerModel) ticket.getCustomer();

			final String formattedNote = formatNote(note, customer.getCustomerID());

			ticketResolution = super.resolveTicket(ticket, interventionType, resolutionType, formattedNote, attachments);
			triggerExportToDataHub(ticket, formattedNote);
		}
		catch (final ModelSavingException | NullPointerException e)
		{
			LOG.error("An error occured while sending resolved ticket" + ticket.getTicketID(), e);

		}

		return ticketResolution;
	}

	/**
	 * This method overrides the default behaviour of ticket reopen by adding the trigger to export updated ticket to
	 * data hub as and when the ticket is saved in Hybris.
	 *
	 * @param ticket
	 *           the ticket to be updated
	 * @param interventionType
	 *           intervention type
	 * @param reason
	 * @param note
	 *           any comments associated with ticket
	 */
	@Override
	public CsCustomerEventModel unResolveTicket(final CsTicketModel ticket, final CsInterventionType interventionType,
			final CsEventReason reason, final String note) throws TicketException
	{
		CsCustomerEventModel customerEvent = null;
		checkNullTicket(ticket);
		try
		{
			LOG.debug("SapDefaultTicketBusinessService : Resolving  ticket with ticket id:" + ticket.getTicketID());
			final CustomerModel customer = (CustomerModel) ticket.getCustomer();
			final String formattedNote = formatNote(note, customer.getCustomerID());

			customerEvent = super.unResolveTicket(ticket, interventionType, reason, formattedNote);
			triggerExportToDataHub(ticket, formattedNote);
		}
		catch (final ModelSavingException | NullPointerException e)
		{
			LOG.error("An error occured while sending unresolved ticket" + ticket.getTicketID(), e);
		}

		return customerEvent;
	}

	/**
	 * This method overrides the default behaviour of adding notes to ticket by adding the trigger to export updated
	 * ticket to data hub as and when the ticket is saved in Hybris.
	 *
	 * @param ticket
	 *           the ticket to be updated
	 * @param interventionType
	 *           intervention type
	 * @param reason
	 * @param note
	 *           any comments associated with ticket
	 */
	@Override
	public CsCustomerEventModel addNoteToTicket(final CsTicketModel ticket, final CsInterventionType interventionType,
			final CsEventReason reason, final String note, final Collection<MediaModel> attachments)
	{
		CsCustomerEventModel customerEvent = null;
		checkNullTicket(ticket);
		try
		{
			LOG.debug("SapDefaultTicketBusinessService : Adding note to ticket with ticket id:" + ticket.getTicketID());
			final CustomerModel customer = (CustomerModel) ticket.getCustomer();
			final String formattedNote = formatNote(note, customer.getCustomerID());

			customerEvent = super.addNoteToTicket(ticket, interventionType, reason, formattedNote, attachments);
			triggerExportToDataHub(ticket, formattedNote);
		}
		catch (final ModelSavingException | NullPointerException e)
		{
			LOG.error("An error occured while updating notes of ticket to datahub" + ticket.getTicketID(), e);
		}
		return customerEvent;
	}

	/**
	 * This method overrides the default behaviour of changing ticket state by adding the trigger to export updated
	 * ticket to data hub as and when the ticket is saved in Hybris.
	 *
	 * @param ticket
	 *           the ticket to be updated
	 * @param newState
	 *           new state
	 * @param note
	 *           any comments associated with ticket
	 */
	@Override
	public CsTicketModel setTicketState(final CsTicketModel ticket, final CsTicketState newState, final String note)
			throws TicketException
	{
		checkNullTicket(ticket);
		try
		{
			LOG.debug("SapDefaultTicketBusinessService : Changing state for ticket ticket with ticket id:" + ticket.getTicketID());
			final CustomerModel customer = (CustomerModel) ticket.getCustomer();

			final String formattedNote = formatNote(note, customer.getCustomerID());

			super.setTicketState(ticket, newState, formattedNote);
			triggerExportToDataHub(ticket, formattedNote);
		}
		catch (final ModelSavingException | NullPointerException e)
		{
			LOG.error("An error occured while sending changed state of ticket to datahub" + ticket.getTicketID(), e);
		}
		return ticket;
	}

	/**
	 * @param customer
	 * @param interventionType
	 * @param reason
	 * @param creationNotes
	 * @param ticket
	 * @return CsCustomerEventModel
	 */
	@Override
	public CsCustomerEventModel createAndTriggerTicket(final UserModel customer, final CsInterventionType interventionType,
			final CsEventReason reason, final String creationNotes, final CsTicketModel ticket)
	{
		final CustomerModel user = (CustomerModel) customer;

		final String formattedNote = formatNote(creationNotes, user.getCustomerID());
		final CsCustomerEventModel creationEvent = this.ticketEventStrategy.createCreationEventForTicket(ticket, reason,
				interventionType, formattedNote);
		triggerExportToDataHub(ticket, creationEvent.getText());
		return creationEvent;
	}

	/**
	 * This method invokes the ticket export service with the ticket to be exported.
	 *
	 * @param csTicket
	 *           the ticket to be updated
	 * @param notes
	 */
	private void triggerExportToDataHub(final CsTicketModel csTicket, final String notes)
	{
		try
		{
			if (null != this.sapCoreSAPGlobalConfigurationService)
			{
				LOG.debug("Trying to export updated Ticket with id  " + csTicket.getTicketID() + " to datahub");
				this.ticketExportService.sendCsTicketData(csTicket, notes);
			}
		}
		catch (final DataHubCommunicationException e)
		{
			LOG.error("SapDefaultTicketBusinessService : An error occured while triggering export of ticket to datahub.Reason : ",
					e);
		}
	}

	/**
	 * @param customerID
	 * @param note
	 */
	public String formatNote(final String note, final String customerID)
	{
		return this.format + customerID + this.format + "\n  " + note + "  \n" + this.format + customerID + this.format;

	}

	/**
	 * Check for null ticket.
	 *
	 * @param ticket
	 */
	private void checkNullTicket(final CsTicketModel ticket)
	{
		if (ticket == null)
		{
			throw new IllegalArgumentException("Cannot unresolve null ticket");
		}
	}

	/**
	 * @return the ticketEventStrategy
	 */
	public TicketEventStrategy getTicketEventStrategy()
	{
		return ticketEventStrategy;
	}

	/**
	 * @return the ticketExportService
	 */
	public CsTicketExportService getTicketExportService()
	{
		return ticketExportService;
	}

	/**
	 * @param format
	 *           the format to set
	 */
	public void setFormat(final String format)
	{
		this.format = format;
	}

	/**
	 * @return the format
	 */
	public String getFormat()
	{
		return format;
	}

	/**
	 * @param ticketExportService
	 *           the ticketExportService to set
	 */
	public void setTicketExportService(final CsTicketExportService ticketExportService)
	{
		this.ticketExportService = ticketExportService;
	}

	/**
	 * @return the sapCoreSAPGlobalConfigurationService
	 */
	public SAPGlobalConfigurationServiceImpl getSapCoreSAPGlobalConfigurationService()
	{
		return sapCoreSAPGlobalConfigurationService;
	}

	/**
	 * @param sapCoreSAPGlobalConfigurationService
	 *           the sapCoreSAPGlobalConfigurationService to set
	 */
	public void setSapCoreSAPGlobalConfigurationService(
			final SAPGlobalConfigurationServiceImpl sapCoreSAPGlobalConfigurationService)
	{
		this.sapCoreSAPGlobalConfigurationService = sapCoreSAPGlobalConfigurationService;
	}

	/**
	 * @return the ticketEventEmailStrategy
	 */
	@Override
	public TicketEventEmailStrategy getTicketEventEmailStrategy()
	{
		return ticketEventEmailStrategy;
	}

	/**
	 * @return the ticketUpdateStrategy
	 */
	public TicketUpdateStrategy getTicketUpdateStrategy()
	{
		return ticketUpdateStrategy;
	}

}
