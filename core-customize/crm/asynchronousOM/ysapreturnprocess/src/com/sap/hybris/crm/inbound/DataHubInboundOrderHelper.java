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
package com.sap.hybris.crm.inbound;
/**
 * @author I324317
 *
 */
public interface DataHubInboundOrderHelper {

    /**
     * Trigger subsequent actions after order confirmation has arrived
     *
     * @param orderNumber
     */
    void processOrderConfirmationFromHub(final String orderNumber);    
    
    
    /**
     * Trigger subsequent actions after delivery and post goods receipt notification has arrived
     * @param orderNumber
     * @param delivInfo
     */
    void processOrderDeliveryNotififcationFromHub(final String orderNumber, final String delivInfo);
    
    
    /**
     * Trigger subsequent actions after credit memo notification has arrived
     * @param orderNumber
     * @param creditinfo
     */
    void processOrderCreditMemoNotififcationFromHub(final String orderNumber, final String creditinfo);

}
