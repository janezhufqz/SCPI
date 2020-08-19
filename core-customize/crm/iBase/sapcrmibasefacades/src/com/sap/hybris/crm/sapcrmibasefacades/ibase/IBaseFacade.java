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
package com.sap.hybris.crm.sapcrmibasefacades.ibase;

import java.util.List;

import com.sap.hybris.crm.sapcrmibasefacades.data.IBaseData;
import com.sap.hybris.crm.sapcrmibasefacades.data.IBaseDetailData;

/**
 * @author C5242879
 *
 */
public interface IBaseFacade {

    /**
     * Get IBase using ID.
     *
     * @param iBaseID
     * @return IBase
     */
    IBaseDetailData getInstalledBaseById(String iBaseID);

    /**
     * Get list of all the iBases.
     *
     * @return list of IBases
     *
     */
    List<IBaseData> getInstalledBases();

    IBaseDetailData getInstalledBaseByParentGuid(String parentGuid);

    IBaseDetailData getInstalledBaseByChildGuid(String childGuid);
}
