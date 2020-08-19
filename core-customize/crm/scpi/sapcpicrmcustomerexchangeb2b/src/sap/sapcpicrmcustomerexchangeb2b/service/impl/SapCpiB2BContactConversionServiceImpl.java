/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package sap.sapcpicrmcustomerexchangeb2b.service.impl;

import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BCustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import sap.sapcpicrmcustomerexchangeb2b.service.SapCpiB2BContactConversionService;

import com.sap.hybris.crm.sapcpicrmcustomerexchange.model.SAPCpiOutboundContactAddressModel;
import com.sap.hybris.sapcrmcustomerb2b.constants.Sapcrmcustomerb2bConstants;


/**
 *
 */
public class SapCpiB2BContactConversionServiceImpl implements SapCpiB2BContactConversionService
{

	private static final String B2BRELATION = "HybrisB2BCustomerCompanyRelation";
	private static final String B2BCONTACT = "HybrisCRMB2BContact";

	private SAPGlobalConfigurationDAO globalConfigurationDAO;

	@Override
	public SAPCpiOutboundB2BCustomerModel convertB2BCustomerToSapCpiBb2BCustomer(final List<Map<String, Object>> rawData,
			final String rawType)
	{

		final SAPCpiOutboundB2BCustomerModel sapCpiOutboundB2BCustomer = new SAPCpiOutboundB2BCustomerModel();
		// XXX Auto-generated method stub

		if (rawType.equalsIgnoreCase(B2BRELATION))
		{
			rawData.stream().findFirst().ifPresent(b2bContact -> {

				sapCpiOutboundB2BCustomer.setUid(mapAttribute(Sapcrmcustomerb2bConstants.UID, b2bContact));
				sapCpiOutboundB2BCustomer.setCustomerId(mapAttribute(Sapcrmcustomerb2bConstants.CONTACT_ID, b2bContact));
				sapCpiOutboundB2BCustomer.setContactId(mapAttribute(Sapcrmcustomerb2bConstants.CUSTOMERID, b2bContact));
				sapCpiOutboundB2BCustomer.setGroups(mapAttribute(Sapcrmcustomerb2bConstants.B2BGROUP, b2bContact));
				sapCpiOutboundB2BCustomer.setB2bContactType(B2BRELATION);

			});

			mapOutboundDestination(sapCpiOutboundB2BCustomer);

		}
		else if (rawType.equalsIgnoreCase(B2BCONTACT))
		{

			rawData.stream().findFirst().ifPresent(b2bContact -> {

				sapCpiOutboundB2BCustomer.setUid(mapAttribute(Sapcrmcustomerb2bConstants.UID, b2bContact));
				sapCpiOutboundB2BCustomer.setCustomerId(mapAttribute(Sapcrmcustomerb2bConstants.CONTACT_ID, b2bContact));
				sapCpiOutboundB2BCustomer.setContactId(mapAttribute(Sapcrmcustomerb2bConstants.CUSTOMERID, b2bContact));
				sapCpiOutboundB2BCustomer.setFirstName(mapAttribute(Sapcrmcustomerb2bConstants.FIRSTNAME, b2bContact));
				sapCpiOutboundB2BCustomer.setLastName(mapAttribute(Sapcrmcustomerb2bConstants.LASTNAME, b2bContact));
				sapCpiOutboundB2BCustomer.setSessionLanguage(mapAttribute(Sapcrmcustomerb2bConstants.SESSION_LANGUAGE, b2bContact));
				sapCpiOutboundB2BCustomer.setTitle(mapAttribute(Sapcrmcustomerb2bConstants.TITLE, b2bContact));
				sapCpiOutboundB2BCustomer.setCrmGuid(mapAttribute(Sapcrmcustomerb2bConstants.PARTYGUID, b2bContact));
				sapCpiOutboundB2BCustomer.setGroups(mapAttribute(Sapcrmcustomerb2bConstants.B2BGROUP, b2bContact));
				sapCpiOutboundB2BCustomer.setB2bContactType(B2BCONTACT);

			});


			sapCpiOutboundB2BCustomer.setSapCpiOutboundContactAddress(mapContactAddresses(rawData));
			mapOutboundDestination(sapCpiOutboundB2BCustomer);
		}
		return sapCpiOutboundB2BCustomer;
	}




