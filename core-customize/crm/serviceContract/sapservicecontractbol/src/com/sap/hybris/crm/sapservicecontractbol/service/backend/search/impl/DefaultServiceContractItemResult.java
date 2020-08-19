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
package com.sap.hybris.crm.sapservicecontractbol.service.backend.search.impl;

import java.util.Date;

import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractItemResult;


/**
 * Default Implementation of ServiceContratItemResult
 */
public class DefaultServiceContractItemResult implements ServiceContractItemResult
{

	private String productId;

	private String serviceContractId;

	private String itemNumber;

	private String description;

	private String releasedQuantity;

	private String netValue;

	private String expectedValue;

	private String targetValue;

	private String releasedValue;

	private String responseProfile;

	private String serviceProfile;

	private String currency;

	private String statusCode;

	private String statusProfile;

	private String productUnit;

	private Date validFrom;

	private Date validTo;

	private String quantity;

	private String itemGuid;

	private String itemSysStatus;

	private Date itemValidTo;

	private Date itemValidFrom;

	@Override
	public String getItemGuid()
	{
		return itemGuid;
	}

	@Override
	public void setItemGuid(final String itemGuid)
	{
		this.itemGuid = itemGuid;
	}

	@Override
	public void setProductId(final String productId)
	{
		this.productId = productId;

	}

	@Override
	public String getProductId()
	{
		return productId;
	}

	@Override
	public void setServiceContractId(final String serviceContractId)
	{
		this.serviceContractId = serviceContractId;
	}

	@Override
	public String getServiceContractId()
	{
		return serviceContractId;
	}

	@Override
	public void setItemNumber(final String itemNumber)
	{
		this.itemNumber = itemNumber;
	}

	@Override
	public String getItemNumber()
	{
		return itemNumber;
	}

	@Override
	public void setDescription(final String description)
	{
		this.description = description;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public void setQuantity(final String quantity)
	{
		this.quantity = quantity;
	}

	@Override
	public String getQuantity()
	{
		return quantity;
	}

	@Override
	public void setReleasedQuantity(final String releasedQuantity)
	{
		this.releasedQuantity = releasedQuantity;
	}

	@Override
	public String getReleasedQuantity()
	{
		return releasedQuantity;
	}

	@Override
	public void setNetValue(final String netValue)
	{
		this.netValue = netValue;
	}

	@Override
	public String getNetValue()
	{
		return netValue;
	}

	@Override
	public void setExpectedValue(final String expectedValue)
	{
		this.expectedValue = expectedValue;
	}

	@Override
	public String getExpectedValue()
	{
		return expectedValue;
	}

	@Override
	public void setTargetValue(final String targetValue)
	{
		this.targetValue = targetValue;
	}

	@Override
	public String getTargetValue()
	{
		return targetValue;
	}

	@Override
	public void setReleasedValue(final String releasedValue)
	{
		this.releasedValue = releasedValue;
	}

	@Override
	public String getReleasedValue()
	{
		return releasedValue;
	}

	@Override
	public void setResponseProfile(final String responseProfile)
	{
		this.responseProfile = responseProfile;
	}

	@Override
	public String getResponseProfile()
	{
		return responseProfile;
	}

	@Override
	public void setServiceProfile(final String serviceProfile)
	{
		this.serviceProfile = serviceProfile;
	}

	@Override
	public String getServiceProfile()
	{
		return serviceProfile;
	}

	@Override
	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	@Override
	public String getCurrency()
	{
		return currency;
	}

	@Override
	public void setProductUnit(final String productUnit)
	{
		this.productUnit = productUnit;
	}

	@Override
	public String getProductUnit()
	{
		return productUnit;
	}

	@Override
	public void setValidFrom(final Date validFrom)
	{
		this.validFrom = validFrom;
	}

	@Override
	public Date getValidFrom()
	{
		return validFrom;
	}

	@Override
	public void setValidTo(final Date validTo)
	{
		this.validTo = validTo;
	}

	@Override
	public Date getValidTo()
	{
		return validTo;
	}

	@Override
	public void setItemSysStatus(final String itemSysStatus)
	{
		this.itemSysStatus = itemSysStatus;
	}

	@Override
	public String getItemSysStatus()
	{
		return itemSysStatus;
	}

	@Override
	public void setItemValidTo(final Date itemValidTo)
	{
		this.itemValidTo = itemValidTo;
	}

	@Override
	public void setItemValidFrom(final Date itemValidFrom)
	{
		this.itemValidFrom = itemValidFrom;
	}

	@Override
	public Date getItemValidFrom()
	{
		return itemValidFrom;
	}

	@Override
	public Date getItemValidTo()
	{
		return itemValidTo;
	}

	@Override
	public String getStatusCode()
	{
		return statusCode;
	}

	@Override
	public void setStatusCode(final String statusCode)
	{
		this.statusCode = statusCode;
	}

	@Override
	public String getStatusProfile()
	{
		return statusProfile;
	}

	@Override
	public void setStatusProfile(final String statusProfile)
	{
		this.statusProfile = statusProfile;
	}

}
