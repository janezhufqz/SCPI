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
package com.sap.hybris.crm.sapcrmticketsystem.dao;

import java.util.List;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ticket.dao.TicketDao;
import de.hybris.platform.ticket.model.CsTicketModel;

/**
 * @author C5242879
 *
 */
public interface SapTicketDao extends TicketDao {

    List<CsTicketModel> findTicketsByIbaseId(UserModel currentUser, String iBaseID, String objectType);

}
