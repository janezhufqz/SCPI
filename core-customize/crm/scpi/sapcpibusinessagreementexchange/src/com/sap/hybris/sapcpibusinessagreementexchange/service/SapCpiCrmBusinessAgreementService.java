/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.sapcpibusinessagreementexchange.service;

import java.util.Map;

import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementHeaderModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementPartnersModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementRuleHeaderModel;

/**
 *
 */
public interface SapCpiCrmBusinessAgreementService
{

	public void updateBusinessAgreement(BusinessAgreementModel businessAgreementModel, final Map<String, Object> businessagreementData);
	public void updateBusinessAgreementRuleHeader(BusinessAgreementRuleHeaderModel businessAgreementModel, final Map<String, Object> businessagreementData);
	public void updateBusinessAgreementPartners(BusinessAgreementPartnersModel businessAgreementModel, final Map<String, Object> businessagreementData);
	public void updateBusinessAgreementHeader(BusinessAgreementHeaderModel businessAgreementModel, final Map<String, Object> businessagreementData);

}
