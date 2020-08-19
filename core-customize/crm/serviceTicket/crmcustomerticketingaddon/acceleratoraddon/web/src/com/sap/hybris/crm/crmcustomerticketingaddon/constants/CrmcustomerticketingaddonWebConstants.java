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
package com.sap.hybris.crm.crmcustomerticketingaddon.constants;

/**
 * Global class for all Crmcustomerticketingaddon web constants. You can add
 * global constants for your extension into this class.
 */
public final class CrmcustomerticketingaddonWebConstants // NOSONAR
{

    public static final String REASON_GETTER_METHOD = "getReasonCategory";
    public static final String REASON_SETTER_METHOD = "setReasonCategory";
    public static final String REASON_CATEGORIES = "reasonCategories";
    public static final String META_ROBOTS = "metaRobots";
    public static final String BREAD_CRUMBS = "breadcrumbs";
    public static final String NO_INDEX_NO_FOLLOW = "noindex,nofollow";
    public static final String TICKET_TYPE = "type";
    public static final String TICKET_TYPE_SUPPORT = "support-ticket";
    public static final String TICKET_TYPE_COMPLAINT = "complaint";

    private CrmcustomerticketingaddonWebConstants() {
        // empty to avoid instantiating this constant class
    }
}
