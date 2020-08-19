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


import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import org.apache.log4j.Logger;

import com.sap.hybris.sapcrmcustomerb2b.events.AddressAddUpdateEvent;


/**
 * The listener for AddressChangeEvent, triggered whenever an address is added or edited
 */
public class AddressAddUpdateEventListener extends AbstractEventListener<AddressAddUpdateEvent>
{
    private B2BContactExportService b2bContactExportService;
    private CommonUtility commonutil;
    private static final Logger LOGGER = Logger
          .getLogger(com.sap.hybris.sapcrmcustomerb2b.outbound.AddressAddUpdateEventListener.class.getName());



    /**
     * This method is executed whenever this listener is triggered. It sends all the data of the customer along with
     * updated address to datahub
     */
    @Override
    protected void onEvent(final AddressAddUpdateEvent event)
    {
        //gets the address model that has been added or edited
        final AddressModel addressModel = event.getAddressmodel();
        B2BCustomerModel b2bCustomerModel = null;

        // get the customer related to the address
        final ItemModel owner = addressModel.getOwner();
        if (owner instanceof B2BCustomerModel)
        {
            b2bCustomerModel = (B2BCustomerModel) owner;

            if (b2bCustomerModel.getCustomerID() == null || b2bCustomerModel.getCustomerID().isEmpty())
            {   LOGGER.debug("customer id not set.");
                return;
            }
            String addressGuid;
            //if the public key is not set, which means it is a new address
            if (addressModel.getPublicKey() == null)
            {
                addressGuid = commonutil.generateGuid();
                setAddressPublicKey(addressModel, b2bCustomerModel, addressGuid);
                LOGGER.debug("Creating a new address with address Guid: "+ addressGuid);
            }
            //sends the customer data along with updated address to datahub
            b2bContactExportService.prepareAndSendB2BContact(b2bCustomerModel, addressModel,"addressAdditionUpdation");

        }
    }


    /**
     * sets the public key of addressmodel. which will be a string containing owner id appended to a 32 character unique
     * guid, with a pipe symbol in between
     *
     * @param addressModel
     * @param b2bCustomerModel
     * @param addressGuid
     */
    private void setAddressPublicKey(final AddressModel addressModel, final B2BCustomerModel b2bCustomerModel,
            final String addressGuid)
    {
        addressModel.setPublicKey(b2bCustomerModel.getSapContactID() + "|" + addressGuid);
    }

    public B2BContactExportService getB2bContactExportService()
    {
        return b2bContactExportService;
    }

    public void setB2bContactExportService(final B2BContactExportService b2bContactExportService)
    {
        this.b2bContactExportService = b2bContactExportService;
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
    public void setCommonutil(CommonUtility commonutil)
    {
        this.commonutil = commonutil;
    }

}
