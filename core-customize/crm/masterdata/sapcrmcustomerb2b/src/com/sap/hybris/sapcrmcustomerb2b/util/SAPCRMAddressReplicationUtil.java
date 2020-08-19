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

/**
 *
 */
package com.sap.hybris.sapcrmcustomerb2b.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapcrmcustomerb2b.constants.Sapcrmcustomerb2bConstants;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * @author C5229493
 *
 *         Class is used for getting addresses from database on basis of public
 *         key and updating addresses with parameterized address
 */
public class SAPCRMAddressReplicationUtil {
    private static final Logger LOG = LoggerFactory.getLogger(SAPCRMAddressReplicationUtil.class);

    /**
     * @param publicKey
     * @param flexibleSearchService
     * @return true list of AddressModel if present otherwise null or empty list
     */
    public List<AddressModel> addressesPresent(final String publicKey,
            final FlexibleSearchService flexibleSearchService) {
        final AddressModel addressModel = new AddressModel();
        addressModel.setPublicKey(publicKey);
        List<AddressModel> b2bAddresses = null;
        try {
            b2bAddresses = flexibleSearchService.getModelsByExample(addressModel);
            if (b2bAddresses == null || b2bAddresses.isEmpty()) {
                LOG.debug("No Existing Address Found with Public Key: " + publicKey);

            }

        } catch (final ModelNotFoundException me) {
            LOG.debug("No Existing Address Found with Public Key: " + publicKey + " Exception is: " + me);

        }
        return b2bAddresses;
    }

    /**
     * @param b2bAddresses
     *            Addresses to Update
     * @param cloneAddress
     *            Reference Address
     * @param addressUsage
     *            Usage Of Address
     * @param modelService
     *            Updates the already existing addresses
     */
    public void updateExistingAddresses(final List<AddressModel> b2bAddresses, final AddressModel cloneAddress,
            final String addressUsage, final ModelService modelService) {

        try {
            for (final AddressModel b2bAddress : b2bAddresses) {
                b2bAddress.setStreetname(cloneAddress.getStreetname());
                b2bAddress.setStreetnumber(cloneAddress.getStreetnumber());
                b2bAddress.setPostalcode(cloneAddress.getPostalcode());
                b2bAddress.setTown(cloneAddress.getTown());
                b2bAddress.setCountry(cloneAddress.getCountry());
                b2bAddress.setRegion(cloneAddress.getRegion());
                b2bAddress.setFax(cloneAddress.getStreetnumber());
                b2bAddress.setPhone1(cloneAddress.getPhone1());
                if (Sapcrmcustomerb2bConstants.RELATION_ALL.equals(addressUsage)) {
                    b2bAddress.setShippingAddress(cloneAddress.getShippingAddress());
                    b2bAddress.setBillingAddress(cloneAddress.getBillingAddress());
                }
                if (Sapcrmcustomerb2bConstants.RELATION_SHIP_TO.equals(addressUsage)) {
                    b2bAddress.setShippingAddress(cloneAddress.getShippingAddress());
                }
                if (Sapcrmcustomerb2bConstants.RELATION_BILL_TO.equals(addressUsage)) {
                    b2bAddress.setBillingAddress(cloneAddress.getBillingAddress());
                }

                b2bAddress.setDistrict(cloneAddress.getDistrict());
                b2bAddress.setPobox(cloneAddress.getPobox());
                b2bAddress.setCellphone(cloneAddress.getCellphone());
                LOG.debug("Updating Existing Address with Public Key: " + cloneAddress.getPublicKey());
                modelService.save(b2bAddress);
            }
        } catch (final Exception ex) {
            LOG.error("Error while updating existing Address with Public Key: " + cloneAddress.getPublicKey()
                    + " Exception is: " + ex);
        }
    }
}