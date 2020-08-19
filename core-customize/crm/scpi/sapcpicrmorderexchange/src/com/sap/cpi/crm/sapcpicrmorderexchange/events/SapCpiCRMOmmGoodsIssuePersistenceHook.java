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
package com.sap.cpi.crm.sapcpicrmorderexchange.events;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.sap.orderexchange.datahub.inbound.DataHubInboundDeliveryHelper;
import de.hybris.platform.sap.sapcrmorderexchange.inbound.impl.DefaultCRMDataHubInboundDeliveryHelper;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class SapCpiCRMOmmGoodsIssuePersistenceHook implements PrePersistHook
{
	private static final Logger LOG = LoggerFactory.getLogger(SapCpiCRMOmmGoodsIssuePersistenceHook.class);

	private final SimpleDateFormat SDF = new SimpleDateFormat(DefaultCRMDataHubInboundDeliveryHelper.IDOC_DATE_FORMAT);

	private DataHubInboundDeliveryHelper sapDataHubInboundDeliveryHelper;

	@Override
	public Optional<ItemModel> execute(final ItemModel item)
	{
		if (item instanceof OrderModel)
		{
			LOG.info("The persistence hook SapCpiCRMOmmGoodsIssuePersistenceHook is called!");
			final OrderModel orderModel = (OrderModel) item;
			getSapDataHubInboundDeliveryHelper().processDeliveryAndGoodsIssue(orderModel.getCode(), orderModel.getSapPlantCode(),
					SDF.format(orderModel.getSapGoodsIssueDate()));
			return Optional.empty();
		}
		return Optional.of(item);
	}

	/**
	 * @return the sapDataHubInboundDeliveryHelper
	 */
	public DataHubInboundDeliveryHelper getSapDataHubInboundDeliveryHelper()
	{
		return sapDataHubInboundDeliveryHelper;
	}

	/**
	 * @param sapDataHubInboundDeliveryHelper
	 *           the sapDataHubInboundDeliveryHelper to set
	 */
	public void setSapDataHubInboundDeliveryHelper(final DataHubInboundDeliveryHelper sapDataHubInboundDeliveryHelper)
	{
		this.sapDataHubInboundDeliveryHelper = sapDataHubInboundDeliveryHelper;
	}



}
