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
package com.sap.hybris.crm.sapcrmordermgmtbol.constants;

import java.util.Date;


/**
 * Global class for all Sapcrmordermgmtbol constants. You can add global constants for your extension into this class.
 */
public final class SapcrmordermgmtbolConstants extends GeneratedSapcrmordermgmtbolConstants
{
	public static final String EXTENSIONNAME = "sapcrmordermgmtbol";

	private SapcrmordermgmtbolConstants()
	{
		//empty to avoid instantiating this constant class
	}

	public static final String PREFIX = "sapCrmOrdermgmt";

	public static final String ALIAS_BEAN_PARTNER_MAPPER = PREFIX + "PartnerMapper";


	public static final String ALIAS_BEAN_HEADER_MAPPER = PREFIX + "HeaderMapper";
	/**
	 * Prototype scope bean / Item mapper
	 */
	public static final String ALIAS_BEAN_ITEM_MAPPER = PREFIX + "ItemMapper";
	/**
	 * Prototype scope bean / Header text mapper
	 */

	public static final String ALIAS_BEAN_ACTIONS_STRATEGY = PREFIX + "ActionsStrategy";

	public static final String ALIAS_BEAN_PARTNER_LIST = PREFIX + "PartnerList";

	public static final String ALIAS_BEAN_PARTNER_LIST_ENTRY = PREFIX + "PartnerListEntry";


	public static final String ALIAS_BO_CART = PREFIX + "CartBO";

	public static final String ALIAS_BEAN_WRITE_STRATEGY = PREFIX + "WriteStrategy";

	public static final String BEAN_ID_BE_CART_CRM = PREFIX + "DefaultCartBeCRM";

	public static final String ORDER_HISTORY_IMPORT_PARAM_SHOP_VALUE = "B2C SHOP";
	public static final String ORDER_HISTORY_IMPORT_PARAM_REQ_ORDERS_VALUE = "A";
	public static final String CONFIGURATION_PROPERTY_SALES_ORG_RESPONSIBLE = "sapcommon_salesOrgResponsible";
	public static final String CONFIGURATION_PROPERTY_MAX_HIT = "sapordermgmt_maxHits";

	public static final String BEAN_ID_SEARCH_BE_CRM = PREFIX + "DefaultSearchBeCRM";


	//bean constant will used for message handleing


	public static final String ALIAS_BEAN_MESSAGE_MAPPING_RULES_LOADER = PREFIX + "MessageMappingRulesLoader";

	public static final String ALIAS_BEAN_MESSAGE_MAPPING_RULES_CONTAINER = PREFIX + "MessageMappingRulesContainer";







	/**
	 * Represents an initial date for the ERP backend layer.
	 */
	public static final Date DATE_INITIAL = new Date(0);

	// implement here constants used by this extension
}
