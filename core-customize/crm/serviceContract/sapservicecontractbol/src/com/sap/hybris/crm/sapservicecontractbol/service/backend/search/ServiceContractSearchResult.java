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
import java.util.List;


/**
 * Container for Service Contract Result from backend
 */
public interface ServiceContractSearchResult
{

	public void setContractId(final String contractId);

	public String getContractId();

	public void setDescription(final String description);

	public String getDescription();

	public void setStartDate(final Date startDate);

	public Date getStartDate();

	public void setEndDate(final Date endDate);

	public Date getEndDate();

	public void setSoldToParty(final String soldToParty);

	public String getSoldToParty();

	public void setCustomer(final String customer);

	public String getCustomer();

	public void setNetValue(final String netValue);

	public String getNetValue();

	public void setGrossValue(final String grossValue);

	public String getGrossValue();

	public String getStatusCode();

	public void setStatusCode(final String statusCode);

	public String getStatusProfile();

	public void setStatusProfile(final String statusProfile);

	public void setContractSysStatus(final String contractSysStatus);

	public String getContractSysStatus();

	public void setItems(final List<ServiceContractItemResult> items);

	public List<ServiceContractItemResult> getItems();

	public void setNotes(final List<String> notes);

	public List<String> getNotes();

	public String getInvoiceCreationDate();

	public String getBillingDate();

	public String getSettlementPeriod();

	public void setInvoiceCreationDate(final String invoiceCreationDate);

	public void setBillingDate(final String billingDate);

	public void setSettlementPeriod(final String settlementPeriod);

	public void setCurrency(final String currency);

	public String getCurrency();

	public void setContractGuid(final String contractGuid);

	public String getContractGuid();

	public void setContractConcatStatus(final String contractConcatStatus);

	public String getContractConcatStatus();

	public Boolean getIsRenewable();

	public void setIsRenewable(Boolean isRenewable);

}
