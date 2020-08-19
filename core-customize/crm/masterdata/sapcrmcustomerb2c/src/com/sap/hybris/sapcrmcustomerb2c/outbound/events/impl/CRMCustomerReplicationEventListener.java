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
package com.sap.hybris.sapcrmcustomerb2c.outbound.events.impl;

import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import java.util.List;
import java.util.Map;

import com.sap.hybris.sapcrmcustomerb2c.constants.Sapcrmcustomerb2cConstants;
import com.sap.hybris.sapcrmcustomerb2c.outbound.CRMCustomerExportService;
import com.sap.hybris.sapcrmcustomerb2c.outbound.events.CRMCustomerReplicationEvent;

public class CRMCustomerReplicationEventListener extends
        AbstractEventListener<CRMCustomerReplicationEvent> {

    private CRMCustomerExportService customerExportService;
    private SAPGlobalConfigurationService sapGlobalConfigurationService;

    @Override
    protected void onEvent(
            final CRMCustomerReplicationEvent customerReplicationEvent) {
        final List<Map<String, Object>> customerDetailsAndAddressDataList = customerReplicationEvent
                .getCustomerDetailsAndAddressDataList();

        if (customerDetailsAndAddressDataList != null
                && !customerDetailsAndAddressDataList.isEmpty()) {
            for (int i = 0; i < customerDetailsAndAddressDataList.size(); i++) {
                getCustomerExportService().sendCustomerToDataHub(
                        customerDetailsAndAddressDataList.get(i));
            }
        }
        final List<Map<String, Object>> customerCreditCardDetailsDataList = customerReplicationEvent
                .getCustomerCreditCardDetailsDataList();

        final Boolean replicateregistereduser = getSapGlobalConfigurationService()
                .getProperty(
                        Sapcrmcustomerb2cConstants.REPLICATE_REGISTERED_USER);
        if (replicateregistereduser.booleanValue()
                && !customerCreditCardDetailsDataList.isEmpty()) {
            getCustomerExportService().sendCreditCardToDataHub(
                    customerCreditCardDetailsDataList);
        }

    }

    /**
     * @return the sapGlobalConfigurationService
     */
    public SAPGlobalConfigurationService getSapGlobalConfigurationService() {
        return sapGlobalConfigurationService;
    }

    /**
     * @param sapGlobalConfigurationService
     *            the sapGlobalConfigurationService to set
     */
    public void setSapGlobalConfigurationService(
            final SAPGlobalConfigurationService sapGlobalConfigurationService) {
        this.sapGlobalConfigurationService = sapGlobalConfigurationService;
    }

    /**
     * @return the customerExportService
     */
    public CRMCustomerExportService getCustomerExportService() {
        return customerExportService;
    }

    /**
     * @param customerExportService
     *            the customerExportService to set
     */
    public void setCustomerExportService(
            final CRMCustomerExportService customerExportService) {
        this.customerExportService = customerExportService;
    }

}
