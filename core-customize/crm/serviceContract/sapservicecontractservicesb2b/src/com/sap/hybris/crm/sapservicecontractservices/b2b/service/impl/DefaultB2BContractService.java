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
package com.sap.hybris.crm.sapservicecontractservices.b2b.service.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.List;

import com.sap.hybris.crm.contract.model.ServiceContractModel;
import com.sap.hybris.crm.sapservicecontractservices.b2b.dao.B2BServiceContractDao;
import com.sap.hybris.crm.sapservicecontractservices.service.impl.DefaultContractService;

/**
 *
 */
public class DefaultB2BContractService extends DefaultContractService {

    private transient B2BServiceContractDao b2bserviceContractDao;

    /**
     * @return the b2bserviceContractDao
     */
    public B2BServiceContractDao getB2bserviceContractDao() {
        return b2bserviceContractDao;
    }

    /**
     * @param b2bserviceContractDao
     *            the b2bserviceContractDao to set
     */
    public void setB2bserviceContractDao(final B2BServiceContractDao b2bserviceContractDao) {
        this.b2bserviceContractDao = b2bserviceContractDao;
    }

    @Override
    public SearchPageData<ServiceContractModel> getAllContracts(final CustomerModel customer,
            final PageableData pageableData) {
        B2BUnitModel unit = null;
        if (customer instanceof B2BCustomerModel) {
            final B2BCustomerModel b2bCustomer = (B2BCustomerModel) customer;
            if (null != b2bCustomer.getDefaultB2BUnit()) {
                unit = b2bCustomer.getDefaultB2BUnit();
            }
            return this.getB2bserviceContractDao().getAllContractsForUnit(unit, pageableData);
        } else {
            return super.getAllContracts(customer, pageableData);
        }
    }

    @Override
    public List<ServiceContractModel> getContracts(final CustomerModel customer) {
        if (customer instanceof B2BCustomerModel) {
            final B2BCustomerModel b2bCustomer = (B2BCustomerModel) customer;
            final B2BUnitModel unit = b2bCustomer.getDefaultB2BUnit();
            return getB2bserviceContractDao().getServiceContractsforUnit(unit);
        } else {
            return super.getContracts(customer);
        }
    }

    @Override
    public String getUid(final CustomerModel customer) {
        if (customer instanceof B2BCustomerModel) {
            final B2BCustomerModel b2bCustomer = (B2BCustomerModel) customer;
            final B2BUnitModel unit = b2bCustomer.getDefaultB2BUnit();
            return null != unit ? unit.getUid() : null;
        } else {
            return super.getUid(customer);
        }

    }

}