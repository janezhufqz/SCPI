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

import de.hybris.platform.sap.core.configuration.SAPConfigurationService;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;






import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoTable;
import com.sap.genil.GenilConst.GenilFeature;
import com.sap.genil.GenilConst.GenilMessageType;
import com.sap.genil.GenilConst.GenilRFC;
import com.sap.genil.exception.GenilBackendException;
import com.sap.genil.rfc.GenilObjInstance;
import com.sap.genil.rfc.GenilRFCWrapper;
import com.sap.genil.rfc.ImportStructure;
import com.sap.genil.rfc.NameValuePair;
import com.sap.genil.rfc.RFCInput;
import com.sap.genil.rfc.RFCOutput;
import com.sap.genil.rfc.TableParamRow;
import com.sap.genil.rfc.TablesParam;


/**
 * This abstract class provides the implementation of IGenilProxy and will act as mother class for stateful and
 * stateless proxiess<br>
 *
 * @author SAP
 * @version 1.0
 *
 * @author C5230248 -- Modified by Removing WCFLocation -- Modified by Removing Genil JCoConnection and added Hybris
 *         JCoConnection,SAPConfigurationService
 */

abstract class GenilProxy implements IGenilProxy
{
	private static final String DELTA_FLAG = "DELTA_FLAG";
	private static final String OBJECT_NAME = "OBJECT_NAME";
	private static final String OBJECT_ID = "OBJECT_ID";
	
	private final GenilModel genilModel;
	private long lastHandleCounter = 1;
	private static final Log4JWrapper logger = Log4JWrapper.getInstance(GenilProxy.class.getName());
	SAPConfigurationService sapCoreConfigurationService;

	protected class ObjModel {
		String rtGuid;
		String objectName;
		ObjModel parent;
		int attrFrom;
		int attrTo;
		char deltaFlag;
		Map<String, List<ObjModel>> rels = new HashMap<String, List<ObjModel>>();

		@Override
		public String toString()
		{
			return "[ObjModel: " + objectName + "]";
		}
	}
	
	protected class RecursiveCount {
		int cAttr = 0;
		int cRel = 0;
		int cObj = 0;
		int cPathCount = 0;
	}

	/**
	 * Application name, i.e. "BP_APPL", "PROD_APPL", ...)
	 */
	protected String componentSet;

	/**
	 * The proxy needs to be initialized with a stateful connection. The RFC GenIL needs the initialization routines run
	 * first, before any request is submitted.
	 */
	protected boolean isInitialized = false;

	private boolean isIgnoringModelErrors = false;

	/**
	 * SAP Java Connector connection
	 */
	protected JCoConnection connection;

	protected GenilRFCWrapper rfcWrapper;

	protected Properties properties;
	/**
	 * Message Container Manager attached to the Genil Proxy
	 */
	protected GenilMessageContainerManager msgContManager;

	
	/**
	 * Controls if messages should be automatically asked for after each operation e.g read, modify or not
	 */

	protected boolean msgOnRequest;
	private String scopeType = null;

	public String getScopeType()
	{
		return scopeType;
	}

	/**
	 * Setter for scope type.
	 *
	 * @param scopeType
	 *           the scopeType to set
	 */
	public void setScopeType(final String scopeType)
	{
		this.scopeType = scopeType;
	}

	/**
	 * Standard constructor. <br>
	 *
	 * @param connection
	 * @param application
	 * @param ingoreModelErrors
	 * @throws GenilBackendException
	 */
	protected GenilProxy(final JCoConnection connection, final String componentSet, final boolean ignoreModelErrors,
			final char componentSetLoadLevel) throws GenilBackendException, JCoException
	{

		super();
		if (connection == null)
		{
			final String msg = "A JCO Connection is required";
			throw new GenilBackendException(msg);
		}

		this.connection = connection;

		this.componentSet = componentSet;
		this.rfcWrapper = new GenilRFCWrapper(this.connection);

		this.msgContManager = new GenilMessageContainerManager();

		genilModel = new GenilModel();
		synchronizeAbapModel(componentSetLoadLevel);
		synchronizeJavaModel(ignoreModelErrors);
	}


	/**
	 * Builds up the internal object tree using model data
	 *
	 * @param header
	 *           JCoTable with header data
	 * @param relations
	 *           JCoTable with relations between header and objects
	 * @param objects
	 *           JCoTable with related objects
	 * @return
	 * @throws GenilBackendException
	 */
	protected List<ObjModel> buildObjectTree(final JCoTable header, final JCoTable relations, final JCoTable objects)
			throws GenilBackendException
	{

		final Map<String, ObjModel> allObjects = new HashMap<String, ObjModel>();
		// For all objects

		// ensure that the model is loaded to be able to read the target object
		
		final GenilModel model = genilModel;
		
		if (!model.isComplete(componentSet))
		{
			readModel(this.isIgnoringModelErrors);
		}

		for (int i = 0; i < header.getNumRows(); i++)
		{
			header.setRow(i);

			final String rtGuid = header.getString(OBJECT_ID);
			final String objName = header.getString(OBJECT_NAME);
			final char deltaFlag = header.getChar(DELTA_FLAG);

			// key is ObjId+Name
			ObjModel objModel = allObjects.get(rtGuid + objName);
			if (objModel == null)
			{
				objModel = new ObjModel();

				// key is ObjId+Name
				allObjects.put(rtGuid + objName, objModel);

				objModel.rtGuid = rtGuid;
				objModel.objectName = objName;
				objModel.deltaFlag = deltaFlag;
			}
			// key is ObjId+Name
			

			// Which attributes belongs to this object
			objModel.attrFrom = Integer.decode(header.getString("ATTR_FROM")) - 1;
			objModel.attrTo = Integer.decode(header.getString("ATTR_TO")) - 1;

			// Which relations belongs to this object
			final int relFrom = Integer.decode(header.getString("REL_FROM")) - 1;
			final int relTo = Integer.decode(header.getString("REL_TO")) - 1;

			relationsSetRow(relations, objects, allObjects, model, objName,
					objModel, relFrom, relTo);
		}
		
		final List<ObjModel> result = getRootObjects(allObjects);

		return result;
	}

	/**
	 * @param relations
	 * @param objects
	 * @param allObjects
	 * @param model
	 * @param objName
	 * @param objModel
	 * @param relFrom
	 * @param relTo
	 */
	@java.lang.SuppressWarnings("squid:S00107")
	private void relationsSetRow(final JCoTable relations,
			final JCoTable objects, final Map<String, ObjModel> allObjects,
			final GenilModel model, final String objName, ObjModel objModel,
			final int relFrom, final int relTo) {
		for (int j = relFrom; j <= relTo; j++)
		{
			relations.setRow(j);

			// Which objects are assigned to this relation
			final int obj_from = Integer.decode(relations.getString("OBJECTS_FROM")) - 1;
			final int obj_to = Integer.decode(relations.getString("OBJECTS_TO")) - 1;
			if (obj_from > obj_to)
			{
				continue;
			}

			final List<ObjModel> childs = new ArrayList<ObjModel>();
			final String relName = relations.getString("RELATION_NAME");

			objModel.rels.put(relName, childs);
			for (int k = obj_from; k <= obj_to; k++)
			{
				objects.setRow(k);

				final String relObjectGuid = objects.getString(OBJECT_ID);
				String relObjName = getRelationObjectName(model,objName,relName);

				

				// Do not create object if the are not in the model. This
				// can happen, when somer objects are removed by swtich

				checkCreationObject(allObjects, objModel, childs,
						relObjectGuid, relObjName);
			}
		}
	}

	/**
	 * @param allObjects
	 * @param objModel
	 * @param childs
	 * @param relObjectGuid
	 * @param relObjName
	 */
	private void checkCreationObject(final Map<String, ObjModel> allObjects,
			ObjModel objModel, final List<ObjModel> childs,
			final String relObjectGuid, String relObjName) {
		if (relObjName != null)
		{
			// create the object only if the object is not already
			// created via another relation!
			ObjModel relObj = allObjects.get(relObjectGuid + relObjName);
			if (relObj == null)
			{
				relObj = createObject(relObjectGuid, relObjName);
			}

			relObj.parent = objModel;
			childs.add(relObj);

			// key is ObjId+Name
			allObjects.put(relObj.rtGuid + relObj.objectName, relObj);
		}
		else
		{
logger.getLogger();
		}
	}

	private List<ObjModel> getRootObjects(final Map<String, ObjModel> allObjects) {
		final List<ObjModel> result = new ArrayList<ObjModel>();
		for (final ObjModel objModel : allObjects.values())
		{
			if (objModel.parent == null)
			{
				result.add(objModel);
			}
		}
		return result;
	}
	
	private String getRelationObjectName(GenilModel model,String objName, String relName){
		// read the relations of the parent object and compare them
		// with the given relation to determine the name of the
		// child object
		final GenilModelObject parentObject = model.getObject(objName);
		
		String relObjName = null;
		for (final Iterator<GenilModelRelation> childRelationIterator = parentObject.getAllChildRelations().iterator(); childRelationIterator
				.hasNext();)
		{
			final GenilModelRelation childRelation = childRelationIterator.next();
			final String childRelationName = childRelation.getRelation();

			if (childRelationName.equals(relName))
			{
				final String objectA = childRelation.getObjectA().getObjectName().toString();
				final String objectB = childRelation.getObjectB().getObjectName().toString();

				if (objectA.equals(objName))
				{
					relObjName = objectB.toString();
				}
				else
				{
					relObjName = objectA.toString();
				}

			}

		}
		
		return relObjName;
	}

