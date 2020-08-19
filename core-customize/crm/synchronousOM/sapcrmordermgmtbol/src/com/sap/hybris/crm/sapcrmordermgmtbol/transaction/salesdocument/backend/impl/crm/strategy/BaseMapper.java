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



/**
 * Base class for LO-API / BOL mappers
 */
abstract public class BaseMapper
{

	/**
	 * Bean initializer.
	 * <p>
	 * Check if all mandatory values injected, else throw runtime error.
	 */
	abstract public void init();

}
