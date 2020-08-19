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
 * @author C5229488
 *
 */
public class EditContractItemForm extends EditContractForm {
    @NotEmpty(message = "{contract.productId.empty.message}")
    @NotNull(message = "{contract.productId.null.message}")
    private String productId;

    @NotEmpty(message = "{contract.itemGuid.empty.message}")
    @NotNull(message = "{contract.itemGuid.null.message}")
    private String itemGuid;

    /**
     * @return the productId
     */
    public String getProductId() {
        return productId;
    }

    /**
     * @param productId
     *            the productId to set
     */
    public void setProductId(final String productId) {
        this.productId = productId;
    }

    /**
     * @return the itemGuid
     */
    public String getItemGuid() {
        return itemGuid;
    }

    /**
     * @param itemGuid
     *            the itemGuid to set
     */
    public void setItemGuid(final String itemGuid) {
        this.itemGuid = itemGuid;
    }
}
