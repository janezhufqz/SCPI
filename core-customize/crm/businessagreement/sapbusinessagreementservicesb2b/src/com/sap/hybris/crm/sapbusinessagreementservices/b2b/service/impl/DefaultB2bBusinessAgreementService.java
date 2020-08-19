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
package com.sap.hybris.crm.sapbusinessagreementservices.b2b.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;

import com.sap.hybris.crm.sapbusinessagreementservices.b2b.dao.B2bBusinessAgreementDao;
import com.sap.hybris.crm.sapbusinessagreementservices.service.impl.DefaultBusinessAgreementService;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;

/**
 *
 */
public class DefaultB2bBusinessAgreementService extends DefaultBusinessAgreementService {

    private B2bBusinessAgreementDao b2bBusinessAgreementDao;

    /**
     * @return the b2bBusinessAgreementDao
     */
    public B2bBusinessAgreementDao getB2bBusinessAgreementDao() {
        return b2bBusinessAgreementDao;
    }

    /**
     * @param b2bBusinessAgreementDao
     *            the businessAgreementDao to set
     */
    public void setB2bBusinessAgreementDao(final B2bBusinessAgreementDao b2bBusinessAgreementDao) {
        this.b2bBusinessAgreementDao = b2bBusinessAgreementDao;
    }

    @Override
    public SearchPageData<BusinessAgreementModel> getBusinessAgreementList(final CustomerModel customerModel,
            final PageableData pageableData) {

        validateParameterNotNull(customerModel, "Customer model cannot be null");
        validateParameterNotNull(pageableData, "PageableData must not be null");

        B2BCustomerModel b2bCustomer = null;
        if (customerModel instanceof B2BCustomerModel) {
            b2bCustomer = (B2BCustomerModel) customerModel;
            final B2BUnitModel b2bUnitModel = b2bCustomer.getDefaultB2BUnit();
            validateParameterNotNull(b2bUnitModel, "b2b unit model cannot be null");
            return this.getB2bBusinessAgreementDao().findBusinessAgreementsForUnit(b2bUnitModel, pageableData);
        } else {
            return this.getB2bBusinessAgreementDao().findBusinessAgreementsForCustomer(customerModel, pageableData);
        }

    }

}
