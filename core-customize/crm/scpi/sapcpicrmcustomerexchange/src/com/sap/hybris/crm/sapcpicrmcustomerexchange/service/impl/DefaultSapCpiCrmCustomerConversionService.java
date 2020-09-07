/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.crm.sapcpicrmcustomerexchange.service.impl;

import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;
import de.hybris.platform.sap.sapcpicustomerexchange.service.impl.SapCpiCustomerDefaultConversionService;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.crm.sapcpicrmcustomerexchange.model.SAPCpiOutboundContactAddressModel;
import com.sap.hybris.crm.sapcpicrmcustomerexchange.model.SAPCpiOutboundCreditCardModel;
import com.sap.hybris.crm.sapcpicrmcustomerexchange.service.SapCpiCrmCustomerConversionService;
import com.sap.hybris.sapcrmcustomerb2c.constants.Sapcrmcustomerb2cConstants;
import com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants;


/**
 *
 */
public class DefaultSapCpiCrmCustomerConversionService extends SapCpiCustomerDefaultConversionService
		implements SapCpiCrmCustomerConversionService
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapCpiCrmCustomerConversionService.class);
	private SAPGlobalConfigurationDAO globalConfigurationDAO;

	private Set<SAPCpiOutboundContactAddressModel> mapb2cContactAddresses(final List<Map<String, Object>> b2cCustomerData)
	{
		final Set<SAPCpiOutboundContactAddressModel> sapCpiContactAddresses = new HashSet<>();
		b2cCustomerData.forEach(b2cCutomer -> {
			final SAPCpiOutboundContactAddressModel sapCpiOutboundContactAddress = new SAPCpiOutboundContactAddressModel();

			sapCpiOutboundContactAddress.setStreet(mapAttribute(Sapcustomerb2cConstants.STREET, b2cCutomer));
			sapCpiOutboundContactAddress.setStreetNumber(mapAttribute(Sapcustomerb2cConstants.STREETNUMBER, b2cCutomer));
			sapCpiOutboundContactAddress.setTown(mapAttribute(Sapcustomerb2cConstants.TOWN, b2cCutomer));
			sapCpiOutboundContactAddress.setCountryIsoCode(mapAttribute(Sapcrmcustomerb2cConstants.COUNTRYISO, b2cCutomer));
			sapCpiOutboundContactAddress.setPhone(mapAttribute(Sapcustomerb2cConstants.PHONE, b2cCutomer));
			sapCpiOutboundContactAddress.setPostalCode(mapAttribute(Sapcustomerb2cConstants.POSTALCODE, b2cCutomer));
			sapCpiOutboundContactAddress.setFaxNumber(mapAttribute(Sapcustomerb2cConstants.FAX, b2cCutomer));
			sapCpiOutboundContactAddress.setRegion(mapAttribute(Sapcustomerb2cConstants.REGION, b2cCutomer));
			sapCpiOutboundContactAddress.setIsDefault(mapAttribute(Sapcrmcustomerb2cConstants.IS_DEFAULT, b2cCutomer));
			sapCpiOutboundContactAddress.setAddressGuid(mapAttribute(Sapcrmcustomerb2cConstants.ADDRESS_GUID, b2cCutomer));

			sapCpiContactAddresses.add(sapCpiOutboundContactAddress);
		});

		return sapCpiContactAddresses;
	}



	protected void mapOutboundDestination(final SAPCpiOutboundCustomerModel sapCpiOutboundCustomer)
	{

		final SAPCpiOutboundConfigModel sapCpiOutboundConfig = new SAPCpiOutboundConfigModel();
		final SAPLogicalSystemModel logicalSystem = readLogicalSystem();

		sapCpiOutboundConfig.setSenderName(logicalSystem.getSenderName());
		sapCpiOutboundConfig.setSenderPort(logicalSystem.getSenderPort());
		sapCpiOutboundConfig.setReceiverName(logicalSystem.getSapLogicalSystemName());
		sapCpiOutboundConfig.setReceiverPort(logicalSystem.getSapLogicalSystemName());
		sapCpiOutboundConfig.setUrl(logicalSystem.getSapHTTPDestination().getTargetURL());
		sapCpiOutboundConfig.setUsername(logicalSystem.getSapHTTPDestination().getUserid());
		sapCpiOutboundConfig
				.setClient(logicalSystem.getSapHTTPDestination().getTargetURL().split("sap-client=")[1].substring(0, 3));

		sapCpiOutboundCustomer.setSapCpiConfig(sapCpiOutboundConfig);

	}

	protected SAPLogicalSystemModel readLogicalSystem()
	{

		final Set<SAPLogicalSystemModel> logicalSystems = getGlobalConfigurationDAO().getSAPGlobalConfiguration()
				.getSapLogicalSystemGlobalConfig();
		Objects.requireNonNull(logicalSystems,
				"The B2B customer cannot be sent to SCPI. There is no SAP logical system maintained in the back office!");

		return logicalSystems.stream().filter(SAPLogicalSystemModel::isDefaultLogicalSystem).findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						"The B2B customer cannot be sent to SCPI. There is no default SAP logical system maintained in the back office!"));

	}

	protected String mapAttribute(final String attribute, final Map<String, Object> row)
	{
		return row.get(attribute) != null ? row.get(attribute).toString() : null;
	}



	private Set<SAPCpiOutboundCreditCardModel> mapb2cCreditCards(final List<Map<String, Object>> b2cCustomerData)
	{

		final Set<SAPCpiOutboundCreditCardModel> sapCpiCreditcards = new HashSet<>();

		b2cCustomerData.forEach(b2cCutomer -> {

			final SAPCpiOutboundCreditCardModel sapCpiOutboundCreditCard = new SAPCpiOutboundCreditCardModel();

			sapCpiOutboundCreditCard.setCardId(mapAttribute(Sapcrmcustomerb2cConstants.CARDID, b2cCutomer));
			sapCpiOutboundCreditCard.setCardName(mapAttribute(Sapcrmcustomerb2cConstants.CARDNAME, b2cCutomer));
			sapCpiOutboundCreditCard.setCardType(mapAttribute(Sapcrmcustomerb2cConstants.CARDTYPE, b2cCutomer));
			sapCpiOutboundCreditCard.setStampName(mapAttribute(Sapcrmcustomerb2cConstants.STAMPNAME, b2cCutomer));
			sapCpiOutboundCreditCard.setCardNumber(mapAttribute(Sapcrmcustomerb2cConstants.CARDNUMBER, b2cCutomer));
			sapCpiOutboundCreditCard.setIsDefault(mapAttribute(Sapcrmcustomerb2cConstants.IS_DEFAULT, b2cCutomer));
			sapCpiOutboundCreditCard.setValidTo(mapAttribute(Sapcrmcustomerb2cConstants.VALIDTO, b2cCutomer));

			sapCpiCreditcards.add(sapCpiOutboundCreditCard);
		});

		return sapCpiCreditcards;
	}


	@Override
	public SAPCpiOutboundCustomerModel convertB2CCustomerToSapCpiCustomer(List<Map<String, Object>> b2cCustomerAddData,
			List<Map<String, Object>> b2cCustomerCreditData)
	{

		LOG.info("convertB2CCustomerToSapCpiCustomer method executed ...... ");
		final SAPCpiOutboundCustomerModel sapCpiOutboundCustomer = getModelService().create(SAPCpiOutboundCustomerModel.class);

		b2cCustomerAddData.stream().findFirst().ifPresent(b2cCutomer -> {

			sapCpiOutboundCustomer.setUid(mapAttribute(Sapcustomerb2cConstants.UID, b2cCutomer));
			sapCpiOutboundCustomer.setCustomerId(mapAttribute(Sapcustomerb2cConstants.CUSTOMER_ID, b2cCutomer));
			sapCpiOutboundCustomer.setContactId(mapAttribute(Sapcustomerb2cConstants.CONTACT_ID, b2cCutomer));
			sapCpiOutboundCustomer.setFirstName(mapAttribute(Sapcustomerb2cConstants.FIRSTNAME, b2cCutomer));
			sapCpiOutboundCustomer.setLastName(mapAttribute(Sapcustomerb2cConstants.LASTNAME, b2cCutomer));
			sapCpiOutboundCustomer.setSessionLanguage(mapAttribute(Sapcustomerb2cConstants.SESSION_LANGUAGE, b2cCutomer));
			sapCpiOutboundCustomer.setSapSessionLanguage(mapAttribute(Sapcrmcustomerb2cConstants.SAP_SESSION_LANGUAGE, b2cCutomer));
			sapCpiOutboundCustomer.setTitle(mapAttribute(Sapcustomerb2cConstants.TITLE, b2cCutomer));
			sapCpiOutboundCustomer.setCrmGuid(mapAttribute(Sapcrmcustomerb2cConstants.CRM_GUID, b2cCutomer));
			sapCpiOutboundCustomer.setB2cFeedType("CRMHybrisCustomer");

			sapCpiOutboundCustomer.setIsConsentGiven(mapAttribute(Sapcrmcustomerb2cConstants.ISCONSENTGIVEN, b2cCutomer));
			sapCpiOutboundCustomer.setChannel(mapAttribute(Sapcrmcustomerb2cConstants.CHANNEL, b2cCutomer));
			sapCpiOutboundCustomer.setConsent_flag(mapAttribute(Sapcrmcustomerb2cConstants.CONSENT_FLAG, b2cCutomer));
			sapCpiOutboundCustomer.setValid_from(mapAttribute(Sapcrmcustomerb2cConstants.VALID_FROM, b2cCutomer));
			sapCpiOutboundCustomer.setConsent_guid(mapAttribute(Sapcrmcustomerb2cConstants.CONSENT_GUID, b2cCutomer));

			sapCpiOutboundCustomer.setCentralBlock(mapAttribute(Sapcrmcustomerb2cConstants.CENTRAL_BLOCK, b2cCutomer));
			sapCpiOutboundCustomer
					.setCentralArchivingFlag(mapAttribute(Sapcrmcustomerb2cConstants.CENTRAL_ARCHIVING_FLAG, b2cCutomer));
			sapCpiOutboundCustomer.setNotReleased(mapAttribute(Sapcrmcustomerb2cConstants.NOT_RELEASED, b2cCutomer));



		});
		sapCpiOutboundCustomer.setSapCpiOutboundContactAddress(mapb2cContactAddresses(b2cCustomerAddData));

		if (b2cCustomerCreditData != null && !b2cCustomerCreditData.isEmpty())
		{
			sapCpiOutboundCustomer.setSapCpiOutboundCreditCard(mapb2cCreditCards(b2cCustomerCreditData));
		}

		mapOutboundDestination(sapCpiOutboundCustomer);

		return sapCpiOutboundCustomer;
	}



	/**
	 * @return the globalConfigurationDAO
	 */
	public SAPGlobalConfigurationDAO getGlobalConfigurationDAO()
	{
		return globalConfigurationDAO;
	}



	/**
	 * @param globalConfigurationDAO
	 *           the globalConfigurationDAO to set
	 */
	public void setGlobalConfigurationDAO(SAPGlobalConfigurationDAO globalConfigurationDAO)
	{
		this.globalConfigurationDAO = globalConfigurationDAO;
	}





}
