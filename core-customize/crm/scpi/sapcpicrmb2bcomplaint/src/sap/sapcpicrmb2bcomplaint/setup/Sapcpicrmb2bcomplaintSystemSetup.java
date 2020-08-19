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
package sap.sapcpicrmb2bcomplaint.setup;

import static sap.sapcpicrmb2bcomplaint.constants.Sapcpicrmb2bcomplaintConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import sap.sapcpicrmb2bcomplaint.constants.Sapcpicrmb2bcomplaintConstants;
import sap.sapcpicrmb2bcomplaint.service.Sapcpicrmb2bcomplaintService;


@SystemSetup(extension = Sapcpicrmb2bcomplaintConstants.EXTENSIONNAME)
public class Sapcpicrmb2bcomplaintSystemSetup
{
	private final Sapcpicrmb2bcomplaintService sapcpicrmb2bcomplaintService;

	public Sapcpicrmb2bcomplaintSystemSetup(final Sapcpicrmb2bcomplaintService sapcpicrmb2bcomplaintService)
	{
		this.sapcpicrmb2bcomplaintService = sapcpicrmb2bcomplaintService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		sapcpicrmb2bcomplaintService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return Sapcpicrmb2bcomplaintSystemSetup.class.getResourceAsStream("/sapcpicrmb2bcomplaint/sap-hybris-platform.png");
	}
}
