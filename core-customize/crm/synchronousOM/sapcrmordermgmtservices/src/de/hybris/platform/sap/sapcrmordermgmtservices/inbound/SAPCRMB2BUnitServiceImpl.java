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
package de.hybris.platform.sap.sapcrmordermgmtservices.inbound;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BUnitService;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sap.hybris.sapcrmcustomerb2b.service.DefaultSAPCRMB2BUnitService;

/**
 * Enhancement of DefaultB2BUnitService to determine the sales area dependent
 * B2BUnit
 *
 */
public class SAPCRMB2BUnitServiceImpl extends DefaultSAPCRMB2BUnitService {
    private final String BACKEND_TYEP_CRM = "CRM";

    @Autowired
    private DefaultB2BUnitService defaultB2BUnitService;

    private BaseStoreService baseStoreService;

    private static final Logger LOGGER = Logger.getLogger(SAPCRMB2BUnitServiceImpl.class);

    @Override
    public B2BUnitModel getParent(final B2BCustomerModel b2bCustomer) {
        // retrieve parent B2BUnit(sales area)
        final B2BUnitModel parentB2BUnit = defaultB2BUnitService.getParent(b2bCustomer);
        String backendType = getBaseStoreServiceFromCurrentBaseStore();

        if (!backendType.equals(BACKEND_TYEP_CRM)) {
            return super.getParent(b2bCustomer);
        }
        if (parentB2BUnit != null) {
            final String parentB2BUnitUID = parentB2BUnit.getUid();
            final String salesAreaSuffix = getSalesAreaSuffix();
            if (salesAreaSuffix.isEmpty()) {
                log("No SAP Base Store Configuration with sale area definition assigned to current Base Store! Returns sales area independent B2B Unit with UID: ["
                        + parentB2BUnitUID + "]");
                return parentB2BUnit;
            } else {
                // search for sales area dependent B2BUnit in members of parent
                // B2BUnit
                final Set<PrincipalModel> parentB2BUnitMembers = parentB2BUnit.getMembers();
                if (parentB2BUnitMembers != null) {
                    final String salesAreaDependentB2BUnitUID = parentB2BUnitUID + salesAreaSuffix;
                    log("Combined Sales Area::::" + salesAreaDependentB2BUnitUID);
                    for (final PrincipalModel parentB2BUnitMember : parentB2BUnitMembers) {
                        // check whether the member is a B2BUnitModel and the
                        // UID is equal to sales area dependent B2BUnit UID
                        if (parentB2BUnitMember instanceof B2BUnitModel
                                && parentB2BUnitMember.getUid().equalsIgnoreCase(salesAreaDependentB2BUnitUID)) {
                            log("Returns sales area dependent B2B Unit with UID: [" + parentB2BUnitMember.getUid()
                                    + "]");
                            return (B2BUnitModel) parentB2BUnitMember;
                        }
                    }
                }
                log("No sales area dependent B2B Unit with UID: [" + parentB2BUnitUID + salesAreaSuffix
                        + "] found! Returns sales area independent B2B Unit with UID: [" + parentB2BUnitUID + "]");
            }
        }
        return parentB2BUnit;
    }

    protected String getBaseStoreServiceFromCurrentBaseStore() {
        String backendType="";
        if (baseStoreService.getCurrentBaseStore() != null) {
            backendType = baseStoreService.getCurrentBaseStore().getSAPConfiguration().getSAPRFCDestination()
                    .getBackendType().getCode();
        }
        return backendType;
    }

    protected void log(final String str) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(str);
        }
    }

    @Override
    protected String getSalesAreaSuffix() {
        String salesAreaSuffix = "";

        // retrieve current base store
        final BaseStoreModel currentBaseStore = baseStoreService.getCurrentBaseStore();
        if (currentBaseStore != null && currentBaseStore.getSAPConfiguration() != null) {
            // SALES ORG
            final String salesOrganization = currentBaseStore.getSAPConfiguration().getSapcommon_salesOrganization();
            // DISTRIBUTION CHANNEL
            String distributionChannel = currentBaseStore.getSAPConfiguration().getSapcommon_distributionChannel();
            // DIVISION
            String division = currentBaseStore.getSAPConfiguration().getSapcommon_division();
            // check whether no part of sales area is blank
            if (!StringUtils.isBlank(salesOrganization) && !StringUtils.isBlank(distributionChannel)) {
                // retrieve common distribution channel
                distributionChannel = getCommonDistributionChannel(salesOrganization, distributionChannel);
                // retrieve common division
                if (!StringUtils.isBlank(division)) {
                    division = getCommonDivision(salesOrganization, division);
                    salesAreaSuffix = "_" + salesOrganization + "_" + distributionChannel + "_" + division;
                } else {
                    salesAreaSuffix = "_" + salesOrganization + "_" + distributionChannel;
                }
                // concatenate sales area suffix
            }
        }
        return salesAreaSuffix;
    }

    /**
     * @param defaultB2BUnitService
     *            the defaultB2BUnitService to set
     */
    public void setDefaultB2BUnitService(final DefaultB2BUnitService defaultB2BUnitService) {
        this.defaultB2BUnitService = defaultB2BUnitService;
    }

    /**
     * Returns the BaseStoreService
     *
     * @return BaseStoreService
     */
    @Override
    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    /**
     * Sets the BaseStoreService
     *
     * @param baseStoreService
     */
    @Override
    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }
}
