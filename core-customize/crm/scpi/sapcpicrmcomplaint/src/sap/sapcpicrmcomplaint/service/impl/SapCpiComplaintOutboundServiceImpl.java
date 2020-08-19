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

import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import rx.Observable;
import sap.sapcpicrmcomplaint.model.SAPCpiOutboundComplaintModel;
import sap.sapcpicrmcomplaint.service.SapCpiComplaintOutboundService;

/**
 *
 */
public class SapCpiComplaintOutboundServiceImpl implements SapCpiComplaintOutboundService
{
	private OutboundServiceFacade outboundServiceFacade;

	@Override
	public Observable<ResponseEntity<Map>> sendComplaint(final SAPCpiOutboundComplaintModel sapCpiOutboundComplaint)
	{
		return getOutboundServiceFacade().send(sapCpiOutboundComplaint, "OutboundComplaint", "scpiComplaintDestination");
	}

	/**
	 * @return the outboundServiceFacade
	 */
	public OutboundServiceFacade getOutboundServiceFacade()
	{
		return outboundServiceFacade;
	}

	/**
	 * @param outboundServiceFacade the outboundServiceFacade to set
	 */
	public void setOutboundServiceFacade(OutboundServiceFacade outboundServiceFacade)
	{
		this.outboundServiceFacade = outboundServiceFacade;
	}

}
