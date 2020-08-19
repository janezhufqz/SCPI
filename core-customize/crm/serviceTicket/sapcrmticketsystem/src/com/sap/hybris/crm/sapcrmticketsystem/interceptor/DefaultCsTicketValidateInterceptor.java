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
package com.sap.hybris.crm.sapcrmticketsystem.interceptor;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.ticket.model.CsTicketModel;


/**
 * @author I340660
 *
 */
public class DefaultCsTicketValidateInterceptor implements ValidateInterceptor<CsTicketModel>
{

	private String isCustomerRequired;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.interceptor.ValidateInterceptor# onValidate(java.lang.Object,
	 * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
	 */
	@Override
	public void onValidate(final CsTicketModel ticket, final InterceptorContext ctx) throws InterceptorException
	{
		if (Boolean.parseBoolean(this.getIsCustomerRequired()) && ticket.getCustomer() == null)
		{
			throw new InterceptorException("Consumer does not  exist in hybris.");
		}

	}

	/**
	 * returns the flag for customer
	 *
	 * @return isCustomerRequired
	 */
	public String getIsCustomerRequired()
	{
		return isCustomerRequired;
	}

	/**
	 * set customer mandatory flag
	 *
	 * @param isCustomerRequired
	 */
	public void setIsCustomerRequired(final String isCustomerRequired)
	{
		this.isCustomerRequired = isCustomerRequired;
	}

}
