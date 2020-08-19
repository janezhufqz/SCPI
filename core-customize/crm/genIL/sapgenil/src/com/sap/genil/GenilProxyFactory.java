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

import de.hybris.platform.sap.core.bol.backend.jco.BackendBusinessObjectBaseJCo;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.impl.JCoConnectionStateless;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sap.conn.jco.JCoException;
import com.sap.genil.exception.GenilBackendException;


/**
 * Responsible for instantiating proxies<br>
 *
 * @author SAP
 * @version 1.0
 *
 * @author C5230248 -- Modified by Removing Genil JCoConnection and added Hybris
 *         JCoConnection,BackendBusinessObjectBaseJCo
 *
 */
public class GenilProxyFactory extends BackendBusinessObjectBaseJCo
{

	/**
	 * @param properties
	 */

	// loadedComponetSetsPerconnection holds per JCO Connection the list of
	// loaded component sets this is required because the Backend caches per
	// connection the Componet_sets in a model. This differs to the JAVA
	// where we hold the complete model once over all users/sessions
	private static Map<String, HashSet<String>> loadedComponetSetsPerConnection;
	private static int lastConnectionHash;
	private static String lastComponentRequested;
	private static char lastComponentSetLoadLevel;

	static
	{
		loadedComponetSetsPerConnection = new HashMap<String, HashSet<String>>();
	}

	/**
	 * Gets back a proxy instance which corresponds either to a statefull proxy or to a stateless proxy.
	 *
	 * @param connection
	 *           decides if the proxy is going to be a statefull or a stateless proxy<br>
	 * @param componentSet
	 *           to be loaded
	 * @return the proxy created
	 * @throws GenilBackendException
	 * @throws JCoException 
	 */
	public static IGenilProxy getProxy(final JCoConnection connection, final String componentSet) throws GenilBackendException, JCoException
	{
		return getProxy(connection, componentSet, true);
	}

	/**
	 * Gets back a proxy instance which corresponds either to a statefull proxy or to a stateless proxy.
	 *
	 * @param connection
	 *           decides if the proxy is going to be a statefull or a stateless proxy<br>
	 * @param componentSet
	 *           to be loaded
	 * @param ignoreModelError
	 *           ignore errors in GENIL model
	 * @return the proxy created
	 * @throws GenilBackendException
	 */
	public static IGenilProxy getProxy(final JCoConnection connection, final String componentSet, final boolean ignoreModelErrors)
			throws GenilBackendException, JCoException
	{

		lastComponentSetLoadLevel = GenilConst.COMPONENT_SET_ALREADY_LOADED;

		if (componentSet == null || connection == null)
		{
			final String msg = "Invalid Parameters for Proxy Generation";
			throw new GenilBackendException(msg);
		}

		lastComponentRequested = componentSet;
		lastConnectionHash = connection.hashCode();

		final String connectionKey = Integer.toString(connection.hashCode());
		Set<String> loadedComponentSets = loadedComponetSetsPerConnection.get(connectionKey);
		if (loadedComponentSets == null)
		{
			loadedComponentSets = new HashSet<String>();
			loadedComponentSets.add(componentSet);
			loadedComponetSetsPerConnection.put(connectionKey, (HashSet<String>) loadedComponentSets);
			lastComponentSetLoadLevel = GenilConst.COMPONENT_SET_INIT_AND_LOAD;
		}
		else
		{
			if (!loadedComponentSets.contains(componentSet))
			{
				loadedComponentSets.add(componentSet);
				lastComponentSetLoadLevel = GenilConst.COMPONENT_SET_LOAD;
			}
		}
		IGenilProxy proxy=null;
                if(connection instanceof JCoConnectionStateless){
                    proxy= new GenilProxyStateless(connection, componentSet, ignoreModelErrors);
                }
                else{
                    proxy =new GenilProxyStateful(connection, componentSet, ignoreModelErrors, lastComponentSetLoadLevel);
                }
                    
		return proxy;
	}

	public static int getLastConnectionHash()
	{
		return lastConnectionHash;
	}

	public static String getLastComponentRequested()
	{
		return lastComponentRequested;
	}

	public static char getLastComponentSetLoadLevel()
	{
		return lastComponentSetLoadLevel;
	}
}
