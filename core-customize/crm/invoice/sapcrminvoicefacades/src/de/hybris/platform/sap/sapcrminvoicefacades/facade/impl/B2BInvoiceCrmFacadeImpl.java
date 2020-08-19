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
package de.hybris.platform.sap.sapcrminvoicefacades.facade.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.sap.sapinvoiceaddon.exception.SapInvoiceException;
import de.hybris.platform.sap.sapinvoiceaddon.facade.impl.B2BInvoiceFacadeImpl;
import de.hybris.platform.sap.sapinvoiceaddon.model.SapB2BDocumentModel;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;


/**
 *
 */
public class B2BInvoiceCrmFacadeImpl extends B2BInvoiceFacadeImpl
{
	private static final Logger LOG = Logger.getLogger(B2BInvoiceCrmFacadeImpl.class.getName());



	private final String delimiterCharacter = "_";

	@Override
	public SapB2BDocumentModel getOrderForCode(final String invoiceDocumentNumber) throws SapInvoiceException
	{
		final SapB2BDocumentModel invoice = getB2BInvoiceService().getInvoiceForDocumentNumber(invoiceDocumentNumber);

		if (invoice == null)
		{
			throw new SapInvoiceException("Invoice with document number " + invoiceDocumentNumber
					+ " not found for current user in current B2Bunit");
		}
		if (invoice.getUnit().getUid() != null && !invoice.getUnit().getUid().equals(determaineSalesAreaUnit()))
		{
			throw new SapInvoiceException("Invoice with document number " + invoiceDocumentNumber + " not Valid for the user");
		}
		return invoice;
	}

	//determine sales area unit of current customer
	private String determaineSalesAreaUnit()
	{
		final B2BUnitModel parentUnit = getB2BCommerceUnitService().getRootUnit();
		if (getBaseStoreService().getCurrentBaseStore().getSAPConfiguration() == null)
		{
			LOG.debug("Sap Base Store Configuration Is Not Present");
			return null;
		}
		final String salesOrgSuffix = getBaseStoreService().getCurrentBaseStore().getSAPConfiguration()
				.getSapcommon_salesOrganization();
		final String salesDistributionChannel = getBaseStoreService().getCurrentBaseStore().getSAPConfiguration()
				.getSapcommon_distributionChannel();
		if (StringUtils.isEmpty(salesOrgSuffix) || StringUtils.isEmpty(salesDistributionChannel))
		{
			return parentUnit != null ? parentUnit.getUid() : null;
		}
		return parentUnit != null ? parentUnit.getUid() + delimiterCharacter + salesOrgSuffix + delimiterCharacter
				+ salesDistributionChannel : null;
	}
}
