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
package sap.sapcpicrmreturnorder.service.inbound;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.returns.model.ReturnRequestModel;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.crm.inbound.DataHubInboundOrderHelper;


/**
 *
 */
public class SapCpiReturnOrderPGRDeliveryNotificationPersistenceHook implements PrePersistHook
{


	private DataHubInboundOrderHelper inboundHelper;

	public DataHubInboundOrderHelper getInboundHelper()
	{
		return inboundHelper;
	}

	public void setInboundHelper(final DataHubInboundOrderHelper inboundHelper)
	{
		this.inboundHelper = inboundHelper;
	}

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiReturnOrderPGRDeliveryNotificationPersistenceHook.class);

	@Override
	public Optional<ItemModel> execute(final ItemModel item)
	{
		if (item instanceof ReturnRequestModel)
		{
			LOG.info("The persistence hook SapCpiReturnOrderCreationNotificationPersistenceHook is called!");
			final ReturnRequestModel lReturnRequest = (ReturnRequestModel) item;

			inboundHelper.processOrderDeliveryNotififcationFromHub(lReturnRequest.getCode(), lReturnRequest.getDeliveryDocNumber());

			return Optional.empty();
		}
		return Optional.of(item);
	}



}
