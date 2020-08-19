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

import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.store.BaseStoreModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sap.hybris.sapcrmcustomerb2c.outbound.events.CRMCustomerReplicationEvent;
import com.sap.hybris.sapcustomerb2c.outbound.CustomerPublishAction;

public class CRMCustomerPublishAction extends CustomerPublishAction {

    private CRMCustomerExportService customerExportService;
    private EventService eventService;

    /**
     * /** action method to the update the customer and trigger the publish to
     * Data Hub
     */
    @Override
    public Transition executeAction(
            final BusinessProcessModel businessProcessModel) {
        // set the time stamp in the sap replication info field
        final DateFormat dateFormat = new SimpleDateFormat(
                "MM/dd/yyyy HH:mm:ss");
        final CustomerModel customerModel = ((StoreFrontCustomerProcessModel) businessProcessModel)
                .getCustomer();
        customerModel.setSapReplicationInfo("Sent to datahub "
                + dateFormat.format(Calendar.getInstance().getTime()));
        setSapContactId(businessProcessModel);
        customerModel.setCrmGuid(UUID.randomUUID().toString()
                .replaceAll("-", "").toUpperCase());
        customerModel.setSapIsReplicatedFromHybris(Boolean.TRUE);
        modelService.save(customerModel);

        final BaseStoreModel store = ((StoreFrontCustomerProcessModel) businessProcessModel)
                .getStore();
        final String baseSiteUid = ((StoreFrontCustomerProcessModel) businessProcessModel)
                .getSite().getUid();

        // prepare sending data to Data Hub
        final String baseStoreUid = store != null ? store.getUid() : null;

        final List<Map<String, Object>> customerDetailsAndAddressDataList = new ArrayList<>();
        customerDetailsAndAddressDataList.add(getCustomerExportService()
                .prepareCompleteCustomerDetailsAndAddressData(
                        customerModel,
                        baseStoreUid,
                        getStoreSessionFacade().getCurrentLanguage()
                                .getIsocode(), null, baseSiteUid));
        final List<Map<String, Object>> customerCreditCardDetailsDataList = getCustomerExportService()
                .prepareCustomerCreditCardDetailsData(customerModel, null);
        final CRMCustomerReplicationEvent customerReplicationEvent = new CRMCustomerReplicationEvent(
                customerDetailsAndAddressDataList,
                customerCreditCardDetailsDataList);

        getEventService().publishEvent(customerReplicationEvent);
        return Transition.OK;
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

}
