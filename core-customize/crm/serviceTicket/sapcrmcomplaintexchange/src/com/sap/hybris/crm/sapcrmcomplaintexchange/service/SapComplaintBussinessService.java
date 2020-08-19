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

package com.sap.hybris.crm.sapcrmcomplaintexchange.service;

import java.util.Collection;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.ticket.enums.CsEventReason;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketException;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;

public interface SapComplaintBussinessService {

    CsTicketModel createComplaint(CsTicketParameter complaintParameter);

    CsTicketModel updateComplaint(CsTicketModel ticket) throws TicketException;

    CsCustomerEventModel addNoteComplaint(CsTicketModel ticket, CsInterventionType interventionType,
            CsEventReason reason, String note, Collection<MediaModel> attachments);

}
