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

package com.sap.hybris.crm.sapcrmticketsystem.inbound.translators;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.crm.sapcrmticketsystem.constants.SapcrmticketsystemConstants;
import com.sap.hybris.crm.sapcrmticketsystem.inbound.DataHubInboundCustomerHelper;

/**
 * This class includes the translator for customer that retrieves customer based
 * on the crm customer id recieved.
 *
 * @author C5229484
 *
 */
public class DataHubCustomerTranslator extends DataHubTranslator<DataHubInboundCustomerHelper> {

    /**
     * Logger for class DataHubCustomerTranslator
     */
    private static final Logger LOG = LoggerFactory.getLogger(DataHubCustomerTranslator.class);

    @SuppressWarnings("javadoc")
    public static final String HELPER_BEAN = "sapDataHubInboundCustomerHelper";

    @SuppressWarnings("javadoc")
    public DataHubCustomerTranslator() {
        super(HELPER_BEAN);
    }

    /**
     * This method intercepts the ticket import by getting the customerID and
     * returning the User uid corresponding to that customer.This is done
     * because ticket contains UserModel and CRM sends customerID of
     * CustomerModel.
     */
    @Override
    public void performImport(final String customer, final Item processedItem) throws ImpExException {
        LOG.debug("DataHubCustomerTranslator: Invoked translator to convert customer with id :" + customer
                + "to CustomerModel");

        if (customer != null && !customer.equals(SapcrmticketsystemConstants.IGNORE)) {
            try {
                processedItem.setAttribute("customer", getInboundHelper().getCustomerFromID(customer));
            } catch (JaloInvalidParameterException | JaloBusinessException e) {
                LOG.error(SapcrmticketsystemConstants.ERROR_CUSTOMER_TRANSLATOR, e);
            }

        }
    }
}
