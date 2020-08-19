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
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.AfterSaveEvent;
import de.hybris.platform.tx.AfterSaveListener;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.sap.hybris.sapbusinessagreement.constants.SapbusinessagreementexchangeConstants;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementPartnersModel;

public class DefaultBusinessAgreementPartnersEventListner implements
        AfterSaveListener {
    private static final Logger LOGGER = Logger
            .getLogger(com.sap.hybris.sapbusinessagreement.events.DefaultBusinessAgreementPartnersEventListner.class
                    .getName());
    private ModelService modelService;

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
            if (AfterSaveEvent.UPDATE == type || AfterSaveEvent.CREATE == type) {

                getBusinessAgreementPartnerModel(eventPK);

            }
        }

    }

    /**
     *
     * @param eventPK
     */
    protected void getBusinessAgreementPartnerModel(final PK eventPK) {
        if (SapbusinessagreementexchangeConstants.ITEM_TYPE_CODE_BUSINESS_AGREEMENT_PARTNERS == eventPK
                .getTypeCode()) {
            final BusinessAgreementPartnersModel businessAgreementPartnersModel = modelService
                    .get(eventPK);
            removeBusinessAgreementPartnerModel(businessAgreementPartnersModel);
        }
    }

    /**
     *
     * @param businessAgreementPartnersModel
     */

    protected void removeBusinessAgreementPartnerModel(
            final BusinessAgreementPartnersModel businessAgreementPartnersModel) {
        if (businessAgreementPartnersModel
                .getRelationTask()
                .equalsIgnoreCase(
                        SapbusinessagreementexchangeConstants.BUSINESS_AGREEMENT_RELATION_DELETION_TASK)) {
            modelService.remove(businessAgreementPartnersModel);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Business Agreement Relation:"
                        + businessAgreementPartnersModel.getPartnerfunction()
                                .getDescription() + " is deleted");
            }
        }
    }
}
