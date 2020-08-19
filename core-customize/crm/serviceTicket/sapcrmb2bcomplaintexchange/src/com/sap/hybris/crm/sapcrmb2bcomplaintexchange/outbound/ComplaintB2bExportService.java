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
package com.sap.hybris.crm.sapcrmb2bcomplaintexchange.outbound;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.crm.sapcrmb2bcomplaintexchange.constants.Sapcrmb2bcomplaintexchangeConstants;
import com.sap.hybris.crm.sapcrmcomplaintexchange.constants.SapcrmcomplaintexchangeConstants;
import com.sap.hybris.crm.sapcrmcomplaintexchange.outbound.ComplaintExportService;


/**
 * This class exports the CsTicket created to DataHub for Hybris - CRM integration.The export is done in csv
 * format.Hybris ticket is mapped to RawHybrisServiceTicket in datahub.The target datahub is configured in
 * local.properties file.
 *
 * @author C5229484
 *
 */
public class ComplaintB2bExportService extends ComplaintExportService
{

	/**
	 * Logger for class
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ComplaintB2bExportService.class);

	/**
	 * @param userModel
	 * @param target
	 */
	@Override
	protected void setCustomerBasedOnB2BorB2C(final UserModel userModel, final Map<String, Object> target)
	{
		if (userModel != null)
		{
			if (userModel instanceof B2BCustomerModel)
			{
				final B2BCustomerModel b2bcustomer = (B2BCustomerModel) userModel;
				LOG.info("user is a b2b customer ");
				target.put(SapcrmcomplaintexchangeConstants.CUSTOMER_ID, b2bcustomer.getCustomerID());
				target.put(Sapcrmb2bcomplaintexchangeConstants.UNIT, b2bcustomer.getDefaultB2BUnit().getUid());
			}
			else
			{
				super.setCustomerBasedOnB2BorB2C(userModel, target);
			}

		}

	}

}
