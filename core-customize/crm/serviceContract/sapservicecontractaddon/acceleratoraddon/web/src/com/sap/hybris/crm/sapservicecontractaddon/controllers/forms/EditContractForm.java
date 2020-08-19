/*
* [y] hybris Platform
*
* Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
*
* This software is the confidential and proprietary information of SAP
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with SAP.
*/
package com.sap.hybris.crm.sapservicecontractaddon.controllers.forms;

import de.hybris.platform.validation.annotations.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Form for the renewal of contract
 * 
 * @author C5229488
 *
 */
public class EditContractForm {
    @NotEmpty(message = "{contract.contractId.empty.message}")
    @NotNull(message = "{contract.contractId.null.message}")
    private String contractId;

    @NotEmpty(message = "{contract.contractGuid.empty.message}")
    @NotNull(message = "{contract.contractGuid.null.message}")
    private String contractGuid;

    /**
     * @return the contractId
     */
    public String getContractId() {
        return contractId;
    }

    /**
     * @param contractId
     *            the contractId to set
     */
    public void setContractId(final String contractId) {
        this.contractId = contractId;
    }

    /**
     * @return the contractGuid
     */
    public String getContractGuid() {
        return contractGuid;
    }

    /**
     * @param contractGuid
     *            the contractGuid to set
     */
    public void setContractGuid(final String contractGuid) {
        this.contractGuid = contractGuid;
    }

}
