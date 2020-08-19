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
package com.sap.hybris.crm.sapcrmticketsystem.outbound;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.hybris.datahub.core.rest.DataHubOutboundException;
import com.hybris.datahub.core.services.DataHubOutboundService;
import com.sap.hybris.crm.sapcrmmodel.util.XSSFilterUtil;
import com.sap.hybris.crm.sapcrmticketsystem.constants.SapcrmticketsystemConstants;
import com.sap.hybris.crm.sapcrmticketsystem.model.CsTicketRelatedObjectModel;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
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
public class CsTicketExportService {

    /**
     * Logger for class
     */
    private static final Logger LOG = LoggerFactory.getLogger(CsTicketExportService.class);

    /**
     * Datahub raw Ticket type
     */
    private static final String RAW_HYBRIS_TICKET = "RawHybrisServiceTicket";

    /**
     * DataHubOutboundService to export data to data hub
     */
    private DataHubOutboundService dataHubOutboundService;
    private BaseStoreService baseStoreService;

    /**
     * Default feed to consider if no feed is specified
     */
    private String feed = "DEFAULT_FEED";

    /**
     * CartFacade to add cartid
     */
    @Resource(name = "cartService")
    private CartService cartService;

    /**
     * This method prepares ticket Model to the target map and send data to the
     * Data Hub.
     *
     * @param ticketModel
     *            ticket to be exported
     * @param notes
     *
     */
    public void sendCsTicketData(final CsTicketModel ticketModel, final String notes)
            throws DataHubCommunicationException {
        final Map<String, Object> target = new HashMap<>();

        LOG.debug("In CsTicketExportService : Preparing ticket data with ID : " + ticketModel.getTicketID()
                + " to be sent to Data Hub ");
        prepareTicketData(ticketModel, notes, target);
        try {
            sendTicketToDataHub(target);
            LOG.debug("In CsTicketExportService : Successfully sent ticket data with ID : " + ticketModel.getTicketID()
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
     * @param target
     *            target map
     */
    public void prepareTicketData(final CsTicketModel ticketModel, final String notes,
            final Map<String, Object> target) {

        final BaseStoreModel baseStore = this.baseStoreService.getCurrentBaseStore();
        final String baseStoreUid = baseStore != null ? baseStore.getUid() : null;
        CartModel cartModel = null;
        cartModel = cartService.getSessionCart();
        target.put(SapcrmticketsystemConstants.UID, ticketModel.getTicketID());
        target.put(SapcrmticketsystemConstants.TITLE, XSSFilterUtil.filter(ticketModel.getHeadline()));
        target.put(SapcrmticketsystemConstants.TICKET_REPLICATED, Boolean.valueOf(ticketModel.isIsTicketReplicated()));
        target.put(SapcrmticketsystemConstants.NOTES, notes);

        setCustomerBasedOnB2BorB2C(ticketModel.getCustomer(), target);
        if (ticketModel.getReasonCategory() != null) {
            target.put(SapcrmticketsystemConstants.TICKET_CATEGORY, ticketModel.getReasonCategory().getCode());
        }
        if (ticketModel.getOrder() != null) {
            target.put(SapcrmticketsystemConstants.ORDER, ticketModel.getOrder().getCode());
        }
        target.put(SapcrmticketsystemConstants.PRIORITY, ticketModel.getPriority());
        target.put(SapcrmticketsystemConstants.STATE, ticketModel.getState());
        target.put(SapcrmticketsystemConstants.BASE_STORE, baseStoreUid);
        if (null != cartModel && (cartModel.getEntries() != null) && !cartModel.getEntries().isEmpty()) {
            target.put(SapcrmticketsystemConstants.CARTID, cartModel.getCode());
        }

        // related Objects List should be sent
        if (ticketModel.getCsTicketRelatedObject() != null && !ticketModel.getCsTicketRelatedObject().isEmpty()) {
            final List<CsTicketRelatedObjectModel> csTicketRelModelList = ticketModel.getCsTicketRelatedObject();

            for (final Iterator iterator = csTicketRelModelList.iterator(); iterator.hasNext();) {
                final CsTicketRelatedObjectModel csTicketRelatedObjectModel = (CsTicketRelatedObjectModel) iterator
                        .next();
                target.put(csTicketRelatedObjectModel.getObjectType(), csTicketRelatedObjectModel.getObjectGuid());

            }
        }
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
                target.put(SapcrmticketsystemConstants.CUSTOMER_ID, customer.getCustomerID());
            }

        }

    }

    /**
     * This method establishes connection to the datahub and send the target Map
     * with ticket data to the datahub.
     *
     * @param target
     *            ticket data to sent to datahub
     * @throws DataHubCommunicationException
     */
    protected void sendTicketToDataHub(final Map<String, Object> target) throws DataHubCommunicationException {
        final String errorMsg = "Error processing sending data to Data Hub. Reason: ";
        if (LOG.isDebugEnabled()) {
            LOG.debug("The following values was send to Data Hub" + target);
            LOG.debug("To the feed" + getFeed() + " into raw model " + RAW_HYBRIS_TICKET);
        }

        try {
            getDataHubOutboundService().sendToDataHub(getFeed(), RAW_HYBRIS_TICKET, target);
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

    /**
     * return Data Hub Outbound Service
     *
     * @return dataHubOutboundService
     */
    public DataHubOutboundService getDataHubOutboundService() {
        return dataHubOutboundService;
    }

    /**
     * set Data Hub Outbound Service
     *
     * @param dataHubOutboundService
     */
    public void setDataHubOutboundService(final DataHubOutboundService dataHubOutboundService) {
        this.dataHubOutboundService = dataHubOutboundService;
    }

    /**
     * @return the baseStoreService
     */
    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    /**
     * @param baseStoreService
     *            the baseStoreService to set
     */
    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    /**
     * @param cartService
     *            the cartService to set
     */
    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

}
