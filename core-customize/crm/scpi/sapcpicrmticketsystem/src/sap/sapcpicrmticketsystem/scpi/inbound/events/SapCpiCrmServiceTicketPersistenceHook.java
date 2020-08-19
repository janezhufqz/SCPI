/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package sap.sapcpicrmticketsystem.scpi.inbound.events;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorizationCategoryModel;
import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorySchemaModel;
import com.sap.hybris.crm.sapcrmcategoryschema.service.SapCrmCategoriesService;


/**
 *
 */
public class SapCpiCrmServiceTicketPersistenceHook implements PrePersistHook
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiCrmServiceTicketPersistenceHook.class);

	private ModelService modelService;
	private SapCrmCategoriesService sapCategoriesService;
	private TicketEventEmailStrategy ticketEventEmailStrategy;
	private TicketEventStrategy ticketEventStrategy;
	private TicketDao ticketDao;

	@Override
	public Optional<ItemModel> execute(final ItemModel item)
	{
		LOG.info("The persistence hook SapCpiCrmServiceTicketPersistenceHook is called!");

		if (item instanceof CsTicketModel)
		{
			final CsTicketModel lServiceTicket = (CsTicketModel) item;

			if (lServiceTicket.getResolutionData() != null)
			{
				final String ticketID = lServiceTicket.getTicketID();
				final String ticketHeadline = lServiceTicket.getHeadline();
				final CsTicketPriority priority = lServiceTicket.getPriority();
				final CsTicketCategory category = lServiceTicket.getCategory();
				createTicketChangeEvent(lServiceTicket.getResolutionData(), ticketID,
						(null == priority) ? CsTicketPriority.MEDIUM : CsTicketPriority.valueOf(priority.getCode()),
						(null == category) ? CsTicketCategory.COMPLAINT : CsTicketCategory.valueOf(category.getCode()), ticketHeadline);
			}

			if (lServiceTicket.getCpi_categorizationcode() != null)
			{
				final List<String> categoryData = Arrays.asList(StringUtils.split(lServiceTicket.getCpi_categorizationcode(), '|'));
				final String categoryCode = categoryData.get(0);
				final String catalogType = categoryData.get(1);
				final String catalogName = categoryData.get(2);
				if (categoryCode != null && catalogType != null && catalogName != null)
				{
					lServiceTicket.setReasonCategory(getCategoryFromSchema(categoryCode, catalogType, catalogName));
				}
			}
			modelService.save(lServiceTicket);
			return Optional.empty();

		}
		return Optional.of(item);
	}

	public void createTicketChangeEvent(final String notes, final String ticketID, final CsTicketPriority priority,
			final CsTicketCategory category, final String headline)
	{
		LOG.debug(" Trying to trigger ticket change event for ticket with ticket id:" + ticketID);

		CsTicketModel ticket = getTicketById(ticketID);
		if (ticket != null)
		{
			addNoteToExistingTicket(notes, ticket);
		}
		else
		{
			ticket = new CsTicketModel();
			ticket.setTicketID(ticketID);
			ticket.setCategory(category);
			ticket.setPriority(priority);
			ticket.setState(CsTicketState.NEW);
			ticket.setHeadline(headline);
			final CsCustomerEventModel creationEvent = this.ticketEventStrategy.createCreationEventForTicket(ticket,
					CsEventReason.FIRSTCONTACT, CsInterventionType.CALL, notes);
			this.ticketEventStrategy.ensureTicketEventSetupForCreationEvent(ticket, creationEvent);

			this.modelService.saveAll(new Object[]
			{ ticket, creationEvent });
			this.modelService.refresh(ticket);

			this.ticketEventEmailStrategy.sendEmailsForEvent(ticket, creationEvent);
		}
	}

	public CategorizationCategoryModel getCategoryFromSchema(final String reasonCatCode, final String catalogType,
			final String catalogName)
	{
		LOG.debug("Trying to resolve the reason category for code :" + reasonCatCode);
		final List<CategorizationCategoryModel> categoriesList = this.sapCategoriesService.getCategoriesByCode(reasonCatCode);
		CategorizationCategoryModel category = null;
		if (categoriesList != null && !categoriesList.isEmpty())
		{
			final CategorySchemaModel catSchema = sapCategoriesService.getModelByCategoryAndSchema(catalogName, categoriesList,
					catalogType);
			if (null != catSchema)
			{
				category = catSchema.getSourceCategory();
			}
		}
		return category;
	}

	/**
	 * Add note to existing ticket and trigger email for that ticket
	 *
	 * @param notes
	 * @param ticket
	 */
	private void addNoteToExistingTicket(final String notes, final CsTicketModel ticket)
	{
		boolean isNotesExisting = false;
		final Pattern regex = Pattern.compile("[_]{2}\\d+[_]{2}");
		final Matcher matcher = regex.matcher(notes);
		if (notes != null && (matcher.find() || isSameNoteExisting(notes, ticket)))
		{
			isNotesExisting = true;
		}
		if (!isNotesExisting)
		{
			final CsCustomerEventModel ret = this.ticketEventStrategy.createNoteForTicket(ticket, CsInterventionType.CALL,
					CsEventReason.FIRSTCONTACT, notes, null);
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
	private boolean isSameNoteExisting(final String notes, final CsTicketModel ticket)
	{
		boolean isExist = false;
		for (final CsTicketEventModel note : ticket.getEvents())
		{
			if (note.getText().equals(notes))
			{
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
	private CsTicketModel getTicketById(final String ticketID)
	{
		final List<CsTicketModel> ticketList = this.ticketDao.findTicketsById(ticketID);
		CsTicketModel ticket = null;
		if (null != ticketList && !ticketList.isEmpty())
		{
			ticket = ticketList.get(0);
		}
		return ticket;
	}


	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 *
	 * modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param ticketEventEmailStrategy
	 *           the ticketEventEmailStrategy to set
	 */
	public void setTicketEventEmailStrategy(final TicketEventEmailStrategy ticketEventEmailStrategy)
	{
		this.ticketEventEmailStrategy = ticketEventEmailStrategy;
	}

	/**
	 * @param ticketEventStrategy
	 *           the ticketEventStrategy to set
	 */
	public void setTicketEventStrategy(final TicketEventStrategy ticketEventStrategy)
	{
		this.ticketEventStrategy = ticketEventStrategy;
	}

	/**
	 * @param ticketDao
	 *           the ticketDao to set
	 */
	public void setTicketDao(final TicketDao ticketDao)
	{
		this.ticketDao = ticketDao;
	}

	/**
	 * @param sapCategoriesService
	 *           the sapCategoriesService to set
	 */
	public void setSapCategoriesService(final SapCrmCategoriesService sapCategoriesService)
	{
		this.sapCategoriesService = sapCategoriesService;
	}


}
