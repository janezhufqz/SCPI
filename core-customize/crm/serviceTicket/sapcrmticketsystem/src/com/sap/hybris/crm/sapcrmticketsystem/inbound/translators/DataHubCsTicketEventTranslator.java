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

package com.sap.hybris.crm.sapcrmticketsystem.inbound.translators;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.crm.sapcrmticketsystem.constants.SapcrmticketsystemConstants;
import com.sap.hybris.crm.sapcrmticketsystem.inbound.DataHubInboundCsTicketEventHelper;

/**
 * This class translates the ticket event .
 *
 * @author C5229484
 *
 */
public class DataHubCsTicketEventTranslator extends DataHubTranslator<DataHubInboundCsTicketEventHelper> {

    /**
     * Logger for class
     */
    private static final Logger LOG = LoggerFactory.getLogger(DataHubCsTicketEventTranslator.class);

    @SuppressWarnings("javadoc")
    public static final String HELPER_BEAN = "sapDataHubInboundCsTicketEventHelper";

    @SuppressWarnings("javadoc")
    public DataHubCsTicketEventTranslator() {
        super(HELPER_BEAN);
    }

    /**
     * This method intercepts the ticket import by getting the ticket attributes
     * to trigger the ticket change event in Hybris.
     */
    @Override
    public void performImport(final String resolution, final Item processedItem) throws ImpExException {
        LOG.debug("DataHubCsTicketEventTranslator: Invoked translator to create a new change event for ticket ");

        if (resolution != null && !resolution.equals(SapcrmticketsystemConstants.IGNORE)) {
            try {
                final String ticketID = (String) processedItem.getAttribute(SapcrmticketsystemConstants.UID);
                final String ticketHeadline = (String) processedItem.getAttribute(SapcrmticketsystemConstants.TITLE);
                final EnumerationValue priority = (EnumerationValue) processedItem
                        .getAttribute(SapcrmticketsystemConstants.PRIORITY);
                final EnumerationValue category = (EnumerationValue) processedItem
                        .getAttribute(SapcrmticketsystemConstants.TICKET_CATEGORY);
                getInboundHelper().createTicketChangeEvent(resolution, ticketID, (null == priority) ? CsTicketPriority.MEDIUM : CsTicketPriority.valueOf(priority.getCode()),
                  		  (null == category) ? CsTicketCategory.COMPLAINT : CsTicketCategory.valueOf(category.getCode()), ticketHeadline);
               

            } catch (final JaloInvalidParameterException e) {
                LOG.error("Exception occured while translating comments.Reason", e);
            } catch (final JaloSecurityException e) {
                LOG.error("DataHubCsTicketEventTranslator: Exception occured while translating comments.Reason", e);
            }

        }
    }
}
