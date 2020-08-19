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
package sap.sapcpicrmb2breturnorder.setup;

import static sap.sapcpicrmb2breturnorder.constants.Sapcpicrmb2breturnorderConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import sap.sapcpicrmb2breturnorder.constants.Sapcpicrmb2breturnorderConstants;
import sap.sapcpicrmb2breturnorder.service.Sapcpicrmb2breturnorderService;


@SystemSetup(extension = Sapcpicrmb2breturnorderConstants.EXTENSIONNAME)
public class Sapcpicrmb2breturnorderSystemSetup
{
	private final Sapcpicrmb2breturnorderService sapcpicrmb2breturnorderService;

	public Sapcpicrmb2breturnorderSystemSetup(final Sapcpicrmb2breturnorderService sapcpicrmb2breturnorderService)
	{
		this.sapcpicrmb2breturnorderService = sapcpicrmb2breturnorderService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		sapcpicrmb2breturnorderService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return Sapcpicrmb2breturnorderSystemSetup.class.getResourceAsStream("/sapcpicrmb2breturnorder/sap-hybris-platform.png");
	}
}
