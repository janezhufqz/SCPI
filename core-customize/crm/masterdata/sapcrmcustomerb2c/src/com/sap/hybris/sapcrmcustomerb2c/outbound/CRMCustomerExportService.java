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

import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.CONTACT_ID;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.COUNTRY;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.COUNTRY_DE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.CUSTOMER_ID;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.DEFAULT_FEED;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.FAX;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.FIRSTNAME;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.LASTNAME;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.POSTALCODE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.REGION;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.SESSION_LANGUAGE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.STREET;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.STREETNUMBER;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.TITLE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.TOWN;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.UID;
import static java.util.Objects.nonNull;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.site.BaseSiteService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.datahub.core.rest.DataHubOutboundException;
import com.hybris.datahub.core.services.DataHubOutboundService;
import com.sap.hybris.sapcrmcustomerb2c.constants.Sapcrmcustomerb2cConstants;
import com.sap.hybris.sapcustomerb2c.outbound.CustomerExportService;
/**
 * @author C5229471
 *
 */
public class CRMCustomerExportService extends CustomerExportService {

	private static final Logger LOGGER = Logger
			.getLogger(com.sap.hybris.sapcustomerb2c.outbound.CustomerExportService.class.getName());

	private CommerceConsentService commerceConsentService;
	private BaseSiteService baseSiteService;
	private CustomerNameStrategy customerNameStrategy;
	private DataHubOutboundService dataHubOutboundService;
	private CommonI18NService commonI18NService;
	private SAPGlobalConfigurationService sapGlobalConfigurationService;
	private String feed = DEFAULT_FEED;
	private String cardType;
	private String consentTemplateId;
	private Integer consentTemplateVersion;
	private ConsentModel consentModel;
	private FlexibleSearchService flexibleSearchService;
	private ModelService modelService;
	
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
	 * @return the consentModel
	 */
	public ConsentModel getConsentModel() {
		return consentModel;
	}

	/**
	 * @param consentModel
	 *            the consentModel to set
	 */
	public void setConsentModel(final ConsentModel consentModel) {
		this.consentModel = consentModel;
	}

	/**
	 * @return the consentTemplateId
	 */
	public String getConsentTemplateId() {
		return consentTemplateId;
	}

	/**
	 * @param consentTemplateId
	 *            the consentTemplateId to set
	 */
	public void setConsentTemplateId(final String consentTemplateId) {
		this.consentTemplateId = consentTemplateId;
	}

	/**
	 * @return the consentTemplateVersion
	 */
	public Integer getConsentTemplateVersion() {
		return consentTemplateVersion;
	}

