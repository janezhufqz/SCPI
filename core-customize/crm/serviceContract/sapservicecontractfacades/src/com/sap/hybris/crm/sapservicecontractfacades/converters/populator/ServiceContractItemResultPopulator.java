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
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.i18n.daos.CurrencyDao;
import de.hybris.platform.servicelayer.type.TypeService;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.sap.hybris.crm.contract.model.SAPStatusModel;
import com.sap.hybris.crm.sapservicecontract.data.ServiceProductData;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractItemResult;
import com.sap.hybris.crm.sapservicecontractfacades.constants.SapservicecontractfacadesConstants;
import com.sap.hybris.crm.sapservicecontractservices.enums.ContractStatus;
import com.sap.hybris.crm.sapservicecontractservices.enums.ResponseProfile;
import com.sap.hybris.crm.sapservicecontractservices.enums.ServiceProfile;
import com.sap.hybris.crm.sapservicecontractservices.service.ContractService;

/**
 * @author C5229476
 *
 */
public class ServiceContractItemResultPopulator implements Populator<ServiceContractItemResult, ServiceProductData> {

    private static final Log4JWrapper Logger = Log4JWrapper
            .getInstance(ServiceContractItemResultPopulator.class.getName());

    private CurrencyDao currencyDao;
    private Converter<CurrencyModel, CurrencyData> currencyConverter;
    private TypeService typeService;
    private ContractService contractService;
    private I18NService i18nService;

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.converters.Populator#populate(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void populate(final ServiceContractItemResult source, final ServiceProductData target) {
        populateBasicDetails(source, target);

        if (StringUtils.isNotEmpty(source.getItemGuid())) {
            target.setItemGuid(source.getItemGuid());
        }

        if (StringUtils.isNotEmpty(source.getReleasedValue())) {
            target.setReleasedValue(Double.parseDouble(source.getReleasedValue()));
        }

        if (StringUtils.isNotEmpty(source.getReleasedQuantity())) {
            target.setReleasedQuantity(Integer.parseInt(source.getReleasedQuantity()));
        }
        populateQuantity(source, target);

        if (StringUtils.isNotEmpty(source.getCurrency())) {
            target.setCurrency(getCurrencyInfo(source.getCurrency()));
        }
        if (StringUtils.isNotEmpty(source.getStatusCode()) && StringUtils.isNotEmpty(source.getStatusProfile())) {
            final SAPStatusModel contractStatus = getContractService()
                    .getStatusForCodeAndProfile(source.getStatusCode(), source.getStatusProfile());
            setServiceProductItemStatus(contractStatus, source, target);

        } else if (StringUtils.isNoneEmpty(source.getItemSysStatus())) {
            final String sysStatus = getTypeService()
                    .getEnumerationValue(ContractStatus.valueOf(source.getItemSysStatus())).getName();
            if (StringUtils.isNoneEmpty(sysStatus)) {
                target.setItemStatus(sysStatus);
            }
        }
        if (StringUtils.isNotEmpty(source.getProductUnit())) {
            target.setProductUnit(source.getProductUnit());
        }
        populateValidity(source, target);

    }

    /**
     *
     */
    private void setServiceProductItemStatus(final SAPStatusModel contractStatus,
            final ServiceContractItemResult source, final ServiceProductData target) {
        if (contractStatus != null) {
            target.setItemStatus(contractStatus.getDescription(getI18nService().getCurrentLocale()));
        } else {
            target.setItemStatus(
                    getTypeService().getEnumerationValue(ContractStatus.valueOf(source.getItemSysStatus())).getName());
        }

    }

    /**
     * @param source
     * @param target
     */
    private void populateValidity(final ServiceContractItemResult source, final ServiceProductData target) {
        if (source.getItemValidFrom() != null) {
            target.setValidFrom(source.getItemValidFrom());
        }
        if (source.getItemValidTo() != null) {
            target.setValidTo(source.getItemValidTo());
        }
        populateItemValidity(source, target);
    }

    /**
     * @param source
     * @param target
     */
    private void populateQuantity(final ServiceContractItemResult source, final ServiceProductData target) {
        try {
            if (StringUtils.isNotEmpty(source.getQuantity())) {
                target.setQuantity((Long) NumberFormat.getIntegerInstance().parse(source.getQuantity()));
            }
        } catch (final ParseException e) {
            Logger.debug("Error in parsing the attribute values", e);
            throw new ApplicationBaseRuntimeException("Error in parsing the data coming from CRM");
        }
    }

    /**
     * @param source
     * @param target
     */
    private void populateBasicDetails(final ServiceContractItemResult source, final ServiceProductData target) {
        if (StringUtils.isNotEmpty(source.getProductId())) {
            target.setProductId(source.getProductId());
        }

        if (StringUtils.isNotEmpty(source.getServiceContractId())) {
            target.setServiceContractId(source.getServiceContractId());
        }
        if (StringUtils.isNotEmpty(source.getItemNumber())) {
            target.setItemNumber(source.getItemNumber());
        }

        if (StringUtils.isNotEmpty(source.getDescription())) {
            target.setDescription(source.getDescription());
        }

        if (StringUtils.isNotEmpty(source.getNetValue())) {
            target.setNetValue(Double.parseDouble(source.getNetValue()));
        }
        if (StringUtils.isNotEmpty(source.getExpectedValue())) {
            target.setExpectedValue(Double.parseDouble(source.getExpectedValue()));
        }
        if (StringUtils.isNotEmpty(source.getTargetValue())) {
            target.setTargetValue(Double.parseDouble(source.getTargetValue()));
        }

        if (StringUtils.isNotEmpty(source.getResponseProfile())) {
            target.setResponseProfile(getTypeService()
                    .getEnumerationValue(ResponseProfile.valueOf(source.getResponseProfile())).getName());
        }

        if (StringUtils.isNotEmpty(source.getServiceProfile())) {
            target.setServiceProfile(
                    getTypeService().getEnumerationValue(ServiceProfile.valueOf(source.getServiceProfile())).getName());
        }
    }

    /**
     * Check whether the item is renewable /terminable
     *
     * @param target
     */
    private void populateItemValidity(final ServiceContractItemResult source, final ServiceProductData target) {
        target.setIsRenewable(isRenewable(source));
        target.setIsTerminable(isTerminable(source));
    }

    /**
     * @param currency
     * @return CurrencyData
     */
    protected CurrencyData getCurrencyInfo(final String currency) {
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

    public ContractService getContractService() {
        return contractService;
    }

    public void setContractService(final ContractService contractService) {
        this.contractService = contractService;
    }

    public I18NService getI18nService() {
        return i18nService;
    }

    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }

    protected Boolean isRenewable(final ServiceContractItemResult source) {
        Boolean result = Boolean.FALSE;
        if (StringUtils.isNotEmpty(source.getItemSysStatus())
                && SapservicecontractfacadesConstants.RELEASED.equalsIgnoreCase(source.getItemSysStatus())) {
            result = Boolean.TRUE;
        }
        return result;
    }

    protected Boolean isTerminable(final ServiceContractItemResult source) {

        Boolean result = Boolean.FALSE;
        if (source.getValidFrom() != null && source.getValidTo() != null) {
            final Date today = new Date();
            if ((source.getValidFrom().before(today) && source.getValidTo().after(today)) && isRenewable(source)) {
                result = Boolean.TRUE;
            }
        }
        return result;
    }

}