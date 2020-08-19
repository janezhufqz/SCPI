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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm.strategy;

import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.strategy.GetAllStrategy;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.strategy.LrdActionsStrategy;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.strategy.SetStrategy;


/**
 * Holder of access (interaction procedure) for a specific release of CRM. <br>
 *
 * @version 1.0
 */
public abstract class StrategyFactoryCRM
{

	/**
	 * Access (interaction procedure) for reading a sales document from CRM with all aspects..<br>
	 *
	 * @return get all strategy
	 */
	public abstract GetAllStrategy createGetAllStrategy();


	/**
	 * Access (interaction procedure) for LORD API.<br>
	 *
	 * @return LORD API strategy
	 */
	public LrdActionsStrategy createLrdActionsStrategy()
	{
		return new LrdActionsStrategyCRM();
	}

	/**
	 * Access (interaction procedure) for CRM_LORD_SET.<br>
	 *
	 * @return CRM_LORD_SET strategy
	 */
	public SetStrategy createSetStrategy()
	{
		return new SetStrategyCRM();
	}


}
