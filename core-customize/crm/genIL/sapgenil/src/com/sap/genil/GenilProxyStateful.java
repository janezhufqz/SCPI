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

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoTable;
import com.sap.genil.GenilConst.GenilFeature;
import com.sap.genil.GenilConst.GenilMessageType;
import com.sap.genil.GenilConst.GenilRFC;
import com.sap.genil.exception.GenilBackendException;
import com.sap.genil.rfc.GenilObjInstance;
import com.sap.genil.rfc.NameValuePair;
import com.sap.genil.rfc.RFCInput;
import com.sap.genil.rfc.RFCOutput;
import com.sap.genil.rfc.TableParamRow;
import com.sap.genil.rfc.TablesParam;


/**
 * Defines a stateful proxy <br>
 *
 * @author SAP
 * @version 1.0
 *
 * @author C5230248 -- Modified by Removing WCFLocation --Modified by Removing Genil JCoConnection and added Hybris
 *         JCoConnection
 */

class GenilProxyStateful extends GenilProxy implements IGenilProxyStateAware
{
	/**
	 * Logging instance
	 */
	protected static final Log4JWrapper logger = Log4JWrapper.getInstance(GenilProxyStateful.class.getName());
	private GenilMessageRequestContainer defaultMsgReq;

	/**
	 * Standard constructor. <br>
	 *
	 * @param connection
	 * @param componentSet
	 * @param ignoreModelError
	 * @throws GenilBackendException
	 */

	GenilProxyStateful(final JCoConnection connection, final String componentSet, final boolean ignoreModelError,
			final char componentSetLoadLevel) throws GenilBackendException, JCoException
	{
		super(connection, componentSet, ignoreModelError, componentSetLoadLevel);
		this.msgOnRequest = true;
	}

	private boolean commit() throws GenilBackendException
	{
		final GenilRFC rfc = GenilFeature.COMMIT.getRFCStateFul();
		return rfcWrapper.doRFC(rfc).isSuccess();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxyStateAware#create(com.sap
	 * .genil.GenilCreateRootRequestContainer)
	 */
	public GenilRootDataContainer create(final GenilCreateRootRequestContainer createReq) throws GenilBackendException
	{
		final GenilMessageRequestContainer msgReq = null;

		return create(createReq, msgReq);
	}

	@Override
	public GenilRootDataContainer create(final GenilCreateRootRequestContainer createReq, GenilMessageRequestContainer msgReq)
			throws GenilBackendException
	{
		GenilMessageRequestContainer msgRe=msgReq;
		final String traceMethodName = "create()";
		final long traceTime = traceEntering(traceMethodName);

		RFCOutput rfcOutput;
		final RFCInput rfcInput = prepareCreateRequest(createReq);

		if (!msgOnRequest)
		{
			msgRe = getDefaultMsgReqContainer();
		}

		if (msgRe != null)
		{
			fillMessageAttributes(msgRe, rfcInput);
		}

		rfcOutput = rfcWrapper.doRFC(rfcInput);

		if (rfcOutput.isSuccess())
		{
			List<GenilRootDataContainer> resultList;

			resultList = fillContainer(rfcOutput, null, true);
			if (!resultList.isEmpty())
			{
				final GenilRootDataContainer result = resultList.get(0);

				String rtGuid = null;
				if (result != null)
				{
					rtGuid = result.getRtGuid();
				}

				// if messages are requested return related container
				if (msgRe != null)
				{
					fillMessages(rfcOutput, rtGuid);
				}
				traceExiting(traceTime, traceMethodName);
				return result;
			}
			traceExiting(traceTime, traceMethodName);
			return null;
		}
		traceExiting(traceTime, traceMethodName);
		return null;
	}

	/**
	 * For deletion of root objects only, child objects deletion is done via update method. if a root object is deleted,
	 * all its assigned child objects will be deleted too.<br>
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxyStateAware#delete(com.sap.wec.tc.core.backend.genil.GenilRootDataContainer)
	 */
	public boolean delete(final GenilRootDataContainer rootCont) throws GenilBackendException
	{

		final String traceMethodName = "delete()";
		final long traceTime = traceEntering(traceMethodName);

		final GenilRFC rfc = GenilFeature.DELETE.getRFCStateFul();
		final boolean result = doObjInstance(rfc, rootCont).isSuccess();
		if (result)
		{
			this.commit();
		}
		else
		{
			this.rollback();
		}
		getMessages(null);
		traceExiting(traceTime, traceMethodName);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxyStateAware#execute(com.sap
	 * .wec.tc.core.backend.genil.GenilExecuteRequestContainer)
	 */

