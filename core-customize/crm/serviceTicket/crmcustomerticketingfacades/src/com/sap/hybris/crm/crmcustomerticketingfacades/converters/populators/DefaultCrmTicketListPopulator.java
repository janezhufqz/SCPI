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

package com.sap.hybris.crm.crmcustomerticketingfacades.converters.populators;

import org.apache.log4j.Logger;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.customerticketingfacades.data.TicketCategory;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.ticket.model.CsTicketModel;

public class DefaultCrmTicketListPopulator<SOURCE extends CsTicketModel, TARGET extends TicketData>
        implements Populator<SOURCE, TARGET> {

    protected static final Logger LOG = Logger.getLogger(DefaultCrmTicketListPopulator.class);

    @Override
    public void populate(SOURCE source, TARGET target) throws ConversionException {
        try {
            target.setTicketCategory(TicketCategory.valueOf(source.getCategory().getCode().toUpperCase()));
        } catch (final IllegalArgumentException ex) {
            LOG.error(source.getCategory().getCode().toUpperCase()
                    + "ticket category is not enabled to display for the customer ticketing", ex);
        }
    }
}