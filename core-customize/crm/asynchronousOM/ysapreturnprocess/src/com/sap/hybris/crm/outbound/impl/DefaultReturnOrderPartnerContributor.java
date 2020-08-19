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

package com.sap.hybris.crm.outbound.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.B2CCustomerHelper;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;
import de.hybris.platform.sap.sapcrmorderexchange.constants.PartnerRoles;

public class DefaultReturnOrderPartnerContributor implements
        RawItemContributor<ReturnRequestModel> {

    private B2CCustomerHelper b2CCustomerHelper;

    private Map<String, String> batchIdAttributes;

    public Map<String, String> getBatchIdAttributes() {
        return batchIdAttributes;
    }

    @Required
    public void setBatchIdAttributes(Map<String, String> batchIdAttributes) {
        this.batchIdAttributes = batchIdAttributes;
    }

    @Override
    public Set<String> getColumns() {
        Set<String> columns = new HashSet<>(Arrays.asList(
                OrderCsvColumns.ORDER_ID, PartnerCsvColumns.PARTNER_ROLE_CODE,
                PartnerCsvColumns.PARTNER_CODE,
                PartnerCsvColumns.DOCUMENT_ADDRESS_ID,
                PartnerCsvColumns.FIRST_NAME, PartnerCsvColumns.LAST_NAME,
                PartnerCsvColumns.STREET, PartnerCsvColumns.CITY,
                PartnerCsvColumns.TEL_NUMBER, PartnerCsvColumns.HOUSE_NUMBER,
                PartnerCsvColumns.POSTAL_CODE,
                PartnerCsvColumns.REGION_ISO_CODE,
                PartnerCsvColumns.COUNTRY_ISO_CODE, PartnerCsvColumns.EMAIL,
                PartnerCsvColumns.LANGUAGE_ISO_CODE,
                PartnerCsvColumns.MIDDLE_NAME, PartnerCsvColumns.MIDDLE_NAME2,
                PartnerCsvColumns.DISTRICT, PartnerCsvColumns.BUILDING,
                PartnerCsvColumns.APPARTMENT, PartnerCsvColumns.POBOX,
                PartnerCsvColumns.FAX, PartnerCsvColumns.TITLE));

        columns.addAll(getBatchIdAttributes().keySet());
        return columns;
    }

    @Override
    public List<Map<String, Object>> createRows(ReturnRequestModel model) {
        List<Map<String, Object>> rows = createSoldtoPartyRows(model.getOrder());
        for (Map<String, Object> row : rows) {
            row.put(OrderCsvColumns.ORDER_ID, model.getCode());
            getBatchIdAttributes().forEach(row::putIfAbsent);
            row.put("dh_batchId", model.getCode());
        }
        return rows;
    }

    public List<Map<String, Object>> createSoldtoPartyRows(
            final OrderModel order) {
        return createRowsB2C(order);
    }

    public List<Map<String, Object>> createRowsB2C(final OrderModel order) {
        final List<Map<String, Object>> result = new ArrayList<>(3);
        Map<String, Object> row = new HashMap<String, Object>();
        final String b2cCustomer = b2CCustomerHelper
                .determineB2CCustomer(order);
        final String sapcommonCustomer = b2cCustomer != null ? b2cCustomer
                : order.getStore().getSAPConfiguration()
                        .getSapcommon_referenceCustomer();
        row.put(OrderCsvColumns.ORDER_ID, order.getCode());
        row.put(PartnerCsvColumns.PARTNER_ROLE_CODE,
                PartnerRoles.SOLD_TO.getCode());
        row.put(PartnerCsvColumns.PARTNER_CODE, sapcommonCustomer);
        result.add(row);
        return result;
    }

    @SuppressWarnings("javadoc")
    public B2CCustomerHelper getB2CCustomerHelper() {
        return b2CCustomerHelper;
    }

    @SuppressWarnings("javadoc")
    public void setB2CCustomerHelper(final B2CCustomerHelper b2cCustomerHelper) {
        b2CCustomerHelper = b2cCustomerHelper;
    }

}
