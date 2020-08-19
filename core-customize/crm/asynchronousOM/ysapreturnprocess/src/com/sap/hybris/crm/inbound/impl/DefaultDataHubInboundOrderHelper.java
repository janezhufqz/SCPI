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

package com.sap.hybris.crm.inbound.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sap.hybris.crm.constants.YsapreturnprocessConstants;
import com.sap.hybris.crm.inbound.DataHubInboundOrderHelper;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

public class DefaultDataHubInboundOrderHelper implements DataHubInboundOrderHelper {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDataHubInboundOrderHelper.class);
    private BusinessProcessService businessProcessService;
    private FlexibleSearchService flexibleSearchService;
    private ModelService modelService;

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @SuppressWarnings("javadoc")
    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    @SuppressWarnings("javadoc")
    public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    public void processOrderConfirmationFromHub(final String orderNumber) {

        String eventName = null;
        if (!orderNumber.isEmpty())
            eventName = YsapreturnprocessConstants.RETURNORDER_CONFIRMATION_EVENT + orderNumber;
        getBusinessProcessService().triggerEvent(eventName);
    }

    @Override
    public void processOrderDeliveryNotififcationFromHub(final String orderNumber, final String delivInfo) {
        LOG.info("Entered DefaultDataHubInboundOrderHelper # processOrderDeliveryNotififcationFromHub() ");
        LOG.info("order number : " + orderNumber);

        String deliveryDocumentNo = determineDeliveryDocumentNo(delivInfo);
        LOG.info("deliveryDocument no : " + deliveryDocumentNo);
        String eventName = null;
        if (!deliveryDocumentNo.isEmpty()) {
            ReturnRequestModel requestModel = readReturnOrder(orderNumber);
             requestModel.setDeliveryDocNumber(deliveryDocumentNo);
            getModelService().save(requestModel);
            LOG.info("PGR document no. " + deliveryDocumentNo + " saved successfully");
            eventName = YsapreturnprocessConstants.RETURNORDER_GOOD_EVENT + orderNumber;
        }
        getBusinessProcessService().triggerEvent(eventName);
    }

    @Override
    public void processOrderCreditMemoNotififcationFromHub(String orderNumber, String creditinfo) {
        LOG.info("Entered DefaultDataHubInboundOrderHelper # processOrderCreditMemoNotififcationFromHub() ");
        LOG.info("order number : " + orderNumber);

        String creditMemoNo = determineDeliveryDocumentNo(creditinfo);
        LOG.info("credit memo no : " + creditMemoNo);
        String eventName = null;
        if (!creditMemoNo.isEmpty()) {

            ReturnRequestModel requestModel = readReturnOrder(orderNumber);
            requestModel.setCreditMemoNumber(creditMemoNo);
            getModelService().save(requestModel);
            LOG.info("credit memo no " + creditMemoNo + " saved successfully");
            eventName = YsapreturnprocessConstants.RETURNORDER_PAYMENT_REVERSAL_EVENT + orderNumber;
        }
        getBusinessProcessService().triggerEvent(eventName);
    }

    public String determineDeliveryDocumentNo(final String deliveryInfo) {
        String result = null;
        final int delviLength = deliveryInfo.length();
        final int delivIndex = deliveryInfo.indexOf(YsapreturnprocessConstants.STR) + 1;
        if (delviLength != delivIndex) {
            result = deliveryInfo.substring(delivIndex, delviLength);
        }
        return result;
    }

    protected ReturnRequestModel readReturnOrder(final String returnOrderCode) {
        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
                "SELECT {rr:pk} FROM {ReturnRequest AS rr} WHERE  {rr.code} = ?code");
        flexibleSearchQuery.addQueryParameter("code", returnOrderCode);

        final ReturnRequestModel returnOrder = getFlexibleSearchService().searchUnique(flexibleSearchQuery);
        if (returnOrder == null) {
            final String msg = "Error while IDoc processing. Called with not existing order for order code : "
                    + returnOrderCode;
            throw new IllegalArgumentException(msg);
        }
        return returnOrder;
    }
}
