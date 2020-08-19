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

/**
 *
 */
package com.sap.hybris.crm.sapcrmpricing.services.impl;

import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sappricing.services.SapPricingBolFactory;
import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricing;

import org.springframework.beans.factory.annotation.Autowired;

import com.sap.hybris.crm.sapcrmpricing.constants.SapcrmpricingConstants;


/**
 * @author C5229090
 *
 */
public class DefaultCRMPricingBolFactory implements SapPricingBolFactory
{

	@Autowired
	private GenericFactory genericFactory;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.sap.sappricing.services.SapPricingBolFactory#getSapPricing()
	 */
	@Override
	public SapPricing getSapPricing()
	{
		return (SapPricing) genericFactory.getBean(SapcrmpricingConstants.CRM_PRICING_BO); //crmPricingBo
	}

	/**
	 * @return the genericFactory
	 */
	public GenericFactory getGenericFactory()
	{
		return genericFactory;
	}

	/**
	 * @param genericFactory
	 *           the genericFactory to set
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

}
