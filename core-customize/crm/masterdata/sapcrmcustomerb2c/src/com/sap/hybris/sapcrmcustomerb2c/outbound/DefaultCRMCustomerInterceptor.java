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

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.hybris.sapcrmcustomerb2c.outbound.events.CRMCustomerReplicationEvent;
import com.sap.hybris.sapcustomerb2c.outbound.DefaultCustomerInterceptor;

public class DefaultCRMCustomerInterceptor extends DefaultCustomerInterceptor {
    private ModelService modelService;
    private CRMCustomerExportService customerExportService;
    private BaseSiteService baseSiteService;
    private EventService eventService;
    private static final Logger LOGGER = Logger
            .getLogger(com.sap.hybris.sapcustomerb2c.outbound.DefaultCustomerInterceptor.class
                    .getName());

    @Override
    public void onValidate(final CustomerModel customerModel,
            final InterceptorContext ctx) throws InterceptorException {
        if (!ctx.isModified(customerModel, CustomerModel.SAPISREPLICATED)) {

            // only if replication of user is requested start publishing to Data
            // Hub process
            if (getCustomerExportService().isCustomerReplicationEnabled()) {
                if (!getCustomerExportService().isClassCustomerModel(
                        customerModel)) {
                    return;
                }
                // check if default shipment address, name or title was updated
                // and the sap contact id is filled
                sendConsumerData(customerModel, ctx);
            } else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Customer "
                        + customerModel.getPk()
                        + " was not send to Data Hub. replicate register user not active");
            }

        }
    }

    private void sendConsumerData(final CustomerModel customerModel,
            final InterceptorContext ctx) {
        if (checkModifications(customerModel, ctx)
                && (null != customerModel.getSapConsumerID())) {
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
            getCustomerDetailsAndAddressDataList(customerModel, baseStoreUid,
                    baseSiteUid, sessionLanguage, addresses,
                    customerDetailsAndAddressDataList);
            final List<Map<String, Object>> customerCreditCardDetailsDataList = getCustomerExportService()
                    .prepareCustomerCreditCardDetailsData(customerModel, null);

            final CRMCustomerReplicationEvent customerReplicationEvent = new CRMCustomerReplicationEvent(
                    customerDetailsAndAddressDataList,
                    customerCreditCardDetailsDataList);

            getEventService().publishEvent(customerReplicationEvent);
        } else if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Customer " + customerModel.getUid()
                    + " was not send to Data Hub.");
            LOGGER.debug("Customer Default shipment address modified =  "
                    + ctx.isModified(customerModel,
                            CustomerModel.DEFAULTSHIPMENTADDRESS));
            LOGGER.debug("Customer name modified = "
                    + ctx.isModified(customerModel, CustomerModel.NAME));
            LOGGER.debug("Customer title modified = "
                    + ctx.isModified(customerModel, CustomerModel.TITLE));
            LOGGER.debug("Customer sapContactId =  "
                    + customerModel.getSapContactID());
        }
    }

    /**
     * @param customerModel
     * @param baseStoreUid
     * @param baseSiteUid
     * @param sessionLanguage
     * @param addresses
     * @param customerDetailsAndAddressDataList
     */
    private void getCustomerDetailsAndAddressDataList(
            final CustomerModel customerModel, final String baseStoreUid,
            final String baseSiteUid, final String sessionLanguage,
            final Collection<AddressModel> addresses,
            final List<Map<String, Object>> customerDetailsAndAddressDataList) {
        if (null == addresses || addresses.isEmpty()) {
            customerDetailsAndAddressDataList
                    .add(getCustomerExportService()
                            .prepareCompleteCustomerDetailsAndAddressData(
                                    customerModel, baseStoreUid,
                                    sessionLanguage, null, baseSiteUid));
        } else {
            for (final Iterator iterator = addresses.iterator(); iterator
                    .hasNext();) {
                final AddressModel addressModel = (AddressModel) iterator
                        .next();
                if (null != addressModel.getPublicKey()) {
                    customerDetailsAndAddressDataList
                            .add(getCustomerExportService()
                                    .prepareCompleteCustomerDetailsAndAddressData(
                                            customerModel, baseStoreUid,
                                            sessionLanguage, addressModel,
                                            baseSiteUid));
                }
            }
        }
    }

    private boolean checkModifications(final CustomerModel customerModel,
            final InterceptorContext ctx) {
        if (ctx.isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS)
                || ctx.isModified(customerModel, CustomerModel.NAME)
                || ctx.isModified(customerModel, CustomerModel.TITLE)) {
            return true;
        } else {
            return ctx.isModified(customerModel, CustomerModel.UID)
                    || ctx.isModified(customerModel,
                            CustomerModel.DEFAULTPAYMENTINFO);
        }
    }

    /**
     * @return the modelService
     */
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * @param modelService
     *            the modelService to set
     */
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * @return the customerExportService
     */
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