	private ObjModel createObject(final String rtGuid, final String objName) {
		ObjModel objModel = new ObjModel();
		
		objModel.rtGuid = rtGuid;
		objModel.objectName = objName;
		
		return objModel;
	}

	protected Set<GenilObjInstance> buildObjInstances(final Set<GenilRootDataContainer> rootConts)
	{
		final Set<GenilObjInstance> objInstances = new HashSet<GenilObjInstance>();
		for (final GenilRootDataContainer root : rootConts)
		{
			objInstances.add(new GenilObjInstance(root.getObjectName(), root.getRtGuid()));
		}
		return objInstances;
	}

	protected RFCOutput doObjInstance(final GenilRFC rfc, final GenilRootDataContainer dataCont) throws GenilBackendException
	{
		final GenilMessageRequestContainer msgReq = null;
		return doObjInstance(rfc, dataCont, msgReq);
	}

	protected RFCOutput doObjInstance(final GenilRFC rfc, final GenilRootDataContainer dataCont,
			final GenilMessageRequestContainer msgReq) throws GenilBackendException
	{
		final GenilObjInstance objInstance = new GenilObjInstance(dataCont.getObjectName(), dataCont.getRtGuid());
		final Set<GenilObjInstance> objInstances = new HashSet<GenilObjInstance>();
		objInstances.add(objInstance);

		return doObjInstances(rfc, objInstances, msgReq);
	}

	protected RFCOutput doObjInstances(final GenilRFC rfc, final Set<GenilObjInstance> objInstances) throws GenilBackendException
	{
		final GenilMessageRequestContainer msgReq = null;
		return doObjInstances(rfc, objInstances, msgReq);
	}

	protected RFCOutput doObjInstances(final GenilRFC rfc, final Set<GenilObjInstance> objInstances,
			final GenilMessageRequestContainer msgReq) throws GenilBackendException
	{
		final RFCInput rfcInput = new RFCInput(rfc);

		int i = 0;
		for (final GenilObjInstance objInst : objInstances)
		{
			rfcInput.addTablesParameter("IT_OBJECT_LIST", i, OBJECT_NAME, objInst.getObjectName());
			rfcInput.addTablesParameter("IT_OBJECT_LIST", i, OBJECT_ID, objInst.getObjectId());
			rfcInput.addTablesParameter("IT_OBJECT_LIST", i, "ID_IS_HANDLE", objInst.idIsHandle());
			i++;
		}
		rfcInput.addRequestedTables("ET_FAILED_OBJ");

		if (msgReq != null)
		{
			fillMessageAttributes(msgReq, rfcInput);
		}

		RFCOutput result;
		result = rfcWrapper.doRFC(rfcInput);

		// if messages are requested return related container
		if (msgReq != null)
		{
			fillMessages(result);
		}

		if (result.isSuccess())
		{
			final JCoTable jcoTable = result.getTablesParameter("ET_FAILED_OBJ");
			if (jcoTable != null && jcoTable.getNumRows() > 0)
			{
				result.setSuccess(false);
			}
		}

		return result;
	}

	protected void fillContainer(final JCoTable attributes, final GenilDataContainer parent, final ObjModel objModel)
			throws GenilBackendException
	{
		

		if (parent == null || !parent.getRtGuid().equals(objModel.rtGuid))
		{
			return;
		}

		// Valid Object?
		final GenilModelObject modelObject = getModel().getObject(objModel.objectName);
		if (modelObject == null)
		{
			return;
		}

		// No attributes requested -> AttrFrom <0
		if (objModel.attrFrom >= 0)
		{

			processAttributes(attributes, parent, objModel, modelObject);

		}
		else
		{
           logger.getLogger();
		}
		parent.setInvalid(false);

		for (final Map.Entry<String, List<ObjModel>> entry : objModel.rels.entrySet())
		{
			for (final ObjModel relObj : entry.getValue())
			{
				GenilDataContainer child = parent.getChildByRtGuid(entry.getKey(), relObj.rtGuid);
				if (child == null)
				{
					child = parent.addChild(entry.getKey(), relObj.rtGuid);
				}
				fillContainer(attributes, child, relObj);
			}
		}
	}

	private void processAttributes(final JCoTable attributes,
			final GenilDataContainer parent, final ObjModel objModel,
			final GenilModelObject modelObject) {
		// Set Attributes
		for (int j = objModel.attrFrom; j <= objModel.attrTo; j++)
		{
			attributes.setRow(j);
			final int vIndex = Integer.decode(attributes.getString("VINDEX"));
			final String value = attributes.getString("VALUE");
			char property = attributes.getString("PROPERTY").toCharArray()[0];

			final String attrName = modelObject.getAttrByIndex(vIndex).getName();
			GenilAttribute attr = parent.getAttribute(attrName);
			
			if (attr == null)
			{
				if (value != null || property != 'C')
				{
					// Create Attribute
					attr = new GenilAttribute(attrName, value, property);
					parent.addAttribute(attr);
				}
			}
			else
			{
				// Update Attribue
				attr.setValueIntern(value);
				attr.setPropertyIntern(property);
			}
		}
	}

	protected List<GenilRootDataContainer> fillContainer(final RFCOutput rfcOutput, final GenilRootDataContainer root)
			throws GenilBackendException
	{
		return fillContainer(rfcOutput, root, false);
	}

	protected List<GenilRootDataContainer> fillContainer(final RFCOutput rfcOutput, final GenilRootDataContainer root,
			final boolean createNew) throws GenilBackendException
	{

		// Object Structure
		final JCoTable header = rfcOutput.getTablesParameter("ET_DATA_HDR");
		final JCoTable attributes = rfcOutput.getTablesParameter("ET_DATA_ATTR");
		final JCoTable relations = rfcOutput.getTablesParameter("ET_DATA_RELS");
		final JCoTable objects = rfcOutput.getTablesParameter("ET_DATA_REL_OBJ");

		final List<GenilRootDataContainer> result = new ArrayList<GenilRootDataContainer>();

		final List<ObjModel> objTree = buildObjectTree(header, relations, objects);
		for (final ObjModel objModel : objTree)
		{

			GenilRootDataContainer currentRoot = root;

			if (currentRoot == null || !currentRoot.getRtGuid().equals(objModel.rtGuid))
			{
				// Create new root container
				final GenilModelObject modelObject = getModel().getObject(objModel.objectName);
				if (modelObject != null
						&& (modelObject.getObjectKind() == GenilModelObjectKind.ROOT
								|| modelObject.getObjectKind() == GenilModelObjectKind.SEARCH_RESULT || modelObject.getObjectKind() == GenilModelObjectKind.ACCESS))
				{
					currentRoot = new GenilRootDataContainer(objModel.objectName, objModel.rtGuid, this, createNew);
					
				}
			}
			if (currentRoot != null && objModel.objectName.compareTo(currentRoot.getObjectName().toString()) >= 0)
			{
				
					fillContainer(attributes, currentRoot, objModel);
					result.add(currentRoot);
				
			}

		}

		return result;
	}

	protected void fillMessages(final RFCOutput rfcOutput)
	{
		fillMessages(rfcOutput, null);
	}

	protected void fillMessages(final RFCOutput rfcOutput, final String rtGuid)
	{
		// fill global messages first
		final JCoTable globalMsgTable = rfcOutput.getTablesParameter("ET_GLOBAL_MESSAGES");
		for (int i = 0; i < globalMsgTable.getNumRows(); i++)
		{
			globalMsgTable.setRow(i);

			final char msgTypeC = globalMsgTable.getChar("TYPE");
			final GenilMessageType msgType = GenilMessageType.getMsgType(msgTypeC);
			final String id = globalMsgTable.getString("ID");
			final int number = globalMsgTable.getInt("NUMBER");
			final String message = globalMsgTable.getString("MESSAGE");
			final String msgVar1 = globalMsgTable.getString("MESSAGE_V1");
			final String msgVar2 = globalMsgTable.getString("MESSAGE_V2");
			final String msgVar3 = globalMsgTable.getString("MESSAGE_V3");
			final String msgVar4 = globalMsgTable.getString("MESSAGE_V4");
			final boolean showOnce = Boolean.getBoolean(globalMsgTable.getString("SHOW_ONCE"));
			final char msgLevel = globalMsgTable.getChar("MESSAGE_LEVEL");

			final GenilMessage msg = new GenilMessage(msgType, id, number, message, msgVar1, msgVar2, msgVar3, msgVar4, showOnce,
					msgLevel);

			msgContManager.getGlobalMessageContainer().addMessage(msg);
		}
		final JCoTable objMsgTable = rfcOutput.getTablesParameter("ET_OBJ_MESSAGES");

		for (int i = 0; i < objMsgTable.getNumRows(); i++)
		{
			objMsgTable.setRow(i);
			final String objectName = objMsgTable.getString(OBJECT_NAME);
			final String guid = objMsgTable.getString(OBJECT_ID);
			final char msgTypeC = objMsgTable.getChar("TYPE");
			final GenilMessageType msgType = GenilMessageType.getMsgType(msgTypeC);
			final String id = objMsgTable.getString("ID");
			final int number = objMsgTable.getInt("NUMBER");
			final String message = objMsgTable.getString("MESSAGE");
			final String msgVar1 = objMsgTable.getString("MESSAGE_V1");
			final String msgVar2 = objMsgTable.getString("MESSAGE_V2");
			final String msgVar3 = objMsgTable.getString("MESSAGE_V3");
			final String msgVar4 = objMsgTable.getString("MESSAGE_V4");
			final boolean showOnce = Boolean.getBoolean(objMsgTable.getString("SHOW_ONCE"));
			final char msgLevel = objMsgTable.getChar("MESSAGE_LEVEL");
			final GenilMessage msg = new GenilMessage(msgType, id, number, message, msgVar1, msgVar2, msgVar3, msgVar4, showOnce,
					msgLevel, objectName, null, guid);

			String containerGuid = null;
			if (guid == null || guid.compareTo(GenilConst.ZERO_GUID) == 0)
			{
				containerGuid = rtGuid;
			}
			else
			{
				// add all unbound messages to the root if given
				containerGuid = guid;
			}

			GenilMessageContainer objMsgCont = msgContManager.getObjectMessageContainer(containerGuid);
			if (objMsgCont == null)
			{
				objMsgCont = new GenilMessageContainer();
				this.msgContManager.setObjectMessageContainer(containerGuid, objMsgCont);
			}
			objMsgCont.addMessage(msg);
		}
	}

