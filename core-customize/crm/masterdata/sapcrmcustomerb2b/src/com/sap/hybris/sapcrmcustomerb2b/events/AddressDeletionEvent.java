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

package com.sap.hybris.sapcrmcustomerb2b.events;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

public class AddressDeletionEvent extends AbstractEvent {

    
    private static final long serialVersionUID = 1L;
    private AddressModel addressmodel;

    public AddressDeletionEvent(AddressModel deletedaddressmodel) {
        this.addressmodel = deletedaddressmodel;
    }

    public AddressDeletionEvent() {
    }

    public AddressModel getAddressmodel() {
        return addressmodel;
    }

    public void setAddressmodel(AddressModel addressmodel) {
        this.addressmodel = addressmodel;
    }

}
