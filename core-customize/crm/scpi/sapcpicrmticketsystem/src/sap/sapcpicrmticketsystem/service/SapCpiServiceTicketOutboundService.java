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
package sap.sapcpicrmticketsystem.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import rx.Observable;
import sap.sapcpicrmticketingsystem.model.SAPCpiOutboundServiceTicketModel;


/**
 *
 */
public interface SapCpiServiceTicketOutboundService
{
	/**
	 * Send ServiceTicket
	 *
	 * @param SAPCpiOutboundServiceTicketModel
	 *           sapCpiOutboundServiceTicketModel
	 * @return Observable<ResponseEntity <Map>>
	 */
	Observable<ResponseEntity<Map>> sendServiceTicket(SAPCpiOutboundServiceTicketModel sapCpiOutboundServiceTicketModel);
}
