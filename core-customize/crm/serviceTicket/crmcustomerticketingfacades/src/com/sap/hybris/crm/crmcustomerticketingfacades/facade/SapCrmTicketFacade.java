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
package com.sap.hybris.crm.crmcustomerticketingfacades.facade;

import de.hybris.platform.customerticketingfacades.TicketFacade;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingfacades.data.TicketPriority;
import de.hybris.platform.customerticketingfacades.data.TicketRelatedObjectData;

import java.util.List;
import java.util.Map;

import com.sap.hybris.crm.crmcustomerticketingfacades.data.ComplaintAssociatedOrderData;
import com.sap.hybris.crm.crmcustomerticketingfacades.data.ComplaintAssociatedOrderEntryData;
import com.sap.hybris.crm.sapcrmticketsystem.model.CsTicketRelatedObjectModel;

/**
 * @author C5230711
 *
 */
public interface SapCrmTicketFacade extends TicketFacade {
    List<TicketPriority> getTicketPriorities();

    List<TicketRelatedObjectData> setQueryKeyParamToRelatedObject(final Map<String, String> queryMap);

    TicketData updateServiceOrderToTicket(TicketData ticketData);

    TicketData getWSTicket(String ticketId);

    TicketData createComplaint(TicketData complaintData);

    TicketData updateComplaint(TicketData complaintData);

    String getCategoryNameForCode(String categorryCode);

    List<CsTicketRelatedObjectModel> populateTicketRelatedObjectList(List<TicketRelatedObjectData> relObjectist);

    List<ComplaintAssociatedOrderData> getAssociatedOrders();

    List<ComplaintAssociatedOrderEntryData> getAssociatedOrderEntries(String orderCode);

    /**
     * Method to get tickets list for ibase.
     * 
     * @param iBaseID
     * @return list of ticketdata.
     */
    List<TicketData> getTicketsForIbase(String iBaseID);
}
