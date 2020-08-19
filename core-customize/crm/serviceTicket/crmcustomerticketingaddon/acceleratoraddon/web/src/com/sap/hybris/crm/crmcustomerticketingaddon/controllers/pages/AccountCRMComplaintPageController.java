 
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
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
import com.sap.hybris.crm.crmcustomerticketingaddon.forms.SapCrmComplaintForm;
import com.sap.hybris.crm.crmcustomerticketingaddon.forms.SapCrmSupportTicketForm;
import com.sap.hybris.crm.crmcustomerticketingfacades.facade.SapCrmTicketFacade;
import com.sap.hybris.crm.sapcrmcategoryschema.facade.SapCrmCategoriesFacade;
import com.sap.security.core.server.csi.XSSEncoder;
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
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketCategory;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingfacades.data.TicketRelatedObjectData;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import com.sap.hybris.crm.crmcustomerticketingfacades.data.ComplaintAssociatedOrderEntryData;

@Controller
@RequestMapping("/my-account")
public class AccountCRMComplaintPageController extends AbstractSearchPageController {

    private static final String TEXT_ACCOUNT_SUPPORTTICKETS_TRY_LATER = "text.account.supporttickets.tryLater";
    private static final String TEXT_ACCOUNT_UPDATE_COMPLAINT = "text.account.complaints.updateComplaint";
    private static final Logger LOG = Logger.getLogger(AccountCRMComplaintPageController.class);

