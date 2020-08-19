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
package com.sap.hybris.crm.sapcrmpricingbol.businessobject.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.core.bol.businessobject.BackendInterface;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sappricingbol.backend.interf.SapPricingBackend;
import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricing;
import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction;
import de.hybris.platform.sap.sappricingbol.converter.ConversionService;
import de.hybris.platform.sap.sappricingbol.exceptions.SapPricingRuntimeException;

import java.util.List;

import com.sap.hybris.crm.sapcrmpricingbol.backend.impl.SapPricingBackendCRM;


/**
 * @author C5229090
 *
 */
@BackendInterface(SapPricingBackendCRM.class)
public class SapCrmPricingImpl extends BusinessObjectBase implements SapPricing
{

	static final private Log4JWrapper LOGGER = Log4JWrapper.getInstance(SapCrmPricingImpl.class.getName());

	/**
	 * Returns Backendbusiness object
	 *
	 * @return backendbusiness object
	 * @throws BackendException
	 */
	public SapPricingBackend getSapPricingBackend() throws BackendException
	{
		return (SapPricingBackend) getBackendBusinessObject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricing#getPriceInformationForCart(de.hybris.
	 * platform.core.model.order.AbstractOrderModel,
	 * de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction,
	 * de.hybris.platform.sap.sappricingbol.converter.ConversionService)
	 */
	@Override
	public void getPriceInformationForCart(final AbstractOrderModel order, final SapPricingPartnerFunction partnerFunction,
			final ConversionService conversionService)
	{
		try
		{
			getSapPricingBackend().readPricesForCart(order, partnerFunction, conversionService);
		}
		catch (final CommunicationException e)
		{
			throw new SapPricingRuntimeException(e);

		}
		catch (final BackendException e)
		{
			throw new SapPricingRuntimeException(e);
		}

	}


	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricing#getPriceInformationForProducts(java.util.
	 * List, de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction,
	 * de.hybris.platform.sap.sappricingbol.converter.ConversionService)
	 */
	@Override
	public List<PriceInformation> getPriceInformationForProducts(final List<ProductModel> productModels,
			final SapPricingPartnerFunction partnerFunction, final ConversionService conversionService)
	{
		LOGGER.entering("getPriceInformationForProducts(ProductModel, SapPricingPartnerFunction, ConversionService)");
		try
		{
			LOGGER.debug("**********************************  CRMPricingImpl :: getPriceInformationForProducts Called");
			return getSapPricingBackend().readPriceInformationForProducts(productModels, partnerFunction, conversionService);
		}

		catch (final BackendException e)
		{
			throw new SapPricingRuntimeException(e);
		}
	}

}
