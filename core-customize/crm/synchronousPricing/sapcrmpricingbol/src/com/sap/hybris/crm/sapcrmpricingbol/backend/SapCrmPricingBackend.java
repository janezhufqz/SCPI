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
package com.sap.hybris.crm.sapcrmpricingbol.backend;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction;
import de.hybris.platform.sap.sappricingbol.converter.ConversionService;

import java.util.List;


/**
 * @author C5229090
 *
 */
public interface SapCrmPricingBackend extends BackendBusinessObject
{
	//test

	public List<PriceInformation> readPriceInformationForProducts(final List<ProductModel> productModels,
			final SapPricingPartnerFunction partnerFunction, ConversionService conversionService) throws BackendException;

	public void readPricesForCart(final AbstractOrderModel order, final SapPricingPartnerFunction partnerFunction,
			ConversionService conversionService) throws BackendException, CommunicationException;

}
