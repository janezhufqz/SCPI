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
package sap.sapcpicrmcustomerexchangeb2b.service.impl;


import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.RESPONSE_MESSAGE;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.getPropertyValue;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.isSentSuccessfully;

import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BCustomerModel;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sap.sapcpicrmcustomerexchangeb2b.service.SapCpiB2BContactConversionService;

import com.sap.hybris.sapcrmcustomerb2b.outbound.B2BContactExporterDataHub;


/**
 *
 */
public class SapCpiCrmB2BContactOutboundService extends B2BContactExporterDataHub
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiCrmB2BContactOutboundService.class);
	private SapCpiB2BContactConversionService sapCpiB2BContactConversionService;

	private SapCpiOutboundService sapCpiOutboundService;

	private String relationshipRawType;
	private String b2bContactRawType;


	@Override
	public void exportContact(final List<Map<String, Object>> rawData)
	{
		sendToDatahub(rawData, getB2bContactRawType());
	}

	@Override
	public void exportRelations(final List<Map<String, Object>> rawData)
	{
		sendToDatahub(rawData, getRelationshipRawType());
	}

	private void sendToDatahub(final List<Map<String, Object>> rawData, final String rawType)
	{
		if (rawData != null && !rawData.isEmpty())
		{

			sendB2BCustomerToSCPI(getSapCpiB2BContactConversionService().convertB2BCustomerToSapCpiBb2BCustomer(rawData, rawType));

		}
		else
		{
			LOG.debug("Data not sent to SCPI because target map is empty");
		}
	}

	protected void sendB2BCustomerToSCPI(final SAPCpiOutboundB2BCustomerModel sapCpiOutboundB2BCustomer)
	{

		getSapCpiOutboundService().sendB2BCustomer(sapCpiOutboundB2BCustomer).subscribe(

				// onNext
				responseEntityMap -> {

					if (isSentSuccessfully(responseEntityMap))
					{

						LOG.info(String.format("B2B customer [%s] has been sent to the SAP backend through SCPI! %n%s",
								sapCpiOutboundB2BCustomer.getUid(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));

					}
					else
					{

						LOG.error(String.format("B2B customer [%s] has not been sent to the SAP backend! %n%s",
								sapCpiOutboundB2BCustomer.getUid(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));

					}

				}

				// onError
				,
				error -> LOG.error(String.format("B2B customer [%s] has not been sent to the SAP backend through SCPI! %n%s",
						sapCpiOutboundB2BCustomer.getUid(), error.getMessage()))

		);

	}


	/**
	 * @return the sapCpiB2BContactConversionService
	 */
	public SapCpiB2BContactConversionService getSapCpiB2BContactConversionService()
	{
		return sapCpiB2BContactConversionService;
	}


	/**
	 * @param sapCpiB2BContactConversionService
	 *           the sapCpiB2BContactConversionService to set
	 */
	public void setSapCpiB2BContactConversionService(final SapCpiB2BContactConversionService sapCpiB2BContactConversionService)
	{
		this.sapCpiB2BContactConversionService = sapCpiB2BContactConversionService;
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
	 * @return the relationshipRawType
	 */
	@Override
	public String getRelationshipRawType()
	{
		return relationshipRawType;
	}

	/**
	 * @param relationshipRawType
	 *           the relationshipRawType to set
	 */
	@Override
	public void setRelationshipRawType(final String relationshipRawType)
	{
		this.relationshipRawType = relationshipRawType;
	}

	/**
	 * @return the b2bContactRawType
	 */
	@Override
	public String getB2bContactRawType()
	{
		return b2bContactRawType;
	}

	/**
	 * @param b2bContactRawType
	 *           the b2bContactRawType to set
	 */
	@Override
	public void setB2bContactRawType(final String b2bContactRawType)
	{
		this.b2bContactRawType = b2bContactRawType;
	}


}
