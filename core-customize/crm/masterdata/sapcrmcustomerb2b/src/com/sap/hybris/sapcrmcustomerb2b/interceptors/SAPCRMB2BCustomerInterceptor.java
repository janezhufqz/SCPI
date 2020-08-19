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
package com.sap.hybris.sapcrmcustomerb2b.interceptors;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

/**
 * @author C5229471
 *
 */
public class SAPCRMB2BCustomerInterceptor implements PrepareInterceptor<B2BCustomerModel> {

    /*
     * (non-Javadoc)
     *
     * @see
     * de.hybris.platform.servicelayer.interceptor.PrepareInterceptor#onPrepare(
     * java.lang.Object,
     * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
     */
    @Override
    public void onPrepare(final B2BCustomerModel b2bCustomerModel, final InterceptorContext interceptorContext)
            throws InterceptorException {
        if (b2bCustomerModel.getErpCustomerId() == null
                || interceptorContext.isModified(b2bCustomerModel, B2BCustomerModel.DEFAULTB2BUNIT)) {
            final B2BUnitModel b2bUnit = b2bCustomerModel.getDefaultB2BUnit();
            if (b2bUnit != null) {
                b2bCustomerModel.setErpCustomerId(b2bUnit.getErpcustomerid());
            }
        }
    }

}
