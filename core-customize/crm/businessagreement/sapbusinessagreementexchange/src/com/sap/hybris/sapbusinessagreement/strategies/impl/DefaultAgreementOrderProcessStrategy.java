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
package com.sap.hybris.sapbusinessagreement.strategies.impl;

import de.hybris.platform.agreement.model.AgreementApprovalProcessModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;

import com.sap.hybris.sapbusinessagreement.strategies.AgreementProcessStrategy;

/**
 *
 */
public class DefaultAgreementOrderProcessStrategy extends AbstractBusinessService implements AgreementProcessStrategy {

    protected BusinessProcessService businessProcessService;
    protected KeyGenerator processCodeGenerator;
    protected String processCode;

    @Override
    public void createAgreementOrderProcess(final OrderModel order) {

        final String agreementOrderProcessCode = this.getProcessCode();
        agreementOrderProcessCode.concat("-" + order.getCode() + "-" + System.currentTimeMillis());

        final AgreementApprovalProcessModel agreementProcess = (AgreementApprovalProcessModel) this
                .getBusinessProcessService()
                .createProcess(String.valueOf(this.getProcessCodeGenerator().generate()), agreementOrderProcessCode);
        agreementProcess.setOrder(order);
        this.getModelService().save(agreementProcess);
        this.getBusinessProcessService().startProcess(agreementProcess);

    }

    /**
     * @return the businessProcessService
     */
    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    /**
     * @param businessProcessService
     *            the businessProcessService to set
     */
    public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    /**
     * @return the processCodeGenerator
     */
    public KeyGenerator getProcessCodeGenerator() {
        return processCodeGenerator;
    }

    /**
     * @param processCodeGenerator
     *            the processCodeGenerator to set
     */
    public void setProcessCodeGenerator(final KeyGenerator processCodeGenerator) {
        this.processCodeGenerator = processCodeGenerator;
    }

    /**
     * @return the processCode
     */
    public String getProcessCode() {
        return processCode;
    }

    /**
     * @param processCode
     *            the processCode to set
     */
    public void setProcessCode(final String processCode) {
        this.processCode = processCode;
    }

}
