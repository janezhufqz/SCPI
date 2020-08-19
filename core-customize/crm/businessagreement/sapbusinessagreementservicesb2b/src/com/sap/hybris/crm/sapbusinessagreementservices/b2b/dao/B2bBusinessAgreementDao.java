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
package com.sap.hybris.crm.sapbusinessagreementservices.b2b.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import com.sap.hybris.crm.sapbusinessagreementservices.dao.BusinessAgreementDao;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;

public interface B2bBusinessAgreementDao extends BusinessAgreementDao {

    /**
     *
     * @param unit
     * @param pageableData
     * @return list of business agreement for b2b unit
     */
    SearchPageData<BusinessAgreementModel> findBusinessAgreementsForUnit(B2BUnitModel unit, PageableData pageableData);

}
