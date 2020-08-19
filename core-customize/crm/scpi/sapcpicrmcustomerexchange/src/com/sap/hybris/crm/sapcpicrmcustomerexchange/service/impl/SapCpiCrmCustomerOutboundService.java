/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.crm.sapcpicrmcustomerexchange.service.impl;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.RESPONSE_MESSAGE;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.getPropertyValue;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.isSentSuccessfully;

import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapcpicustomerexchange.service.SapCpiCustomerConversionService;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.crm.sapcpicrmcustomerexchange.service.SapCpiCrmCustomerConversionService;
import com.sap.hybris.crm.sapcpicrmcustomerexchange.service.SapCrmCustomerOutboundService;
import com.sap.hybris.sapcrmcustomerb2c.outbound.CRMCustomerExportService;


/**
 *
 */
public class SapCpiCrmCustomerOutboundService extends CRMCustomerExportService implements SapCrmCustomerOutboundService
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiCrmCustomerOutboundService.class);
	private SapCpiCustomerConversionService sapCpiCustomerConversionService;
	private SapCpiOutboundService sapCpiOutboundService;

	private SapCpiCrmCustomerConversionService sapCpiCrmCustomerConversionService;


	/**
	 *
	 */
	private void sendCrmCustomerToScpi(final SAPCpiOutboundCustomerModel sapCpiOutboundCustomer)
	{

		getSapCpiOutboundService().sendCustomer(sapCpiOutboundCustomer).subscribe(responseEntityMap -> {

			if (isSentSuccessfully(responseEntityMap))
			{

				LOG.info(String.format("The customer [%s] has been sent to the SAP backend through SCPI! %n%s",
						sapCpiOutboundCustomer.getCustomerId(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));

			}
			else
			{

				LOG.error(String.format("The customer [%s] has not been sent to the SAP backend! %n%s",
						sapCpiOutboundCustomer.getCustomerId(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));

			}

		}

		// onError
				, error -> LOG.error(String.format("The customer [%s] has not been sent to the SAP backend through SCPI! %n%s",
						sapCpiOutboundCustomer.getCustomerId(), error.getMessage()))

		);
	}

	/**
	 * @return the sapCpiCustomerConversionService
	 */
	public SapCpiCustomerConversionService getSapCpiCustomerConversionService()
	{
		return sapCpiCustomerConversionService;
	}


	/**
	 * @param sapCpiCustomerConversionService
	 *           the sapCpiCustomerConversionService to set
	 */
	public void setSapCpiCustomerConversionService(final SapCpiCustomerConversionService sapCpiCustomerConversionService)
	{
		this.sapCpiCustomerConversionService = sapCpiCustomerConversionService;
	}


	/**
	 * @return the sapCpiOutboundService
	 */
	public SapCpiOutboundService getSapCpiOutboundService()
	{
		return sapCpiOutboundService;
	}


	/**
	 * @param sapCpiOutboundService
	 *           the sapCpiOutboundService to set
	 */
	public void setSapCpiOutboundService(final SapCpiOutboundService sapCpiOutboundService)
	{
		this.sapCpiOutboundService = sapCpiOutboundService;
	}




	/**
	 * @return the sapCpiCrmCustomerConversionService
	 */
	public SapCpiCrmCustomerConversionService getSapCpiCrmCustomerConversionService()
	{
		return sapCpiCrmCustomerConversionService;
	}




	/**
	 * @param sapCpiCrmCustomerConversionService
	 *           the sapCpiCrmCustomerConversionService to set
	 */
	public void setSapCpiCrmCustomerConversionService(SapCpiCrmCustomerConversionService sapCpiCrmCustomerConversionService)
	{
		this.sapCpiCrmCustomerConversionService = sapCpiCrmCustomerConversionService;
	}

	@Override
	public void sendB2CCustomerToScpi(List<Map<String, Object>> b2cCustomerAddressData,
			List<Map<String, Object>> b2cCustomerCreditCardData)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("The following values was send to SCPi" + b2cCustomerAddressData);

		}

		final SAPCpiOutboundCustomerModel sapCpiOutboundCustomer = getSapCpiCrmCustomerConversionService()
				.convertB2CCustomerToSapCpiCustomer(b2cCustomerAddressData, b2cCustomerCreditCardData);

		sendCrmCustomerToScpi(sapCpiOutboundCustomer);

	}







}