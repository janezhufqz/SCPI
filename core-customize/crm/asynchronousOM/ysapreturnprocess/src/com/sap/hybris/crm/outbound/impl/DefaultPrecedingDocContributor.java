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

import com.sap.hybris.crm.constants.CrmOrderEntryCsvColumns;

import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;

public class DefaultPrecedingDocContributor implements RawItemContributor<ReturnRequestModel> {
	
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
    	Set<String> columns = new HashSet<>(Arrays.asList(OrderCsvColumns.ORDER_ID, CrmOrderEntryCsvColumns.PRECEDING_DOCUMENT_ID));
        columns.addAll(getBatchIdAttributes().keySet());
		return columns;
    }

    @Override
    public List<Map<String, Object>> createRows(ReturnRequestModel model) {
        final List<Map<String, Object>> result = new ArrayList<>();
        final Map<String, Object> row = new HashMap<>();
        row.put(OrderCsvColumns.ORDER_ID, model.getCode());
        row.put(CrmOrderEntryCsvColumns.PRECEDING_DOCUMENT_ID, model.getOrder().getCode());
        getBatchIdAttributes().forEach(row::putIfAbsent);
		row.put("dh_batchId", model.getCode());
        result.add(row);
        return result;
    }

}
