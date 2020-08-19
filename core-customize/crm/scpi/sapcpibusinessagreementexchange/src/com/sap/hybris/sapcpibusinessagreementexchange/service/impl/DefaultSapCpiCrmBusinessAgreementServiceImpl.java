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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.hybris.crm.sapcrmmodel.util.XSSFilterUtil;
import com.sap.hybris.sapbusinessagreement.constants.BusinessAgreemenPartnerRelationCsvColumns;
import com.sap.hybris.sapbusinessagreement.constants.BusinessAgreementCsvColumns;
import com.sap.hybris.sapbusinessagreement.constants.BusinessAgreementRuleHeaderCsvColumns;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementHeaderModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementPartnersModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementRuleHeaderModel;
import com.sap.hybris.sapcpibusinessagreementexchange.service.SapCpiCrmBusinessAgreementService;


/**
 *
 */
public class DefaultSapCpiCrmBusinessAgreementServiceImpl implements SapCpiCrmBusinessAgreementService
{


	@Override
	public void updateBusinessAgreement(final BusinessAgreementModel businessAgreementModel,
			final Map<String, Object> businessagreementData)
	{
		final BusinessAgreementHeaderModel headerModel = getBusinessAgreementHeaderModel(businessAgreementModel);
		final BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel = getBusinessAgreementRuleHeaderModel(headerModel);
		final BusinessAgreementPartnersModel businessAgreementPartnersModel = getBusinessAgreementPartnersModel(headerModel);
		if (headerModel != null && businessAgreementRuleHeaderModel != null)
		{
			if (businessAgreementPartnersModel != null)
			{
				createRawBusinessAgreementPartners(businessAgreementPartnersModel, businessagreementData);
			}
			createRawBusinessAgreementRules(businessAgreementRuleHeaderModel, businessagreementData);
			createRawBusinessAgreement(headerModel, businessAgreementModel, businessagreementData);
		}
	}


	@Override
	public void updateBusinessAgreementRuleHeader(final BusinessAgreementRuleHeaderModel businessAgreementModel,
			final Map<String, Object> businessagreementData)
	{
		final BusinessAgreementHeaderModel headerModel = businessAgreementModel.getBusinessagHeader();
		final BusinessAgreementPartnersModel businessAgreementPartnersModel = getBusinessAgreementPartnersModel(headerModel);
		if (headerModel != null && getBusinessAgreementRuleHeaderModel(headerModel) != null)
		{
			if (businessAgreementPartnersModel != null)
			{
				createRawBusinessAgreementPartners(businessAgreementPartnersModel, businessagreementData);
			}
			createRawBusinessAgreementRules(businessAgreementModel, businessagreementData);
			createRawBusinessAgreement(headerModel, headerModel.getBusinessagreement(), businessagreementData);
		}

	}

	@Override
	public void updateBusinessAgreementPartners(final BusinessAgreementPartnersModel businessAgreementPartnersModel,
			final Map<String, Object> businessagreementData)
	{
		final BusinessAgreementHeaderModel headerModel = businessAgreementPartnersModel.getBusinessagreementheader();
		final BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel = getBusinessAgreementRuleHeaderModel(headerModel);
		if (headerModel != null && businessAgreementRuleHeaderModel != null)
		{
			createRawBusinessAgreementPartners(businessAgreementPartnersModel, businessagreementData);
			createRawBusinessAgreementRules(businessAgreementRuleHeaderModel, businessagreementData);
			createRawBusinessAgreement(headerModel, headerModel.getBusinessagreement(), businessagreementData);
		}
	}

