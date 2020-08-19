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
package com.sap.hybris.sapbusinessagreementaddon.constants;

/**
 * Global class for all Sapbusinessagreementaddon web constants. You can add
 * global constants for your extension into this class.
 */
public final class SapbusinessagreementaddonWebConstants // NOSONAR
{

    public static final String BUSINESS_AGREEMENTS_PAGE = "business-agreements";
    public static final String BUSINESS_AGREEMENT_DETAILS_PAGE = "business-agreement";
    public static final String ADD_BUSINESS_AGREEMENTS_PAGE = "add-business-agreement";
    public static final String META_ROBOTS = "metaRobots";
    public static final String NO_INDEX = "noindex,nofollow";
    public static final String BREAD_CRUMBS = "breadcrumbs";
    public static final String BUSINESS_AGREEMENTS_BREADCRUMB_KEY = "text.account.businessagreements.breadcrumb";
    public static final String BUSINESS_AGREEMENT_BREADCRUMB_KEY = "text.account.businessagreement.breadcrumb";
    public static final String ADD_BUSINESS_AGREEMENTS_BREADCRUMB_KEY = "text.account.businessagreement.add.breadcrumb";

    public static final String BUSINESS_AGREEMENT_CODE_VARIABLE_PATTERN = "{businessAgreementID:.*}";

    private SapbusinessagreementaddonWebConstants() {
        // empty to avoid instantiating this constant class
    }

    // implement here constants used by this extension
}
