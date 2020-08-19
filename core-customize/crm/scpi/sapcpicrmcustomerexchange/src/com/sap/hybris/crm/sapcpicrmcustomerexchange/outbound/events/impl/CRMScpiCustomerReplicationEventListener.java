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
package com.sap.hybris.crm.sapcpicrmcustomerexchange.outbound.events.impl;

import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import java.util.List;
import java.util.Map;

import com.sap.hybris.crm.sapcpicrmcustomerexchange.service.SapCrmCustomerOutboundService;
import com.sap.hybris.sapcrmcustomerb2c.constants.Sapcrmcustomerb2cConstants;
import com.sap.hybris.sapcrmcustomerb2c.outbound.CRMCustomerExportService;
import com.sap.hybris.sapcrmcustomerb2c.outbound.events.CRMCustomerReplicationEvent;


/**
 *
 */
public class CRMScpiCustomerReplicationEventListener extends AbstractEventListener<CRMCustomerReplicationEvent>
{

	private CRMCustomerExportService customerExportService;
	private SapCrmCustomerOutboundService sapCrmCustomerOutboundService;
	private SAPGlobalConfigurationService sapGlobalConfigurationService;

	@Override
	protected void onEvent(final CRMCustomerReplicationEvent customerReplicationEvent)
	{
		final List<Map<String, Object>> customerDetailsAndAddressDataList = customerReplicationEvent
				.getCustomerDetailsAndAddressDataList();

		final List<Map<String, Object>> customerCreditCardDetailsDataList = customerReplicationEvent
				.getCustomerCreditCardDetailsDataList();

		final Boolean replicateregistereduser = getSapGlobalConfigurationService().getProperty(
				Sapcrmcustomerb2cConstants.REPLICATE_REGISTERED_USER);
		if ((customerDetailsAndAddressDataList != null && !customerDetailsAndAddressDataList.isEmpty())
				|| (replicateregistereduser.booleanValue() && !customerCreditCardDetailsDataList.isEmpty()))
		{

			getSapCrmCustomerOutboundService().sendB2CCustomerToScpi(customerDetailsAndAddressDataList,
					customerCreditCardDetailsDataList);
		}

	}

	/**
	 * @return the customerExportService
	 */
	public CRMCustomerExportService getCustomerExportService()
	{
		return customerExportService;
	}

	/**
	 * @param customerExportService
	 *           the customerExportService to set
	 */
	public void setCustomerExportService(CRMCustomerExportService customerExportService)
	{
		this.customerExportService = customerExportService;
	}

	/**
	 * @return the sapGlobalConfigurationService
	 */
	public SAPGlobalConfigurationService getSapGlobalConfigurationService()
	{
		return sapGlobalConfigurationService;
	}

	/**
	 * @param sapGlobalConfigurationService
	 *           the sapGlobalConfigurationService to set
	 */
	public void setSapGlobalConfigurationService(SAPGlobalConfigurationService sapGlobalConfigurationService)
	{
		this.sapGlobalConfigurationService = sapGlobalConfigurationService;
	}

	/**
	 * @return the sapCrmCustomerOutboundService
	 */
	public SapCrmCustomerOutboundService getSapCrmCustomerOutboundService()
	{
		return sapCrmCustomerOutboundService;
	}

	/**
	 * @param sapCrmCustomerOutboundService
	 *           the sapCrmCustomerOutboundService to set
	 */
	public void setSapCrmCustomerOutboundService(SapCrmCustomerOutboundService sapCrmCustomerOutboundService)
	{
		this.sapCrmCustomerOutboundService = sapCrmCustomerOutboundService;
	}



}
