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
package com.sap.hybris.sapcpibusinessagreementexchange.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import rx.Observable;

import com.sap.hybris.sapcpibusinessagreementexchange.model.SAPCpiBusinessAgreementOutboundModel;


/**
 *
 */
public interface SapCpiCrmBAOutboundService
{

	public Observable<ResponseEntity<Map>> sendBusinessagreement(SAPCpiBusinessAgreementOutboundModel sapCpiBAOutboundModel);
}
