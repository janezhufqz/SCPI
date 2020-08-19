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

import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.impl.SapCpiOmmOrderMapperService;


/**
 *
 */
public class SapCpiCrmBAOmmOrderMapperService extends SapCpiOmmOrderMapperService
{

	@Override
	protected void mapSapCpiOrderToSAPCpiOrderOutbound(final SapCpiOrder sapCpiOrder,
			final SAPCpiOutboundOrderModel sapCpiOutboundOrder)
	{
		super.mapSapCpiOrderToSAPCpiOrderOutbound(sapCpiOrder, sapCpiOutboundOrder);
		sapCpiOutboundOrder.setAgreementId(sapCpiOrder.getAgreementId());

	}


}
