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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.crm.constants.CrmOrderEntryCsvColumns;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;

public class DefaultReturnOrderSalesConditionsContributor implements RawItemContributor<ReturnRequestModel> {

    RawItemContributor<OrderModel> salesConditionsContributor;
    
    private Map<String, String> batchIdAttributes;
	
	public Map<String, String> getBatchIdAttributes() {
		return batchIdAttributes;
	}

	@Required
	public void setBatchIdAttributes(Map<String, String> batchIdAttributes) {
		this.batchIdAttributes = batchIdAttributes;
	}

	public List<Map<String, Object>> createRows(final ReturnRequestModel returnRequest) {
        List<Map<String, Object>> rows = salesConditionsContributor.createRows(returnRequest.getOrder());
        for (Map<String, Object> row : rows) {
            row.put(OrderCsvColumns.ORDER_ID, returnRequest.getCode());
            row.put(CrmOrderEntryCsvColumns.REFUND_DELIVERY_COST, returnRequest.getRefundDeliveryCost());
            row.put("dh_sourceId", this.getBatchIdAttributes().get("dh_sourceId"));
            row.put("dh_batchId", returnRequest.getCode());
            row.put("dh_type", this.getBatchIdAttributes().get("dh_type"));
        }
        return rows;
    }
	
    @Override
    public Set<String> getColumns() {
        return salesConditionsContributor.getColumns();
    }
    
    public RawItemContributor<OrderModel> getSalesConditionsContributor() {
		return salesConditionsContributor;
	}
    
	public void setSalesConditionsContributor(RawItemContributor<OrderModel> salesConditionsContributor) {
		this.salesConditionsContributor = salesConditionsContributor;
	}
}