	protected List<GenilRootDataContainer> getDQueryResult(final GenilQueryRequestContainer queryReqCont,
			final GenilMessageRequestContainer msgReq) throws GenilBackendException
	{

		final RFCInput rfcInput = prepareDQueryRequest(queryReqCont);

		final GenilReadRootRequestContainer reqCont = queryReqCont.getRequestContainter();
		if (reqCont != null)
		{
			// RFC Intf not yet prepared for it
			throw new GenilBackendException("Relations not yet supported for Search Results");
		}

		rfcInput.addRequestedTables("ET_DATA_HDR");
		rfcInput.addRequestedTables("ET_DATA_ATTR");
		rfcInput.addRequestedTables("ET_DATA_RELS");
		rfcInput.addRequestedTables("ET_DATA_REL_OBJ");

		// if messages are requested fill related container
		if (msgReq != null)
		{
			fillMessageAttributes(msgReq, rfcInput);
		}

		RFCOutput rfcOutput;
		rfcOutput = rfcWrapper.doRFC(rfcInput);

		if (rfcOutput.isSuccess())
		{

			// if messages are requested return related container
			if (msgReq != null)
			{
				fillMessages(rfcOutput);
			}

			return fillContainer(rfcOutput, null);
		}
		return Collections.emptyList();
	}

	/**
	 * Returns the global message container which contains the global GENIL messages send from GENIL ABAP API
	 *
	 * @return global message container
	 */
	public GenilMessageContainer getGlobalMessageContainer()
	{
		return this.msgContManager.getGlobalMessageContainer();
	}

	/**
	 * @return
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxy#isIgnoringModelErrors()
	 */
	public boolean isIgnoringModelErrors()
	{
		return isIgnoringModelErrors;
	}

	protected void setIgnoringModelErrors(final boolean isIgnoringModelErrors)
	{
		this.isIgnoringModelErrors = isIgnoringModelErrors;
	}

	/**
	 * Creates a request container which will be used for getting back messages from genil<br>
	 *
	 * @return
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxyStateAware#getMessageRequestContainer()
	 */
	public GenilMessageRequestContainer getMessageRequestContainer()
	{
		return new GenilMessageRequestContainer();
	}

	/**
	 * Getter-Method for property {@link #model}. <br>
	 *
	 * @return Returns the {@link #model}.
	 */
	GenilModel getModel()
	{
		return genilModel;
	}

	/**
	 * <p>
	 * Provide the property {@link #msgContManager}.
	 * </p>
	 *
	 * @return Returns the {@link #msgContManager} property.
	 */
	GenilMessageContainerManager getMsgContManager()
	{
		return this.msgContManager;
	}

	/**
	 * Returns a GenilQueryRequestContainer for issuing a GenIL query<br>
	 *
	 * @param queryObjectName
	 * @return GenilQueryRequestContainer
	 * @throws GenilBackendException
	 */
	public GenilQueryRequestContainer getQueryRequestContainer(final String queryObjectName) throws GenilBackendException
	{
		// model check inside constructor
		return new GenilQueryRequestContainer(queryObjectName, this);

	}

	protected List<GenilRootDataContainer> getQueryResult(final GenilQueryRequestContainer queryReqCont,
			final GenilMessageRequestContainer msgReq) throws GenilBackendException
	{

		final String traceMethodName = "getQueryResult()";
		final long traceTime = traceEntering(traceMethodName);
		final RFCInput rfcInput = getRFCInput(GenilFeature.GET_QUERY_RESULT);

		prepareQueryRequest(queryReqCont, rfcInput);

		final GenilReadRootRequestContainer reqCont = queryReqCont.getRequestContainter();

		if (reqCont != null)
		{
			// RFC Intf not yet prepared for it
			throw new GenilBackendException("Relations not yet supported for Search Results");
		}

		rfcInput.addRequestedTables("ET_DATA_HDR");
		rfcInput.addRequestedTables("ET_DATA_ATTR");
		rfcInput.addRequestedTables("ET_DATA_RELS");
		rfcInput.addRequestedTables("ET_DATA_REL_OBJ");

		// if messages are requested fill related container
		if (msgReq != null)
		{
			fillMessageAttributes(msgReq, rfcInput);
		}

		RFCOutput rfcOutput;
		rfcOutput = rfcWrapper.doRFC(rfcInput);

		if (rfcOutput.isSuccess())
		{

			// if messages are requested return related container
			if (msgReq != null)
			{
				fillMessages(rfcOutput);
			}
			traceExiting(traceTime, traceMethodName);
			return fillContainer(rfcOutput, null);
		}
		traceExiting(traceTime, traceMethodName);
		return Collections.emptyList();
	}

	protected void prepareQueryRequest(final GenilQueryRequestContainer queryReqCont, final RFCInput rfcInput)
	{
		rfcInput.addImportParameter("IV_QUERY_NAME", queryReqCont.getQueryObjectName());

		int i = 0;
		for (final GenilQueryParameter qp : queryReqCont.getParameters())
		{
			rfcInput.addTablesParameter("IT_PARAMETERS", i, "NAME", qp.getParameterName());
			rfcInput.addTablesParameter("IT_PARAMETERS", i, "VALUE", qp.getLow());

			i++;
		}
	}

	/**
	 * Returns a GeniILReadRootRequestContainer for issuing a GenIL read<br>
	 *
	 * @param rootObjectName
	 * @param rtGuid
	 * @return GenilReadRootRequestContainer
	 * @throws GenilBackendException
	 */
	public GenilReadRootRequestContainer getReadRequestContainer(final String rootObjectName, final String rtGuid)
			throws GenilBackendException
	{
		// model check inside constructor
		return new GenilReadRootRequestContainer(rootObjectName, rtGuid, this);
	}

	protected abstract RFCInput getRFCInput(GenilFeature feature);

	/**
	 * Initializes GenIL RFC (GENIL_RFCL) function group<br>
	 *
	 * @return <code>true</code> only if the initialize operations were successfully executed in the GenIL layer.
	 * @throws GenilBackendException
	 */

	protected abstract boolean synchronizeAbapModel(char componentSetLoadLevel) throws GenilBackendException;

	protected abstract void synchronizeJavaModel(boolean ignoreModelErrors) throws GenilBackendException;

	protected void invalidateContainer(final RFCOutput rfcOutput, final GenilDataContainer dataCont)
	{
		final Map<String, GenilObjInstance> changedObj = new HashMap<String, GenilObjInstance>();
		final JCoTable chObj = rfcOutput.getTablesParameter("ET_CHANGED_OBJ");
		for (int i = 0; i < chObj.getNumRows(); i++)
		{
			chObj.setRow(i);
			final String objectName = chObj.getString(OBJECT_NAME);
			final String rtGuid = chObj.getString(OBJECT_ID);
			changedObj.put(rtGuid, new GenilObjInstance(objectName, rtGuid));
		}
		dataCont.invalidate(changedObj);
	}

	/**
	 * Returns true if the genil proxy container is in initial state
	 *
	 * @return Initialize status of Genil Proxy
	 */
	public boolean isInitialized()
	{
		return isInitialized;
	}

	/**
	 * <p>
	 * Provide the property {@link #msgOnRequest}.
	 * </p>
	 *
	 * @return Returns the {@link #msgOnRequest} property.
	 */
	public boolean isMsgOnRequest()
	{
		return this.msgOnRequest;
	}

