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
package com.sap.hybris.sapbusinessagreement.outbound.impl;

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.sapcrmorderexchange.constants.CrmOrderCsvColumns;
import de.hybris.platform.sap.sapcrmorderexchange.outbound.impl.DefaultCrmOrderContributor;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.hybris.sapbusinessagreement.constants.BusinessAgreementCsvColumns;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementHeaderModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;


/**
 *
 */
public class DefaultCrmAgreementOrderContributor extends DefaultCrmOrderContributor
{

	private final Set<String> columns = new HashSet<>(Arrays.asList(OrderCsvColumns.ORDER_ID, OrderCsvColumns.DATE,
			OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, OrderCsvColumns.PAYMENT_MODE, OrderCsvColumns.DELIVERY_MODE,
			OrderCsvColumns.BASE_STORE, CrmOrderCsvColumns.NAMED_DELIVERY_DATE, BusinessAgreementCsvColumns.AGREEMENTID));

	private ModelService modelService;

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	public Set<String> getColumns()
	{
		columns.addAll(this.getBatchIdAttributes().keySet());
		return columns;
	}

	@Override
	public List<Map<String, Object>> createRows(final OrderModel order)
	{

		final Map<String, Object> row = new HashMap<>();

		final CustomerModel customer = (CustomerModel) order.getUser();

		BusinessAgreementModel agreementModel = null;

		if (order.getPaymentInfo() instanceof CreditCardPaymentInfoModel)
		{
			final CreditCardPaymentInfoModel cc = (CreditCardPaymentInfoModel) order.getPaymentInfo();
			if (!customer.getBusinessagreement().isEmpty() && customer.getType().equals(CustomerType.REGISTERED))
			{

				agreementModel = fetchAgreementId(customer.getBusinessagreement(), cc.getCardInc());
			}
		}

		row.put(BusinessAgreementCsvColumns.AGREEMENTID, null != agreementModel ? agreementModel.getId() : "");
		if (null != agreementModel)
		{
			order.setBusinessAgreement(agreementModel);
			modelService.save(order);
		}
		row.put(OrderCsvColumns.ORDER_ID, order.getCode());
		row.put(OrderCsvColumns.DATE, order.getDate());
		row.put(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, order.getCurrency().getIsocode());
		final DeliveryModeModel deliveryMode = order.getDeliveryMode();
		row.put(OrderCsvColumns.DELIVERY_MODE, deliveryMode != null ? deliveryMode.getCode() : "");
		row.put(OrderCsvColumns.BASE_STORE, order.getStore().getUid());
		getBatchIdAttributes().forEach(row::putIfAbsent);
		row.put("dh_batchId", order.getCode());
		return Arrays.asList(row);

	}

	/**
	  *
	  */
	protected BusinessAgreementModel fetchAgreementId(final Set<BusinessAgreementModel> agreements, final String cardInc)
	{
		for (final BusinessAgreementModel ba : agreements)
		{
			for (final BusinessAgreementHeaderModel bh : ba.getBusinessagreementheader())
			{
				if (null != bh.getCreditcardinc() && bh.getCreditcardinc().equalsIgnoreCase(cardInc))
				{
					return ba;
				}
			}
		}
		return null;
	}
}
