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

/**
 *
 */
package com.sap.hybris.crm.sapserviceorderservices.populators;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.sap.hybris.crm.sapserviceorderservices.ServiceOrderData;
import com.sap.hybris.crm.sapserviceorderservices.model.ServiceOrderModel;

/**
 * @author SAP
 *
 */
public class ServiceOrderDetailsPopulator implements Populator<ServiceOrderModel, ServiceOrderData> {
    private Converter<AddressModel, AddressData> addressConverter;
    private Converter<CurrencyModel, CurrencyData> currencyConverter;
    private PriceDataFactory priceFactory;
    private EnumerationService enumerationService;
    private StoreSessionFacade storeSessionFacade;

    
    @Override
    public void populate(final ServiceOrderModel source, final ServiceOrderData target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        final String iso = storeSessionFacade.getCurrentLanguage().getIsocode();
        final Locale currentLocale = new Locale(iso);

        target.setCode(source.getCode());
        target.setStatus(source.getStatus());
        if (source.getStatus() != null && source.getStatus().getCode() != null) {

            final String statusToDisplay = enumerationService.getEnumerationName(
                    OrderStatus.valueOf(source.getStatus().getCode()), currentLocale);
            target.setStatusDisplay(statusToDisplay);
        }
        target.setCreated(source.getCreationtime());
        target.setNote(source.getNote());
        target.setDescription(source.getDescription());
        target.setCurrency(currencyConverter.convert(source.getCurrency()));
        target.setServiceOrderType(source.getServiceOrderType() != null ? source.getServiceOrderType().getCode() : null);
        target.setIbase(source.getIbase());
        target.setIbasecomponent(source.getIbasecomponent());
        target.setObjectid(source.getObjectid());
        target.setServiceContractId(source.getServicecontractid());
        target.setServiceRequestId(source.getServicerequestid());
        target.setWarrantyId(source.getWarrantyid());
        target.setRequestedStartDate(source.getRequestedstartdate());
        target.setRequestedEndDate(source.getRequestedenddate());
        target.setDeliveryAddress(source.getDeliveryAddress() != null ? getAddressConverter().convert(
                source.getDeliveryAddress()) : null);
        target.setBillingAddress(source.getPaymentAddress() != null ? getAddressConverter().convert(
                source.getPaymentAddress()) : null);
        if (source.getCurrency() != null) {
            addTotals(source, target);
        }
    }


    protected void addTotals(ServiceOrderModel source, ServiceOrderData target) {

        if (source.getTotalPrice() != null) {
            target.setTotalPrice(createPrice(source, source.getTotalPrice()));
        }
        if (source.getNetPrice() != null) {
            target.setNetPrice(createPrice(source, source.getNetPrice()));
        }
    }

    protected PriceData createPrice(final AbstractOrderModel source, final Double val) {
        final CurrencyModel currency = source.getCurrency();

        // Get double value, handle null as zero
        final double priceValue = val != null ? val.doubleValue() : 0d;

        return getPriceFactory().create(PriceDataType.BUY, BigDecimal.valueOf(priceValue), currency);
    }

    protected Converter<AddressModel, AddressData> getAddressConverter() {
        return addressConverter;
    }

    @Required
    public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter) {
        this.addressConverter = addressConverter;
    }

    public PriceDataFactory getPriceFactory() {
        return priceFactory;
    }

    @Required
    public void setPriceFactory(final PriceDataFactory priceFactory) {
        this.priceFactory = priceFactory;
    }

    @Required
    public void setCurrencyConverter(final Converter<CurrencyModel, CurrencyData> currencyConverter) {
        this.currencyConverter = currencyConverter;
    }

    protected EnumerationService getEnumerationService() {
        return enumerationService;
    }

    @Required
    public void setEnumerationService(EnumerationService enumerationService) {
        this.enumerationService = enumerationService;
    }

    protected StoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    @Required
    public void setStoreSessionFacade(StoreSessionFacade storeSessionFacade) {
        this.storeSessionFacade = storeSessionFacade;
    }

}
