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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.messagemapping;

import java.util.Map;

import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingCRMRule;


/**
 * Container for the message mapping rules maintained in messages.xml
 */
public interface MessageMappingCRMRulesContainer
{

	/**
	 * @return Do we hide info and warning messages?
	 */
	public boolean isHideNonErrorMsg();

	/**
	 * @param beMes
	 * @return The most narrow mapping rule
	 */
	public MessageMappingCRMRule mostNarrow(MessageMappingCRMRule.Pattern beMes);

	/**
	 * @return Map of callback implementations
	 */
	Map<String, MessageMappingCRMCallbackProcessor> getCallbacks();

}
