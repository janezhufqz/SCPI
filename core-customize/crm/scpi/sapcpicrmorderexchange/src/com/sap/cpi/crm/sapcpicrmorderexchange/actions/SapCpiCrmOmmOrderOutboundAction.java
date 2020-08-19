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
package com.sap.cpi.crm.sapcpicrmorderexchange.actions;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.RESPONSE_MESSAGE;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.getPropertyValue;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.isSentSuccessfully;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.sap.orderexchange.constants.SapOrderExchangeActionConstants;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapcpiorderexchange.actions.SapCpiOmmOrderOutboundAction;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderOutboundConversionService;
import de.hybris.platform.task.RetryLaterException;

import org.apache.log4j.Logger;


/**
 *
 */
public class SapCpiCrmOmmOrderOutboundAction extends SapCpiOmmOrderOutboundAction
{


	private static final Logger LOG = Logger.getLogger(SapCpiCrmOmmOrderOutboundAction.class);

	private SapCpiOutboundService sapCpiOutboundService;
	private SapCpiOrderOutboundConversionService sapCpiOrderOutboundConversionService;


	@Override
	public void executeAction(final OrderProcessModel process) throws RetryLaterException
	{

		final OrderModel order = process.getOrder();
		LOG.info("************** CRM Action Executing now ******************");

		getSapCpiOutboundService().sendOrder(getSapCpiOrderOutboundConversionService().convertOrderToSapCpiOrder(order)).subscribe(

				// onNext
				responseEntityMap -> {

					Registry.activateMasterTenant();

					if (isSentSuccessfully(responseEntityMap))
					{

						setOrderStatus(order, ExportStatus.EXPORTED);
						resetEndMessage(process);
						LOG.info(String.format("The OMM order [%s] has been successfully sent to the SAP backend through SCPI! %n%s",
								order.getCode(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));

					}
					else
					{

						setOrderStatus(order, ExportStatus.NOTEXPORTED);
						LOG.error(String.format("The OMM order [%s] has not been sent to the SAP backend! %n%s", order.getCode(),
								getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));

					}

					final String eventName = new StringBuilder()
							.append(SapOrderExchangeActionConstants.ERP_ORDER_SEND_COMPLETION_EVENT).append(order.getCode()).toString();
					getBusinessProcessService().triggerEvent(eventName);

				}

				// onError
				,
				error -> {

					Registry.activateMasterTenant();

					setOrderStatus(order, ExportStatus.NOTEXPORTED);
					LOG.error(String.format("The OMM order [%s] has not been sent to the SAP backend through SCPI! %n%s",
							order.getCode(), error.getMessage()));

					final String eventName = new StringBuilder()
							.append(SapOrderExchangeActionConstants.ERP_ORDER_SEND_COMPLETION_EVENT).append(order.getCode()).toString();
					getBusinessProcessService().triggerEvent(eventName);

				}

		);

	}





	/**
	 * @return the sapCpiOutboundService
	 */
	@Override
	public SapCpiOutboundService getSapCpiOutboundService()
	{
		return sapCpiOutboundService;
	}

	/**
	 * @param sapCpiOutboundService
	 *           the sapCpiOutboundService to set
	 */
	@Override
	public void setSapCpiOutboundService(final SapCpiOutboundService sapCpiOutboundService)
	{
		this.sapCpiOutboundService = sapCpiOutboundService;
	}

	/**
	 * @return the sapCpiOrderOutboundConversionService
	 */
	@Override
	public SapCpiOrderOutboundConversionService getSapCpiOrderOutboundConversionService()
	{
		return sapCpiOrderOutboundConversionService;
	}

	/**
	 * @param sapCpiOrderOutboundConversionService
	 *           the sapCpiOrderOutboundConversionService to set
	 */
	@Override
	public void setSapCpiOrderOutboundConversionService(
			final SapCpiOrderOutboundConversionService sapCpiOrderOutboundConversionService)
	{
		this.sapCpiOrderOutboundConversionService = sapCpiOrderOutboundConversionService;
	}






}
