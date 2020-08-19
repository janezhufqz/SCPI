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
package de.hybris.platform.sap.sapcpicrmibase.setup;

import static de.hybris.platform.sap.sapcpicrmibase.constants.SapcpicrmibaseConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import de.hybris.platform.sap.sapcpicrmibase.constants.SapcpicrmibaseConstants;
import de.hybris.platform.sap.sapcpicrmibase.service.SapcpicrmibaseService;


@SystemSetup(extension = SapcpicrmibaseConstants.EXTENSIONNAME)
public class SapcpicrmibaseSystemSetup
{
	private final SapcpicrmibaseService sapcpicrmibaseService;

	public SapcpicrmibaseSystemSetup(final SapcpicrmibaseService sapcpicrmibaseService)
	{
		this.sapcpicrmibaseService = sapcpicrmibaseService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		sapcpicrmibaseService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return SapcpicrmibaseSystemSetup.class.getResourceAsStream("/sapcpicrmibase/sap-hybris-platform.png");
	}
}
