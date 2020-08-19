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
package com.sap.hybris.crm.sapcrmibasefacades.converters.populator;

import com.sap.hybris.crm.sapcrmibasecore.model.IBaseModel;
import com.sap.hybris.crm.sapcrmibasefacades.data.IBaseData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * @author C5242879
 *
 */
public class IBaseDataPopulator implements Populator<IBaseModel, IBaseData> {

    /**
     * Method to populate Ibase summary data.
     */
    @Override
    public void populate(final IBaseModel source, final IBaseData target) throws ConversionException {
        target.setNumber(source.getNumber());
        target.setExternalId(source.getExternalId());
        if (null != source.getIBaseType()) {
            target.setCategory(null != source.getIBaseType().getDescription() ? source.getIBaseType().getDescription()
                    : source.getIBaseType().getCode());
        }
        target.setDescription(source.getDescription());
        // set ibase status
        if (null != source.getStatus()) {
            target.setStatus(null != source.getStatus().getDescription() ? source.getStatus().getDescription()
                    : source.getStatus().getCode());
        }
    }
}
