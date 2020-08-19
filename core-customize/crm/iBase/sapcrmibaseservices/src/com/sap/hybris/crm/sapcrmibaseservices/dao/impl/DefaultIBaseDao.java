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
package com.sap.hybris.crm.sapcrmibaseservices.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.hybris.crm.sapcrmibasecore.model.IBaseComponentModel;
import com.sap.hybris.crm.sapcrmibasecore.model.IBaseModel;
import com.sap.hybris.crm.sapcrmibasecore.model.IBasePartnerModel;
import com.sap.hybris.crm.sapcrmibaseservices.dao.IBaseDao;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Default Data Access for looking up installed bases.
 *
 * @author C5242879
 *
 */
public class DefaultIBaseDao extends DefaultGenericDao<IBaseModel> implements IBaseDao {
    private static final Logger LOG = Logger.getLogger(DefaultIBaseDao.class);

    public static final String SELECT_CLAUSE = "SELECT {";
    public static final String FROM_CLAUSE = "} FROM  {";
    public static final String WHERE_CLAUSE = "} WHERE {";

    protected static final String FIND_IBASE_BY_ID = SELECT_CLAUSE + IBaseModel.PK + FROM_CLAUSE + IBaseModel._TYPECODE
            + WHERE_CLAUSE + IBaseModel.NUMBER + "} = ?number";

    protected static final String FIND_ALL_IBASES_BY_B2BUNIT = SELECT_CLAUSE + IBaseModel.PK + FROM_CLAUSE
            + IBaseModel._TYPECODE + WHERE_CLAUSE + IBaseModel._TYPECODE + "." + IBaseModel.PK + "} IN ({{ "
            + "SELECT DISTINCT{" + IBasePartnerModel._TYPECODE + "." + IBasePartnerModel.IBASE + "} FROM {"
            + IBasePartnerModel._TYPECODE + "}" + "WHERE {" + IBasePartnerModel._TYPECODE + "."
            + IBasePartnerModel.B2BUNIT + "} = ?b2bunit" + " }})";

    protected static final String FIND_IBASE_BY_PARENT_GUID = SELECT_CLAUSE + IBaseModel.PK + FROM_CLAUSE
            + IBaseModel._TYPECODE + WHERE_CLAUSE + IBaseModel.GUID + "} = ?parentGuid";

    protected static final String FIND_IBASE_BY_CHILD_GUID = "SELECT DISTINCT{" + IBaseComponentModel.IBASE
            + FROM_CLAUSE + IBaseComponentModel._TYPECODE + WHERE_CLAUSE + IBaseComponentModel.OBJECTGUID
            + "} = ?childGuid";

    public DefaultIBaseDao() {
        super(IBaseModel._TYPECODE);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sap.hybris.crm.sapcrmibaseaddon.dao.IBaseDao#findIBaseById(java.lang.
     * String)
     */
    @Override
    public IBaseModel findIBaseById(final String iBaseID) {
        if (LOG.isDebugEnabled()) {
            LOG.info("Searching for iBase with id " + iBaseID);
        }
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("number", iBaseID);

        final StringBuilder query = new StringBuilder(FIND_IBASE_BY_ID);

        return getFlexibleSearchService().searchUnique(new FlexibleSearchQuery(query.toString(), queryParams));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sap.hybris.crm.sapcrmibaseaddon.dao.IBaseDao#findInstalledBases()
     */
    @Override
    public List<IBaseModel> findInstalledBases(final String b2bUnitPK) {
        if (LOG.isDebugEnabled()) {
            LOG.info("Searching for iBases ");
        }

        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("b2bunit", b2bUnitPK);

        final StringBuilder query = new StringBuilder(FIND_ALL_IBASES_BY_B2BUNIT);

        final SearchResult<IBaseModel> results = getFlexibleSearchService()
                .<IBaseModel> search(new FlexibleSearchQuery(query.toString(), queryParams));

        if (LOG.isDebugEnabled()) {
            LOG.info("Results: " + (results == null ? "null" : String.valueOf(results.getCount())));
        }

        return results.getResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sap.hybris.crm.sapcrmibaseservices.dao.IBaseDao#
     * getInstalledBaseByParentGuid(java.lang.String)
     */
    @Override
    public IBaseModel getInstalledBaseByParentGuid(String parentGuid) {
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        StringBuilder query = null;
        queryParams.put("parentGuid", parentGuid);
        query = new StringBuilder(FIND_IBASE_BY_PARENT_GUID);
        return getFlexibleSearchService().searchUnique(new FlexibleSearchQuery(query.toString(), queryParams));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sap.hybris.crm.sapcrmibaseservices.dao.IBaseDao#
     * getInstalledBaseByChildGuid(java.lang.String)
     */
    @Override
    public IBaseModel getInstalledBaseByChildGuid(String childGuid) {
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        StringBuilder query = null;
        queryParams.put("childGuid", childGuid);
        query = new StringBuilder(FIND_IBASE_BY_CHILD_GUID);
        return getFlexibleSearchService().searchUnique(new FlexibleSearchQuery(query.toString(), queryParams));
    }

}
