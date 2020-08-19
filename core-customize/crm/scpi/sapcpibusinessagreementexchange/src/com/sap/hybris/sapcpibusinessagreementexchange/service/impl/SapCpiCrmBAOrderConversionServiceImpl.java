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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cpi.crm.sapcpicrmorderexchange.service.impl.SapCpiCrmOrderConversionService;
import com.sap.hybris.sapbusinessagreement.constants.BusinessAgreementCsvColumns;


/**
 *
 */
public class SapCpiCrmBAOrderConversionServiceImpl extends SapCpiCrmOrderConversionService
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiCrmBAOrderConversionServiceImpl.class);


	@Override
	public SapCpiOrder convertOrderToSapCpiOrder(final OrderModel orderModel)
	{

		final SapCpiOrder sapCpiOrder = new SapCpiOrder();

		getSapOrderContributor().createRows(orderModel).stream().findFirst().ifPresent(row -> {

							sapCpiOrder.setSapCpiConfig(super.mapOrderConfigInfo(orderModel));

							sapCpiOrder.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
							sapCpiOrder.setBaseStoreUid(mapAttribute(OrderCsvColumns.BASE_STORE, row));
							sapCpiOrder.setCreationDate(mapDateAttribute(OrderCsvColumns.DATE, row));
							sapCpiOrder.setCurrencyIsoCode(mapAttribute(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, row));
							sapCpiOrder.setPaymentMode(mapAttribute(OrderCsvColumns.PAYMENT_MODE, row));
							sapCpiOrder.setDeliveryMode(mapAttribute(OrderCsvColumns.DELIVERY_MODE, row));
							sapCpiOrder.setChannel(mapAttribute(OrderCsvColumns.CHANNEL, row));
							sapCpiOrder.setPurchaseOrderNumber(mapAttribute(OrderCsvColumns.PURCHASE_ORDER_NUMBER, row));
							sapCpiOrder.setAgreementId(mapAttribute(BusinessAgreementCsvColumns.AGREEMENTID, row));
							sapCpiOrder.setTransactionType(orderModel.getStore().getSAPConfiguration().getSapcommon_transactionType());
							sapCpiOrder.setSalesOrganization(orderModel.getStore().getSAPConfiguration()
									.getSapcommon_salesOrganization());
							sapCpiOrder.setDistributionChannel(orderModel.getStore().getSAPConfiguration()
									.getSapcommon_distributionChannel());
							sapCpiOrder.setDivision(orderModel.getStore().getSAPConfiguration().getSapcommon_division());

							orderModel.getStore().getSAPConfiguration().getSapDeliveryModes().stream().filter(
											entry -> entry.getDeliveryMode().getCode().contentEquals(orderModel.getDeliveryMode().getCode()))
									.findFirst().ifPresent(entry -> sapCpiOrder.setShippingCondition(entry.getDeliveryValue()));

							sapCpiOrder.setSapCpiOrderItems(super.mapOrderItems(orderModel));
							sapCpiOrder.setSapCpiPartnerRoles(super.mapOrderPartners(orderModel));
							sapCpiOrder.setSapCpiOrderAddresses(super.mapOrderAddresses(orderModel));
							sapCpiOrder.setSapCpiOrderPriceComponents(super.mapOrderPrices(orderModel));
							sapCpiOrder.setSapCpiCreditCardPayments(super.mapCreditCards(orderModel));

						});

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("SCPI CRM order object: %n %s",
					ReflectionToStringBuilder.toString(sapCpiOrder, new RecursiveToStringStyle()).toString()));
		}

		return sapCpiOrder;

	}


}
