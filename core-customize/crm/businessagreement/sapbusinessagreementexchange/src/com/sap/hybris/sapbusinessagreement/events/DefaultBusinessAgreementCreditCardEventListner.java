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
package com.sap.hybris.sapbusinessagreement.events;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.AfterSaveEvent;
import de.hybris.platform.tx.AfterSaveListener;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.sap.hybris.sapbusinessagreement.constants.SapbusinessagreementexchangeConstants;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementProcessModel;
import com.sap.hybris.sapbusinessagreement.service.impl.DefaultBusinessAgreementCreateService;

/**
 *
 */
public class DefaultBusinessAgreementCreditCardEventListner implements
        AfterSaveListener {
    private static final Logger LOGGER = Logger
            .getLogger(com.sap.hybris.sapbusinessagreement.events.DefaultBusinessAgreementCreditCardEventListner.class
                    .getName());
    private ModelService modelService;
    private BusinessProcessService businessProcessService;
    private DefaultBusinessAgreementCreateService businessAgreementCreateService;

    /**
     * @return the businessAgreementCreateService
     */
    public DefaultBusinessAgreementCreateService getBusinessAgreementCreateService() {
        return businessAgreementCreateService;
    }

    /**
     * @param businessAgreementCreateService
     *            the businessAgreementCreateService to set
     */
    public void setBusinessAgreementCreateService(
            final DefaultBusinessAgreementCreateService businessAgreementCreateService) {
        this.businessAgreementCreateService = businessAgreementCreateService;
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
    public void setBusinessProcessService(
            final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    /**
     * @return the modelService
     */
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * @param modelService
     *            the modelService to set
     */
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    public void afterSave(final Collection<AfterSaveEvent> events) {

        for (final AfterSaveEvent event : events) {
            final int type = event.getType();
            final PK eventPK = event.getPk();
            if (AfterSaveEvent.CREATE == type) {

                saveBusinessAgreementForCreditCard(eventPK);
            }

        }

    }

    /**
     *
     * @param pk
     */
    protected void saveBusinessAgreementForCreditCard(final PK pk) {
        if (SapbusinessagreementexchangeConstants.ITEM_TYPE_CODE_PAYMENTINFO == pk
                .getTypeCode()) {
            final PaymentInfoModel paymentInfoModel = modelService.get(pk);
            if (paymentInfoModel instanceof CreditCardPaymentInfoModel) {
                final CreditCardPaymentInfoModel creditCardPaymentInfoModel = (CreditCardPaymentInfoModel) paymentInfoModel;

                createBusinessAgreementForCreditCard(creditCardPaymentInfoModel);
            }
        }
    }

    /**
     * Create a Business Agreement Model
     *
     * @param creditCardPaymentInfoModel
     */
    protected void createBusinessAgreementForCreditCard(
            final CreditCardPaymentInfoModel creditCardPaymentInfoModel) {
        if (creditCardPaymentInfoModel.getCardInc() != null
                && creditCardPaymentInfoModel.isSaved()
                && !creditCardPaymentInfoModel.getSapIsReplicated()
                        .booleanValue()
                && !creditCardPaymentInfoModel.getDuplicate().booleanValue()) {

            final BusinessAgreementModel businessAgreementModel = businessAgreementCreateService
                    .createBusinessAgreementModel(creditCardPaymentInfoModel);
            if (businessAgreementModel != null) {
                startBusinessAgreementProcess(businessAgreementModel,
                        creditCardPaymentInfoModel);

            }
        }
    }

    /**
     * Create business Agreement process and save business agreement model and
     * credit card model both to this process
     *
     * @param businessAgreementModel
     * @param creditCardPaymentInfoModel
     */
    protected void startBusinessAgreementProcess(
            final BusinessAgreementModel businessAgreementModel,
            final CreditCardPaymentInfoModel creditCardPaymentInfoModel) {
        final BusinessAgreementProcessModel businessAgreementProcessModel = (BusinessAgreementProcessModel) getBusinessProcessService()
                .createProcess(
                        SapbusinessagreementexchangeConstants.BUSINESS_AGREEMENT_PROCESS_DEFINITION
                                + "-"
                                + System.currentTimeMillis()
                                + "-"
                                + businessAgreementModel.getId(),
                        SapbusinessagreementexchangeConstants.BUSINESS_AGREEMENT_PROCESS_DEFINITION);

        businessAgreementProcessModel
                .setBusinessAgreement(businessAgreementModel);
        businessAgreementProcessModel
                .setCreditCardPaymentInfo(creditCardPaymentInfoModel);
        getModelService().save(businessAgreementProcessModel);
        getModelService().refresh(businessAgreementProcessModel);
        getBusinessProcessService().startProcess(businessAgreementProcessModel);

        LOGGER.info("Business Agreement Process:"
                + businessAgreementProcessModel.getCode() + "is Created");

    }

}
