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
package sap.sapcpicrmb2bcomplaint.service.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ticket.model.CsTicketModel;

import sap.sapcpicrmcomplaint.model.SAPCpiOutboundComplaintModel;
import sap.sapcpicrmcomplaint.service.impl.SapCpiComplaintConversionServiceImpl;


/**
 *
 */
public class SapCpiB2BComplaintConversionServiceImpl extends SapCpiComplaintConversionServiceImpl
{

	@Override
	public SAPCpiOutboundComplaintModel convertComplaintToOutboundComplaint(final CsTicketModel pComplaint, final String pNotes)
	{
		final SAPCpiOutboundComplaintModel lOutboundComplaint = super.convertComplaintToOutboundComplaint(pComplaint, pNotes);



		final UserModel userModel = pComplaint.getCustomer();

		if (userModel != null)
		{
			if (userModel instanceof B2BCustomerModel)
			{
				final B2BCustomerModel b2bcustomer = (B2BCustomerModel) userModel;
				lOutboundComplaint.setCustomer(b2bcustomer.getCustomerID());
				lOutboundComplaint.setB2bunit(b2bcustomer.getDefaultB2BUnit().getUid());
			}
		}

		return lOutboundComplaint;
	}
}
