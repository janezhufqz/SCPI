/*
 *
 *  [y] hybris Platform
 *
 *  Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 *  This software is the confidential and proprietary information of SAP
 *  ("Confidential Information"). You shall not disclose such Confidential
 *  Information and shall use it only in accordance with the terms of the
 *  license agreement you entered into with SAP.
 * /
 */
package com.sap.hybris.sapbusinessagreementaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sap.hybris.sapbusinessagreementaddon.constants.SapbusinessagreementaddonWebConstants;
import com.sap.hybris.sapbusinessagreementfacades.data.BusinessAgreementData;
import com.sap.hybris.sapbusinessagreementfacades.data.BusinessAgreementDetailsData;
import com.sap.hybris.sapbusinessagreementfacades.facade.BusinessAgreementFacade;


@Controller
@RequestMapping("/my-company")
@RequireHardLogIn
public class BusinessAgreementPageController extends AbstractSearchPageController
{
	private static final Logger LOG = Logger.getLogger(BusinessAgreementPageController.class);

	private static final String URL_BUSINESS_AGREEMENTS_PAGE = "/my-company/business-agreements";

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	@Resource(name = "businessAgreementFacade")
	private BusinessAgreementFacade businessAgreementFacade;

	/**
	 * Method to render all the business Agreements for logged in customer.
	 *
	 * @param page
	 * @param showMode
	 * @param sortCode
	 * @param model
	 * @return business agreement listing page
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/business-agreements", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getBusinessAgreements(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final Model model) throws CMSItemNotFoundException
	{
		try
		{

			final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
			final SearchPageData<BusinessAgreementData> searchPageData = businessAgreementFacade
					.getPagedBusinessAgreementData(pageableData);
			populateModel(model, searchPageData, showMode);
		}
		catch (final Exception e)
		{
			LOG.error("An error occured while fetching Business Agreement results", e);
		}
		model.addAttribute(SapbusinessagreementaddonWebConstants.META_ROBOTS, SapbusinessagreementaddonWebConstants.NO_INDEX);

		model.addAttribute(SapbusinessagreementaddonWebConstants.BREAD_CRUMBS,
				accountBreadcrumbBuilder.getBreadcrumbs(SapbusinessagreementaddonWebConstants.BUSINESS_AGREEMENTS_BREADCRUMB_KEY));
		model.addAttribute("business agreements",
				accountBreadcrumbBuilder.getBreadcrumbs(SapbusinessagreementaddonWebConstants.BUSINESS_AGREEMENTS_BREADCRUMB_KEY));
		storeCmsPageInModel(model, getContentPageForLabelOrId(SapbusinessagreementaddonWebConstants.BUSINESS_AGREEMENTS_PAGE));
		setUpMetaDataForContentPage(model,
				getContentPageForLabelOrId(SapbusinessagreementaddonWebConstants.BUSINESS_AGREEMENTS_PAGE));
		return getViewForPage(model);

	}

	/**
	 * Method to Render an business Agreement detail
	 *
	 * @param businessAgreementID
	 * @param model
	 * @return business agreement detail page
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/business-agreement/"
			+ SapbusinessagreementaddonWebConstants.BUSINESS_AGREEMENT_CODE_VARIABLE_PATTERN, method = RequestMethod.GET)
	public String getBusinessAgreementDetails(@PathVariable("businessAgreementID") final String businessAgreementID,
			final Model model) throws CMSItemNotFoundException
	{
		final BusinessAgreementDetailsData businessAgreementDetailsData = businessAgreementFacade
				.getBusinessAggreementDetails(businessAgreementID);
		model.addAttribute("businessAgreementDetailsData", businessAgreementDetailsData);

		model.addAttribute(SapbusinessagreementaddonWebConstants.META_ROBOTS, SapbusinessagreementaddonWebConstants.NO_INDEX);

		model.addAttribute(SapbusinessagreementaddonWebConstants.BREAD_CRUMBS,
				accountBreadcrumbBuilder.getBreadcrumbs(SapbusinessagreementaddonWebConstants.BUSINESS_AGREEMENT_BREADCRUMB_KEY));

		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb(URL_BUSINESS_AGREEMENTS_PAGE,
				getMessageSource().getMessage(SapbusinessagreementaddonWebConstants.BUSINESS_AGREEMENTS_BREADCRUMB_KEY, null,
						getI18nService().getCurrentLocale()),
				null));
		breadcrumbs.add(new Breadcrumb(URL_BUSINESS_AGREEMENTS_PAGE + businessAgreementID,
				getMessageSource().getMessage(SapbusinessagreementaddonWebConstants.BUSINESS_AGREEMENT_BREADCRUMB_KEY, new Object[]
				{ businessAgreementID }, "Business Agreement {0}", getI18nService().getCurrentLocale()), null));
		model.addAttribute("breadcrumbs", breadcrumbs);
		storeCmsPageInModel(model,
				getContentPageForLabelOrId(SapbusinessagreementaddonWebConstants.BUSINESS_AGREEMENT_DETAILS_PAGE));
		setUpMetaDataForContentPage(model,
				getContentPageForLabelOrId(SapbusinessagreementaddonWebConstants.BUSINESS_AGREEMENT_DETAILS_PAGE));

		return getViewForPage(model);
	}

}