	@Override
	public void updateBusinessAgreementHeader(final BusinessAgreementHeaderModel businessAgreementHeaderModel,
			final Map<String, Object> businessagreementData)
	{
		final BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel = getBusinessAgreementRuleHeaderModel(businessAgreementHeaderModel);
		final BusinessAgreementPartnersModel businessAgreementPartnersModel = getBusinessAgreementPartnersModel(businessAgreementHeaderModel);
		if (businessAgreementHeaderModel != null && businessAgreementRuleHeaderModel != null)
		{
			if (businessAgreementPartnersModel != null)
			{
				createRawBusinessAgreementPartners(businessAgreementPartnersModel, businessagreementData);
			}
			createRawBusinessAgreementRules(businessAgreementRuleHeaderModel, businessagreementData);
			createRawBusinessAgreement(businessAgreementHeaderModel, businessAgreementHeaderModel.getBusinessagreement(),
					businessagreementData);
		}
	}


	/**
	 *
	 */
	public void createRawBusinessAgreementPartners(final BusinessAgreementPartnersModel businessAgreementPartnersModel,
			final Map<String, Object> businessagreementData)
	{
		final Set<BusinessAgreementPartnersModel> partners = businessAgreementPartnersModel.getBusinessagreementheader()
				.getBusinessagreementpartners();


		for (final Iterator iterator = partners.iterator(); iterator.hasNext();)
		{
			final BusinessAgreementPartnersModel partnermodel = (BusinessAgreementPartnersModel) iterator.next();

			if (partnermodel.getPk().equals(businessAgreementPartnersModel.getPk()))
			{
				createPartnersRelation(businessagreementData, businessAgreementPartnersModel);
			}
			else
			{
				createPartnersRelation(businessagreementData, partnermodel);
			}
		}

	}

	public void createPartnersRelation(final Map<String, Object> rawHybrisBusinessAgreementpertner,
			final BusinessAgreementPartnersModel partnermodel)
	{
		if (partnermodel.getBusinessagreementheader().getBusinessagreement() != null)
		{
			rawHybrisBusinessAgreementpertner.put(BusinessAgreemenPartnerRelationCsvColumns.BUAGID, partnermodel
					.getBusinessagreementheader().getBusinessagreement().getId());
		}
		else
		{
			rawHybrisBusinessAgreementpertner.put(BusinessAgreemenPartnerRelationCsvColumns.BUAGID, "");
		}
		if (partnermodel.getBusinessagreementpartnercat() != null)
		{
			rawHybrisBusinessAgreementpertner.put(BusinessAgreemenPartnerRelationCsvColumns.PARTNERFUNCTIONCATEGORY,
					partnermodel.getBusinessagreementpartnercat());
		}
		else
		{
			rawHybrisBusinessAgreementpertner.put(BusinessAgreemenPartnerRelationCsvColumns.PARTNERFUNCTIONCATEGORY, "");
		}
		if (partnermodel.getPartnerguid1() != null)
		{
			rawHybrisBusinessAgreementpertner.put(BusinessAgreemenPartnerRelationCsvColumns.PRIMARYPARTNERGUID,
					partnermodel.getPartnerguid1());
		}
		else
		{
			rawHybrisBusinessAgreementpertner.put(BusinessAgreemenPartnerRelationCsvColumns.PRIMARYPARTNERGUID, "");
		}
		if (partnermodel.getCustomerguid() != null)
		{
			rawHybrisBusinessAgreementpertner.put(BusinessAgreemenPartnerRelationCsvColumns.SECONDARYPARTNERGUID, partnermodel
					.getCustomerguid().getCrmGuid());
		}

		rawHybrisBusinessAgreementpertner.put(BusinessAgreemenPartnerRelationCsvColumns.BPRELATIONSHIPNUMBER, "");
		if (partnermodel.getPartnerfunction() != null)
		{
			rawHybrisBusinessAgreementpertner.put(BusinessAgreemenPartnerRelationCsvColumns.PARTNERFUNCTION, partnermodel
					.getPartnerfunction().getCode());
		}
		else
		{
			rawHybrisBusinessAgreementpertner.put(BusinessAgreemenPartnerRelationCsvColumns.PARTNERFUNCTION, "");
		}
	}


