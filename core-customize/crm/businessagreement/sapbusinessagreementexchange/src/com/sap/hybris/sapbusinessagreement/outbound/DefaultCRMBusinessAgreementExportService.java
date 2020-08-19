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
package com.sap.hybris.sapbusinessagreement.outbound;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import com.sap.hybris.crm.sapcrmmodel.util.XSSFilterUtil;
import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.hybris.datahub.core.rest.DataHubOutboundException;
import com.hybris.datahub.core.services.DataHubOutboundService;
import com.sap.hybris.sapbusinessagreement.constants.BusinessAgreemenPartnerRelationCsvColumns;
import com.sap.hybris.sapbusinessagreement.constants.BusinessAgreementCsvColumns;
import com.sap.hybris.sapbusinessagreement.constants.BusinessAgreementRuleHeaderCsvColumns;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementHeaderModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementPartnersModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementRuleHeaderModel;


/**
 *
 */
public class DefaultCRMBusinessAgreementExportService
{
	private static final Logger LOGGER = Logger
			.getLogger(com.sap.hybris.sapbusinessagreement.outbound.DefaultCRMBusinessAgreementExportService.class.getName());
	private DataHubOutboundService dataHubOutboundService;
	private String rawItemType;
	public static final String SAP_CRM_BUSINESS_AGREEMENT_OUTBOUND_FEED = "SAP_CRM_BUSINESS_AGREEMENT_OUTBOUND_FEED";
	public static final String RAW_HYBRIS_BUSINESS_AGREEMENT = "RawBusinessAgreement";
	public static final String RAW_HYBRIS_BUSINESS_AGREEMENT_RULE = "RawBusinessAgreementRuleHeader";
	public static final String RAW_HYBRIS_BUSINESS_AGREEMENT_PARTNERS = "RawBusinessAgreementPartnerRelation";

	public void updateBusinessAgreement(final BusinessAgreementModel businessagreementmodel)
	{

		final BusinessAgreementHeaderModel headerModel = getBusinessAgreementHeaderModel(businessagreementmodel);
		final BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel = getBusinessAgreementRuleHeaderModel(headerModel);
		final BusinessAgreementPartnersModel businessAgreementPartnersModel = getBusinessAgreementPartnersModel(headerModel);
		if (headerModel != null && businessAgreementRuleHeaderModel != null)
		{
			if (businessAgreementPartnersModel != null)
			{
				createRawBusinessAgreementPartners(businessAgreementPartnersModel);
			}
			createRawBusinessAgreementRules(businessAgreementRuleHeaderModel);
			createRawBusinessAgreement(headerModel, businessagreementmodel);
		}

	}

	public void updateBusinessAgreementRuleHeader(final BusinessAgreementRuleHeaderModel rulemodel)
	{

		final BusinessAgreementHeaderModel headerModel = rulemodel.getBusinessagHeader();
		final BusinessAgreementPartnersModel businessAgreementPartnersModel = getBusinessAgreementPartnersModel(headerModel);
		if (headerModel != null && getBusinessAgreementRuleHeaderModel(headerModel) != null)
		{
			if (businessAgreementPartnersModel != null)
			{
				createRawBusinessAgreementPartners(businessAgreementPartnersModel);
			}
			createRawBusinessAgreementRules(rulemodel);
			createRawBusinessAgreement(headerModel, headerModel.getBusinessagreement());
		}
	}

	public void updateBusinessAgreementPartners(final BusinessAgreementPartnersModel partnersmodel)
	{

		final BusinessAgreementHeaderModel headerModel = partnersmodel.getBusinessagreementheader();
		final BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel = getBusinessAgreementRuleHeaderModel(headerModel);
		if (headerModel != null && businessAgreementRuleHeaderModel != null)
		{
			createRawBusinessAgreementPartners(partnersmodel);
			createRawBusinessAgreementRules(businessAgreementRuleHeaderModel);
			createRawBusinessAgreement(headerModel, headerModel.getBusinessagreement());
		}
	}

	public void updateBusinessAgreementHeader(final BusinessAgreementHeaderModel headerModel)
	{
		final BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel = getBusinessAgreementRuleHeaderModel(headerModel);
		final BusinessAgreementPartnersModel businessAgreementPartnersModel = getBusinessAgreementPartnersModel(headerModel);
		if (headerModel != null && businessAgreementRuleHeaderModel != null)
		{
			if (businessAgreementPartnersModel != null)
			{
				createRawBusinessAgreementPartners(businessAgreementPartnersModel);
			}
			createRawBusinessAgreementRules(businessAgreementRuleHeaderModel);
			createRawBusinessAgreement(headerModel, headerModel.getBusinessagreement());
		}
	}

