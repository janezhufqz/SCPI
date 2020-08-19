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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;

public class DefaultReturnOrderEntryContributor implements RawItemContributor<ReturnRequestModel> {
    private static final Logger LOG = Logger.getLogger(DefaultReturnOrderEntryContributor.class);
    
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
    	Set<String> columns = new HashSet<>(Arrays.asList(OrderCsvColumns.ORDER_ID, OrderEntryCsvColumns.ENTRY_NUMBER,
                OrderEntryCsvColumns.QUANTITY, OrderEntryCsvColumns.REJECTION_REASON,
                OrderEntryCsvColumns.NAMED_DELIVERY_DATE, OrderEntryCsvColumns.ENTRY_UNIT_CODE,
                OrderEntryCsvColumns.PRODUCT_CODE, OrderEntryCsvColumns.PRODUCT_NAME));
    	
    	columns.addAll(getBatchIdAttributes().keySet());
		return columns;
    }

    @Override
    public List<Map<String, Object>> createRows(final ReturnRequestModel returnRequest) {
        final List<ReturnEntryModel> entries = returnRequest.getReturnEntries();
        final List<Map<String, Object>> result = new ArrayList<>();

        for (final ReturnEntryModel returnEntry : entries) {
            result.add(createReturnEntryRow(returnEntry));
        }
        return result;
    }

    protected String determineItemShortText(final AbstractOrderEntryModel item, final String language) {
        final String shortText = item.getProduct().getName(new java.util.Locale(language));
        return shortText == null ? "" : shortText;
    }
    
    private Map<String, Object> createReturnEntryRow(ReturnEntryModel returnEntry){
        AbstractOrderEntryModel returnItem = returnEntry.getOrderEntry();
        final Map<String, Object> row = new HashMap<>();
        
        row.put(OrderCsvColumns.ORDER_ID, returnEntry.getReturnRequest().getCode());
        row.put(OrderEntryCsvColumns.ENTRY_NUMBER, returnItem.getEntryNumber());
        row.put(OrderEntryCsvColumns.QUANTITY, returnEntry.getExpectedQuantity());
        row.put(OrderEntryCsvColumns.PRODUCT_CODE, returnItem.getProduct().getCode());
        RefundEntryModel entryModel = (RefundEntryModel)returnEntry;
        row.put(OrderEntryCsvColumns.REJECTION_REASON, entryModel.getReason());
        final UnitModel unit = returnEntry.getOrderEntry().getProduct().getUnit();
        if (unit != null) {
            row.put(OrderEntryCsvColumns.ENTRY_UNIT_CODE, unit.getCode());
        } else {
            LOG.warn("Could not determine unit code for product " + returnItem.getProduct().getCode() + "as entry "
                    + returnItem.getEntryNumber() + "of order " + returnEntry.getReturnRequest().getOrder().getCode());
        }
        String language = returnEntry.getReturnRequest().getOrder().getLanguage().getIsocode();
        String shortText = determineItemShortText(returnItem, language);

        if (shortText.isEmpty()) {
            final List<LanguageModel> fallbackLanguages = returnEntry.getReturnRequest().getOrder().getLanguage().getFallbackLanguages();
            if (!fallbackLanguages.isEmpty()) {
                language = fallbackLanguages.get(0).getIsocode();
                shortText = determineItemShortText(returnItem, language);
            }
        }
        row.put(OrderEntryCsvColumns.PRODUCT_NAME, shortText);
        getBatchIdAttributes().forEach(row::putIfAbsent);
		row.put("dh_batchId", returnEntry.getReturnRequest().getCode());
        return row;
    }

}