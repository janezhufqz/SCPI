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
package com.sap.hybris.crm.sapcrmibasefacades.converters.populator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sap.hybris.crm.sapcrmibasecore.model.IBaseAddressModel;
import com.sap.hybris.crm.sapcrmibasecore.model.IBaseComponentModel;
import com.sap.hybris.crm.sapcrmibasecore.model.IBaseModel;
import com.sap.hybris.crm.sapcrmibasecore.model.IBasePartnerModel;
import com.sap.hybris.crm.sapcrmibasefacades.data.IBaseComponentData;
import com.sap.hybris.crm.sapcrmibasefacades.data.IBaseData;
import com.sap.hybris.crm.sapcrmibasefacades.data.IBaseDetailData;
import com.sap.hybris.crm.sapcrmibasefacades.data.IBasePartnerData;
import com.sap.hybris.crm.sapcrmibasefacades.data.ObjectComponentData;
import com.sap.hybris.crm.sapcrmibasefacades.data.ProductComponentData;
import com.sap.hybris.crm.sapcrmibasefacades.data.TextComponentData;

import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * @author C5242879
 *
 */
public class IBaseDetailDataPopulator implements Populator<IBaseModel, IBaseDetailData> {
    @Autowired
    private Converter<IBasePartnerModel, IBasePartnerData> iBasePartnerConverter;

    @Autowired
    private Converter<IBaseModel, IBaseDetailData> iBaseDetailDataConverter;

    @Autowired
    private UrlResolver<ProductModel> productModelUrlResolver;

    @Autowired
    Converter<IBaseModel, IBaseData> iBaseDataConverter;

    private static final String PRODUCT_OBJECT_CODE = "0001";
    private static final String TEXT_OBJECT_CODE = "0004";
    private static final String OBJECT_OBJECT_CODE = "0030";
    private static final String IBASE_OBJECT_CODE = "0016";

    /**
     * Method to populate Ibase detail data.
     */
    @Override
    public void populate(final IBaseModel source, final IBaseDetailData target) throws ConversionException {
        target.setNumber(source.getNumber());
        target.setExternalId(source.getExternalId());
        target.setGuid(source.getGuid());
        // set category
        if (null != source.getIBaseType()) {
            target.setCategory(null != source.getIBaseType().getDescription() ? source.getIBaseType().getDescription()
                    : source.getIBaseType().getCode());
        }
        // set ibase status
        if (null != source.getStatus()) {
            target.setStatus(null != source.getStatus().getDescription() ? source.getStatus().getDescription()
                    : source.getStatus().getCode());
        }
        final IBaseAddressModel address = (IBaseAddressModel) source.getAddress();
        if (null != address) {
            populateAddress(address, target);
        }
        // set permission group
        if (null != source.getPermissionGroup()) {
            target.setPermissionGroup(null != source.getPermissionGroup().getDescription()
                    ? source.getPermissionGroup().getDescription() : source.getPermissionGroup().getCode());
        }
        target.setDescription(source.getDescription());

        // populating partners
        final List<IBasePartnerData> partners = new ArrayList<IBasePartnerData>();
        for (final IBasePartnerModel partnerModel : source.getPartners()) {
            partners.add(iBasePartnerConverter.convert(partnerModel));
        }
        target.setPartners(partners);

        // setting object type
        populateComponents(source, target);

    }

    /**
     * Method to populating all the components
     *
     * @param source
     * @param target
     */
    private void populateComponents(final IBaseModel source, final IBaseDetailData target) {
        final Map<String, List<IBaseComponentModel>> componentsMap = createMapForComponents(source);
        if (componentsMap.containsKey(IBASE_OBJECT_CODE)) {
            populateIBaseComponents(target, componentsMap.get(IBASE_OBJECT_CODE));
        }
        if (componentsMap.containsKey(OBJECT_OBJECT_CODE)) {
            populateObjectComponents(target, componentsMap.get(OBJECT_OBJECT_CODE));
        }
        if (componentsMap.containsKey(TEXT_OBJECT_CODE)) {
            populateTextComponents(target, componentsMap.get(TEXT_OBJECT_CODE));
        }
        if (componentsMap.containsKey(PRODUCT_OBJECT_CODE)) {
            populateProductComponent(target, componentsMap.get(PRODUCT_OBJECT_CODE));
        }

    }

    /**
     * Method to populate product component.
     *
     * @param target
     * @param list
     */
    private void populateProductComponent(final IBaseDetailData target, final List<IBaseComponentModel> list) {
        if (null != list && !list.isEmpty()) {
            final List<ProductComponentData> componentList = new ArrayList<ProductComponentData>();
            ProductComponentData component;
            for (final IBaseComponentModel componentModel : list) {
                component = new ProductComponentData();
                component.setInstanceNumber(componentModel.getInstanceNumber());
                component.setGuid(componentModel.getObjectGuid());
                if (null != componentModel.getProduct()) {
                    component.setProductId(componentModel.getProduct().getCode());
                    component.setName(componentModel.getProduct().getName());
                    component.setUrl(productModelUrlResolver.resolve(componentModel.getProduct()));
                }
                component.setQuantity(componentModel.getQuantity());
                if (null != componentModel.getUnit()) {
                    component.setUnit(componentModel.getUnit().getName());
                }

                if (null != componentModel.getWarrantyStartDate()) {
                    component.setWarrantyStartDate(componentModel.getWarrantyStartDate());
                }
                if (null != componentModel.getWarrantyEndDate()) {
                    component.setWarrantyEndDate(componentModel.getWarrantyEndDate());
                }
                componentList.add(component);
            }
            target.setProductComponents(componentList);
        }
    }

