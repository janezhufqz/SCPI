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
package com.sap.hybris.crm.sapcrmserviceorderaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController.ShowMode;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;

import com.sap.hybris.crm.sapserviceorderservices.ServiceOrderData;

import javax.servlet.http.HttpServletRequest;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sap.hybris.crm.sapserviceorderservices.facade.ServiceOrderFacade;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import com.sap.hybris.crm.sapserviceorderservices.ServiceOrderHistoryData;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for Service Order History And Details
 */

@SuppressWarnings("unused")
@Controller("ServiceOrderController")
@Scope("tenant")
@RequestMapping("/my-account")
public class ServiceOrderController extends AbstractSearchPageController {

    private static final Logger LOG = Logger.getLogger(ServiceOrderController.class.getName());

    private static final String SERVICE_ORDER_HISTORY_CMS_PAGE = "serviceorders";
    private static final String BREADCRUMBS_ATTR = "breadcrumbs";
    private static final String SERVICE_ORDER_DETAIL_CMS_PAGE = "serviceorder";
    private static final String REDIRECT_TO_SERVICE_ORDER_HISTORY_PAGE = REDIRECT_PREFIX + "/my-account/service-orders";
    private static final String REDIRECT_TO_HOME_PAGE_PAGE = REDIRECT_PREFIX + "/";
    private static final String DETAULT_PAGE_SIZE_FROM_PROPERTY = "sapcrmserviceorderaddon.servicesorder.history.pagesize";
    private static final int DETAULT_PAGE_SIZE = 5;

    @Resource(name = "serviceOrderFacade")
    private ServiceOrderFacade serviceOrderFacade;

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Resource(name = "serviceOrderBreadcrumbBuilder")
    private ResourceBreadcrumbBuilder serviceOrderBreadcrumbBuilder;

    @RequestMapping(value = "/service-orders", method = RequestMethod.GET)
    @RequireHardLogIn
    public String serviceorders(@RequestParam(value = "page", defaultValue = "0") final int page,
            @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
            @RequestParam(value = "sort", defaultValue = "byDate", required = false) final String sortCode,
            @RequestParam(value = "ascendingSortOrder", defaultValue = "false") final boolean ascendingSortOrder,
            final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException {

        final int pagesize = getConfigurationService().getConfiguration().getInt(DETAULT_PAGE_SIZE_FROM_PROPERTY,
                DETAULT_PAGE_SIZE);
        final PageableData pageableData = createPageableData(page, pagesize, sortCode, showMode);

        final SearchPageData<ServiceOrderHistoryData> searchPageData = serviceOrderFacade
                .getPagedOrderHistory(pageableData, sortCode, ascendingSortOrder, null);

        if (searchPageData == null) {
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
                    "text.serviceorder.page.histroy.error.message");
            LOG.error("Failed to fetch service order history");
            return REDIRECT_TO_HOME_PAGE_PAGE;
        }
        populateModel(model, searchPageData, showMode);

        storeCmsPageInModel(model, getContentPageForLabelOrId(SERVICE_ORDER_HISTORY_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SERVICE_ORDER_HISTORY_CMS_PAGE));
        model.addAttribute(BREADCRUMBS_ATTR,
                serviceOrderBreadcrumbBuilder.getBreadcrumbs("text.account.serviceOrderHistory"));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        return getViewForPage(model);

    }

    @RequestMapping(value = "/service-order", method = RequestMethod.GET)
    @RequireHardLogIn
    public String serviceorderdetails(@RequestParam("orderCode") final String orderCode, final Model model,
            final RedirectAttributes redirectModel, final HttpServletRequest request) throws CMSItemNotFoundException {
        try {
            ServiceOrderData orderDetails = serviceOrderFacade.getServiceOrderDetails(orderCode);
            orderDetails = serviceOrderFacade.populateRelatedObject(orderDetails);
            model.addAttribute("orderData", orderDetails);
            if (orderDetails != null) {
                model.addAttribute(BREADCRUMBS_ATTR, getBreadcrumbsForOrderDetails(orderDetails));
            }

        } catch (final UnknownIdentifierException e) {
            LOG.warn("Attempted to load a sevice order that does not exist or is not visible", e);
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
                    "system.error.page.not.found", null);
            return REDIRECT_TO_SERVICE_ORDER_HISTORY_PAGE;
        }
        storeCmsPageInModel(model, getContentPageForLabelOrId(SERVICE_ORDER_DETAIL_CMS_PAGE));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SERVICE_ORDER_DETAIL_CMS_PAGE));
        return getViewForPage(model);
    }

    /**
     * Prepare the Breadcrumbs for Service Order History
     *
     * @param order
     * 
     * @return List<Breadcrumb>
     */
    private List<Breadcrumb> getBreadcrumbsForOrderDetails(final ServiceOrderData order) {
        final List<Breadcrumb> breadcrumbs = serviceOrderBreadcrumbBuilder.getBreadcrumbs(null);
        breadcrumbs.add(new Breadcrumb("/my-account/service-orders", getMessageSource().getMessage(
                "text.account.serviceOrderHistory", null, getI18nService().getCurrentLocale()), null));
        breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage(
                "text.account.serviceorder.serviceorderBreadcrumb", new Object[] { order.getCode() }, "Order {0}",
                getI18nService().getCurrentLocale()), null));
        return breadcrumbs;

    }

    /**
     * @return the configurationService
     */
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    /**
     * @param configurationService
     *            the configurationService to set
     */
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
