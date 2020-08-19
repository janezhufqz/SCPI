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
package de.hybris.platform.sap.sapcpicrminvoice.setup;

import static de.hybris.platform.sap.sapcpicrminvoice.constants.SapcpicrminvoiceConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import de.hybris.platform.sap.sapcpicrminvoice.constants.SapcpicrminvoiceConstants;
import de.hybris.platform.sap.sapcpicrminvoice.service.SapcpicrminvoiceService;


@SystemSetup(extension = SapcpicrminvoiceConstants.EXTENSIONNAME)
public class SapcpicrminvoiceSystemSetup
{
	private final SapcpicrminvoiceService sapcpicrminvoiceService;

	public SapcpicrminvoiceSystemSetup(final SapcpicrminvoiceService sapcpicrminvoiceService)
	{
		this.sapcpicrminvoiceService = sapcpicrminvoiceService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		sapcpicrminvoiceService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return SapcpicrminvoiceSystemSetup.class.getResourceAsStream("/sapcpicrminvoice/sap-hybris-platform.png");
	}
}
