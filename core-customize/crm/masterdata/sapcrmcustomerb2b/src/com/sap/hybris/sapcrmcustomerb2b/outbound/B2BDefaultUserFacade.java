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

package com.sap.hybris.sapcrmcustomerb2b.outbound;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import de.hybris.platform.commercefacades.user.impl.DefaultUserFacade;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;

public class B2BDefaultUserFacade extends DefaultUserFacade {

    @Override
    public void editAddress(final AddressData addressData) {
        validateParameterNotNullStandardMessage("addressData", addressData);
        final CustomerModel currentCustomer = getCurrentUserForCheckout();
        final AddressModel addressModel = getCustomerAccountService().getAddressForCode(currentCustomer,
                addressData.getId());
        addressModel.setRegion(null);
        getAddressReversePopulator().populate(addressData, addressModel);
        getCustomerAccountService().saveAddressEntry(currentCustomer, addressModel);
        if (addressData.isDefaultAddress()) {
            getCustomerAccountService().setDefaultAddressEntry(currentCustomer, addressModel);
        }

    }

}
