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
package sap.sapcpicrmticketsystem.setup;

import static sap.sapcpicrmticketsystem.constants.SapcpicrmticketsystemConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import sap.sapcpicrmticketsystem.constants.SapcpicrmticketsystemConstants;
import sap.sapcpicrmticketsystem.service.SapcpicrmticketsystemService;


@SystemSetup(extension = SapcpicrmticketsystemConstants.EXTENSIONNAME)
public class SapcpicrmticketsystemSystemSetup
{
	private final SapcpicrmticketsystemService sapcpicrmticketsystemService;

	public SapcpicrmticketsystemSystemSetup(final SapcpicrmticketsystemService sapcpicrmticketsystemService)
	{
		this.sapcpicrmticketsystemService = sapcpicrmticketsystemService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		sapcpicrmticketsystemService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return SapcpicrmticketsystemSystemSetup.class.getResourceAsStream("/sapcpicrmticketsystem/sap-hybris-platform.png");
	}
}
