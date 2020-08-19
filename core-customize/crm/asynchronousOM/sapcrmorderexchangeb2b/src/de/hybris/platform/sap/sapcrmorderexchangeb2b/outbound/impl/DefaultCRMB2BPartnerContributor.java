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
package de.hybris.platform.sap.sapcrmorderexchangeb2b.outbound.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.SaporderexchangeConstants;
import de.hybris.platform.sap.sapcrmorderexchange.constants.PartnerRoles;
import de.hybris.platform.sap.sapcrmorderexchange.outbound.impl.DefaultCRMPartnerContributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class DefaultCRMB2BPartnerContributor extends DefaultCRMPartnerContributor {

    private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;

    @Override
    public List<Map<String, Object>> createRows(final OrderModel order) {
        return isB2BOrder(order) ? createB2BRows(order) : super.createRows(order);
    }

    protected List<Map<String, Object>> createB2BRows(final OrderModel order) {
    		final List<Map<String, Object>> result = new ArrayList<>(3);
        Map<String, Object> row = createPartnerRow(order, PartnerRoles.SOLD_TO, soldToFromOrder(order));
        result.add(row);
        row = createPartnerRow(order, PartnerRoles.CONTACT, contactFromOrder(order));
        result.add(row);
        row = createAddressRow(order, PartnerRoles.SHIP_TO,
                SaporderexchangeConstants.ADDRESS_ONE);
        result.add(row);
        row = createAddressRow(order, PartnerRoles.BILL_TO,
                SaporderexchangeConstants.ADDRESS_TWO);
        if(row!=null)
        result.add(row);
        return result;
    }

    protected String contactFromOrder(final OrderModel order) {
        return ((B2BCustomerModel) order.getUser()).getCustomerID();
    }

    protected String soldToFromOrder(final OrderModel order) {
        final CompanyModel rootUnit = getB2bUnitService().getRootUnit(order.getUnit());
        return rootUnit.getUid();
    }

    protected Map<String, Object> createPartnerRow(final OrderModel order, final PartnerRoles partnerRole,
            final String partnerId) {
        final Map<String, Object> row = new HashMap<>();
        row.put(OrderCsvColumns.ORDER_ID, order.getCode());
        row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, partnerRole.getCode());
        row.put(PartnerCsvColumns.PARTNER_CODE, partnerId);
        row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, "");
        getBatchIdAttributes().forEach(row::putIfAbsent);
        row.put("dh_batchId", order.getCode());
        return row;
    }

    protected Map<String, Object> createAddressRow(final OrderModel order, final PartnerRoles partnerRole,
            final String addressNumber) {
        final AddressModel address = addressForPartnerRole(order, partnerRole);
        Map<String, Object> row = null;
        if (address != null) {
            row = new HashMap<>();
            row.put(OrderCsvColumns.ORDER_ID, order.getCode());
            row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, partnerRole.getCode());
            final String sapCustomer = address.getSapCustomerID();
            mapAddressData(order, address, row);
            if (sapCustomer == null || sapCustomer.isEmpty()) {
                row.put(PartnerCsvColumns.PARTNER_CODE, soldToFromOrder(order));
                row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, addressNumber);
            } else {
                row.put(PartnerCsvColumns.PARTNER_CODE, sapCustomer);
                row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, "");
            }
            getBatchIdAttributes().forEach(row::putIfAbsent);
            row.put("dh_batchId", order.getCode());
        }
        return row;
    }

    protected AddressModel addressForPartnerRole(final OrderModel order, final PartnerRoles partnerRole) {
        AddressModel result = null;
        if (partnerRole == PartnerRoles.SHIP_TO) {
            result = order.getDeliveryAddress();
        } else if (partnerRole == PartnerRoles.BILL_TO) {
            result = order.getPaymentAddress();
        }
        return result;
    }

    protected boolean isB2BOrder(final OrderModel orderModel) {
        return orderModel.getSite().getChannel() == SiteChannel.B2B;
    }

    /**
     * @return the b2bUnitService
     */
    public B2BUnitService<B2BUnitModel, UserModel> getB2bUnitService() {
        return b2bUnitService;
    }

    /**
     * @param b2bUnitService
     *            the b2bUnitService to set
     */
    public void setB2bUnitService(final B2BUnitService<B2BUnitModel, UserModel> b2bUnitService) {
        this.b2bUnitService = b2bUnitService;
    }
}