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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm.strategy;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.PartnerFunctionData;
import de.hybris.platform.sap.sapcommonbol.constants.SapcommonbolConstants;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionTools;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerListEntry;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.PartnerBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.genilutil.CRMAddressResolverUtil;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.BackendState;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.CRMConstants;

/**
 * Class is responsible to map partner information between LO-API and the BOL
 * layer
 */
public class PartnerMapper extends BaseMapper {

	/**
	 * ID of LO-API segment which deals with partners
	 */
	public static final String OBJECT_ID_PARTY = "PARTY";
	public static final String PARTNER_ADDRESS_NUMBER = "ADDR_NR";
	/**
	 * Logging instance
	 */
	public static final Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(PartnerMapper.class.getName());

	/**
	 * Factory to access SAP session beans
	 */
	protected GenericFactory genericFactory = null;

	@Autowired
	private SessionService sessionService;

	@Resource(name = "crmAddressResolverUtil")
	CRMAddressResolverUtil crmAddressResolverUtil;

	Map<String, String> addressMapWithId = null;
	Map<String, String> addressMapWithGuid = null;

	/**
	 * Injected generic factory.
	 *
	 * @param genericFactory
	 */
	public void setGenericFactory(final GenericFactory genericFactory) {
		this.genericFactory = genericFactory;
	}

	@Override
	public void init() {
		/* nothing to initialize */
	}

