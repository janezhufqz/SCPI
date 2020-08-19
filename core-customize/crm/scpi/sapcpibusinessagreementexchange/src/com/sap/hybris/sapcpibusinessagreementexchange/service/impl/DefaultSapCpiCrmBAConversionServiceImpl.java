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
package com.sap.hybris.sapcpibusinessagreementexchange.service.impl;

import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapbusinessagreement.constants.BusinessAgreemenPartnerRelationCsvColumns;
import com.sap.hybris.sapbusinessagreement.constants.BusinessAgreementCsvColumns;
import com.sap.hybris.sapbusinessagreement.constants.BusinessAgreementRuleHeaderCsvColumns;
import com.sap.hybris.sapcpibusinessagreementexchange.model.SAPCpiBusinessAgreementOutboundModel;
import com.sap.hybris.sapcpibusinessagreementexchange.service.SapCpiCrmBAConversionService;


/**
 *
 */
public class DefaultSapCpiCrmBAConversionServiceImpl implements SapCpiCrmBAConversionService
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapCpiCrmBAConversionServiceImpl.class);

	private ModelService modelService;
	private SAPGlobalConfigurationDAO globalConfigurationDAO;


	@Override
	public SAPCpiBusinessAgreementOutboundModel convertBAToSapCpiOutboundBA(final String businessagreementType,
			final List<Map<String, Object>> businessagreementData)
	{
		final SAPCpiBusinessAgreementOutboundModel sapCrmCpiBAModel = getModelService().create(
				SAPCpiBusinessAgreementOutboundModel.class);


		businessagreementData.stream().findFirst().ifPresent(agreementData -> {
								sapCrmCpiBAModel.setId(mapAttribute(BusinessAgreementCsvColumns.BUAGID, agreementData));
								sapCrmCpiBAModel.setGuid(mapAttribute(BusinessAgreementCsvColumns.BUAGUUID, agreementData));
								sapCrmCpiBAModel.setPartnerid(mapAttribute(BusinessAgreementCsvColumns.PARENTUNIT, agreementData));
								sapCrmCpiBAModel.setPartnerguid(mapAttribute(BusinessAgreementCsvColumns.PARENTUNITGUID, agreementData));
								sapCrmCpiBAModel.setAddresscorrespondenceguid(mapAttribute(BusinessAgreementCsvColumns.ADDRCR,
										agreementData));
								sapCrmCpiBAModel.setAddressdunningguid(mapAttribute(BusinessAgreementCsvColumns.ADDRDR, agreementData));
								sapCrmCpiBAModel.setAddressirguid(mapAttribute(BusinessAgreementCsvColumns.ADDRIR, agreementData));
								sapCrmCpiBAModel.setAddressrecipientguid(mapAttribute(BusinessAgreementCsvColumns.ADDRPR, agreementData));
								sapCrmCpiBAModel.setAddresspayerguid(mapAttribute(BusinessAgreementCsvColumns.ADDRPY, agreementData));
								sapCrmCpiBAModel.setAddresstaxguid(mapAttribute(BusinessAgreementCsvColumns.ADDRTAXGUID, agreementData));
								sapCrmCpiBAModel.setBankidinc(mapAttribute(BusinessAgreementCsvColumns.BANKINCOMING, agreementData));
								sapCrmCpiBAModel.setBankidout(mapAttribute(BusinessAgreementCsvColumns.BANKOUTCOMING, agreementData));
								sapCrmCpiBAModel.setBusinessagreementclass(mapAttribute(
										BusinessAgreementCsvColumns.BUSINESSAGREEMENTCLASS, agreementData));
								sapCrmCpiBAModel.setBusinessagreementpayer(mapAttribute(
										BusinessAgreementCsvColumns.BUSINESSAGREEMENTPAYER, agreementData));
								sapCrmCpiBAModel.setCreditcardinc(mapAttribute(BusinessAgreementCsvColumns.CARDINCOMING, agreementData));
								sapCrmCpiBAModel.setCreditcardout(mapAttribute(BusinessAgreementCsvColumns.CARDOUTCOMING, agreementData));
								sapCrmCpiBAModel.setBusinessagreementdefault(mapAttribute(
										BusinessAgreementCsvColumns.DEFAULTBUSINESSAGREEMENT, agreementData));
								sapCrmCpiBAModel.setRefnumber(mapAttribute(BusinessAgreementCsvColumns.REFNUMBER, agreementData));
								sapCrmCpiBAModel.setTaxcategory(mapAttribute(BusinessAgreementCsvColumns.TAXCATEGORY, agreementData));
								sapCrmCpiBAModel.setTaxcode(mapAttribute(BusinessAgreementCsvColumns.TAXCODE, agreementData));
								sapCrmCpiBAModel.setValidfrom(mapAttribute(BusinessAgreementCsvColumns.VALIDFROM, agreementData));
								sapCrmCpiBAModel.setValidto(mapAttribute(BusinessAgreementCsvColumns.VALIDTO, agreementData));
								sapCrmCpiBAModel.setText(mapAttribute(BusinessAgreementCsvColumns.TEXT, agreementData));
								
								sapCrmCpiBAModel.setId(mapAttribute(BusinessAgreementRuleHeaderCsvColumns.BUAGID, agreementData));
								sapCrmCpiBAModel.setValidto1(mapAttribute(BusinessAgreementRuleHeaderCsvColumns.VALIDTO, agreementData));
								sapCrmCpiBAModel
										.setValidfrom1(mapAttribute(BusinessAgreementRuleHeaderCsvColumns.VALIDFROM, agreementData));
								sapCrmCpiBAModel.setPaymentmethodinc(mapAttribute(
										BusinessAgreementRuleHeaderCsvColumns.PAYMENTMETHODINCOMING, agreementData));
								sapCrmCpiBAModel.setPaymentmethodout(mapAttribute(
										BusinessAgreementRuleHeaderCsvColumns.PAYMENTMETHODOUTCOMING, agreementData));
								sapCrmCpiBAModel.setShippingcontroldr(mapAttribute(
										BusinessAgreementRuleHeaderCsvColumns.SHIPPINGCONTROLDR, agreementData));
								sapCrmCpiBAModel.setTermofpayment(mapAttribute(BusinessAgreementRuleHeaderCsvColumns.TERMOFPAYMENT,
										agreementData));
								sapCrmCpiBAModel.setShippingcontrolbr(mapAttribute(
										BusinessAgreementRuleHeaderCsvColumns.SHIPPINGCONTROLBR, agreementData));
								sapCrmCpiBAModel.setCorrepondencevariant(mapAttribute(
										BusinessAgreementRuleHeaderCsvColumns.CORRESPONDENTVARIANT, agreementData));
								sapCrmCpiBAModel.setShippingcontrolbp(mapAttribute(
										BusinessAgreementRuleHeaderCsvColumns.SHIPPINGCONTROLBP, agreementData));
								sapCrmCpiBAModel.setText1(mapAttribute(BusinessAgreementRuleHeaderCsvColumns.TEXT, agreementData));
								
								sapCrmCpiBAModel.setId(mapAttribute(BusinessAgreemenPartnerRelationCsvColumns.BUAGID, agreementData));

								sapCrmCpiBAModel.setBusinessagreementpartnercat(mapAttribute(
										BusinessAgreemenPartnerRelationCsvColumns.PARTNERFUNCTIONCATEGORY, agreementData));
								sapCrmCpiBAModel.setPartnerguid1(mapAttribute(
										BusinessAgreemenPartnerRelationCsvColumns.PRIMARYPARTNERGUID, agreementData));
								sapCrmCpiBAModel.setPartnerguid2(mapAttribute(
										BusinessAgreemenPartnerRelationCsvColumns.SECONDARYPARTNERGUID, agreementData));
								sapCrmCpiBAModel.setBprelationshipnum(mapAttribute(
										BusinessAgreemenPartnerRelationCsvColumns.BPRELATIONSHIPNUMBER, agreementData));
								sapCrmCpiBAModel.setPartnerfunction(mapAttribute(
										BusinessAgreemenPartnerRelationCsvColumns.PARTNERFUNCTION, agreementData));

								sapCrmCpiBAModel.setHybrisBAType("BusinessAgreement");
							});
			mapOutboundDestination(sapCrmCpiBAModel);

		return sapCrmCpiBAModel;
	}


	protected String mapAttribute(final String attribute, final Map<String, Object> row)
	{
		return row.get(attribute) != null ? row.get(attribute).toString() : null;
	}


	protected void mapOutboundDestination(final SAPCpiBusinessAgreementOutboundModel sapCpiBaOutbound)
	{

		final SAPCpiOutboundConfigModel sapCpiOutboundConfig = getModelService().create(SAPCpiOutboundConfigModel.class);
		final SAPLogicalSystemModel logicalSystem = readLogicalSystem();

		sapCpiOutboundConfig.setSenderName(logicalSystem.getSenderName());
		sapCpiOutboundConfig.setSenderPort(logicalSystem.getSenderPort());
		sapCpiOutboundConfig.setReceiverName(logicalSystem.getSapLogicalSystemName());
		sapCpiOutboundConfig.setReceiverPort(logicalSystem.getSapLogicalSystemName());
		sapCpiOutboundConfig.setUrl(logicalSystem.getSapHTTPDestination().getTargetURL());
		sapCpiOutboundConfig.setUsername(logicalSystem.getSapHTTPDestination().getUserid());
		sapCpiOutboundConfig
				.setClient(logicalSystem.getSapHTTPDestination().getTargetURL().split("sap-client=")[1].substring(0, 3));

		sapCpiBaOutbound.setSapCpiConfig(sapCpiOutboundConfig);

	}

	protected SAPLogicalSystemModel readLogicalSystem()
	{

		final Set<SAPLogicalSystemModel> logicalSystems = getGlobalConfigurationDAO().getSAPGlobalConfiguration()
				.getSapLogicalSystemGlobalConfig();
		Objects.requireNonNull(logicalSystems,
				"The BusinessAgreement cannot be sent to SCPI. There is no SAP logical system maintained in the back office!");

		return logicalSystems
				.stream()
				.filter(SAPLogicalSystemModel::isDefaultLogicalSystem)
				.findFirst()
				.orElseThrow(
						() -> new IllegalArgumentException(
								"The BusinessAgreement cannot be sent to SCPI. There is no default SAP logical system maintained in the back office!"));

	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}


	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
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
