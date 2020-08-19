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
package com.sap.hybris.crm.crmcustomerticketingaddon.controllers.pages;

import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
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
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.fest.util.Collections;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.sap.hybris.crm.crmcustomerticketingaddon.data.RelatedObjectData;
import com.sap.hybris.crm.crmcustomerticketingfacades.facade.SapCrmTicketFacade;
import com.sap.security.core.server.csi.XSSEncoder;

/**
 * @author C5230711
 *
 */
@Controller
@RequestMapping("/crm-ticket-relobj")
@ApiVersion("v2")
public class AccountSupportTicketWSController {
    @Resource(name = "defaultTicketFacade")
    private SapCrmTicketFacade ticketFacade;
    private static final Logger LOG = Logger.getLogger(AccountSupportTicketWSController.class);

    /**
     * Creates an Order Form
     *
     * @throws UnsupportedEncodingException
     */
    @ResponseStatus(value = HttpStatus.CREATED)
    @Secured("ROLE_ADMINGROUP")
    @RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE })
    public void linkServiceOrderToTicket(@RequestBody final RelatedObjectData relatedObjectData,
            final HttpServletRequest request) throws UnsupportedEncodingException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("****************************request.isUserInRole(ROLE_ADMINGROUP): "
                    + request.isUserInRole("ROLE_ADMINGROUP"));
        }
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
     * To Verify If the object is already present when there is a secondtime
     * update
     */
    public void checkifObjectAlreadyAvailableAndAdd(final TicketData ticketData,
            final List<TicketRelatedObjectData> relatedObjList) {
        final List<TicketRelatedObjectData> ticketRelObjList = ticketData.getRelatedObjects();
        final Set<String> ticketRelSet = new HashSet<String>();
        for (final Iterator iterator = ticketRelObjList.iterator(); iterator.hasNext();) {
            final TicketRelatedObjectData ticketRelatedObjectData = (TicketRelatedObjectData) iterator.next();
            ticketRelSet.add(ticketRelatedObjectData.getObjectId());
        }

        for (final Iterator iterator = relatedObjList.iterator(); iterator.hasNext();) {
            final TicketRelatedObjectData ticketRelatedObjectData = (TicketRelatedObjectData) iterator.next();
            if (!ticketRelSet.contains(ticketRelatedObjectData.getObjectId())) {
                ticketData.getRelatedObjects().add(ticketRelatedObjectData);
            }

        }

    }

}