	protected void prepareChildModifyRequest(final GenilDataContainer parent, final RFCInput rfcInput, final RecursiveCount rc)
	{
		final TablesParam tpHdr = rfcInput.getTablesParameter("IT_DATA_HDR");

		// Fill Header Objects
		final TableParamRow tprHdr = new TableParamRow();
		tpHdr.addRow(tprHdr);
		tprHdr.add(OBJECT_NAME, parent.getObjectName());
		tprHdr.add(DELTA_FLAG, parent.getDeltaFlag());
		if (parent.getDeltaFlag() == GenilDataContainer.DELTA_CREATED)
		{
			tprHdr.add("ID_IS_HANDLE", GenilConst.ABAP_TRUE);
		}
		tprHdr.add(OBJECT_ID, parent.getRtGuid());

		// Fill Changed Attribute Values
		fillChangedAttr(parent, rfcInput, rc, tprHdr);

		// Handle changed Childs
		final Map<String, Set<GenilDataContainer>> childsPerRelation = parent.getChangedContainersPerRelation();
		checkChildPerRelation(rfcInput, rc, tprHdr, childsPerRelation);
	}

	/**
	 * @param parent
	 * @param rfcInput
	 * @param rc
	 * @param tprHdr
	 */
	private void fillChangedAttr(final GenilDataContainer parent,
			final RFCInput rfcInput, final RecursiveCount rc,
			final TableParamRow tprHdr) {
		if (parent.getDeltaFlag() == GenilDataContainer.DELTA_MODIFIED || parent.getDeltaFlag() == GenilDataContainer.DELTA_CREATED)
		{
			final List<GenilAttribute> attributes = parent.getAllUpdatedAttributes();
			if (!attributes.isEmpty())
			{
				rc.cAttr++;
				tprHdr.add("ATTR_FROM", rc.cAttr);
				rc.cAttr += attributes.size() - 1;
				tprHdr.add("ATTR_TO", rc.cAttr);

				final GenilModelObject object = getModel().getObject(parent.getObjectName());

				final TablesParam tpAttr = rfcInput.getTablesParameter("IT_DATA_ATTR");
				for (final GenilAttribute attribute : attributes)
				{
					final GenilModelAttr attr = object.getAttrByName(attribute.getName());

					final TableParamRow tprAttr = new TableParamRow();
					tpAttr.addRow(tprAttr);
					tprAttr.add("VINDEX", attr.getStructIndex());
					tprAttr.add("VALUE", attribute.getValue());
					tprAttr.add("PROPERTY", String.valueOf(GenilAttribute.CHANGED));
				}

			}
		}
	}

	/**
	 * @param rfcInput
	 * @param rc
	 * @param tprHdr
	 * @param childsPerRelation
	 */
	private void checkChildPerRelation(final RFCInput rfcInput,
			final RecursiveCount rc, final TableParamRow tprHdr,
			final Map<String, Set<GenilDataContainer>> childsPerRelation) {
		if (!childsPerRelation.isEmpty())
		{

			rc.cRel++;
			tprHdr.add("REL_FROM", rc.cRel);
			rc.cRel += childsPerRelation.size() - 1;
			tprHdr.add("REL_TO", rc.cRel);

			final TablesParam tpRel = rfcInput.getTablesParameter("IT_DATA_RELS");
			for (final Map.Entry<String, Set<GenilDataContainer>> entry : childsPerRelation.entrySet())
			{
				final TableParamRow tprRel = new TableParamRow();
				tpRel.addRow(tprRel);
				tprRel.add("RELATION_NAME", entry.getKey());

				rc.cObj++;
				tprRel.add("OBJECTS_FROM", rc.cObj);
				rc.cObj += entry.getValue().size() - 1;
				tprRel.add("OBJECTS_TO", rc.cObj);

				handleChangedChilds(rfcInput, entry);
			}
			for (final Map.Entry<String, Set<GenilDataContainer>> entry : childsPerRelation.entrySet())
			{
				for (final GenilDataContainer cont : entry.getValue())
				{
					prepareChildModifyRequest(cont, rfcInput, rc);
				}
			}
		}
	}

	private void handleChangedChilds(final RFCInput rfcInput,
			final Map.Entry<String, Set<GenilDataContainer>> entry) {
		final TablesParam tpObj = rfcInput.getTablesParameter("IT_DATA_REL_OBJ");
		for (final GenilDataContainer cont : entry.getValue())
		{
			final TableParamRow tprObj = new TableParamRow();
			tpObj.addRow(tprObj);
			String rtGuid = cont.getRtGuid();

			if (cont.getDeltaFlag() == GenilDataContainer.DELTA_CREATED && rtGuid == null)
			{
				rtGuid = "" + lastHandleCounter;
				cont.setRtGuid(rtGuid);
				lastHandleCounter = lastHandleCounter + 1;
			}
			tprObj.add(OBJECT_ID, rtGuid);

			if (cont.getDeltaFlag() == GenilDataContainer.DELTA_CREATED)
			{
				tprObj.add("ID_IS_HANDLE", "X");

			}

		}
	}

	protected void prepareChildReadRequest(final GenilReadAllRequestContainer parent, final RFCInput rfcInput,
			final RecursiveCount rc)
	{
		final TablesParam tpPath = rfcInput.getTablesParameter("IT_PATH");
		final TablesParam tpReqObjSpec = rfcInput.getTablesParameter("IT_REQUESTED_OBJECTS_SPEC");

		TableParamRow tprO = null;

		long foundParamRowAt = -1;
		long paramRowCount = 0;
		
		// check if object already exists in the table parameter row
		for (final TableParamRow tpr : tpReqObjSpec.getRows())
		{
			tprO = tpr;
			foundParamRowAt = checkNameValuePair(parent, foundParamRowAt,
					paramRowCount, tpr);
			paramRowCount++;
		}

		// parameter does not exist, so create and process it
		if (foundParamRowAt == -1)
		{
			// Process Object and Attributes
			tprO = new TableParamRow();
			tprO.add(OBJECT_NAME, parent.getEntityName());
			rc.cObj++;
		}

		// first check if there are already relations added. If not ->
		// normal procedure
		NameValuePair nvp = tprO.get("ATTR_END_IDX");
		checkNvpNull(parent, rfcInput, rc, tpReqObjSpec, tprO, nvp);

		nvp = tprO.get("RELATION_END_IDX");
		checkNvpNullRequest(parent, rfcInput, rc, tpReqObjSpec, tprO, nvp);

		// simply add it, independent if it was changed or not, because table
		// does ignore duplicates
		tpReqObjSpec.addRow(tprO);

		// Process Relations
		final Map<String, GenilReadAllRequestContainer> childs = parent.getChilds();
		checkChildIsEmptyRelations(parent, rfcInput, rc, tpPath, childs);
	}

	/**
	 * @param parent
	 * @param rfcInput
	 * @param rc
	 * @param tpReqObjSpec
	 * @param tprO
	 * @param nvp
	 */
	private void checkNvpNullRequest(final GenilReadAllRequestContainer parent,
			final RFCInput rfcInput, final RecursiveCount rc,
			final TablesParam tpReqObjSpec, TableParamRow tprO,
			NameValuePair nvp) {
		if (nvp != null)
		{
			reorgRequestedRelations(parent, rfcInput, rc, tpReqObjSpec, tprO);
		}
		else if (parent.isNoRelsRequestedStr().compareTo(GenilConst.ABAP_FALSE) == 0)
		{

			final Map<String, GenilReadAllRequestContainer> childs = parent.getChilds();
			checkChildEmpty(rfcInput, rc, tprO, childs);
		}
		else
		{
			tprO.add("NO_RELS_REQUESTED", GenilConst.ABAP_TRUE);
		}
	}

	/**
	 * @param parent
	 * @param rfcInput
	 * @param rc
	 * @param tpReqObjSpec
	 * @param tprO
	 * @param nvp
	 */
	private void checkNvpNull(final GenilReadAllRequestContainer parent,
			final RFCInput rfcInput, final RecursiveCount rc,
			final TablesParam tpReqObjSpec, TableParamRow tprO,
			NameValuePair nvp) {
		if (nvp != null)
		{
			reorgRequestedAttribues(parent, rfcInput, rc, tpReqObjSpec, tprO);
		}
		else if (parent.isAttrRequested())
		{

			final List<GenilModelAttr> attributes = parent.getRequestedAttributes();
			checkAttrsIsEmpty(rfcInput, rc, tprO, attributes);
		}
		else
		{
			tprO.add("NO_ATTR_REQUESTED", GenilConst.ABAP_TRUE);
		}
	}

	/**
	 * @param parent
	 * @param rfcInput
	 * @param rc
	 * @param tpPath
	 * @param childs
	 */
	private void checkChildIsEmptyRelations(
			final GenilReadAllRequestContainer parent, final RFCInput rfcInput,
			final RecursiveCount rc, final TablesParam tpPath,
			final Map<String, GenilReadAllRequestContainer> childs) {
		if (!childs.isEmpty())
		{
			final Collection<GenilReadAllRequestContainer> colChilds = childs.values();
			Iterator<GenilReadAllRequestContainer> itr = colChilds.iterator();

			checkRelationsTable(parent, rc, tpPath, itr);

			itr = colChilds.iterator();
			while (itr.hasNext())
			{
				final GenilReadAllRequestContainer child = itr.next(); // Process
				// Relations
				prepareChildReadRequest(child, rfcInput, rc);
			}

		}
		else if (parent.isRoot())
		{
			final TableParamRow tprP = new TableParamRow();
			tprP.add("OBJECT_A", parent.getEntityName());
			if (parent.isKeySet())
			{
				final String rtGuid = ((GenilReadKeyRequestContainer) parent).getRtGuid();
				tprP.add("ID_A", rtGuid);
			}
			tpPath.addRow(tprP);
			rc.cPathCount++;
		}
	}

