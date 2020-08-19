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
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.tx.AfterSaveEvent;
import de.hybris.platform.tx.AfterSaveListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.hybris.sapcrmcustomerb2c.constants.Sapcrmcustomerb2cConstants;
import com.sap.hybris.sapcrmcustomerb2c.outbound.events.CRMCustomerReplicationEvent;

/**
 * Event listener called when Consent model is created or updated
 */
public class DefaultConsentEventListener implements AfterSaveListener {
	private static final Logger LOGGER = Logger
			.getLogger(com.sap.hybris.sapcrmcustomerb2c.outbound.DefaultConsentEventListener.class.getName());

	private ModelService modelService;
	private BaseStoreService baseStoreService;
	private BaseSiteService baseSiteService;
	private DefaultStoreSessionFacade storeSessionFacade;
	private CRMCustomerExportService customerExportService;
	private EventService eventService;

	@Override
	public void afterSave(final Collection<AfterSaveEvent> events) {

		for (final AfterSaveEvent event : events) {
			final int type = event.getType();
			final PK eventPK = event.getPk();
			if (Sapcrmcustomerb2cConstants.ITEM_TYPE_CODE_CONSENT == eventPK.getTypeCode()) {
				final ConsentModel consentModel = getModelService().get(eventPK);
				final CustomerModel customerModel = consentModel.getCustomer();
				if (!getCustomerExportService().isCustomerReplicationEnabled()) {
					LOGGER.warn("Customer " + customerModel.getPk()
							+ " was not send to Data Hub. replicate register user not active");
					return;
				}
				if (AfterSaveEvent.CREATE == type
						|| ((AfterSaveEvent.UPDATE == type) && (null != consentModel.getConsentWithdrawnDate())
								&& (null != consentModel.getConsentGuid()))) {
					sendConsumerData(customerModel, consentModel.getConsentTemplate().getBaseSite().getUid());
				}
			}
		}
	}

	void sendConsumerData(final CustomerModel customerModel, final String baseSiteUid) {

		final String sessionLanguage = getStoreSessionFacade().getCurrentLanguage() != null
				? getStoreSessionFacade().getCurrentLanguage().getIsocode() : null;
		final String baseStoreUid = getBaseStoreService().getCurrentBaseStore() != null
				? getBaseStoreService().getCurrentBaseStore().getUid() : null;
		final Collection<AddressModel> addresses = customerModel.getAddresses();

		final List<Map<String, Object>> customerDetailsAndAddressDataList = new ArrayList<>();

		if (null == addresses || addresses.isEmpty()) {
			customerDetailsAndAddressDataList
					.add(getCustomerExportService().prepareCompleteCustomerDetailsAndAddressData(customerModel,
							baseStoreUid, sessionLanguage, null, baseSiteUid));

		} else {

			for (final Iterator iterator = addresses.iterator(); iterator.hasNext();) {

				final AddressModel addressModel = (AddressModel) iterator.next();

				if (null != addressModel.getPublicKey()) {
					customerDetailsAndAddressDataList
							.add(getCustomerExportService().prepareCompleteCustomerDetailsAndAddressData(customerModel,
									baseStoreUid, sessionLanguage, addressModel, baseSiteUid));
				}
			}
		}

		final List<Map<String, Object>> customerCreditCardDetailsDataList = getCustomerExportService()
				.prepareCustomerCreditCardDetailsData(customerModel, null);

		final CRMCustomerReplicationEvent customerReplicationEvent = new CRMCustomerReplicationEvent(
				customerDetailsAndAddressDataList, customerCreditCardDetailsDataList);

		getEventService().publishEvent(customerReplicationEvent);

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
	 * @return the storeSessionFacade
	 */
	public DefaultStoreSessionFacade getStoreSessionFacade() {
		return storeSessionFacade;
	}

	/**
	 * @param storeSessionFacade
	 *            the storeSessionFacade to set
	 */
	public void setStoreSessionFacade(final DefaultStoreSessionFacade storeSessionFacade) {
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
	public void setCustomerExportService(final CRMCustomerExportService customerExportService) {
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
