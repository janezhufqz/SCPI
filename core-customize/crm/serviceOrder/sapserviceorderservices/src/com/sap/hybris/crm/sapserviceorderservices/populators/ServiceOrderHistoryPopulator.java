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
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.enumeration.EnumerationService;

import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.sap.hybris.crm.sapserviceorderservices.ServiceOrderHistoryData;
import com.sap.hybris.crm.sapserviceorderservices.enums.ServiceType;
import com.sap.hybris.crm.sapserviceorderservices.model.ServiceOrderModel;

/**
 * @author SAP
 *
 */
public class ServiceOrderHistoryPopulator implements Populator<ServiceOrderModel, ServiceOrderHistoryData> {

    private PriceDataFactory priceDataFactory;
    private EnumerationService enumerationService;
    private StoreSessionFacade storeSessionFacade;

    @Override
    public void populate(final ServiceOrderModel source, final ServiceOrderHistoryData target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        final String iso = storeSessionFacade.getCurrentLanguage().getIsocode();
        final Locale currentLocale = new Locale(iso);

        target.setCode(source.getCode());
        if (source.getServiceOrderType() != null && source.getServiceOrderType().getCode() != null) {
            final String serviceOrderType = enumerationService.getEnumerationName(
                    ServiceType.valueOf(source.getServiceOrderType().getCode()), currentLocale);
            target.setType(serviceOrderType != null ? serviceOrderType : null);
        }
        target.setStatus(source.getStatus());
        target.setStartDate(source.getCreationtime());
        target.setEndDate(source.getRequestedstartdate());
        target.setCreateDate(source.getCreationtime());
        target.setDescription(source.getDescription());
        if (source.getCurrency() != null && source.getTotalPrice() != null) {
                BigDecimal totalPrice = BigDecimal.valueOf(source.getTotalPrice().doubleValue());
                target.setTotal(getPriceDataFactory().create(PriceDataType.BUY, totalPrice, source.getCurrency()));
        }
        if (source.getStatus() != null && source.getStatus().getCode() != null) {

            final String statusToDisplay = enumerationService.getEnumerationName(
                    OrderStatus.valueOf(source.getStatus().getCode()), currentLocale);
            target.setStatusDisplay(statusToDisplay);
        }
    }

    protected PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    @Required
    public void setPriceDataFactory(final PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
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
