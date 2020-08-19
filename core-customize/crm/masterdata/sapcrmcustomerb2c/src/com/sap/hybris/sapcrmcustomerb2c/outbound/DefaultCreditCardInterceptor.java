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
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.hybris.sapcrmcustomerb2c.outbound.events.CRMCustomerReplicationEvent;

public class DefaultCreditCardInterceptor implements
        ValidateInterceptor<CreditCardPaymentInfoModel> {

    BaseStoreService baseStoreService;
    private BaseSiteService baseSiteService;
    private DefaultStoreSessionFacade storeSessionFacade;
    private CRMCustomerExportService customerExportService;
    private EventService eventService;

    private static final Logger LOGGER = Logger
            .getLogger(com.sap.hybris.sapcrmcustomerb2c.outbound.DefaultCreditCardInterceptor.class
                    .getName());

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

    /*
     * (non-Javadoc)
     *
     * @see
     * de.hybris.platform.servicelayer.interceptor.ValidateInterceptor#onValidate
     * (java.lang.Object,
     * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
     */
    @Override
    public void onValidate(
            final CreditCardPaymentInfoModel creditCardPaymentInfoModel,
            final InterceptorContext ctx) throws InterceptorException {
        final CustomerModel customerModel = (CustomerModel) creditCardPaymentInfoModel
                .getUser();
        if (creditCardPaymentInfoModel.getSapIsReplicated().equals(
                Boolean.FALSE)
                && creditCardPaymentInfoModel.isSaved()
                && ctx.isNew(creditCardPaymentInfoModel)
                && creditCardPaymentInfoModel.getCardInc() == null) {
            final String baseStoreUid = getBaseStoreService()
                    .getCurrentBaseStore() != null ? getBaseStoreService()
                    .getCurrentBaseStore().getUid() : null;
            final String baseSiteUid = getBaseSiteService()
                    .getCurrentBaseSite() != null ? getBaseSiteService()
                    .getCurrentBaseSite().getUid() : null;
            if (baseStoreUid != null) {
                final String sessionLanguage = getStoreSessionFacade()
                        .getCurrentLanguage() != null ? getStoreSessionFacade()
                        .getCurrentLanguage().getIsocode() : null;

                if (customerModel.getType().equals(CustomerType.GUEST)) {
                    LOGGER.debug("Customer is guest customer");
                } else {
                    final List<Map<String, Object>> customerDetailsAndAddressDataList = new ArrayList<>();

                    getCustomerDetailsAndAddressDataList(customerModel,
                            baseStoreUid, baseSiteUid, sessionLanguage,
                            customerDetailsAndAddressDataList);
                    final List<Map<String, Object>> customerCreditCardDetailsDataList = getCustomerExportService()
                            .prepareCustomerCreditCardDetailsData(
                                    customerModel, creditCardPaymentInfoModel);

                    final CRMCustomerReplicationEvent customerReplicationEvent = new CRMCustomerReplicationEvent(
                            customerDetailsAndAddressDataList,
                            customerCreditCardDetailsDataList);

                    getEventService().publishEvent(customerReplicationEvent);
                    LOGGER.debug("Enter in Credit card Inteceptor :");
                }
            }

        }

        LOGGER.debug("Credit Card Code info is not avaiable or saved ");

    }

    /**
     * @param customerModel
     * @param baseStoreUid
     * @param baseSiteUid
     * @param sessionLanguage
     * @param customerDetailsAndAddressDataList
     */
    private void getCustomerDetailsAndAddressDataList(
            final CustomerModel customerModel, final String baseStoreUid,
            final String baseSiteUid, final String sessionLanguage,
            final List<Map<String, Object>> customerDetailsAndAddressDataList) {
        final Collection<AddressModel> addressess = customerModel
                .getAddresses();
        if (null == addressess) {
            customerDetailsAndAddressDataList
                    .add(getCustomerExportService()
                            .prepareCompleteCustomerDetailsAndAddressData(
                                    customerModel, baseStoreUid,
                                    sessionLanguage, null,
                                    baseSiteUid));
        } else {
            for (final Iterator iterator = addressess.iterator(); iterator
                    .hasNext();) {
                final AddressModel addressModel = (AddressModel) iterator
                        .next();
                if (null != addressModel.getPublicKey()) {
                    customerDetailsAndAddressDataList
                            .add(getCustomerExportService()
                                    .prepareCompleteCustomerDetailsAndAddressData(
                                            customerModel,
                                            baseStoreUid,
                                            sessionLanguage,
                                            addressModel,
                                            baseSiteUid));
                }
            }
        }
    }

}
