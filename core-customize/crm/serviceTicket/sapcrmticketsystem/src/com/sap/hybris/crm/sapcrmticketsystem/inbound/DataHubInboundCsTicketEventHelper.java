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
package com.sap.hybris.crm.sapcrmticketsystem.inbound;

import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;

/**
 * Data Hub Inbound Helper for Ticket Comments and Event
 */
public interface DataHubInboundCsTicketEventHelper {

    /**
     * This method creates a new ticket change event for the ticket coming from
     * DataHub
     *
     * @param notes
     *            representing ticketNotes
     * @param ticketID
     *            ticket for which change event is to be processed
     * @param category
     * @param priority
     * @param ticketHeadline
     */
    void createTicketChangeEvent(String notes, String ticketID, CsTicketPriority priority, CsTicketCategory category,
            String ticketHeadline);

}
