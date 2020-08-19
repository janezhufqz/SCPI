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
package com.sap.hybris.crm.sapcrmibasecore.model.dynamichandler;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.crm.sapcrmibasecore.model.IBaseComponentModel;
import com.sap.hybris.crm.sapcrmibasecore.model.IBaseModel;
import com.sap.hybris.crm.sapcrmibasecore.model.ObjectTypeModel;

import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.attribute.DynamicLocalizedAttributeHandler;

/**
 * Dynamic attribute handler for IBaseComponent return : display text will be
 * returned based on the objecttype of the component.
 *
 * @author C5230708
 *
 */
public class ComponentTypeDynamicDescription implements DynamicLocalizedAttributeHandler<String, IBaseComponentModel> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentTypeDynamicDescription.class);

    private I18NService i18NService;
    private String productComponentCode;
    private String objectComponentCode;
    private String ibaseComponentCode;
    private String textComponentCode;

    @Override
    public String get(final IBaseComponentModel component, final Locale locale) {
        String displayType = "( No Text Available";
        String displayText = "Display Text :";
        final ObjectTypeModel typeModel = component.getObjectType();
        LOGGER.debug("Type Model :" + typeModel);
        if (null != typeModel) {
            final String code = typeModel.getCode();
            displayType = component.getInstanceNumber() + " (";
            LOGGER.debug("Type Model Code :" + code);
            if (code.equalsIgnoreCase(this.textComponentCode)) {
                displayType += component.getDescription(locale);
                LOGGER.debug(displayText + displayType);
            } else if (code.equalsIgnoreCase(this.productComponentCode)) {
                displayType += component.getProduct() == null ? "" : component.getProduct().getName(locale);
                LOGGER.debug(displayText + displayType);
            } else if (code.equalsIgnoreCase(this.objectComponentCode)) {
                displayType += component.getProduct() == null ? "" : component.getProduct().getName(locale);
                LOGGER.debug(displayText + displayType);
            } else if (code.equalsIgnoreCase(this.ibaseComponentCode)) {
                final IBaseModel ibase = component.getComponentIBase();
                displayType += ibase == null ? "" : ibase.getDescription(locale);
                LOGGER.debug(displayText + displayType);
            }
        }

        return displayType + " )";
    }

    @Override
    public void set(final IBaseComponentModel component, final String value, final Locale arg1) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param productComponentCode
     *            the productComponentCode to set
     */
    public void setProductComponentCode(final String productComponentCode) {
        this.productComponentCode = productComponentCode;
    }

    /**
     * @param objectComponentCode
     *            the objectComponentCode to set
     */
    public void setObjectComponentCode(final String objectComponentCode) {
        this.objectComponentCode = objectComponentCode;
    }

    /**
     * @param ibaseComponentCode
     *            the ibaseComponentCode to set
     */
    public void setIbaseComponentCode(final String ibaseComponentCode) {
        this.ibaseComponentCode = ibaseComponentCode;
    }

    /**
     * @param textComponentCode
     *            the textComponentCode to set
     */
    public void setTextComponentCode(final String textComponentCode) {
        this.textComponentCode = textComponentCode;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler#
     * get(de.hybris.platform.servicelayer.model .AbstractItemModel)
     */
    @Override
    public String get(final IBaseComponentModel arg0) {
        return get(arg0, i18NService.getCurrentLocale());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler#
     * set(de.hybris.platform.servicelayer.model .AbstractItemModel,
     * java.lang.Object)
     */
    @Override
    public void set(final IBaseComponentModel arg0, final String arg1) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the i18NService
     */
    public I18NService getI18NService() {
        return i18NService;
    }

    /**
     * @param i18nService
     *            the i18NService to set
     */
    public void setI18NService(final I18NService i18nService) {
        i18NService = i18nService;
    }

}