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

import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sap.hybris.sapcrmcustomerb2c.outbound.events.CRMCustomerReplicationEvent;

public class DefaultCRMAddressRemoveInterceptor implements
        RemoveInterceptor<AddressModel> {

    private DefaultStoreSessionFacade storeSessionFacade;
    private CRMCustomerExportService customerExportService;
    private BaseStoreService baseStoreService;
    private BaseSiteService baseSiteService;
    private EventService eventService;

    /*
     * (non-Javadoc)
     *
     * @see
     * de.hybris.platform.servicelayer.interceptor.RemoveInterceptor#onRemove(
     * java.lang.Object,
     * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
     */
    @Override
    public void onRemove(final AddressModel addressModel,
            final InterceptorContext interceptorContext)
            throws InterceptorException {
        if (addressModel.getOwner() instanceof CustomerModel) {
            final CustomerModel owner = (CustomerModel) addressModel.getOwner();
            if (null != owner
                    && getCustomerExportService().isClassCustomerModel(owner)) {
                sendConsumerData(addressModel, owner);
            }
        }
    }

    private void sendConsumerData(final AddressModel addressModel,
            final CustomerModel customerModel) {
        final AddressModel defaultShipmentAddress = customerModel
                .getDefaultShipmentAddress();
        final Collection<AddressModel> addresses = customerModel.getAddresses();
        if ((null == defaultShipmentAddress)
                || (!defaultShipmentAddress.getPk()
                        .equals(addressModel.getPk())) || 1 == addresses.size()) {
            final String baseStoreUid = getBaseStoreService()
                    .getCurrentBaseStore() != null ? getBaseStoreService()
                    .getCurrentBaseStore().getUid() : null;
            final String baseSiteUid = getBaseSiteService()
                    .getCurrentBaseSite() != null ? getBaseSiteService()
                    .getCurrentBaseSite().getUid() : null;
            final String sessionLanguage = getStoreSessionFacade()
                    .getCurrentLanguage() != null ? getStoreSessionFacade()
                    .getCurrentLanguage().getIsocode() : null;
            boolean noAddressFlag = true;
            final List<Map<String, Object>> customerDetailsAndAddressDataList = new ArrayList<>();

            for (final Iterator iterator = addresses.iterator(); iterator
                    .hasNext();) {
                final AddressModel address = (AddressModel) iterator.next();
                if (address.getPk() == addressModel.getPk()) {
                    continue;
                }
                noAddressFlag = false;
                customerDetailsAndAddressDataList
                        .add(getCustomerExportService()
                                .prepareCompleteCustomerDetailsAndAddressData(
                                        customerModel, baseStoreUid,
                                        sessionLanguage, address, baseSiteUid));
            }
            if (noAddressFlag) {
                customerDetailsAndAddressDataList
                        .add(getCustomerExportService()
                                .prepareCompleteCustomerDetailsAndAddressData(
                                        customerModel, baseStoreUid,
                                        sessionLanguage, null, baseSiteUid));
            }
            final List<Map<String, Object>> customerCreditCardDetailsDataList = getCustomerExportService()
                    .prepareCustomerCreditCardDetailsData(customerModel, null);

            final CRMCustomerReplicationEvent customerReplicationEvent = new CRMCustomerReplicationEvent(
                    customerDetailsAndAddressDataList,
                    customerCreditCardDetailsDataList);

            getEventService().publishEvent(customerReplicationEvent);
        }
    }

    /**
     * @return the customerExportService
     */
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
