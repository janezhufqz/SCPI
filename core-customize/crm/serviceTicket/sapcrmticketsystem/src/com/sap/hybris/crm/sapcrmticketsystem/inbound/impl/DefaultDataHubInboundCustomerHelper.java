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
package com.sap.hybris.crm.sapcrmticketsystem.inbound.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.crm.sapcrmticketsystem.inbound.DataHubInboundCustomerHelper;

/**
 * Default Data Hub Inbound helper for getting customer
 *
 * @author C5229484
 */
public class DefaultDataHubInboundCustomerHelper implements DataHubInboundCustomerHelper {

    /**
     * Logger for class DefaultDataHubInboundCustomerHelper
     */
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDataHubInboundCustomerHelper.class);
    private FlexibleSearchService flexibleSearchService;
    private ModelService modelService;

    /**
     * Query to get User corresponding to customerID
     */
    private static final String FIND_CUSTOMERS_BY_CUSTOMERID_QUERY = "SELECT {" + CustomerModel.PK + "} FROM {"
            + CustomerModel._TYPECODE + "} WHERE {" + CustomerModel.CUSTOMERID + "} = ?customerID ";

    /**
     * This method returns the customer corresponding to the customer id
     * recieved from impex import.
     *
     * @param customerID
     *            the customer id recieved from impex
     * @return uid for that customer
     */
    @Override
    public User getCustomerFromID(final String customerID) {
        LOG.debug("DefaultDataHubInboundCustomerHelper : Translating the customer ID : " + customerID
                + " recieved from impex to UserModel");
        User jaloUser = null;
        if (null != customerID) {
            jaloUser = modelService.getSource(findUserByCustomerID(customerID));
        }
        return jaloUser;
    }

    /**
     * Get the user corresponding to the customer ID
     *
     * @param customerID
     * @return UserModel
     */
    public UserModel findUserByCustomerID(final String customerID) {
        validateParameterNotNull(customerID, "CustomerID must not be null");
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("customerID", customerID);
        return getFlexibleSearchService().searchUnique(
                new FlexibleSearchQuery(FIND_CUSTOMERS_BY_CUSTOMERID_QUERY, queryParams));
    }

    /**
     * @return the flexibleSearchService
     */
    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    /**
     * @param flexibleSearchService
     *            the flexibleSearchService to set
     */
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    /**
     * @return the modelService
     */
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * @param modelService
     *            the modelService to set
     */
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

}
