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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapcrmcustomerb2b.util.SAPCRMAddressReplicationUtil;
import com.sap.hybris.sapcrmcustomerb2b.constants.Sapcrmcustomerb2bConstants;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.SAPCRMB2BRelationsModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.exceptions.FlexibleSearchException;

/**
 * @author C5229493
 *
 *         Replicate Address From One B2B Unit to Related Sales Area Units
 */
public class SAPCRMB2BUnitAddressConsistencyInterceptor implements PrepareInterceptor<AddressModel> {
    private static final String QUERY = "select pk from {SAPCRMB2BRelations}  where {source} =?sourceuid";
    private static final Logger LOG = LoggerFactory.getLogger(SAPCRMB2BUnitAddressConsistencyInterceptor.class);

    private ModelService modelService;
    private FlexibleSearchService flexibleSearchService;

    @Override
    public void onPrepare(final AddressModel address, final InterceptorContext context) throws InterceptorException {
        // check whether the SAP CRM customer ID is not null
        if (address.getSapCustomerID() != null && address.getDuplicate().equals(Boolean.FALSE)) {
            // Determine UID of Owner of Address
            final String b2bUnitUid = getUIDOfAddressOwner(address);
            // check whether SAP customer ID is equal to UID of address owner
            if (b2bUnitUid != null && b2bUnitUid.equals(address.getSapCustomerID())) {
                final List<SAPCRMB2BRelationsModel> b2bRelations = getTargetB2BUnits(b2bUnitUid);
                // Check Whether B2B Relation Present or not for B2B Unit
                if (b2bRelations != null && !b2bRelations.isEmpty()) {
                    replicateAddresses(b2bRelations, address);
                }
            }
        }

    }

    /**
     * @param b2bRelations
     * @param address
     *            Replicate Address to Different B2BUnits based on Relations b/w
     *            them
     */
    protected void replicateAddresses(final List<SAPCRMB2BRelationsModel> b2bRelations, final AddressModel address) {
        final SAPCRMAddressReplicationUtil sapCRMAddressReplicationUtil = getSAPCRMAddressReplicationUtil();
        for (final SAPCRMB2BRelationsModel b2brelation : b2bRelations) {
            final B2BUnitModel b2bunit = getB2BUnitForRelation(b2brelation);
            if (b2bunit != null) {
                boolean isShiptoParty = false;
                boolean isBilltoParty = false;
                final String addressUsage = b2brelation.getAddressUsage();
                if (Sapcrmcustomerb2bConstants.RELATION_ALL.equals(addressUsage)) {
                    isShiptoParty = true;
                    isBilltoParty = true;
                }
                if (Sapcrmcustomerb2bConstants.RELATION_SHIP_TO.equals(addressUsage)) {
                    isShiptoParty = true;
                }
                if (Sapcrmcustomerb2bConstants.RELATION_BILL_TO.equals(addressUsage)) {
                    isBilltoParty = true;
                }
                final AddressModel cloneAddress = getModelService().clone(address);
                cloneAddress.setOwner(b2bunit);
                String publicKey = cloneAddress.getPublicKey();
                final int index = publicKey.indexOf("|");
                if (index >= 0) {
                    publicKey = b2bunit.getUid() + publicKey.substring(index);
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
                    LOG.debug("Creating New Address For B2BUnit with UID: " + b2bunit.getUid() + " With Public Key"
                            + publicKey);
                    getModelService().save(cloneAddress);
                }
            }
        }
    }

    /**
     * @param sourceUid
     * @return List Of Relations with other B2BUnits to -do
     */
    protected List<SAPCRMB2BRelationsModel> getTargetB2BUnits(final String sourceUid) {

        final FlexibleSearchQuery fsQuery = getFlexibleSearchQuery();
        fsQuery.addQueryParameter("sourceuid", sourceUid);
        List<SAPCRMB2BRelationsModel> b2bUnits = null;

        try {
            final SearchResult<SAPCRMB2BRelationsModel> searchResult = getFlexibleSearchService().search(fsQuery);
            if (searchResult != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Search Result: found " + searchResult.getCount() + " B2BUnits");
                }
                b2bUnits = searchResult.getResult();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Search Result: nothing found");
                }
            }

        } catch (final FlexibleSearchException fe) {
            LOG.error("Error in Query Execution " + fsQuery.getQuery() + " Exception is " + fe);
        }
        return b2bUnits;

    }

    /**
     * @param b2brelation
     * @return Target B2BUnit For Relation to-do
     */
    protected B2BUnitModel getB2BUnitForRelation(final SAPCRMB2BRelationsModel b2brelation) {
        final B2BUnitModel b2bUnitExample = new B2BUnitModel();
        b2bUnitExample.setUid(b2brelation.getTarget());
        b2bUnitExample.setBuyer(Boolean.TRUE);
        B2BUnitModel b2bunit = null;
        try {
            b2bunit = getFlexibleSearchService().getModelByExample(b2bUnitExample);
        } catch (final ModelNotFoundException me) {
            LOG.warn("No B2B Unit Found With Id: " + b2brelation.getTarget() + " Exception is :" + me);
        }
        return b2bunit;
    }

    /**
     * @param address
     * @return uid of owner
     */
    protected String getUIDOfAddressOwner(final AddressModel address) {
        String uid = null;
        if (address.getOwner() instanceof UserModel) {
            uid = ((UserModel) address.getOwner()).getUid();
        } else if (address.getOwner() instanceof B2BUnitModel) {
            uid = ((B2BUnitModel) address.getOwner()).getUid();
        }
        return uid;
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

    public SAPCRMAddressReplicationUtil getSAPCRMAddressReplicationUtil() {
        return new SAPCRMAddressReplicationUtil();
    }
}
