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

/**
 *
 */
package com.sap.hybris.productregistrationservices.service;

import com.sap.hybris.crm.sapcrmproductregistrationbol.businessobject.ProductRegistrationBO;

/**
 * @author C5230710
 *
 */
public interface ProductRegistrationBOFactory {
    /**
     * @return ProductRegistrationBO
     */
    public ProductRegistrationBO getProductRegistrationBO();
}
