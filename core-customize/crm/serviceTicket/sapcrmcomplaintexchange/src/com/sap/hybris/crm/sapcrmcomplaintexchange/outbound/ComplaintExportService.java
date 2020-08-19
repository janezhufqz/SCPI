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
package com.sap.hybris.crm.sapcrmcomplaintexchange.outbound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.hybris.datahub.core.rest.DataHubOutboundException;
import com.hybris.datahub.core.services.DataHubOutboundService;
import com.sap.hybris.crm.sapcrmcomplaintexchange.constants.SapcrmcomplaintexchangeConstants;
import com.sap.hybris.crm.sapcrmmodel.util.XSSFilterUtil;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.ticket.model.CsTicketModel;

/**
 * This class exports the CsTicket created to DataHub for Hybris - CRM
 * integration.The export is done in csv format.Hybris ticket is mapped to
 * RawHybrisServiceTicket in datahub.The target datahub is configured in
 * local.properties file.
 *
 * @author C5229484
 *
 */
public class ComplaintExportService {

    /**
     * Logger for class
     */
    private static final Logger LOG = LoggerFactory.getLogger(ComplaintExportService.class);

    /**
     * Datahub raw Ticket type
     */
    private static final String RAW_HYBRIS_COMPLAINT = "RawHybrisComplaint";

    /**
     * DataHubOutboundService to export data to data hub
     */
    @Autowired
    private DataHubOutboundService dataHubOutboundService;
    @Autowired
    private BaseStoreService baseStoreService;

    /**
     * Default feed to consider if no feed is specified
     */
    private String feed = "DEFAULT_FEED";

    /**
     * This method prepares ticket Model to the target map and send data to the
     * Data Hub.
     *
     * @param ticketModel
     *            ticket to be exported
     * @param notes
     *
     */
    public void sendComplaintData(final CsTicketModel ticketModel, final String notes)
            throws DataHubCommunicationException {
        final List<Map<String, Object>> target = new ArrayList<>();

        LOG.debug("In ComplaintExportService : Preparing ticket data with ID : " + ticketModel.getTicketID()
                + " to be sent to Data Hub ");
        prepareComplaintData(ticketModel, notes, target);
        try {
            sendTicketToDataHub(target);
            LOG.debug("In ComplaintExportService : Successfully sent ticket data with ID : " + ticketModel.getTicketID()
                    + " to Data Hub ");
        } catch (final DataHubCommunicationException e) {
            LOG.error("Error processing sending data to Data Hub. DataHubOutboundException: " + e);
        }

    }

    /**
     * Put the ticket details in the target map.The Map contains key value pair
     * where the key is ticket attribute and value corresponds to the that
     * attribute
     *
     * @param ticketModel
     *            ticket to export
     * @param notes
     * @param rawdata
     *            rawdata list of map
     */
    public void prepareComplaintData(final CsTicketModel ticketModel, final String notes,
            final List<Map<String, Object>> rawdata) {
        if (ticketModel.getAssociatedOrderEntries().isEmpty()) {
            final Map<String, Object> target = getTarget();
            populateBasicData(ticketModel, target, notes);
            rawdata.add(target);
        } else {
            for (final AbstractOrderEntryModel orderEntry : ticketModel.getAssociatedOrderEntries()) {
                final Map<String, Object> target = getTarget();
                populateBasicData(ticketModel, target, notes);
                if (null != orderEntry.getProduct()) {
                    target.put(SapcrmcomplaintexchangeConstants.PRODUCT_ID, orderEntry.getProduct().getCode());
                }
                target.put(SapcrmcomplaintexchangeConstants.QUANTITY, orderEntry.getQuantity());
                target.put(SapcrmcomplaintexchangeConstants.ITEM_NUMBER,
                        convertToCrmEntryNumber(orderEntry.getEntryNumber()));
                target.put(SapcrmcomplaintexchangeConstants.ITEM_UNIT, orderEntry.getUnit());
                rawdata.add(target);
            }
        }
    }

    /**
     * Method to populate data in target.
     */
    protected void populateBasicData(final CsTicketModel ticketModel, final Map<String, Object> target,
            final String notes) {
        final BaseStoreModel baseStore = this.baseStoreService.getCurrentBaseStore();
        final String baseStoreUid = baseStore != null ? baseStore.getUid() : null;
        target.put(SapcrmcomplaintexchangeConstants.UID, ticketModel.getTicketID());
        target.put(SapcrmcomplaintexchangeConstants.TITLE, XSSFilterUtil.filter(ticketModel.getHeadline()));
        target.put(SapcrmcomplaintexchangeConstants.COMPLAINT_REPLICATED,
                Boolean.valueOf(ticketModel.isIsTicketReplicated()));
        target.put(SapcrmcomplaintexchangeConstants.NOTES, notes);
        target.put(SapcrmcomplaintexchangeConstants.BASE_STORE, baseStoreUid);
        setCustomerBasedOnB2BorB2C(ticketModel.getCustomer(), target);
        if (ticketModel.getReasonCategory() != null) {
            target.put(SapcrmcomplaintexchangeConstants.COMPLAINT_CATEGORY, ticketModel.getReasonCategory().getCode());
        }
        if (ticketModel.getOrder() != null) {
            target.put(SapcrmcomplaintexchangeConstants.ORDER_ID, ticketModel.getOrder().getCode());
        }

        target.put(SapcrmcomplaintexchangeConstants.PRIORITY, ticketModel.getPriority());
        target.put(SapcrmcomplaintexchangeConstants.STATE, ticketModel.getState());

    }

    /**
     * @param userModel
     * @param target
     */
    protected void setCustomerBasedOnB2BorB2C(final UserModel userModel, final Map<String, Object> target) {
        if (userModel != null) {
        	final CustomerModel customer = (CustomerModel) userModel;
        	if (null != customer.getSapConsumerID())
        	{
                target.put(SapcrmcomplaintexchangeConstants.CUSTOMER_ID, customer.getCustomerID());
            }

        }

    }

    /**
     * Method to convert to entry number for crm
     */
    protected Integer convertToCrmEntryNumber(final Integer number) {
        return number.intValue() + 1;
    }

    /**
     * This method establishes connection to the datahub and send the target Map
     * with ticket data to the datahub.
     *
     * @param target
     *            ticket data to sent to datahub
     * @throws DataHubCommunicationException
     */
    protected void sendTicketToDataHub(final List<Map<String, Object>> target) throws DataHubCommunicationException {
        final String errorMsg = "Error processing sending data to Data Hub. Reason: ";
        if (LOG.isDebugEnabled()) {
            LOG.debug("The following values was send to Data Hub" + target);
            LOG.debug("To the feed" + getFeed() + " into raw model " + RAW_HYBRIS_COMPLAINT);
        }

        try {
            dataHubOutboundService.sendToDataHub(getFeed(), RAW_HYBRIS_COMPLAINT, target);
        } catch (DataHubOutboundException e) {
            LOG.error(errorMsg + e);
        } catch (final Exception e) {
            LOG.error(errorMsg + e);
        }

    }

    /**
     * Instantiate target Map
     *
     * @return new target instance
     */
    protected Map<String, Object> getTarget() {
        return new HashMap<>();
    }

    /**
     * return data hub feed
     *
     * @return feed
     */
    public String getFeed() {
        return feed;
    }

    /**
     * set data hub feed (usually set via the local property file)
     *
     * @param feed
     */
    public void setFeed(final String feed) {
        this.feed = feed;
    }

}
