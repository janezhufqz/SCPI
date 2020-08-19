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
package com.sap.hybris.productregistrationaddon.forms.validation;

import java.util.Arrays;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.sap.hybris.productregistrationaddon.forms.RegisterProductForm;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;

@Component("registerProductFormValidator")
public class RegisterProductFormValidator implements Validator {

    private static final Logger LOG = Logger.getLogger(RegisterProductFormValidator.class);

    @Resource
    private ProductFacade productFacade;

    @Override
    public boolean supports(final Class<?> aClass) {
        return RegisterProductForm.class.equals(aClass);
    }

    @Override
    public void validate(final Object object, final Errors errors) {

        final RegisterProductForm registerProductForm = (RegisterProductForm) object;
        final String productCode = registerProductForm.getProductID();
        try {
            productFacade.getProductForCodeAndOptions(productCode,
                    Arrays.asList(ProductOption.BASIC, ProductOption.CATEGORIES));
        } catch (final Exception e) {
            LOG.debug("Product with code " + productCode + "does not exist", e);
            errors.rejectValue("productID", "productregistraion.product.not.found");
        }

    }
}
