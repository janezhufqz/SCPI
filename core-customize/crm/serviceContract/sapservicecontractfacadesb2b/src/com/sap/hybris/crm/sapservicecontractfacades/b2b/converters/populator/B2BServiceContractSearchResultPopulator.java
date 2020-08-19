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
package com.sap.hybris.crm.sapservicecontractfacades.b2b.converters.populator;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import com.sap.hybris.crm.sapservicecontract.data.ServiceContractData;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractSearchResult;

/**
 *
 */
public class B2BServiceContractSearchResultPopulator
        implements Populator<ServiceContractSearchResult, ServiceContractData> {

    private UserService userService;
    private Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter;

    @Override
    public void populate(final ServiceContractSearchResult source, final ServiceContractData target) {

        final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
        if (currentCustomer instanceof B2BCustomerModel) {
            final B2BCustomerModel b2bCustomer = (B2BCustomerModel) currentCustomer;
            target.setUnit(getB2bUnitConverter().convert(b2bCustomer.getDefaultB2BUnit()));
        }
    }

    /**
     * @return the userService
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * @param userService
     *            the userService to set
     */
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    /**
     * @return the b2bUnitConverter
     */
    public Converter<B2BUnitModel, B2BUnitData> getB2bUnitConverter() {
        return b2bUnitConverter;
    }

    /**
     * @param b2bUnitConverter
     *            the b2bUnitConverter to set
     */
    public void setB2bUnitConverter(final Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter) {
        this.b2bUnitConverter = b2bUnitConverter;
    }

}