	@Override
	public List<GenilRootDataContainer> execute(final GenilExecuteRequestContainer executeReq) throws GenilBackendException
	{

		final List<GenilSimpleObjectInstance> failedObjects = null;
		final List<GenilSimpleObjectInstance> changedObjects = null;

		return executeGenilFunction(executeReq, failedObjects, changedObjects);
	}

	public List<GenilRootDataContainer> execute(final GenilExecuteRequestContainer executeReq,
			List<GenilSimpleObjectInstance> failedObjects, List<GenilSimpleObjectInstance> changedObjects)
			throws GenilBackendException
	{
		List<GenilSimpleObjectInstance> changedObjec =changedObjects;
		List<GenilSimpleObjectInstance> failedObject=failedObjects;
		if (failedObject == null)
		{
			failedObject = new ArrayList<GenilSimpleObjectInstance>();
		}

		if (changedObjec == null)
		{
			changedObjec = new ArrayList<GenilSimpleObjectInstance>();
		}

		return executeGenilFunction(executeReq, failedObject, changedObjec);

	}

	private void fillRFCResultTable(final JCoTable jcoResult, final List<GenilSimpleObjectInstance> objectInstances)
	{

		GenilSimpleObjectInstance resultObject = null;

		for (int i = 0; i < jcoResult.getNumRows(); i++)
		{
			jcoResult.setRow(i);

			final String rtGuid = jcoResult.getString("OBJECT_ID");
			final String objName = jcoResult.getString("OBJECT_NAME");
			final char idIsHandle = jcoResult.getChar("ID_IS_HANDLE");

			if (idIsHandle == 'X')
			{
				resultObject = new GenilSimpleObjectInstance(rtGuid, objName, true);
			}
			else
			{
				resultObject = new GenilSimpleObjectInstance(rtGuid, objName, false);
			}

			objectInstances.add(resultObject);
		}
	}

	private List<GenilRootDataContainer> executeGenilFunction(final GenilExecuteRequestContainer executeReq,
			final List<GenilSimpleObjectInstance> failedObjects, final List<GenilSimpleObjectInstance> changedObjects)
			throws GenilBackendException
	{
		/*
		 * object-specific methods such as copy, consistency checks, etc... the method has to be written in a very generic
		 * style to allow arbitrary input and output parameters in order to fulfill the needs...
		 */
		final String traceMethodName = "execute()";
		final long traceTime = traceEntering(traceMethodName);

		final GenilRFC rfc = GenilFeature.EXEC_BO_METHOD.getRFCStateFul();
		final RFCInput rfcInput = new RFCInput(rfc);
		rfcInput.addImportParameter("IV_OBJECT_NAME", executeReq.getGenilDataContainer().getObjectName());
		rfcInput.addImportParameter("IV_METHOD_NAME", executeReq.getMethodName());

		rfcInput.addTablesParameter("IT_OBJECT_LIST", 0, "OBJECT_NAME", executeReq.getGenilDataContainer().getObjectName());
		rfcInput.addTablesParameter("IT_OBJECT_LIST", 0, "OBJECT_ID", executeReq.getGenilDataContainer().getRtGuid());
		rfcInput.addTablesParameter("IT_OBJECT_LIST", 0, "ID_IS_HANDLE", GenilConst.ABAP_FALSE);

		// fill input parameter
		final TablesParam tp = new TablesParam("IT_PARAMETERS");
		rfcInput.addTablesParameter("IT_PARAMETERS", tp);
		for (final Map.Entry<String, String> entry : executeReq.getParameters().entrySet())
		{
			final TableParamRow tpr = new TableParamRow();
			tpr.add(new NameValuePair("NAME", entry.getKey()));
			tpr.add(new NameValuePair("VALUE", entry.getValue()));
			tp.addRow(tpr);
		}

		// Result Collection
		rfcInput.addRequestedTables("ET_DATA_HDR");
		rfcInput.addRequestedTables("ET_DATA_ATTR");
		rfcInput.addRequestedTables("ET_DATA_RELS");
		rfcInput.addRequestedTables("ET_DATA_REL_OBJ");
		// Changed Objects
		rfcInput.addRequestedTables("ET_CHANGED_OBJ");
		// Failed Objects
		rfcInput.addRequestedTables("ET_FAILED_OBJ");

		final RFCOutput rfcOutput = rfcWrapper.doRFC(rfcInput);

		JCoTable resultTable = null;
		if (failedObjects != null)
		{
			resultTable = rfcOutput.getTablesParameter("ET_FAILED_OBJ");
			fillRFCResultTable(resultTable, failedObjects);
		}

		if (changedObjects != null)
		{
			resultTable = rfcOutput.getTablesParameter("ET_CHANGED_OBJ");
			fillRFCResultTable(resultTable, changedObjects);
		}

		if (rfcOutput.isSuccess())
		{
			final List<GenilRootDataContainer> result = fillContainer(rfcOutput, null);
			invalidateContainer(rfcOutput, executeReq.getGenilDataContainer());
			getMessages(null);
			traceExiting(traceTime, traceMethodName);
			return result;
		}
		traceExiting(traceTime, traceMethodName);
		return Collections.emptyList();
	}

