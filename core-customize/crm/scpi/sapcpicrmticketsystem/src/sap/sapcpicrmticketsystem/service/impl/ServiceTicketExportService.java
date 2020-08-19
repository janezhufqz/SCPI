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

import de.hybris.platform.ticket.model.CsTicketModel;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import com.google.common.base.Preconditions;
import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.sap.hybris.crm.sapcrmticketsystem.outbound.CsTicketExportService;

import sap.sapcpicrmticketingsystem.model.SAPCpiOutboundServiceTicketModel;
import sap.sapcpicrmticketsystem.service.SAPCpiOutboundServiceTicketConversionService;
import sap.sapcpicrmticketsystem.service.SapCpiServiceTicketOutboundService;


/**
 *
 */
public class ServiceTicketExportService extends CsTicketExportService
{
	private static final Logger LOG = LoggerFactory.getLogger(ServiceTicketExportService.class);

	private SAPCpiOutboundServiceTicketConversionService sapCpiServiceTicketConversionService;

	private SapCpiServiceTicketOutboundService serviceTicketOutboundService;

	public SAPCpiOutboundServiceTicketConversionService getSapCpiServiceTicketConversionService()
	{
		return sapCpiServiceTicketConversionService;
	}

	public void setSapCpiServiceTicketConversionService(
			final SAPCpiOutboundServiceTicketConversionService sapCpiServiceTicketConversionService)
	{
		this.sapCpiServiceTicketConversionService = sapCpiServiceTicketConversionService;
	}

	public SapCpiServiceTicketOutboundService getServiceTicketOutboundService()
	{
		return serviceTicketOutboundService;
	}

	public void setServiceTicketOutboundService(final SapCpiServiceTicketOutboundService serviceTicketOutboundService)
	{
		this.serviceTicketOutboundService = serviceTicketOutboundService;
	}

	@Override
	public void sendCsTicketData(final CsTicketModel pServiceTicket, final String notes) throws DataHubCommunicationException
	{

		final SAPCpiOutboundServiceTicketModel sapCpiOutboundSrvTktModel = getSapCpiServiceTicketConversionService()
				.convertSrvTicketToSapCpiSrvTkt(pServiceTicket, notes);

		if (Boolean.valueOf(pServiceTicket.isIsTicketReplicated()) && "".equalsIgnoreCase(notes))
		{
			try
			{
				Thread.sleep(10000);
			}
			catch (final InterruptedException ie)
			{
				LOG.debug("Exception occurred : " + ie);
			}
		}

		getServiceTicketOutboundService().sendServiceTicket(sapCpiOutboundSrvTktModel).subscribe(

				responseEntityMap -> {

					if (isSentSuccessfully(responseEntityMap))
					{
						LOG.info(String.format("The ServiceTicket [%s] has been sent to the SAP backend through SCPI! %n%s",
								pServiceTicket.getTicketID(), getPropertyValue(responseEntityMap, "responseMessage")));

					}
					else
					{
						LOG.error(String.format("The ServiceTicket [%s] has not been sent to the SAP backend! %n%s",
								pServiceTicket.getTicketID(), getPropertyValue(responseEntityMap, "responseMessage")));
					}

				}
				// onError
				, error -> LOG.error(String.format("The ServiceTicket [%s] has not been sent to the SAP backend through SCPI! %n%s",
						pServiceTicket.getTicketID(), error.getMessage()))

		);

	}

	boolean isSentSuccessfully(final ResponseEntity<Map> responseEntityMap)
	{
		return "success".equalsIgnoreCase(getPropertyValue(responseEntityMap, "responseStatus"))
				&& responseEntityMap.getStatusCode().is2xxSuccessful();
	}

	String getPropertyValue(final ResponseEntity<Map> responseEntityMap, final String property)
	{

		final Object next = responseEntityMap.getBody().keySet().iterator().next();
		Preconditions.checkArgument(next != null,
				String.format("SCPI response entity key set cannot be null for property [%s]!", property));

		final String responseKey = next.toString();
		Preconditions.checkArgument(responseKey != null && !responseKey.isEmpty(),
				String.format("SCPI response property can neither be null nor empty for property [%s]!", property));

		final Object propertyValue = ((HashMap) responseEntityMap.getBody().get(responseKey)).get(property);
		Preconditions.checkArgument(propertyValue != null,
				String.format("SCPI response property [%s] value cannot be null!", property));

		return propertyValue.toString();

	}

}
