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

package com.sap.hybris.crm.sapcrmticketsystem.converters.populators;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.ticket.converters.populator.CsTicketPopulator;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;

/**
 * Convert category data to category model
 */
public class CrmCsTicketPopulator extends CsTicketPopulator<CsTicketParameter, CsTicketModel> {

    @Override
    public void populate(final CsTicketParameter source, final CsTicketModel target) throws ConversionException {

        super.populate(source, target);
        target.setReasonCategory(source.getReasonCategory());
        target.setCsTicketRelatedObject(source.getRelatedObjects());
    }

}