	/**
	 * @param consentTemplateVersion
	 *            the consentTemplateVersion to set
	 */
	public void setConsentTemplateVersion(final Integer consentTemplateVersion) {
		this.consentTemplateVersion = consentTemplateVersion;
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
	 * @return the commerceConsentService
	 */
	public CommerceConsentService getCommerceConsentService() {
		return commerceConsentService;
	}

	/**
	 * @param commerceConsentService
	 *            the commerceConsentService to set
	 */
	public void setCommerceConsentService(final CommerceConsentService commerceConsentService) {
		this.commerceConsentService = commerceConsentService;
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
	public void setSapGlobalConfigurationService(final SAPGlobalConfigurationService sapGlobalConfigurationService) {
		this.sapGlobalConfigurationService = sapGlobalConfigurationService;
	}

	/**
	 * return Data Hub Outbound Service
	 *
	 * @return dataHubOutboundService
	 */
	@Override
	public DataHubOutboundService getDataHubOutboundService() {
		return dataHubOutboundService;
	}

	/**
	 * set Data Hub Outbound Service
	 *
	 * @param dataHubOutboundService
	 */
	@Override
	public void setDataHubOutboundService(final DataHubOutboundService dataHubOutboundService) {
		this.dataHubOutboundService = dataHubOutboundService;
	}

	/**
	 * map customer Model to the target map, set session language and base store
	 * name, and send data to the Data Hub
	 *
	 * @param customerModel
	 * @param baseStoreUid
	 * @param sessionLanguage
	 */
	@Override
	public void sendCustomerData(final CustomerModel customerModel, final String baseStoreUid,
			final String sessionLanguage) {
		sendCustomerData(customerModel, baseStoreUid, sessionLanguage, null, null);
	}

	@Override
	public void sendCustomerData(final CustomerModel customerModel, final String baseStoreUid,
			final String sessionLanguage, final AddressModel addressModel) {
		sendCustomerData(customerModel, baseStoreUid, sessionLanguage, addressModel, null);
	}

	/**
	 * map customer Model and address Model to the target map, set session
	 * language and base store name, and send data to the Data Hub
	 *
	 * @param customerModel
	 * @param baseStoreUid
	 * @param sessionLanguage
	 * @param addressModel
	 */

	public void sendCustomerData(final CustomerModel customerModel, final String baseStoreUid,
			final String sessionLanguage, final AddressModel addressModel, final String baseSiteUid) {
		final Map<String, Object> target = prepareCompleteCustomerDetailsAndAddressData(customerModel, baseStoreUid,
				sessionLanguage, addressModel, baseSiteUid);
		sendCustomerToDataHub(target);
	}

	/**
	 * @param customerModel
	 * @param baseStoreUid
	 * @param sessionLanguage
	 * @param addressModel
	 * @param baseSiteUid
	 * @param target
	 */
	public Map<String, Object> prepareCompleteCustomerDetailsAndAddressData(final CustomerModel customerModel,
			final String baseStoreUid, final String sessionLanguage, final AddressModel addressModel,
			final String baseSiteUid) {
		final Map<String, Object> target = getTarget();
		prepareCustomerData(customerModel, baseStoreUid, sessionLanguage, target, baseSiteUid);

		if (addressModel == null) {
			target.put(Sapcrmcustomerb2cConstants.COUNTRYISO, COUNTRY_DE);
			target.put(Sapcrmcustomerb2cConstants.ADDRESS_GUID,
					UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
			target.put(Sapcrmcustomerb2cConstants.IS_DEFAULT, Boolean.TRUE);
		} else {
			prepareAddressData(addressModel, target);
		}
		return target;
	}

	public Map<String, Object> deleteCompleteCustomerDetailsAndAddressData(final CustomerModel customerModel,
			final String baseStoreUid, final String sessionLanguage, final AddressModel addressModel,
			final String baseSiteUid) {
		final Map<String, Object> target = getTarget();
		deleteCustomerData(customerModel, baseStoreUid, sessionLanguage, target, baseSiteUid);

		if (addressModel == null) {
			target.put(Sapcrmcustomerb2cConstants.COUNTRYISO, COUNTRY_DE);
			target.put(Sapcrmcustomerb2cConstants.ADDRESS_GUID,
					UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
			target.put(Sapcrmcustomerb2cConstants.IS_DEFAULT, Boolean.TRUE);
		} else {
			prepareAddressData(addressModel, target);
		}
		return target;
	}

	public Map<String, Object> prepareCustomerDeleteData(final CustomerModel customerModel) {
		final Map<String, Object> target = deleteCompleteCustomerDetailsAndAddressData(customerModel, null, null, null,
				null);
		target.put(Sapcrmcustomerb2cConstants.CENTRAL_BLOCK, Boolean.TRUE);
		target.put(Sapcrmcustomerb2cConstants.CENTRAL_ARCHIVING_FLAG, Boolean.TRUE);
		target.put(Sapcrmcustomerb2cConstants.NOT_RELEASED, Boolean.TRUE);
		return target;
	}

	public void sendCreditcardData(final CustomerModel customerModel,
			final CreditCardPaymentInfoModel creditCardPaymentInfoModel) {

		List<Map<String, Object>> objList;
		objList = cardDataEnrty(customerModel);
		prepareCreditCardData(customerModel, creditCardPaymentInfoModel, objList);
		final Boolean replicateregistereduser = getSapGlobalConfigurationService()
				.getProperty(Sapcrmcustomerb2cConstants.REPLICATE_REGISTERED_USER);
		if (replicateregistereduser.booleanValue() && !objList.isEmpty()) {
			sendCreditCardToDataHub(objList);
		}

	}

	public void prepareCreditCardData(final CustomerModel customerModel,
			final CreditCardPaymentInfoModel creditCardinfomodel, final List<Map<String, Object>> objList) {

		if (creditCardinfomodel != null) {
			final String latestCardInc = getLatestCardIncForCustomer(objList);
			creditCardinfomodel.setCardInc(latestCardInc);
			creditCardinfomodel.setCode(customerModel.getCustomerID() + "_" + latestCardInc);
			objList.add(initiateCreditCardData(creditCardinfomodel));
		}
	}

	public List<Map<String, Object>> prepareCustomerCreditCardDetailsData(final CustomerModel customerModel,
			final CreditCardPaymentInfoModel creditCardPaymentInfoModel) {

		List<Map<String, Object>> objList;
		objList = cardDataEnrty(customerModel);
		prepareCreditCardData(customerModel, creditCardPaymentInfoModel, objList);
		return objList;
	}

	/**
	 * @return new target instance
	 */
	@Override
	protected Map<String, Object> getTarget() {
		return new HashMap<>();
	}

	protected void prepareCustomerData(final CustomerModel customerModel, final String baseStoreUid,
			final String sessionLanguage, final Map<String, Object> target, final String baseSiteUid) {
		final String[] names = customerNameStrategy.splitName(customerModel.getName());

		target.put(UID, customerModel.getUid());
		target.put(CUSTOMER_ID, customerModel.getCustomerID());
		target.put(CONTACT_ID, customerModel.getSapContactID());
		target.put(FIRSTNAME, names[0]);
		target.put(LASTNAME, names[1]);
		target.put(SESSION_LANGUAGE, sessionLanguage);
		target.put(Sapcrmcustomerb2cConstants.SAP_SESSION_LANGUAGE,
				getCommonI18NService().getCurrentLanguage().getSapCode());
		target.put(Sapcrmcustomerb2cConstants.CRM_GUID, customerModel.getCrmGuid());
		if (null != customerModel.getTitle()) {
			target.put(TITLE, customerModel.getTitle().getCode());
		}
		if (baseSiteUid != null) {
			retrieveConsentModel(customerModel, baseSiteUid);
			if (null != getConsentModel()) {
				if (getConsentModel().getConsentWithdrawnDate() == null && getConsentModel().getConsentGuid() == null) {
					LOGGER.info("consent given !!! " + getConsentModel().getConsentWithdrawnDate() + " :: "
							+ getConsentModel().getConsentGuid());
					prepareConsentData(target);
				}
				if (null != getConsentModel().getConsentWithdrawnDate() && null != getConsentModel().getConsentGuid()) {
					LOGGER.info("consent withdrawn !!! " + getConsentModel().getConsentWithdrawnDate() + " :: "
							+ getConsentModel().getConsentGuid());
					prepareConsentData(target);
					getConsentModel().setConsentGuid(null);
					getModelService().save(getConsentModel());
					LOGGER.info("consent guid set to :: " + getConsentModel().getConsentGuid());
				}

			}
		}
	}

	private ConsentModel getConsentModelForCustomer(final String customerpk) {

		final Map<String, Object> params = new HashMap<>();
		final StringBuilder queryString = new StringBuilder("SELECT {").append(ConsentModel.PK).append("} ");
		queryString.append("FROM {").append(ConsentModel._TYPECODE).append("} ");
		queryString.append("WHERE {").append(ConsentModel.CUSTOMER).append("} = (?customerpk)");
		queryString.append(" AND {").append(ConsentModel.CONSENTWITHDRAWNDATE).append("} IS NULL");

		params.put("customerpk", customerpk);

		final SearchResult<ConsentModel> searchResults = getFlexibleSearchService().search(queryString.toString(),
				params);

		if (!searchResults.getResult().isEmpty()) {
			return searchResults.getResult().get(0);
		}
		return null;
	}

	protected void deleteCustomerData(final CustomerModel customerModel, final String baseStoreUid,
			final String sessionLanguage, final Map<String, Object> target, final String baseSiteUid) {
		final String[] names = customerNameStrategy.splitName(customerModel.getName());

		target.put(UID, customerModel.getUid());
		target.put(CUSTOMER_ID, customerModel.getCustomerID());
		target.put(CONTACT_ID, customerModel.getSapContactID());
		target.put(FIRSTNAME, names[0]);
		target.put(LASTNAME, names[1]);
		target.put(SESSION_LANGUAGE, sessionLanguage);
		target.put(Sapcrmcustomerb2cConstants.SAP_SESSION_LANGUAGE,
				getCommonI18NService().getCurrentLanguage().getSapCode());
		target.put(Sapcrmcustomerb2cConstants.CRM_GUID, customerModel.getCrmGuid());
		if (null != customerModel.getTitle()) {
			target.put(TITLE, customerModel.getTitle().getCode());
		}
		setConsentModel(getConsentModelForCustomer(customerModel.getPk().toString()));
		if ((null != getConsentModel()) && (null == getConsentModel().getConsentWithdrawnDate()
				&& null != getConsentModel().getConsentGuid())) {
			LOGGER.error(getConsentModel().getConsentWithdrawnDate() + " :: " + getConsentModel().getConsentGuid());
			deleteConsentData(target);
		}
	}

	private void prepareConsentData(final Map<String, Object> target) {

		target.put(Sapcrmcustomerb2cConstants.ISCONSENTGIVEN, Sapcrmcustomerb2cConstants.CONSENT_GIVEN);
		target.put(Sapcrmcustomerb2cConstants.CHANNEL, Sapcrmcustomerb2cConstants.CHANNEL_INTERNET);
		target.put(Sapcrmcustomerb2cConstants.CONSENT_FLAG, nonNull(consentModel.getConsentWithdrawnDate())
				? Sapcrmcustomerb2cConstants.DELETE : Sapcrmcustomerb2cConstants.INSERT);
		final SimpleDateFormat formatter = new SimpleDateFormat(Sapcrmcustomerb2cConstants.DATEFORMAT);
		target.put(Sapcrmcustomerb2cConstants.VALID_FROM, formatter.format(getConsentModel().getConsentGivenDate())
				.replaceAll(Sapcrmcustomerb2cConstants.HYPHEN, Sapcrmcustomerb2cConstants.NULL));
		target.put(Sapcrmcustomerb2cConstants.CONSENT_GUID, getConsentModel().getConsentGuid());

	}

	private void deleteConsentData(final Map<String, Object> target) {

		target.put(Sapcrmcustomerb2cConstants.ISCONSENTGIVEN, Sapcrmcustomerb2cConstants.CONSENT_GIVEN);
		target.put(Sapcrmcustomerb2cConstants.CHANNEL, Sapcrmcustomerb2cConstants.CHANNEL_INTERNET);
		target.put(Sapcrmcustomerb2cConstants.CONSENT_FLAG, Sapcrmcustomerb2cConstants.DELETE);
		final SimpleDateFormat formatter = new SimpleDateFormat(Sapcrmcustomerb2cConstants.DATEFORMAT);
		target.put(Sapcrmcustomerb2cConstants.VALID_FROM, formatter.format(getConsentModel().getConsentGivenDate())
				.replaceAll(Sapcrmcustomerb2cConstants.HYPHEN, Sapcrmcustomerb2cConstants.NULL));
		target.put(Sapcrmcustomerb2cConstants.CONSENT_GUID, getConsentModel().getConsentGuid());

	}

	/**
	 * @param customerModel
	 * @param baseSiteUid
	 *
	 * @return ConsentModel
	 */
	private void retrieveConsentModel(final CustomerModel customerModel, final String baseSiteUid) {

		final BaseSiteModel basesitemodel = this.getBaseSiteService().getBaseSiteForUID(baseSiteUid);
		final ConsentTemplateModel consentTemplate = getCommerceConsentService().getConsentTemplate(consentTemplateId,
				consentTemplateVersion, basesitemodel);
		setConsentModel(getCommerceConsentService().getActiveConsent(customerModel, consentTemplate));
	}

	@Override
	protected void prepareAddressData(final AddressModel addressModel, final Map<String, Object> target) {
		final String countryIsoCode = addressModel.getCountry() != null ? addressModel.getCountry().getIsocode() : null;
		final String countryCode = addressModel.getCountry() != null ? addressModel.getCountry().getSapCode() : null;
		target.put(COUNTRY, countryCode);
		target.put(Sapcrmcustomerb2cConstants.COUNTRYISO, countryIsoCode);

		target.put(STREET, addressModel.getStreetname());
		target.put(Sapcrmcustomerb2cConstants.PHONE, addressModel.getPhone1());
		target.put(FAX, addressModel.getFax());
		target.put(TOWN, addressModel.getTown());
		target.put(POSTALCODE, addressModel.getPostalcode());
		target.put(STREETNUMBER, addressModel.getStreetnumber());

		final String regionIsoCode = addressModel.getRegion() != null ? addressModel.getRegion().getIsocodeShort()
				: null;
		target.put(REGION, regionIsoCode);
		target.put(Sapcrmcustomerb2cConstants.ADDRESS_GUID, addressModel.getPublicKey());
		final CustomerModel owner = (CustomerModel) addressModel.getOwner();
		if (null != owner) {
			final AddressModel defaultShipmentAddress = owner.getDefaultShipmentAddress();
			if ((null != defaultShipmentAddress) && (defaultShipmentAddress.getPk().equals(addressModel.getPk()))) {
				target.put(Sapcrmcustomerb2cConstants.IS_DEFAULT, Boolean.TRUE);
			} else {
				target.put(Sapcrmcustomerb2cConstants.IS_DEFAULT, Boolean.FALSE);
			}
		}
	}

	/**
	 * @param customerModel
	 * @return
	 */
	protected List<Map<String, Object>> cardDataEnrty(final CustomerModel customerModel) {
		final List<Map<String, Object>> objList = new ArrayList<>();

		final Collection<PaymentInfoModel> paymentInfoModels = customerModel.getPaymentInfos();
		final Iterator<PaymentInfoModel> itr = paymentInfoModels.iterator();

		while (itr.hasNext()) {
			final PaymentInfoModel paymentInfomodel = itr.next();
			if (paymentInfomodel != null && paymentInfomodel instanceof CreditCardPaymentInfoModel) {

				final CreditCardPaymentInfoModel creditCardPaymentInfoModel = (CreditCardPaymentInfoModel) paymentInfomodel;
				creditCardEntryMapper(objList, creditCardPaymentInfoModel);

			}

		}
		return objList;
	}

	/**
	 * @param objList
	 * @param creditCardPaymentInfoModel
	 */
	protected void creditCardEntryMapper(final List<Map<String, Object>> objList,
			final CreditCardPaymentInfoModel creditCardPaymentInfoModel) {
		if (creditCardPaymentInfoModel.getCardInc() != null && creditCardPaymentInfoModel.isSaved()) {
			objList.add(initiateCreditCardData(creditCardPaymentInfoModel));
		}
	}

	protected Map<String, Object> initiateCreditCardData(final CreditCardPaymentInfoModel creditCardPaymentInfoModel) {

		final Map<String, Object> target = new HashMap<>();
		final CustomerModel customerModel = (CustomerModel) creditCardPaymentInfoModel.getUser();
		target.put(Sapcrmcustomerb2cConstants.PARTYID, customerModel.getCustomerID());
		target.put(Sapcrmcustomerb2cConstants.CARDID, creditCardPaymentInfoModel.getCardInc());
		target.put(Sapcrmcustomerb2cConstants.CARDNAME, creditCardPaymentInfoModel.getCcOwner());
		target.put(Sapcrmcustomerb2cConstants.CARDTYPE, this.getCardType());
		target.put(Sapcrmcustomerb2cConstants.STAMPNAME, creditCardPaymentInfoModel.getCcOwner());
		target.put(Sapcrmcustomerb2cConstants.CARDNUMBER, creditCardPaymentInfoModel.getSubscriptionId());

		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
		final String dateInString = creditCardPaymentInfoModel.getValidToYear() + "-"
				+ creditCardPaymentInfoModel.getValidToMonth() + "-"
				+ LocalDate.of(Integer.parseInt(creditCardPaymentInfoModel.getValidToYear()),
						Integer.parseInt(creditCardPaymentInfoModel.getValidToMonth()), 1).lengthOfMonth();
		final CustomerModel owner = (CustomerModel) creditCardPaymentInfoModel.getUser();
		if (null != owner) {
			final CreditCardPaymentInfoModel creditcardPaymentInfoModel = (CreditCardPaymentInfoModel) owner
					.getDefaultPaymentInfo();
			if ((null != creditcardPaymentInfoModel)
					&& (creditcardPaymentInfoModel.getPk().equals(creditCardPaymentInfoModel.getPk()))) {
				target.put(Sapcrmcustomerb2cConstants.IS_DEFAULT, Boolean.TRUE);
			} else {
				target.put(Sapcrmcustomerb2cConstants.IS_DEFAULT, Boolean.FALSE);
			}
		}
		try {
			final Date date = formatter.parse(dateInString);
			target.put(Sapcrmcustomerb2cConstants.VALIDTO, formatter.format(date).replaceAll("-", ""));
		} catch (final ParseException e) {
			LOGGER.info("Date Parsing Exception is ------------------");
		}

		return target;
	}

	/**
	 * @param cardList
	 * @return Card Inc For new Credit Card
	 */
	protected String getLatestCardIncForCustomer(final List<Map<String, Object>> cardList) {
		int lastCardInc = 0;
		for (int i = 0; i < cardList.size(); i++) {
			final Map<String, Object> target = cardList.get(i);
			final String cardInc = (String) target.get(Sapcrmcustomerb2cConstants.CARDID);
			if (cardInc.matches(Sapcrmcustomerb2cConstants.REGEX_PATTERN_1)) {
				final int cardId = Integer.parseInt(cardInc);
				if (cardId >= lastCardInc) {
					lastCardInc = cardId;
				}
			}
		}
		return Integer.toString(lastCardInc + 1);
	}

	/**
	 * return data hub feed
	 *
	 * @return feed
	 */
	@Override
	public String getFeed() {
		return feed;
	}

	/**
	 * set data hub feed (usually set via the local property file)
	 *
	 * @param feed
	 */
	@Override
	public void setFeed(final String feed) {
		this.feed = feed;
	}

	/**
	 * @return customerNameStrategy
	 */
	@Override
	public CustomerNameStrategy getCustomerNameStrategy() {
		return customerNameStrategy;
	}

	/**
	 * @param customerNameStrategy
	 */
	@Override
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy) {
		this.customerNameStrategy = customerNameStrategy;
	}

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService() {
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *            the commonI18NService to set
	 */
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService) {
		this.commonI18NService = commonI18NService;
	}

	@Override
	public void sendCustomerToDataHub(final Map<String, Object> target) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("The following values was send to Data Hub" + target);
			LOGGER.debug("To the feed" + getFeed() + " into raw model "
					+ Sapcrmcustomerb2cConstants.RAW_CRM_HYBRIS_CUSTOMER);
		}
		try {
			getDataHubOutboundService().sendToDataHub(getFeed(), Sapcrmcustomerb2cConstants.RAW_CRM_HYBRIS_CUSTOMER,
					target);
		} catch (final DataHubOutboundException e) {
			LOGGER.warn("Error processing sending data to Data Hub. DataHubOutboundException: ", e);
		}
	}

	public void sendCreditCardToDataHub(final List<Map<String, Object>> target) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("The following values was send to Data Hub" + target);

		}
		try {
			getDataHubOutboundService().sendToDataHub(Sapcrmcustomerb2cConstants.SAPCONSUMER_OUTBOUND_FEED,
					Sapcrmcustomerb2cConstants.RAW_CRM_HYBRIS_CREDITCARD, target);
		} catch (final DataHubOutboundException e) {
			LOGGER.warn("Error processing sending data to Data Hub. DataHubOutboundException: ", e);
		} 
	}

	/**
	 * @return the cardType
	 */
	public String getCardType() {
		return cardType;
	}

	/**
	 * @param cardType
	 *            the cardType to set
	 */
	public void setCardType(final String cardType) {
		this.cardType = cardType;
	}
}