    /**
     * Method to populate text component.
     *
     * @param target
     * @param list
     */
    private void populateTextComponents(final IBaseDetailData target, final List<IBaseComponentModel> list) {
        if (null != list && !list.isEmpty()) {
            final List<TextComponentData> componentList = new ArrayList<TextComponentData>();
            TextComponentData component;
            for (final IBaseComponentModel componentModel : list) {
                component = new TextComponentData();
                component.setInstanceNumber(componentModel.getInstanceNumber());
                component.setGuid(componentModel.getObjectGuid());
                component.setDescription(componentModel.getDescription());
                componentList.add(component);
            }
            target.setTextComponents(componentList);
        }

    }

    /**
     * Method to populate Object component.
     *
     * @param target
     * @param list
     */
    private void populateObjectComponents(final IBaseDetailData target, final List<IBaseComponentModel> list) {
        if (null != list && !list.isEmpty()) {
            final List<ObjectComponentData> componentList = new ArrayList<ObjectComponentData>();
            ObjectComponentData component;
            for (final IBaseComponentModel componentModel : list) {
                component = new ObjectComponentData();
                component.setInstanceNumber(componentModel.getInstanceNumber());
                component.setGuid(componentModel.getObjectGuid());
                populateObjectOfObjectComponent(component, componentModel);
                component.setQuantity(componentModel.getQuantity());
                if (null != componentModel.getUnit()) {
                    component.setUnit(componentModel.getUnit().getName());
                }
                if (null != componentModel.getWarrantyStartDate()) {
                    component.setWarrantyStartDate(componentModel.getWarrantyStartDate());
                }
                if (null != componentModel.getWarrantyEndDate()) {
                    component.setWarrantyEndDate(componentModel.getWarrantyEndDate());
                }
                componentList.add(component);
            }
            target.setObjectComponents(componentList);
        }
    }

    /**
     * Method to populate child ibase component
     *
     * @param target
     * @param list
     */
    private void populateIBaseComponents(final IBaseDetailData target, final List<IBaseComponentModel> list) {
        if (null != list && !list.isEmpty()) {
            final List<IBaseComponentData> componentList = new ArrayList<IBaseComponentData>();
            IBaseComponentData component;
            for (final IBaseComponentModel componentModel : list) {
                component = new IBaseComponentData();
                component.setInstanceNumber(componentModel.getInstanceNumber());
                component.setGuid(componentModel.getObjectGuid());
                if (null != componentModel.getComponentIBase()) {
                    component.setIbaseId(componentModel.getComponentIBase().getNumber());
                    component.setDescription(componentModel.getComponentIBase().getDescription());
                    component.setIbase(iBaseDetailDataConverter.convert(componentModel.getComponentIBase()));
                }
                component.setQuantity(componentModel.getQuantity());
                if (null != componentModel.getUnit()) {
                    component.setUnit(componentModel.getUnit().getName());
                }
                if (null != componentModel.getWarrantyStartDate()) {
                    component.setWarrantyStartDate(componentModel.getWarrantyStartDate());
                }
                if (null != componentModel.getWarrantyEndDate()) {
                    component.setWarrantyEndDate(componentModel.getWarrantyEndDate());
                }
                componentList.add(component);
            }
            target.setIbaseComponents(componentList);
        }
    }

    /**
     * Method to populate object details in object component
     *
     * @param component
     * @param componentModel
     */
    private void populateObjectOfObjectComponent(final ObjectComponentData component,
            final IBaseComponentModel componentModel) {
        if (null != componentModel.getObject()) {
            component.setObjectId(componentModel.getObject().getCode());
            if (null != componentModel.getObject().getRefProduct()) {
                component.setProductId(componentModel.getObject().getRefProduct().getCode());
                component.setProductName(componentModel.getObject().getRefProduct().getName());
                component.setProductUrl(productModelUrlResolver.resolve(componentModel.getObject().getRefProduct()));
            }
        }
    }

    /**
     * Method to create a map of component from iBase model.
     *
     * @param source
     * @return map of components
     */
    private Map<String, List<IBaseComponentModel>> createMapForComponents(final IBaseModel source) {
        final Map<String, List<IBaseComponentModel>> componentsMap = new HashMap<String, List<IBaseComponentModel>>();
        for (final IBaseComponentModel component : source.getComponents()) {
            if (null != component.getObjectType() && isDateValidToday(component)) {
                final String objectCode = component.getObjectType().getCode();
                List<IBaseComponentModel> list = componentsMap.get(objectCode);
                if (null == list) {
                    list = new ArrayList<IBaseComponentModel>();
                }
                list.add(component);
                componentsMap.put(objectCode, list);
            }
        }
        return componentsMap;
    }

    /**
     * Check whether date is valid today or not.
     *
     * @param component
     * @return true if date valid today
     */
    private boolean isDateValidToday(final IBaseComponentModel component) {
        final Date validToDate = component.getValidTo();
        return null != validToDate && validToDate.after(new Date());
    }

    /**
     * Method to populate address.
     *
     * @param source
     * @param target
     */
    private void populateAddress(final IBaseAddressModel source, final IBaseDetailData target) {
        // populate address details
        target.setName(source.getName());
        target.setAdditionalName(source.getName2());
        target.setCity(source.getCity());
        target.setStreetname(source.getStreetname());
        target.setStreetnumber(source.getStreetnumber());
        target.setPostalcode(source.getPostalcode());
        if (null != source.getCountry()) {
            target.setCountry(source.getCountry().getName());
        }

    }
}
