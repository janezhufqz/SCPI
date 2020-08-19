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
package com.sap.hybris.crm.sapbusinessagreementservices.service;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;

import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;

public interface BusinessAgreementService {
    /**
     *
     * @param customerModel
     * @param pageableData
     * @return list of business agreement
     */
    SearchPageData<BusinessAgreementModel> getBusinessAgreementList(
            final CustomerModel customerModel, final PageableData pageableData);

    /**
     *
     * @param businessAgreementId
     * @return details of business agreement
     */
    BusinessAgreementModel getBusinessAggreementDetails(
            String businessAgreementId);

}
