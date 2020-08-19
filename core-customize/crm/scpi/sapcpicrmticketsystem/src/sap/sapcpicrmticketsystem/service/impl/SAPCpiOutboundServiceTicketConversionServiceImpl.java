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
package sap.sapcpicrmticketsystem.service.impl;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.ticket.model.CsTicketModel;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import sap.sapcpicrmticketingsystem.model.SAPCpiOutboundServiceTicketModel;
import sap.sapcpicrmticketingsystem.model.SAPCpiServiceTicketOutboundConfigModel;
import sap.sapcpicrmticketsystem.service.SAPCpiOutboundServiceTicketConversionService;


/**
 *
 */
public class SAPCpiOutboundServiceTicketConversionServiceImpl implements SAPCpiOutboundServiceTicketConversionService
{
	private static final Logger LOG = LoggerFactory.getLogger(SAPCpiOutboundServiceTicketConversionServiceImpl.class);

	private ModelService modelService;
	private SAPGlobalConfigurationDAO globalConfigurationDAO;
	private BaseStoreService baseStoreService;

	@Resource(name = "ticketStateMap")
	private Map<String, String> ticketStateMap;

	@Resource(name = "ticketPriorityMap")
	private Map<String, String> ticketPriorityMap;

	@Resource(name = "ticketCategoryMap")
	private Map<String, String> ticketCategoryMap;

	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected SAPGlobalConfigurationDAO getGlobalConfigurationDAO()
	{
		return globalConfigurationDAO;
	}

	@Required
	public void setGlobalConfigurationDAO(final SAPGlobalConfigurationDAO globalConfigurationDAO)
	{
		this.globalConfigurationDAO = globalConfigurationDAO;
	}

	public Map<String, String> getTicketStateMap()
	{
		return ticketStateMap;
	}

	public void setTicketStateMap(final Map<String, String> ticketStateMap)
	{
		this.ticketStateMap = ticketStateMap;
	}

	public Map<String, String> getTicketPriorityMap()
	{
		return ticketPriorityMap;
	}

	public void setTicketPriorityMap(final Map<String, String> ticketPriorityMap)
	{
		this.ticketPriorityMap = ticketPriorityMap;
	}

	public Map<String, String> getTicketCategoryMap()
	{
		return ticketCategoryMap;
	}

	public void setTicketCategoryMap(final Map<String, String> ticketCategoryMap)
	{
		this.ticketCategoryMap = ticketCategoryMap;
	}

	@Override
	public SAPCpiOutboundServiceTicketModel convertSrvTicketToSapCpiSrvTkt(final CsTicketModel pServiceTicket, final String notes)
	{

		final SAPCpiOutboundServiceTicketModel sapCpiOutboundSrvTktModel = getModelService()
				.create(SAPCpiOutboundServiceTicketModel.class);


		final UserModel lUserModel = pServiceTicket.getCustomer();

		if (lUserModel instanceof CustomerModel)
		{
			final CustomerModel b2ccustomer = (CustomerModel) lUserModel;
			sapCpiOutboundSrvTktModel.setCustomer(b2ccustomer.getCustomerID());
		}

		if (pServiceTicket.getOrder() != null)
		{
			sapCpiOutboundSrvTktModel.setOrder(pServiceTicket.getOrder().getCode());
		}

		sapCpiOutboundSrvTktModel.setTicketID(pServiceTicket.getTicketID());
		sapCpiOutboundSrvTktModel.setCategory(pServiceTicket.getReasonCategory().getCode());
		sapCpiOutboundSrvTktModel.setState(getState(pServiceTicket.getState().toString()));
		sapCpiOutboundSrvTktModel.setPriority(getPriority(pServiceTicket.getPriority().getCode()));
		sapCpiOutboundSrvTktModel.setHeadline(pServiceTicket.getHeadline());

		sapCpiOutboundSrvTktModel.setModifiedTs(pServiceTicket.getModifiedtime().toString());
		sapCpiOutboundSrvTktModel.setIsTicketReplicated(Boolean.valueOf(pServiceTicket.isIsTicketReplicated()).toString());
		sapCpiOutboundSrvTktModel.setInstalledBaseId(null);
		sapCpiOutboundSrvTktModel.setComponentId(null);

		sapCpiOutboundSrvTktModel.setNotes(notes);



		final SAPCpiServiceTicketOutboundConfigModel config = getModelService()
				.create(SAPCpiServiceTicketOutboundConfigModel.class);


		final Set<SAPLogicalSystemModel> logicalSystems = getGlobalConfigurationDAO().getSAPGlobalConfiguration()
				.getSapLogicalSystemGlobalConfig();

		if (logicalSystems == null || logicalSystems.isEmpty())
		{
			LOG.error("No Logical system is maintained in back-office");
			return null;
		}
		else
		{
			final SAPLogicalSystemModel logicalSystem = logicalSystems.stream().filter(ls -> ls.isDefaultLogicalSystem()).findFirst()
					.orElse(null);

			if (logicalSystem != null)
			{
				config.setSenderName(logicalSystem.getSenderName());
				config.setSenderPort(logicalSystem.getSenderPort());
				config.setReceiverName(logicalSystem.getSenderName());
				config.setReceiverPort(logicalSystem.getSenderName());
				config.setUrl(logicalSystem.getSapHTTPDestination().getTargetURL());
				config.setUserName(logicalSystem.getSapHTTPDestination().getUserid());
			}
			else
			{
				return null;
			}
		}

		final SAPConfigurationModel sapConfiguration = baseStoreService.getCurrentBaseStore().getSAPConfiguration();

		config.setSap_serviceOrg(sapConfiguration.getSap_serviceOrg());
		config.setSap_serviceOrgResp(sapConfiguration.getSap_serviceOrgResp());
		config.setSap_serviceProcessType(sapConfiguration.getSap_serviceProcessType());
		config.setSap_servOrgShort(sapConfiguration.getSap_servOrgShort());
		config.setServiceRequestCatalogName(sapConfiguration.getServiceRequestCatalogName());
		config.setServiceRequestCategorySchemaId(sapConfiguration.getServiceRequestCategorySchemaId());
		config.setSalesOrg(sapConfiguration.getSapcommon_salesOrganization());
		config.setDistributionChannel(sapConfiguration.getSapcommon_distributionChannel());

		sapCpiOutboundSrvTktModel.setSapCpiConfig(config);

		return sapCpiOutboundSrvTktModel;
	}

	/**
	 *
	 */
	private String getCategory(final String pCategory)
	{
		if (pCategory != null && ticketCategoryMap.get(pCategory) != null)
		{
			return ticketCategoryMap.get(pCategory);
		}
		return "";
	}

	/**
	 *
	 */
	private String getPriority(final String pPriority)
	{
		if (pPriority != null && ticketPriorityMap.get(pPriority) != null)
		{
			return ticketPriorityMap.get(pPriority);
		}

		return "";
	}

	/**
	 *
	 */
	private String getState(final String pState)
	{
		if (pState != null && ticketStateMap.get(pState) != null)
		{
			return ticketStateMap.get(pState);
		}

		return "";
	}

}
