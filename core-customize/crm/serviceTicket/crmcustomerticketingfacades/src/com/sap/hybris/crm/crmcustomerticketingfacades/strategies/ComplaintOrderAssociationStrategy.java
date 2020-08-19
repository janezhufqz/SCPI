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

package com.sap.hybris.crm.crmcustomerticketingfacades.strategies;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.sap.hybris.crm.crmcustomerticketingfacades.data.ComplaintAssociatedOrderData;
import com.sap.hybris.crm.crmcustomerticketingfacades.data.ComplaintAssociatedOrderEntryData;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.services.BaseStoreService;

public class ComplaintOrderAssociationStrategy {
    @Autowired
    private Converter<OrderModel, ComplaintAssociatedOrderData> complaintOrderCoverter;
    @Autowired
    private Converter<AbstractOrderEntryModel, ComplaintAssociatedOrderEntryData> complaintOrderEntryCoverter;
    @Autowired
    private CustomerAccountService customerAccountService;
    @Autowired
    private BaseStoreService baseStoreService;

    public List<ComplaintAssociatedOrderData> associatedOrders(UserModel currentUser) {
        List<ComplaintAssociatedOrderData> orderList = new ArrayList<ComplaintAssociatedOrderData>();
        for (OrderModel order : currentUser.getOrders()) {
            if (null != order.getStatus() && order.getStatus().equals(OrderStatus.COMPLETED)) {
                orderList.add(complaintOrderCoverter.convert(order));
            }
        }
        return orderList;
    }

    public List<ComplaintAssociatedOrderEntryData> getAssociatedOrderEntries(String orderCode) {
        List<ComplaintAssociatedOrderEntryData> orderEntryList = new ArrayList<ComplaintAssociatedOrderEntryData>();
        AbstractOrderModel order = customerAccountService.getOrderForCode(orderCode,
                baseStoreService.getCurrentBaseStore());
        List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
        if (null != order) {
            entries = order.getEntries();
        }
        for (AbstractOrderEntryModel entry : entries) {
            orderEntryList.add(complaintOrderEntryCoverter.convert(entry));
        }
        return orderEntryList;
    }
}
