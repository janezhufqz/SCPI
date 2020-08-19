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
package com.sap.hybris.sapcpibusinessagreementexchangeb2b.service.impl;

import java.util.Map;

import com.sap.hybris.crm.sapcrmmodel.util.XSSFilterUtil;
import com.sap.hybris.sapbusinessagreement.constants.BusinessAgreemenPartnerRelationCsvColumns;
import com.sap.hybris.sapbusinessagreement.constants.BusinessAgreementCsvColumns;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementHeaderModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementPartnersModel;
import com.sap.hybris.sapcpibusinessagreementexchange.service.impl.DefaultSapCpiCrmBusinessAgreementServiceImpl;


/**
 *
 */
public class DefaultSapCpiCrmB2BBusinessAgreementServiceImpl extends DefaultSapCpiCrmBusinessAgreementServiceImpl
{

	@Override
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

		if (model.getPartner() != null)
		{
			businessagreementData.put(BusinessAgreementCsvColumns.PARENTUNIT, model.getPartner().getUid());
			businessagreementData.put(BusinessAgreementCsvColumns.PARENTUNITGUID, model.getPartner().getB2bunitguid());
		}
		else
		{
			if (model.getCustomer() != null)
			{
				businessagreementData.put(BusinessAgreementCsvColumns.PARENTUNIT, model.getCustomer().getCustomerID());
				businessagreementData.put(BusinessAgreementCsvColumns.PARENTUNITGUID, model.getCustomer().getCrmGuid());
			}
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


	@Override
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
		if (partnermodel.getPartnerguid2() != null)
		{
			rawHybrisBusinessAgreementpertner.put(BusinessAgreemenPartnerRelationCsvColumns.SECONDARYPARTNERGUID, partnermodel
					.getPartnerguid2().getB2bunitguid());
		}
		else
		{
			if (partnermodel.getCustomerguid() != null)
			{
				rawHybrisBusinessAgreementpertner.put(BusinessAgreemenPartnerRelationCsvColumns.SECONDARYPARTNERGUID, partnermodel
						.getCustomerguid().getCrmGuid());
			}
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



}
