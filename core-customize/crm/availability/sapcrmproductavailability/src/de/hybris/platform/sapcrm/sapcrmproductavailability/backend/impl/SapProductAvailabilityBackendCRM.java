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
package de.hybris.platform.sapcrm.sapcrmproductavailability.backend.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.sap.sapproductavailability.backend.impl.SapProductAvailabilityBackendERP;
import de.hybris.platform.sap.sapproductavailability.constants.SapproductavailabilityConstants;

import java.util.HashMap;
import java.util.Map;

import org.fest.util.Collections;

/**
 * @author C5229471
 *
 */

public class SapProductAvailabilityBackendCRM extends SapProductAvailabilityBackendERP {
    final Map<String, String> propertyMapToCRM = new HashMap<String, String>();

    @Override
    protected String getProperty(final String name) {
        setPropertyMap();
        final String crmPropertyName = propertyMapToCRM.get(name);
        final String propertyValue = super.getProperty(crmPropertyName);
        validateParameterNotNull(propertyValue, "no property value found for property name : " + name);
        return propertyValue;
    }

    private void setPropertyMap() {

        if (Collections.isEmpty(propertyMapToCRM.keySet())) {
            propertyMapToCRM.put(SapproductavailabilityConstants.SALES_ORG, "saperp_salesOrganization");
            propertyMapToCRM.put(SapproductavailabilityConstants.DIS_CHANNEL, "saperp_distributionChannel");
            propertyMapToCRM.put(SapproductavailabilityConstants.DIVISION, "saperp_division");
        }

    }

}
