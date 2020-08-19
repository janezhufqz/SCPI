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

import de.hybris.platform.ticket.model.CsTicketModel;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import com.google.common.base.Preconditions;
import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.sap.hybris.crm.sapcrmcomplaintexchange.outbound.ComplaintExportService;

import sap.sapcpicrmcomplaint.model.SAPCpiOutboundComplaintModel;
import sap.sapcpicrmcomplaint.service.SapCpiComplaintConversionService;
import sap.sapcpicrmcomplaint.service.SapCpiComplaintOutboundService;


/**
 *
 */
public class SapCpiComplaintExportServiceImpl extends ComplaintExportService
{
	private static final Logger LOG = LoggerFactory.getLogger(SapCpiComplaintExportServiceImpl.class);

	private SapCpiComplaintConversionService sapCpiComplaintConversionService;

	private SapCpiComplaintOutboundService sapCpiComplaintOutboundService;

	public SapCpiComplaintConversionService getSapCpiComplaintConversionService()
	{
		return sapCpiComplaintConversionService;
	}

	public void setSapCpiComplaintConversionService(final SapCpiComplaintConversionService sapCpiComplaintConversionService)
	{
		this.sapCpiComplaintConversionService = sapCpiComplaintConversionService;
	}

	public SapCpiComplaintOutboundService getSapCpiComplaintOutboundService()
	{
		return sapCpiComplaintOutboundService;
	}

	public void setSapCpiComplaintOutboundService(final SapCpiComplaintOutboundService sapCpiComplaintOutboundService)
	{
		this.sapCpiComplaintOutboundService = sapCpiComplaintOutboundService;
	}

	@Override
	public void sendComplaintData(final CsTicketModel pComplaint, final String notes) throws DataHubCommunicationException
	{
		final SAPCpiOutboundComplaintModel sapCpiOutboundComplaint = getSapCpiComplaintConversionService()
				.convertComplaintToOutboundComplaint(pComplaint, notes);

		if (Boolean.valueOf(pComplaint.isIsTicketReplicated()) && "".equalsIgnoreCase(notes))
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

		getSapCpiComplaintOutboundService().sendComplaint(sapCpiOutboundComplaint).subscribe(

				responseEntityMap -> {

					if (isSentSuccessfully(responseEntityMap))
					{
						LOG.info(String.format("The Complaint [%s] has been sent to the SAP backend through SCPI! %n%s",
								sapCpiOutboundComplaint.getComplaintId(), getPropertyValue(responseEntityMap, "responseMessage")));

					}
					else
					{
						LOG.error(String.format("The Complaint [%s] has not been sent to the SAP backend! %n%s",
								sapCpiOutboundComplaint.getComplaintId(), getPropertyValue(responseEntityMap, "responseMessage")));
					}

				}
				// onError
				, error -> LOG.error(String.format("The Complaint [%s] has not been sent to the SAP backend through SCPI! %n%s",
						sapCpiOutboundComplaint.getComplaintId(), error.getMessage()))

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
