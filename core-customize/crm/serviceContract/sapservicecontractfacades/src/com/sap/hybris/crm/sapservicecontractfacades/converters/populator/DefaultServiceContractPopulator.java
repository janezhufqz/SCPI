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
package com.sap.hybris.crm.sapservicecontractfacades.converters.populator;

import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.util.Assert;

import com.sap.hybris.crm.contract.model.ServiceContractModel;
import com.sap.hybris.crm.sapservicecontract.data.ServiceContractData;

/**
 * Populate the DTO ServiceContractData
 *
 * @author C5229488
 *
 */
public class DefaultServiceContractPopulator implements Populator<ServiceContractModel, ServiceContractData> {

    private Converter<CurrencyModel, CurrencyData> currencyConverter;
    private Converter<CustomerModel, CustomerData> customerConverter;

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.converters.Populator#populate(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void populate(final ServiceContractModel source, final ServiceContractData target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        if (source.getCurrency() != null) {
            target.setCurrency(getCurrencyConverter().convert(source.getCurrency()));
        }

        if (source.getDescription() != null && source.getDescription().length() != 0) {
            target.setDescription(source.getDescription());
        }

        if (source.getNetValue() != null) {
            target.setNetValue(source.getNetValue());
        }

        populateSomeMoreDetails(source, target);

    }

    /**
     * @param source
     *            ServiceContractModel
     * @param target
     *            ServiceContractData
     */
    private void populateSomeMoreDetails(final ServiceContractModel source, final ServiceContractData target) {

        if (source.getCustomer() != null) {
            target.setCustomer(getCustomerConverter().convert(source.getCustomer()));
        }

        if (source.getCreationtime() != null) {
            target.setContractId(source.getContractId());
        }

        if (source.getStartDate() != null) {
            target.setStartDate(source.getStartDate());
        }

        if (source.getEndDate() != null) {
            target.setEndDate(source.getEndDate());
        }

    }

    /**
     * @return the currencyConverter
     */
    public Converter<CurrencyModel, CurrencyData> getCurrencyConverter() {
        return currencyConverter;
    }

    /**
     * @param currencyConverter
     *            the currencyConverter to set
     */
    public void setCurrencyConverter(final Converter<CurrencyModel, CurrencyData> currencyConverter) {
        this.currencyConverter = currencyConverter;
    }

    /**
     * @return the customerConverter
     */
    public Converter<CustomerModel, CustomerData> getCustomerConverter() {
        return customerConverter;
    }

    /**
     * @param customerConverter
     *            the customerConverter to set
     */
    public void setCustomerConverter(final Converter<CustomerModel, CustomerData> customerConverter) {
        this.customerConverter = customerConverter;
    }

}