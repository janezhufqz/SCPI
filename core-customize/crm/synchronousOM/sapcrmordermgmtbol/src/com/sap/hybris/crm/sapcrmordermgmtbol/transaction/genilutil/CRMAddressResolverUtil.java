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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.genilutil;

import de.hybris.platform.sap.core.bol.backend.jco.BackendBusinessObjectBaseJCo;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionFactory;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionTools;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.sap.conn.jco.JCoException;
import com.sap.genil.GenilConst;
import com.sap.genil.GenilConst.GenilMessageType;
import com.sap.genil.GenilDataContainer;
import com.sap.genil.GenilMessageContainer;
import com.sap.genil.GenilMessageRequestContainer;
import com.sap.genil.GenilProxyFactory;
import com.sap.genil.GenilQueryRequestContainer;
import com.sap.genil.GenilReadAllRequestContainer;
import com.sap.genil.GenilReadRootRequestContainer;
import com.sap.genil.GenilRootDataContainer;
import com.sap.genil.IGenilProxy;
import com.sap.genil.exception.GenilBackendException;



/**
 * The Class CRMAddressResolverUtil.
 *
 * @author C5230727
 */
public class CRMAddressResolverUtil extends BackendBusinessObjectBaseJCo
{

	/** The Constant sapLogger. */
	private static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(CRMAddressResolverUtil.class.getName());

	/** The managed connection factory. */
	@Resource(name = "sapCoreJCoManagedConnectionFactory")
	protected JCoManagedConnectionFactory managedConnectionFactory;

	/** The search result. */
	static List<GenilRootDataContainer> searchResult;

	/** The Constant COMPONENT_SET. */
	public static final String COMPONENT_SET = "BP_APPL";

	/** The Constant BUILD_HEADER_ADVANCE_SEARCH. */
	public static final String BUILD_HEADER_ADVANCE_SEARCH = "BuilHeaderAdvancedSearch";

	/** The Constant QUERY_TYPE. */
	public static final String QUERY_TYPE = "PARTNER";

	/** The Constant BUIL_ADDRESS_REL. */
	public static final String BUIL_ADDRESS_REL = "BuilAddressRel";

	@Autowired
	private SessionService sessionService;


	/**
	 * Gets the shipping address from genil.
	 *
	 * @param partnerId
	 *           the partner id
	 * @return the shipping address from genil
	 */
	public Map<String, String> getShippingAddressFromGenil(final String partnerId)
	{

		// Get JCo connection
		final JCoConnection jCoConnection = getDefaultJCoConnection();
		//Getting GenilProxy and execute
		final IGenilProxy genilproxy;
		Map<String, String> addressMapwithGUID = null;
		if (sessionService.getAttribute("addressMapwithGUID") == null)
		{
			try
			{
				sapLogger.getLogger().info("******************calling backend for getShippingAddressFromGenil************ ");
				genilproxy = GenilProxyFactory.getProxy(jCoConnection, COMPONENT_SET);
				genilproxy.isInitialized();
				GenilReadRootRequestContainer rootReq;
				GenilRootDataContainer root;
				GenilQueryRequestContainer queryReqCont;
				final GenilReadAllRequestContainer childReq = null;
				addressMapwithGUID = new HashMap<String, String>();

				//Initializing  GenilQueryRequestContainer
				queryReqCont = genilproxy.getQueryRequestContainer(BUILD_HEADER_ADVANCE_SEARCH);

				//add some parameters
				//using a parameter container
				queryReqCont.addQueryParam(QUERY_TYPE, 'I', "EQ", partnerId, "");
				queryReqCont.setMaxHits(10);
				searchResult = genilproxy.search(queryReqCont);

				if (searchResult.isEmpty())
				{
					// Error Handling
				}
				final GenilMessageContainer msgCont = genilproxy.getGlobalMessageContainer();

				msgCont.getMessages();


				root = searchResult.get(0);
				final String customerGuid = root.getRtGuid();
				rootReq = genilproxy.getReadRequestContainer(root.getObjectName(), customerGuid);


				final GenilMessageRequestContainer msgReqCont = genilproxy.getMessageRequestContainer();
				setChildRelationToRoot(rootReq, childReq, msgReqCont);
				// trigger read
				root = genilproxy.read(rootReq, msgReqCont);

				//Reading BuilAddressRelation
				final List<GenilDataContainer> addressList = root.getChilds(BUIL_ADDRESS_REL);
				//populate and add in map
				populateAddressMapwithGUID(addressList, addressMapwithGUID);

			}
			catch (final GenilBackendException e)
			{
				sapLogger.getLogger().error(e);
			}
			catch (final JCoException e)
			{
				sapLogger.getLogger().error(e);
			}
		}
		else
		{
			addressMapwithGUID = (Map<String, String>) sessionService.getAttribute("addressMapwithGUID");
		}
		if (sessionService.getAttribute("addressMapwithGUID") == null && !CollectionUtils.isEmpty(addressMapwithGUID))
		{
			sessionService.setAttribute("addressMapwithGUID", addressMapwithGUID);
		}
		sapLogger.getLogger().info(addressMapwithGUID);
		return addressMapwithGUID;

	}




