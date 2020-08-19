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
package com.sap.hybris.sapcrmcustomerb2b.interceptors;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapcrmcustomerb2b.util.SAPCRMAddressReplicationUtil;
import com.sap.hybris.sapcrmcustomerb2b.constants.Sapcrmcustomerb2bConstants;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.SAPCRMB2BRelationsModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * @author C5229493
 *
 *         Replicate Address From Source B2B Unit to Target B2BUnit Of Relation
 */
public class SAPCRMB2BRelationAddressConsistencyInterceptor implements PrepareInterceptor<SAPCRMB2BRelationsModel> {
    private static final Logger LOG = LoggerFactory.getLogger(SAPCRMB2BRelationAddressConsistencyInterceptor.class);

    private ModelService modelService;
    private FlexibleSearchService flexibleSearchService;

    @Override
    public void onPrepare(final SAPCRMB2BRelationsModel b2bRelationModel, final InterceptorContext arg1)
            throws InterceptorException {
        // Only Applicable for Relation Between two B2bunits
        if (!Sapcrmcustomerb2bConstants.RELATION_ALL.equals(b2bRelationModel.getAddressUsage())) {
            // Getting Source B2BUnit
            final B2BUnitModel sourceB2BUnit = getB2BUnit(b2bRelationModel.getSource());
            // Getting Target B2BUnit
            final B2BUnitModel targetB2BUnit = getB2BUnit(b2bRelationModel.getTarget());
            // Checking whether Source and Target B2BUnit has been replicated or
            // not
            if (sourceB2BUnit != null && targetB2BUnit != null) {
                final String addressUsage = b2bRelationModel.getAddressUsage();
                replicateAddresses(sourceB2BUnit, targetB2BUnit, addressUsage);
            }

        }

    }

    protected void replicateAddresses(final B2BUnitModel sourceB2BUnit, final B2BUnitModel targetB2BUnit,
            final String addressUsage) {
        final Collection<AddressModel> addresses = sourceB2BUnit.getAddresses();

        boolean isShiptoParty = false;
        boolean isBilltoParty = false;
        final SAPCRMAddressReplicationUtil sapCRMAddressReplicationUtil = getSAPCRMAddressReplicationUtil();
        if (Sapcrmcustomerb2bConstants.RELATION_SHIP_TO.equals(addressUsage)) {
            isShiptoParty = true;
        } else if (Sapcrmcustomerb2bConstants.RELATION_BILL_TO.equals(addressUsage)) {
            isBilltoParty = true;
        }
        for (final AddressModel address : addresses) {
            final AddressModel cloneAddress = getModelService().clone(address);
            cloneAddress.setOwner(targetB2BUnit);
            String publicKey = cloneAddress.getPublicKey();
            if (publicKey.contains("|")) {
                publicKey = targetB2BUnit.getUid() + publicKey.substring(publicKey.indexOf("|"));
            }
            cloneAddress.setPublicKey(publicKey);
            cloneAddress.setBillingAddress(Boolean.valueOf(isBilltoParty));
            cloneAddress.setShippingAddress(Boolean.valueOf(isShiptoParty));
            final List<AddressModel> b2bAddresses = sapCRMAddressReplicationUtil.addressesPresent(publicKey,
                    getFlexibleSearchService());
            if (b2bAddresses != null && !b2bAddresses.isEmpty()) {
                sapCRMAddressReplicationUtil.updateExistingAddresses(b2bAddresses, cloneAddress, addressUsage,
                        getModelService());
            } else {
                LOG.debug("Creating New Address For B2BUnit with UID: " + targetB2BUnit.getUid() + " With Public Key: "
                        + publicKey);
                getModelService().save(cloneAddress);
            }
        }
    }

    /**
     * @param uId
     * @return B2B Unit For Particular uId
     */
    protected B2BUnitModel getB2BUnit(final String uId) {
        final B2BUnitModel b2bUnitExample = new B2BUnitModel();
        b2bUnitExample.setUid(uId);
        b2bUnitExample.setBuyer(Boolean.TRUE);
        B2BUnitModel b2bunit = null;
        try {
            b2bunit = getFlexibleSearchService().getModelByExample(b2bUnitExample);
        } catch (final ModelNotFoundException me) {
            LOG.warn("No Source B2B Unit Found With Id: " + uId + " Exception is: " + me);
        }
        return b2bunit;

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

    public SAPCRMAddressReplicationUtil getSAPCRMAddressReplicationUtil() {
        return new SAPCRMAddressReplicationUtil();
    }

}
