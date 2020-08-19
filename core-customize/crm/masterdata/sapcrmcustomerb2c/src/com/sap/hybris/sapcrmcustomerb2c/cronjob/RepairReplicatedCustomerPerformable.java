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
package com.sap.hybris.sapcrmcustomerb2c.cronjob;

import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sap.hybris.sapcrmcustomerb2c.outbound.CRMCustomerExportService;
import com.sap.hybris.sapcrmcustomerb2c.outbound.events.CRMCustomerReplicationEvent;

public class RepairReplicatedCustomerPerformable extends
        AbstractJobPerformable<CronJobModel> {

    private CRMCustomerExportService customerExportService;
    private BaseStoreService baseStoreService;
    private DefaultStoreSessionFacade storeSessionFacade;
    private BaseSiteService baseSiteService;
    private EventService eventService;

    /**
     * @return the baseSiteService
     */
    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    /**
     * @param baseSiteService
     *            the baseSiteService to set
     */
    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
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

    @Override
    public PerformResult perform(final CronJobModel arg0) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT {").append(CustomerModel.PK).append("} ");
        stringBuilder.append("FROM {").append(CustomerModel._TYPECODE)
                .append("} ");
        stringBuilder.append("WHERE {")
                .append(CustomerModel.SAPREPLICATIONINFO)
                .append("} is not null ");
        stringBuilder.append("AND {").append(CustomerModel.SAPISREPLICATED)
                .append("} = 0");
        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
                stringBuilder.toString());
        final SearchResult<CustomerModel> results = flexibleSearchService
                .search(flexibleSearchQuery);
        final List<CustomerModel> customerModels = results.getResult();
        for (final CustomerModel customerModel : customerModels) {

            final String baseStoreUid = baseStoreService.getCurrentBaseStore() != null ? baseStoreService
                    .getCurrentBaseStore().getUid() : null;
            final String baseSiteUid = getBaseSiteService()
                    .getCurrentBaseSite() != null ? getBaseSiteService()
                    .getCurrentBaseSite().getUid() : null;
            final String sessionLanguage = getStoreSessionFacade()
                    .getCurrentLanguage() != null ? getStoreSessionFacade()
                    .getCurrentLanguage().getIsocode() : null;
            final AddressModel addressModel = customerModel
                    .getDefaultShipmentAddress();
            final List<Map<String, Object>> customerDetailsAndAddressDataList = new ArrayList<>();
            if (null == addressModel) {
                customerDetailsAndAddressDataList
                        .add(getCustomerExportService()
                                .prepareCompleteCustomerDetailsAndAddressData(
                                        customerModel, baseStoreUid,
                                        sessionLanguage, null, baseSiteUid));
            } else {
                customerDetailsAndAddressDataList
                        .add(getCustomerExportService()
                                .prepareCompleteCustomerDetailsAndAddressData(
                                        customerModel, baseStoreUid,
                                        sessionLanguage, addressModel,
                                        baseSiteUid));

            }

            final List<Map<String, Object>> customerCreditCardDetailsDataList = getCustomerExportService()
                    .prepareCustomerCreditCardDetailsData(customerModel, null);
            final CRMCustomerReplicationEvent customerReplicationEvent = new CRMCustomerReplicationEvent(
                    customerDetailsAndAddressDataList,
                    customerCreditCardDetailsDataList);

            getEventService().publishEvent(customerReplicationEvent);
        }
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    /**
     * @return the baseStoreService
     */
    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    /**
     * @param baseStoreService
     *            the baseStoreService to set
     */
    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    /**
     * @return the storeSessionFacade
     */
    public DefaultStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    /**
     * @param storeSessionFacade
     *            the storeSessionFacade to set
     */
    public void setStoreSessionFacade(
            final DefaultStoreSessionFacade storeSessionFacade) {
        this.storeSessionFacade = storeSessionFacade;
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
