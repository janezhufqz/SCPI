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
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;


/**
 * This event is triggered whenever an address is edited or a new address is added
 */
public class AddressAddUpdateEvent extends AbstractEvent
{

    private static final long serialVersionUID = 1L;
    private AddressModel addressmodel;
    private InterceptorContext context;
    public AddressAddUpdateEvent()
    {
    }
    public AddressAddUpdateEvent(final AddressModel changedaddressmodel, final InterceptorContext ctx)
    {
        this.addressmodel = changedaddressmodel;
        this.context = ctx;
    }

    public InterceptorContext getContext()
    {
        return context;
    }

    public void setContext(final InterceptorContext context)
    {
        this.context = context;
    }

    

    public AddressModel getAddressmodel()
    {
        return addressmodel;
    }

    public void setAddressmodel(final AddressModel addressmodel)
    {
        this.addressmodel = addressmodel;
    }

}
