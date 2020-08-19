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
package com.sap.hybris.crm.sapcrmticketsystem.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.sap.hybris.crm.sapcrmticketsystem.dao.SapTicketDao;
import com.sap.hybris.crm.sapcrmticketsystem.service.SapTicketService;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.impl.DefaultTicketService;

/**
 * @author C5242879
 *
 */
public class SapDefaultTicketService extends DefaultTicketService implements SapTicketService {

    @Autowired
    private SapTicketDao ticketDao;

    /*
     * (non-Javadoc)
     *
     * @see com.sap.hybris.crm.sapcrmticketsystem.service.SapTicketService#
     * getTicketsForIbase(de.hybris.platform.core.model. user.UserModel,
     * java.lang.String)
     */
    @Override
    public List<CsTicketModel> getTicketsForIbase(final UserModel currentUser, final String iBaseID,
            final String objectType) {
        if (currentUser == null) {
            return Collections.emptyList();
        }

        return ticketDao.findTicketsByIbaseId(currentUser, iBaseID, objectType);
    }

}
