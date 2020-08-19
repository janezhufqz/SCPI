/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package sap.sapcpicrmcomplaint.setup;

import static sap.sapcpicrmcomplaint.constants.SapcpicrmcomplaintConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import sap.sapcpicrmcomplaint.constants.SapcpicrmcomplaintConstants;
import sap.sapcpicrmcomplaint.service.SapcpicrmcomplaintService;


@SystemSetup(extension = SapcpicrmcomplaintConstants.EXTENSIONNAME)
public class SapcpicrmcomplaintSystemSetup
{
	private final SapcpicrmcomplaintService sapcpicrmcomplaintService;

	public SapcpicrmcomplaintSystemSetup(final SapcpicrmcomplaintService sapcpicrmcomplaintService)
	{
		this.sapcpicrmcomplaintService = sapcpicrmcomplaintService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		sapcpicrmcomplaintService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return SapcpicrmcomplaintSystemSetup.class.getResourceAsStream("/sapcpicrmcomplaint/sap-hybris-platform.png");
	}
}
