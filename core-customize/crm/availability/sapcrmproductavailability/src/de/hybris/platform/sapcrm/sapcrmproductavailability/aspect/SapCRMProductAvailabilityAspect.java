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
package de.hybris.platform.sapcrm.sapcrmproductavailability.aspect;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapproductavailability.converter.ConversionTools;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

/**
 * @author C5230711
 *
 */

public class SapCRMProductAvailabilityAspect {
    private static final Logger LOGGER = Logger.getLogger(SapCRMProductAvailabilityAspect.class.getName());
    private UserService userService;
    
    private static final int CUSTOMER_ID_LENGTH = 10;

    @Pointcut
    public Object changeTheIncomingCustomerId(final ProceedingJoinPoint pjp) throws Throwable {

        final int customerIdParamIndexInOriginalMethod = 1;

        final Object[] args = pjp.getArgs();

        if (args.length > 1 && !StringUtils.isEmpty(args[customerIdParamIndexInOriginalMethod])) {
            String newCustomerId = getErpCustomerIdByCustomerId();
            if (!StringUtils.isEmpty(newCustomerId)) {
                newCustomerId = ConversionTools.addLeadingZerosToNumericID(newCustomerId, CUSTOMER_ID_LENGTH);
                args[customerIdParamIndexInOriginalMethod] = newCustomerId;
                LOGGER.debug(String.format("PointCut for Product Availability Changed CustomerID: "
                        + args[customerIdParamIndexInOriginalMethod]));
            }
        }

        return pjp.proceed(args);

    }

    protected String getErpCustomerIdByCustomerId() {
        String newCustomerId = null;
        if (!getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
            final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
            newCustomerId = currentCustomer != null ? currentCustomer.getErpCustomerId() : null;
        }

        return newCustomerId;
    }

    protected UserService getUserService() {
        return userService;
    }

    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

}