	/**
	 * Gets the shipping address guid map from genil.
	 *
	 * @param partnerId
	 *           the partner id
	 * @return the shipping address guid map from genil
	 */
	//Changes using Genil extension
	public Map<String, String> getShippingAddressGuidMapFromGenil(final String partnerId)
	{
		// Get JCo connection
		final JCoConnection jCoConnection = getDefaultJCoConnection();
		//Getting GenilProxy and execute
		final IGenilProxy genilproxy;
		Map<String, String> addressMapwithNUMBER = null;
		if (sessionService.getAttribute("addressMapwithNUMBER") == null)
		{
			try
			{
				sapLogger.getLogger().info("****************calling backend for getShippingAddressGuidMapFromGenil()************** ");
				genilproxy = GenilProxyFactory.getProxy(jCoConnection, COMPONENT_SET);
				genilproxy.isInitialized();
				GenilReadRootRequestContainer rootReq;
				GenilRootDataContainer root;
				GenilQueryRequestContainer queryReqCont;
				final GenilReadAllRequestContainer childReq = null;
				addressMapwithNUMBER = new HashMap<String, String>();

				//Initializing  GenilQueryRequestContainer
				queryReqCont = genilproxy.getQueryRequestContainer(BUILD_HEADER_ADVANCE_SEARCH);

				//add some parameters
				//using a parameter container
				queryReqCont.addQueryParam(QUERY_TYPE, 'I', "EQ", partnerId, "");
				queryReqCont.setMaxHits(10);
				searchResult = genilproxy.search(queryReqCont);

				if (searchResult.isEmpty())
				{
					// Error Handling
				}
				final GenilMessageContainer msgCont = genilproxy.getGlobalMessageContainer();

				msgCont.getMessages();
				root = searchResult.get(0);
				final String customerGuid = root.getRtGuid();
				rootReq = genilproxy.getReadRequestContainer(root.getObjectName(), customerGuid);


				// Request messages
				final GenilMessageRequestContainer msgReqCont = genilproxy.getMessageRequestContainer();

				setChildRelationToRoot(rootReq, childReq, msgReqCont);
				// trigger read
				root = genilproxy.read(rootReq, msgReqCont);

				//Reading BuilAddressRelation
				final List<GenilDataContainer> addressList = root.getChilds(BUIL_ADDRESS_REL);
				//populate and add to map
				populateAddressMapwithNumber(addressList, addressMapwithNUMBER);

			}

			catch (final GenilBackendException e)
			{
				sapLogger.getLogger().error(e);
			}
			catch (final JCoException e)
			{
				sapLogger.getLogger().error(e);
			}
		}
		else
		{
			addressMapwithNUMBER = (Map<String, String>) sessionService.getAttribute("addressMapwithNUMBER");
		}
		if (sessionService.getAttribute("addressMapwithNUMBER") == null && !CollectionUtils.isEmpty(addressMapwithNUMBER))
		{
			sessionService.setAttribute("addressMapwithNUMBER", addressMapwithNUMBER);
		}
		sapLogger.getLogger().info(addressMapwithNUMBER);
		return addressMapwithNUMBER;

	}





	/**
	 * Populate address mapwith number.
	 *
	 * @param addressList
	 *           the address list
	 * @param addressMapwithNUMBER
	 *           the address mapwith number
	 */
	private void populateAddressMapwithNumber(final List<GenilDataContainer> addressList,
			final Map<String, String> addressMapwithNUMBER)
	{
		if (!CollectionUtils.isEmpty(addressList))
		{
			for (final GenilDataContainer genilDataContainer : addressList)
			{
				addressMapwithNUMBER.put(
						ConversionTools.addLeadingZerosToNumericID(genilDataContainer.getAttributeValue("ADDRESS_NUMBER"), 10),
						genilDataContainer.getAttributeValue("ADDRESS_GUID"));
			}
		}

	}

	/**
	 * Populate address mapwith guid.
	 *
	 * @param addressList
	 *           the address list
	 * @param addressMapwithGUID
	 *           the address mapwith guid
	 */
	private void populateAddressMapwithGUID(final List<GenilDataContainer> addressList,
			final Map<String, String> addressMapwithGUID)
	{
		if (!CollectionUtils.isEmpty(addressList))
		{
			for (final GenilDataContainer genilDataContainer : addressList)
			{
				addressMapwithGUID.put(genilDataContainer.getAttributeValue("ADDRESS_GUID"),
						ConversionTools.addLeadingZerosToNumericID(genilDataContainer.getAttributeValue("ADDRESS_NUMBER"), 10));
			}
		}

	}



	/**
	 * Sets the child relation to root.
	 *
	 * @param rootReqParam
	 *           the root req param
	 * @param child
	 *           the child
	 * @param msgReqCont
	 *           the msg req cont
	 */
	private void setChildRelationToRoot(final GenilReadRootRequestContainer rootReqParam,
			final GenilReadAllRequestContainer child, final GenilMessageRequestContainer msgReqCont)
	{
		GenilReadAllRequestContainer childReqparam = child;
		// Request All Attributes
		rootReqParam.setAttrRequested(true);
		// Request Address Rel
		childReqparam = rootReqParam.addChildRel(BUIL_ADDRESS_REL);
		// Request Single Attributes
		childReqparam.addRequestedAttribute("ADDRESS_GUID");
		childReqparam.addRequestedAttribute("ADDRESS_NUMBER");
		// Request messages
		msgReqCont.setMsgType(GenilMessageType.ALL);
		msgReqCont.setMsgLevel(GenilConst.MESSAGE_LEVEL_TRACE);
		msgReqCont.setGlobalMessages(true);
		msgReqCont.setObjectMessages(true);

	}
}
