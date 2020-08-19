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
package com.sap.hybris.crm.sapcrmibaseservices.dao;

import java.util.List;

import com.sap.hybris.crm.sapcrmibasecore.model.IBaseModel;

/**
 * @author C5242879
 *
 */
public interface IBaseDao {

    /**
     * Method to find IBase using IBaseId.
     *
     * @param iBaseID
     * @return IBase
     */
    IBaseModel findIBaseById(String iBaseID);

    /**
     * Method to find all the ibases for logged-in customer.
     *
     * @param b2bUnitUid
     *
     * @return List of IBases
     */
    List<IBaseModel> findInstalledBases(String b2bUnitPK);

    /**
     * Method to find IBase by parent GUID
     * 
     * @param parentGuid
     * @return IBase Model
     */
    IBaseModel getInstalledBaseByParentGuid(String parentGuid);

    /**
     * Method to find IBase by child GUID
     * 
     * @param childGuid
     * @return IBase Model
     */
    IBaseModel getInstalledBaseByChildGuid(String childGuid);

}
