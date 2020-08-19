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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCRMCallbackProcessor;





/**
 * Loads call backs during message mapping
 */
public class MessageMappingCRMCallbackLoader
{

	@Autowired
	private ApplicationContext appContext;


	/**
	 * Loads call backs
	 *
	 * @return Map of call backs with ID as key
	 */
	public Map<String, MessageMappingCRMCallbackProcessor> loadCallbacks()
	{

		final Map<String, MessageMappingCRMCallbackProcessor> callbackProcessors = new HashMap<String, MessageMappingCRMCallbackProcessor>();

		final Map<String, MessageMappingCRMCallbackProcessor> processors = appContext
				.getBeansOfType(MessageMappingCRMCallbackProcessor.class);


		for (final Map.Entry<String, MessageMappingCRMCallbackProcessor> entry : processors.entrySet())
		{
			callbackProcessors.put(entry.getValue().getId(), entry.getValue());
		}

		return callbackProcessors;

	}


}
