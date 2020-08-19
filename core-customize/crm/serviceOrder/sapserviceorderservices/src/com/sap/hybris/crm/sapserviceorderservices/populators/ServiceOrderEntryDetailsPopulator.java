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

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import com.sap.hybris.crm.sapserviceorderservices.ScheduleEntryData;
import com.sap.hybris.crm.sapserviceorderservices.ServiceOrderData;
import com.sap.hybris.crm.sapserviceorderservices.model.ScheduleEntryModel;
import com.sap.hybris.crm.sapserviceorderservices.model.ServiceOrderEntryModel;
import com.sap.hybris.crm.sapserviceorderservices.model.ServiceOrderModel;

/**
 * @author SAP
 *
 */
public class ServiceOrderEntryDetailsPopulator implements Populator<ServiceOrderModel, ServiceOrderData> {

    private PriceDataFactory priceFactory;
    private Converter<ProductModel, ProductData> productConverter;

    @Override
    public void populate(final ServiceOrderModel source, final ServiceOrderData target) {
        target.setEntries(groupEntries(source));

    }

    /**
     * @param serviceOrderModel
     * @return List<OrderEntryData>
     */
    private List<OrderEntryData> groupEntries(final ServiceOrderModel serviceOrderModel) {
        final List<AbstractOrderEntryModel> serviceOrderEntrylist = serviceOrderModel.getEntries();

        if (serviceOrderEntrylist != null) {
            final List<OrderEntryData> invoiceItemsData = new ArrayList<OrderEntryData>();
            for (final AbstractOrderEntryModel serviceOrderEntryModel : serviceOrderEntrylist) {
                if (!(serviceOrderEntryModel instanceof ServiceOrderEntryModel)) {
                    continue;
                }
                final ServiceOrderEntryModel serviceOrderEntry = (ServiceOrderEntryModel) serviceOrderEntryModel;
                final OrderEntryData orderEntryData = new OrderEntryData();
                orderEntryData.setRequestedStartDate(serviceOrderEntry.getRequestedstartdate());
                orderEntryData.setRequestedEndDate(serviceOrderEntry.getRequestedenddate());
                orderEntryData.setQuantity(serviceOrderEntryModel.getQuantity());
                if (serviceOrderEntry.getUnit() != null) {
                    orderEntryData.setUnitOfMeasure(serviceOrderEntry.getUnit().getName());
                }
                orderEntryData.setScheduleServiceOrderEntries(setScheduleEntry(serviceOrderEntry));
                if (!StringUtils.isEmpty(serviceOrderEntryModel.getBasePrice())
                        && serviceOrderModel.getCurrency() != null) {
                    orderEntryData.setNetPrice(priceFactory.create(PriceDataType.BUY,
                            BigDecimal.valueOf(serviceOrderEntryModel.getBasePrice().doubleValue()),
                            serviceOrderModel.getCurrency()));
                }

                if (!StringUtils.isEmpty(serviceOrderEntryModel.getProduct())) {
                    final ProductModel product = serviceOrderEntryModel.getProduct();
                    orderEntryData.setProduct(product != null ? productConverter.convert(product) : null);
                }
                invoiceItemsData.add(orderEntryData);
            }
            return invoiceItemsData;
        }

        return Collections.<OrderEntryData> emptyList();
    }

    /**
     * @param serviceOrderEntry
     * @return List<ScheduleEntryData>
     */
    private List<ScheduleEntryData> setScheduleEntry(final ServiceOrderEntryModel serviceOrderEntry) {
        final List<ScheduleEntryModel> scheduleEntries = (List<ScheduleEntryModel>) serviceOrderEntry
                .getScheduleEntry();
        final List<ScheduleEntryData> scheduleEntriesData = new ArrayList<ScheduleEntryData>();
        for (final ScheduleEntryModel scheduleEntry : scheduleEntries) {
            final ScheduleEntryData scheduleEntryData = new ScheduleEntryData();
            scheduleEntryData.setEndDate(scheduleEntry.getEnddate());
            scheduleEntryData.setStartDate(scheduleEntry.getStartdate());
            scheduleEntryData.setItemNumber(scheduleEntry.getItemnumber());
            scheduleEntryData.setOrderNumber(scheduleEntry.getOrdernumber());
            scheduleEntriesData.add(scheduleEntryData);

        }

        return scheduleEntriesData;
    }

    public PriceDataFactory getPriceFactory() {
        return priceFactory;
    }

    @Required
    public void setPriceFactory(final PriceDataFactory priceFactory) {
        this.priceFactory = priceFactory;
    }

    protected Converter<ProductModel, ProductData> getProductConverter() {
        return productConverter;
    }

    @Required
    public void setProductConverter(final Converter<ProductModel, ProductData> productConverter) {
        this.productConverter = productConverter;
    }

}
