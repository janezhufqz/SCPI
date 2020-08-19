
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

package com.sap.hybris.productregistrationaddon.forms;

import javax.validation.constraints.NotNull;

import de.hybris.platform.validation.annotations.NotEmpty;

/**
 * @author C5229484
 *
 */
public class RegisterProductForm {
    @NotEmpty(message = "{productregistration.productID.invalid}")
    @NotNull(message = "{productregistration.productID.invalid}")
    private String productID;

    /**
     * @return the productID
     */
    public String getProductID() {
        return productID;
    }

    /**
     * @param productID
     *            the productID to set
     */
    public void setProductID(final String productID) {
        this.productID = productID;
    }

}