    // CMS Pages
    private static final String REDIRECT_TO_SUPPORT_TICKETS_PAGE = REDIRECT_PREFIX + "/my-account/crm-support-tickets";
    private static final String COMPLAINT_DATA = "ticketData";
    private static final String SUPPORT_TICKET_CATEGORY = "category";
    private static final String SUPPORT_TICKET_PRIORITIES = "priorities";
    private static final String ADD_COMPLAINT_PAGE = "crm-add-complaint";
    private static final String UPDATE_COMPLAINT_PAGE = "crm-update-complaint";
    private static final String COMPLAINT_FORM = "sapCrmComplaintForm";
    private static final String COMPLAINT_CODE_PATH_VARIABLE_PATTERN = "{complaintId:.*}";
    private static final String COMPLAINT_ASSOCIATED_ORDERS = "associatedOrders";
    private static final String COMPLAINT_ASSOCIATED_ORDER_ENTRIES = "associatedOrderEntries";

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
     * Used for retrieving page to create a customer complaint.
     *
     * @param model
     * @return View String
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = "/crm-add-complaint", method = RequestMethod.GET)
    @RequireHardLogIn
    public String addComplaint(@RequestParam final Map<String, String> queryMap, final Model model)
            throws CMSItemNotFoundException {
        final SapCrmComplaintForm complaintForm = new SapCrmComplaintForm();

        if (!queryMap.isEmpty()) {

            final List<TicketRelatedObjectData> tickecketRelatedObjList = ticketFacade
                    .setQueryKeyParamToRelatedObject(queryMap);
            final TicketData complaintData = new TicketData();
            if (!tickecketRelatedObjList.isEmpty()) {
                complaintForm.setTicketRelatedObjectData(tickecketRelatedObjList);
                complaintData.setRelatedObjects(tickecketRelatedObjList);
                model.addAttribute(COMPLAINT_DATA, complaintData);
            }
        }
        storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_COMPLAINT_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_COMPLAINT_PAGE));

        model.addAttribute(CrmcustomerticketingaddonWebConstants.BREAD_CRUMBS,
                getBreadcrumbs("text.account.complaints.addComplaint"));
        model.addAttribute(CrmcustomerticketingaddonWebConstants.META_ROBOTS,
                CrmcustomerticketingaddonWebConstants.NO_INDEX_NO_FOLLOW);
        model.addAttribute(COMPLAINT_FORM, complaintForm);

        try {
            setModelAttributes(model, null);
        } catch (final UnsupportedOperationException ex) {
            LOG.info("UnsupportedOperationException" + ex);

        }
        return getViewForPage(model);

    }

    /**
     * Creates a Complaint.
     *
     * @param sapCrmComplaintForm
     * @param bindingResult
     * @param model
     * @param redirectModel
     * @return View String
     * @throws CMSItemNotFoundException
     */
    @SuppressWarnings("unused")
    @RequestMapping(value = "/crm-add-complaint", method = RequestMethod.POST)
    @RequireHardLogIn
    public String addComplaint(@Valid final SapCrmComplaintForm sapCrmComplaintForm, final BindingResult bindingResult,
            final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            model.addAttribute(CrmcustomerticketingaddonWebConstants.BREAD_CRUMBS,
                    getBreadcrumbs("text.account.complaints.addComplaint"));
            setComplaintFormAttributes(sapCrmComplaintForm, model);
            storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_COMPLAINT_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_COMPLAINT_PAGE));
            return getViewForPage(model);
        }
        TicketData complaintData = ticketFacade.createComplaint(this.populateComplaintData(sapCrmComplaintForm));
        if (complaintData == null) {
            GlobalMessages.addErrorMessage(model, TEXT_ACCOUNT_SUPPORTTICKETS_TRY_LATER);

            setComplaintFormAttributes(sapCrmComplaintForm, model);
            storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_COMPLAINT_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_COMPLAINT_PAGE));
            return getViewForPage(model);
        }

        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
                "text.account.complaints.confirmation.added", null);
        return REDIRECT_TO_SUPPORT_TICKETS_PAGE;
    }

    protected void setComplaintFormAttributes(SapCrmComplaintForm sapCrmComplaintForm, Model model) {
        TicketData complaintData = null;
        if (sapCrmComplaintForm.getTicketRelatedObjectData() != null
                && !sapCrmComplaintForm.getTicketRelatedObjectData().isEmpty()) {
            complaintData = new TicketData();
            complaintData.setRelatedObjects(sapCrmComplaintForm.getTicketRelatedObjectData());
            model.addAttribute(COMPLAINT_DATA, complaintData);
        }
        model.addAttribute(COMPLAINT_FORM, sapCrmComplaintForm);
        if (null != complaintData && null != complaintData.getAssociatedOrderCode()) {
            model.addAttribute(COMPLAINT_ASSOCIATED_ORDER_ENTRIES,
                    ticketFacade.getAssociatedOrderEntries(complaintData.getAssociatedOrderCode()));
        }
        setModelAttributes(model, null);
    }

    /**
     * Get Complaint Details.
     *
     * @param complaintId
     * @param model
     * @param redirectModel
     * @return View String
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = "/crm-complaint/" + COMPLAINT_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    @RequireHardLogIn
    public String getComplaint(@PathVariable("complaintId") final String complaintId, final Model model,
            final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_COMPLAINT_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_COMPLAINT_PAGE));
        final SapCrmComplaintForm complaintForm = new SapCrmComplaintForm();
        model.addAttribute(CrmcustomerticketingaddonWebConstants.BREAD_CRUMBS,
                getBreadcrumbs(TEXT_ACCOUNT_UPDATE_COMPLAINT));
        model.addAttribute(CrmcustomerticketingaddonWebConstants.META_ROBOTS,
                CrmcustomerticketingaddonWebConstants.NO_INDEX_NO_FOLLOW);
        try {
            final TicketData complaintData = ticketFacade.getTicket(XSSEncoder.encodeHTML(complaintId));
            if (complaintData == null) {
                LOG.error("Attempted to load ticket details that does not exist or is not visible");
                GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
                        TEXT_ACCOUNT_SUPPORTTICKETS_TRY_LATER, null);
                return REDIRECT_TO_SUPPORT_TICKETS_PAGE;
            }
            complaintForm.setSubject(XSSFilterUtil.filter(complaintData.getSubject()));
            model.addAttribute(COMPLAINT_FORM, complaintForm);
            setUpdateAttributes(model, complaintData);
            model.addAttribute(COMPLAINT_DATA, complaintData);
        } catch (final Exception e) {
            LOG.error("Attempted to load ticket details that does not exist or is not visible", e);
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
                    TEXT_ACCOUNT_SUPPORTTICKETS_TRY_LATER, null);
            return REDIRECT_TO_SUPPORT_TICKETS_PAGE;
        }
        return getViewForPage(model);
    }

    /**
     * 
     * @param orderCode
     * @return
     */
    @RequestMapping(value = "/getAssociatedOrderEntries", method = RequestMethod.GET)
    @ResponseBody
    public List<ComplaintAssociatedOrderEntryData> getOrderEntries(@RequestParam("orderCode") final String orderCode) {
        return ticketFacade.getAssociatedOrderEntries(orderCode);
    }

    /**
     * Updates a complaint with new information from form.
     *
     * @param SapCrmComplaintForm
     * @param bindingResult
     * @param model
     * @param redirectModel
     * @return View String
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = "/crm-complaint/" + COMPLAINT_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.POST)
    @RequireHardLogIn
    public String updateComplaint(@Valid final SapCrmComplaintForm sapCrmComplaintForm,
            final BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel)
            throws CMSItemNotFoundException {
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            model.addAttribute(COMPLAINT_FORM, sapCrmComplaintForm);
            model.addAttribute(SUPPORT_TICKET_PRIORITIES, ticketFacade.getTicketPriorities());
            final TicketData ticketData = ticketFacade.getTicket(sapCrmComplaintForm.getId());
            model.addAttribute(COMPLAINT_DATA, ticketData);
            model.addAttribute(SUPPORT_TICKET_CATEGORY, TicketCategory.COMPLAINT);
            model.addAttribute(CrmcustomerticketingaddonWebConstants.BREAD_CRUMBS,
                    getBreadcrumbs(TEXT_ACCOUNT_UPDATE_COMPLAINT));
            setUpdateAttributes(model, ticketData);
            storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_COMPLAINT_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_COMPLAINT_PAGE));
            return getViewForPage(model);
        }
        final TicketData updateTicket = ticketFacade.updateComplaint(this.populateComplaintData(sapCrmComplaintForm));
        // Assuming there might have been an error occurred, If ticket data
        // returned as null. Return to the update page.
        if (updateTicket == null) {
            GlobalMessages.addErrorMessage(model, TEXT_ACCOUNT_SUPPORTTICKETS_TRY_LATER);
            model.addAttribute(COMPLAINT_FORM, sapCrmComplaintForm);
            model.addAttribute(SUPPORT_TICKET_PRIORITIES, ticketFacade.getTicketPriorities());
            final TicketData ticketData = ticketFacade.getTicket(sapCrmComplaintForm.getId());
            setUpdateAttributes(model, ticketData);
            model.addAttribute(COMPLAINT_DATA, ticketData);
            model.addAttribute(SUPPORT_TICKET_CATEGORY, TicketCategory.COMPLAINT);
            model.addAttribute(CrmcustomerticketingaddonWebConstants.BREAD_CRUMBS,
                    getBreadcrumbs(TEXT_ACCOUNT_UPDATE_COMPLAINT));
            storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_COMPLAINT_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_COMPLAINT_PAGE));
            return getViewForPage(model);
        }
        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
                "text.account.complaints.confirmation.added", null);
        setUpdateAttributes(model, updateTicket);

        return REDIRECT_TO_SUPPORT_TICKETS_PAGE;
    }

    /**
     * Populated the data from the form bean to ticket data object.
     *
     * @param sapCrmComplaintForm
     * @return TicketData
     */
    private TicketData populateComplaintData(final SapCrmComplaintForm sapCrmComplaintForm) {
        final TicketData complaintData = new TicketData();
        if (cartFacade.hasSessionCart()) {
            final CartData cartData = cartFacade.getSessionCart();
            if (!cartData.getEntries().isEmpty()) {
                complaintData.setCartId(cartData.getCode());
            }
        }
        if (StringUtils.isNotBlank(sapCrmComplaintForm.getId())) {
            complaintData.setId(sapCrmComplaintForm.getId());
        }
        final StatusData status = new StatusData();
        status.setId(sapCrmComplaintForm.getStatus());
        complaintData.setStatus(status);
        complaintData.setCustomerId(customerFacade.getCurrentCustomerUid());
        complaintData.setSubject(XSSFilterUtil.filter(sapCrmComplaintForm.getSubject()));
        complaintData.setMessage(XSSFilterUtil.filter(sapCrmComplaintForm.getMessage()));
        complaintData.setTicketCategory(sapCrmComplaintForm.getTicketCategory());
        complaintData.setTicketPriority(sapCrmComplaintForm.getTicketPriority());
        if (sapCrmComplaintForm.getTicketRelatedObjectData() != null) {
            complaintData.setRelatedObjects(sapCrmComplaintForm.getTicketRelatedObjectData());
        }
        complaintData.setReasonCategoryMap(determineReasonCategoryForTicket(sapCrmComplaintForm));
        complaintData.setAssociatedOrderCode(sapCrmComplaintForm.getAssociatedOrderCode());
        List<String> entrycodeList = new ArrayList<String>();
        if (null != sapCrmComplaintForm.getAssociatedOrderEntryCodes()) {
            for (String i : sapCrmComplaintForm.getAssociatedOrderEntryCodes()) {
                entrycodeList.add(i);
            }
        }
        complaintData.setAssociatedOrderEntryCodes(entrycodeList);
        return complaintData;
    }

    private List<Breadcrumb> getBreadcrumbs(final String breadcrumbCode) {
        final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
        breadcrumbs.add(new Breadcrumb("/my-account/crm-support-tickets", getMessageSource()
                .getMessage("text.account.supporttickets.history", null, getI18nService().getCurrentLocale()), null));
        breadcrumbs.add(new Breadcrumb("#",
                getMessageSource().getMessage(breadcrumbCode, null, getI18nService().getCurrentLocale()), null));
        return breadcrumbs;
    }

    /**
     * @param SapCrmSupportTicketForm
     * @return CategoryData
     */
    protected Map<String, String> determineReasonCategoryForTicket(final SapCrmComplaintForm sapCrmComplaintForm) {
        final Map<String, String> ticketParams = new HashMap<>();
        if (null != sapCrmComplaintForm.getReasonCategory4() && sapCrmComplaintForm.getReasonCategory4().length() > 0) {
            ticketParams.put("4", sapCrmComplaintForm.getReasonCategory4());
        }
        if (null != sapCrmComplaintForm.getReasonCategory3() && sapCrmComplaintForm.getReasonCategory3().length() > 0) {
            ticketParams.put("3", sapCrmComplaintForm.getReasonCategory3());
        }
        if (null != sapCrmComplaintForm.getReasonCategory2() && sapCrmComplaintForm.getReasonCategory2().length() > 0) {
            ticketParams.put("2", sapCrmComplaintForm.getReasonCategory2());
        }
        ticketParams.put("1", sapCrmComplaintForm.getReasonCategory1());
        return ticketParams;
    }

    /**
     * Set model attributes
     *
     * @param model
     */
    protected void setModelAttributes(final Model model, final Date creationDate) {

        try {
            model.addAttribute(COMPLAINT_ASSOCIATED_ORDERS, ticketFacade.getAssociatedOrders());
            model.addAttribute(CrmcustomerticketingaddonWebConstants.TICKET_TYPE,
                    CrmcustomerticketingaddonWebConstants.TICKET_TYPE_COMPLAINT);
            model.addAttribute(COMPLAINT_ASSOCIATED_ORDER_ENTRIES, new ArrayList<ComplaintAssociatedOrderEntryData>());
            model.addAttribute(SUPPORT_TICKET_CATEGORY, TicketCategory.COMPLAINT);
            model.addAttribute(SUPPORT_TICKET_PRIORITIES, ticketFacade.getTicketPriorities());
            model.addAttribute(SUPPORT_TICKET_REASON_CATEGORIES_LEVEL1, sapCategoriesFacade.getDefaultCategories(1,
                    SAPConfigurationModel.COMPLAINTCATAGORYCODEGROUP, creationDate));
            model.addAttribute(SUPPORT_TICKET_REASON_CATEGORIES_LEVEL2, sapCategoriesFacade.getDefaultCategories(2,
                    SAPConfigurationModel.COMPLAINTCATAGORYCODEGROUP, creationDate));
            model.addAttribute(SUPPORT_TICKET_REASON_CATEGORIES_LEVEL3, sapCategoriesFacade.getDefaultCategories(3,
                    SAPConfigurationModel.COMPLAINTCATAGORYCODEGROUP, creationDate));
            model.addAttribute(SUPPORT_TICKET_REASON_CATEGORIES_LEVEL4, sapCategoriesFacade.getDefaultCategories(4,
                    SAPConfigurationModel.COMPLAINTCATAGORYCODEGROUP, creationDate));
        } catch (final UnsupportedOperationException | UnknownIdentifierException | IllegalArgumentException ex) {
            LOG.info("Error occured while laoding complaint list" + ex);
        }
    }

    /**
     * @param model
     * @param ticketData
     * @param ticketCategory
     */
    protected void setUpdateAttributes(final Model model, final TicketData ticketData) {

        try {
            model.addAttribute(SUPPORT_TICKET_CATEGORY, TicketCategory.COMPLAINT);
            model.addAttribute(CrmcustomerticketingaddonWebConstants.TICKET_TYPE,
                    CrmcustomerticketingaddonWebConstants.TICKET_TYPE_COMPLAINT);
            model.addAttribute(SUPPORT_TICKET_PRIORITIES, ticketFacade.getTicketPriorities());
            model.addAttribute(SUPPORT_TICKET_REASON_CATEGORIES_LEVEL1, sapCategoriesFacade.getDefaultCategories(1,
                    SAPConfigurationModel.COMPLAINTCATAGORYCODEGROUP, ticketData.getCreationDate()));
            if (null != ticketData.getReasonCategoryMap()) {
                for (final Map.Entry<String, String> entry : ticketData.getReasonCategoryMap().entrySet()) {
                    String modelAttribute;
                    final List<CategoryData> subCategories = this.sapCategoriesFacade.getSubCategoriesForCode(
                            entry.getValue(), SAPConfigurationModel.COMPLAINTCATAGORYCODEGROUP,
                            ticketData.getCreationDate());
                    final int level = Integer.valueOf(entry.getKey()) + 1;
                    modelAttribute = CrmcustomerticketingaddonWebConstants.REASON_CATEGORIES
                            .concat(String.valueOf(level));
                    model.addAttribute(modelAttribute, subCategories);
                }
            }
        } catch (final UnsupportedOperationException | UnknownIdentifierException | IllegalArgumentException ex) {
            LOG.info("Error occured while laoding complaint list" + ex);
        }
    }

}