	/**
	 * Returns a GenilCreateRootRequestContainer for issuing a GenIL create<br>
	 *
	 * @param rootObjectName
	 * @return GenilCreateRootRequestContainer
	 * @throws GenilBackendException
	 */
	public GenilCreateRootRequestContainer getCreateRootRequestContainer(final String rootObjectName) throws GenilBackendException
	{
		// model check inside constructor
		return new GenilCreateRootRequestContainer(rootObjectName, this);

	}

	protected GenilMessageRequestContainer getDefaultMsgReqContainer()
	{
		final GenilMessageRequestContainer result = getMessageRequestContainer();
		result.setGlobalMessages(true);
		result.setObjectMessages(true);
		result.setMsgType(GenilMessageType.ALL);
		result.setMsgLevel(GenilConst.MESSAGE_LEVEL_CUSTOMER);
		return result;
	}

	/**
	 * Creates a request container for the execBOMethod<br>
	 *
	 * @param cont
	 *           coressponds to a genil object
	 * @param methodName
	 *           method to be executed
	 * @return
	 * @throws GenilBackendException
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxyStateAware#getExecBoMethodRequestContainer(com.sap.wec.tc.core.backend.genil.GenilDataContainer,
	 *      java.lang.String)
	 */
	public GenilExecuteRequestContainer getExecBoMethodRequestContainer(final GenilDataContainer cont, final String methodName)
			throws GenilBackendException
	{
		// model check inside constructor
		return new GenilExecuteRequestContainer(cont, methodName, this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxyStateAware#getMessages(com
	 * .sap.wec.tc.core.backend.genil.GenilMessageRequestContainer,
	 * com.sap.wec.tc.core.backend.genil.GenilRootDataContainer)
	 */
	@Override
	public void getMessages(final GenilMessageRequestContainer msgReq, final GenilRootDataContainer rootCont)
			throws GenilBackendException
	{
		super.getMessages(msgReq, rootCont);
	}

	protected void getMessages(final GenilRootDataContainer rootCont) throws GenilBackendException
	{
		if (!msgOnRequest)
		{
			if (defaultMsgReq == null)
			{
				defaultMsgReq = getDefaultMsgReqContainer();
			}
			getMessages(defaultMsgReq, rootCont);
		}
	}

	@Override
	protected RFCInput getRFCInput(final GenilFeature feature)
	{
		return new RFCInput(feature.getRFCStateFul());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxyStateAware#init(com.sap.
	 * wec.tc.core.backend.genil.GenilRootDataContainer)
	 */
	public boolean init(final GenilRootDataContainer rootCont) throws GenilBackendException
	{
		final String traceMethodName = "modify()";
		logger.getInstance(traceMethodName);

		final Set<GenilRootDataContainer> rootConts = new HashSet<GenilRootDataContainer>();
		rootConts.add(rootCont);

		return init(rootConts);
	}

	public boolean init(final Set<GenilRootDataContainer> rootConts) throws GenilBackendException
	{
		final GenilRFC rfc = GenilFeature.INIT.getRFCStateFul();
		final Set<GenilObjInstance> objInstances = buildObjInstances(rootConts);

		return doObjInstances(rfc, objInstances).isSuccess();
	}

	@Override
	protected boolean synchronizeAbapModel(final char componentSetLoadLevel) throws GenilBackendException
	{
		if (!isInitialized)
		{
			RFCInput rfcInput = null;
			if (componentSetLoadLevel == GenilConst.COMPONENT_SET_INIT_AND_LOAD)
			{
				rfcInput = getRFCInput(GenilFeature.INITIALIZE);
				rfcInput.addImportParameter("IV_COMPONENT_SET", componentSet);
				if (rfcWrapper.doRFC(rfcInput).isSuccess())
				{
					isInitialized = true;
				}
			}
			else
			{
				checkComponentIntialize(componentSetLoadLevel);
			}
		}
		return isInitialized;
	}

	/**
	 * @param componentSetLoadLevel
	 * @throws GenilBackendException
	 */
	private void checkComponentIntialize(final char componentSetLoadLevel) throws GenilBackendException
	{
		RFCInput rfcInput;
		if (componentSetLoadLevel == GenilConst.COMPONENT_SET_LOAD)
		{
			rfcInput = getRFCInput(GenilFeature.LOAD);
			rfcInput.addImportParameter("IV_COMPONENT_SET", componentSet);
			if (rfcWrapper.doRFC(rfcInput).isSuccess())
			{
				isInitialized = true;
			}
		}
		else
		{
			isInitialized = true;
		}
	}

	@Override
	protected void synchronizeJavaModel(final boolean ignoreModelErrors) throws GenilBackendException
	{
		if (isInitialized)
		{
			readModel(ignoreModelErrors);
		}
		else
		{
			final String msg = "Model is not initialized, loadModel not possible";
			throw new GenilBackendException(msg);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxyStateAware#load(java.lang .String, java.lang.String)
	 */
	public boolean load(final String componentSet, final String component) throws GenilBackendException
	{
		final RFCInput rfcInput = getRFCInput(GenilFeature.LOAD);

		if (componentSet != null && componentSet.length() > 0)
		{
			rfcInput.addImportParameter("IV_COMPONENT_SET", componentSet);
		}
		else if (component != null && component.length() > 0)
		{
			rfcInput.addImportParameter("IV_COMPONENT", component);
		}

		return rfcWrapper.doRFC(rfcInput).isSuccess();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxyStateAware#lock(com.sap.
	 * wec.tc.core.backend.genil.GenilRootDataContainer)
	 */
	public boolean lock(final GenilRootDataContainer rootCont, final boolean doReRead) throws GenilBackendException
	{
		final Set<GenilRootDataContainer> rootConts = new HashSet<GenilRootDataContainer>();
		rootConts.add(rootCont);
		return lock(rootConts, doReRead);
	}

	public boolean lock(final Set<GenilRootDataContainer> rootConts, final boolean doReRead) throws GenilBackendException
	{
		final String traceMethodName = "lock()";
		final long traceTime = traceEntering(traceMethodName);

		final GenilRFC rfc = GenilFeature.LOCK.getRFCStateFul();

		for (final GenilRootDataContainer rootCont : rootConts)
		{
			if (rootCont.isLocked())
			{
				rootConts.remove(rootCont);
			}
		}

		final Set<GenilObjInstance> objInstances = buildObjInstances(rootConts);
		final boolean success = doObjInstances(rfc, objInstances).isSuccess();

		if (success)
		{
			for (final GenilRootDataContainer rootCont : rootConts)
			{
				rootCont.setLocked(true);
				getMessages(rootCont);

				if (doReRead)
				{
					reRead(rootCont, false);
				}

			}
		}
		traceExiting(traceTime, traceMethodName);
		return success;
	}

	public boolean lock(final GenilRootDataContainer rootCont) throws GenilBackendException
	{
		return lock(rootCont, false);
	}

	public boolean lock(final Set<GenilRootDataContainer> rootConts) throws GenilBackendException
	{
		return lock(rootConts, false);
	}

	/**
	 * All attributes which have set the flag 'A' will be updated. The container will then be automatically invalidated
	 * (flag DELTA_MODIFIED = 'M'). Additionally, all appendant child objects will be automatically invalidated (flag
	 * DELTA_MODIFIED = 'M'). Then a reread will be triggered on all invalidated objects. <br>
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxyStateAware#modify(com.sap.wec.tc.core.backend.genil.GenilRootDataContainer,
	 *      boolean)
	 */

	public boolean modify(final GenilRootDataContainer rootCont, final boolean doReRead) throws GenilBackendException
	{
		final GenilMessageRequestContainer msgReq = null;
		return modify(rootCont, doReRead, msgReq);
	}

	@Override
	public boolean modify(final GenilRootDataContainer rootCont, final boolean doReRead, GenilMessageRequestContainer msgReq)
			throws GenilBackendException
	{
		/*
		 * DELTA_MODIFIED = 'M' for container is set and the container will be updated (modified). Only the attributes
		 * which have been changed will be updated (the attributes which have the flag CHANGED = 'A' set). All depending
		 * children will be invalidated too. reRead(..) is triggered after that. Create Children, Delete Children, Modify
		 * Children are separate use cases... It is most probably a modify on the root object with specific delta
		 * handling...
		 */
		final String traceMethodName = "modify()";
		final long traceTime = traceEntering(traceMethodName);
		GenilMessageRequestContainer msgR=msgReq;
		if (rootCont == null)
		{
			return false;

		}

		RFCOutput rfcOutput;
		final RFCInput rfcInput = prepareModifyRequest(rootCont);

		if (!msgOnRequest)
		{
			msgR = getDefaultMsgReqContainer();
		}

		rfcOutput = checkMsgReq(rootCont, doReRead, msgR, rfcInput);

		if (rfcOutput.isSuccess())
		{
			// Handle ET_ID_MAPPING
			final JCoTable chObj = rfcOutput.getTablesParameter("ET_ID_MAPPING");
			handleEtIdMapping(rootCont, chObj);
			traceExiting(traceTime, traceMethodName);
			return true;
		}
		traceExiting(traceTime, traceMethodName);
		return false;
	}

	/**
	 * @param rootCont
	 * @param doReRead
	 * @param msgReq
	 * @param rfcInput
	 * @return
	 * @throws GenilBackendException
	 */
	private RFCOutput checkMsgReq(final GenilRootDataContainer rootCont, final boolean doReRead,
			GenilMessageRequestContainer msgReq, final RFCInput rfcInput) throws GenilBackendException
	{
		RFCOutput rfcOutput;
		if (msgReq != null)
		{
			fillMessageAttributes(msgReq, rfcInput);
		}

		rfcOutput = rfcWrapper.doRFC(rfcInput);
		invalidateContainer(rfcOutput, rootCont);
		if (msgReq != null)
		{
			fillMessages(rfcOutput, rootCont.getRtGuid());
		}

		if (doReRead)
		{
			reRead(rootCont, false);
		}
		return rfcOutput;
	}

	/**
	 * @param rootCont
	 * @param chObj
	 */
	private void handleEtIdMapping(final GenilRootDataContainer rootCont, final JCoTable chObj)
	{
		for (int i = 0; i < chObj.getNumRows(); i++)
		{
			chObj.setRow(i);
			// get data from the table parameter
			final String objectName = chObj.getString("OBJECT_NAME");
			final String oldRtGuid = chObj.getString("OLD_ID");
			final String newRtGuid = chObj.getString("NEW_ID");

			// get all children and check if something must be updated
			final List<GenilDataContainer> allChilds = rootCont.getAllChilds();
			final ListIterator<GenilDataContainer> iterator = allChilds.listIterator();
			GenilDataContainer child = null;
			while (iterator.hasNext())
			{
				child = iterator.next();
			}
			if (child != null && child.getObjectName().compareTo(objectName) == 0 && child.getRtGuid() != null
					&& child.getRtGuid().compareTo(oldRtGuid) == 0)
			{
				child.setRtGuid(newRtGuid);

				// add the updated object (new GUID) into the Object list
				rootCont.getAllObjectsByRTGuid().put(objectName + newRtGuid,
						rootCont.getAllObjectsByRTGuid().get(objectName + oldRtGuid));
				// remove the old object (old GUID) from the Object list
				rootCont.getAllObjectsByRTGuid().remove(objectName + oldRtGuid);

			}
			setNewId(child, objectName, oldRtGuid, newRtGuid);
		}
	}

	/**
	 * Update for all children whose the ID/GUID are changed to the new value and also update all relations according to
	 * the new ID/GUID
	 *
	 * @param parent
	 *           the current parent
	 * @param objectName
	 *           The objectName of the changed child
	 * @param oldRtGuid
	 *           The old ID/GUID of the changed child
	 * @param newRtGuid
	 *           The new ID/GUID of the changed child
	 */
	private void setNewId(final GenilDataContainer parent, final String objectName, final String oldRtGuid, final String newRtGuid)
	{
		final List<GenilDataContainer> childs = parent.getAllChilds();
		final Iterator<GenilDataContainer> iterator = childs.iterator();
		while (iterator.hasNext())
		{
			final GenilDataContainer child = iterator.next();
			if (child.getObjectName().compareTo(objectName) == 0 && child.getRtGuid() != null
					&& child.getRtGuid().compareTo(oldRtGuid) == 0)
			{
				child.setRtGuid(newRtGuid);

				// add the updated object (new GUID) into the Object list
				parent.getRoot().getAllObjectsByRTGuid()
						.put(objectName + newRtGuid, parent.getRoot().getAllObjectsByRTGuid().get(objectName + oldRtGuid));
				// remove the old object (old GUID) from the Object list
				parent.getRoot().getAllObjectsByRTGuid().remove(objectName + oldRtGuid);

			}

			// update relation table with the new GUID
			// collect all the changes
			final Map<String, Map<String, GenilDataContainer>> updatedRelation = new HashMap<String, Map<String, GenilDataContainer>>();
			final List<String> deletedKeys = new ArrayList<String>();
			boolean changed = false;
			for (final Entry<String, List<GenilDataContainer>> entry : child.getRelationsByName().entrySet())
			{
				final String key = entry.getKey() + oldRtGuid;
				if (child.getRelationsByRTGuid().containsKey(key))
				{
					updatedRelation.put(entry.getKey() + newRtGuid, child.getRelationsByRTGuid().get(key));
					deletedKeys.add(entry.getKey() + oldRtGuid);
					changed = true;
				}
				else
				{
					updatedRelation.put(key, child.getRelationsByRTGuid().get(key));
				}
				deletedKeys.add(key);
			}
			// if something has changed then update the
			if (changed)
			{
				// update the relations with the new values
				child.getRelationsByRTGuid().clear();
				child.setRelationsByRTGuid(updatedRelation);
				child.getRelations().clear();
				child.setRelations(updatedRelation);
				// delete relation on root level which has the old GUID in the
				// key (part of the key)
				for (int i = 0; i < deletedKeys.size(); i++)
				{
					child.getRoot().getRelationsByRTGuid().remove(deletedKeys.get(i));
					child.getRoot().getRelations().remove(deletedKeys.get(i));
				}
				// insert all relation on root level with the new GUID in the
				// key (part of the key)
				child.getRoot().getRelationsByRTGuid().putAll(updatedRelation);
				child.getRoot().getRelations().putAll(updatedRelation);

			}
			setNewId(child, objectName, oldRtGuid, newRtGuid);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.wec.tc.core.backend.genil.GenilProxy#read(com.sap.wec.tc.core .backend.genil.GenilRootDataContainer,
	 * com.sap.wec.tc.core.backend.genil.GenilReadRootRequestContainer)
	 */
	@Override
	public GenilRootDataContainer read(final GenilReadRootRequestContainer rootReq, final GenilMessageRequestContainer msgReq)
			throws GenilBackendException
	{
		// at the moment, statefull read functions do not return messages, so
		// call messageles read and get messages in second step
		final GenilRootDataContainer rootCont = super.read(rootReq);
		getMessages(msgReq, rootCont);
		return rootCont;
	}

	@Override
	public void read(final GenilRootDataContainer rootCont, final GenilReadRootRequestContainer rootReq,
			final GenilMessageRequestContainer msgReq) throws GenilBackendException
	{
		// at the moment, statefull read functions do not return messages, so
		// call messageles read and get messages in second step
		super.read(rootCont, rootReq);
		getMessages(msgReq, rootCont);
	}

	/**
	 * The search operation can function like a normal search or dynamic search depending on query request settings<br>
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxy#search(com.sap.wec.tc.core.backend.genil.GenilQueryRequestContainer)
	 */
	@Override
	public List<GenilRootDataContainer> search(final GenilQueryRequestContainer queryReq, final GenilMessageRequestContainer msgReq)
			throws GenilBackendException
	{

		final GenilRootDataContainer rootCont = null;
		final List<GenilRootDataContainer> searchResult = super.search(queryReq);
		getMessages(msgReq, rootCont);
		return searchResult;
	}

	/**
	 * Reads the attributes depending on the invalidation flag of the root object or its child objects. Only the objects
	 * in the container are reread. <br>
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxyStateAware#reRead(com.sap.wec.tc.core.backend.genil.GenilRootDataContainer,
	 *      boolean)
	 * @param forceReadAll
	 *           - Forces reread of all objects in the container, regardless of invalidation flag
	 * @param dataCont
	 * @return
	 * @throws GenilBackendException
	 */

	public void reRead(final GenilRootDataContainer rootCont, final boolean forceReadAll) throws GenilBackendException
	{
		final String traceMethodName = "reRead()";
		final long traceTime = traceEntering(traceMethodName);

		if (rootCont == null)
		{
			return;
		}

		final String objectName = rootCont.getObjectName();
		final String rtGuid = rootCont.getRtGuid();
		GenilReadRootRequestContainer reqCont = new GenilReadRootRequestContainer(objectName, rtGuid, this);
		if (forceReadAll || rootCont.getLastGenilReadRootRequest() == null)
		{
			prepareChildReReadRequest(rootCont, reqCont, forceReadAll);
		}
		else
		{
			// get the saved root request container from the last read
			reqCont = rootCont.getLastGenilReadRootRequest();
		}

		RFCOutput rfcOutput;
		final RFCInput rfcInput = prepareReadRequest(reqCont);
		rfcOutput = rfcWrapper.doRFC(rfcInput);
		getMessages(rootCont);
		if (rfcOutput.isSuccess())
		{
			rootCont.resetDataContainer();
			fillContainer(rfcOutput, rootCont);

			// reset the invalid flag, because root container is the only reused
			// container, so invalid is not resetted automatically
			rootCont.setInvalid(false);
		}
		traceExiting(traceTime, traceMethodName);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxyStateAware#reset()
	 */
	public boolean reset() throws GenilBackendException
	{
		final String traceMethodName = "reset()";
		logger.getInstance(traceMethodName);

		final RFCInput rfcInput = getRFCInput(GenilFeature.RESET);
		rfcInput.addImportParameter("IV_INCLUDING_KEY_MAPPING", GenilConst.ABAP_TRUE);


		return rfcWrapper.doRFC(rfcInput).isSuccess();
	}

	private boolean rollback() throws GenilBackendException
	{
		final GenilRFC rfc = GenilFeature.ROLLBACK.getRFCStateFul();
		return rfcWrapper.doRFC(rfc).isSuccess();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxyStateAware#save(com.sap.
	 * wec.tc.core.backend.genil.GenilRootDataContainer)
	 */
	public boolean save(final GenilRootDataContainer rootCont) throws GenilBackendException
	{
		final GenilMessageRequestContainer msgReq = null;
		final Set<GenilRootDataContainer> rootConts = new HashSet<GenilRootDataContainer>();
		rootConts.add(rootCont);
		return save(rootConts, msgReq);
	}

	@Override
	public boolean save(final GenilRootDataContainer rootCont, final GenilMessageRequestContainer msgReq)
			throws GenilBackendException
	{
		final Set<GenilRootDataContainer> rootConts = new HashSet<GenilRootDataContainer>();
		rootConts.add(rootCont);
		return save(rootConts, msgReq);
	}

	/**
	 * Saves a set GenIL root objects<br>
	 *
	 * @param rootConts
	 * @return <code>true</code> only if the save operation was successfully executed in the GenIL layer.
	 */
	public boolean save(final Set<GenilRootDataContainer> rootConts) throws GenilBackendException
	{
		final GenilMessageRequestContainer msgReq = null;
		return save(rootConts, msgReq);
	}

	@Override
	public boolean save(final Set<GenilRootDataContainer> rootConts, GenilMessageRequestContainer msgReqs)
			throws GenilBackendException
	{
		GenilMessageRequestContainer msgReqst=msgReqs;
		boolean success = false;
		final String traceMethodName = "save()";
		final long traceTime = traceEntering(traceMethodName);

		final Set<GenilObjInstance> objInstances = buildObjInstances(rootConts);
		final GenilRFC rfc = GenilFeature.SAVE.getRFCStateFul();

		if (!msgOnRequest)
		{
			msgReqst = getDefaultMsgReqContainer();
		}

		success = doObjInstances(rfc, objInstances, msgReqst).isSuccess();
		if (success)
		{
			this.commit();
		}
		else
		{
			this.rollback();
		}

		if (!msgOnRequest)
		{
			for (final GenilRootDataContainer root : rootConts)
			{
				getMessages(root);
			}
		}
		traceExiting(traceTime, traceMethodName);
		return success;
	}

	public void setMsgOnRequest(final boolean msgOnRequest)
	{
		this.msgOnRequest = msgOnRequest;
	}

}
