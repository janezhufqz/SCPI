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

package com.sap.hybris.productregistrationaddon.controllers.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.ConfigurationRuntimeException;
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

import com.sap.hybris.productregistrationaddon.constants.ProductregistrationaddonWebConstants;
import com.sap.hybris.productregistrationaddon.forms.RegisterProductForm;
import com.sap.hybris.productregistrationaddon.forms.validation.RegisterProductFormValidator;
import com.sap.hybris.productregistrationfacades.registration.ProductRegistrationFacade;
import com.sap.security.core.server.csi.XSSEncoder;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.AutocompleteResultData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.productregistrationfacades.data.ProductRegistrationData;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

@Controller
@RequestMapping("/my-account")
public class ProductRegistrationPageController extends AbstractSearchPageController {

    private static final Logger LOG = Logger.getLogger(ProductRegistrationPageController.class);
    // CMS Pages
    private static final String REGISTERED_PRODUCTS_PAGE = "registered-products";
    private static final String REGISTER_PRODUCT_PAGE = "add-registered-products";
    private static final String REGISTER_PRODUCT_DETAILS_PAGE = "register-product-details";

    private static final String REGISTRATION_CODE_PATH_VARIABLE_PATTERN = "{registrationGuid:.*}";

    private static final String REGISTERED_PRODUCTS = "registeredProducts";

    private static final String REDIRECT_TO_PRODUCT_REGISTRATION_PAGE = REDIRECT_PREFIX
            + "/my-account/registered-products";

    private static final String REGISTERPRODUCTDATA = "registrationProdData";

    @Resource(name = "accountBreadcrumbBuilder")
    private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

    @Resource(name = "productRegistrationFacade")
    private ProductRegistrationFacade productRegistrationFacade;

    @Resource(name = "registerProductFormValidator")
    private RegisterProductFormValidator registerProductFormValidator;

    @Resource(name = "productSearchFacade")
    private ProductSearchFacade<ProductData> productSearchFacade;

    @RequestMapping(value = "/registered-products", method = RequestMethod.GET)
    @RequireHardLogIn
    public String registeredProducts(@RequestParam(value = "page", defaultValue = "0")
    final int pageSize, @RequestParam(value = "show", defaultValue = "Page")
    final ShowMode showMode, @RequestParam(value = "sort", required = false)
    final String sortCode, final Model model) throws CMSItemNotFoundException {
        try {
            storeCmsPageInModel(model, getContentPageForLabelOrId(REGISTERED_PRODUCTS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(REGISTERED_PRODUCTS_PAGE));
            model.addAttribute(ProductregistrationaddonWebConstants.BREAD_CRUMBS,
                    accountBreadcrumbBuilder.getBreadcrumbs(ProductregistrationaddonWebConstants.REGISTRATION_HISTORY));
            model.addAttribute(ProductregistrationaddonWebConstants.META_ROBOTS,
                    ProductregistrationaddonWebConstants.REGISTRATION_INDEX);
            final PageableData pageableData = createPageableData(pageSize, 5, sortCode, showMode);
            final SearchPageData<ProductRegistrationData> searchPageData = productRegistrationFacade
                    .getRegisteredProducts(pageableData);
            populateModel(model, searchPageData, showMode);
            model.addAttribute(REGISTERED_PRODUCTS, searchPageData.getResults());
        } catch (final Exception e) {
            LOG.error("An error occured while fetching product registration results", e);
        }

        return getViewForPage(model);
    }

    @RequestMapping(value = "/register-a-product", method = RequestMethod.GET)
    @RequireHardLogIn
    public String registerProduct(final Model model) throws CMSItemNotFoundException {
        final RegisterProductForm productRegistrationForm = new RegisterProductForm();
        model.addAttribute(ProductregistrationaddonWebConstants.BREAD_CRUMBS,
                accountBreadcrumbBuilder.getBreadcrumbs(ProductregistrationaddonWebConstants.REGISTRATION_HISTORY));
        model.addAttribute(ProductregistrationaddonWebConstants.META_ROBOTS,
                ProductregistrationaddonWebConstants.REGISTRATION_INDEX);
        getCreateProductRegistrationPage(productRegistrationForm, model);

        return getViewForPage(model);
    }

    @RequestMapping(value = "/register-a-product/getSuggestions", method = RequestMethod.GET)
    @ResponseBody
    public List<ProductData> getAutocompleteSuggestions(@RequestParam("searchText")
    final String searchText) throws CMSItemNotFoundException {
        final List<ProductData> products = new ArrayList<ProductData>();
        final AutocompleteResultData resultData = new AutocompleteResultData();

        resultData.setSuggestions(subList(productSearchFacade.getAutocompleteSuggestions(searchText), 5));

        resultData.setProducts(subList(productSearchFacade.textSearch(searchText).getResults(), 3));
        for (final ProductData product : resultData.getProducts()) {
            products.add(product);
        }
        return products;
    }

    @RequestMapping(value = "/register-a-product", method = RequestMethod.POST)
    @RequireHardLogIn
    public String registerProduct(@Valid
    final RegisterProductForm productRegistrationForm, final BindingResult bindingResult, final Model model,
            final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        this.registerProductFormValidator.validate(productRegistrationForm, bindingResult);
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "productregistraion.product.not.found");

            model.addAttribute(ProductregistrationaddonWebConstants.BREAD_CRUMBS,
                    getBreadcrumbs("text.account.productregistration.addRegisterProduct"));

            getCreateProductRegistrationPage(productRegistrationForm, model);

            return getViewForPage(model);
        }
        try {
            productRegistrationFacade
                    .createProductRegistration(this.populateProductRegistrationData(productRegistrationForm));
        } catch (final ConfigurationRuntimeException | BackendException e) {
            LOG.error("An error occured while creating product registration request", e);
            GlobalMessages.addErrorMessage(model, "text.account.productregistration.backend.down");
            getCreateProductRegistrationPage(productRegistrationForm, model);
            return getViewForPage(model);
        } catch (final Exception e) {
            LOG.error("An error occured while creating product registration request", e);
            GlobalMessages.addErrorMessage(model, "productregistraion.product.not.found");
            getCreateProductRegistrationPage(productRegistrationForm, model);
            return getViewForPage(model);
        }

        GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
                "text.account.productregistration.confirmation.registration.added", null);

        return REDIRECT_TO_PRODUCT_REGISTRATION_PAGE;
    }

    @RequestMapping(value = "/registered-product/"
            + REGISTRATION_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    @RequireHardLogIn
    public String getRegisteredProduct(@PathVariable("registrationGuid")
    final String registrationNo, final Model model, final RedirectAttributes redirectModel)
            throws CMSItemNotFoundException {
        storeCmsPageInModel(model, getContentPageForLabelOrId(REGISTER_PRODUCT_DETAILS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(REGISTER_PRODUCT_DETAILS_PAGE));

        model.addAttribute(ProductregistrationaddonWebConstants.BREAD_CRUMBS,
                getBreadcrumbs("text.account.productregistration.detailsHistory"));
        model.addAttribute(ProductregistrationaddonWebConstants.META_ROBOTS,
                ProductregistrationaddonWebConstants.REGISTRATION_INDEX);
        model.addAttribute("productRegistrationForm", new RegisterProductForm());
        try {
            final ProductRegistrationData registrationData = productRegistrationFacade
                    .getRegisteredProductByGuid(XSSEncoder.encodeHTML(registrationNo));
            if (registrationData == null) {
                throw new ModelNotFoundException("Product Registration not found for the given ID " + registrationNo);
            }
            model.addAttribute(REGISTERPRODUCTDATA, registrationData);
        } catch (final Exception e) {
            LOG.error("Attempted to load product registration details that does not exist or is not visible", e);
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
                    "text.account.productregistration.tryLater", null);
            return REDIRECT_TO_PRODUCT_REGISTRATION_PAGE;
        }

        return getViewForPage(model);
    }

    /**
     * @param productRegistrationForm
     * @param model
     * @throws CMSItemNotFoundException
     */
    private void getCreateProductRegistrationPage(final RegisterProductForm productRegistrationForm, final Model model)
            throws CMSItemNotFoundException {
        storeCmsPageInModel(model, getContentPageForLabelOrId(REGISTER_PRODUCT_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(REGISTER_PRODUCT_PAGE));
        model.addAttribute("productRegistrationForm", productRegistrationForm);
    }

    /**
     * @param productRegistrationForm
     * @return ProductRegistrationData
     */
    private ProductRegistrationData populateProductRegistrationData(final RegisterProductForm productRegistrationForm) {
        final ProductRegistrationData registrationData = new ProductRegistrationData();
        registrationData.setProductCode(productRegistrationForm.getProductID());
        return registrationData;
    }

    private List<Breadcrumb> getBreadcrumbs(final String breadcrumbCode) {
        final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
        breadcrumbs.add(new Breadcrumb("/my-account/registered-products",
                getMessageSource().getMessage(ProductregistrationaddonWebConstants.REGISTRATION_HISTORY, null,
                        getI18nService().getCurrentLocale()),
                null));
        breadcrumbs.add(new Breadcrumb("#",
                getMessageSource().getMessage(breadcrumbCode, null, getI18nService().getCurrentLocale()), null));
        return breadcrumbs;
    }

    protected <E> List<E> subList(final List<E> list, final int maxElements) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        if (list.size() > maxElements) {
            return list.subList(0, maxElements);
        }

        return list;
    }

    /**
     * @return the registerProductFormValidator
     */
    public RegisterProductFormValidator getRegisterProductFormValidator() {
        return registerProductFormValidator;
    }

    /**
     * @param registerProductFormValidator
     *            the registerProductFormValidator to set
     */
    public void setRegisterProductFormValidator(final RegisterProductFormValidator registerProductFormValidator) {
        this.registerProductFormValidator = registerProductFormValidator;
    }

}
