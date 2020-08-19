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

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import org.springframework.beans.factory.annotation.Autowired;

import com.sap.hybris.sapcrmcustomerb2b.events.AddressAddUpdateEvent;
import com.sap.hybris.sapcrmcustomerb2b.events.AddressDeletionEvent;


/**
 * If there is any change in customer address this interceptor will get triggered
 */
public class DefaultB2BCustomerAddressInterceptor implements ValidateInterceptor<AddressModel>, RemoveInterceptor<AddressModel>
{
    @Autowired
    private EventService eventService;
      private CommonUtility commonutil;
    /**
     * If the interceptor got triggered due to adding or updating of an address, then this method gets executed
     */
    @Override
    public void onValidate(final AddressModel addressModel, final InterceptorContext ctx) throws InterceptorException
    {
            final boolean replicateb2bContact = commonutil.shallB2BCustomerBeReplicatedToBackend();
        if (notAReplicationFromBackEnd(addressModel, ctx) && replicateb2bContact)
        {
            //publishes AddressChangeEvent, which in turn triggers onEvent method of AddressChangeEventListener
            eventService.publishEvent(new AddressAddUpdateEvent(addressModel, ctx));
        }
    }

    /**
     * Checks whether or not it is a case of replication of address from backend
     *
     * @param addressModel
     * @param ctx
     * @return
     */
    private boolean notAReplicationFromBackEnd(final AddressModel addressModel, final InterceptorContext ctx)
    {
        return !ctx.isModified(addressModel, "sapIsReplicated") || addressModel.getPublicKey() == null;
    }

    /**
     * If the interceptor got triggered due to deletion of an address, then this method gets executed
     */
    @Override
    public void onRemove(final AddressModel deletedaddressmodel, final InterceptorContext paramInterceptorContext)
            throws InterceptorException
    {
          final boolean replicateb2bContact = commonutil.shallB2BCustomerBeReplicatedToBackend();
          if(replicateb2bContact){
        //publishes AddressDeletionEvent, which in turn triggers onEvent method of AddressRemovalEventListener
        eventService.publishEvent(new AddressDeletionEvent(deletedaddressmodel));
          }
    }
/**
     * @return the commonutil
     */
    public CommonUtility getCommonutil()
    {
        return commonutil;
    }

    /**
     * @param commonutil
     *           the commonutil to set
     */
    public void setCommonutil(final CommonUtility commonutil)
    {
        this.commonutil = commonutil;
    }
}
