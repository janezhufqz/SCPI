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

package com.sap.hybris.crm.crmcustomerticketingaddon.forms;

public class SapCrmComplaintForm extends SapCrmSupportTicketForm {

    private String associatedOrderCode;
    private String[] associatedOrderEntryCodes;

    public String[] getAssociatedOrderEntryCodes() {
        return associatedOrderEntryCodes;
    }

    public void setAssociatedOrderEntryCodes(String[] associatedOrderEntryCodes) {
        this.associatedOrderEntryCodes = associatedOrderEntryCodes;
    }

    public String getAssociatedOrderCode() {
        return associatedOrderCode;
    }

    public void setAssociatedOrderCode(String associatedOrderCode) {
        this.associatedOrderCode = associatedOrderCode;
    }

}
