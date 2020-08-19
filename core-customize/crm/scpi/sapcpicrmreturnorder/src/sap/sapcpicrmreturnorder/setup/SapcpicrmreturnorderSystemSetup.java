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
package sap.sapcpicrmreturnorder.setup;

import static sap.sapcpicrmreturnorder.constants.SapcpicrmreturnorderConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import sap.sapcpicrmreturnorder.constants.SapcpicrmreturnorderConstants;
import sap.sapcpicrmreturnorder.service.SapcpicrmreturnorderService;


@SystemSetup(extension = SapcpicrmreturnorderConstants.EXTENSIONNAME)
public class SapcpicrmreturnorderSystemSetup
{
	private final SapcpicrmreturnorderService sapcpicrmreturnorderService;

	public SapcpicrmreturnorderSystemSetup(final SapcpicrmreturnorderService sapcpicrmreturnorderService)
	{
		this.sapcpicrmreturnorderService = sapcpicrmreturnorderService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		sapcpicrmreturnorderService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return SapcpicrmreturnorderSystemSetup.class.getResourceAsStream("/sapcpicrmreturnorder/sap-hybris-platform.png");
	}
}
