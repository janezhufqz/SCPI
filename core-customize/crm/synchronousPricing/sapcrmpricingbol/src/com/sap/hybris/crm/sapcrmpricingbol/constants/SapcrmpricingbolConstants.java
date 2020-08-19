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
package com.sap.hybris.crm.sapcrmpricingbol.constants;

/**
 * Global class for all Sapcrmpricingbol constants. You can add global constants for your extension into this class.
 */
public final class SapcrmpricingbolConstants extends GeneratedSapcrmpricingbolConstants
{
	public static final String EXTENSIONNAME = "sapcrmpricingbol";

	private SapcrmpricingbolConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
	public static final String CONF_PROP_PRICE_SUBTOTAL_CRM = "crmpricingbol_priceSub";
	public static final String SPE_CALCULATE_PRICE = "SPE_CALCULATE_PRICE";

	public static final String JCO_PRICE_INDICATOR_VALUE = "sap.jco.priceindicator.value";
	public static final String JCO_PRICE_INDICATOR_KEY = "sap.jco.priceindicator.key";
	public static final String JCO_PRODUCT = "sap.jco.price.product";
	public static final String JCO_TABLE_PRICE_ITEM = "sap.jco.price.itemtable";
	public static final String JCO_TABLE_PRICE_ITEM_ATTRIBUTES = "sap.jco.price.attributestable";
	public static final String JCO_TABLE_PRICE_TIMESTAMPS = "sap.jco.price.timestampstable";

	public static final String JCO_PRT_DELIVERYDATE = "sap.jco.price.prtdeliverydate";
	public static final String JCO_PRT_ORDERDATE = "sap.jco.price.prtorderdate";

	public static final String JCO_PRICE_DATE = "sap.jco.price.pricedate";
	public static final String JCO_TAX_DATE = "sap.jco.price.taxdate";


}
