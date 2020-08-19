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

/**
 *
 */
package de.hybris.platform.sap.sapcrminvoiceaddon.aspect;

import de.hybris.platform.store.services.BaseStoreService;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.Model;


/**
 * @author C5230258
 *
 */
public class SapCrmInvoiceViewSwitchUIComponentsAspect
{



	public static final Logger LOG = Logger.getLogger(SapCrmInvoiceViewSwitchUIComponentsAspect.class);
	public static final String INVOICE_ADDON_PREFIX = "addon:/sapcrminvoiceaddon/";
	public static final String REDIRECT_PREFIX = "redirect:";
	public static final String FORWARD_PREFIX = "forward:";
	public static final String ADDON_PREFIX = "addon:";

	private BaseStoreService baseStoreService;


	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}


	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * Apply the UI changes when switching the View of Account Summary
	 *
	 * @param pjp
	 * @return the UI component name
	 * @throws Throwable
	 */
	public Object applyUIChanges(final ProceedingJoinPoint pjp) throws Throwable
	{

		String uiComponent = pjp.proceed().toString();

		if (isUIChangeRequired(uiComponent))
		{
			final StringBuilder prefix = new StringBuilder(INVOICE_ADDON_PREFIX);
			prefix.append(uiComponent);
			uiComponent = prefix.toString();

			logInfoMessage(pjp.getSignature().toString(), uiComponent, true);

		}
		else
		{
			logInfoMessage(pjp.getSignature().toString(), uiComponent, false);
		}
		return uiComponent;

	}

	/**
	 * Switch order page
	 *
	 * @param pjp
	 * @return UI Component
	 * @throws Throwable
	 */
	public void switchOrderInvoicePage(final ProceedingJoinPoint pjp) throws Throwable
	{
		Model uiModel = null;
		final Object[] uiComponents = pjp.getArgs();
		for (final Object uiComponent : uiComponents)
		{
			if (uiComponent instanceof Model)
			{
				uiModel = (Model) uiComponent;
			}
			uiModel.addAttribute("showInvoiceDownloadButton", Boolean.TRUE);
		}
		pjp.proceed();
	}


	/**
	 * Switch the UI component definition from the accountsummaryaddon add-on to the sapinvoiceaddon add-on
	 *
	 * @param pjp
	 * @return the UI component name
	 * @throws Throwable
	 */
	public void switchAddonUIComponent(final ProceedingJoinPoint pjp) throws Throwable
	{
		Model uiModel = null;
		final Object[] uiComponents = pjp.getArgs();
		for (final Object uiComponent : uiComponents)
		{
			if (uiComponent instanceof Model)
			{
				uiModel = (Model) uiComponent;
			}
			uiModel.addAttribute("showInvoiceDownloadButton", Boolean.TRUE);
		}
		pjp.proceed();

	}


	/**
	 * Log an information message
	 *
	 * @param methodSignature
	 * @param uiComponent
	 * @param somEnabled
	 */
	private void logInfoMessage(final String methodSignature, final String uiComponent, final boolean somEnabled)
	{
		if (LOG.isInfoEnabled())
		{
			if (somEnabled)
			{
				LOG.info("The synchronous order scenario SOM is active and the intercepted method is [" + methodSignature
						+ "]. The synchronous order UI component is [" + uiComponent + "]");
			}
			else
			{
				LOG.info("The asynchronous order scenario AOM is active and the intercepted method is [" + methodSignature
						+ "]. The asynchronous order UI component is [" + uiComponent + "]");
			}
		}
	}

	//	/**
	//	 * Check if synchronous order management SOM is active
	//	 *
	//	 * @return true is SOM is active
	//	 */
	//	protected boolean isSapOrderMgmtEnabled()
	//	{
	//		return getBaseStoreService().getCurrentBaseStore().getSAPConfiguration() != null
	//				&& getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().isSapordermgmt_enabled();
	//	}

	/**
	 * @param uiComponent
	 * @return true if the UI component switch is required
	 */
	protected boolean isUIChangeRequired(final String uiComponent)
	{
		return true;

	}
}
