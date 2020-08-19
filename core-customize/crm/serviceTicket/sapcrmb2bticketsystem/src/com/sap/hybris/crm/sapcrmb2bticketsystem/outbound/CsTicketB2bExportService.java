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
package com.sap.hybris.crm.sapcrmb2bticketsystem.outbound;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Map;

import com.sap.hybris.crm.sapcrmb2bticketsystem.constants.Sapcrmb2bticketsystemConstants;
import com.sap.hybris.crm.sapcrmticketsystem.constants.SapcrmticketsystemConstants;
import com.sap.hybris.crm.sapcrmticketsystem.outbound.CsTicketExportService;


/**
 * This class exports the CsTicket created to DataHub for Hybris - CRM integration.The export is done in csv
 * format.Hybris ticket is mapped to RawHybrisServiceTicket in datahub.The target datahub is configured in
 * local.properties file.
 *
 * @author C5229484
 *
 */
public class CsTicketB2bExportService extends CsTicketExportService
{

	/**
	 * Logger for class
	 */


	@Override
	protected void setCustomerBasedOnB2BorB2C(final UserModel userModel, final Map<String, Object> target)
	{
		if (userModel != null)
		{
			if (userModel instanceof B2BCustomerModel)
			{
				final B2BCustomerModel b2bcustomer = (B2BCustomerModel) userModel;
				target.put(SapcrmticketsystemConstants.CUSTOMER_ID, b2bcustomer.getCustomerID());
				target.put(Sapcrmb2bticketsystemConstants.UNIT, b2bcustomer.getDefaultB2BUnit().getUid());
			}
			else
			{
				super.setCustomerBasedOnB2BorB2C(userModel, target);
			}
		}
	}

}
