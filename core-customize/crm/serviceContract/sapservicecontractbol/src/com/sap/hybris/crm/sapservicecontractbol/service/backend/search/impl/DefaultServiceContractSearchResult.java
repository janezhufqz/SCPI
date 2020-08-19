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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractItemResult;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractSearchResult;


/**
 * Container for Service Contract Serach Result
 */
public class DefaultServiceContractSearchResult implements ServiceContractSearchResult
{

	private String contractId;

	private String description;

	private Date startDate;

	private Date endDate;

	private String soldToParty;

	private String customer;

	private String netValue;

	private String grossValue;

	private String statusCode;

	private String statusProfile;

	private String contractSysStatus;

	private List<String> notes;

	private String invoiceCreationDate;

	private String billingDate;

	private String settlementPeriod;

	private String currency;

	private String contractGuid;

	private String contractConcatStatus;

	private Boolean isRenewable;

	List<ServiceContractItemResult> items = new ArrayList<ServiceContractItemResult>();

	@Override
	public void setContractId(final String contractId)
	{
		this.contractId = contractId;
	}

	@Override
	public String getContractId()
	{
		return contractId;
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
	public void setStartDate(final Date startDate)
	{
		this.startDate = startDate;
	}

	@Override
	public Date getStartDate()
	{
		return startDate;
	}

	@Override
	public void setEndDate(final Date endDate)
	{
		this.endDate = endDate;
	}

	@Override
	public Date getEndDate()
	{
		return endDate;
	}

	@Override
	public void setCustomer(final String customer)
	{
		this.customer = customer;
	}

	@Override
	public String getCustomer()
	{
		return customer;
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
	public void setGrossValue(final String grossValue)
	{
		this.grossValue = grossValue;
	}

	@Override
	public String getGrossValue()
	{
		return grossValue;
	}

	@Override
	public void setItems(final List<ServiceContractItemResult> items)
	{
		this.items = items;
	}

	@Override
	public List<ServiceContractItemResult> getItems()
	{
		return items;
	}

	@Override
	public void setNotes(final List<String> notes)
	{
		this.notes = notes;
	}

	@Override
	public List<String> getNotes()
	{
		return notes;
	}

	@Override
	public String getInvoiceCreationDate()
	{
		return invoiceCreationDate;
	}

	@Override
	public String getBillingDate()
	{
		return billingDate;
	}

	@Override
	public String getSettlementPeriod()
	{
		return settlementPeriod;
	}

	@Override
	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	@Override
	public void setInvoiceCreationDate(final String invoiceCreationDate)
	{
		this.invoiceCreationDate = invoiceCreationDate;
	}

	@Override
	public void setBillingDate(final String billingDate)
	{
		this.billingDate = billingDate;
	}

	@Override
	public void setSettlementPeriod(final String settlementPeriod)
	{
		this.settlementPeriod = settlementPeriod;
	}

	@Override
	public String getCurrency()
	{
		return currency;
	}

	@Override
	public void setSoldToParty(final String soldToParty)
	{
		this.soldToParty = soldToParty;
	}

	@Override
	public String getSoldToParty()
	{
		return soldToParty;
	}

	@Override
	public void setContractGuid(final String contractGuid)
	{
		this.contractGuid = contractGuid;

	}

	@Override
	public String getContractGuid()
	{

		return contractGuid;
	}

	@Override
	public void setContractConcatStatus(final String contractConcatStatus)
	{
		this.contractConcatStatus = contractConcatStatus;
	}

	@Override
	public String getContractConcatStatus()
	{
		return contractConcatStatus;
	}

	@Override
	public String getContractSysStatus()
	{
		return contractSysStatus;
	}

	@Override
	public void setContractSysStatus(final String contractSysStatus)
	{
		this.contractSysStatus = contractSysStatus;
	}


	@Override
	public Boolean getIsRenewable()
	{
		return isRenewable;
	}

	@Override
	public void setIsRenewable(final Boolean isRenewable)
	{
		this.isRenewable = isRenewable;
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
