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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.strategy;

import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult;
import com.sap.conn.jco.JCoFunction;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.BackendState;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.CRMConstants;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.ItemBuffer;

/**
 * Strategy interface for reading a sales document from CRM with all aspects.
 * Respective function module is {@link CRMConstants#FM_LO_API_WEC_ORDER_GET}
 *
 */
public interface GetAllStrategy {

	/**
	 * Execute the read call to fetch sales document details from the CRM
	 * backend. Is able to take a buffered state of items into account to
	 * optimize performance (i.e. those items won't be requested from the
	 * backend).
	 *
	 * @param backendState
	 *            CRM specific state of the document
	 * @param salesDocument
	 *            BO representation of the sales document
	 * @param itemBuffer
	 *            State of previously read items (performance improvement)
	 * @param readParams
	 *            control read call (i.e. do we need to take IPC pricing into
	 *            account)
	 * @param connection
	 *            connection to backend system
	 * @return result of backend call
	 * @throws BackendException
	 *             exception from R/3
	 */
	public BackendCallResult execute(BackendState backendState,
			SalesDocument salesDocument, ItemBuffer itemBuffer,
			JCoConnection connection) throws BackendException;

	/**
	 * Performsm the actual JCO call
	 *
	 * @param connection
	 *            connection to the backend system
	 * @param function
	 *            JCO function
	 * @throws BackendException
	 *             exception from R/3
	 */
	public void executeRfc(JCoConnection connection, JCoFunction function)
			throws BackendException;

}
