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

package com.sap.hybris.crm.crmcustomerticketingaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.util.XSSFilterUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketCategory;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingfacades.data.TicketRelatedObjectData;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sap.hybris.crm.crmcustomerticketingaddon.constants.CrmcustomerticketingaddonWebConstants;
import com.sap.hybris.crm.crmcustomerticketingaddon.forms.SapCrmSupportTicketForm;
import com.sap.hybris.crm.crmcustomerticketingfacades.facade.SapCrmTicketFacade;
import com.sap.hybris.crm.sapcrmcategoryschema.facade.SapCrmCategoriesFacade;
import com.sap.security.core.server.csi.XSSEncoder;

/**
 *
 */

/**
 * @author C5230711
 *
 */

@Controller
@RequestMapping("/my-account")
public class AccountCRMSupportTicketPageController extends AbstractSearchPageController {

    /**
     *
     */
    private static final String TEXT_ACCOUNT_SUPPORTTICKETS_UPDATE_SUPPORT_TIECKT = "text.account.supporttickets.updateSupportTicket";

    /**
     *
     */
    private static final String TEXT_ACCOUNT_SUPPORTTICKETS_TRY_LATER = "text.account.supporttickets.tryLater";

    private static final Logger LOG = Logger.getLogger(AccountCRMSupportTicketPageController.class);

    // CMS Pages
    // CMS Pages

    private static final String ADD_SUPPORT_TICKET_PAGE = "crm-add-support-ticket";
    private static final String UPDATE_SUPPORT_TICKET_PAGE = "crm-update-support-ticket";
    private static final String SUPPORT_TICKETS_PAGE = "crm-support-tickets";
    private static final String SUPPORT_TICKET_FORM = "sapCrmSupportTicketForm";
    private static final String SUPPORT_TICKET_DATA = "ticketData";
    private static final String SUPPORT_TICKET_CODE_PATH_VARIABLE_PATTERN = "{ticketId:.*}";
    private static final String SUPPORT_TICKET_CATEGORY_PATH_VARIABLE_PATTERN = "{category:.*}";
    private static final String REDIRECT_TO_SUPPORT_TICKETS_PAGE = REDIRECT_PREFIX + "/my-account/crm-support-tickets";
    private static final String REDIRECT_TO_CREATE_COMPLAINT_PAGE = REDIRECT_PREFIX + "/my-account/crm-add-complaint";
    private static final String SUPPORT_TICKET_ASSOCIATED_OBJECTS = "associatedObjects";
    private static final String SUPPORT_TICKET_CATEGORY = "category";
    private static final String SUPPORT_TICKET_CATEGORIES = "categories";
    private static final String SUPPORT_TICKET_PRIORITIES = "priorities";
    private static final String SUPPORT_TICKET_REASON_CATEGORIES_LEVEL1 = "reasonCategories1";
    private static final String SUPPORT_TICKET_REASON_CATEGORIES_LEVEL2 = "reasonCategories2";
    private static final String SUPPORT_TICKET_REASON_CATEGORIES_LEVEL3 = "reasonCategories3";
    private static final String SUPPORT_TICKET_REASON_CATEGORIES_LEVEL4 = "reasonCategories4";

    @Resource(name = "accountBreadcrumbBuilder")
    private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

    @Resource(name = "defaultTicketFacade")
    private SapCrmTicketFacade ticketFacade;

    @Resource(name = "cartFacade")
    private CartFacade cartFacade;

    @Resource(name = "customerFacade")
    private CustomerFacade customerFacade;

    @Resource(name = "sapCategoriesFacade")
    private SapCrmCategoriesFacade sapCategoriesFacade;

