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
package com.sap.hybris.crm.sapcrmpricingbolb2b.backend.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction;

import java.util.Optional;
import java.util.Set;

import com.sap.conn.jco.JCoTable;
import com.sap.hybris.crm.sapcrmpricingbol.backend.impl.SapCrmPricingHeaderMapper;


/**
 * sets B2B specific header import parameters to JCo Structures
 */
public class SapCrmPricingB2bHeaderMapper extends SapCrmPricingHeaderMapper
{

	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

	@Override
	public void fillIncotermsParameters(final UserModel currentCustomer, final JCoTable attributes,
			final SapPricingPartnerFunction partnerFunction)
	{
		if (!(currentCustomer instanceof B2BCustomerModel))
		{
			super.fillIncotermsParameters(currentCustomer, attributes, partnerFunction);
		}
		else
		{
			//Current user is final a B2B Customer
			final Optional<B2BUnitModel> b2bModel = Optional.ofNullable(getB2BUnitModel(partnerFunction.getSoldTo()));

			attributes.appendRow();
			attributes.setValue(FIELDNAME, SOLD_TO_PARTY);
			final JCoTable soldToParty = attributes.getTable(VALUES);
			soldToParty.appendRow();
			soldToParty.setValue(0, b2bModel.isPresent() ? b2bModel.get().getB2bunitguid() : "");

			attributes.appendRow();
			attributes.setValue(FIELDNAME, INCOTERMS1);
			final JCoTable incoTerms1 = attributes.getTable(VALUES);
			incoTerms1.appendRow();
			incoTerms1.setValue(0, b2bModel.isPresent() ? b2bModel.get().getIncoterms1() : "");

			attributes.appendRow();
			attributes.setValue(FIELDNAME, INCOTERMS2);
			final JCoTable incoTerms2 = attributes.getTable(VALUES);
			incoTerms2.appendRow();
			incoTerms2.setValue(0, b2bModel.isPresent() ? b2bModel.get().getIncoterms2() : "");

		}
	}

	@Override
	public void fillTaxIncotermsParameters(final UserModel currentCustomer, final JCoTable attributes,
			final SapPricingPartnerFunction partnerFunction)
	{

		if (!(currentCustomer instanceof B2BCustomerModel))
		{
			super.fillTaxIncotermsParameters(currentCustomer, attributes, partnerFunction);
		}
		else
		{
			//Current user is a B2B Customer
			final Optional<B2BUnitModel> b2bModel = Optional.ofNullable(getB2BUnitModel(partnerFunction.getSoldTo()));
			attributes.appendRow();
			attributes.setValue(FIELDNAME, SOLD_TO_PARTY);
			final JCoTable soldToParty = attributes.getTable(VALUES);
			soldToParty.appendRow();
			soldToParty.setValue(0, b2bModel.isPresent() ? b2bModel.get().getB2bunitguid() : "");

			attributes.appendRow();
			attributes.setValue(FIELDNAME, INCOTERMS1);
			final JCoTable incoTerms1 = attributes.getTable(VALUES);
			incoTerms1.appendRow();
			incoTerms1.setValue(0, b2bModel.isPresent() ? b2bModel.get().getIncoterms1() : "");

			attributes.appendRow();
			attributes.setValue(FIELDNAME, INCOTERMS2);
			final JCoTable incoTerms2 = attributes.getTable(VALUES);
			incoTerms2.appendRow();
			incoTerms2.setValue(0, b2bModel.isPresent() ? b2bModel.get().getIncoterms2() : "");
		}

	}

	private B2BUnitModel getB2BUnitModel(final String soldTo)
	{
		final Set<PrincipalModel> salesAreaB2bUnit = getB2BUnitService().getUnitForUid(soldTo).getMembers();
		for (final PrincipalModel salesAreaUnit : salesAreaB2bUnit)
		{
			if (salesAreaUnit instanceof B2BUnitModel)
			{
				final B2BUnitModel currentUnit = (B2BUnitModel) salesAreaUnit;
				final String[] b2bUnitUid = currentUnit.getUid().split("_");
				if (b2bUnitUid.length >= 3
						&& (b2bUnitUid[b2bUnitUid.length - 2].equals(getProperty("sapcommon_salesOrganization")) && b2bUnitUid[b2bUnitUid.length - 1]
								.equals(getProperty("sapcommon_distributionChannel"))))
				{
					return currentUnit;
				}
			}
		}
		/**
		 * In case there is no matching Sales area specific unit available, the parent Unit will be returned.
		 */
		return getB2BUnitService().getUnitForUid(soldTo);

	}

	/**
	 * @return the b2BUnitService
	 */
	public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService()
	{
		return b2BUnitService;
	}


	/**
	 * @param b2bUnitService
	 *           the b2BUnitService to set
	 */
	public void setB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		b2BUnitService = b2bUnitService;
	}
}