	/**
	 *
	 */
	private Set<SAPCpiOutboundContactAddressModel> mapContactAddresses(final List<Map<String, Object>> rawData)
	{

		final Set<SAPCpiOutboundContactAddressModel> sapCpiContactAddresses = new HashSet<>();

		rawData.forEach(contactAddress -> {

			final SAPCpiOutboundContactAddressModel sapCpiOutboundContactAddress = new SAPCpiOutboundContactAddressModel();

			sapCpiOutboundContactAddress.setStreet(mapAttribute(Sapcrmcustomerb2bConstants.STREET, contactAddress));
			sapCpiOutboundContactAddress.setStreetNumber(mapAttribute(Sapcrmcustomerb2bConstants.STREETNUMBER, contactAddress));
			sapCpiOutboundContactAddress.setTown(mapAttribute(Sapcrmcustomerb2bConstants.TOWN, contactAddress));
			sapCpiOutboundContactAddress.setCountryIsoCode(mapAttribute(Sapcrmcustomerb2bConstants.COUNTRY, contactAddress));
			sapCpiOutboundContactAddress.setPhone(mapAttribute(Sapcrmcustomerb2bConstants.PHONE, contactAddress));
			sapCpiOutboundContactAddress.setPostalCode(mapAttribute(Sapcrmcustomerb2bConstants.POSTALCODE, contactAddress));
			sapCpiOutboundContactAddress.setFaxNumber(mapAttribute(Sapcrmcustomerb2bConstants.FAX, contactAddress));
			sapCpiOutboundContactAddress.setIsDefault(mapAttribute(Sapcrmcustomerb2bConstants.ISDEFAULT, contactAddress));
			sapCpiOutboundContactAddress.setAddressGuid(mapAttribute(Sapcrmcustomerb2bConstants.ADDRESSGUID, contactAddress));
			sapCpiOutboundContactAddress.setDeleteAddress(mapAttribute(Sapcrmcustomerb2bConstants.DELETEADDRESS, contactAddress));

			sapCpiContactAddresses.add(sapCpiOutboundContactAddress);
		});

		return sapCpiContactAddresses;
	}


	protected void mapOutboundDestination(final SAPCpiOutboundB2BCustomerModel sapCpiOutboundB2BCustomer)
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

		sapCpiOutboundB2BCustomer.setSapCpiConfig(sapCpiOutboundConfig);

	}

	protected String mapAttribute(final String attribute, final Map<String, Object> row)
	{
		return row.get(attribute) != null ? row.get(attribute).toString() : null;
	}

	protected SAPLogicalSystemModel readLogicalSystem()
	{

		final Set<SAPLogicalSystemModel> logicalSystems = getGlobalConfigurationDAO().getSAPGlobalConfiguration()
				.getSapLogicalSystemGlobalConfig();
		Objects.requireNonNull(logicalSystems,
				"The B2B customer cannot be sent to SCPI. There is no SAP logical system maintained in the back office!");

		return logicalSystems
				.stream()
				.filter(SAPLogicalSystemModel::isDefaultLogicalSystem)
				.findFirst()
				.orElseThrow(
						() -> new IllegalArgumentException(
								"The B2B customer cannot be sent to SCPI. There is no default SAP logical system maintained in the back office!"));

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
	public void setGlobalConfigurationDAO(final SAPGlobalConfigurationDAO globalConfigurationDAO)
	{
		this.globalConfigurationDAO = globalConfigurationDAO;
	}





}
