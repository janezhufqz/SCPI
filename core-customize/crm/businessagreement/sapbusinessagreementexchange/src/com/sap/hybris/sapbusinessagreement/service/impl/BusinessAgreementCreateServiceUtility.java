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
package com.sap.hybris.sapbusinessagreement.service.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;

import java.util.UUID;

import com.sap.hybris.sapbusinessagreement.constants.SapbusinessagreementexchangeConstants;

/**
 *
 */
public class BusinessAgreementCreateServiceUtility {
    private PersistentKeyGenerator sapBusinessAgreementIdGenerator;
    private SAPGlobalConfigurationService sapGlobalConfigurationService;

    /**
     * @return the sapGlobalConfigurationService
     */
    public SAPGlobalConfigurationService getSapGlobalConfigurationService() {
        return sapGlobalConfigurationService;
    }

    /**
     * @param sapGlobalConfigurationService
     *            the sapGlobalConfigurationService to set
     */
    public void setSapGlobalConfigurationService(
            final SAPGlobalConfigurationService sapGlobalConfigurationService) {
        this.sapGlobalConfigurationService = sapGlobalConfigurationService;
    }

    /**
     * @return the sapBusinessAgreementIdGenerator
     */
    public PersistentKeyGenerator getSapBusinessAgreementIdGenerator() {
        return sapBusinessAgreementIdGenerator;
    }

    /**
     * @param sapBusinessAgreementIdGenerator
     *            the sapBusinessAgreementIdGenerator to set
     */
    public void setSapBusinessAgreementIdGenerator(
            final PersistentKeyGenerator sapBusinessAgreementIdGenerator) {
        this.sapBusinessAgreementIdGenerator = sapBusinessAgreementIdGenerator;
    }

    public String generateGuid() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    public String generateBusinessAgreementId() {

        return (String) getSapBusinessAgreementIdGenerator().generate();
    }

    public PK getBusinessagreementclassPK() {
        if (getSapGlobalConfigurationService().getPropertyAccess(
                SapbusinessagreementexchangeConstants.BUSINESS_AGREEMENT_CLASS) != null) {
            return (PK) getSapGlobalConfigurationService()
                    .getPropertyAccess(
                            SapbusinessagreementexchangeConstants.BUSINESS_AGREEMENT_CLASS)
                    .getProperty(SapbusinessagreementexchangeConstants.PK);
        }
        return null;
    }

    public PK getTermOFPaymentPK() {

        if (getSapGlobalConfigurationService()
                .getPropertyAccess(
                        SapbusinessagreementexchangeConstants.BUSINESS_AGREEMENT_TERMS_OF_PAYMENT) != null) {
            return (PK) getSapGlobalConfigurationService()
                    .getPropertyAccess(
                            SapbusinessagreementexchangeConstants.BUSINESS_AGREEMENT_TERMS_OF_PAYMENT)
                    .getProperty(SapbusinessagreementexchangeConstants.PK);
        }
        return null;
    }

    public PK getPaymentMethodPK() {

        if (getSapGlobalConfigurationService()
                .getPropertyAccess(
                        SapbusinessagreementexchangeConstants.BUSINESS_AGREEMENT_PAYMENT_METHOD) != null) {
            return (PK) getSapGlobalConfigurationService()
                    .getPropertyAccess(
                            SapbusinessagreementexchangeConstants.BUSINESS_AGREEMENT_PAYMENT_METHOD)
                    .getProperty(SapbusinessagreementexchangeConstants.PK);
        }
        return null;
    }

}
