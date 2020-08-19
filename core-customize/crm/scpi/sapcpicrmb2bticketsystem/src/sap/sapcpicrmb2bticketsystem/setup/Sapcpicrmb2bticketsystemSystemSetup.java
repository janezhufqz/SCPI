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
package sap.sapcpicrmb2bticketsystem.setup;

import static sap.sapcpicrmb2bticketsystem.constants.Sapcpicrmb2bticketsystemConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import sap.sapcpicrmb2bticketsystem.constants.Sapcpicrmb2bticketsystemConstants;
import sap.sapcpicrmb2bticketsystem.service.Sapcpicrmb2bticketsystemService;


@SystemSetup(extension = Sapcpicrmb2bticketsystemConstants.EXTENSIONNAME)
public class Sapcpicrmb2bticketsystemSystemSetup
{
	private final Sapcpicrmb2bticketsystemService sapcpicrmb2bticketsystemService;

	public Sapcpicrmb2bticketsystemSystemSetup(final Sapcpicrmb2bticketsystemService sapcpicrmb2bticketsystemService)
	{
		this.sapcpicrmb2bticketsystemService = sapcpicrmb2bticketsystemService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		sapcpicrmb2bticketsystemService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return Sapcpicrmb2bticketsystemSystemSetup.class.getResourceAsStream("/sapcpicrmb2bticketsystem/sap-hybris-platform.png");
	}
}
