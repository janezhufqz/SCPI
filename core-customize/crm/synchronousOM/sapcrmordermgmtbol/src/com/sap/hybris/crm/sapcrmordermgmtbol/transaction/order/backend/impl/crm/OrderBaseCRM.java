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

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.OverallStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ProcessingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShippingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.interf.OrderBaseBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.SalesDocumentType;

import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm.LoadOperation;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm.SalesDocumentCRM;


/**
 * Back end Object representing an CRM Order document
 */
public abstract class OrderBaseCRM extends SalesDocumentCRM implements OrderBaseBackend
{

	private static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(OrderBaseCRM.class.getName());

	/**
	 * When the order is created with a reference to a quotation, the following reads of the order do not return docflow
	 * entries as long as the order was not saved. Thus we remember the Predecessor Quotation here, until the order has
	 * been saved, so we can properly fill the predecessor list for the reads before the order was saved.
	 */
	protected ConnectedDocument quotationPredecessorDocument = null;




	/**
	 * Saves the order in the backend.<br>
	 * The order will be saved in the backend.
	 *
	 * @param ordr
	 *           The order to be saved
	 * @param commit
	 *           The transaction should also be commited
	 */
	@Override
	public boolean saveInBackend(final Order ordr, final boolean commit) throws BackendException
	{

		final String METHOD_NAME = "saveInBackend(order, commit = '" + Boolean.toString(commit) + "')";
		sapLogger.entering(METHOD_NAME);
		if (checkForInitializationError(ordr))
		{
			sapLogger.exiting();
			return false;
		}

		final boolean saveWasSuccessful = saveOrder(ordr, commit);

		sapLogger.exiting();
		return saveWasSuccessful;
	}

	/**
	 * Saves the order in the backend and returns, if it was successufull.
	 *
	 * @param ordr
	 * @param commit
	 * @return true, if save was successfull, false otherwise
	 * @throws BackendException
	 */
	protected boolean saveOrder(final Order ordr, final boolean commit) throws BackendException
	{

		final String METHOD_NAME = "saveOrder(order, commit = '" + Boolean.toString(commit) + "')";
		sapLogger.entering(METHOD_NAME);

		boolean success = true;

		// get JCOConnection
		final JCoConnection aJCoCon = getDefaultJCoConnection();

		final BackendCallResult retVal = lrdActionsStrategy.saveCrmOrder(ordr, commit, aJCoCon);

		if (retVal.isFailure())
		{
			ordr.setInvalid(); // order could not be saved
			success = false;
		}

		// Call the lord load
		if (success)
		{
			// after the order was saved the docflow entries will be returned by
			// the following reads
			quotationPredecessorDocument = null;
			// Now we are in display mode again
			loadState.setLoadOperation(LoadOperation.DISPLAY);
			clearItemBuffer();
		}

		sapLogger.exiting();

		return success;
	}

	/**
	 * Copies the basket object to the order object
	 *
	 * @param ordr
	 *           Order document
	 * @param commit
	 *           Flag controlling DB commit in back end
	 * @return 0, if success, otherwise -1
	 * @throws BackendException
	 *            in case of a back-end error
	 */
	public int saveOrderInBackend(final Order ordr, final boolean commit) throws BackendException
	{

		final boolean success = saveOrder(ordr, commit);
		if (success)
		{
			return 0;
		}
		return -1;
	}

	/**
	 * Sets the Load State of LO-API to "create" mode. This is needed in case a basket gets copied into an order. Only
	 * for CRM backend.
	 */
	@Override
	public void setLoadStateCreate()
	{
		loadState.setLoadOperation(LoadOperation.CREATE);
	}

	@Override
	public SalesDocumentType getSalesDocumentType()
	{
		return SalesDocumentType.ORDER;
	}


	/**
	 * The CRM order can be canceled if the overall status is opened or if the overall status is in process , but not
	 * shipped, not billed and no delivery created for the any position can be cancelled. This can happened if the credit
	 * check is switched off for instance. The credit check is performed for the order and set order status "in process".
	 * But order can be cancelled in this case
	 */
	@Override
	public boolean isCancelable(final Order order)
	{
		boolean isCancelable = true;
		boolean isBillingStatusAvil = false;
		boolean isShippingStatusAvil = false;
		final Header header = order.getHeader();
		final OverallStatus overallStatus = header.getOverallStatus();
		if (overallStatus != null)
		{
			final BillingStatus billingStatus = (BillingStatus) header.getBillingStatus();
			final ShippingStatus shippingStatus = (ShippingStatus) header.getShippingStatus();
			isBillingStatusAvil = (null == billingStatus || billingStatus.isNotProcessed());
			isShippingStatusAvil = (null == shippingStatus || shippingStatus.isNotProcessed());
			isCancelable = overallStatus.isNotProcessed()
					|| (overallStatus.isPartiallyProcessed() && isBillingStatusAvil && isShippingStatusAvil);

		}
		isCancelable = isCancelable && noItemIsFullyProcessed(order);
		return isCancelable;
	}

	private boolean noItemIsFullyProcessed(final Order order)
	{
		boolean noItemIsFullyProcessed = true;
		final ItemList itemList = order.getItemList();
		for (final Item item : itemList)
		{
			final ProcessingStatus processingStatus = item.getProcessingStatus();
			if (processingStatus.isProcessed())
			{
				noItemIsFullyProcessed = false;
				break;
			}
		}
		return noItemIsFullyProcessed;
	}



	@Override
	public void updateInBackend(final SalesDocument salesDocument, final TransactionConfiguration shop) throws BackendException
	{
		super.updateInBackend(salesDocument, shop);

	}

	@Override
	public void readFromBackend(final SalesDocument salesDocument, final TransactionConfiguration transConf,
			final boolean directRead) throws BackendException
	{
		super.readFromBackend(salesDocument, transConf, directRead);
	}



}
