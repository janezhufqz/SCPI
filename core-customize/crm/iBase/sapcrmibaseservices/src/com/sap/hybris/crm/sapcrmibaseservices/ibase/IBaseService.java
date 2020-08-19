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
package com.sap.hybris.crm.sapcrmibaseservices.ibase;

import java.util.List;

import com.sap.hybris.crm.sapcrmibasecore.model.IBaseModel;

/**
 * @author C5242879
 *
 */
public interface IBaseService {

    /**
     * Get IBase By IBaseId
     *
     * @param iBaseID
     * @return IBase
     */
    IBaseModel getInstalledBaseById(String iBaseID);

    /**
     * Get all the ibases.
     *
     * @return list of ibases
     */
    List<IBaseModel> getInstalledBases();

    /**
     * Fetch IBase By ParentGUID
     */
    IBaseModel getInstalledBaseByParentGuid(String parentGuid);

    /**
     * Fetch IBase By childGUID
     */
    IBaseModel getInstalledBaseByChildGuid(String childGuid);

}
