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
package com.sap.hybris.sapbusinessagreementfacades.facade;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import com.sap.hybris.sapbusinessagreementfacades.data.BusinessAgreementData;
import com.sap.hybris.sapbusinessagreementfacades.data.BusinessAgreementDetailsData;

/**
 *
 */
public interface BusinessAgreementFacade {
    /**
     *
     * @param businessAgreementData
     * @return add a agreement
     */
    String addBusinessAgreement(BusinessAgreementData businessAgreementData);

    /**
     *
     * @param pageableData
     * @return list of business agreement
     */
    SearchPageData<BusinessAgreementData> getPagedBusinessAgreementData(
            PageableData pageableData);

    /**
     *
     * @param businessAgreementID
     * @return details of business agreement
     */
    BusinessAgreementDetailsData getBusinessAggreementDetails(
            String businessAgreementID);
}
