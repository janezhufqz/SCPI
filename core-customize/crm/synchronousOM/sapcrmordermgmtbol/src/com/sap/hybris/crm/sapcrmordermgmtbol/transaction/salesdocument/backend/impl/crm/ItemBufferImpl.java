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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.ItemBuffer;


/**
 * Standard implementation for {@link ItemBuffer}.
 *
 */
public class ItemBufferImpl implements ItemBuffer
{
	/**
	 * The list of items which represents the CRM status. We use it to compile a delta to the status we get from the BO
	 * layer. Aim is to optimise the LO-API call
	 */
	private Map<String, Item> itemsCRMState;

	/**
	 * Initialization
	 */
	public void init()
	{
		this.setItemsCRMState(new HashMap<String, Item>());
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.salesdocument.backend.impl. CRM.ItemBuffer#getItemsCRMState()
	 */
	@Override
	public Map<String, Item> getItemsCRMState()
	{
		return itemsCRMState;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.app.esales.module.transaction.salesdocument.backend.impl.
	 * CRM.ItemBuffer#setItemsCRMState(java.util.Map)
	 */
	@Override
	public void setItemsCRMState(final Map<String, Item> itemsCRMState)
	{
		this.itemsCRMState = itemsCRMState;
	}

	@Override
	public void removeItemCRMState(final String idAsString)
	{
		// first remove main item
		itemsCRMState.remove(idAsString);

		// now remove childs
		final List<String> childs = new ArrayList<String>();
		for (final Item item : itemsCRMState.values())
		{

			final TechKey parentId = item.getParentId();
			if (parentId != null && parentId.getIdAsString().equals(idAsString))
			{
				childs.add(item.getTechKey().getIdAsString());
			}
		}

		for (final String key : childs)
		{
			itemsCRMState.remove(key);
		}

	}

	@Override
	public void clearCRMBuffer()
	{
		if (getItemsCRMState() != null)
		{
			getItemsCRMState().clear();
		}
	}
}
