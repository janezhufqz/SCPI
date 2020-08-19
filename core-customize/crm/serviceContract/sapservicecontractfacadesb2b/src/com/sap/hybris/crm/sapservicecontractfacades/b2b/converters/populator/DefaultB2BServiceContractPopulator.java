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

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import com.sap.hybris.crm.contract.model.ServiceContractModel;
import com.sap.hybris.crm.sapservicecontract.data.ServiceContractData;

/**
 *
 */
public class DefaultB2BServiceContractPopulator implements Populator<ServiceContractModel, ServiceContractData> {

    private Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter;

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

    @Override
    public void populate(final ServiceContractModel source, final ServiceContractData target) {

        if (source.getUnit() != null) {
            target.setUnit(this.getB2bUnitConverter().convert(source.getUnit()));
        }
    }

}