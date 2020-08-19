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
package com.sap.hybris.sapbusinessagreement.b2b.outbound.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.sapcrmorderexchangeb2b.outbound.impl.DefaultCRMB2BPartnerContributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.hybris.sapbusinessagreement.constants.PartnerRoles;

/**
 *
 */
public class DefaultCRMB2BAgreementPartnerContributor extends DefaultCRMB2BPartnerContributor {

    @Override
    public List<Map<String, Object>> createRows(final OrderModel order) {
        final List<Map<String, Object>> rows = super.createRows(order);

        String partnerId = null;
        if (this.isB2BOrder(order)) {
            partnerId = getB2bUnitService().getRootUnit(order.getUnit()).getUid();
        } else {
            partnerId = getB2CCustomerHelper().determineB2CCustomer(order);
            if (partnerId == null) {
                partnerId = order.getStore().getSAPConfiguration().getSapcommon_referenceCustomer();
            }
        }

        final Map<String, Object> row = new HashMap<>();
        row.put(OrderCsvColumns.ORDER_ID, order.getCode());
        row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, PartnerRoles.PAIR.getCode());
        row.put(PartnerCsvColumns.PARTNER_CODE, partnerId);
        this.getBatchIdAttributes().forEach(row::putIfAbsent);
        row.put("dh_batchId", order.getCode());
        rows.add(row);
        return rows;
    }

}
