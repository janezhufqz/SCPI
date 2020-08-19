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
package sap.sapcpicrmb2bcomplaint.controller;

import static sap.sapcpicrmb2bcomplaint.constants.Sapcpicrmb2bcomplaintConstants.PLATFORM_LOGO_CODE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import sap.sapcpicrmb2bcomplaint.service.Sapcpicrmb2bcomplaintService;


@Controller
public class Sapcpicrmb2bcomplaintHelloController
{
	@Autowired
	private Sapcpicrmb2bcomplaintService sapcpicrmb2bcomplaintService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String printWelcome(final ModelMap model)
	{
		model.addAttribute("logoUrl", sapcpicrmb2bcomplaintService.getHybrisLogoUrl(PLATFORM_LOGO_CODE));
		return "welcome";
	}
}
