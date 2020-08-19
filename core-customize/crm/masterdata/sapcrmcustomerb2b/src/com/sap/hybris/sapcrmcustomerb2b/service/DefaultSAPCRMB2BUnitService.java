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

package com.sap.hybris.sapcrmcustomerb2b.service;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.sap.hybris.sapcustomerb2b.inbound.DefaultSAPB2BUnitService;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.AddressModel;

/**
 * 
 *
 */
public class DefaultSAPCRMB2BUnitService extends DefaultSAPB2BUnitService {

    /**
     * To find address of b2bunit based on addressGUID
     * 
     * @param addressGUID
     * @param b2bunitModel
     * @return AddressModel based on addressGUID
     */
    public AddressModel findAddressByAddressGUID(final String addressGUID, final B2BUnitModel b2bunitModel) {
        validateParameterNotNull(b2bunitModel, "B2BUnit model cannot be null");
        Collection<AddressModel> addresses = b2bunitModel.getAddresses();

        /*
         * TO DO: Below logic need to be changed after handling default flag on
         * addresses
         */
        if (StringUtils.isBlank(addressGUID) && addresses != null && addresses.size() == 1) {

            return addresses.iterator().next();
        }

        return getReturnAddress(addressGUID, addresses);
    }

    private AddressModel getReturnAddress(final String addressGUID, Collection<AddressModel> addresses) {
        AddressModel returnAddress = null;
        if (StringUtils.isNotBlank(addressGUID) && addresses != null && !addresses.isEmpty()) {
            Iterator<AddressModel> itr = addresses.iterator();
            while (itr.hasNext()) {
                AddressModel address = itr.next();
                if (address.getPublicKey() != null && address.getPublicKey().contains(addressGUID)) {
                    returnAddress = address;
                    break;
                }
            }
        }
        return returnAddress;
    }
}