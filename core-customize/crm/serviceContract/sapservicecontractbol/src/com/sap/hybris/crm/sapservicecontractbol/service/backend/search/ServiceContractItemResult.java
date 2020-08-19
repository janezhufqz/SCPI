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
package com.sap.hybris.crm.sapservicecontractbol.service.backend.search;

import java.util.Date;


/**
 * Container for Service Contract Item result from backend
 */
public interface ServiceContractItemResult
{
	public void setProductId(final String productId);

	public String getProductId();

	public void setServiceContractId(final String serviceContractId);

	public String getServiceContractId();

	public void setItemNumber(final String itemNumber);

	public String getItemNumber();

	public void setDescription(final String description);

	public String getDescription();

	public void setQuantity(final String quantity);

	public String getQuantity();

	public void setReleasedQuantity(final String releasedQuantity);

	public String getReleasedQuantity();

	public void setNetValue(final String netValue);

	public String getNetValue();

	public void setExpectedValue(final String expectedValue);

	public String getExpectedValue();

	public void setTargetValue(final String targetValue);

	public String getTargetValue();

	public void setReleasedValue(final String releasedValue);

	public String getReleasedValue();

	public void setResponseProfile(final String responseProfile);

	public String getResponseProfile();

	public void setServiceProfile(final String serviceProfile);

	public String getServiceProfile();

	public void setCurrency(final String currency);

	public String getCurrency();

	public String getStatusCode();

	public void setStatusCode(final String statusCode);

	public String getStatusProfile();

	public void setStatusProfile(final String statusProfile);

	public void setItemSysStatus(final String itemSysStatus);

	public String getItemSysStatus();

	public void setProductUnit(final String productUnit);

	public String getProductUnit();

	public void setValidFrom(final Date validFrom);

	public Date getValidFrom();

	public void setValidTo(final Date validTo);

	public Date getValidTo();

	public String getItemGuid();

	public void setItemGuid(String itemGuid);

	public void setItemValidTo(final Date validTo);

	public void setItemValidFrom(final Date validFrom);

	public Date getItemValidFrom();

	public Date getItemValidTo();
}
