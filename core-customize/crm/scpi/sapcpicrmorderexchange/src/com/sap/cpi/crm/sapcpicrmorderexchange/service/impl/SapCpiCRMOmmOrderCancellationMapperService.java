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
package com.sap.cpi.crm.sapcpicrmorderexchange.service.impl;

import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderCancellation;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.impl.SapCpiOmmOrderCancellationMapperService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 *
 */
public class SapCpiCRMOmmOrderCancellationMapperService extends SapCpiOmmOrderCancellationMapperService
{


	@Override
	protected void mapSapCpiCancelOrderToSapCpiCancelOrderOutbound(final List<SapCpiOrderCancellation> sapCpiOrderCancellations,
			final List<SAPCpiOutboundOrderCancellationModel> sapCpiOutboundOrderCancellations)
	{
		sapCpiOrderCancellations.forEach(cancellation -> {

			final SAPCpiOutboundOrderCancellationModel sapCpiOutboundOrderCancellation = new SAPCpiOutboundOrderCancellationModel();
			sapCpiOutboundOrderCancellation.setSapCpiConfig(mapOrderCancellationConfigInfo(cancellation.getSapCpiConfig()));
			sapCpiOutboundOrderCancellation.setOrderId(cancellation.getOrderId());
			sapCpiOutboundOrderCancellation.setRejectionReason(cancellation.getRejectionReason());
			sapCpiOutboundOrderCancellation.setIsCancelRequest(cancellation.getIsCancelRequest());

			final List<SAPCpiOutboundOrderItemModel> sapCpiOutboundOrderItems = new ArrayList<>();
			cancellation.getSapCpiOrderCancellationItems().forEach(item -> {
				final SAPCpiOutboundOrderItemModel sapCpiOutboundOrderItem = new SAPCpiOutboundOrderItemModel();
				sapCpiOutboundOrderItem.setProductCode(item.getProductCode());
				sapCpiOutboundOrderItem.setEntryNumber(item.getEntryNumber());
				sapCpiOutboundOrderItems.add(sapCpiOutboundOrderItem);

			});

			sapCpiOutboundOrderCancellation.setSapCpiOutboundOrderItems(new HashSet<>(sapCpiOutboundOrderItems));
			sapCpiOutboundOrderCancellations.add(sapCpiOutboundOrderCancellation);

		});

	}


}
