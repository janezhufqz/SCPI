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
package com.sap.hybris.crm.crmcustomerticketingaddon.integration;

import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingfacades.data.TicketRelatedObjectData;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.fest.util.Collections;

import com.sap.hybris.crm.crmcustomerticketingaddon.constants.CrmcustomerticketingaddonConstants;
import com.sap.hybris.crm.crmcustomerticketingaddon.data.RelatedObjectData;
import com.sap.hybris.crm.crmcustomerticketingfacades.facade.SapCrmTicketFacade;
import com.sap.security.core.server.csi.XSSEncoder;

/**
 * @author C5230711
 *
 */
public class ServiceTicketLinkManager {

    private static final String OBJECT_TYPE = "objectType";
    private static final String OBJECTID = "objectid";
    private static final String TICKETID = "ticketid";
    @Resource(name = "defaultTicketFacade")
    private SapCrmTicketFacade ticketFacade;
    private static final Logger LOG = Logger.getLogger(ServiceTicketLinkManager.class);

    public void serviceTicketLink(final String serviceTicketLink) throws UnsupportedEncodingException {

        final RelatedObjectData relatedObjectData = new RelatedObjectData();
        if (LOG.isDebugEnabled()) {
            LOG.debug("****************************The Transfered Message: " + serviceTicketLink);
        }

        parseStringToRelatedObject(serviceTicketLink, relatedObjectData);
        final String ticketId = relatedObjectData.getTicketid();
        final String objectId = relatedObjectData.getObjectid();
        final String objectType = relatedObjectData.getObjectType();
        final TicketData ticketData = ticketFacade.getWSTicket(XSSEncoder.encodeHTML(ticketId));

        if (ticketData != null) {
            final Map<String, String> queryMap = new HashMap<String, String>();
            queryMap.put(objectType, objectId);

            final List<TicketRelatedObjectData> relatedObjList = ticketFacade.setQueryKeyParamToRelatedObject(queryMap);
            if (!Collections.isEmpty(relatedObjList)) {
                if (Collections.isEmpty(ticketData.getRelatedObjects())) {
                    ticketData.setRelatedObjects(relatedObjList);
                } else {
                    checkifObjectAlreadyAvailableAndAdd(ticketData, relatedObjList);
                }

                ticketFacade.updateServiceOrderToTicket(ticketData);
            }

        }

    }

    /**
     * @param serviceTicketLink
     * @param relatedObjectData
     */
    protected void parseStringToRelatedObject(final String serviceTicketLink,
            final RelatedObjectData relatedObjectData) {
        if (!StringUtils.isEmpty(serviceTicketLink)) {
            final String[] strTemp = serviceTicketLink.split(CrmcustomerticketingaddonConstants.PIPE_SEPERATOR);
            for (int i = 0; i < strTemp.length; i++) {

                final String[] tempObj = strTemp[i].split(CrmcustomerticketingaddonConstants.COLON_SEPERATOR);
                if (TICKETID.equalsIgnoreCase(tempObj[0])) {
                    relatedObjectData.setTicketid(tempObj[1]);
                }
                if (OBJECTID.equalsIgnoreCase(tempObj[0])) {
                    relatedObjectData.setObjectid(tempObj[1]);
                }
                if (OBJECT_TYPE.equalsIgnoreCase(tempObj[0])) {
                    relatedObjectData.setObjectType(tempObj[1]);
                }

            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("****************************The Transfered Message: " + relatedObjectData.getTicketid());
            }
        }

    }

    /**
     * To Verify If the object is already present when there is a secondtime
     * update
     */
    public void checkifObjectAlreadyAvailableAndAdd(final TicketData ticketData,
            final List<TicketRelatedObjectData> relatedObjList) {
        final List<TicketRelatedObjectData> ticketRelObjList = ticketData.getRelatedObjects();
        final Set<String> ticketRelSet = new HashSet<String>();

        final Iterator iterator = ticketRelObjList.iterator();
        while (iterator.hasNext()) {
            final TicketRelatedObjectData ticketRelatedObjectData = (TicketRelatedObjectData) iterator.next();
            ticketRelSet.add(ticketRelatedObjectData.getObjectId());
        }

        final Iterator itr = relatedObjList.iterator();
        while (itr.hasNext()) {

            final TicketRelatedObjectData ticketRelatedObjectData = (TicketRelatedObjectData) itr.next();
            if (!ticketRelSet.contains(ticketRelatedObjectData.getObjectId())) {
                ticketData.getRelatedObjects().add(ticketRelatedObjectData);
            }

        }
    }

}
