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

package com.sap.hybris.crm.actions;

import org.springframework.beans.factory.annotation.Required;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubHelper;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubResult;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.returns.model.ReturnProcessModel;

public class SendReturnOrderToDataHubAction extends AbstractSimpleDecisionAction<ReturnProcessModel> {
    public static final String ERROR_END_MESSAGE = "Sending to ERP went wrong.";
    static final int DEFAULT_MAX_RETRIES = 10;
    static final int DEFAULT_RETRY_DELAY = 60 * 1000; // value in ms

    private int maxRetries = DEFAULT_MAX_RETRIES;
    private int retryDelay = DEFAULT_RETRY_DELAY;

    private SendToDataHubHelper<ReturnRequestModel> sendOrderToDataHubHelper;

    @SuppressWarnings("javadoc")
    public SendToDataHubHelper<ReturnRequestModel> getSendOrderToDataHubHelper() {
        return sendOrderToDataHubHelper;
    }

    @SuppressWarnings("javadoc")
    @Required
    public void setSendOrderToDataHubHelper(final SendToDataHubHelper<ReturnRequestModel> sendOrderAsCSVHelper) {
        this.sendOrderToDataHubHelper = sendOrderAsCSVHelper;
    }

    @SuppressWarnings("javadoc")
    public int getMaxRetries() {
        return maxRetries;
    }

    @SuppressWarnings("javadoc")
    public void setMaxRetries(final int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @SuppressWarnings("javadoc")
    public int getRetryDelay() {
        return retryDelay;
    }

    @SuppressWarnings("javadoc")
    public void setRetryDelay(final int retryDelay) {
        this.retryDelay = retryDelay;
    }

    @Override
    public Transition executeAction(final ReturnProcessModel process) throws RetryLaterException {
        final ReturnRequestModel returnRequest = process.getReturnRequest();
        final SendToDataHubResult result = sendOrderToDataHubHelper.createAndSendRawItem(returnRequest);
        if (result.isSuccess()) {
            resetEndMessage(process);
            return Transition.OK;
        } else {
            return Transition.NOK;
        }

    }

    protected void resetEndMessage(final ReturnProcessModel process) {
        final String endMessage = process.getEndMessage();
        if (ERROR_END_MESSAGE.equals(endMessage)) {
            process.setEndMessage("");
            modelService.save(process);
        }
    }

  }
