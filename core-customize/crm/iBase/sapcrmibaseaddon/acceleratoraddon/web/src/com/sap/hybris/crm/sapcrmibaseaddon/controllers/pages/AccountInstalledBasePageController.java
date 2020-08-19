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
package com.sap.hybris.crm.sapcrmibaseaddon.controllers.pages;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sap.hybris.crm.crmcustomerticketingfacades.facade.SapCrmTicketFacade;
import com.sap.hybris.crm.sapcrmibasefacades.data.IBaseData;
import com.sap.hybris.crm.sapcrmibasefacades.data.IBaseDetailData;
import com.sap.hybris.crm.sapcrmibasefacades.ibase.IBaseFacade;
import com.sap.hybris.crm.sapservicecontract.data.ServiceContractData;
import com.sap.hybris.crm.sapservicecontractfacades.facade.ServiceContractFacade;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.Registry;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

/**
 * Controller for installed base management.
 *
 * @author C5242879
 *
 */
@Controller
@RequestMapping("/my-company/installed-bases")
@RequireHardLogIn
public class AccountInstalledBasePageController extends AbstractSearchPageController {

    private static final String BREADCRUMBS_ATTR = "breadcrumbs";
    private static final String TEXT_ACCOUNT_INSTALLED_BASES = "text.installedBases.title";
    private static final String TEXT_ACCOUNT_INSTALLED_BASE = "text.installedBase.titleBreadcrumb";
    // CMS Pages
    private static final String INSTALLED_BASES_CMS_PAGE = "installed-bases";
    private static final String INSTALLED_BASE_CMS_PAGE = "installed-base";

    private static final String IBASE_ID_PATH_VARIABLE_PATTERN = "{iBaseID:.*}";

    private static final String SERVICE_TICKET_EXTENSION = "serviceticket.extension.name";
    private static final String SERVICE_TICKET_URL = "serviceticket.extension.url";
    private static final String SERVICE_TICKET_IBASEID = "serviceticket.param.ibaseid";
    private static final String SERVICE_TICKET_COMPONENTID = "serviceticket.param.componentid";
    private static final String SERVICE_TICKET_IBASEGUID = "serviceticket.param.ibaseguid";
    private static final String SERVICE_TICKET_COMPONENTGUID = "serviceticket.param.componentguid";
    private static final String SERVICE_CONTRACT_EXTENSION = "servicecontract.extension.name";
    private static final String SERVICE_CONTRACT_VIEW_URL = "servicecontract.extension.view.url";
    private static final String SERVICE_TICKET_VIEW_URL = "serviceticket.extension.view.url";

    @Resource(name = "accountBreadcrumbBuilder")
    private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Resource(name = "serviceContractFacade")
    private ServiceContractFacade serviceContractFacade;

    @Resource(name = "defaultTicketFacade")
    private SapCrmTicketFacade ticketFacade;

    @Autowired
    private IBaseFacade iBaseFacade;

    @ExceptionHandler(ModelNotFoundException.class)
    public String handleModelNotFoundException(final ModelNotFoundException exception,
            final HttpServletRequest request) {
        request.setAttribute("message", exception.getMessage());
        return FORWARD_PREFIX + "/404";
    }

