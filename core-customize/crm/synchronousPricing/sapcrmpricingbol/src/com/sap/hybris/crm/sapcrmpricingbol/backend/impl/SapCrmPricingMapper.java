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
package com.sap.hybris.crm.sapcrmpricingbol.backend.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction;

import java.util.List;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;


/**
 * @author C5229090
 *
 */
public class SapCrmPricingMapper
{

	private SapCrmPricingHeaderMapper headerMapper;
	private SapCrmPricingItemMapper itemMapper;


	/**
	 *
	 * @return the headerMapper
	 */
	public SapCrmPricingHeaderMapper getHeaderMapper()
	{
		return headerMapper;
	}

	/**
	 * @param headerMapper
	 *           the headerMapper to set
	 */
	public void setHeaderMapper(final SapCrmPricingHeaderMapper headerMapper)
	{
		this.headerMapper = headerMapper;
	}

	/**
	 * @return the itemMapper
	 */


	public SapCrmPricingItemMapper getItemMapper()
	{
		return itemMapper;
	}

	/**
	 * @param itemMapper
	 *           the itemMapper to set
	 */
	public void setItemMapper(final SapCrmPricingItemMapper itemMapper)
	{
		this.itemMapper = itemMapper;
	}

	/**
	 * Read catalog result prices
	 *
	 * @param resultTable
	 * @return price
	 */
	public List<PriceInformation> readResultExport(final JCoTable resultTable)
	{
		return getItemMapper().readPrices(resultTable);
	}

	/**
	 * Read cart result table
	 *
	 * @param order
	 * @param resultTable
	 */
	public void readResultExport(final AbstractOrderModel order, final JCoTable resultTable)
	{
		getItemMapper().readPrices(order, resultTable);
	}

	/**
	 * Fill catalog items imports
	 *
	 * @param function
	 * @param partnerFunction
	 * @param productModels
	 */
	public void fillImportParameters(final JCoFunction function, final List<ProductModel> productModels,
			final SapPricingPartnerFunction partnerFunction)
	{

		fillHeaderImports(function, partnerFunction);
		fillItemImports(function, productModels);
	}

	/**
	 * Fill cart items imports
	 *
	 * @param function
	 * @param partnerFunction
	 * @param order
	 */
	public void fillImportParameters(final JCoFunction function, final AbstractOrderModel order,
			final SapPricingPartnerFunction partnerFunction)
	{
		fillHeaderImports(function, partnerFunction, order);
		fillItemImports(order, function);
	}


	/**
	 * Fill catalog items imports
	 *
	 * @param function
	 * @param productModels
	 */
	private void fillItemImports(final JCoFunction function, final List<ProductModel> productModels)
	{
		getItemMapper().fillImportParameters(function, productModels);
	}

	/**
	 * Fill cart items imports
	 *
	 * @param function
	 * @param order
	 *
	 */
	private void fillItemImports(final AbstractOrderModel order, final JCoFunction function)
	{
		getItemMapper().fillImportParameters(order, function);
	}

	/**
	 * Fill catalog header imports
	 *
	 * @param function
	 * @param partnerFunction
	 */
	private void fillHeaderImports(final JCoFunction function, final SapPricingPartnerFunction partnerFunction)
	{
		getHeaderMapper().fillImportParameters(function, partnerFunction);
	}

	/**
	 * @param function
	 * @param order
	 */
	private void fillHeaderImports(final JCoFunction function, final SapPricingPartnerFunction partnerFunction,
			final AbstractOrderModel order)
	{
		getHeaderMapper().fillImportTaxParameters(function, partnerFunction, order);
	}


}
