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
package com.sap.hybris.sapcrmcustomerb2c.outbound;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.hybris.sapcrmcustomerb2c.outbound.events.CRMCustomerReplicationEvent;

public class DefaultCRMCustomerRemoveInterceptor implements
        RemoveInterceptor<CustomerModel> {
    private static final Logger LOGGER = Logger
            .getLogger(com.sap.hybris.sapcustomerb2c.outbound.CustomerExportService.class
                    .getName());
    private CRMCustomerExportService customerExportService;
    private EventService eventService;

    /**
     * @return the eventService
     */
    public EventService getEventService() {
        return eventService;
    }

    /**
     * @param eventService
     *            the eventService to set
     */
    public void setEventService(final EventService eventService) {
        this.eventService = eventService;
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

    /*
     * (non-Javadoc)
     *
     * @see
     * de.hybris.platform.servicelayer.interceptor.RemoveInterceptor#onRemove
     * (java.lang.Object,
     * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
     */
    @Override
    public void onRemove(final CustomerModel customerModel,
            final InterceptorContext paramInterceptorContext)
            throws InterceptorException {
        final List<Map<String, Object>> customerDetailsAndAddressDataList = new ArrayList<>();

        if (customerModel.getSapIsReplicatedFromHybris().equals(Boolean.TRUE)) {
            LOGGER.debug("Customer "
                    + customerModel.getContactEmail()
                    + "with customerID "
                    + customerModel.getCustomerID()
                    + " was replicated from hybris hence block request is being send to central system");
            customerDetailsAndAddressDataList.add(getCustomerExportService()
                    .prepareCustomerDeleteData(customerModel));
            final List<Map<String, Object>> customerCreditCardDetailsDataList = getCustomerExportService()
                    .prepareCustomerCreditCardDetailsData(customerModel, null);
            final CRMCustomerReplicationEvent customerReplicationEvent = new CRMCustomerReplicationEvent(
                    customerDetailsAndAddressDataList,
                    customerCreditCardDetailsDataList);

            getEventService().publishEvent(customerReplicationEvent);
        } else {
            LOGGER.debug("Customer "
                    + customerModel.getContactEmail()
                    + "with customerID "
                    + customerModel.getCustomerID()
                    + " was not replicated from hybris hence block request is not being send to central system");
        }

    }
}
