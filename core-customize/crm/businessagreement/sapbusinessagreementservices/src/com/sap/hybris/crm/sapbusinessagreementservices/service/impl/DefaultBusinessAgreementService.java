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
package com.sap.hybris.crm.sapbusinessagreementservices.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;

import com.sap.hybris.crm.sapbusinessagreementservices.dao.BusinessAgreementDao;
import com.sap.hybris.crm.sapbusinessagreementservices.service.BusinessAgreementService;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;

/**
 *
 */
public class DefaultBusinessAgreementService implements BusinessAgreementService {

    private BusinessAgreementDao businessAgreementDao;

    /**
     * @return the businessAgreementDao
     */
    public BusinessAgreementDao getBusinessAgreementDao() {
        return businessAgreementDao;
    }

    /**
     * @param businessAgreementDao
     *            the businessAgreementDao to set
     */
    public void setBusinessAgreementDao(final BusinessAgreementDao businessAgreementDao) {
        this.businessAgreementDao = businessAgreementDao;
    }

    @Override
    public SearchPageData<BusinessAgreementModel> getBusinessAgreementList(final CustomerModel customerModel,
            final PageableData pageableData) {

        validateParameterNotNull(customerModel, "Customer model cannot be null");
        validateParameterNotNull(pageableData, "PageableData must not be null");

        return this.getBusinessAgreementDao().findBusinessAgreementsForCustomer(customerModel, pageableData);
    }

    @Override
    public BusinessAgreementModel getBusinessAggreementDetails(final String businessAgreementId) {

        return this.getBusinessAgreementDao().getBusinessAggreementDetails(businessAgreementId);
    }

}
