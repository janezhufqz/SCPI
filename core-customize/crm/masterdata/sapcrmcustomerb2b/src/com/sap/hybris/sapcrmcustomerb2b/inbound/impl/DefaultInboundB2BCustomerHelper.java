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
package com.sap.hybris.sapcrmcustomerb2b.inbound.impl;

import static com.sap.hybris.sapcrmcustomerb2b.constants.Sapcrmcustomerb2bConstants.DEFAULTB2BUNIT;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;

import com.sap.hybris.sapcrmcustomerb2b.inbound.InboundB2BCustomerHelper;

public class DefaultInboundB2BCustomerHelper implements InboundB2BCustomerHelper {

    B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;

    @Override
    public String getB2BUnitForCustomer(String b2bCustomerId) {
        String b2bUnitId=null;
        B2BCustomerModel b2bCustomerModel = this.getB2bCustomerService().getUserForUID(b2bCustomerId);
        if (b2bCustomerModel != null && b2bCustomerModel.getDefaultB2BUnit()!=null) {
            b2bUnitId=b2bCustomerModel.getDefaultB2BUnit().getUid();
        }
        else {
            b2bUnitId=DEFAULTB2BUNIT;
        }
        return b2bUnitId;
    }

    /**
     * @return the b2bCustomerService
     */
    public B2BCustomerService<B2BCustomerModel, B2BUnitModel> getB2bCustomerService() {
        return b2bCustomerService;
    }

    /**
     * @param b2bCustomerService
     *            the b2bCustomerService to set
     */
    public void setB2bCustomerService(B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService) {
        this.b2bCustomerService = b2bCustomerService;
    }

}
