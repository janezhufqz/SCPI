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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultOrderContributor;
import de.hybris.platform.sap.sapcrmorderexchange.constants.CrmOrderCsvColumns;

public class DefaultCrmOrderContributor extends DefaultOrderContributor {

    private final Set<String> columns = new HashSet<>(Arrays.asList(OrderCsvColumns.ORDER_ID,
            OrderCsvColumns.DATE, OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, OrderCsvColumns.PAYMENT_MODE,
            OrderCsvColumns.DELIVERY_MODE, OrderCsvColumns.BASE_STORE, CrmOrderCsvColumns.NAMED_DELIVERY_DATE));

    @Override
    public Set<String> getColumns() {

        columns.addAll(this.getBatchIdAttributes().keySet());
        return columns;
    }

    @Override
    public List<Map<String, Object>> createRows(OrderModel order) {

        // class extended to implement logic for sending named delivery date
        // from hybris.
        List<Map<String, Object>> rows = super.createRows(order);
        for (Map<String, Object> row : rows) {
            row.put("dh_sourceId", this.getBatchIdAttributes().get("dh_sourceId"));
            row.put("dh_batchId", order.getCode());
            row.put("dh_type", this.getBatchIdAttributes().get("dh_type"));
        }
        return rows;
    }
}
