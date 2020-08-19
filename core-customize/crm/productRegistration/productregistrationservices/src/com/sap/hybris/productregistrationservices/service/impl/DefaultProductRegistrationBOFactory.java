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
package com.sap.hybris.productregistrationservices.service.impl;

import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.crm.sapcrmproductregistrationbol.businessobject.ProductRegistrationBO;
import com.sap.hybris.productregistrationservices.constants.ProductregistrationservicesConstants;
import com.sap.hybris.productregistrationservices.service.ProductRegistrationBOFactory;

import de.hybris.platform.sap.core.common.util.GenericFactory;

/**
 * @author C5229484
 *
 */

public class DefaultProductRegistrationBOFactory implements ProductRegistrationBOFactory {
    private GenericFactory genericFactory;

    /**
     *
     * @return the genericFactory
     */
    public GenericFactory getGenericFactory() {
        return genericFactory;
    }

    /**
     *
     * @param genericFactory
     */
    @Required
    public void setGenericFactory(final GenericFactory genericFactory) {
        this.genericFactory = genericFactory;
    }

    @Override
    public ProductRegistrationBO getProductRegistrationBO() {
        return (ProductRegistrationBO) genericFactory
                .getBean(ProductregistrationservicesConstants.SAP_PRDUCT_REGISTRATION_BO);

    }

}
