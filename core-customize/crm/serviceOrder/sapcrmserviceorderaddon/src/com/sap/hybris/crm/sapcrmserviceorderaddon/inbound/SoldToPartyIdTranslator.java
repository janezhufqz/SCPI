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

package com.sap.hybris.crm.sapcrmserviceorderaddon.inbound;

import org.apache.log4j.Logger;

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * This class includes the translator for B2BUnit address deletion notification
 */
public class SoldToPartyIdTranslator extends AbstractSpecialValueTranslator {
    private static final Logger LOG = Logger.getLogger(SoldToPartyIdTranslator.class.getName());
    private FlexibleSearchService flexibleSearchService;
    private B2BUnitExistenceCheckService b2bUnitExistenceCheckService;

    /**
     * @return flexible Search Instance
     */
    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    /**
     * @param flexibleSearchService
     */
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    @Override
    public void init(final SpecialColumnDescriptor columnDescriptor) throws HeaderValidationException {
        if (b2bUnitExistenceCheckService == null) {
            b2bUnitExistenceCheckService = (B2BUnitExistenceCheckService) Registry.getApplicationContext().getBean(
                    "b2bUnitExistenceCheckService");
        }
    }

    @Override
    public void performImport(final String transformationExpression, final Item processedItem) throws ImpExException {
        final String businesspartnerId = transformationExpression;
        try {

            processedItem.setAttribute("Unit", b2bUnitExistenceCheckService.readB2BUnit(businesspartnerId));

        } catch (JaloInvalidParameterException | JaloBusinessException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Exception occurred", e);
            }
        }

    }

    private String getAttributeValue(final Item processedItem, final String attributeName) throws ImpExException {
        String attributeValue = null;
        try {
            final Object attributeObject = processedItem.getAttribute(attributeName);
            if (attributeObject != null) {
                attributeValue = attributeObject.toString();
            }
        } catch (final JaloSecurityException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Exception occurred", e);
            }
        }
        return attributeValue;
    }

    @Override
    public String performExport(final Item paramItem) throws ImpExException {
        return null;
    }

    @Override
    public boolean isEmpty(final String paramString) {
        return false;
    }

    public B2BUnitExistenceCheckService getB2bunitexistencecheckservice() {
        return b2bUnitExistenceCheckService;
    }

    public void setB2bunitexistencecheckservice(final B2BUnitExistenceCheckService b2bunitexistencecheckservice) {
        this.b2bUnitExistenceCheckService = b2bunitexistencecheckservice;
    }

}
