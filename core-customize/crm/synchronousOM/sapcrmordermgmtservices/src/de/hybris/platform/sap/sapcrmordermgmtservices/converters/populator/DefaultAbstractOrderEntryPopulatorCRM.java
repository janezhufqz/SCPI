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

package de.hybris.platform.sap.sapcrmordermgmtservices.converters.populator;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultAbstractOrderEntryPopulator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * @author C5230711
 *
 */
public class DefaultAbstractOrderEntryPopulatorCRM extends DefaultAbstractOrderEntryPopulator
{

	@Override
	public void populate(final Item item, final OrderEntryData target) throws ConversionException
	{
		super.populate(item, target);
		final ProductData productData = createProductFromItem(item);
		productData.setPurchasable(Boolean.TRUE);
		target.setProduct(productData);

	}

	ProductData createProductFromItem(final Item item)
	{
		final ProductData productData = new ProductData();
		productData.setCode(formatProductIdForHybris(item.getProductId()));
		productData.setName(item.getProductId());
		return productData;
	}
}