	/**
	 * @param rfcInput
	 * @param rc
	 * @param tprO
	 * @param attributes
	 */
	private void checkAttrsIsEmpty(final RFCInput rfcInput,
			final RecursiveCount rc, TableParamRow tprO,
			final List<GenilModelAttr> attributes) {
		if (!attributes.isEmpty())
		{
			rc.cAttr++;
			tprO.add("ATTR_START_IDX", rc.cAttr);
			rc.cAttr += attributes.size() - 1;
			tprO.add("ATTR_END_IDX", rc.cAttr);

			TablesParam tpAttr = rfcInput.getTablesParameter("IT_REQUEST_ATTRIBUTES");
			if (tpAttr == null)
			{
				tpAttr = new TablesParam("IT_REQUEST_ATTRIBUTES");
				rfcInput.addTablesParameter("IT_REQUEST_ATTRIBUTES", tpAttr);
			}

			for (final GenilModelAttr attr : attributes)
			{
				final TableParamRow tprA = new TableParamRow();
				tprA.add("ATTR_NAME", attr.getName());
				tpAttr.addRow(tprA);
			}
		}
	}

	/**
	 * @param rfcInput
	 * @param rc
	 * @param tprO
	 * @param childs
	 */
	private void checkChildEmpty(final RFCInput rfcInput,
			final RecursiveCount rc, TableParamRow tprO,
			final Map<String, GenilReadAllRequestContainer> childs) {
		if (!childs.isEmpty())
		{
			final Collection<GenilReadAllRequestContainer> colChilds = childs.values();
			final Iterator<GenilReadAllRequestContainer> itr = colChilds.iterator();
			if (itr.hasNext())
			{
				rc.cRel++;
				tprO.add("RELATION_START_IDX", rc.cRel);
				rc.cRel += colChilds.size() - 1;
				tprO.add("RELATION_END_IDX", rc.cRel);

			}
			while (itr.hasNext())
			{
				final GenilReadAllRequestContainer child = itr.next(); // Process
				// Relations

				TablesParam tpRel = rfcInput.getTablesParameter("IT_REQUEST_RELATIONS");
				tpRel = checkTableRelation(rfcInput, tpRel);

				final TableParamRow tprRel = new TableParamRow();
				tprRel.add("RELATION_NAME", child.getParentRelName());
				tpRel.addRow(tprRel);

			}
		}
	}

	/**
	 * @param parent
	 * @param rc
	 * @param tpPath
	 * @param itr
	 */
	private void checkRelationsTable(final GenilReadAllRequestContainer parent,
			final RecursiveCount rc, final TablesParam tpPath,
			Iterator<GenilReadAllRequestContainer> itr) {
		while (itr.hasNext())
		{
			final GenilReadAllRequestContainer child = itr.next(); // Process
			// Relations
			final TableParamRow tprP = new TableParamRow();
			tprP.add("OBJECT_A", parent.getEntityName());
			if (parent.isKeySet())
			{
				final String rtGuid = ((GenilReadKeyRequestContainer) parent).getRtGuid();
				tprP.add("ID_A", rtGuid);
			}
			tprP.add("RELATION_NAME", child.getParentRelName());
			tprP.add("OBJECT_B", child.getEntityName());
			if (child.isKeySet())
			{
				final String rtGuid = ((GenilReadKeyRequestContainer) child).getRtGuid();
				tprP.add("ID_B", rtGuid);
			}

			tpPath.addRow(tprP);
			rc.cPathCount++;
		}
	}

	/**
	 * @param parent
	 * @param foundParamRowAt
	 * @param paramRowCount
	 * @param tpr
	 * @return
	 */
	private long checkNameValuePair(final GenilReadAllRequestContainer parent,
			long foundParamRowAt, long paramRowCount, final TableParamRow tpr) {
		long foundParamRowAtt=foundParamRowAt;
		for (final NameValuePair nvp : tpr.getRow())
		{
			if (nvp.compareToNVP(OBJECT_NAME, parent.getEntityName()))
			{
				foundParamRowAtt = paramRowCount;
		

			}
		}
		return foundParamRowAtt;
	}

	private void reorgRequestedRelations(final GenilReadAllRequestContainer parent, final RFCInput rfcInput,
			final RecursiveCount rc, final TablesParam tpReqObjSpec, final TableParamRow tprO)
	{

		if (parent.isNoRelsRequestedStr().compareTo(GenilConst.ABAP_FALSE) == 0)
		{
			// update the no relation requested flag if required
			NameValuePair nvp = tprO.get("NO_RELS_REQUESTED");
			if (nvp != null)
			{
				nvp.setValue(GenilConst.ABAP_FALSE);
			}
			else
			{
				// Should never happen. Normally it have to be the first
				// attribute, but you can't be too sure
				tprO.add("NO_RELS_REQUESTED", GenilConst.ABAP_FALSE);
			}
			// Now we have to update the relations with the relations which
			// are on the new object
			nvp = tprO.get("RELATION_START_IDX");
			updateRelationForNewObject(parent, rfcInput, rc, tpReqObjSpec,
					tprO, nvp);
		}
	}

	private void updateRelationForNewObject(
			final GenilReadAllRequestContainer parent, final RFCInput rfcInput,
			final RecursiveCount rc, final TablesParam tpReqObjSpec,
			final TableParamRow tprO, NameValuePair nvp) {
		
		NameValuePair nvpp=nvp;
		final int startIdx = Integer.parseInt(nvp.getValue().toString());

		nvpp = tprO.get("RELATION_END_IDX");
		final int endIdx = Integer.parseInt(nvpp.getValue().toString());

		final Map<String, GenilReadAllRequestContainer> childs = parent.getChilds();
		if (!childs.isEmpty())
		{
			final Collection<GenilReadAllRequestContainer> colChilds = childs.values();
			final Iterator<GenilReadAllRequestContainer> itr = colChilds.iterator();

			// set the new end index
			int recordsAdded = 0;
			while (itr.hasNext())
			{
				recordsAdded = setEndIndexForReorgReqRelation(rfcInput,
						startIdx, endIdx, itr, recordsAdded);

			}

			if (recordsAdded != 0)
			{
				// we inserted a row. So all pointers in the
				// object tab have to be updated

				insertRowForReOrg(tpReqObjSpec, endIdx, recordsAdded);
				rc.cRel += recordsAdded;
			}
		}
	}

	private void insertRowForReOrg(final TablesParam tpReqObjSpec,
			final int endIdx, int recordsAdded) {
		NameValuePair nvp;
		for (final TableParamRow tpr : tpReqObjSpec.getRows())
		{
			nvp = tpr.get("RELATION_START_IDX");
			if (nvp != null)
			{
				final int startIdxBuffer = Integer.parseInt(nvp.getValue().toString());
				if (startIdxBuffer >= endIdx)
				{
					nvp.setValue(startIdxBuffer + recordsAdded);
				}
			}
			nvp = tpr.get("RELATION_END_IDX");
			if (nvp != null)
			{
				final int endIdxBuffer = Integer.parseInt(nvp.getValue().toString());
				if (endIdxBuffer >= endIdx)
				{
					nvp.setValue(endIdxBuffer + recordsAdded);
				}
			}

		}
	}

	private int setEndIndexForReorgReqRelation(final RFCInput rfcInput,
			final int startIdx, final int endIdx,
			final Iterator<GenilReadAllRequestContainer> itr, int recordsAdded) {
		final GenilReadAllRequestContainer child = itr.next(); // Process
		// Relations

		TablesParam tpRel = rfcInput.getTablesParameter("IT_REQUEST_RELATIONS");
		tpRel = checkTableRelation(rfcInput, tpRel);

		// check if the relation is already in for this relation
		int recordadd =recordsAdded;

		for (int i = startIdx - 1; i < endIdx; i++)
		{
			final TableParamRow row = tpRel.getRow(i);
			if (row.get("RELATION_NAME").getValue().toString().compareTo(child.getParentRelName().toString()) == 0)
			{
				
				break;
			}
		}
		final TableParamRow tprRel = new TableParamRow();
		tprRel.add("RELATION_NAME", child.getParentRelName());
		tpRel.insertRow(tprRel, endIdx);
		recordadd++;
		return recordadd;
	}

	private TablesParam checkTableRelation(final RFCInput rfcInput,
			TablesParam tpRel) {
		
		TablesParam tpRela=tpRel;
		tpRela = new TablesParam("IT_REQUEST_RELATIONS", false);
		rfcInput.addTablesParameter("IT_REQUEST_RELATIONS", tpRela);
		return tpRela;
	}

