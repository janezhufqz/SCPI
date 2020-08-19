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
package de.hybris.platform.sap.sapcpicrmserviceorder.setup;

import static de.hybris.platform.sap.sapcpicrmserviceorder.constants.SapcpicrmserviceorderConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import de.hybris.platform.sap.sapcpicrmserviceorder.constants.SapcpicrmserviceorderConstants;
import de.hybris.platform.sap.sapcpicrmserviceorder.service.SapcpicrmserviceorderService;


@SystemSetup(extension = SapcpicrmserviceorderConstants.EXTENSIONNAME)
public class SapcpicrmserviceorderSystemSetup
{
	private final SapcpicrmserviceorderService sapcpicrmserviceorderService;

	public SapcpicrmserviceorderSystemSetup(final SapcpicrmserviceorderService sapcpicrmserviceorderService)
	{
		this.sapcpicrmserviceorderService = sapcpicrmserviceorderService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		sapcpicrmserviceorderService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return SapcpicrmserviceorderSystemSetup.class.getResourceAsStream("/sapcpicrmserviceorder/sap-hybris-platform.png");
	}
}
