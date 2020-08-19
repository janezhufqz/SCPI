/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package sap.sapcpicrmcomplaint.service.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.ticket.model.CsTicketModel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import sap.sapcpicrmcomplaint.model.SAPCpiOutboundComplaintConfigModel;
import sap.sapcpicrmcomplaint.model.SAPCpiOutboundComplaintModel;
import sap.sapcpicrmcomplaint.model.SAPCpiOutboundComplaintOrderItemModel;
import sap.sapcpicrmcomplaint.service.SapCpiComplaintConversionService;


/**
 *
 */
public class SapCpiComplaintConversionServiceImpl implements SapCpiComplaintConversionService
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiComplaintConversionServiceImpl.class);

	private ModelService modelService;
	private SAPGlobalConfigurationDAO globalConfigurationDAO;
	private BaseStoreService baseStoreService;

	@Resource(name = "complaintStateMap")
	private Map<String, String> complaintStateMap;

	@Resource(name = "complaintPriorityMap")
	private Map<String, String> complaintPriorityMap;


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

	public Map<String, String> getComplaintStateMap()
	{
		return complaintStateMap;
	}

	public void setComplaintStateMap(final Map<String, String> complaintStateMap)
	{
		this.complaintStateMap = complaintStateMap;
	}

	public Map<String, String> getComplaintPriorityMap()
	{
		return complaintPriorityMap;
	}

	public void setComplaintPriorityMap(final Map<String, String> complaintPriorityMap)
	{
		this.complaintPriorityMap = complaintPriorityMap;
	}

	@Override
	public SAPCpiOutboundComplaintModel convertComplaintToOutboundComplaint(final CsTicketModel pComplaint, final String pNotes)
	{
		final SAPCpiOutboundComplaintModel sapCpiOutboundComplaintModel = getModelService()
				.create(SAPCpiOutboundComplaintModel.class);


		sapCpiOutboundComplaintModel.setComplaintId(pComplaint.getTicketID());
		sapCpiOutboundComplaintModel.setIsComplaintReplicated(Boolean.valueOf(pComplaint.isIsTicketReplicated()).toString());
		sapCpiOutboundComplaintModel.setHeadline(pComplaint.getHeadline());
		sapCpiOutboundComplaintModel.setB2bunit(null);
		sapCpiOutboundComplaintModel.setCategory(pComplaint.getReasonCategory().getCode());
		sapCpiOutboundComplaintModel.setState(getState(pComplaint.getState().getCode()));
		sapCpiOutboundComplaintModel.setPriority(getPriority(pComplaint.getPriority().getCode()));
		sapCpiOutboundComplaintModel.setNotes(pNotes);


		final UserModel lUserModel = pComplaint.getCustomer();

		if (lUserModel instanceof CustomerModel)
		{
			final CustomerModel b2ccustomer = (CustomerModel) lUserModel;
			sapCpiOutboundComplaintModel.setCustomer(b2ccustomer.getCustomerID());
		}

		setOrderData(pComplaint, sapCpiOutboundComplaintModel);

		final SAPCpiOutboundComplaintConfigModel config = getModelService().create(SAPCpiOutboundComplaintConfigModel.class);


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
				config.setUrl(logicalSystem.getSapHTTPDestination().getTargetURL());
				config.setUserName(logicalSystem.getSapHTTPDestination().getUserid());
			}
			else
			{
				return null;
			}
		}

		final SAPConfigurationModel sapConfiguration = baseStoreService.getCurrentBaseStore().getSAPConfiguration();


		config.setComp_serviceOrg(sapConfiguration.getComp_serviceOrg());
		config.setComp_serviceOrgResp(sapConfiguration.getComp_serviceOrgResp());
		config.setComp_ProcessType(sapConfiguration.getComp_processType());
		config.setComp_servOrgShort(sapConfiguration.getComp_servOrgShort());
		config.setComplaintCatagoryCatalogType(sapConfiguration.getComplaintCatagoryCatalogType());
		config.setComplaintCatagoryCodeGroup(sapConfiguration.getComplaintCatagoryCodeGroup());

		config.setSalesOrg(sapConfiguration.getSapcommon_salesOrganization());
		config.setDistributionChannel(sapConfiguration.getSapcommon_distributionChannel());

		sapCpiOutboundComplaintModel.setSapCpiConfig(config);

		return sapCpiOutboundComplaintModel;
	}

	private void setOrderData(final CsTicketModel pComplaint, final SAPCpiOutboundComplaintModel sapCpiOutboundComplaintModel)
	{
		if (pComplaint.getOrder() != null)
		{
			sapCpiOutboundComplaintModel.setOrderId(pComplaint.getOrder().getCode());
		}

		if (!pComplaint.getAssociatedOrderEntries().isEmpty())
		{
			final Set<SAPCpiOutboundComplaintOrderItemModel> lOrderItemsList = new HashSet<>();

			for (final AbstractOrderEntryModel orderEntry : pComplaint.getAssociatedOrderEntries())
			{
				final SAPCpiOutboundComplaintOrderItemModel sapCpiOutboundComplaintOrderItemModel = getModelService()
						.create(SAPCpiOutboundComplaintOrderItemModel.class);

				if (null != orderEntry.getProduct())
				{
					sapCpiOutboundComplaintOrderItemModel.setProductId(orderEntry.getProduct().getCode());
				}

				sapCpiOutboundComplaintOrderItemModel.setQuantity(orderEntry.getQuantity().toString());
				sapCpiOutboundComplaintOrderItemModel.setItemNumber(convertToCrmEntryNumber(orderEntry.getEntryNumber()).toString());
				sapCpiOutboundComplaintOrderItemModel.setItemUnit(orderEntry.getUnit().getUnitType());

				lOrderItemsList.add(sapCpiOutboundComplaintOrderItemModel);
			}

			sapCpiOutboundComplaintModel.setSapCpiOutboundComplaintOrderItems(lOrderItemsList);
		}
	}

	protected Integer convertToCrmEntryNumber(final Integer number)
	{
		return number.intValue() + 1;
	}

	private String getPriority(final String pPriority)
	{
		if (pPriority != null && complaintPriorityMap.get(pPriority) != null)
		{
			return complaintPriorityMap.get(pPriority);
		}

		return "";
	}

	/**
	 *
	 */
	private String getState(final String pState)
	{
		if (pState != null && complaintStateMap.get(pState) != null)
		{
			return complaintStateMap.get(pState);
		}

		return "";
	}

}