	public void createRawBusinessAgreement(final BusinessAgreementHeaderModel headermodel, final BusinessAgreementModel model,
			final Map<String, Object> businessagreementData)
	{

		if (model.getId() != null)
		{
			businessagreementData.put(BusinessAgreementCsvColumns.BUAGID, model.getId());
		}
		else
		{
			businessagreementData.put(BusinessAgreementCsvColumns.BUAGID, "");
		}
		if (model.getGuid() != null)
		{
			businessagreementData.put(BusinessAgreementCsvColumns.BUAGUUID, model.getGuid());
		}
		else
		{
			businessagreementData.put(BusinessAgreementCsvColumns.BUAGUUID, "");
		}
		if (model.getCustomer() != null)
		{
			businessagreementData.put(BusinessAgreementCsvColumns.PARENTUNIT, model.getCustomer().getCustomerID());
			businessagreementData.put(BusinessAgreementCsvColumns.PARENTUNITGUID, model.getCustomer().getCrmGuid());
		}
		if (model.getText() != null || ("").equals(model.getText()))
		{
			businessagreementData.put(BusinessAgreementCsvColumns.TEXT, XSSFilterUtil.filter(model.getText()));
		}
		else
		{
			businessagreementData.put(BusinessAgreementCsvColumns.TEXT, "");
		}

		createBusinessAgreementHeader(businessagreementData, headermodel);

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

	/**
	 *
	 */
	public void createRawBusinessAgreementRules(final BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel,
			final Map<String, Object> businessagreementData)
	{

		if (businessAgreementRuleHeaderModel != null)
		{
			if (businessAgreementRuleHeaderModel.getBusinessagHeader().getBusinessagreement().getId() != null)
			{
				businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.BUAGID, businessAgreementRuleHeaderModel
						.getBusinessagHeader().getBusinessagreement().getId());
			}
			else
			{
				businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.BUAGID, "");
			}

			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.VALIDTO, businessAgreementRuleHeaderModel.getValidto());

			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.VALIDFROM,
					businessAgreementRuleHeaderModel.getValidfrom());

			populateRawHybrisBusinessAgreementRule(businessagreementData, businessAgreementRuleHeaderModel);

			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.TEXT,
					XSSFilterUtil.filter(businessAgreementRuleHeaderModel.getText()));
		}
	}


	public void populateRawHybrisBusinessAgreementRule(final Map<String, Object> businessagreementData,
			final BusinessAgreementRuleHeaderModel model)
	{
		if (model.getPaymentmethodinc() != null)
		{
			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.PAYMENTMETHODINCOMING, model.getPaymentmethodinc()
					.getCode());
		}
		else
		{
			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.PAYMENTMETHODINCOMING, "");
		}
		if (model.getPaymentmethodout() != null)
		{
			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.PAYMENTMETHODOUTCOMING, model.getPaymentmethodout()
					.getCode());
		}
		else
		{
			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.PAYMENTMETHODOUTCOMING, "");
		}
		if (model.getShippingcontroldr() != null)
		{
			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.SHIPPINGCONTROLDR, model.getShippingcontroldr()
					.getCode());
		}
		else
		{
			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.SHIPPINGCONTROLDR, "");
		}
		if (model.getTermofpayment() != null)
		{
			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.TERMOFPAYMENT, model.getTermofpayment().getCode());
		}
		else
		{
			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.TERMOFPAYMENT, "");
		}
		if (model.getShippingcontrolbr() != null)
		{
			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.SHIPPINGCONTROLBR, model.getShippingcontrolbr()
					.getCode());
		}
		else
		{
			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.SHIPPINGCONTROLBR, "");
		}
		if (model.getCorrepondencevariant() != null)
		{
			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.CORRESPONDENTVARIANT, model.getCorrepondencevariant()
					.getCode());
		}
		else
		{
			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.CORRESPONDENTVARIANT, "");
		}
		if (model.getShippingcontrolbp() != null)
		{
			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.SHIPPINGCONTROLBP, model.getShippingcontrolbp()
					.getCode());
		}
		else
		{
			businessagreementData.put(BusinessAgreementRuleHeaderCsvColumns.SHIPPINGCONTROLBP, "");
		}
	}

}