	public void createRawBusinessAgreement(final BusinessAgreementHeaderModel headermodel, final BusinessAgreementModel model)
	{

		final Map<String, Object> rawHybrisBusinessAgreement = new HashMap<>();
		if (model.getId() != null)
		{
			rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.BUAGID, model.getId());
		}
		else
		{
			rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.BUAGID, "");
		}
		if (model.getGuid() != null)
		{
			rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.BUAGUUID, model.getGuid());
		}
		else
		{
			rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.BUAGUUID, "");
		}
		if (model.getCustomer() != null)
		{
			rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.PARENTUNIT, model.getCustomer().getCustomerID());
			rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.PARENTUNITGUID, model.getCustomer().getCrmGuid());
		}
		if (model.getText() != null || ("").equals(model.getText()))
		{
			rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.TEXT, XSSFilterUtil.filter(model.getText()));
		}
		else
		{
			rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.TEXT, "");
		}

		createBusinessAgreementHeader(rawHybrisBusinessAgreement, headermodel);

		sendRawBusinessItemsToDataHub(RAW_HYBRIS_BUSINESS_AGREEMENT, Collections.singletonList(rawHybrisBusinessAgreement));
	}

	public void createBusinessAgreementHeader(final Map<String, Object> rawHybrisBusinessAgreement,
			final BusinessAgreementHeaderModel headermodel)
	{

		rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.ADDRCR, headermodel.getAddresscorrespondenceguid());

		rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.ADDRDR, headermodel.getAddressdunningguid());

		rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.ADDRIR, headermodel.getAddressirguid());

		rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.ADDRPR, headermodel.getAddressrecipientguid());

		rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.ADDRPY, headermodel.getAddresspayerguid());

		rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.ADDRTAXGUID, headermodel.getAddresstaxguid());

		rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.BANKINCOMING, headermodel.getBankidinc());

		rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.BANKOUTCOMING, headermodel.getBankidout());

		if (headermodel.getBusinessagreementclass() != null)
		{
			rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.BUSINESSAGREEMENTCLASS, headermodel
					.getBusinessagreementclass().getCode());
		}
		else
		{
			rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.BUSINESSAGREEMENTCLASS, "");
		}

		rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.BUSINESSAGREEMENTPAYER, headermodel.getBusinessagreementpayer());

		rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.CARDINCOMING, headermodel.getCreditcardinc());

		rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.CARDOUTCOMING, headermodel.getCreditcardout());

		if (headermodel.getBusinessagreementdefault() != null)
		{
			if (headermodel.getBusinessagreementdefault().booleanValue())
			{
				rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.DEFAULTBUSINESSAGREEMENT, "X");
			}
			else
			{
				rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.DEFAULTBUSINESSAGREEMENT, "");
			}
		}
		else
		{
			rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.DEFAULTBUSINESSAGREEMENT, "");
		}

		rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.REFNUMBER, XSSFilterUtil.filter(headermodel.getRefnumber()));

		if (headermodel.getTaxcategory() != null)
		{
			rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.TAXCATEGORY, headermodel.getTaxcategory().getCode());
		}
		else
		{
			rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.TAXCATEGORY, "");
		}
		if (headermodel.getTaxcode() != null)
		{
			rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.TAXCODE, headermodel.getTaxcode().getCode());
		}
		else
		{
			rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.TAXCODE, "");
		}

		rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.VALIDFROM, headermodel.getValidfrom());

		rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.VALIDTO, headermodel.getValidto());
	}

	public void createRawBusinessAgreementRules(final BusinessAgreementRuleHeaderModel model)
	{
		final Map<String, Object> rawHybrisBusinessAgreementrule = new HashMap<>();

		if (model != null)
		{
			if (model.getBusinessagHeader().getBusinessagreement().getId() != null)
			{
				rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.BUAGID, model.getBusinessagHeader()
						.getBusinessagreement().getId());
			}
			else
			{
				rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.BUAGID, "");
			}

			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.VALIDTO, model.getValidto());

			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.VALIDFROM, model.getValidfrom());

			populateRawHybrisBusinessAgreementRule(rawHybrisBusinessAgreementrule, model);

			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.TEXT, XSSFilterUtil.filter(model.getText()));

			sendRawBusinessItemsToDataHub(RAW_HYBRIS_BUSINESS_AGREEMENT_RULE,
					Collections.singletonList(rawHybrisBusinessAgreementrule));
		}
	}

	/**
	 * @param rawHybrisBusinessAgreementrule
	 */
	public void populateRawHybrisBusinessAgreementRule(final Map<String, Object> rawHybrisBusinessAgreementrule,
			final BusinessAgreementRuleHeaderModel model)
	{
		if (model.getPaymentmethodinc() != null)
		{
			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.PAYMENTMETHODINCOMING, model
					.getPaymentmethodinc().getCode());
		}
		else
		{
			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.PAYMENTMETHODINCOMING, "");
		}
		if (model.getPaymentmethodout() != null)
		{
			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.PAYMENTMETHODOUTCOMING, model
					.getPaymentmethodout().getCode());
		}
		else
		{
			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.PAYMENTMETHODOUTCOMING, "");
		}
		if (model.getShippingcontroldr() != null)
		{
			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.SHIPPINGCONTROLDR, model.getShippingcontroldr()
					.getCode());
		}
		else
		{
			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.SHIPPINGCONTROLDR, "");
		}
		if (model.getTermofpayment() != null)
		{
			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.TERMOFPAYMENT, model.getTermofpayment()
					.getCode());
		}
		else
		{
			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.TERMOFPAYMENT, "");
		}
		if (model.getShippingcontrolbr() != null)
		{
			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.SHIPPINGCONTROLBR, model.getShippingcontrolbr()
					.getCode());
		}
		else
		{
			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.SHIPPINGCONTROLBR, "");
		}
		if (model.getCorrepondencevariant() != null)
		{
			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.CORRESPONDENTVARIANT, model
					.getCorrepondencevariant().getCode());
		}
		else
		{
			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.CORRESPONDENTVARIANT, "");
		}
		if (model.getShippingcontrolbp() != null)
		{
			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.SHIPPINGCONTROLBP, model.getShippingcontrolbp()
					.getCode());
		}
		else
		{
			rawHybrisBusinessAgreementrule.put(BusinessAgreementRuleHeaderCsvColumns.SHIPPINGCONTROLBP, "");
		}
	}

	public void createRawBusinessAgreementPartners(final BusinessAgreementPartnersModel businessAgreementPartnerModel)
	{

		final Set<BusinessAgreementPartnersModel> partners = businessAgreementPartnerModel.getBusinessagreementheader()
				.getBusinessagreementpartners();
		Map<String, Object> rawHybrisBusinessAgreementpertners = null;

		final List<Map<String, Object>> rawHybrisBusinessAgreementpertnerslist = new ArrayList<Map<String, Object>>();

		for (final Iterator iterator = partners.iterator(); iterator.hasNext();)
		{
			rawHybrisBusinessAgreementpertners = new HashMap<>();
			final BusinessAgreementPartnersModel partnermodel = (BusinessAgreementPartnersModel) iterator.next();

			if (partnermodel.getPk().equals(businessAgreementPartnerModel.getPk()))
			{
				createPartnersRelation(rawHybrisBusinessAgreementpertners, rawHybrisBusinessAgreementpertnerslist,
						businessAgreementPartnerModel);
			}
			else
			{
				createPartnersRelation(rawHybrisBusinessAgreementpertners, rawHybrisBusinessAgreementpertnerslist, partnermodel);
			}
		}

		sendRawBusinessItemsToDataHub(RAW_HYBRIS_BUSINESS_AGREEMENT_PARTNERS, rawHybrisBusinessAgreementpertnerslist);
	}

	/**
     *
     */
	public void createPartnersRelation(final Map<String, Object> rawHybrisBusinessAgreementpertners,
			final List<Map<String, Object>> rawHybrisBusinessAgreementpertnerslist, final BusinessAgreementPartnersModel partnermodel)
	{
		if (partnermodel.getBusinessagreementheader().getBusinessagreement() != null)
		{
			rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.BUAGID, partnermodel
					.getBusinessagreementheader().getBusinessagreement().getId());
		}
		else
		{
			rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.BUAGID, "");
		}
		if (partnermodel.getBusinessagreementpartnercat() != null)
		{
			rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.PARTNERFUNCTIONCATEGORY,
					partnermodel.getBusinessagreementpartnercat());
		}
		else
		{
			rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.PARTNERFUNCTIONCATEGORY, "");
		}
		if (partnermodel.getPartnerguid1() != null)
		{
			rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.PRIMARYPARTNERGUID,
					partnermodel.getPartnerguid1());
		}
		else
		{
			rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.PRIMARYPARTNERGUID, "");
		}
		if (partnermodel.getCustomerguid() != null)
		{
			rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.SECONDARYPARTNERGUID, partnermodel
					.getCustomerguid().getCrmGuid());
		}

		rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.BPRELATIONSHIPNUMBER, "");
		if (partnermodel.getPartnerfunction() != null)
		{
			rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.PARTNERFUNCTION, partnermodel
					.getPartnerfunction().getCode());
		}
		else
		{
			rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.PARTNERFUNCTION, "");
		}
		rawHybrisBusinessAgreementpertnerslist.add(rawHybrisBusinessAgreementpertners);
	}

	public BusinessAgreementPartnersModel getBusinessAgreementPartnersModel(final BusinessAgreementHeaderModel source)
	{

		BusinessAgreementPartnersModel businessAgreementPartnersModel = null;
		if (source != null)
		{

			final Set<BusinessAgreementPartnersModel> businessAgreementPartnersSet = source.getBusinessagreementpartners();
			if (!businessAgreementPartnersSet.isEmpty())
			{
				businessAgreementPartnersModel = businessAgreementPartnersSet.iterator().next();
			}

		}

		return businessAgreementPartnersModel;
	}

	public BusinessAgreementRuleHeaderModel getBusinessAgreementRuleHeaderModel(final BusinessAgreementHeaderModel source)
	{
		BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel = null;
		if (source != null)
		{

			final Set<BusinessAgreementRuleHeaderModel> businessAgreementRuleHeaderSet = source.getBusinessruleHeader();
			if (!businessAgreementRuleHeaderSet.isEmpty())
			{
				businessAgreementRuleHeaderModel = businessAgreementRuleHeaderSet.iterator().next();
			}

		}
		return businessAgreementRuleHeaderModel;
	}

	public BusinessAgreementHeaderModel getBusinessAgreementHeaderModel(final BusinessAgreementModel source)
	{
		BusinessAgreementHeaderModel businessAgreementHeaderModel = null;
		if (source != null)
		{

			final Set<BusinessAgreementHeaderModel> businessAgreementHeaderSet = source.getBusinessagreementheader();

			if (!businessAgreementHeaderSet.isEmpty())
			{
				businessAgreementHeaderModel = businessAgreementHeaderSet.iterator().next();
			}
		}

		return businessAgreementHeaderModel;
	}

	public void sendRawBusinessItemsToDataHub(final String rawItemType, final List<Map<String, Object>> rawData)
	{
		if (rawData != null && !rawData.isEmpty())
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("The following values were sent to Data Hub " + rawData + " (to the feed "
						+ SAP_CRM_BUSINESS_AGREEMENT_OUTBOUND_FEED + " into raw item type " + rawItemType + ")");
			}
			try
			{
				if (rawItemType.equals(RAW_HYBRIS_BUSINESS_AGREEMENT_PARTNERS))
				{
					dataHubOutboundService.sendToDataHub(SAP_CRM_BUSINESS_AGREEMENT_OUTBOUND_FEED,
							RAW_HYBRIS_BUSINESS_AGREEMENT_PARTNERS, rawData);
				}
				if (rawItemType.equals(RAW_HYBRIS_BUSINESS_AGREEMENT_RULE))
				{
					dataHubOutboundService.sendToDataHub(SAP_CRM_BUSINESS_AGREEMENT_OUTBOUND_FEED, RAW_HYBRIS_BUSINESS_AGREEMENT_RULE,
							rawData);
				}
				if (rawItemType.equals(RAW_HYBRIS_BUSINESS_AGREEMENT))
				{
					dataHubOutboundService.sendToDataHub(SAP_CRM_BUSINESS_AGREEMENT_OUTBOUND_FEED, RAW_HYBRIS_BUSINESS_AGREEMENT,
							rawData);
				}

			}
			catch (final DataHubOutboundException e)
			{
				LOGGER.warn("Error processing sending data to Data Hub. DataHubOutboundException: " + e);
			}

		}
		else
		{
			LOGGER.debug("No send to datahub occured because target is empty");
		}
	}

	/**
	 * @return the dataHubOutboundService
	 */
	public DataHubOutboundService getDataHubOutboundService()
	{
		return dataHubOutboundService;
	}

	/**
	 * @param dataHubOutboundService
	 *           the dataHubOutboundService to set
	 */
	public void setDataHubOutboundService(final DataHubOutboundService dataHubOutboundService)
	{
		this.dataHubOutboundService = dataHubOutboundService;
	}

	/**
	 * @return the rawItemType
	 */
	public String getRawItemType()
	{
		return rawItemType;
	}

	/**
	 * @param rawItemType
	 *           the rawItemType to set
	 */
	public void setRawItemType(final String rawItemType)
	{
		this.rawItemType = rawItemType;
	}

}
