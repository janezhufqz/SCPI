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

package com.sap.hybris.crm.sapserviceorderservices.interceptors;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.exceptions.FlexibleSearchException;
import com.sap.hybris.crm.sapserviceorderservices.model.ServiceOrderModel;

/**
 * This interceptor is triggered whenever there's a change in any address. It gets the
 * public key from the address and searches for the cloned addresses. In the
 * cloned addresses it searches whether any address belongs to some service
 * order, if so it updates that address
 * 
 */
public class ServiceOrderAddressUpdaterInterceptor implements PrepareInterceptor<AddressModel> {
    private static final String QUERY = "select pk from {Address}  where {publickey} =?originaladdresspublickey";
    private static final Logger LOG = LoggerFactory.getLogger(ServiceOrderAddressUpdaterInterceptor.class);

    private ModelService modelService;
    private FlexibleSearchService flexibleSearchService;

    @Override
    public void onPrepare(final AddressModel address, final InterceptorContext context) throws InterceptorException {
        if (address.getDuplicate().equals(Boolean.FALSE)) {
                String publickey = address.getPublicKey();

                List<AddressModel> clonedaddresses = getClonedAddresses(publickey);
                if (clonedaddresses != null) {
                    updateClonedAddressBelongingtoServiceOrder(clonedaddresses, address);
                }
            }
    }

    /**
     * searches for addresses in the cloned addresses which belong to any
     * service order if found it updates the address
     * 
     * @param clonedaddresses
     * @param originalAddress
     */
    private void updateClonedAddressBelongingtoServiceOrder(List<AddressModel> clonedaddresses,
            final AddressModel originalAddress) {
        for (AddressModel address : clonedaddresses) {
            ItemModel owner = address.getOwner();
            if (owner instanceof ServiceOrderModel) {
                updateServiceOrderAddress(address, originalAddress);
            }
        }
    }

    /**
     * updates the given address in the service order
     * 
     * @param clonedAddress
     * @param originalAddress
     */
    private void updateServiceOrderAddress(final AddressModel clonedAddress, final AddressModel originalAddress) {
        ServiceOrderModel order = (ServiceOrderModel) clonedAddress.getOwner();
        if (clonedAddress.equals(order.getPaymentAddress())) {
            order.setPaymentAddress(originalAddress);
            modelService.save(order);
        } else if (clonedAddress.equals(order.getDeliveryAddress())) {
            order.setDeliveryAddress(originalAddress);
            modelService.save(order);
        }
    }

    /**
     * 
     * gets the list of the cloned addresses based on public key
     * 
     * @param publicKey
     * @return
     */
    protected List<AddressModel> getClonedAddresses(final String publicKey) {
        if (publicKey == null) {
        	return Collections.emptyList();
        }
        final FlexibleSearchQuery fsQuery = getFlexibleSearchQuery();
        fsQuery.addQueryParameter("originaladdresspublickey", publicKey);
        List<AddressModel> clonedAddresses = null;
        try {
            final SearchResult<AddressModel> searchResult = getFlexibleSearchService().search(fsQuery);
            if (searchResult != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Search Result: found " + searchResult.getCount() + " Addresses");
                }
                clonedAddresses = searchResult.getResult();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Search Result: nothing found");
                }
            }

        } catch (final FlexibleSearchException fe) {
            LOG.error("Error in Query Execution " + fsQuery.getQuery() + " Exception is " + fe);
        }
        return clonedAddresses;

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

    protected FlexibleSearchQuery getFlexibleSearchQuery() {
        return new FlexibleSearchQuery(QUERY);
    }

    protected String getQuery() {
        return QUERY;
    }

}