    /**
     * Method to render all the ibases for logged in customer.
     *
     * @param model
     * @return ibase listing page
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET)
    public String installedBases(final Model model) throws CMSItemNotFoundException {
        final List<IBaseData> ibases = iBaseFacade.getInstalledBases();
        model.addAttribute("ibases", ibases);

        storeCmsPageInModel(model, getContentPageForLabelOrId(INSTALLED_BASES_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(INSTALLED_BASES_CMS_PAGE));
        model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs(TEXT_ACCOUNT_INSTALLED_BASES));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        return getViewForPage(model);
    }

    /**
     * Method to Render an ibase's detail.
     *
     * @param iBaseID
     * @param model
     * @return ibase detail page
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = "/view/" + IBASE_ID_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    public String installedBase(@PathVariable("iBaseID") final String iBaseID, final Model model)
            throws CMSItemNotFoundException {
        final IBaseDetailData iBaseData = iBaseFacade.getInstalledBaseById(iBaseID);
        String result;
        if (null != iBaseData) {
            result = fetchInstalledBase(model, iBaseData, iBaseID);
        } else {
            result = REDIRECT_PREFIX + "/";
        }

        return result;

    }

    @RequestMapping(value = "/installed-base-guid", method = RequestMethod.GET)
    public String installedBaseWithGuid(@RequestParam final Map<String, String> pathVars, final Model model)
            throws CMSItemNotFoundException {

        IBaseDetailData iBaseData = null;
        String result = null;
        if (pathVars.containsKey("parentGuid")) {
            final String parentGuid = pathVars.get("parentGuid");
            iBaseData = iBaseFacade.getInstalledBaseByParentGuid(parentGuid);
        } else if (pathVars.containsKey("childGuid")) {
            final String childGuid = pathVars.get("childGuid");
            iBaseData = iBaseFacade.getInstalledBaseByChildGuid(childGuid);
        }
        if (null != iBaseData) {
            final String iBaseID = iBaseData.getNumber();
            result = fetchInstalledBase(model, iBaseData, iBaseID);
        } else {
            result = REDIRECT_PREFIX + "/";
        }

        return result;
    }

    /**
     * @param model
     * @param iBaseData
     * @param iBaseID
     * @return
     * @throws CMSItemNotFoundException
     */
    protected String fetchInstalledBase(final Model model, final IBaseDetailData iBaseData, final String iBaseID)
            throws CMSItemNotFoundException {
        final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
        breadcrumbs.add(new Breadcrumb("/my-company/installed-bases",
                getMessageSource().getMessage(TEXT_ACCOUNT_INSTALLED_BASES, null, getI18nService().getCurrentLocale()),
                null));
        breadcrumbs.add(new Breadcrumb(String.format("/my-company/installed-bases/view/%s/", urlEncode(iBaseID)),
                getMessageSource().getMessage(TEXT_ACCOUNT_INSTALLED_BASE, new Object[] { iBaseID },
                        "Installed Base {0}", getI18nService().getCurrentLocale()),
                null));

        populateServiceContracts(model, iBaseID);
        populateServiceTickets(model, iBaseID);

        model.addAttribute("breadcrumbs", breadcrumbs);

        model.addAttribute("iBase", iBaseData);
        storeCmsPageInModel(model, getContentPageForLabelOrId(INSTALLED_BASE_CMS_PAGE));
        model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(INSTALLED_BASE_CMS_PAGE));
        return getViewForPage(model);
    }

    /**
     * Method to populate attributes for showing service ticket.
     * 
     * @param model
     * @param iBaseID
     */
    protected void populateServiceTickets(Model model, String iBaseID) {
        final List<TicketData> serviceTickets = ticketFacade.getTicketsForIbase(iBaseID);
        if (isExtensionLoaded(getServiceTicketExtensionName())) {
            model.addAttribute("isserviceticketloaded", Boolean.TRUE);
            model.addAttribute("serviceticketviewurl", getServiceTicketViewURL());

            // attribute for create ticket functionality
            model.addAttribute("showserviceticketbutton", Boolean.TRUE);
            model.addAttribute("serviceticketurl", getServiceTicketExtensionURL());
            model.addAttribute("serviceticketibaseid", getServiceTicketIBaseIDAttr());
            model.addAttribute("serviceticketcomponentid", getServiceTicketComponentIDAttr());
            model.addAttribute("serviceticketibaseguid", getServiceTicketIBaseGUIDAttr());
            model.addAttribute("serviceticketcomponentguid", getServiceTicketComponentGUIDAttr());

        } else {
            model.addAttribute("serviceticketextensionname", getServiceTicketExtensionName());
        }
        model.addAttribute(serviceTickets);
    }

    /**
     * Method to populate attributes for showing service contracts.
     * 
     * @param model
     * @param iBaseID
     */
    protected void populateServiceContracts(Model model, String iBaseID) {
        final List<ServiceContractData> serviceContracts = serviceContractFacade
                .getServiceContractDataForIBase(iBaseID);
        if (isExtensionLoaded(getServiceContractExtensionName())) {
            model.addAttribute("isservicecontractloaded", Boolean.TRUE);
            model.addAttribute("servicecontractviewurl", getServiceContractViewURL());
        } else {
            model.addAttribute("servicecontractextensionname", getServiceContractExtensionName());
        }
        model.addAttribute("serviceContracts", serviceContracts);
    }

    protected boolean isExtensionLoaded(final String extensionNameToCheck) {
        final List<String> loadedExtensionNames = getLoadedExtensionNames();
        return loadedExtensionNames.contains(extensionNameToCheck);
    }

    protected List<String> getLoadedExtensionNames() {
        return Registry.getCurrentTenant().getTenantSpecificExtensionNames();
    }

    protected String getServiceTicketExtensionName() {
        return configurationService.getConfiguration().getString(SERVICE_TICKET_EXTENSION);
    }

    protected String getServiceTicketExtensionURL() {
        return configurationService.getConfiguration().getString(SERVICE_TICKET_URL);
    }

    protected String getServiceTicketIBaseIDAttr() {
        return configurationService.getConfiguration().getString(SERVICE_TICKET_IBASEID);
    }

    protected String getServiceTicketComponentIDAttr() {
        return configurationService.getConfiguration().getString(SERVICE_TICKET_COMPONENTID);
    }

    protected String getServiceTicketIBaseGUIDAttr() {
        return configurationService.getConfiguration().getString(SERVICE_TICKET_IBASEGUID);
    }

    protected String getServiceTicketComponentGUIDAttr() {
        return configurationService.getConfiguration().getString(SERVICE_TICKET_COMPONENTGUID);
    }

    protected String getServiceContractExtensionName() {
        return configurationService.getConfiguration().getString(SERVICE_CONTRACT_EXTENSION);
    }

    protected String getServiceContractViewURL() {
        return configurationService.getConfiguration().getString(SERVICE_CONTRACT_VIEW_URL);
    }

    protected String getServiceTicketViewURL() {
        return configurationService.getConfiguration().getString(SERVICE_TICKET_VIEW_URL);
    }
}
