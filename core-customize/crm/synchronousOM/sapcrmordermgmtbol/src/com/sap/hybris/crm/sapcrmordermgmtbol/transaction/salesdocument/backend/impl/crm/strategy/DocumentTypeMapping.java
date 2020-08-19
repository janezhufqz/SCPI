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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm.strategy;

import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;

import org.springframework.util.StringUtils;


/**
 * Class to Map R3 backend specific documents to Ecommerce specific ones.
 **/
public class DocumentTypeMapping
{
	/**
	 * Method maps the R3 type (TRANS_GROUP or TRVOG) to document type.
	 *
	 * @param type
	 *           (R3 type)
	 * @return String document type
	 */

	private DocumentTypeMapping()
	{


	}

	//TODO: Need implementation once quotation will be implemented
	public static DocumentType getDocumentTypeByTransactionGroup(final String type)
	{
		if (!StringUtils.isEmpty(type))
		{

			return DocumentType.ORDER;
		}
		else
		{
			return DocumentType.ORDER;
		}
	}


}
