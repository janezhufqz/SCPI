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

/**
 *
 */
package de.hybris.platform.sap.sapcrmorderexchange.outbound.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.B2CCustomerHelper;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultPartnerContributor;
import de.hybris.platform.sap.sapcrmorderexchange.constants.CrmOrderCsvColumns;
import de.hybris.platform.sap.sapcrmorderexchange.constants.PartnerRoles;

/**
 * @author C5229483
 *
 */
public class DefaultCRMPartnerContributor extends DefaultPartnerContributor {
    private B2CCustomerHelper b2CCustomerHelper;

    @Override
    public List<Map<String, Object>> createRows(final OrderModel order) {
        final List<Map<String, Object>> result = new ArrayList<>(3);
        final AddressModel paymentAddress = order.getPaymentAddress();
        AddressModel deliveryAddress = order.getDeliveryAddress();
        if (deliveryAddress == null) {
            deliveryAddress = paymentAddress;
        }

        final String b2cCustomer = b2CCustomerHelper.determineB2CCustomer(order);
        final String sapcommonCustomer = b2cCustomer != null ? b2cCustomer
                : order.getStore().getSAPConfiguration().getSapcommon_referenceCustomer();

        final String deliveryAddressNumber = getShipToAddressNumber();
        Map<String, Object> row = mapAddressData(order, deliveryAddress);
        row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, PartnerRoles.SHIP_TO.getCode());
        row.put(PartnerCsvColumns.PARTNER_CODE, sapcommonCustomer);
        row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, deliveryAddressNumber);
        getBatchIdAttributes().forEach(row::putIfAbsent);
        row.put(CrmOrderCsvColumns.DH_BATCHID, order.getCode());
        result.add(row);

        if (paymentAddress != null) {
            final String paymentAddressNumber = getSoldToAddressNumber();
            row = mapAddressData(order, paymentAddress);
            row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, PartnerRoles.BILL_TO.getCode());
            row.put(PartnerCsvColumns.PARTNER_CODE, sapcommonCustomer);
            row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, paymentAddressNumber);
            getBatchIdAttributes().forEach(row::putIfAbsent);
            row.put(CrmOrderCsvColumns.DH_BATCHID, order.getCode());
            result.add(row);
        }
        row = new HashMap<>();
        row.put(OrderCsvColumns.ORDER_ID, order.getCode());
        row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, PartnerRoles.SOLD_TO.getCode());
        row.put(PartnerCsvColumns.PARTNER_CODE, sapcommonCustomer);
        getBatchIdAttributes().forEach(row::putIfAbsent);
        row.put(CrmOrderCsvColumns.DH_BATCHID, order.getCode());
        result.add(row);

        /*
         * Add partner function for sale rep ID and partner function (00000012)
         * for Assisted Service Mode (ASM)
         */
        UserModel salesRep = order.getPlacedBy();
        if (salesRep != null) {

            row = new HashMap<>();
            row.put(OrderCsvColumns.ORDER_ID, order.getCode());
            row.put(PartnerCsvColumns.LANGUAGE_ISO_CODE, order.getLanguage().getIsocode());
            row.put(PartnerCsvColumns.PARTNER_ROLE_CODE, PartnerRoles.PLACED_BY.getCode());
            row.put(PartnerCsvColumns.PARTNER_CODE, salesRep.getUid());
            row.put(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, "");
            getBatchIdAttributes().forEach(row::putIfAbsent);
            row.put(CrmOrderCsvColumns.DH_BATCHID, order.getCode());
            result.add(row);

        }

        return result;
    }

    @Override
    @SuppressWarnings("javadoc")
    public B2CCustomerHelper getB2CCustomerHelper() {
        return b2CCustomerHelper;
    }

    @Override
    @SuppressWarnings("javadoc")
    public void setB2CCustomerHelper(final B2CCustomerHelper b2cCustomerHelper) {
        b2CCustomerHelper = b2cCustomerHelper;
    }

    protected void mapAddressData(final OrderModel order, final AddressModel address, final Map<String, Object> row) {
        row.put(PartnerCsvColumns.FIRST_NAME, address.getFirstname());
        row.put(PartnerCsvColumns.LAST_NAME, address.getLastname());
        row.put(PartnerCsvColumns.STREET, address.getStreetname());
        row.put(PartnerCsvColumns.CITY, address.getTown());
        row.put(PartnerCsvColumns.TEL_NUMBER, address.getPhone1());
        row.put(PartnerCsvColumns.HOUSE_NUMBER, address.getStreetnumber());
        row.put(PartnerCsvColumns.POSTAL_CODE, address.getPostalcode());
        row.put(PartnerCsvColumns.REGION_ISO_CODE,
                (address.getRegion() != null) ? address.getRegion().getIsocodeShort() : "");
        row.put(PartnerCsvColumns.COUNTRY_ISO_CODE,
                (address.getCountry() != null) ? address.getCountry().getIsocode() : "");
        row.put(PartnerCsvColumns.EMAIL, address.getEmail());
        row.put(PartnerCsvColumns.MIDDLE_NAME, address.getMiddlename());
        row.put(PartnerCsvColumns.MIDDLE_NAME2, address.getMiddlename2());
        row.put(PartnerCsvColumns.DISTRICT, address.getDistrict());
        row.put(PartnerCsvColumns.BUILDING, address.getBuilding());
        row.put(PartnerCsvColumns.APPARTMENT, address.getAppartment());
        row.put(PartnerCsvColumns.POBOX, address.getPobox());
        row.put(PartnerCsvColumns.FAX, address.getFax());
        row.put(PartnerCsvColumns.LANGUAGE_ISO_CODE, order.getLanguage().getIsocode());
        row.put(PartnerCsvColumns.TITLE, (address.getTitle() != null) ? address.getTitle().getCode() : "");
    }

}