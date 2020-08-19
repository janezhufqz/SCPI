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
package com.sap.hybris.crm.sapservicecontractservices.b2b.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.List;

import com.sap.hybris.crm.contract.model.ServiceContractModel;
import com.sap.hybris.crm.sapservicecontractservices.dao.ServiceContractDao;

/**
 *
 */
public interface B2BServiceContractDao extends ServiceContractDao {

    /**
     * Get Contracts for given B2Bunit
     *
     * @param unit
     * @return List<ServiceContractModel>
     */
    List<ServiceContractModel> getServiceContractsforUnit(final B2BUnitModel unit);

    /**
     * Get Contracts for given B2Bunit with SearchPageData Information
     *
     * @param unit
     * @param pageableData
     * @return SearchPageData<ServiceContractModel>
     */
    SearchPageData<ServiceContractModel> getAllContractsForUnit(final B2BUnitModel unit,
            final PageableData pageableData);
}