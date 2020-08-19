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
package com.sap.hybris.crm.sapcrmibaseaddon.aop;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Aspect for logging the entry and exit of method.
 * 
 * @author C5242879
 *
 */
@SuppressWarnings("squid:S1312")
public class SapcrmibaseLoggingAspect {
    /**
     * Invokes a method and log the entry and exit.
     * 
     * @param pjp
     * @return result of the invocation
     * @throws Throwable
     */
    public Object logEntryExit(final ProceedingJoinPoint pjp) throws Throwable // NOSONAR
    {
        Logger logger = Logger.getLogger(pjp.getTarget().getClass().getName());
        if (logger.isDebugEnabled()) {
            logger.debug("Entering method " + pjp.getSignature());
        }
        final Object result = pjp.proceed();
        if (logger.isDebugEnabled()) {
            logger.debug("Exiting method :" + pjp.getSignature());
        }
        return result;
    }
}
