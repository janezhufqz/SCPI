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
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.sap.hybris.sapcustomerb2b.outbound.B2BRegistrationEvent;

/**
 * Catch the register event and start the <code>sapCustomerPublishProcess</code>
 * business process
 */
public class DefaultB2BCustomerRegistrationEventListener extends AbstractEventListener<B2BRegistrationEvent> {
    private static final Logger LOGGER = Logger.getLogger(
            com.sap.hybris.sapcrmcustomerb2b.outbound.DefaultB2BCustomerRegistrationEventListener.class.getName());

    private ModelService modelService;
    private B2BContactExportService b2bContactExportService;
    private CommonUtility commonutil;
    private String dateformat;

    private PersistentKeyGenerator sapContactIdGenerator;

    /**
     * start the <code>sapCustomerPublishProcess</code> business process
     *
     */
    @Override
    protected void onEvent(final B2BRegistrationEvent registerEvent) {
        // only if replication of user is requested start publishing to Data Hub
        // process
        final boolean replicateb2bContact = commonutil.shallB2BCustomerBeReplicatedToBackend();
        final CustomerModel customerModel = registerEvent != null ? registerEvent.getCustomer() : null;
        if (replicateb2bContact && customerModel != null && (customerModel instanceof B2BCustomerModel)
                && notReplicationFromBackend(customerModel)) {
            final B2BCustomerModel b2bCustomer = (B2BCustomerModel) customerModel;

            // Set the customer ID
            customerModel.setCustomerID((String) getSapContactIdGenerator().generate());

            // Set the SAP contact ID -
            customerModel.setSapContactID(customerModel.getCustomerID());
            LOGGER.debug("A new contact with ID: " + customerModel.getCustomerID() + " is being created");
            // set the customer GUID
            final String customerGuid = commonutil.generateGuid();
            customerModel.setCrmGuid(customerGuid);

            // Disable the login initially till acknowledgement comes back from
            // backend
            b2bCustomer.setLoginDisabled(true);

            setReplicationInfo(b2bCustomer);

            // Save the customer to hybris
            modelService.save(b2bCustomer);
            // Now send the customer to data hub for replication to backend
            b2bContactExportService.prepareAndSendB2BContact(b2bCustomer, null, "newRegistration");

        } else if (LOGGER.isDebugEnabled() && !replicateb2bContact && !notReplicationFromBackend(customerModel)
                && customerModel != null) {
            LOGGER.debug("During registration the B2B Contact " + customerModel.getPk()
                    + " was not sent to Data Hub. Either ReplicateRegisteredUser field is not active in SAP Global Configuration, associated with the current base store OR The data is already there in the backend");

        }
    }

    // SAP Contact Id supposed to be non empty or non null in case if customer
    // is
    // replicated after being created in CRM
    protected boolean notReplicationFromBackend(final CustomerModel customerModel) {
        return StringUtils.isEmpty(customerModel.getSapContactID());
    }

    // sets the information about the time at which data was sent to backend for
    // replication
    protected void setReplicationInfo(final B2BCustomerModel b2bCustomer) {
        final DateFormat dateFormat = new SimpleDateFormat(dateformat);
        b2bCustomer.setSapReplicationInfo("Sent to datahub " + dateFormat.format(Calendar.getInstance().getTime()));
    }

    /**
     * @return modelService
     */
    protected ModelService getModelService() {
        return modelService;
    }

    /**
     * @param modelService
     */
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public B2BContactExportService getB2bContactExportService() {
        return b2bContactExportService;
    }

    public void setB2bContactExportService(final B2BContactExportService b2bContactExportService) {
        this.b2bContactExportService = b2bContactExportService;
    }

    /**
     * @return the sapContactIdGenerator
     */
    public PersistentKeyGenerator getSapContactIdGenerator() {
        return sapContactIdGenerator;
    }

    /**
     * @param sapContactIdGenerator
     *            the sapContactIdGenerator to set
     */
    public void setSapContactIdGenerator(final PersistentKeyGenerator sapContactIdGenerator) {
        this.sapContactIdGenerator = sapContactIdGenerator;
    }

    /**
     * @return the commonutil
     */
    public CommonUtility getCommonutil() {
        return commonutil;
    }

    /**
     * @param commonutil
     *            the commonutil to set
     */
    public void setCommonutil(final CommonUtility commonutil) {
        this.commonutil = commonutil;
    }

    /**
     * @return the dateformat
     */
    public String getDateformat() {
        return dateformat;
    }

    /**
     * @param dateformat
     *            the dateformat to set
     */
    public void setDateformat(final String dateformat) {
        this.dateformat = dateformat;
    }

}
