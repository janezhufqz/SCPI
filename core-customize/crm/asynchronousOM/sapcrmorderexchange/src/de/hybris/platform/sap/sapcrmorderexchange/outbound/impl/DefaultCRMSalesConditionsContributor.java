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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.SalesConditionCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultSalesConditionsContributor;
import de.hybris.platform.sap.sapcrmorderexchange.constants.CrmOrderCsvColumns;
import de.hybris.platform.sap.sapmodel.model.SAPPricingConditionModel;

public class DefaultCRMSalesConditionsContributor extends DefaultSalesConditionsContributor {
    private String deliveryCosts;
    private String paymentCosts;

    @Override
    public List<Map<String, Object>> createRows(final OrderModel order) {
        final List<AbstractOrderEntryModel> entries = order.getEntries();
        return syncPricingInactive(entries) ? createRowsHybrisPricing(order, entries)
                : createRowsSyncPricing(order, entries);
    }

    protected List<Map<String, Object>> createRowsHybrisPricing(final OrderModel order,
            final List<AbstractOrderEntryModel> entries) {
        final List<Map<String, Object>> result = new ArrayList<>();
        setConditionTypes(order);
        int totalEntries = entries.size();
        for (final AbstractOrderEntryModel entry : entries) {
            createGrossPriceRow(order, result, entry);
            createTaxRows(order, result, entry);
            createProductDiscountRows(order, result, entry);
            createOrderDiscountRows(order, result);

            createDeliveryCostRow(order, result, entry, totalEntries);
            createPaymentCostRow(order, result, entry, totalEntries);
        }
        return result;
    }

    protected List<Map<String, Object>> createRowsSyncPricing(final OrderModel order,
            final List<AbstractOrderEntryModel> entries) {
        final List<Map<String, Object>> result = new ArrayList<>();

        for (final AbstractOrderEntryModel entry : entries) {
            final Iterator<SAPPricingConditionModel> it = entry.getSapPricingConditions().iterator();
            while (it.hasNext()) {
                final SAPPricingConditionModel condition = it.next();
                final Map<String, Object> row = new HashMap<>();

                row.put(OrderCsvColumns.ORDER_ID, order.getCode());
                row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, entry.getEntryNumber());

                row.put(SalesConditionCsvColumns.CONDITION_CODE, condition.getConditionType());
                row.put(SalesConditionCsvColumns.CONDITION_VALUE, condition.getConditionRate());
                row.put(SalesConditionCsvColumns.CONDITION_UNIT_CODE, condition.getConditionUnit());
                row.put(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY, condition.getConditionPricingUnit());
                row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, condition.getCurrencyKey());
                row.put(SalesConditionCsvColumns.CONDITION_COUNTER, condition.getConditionCounter());
                getBatchIdAttributes().forEach(row::putIfAbsent);
                row.put(CrmOrderCsvColumns.DH_BATCHID, order.getCode());
                result.add(row);
            }
        }
        return result;
    }

    protected void setConditionTypes(final OrderModel order) {
        final SAPConfigurationModel sapConfiguration = order.getStore().getSAPConfiguration();
        if (sapConfiguration != null) {
            setGrossPrice(sapConfiguration.getSaporderexchange_itemPriceConditionType());
            setDeliveryCosts(sapConfiguration.getSaporderexchange_deliveryCostConditionType());
            setPaymentCosts(sapConfiguration.getSaporderexchange_paymentCostConditionType());
            super.setGrossPrice(sapConfiguration.getSaporderexchange_itemPriceConditionType());
            super.setDeliveryCosts(sapConfiguration.getSaporderexchange_deliveryCostConditionType());
            super.setPaymentCosts(sapConfiguration.getSaporderexchange_paymentCostConditionType());
        }
    }

    protected void createPaymentCostRow(final OrderModel order, final List<Map<String, Object>> result,
            AbstractOrderEntryModel entry, int totalEntries) {
        double itemcost = order.getPaymentCost() / totalEntries;
        final Map<String, Object> row = new HashMap<>();
        row.put(OrderCsvColumns.ORDER_ID, order.getCode());
        row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, entry.getEntryNumber());
        row.put(SalesConditionCsvColumns.CONDITION_CODE, paymentCosts);
        row.put(SalesConditionCsvColumns.CONDITION_VALUE, itemcost);
        row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, order.getCurrency().getIsocode());
        row.put(SalesConditionCsvColumns.CONDITION_COUNTER, getConditionCounterPaymentCost());
        row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.TRUE);
        getBatchIdAttributes().forEach(row::putIfAbsent);
        row.put(CrmOrderCsvColumns.DH_BATCHID, order.getCode());
        result.add(row);
    }

    protected void createDeliveryCostRow(final OrderModel order, final List<Map<String, Object>> result,
            AbstractOrderEntryModel entry, int totalEntries) {
        double itemcost = order.getDeliveryCost() / totalEntries;
        final Map<String, Object> row = new HashMap<>();
        row.put(OrderCsvColumns.ORDER_ID, order.getCode());
        row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, entry.getEntryNumber());
        row.put(SalesConditionCsvColumns.CONDITION_CODE, deliveryCosts);
        row.put(SalesConditionCsvColumns.CONDITION_VALUE, itemcost);
        row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, order.getCurrency().getIsocode());
        row.put(SalesConditionCsvColumns.CONDITION_COUNTER, getConditionCounterDeliveryCost());
        row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.TRUE);
        getBatchIdAttributes().forEach(row::putIfAbsent);
        row.put(CrmOrderCsvColumns.DH_BATCHID, order.getCode());
        result.add(row);
    }

    protected String getDeliveryCosts() {
        return deliveryCosts;
    }

    @SuppressWarnings("javadoc")
    public void setDeliveryCosts(final String deliveryCosts) {
        this.deliveryCosts = deliveryCosts;
    }

    @SuppressWarnings("javadoc")
    public void setPaymentCosts(final String paymentCosts) {
        this.paymentCosts = paymentCosts;
    }

    protected String getPaymentCosts() {
        return paymentCosts;
    }
}