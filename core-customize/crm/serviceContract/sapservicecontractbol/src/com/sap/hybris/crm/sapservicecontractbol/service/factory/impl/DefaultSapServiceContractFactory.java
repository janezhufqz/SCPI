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
package com.sap.hybris.crm.sapservicecontractbol.service.factory.impl;

import de.hybris.platform.sap.core.common.util.GenericFactory;

import com.sap.hybris.crm.sapservicecontractbol.backendbusinessobject.SapServiceContractBackendBusinessObject;
import com.sap.hybris.crm.sapservicecontractbol.service.factory.SapServiceContractFactory;

/**
 * Default implementation of SapServiceContractFactory.
 *
 *
 */
public class DefaultSapServiceContractFactory implements SapServiceContractFactory {

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
    public void setGenericFactory(final GenericFactory genericFactory) {
        this.genericFactory = genericFactory;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sap.hybris.sapservicecontractfacades.service.factory.
     * SapServiceContractFactory# getSapServiceContractBackendBusinessObject()
     */
    @Override
    public SapServiceContractBackendBusinessObject getSapServiceContractBackendBusinessObject() {
        return (SapServiceContractBackendBusinessObject) genericFactory.getBean("sapCrmbackendBusinessObject");
    }

}
