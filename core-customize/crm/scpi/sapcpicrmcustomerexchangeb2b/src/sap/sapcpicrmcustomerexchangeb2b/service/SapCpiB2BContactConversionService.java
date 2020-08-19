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
package sap.sapcpicrmcustomerexchangeb2b.service;

import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BCustomerModel;

import java.util.List;
import java.util.Map;


/**
 *
 */
public interface SapCpiB2BContactConversionService
{


	SAPCpiOutboundB2BCustomerModel convertB2BCustomerToSapCpiBb2BCustomer(List<Map<String, Object>> rawData, String rawType);

}
