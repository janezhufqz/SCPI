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
import java.util.Set;

import com.sap.genil.exception.GenilBackendException;


/**
 * This interface defines operations which are valid for state aware proxies<br>
 *
 * @author SAP
 * @version 1.0
 */
public interface IGenilProxyStateAware extends IGenilProxy
{
	/**
	 * Creates a new genil object<br>
	 *
	 * @param createReq
	 *           holds what is to be created
	 * @return the root data container coresponding to the genil object created
	 * @throws GenilBackendException
	 */
	public GenilRootDataContainer create(GenilCreateRootRequestContainer createReq) throws GenilBackendException;

	/**
	 * Will delete a genil object.
	 *
	 * @param rootCont
	 *           the data container corresponding to the genil object to be deleted
	 * @return true if the operation was successful, false otherwise
	 * @throws GenilBackendException
	 */
	public boolean delete(GenilRootDataContainer rootCont) throws GenilBackendException;

	/**
	 * Executes business specific methods
	 *
	 * @param executeReq
	 *           holds what is suppossed to be executed
	 * @return the result of method execution
	 * @throws GenilBackendException
	 */
	public List<GenilRootDataContainer> execute(GenilExecuteRequestContainer executeReq) throws GenilBackendException;

	public List<GenilRootDataContainer> execute(GenilExecuteRequestContainer executeReq,
			List<GenilSimpleObjectInstance> failedObjects, List<GenilSimpleObjectInstance> changedObjects)
			throws GenilBackendException;

	public GenilExecuteRequestContainer getExecBoMethodRequestContainer(GenilDataContainer cont, String methodName)
			throws GenilBackendException;

	public GenilCreateRootRequestContainer getCreateRootRequestContainer(String rootObjectName) throws GenilBackendException;

	/**
	 * Gets back the messages corresponding to a certain genil object<br>
	 *
	 * @param msgReq
	 *           what is requested e.g message types
	 * @param rootCont
	 * @throws GenilBackendException
	 */
	public void getMessages(GenilMessageRequestContainer msgReq, GenilRootDataContainer rootCont) throws GenilBackendException;

	/**
	 * Initializes a genil object<br>
	 *
	 * @param rootCont
	 *           the root data container corresponding to the genil object to be initialized
	 * @return true if the initialization was successful, false otherwise
	 * @throws GenilBackendException
	 */
	public boolean init(GenilRootDataContainer rootCont) throws GenilBackendException;

	public boolean init(Set<GenilRootDataContainer> rootConts) throws GenilBackendException;

	/**
	 * @return true if messages are going to be supplied only with the call of getMessages(...) <br>
	 *         false if messages are going to be automatically supplied e.g after each read, modify
	 */
	public boolean isMsgOnRequest();

	/**
	 * Loads components and component sets
	 *
	 * @param componentSet
	 *           name of component set to be loaded
	 * @param component
	 *           name of component to be loaded
	 * @return true if the operation was successful
	 * @throws GenilBackendException
	 */
	public boolean load(String componentSet, String component) throws GenilBackendException;

	/**
	 * Locks a genil object<br>
	 *
	 * @param rootCont
	 *           the root data container corresponding to the genil object to be locked
	 * @return true if the operation was successful
	 * @throws GenilBackendException
	 */
	public boolean lock(GenilRootDataContainer rootCont) throws GenilBackendException;

	public boolean lock(Set<GenilRootDataContainer> rootConts) throws GenilBackendException;

	public boolean lock(GenilRootDataContainer rootCont, boolean doReRead) throws GenilBackendException;

	public boolean lock(Set<GenilRootDataContainer> rootConts, boolean doReRead) throws GenilBackendException;


	/**
	 * Performs an update operation on a GenilRootDataContainer which is taken as parameter<br>
	 *
	 * @param rootCont
	 * @param doReRead
	 *           true if a reread will follow
	 * @return true if the operation was successful
	 * @throws GenilBackendException
	 */
	public boolean modify(GenilRootDataContainer rootCont, boolean doReRead) throws GenilBackendException;

	/**
	 * Offers the advantage of reading only attributes, children objects which were invalidated for example, in contrast
	 * to normal read operation<br>
	 *
	 * @param rootCont
	 *           the root of the hierarchy which is going to be read
	 * @param forceReadAll
	 *           true if everything shall be read
	 * @throws GenilBackendException
	 */
	public void reRead(GenilRootDataContainer rootCont, boolean forceReadAll) throws GenilBackendException;

	/**
	 * Initializes all the Genil objects belonging to the components which were previously loaded<br>
	 *
	 * @return
	 * @throws GenilBackendException
	 */
	public boolean reset() throws GenilBackendException;

	/**
	 * Will persist all the changes made to the Genil object corresponding to the GenilRootDataContainer received as
	 * parameter. If the operation was successful a commit will follow, otherwise a rollback of all changes which have
	 * been performed.
	 *
	 * @param rootCont
	 * @return true if the operation was successful
	 * @throws GenilBackendException
	 */
	public boolean save(GenilRootDataContainer rootCont) throws GenilBackendException;

	public boolean save(Set<GenilRootDataContainer> rootConts) throws GenilBackendException;

	public void setMsgOnRequest(boolean msgOnRequest);

	/**
	 * Creates a new genil object<br>
	 *
	 * @param createReq
	 *           holds what is to be created
	 * @return the root data container coresponding to the genil object created
	 * @throws GenilBackendException
	 */
	public GenilRootDataContainer create(GenilCreateRootRequestContainer createReq, GenilMessageRequestContainer msgReq)
			throws GenilBackendException;

	/**
	 * Performs an update operation on a GenilRootDataContainer which is taken as parameter<br>
	 *
	 * @param rootCont
	 * @param doReRead
	 *           true if a reread will follow
	 * @return true if the operation was successful
	 * @throws GenilBackendException
	 */
	public boolean modify(GenilRootDataContainer rootCont, boolean doReRead, GenilMessageRequestContainer msgReq)
			throws GenilBackendException;

	/**
	 * Will persist all the changes made to the Genil object corresponding to the GenilRootDataContainer received as
	 * parameter. If the operation was successful a commit will follow, otherwise a rollback of all changes which have
	 * been performed.
	 *
	 * @param rootCont
	 * @return true if the operation was successful
	 * @throws GenilBackendException
	 */
	public boolean save(GenilRootDataContainer rootCont, GenilMessageRequestContainer msgReq) throws GenilBackendException;

	public boolean save(Set<GenilRootDataContainer> rootConts, GenilMessageRequestContainer msgReq) throws GenilBackendException;
}
