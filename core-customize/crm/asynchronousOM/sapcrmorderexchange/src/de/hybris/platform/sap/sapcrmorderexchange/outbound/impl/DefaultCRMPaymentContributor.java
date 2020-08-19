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

package de.hybris.platform.sap.sapcrmorderexchange.outbound.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultPaymentContributor;
import de.hybris.platform.sap.sapcrmorderexchange.constants.CRMPaymentCsvColumns;

public class DefaultCRMPaymentContributor extends DefaultPaymentContributor {

    @Override
    public Set<String> getColumns() {
        Set<String> columns = new HashSet<>(Arrays.asList(OrderCsvColumns.ORDER_ID, CRMPaymentCsvColumns.CC_OWNER,
                CRMPaymentCsvColumns.VALID_TO_MONTH, CRMPaymentCsvColumns.VALID_TO_YEAR,
                CRMPaymentCsvColumns.SUBSCRIPTION_ID, CRMPaymentCsvColumns.PAYMENT_PROVIDER,
                CRMPaymentCsvColumns.REQUEST_ID, CRMPaymentCsvColumns.CARD_TYPE, CRMPaymentCsvColumns.CARD_NO));
        columns.addAll(getBatchIdAttributes().keySet());
        return columns;
    }

    @Override
    public List<Map<String, Object>> createRows(final OrderModel order) {
        final List<Map<String, Object>> result = new ArrayList<>();

        for (final PaymentTransactionModel payment : order.getPaymentTransactions()) {
            final PaymentInfoModel paymentInfo = order.getPaymentInfo();

            final Map<String, Object> row = new HashMap<>();
            row.put(OrderCsvColumns.ORDER_ID, order.getCode());
            row.put(CRMPaymentCsvColumns.PAYMENT_PROVIDER, payment.getPaymentProvider());
            if (payment.getRequestId() == null || payment.getRequestId().isEmpty()) {
                row.put(CRMPaymentCsvColumns.REQUEST_ID, "1");
            } else {
                row.put(CRMPaymentCsvColumns.REQUEST_ID, payment.getRequestId());
            }

            if (paymentInfo instanceof CreditCardPaymentInfoModel) {
                final CreditCardPaymentInfoModel ccPaymentInfo = (CreditCardPaymentInfoModel) paymentInfo;
                row.put(CRMPaymentCsvColumns.CC_OWNER, ccPaymentInfo.getCcOwner());
                row.put(CRMPaymentCsvColumns.VALID_TO_MONTH, ccPaymentInfo.getValidToMonth());
                row.put(CRMPaymentCsvColumns.VALID_TO_YEAR, ccPaymentInfo.getValidToYear());
                row.put(CRMPaymentCsvColumns.SUBSCRIPTION_ID, ccPaymentInfo.getSubscriptionId());
                row.put(CRMPaymentCsvColumns.CARD_TYPE, ccPaymentInfo.getType());
                row.put(CRMPaymentCsvColumns.CARD_NO, ccPaymentInfo.getNumber());
            }
            getBatchIdAttributes().forEach(row::putIfAbsent);
            row.put("dh_batchId", order.getCode());
            result.add(row);
        }

        return result;
    }
}