	/**
	 * @param etPartner
	 *            - is CRM table "ET_PARTNER"
	 * @param salesDoc
	 *            - representing sales documents like Basket or Order
	 * @param baseR3Lrd
	 *            - Represents the state of a sales document
	 * @param header
	 *            - provides header data of a SalesDocument
	 */
	public void read(final JCoTable etPartner, final SalesDocument salesDoc,
			final BackendState baseR3Lrd, final Header header) {

		if ((etPartner == null) || (etPartner.getNumRows() <= 0)) {
			return;
		}

		header.setBillTo(salesDoc.createBillTo());

		final ShipTo shipTo = salesDoc.createShipTo();

		if (sapLogger.isDebugEnabled()) {
			sapLogger.debug("handleEtPartner)");
		}

		for (int i = 0; i < etPartner.getNumRows(); i++) {
			etPartner.setRow(i);
			final String partnerFunction = etPartner
					.getString(CRMConstants.PARTNER_FCT);
			if (partnerFunction != null) {
				final String partnerId = ConversionTools
						.addLeadingZerosToNumericID(
								etPartner.getString(CRMConstants.PARTNER_ID),
								10);
				final PartnerListEntry partner = (PartnerListEntry) genericFactory
						.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST_ENTRY);
				partner.setPartnerId(partnerId);
				partner.setPartnerTechKey(new TechKey(partnerId));
				partner.setHandle(etPartner.getString(CRMConstants.REF_HANDLE));
				final String businessPartnerRole = mapPartnerFunctionToRole(partnerFunction);
				if (businessPartnerRole != null) {
					header.getPartnerList().setPartnerData(businessPartnerRole,
							partner);
					sapLogger.debug("added partner: " + partnerId
							+ partnerFunction);
				}

				fillPatnerReference(partnerFunction, etPartner, shipTo, header,
						partner, baseR3Lrd);
			}
			etPartner.nextRow();

		}
	}

	/**
	 * @param etPartner
	 * @param shipTo
	 * @param header
	 */
	private void fillPatnerReference(final String partnerFunction,
			final JCoTable etPartner, final ShipTo shipTo, final Header header,
			final PartnerListEntry partner, final BackendState baseR3Lrd) {
		if (partnerFunction.equals(CRMConstants.ROLE_BILLPARTY)) {
			fillBillToToAddress(etPartner, header);

		}

		else if (partnerFunction.equals(CRMConstants.ROLE_SHIPTO)) {
			fillShipToAddress(etPartner, shipTo, header);

		}

		else if (partnerFunction.equals(CRMConstants.ROLE_CONTACT)) {
			header.getPartnerList().setContactData(partner);
		} else if (partnerFunction.equals(CRMConstants.ROLE_SOLDTO)) {
			header.getPartnerList().setSoldToData(partner);
		} else if (partnerFunction.equals(CRMConstants.ROLE_PAYER)) {
			// store soldTo handle
			baseR3Lrd.setPayerHandle(etPartner
					.getString(CRMConstants.REF_HANDLE));
		}

	}

	/**
	 * @param etPartner
	 * @param header
	 */
	private void fillBillToToAddress(final JCoTable etPartner,
			final Header header) {
		sapLogger.debug("header level billto found");
		final String partnerId = ConversionTools.addLeadingZerosToNumericID(
				etPartner.getString(CRMConstants.PARTNER_ID), 10);
		final BillTo billTo = header.getBillTo();
		billTo.setId(partnerId);
		billTo.setHandle(etPartner.getString(CRMConstants.REF_HANDLE));
		sapLogger.debug("fillBillToAdress start for: " + billTo.getTechKey());
		final Address address = readAddressFromCRM(etPartner, partnerId);
		billTo.setAddress(address);

	}

	/**
	 * @param etPartner
	 * @param shipTo
	 * @param header
	 */
	private void fillShipToAddress(final JCoTable etPartner,
			final ShipTo shipTo, final Header header) {
		sapLogger.debug("header level shipto found");
		final String partnerId = ConversionTools.addLeadingZerosToNumericID(
				etPartner.getString(CRMConstants.PARTNER_ID), 10);
		shipTo.setId(partnerId);
		shipTo.setHandle(etPartner.getString(CRMConstants.REF_HANDLE));
		shipTo.setTechKey(new TechKey(etPartner.getString("PARTNER_GUID")));
		sapLogger.debug("fillShipToAdress start for: " + shipTo.getTechKey());
		final Address address = readAddressFromCRM(etPartner, partnerId);
		shipTo.setAddress(address);

		if (!StringUtils.isEmpty(etPartner.getString(PARTNER_ADDRESS_NUMBER))) {
			final String addressGuid = getShppingAddressGuidWithGenil(
					etPartner.getString(PARTNER_ADDRESS_NUMBER), partnerId);
			if (StringUtils.isEmpty(addressGuid)) {

				shipTo.getAddress().setId(
						etPartner.getString(PARTNER_ADDRESS_NUMBER));
			} else {
				shipTo.getAddress().setId(addressGuid);
			}
		}
		// change ID to guid

		header.setShipTo(shipTo);
	}

	/**
	 * @param ttHeadPartyComV
	 *            - etPartner
	 * @param partnerId
	 * @return
	 */
	protected Address readAddressFromCRM(final JCoTable ttHeadPartyComV,
			final String partnerId) {

		final Address address = (Address) genericFactory
				.getBean(SapcommonbolConstants.ALIAS_BO_ADDRESS);
		address.setType(Address.TYPE_ORGANISATION);
		// address.setType(Address.TYPE_PERSON); // B2C

		address.setAddrguid(TechKey.generateKey().toString());
		final String name2 = ttHeadPartyComV.getString("NAME");
		final String name = ttHeadPartyComV.getString("NAME_2");
		address.setFirstName(name2);
		address.setLastName(name);
		address.setName1(name);
		address.setName2(name2);
		address.setCompanyName(name);
		address.setStreet(ttHeadPartyComV.getString("STREET"));
		String houseNumber = ttHeadPartyComV.getString("HOUSE_NO");
		if ("000000".equals(houseNumber)) {
			houseNumber = ("");
		}
		address.setHouseNo(houseNumber);
		address.setPostlCod1(ttHeadPartyComV.getString("POSTL_COD1"));
		address.setCity(ttHeadPartyComV.getString("CITY"));
		address.setDistrict(ttHeadPartyComV.getString("DISTRICT"));
		address.setCountry(ttHeadPartyComV.getString("COUNTRY").trim());
		address.setRegion(ttHeadPartyComV.getString("REGION").trim());
		address.setTaxJurCode(ttHeadPartyComV.getString("TAXJURCODE").trim());
		address.setEmail(ttHeadPartyComV.getString("E_MAIL"));
		address.setTel1Numbr(ttHeadPartyComV.getString("TEL1_NUMBR"));
		address.setTel1Ext(ttHeadPartyComV.getString("TEL1_EXT"));
		address.setFaxNumber(ttHeadPartyComV.getString("FAX_NUMBER"));
		address.setFaxExtens(ttHeadPartyComV.getString("FAX_EXTENS"));
		address.setTitleKey(ttHeadPartyComV.getString("TITLE"));
		address.setAddressPartner(partnerId);
		address.setAddrnum(partnerId); // As we do not have a
		// native address number
		// from LO-API

		address.setAddressStringC(ttHeadPartyComV.getString("ADDRESS_SHORT"));
		address.clearX();
		return address;
	}

	protected String mapPartnerFunctionToRole(final String partnerFunction) {
		switch (partnerFunction) {
		case CRMConstants.ROLE_SOLDTO:
			return PartnerFunctionData.SOLDTO;

		case CRMConstants.ROLE_SHIPTO:
			return PartnerFunctionData.SHIPTO;

		case CRMConstants.ROLE_CONTACT:
			return PartnerFunctionData.CONTACT;

		case CRMConstants.ROLE_PAYER:
			return PartnerFunctionData.PAYER;

		default:
			return null;
		}
	}

	/**
	 * Write JCO partner related tables before the LO-API update call
	 *
	 * @param salesDoc
	 *
	 * @param tc
	 *            Configuration settings
	 * @param paytypeCOD
	 *            indicates that selected paytype was COD
	 */
	public void populatePartnerImportParams(final SalesDocument salesDoc,
			final JCoFunction jcoFunction, final TransactionConfiguration tc,
			final boolean paytypeCOD) {

		final JCoTable importPartner = jcoFunction.getTableParameterList()
				.getTable("IT_PARTNER");
		final JCoTable importPartnetFields = jcoFunction
				.getTableParameterList().getTable("IT_FIELDS_PARTNER_SET");

		sapLogger.debug("fillPartner start");

		final Header header = salesDoc.getHeader();
		final BillTo billTo = header.getBillTo();
		// don't set address from billto in case it's a guest user scenario,
		// then the billto adress will be set directly from common address
		if (billTo != null
				&& (billTo.isIdX() || isAddressChanged(billTo.getAddress()))) {
			setPartnerRFCTables(salesDoc, importPartner, importPartnetFields,
					CRMConstants.ROLE_BILLPARTY_PFT);
		}

		// ShipTo on Header level
		populateShip2ImportParams(header, importPartner, importPartnetFields,
				salesDoc);
		// populating SOLDTO partner information
		populateSold2ImportParams(header, importPartner, importPartnetFields,
				salesDoc);
		// contact person addition
		populateContactImportParams(header, importPartner, importPartnetFields,
				salesDoc);
	}

	/**
	 * @param header
	 * @param importPartner
	 * @param importPartnetFields
	 * @param salesDoc
	 */
	private void populateContactImportParams(final Header header,
			final JCoTable importPartner, final JCoTable importPartnetFields,
			final SalesDocument salesDoc) {
		final String contactRole = mapPartnerFunctionToRole(CRMConstants.ROLE_CONTACT);
		final PartnerListEntry contactPerson = header.getPartnerList()
				.getPartnerData(contactRole);
		if ((contactPerson != null) && contactPerson.getHandle().isEmpty()) {
			importPartner.appendRow();
			setRequiredImportParams(importPartner, salesDoc);
			importPartner.setValue(CRMConstants.PARTNER_PFT,
					CRMConstants.ROLE_CONTACT_PFT);
			importPartner.setValue(CRMConstants.PARTNER_ID,
					contactPerson.getPartnerId());
			importPartner
					.setValue("REF_HANDLE", CRMConstants.FIRST_HEAD_HANDLE);

			final List<String> contactRoleParams = new ArrayList<String>();
			contactRoleParams.add("KIND_OF_ENTRY");
			contactRoleParams.add("PARTNER_NO");

			for (final String param : contactRoleParams) {
				importPartnetFields.appendRow();
				setRequiredPartnerFieldParams(importPartnetFields, salesDoc);
				importPartnetFields.setValue(CRMConstants.PARTNER_PFT,
						CRMConstants.ROLE_CONTACT_PFT);
				importPartnetFields.setValue("REF_HANDLE",
						CRMConstants.FIRST_HEAD_HANDLE);
				importPartnetFields.setValue("FIELDNAME", param);
			}
		}
	}

	/**
	 * @param header
	 * @param importPartner
	 * @param importPartnetFields
	 * @param salesDoc
	 */
	private void populateSold2ImportParams(final Header header,
			final JCoTable importPartner, final JCoTable importPartnetFields,
			final SalesDocument salesDoc) {
		final String role = mapPartnerFunctionToRole(CRMConstants.ROLE_SOLDTO);
		final PartnerListEntry contact = header.getPartnerList()
				.getPartnerData(role);
		if ((contact != null) && contact.getHandle().isEmpty()) {
			importPartner.appendRow();
			setRequiredImportParams(importPartner, salesDoc);
			importPartner.setValue(CRMConstants.PARTNER_PFT,
					CRMConstants.ROLE_SOLDTO_PFT);
			importPartner.setValue(CRMConstants.PARTNER_ID,
					contact.getPartnerId());
			importPartner
					.setValue("REF_HANDLE", CRMConstants.FIRST_HEAD_HANDLE);

			final List<String> fieldList = new ArrayList<String>();
			fieldList.add("KIND_OF_ENTRY");
			fieldList.add("PARTNER_NO");

			for (final String field : fieldList) {
				importPartnetFields.appendRow();
				setRequiredPartnerFieldParams(importPartnetFields, salesDoc);
				importPartnetFields.setValue(CRMConstants.PARTNER_PFT,
						CRMConstants.ROLE_SOLDTO_PFT);
				importPartnetFields.setValue("REF_HANDLE",
						CRMConstants.FIRST_HEAD_HANDLE);
				importPartnetFields.setValue("FIELDNAME", field);
			}
		}
	}

	/**
	 * @param header
	 * @param importPartner
	 * @param importPartnetFields
	 * @param salesDoc
	 */
	private void populateShip2ImportParams(final Header header,
			final JCoTable importPartner, final JCoTable importPartnetFields,
			final SalesDocument salesDoc) {
		final ShipTo shipTo = header.getShipTo();
		if (shipTo != null && shipTo.getAddress() != null
				&& !StringUtils.isEmpty(shipTo.getAddress().getId())) {
			String addressShipNumber = null;
			importPartner.appendRow();

			importPartner.setValue("REF_HANDLE", shipTo.getHandle());
			importPartner.setValue("PARTNER_GUID", shipTo.getTechKey()
					.getIdAsString());
			setRequiredImportParams(importPartner, salesDoc);
			importPartner.setValue(CRMConstants.PARTNER_PFT,
					CRMConstants.ROLE_SHIPTO_PFT);
			importPartner.setValue(CRMConstants.PARTNER_ID, shipTo.getId());
			if (shipTo.getAddress().getId() != null) {
				// Here Need to Pass the Jco Connection String.. so that we can
				// go further.. and do transformation....
				final String shippingAddressGuid = shipTo.getAddress().getId();
				addressShipNumber = getShppingAddressIdWithGenil(
						shippingAddressGuid, shipTo.getId());
				if (!StringUtils.isEmpty(addressShipNumber)) {
					importPartner.setValue(PARTNER_ADDRESS_NUMBER,
							addressShipNumber);
					sessionService.setAttribute(PARTNER_ADDRESS_NUMBER,
							addressShipNumber);
				}
				if (sessionService.getAttribute(PARTNER_ADDRESS_NUMBER) != null) {
					importPartner.setValue(PARTNER_ADDRESS_NUMBER, (sessionService.getAllAttributes().get(PARTNER_ADDRESS_NUMBER)));
				}
			}

			importPartner.setValue("ADDR_TYPE", "1");
			importPartner.setValue("ADDR_ORIGIN", "A");
			importPartner.setValue("MODE", "B");

			final List<String> fieldList = new ArrayList<String>();
			fieldList.add("KIND_OF_ENTRY");
			fieldList.add("PARTNER_NO");
			fieldList.add(PARTNER_ADDRESS_NUMBER);
			fieldList.add("ADDR_ORIGIN");
			fieldList.add("ADDR_TYPE");
			for (final String partnerField : fieldList) {
				importPartnetFields.appendRow();
				setRequiredPartnerFieldParams(importPartnetFields, salesDoc);
				importPartnetFields.setValue(CRMConstants.PARTNER_PFT,
						CRMConstants.ROLE_SHIPTO_PFT);
				importPartnetFields.setValue("REF_HANDLE",
						CRMConstants.FIRST_HEAD_HANDLE);
				importPartnetFields.setValue("FIELDNAME", partnerField);
			}
		}
	}

	/**
	 * @param importPartnetField
	 * @param salesDoc
	 */
	private void setRequiredPartnerFieldParams(
			final JCoTable importPartnetField, final SalesDocument salesDoc) {
		importPartnetField.setValue(CRMConstants.REF_GUID, salesDoc.getHeader()
				.getTechKey().getIdAsString());
		importPartnetField.setValue(CRMConstants.REF_KIND, "A");
	}

	/**
	 * @param importPartner
	 * @param salesDoc
	 */
	private void setRequiredImportParams(final JCoTable importPartner,
			final SalesDocument salesDoc) {
		importPartner.setValue(CRMConstants.REF_GUID, salesDoc.getHeader()
				.getTechKey().getIdAsString());
		importPartner.setValue(CRMConstants.REF_KIND, "A");
	}

	protected void setPartnerRFCTables(final SalesDocument salesDoc,
			final JCoTable PartnerComV, final JCoTable PartnerComX,
			final String partnerPFT) {
		final Header header = salesDoc.getHeader();
		final BillTo billTo = header.getBillTo();
		final String handle = billTo.getHandle();
		final String partnerNumber = billTo.getId();
		final PartnerBase partner = billTo;

		PartnerComV.appendRow();
		PartnerComV.setValue(CRMConstants.REF_HANDLE, handle);
		PartnerComX.appendRow();
		PartnerComX.setValue(CRMConstants.REF_HANDLE, handle);

		if (partner.isIdX()) {
			PartnerComV.setValue("REF_PARTNER_NO", partnerNumber);
		}

		PartnerComV.setValue(CRMConstants.PARTNER_PFT, partnerPFT);

		Address address = null;
		address = partner.getAddress();

		final List<String> str = new ArrayList<String>();
		str.add("KIND_OF_ENTRY");
		str.add("PARTNER_NO");

		if (address != null && isAddressChanged(address)) {
			PartnerComV.setValue("FIRSTNAME", address.getLastName());
			str.add("FIRSTNAME");
			PartnerComV.setValue("LASTNAME", address.getFirstName());
			str.add("LASTNAME");
			PartnerComV.setValue("CITY", address.getCity());
			str.add("CITY");
			PartnerComV.setValue("STREET", address.getStreet());
			str.add("STREET");
			PartnerComV.setValue("STREET_NO", address.getHouseNo());
			str.add("STREET_NO");
			PartnerComV.setValue("COUNTRY", address.getCountry());
			str.add("COUNTRY");
			PartnerComV.setValue("HOME_CITY", address.getDistrict());
			str.add("HOME_CITY");
			PartnerComV.setValue("CITY_NO", address.getPostlCod1());
			str.add("CITY_NO");
			PartnerComV.setValue("PBOXCIT_NO", address.getPostlCod2());
			str.add("PBOXCIT_NO");
			PartnerComV.setValue("REGION", address.getRegion());
			str.add("REGION");
			PartnerComV.setValue("TEL1_NUMBR", address.getTel1Numbr());
			str.add("TEL1_NUMBR");
			PartnerComV.setValue("TEL1_EXT", address.getTel1Ext());
			str.add("TEL1_EXT");
			PartnerComV.setValue("FAX_NUMBER", address.getFaxNumber());
			str.add("FAX_NUMBER");
			PartnerComV.setValue("FAX_EXTENS", address.getFaxExtens());
			str.add("FAX_EXTENS");
			PartnerComV.setValue("E_MAIL", address.getEmail());
			str.add("E_MAIL");
			PartnerComV.setValue("TAXJURCODE", address.getTaxJurCode());
			str.add("TAXJURCODE");
			PartnerComV.setValue("TITLE_P", address.getTitleKey());
			str.add("TITLE_P");
		}

		for (final String string : str) {
			PartnerComX.appendRow();
			PartnerComX.setValue(CRMConstants.REF_GUID, salesDoc.getHeader()
					.getTechKey().getIdAsString());
			PartnerComX.setValue(CRMConstants.REF_KIND, "A");
			PartnerComX.setValue(CRMConstants.PARTNER_PFT, partnerPFT);
			PartnerComX.setValue("FIELDNAME", string);
		}
	}

	protected boolean isAddressChanged(final Address address) {
		if (address == null) {
			return false;
		}

		// implemented like below just because to avoid Cyclo metric complexity
		boolean addressChanged = false;
		final boolean firstSet = isFirstSetAddressChanged(address);
		final boolean secondSet = isSecondSetAddressChanged(address);
		final boolean thirdSet = isThirdSetAddressChanged(address);
		final boolean forthSet = isFourthSetAddressChanged(address);
		final boolean fifthSet = isFifthSetAddressChanged(address);

		final boolean condition1 = firstSet || secondSet || thirdSet;

		addressChanged = condition1 || forthSet || fifthSet;

		return addressChanged;
	}

	private boolean isFirstSetAddressChanged(final Address address) {
		final boolean firstSet = address.getStreetX() || address.getCityX()
				|| address.getHouseNoX() || address.getCountryX();
		return firstSet;
	}

	private boolean isSecondSetAddressChanged(final Address address) {
		final boolean secondSet = address.getPostlCod1X()
				|| address.getPostlCod2X() || address.getRegionX()
				|| address.getFirstNameX();
		return secondSet;
	}

	private boolean isThirdSetAddressChanged(final Address address) {
		final boolean thirdSet = address.getLastNameX()
				|| address.getTel1NumbrX() || address.getTel1ExtX()
				|| address.getFaxNumberX();
		return thirdSet;
	}

	private boolean isFourthSetAddressChanged(final Address address) {
		final boolean fourthSet = address.getFaxExtensX()
				|| address.getEmailX() || address.getTaxJurCodeX()
				|| address.getTitleKeyX();
		return fourthSet;
	}

	private boolean isFifthSetAddressChanged(final Address address) {
		final boolean fifthSet = address.getCompanyNameX()
				|| address.getTelmob1X() || address.getDistrictX();
		return fifthSet;
	}

	public String getShppingAddressIdWithGenil(final String guid,
			final String partnerId) {
		String addressNumber = null;
		final Map<String, String> addressMap = crmAddressResolverUtil
				.getShippingAddressFromGenil(partnerId);
		addressNumber = addressMap.get(guid);
		return addressNumber;
	}

	public String getShppingAddressGuidWithGenil(final String addressNumber,
			final String partnerId) {
		String addressGuid = null;
		final Map<String, String> addressMap = crmAddressResolverUtil
				.getShippingAddressGuidMapFromGenil(partnerId);
		addressGuid = addressMap.get(addressNumber);
		return addressGuid;
	}

}
