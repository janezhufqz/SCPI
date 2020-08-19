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
package sap.sapcpicrmb2bticketsystem.service.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ticket.model.CsTicketModel;

import sap.sapcpicrmticketingsystem.model.SAPCpiOutboundServiceTicketModel;
import sap.sapcpicrmticketsystem.service.impl.SAPCpiOutboundServiceTicketConversionServiceImpl;


/**
 *
 */
public class SAPCpiB2BOutboundServiceTicketConversionServiceImpl extends SAPCpiOutboundServiceTicketConversionServiceImpl
{

	@Override
	public SAPCpiOutboundServiceTicketModel convertSrvTicketToSapCpiSrvTkt(final CsTicketModel pServiceTicket, final String notes)
	{

		final SAPCpiOutboundServiceTicketModel lServiceTicket = super.convertSrvTicketToSapCpiSrvTkt(pServiceTicket, notes);

		final UserModel userModel = pServiceTicket.getCustomer();

		if (userModel != null)
		{
			if (userModel instanceof B2BCustomerModel)
			{
				final B2BCustomerModel b2bcustomer = (B2BCustomerModel) userModel;
				lServiceTicket.setCustomer(b2bcustomer.getCustomerID());
				lServiceTicket.setB2bunit(b2bcustomer.getDefaultB2BUnit().getUid());
			}
		}

		return lServiceTicket;
	}

}
