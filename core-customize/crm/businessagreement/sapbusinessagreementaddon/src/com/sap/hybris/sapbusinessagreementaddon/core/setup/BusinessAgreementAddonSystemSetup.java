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
package com.sap.hybris.sapbusinessagreementaddon.core.setup;

import de.hybris.platform.addonsupport.setup.impl.DefaultAddOnSystemSetupSupport;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;


@SystemSetup(extension = "sapbusinessagreementaddon")
public class BusinessAgreementAddonSystemSetup extends DefaultAddOnSystemSetupSupport
{
	private TypeService typeService;
	private ModelService modelService;

	public static final String IMPORT_SYNC_CONTENT = "syncContent";
	public static final String IMPORT_COCKPIT_COMPONENTS = "cockpitComponents";
	public static final String IMPORT_ACCESS_RIGHTS = "accessRights";
	public static final String DATA_IMPORT_FOLDER = "sapbusinessagreementaddon";
	public static final String CONTENT_CATALOG = "/contentCatalogs/";

	private String productCatalogNameForRerun = null; // null indicates no rerun
																	  // needed

	/**
	 * Change the boolean value passed to each SystemSetup parameters based on your requirements
	 */

	@Override
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();
		params.add(createBooleanSystemSetupParameter(IMPORT_SYNC_CONTENT, "Sync Content", true));
		params.add(createBooleanSystemSetupParameter(IMPORT_COCKPIT_COMPONENTS, "Import Cockpit Components", true));
		params.add(createBooleanSystemSetupParameter(IMPORT_ACCESS_RIGHTS, "Import Users & Groups", true));
		return params;
	}

	/**
	 * Implement this method to create data that is used in your project. This method will be called during the system
	 * initialization.
	 *
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{
		// Add Store imports here as you require
		importContentCatalog(context, DATA_IMPORT_FOLDER, "electronics");
		importContentCatalog(context, DATA_IMPORT_FOLDER, "powertools");

	}

	protected void importContentCatalog(final SystemSetupContext context, final String importDirectory, final String catalogName)
	{
		logInfo(context, "Begin importing Content Catalog [" + catalogName + "]");

		final String importRoot = "/" + importDirectory + "/import";

		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-content.impex", false);
		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-mobile-content.impex", false);
		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/email-content.impex", false);

		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-addon-extra.impex", false);
		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-content_en.impex", false);
		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-content_hi.impex", false);
		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-content_ja.impex", false);
		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-content_ru.impex", false);
		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-content_zh.impex", false);
		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-content_zh_CN.impex", false);

		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-responsive-content.impex", false);

		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-responsive-content_en.impex",
				false);
		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-responsive-content_de.impex",
				false);
		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-responsive-content_hi.impex",
				false);
		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-responsive-content_ja.impex",
				false);
		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-responsive-content_ru.impex",
				false);
		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-responsive-content_zh.impex",
				false);
		importImpexFile(context, importRoot + CONTENT_CATALOG + catalogName + "ContentCatalog/cms-responsive-content_zh_CN.impex",
				false);

		logInfo(context, "Done importing Content Catalog [" + catalogName + "]");
		synchronizeContentCatalog(context, catalogName, true);
		logInfo(context, "Done synchronizing Content Catalog [" + catalogName + "]");

	}

	protected void synchronizeContentCatalog(final SystemSetupContext context, final String catalogName, final boolean sync)
	{
		logInfo(context, "Begin synchronizing Content Catalog [" + catalogName + "] - "
				+ (sync ? "synchronizing" : "initializing job"));

		createContentCatalogSyncJob(context, catalogName + "ContentCatalog");

		if (sync)
		{
			executeCatalogSyncJob(context, catalogName + "ContentCatalog");
		}

		if (sync && StringUtils.isNotEmpty(this.productCatalogNameForRerun))
		{
			logInfo(context, "Rerunnig product catalog synchronization for  [" + this.productCatalogNameForRerun + "]");
			if (isSyncRerunNeeded(executeCatalogSyncJob(context, this.productCatalogNameForRerun + "ProductCatalog")))
			{
				logInfo(context, "Rerunnig product catalog synchronization for [" + this.productCatalogNameForRerun
						+ "], failed   please consult logs for more details.");
			}
			this.productCatalogNameForRerun = null;
		}

		logInfo(context, "Done " + (sync ? "synchronizing" : "initializing job") + " Content Catalog [" + catalogName + "]");

	}

	/**
	 * @return the typeService
	 */
	public TypeService getTypeService()
	{
		return typeService;
	}

	/**
	 * @param typeService
	 *           the typeService to set
	 */
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
