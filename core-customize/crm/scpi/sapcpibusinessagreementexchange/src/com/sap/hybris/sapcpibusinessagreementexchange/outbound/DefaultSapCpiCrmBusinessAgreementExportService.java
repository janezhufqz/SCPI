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
package com.sap.hybris.sapcpibusinessagreementexchange.outbound;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.RESPONSE_MESSAGE;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.getPropertyValue;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.isSentSuccessfully;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementHeaderModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementPartnersModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementRuleHeaderModel;
import com.sap.hybris.sapbusinessagreement.outbound.DefaultCRMBusinessAgreementExportService;
import com.sap.hybris.sapcpibusinessagreementexchange.model.SAPCpiBusinessAgreementOutboundModel;
import com.sap.hybris.sapcpibusinessagreementexchange.service.SapCpiCrmBAConversionService;
import com.sap.hybris.sapcpibusinessagreementexchange.service.SapCpiCrmBAOutboundService;
import com.sap.hybris.sapcpibusinessagreementexchange.service.SapCpiCrmBusinessAgreementService;


/**
 *
 */
public class DefaultSapCpiCrmBusinessAgreementExportService extends DefaultCRMBusinessAgreementExportService
{


	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapCpiCrmBusinessAgreementExportService.class);

	private SapCpiCrmBAConversionService sapCpiCrmBAConversionService;
	private SapCpiCrmBAOutboundService sapCpiCrmBAOutboundService;
	private SapCpiCrmBusinessAgreementService sapCpiCrmBusinessAgreementService;

	@Override
	public void updateBusinessAgreement(final BusinessAgreementModel businessagreementmodel)
	{

		final Map<String, Object> hyybrisBusinessAgreementData = new HashMap<>();
		getSapCpiCrmBusinessAgreementService().updateBusinessAgreement(businessagreementmodel, hyybrisBusinessAgreementData);
		LOG.info("BusinessAgreement data is Updated..... ");
		sendRawBusinessItemsToSCPI(RAW_HYBRIS_BUSINESS_AGREEMENT, Collections.singletonList(hyybrisBusinessAgreementData));
	}


	@Override
	public void updateBusinessAgreementRuleHeader(final BusinessAgreementRuleHeaderModel rulemodel)
	{

		final Map<String, Object> hyybrisBusinessAgreementData = new HashMap<>();
		getSapCpiCrmBusinessAgreementService().updateBusinessAgreementRuleHeader(rulemodel, hyybrisBusinessAgreementData);
		LOG.info("BusinessAgreementRuleHeader data is Updated..... ");
		sendRawBusinessItemsToSCPI(RAW_HYBRIS_BUSINESS_AGREEMENT_RULE, Collections.singletonList(hyybrisBusinessAgreementData));
	}

	@Override
	public void updateBusinessAgreementPartners(final BusinessAgreementPartnersModel partnersmodel)
	{

		final Map<String, Object> hyybrisBusinessAgreementData = new HashMap<>();
		getSapCpiCrmBusinessAgreementService().updateBusinessAgreementPartners(partnersmodel, hyybrisBusinessAgreementData);
		LOG.info("BusinessAgreementPartners data is Updated..... ");
		sendRawBusinessItemsToSCPI(RAW_HYBRIS_BUSINESS_AGREEMENT_PARTNERS, Collections.singletonList(hyybrisBusinessAgreementData));
	}

	@Override
	public void updateBusinessAgreementHeader(final BusinessAgreementHeaderModel businessAgreementHeaderModel)
	{
		final Map<String, Object> hyybrisBusinessAgreementData = new HashMap<>();
		getSapCpiCrmBusinessAgreementService().updateBusinessAgreementHeader(businessAgreementHeaderModel,
				hyybrisBusinessAgreementData);
		LOG.info("BusinessAgreementHeader data is Updated..... ");
		sendRawBusinessItemsToSCPI(RAW_HYBRIS_BUSINESS_AGREEMENT, Collections.singletonList(hyybrisBusinessAgreementData));
	}


	public void sendRawBusinessItemsToSCPI(final String rawItemType, final List<Map<String, Object>> rawData)
	{
		if (rawData != null && !rawData.isEmpty())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("The following values were sent to SCPI " + rawData + " (to the SCPI feed "
						+ SAP_CRM_BUSINESS_AGREEMENT_OUTBOUND_FEED + " into raw item type " + rawItemType + ")");
			}
			LOG.info("Business agreement data sent to SCPI with raw item type: {}", rawItemType);

			if (rawItemType.equals(RAW_HYBRIS_BUSINESS_AGREEMENT_PARTNERS))
			{
				final SAPCpiBusinessAgreementOutboundModel sapCpiBAOutboundModel = getSapCpiCrmBAConversionService()
						.convertBAToSapCpiOutboundBA(rawItemType, rawData);

				sendOutboundBAToScpi(sapCpiBAOutboundModel);

			}
			if (rawItemType.equals(RAW_HYBRIS_BUSINESS_AGREEMENT_RULE))
			{
				final SAPCpiBusinessAgreementOutboundModel sapCpiBAOutboundModel = getSapCpiCrmBAConversionService()
						.convertBAToSapCpiOutboundBA(rawItemType, rawData);
				sendOutboundBAToScpi(sapCpiBAOutboundModel);
			}
			if (rawItemType.equals(RAW_HYBRIS_BUSINESS_AGREEMENT))
			{
				final SAPCpiBusinessAgreementOutboundModel sapCpiBAOutboundModel = getSapCpiCrmBAConversionService()
						.convertBAToSapCpiOutboundBA(rawItemType, rawData);
				sendOutboundBAToScpi(sapCpiBAOutboundModel);
			}

		}
		else
		{
			LOG.debug("No business agreement data has been sent to SCPI, because the target is empty");
		}
	}




	/**
	 *
	 */
	protected void sendOutboundBAToScpi(final SAPCpiBusinessAgreementOutboundModel sapCpiBAOutbound)
	{
		getSapCpiCrmBAOutboundService().sendBusinessagreement(sapCpiBAOutbound).subscribe(

				// onNext
				responseEntityMap -> {

					if (isSentSuccessfully(responseEntityMap))
					{

						LOG.info(String.format("The BusinessAgreement [%s] has been sent to the SAP backend through SCPI! %n%s",
								sapCpiBAOutbound.getId(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));

					}
					else
					{

						LOG.error(String.format("The BusinessAgreement [%s] has not been sent to the SAP backend! %n%s",
								sapCpiBAOutbound.getId(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));

					}

				}

				// onError
				,
				error -> LOG.error(String.format("The BusinessAgreement [%s] has not been sent to the SAP backend through SCPI! %n%s",
						sapCpiBAOutbound.getId(), error.getMessage()))

		);
	}


	/**
	 * @return the sapCpiCrmBAConversionService
	 */
	public SapCpiCrmBAConversionService getSapCpiCrmBAConversionService()
	{
		return sapCpiCrmBAConversionService;
	}



	/**
	 * @param sapCpiCrmBAConversionService
	 *           the sapCpiCrmBAConversionService to set
	 */
	public void setSapCpiCrmBAConversionService(final SapCpiCrmBAConversionService sapCpiCrmBAConversionService)
	{
		this.sapCpiCrmBAConversionService = sapCpiCrmBAConversionService;
	}




	/**
	 * @return the sapCpiCrmBAOutboundService
	 */
	public SapCpiCrmBAOutboundService getSapCpiCrmBAOutboundService()
	{
		return sapCpiCrmBAOutboundService;
	}




	/**
	 * @param sapCpiCrmBAOutboundService
	 *           the sapCpiCrmBAOutboundService to set
	 */
	public void setSapCpiCrmBAOutboundService(final SapCpiCrmBAOutboundService sapCpiCrmBAOutboundService)
	{
		this.sapCpiCrmBAOutboundService = sapCpiCrmBAOutboundService;
	}



	/**
	 * @return the sapCpiCrmBusinessAgreementService
	 */
	public SapCpiCrmBusinessAgreementService getSapCpiCrmBusinessAgreementService()
	{
		return sapCpiCrmBusinessAgreementService;
	}



	/**
	 * @param sapCpiCrmBusinessAgreementService
	 *           the sapCpiCrmBusinessAgreementService to set
	 */
	public void setSapCpiCrmBusinessAgreementService(final SapCpiCrmBusinessAgreementService sapCpiCrmBusinessAgreementService)
	{
		this.sapCpiCrmBusinessAgreementService = sapCpiCrmBusinessAgreementService;
	}





}
