/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sap.hybris.crm.sapservicecontractaddon.constants;

/**
 * Global class for all Sapservicecontractaddon web constants. You can add global constants for your extension into this
 * class.
 */
public final class SapservicecontractaddonWebConstants
{
	public static final String BREAD_CRUMBS = "breadcrumbs";
	public static final String META_ROBOTS = "metaRobots";
	public static final String SERVICE_CONTRACTS_PAGE = "service-contracts";
	public static final String SERVICE_CONTRACT_DETAILS_PAGE = "service-contract";
	public static final String SERVICE_CONTRACTS = "serviceContracts";
	public static final String CONTRACT_CODE_VARIABLE_PATTERN = "{contractId:.*}";
	public static final String BACKEND_ERROR = "backendError";
	public static final String SERVICE_CONTRACTS_BREADCRUMB_KEY = "text.account.serviceContracts.breadcrumb";
	public static final String CONTRACT_BREADCRUMB_KEY = "text.account.contract.contractBreadcrumb";
	public static final String ERROR_KEY = "text.account.serviceContracts.backend.error";
	public static final String CONTRACT_GUID_VARIABLE_PATTERN = "{contractGuid:.*}";
	public static final String PRODUCT_GUID_VARIABLE_PATTERN = "{itemGuid:.*}";
	public static final String PRODUCT_ID_VARIABLE_PATTERN = "{productId:.*}";
	public static final String SERVICE_CONTRACTS_RENEWAL_FAILURE_KEY = "text.account.serviceContracts.renew.failure.message";
	public static final String SERVICE_CONTRACTS_RENEWAL_SUCCESS_KEY = "text.account.serviceContracts.renew.success.message";
	public static final String SERVICE_CONTRACTS_RENEWAL_ITEM_FAILURE_KEY = "text.account.serviceContracts.renew.item.failure.message";
	public static final String SERVICE_CONTRACTS_RENEWAL_ITEM_SUCCESS_KEY = "text.account.serviceContracts.renew.item.success.message";

	public static final String SUCCESS_STATUS = "X";
	public static final String REDIRECT_PREFIX = "redirect:";
	public static final String REDIRECT_TO_SERVICE_CONTRACT_LIST_PAGE = REDIRECT_PREFIX + "/my-account/service-contracts";
	public static final String SERVICE_CONTRACTS_TERMINATION_SUCCESS_KEY = "text.account.servicecontract.termination.message.success";
	public static final String SERVICE_CONTRACTS_TERMINATION_FAILURE_KEY = "text.account.servicecontract.termination.message.failure";
	public static final String SERVICE_CONTRACTS_PAGE_SIZE_KEY = "service.contract.page.size";
	public static final String SERVICE_CONTRACTS_RELOAD = "?reload=true";
	public static final String RELOAD_TRUE = "true";
	public static final String REDIRECT_TO_SERVICE_CONTRACT_DETAIL_PAGE = REDIRECT_PREFIX + "/my-account/service-contract/";
	public static final String COMMON_ERROR_STRING = " but unable to connect to backend";
	public static final String CONTRACT_LIST_PAGE = "/my-account/service-contracts";

	private SapservicecontractaddonWebConstants()
	{
		// empty to avoid instantiating this constant class
	}

}
