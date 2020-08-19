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
package com.sap.hybris.crm.sapcrmibasefacades.ibase.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.sap.hybris.crm.sapcrmibasecore.model.IBaseModel;
import com.sap.hybris.crm.sapcrmibasefacades.data.IBaseData;
import com.sap.hybris.crm.sapcrmibasefacades.data.IBaseDetailData;
import com.sap.hybris.crm.sapcrmibasefacades.ibase.IBaseFacade;
import com.sap.hybris.crm.sapcrmibaseservices.ibase.IBaseService;

import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * @author C5242879
 *
 */
public class DefaultIBaseFacade implements IBaseFacade {

    @Autowired
    private IBaseService iBaseService;

    @Autowired
    private Converter<IBaseModel, IBaseData> iBaseDataConverter;

    @Autowired
    private Converter<IBaseModel, IBaseDetailData> iBaseDetailDataConverter;

    /*
     * (non-Javadoc)
     *
     * @see com.sap.hybris.crm.sapcrmibaseaddon.facades.IBaseFacade#getIBases()
     */
    @Override
    public List<IBaseData> getInstalledBases() {
        final List<IBaseModel> ibases = iBaseService.getInstalledBases();
        return Converters.convertAll(ibases, iBaseDataConverter);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sap.hybris.crm.sapcrmibaseaddon.facades.IBaseFacade#
     * getInstalledBaseById(java.lang.String)
     */
    @Override
    public IBaseDetailData getInstalledBaseById(final String iBaseID) {
        final IBaseModel iBaseModel = iBaseService.getInstalledBaseById(iBaseID);
        IBaseDetailData iBaseData = null;
        if (null != iBaseModel) {
            iBaseData = iBaseDetailDataConverter.convert(iBaseModel);
        }
        return iBaseData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sap.hybris.crm.sapcrmibasefacades.ibase.IBaseFacade#
     * getInstalledBaseByParentGuid(java.lang.String)
     */
    @Override
    public IBaseDetailData getInstalledBaseByParentGuid(final String parentGuid) {
        final IBaseModel iBaseModel = iBaseService.getInstalledBaseByParentGuid(parentGuid);
        IBaseDetailData iBaseData = null;
        if (null != iBaseModel) {
            iBaseData = iBaseDetailDataConverter.convert(iBaseModel);
        }
        return iBaseData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sap.hybris.crm.sapcrmibasefacades.ibase.IBaseFacade#
     * getInstalledBaseByChildGuid(java.lang.String)
     */
    @Override
    public IBaseDetailData getInstalledBaseByChildGuid(final String childGuid) {
        final IBaseModel iBaseModel = iBaseService.getInstalledBaseByChildGuid(childGuid);
        IBaseDetailData iBaseData = null;
        if (null != iBaseModel) {
            iBaseData = iBaseDetailDataConverter.convert(iBaseModel);
        }
        return iBaseData;

    }
}
