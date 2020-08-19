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
package com.sap.hybris.sapbusinessagreement.constants;

/**
 *
 * Partner roles for sales order processing as defined in SAP CRM for business
 * agreement.
 *
 */
public enum PartnerRoles {

    @SuppressWarnings("javadoc") PAIR("00000004");

    private String code;

    private PartnerRoles(final String code) {
        this.code = code;
    }

    @SuppressWarnings("javadoc")
    public String getCode() {
        return code;
    }
}