	private void reorgRequestedAttribues(final GenilReadAllRequestContainer parent, final RFCInput rfcInput,
			final RecursiveCount rc, final TablesParam tpReqObjSpec, final TableParamRow tprO)
	{
		if (parent.isAttrRequested())
		{
			NameValuePair nvp = tprO.get("NO_ATTR_REQUESTED");
			if (nvp != null)
			{
				nvp.setValue(GenilConst.ABAP_FALSE);
			}
			else
			{
				// Should never happen. Normally it have to be the first
				// attribute, but you can't be too sure
				tprO.add("NO_ATTR_REQUESTED", GenilConst.ABAP_FALSE);
			}
			// Now we have to update the relations with the relations which
			// are on the new object
			nvp = tprO.get("ATTR_START_IDX");
			final int startIdx = Integer.parseInt(nvp.getValue().toString());

			nvp = tprO.get("ATTR_END_IDX");
			final int endIdx = Integer.parseInt(nvp.getValue().toString());

			final List<GenilModelAttr> attributes = parent.getRequestedAttributes();

			if (!attributes.isEmpty())
			{

				setNewIndexAttr(rfcInput, rc, tpReqObjSpec, startIdx, endIdx,
						attributes);
			}
		}
	}

	private void setNewIndexAttr(final RFCInput rfcInput,
			final RecursiveCount rc, final TablesParam tpReqObjSpec,
			final int startIdx, final int endIdx,
			final List<GenilModelAttr> attributes) {
		// set the new end index
		int recordsAdded = 0;
		for (final GenilModelAttr attr : attributes)
		{

			TablesParam tpAttr = rfcInput.getTablesParameter("IT_REQUEST_ATTRIBUTES");
			if (tpAttr == null)
			{
				tpAttr = new TablesParam("IT_REQUEST_ATTRIBUTES");
				rfcInput.addTablesParameter("IT_REQUEST_ATTRIBUTES", tpAttr);
			}

			// check if the relation is already in for this relation
			int attributesFoundAt = -1;

			for (int i = startIdx - 1; i < endIdx; i++)
			{
				final TableParamRow row = tpAttr.getRow(i);
				if (row.get("ATTR_NAME").getValue().toString().compareTo(attr.getName().toString()) == 0)
				{
					attributesFoundAt = i;
					break;
				}
			}
			// not found? So INSERT it!
			if (attributesFoundAt == -1)
			{

				final TableParamRow tprA = new TableParamRow();
				tprA.add("ATTR_NAME", attr.getName());
				tpAttr.insertRow(tprA, endIdx);
				recordsAdded++;
			}

		}

		if (recordsAdded != 0)
		{
			// we inserted a row. So all pointers in the
			// object tab have to be updated

			insertRowObject(tpReqObjSpec, endIdx, recordsAdded);
			rc.cAttr += recordsAdded;
		}
	}

	private void insertRowObject(final TablesParam tpReqObjSpec,
			final int endIdx, int recordsAdded) {
		NameValuePair nvp;
		for (final TableParamRow tpr : tpReqObjSpec.getRows())
		{
			nvp = tpr.get("ATTR_START_IDX");
			if (nvp != null)
			{
				final int startIdxBuffer = Integer.parseInt(nvp.getValue().toString());
				if (startIdxBuffer >= endIdx)
				{
					nvp.setValue(startIdxBuffer + recordsAdded);
				}
			}
			nvp = tpr.get("ATTR_END_IDX");
			if (nvp != null)
			{
				final int endIdxBuffer = Integer.parseInt(nvp.getValue().toString());
				if (endIdxBuffer >= endIdx)
				{
					nvp.setValue(endIdxBuffer + recordsAdded);
				}
			}

		}
	}

	protected void prepareChildReReadRequest(final GenilDataContainer parentDataCont,
			final GenilReadKeyRequestContainer parentRequestCont, final boolean forceReadAll)
	{
		if (forceReadAll || parentDataCont.isInvalid())
		{
			// Add the requested attributes
			for (final GenilAttribute attr : parentDataCont.getAllAttribues())
			{
				parentRequestCont.addRequestedAttribute(attr.getName());
			}

			// Process Children
			for (final GenilDataContainer child : parentDataCont.getAllChilds())
			{
				GenilReadKeyRequestContainer childReqCont;
				// Add new Child Relation Request
				final String relName = child.getRelationFromParent();
				final String rtGuid = child.getRtGuid();
				childReqCont = parentRequestCont.addChildRel(relName, rtGuid);

				prepareChildReReadRequest(child, childReqCont, forceReadAll);
			}
		}

	}

	protected RFCInput prepareCreateRequest(final GenilCreateRootRequestContainer createReqCont)
	{
		final RFCInput rfcInput = getRFCInput(GenilFeature.CREATE);
		rfcInput.addImportParameter("IV_OBJECT_NAME", createReqCont.getRootName());
		rfcInput.addImportParameter("IV_NUMBER", createReqCont.getNumber());
		int i = 0;
		for (final GenilAttribute attr : createReqCont.getAllCreateParameters())
		{
			rfcInput.addTablesParameter("IT_PARAMETERS", i, "NAME", attr.getName());
			rfcInput.addTablesParameter("IT_PARAMETERS", i, "VALUE", attr.getValue());
			i++;
		}
		rfcInput.addRequestedTables("ET_DATA_HDR");
		rfcInput.addRequestedTables("ET_DATA_ATTR");
		rfcInput.addRequestedTables("ET_DATA_RELS");
		rfcInput.addRequestedTables("ET_DATA_REL_OBJ");

		return rfcInput;
	}

	protected RFCInput prepareModifyRequest(final GenilRootDataContainer dataCont)
	{
		final RFCInput rfcInput = getRFCInput(GenilFeature.MODIFY);

		rfcInput.addTablesParameter("IT_DATA_HDR", new TablesParam("IT_DATA_HDR"));
		rfcInput.addTablesParameter("IT_DATA_ATTR", new TablesParam("IT_DATA_ATTR"));
		rfcInput.addTablesParameter("IT_DATA_RELS", new TablesParam("IT_DATA_RELS"));
		rfcInput.addTablesParameter("IT_DATA_REL_OBJ", new TablesParam("IT_DATA_REL_OBJ"));

		prepareChildModifyRequest(dataCont, rfcInput, new RecursiveCount());

		rfcInput.addRequestedTables("ET_ID_MAPPING");
		rfcInput.addRequestedTables("ET_CHANGED_OBJ");

		return rfcInput;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxy#read(com.sap.wec.tc.core
	 * .backend.genil.GenilReadRootRequestContainer)
	 */
	public GenilRootDataContainer read(final GenilReadRootRequestContainer rootReq) throws GenilBackendException
	{

		final GenilMessageRequestContainer msgReq = null;
		final GenilRootDataContainer dataCont = new GenilRootDataContainer(rootReq.getEntityName(), rootReq.getRtGuid(), this);

		readDataContainer(dataCont, rootReq, msgReq);
		return dataCont;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxy#read(com.sap.wec.tc.core
	 * .backend.genil.GenilReadRootRequestContainer)
	 */
	public GenilRootDataContainer read(final GenilReadRootRequestContainer rootReq, final GenilMessageRequestContainer msgReq)
			throws GenilBackendException
	{

		final GenilRootDataContainer dataCont = new GenilRootDataContainer(rootReq.getEntityName(), rootReq.getRtGuid(), this);

		readDataContainer(dataCont, rootReq, msgReq);
		return dataCont;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxy#read(com.sap.wec.tc.core .backend.genil.GenilRootDataContainer,
	 * com.sap.wec.tc.core.backend.genil.GenilReadRootRequestContainer)
	 */
	public void read(final GenilRootDataContainer rootCont, final GenilReadRootRequestContainer rootReq)
			throws GenilBackendException
	{
		final GenilMessageRequestContainer msgReq = null;
		readDataContainer(rootCont, rootReq, msgReq);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxy#read(com.sap.wec.tc.core .backend.genil.GenilRootDataContainer,
	 * com.sap.wec.tc.core.backend.genil.GenilReadRootRequestContainer)
	 */
	public void read(final GenilRootDataContainer rootCont, final GenilReadRootRequestContainer rootReq,
			final GenilMessageRequestContainer msgReq) throws GenilBackendException
	{
		readDataContainer(rootCont, rootReq, msgReq);
	}

	private void readDataContainer(final GenilRootDataContainer rootCont, final GenilReadRootRequestContainer rootReq,
			final GenilMessageRequestContainer msgReq) throws GenilBackendException
	{
		final String traceMethodName = "read()";
		final long traceTime = traceEntering(traceMethodName);
		if (rootCont == null)
		{
			return;
		}

		RFCOutput rfcOutput;
		final RFCInput rfcInput = prepareReadRequest(rootReq);

		// Buffer the last read request for reRead
		rootCont.setLastGenilReadRootRequest(rootReq);

		// if messages are requested fill related container
		if (msgReq != null)
		{
			fillMessageAttributes(msgReq, rfcInput);
		}
		rfcOutput = rfcWrapper.doRFC(rfcInput);

		if (rfcOutput.isSuccess())
		{
			rootCont.resetDataContainer();
			if (fillContainer(rfcOutput, rootCont).size() > 1)
			{
				// throw exception if nothing was found
				final String msg = "Nothing found for requested data";
				final GenilBackendException gnlBkndExc =new GenilBackendException(msg);
				throw gnlBkndExc;
			}

			// if messages are requested return related container
			if (msgReq != null)
			{
				fillMessages(rfcOutput, rootCont.getRtGuid());
			}
		}
		traceExiting(traceTime, traceMethodName);
	}

	/**
	 * This method reads GenIL models with objects and its relations and build up the model with corresponding java
	 * objects
	 *
	 * @throws GenilBackendException
	 */
	protected void readModel() throws GenilBackendException
	{
		readModel(false);
	}

