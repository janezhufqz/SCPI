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
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Logger;

import com.sap.hybris.sapcrmcustomerb2b.events.AddressDeletionEvent;

/**
 * this listener is triggered whenever an address of a contact person is deleted
 * It sends all the data of a contact person including all its addresses(except
 * the one that has been deleted), to the datahub
 **/
public class AddressRemovalEventListener extends AbstractEventListener<AddressDeletionEvent> {

    private B2BContactExportService b2bContactExportService;
    private ModelService modelService;
    private static final Logger LOGGER = Logger
            .getLogger(com.sap.hybris.sapcrmcustomerb2b.outbound.AddressRemovalEventListener.class.getName());

    public AddressRemovalEventListener() {
    }

    /**
     * This is the method which is executed whenever this event listener is
     * triggered.
     */
    @Override
    protected void onEvent(final AddressDeletionEvent event) {
        // gets the address model that has been deleted from the frontend
        final AddressModel deletedAddress = event.getAddressmodel();
        B2BCustomerModel b2bCustomerModel = null;
        // gets the customer related to the deleted address
        final ItemModel owner = deletedAddress.getOwner();
        if (owner instanceof B2BCustomerModel) {
            b2bCustomerModel = (B2BCustomerModel) owner;
            if (b2bCustomerModel.getCustomerID() == null || b2bCustomerModel.getCustomerID().isEmpty()) {
                return;
            }
            LOGGER.debug("Address " + deletedAddress.getPublicKey() + " is being deleted");
            prepareAndSend(b2bCustomerModel, deletedAddress);
        }
    }

    /**
     * puts B2B customer and its address data in the target map and sends it to
     * the Data Hub
     *
     * @param changedB2bCustomerModel
     */
    public void prepareAndSend(final B2BCustomerModel changedB2bCustomerModel, final AddressModel deletedAddress) {
        if (changedB2bCustomerModel.getDefaultB2BUnit() == null) {
            return;
        }

        getB2bContactExportService().prepareAndSendB2BContact(changedB2bCustomerModel, deletedAddress, "addressDeletion");
    }

    /**
     * Sets the given address as default shipment and payment address for the
     * given customer and then saves the customer model
     *
     * @param customerModel
     * @param addressModel
     */

    public B2BContactExportService getB2bContactExportService() {
        return b2bContactExportService;
    }

    public void setB2bContactExportService(final B2BContactExportService b2bContactExportService) {
        this.b2bContactExportService = b2bContactExportService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

}
