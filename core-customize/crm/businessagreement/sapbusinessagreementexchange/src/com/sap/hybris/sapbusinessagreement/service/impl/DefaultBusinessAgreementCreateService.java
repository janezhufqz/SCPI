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
package com.sap.hybris.sapbusinessagreement.service.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Logger;

import com.sap.hybris.sapbusinessagreement.constants.SapbusinessagreementexchangeConstants;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementHeaderModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementRuleHeaderModel;
import com.sap.hybris.sapbusinessagreement.model.CRMBusinessAgreementClassModel;
import com.sap.hybris.sapbusinessagreement.model.CRMBusinessAgreementPaymentMethodModel;
import com.sap.hybris.sapbusinessagreement.model.CRMBusinessAgreementTermsPaymentModel;
import com.sap.hybris.sapbusinessagreement.service.BusinessAgreementCreateService;

/**
 *
 */
public class DefaultBusinessAgreementCreateService implements BusinessAgreementCreateService {
    private ModelService modelService;

    private BusinessAgreementCreateServiceUtility businessAgreementCreateServiceUtility;
    private static final Logger LOGGER = Logger.getLogger(
            com.sap.hybris.sapbusinessagreement.service.impl.DefaultBusinessAgreementCreateService.class.getName());

    /**
     * @return the businessAgreementCreateServiceUtility
     */
    public BusinessAgreementCreateServiceUtility getBusinessAgreementCreateServiceUtility() {
        return businessAgreementCreateServiceUtility;
    }

    /**
     * @param businessAgreementCreateServiceUtility
     *            the businessAgreementCreateServiceUtility to set
     */
    public void setBusinessAgreementCreateServiceUtility(
            final BusinessAgreementCreateServiceUtility businessAgreementCreateServiceUtility) {
        this.businessAgreementCreateServiceUtility = businessAgreementCreateServiceUtility;
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
    public BusinessAgreementModel createBusinessAgreementModel(
            final CreditCardPaymentInfoModel creditCardPaymentInfoModel) {
        if (creditCardPaymentInfoModel != null) {
            final String businessAgreementGuid = businessAgreementCreateServiceUtility.generateGuid();
            final BusinessAgreementModel businessAgreementModel = createBusinessAgreementModel(
                    creditCardPaymentInfoModel, businessAgreementGuid);
            final BusinessAgreementHeaderModel businessAgreementHeaderModel = createBusinessAgreementHeaderModel(
                    businessAgreementModel, creditCardPaymentInfoModel);
            final BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel = createBusinessAgreementRuleHeaderModel(
                    businessAgreementHeaderModel, businessAgreementGuid);

            if ((businessAgreementModel != null) && (businessAgreementHeaderModel != null)
                    && (businessAgreementRuleHeaderModel != null)) {
                getModelService().save(businessAgreementModel);
                getModelService().save(businessAgreementHeaderModel);
                getModelService().save(businessAgreementRuleHeaderModel);
                getModelService().refresh(businessAgreementModel);
                getModelService().refresh(businessAgreementHeaderModel);
                getModelService().refresh(businessAgreementRuleHeaderModel);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("business agreement :" + businessAgreementModel.getId() + " is created");
                }
                return businessAgreementModel;

            }
        }
        return null;
    }

    protected BusinessAgreementModel createBusinessAgreementModel(
            final CreditCardPaymentInfoModel creditCardPaymentInfoModel, final String businessAgreementGuid) {
        final BusinessAgreementModel businessAgreementModel = getModelService().create(BusinessAgreementModel.class);

        businessAgreementModel.setGuid(businessAgreementGuid);
        businessAgreementModel.setId(businessAgreementCreateServiceUtility.generateBusinessAgreementId());

        final UserModel userModel = creditCardPaymentInfoModel.getUser();
        if (userModel instanceof CustomerModel) {
            final CustomerModel customerModel = (CustomerModel) userModel;
            businessAgreementModel.setCustomer(customerModel);
            businessAgreementModel.setPartnerid(customerModel.getCustomerID());
        }

        return businessAgreementModel;
    }

    /**
    *
    */

    /**
    *
    */
    private BusinessAgreementHeaderModel createBusinessAgreementHeaderModel(

            final BusinessAgreementModel businessAgreementModel,
            final CreditCardPaymentInfoModel creditCardPaymentInfoModel) {
        final PK pk = businessAgreementCreateServiceUtility.getBusinessagreementclassPK();
        BusinessAgreementHeaderModel businessAgreementHeaderModel = null;
        if (pk != null) {
            businessAgreementHeaderModel = getModelService().create(BusinessAgreementHeaderModel.class);
            businessAgreementHeaderModel.setBusinessagreement(businessAgreementModel);
            businessAgreementHeaderModel.setValidto(SapbusinessagreementexchangeConstants.VALID_TO);

            businessAgreementHeaderModel.setBusinessagreementdefault(Boolean.FALSE);

            final CRMBusinessAgreementClassModel crmBusinessAgreementClassModel = (CRMBusinessAgreementClassModel) modelService
                    .get(pk);

            businessAgreementHeaderModel.setBusinessagreementclass(crmBusinessAgreementClassModel);

            businessAgreementHeaderModel.setCreditcardinc(creditCardPaymentInfoModel.getCardInc());

        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("business agreement class is not configured in sap global configuration:");
            }
        }
        return businessAgreementHeaderModel;
    }

    /**
     * @param businessAgreementHeaderModel
     *
     */
    private BusinessAgreementRuleHeaderModel createBusinessAgreementRuleHeaderModel(
            final BusinessAgreementHeaderModel businessAgreementHeaderModel, final String businessAgreementGuid) {

        final PK pk = businessAgreementCreateServiceUtility.getTermOFPaymentPK();
        BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel = null;
        if (pk != null) {
            businessAgreementRuleHeaderModel = getModelService().create(BusinessAgreementRuleHeaderModel.class);
            businessAgreementRuleHeaderModel.setBusinessruleguid(
                    businessAgreementGuid + SapbusinessagreementexchangeConstants.RULE_HEADER_GUID);
            businessAgreementRuleHeaderModel.setValidto(SapbusinessagreementexchangeConstants.VALID_TO);

            final CRMBusinessAgreementTermsPaymentModel crmBusinessAgreementTermsPaymentModel = (CRMBusinessAgreementTermsPaymentModel) modelService
                    .get(pk);

            businessAgreementRuleHeaderModel.setTermofpayment(crmBusinessAgreementTermsPaymentModel);
            final PK paymentMethodPK = businessAgreementCreateServiceUtility.getPaymentMethodPK();
            if (paymentMethodPK != null) {
                final CRMBusinessAgreementPaymentMethodModel crmBusinessAgreementPaymentMethodModel = modelService
                        .get(paymentMethodPK);

                businessAgreementRuleHeaderModel.setPaymentmethodinc(crmBusinessAgreementPaymentMethodModel);
            }

            businessAgreementRuleHeaderModel.setBusinessagHeader(businessAgreementHeaderModel);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("business agreement Terms of payment is not configured in sap global configuration:");
            }
        }
        return businessAgreementRuleHeaderModel;

    }
}
