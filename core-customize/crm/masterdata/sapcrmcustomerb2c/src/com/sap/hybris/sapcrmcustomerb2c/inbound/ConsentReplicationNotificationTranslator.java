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
package com.sap.hybris.sapcrmcustomerb2c.inbound;

import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.sap.hybris.sapcustomerb2c.inbound.CustomerReplicationNotificationTranslator;

/**
 * This class includes the translator for customer replication notification
 */
public class ConsentReplicationNotificationTranslator extends CustomerReplicationNotificationTranslator {

	private ModelService modelService;
	private FlexibleSearchService flexibleSearchService;

	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService() {
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *            the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
		this.flexibleSearchService = flexibleSearchService;
	}

	/**
	 * @return the modelservice
	 */
	public ModelService getModelservice() {
		return modelService;
	}

	/**
	 * @param modelservice
	 *            the modelservice to set
	 */
	public void setModelservice(final ModelService modelService) {
		this.modelService = modelService;
	}

	@Override
	public void init(final SpecialColumnDescriptor columnDescriptor) throws HeaderValidationException {

		super.init(columnDescriptor);
		if (modelService == null) {
			modelService = (ModelService) Registry.getApplicationContext().getBean("modelService");
		}
		if (flexibleSearchService == null) {
			flexibleSearchService = (FlexibleSearchService) Registry.getApplicationContext()
					.getBean("flexibleSearchService");
		}
	}

	@Override
	public void performImport(final String transformationExpression, final Item processedItem) throws ImpExException {

		super.performImport(transformationExpression.split("#")[0], processedItem);

		final String customerId = transformationExpression.split("#")[0];
		final String consentGuid = transformationExpression.split("#")[1];

		if (null != customerId) {
			final CustomerModel customermodel = getCustomerModel(customerId);
			if (null != customermodel) {
				final ConsentModel consentModel = getConsentModel(customermodel.getPk().toString());
				/**
				 * single guid caters single consentModel for a customer
				 */

				if (null != consentModel) {
					if (null == consentModel.getConsentGuid() && null != consentGuid && !consentGuid.equals("NA")) {
						consentModel.setConsentGuid(consentGuid);
						modelService.save(consentModel);
					}
					// Notification with record Guid = NA :: withdrawn from CRM
					if (consentGuid.equals("NA")) {
						consentModel.setConsentWithdrawnDate(new Date(System.currentTimeMillis()));
						consentModel.setConsentGuid(null);
						modelService.save(consentModel);
					}

				}
			}
		}
	}

	private CustomerModel getCustomerModel(final String customerId) {

		final Map<String, Object> params = new HashMap<>();
		final StringBuilder queryString = new StringBuilder("SELECT {").append(CustomerModel.PK).append("} ");
		queryString.append("FROM {").append(CustomerModel._TYPECODE).append("} ");
		queryString.append("WHERE {").append(CustomerModel.CUSTOMERID).append("} = (?customerId)");

		params.put("customerId", customerId);

		final SearchResult<CustomerModel> searchResults = getFlexibleSearchService().search(queryString.toString(),
				params);

		if (searchResults.getResult() != null) {
			return searchResults.getResult().get(0);
		}
		return null;
	}

	private ConsentModel getConsentModel(final String customerpk) {

		final Map<String, Object> params = new HashMap<>();
		final StringBuilder queryString = new StringBuilder("SELECT {").append(ConsentModel.PK).append("} ");
		queryString.append("FROM {").append(ConsentModel._TYPECODE).append("} ");
		queryString.append("WHERE {").append(ConsentModel.CUSTOMER).append("} = (?customerpk)");
		queryString.append(" AND {").append(ConsentModel.CONSENTWITHDRAWNDATE).append("} IS NULL");
		queryString.append(" ORDER BY {").append(ConsentModel.CONSENTGIVENDATE).append("} DESC");

		params.put("customerpk", customerpk);

		final SearchResult<ConsentModel> searchResults = getFlexibleSearchService().search(queryString.toString(),
				params);

		if (!searchResults.getResult().isEmpty()) {
			return searchResults.getResult().get(0);
		}
		return null;
	}

}
