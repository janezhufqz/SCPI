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

import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubResult;
import de.hybris.platform.sap.orderexchange.outbound.impl.AbstractSendToDataHubHelper;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultSendToDataHubResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import com.google.common.base.Preconditions;

import sap.sapcpicrmreturnorder.model.SAPCpiOutboundReturnOrderModel;
import sap.sapcpicrmreturnorder.service.SapCpiReturnOrderConversionService;
import sap.sapcpicrmreturnorder.service.SapCpiReturnOrderExportService;
import sap.sapcpicrmreturnorder.service.SapCpiReturnOrderOutboundService;

/**
 *
 */
public class SapCpiReturnOrderExportServiceImpl extends AbstractSendToDataHubHelper<ReturnRequestModel>
		implements SapCpiReturnOrderExportService
{

	String SUCCESS = "success";
	String RESPONSE_STATUS = "responseStatus";
	String RESPONSE_MESSAGE = "responseMessage";

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiReturnOrderExportServiceImpl.class);

	private SapCpiReturnOrderOutboundService returnOrderOutboundService;

	private SapCpiReturnOrderConversionService returnOrderConversionService;

	public SapCpiReturnOrderOutboundService getReturnOrderOutboundService()
	{
		return returnOrderOutboundService;
	}

	public void setReturnOrderOutboundService(final SapCpiReturnOrderOutboundService returnOrderOutboundService)
	{
		this.returnOrderOutboundService = returnOrderOutboundService;
	}

	public SapCpiReturnOrderConversionService getReturnOrderConversionService()
	{
		return returnOrderConversionService;
	}

	public void setReturnOrderConversionService(final SapCpiReturnOrderConversionService returnOrderConversionService)
	{
		this.returnOrderConversionService = returnOrderConversionService;
	}

	@Override
	public SendToDataHubResult createAndSendRawItem(final ReturnRequestModel pReturnOrder)
	{

		final List<Map<String, Object>> rowsAsNameValuePairs = getRawItemBuilder().rowsAsNameValuePairs(pReturnOrder);

		final SAPCpiOutboundReturnOrderModel convertReturnOrderSapCpiReturnOrder = returnOrderConversionService
				.convertReturnOrderSapCpiReturnOrder(rowsAsNameValuePairs, pReturnOrder);

		getReturnOrderOutboundService().sendReturnOrder(convertReturnOrderSapCpiReturnOrder).subscribe(

				responseEntityMap -> {

					if (isSentSuccessfully(responseEntityMap))
					{
						LOG.info(String.format("The ServiceTicket 123 has been sent to the SAP backend through SCPI! %n%s",
								getPropertyValue(responseEntityMap, "responseMessage")));

					}
					else
					{
						LOG.error(String.format("The ServiceTicket 123 has not been sent to the SAP backend! %n%s",
								getPropertyValue(responseEntityMap, "responseMessage")));
					}

				}
				// onError
				, error -> LOG.error(String.format("The ServiceTicket 123 has not been sent to the SAP backend through SCPI! %n%s",
						error.getMessage()))

		);

		return DefaultSendToDataHubResult.OKAY;
	}

	boolean isSentSuccessfully(final ResponseEntity<Map> responseEntityMap)
	{
		return "success".equalsIgnoreCase(getPropertyValue(responseEntityMap, "responseStatus"))
				&& responseEntityMap.getStatusCode().is2xxSuccessful();
	}

	String getPropertyValue(final ResponseEntity<Map> responseEntityMap, final String property)
	{

		final Object next = responseEntityMap.getBody().keySet().iterator().next();
		Preconditions.checkArgument(next != null,
				String.format("SCPI response entity key set cannot be null for property [%s]!", property));

		final String responseKey = next.toString();
		Preconditions.checkArgument(responseKey != null && !responseKey.isEmpty(),
				String.format("SCPI response property can neither be null nor empty for property [%s]!", property));

		final Object propertyValue = ((HashMap) responseEntityMap.getBody().get(responseKey)).get(property);
		Preconditions.checkArgument(propertyValue != null,
				String.format("SCPI response property [%s] value cannot be null!", property));

		return propertyValue.toString();

	}
}
