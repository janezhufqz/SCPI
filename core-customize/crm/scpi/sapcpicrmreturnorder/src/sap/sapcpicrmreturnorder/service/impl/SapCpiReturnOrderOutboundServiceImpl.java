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
package sap.sapcpicrmreturnorder.service.impl;

import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import rx.Observable;
import sap.sapcpicrmreturnorder.model.SAPCpiOutboundReturnOrderModel;
import sap.sapcpicrmreturnorder.service.SapCpiReturnOrderOutboundService;


/**
 *
 */
public class SapCpiReturnOrderOutboundServiceImpl implements SapCpiReturnOrderOutboundService
{

	private OutboundServiceFacade outboundServiceFacade;

	@Override
	public Observable<ResponseEntity<Map>> sendReturnOrder(final SAPCpiOutboundReturnOrderModel sapCpiOutboundReturnOrderModel)
	{
		return getOutboundServiceFacade().send(sapCpiOutboundReturnOrderModel, "OutboundReturnOrder", "scpiReturnOrderDestination");
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

