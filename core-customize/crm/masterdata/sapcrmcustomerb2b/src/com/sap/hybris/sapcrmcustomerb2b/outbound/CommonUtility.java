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
package com.sap.hybris.sapcrmcustomerb2b.outbound;

import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.UUID;


/**
 * A class which contains method that are common to multiple classes
 */
public class CommonUtility
{
    private BaseStoreService baseStoreService;

    /**
     * generates a unique 32 character string as GUID
     *
     * @return
     */
    protected String generateGuid()
    {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    /**
     * checks whether the attribute replicateregistereduser is active in Sap Global configuration associated with current
     * base store
     */
    protected boolean shallB2BCustomerBeReplicatedToBackend()
    {
        final BaseStoreModel basestore = getBaseStoreService().getCurrentBaseStore();
        final SAPConfigurationModel sapConfigurationModel = (basestore != null) ? basestore.getSAPConfiguration() : null;
        return (sapConfigurationModel != null) ? sapConfigurationModel.getReplicateregisteredb2buser(): false;
    }

    /**
     * @return the baseStoreService
     */
    public BaseStoreService getBaseStoreService()
    {
        return baseStoreService;
    }

    /**
     * @param baseStoreService
     *           the baseStoreService to set
     */
    public void setBaseStoreService(final BaseStoreService baseStoreService)
    {
        this.baseStoreService = baseStoreService;
    }
}
