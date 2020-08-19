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
package com.sap.hybris.crm.sapcrmserviceorderaddon.setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.sap.hybris.crm.sapcrmserviceorderaddon.constants.SapcrmserviceorderaddonConstants;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

/**
 * This class provides hooks into the system's initialization and update
 * processes.
 *
 * @see "https://wiki.hybris.com/display/release4/Hooks+for+Initialization+and+Update+Process"
 */
@SystemSetup(extension = SapcrmserviceorderaddonConstants.EXTENSIONNAME)
public class InitialDataSystemSetup extends AbstractSystemSetup {

	public static final String IMPORT_SYNC_CATALOGS = "contentCatalogs";
	public static final String POWERTOOLS = "powertools";
	public static final String ELECTRONICS = "electronics";
	public static final String APPLICATION_TYPE = "sapcrmserviceorderaddon.content.import.applicationtype";

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	/**
	 * Generates the Dropdown and Multi-select boxes for the project data import
	 */
	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions() {
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

		params.add(createBooleanSystemSetupParameter(IMPORT_SYNC_CATALOGS, "Sync Content Catalogs", true));

		return params;
	}

	/**
	 * This method will be called during the system initialization.
	 *
	 * @param context
	 *            the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext context) {

		if (getBooleanSystemSetupParameter(context, IMPORT_SYNC_CATALOGS)) {

			final List<String> applicationTypes = Arrays
					.asList((getConfigurationService().getConfiguration().getString(APPLICATION_TYPE)).split(","));
			for (final String applicationType : applicationTypes) {
				if ("B2B".equalsIgnoreCase(applicationType)) {
					importContentCatalog(context, POWERTOOLS, Collections.singletonList(POWERTOOLS));
				}

				if ("B2C".equalsIgnoreCase(applicationType)) {
					importContentCatalog(context, ELECTRONICS, Collections.singletonList(ELECTRONICS));
				}
			}
		}
	}

	protected boolean synchronizeContentCatalog(final SystemSetupContext context, final String catalogName,
			final boolean sync) {
		logInfo(context, "Begin synchronizing Content Catalog [" + catalogName + "] - "
				+ (sync ? "synchronizing" : "initializing job"));

		createContentCatalogSyncJob(context, catalogName + "ContentCatalog");

		boolean result = true;

		if (sync) {
			final PerformResult syncCronJobResult = executeCatalogSyncJob(context, catalogName + "ContentCatalog");
			if (isSyncRerunNeeded(syncCronJobResult)) {
				logInfo(context, "Catalog catalog [" + catalogName + "] sync has issues.");
				result = false;
			}
		}

		logInfo(context,
				"Done " + (sync ? "synchronizing" : "initializing job") + " Content Catalog [" + catalogName + "]");
		return result;
	}

	protected void importContentCatalog(final SystemSetupContext context, final String catalogName,
			final List<String> contentCatalogs) {
		logInfo(context, "Begin importing catalog [" + catalogName + "]");
		String filePath = "/sapcrmserviceorderaddon/import/contentCatalogs/" + catalogName;
		importImpexFile(context, filePath + "ContentCatalog/cms-content.impex", true);
		importImpexFile(context, filePath + "ContentCatalog/cms-content_en.impex", true);
		importImpexFile(context, filePath + "ContentCatalog/cms-content_de.impex", true);
		importImpexFile(context, filePath + "ContentCatalog/cms-content_es_CO.impex", true);
		importImpexFile(context, filePath + "ContentCatalog/cms-content_fr.impex", true);
		importImpexFile(context, filePath + "ContentCatalog/cms-content_hi.impex", true);
		importImpexFile(context, filePath + "ContentCatalog/cms-content_id.impex", true);
		importImpexFile(context, filePath + "ContentCatalog/cms-content_it.impex", true);
		importImpexFile(context, filePath + "ContentCatalog/cms-content_ja.impex", true);
		importImpexFile(context, filePath + "ContentCatalog/cms-content_ko.impex", true);
		importImpexFile(context, filePath + "ContentCatalog/cms-content_pt.impex", true);
		importImpexFile(context, filePath + "ContentCatalog/cms-content_ru.impex", true);
		importImpexFile(context, filePath + "ContentCatalog/cms-content_zh_CN.impex", true);
		importImpexFile(context, filePath + "ContentCatalog/cms-content_zh_TW.impex", true);
		importImpexFile(context, filePath + "ContentCatalog/cms-content_zh.impex", true);

		logInfo(context, "Done importing catalog [" + catalogName + "]");

		// perform content sync jobs
		for (final String contentCatalog : contentCatalogs) {
			synchronizeContentCatalog(context, contentCatalog, true);
		}
	}

	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	/**
	 * @param configurationService
	 *            the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