	/**
	 * This method reads GenIL models with objects and its relations and build up the model with corresponding java
	 * objects
	 *
	 * @param ignoreModelErrors
	 *           True = Ignores errors in the genil model
	 * @throws GenilBackendException
	 */
	@java.lang.SuppressWarnings("squid:ForLoopCounterChangedCheck")
	protected synchronized void readModel(final boolean ignoreModelErrors) throws GenilBackendException
	{
		setIgnoringModelErrors(isIgnoringModelErrors);
		final String traceMethodName = "readModel()";
		final long traceTime = traceEntering(traceMethodName);

		final GenilModel model = genilModel;
		if (model.isComplete(componentSet))
		{
			return;
		}
		RFCInput rfcInput = null;
		rfcInput = getRFCInput(GenilFeature.GET_MODEL);

		rfcInput.addImportParameter("IV_COMPONENT_SET", componentSet);
		rfcInput.addRequestedTables("ET_OBJECTS");
		rfcInput.addRequestedTables("ET_OBJECT_ATTRS");
		rfcInput.addRequestedTables("ET_OBJECT_METHODS");
		rfcInput.addRequestedTables("ET_RELATIONS");
		rfcInput.addRequestedTables("ET_DQUERY_DEF");
		rfcInput.addRequestedTables("ET_DQUERY_ATTR_DEF");
		rfcInput.addRequestedTables("ET_DQUERY_OPTIONS");

		if (ignoreModelErrors)
		{
			rfcInput.addImportParameter("IV_IGNORE_MODEL_ERRORS", GenilConst.ABAP_TRUE);
		}

		final RFCOutput rfcOutput = rfcWrapper.doRFC(rfcInput);

		final JCoTable objects = rfcOutput.getTablesParameter("ET_OBJECTS");
		if (objects.getNumRows() == 0)
		{
			final String msg = "The model for ComponentSet " + componentSet
					+ " does not contain any objects. Please check if the used Model is error free";

			final GenilBackendException gnlBkndExc =  new GenilBackendException(msg);
			throw gnlBkndExc;
		}

		final JCoTable relations = rfcOutput.getTablesParameter("ET_RELATIONS");


		final Object lock = new Object();
		synchronized (lock)
		{
			// build up model: loop over objects,
			// get corresponding attributes for object and fill relations
			buildModelObjects(model, rfcOutput, objects);
			// loop over relations and fill corresponding objects with them
			for (int i = 0; i < relations.getNumRows(); i++)
			{
				relations.setRow(i);
				final GenilModelObject objectA = model.getObject(relations.getString("OBJECT_A"));
				final GenilModelObject objectB = model.getObject(relations.getString("OBJECT_B"));

				// for performance reason, relation with empty source can appear
				// and have to be ignored (normally they would not appear if we
				// build the tree in the 'normal' way from root via relations to
				// the nodes.
				if (objectA == null)
				{
					relations.deleteRow();
					 i--;

					// But nodes with SOURCE but empty TARGET are not allowed
				}
				else if (objectB == null)
				{

					final String msg = "At least one target object in relation " + relations.getString("RELATION_NAME")
							+ " couldn't be found";

					final GenilBackendException gnlBkndExc = new GenilBackendException(msg);
					throw gnlBkndExc;
				}
				checkObjectAbNull(relations, objectA, objectB);

			}
			// dQuery options
			final JCoTable dQueryDefinition = rfcOutput.getTablesParameter("ET_DQUERY_DEF");
			final JCoTable dQueryAttributes = rfcOutput.getTablesParameter("ET_DQUERY_ATTR_DEF");
			final JCoTable dQueryOptions = rfcOutput.getTablesParameter("ET_DQUERY_OPTIONS");

			checkQueryDefn(model, dQueryDefinition, dQueryAttributes,
					dQueryOptions);

			model.setComplete(componentSet);
		}
		traceExiting(traceTime, traceMethodName);

	}

	/**
	 * @param model
	 * @param rfcOutput
	 * @param objects
	 */
	private void buildModelObjects(final GenilModel model,
			final RFCOutput rfcOutput, final JCoTable objects) {
		for (int i = 0; i < objects.getNumRows(); i++)
		{
			objects.setRow(i);

			// create object and fill it with attributes
			final GenilModelObjectKind objectKind = GenilModelObjectKind.get(objects.getChar("OBJECT_KIND"));
			final String objectName = objects.getString(OBJECT_NAME);


			createModelObjectIfDoesNotExit(objects,model,objectName,objectKind,rfcOutput);
		}
	}

	/**
	 * @param relations
	 * @param objectA
	 * @param objectB
	 * @throws GenilBackendException
	 */
	private void checkObjectAbNull(final JCoTable relations,
			final GenilModelObject objectA, final GenilModelObject objectB)
			throws GenilBackendException {
		if ((objectA != null && objectB != null))
		{
			// create relation; relation is unique only with combination
			// of
			// parent-child
			final GenilModelRelation rel = new GenilModelRelation(relations.getString("RELATION_NAME"));


			// fill child and parent for current relation
			objectA.addChildRelation(rel);
			rel.setObjectA(objectA);
			objectB.addParentRelation(rel);
			rel.setObjectB(objectB);

			final String cardA = relations.getString("CARD_A");
			final String cardB = relations.getString("CARD_B");
			final String relationKind = relations.getString("RELATION_KIND");

			boolean isCardANullOrEmpty = cardA == null || cardA.isEmpty();
			boolean isCardBNullOrEmpty = cardB == null || cardB.isEmpty();
			boolean isRelationKind = relationKind == null || relationKind.isEmpty() ;
			
			if (isCardANullOrEmpty || isCardBNullOrEmpty|| isRelationKind)
			{

				final String msg = "Errornous relation handling in model for ComponentSet " + componentSet
						+ " found. Details see Log.";

				throw new GenilBackendException(msg);
			}

			// set cardinalities for relation
			rel.setCardA(cardA.toCharArray()[0]);
			rel.setCardB(cardB.toCharArray()[0]);
			rel.setCardRelation(relationKind.toCharArray()[0]);
		}
	}

	/**
	 * @param model
	 * @param dQueryDefinition
	 * @param dQueryAttributes
	 * @param dQueryOptions
	 */
	private void checkQueryDefn(final GenilModel model,
			final JCoTable dQueryDefinition, final JCoTable dQueryAttributes,
			final JCoTable dQueryOptions) {
		for (int i = 0; i < dQueryDefinition.getNumRows(); i++)
		{
			dQueryDefinition.setRow(i);

			final GenilModelObject object = model.getObject(dQueryDefinition.getString("DQUERY_NAME"));

			if (object != null)
			{

				startEndDefaultIndex(dQueryDefinition, dQueryOptions,
						object);

				startEndOptIndex(dQueryDefinition, dQueryAttributes,
						dQueryOptions, object);
			}

		}
	}



	private void startEndOptIndex(final JCoTable dQueryDefinition,
			final JCoTable dQueryAttributes, final JCoTable dQueryOptions,
			final GenilModelObject object) {
		final int startAttrIndex = Integer.decode(dQueryDefinition.getString("ATTR_OPTIONS_START_IDX")) - 1;
		final int endAttrIndex = Integer.decode(dQueryDefinition.getString("ATTR_OPTIONS_END_IDX")) - 1;

		if (startAttrIndex >= 0)
		{
			// fill object with its attributes
			for (int j = startAttrIndex; j <= endAttrIndex; j++)
			{
				dQueryAttributes.setRow(j);

				fillObjectAttr(dQueryDefinition, dQueryAttributes,
						dQueryOptions, object);
			}
		}
	}

	private void fillObjectAttr(final JCoTable dQueryDefinition,
			final JCoTable dQueryAttributes, final JCoTable dQueryOptions,
			final GenilModelObject object) {
		final int startOptIndex = Integer.decode(dQueryAttributes.getString("OPTION_START_IDX")) - 1;
		final int endOptIndex = Integer.decode(dQueryAttributes.getString("OPTION_END_IDX")) - 1;

		final GenilModelAttr attribute = object.getAttrByName(dQueryAttributes.getString("ATTRIBUTE_NAME"));

		if (attribute == null)
		{
			final String msg = "Attribute (" + dQueryAttributes.getString("ATTRIBUTE_NAME") + ") for object "
					+ dQueryDefinition.getString("DQUERY_NAME") + " couldn't be found";
			logger.debug(msg);

		}
		else
		{

			if (startOptIndex >= 0)
			{
				// fill object with its attributes
				for (int k = startOptIndex; k <= endOptIndex; k++)
				{
					dQueryOptions.setRow(k);

					attribute.addAttributeOption(dQueryOptions.getString("SIGN").toCharArray()[0], dQueryOptions
							.getString("OPTION").toString());

				}
			}
		}
	}

	private void startEndDefaultIndex(final JCoTable dQueryDefinition,
			final JCoTable dQueryOptions, final GenilModelObject object) {
		final int startDefaultIndex = Integer.decode(dQueryDefinition.getString("DEFAULT_START_IDX")) - 1;
		final int endDefaultIndex = Integer.decode(dQueryDefinition.getString("DEFAULT_END_IDX")) - 1;

		if (startDefaultIndex >= 0)
		{
			// fill object with its attributes
			for (int j = startDefaultIndex; j <= endDefaultIndex; j++)
			{
				dQueryOptions.setRow(j);
				if (dQueryOptions.getString("SIGN").toCharArray() != null)
				{
					object.addAttributeDefaultOption(dQueryOptions.getString("SIGN").toCharArray()[0], dQueryOptions
							.getString("OPTION").toString());
				}

			}
		}
	}

