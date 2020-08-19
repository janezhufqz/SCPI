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

package com.sap.hybris.crm.inbound.events;

import com.sap.hybris.crm.constants.YsapreturnprocessConstants;
import com.sap.hybris.crm.inbound.DataHubInboundOrderHelper;
import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.security.JaloSecurityException;

public class DataHubReturnOrderCreditMemoTranslator extends AbstractSpecialValueTranslator {

    private final String helperBeanName;
    @SuppressWarnings("javadoc")
    public static final String HELPER_BEAN = "sapDataHubInboundReturnOrderHelper";
    private DataHubInboundOrderHelper inboundHelper;

    public DataHubReturnOrderCreditMemoTranslator() {
        this.helperBeanName = HELPER_BEAN;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(final SpecialColumnDescriptor columnDescriptor) throws HeaderValidationException {
        if (getInboundHelper() == null) {
            setInboundHelper((DataHubInboundOrderHelper) Registry.getApplicationContext().getBean(helperBeanName));
        }
    }

    @Override
    public void performImport(final String creditinfo, final Item processedItem) throws ImpExException {
        final String orderCode = getOrderCode(processedItem);    
        getInboundHelper().processOrderCreditMemoNotififcationFromHub(orderCode, creditinfo);
    }

    private String getOrderCode(final Item processedItem) throws ImpExException {
        String orderCode = null;

        try {
            orderCode = processedItem.getAttribute(YsapreturnprocessConstants.CODE).toString();
        } catch (final JaloSecurityException e) {
            throw new ImpExException(e);
        }
        return orderCode;
    }

    public DataHubInboundOrderHelper getInboundHelper() {
        return inboundHelper;
    }

    public void setInboundHelper(DataHubInboundOrderHelper inboundHelper) {
        this.inboundHelper = inboundHelper;
    }

}
