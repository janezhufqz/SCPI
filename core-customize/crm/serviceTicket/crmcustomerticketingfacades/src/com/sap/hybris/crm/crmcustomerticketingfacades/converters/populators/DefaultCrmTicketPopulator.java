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

/**
 *
 */
package com.sap.hybris.crm.crmcustomerticketingfacades.converters.populators;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.customerticketingfacades.converters.populators.DefaultTicketPopulator;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingfacades.data.TicketPriority;
import de.hybris.platform.customerticketingfacades.data.TicketRelatedObjectData;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.model.CsTicketModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.sap.hybris.crm.crmcustomerticketingfacades.constants.CrmcustomerticketingfacadesConstants;
import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorizationCategoryModel;
import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorySchemaModel;
import com.sap.hybris.crm.sapcrmcategoryschema.service.SapCrmCategoriesService;
import com.sap.hybris.crm.sapcrmmodel.util.XSSFilterUtil;
import com.sap.hybris.crm.sapcrmticketsystem.model.CsTicketRelatedObjectModel;

/**
 * @author C5230711
 *
 */
public class DefaultCrmTicketPopulator extends DefaultTicketPopulator {

    private static final Logger LOG = Logger.getLogger(DefaultCrmTicketPopulator.class);

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;
    private SapCrmCategoriesService sapCategoriesService;
    protected static final String ORDER_TOTAL = "Total Price";

    @Override
    public void populate(final CsTicketModel source, final TicketData target) throws ConversionException {
        super.populate(source, target);
        target.setSubject(XSSFilterUtil.filter(source.getHeadline()));
        final CategorizationCategoryModel reasonCategoryModel = source.getReasonCategory();
        final List<TicketRelatedObjectData> relObjDataList = getRelatedObjectList(source.getCsTicketRelatedObject());
        if (!relObjDataList.isEmpty()) {
            target.setRelatedObjects(relObjDataList);
        }
        try {
            if (null != reasonCategoryModel) {
                target.setSelectedReasonCat(reasonCategoryModel.getName());

                if (source.getCategory().equals(CsTicketCategory.COMPLAINT)) {
                    target.setReasonCategoryMap(populateCategoryMap(source, reasonCategoryModel,
                            SAPConfigurationModel.COMPLAINTCATAGORYCODEGROUP));
                } else {
                    target.setReasonCategoryMap(populateCategoryMap(source, reasonCategoryModel,
                            SAPConfigurationModel.SERVICEREQUESTCATEGORYSCHEMAID));
                }
            }

        } catch (final IllegalArgumentException | NullPointerException e) {
            LOG.debug("Could not set reason category for ticket data",e);
        }
        target.setTicketPriority(TicketPriority.valueOf(source.getPriority().getCode().toUpperCase()));
        if (null != source.getOrder()) {
            final AbstractOrderModel abstractOrderModel = source.getOrder();
            final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
            target.setAssociatedOrderCode(abstractOrderModel.getItemtype() + ": " + abstractOrderModel.getCode() + "; "
                    + LAST_UPDATED + ": " + sdf.format(abstractOrderModel.getModifiedtime()) + "; " + ORDER_TOTAL + ":"
                    + abstractOrderModel.getTotalPrice());
        }
        if (null != source.getAssociatedOrderEntries() && !source.getAssociatedOrderEntries().isEmpty()) {
            final List<String> orderEntryCodes = new ArrayList<String>();
            for (final AbstractOrderEntryModel entry : source.getAssociatedOrderEntries()) {
                if (null != entry.getProduct()) {
                    orderEntryCodes
                            .add("Product: " + entry.getProduct().getCode() + "; Quantity: " + entry.getQuantity());
                }
            }
            target.setAssociatedOrderEntryCodes(orderEntryCodes);
        }
    }

    /**
     * @param schemaAttribute
     *            schema attribute
     *
     */
    protected Map<String, String> populateCategoryMap(final CsTicketModel source,
            final CategorizationCategoryModel categoryModel, final String schemaAttribute) {
        final Map<String, String> categoryMap = new HashMap<String, String>();

        CategorySchemaModel schema = this.sapCategoriesService.getSchemaByCategoryCode(categoryModel, schemaAttribute,
                source.getCreationtime());

        int level = schema.getLevel().intValue();
        categoryMap.put(String.valueOf(level), categoryModel.getCategorizationGuid());
        while (level > 1) {
            level--;
            final String categoryToPut = schema.getParentCategory().getCategorizationGuid();
            categoryMap.put(String.valueOf(level), categoryToPut);

            schema = this.sapCategoriesService.getSchemaByCategoryCode(
                    this.sapCategoriesService.getCategoryByGuid(categoryToPut), schemaAttribute,
                    source.getCreationtime());

        }
        return categoryMap;
    }

    /**
     * @param sourceRelatedObjList
     */
    protected final List<TicketRelatedObjectData> getRelatedObjectList(
            final List<CsTicketRelatedObjectModel> sourceRelatedObjList) {
        final List<TicketRelatedObjectData> relobjList = new ArrayList<TicketRelatedObjectData>();

        for (final Iterator iterator = sourceRelatedObjList.iterator(); iterator.hasNext();) {
            final CsTicketRelatedObjectModel csTicketRelatedObjectModel = (CsTicketRelatedObjectModel) iterator.next();
            relobjList.add(getRelatedObjectData(csTicketRelatedObjectModel.getObjectId(),
                    csTicketRelatedObjectModel.getObjectType(), csTicketRelatedObjectModel.getObjectGuid()));
        }
        return relobjList;
    }

    protected TicketRelatedObjectData getRelatedObjectData(final String objectId, final String objectType,
            final String objectGuid) {
        final TicketRelatedObjectData ticketRelateObData = new TicketRelatedObjectData();
        ticketRelateObData.setObjectId(objectId);
        ticketRelateObData.setObjectType(objectType);
        ticketRelateObData.setObjectGuid(objectGuid);
        ticketRelateObData.setObjectUrl(getObjectUrl(ticketRelateObData.getObjectType()));
        return ticketRelateObData;

    }

    protected String getObjectUrl(final String objectType) {

        final String objectUrl = (String) configurationService.getConfiguration()
                .getProperty(CrmcustomerticketingfacadesConstants.PREFIX_URL + "." + objectType.toLowerCase());
        if (!StringUtils.isEmpty(objectUrl)) {
            return objectUrl;
        } else {
            return null;
        }
    }

    /**
     * @param sapCategoriesService
     *            the sapCategoriesService to set
     */
    public void setSapCategoriesService(final SapCrmCategoriesService sapCategoriesService) {
        this.sapCategoriesService = sapCategoriesService;
    }

}
