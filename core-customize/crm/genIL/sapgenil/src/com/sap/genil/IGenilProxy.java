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

package com.sap.genil;

import java.util.List;

import com.sap.genil.exception.GenilBackendException;


/**
 * The root interface of the genil proxies hierarchy.It defines basic operations each proxy should have<br>
 *
 * @author SAP
 * @version 1.0
 */

public interface IGenilProxy
{

	public GenilQueryRequestContainer getQueryRequestContainer(String queryObjectName) throws GenilBackendException;

	public GenilReadRootRequestContainer getReadRequestContainer(String rootObjectName, String rtGuid)
			throws GenilBackendException;

	public boolean isInitialized();

	public boolean isIgnoringModelErrors();

	/**
	 * Facilitates reading of objects from Genil<br>
	 *
	 * @param rootReq
	 *           holds what was requested
	 * @return fills a root data container with the results of the operation
	 * @throws GenilBackendException
	 *            if the operation failed
	 */
	public GenilRootDataContainer read(GenilReadRootRequestContainer rootReq) throws GenilBackendException;

	/**
	 * Facilitates reading of objects from Genil<br>
	 *
	 * @param rootReq
	 *           holds what was requested
	 * @param rootReq
	 *           holds what was requested
	 * @return fills a root data container with the results of the operation
	 * @throws GenilBackendException
	 *            if the operation failed
	 */
	public GenilRootDataContainer read(GenilReadRootRequestContainer rootReq, GenilMessageRequestContainer msgReq)
			throws GenilBackendException;

	/**
	 * Facilitates reading of objects from Genil <br>
	 * This method is used if data is to be incrementally read. For instance, if portions of the root object or its child
	 * object attributes are known, only the delta will be requested from the backend. A dataContainer has to be passed
	 * which attribute flags will be evaluated in order to only deliver the requested attributes <br>
	 *
	 * @param rootCont
	 *           is the root of the data containers hierarchy which a will be filled
	 * @param rootReq
	 *           holds what was requested
	 * @throws GenilBackendException
	 *            if the read operation failed
	 */
	public void read(GenilRootDataContainer rootCont, GenilReadRootRequestContainer rootReq) throws GenilBackendException;

	/**
	 * Facilitates reading of objects from Genil <br>
	 * This method is used if data is to be incrementally read. For instance, if portions of the root object or its child
	 * object attributes are known, only the delta will be requested from the backend. A dataContainer has to be passed
	 * which attribute flags will be evaluated in order to only deliver the requested attributes <br>
	 *
	 * @param rootCont
	 *           is the root of the data containers hierarchy which a will be filled
	 * @param rootReq
	 *           holds what was requested
	 * @param msgReq
	 *           holds what messages was requested
	 * @throws GenilBackendException
	 *            if the read operation failed
	 *
	 */
	public void read(GenilRootDataContainer rootCont, GenilReadRootRequestContainer rootReq, GenilMessageRequestContainer msgReq)
			throws GenilBackendException;

	/**
	 * Launched a query to Genil in order to get data back<br>
	 *
	 * @param queryReq
	 *           holds what was requested
	 * @return a list of root data containers filled with the results of search
	 * @throws GenilBackendException
	 *            if the operation failed
	 */
	public List<GenilRootDataContainer> search(GenilQueryRequestContainer queryReq) throws GenilBackendException;

	public GenilMessageRequestContainer getMessageRequestContainer();

	public GenilMessageContainer getGlobalMessageContainer();


	/**
	 * Launched a query to Genil in order to get data back<br>
	 *
	 * @param queryReq
	 *           holds what was requested
	 * @return a list of root data containers filled with the results of search
	 * @throws GenilBackendException
	 *            if the operation failed
	 */
	public List<GenilRootDataContainer> search(GenilQueryRequestContainer queryReq, GenilMessageRequestContainer msgReq)
			throws GenilBackendException;

	/**
	 * Facilitates reading of objects from Genil<br>
	 *
	 * @param rootReq
	 *           holds what was requested
	 * @return fills a root data container with the results of the operation
	 * @throws GenilBackendException
	 *            if the operation failed
	 */

}