    /**
     * Used for retrieving page to create a customer support ticket.
     *
     * @param model
     * @return View String
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = "/crm-add-support-ticket/"
            + SUPPORT_TICKET_CATEGORY_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    @RequireHardLogIn
    public String addSupportTicket(@PathVariable("category") final TicketCategory category,
            @RequestParam final Map<String, String> queryMap, final Model model) throws CMSItemNotFoundException {
        if (TicketCategory.COMPLAINT == category) {
            return REDIRECT_TO_CREATE_COMPLAINT_PAGE;
        } else {
            final SapCrmSupportTicketForm stf = new SapCrmSupportTicketForm();

            if (!queryMap.isEmpty()) {

                final List<TicketRelatedObjectData> tickecketRelatedObjList = ticketFacade
                        .setQueryKeyParamToRelatedObject(queryMap);
                final TicketData ticketData = new TicketData();
                if (!tickecketRelatedObjList.isEmpty()) {
                    stf.setTicketRelatedObjectData(tickecketRelatedObjList);
                    ticketData.setRelatedObjects(tickecketRelatedObjList);
                    model.addAttribute(SUPPORT_TICKET_DATA, ticketData);
                }
            }
            storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_SUPPORT_TICKET_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_SUPPORT_TICKET_PAGE));

            model.addAttribute(CrmcustomerticketingaddonWebConstants.BREAD_CRUMBS,
                    getBreadcrumbs("text.account.supporttickets.addSupportTicket"));
            model.addAttribute(CrmcustomerticketingaddonWebConstants.META_ROBOTS,
                    CrmcustomerticketingaddonWebConstants.NO_INDEX_NO_FOLLOW);
            model.addAttribute(SUPPORT_TICKET_FORM, stf);

            try {
                setModelAttributes(model, null, category);
            } catch (final UnsupportedOperationException | UnknownIdentifierException | IllegalArgumentException ex) {
                LOG.info("Error occured while laoding ticket list" + ex);
            }
            return getViewForPage(model);
        }
    }

    /**
     * Creates a ticket.
     *
     * @param sapCrmSupportTicketForm
     * @param bindingResult
     * @param model
     * @param redirectModel
     * @return View String
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = "/crm-add-support-ticket/"
            + SUPPORT_TICKET_CATEGORY_PATH_VARIABLE_PATTERN, method = RequestMethod.POST)
    @RequireHardLogIn
    public String addSupportTicket(@PathVariable("category") final TicketCategory category,
            @Valid final SapCrmSupportTicketForm sapCrmSupportTicketForm, final BindingResult bindingResult,
            final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        TicketData ticketData = null;
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");

            model.addAttribute(CrmcustomerticketingaddonWebConstants.BREAD_CRUMBS,
                    getBreadcrumbs("text.account.supporttickets.addSupportTieckt"));

            if (sapCrmSupportTicketForm.getTicketRelatedObjectData() != null
                    && !sapCrmSupportTicketForm.getTicketRelatedObjectData().isEmpty()) {
                ticketData = new TicketData();
                ticketData.setRelatedObjects(sapCrmSupportTicketForm.getTicketRelatedObjectData());
                model.addAttribute(SUPPORT_TICKET_DATA, ticketData);
            }
            model.addAttribute(SUPPORT_TICKET_FORM, sapCrmSupportTicketForm);
            setModelAttributes(model, null, category);
            storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_SUPPORT_TICKET_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_SUPPORT_TICKET_PAGE));
            return getViewForPage(model);
        }
        try {

            ticketData = ticketFacade.createTicket(this.populateTicketData(sapCrmSupportTicketForm));
        } catch (final ModelNotFoundException e) {
            LOG.debug("Ticket creation failed", e);
            ticketData = null;
        }
        if (ticketData == null) {
            GlobalMessages.addErrorMessage(model, TEXT_ACCOUNT_SUPPORTTICKETS_TRY_LATER);

            if (sapCrmSupportTicketForm.getTicketRelatedObjectData() != null
                    && !sapCrmSupportTicketForm.getTicketRelatedObjectData().isEmpty()) {
                ticketData = new TicketData();
                ticketData.setRelatedObjects(sapCrmSupportTicketForm.getTicketRelatedObjectData());
                model.addAttribute(SUPPORT_TICKET_DATA, ticketData);

            }
            model.addAttribute(SUPPORT_TICKET_FORM, sapCrmSupportTicketForm);
            setModelAttributes(model, null, category);
            storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_SUPPORT_TICKET_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_SUPPORT_TICKET_PAGE));
            return getViewForPage(model);
        }
        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
                "text.account.supportTicket.confirmation.ticket.added", null);

        return REDIRECT_TO_SUPPORT_TICKETS_PAGE;
    }

    /**
     * Get Ticket Details.
     *
     * @param ticketId
     * @param model
     * @param redirectModel
     * @return View String
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = "/crm-support-ticket/"
            + SUPPORT_TICKET_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    @RequireHardLogIn
    public String getSupportTicket(@PathVariable("ticketId") final String ticketId, final Model model,
            final HttpServletRequest request, final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_SUPPORT_TICKET_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_SUPPORT_TICKET_PAGE));
        final SapCrmSupportTicketForm sf = new SapCrmSupportTicketForm();
        model.addAttribute(CrmcustomerticketingaddonWebConstants.BREAD_CRUMBS,
                getBreadcrumbs(TEXT_ACCOUNT_SUPPORTTICKETS_UPDATE_SUPPORT_TIECKT));
        model.addAttribute(CrmcustomerticketingaddonWebConstants.META_ROBOTS,
                CrmcustomerticketingaddonWebConstants.NO_INDEX_NO_FOLLOW);
        try {
            final TicketData ticketData = ticketFacade.getTicket(XSSEncoder.encodeHTML(ticketId));
            if (ticketData == null) {
                LOG.error("Attempted to load ticket details that does not exist or is not visible");
                GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
                        TEXT_ACCOUNT_SUPPORTTICKETS_TRY_LATER, null);
                return REDIRECT_TO_SUPPORT_TICKETS_PAGE;
            }
            sf.setSubject(XSSFilterUtil.filter(ticketData.getSubject()));
            model.addAttribute(SUPPORT_TICKET_FORM, sf);
            setUpdateAttributes(model, ticketData);
            model.addAttribute(SUPPORT_TICKET_DATA, ticketData);
            model.addAttribute("backlinkurl", request.getHeader("Referer"));
        } catch (final Exception e) {
            LOG.error("Attempted to load ticket details that does not exist or is not visible", e);
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
                    TEXT_ACCOUNT_SUPPORTTICKETS_TRY_LATER, null);
            return REDIRECT_TO_SUPPORT_TICKETS_PAGE;
        }
        return getViewForPage(model);
    }

    @RequestMapping(value = "/getRelatedCategories", method = RequestMethod.GET)
    @ResponseBody
    public List<CategoryData> getRelatedCategories(@RequestParam("categoryGuid") final String categoryGuid,
            @RequestParam("type") final String type) throws CMSItemNotFoundException {
        String schemaId;
        if (type.equals(CrmcustomerticketingaddonWebConstants.TICKET_TYPE_COMPLAINT)) {
            schemaId = SAPConfigurationModel.COMPLAINTCATAGORYCODEGROUP;
        } else {
            schemaId = SAPConfigurationModel.SERVICEREQUESTCATEGORYSCHEMAID;
        }
        return this.sapCategoriesFacade.getSubCategoriesForCode(categoryGuid, schemaId, null);
    }

    /**
     * Updates a ticket with new information from form.
     *
     * @param sapCrmSupportTicketForm
     * @param bindingResult
     * @param model
     * @param redirectModel
     * @return View String
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = "/crm-support-ticket/"
            + SUPPORT_TICKET_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.POST)
    @RequireHardLogIn
    public String updateSupportTicket(@Valid final SapCrmSupportTicketForm sapCrmSupportTicketForm,
             final BindingResult bindingResult, final HttpServletRequest request,final Model model,
            final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            model.addAttribute(SUPPORT_TICKET_FORM, sapCrmSupportTicketForm);
            model.addAttribute(SUPPORT_TICKET_PRIORITIES, ticketFacade.getTicketPriorities());
            final TicketData ticketData = ticketFacade.getTicket(sapCrmSupportTicketForm.getId());

            model.addAttribute(SUPPORT_TICKET_DATA, ticketData);
            model.addAttribute(SUPPORT_TICKET_CATEGORY, ticketData.getTicketCategory());
            model.addAttribute(CrmcustomerticketingaddonWebConstants.BREAD_CRUMBS,
                    getBreadcrumbs(TEXT_ACCOUNT_SUPPORTTICKETS_UPDATE_SUPPORT_TIECKT));
            setUpdateAttributes(model, ticketData);
            model.addAttribute("backlinkurl", request.getHeader("Referer"));
            storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_SUPPORT_TICKET_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_SUPPORT_TICKET_PAGE));
            return getViewForPage(model);
        }

        final TicketData updateTicket = ticketFacade.updateTicket(this.populateTicketData(sapCrmSupportTicketForm));
        // Assuming there might have been an error occurred, If ticket data
        // returned as null. Return to the update page.
        if (updateTicket == null) {

            GlobalMessages.addErrorMessage(model, TEXT_ACCOUNT_SUPPORTTICKETS_TRY_LATER);
            model.addAttribute(SUPPORT_TICKET_FORM, sapCrmSupportTicketForm);
            model.addAttribute(SUPPORT_TICKET_PRIORITIES, ticketFacade.getTicketPriorities());
            final TicketData ticketData = ticketFacade.getTicket(sapCrmSupportTicketForm.getId());
            setUpdateAttributes(model, ticketData);
            model.addAttribute(SUPPORT_TICKET_DATA, ticketData);
            model.addAttribute(SUPPORT_TICKET_CATEGORY, ticketData.getTicketCategory());
            model.addAttribute(CrmcustomerticketingaddonWebConstants.BREAD_CRUMBS,
                    getBreadcrumbs(TEXT_ACCOUNT_SUPPORTTICKETS_UPDATE_SUPPORT_TIECKT));
            storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_SUPPORT_TICKET_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_SUPPORT_TICKET_PAGE));
            return getViewForPage(model);
        }
        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
                "text.account.supportTicket.confirmation.ticket.added", null);
        setUpdateAttributes(model, updateTicket);

        return REDIRECT_TO_SUPPORT_TICKETS_PAGE;
    }

    /**
     * Lists all tickets
     *
     * @param pageNumber
     * @param showMode
     * @param sortCode
     * @param model
     * @return View String
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = "/crm-support-tickets", method = RequestMethod.GET)
    @RequireHardLogIn
    public String crmSupportTickets(@RequestParam(value = "page", defaultValue = "0") final int pageNumber,
            @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
            @RequestParam(value = "sort", required = false) final String sortCode, final Model model)
            throws CMSItemNotFoundException {
        storeCmsPageInModel(model, getContentPageForLabelOrId(SUPPORT_TICKETS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SUPPORT_TICKETS_PAGE));
        model.addAttribute(SUPPORT_TICKET_CATEGORIES, ticketFacade.getTicketCategories());
        model.addAttribute(CrmcustomerticketingaddonWebConstants.BREAD_CRUMBS,
                accountBreadcrumbBuilder.getBreadcrumbs("text.account.supporttickets.history"));
        model.addAttribute(CrmcustomerticketingaddonWebConstants.META_ROBOTS,
                CrmcustomerticketingaddonWebConstants.NO_INDEX_NO_FOLLOW);

        final PageableData pageableData = createPageableData(pageNumber, 5, sortCode, showMode);
        final SearchPageData<TicketData> searchPageData = ticketFacade.getTickets(pageableData);

        populateModel(model, searchPageData, showMode);

        return getViewForPage(model);
    }

    /**
     * @param model
     * @param ticketData
     */
    protected void setUpdateAttributes(final Model model, final TicketData ticketData) {

        model.addAttribute(SUPPORT_TICKET_ASSOCIATED_OBJECTS, ticketFacade.getAssociatedToObjects());
        model.addAttribute(SUPPORT_TICKET_CATEGORY, ticketData.getTicketCategory());
        model.addAttribute(CrmcustomerticketingaddonWebConstants.TICKET_TYPE,
                CrmcustomerticketingaddonWebConstants.TICKET_TYPE_SUPPORT);
        model.addAttribute(SUPPORT_TICKET_PRIORITIES, ticketFacade.getTicketPriorities());
        model.addAttribute(SUPPORT_TICKET_REASON_CATEGORIES_LEVEL1, sapCategoriesFacade.getDefaultCategories(1,
                SAPConfigurationModel.SERVICEREQUESTCATEGORYSCHEMAID, ticketData.getCreationDate()));
        if (null != ticketData.getReasonCategoryMap()) {
            for (final Map.Entry<String, String> entry : ticketData.getReasonCategoryMap().entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    String modelAttribute;
                    final List<CategoryData> subCategories = this.sapCategoriesFacade.getSubCategoriesForCode(
                            entry.getValue(), SAPConfigurationModel.SERVICEREQUESTCATEGORYSCHEMAID,
                            ticketData.getCreationDate());
                    final int level = Integer.valueOf(entry.getKey()) + 1;
                    modelAttribute = CrmcustomerticketingaddonWebConstants.REASON_CATEGORIES.concat(String
                            .valueOf(level));
                    model.addAttribute(modelAttribute, subCategories);
                }
            }
        }

    }

    /**
     * Set model attributes
     *
     * @param model
     * @param category
     */

    protected void setModelAttributes(final Model model, final Date creationDate, final TicketCategory category)
            throws UnknownIdentifierException {

        model.addAttribute(SUPPORT_TICKET_ASSOCIATED_OBJECTS, ticketFacade.getAssociatedToObjects());
        model.addAttribute(SUPPORT_TICKET_CATEGORY, category);
        model.addAttribute(CrmcustomerticketingaddonWebConstants.TICKET_TYPE,
                CrmcustomerticketingaddonWebConstants.TICKET_TYPE_SUPPORT);
        model.addAttribute(SUPPORT_TICKET_PRIORITIES, ticketFacade.getTicketPriorities());
        model.addAttribute(SUPPORT_TICKET_REASON_CATEGORIES_LEVEL1, sapCategoriesFacade.getDefaultCategories(1,
                SAPConfigurationModel.SERVICEREQUESTCATEGORYSCHEMAID, creationDate));
        model.addAttribute(SUPPORT_TICKET_REASON_CATEGORIES_LEVEL2, sapCategoriesFacade.getDefaultCategories(2,
                SAPConfigurationModel.SERVICEREQUESTCATEGORYSCHEMAID, creationDate));
        model.addAttribute(SUPPORT_TICKET_REASON_CATEGORIES_LEVEL3, sapCategoriesFacade.getDefaultCategories(3,
                SAPConfigurationModel.SERVICEREQUESTCATEGORYSCHEMAID, creationDate));
        model.addAttribute(SUPPORT_TICKET_REASON_CATEGORIES_LEVEL4, sapCategoriesFacade.getDefaultCategories(4,
                SAPConfigurationModel.SERVICEREQUESTCATEGORYSCHEMAID, creationDate));
    }

    /**
     * Populated the data from the form bean to ticket data object.
     *
     * @param sapCrmSupportTicketForm
     * @return TicketData
     */
    protected TicketData populateTicketData(final SapCrmSupportTicketForm sapCrmSupportTicketForm) {
        final TicketData ticketData = new TicketData();
        if (cartFacade.hasSessionCart()) {
            final CartData cartData = cartFacade.getSessionCart();
            if (!cartData.getEntries().isEmpty()) {
                ticketData.setCartId(cartData.getCode());
            }
        }
        if (StringUtils.isNotBlank(sapCrmSupportTicketForm.getId())) {
            ticketData.setId(sapCrmSupportTicketForm.getId());
        }
        final StatusData status = new StatusData();
        status.setId(sapCrmSupportTicketForm.getStatus());
        ticketData.setStatus(status);
        ticketData.setCustomerId(customerFacade.getCurrentCustomerUid());
        ticketData.setSubject(XSSFilterUtil.filter(sapCrmSupportTicketForm.getSubject()));
        ticketData.setMessage(XSSFilterUtil.filter(sapCrmSupportTicketForm.getMessage()));
        ticketData.setAssociatedTo(sapCrmSupportTicketForm.getAssociatedTo());
        ticketData.setTicketCategory(sapCrmSupportTicketForm.getTicketCategory());
        ticketData.setTicketPriority(sapCrmSupportTicketForm.getTicketPriority());
        if (sapCrmSupportTicketForm.getTicketRelatedObjectData() != null) {
            ticketData.setRelatedObjects(sapCrmSupportTicketForm.getTicketRelatedObjectData());
        }
        ticketData.setReasonCategoryMap(determineReasonCategoryForTicket(sapCrmSupportTicketForm));
        return ticketData;
    }

    /**
     * @param SapCrmSupportTicketForm
     * @return CategoryData
     */
    protected Map<String, String> determineReasonCategoryForTicket(
            final SapCrmSupportTicketForm sapCrmSupportTicketForm) {
        final Map<String, String> ticketParams = new HashMap<>();
        if (null != sapCrmSupportTicketForm.getReasonCategory4()
                && sapCrmSupportTicketForm.getReasonCategory4().length() > 0) {
            ticketParams.put("4", sapCrmSupportTicketForm.getReasonCategory4());
        }
        if (null != sapCrmSupportTicketForm.getReasonCategory3()
                && sapCrmSupportTicketForm.getReasonCategory3().length() > 0) {
            ticketParams.put("3", sapCrmSupportTicketForm.getReasonCategory3());
        }
        if (null != sapCrmSupportTicketForm.getReasonCategory2()
                && sapCrmSupportTicketForm.getReasonCategory2().length() > 0) {
            ticketParams.put("2", sapCrmSupportTicketForm.getReasonCategory2());
        }
        ticketParams.put("1", sapCrmSupportTicketForm.getReasonCategory1());
        return ticketParams;
    }

    private List<Breadcrumb> getBreadcrumbs(final String breadcrumbCode) {
        final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
        breadcrumbs.add(new Breadcrumb("/my-account/crm-support-tickets", getMessageSource()
                .getMessage("text.account.supporttickets.history", null, getI18nService().getCurrentLocale()), null));
        breadcrumbs.add(new Breadcrumb("#",
                getMessageSource().getMessage(breadcrumbCode, null, getI18nService().getCurrentLocale()), null));
        return breadcrumbs;
    }
}
