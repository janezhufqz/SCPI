/*
 *
 *  [y] hybris Platform
 *
 *  Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 *  This software is the confidential and proprietary information of SAP
 *  ("Confidential Information"). You shall not disclose such Confidential
 *  Information and shall use it only in accordance with the terms of the
 *  license agreement you entered into with SAP.
 * /
 */

package de.hybris.platform.sap.sapcrmorderexchange.inbound.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.orderexchange.datahub.inbound.impl.DefaultDataHubInboundDeliveryHelper;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DefaultCRMDataHubInboundDeliveryHelper extends DefaultDataHubInboundDeliveryHelper{
	
	public static final String IDOC_DATE_FORMAT = "yyyyMMddhhmmss";
	
	@Override
	protected Date convertStringToDate(final String dateString)
	{
		Date date = null;
		if (isDateSet(dateString))
		{
			String dateStringfield = dateString.trim();
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(IDOC_DATE_FORMAT);
			try
			{
				date = simpleDateFormat.parse(dateStringfield);
			}
			catch (final ParseException e)
			{
				throw new IllegalArgumentException("Date " + dateString + " can not be converted to a date", e);
			}
		}
		return date;
	}
	
	protected void setWarehouseOfConsignment(final String warehouseReferenceId, final AbstractOrderModel order,
			final ConsignmentModel consignment)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder queryString = new StringBuilder("SELECT {").append(WarehouseModel.PK).append("} ");
		queryString.append("FROM {").append(WarehouseModel._TYPECODE).append("} ");
		queryString.append("WHERE {").append(WarehouseModel.SAPCRMCODE).append("} = (?sapCrmCode)");
		params.put("sapCrmCode", warehouseReferenceId);
		final SearchResult<WarehouseModel> searchResults = getFlexibleSearchService().search(queryString.toString(), params);
		WarehouseModel warehouseResult = searchResults.getResult().get(0);
		final String crmDeliverPlant = warehouseResult.getCode();
		for (final WarehouseModel warehouse : order.getStore().getWarehouses())
		{
			if (warehouse.getCode().equals(crmDeliverPlant))
			{
				consignment.setWarehouse(warehouse);
				return;
			}
		}
	}
}
