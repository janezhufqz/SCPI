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
package com.sap.hybris.crm.sapcrmpricingb2b.services.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.store.services.BaseStoreService;

import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.crm.sapcrmpricing.services.impl.SapCRMCartCalculationService;


/**
 *
 */
public class SapCRMB2bCartCalculationService extends SapCRMCartCalculationService
{

	private BaseStoreService baseStoreService;

	@Override
	protected void resetAllValues(final AbstractOrderEntryModel entry) throws CalculationException
	{
		if (!isSyncOrdermgmtEnabled())
		{
			super.resetAllValues(entry);
		}
	}

	/**
	 * @return boolean whether SOM is enabled OR not
	 */
	protected boolean isSyncOrdermgmtEnabled()
	{
		return (getBaseStoreService().getCurrentBaseStore() != null && getBaseStoreService().getCurrentBaseStore()
				.getSAPConfiguration() != null)
				&& (getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().isSapordermgmt_enabled());
	}

	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}


}
