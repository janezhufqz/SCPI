/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package com.sap.hybris.crm.sapservicecontractaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.exceptions.CoreBaseRuntimeException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sap.hybris.crm.sapservicecontract.data.ServiceContractData;
import com.sap.hybris.crm.sapservicecontractaddon.constants.SapservicecontractaddonWebConstants;
import com.sap.hybris.crm.sapservicecontractaddon.controllers.forms.EditContractForm;
import com.sap.hybris.crm.sapservicecontractaddon.controllers.forms.EditContractItemForm;
import com.sap.hybris.crm.sapservicecontractfacades.facade.ServiceContractFacade;


/**
 *
 * @author C5229488
 *
 */
@Controller
@RequestMapping("/my-account")
@RequireHardLogIn
public class AccountServiceContractsPageController extends AbstractSearchPageController
{
	private static final Logger LOGGER = Logger.getLogger(AccountServiceContractsPageController.class);
	private static final String FORM_GLOBAL_ERROR = "form.global.error";
	@Resource(name = "serviceContractFacade")
	private ServiceContractFacade serviceContractFacade;

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	/**
	 * Return page with all the service contracts for current LOGGERin customer.
	 *
	 * @param page
	 * @param showMode
	 * @param sortCode
	 * @param model
	 * @return Service Contract Listing Page
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/service-contracts", method = RequestMethod.GET)
	public String serviceContracts(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final Model model,
			@RequestParam(value = "reload", required = false, defaultValue = "false") final String reload)
			throws CMSItemNotFoundException
	{
		boolean isReloadRequired = false;
		try
		{
			final int pageSize = getSiteConfigService().getInt(SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_PAGE_SIZE_KEY,
					10);
			final PageableData pageableData = createPageableData(page, pageSize, sortCode, showMode);
			if (SapservicecontractaddonWebConstants.RELOAD_TRUE.equals(reload))
			{
				isReloadRequired = true;
			}
			final SearchPageData<ServiceContractData> searchPageData = serviceContractFacade
					.getPagedSynchronizedContractData(pageableData, isReloadRequired);
			model.addAttribute(SapservicecontractaddonWebConstants.SERVICE_CONTRACTS, searchPageData.getResults());
			populateModel(model, searchPageData, showMode);
		}
		catch (final ApplicationBaseRuntimeException e)
		{
			LOGGER.warn("Attempted to load service contracts but unable to connect to backend", e);
			model.addAttribute(SapservicecontractaddonWebConstants.BACKEND_ERROR, getMessageSource()
					.getMessage(SapservicecontractaddonWebConstants.ERROR_KEY, null, getI18nService().getCurrentLocale()));
		}
		catch (final CoreBaseRuntimeException e)
		{
			LOGGER.warn("Invalid backend type for RFC Destination is configured in backoffice. Please update the same to CRM!!!", e);
			model.addAttribute(SapservicecontractaddonWebConstants.BACKEND_ERROR, e.getMessage());
		}
		final EditContractForm renewContractForm = new EditContractForm();
		model.addAttribute("renewContractForm", renewContractForm);
		model.addAttribute(SapservicecontractaddonWebConstants.META_ROBOTS, "noindex,nofollow");
		model.addAttribute(SapservicecontractaddonWebConstants.BREAD_CRUMBS,
				accountBreadcrumbBuilder.getBreadcrumbs(SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_BREADCRUMB_KEY));
		model.addAttribute("service contracts",
				accountBreadcrumbBuilder.getBreadcrumbs(SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_BREADCRUMB_KEY));
		storeCmsPageInModel(model, getContentPageForLabelOrId(SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_PAGE));

		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_PAGE));
		return getViewForPage(model);
	}

	/**
	 * Return Contract detail page for selected Contract.
	 *
	 * @param contractId
	 * @param model
	 * @return Contract Detail Page
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/service-contract/"
			+ SapservicecontractaddonWebConstants.CONTRACT_CODE_VARIABLE_PATTERN, method = RequestMethod.GET)
	public String serviceContractDetails(@PathVariable("contractId") final String contractId, final Model model,
			@RequestParam(value = "page", defaultValue = "0") final int page, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		final EditContractForm renewContractForm = new EditContractForm();
		final EditContractItemForm renewContractItemForm = new EditContractItemForm();
		final EditContractItemForm terminateContractItemForm = new EditContractItemForm();

		try
		{
			final ServiceContractData contractData = serviceContractFacade.getSynchronizedServiceContractDetails(contractId);
			model.addAttribute("contractData", contractData);
			model.addAttribute("page", new Integer(page));
		}
		catch (final ApplicationBaseRuntimeException e)
		{
			LOGGER.debug("Attempted to load service contract details for contract ID " + "but unable to connect to backend", e);
			model.addAttribute(SapservicecontractaddonWebConstants.BACKEND_ERROR, getMessageSource()
					.getMessage(SapservicecontractaddonWebConstants.ERROR_KEY, null, getI18nService().getCurrentLocale()));
		}
		catch (final CoreBaseRuntimeException e)
		{
			LOGGER.warn("Invalid backend type for RFC Destination is configured in backoffice. Please update the same to CRM!!!", e);
			model.addAttribute(SapservicecontractaddonWebConstants.BACKEND_ERROR, e.getMessage());
		}
		catch (final UnknownIdentifierException e)
		{
			LOGGER.warn("Attempted to load a contract that does not exist or is not visible", e);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "system.error.page.not.found", null);
			return REDIRECT_PREFIX + "/";
		}
		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb("/my-account/service-contracts",
				getMessageSource().getMessage(SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_BREADCRUMB_KEY, null,
						getI18nService().getCurrentLocale()),
				null));
		breadcrumbs.add(new Breadcrumb("#",
				getMessageSource().getMessage(SapservicecontractaddonWebConstants.CONTRACT_BREADCRUMB_KEY, new Object[]
				{ contractId }, "Contract {0}", getI18nService().getCurrentLocale()), null));
		model.addAttribute("renewContractForm", renewContractForm);
		model.addAttribute("renewContractItemForm", renewContractItemForm);
		model.addAttribute("terminateContractItemForm", terminateContractItemForm);
		model.addAttribute("breadcrumbs", breadcrumbs);

		model.addAttribute(SapservicecontractaddonWebConstants.META_ROBOTS, "noindex,nofollow");
		storeCmsPageInModel(model, getContentPageForLabelOrId(SapservicecontractaddonWebConstants.SERVICE_CONTRACT_DETAILS_PAGE));
		setUpMetaDataForContentPage(model,
				getContentPageForLabelOrId(SapservicecontractaddonWebConstants.SERVICE_CONTRACT_DETAILS_PAGE));
		return getViewForPage(model);
	}

	/**
	 * Renew the selected contract.
	 *
	 * @param model
	 * @return Contract same page
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/service-contract/renew/", method = RequestMethod.POST)
	public String renewContract(final EditContractForm renewContractForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		String contractId = null;
		String contractGuid = null;
		if (!bindingResult.hasErrors())
		{
			contractId = renewContractForm.getContractId();
			contractGuid = renewContractForm.getContractGuid();
			try
			{
				final String quotationID = serviceContractFacade.renewContract(contractGuid);
				if (StringUtils.isEmpty(quotationID))
				{
					GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
							SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_RENEWAL_FAILURE_KEY, new Object[]
							{ contractId });
				}
				else
				{
					GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
							SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_RENEWAL_SUCCESS_KEY, new Object[]
							{ contractId, quotationID });
				}
			}
			catch (final ApplicationBaseRuntimeException e)
			{
				LOGGER.warn("Attempted to renew service contract for contract GUID " + contractGuid
						+ SapservicecontractaddonWebConstants.BACKEND_ERROR, e);
				GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER,
						SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_RENEWAL_FAILURE_KEY, new Object[]
						{ contractId });
			}
			catch (final CoreBaseRuntimeException e)
			{
				LOGGER.warn("Invalid backend type for RFC Destination is configured in backoffice. Please update the same to CRM!!!",
						e);
				model.addAttribute(SapservicecontractaddonWebConstants.BACKEND_ERROR, e.getMessage());
			}
		}
		else
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER, FORM_GLOBAL_ERROR, null);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_PAGE));
		return SapservicecontractaddonWebConstants.REDIRECT_TO_SERVICE_CONTRACT_LIST_PAGE;
	}

	/**
	 * Return Contract Listing page after terminating the selected Contract.
	 *
	 * @param model
	 * @return Contract Detail Page
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/service-contract-detail/terminate/", method = RequestMethod.POST)
	public String terminateContractItem(final EditContractItemForm terminateContractItemForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		final String returnAction = SapservicecontractaddonWebConstants.REDIRECT_TO_SERVICE_CONTRACT_DETAIL_PAGE;
		String terminationStatus = null;
		String contractId = null;
		String contractGuid = null;
		String productId = null;
		String itemGuid = null;
		if (!bindingResult.hasErrors())
		{
			contractId = terminateContractItemForm.getContractId();
			contractGuid = terminateContractItemForm.getContractGuid();
			productId = terminateContractItemForm.getProductId();
			itemGuid = terminateContractItemForm.getItemGuid();

			try
			{

				terminationStatus = serviceContractFacade.terminateContractItem(contractGuid, itemGuid);
				if (SapservicecontractaddonWebConstants.SUCCESS_STATUS.equals(terminationStatus))
				{
					GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
							SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_TERMINATION_SUCCESS_KEY, new Object[]
							{ productId });
				}
				else
				{
					GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER,
							SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_TERMINATION_FAILURE_KEY, new Object[]
							{ productId });
				}
			}
			catch (final ApplicationBaseRuntimeException e)
			{
				LOGGER.warn("Attempted to terminate service contract details for contract GUID " + contractGuid
						+ SapservicecontractaddonWebConstants.COMMON_ERROR_STRING, e);
				GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER,
						SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_TERMINATION_FAILURE_KEY, new Object[]
						{ productId });
			}
			catch (final CoreBaseRuntimeException e)
			{
				LOGGER.warn("Invalid backend type for RFC Destination is configured in backoffice. Please update the same to CRM!!!",
						e);
				model.addAttribute(SapservicecontractaddonWebConstants.BACKEND_ERROR, e.getMessage());
			}
		}
		else
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER, FORM_GLOBAL_ERROR, null);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_PAGE));
		return returnAction + contractId;
	}

	/**
	 * Renew the selected contract.
	 *
	 * @param model
	 * @return Contract same page
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/service-contract-detail/renewItem/", method = RequestMethod.POST)
	public String renewContractItem(final EditContractItemForm renewContractItemForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		String contractId = null;
		String contractGuid = null;
		String productId = null;
		String itemGuid = null;
		if (!bindingResult.hasErrors())
		{
			contractId = renewContractItemForm.getContractId();
			contractGuid = renewContractItemForm.getContractGuid();
			productId = renewContractItemForm.getProductId();
			itemGuid = renewContractItemForm.getItemGuid();

			try
			{
				final String status = serviceContractFacade.renewItem(contractGuid, itemGuid);
				if (SapservicecontractaddonWebConstants.SUCCESS_STATUS.equals(status))
				{
					GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
							SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_RENEWAL_ITEM_SUCCESS_KEY, new Object[]
							{ productId });

				}
				else
				{
					GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER,
							SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_RENEWAL_ITEM_FAILURE_KEY, new Object[]
							{ productId });

				}
			}
			catch (final ApplicationBaseRuntimeException e)
			{
				LOGGER.warn("Attempted to renew Item for contract ID " + contractId
						+ SapservicecontractaddonWebConstants.COMMON_ERROR_STRING, e);
				GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER,
						SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_RENEWAL_ITEM_FAILURE_KEY, new Object[]
						{ productId });

			}
			catch (final CoreBaseRuntimeException e)
			{
				LOGGER.warn("Invalid backend type for RFC Destination is configured in backoffice. Please update the same to CRM!!!",
						e);
				model.addAttribute(SapservicecontractaddonWebConstants.BACKEND_ERROR, e.getMessage());
			}
		}
		else
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER, FORM_GLOBAL_ERROR, null);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_PAGE));
		return SapservicecontractaddonWebConstants.REDIRECT_TO_SERVICE_CONTRACT_DETAIL_PAGE + contractId;
	}

	/**
	 * Renew the selected contract.
	 *
	 * @param model
	 * @return Contract same page
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/service-contract-detail/renew/", method = RequestMethod.POST)
	public String renewContractFromDetail(final EditContractForm renewContractForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		String contractId = null;
		String contractGuid = null;
		if (!bindingResult.hasErrors())
		{
			contractId = renewContractForm.getContractId();
			contractGuid = renewContractForm.getContractGuid();
			try
			{

				final String quotationID = serviceContractFacade.renewContract(contractGuid);
				if (StringUtils.isEmpty(quotationID))
				{
					GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER,
							SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_RENEWAL_FAILURE_KEY, new Object[]
							{ contractId });
				}
				else
				{
					GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
							SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_RENEWAL_SUCCESS_KEY, new Object[]
							{ contractId, quotationID });
				}
			}
			catch (final ApplicationBaseRuntimeException e)
			{
				LOGGER.warn("Attempted to renew service contract for contract GUID " + contractGuid
						+ SapservicecontractaddonWebConstants.COMMON_ERROR_STRING, e);
				GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER,
						SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_RENEWAL_FAILURE_KEY, new Object[]
						{ contractId });
			}
			catch (final CoreBaseRuntimeException e)
			{
				LOGGER.warn("Invalid backend type for RFC Destination is configured in backoffice. Please update the same to CRM!!!",
						e);
				model.addAttribute(SapservicecontractaddonWebConstants.BACKEND_ERROR, e.getMessage());
			}
		}
		else
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER, FORM_GLOBAL_ERROR, null);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SapservicecontractaddonWebConstants.SERVICE_CONTRACTS_PAGE));
		return SapservicecontractaddonWebConstants.REDIRECT_TO_SERVICE_CONTRACT_DETAIL_PAGE + contractId;
	}

}
