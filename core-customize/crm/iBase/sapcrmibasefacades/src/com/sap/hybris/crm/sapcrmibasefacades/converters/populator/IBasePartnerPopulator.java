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

import com.sap.hybris.crm.sapcrmibasecore.model.IBasePartnerModel;
import com.sap.hybris.crm.sapcrmibasefacades.data.IBasePartnerData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * @author C5242879
 *
 */
public class IBasePartnerPopulator implements Populator<IBasePartnerModel, IBasePartnerData> {

    /**
     * Method to populate IBase partner of an IBase.
     */
    @Override
    public void populate(final IBasePartnerModel source, final IBasePartnerData target) throws ConversionException {
        if (null != source.getFunction()) {
            target.setFunction(null != source.getFunction().getDescription() ? source.getFunction().getDescription()
                    : source.getFunction().getCode());
        }
        if (null != source.getPartner()) {
            target.setId(source.getPartner().getUid());
            target.setName(source.getPartner().getName());
        } else if (null != source.getB2bUnit()) {
            target.setId(source.getB2bUnit().getUid());
            target.setName(source.getB2bUnit().getName());
            if (null != source.getB2bUnit().getAddresses() && !source.getB2bUnit().getAddresses().isEmpty()) {
                final AddressModel address = source.getB2bUnit().getAddresses().iterator().next();
                target.setAddress(address.getStreetname() + " " + address.getStreetnumber() + " "
                        + address.getPostalcode() + " " + address.getTown());
            }

        }
        target.setIsMainPartner(source.getMainPartner());

    }

}
