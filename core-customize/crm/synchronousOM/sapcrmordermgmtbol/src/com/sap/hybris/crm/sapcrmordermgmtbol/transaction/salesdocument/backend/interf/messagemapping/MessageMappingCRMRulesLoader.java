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



import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingCRMRule;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingCRMRulesContainerImpl.Key;


/**
 *
 */
public interface MessageMappingCRMRulesLoader
{

	/**
	 * @return Map of message mapping rules
	 * @throws SAXException
	 * @throws IOException
	 */
	Map<Key, List<MessageMappingCRMRule>> loadRules() throws SAXException, IOException;

	/**
	 * @return true if non error messages should be hidden
	 */
	boolean isHideNonErrorMsg();

}
