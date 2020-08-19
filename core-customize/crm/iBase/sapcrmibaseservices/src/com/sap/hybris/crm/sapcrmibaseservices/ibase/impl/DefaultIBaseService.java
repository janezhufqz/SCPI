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
package com.sap.hybris.crm.sapcrmibaseservices.ibase.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.sap.hybris.crm.sapcrmibasecore.model.IBaseModel;
import com.sap.hybris.crm.sapcrmibasecore.model.IBasePartnerModel;
import com.sap.hybris.crm.sapcrmibaseservices.dao.IBaseDao;
import com.sap.hybris.crm.sapcrmibaseservices.ibase.IBaseService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * @author C5242879
 *
 */
public class DefaultIBaseService implements IBaseService {

    @Autowired
    IBaseDao iBaseDao;

    @Autowired
    UserService userService;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sap.hybris.crm.sapcrmibaseaddon.services.IBaseService#getIBases()
     */
    @Override
    public List<IBaseModel> getInstalledBases() {
        final UserModel user = userService.getCurrentUser();

        String b2bUnitPK = null;

        List<IBaseModel> ibases = null;

        if (user instanceof B2BCustomerModel) {
            final B2BCustomerModel b2bCustomer = (B2BCustomerModel) user;
            final B2BUnitModel b2bUnitModel = b2bCustomer.getDefaultB2BUnit();
            if (null != b2bUnitModel) {
                b2bUnitPK = b2bUnitModel.getPk().toString();
                ibases = iBaseDao.findInstalledBases(b2bUnitPK);
            } else {
                ibases = Collections.emptyList();
            }
        }
        return ibases;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sap.hybris.crm.sapcrmibaseaddon.services.IBaseService#
     * getInstalledBaseById(java.lang.String)
     */
    @Override
    public IBaseModel getInstalledBaseById(final String iBaseID) {
        validateParameterNotNull(iBaseID, "IBase Id must not be null");
        return getIBaseForB2BUnit(iBaseDao.findIBaseById(iBaseID));
    }

    protected IBaseModel getIBaseForB2BUnit(IBaseModel ibase) {
        final UserModel user = userService.getCurrentUser();
        B2BUnitModel b2bUnitModel = null;
        if (user instanceof B2BCustomerModel) {
            final B2BCustomerModel b2bCustomer = (B2BCustomerModel) user;
            b2bUnitModel = b2bCustomer.getDefaultB2BUnit();
        }
        IBaseModel ibaseForB2Bunit = null;
        if (null != b2bUnitModel && null != ibase) {
            for (IBasePartnerModel partner : ibase.getPartners()) {
                if (partner.getB2bUnit().equals(b2bUnitModel)) {
                    ibaseForB2Bunit = ibase;
                    break;
                }
            }
        }
        return ibaseForB2Bunit;
    }

    @Override
    public IBaseModel getInstalledBaseByParentGuid(String parentGuid) {
        validateParameterNotNull(parentGuid, "Parent Guid must not be null");
        return getIBaseForB2BUnit(iBaseDao.getInstalledBaseByParentGuid(parentGuid));
    }

    @Override
    public IBaseModel getInstalledBaseByChildGuid(String childGuid) {
        validateParameterNotNull(childGuid, "Child Guid must not be null");
        return getIBaseForB2BUnit(iBaseDao.getInstalledBaseByChildGuid(childGuid));
    }

}
