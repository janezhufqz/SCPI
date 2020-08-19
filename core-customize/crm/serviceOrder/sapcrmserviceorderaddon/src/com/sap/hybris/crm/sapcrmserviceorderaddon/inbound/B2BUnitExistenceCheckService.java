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

import javax.annotation.Resource;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import org.apache.log4j.Logger;

public class B2BUnitExistenceCheckService {
    private static final Logger LOG = Logger.getLogger(B2BUnitExistenceCheckService.class.getName());
    private FlexibleSearchService flexibleSearchService;
    @Resource
    ModelService modelService;

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

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

    public Object readB2BUnit(final String b2bUnitId) {
        final FlexibleSearchQuery flexibleSearchQuery = getB2BUnitFlexibleSearchQuery(b2bUnitId);
        try {
            return modelService.getSource(this.flexibleSearchService.searchUnique(flexibleSearchQuery));
        } catch (final Exception e) {
            LOG.debug("B2B extension not included, Service order replication will ignore the b2b unit information..", e);
            return null;
        }
    }

    protected FlexibleSearchQuery getB2BUnitFlexibleSearchQuery(final String b2bUnitId) {
        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
                "SELECT {c:pk} FROM {B2BUnit AS c} WHERE  {c.UID} = ?b2bUnitId");
        flexibleSearchQuery.addQueryParameter("b2bUnitId", b2bUnitId);
        return flexibleSearchQuery;
    }
}
