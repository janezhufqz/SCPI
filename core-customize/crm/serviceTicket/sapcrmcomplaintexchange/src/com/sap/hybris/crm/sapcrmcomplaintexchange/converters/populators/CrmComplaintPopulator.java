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

package com.sap.hybris.crm.sapcrmcomplaintexchange.converters.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;

public class CrmComplaintPopulator implements Populator<CsTicketParameter, CsTicketModel> {
    @Override
    public void populate(final CsTicketParameter source, final CsTicketModel target) throws ConversionException {
        if (null != source.getAssociatedTo()) {
            target.setOrder(source.getAssociatedTo());
        }
        if (null != source.getAssociatedOrderEntries()) {
            target.setAssociatedOrderEntries(source.getAssociatedOrderEntries());
        }
    }
}
