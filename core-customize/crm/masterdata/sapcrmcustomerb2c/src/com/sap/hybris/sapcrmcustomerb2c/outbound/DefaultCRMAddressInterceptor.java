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

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.sap.hybris.sapcrmcustomerb2c.outbound.events.CRMCustomerReplicationEvent;
import com.sap.hybris.sapcustomerb2c.outbound.DefaultAddressInterceptor;

public class DefaultCRMAddressInterceptor extends DefaultAddressInterceptor {
    private CRMCustomerExportService customerExportService;
    private BaseSiteService baseSiteService;
    private EventService eventService;
    private static final Logger LOGGER = Logger
            .getLogger(com.sap.hybris.sapcustomerb2c.outbound.DefaultAddressInterceptor.class
                    .getName());

    @Override
    public void onValidate(final AddressModel addressModel,
            final InterceptorContext ctx) throws InterceptorException {
        if (!ctx.isModified(addressModel, AddressModel.SAPISREPLICATED)
                || !addressModel.getSapIsReplicated().booleanValue()) {

            // only if replication of user is requested start publishing to Data
            // Hub
            // process
            if (getCustomerExportService().isCustomerReplicationEnabled()) {
                if (!getCustomerExportService().isClassCustomerModel(
                        addressModel.getOwner())) {
                    return;
                }
                checkCustomerData(addressModel, ctx);
            } else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Address "
                        + addressModel.getPk()
                        + " was not send to Data Hub. replicate register user not active");
            }
        }
    }

    private void checkCustomerData(final AddressModel addressModel,
            final InterceptorContext ctx) {
        if (addressModel.getOwner() instanceof CustomerModel) {
            final CustomerModel owner = (CustomerModel) addressModel.getOwner();
            if (owner.getType() != CustomerType.GUEST) {
                sendConsumerData(addressModel, ctx, owner);
            }
        }
    }

    private void sendConsumerData(final AddressModel addressModel,
            final InterceptorContext ctx, final CustomerModel customerModel) {
        if (checkModification1(addressModel, ctx)
                || checkModification2(addressModel, ctx)) {

            final String baseStoreUid = getBaseStoreService()
                    .getCurrentBaseStore() != null ? getBaseStoreService()
                    .getCurrentBaseStore().getUid() : null;
            final String baseSiteUid = getBaseSiteService()
                    .getCurrentBaseSite() != null ? getBaseSiteService()
                    .getCurrentBaseSite().getUid() : null;
            final String sessionLanguage = getStoreSessionFacade()
                    .getCurrentLanguage() != null ? getStoreSessionFacade()
                    .getCurrentLanguage().getIsocode() : null;
            final Collection<AddressModel> addresses = customerModel
                    .getAddresses();
            final List<Map<String, Object>> customerDetailsAndAddressDataList = new ArrayList<>();

            getCustomerDetailsAndAddressDataList(addressModel, customerModel,
                    baseStoreUid, baseSiteUid, sessionLanguage, addresses,
                    customerDetailsAndAddressDataList);
            if (null == addressModel.getPublicKey()) {
                addressModel.setPublicKey(UUID.randomUUID().toString()
                        .replaceAll("-", "").toUpperCase());
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
        } else if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Address " + addressModel.getPk()
                    + " was not send to Data Hub.");
            LOGGER.debug("Address country modified =  "
                    + ctx.isModified(addressModel, AddressModel.COUNTRY));
            LOGGER.debug("Address streetname modified = "
                    + ctx.isModified(addressModel, AddressModel.STREETNAME));
            LOGGER.debug("Address phone1 modified = "
                    + ctx.isModified(addressModel, AddressModel.PHONE1));
            LOGGER.debug("Address fax modified = "
                    + ctx.isModified(addressModel, AddressModel.FAX));
            LOGGER.debug("Address town modified = "
                    + ctx.isModified(addressModel, AddressModel.TOWN));
            LOGGER.debug("Address postalcode modified = "
                    + ctx.isModified(addressModel, AddressModel.POSTALCODE));
            LOGGER.debug("Address streetnumber modified = "
                    + ctx.isModified(addressModel, AddressModel.STREETNUMBER));
            LOGGER.debug("Address region modified = "
                    + ctx.isModified(addressModel, AddressModel.REGION));
            LOGGER.debug("Customer sapContactId = "
                    + customerModel.getSapContactID());
        }
    }

    /**
     * @param addressModel
     * @param customerModel
     * @param baseStoreUid
     * @param baseSiteUid
     * @param sessionLanguage
     * @param addresses
     * @param customerDetailsAndAddressDataList
     */
    private void getCustomerDetailsAndAddressDataList(
            final AddressModel addressModel, final CustomerModel customerModel,
            final String baseStoreUid, final String baseSiteUid,
            final String sessionLanguage,
            final Collection<AddressModel> addresses,
            final List<Map<String, Object>> customerDetailsAndAddressDataList) {
        for (final Iterator iterator = addresses.iterator(); iterator
                .hasNext();) {
            final AddressModel address = (AddressModel) iterator.next();

            if (null == address.getPublicKey()) {
                address.setPublicKey(UUID.randomUUID().toString()
                        .replaceAll("-", "").toUpperCase());
            }
            if (address.getPk().equals(addressModel.getPk())) {
                customerDetailsAndAddressDataList
                        .add(getCustomerExportService()
                                .prepareCompleteCustomerDetailsAndAddressData(
                                        customerModel, baseStoreUid,
                                        sessionLanguage, addressModel,
                                        baseSiteUid));
            } else {
                customerDetailsAndAddressDataList
                        .add(getCustomerExportService()
                                .prepareCompleteCustomerDetailsAndAddressData(
                                        customerModel, baseStoreUid,
                                        sessionLanguage, address,
                                        baseSiteUid));
            }
        }
    }

    private boolean checkModification2(final AddressModel addressModel,
            final InterceptorContext ctx) {
        return ctx.isModified(addressModel, AddressModel.POSTALCODE)
                || ctx.isModified(addressModel, AddressModel.STREETNUMBER)
                || ctx.isModified(addressModel, AddressModel.REGION)
                || ctx.isModified(addressModel, AddressModel.TOWN);
    }

    private boolean checkModification1(final AddressModel addressModel,
            final InterceptorContext ctx) {
        return ctx.isModified(addressModel, AddressModel.COUNTRY)
                || ctx.isModified(addressModel, AddressModel.STREETNAME)
                || ctx.isModified(addressModel, AddressModel.PHONE1)
                || ctx.isModified(addressModel, AddressModel.FAX);
    }

    @Override
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
