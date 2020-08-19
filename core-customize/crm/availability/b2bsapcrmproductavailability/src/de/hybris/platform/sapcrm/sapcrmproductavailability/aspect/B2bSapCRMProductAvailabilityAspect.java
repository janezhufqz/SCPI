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
package de.hybris.platform.sapcrm.sapcrmproductavailability.aspect;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.site.BaseSiteService;

import org.springframework.beans.factory.annotation.Required;

/**
 *
 */
public class B2bSapCRMProductAvailabilityAspect extends SapCRMProductAvailabilityAspect {
    private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
    private BaseSiteService baseSiteService;

    @Override
    protected String getErpCustomerIdByCustomerId() {

        if (SiteChannel.B2B.equals(getBaseSiteService().getCurrentBaseSite().getChannel())) {

            final B2BUnitModel root = determineB2BUnitOfCurrentB2BCustomer();

            if (root != null) {
                return root.getErpcustomerid();
            }

            return b2bCustomerService.getCurrentB2BCustomer() != null
                    ? b2bCustomerService.getCurrentB2BCustomer().getErpCustomerId() : null;

        } else {
            return super.getErpCustomerIdByCustomerId();
        }
    }

    protected B2BUnitModel determineB2BUnitOfCurrentB2BCustomer() {

        final B2BCustomerModel b2bCustomer = b2bCustomerService.getCurrentB2BCustomer();
        final B2BUnitModel parent = b2bUnitService.getParent(b2bCustomer);
        return b2bUnitService.getRootUnit(parent);

    }

    protected B2BCustomerService<B2BCustomerModel, B2BUnitModel> getB2bCustomerService() {
        return b2bCustomerService;
    }

    @Required
    public void setB2bCustomerService(final B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService) {
        this.b2bCustomerService = b2bCustomerService;
    }

    protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService() {
        return b2bUnitService;
    }

    @Required
    public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService) {
        this.b2bUnitService = b2bUnitService;
    }

    protected BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Required
    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }
}
