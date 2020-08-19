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
package com.sap.hybris.sapcpibusinessagreementexchange.service.impl;

import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.sap.hybris.sapcpibusinessagreementexchange.model.SAPCpiBusinessAgreementOutboundModel;
import com.sap.hybris.sapcpibusinessagreementexchange.service.SapCpiCrmBAOutboundService;

import rx.Observable;


/**
 *
 */
public class SapCpiCrmBAOutboundServiceImpl implements SapCpiCrmBAOutboundService
{

	// BA Outbound
	private static final String OUTBOUND_BA_OBJECT = "OutboundBusinessagreement";
	private static final String OUTBOUND_BA_DESTINATION = "scpiBusinessagreementDestination";

	private OutboundServiceFacade outboundServiceFacade;

	@Override
	public Observable<ResponseEntity<Map>> sendBusinessagreement(final SAPCpiBusinessAgreementOutboundModel sapCpiBAOutboundModel)
	{
		return getOutboundServiceFacade().send(sapCpiBAOutboundModel, OUTBOUND_BA_OBJECT, OUTBOUND_BA_DESTINATION);
	}

	/**
	 * @return the outboundServiceFacade
	 */
	public OutboundServiceFacade getOutboundServiceFacade()
	{
		return outboundServiceFacade;
	}

	/**
	 * @param outboundServiceFacade
	 *           the outboundServiceFacade to set
	 */
	public void setOutboundServiceFacade(final OutboundServiceFacade outboundServiceFacade)
	{
		this.outboundServiceFacade = outboundServiceFacade;
	}

}
