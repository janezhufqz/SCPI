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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.basket.backend.impl.crm;

import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.backend.interf.BasketBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.SalesDocumentType;

import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm.SalesDocumentCRM;


/**
 * @author C5230711
 *
 */
@BackendType("CRM")
public class BasketCRM extends SalesDocumentCRM implements BasketBackend
{
	static final private Log4JWrapper sapLogger = Log4JWrapper.getInstance(BasketCRM.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.interf.BasketBackend #
	 * saveInBackend(com.sap.hybris.app.esales.module.transaction.businessobject.interf .Basket, boolean)
	 */
	@Override
	public boolean saveInBackend(final Basket basket, final boolean commit) throws BackendException
	{

		final String METHOD_NAME = "saveInBackend(basket, commit = '" + Boolean.toString(commit) + "')";
		sapLogger.entering(METHOD_NAME);

		return true;
	}

	@Override
	public SalesDocumentType getSalesDocumentType()
	{
		return SalesDocumentType.BASKET;
	}

	@Override
	public void readFromBackend(final SalesDocument salesDocument, final TransactionConfiguration transConf,
			final boolean directRead) throws BackendException
	{
		//commented
	}



	/**
	 * In the BasketCRM case the readForUpdateFromBackend calls readFromBackend to avoid calling of CRM_WEC_ORDER_LOAD
	 */
	@Override
	public void readForUpdateFromBackend(final SalesDocument salesDocument) throws BackendException
	{
		/*
		 * //Header
		 */

	}

	@Override
	public void updateInBackend(final SalesDocument salesDocument, final TransactionConfiguration shop) throws BackendException
	{

		super.updateInBackend(salesDocument, shop);



	}

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

}
