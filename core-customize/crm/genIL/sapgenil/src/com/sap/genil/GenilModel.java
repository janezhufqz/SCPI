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

import java.util.HashMap;
import java.util.Map;


/**
 * This class represents the GenIL model which will be hold by GenilProxy. <br>
 *
 * @author SAP
 * @version 1.0
 *
 * @author C5230248 -- Modified by Removing WCFLocation,CacheAccess,CacheProvider,WCFCacheException,SystemId
 *
 */
public class GenilModel
{






	// componentSet added to Model an already loaded or not. If the instance of
	// the model is requested for a ComponentSet, the record in the hashmap is
	// created, but with value false. After the whole fetch of the model is
	// done, the status will be set to true. This acts similar to the old
	// 'Initialised' flag of the layer
	private Map<String, Boolean> componentSetLoaded;

	// This Map stores all objects that belong to the current model. Key is the
	// object name.
	private Map<String, GenilModelObject> objects;





	public GenilModel()
	{
		if (this.objects == null)
		{
			initModelData();
		}
	}

	@SuppressWarnings("unchecked")
	private synchronized void initModelData()
	{
		if (this.objects == null)
		{

			this.objects = new HashMap<String, GenilModelObject>(1000);
			this.componentSetLoaded = new HashMap<String, Boolean>();
		}
	}


	/**
	 * Returns the Object which matches the object name
	 *
	 * @param objectName
	 * @return
	 */
	public GenilModelObject getObject(final String objectName)
	{
		return objects.get(objectName);

	}

	// the model check for completeness (loaded and initialised) or not is now
	// dependent to the component_set, because we have only one growing model
	// the old completeness check which was related to the
	// "one model per component set"
	// GenIL is deprecated

	/**
	 * Getter-Method for property {@link #complete}. <br>
	 *
	 * @param componentSet
	 * @return Gets the {@link #complete}.
	 */

	public boolean isComplete(final String componentSet)
	{
		if (componentSetLoaded.get(componentSet) == null)
		{
			return false;
		}
		else
		{
			return componentSetLoaded.get(componentSet);
		}
	}



	/**
	 * Setter-Method for property {@link #complete}. <br>
	 *
	 * @param componentSet
	 * @param attrName
	 *           Sets the {@link #complete}.
	 */

	public void setComplete(final String componentSet)
	{
		if (componentSet != null && componentSet.length() != 0)
		{
			synchronized (componentSetLoaded)
			{
				componentSetLoaded.put(componentSet, true);
			}
		}
	}

	protected void setObject(final GenilModelObject object)
	{
		if (objects.get(object.getObjectName()) == null)
		{
			objects.put(object.getObjectName(), object);
		}
	}
}