	private void createModelObjectIfDoesNotExit(JCoTable objects,GenilModel model, String objectName, GenilModelObjectKind objectKind,RFCOutput rfcOutput) {
		final JCoTable attributes = rfcOutput.getTablesParameter("ET_OBJECT_ATTRS");
		final JCoTable methods = rfcOutput.getTablesParameter("ET_OBJECT_METHODS");

		
		
		if (model.getObject(objectName) == null)
		{
			final GenilModelObject object = new GenilModelObject(objectName, objectKind);
			model.setObject(object);
			// java counts beginning with 0 => -1
			final int startAttrIndex = Integer.decode(objects.getString("ATTR_START_IDX")) - 1;
			final int endAttrIndex = Integer.decode(objects.getString("ATTR_END_IDX")) - 1;
			if (startAttrIndex >= 0)
			{
				// fill object with its attribute
			
				for (int j = startAttrIndex; j <= endAttrIndex; j++)
				{
					attributes.setRow(j);
					final GenilModelAttr attr = new GenilModelAttr();
					attr.setName(attributes.getString("ATTRIBUTE_NAME"));
					attr.setProperty(attributes.getString("DEFAULT_PROP"));
					attr.setStructIndex(Integer.decode(attributes.getString("STRUCT_INDEX")));
					attr.setObject(object);
					object.addAttribute(attr);

				}
			}

			// java counts beginning with 0 => -1
			final int startMethIndex = Integer.decode(objects.getString("METHODS_START_IDX")) - 1;
			final int endMethIndex = Integer.decode(objects.getString("METHODS_END_IDX")) - 1;
			if (startMethIndex >= 0)
			{
				// fill object with its methods
				for (int j = startMethIndex; j <= endMethIndex; j++)
				{
					methods.setRow(j);
					final String methodName = methods.getString("METHOD_NAME");
					final GenilModelMethod meth = new GenilModelMethod(object, methodName);
					meth.setParamStructure(methods.getString("PARAM_STRUCT"));
					meth.setReturnType(methods.getString("RETURN_TYPE"));

				}
			}
		}
		
	}

	/**
	 * The search operation can function like a normal search or dynamic search depending on query request settings<br>
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxy#search(com.sap.wec.tc.core.backend.genil.GenilQueryRequestContainer)
	 */
	public List<GenilRootDataContainer> search(final GenilQueryRequestContainer queryReq) throws GenilBackendException
	{
		final GenilMessageRequestContainer msgReq = null;
		return searchRootDataContainer(queryReq, msgReq);
	}

	/**
	 * The search operation can function like a normal search or dynamic search depending on query request settings<br>
	 *
	 * @see com.sap.wec.tc.core.backend.genil.IGenilProxy#search(com.sap.wec.tc.core.backend.genil.GenilQueryRequestContainer)
	 */
	public List<GenilRootDataContainer> search(final GenilQueryRequestContainer queryReq, final GenilMessageRequestContainer msgReq)
			throws GenilBackendException
	{
		return searchRootDataContainer(queryReq, msgReq);
	}

	private List<GenilRootDataContainer> searchRootDataContainer(final GenilQueryRequestContainer queryReq,
			final GenilMessageRequestContainer msgReq) throws GenilBackendException
	{
		List<GenilRootDataContainer> rList = null;

		switch (queryReq.getModelObject().getObjectKind())
		{
			case SEARCH:
				rList = getQueryResult(queryReq, msgReq);
				break;
			case DYNAMIC_SEARCH:
				rList = getDQueryResult(queryReq, msgReq);
				break;
			default:
				rList = Collections.emptyList();
				break;
		}

		return rList;
	}

	protected void getMessages(final GenilMessageRequestContainer msgReq, final GenilRootDataContainer rootCont)
			throws GenilBackendException
	{
		final String traceMethodName = "getMessages()";
		final long traceTime = traceEntering(traceMethodName);

		if (msgReq == null)
		{
			return;
		}

		RFCOutput rfcOutput;
		final RFCInput rfcInput = getRFCInput(GenilFeature.GET_MESSAGES);

		final TablesParam tpReqObjList = new TablesParam("IT_OBJECT_LIST");

		if (rootCont != null)
		{
			final TableParamRow tprO = new TableParamRow();
			tprO.add(OBJECT_NAME, rootCont.getObjectName());
			tprO.add(OBJECT_ID, rootCont.getRtGuid());
			tpReqObjList.addRow(tprO);
		}

		rfcInput.addTablesParameter("IT_OBJECT_LIST", tpReqObjList);
		fillMessageAttributes(msgReq, rfcInput);

		rfcOutput = rfcWrapper.doRFC(rfcInput);

		String rtGuid = null;
		if (rootCont != null)
		{
			rtGuid = rootCont.getRtGuid();
		}

		if (rfcOutput.isSuccess())
		{
			fillMessages(rfcOutput, rtGuid);
		}
		traceExiting(traceTime, traceMethodName);
	}

	protected void fillMessageAttributes(final GenilMessageRequestContainer msgReq, final RFCInput rfcInput)
	{
		rfcInput.addImportParameter("IV_GLOBAL_MESSAGES_REQ", msgReq.isGlobalMessages());
		rfcInput.addImportParameter("IV_OBJ_MESSAGES_REQ", msgReq.isObjectMessages());
		rfcInput.addImportParameter("IV_MESSAGE_TYPE", msgReq.getMsgType().getMsgTypeAsChar());
		rfcInput.addImportParameter("IV_MSG_LEVEL", msgReq.getMsgLevel());
		rfcInput.addImportParameter("IV_FOR_DISPLAY", msgReq.isForDisplay());
		if (msgReq.isGlobalMessages())
		{
			rfcInput.addRequestedTables("ET_GLOBAL_MESSAGES");
		}
		if (msgReq.isObjectMessages())
		{
			rfcInput.addRequestedTables("ET_OBJ_MESSAGES_HDR");
			rfcInput.addRequestedTables("ET_OBJ_MESSAGES");
		}
	}

	protected RFCInput prepareDQueryRequest(final GenilQueryRequestContainer queryReqCont)
	{
		final RFCInput rfcInput = getRFCInput(GenilFeature.GET_DQUERY_RESULT);

		rfcInput.addImportParameter("IV_DQUERY_NAME", queryReqCont.getQueryObjectName());

		final ImportStructure is = new ImportStructure("IS_DQUERY_PARAMETERS");
		is.setValue("MAX_HITS", queryReqCont.getMaxHits());
		is.setValue("MATCH_TYPE", queryReqCont.getMatchType());

		rfcInput.addImportParameter(is.getName(), is);

		int i = 0;
		for (final GenilQueryParameter qp : queryReqCont.getParameters())
		{
			rfcInput.addTablesParameter("IT_SELECTION_PARAMETERS", i, "ATTR_NAME", qp.getParameterName());
			rfcInput.addTablesParameter("IT_SELECTION_PARAMETERS", i, "OPTION", qp.getOption());
			rfcInput.addTablesParameter("IT_SELECTION_PARAMETERS", i, "SIGN", qp.getSign());
			rfcInput.addTablesParameter("IT_SELECTION_PARAMETERS", i, "LOW", qp.getLow());
			rfcInput.addTablesParameter("IT_SELECTION_PARAMETERS", i, "HIGH", qp.getHigh());

			i++;
		}

		return rfcInput;
	}

	protected RFCInput prepareReadRequest(final GenilReadRootRequestContainer rootReqCont)
	{
		// create a non unique request container
		final RFCInput rfcInput = getRFCInput(GenilFeature.READ);

		final TablesParam tpROS = new TablesParam("IT_REQUESTED_OBJECTS_SPEC", true);
		rfcInput.addTablesParameter("IT_REQUESTED_OBJECTS_SPEC", tpROS);

		final TablesParam tpRR = new TablesParam("IT_REQUEST_RELATIONS");
		rfcInput.addTablesParameter("IT_REQUEST_RELATIONS", tpRR);

		final TablesParam tpP = new TablesParam("IT_PATH");
		rfcInput.addTablesParameter("IT_PATH", tpP);

		final RecursiveCount rc = new RecursiveCount();

		prepareChildReadRequest(rootReqCont, rfcInput, rc);

		rfcInput.addTablesParameter("IT_PATH_HDR", 0, "ENTRY_FROM", 1);
		rfcInput.addTablesParameter("IT_PATH_HDR", 0, "ENTRY_TO", rc.cPathCount);

		rfcInput.addRequestedTables("ET_DATA_HDR");
		rfcInput.addRequestedTables("ET_DATA_ATTR");
		rfcInput.addRequestedTables("ET_DATA_RELS");
		rfcInput.addRequestedTables("ET_DATA_REL_OBJ");

		return rfcInput;
	}

	protected long traceEntering(final String traceMethodName)
	{

		return 0;
	}

	protected void traceExiting(final long traceStartMillis, final String traceMethodName)
	{
    logger.getLogger();
	}
}
