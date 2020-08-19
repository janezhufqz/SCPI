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
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.i18n.daos.CurrencyDao;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.sap.hybris.crm.contract.model.SAPStatusModel;
import com.sap.hybris.crm.sapservicecontract.data.BillPlanData;
import com.sap.hybris.crm.sapservicecontract.data.ServiceContractData;
import com.sap.hybris.crm.sapservicecontract.data.ServiceProductData;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractItemResult;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractSearchResult;
import com.sap.hybris.crm.sapservicecontractservices.enums.ContractStatus;
import com.sap.hybris.crm.sapservicecontractservices.enums.ServiceContractBillingPeriod;
import com.sap.hybris.crm.sapservicecontractservices.service.ContractService;

/**
 * @author C5229476
 *
 */
public class ServiceContractSearchResultPopulator
        implements Populator<ServiceContractSearchResult, ServiceContractData> {

    private CurrencyDao currencyDao;
    private Converter<CurrencyModel, CurrencyData> currencyConverter;
    private Converter<ServiceContractItemResult, ServiceProductData> serviceContractItemResultConverter;
    private UserService userService;
    private ContractService contractService;
    private Converter<CustomerModel, CustomerData> customerConverter;
    private TypeService typeService;
    private I18NService i18nService;

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.converters.Populator#populate(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void populate(final ServiceContractSearchResult source, final ServiceContractData target) {

        target.setContractId(source.getContractId());
        if (StringUtils.isNotEmpty(source.getDescription())) {
            target.setDescription(source.getDescription());
        }
        if (source.getStartDate() != null) {
            target.setStartDate(source.getStartDate());
        }
        if (source.getEndDate() != null) {
            target.setEndDate(source.getEndDate());
        }

        if (StringUtils.isNotEmpty(source.getNetValue())) {
            target.setNetValue(Double.parseDouble(source.getNetValue()));
        }

        if (StringUtils.isNotEmpty(source.getGrossValue())) {
            target.setGrossValue(Double.parseDouble(source.getGrossValue()));
        }
        if (StringUtils.isNotEmpty(source.getCurrency())) {
            target.setCurrency(getCurrencyInfo(source.getCurrency()));
        }
        setContractDataContractStatus(source, target);
        if (StringUtils.isNotEmpty(source.getContractConcatStatus())) {
            target.setContractCondensedStatus(source.getContractConcatStatus());
        }
        target.setIsRenewable(source.getIsRenewable());
        target.setNotes(source.getNotes());
        target.setBillingPlan(getBillingPlan(source));
        target.setContractGuid(source.getContractGuid());
        target.setItems(getServiceContractItemResultConverter().convertAll(source.getItems()));

        final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
        if (null != currentCustomer.getSapConsumerID()) {
            target.setCustomer(getCustomerConverter().convert(currentCustomer));
        }
    }

    /**
    *
    */
    private void setContractDataContractStatus(final ServiceContractSearchResult source,
            final ServiceContractData target) {
        if (StringUtils.isNotEmpty(source.getStatusCode()) && StringUtils.isNotEmpty(source.getStatusProfile())) {
            final SAPStatusModel contractStatus = getContractService()
                    .getStatusForCodeAndProfile(source.getStatusCode(), source.getStatusProfile());
            if (contractStatus != null) {
                target.setContractStatus(contractStatus.getDescription(getI18nService().getCurrentLocale()));
            } else {
                target.setContractStatus(getTypeService()
                        .getEnumerationValue(ContractStatus.valueOf(source.getContractSysStatus())).getName());
            }
        } else if (StringUtils.isNotEmpty(source.getContractSysStatus())) {
            final String sysStatus = getTypeService()
                    .getEnumerationValue(ContractStatus.valueOf(source.getContractSysStatus())).getName();
            if (StringUtils.isNoneEmpty(sysStatus)) {
                target.setContractStatus(getTypeService()
                        .getEnumerationValue(ContractStatus.valueOf(source.getContractSysStatus())).getName());
            } else {
                target.setContractStatus(source.getContractConcatStatus());
            }
        }

    }

    /**
     * @param source
     * @return BillPlanData
     */
    private BillPlanData getBillingPlan(final ServiceContractSearchResult source) {
        final BillPlanData billPlanData = new BillPlanData();

        if (StringUtils.isNotEmpty(source.getBillingDate())) {
            billPlanData.setBillingDate(getTypeService()
                    .getEnumerationValue(ServiceContractBillingPeriod.valueOf(source.getBillingDate())).getName());
        }
        if (StringUtils.isNotEmpty(source.getInvoiceCreationDate())) {
            billPlanData.setInvoiceCreationDate(getTypeService()
                    .getEnumerationValue(ServiceContractBillingPeriod.valueOf(source.getInvoiceCreationDate()))
                    .getName());
        }
        if (StringUtils.isNotEmpty(source.getSettlementPeriod())) {
            billPlanData.setSettlementPeriod(getTypeService()
                    .getEnumerationValue(ServiceContractBillingPeriod.valueOf(source.getSettlementPeriod())).getName());
        }
        return billPlanData;
    }

    private CurrencyData getCurrencyInfo(final String currency) {
        final CurrencyData currencyData = new CurrencyData();
        currencyData.setIsocode(currency);

        final List<CurrencyModel> currencies = getCurrencyDao().findCurrenciesByCode(currencyData.getIsocode());
        final CurrencyModel currModel = !CollectionUtils.isEmpty(currencies) ? currencies.get(0) : null;
        if (currModel != null) {
            getCurrencyConverter().convert(currModel, currencyData);
        }

        return currencyData;
    }

    /**
     * @return the currencyDao
     */
    public CurrencyDao getCurrencyDao() {
        return currencyDao;
    }

    /**
     * @param currencyDao
     *            the currencyDao to set
     */
    public void setCurrencyDao(final CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
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
     * @return the serviceContractItemResultConverter
     */
    public Converter<ServiceContractItemResult, ServiceProductData> getServiceContractItemResultConverter() {
        return serviceContractItemResultConverter;
    }

    /**
     * @param serviceContractItemResultConverter
     *            the serviceContractItemResultConverter to set
     */
    public void setServiceContractItemResultConverter(
            final Converter<ServiceContractItemResult, ServiceProductData> serviceContractItemResultConverter) {
        this.serviceContractItemResultConverter = serviceContractItemResultConverter;
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
     * @return the contractService
     */
    public ContractService getContractService() {
        return contractService;
    }

    /**
     * @param contractService
     *            the contractService to set
     */
    public void setContractService(final ContractService contractService) {
        this.contractService = contractService;
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

    /**
     * @return the typeService
     */
    public TypeService getTypeService() {
        return typeService;
    }

    /**
     * @param typeService
     *            the typeService to set
     */
    public void setTypeService(final TypeService typeService) {
        this.typeService = typeService;
    }

    public I18NService getI18nService() {
        return i18nService;
    }

    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }

}
