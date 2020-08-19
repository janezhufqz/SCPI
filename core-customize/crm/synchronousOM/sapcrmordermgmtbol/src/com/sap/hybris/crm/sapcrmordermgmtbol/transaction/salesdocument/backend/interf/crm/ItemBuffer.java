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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm;

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import java.util.Map;


/**
 * Helper Interface to Buffer Item data, typically used by CRM backend implementation.<br>
 *
 */
public interface ItemBuffer
{

	/**
	 * The list of items which represents the CRM status. We use it to compile a delta to the status we get from the BO
	 * layer. Aim is to optimise the LO-API call
	 *
	 * @return list of items
	 */
	public Map<String, Item> getItemsCRMState();

	/**
	 * The list of items which represents the CRM status. We use it to compile a delta to the status we get from the BO
	 * layer. Aim is to optimise the LO-API call
	 *
	 * @param itemsCRMState
	 *           list of items
	 */
	public void setItemsCRMState(Map<String, Item> itemsCRMState);

	/**
	 * Removes item from CRM state map, together with sub items (free goods!)
	 *
	 * @param idAsString
	 *           item ID
	 */
	public void removeItemCRMState(String idAsString);

	/**
	 * Clears the buffer for the CRM state of the document.
	 */
	public void clearCRMBuffer();

}