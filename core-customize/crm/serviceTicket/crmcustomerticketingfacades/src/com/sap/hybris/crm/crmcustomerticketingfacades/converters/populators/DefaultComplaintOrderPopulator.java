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

package com.sap.hybris.crm.crmcustomerticketingfacades.converters.populators;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import com.sap.hybris.crm.crmcustomerticketingfacades.data.ComplaintAssociatedOrderData;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * This is used to populate the required data to display on the customer request
 * tickets form.
 *
 */
public class DefaultComplaintOrderPopulator<Source extends AbstractOrderModel, Target extends ComplaintAssociatedOrderData>
        implements Populator<Source, Target> {

    @Autowired
    private PriceDataFactory priceDataFactory;

    @Override
    public void populate(final AbstractOrderModel source, final ComplaintAssociatedOrderData target)
            throws ConversionException {
        target.setCode(source.getCode());
        target.setModifiedtime(source.getModifiedtime());
        target.setTotalPrice(createPrice(source, source.getTotalPrice()));
    }

    protected PriceData createPrice(final AbstractOrderModel source, final Double val) {
        if (source == null) {
            throw new IllegalArgumentException("source order must not be null");
        }

        final CurrencyModel currency = source.getCurrency();
        if (currency == null) {
            throw new IllegalArgumentException("source order currency must not be null");
        }

        // Get double value, handle null as zero
        final double priceValue = val != null ? val.doubleValue() : 0d;

        return priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(priceValue), currency);
    }

}
