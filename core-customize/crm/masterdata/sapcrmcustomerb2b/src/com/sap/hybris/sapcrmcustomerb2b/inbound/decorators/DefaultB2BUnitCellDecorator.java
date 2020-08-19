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

package com.sap.hybris.sapcrmcustomerb2b.inbound.decorators;

import de.hybris.platform.core.Registry;
import de.hybris.platform.util.CSVCellDecorator;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapcrmcustomerb2b.inbound.InboundB2BCustomerHelper;
/**
 * Decorator class to Set the Default B2BUnit of the B2BCustomer
 */
public class DefaultB2BUnitCellDecorator implements CSVCellDecorator {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultB2BUnitCellDecorator.class);

	private InboundB2BCustomerHelper inboundB2BCustomerHelper = (InboundB2BCustomerHelper) Registry.getApplicationContext()
			.getBean("inboundB2BCustomerHelper");

	@Override
	public String decorate(int position, Map<Integer, String> impexLine) {
		LOG.debug("Decorating the default B2BUnit field");
		String b2bUnitId = null;
		final String b2bCustomerId = impexLine.get(Integer.valueOf(position));
		if (StringUtils.isNotEmpty(b2bCustomerId)) {
			b2bUnitId = getInboundB2BCustomerHelper().getB2BUnitForCustomer(b2bCustomerId);
		}
		return b2bUnitId;
	}

	public InboundB2BCustomerHelper getInboundB2BCustomerHelper() {
		return inboundB2BCustomerHelper;
	}

	public void setInboundB2BCustomerHelper(InboundB2BCustomerHelper inboundB2BCustomerHelper) {
		this.inboundB2BCustomerHelper = inboundB2BCustomerHelper;
	}
}
