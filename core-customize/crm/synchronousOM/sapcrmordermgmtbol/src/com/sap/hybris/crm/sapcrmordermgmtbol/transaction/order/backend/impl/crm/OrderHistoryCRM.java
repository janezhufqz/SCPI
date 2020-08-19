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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.order.backend.impl.crm;

import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.interf.OrderHistoryBackend;


/**
 * Back end Object representing an persisted CRM Order document used in order history (different connection compared to
 * checkout)
 */
@BackendType("CRM")
public class OrderHistoryCRM extends OrderBaseCRM implements OrderHistoryBackend
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.SalesDocumentBackend#
	 * deleteItemsInBackend(de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument,
	 * de.hybris.platform.sap.core.common.TechKey[],
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration)
	 */
	@Override
	public void deleteItemsInBackend(final SalesDocument salesDocument, final TechKey[] itemsToDelete,
			final TransactionConfiguration transConf) throws BackendException
	{
		// YTODO Auto-generated method stub

	}
	//
}
