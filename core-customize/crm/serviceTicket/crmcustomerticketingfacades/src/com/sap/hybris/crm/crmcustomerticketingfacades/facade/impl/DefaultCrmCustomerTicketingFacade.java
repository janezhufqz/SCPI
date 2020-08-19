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
package com.sap.hybris.crm.crmcustomerticketingfacades.facade.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sap.hybris.crm.crmcustomerticketingfacades.constants.CrmcustomerticketingfacadesConstants;
import com.sap.hybris.crm.crmcustomerticketingfacades.data.ComplaintAssociatedOrderData;
import com.sap.hybris.crm.crmcustomerticketingfacades.data.ComplaintAssociatedOrderEntryData;
import com.sap.hybris.crm.crmcustomerticketingfacades.facade.SapCrmTicketFacade;
import com.sap.hybris.crm.crmcustomerticketingfacades.strategies.ComplaintOrderAssociationStrategy;
import com.sap.hybris.crm.sapcrmcategoryschema.model.CategorizationCategoryModel;
import com.sap.hybris.crm.sapcrmcategoryschema.service.SapCrmCategoriesService;
import com.sap.hybris.crm.sapcrmcomplaintexchange.service.SapComplaintBussinessService;
import com.sap.hybris.crm.sapcrmmodel.util.XSSFilterUtil;
import com.sap.hybris.crm.sapcrmticketsystem.model.CsTicketRelatedObjectModel;
import com.sap.hybris.crm.sapcrmticketsystem.service.SapTicketBusinessService;
import com.sap.hybris.crm.sapcrmticketsystem.service.SapTicketService;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.customerticketingfacades.customerticket.DefaultCustomerTicketingFacade;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingfacades.data.TicketPriority;
import de.hybris.platform.customerticketingfacades.data.TicketRelatedObjectData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.ticket.enums.CsEventReason;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;

/**
 * @author C5230711
 *
 */
public class DefaultCrmCustomerTicketingFacade extends DefaultCustomerTicketingFacade implements SapCrmTicketFacade {

    private static final Logger LOG = Logger.getLogger(DefaultCrmCustomerTicketingFacade.class);

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;
    @Resource(name = "modelService")
    private ModelService modelService;
    @Autowired
    private BaseStoreService baseStoreService;
    @Autowired
    private CustomerAccountService customerAccountService;
    private SapTicketBusinessService sapTicketBusinessService;

    private Converter<CategorizationCategoryModel, CategoryData> categorizationCategoryConverter;
    private SapCrmCategoriesService sapCategoriesService;
    @Autowired
    private SapComplaintBussinessService sapComplaintBussinessService;
    @Autowired
    private ComplaintOrderAssociationStrategy complaintOrderAssociationStrategy;
    @Autowired
    private SapTicketService ticketService;

    @Override
    public List<TicketPriority> getTicketPriorities() {
        return Arrays.asList(TicketPriority.values());
    }

    @Override
    public TicketData createTicket(final TicketData ticketData) {
        TicketData ticketDataObj = null;
        if (!StringUtils.isEmpty(ticketData.getTicketPriority())) {
            setTicketPriority(ticketData.getTicketPriority().toString());
        }
        final CsTicketParameter ticketParameter = createCsTicketParameter(ticketData);
        final CsTicketModel ticket = this.sapTicketBusinessService.createTicket(ticketParameter);
        ticketDataObj = new TicketData();
        ticketDataObj.setId(ticket.getTicketID());
        return ticketDataObj;

    }

    @Override
    public TicketData createComplaint(final TicketData complaintData) {
        TicketData complaintDataObj = null;
        if (!StringUtils.isEmpty(complaintData.getTicketPriority())) {
            setTicketPriority(complaintData.getTicketPriority().toString());
        }
        final CsTicketParameter complaintParameter = createSapComplaintParameter(complaintData);
        final CsTicketModel ticket = this.sapComplaintBussinessService.createComplaint(complaintParameter);
        complaintDataObj = new TicketData();
        complaintDataObj.setId(ticket.getTicketID());
        return complaintDataObj;
    }

    protected CsTicketParameter createSapComplaintParameter(final TicketData complaintData) {
        final CsTicketParameter complaintParameter = new CsTicketParameter();
        complaintParameter.setPriority(
                getEnumerationService().getEnumerationValue(CsTicketPriority._TYPECODE, getTicketPriority()));
        complaintParameter
                .setReason(getEnumerationService().getEnumerationValue(CsEventReason._TYPECODE, getTicketReason()));
        complaintParameter.setAssignedGroup(getDefaultCsAgentManagerGroup());
        complaintParameter.setCategory(CsTicketCategory.valueOf(complaintData.getTicketCategory().name()));
        complaintParameter.setHeadline(XSSFilterUtil.filter(complaintData.getSubject()));
        complaintParameter.setInterventionType(CsInterventionType.TICKETMESSAGE);
        complaintParameter.setCreationNotes(XSSFilterUtil.filter(complaintData.getMessage()));
        complaintParameter.setCustomer(getUserService().getCurrentUser());
        complaintParameter.setAttachments(complaintData.getAttachments());
        complaintParameter.setReasonCategory(
                getCategoryModel(determineReasonCategoryForTicket(complaintData.getReasonCategoryMap())));
        complaintParameter.setRelatedObjects(populateTicketRelatedObjectList(complaintData.getRelatedObjects()));
        AbstractOrderModel order = null;
        final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
        if (null != complaintData.getAssociatedOrderCode() && !complaintData.getAssociatedOrderCode().isEmpty()) {
            order = customerAccountService.getOrderForCode(complaintData.getAssociatedOrderCode(),
                    baseStoreService.getCurrentBaseStore());
            complaintParameter.setAssociatedTo(order);
        }
        if (null != complaintData.getAssociatedOrderCode() && !complaintData.getAssociatedOrderEntryCodes().isEmpty()) {
            for (final AbstractOrderEntryModel entry : order.getEntries()) {
                if (complaintData.getAssociatedOrderEntryCodes().contains(entry.getEntryNumber().toString())) {
                    entries.add(entry);
                }
            }
            complaintParameter.setAssociatedOrderEntries(entries);
        }
        return complaintParameter;
    }

    @Override
    public TicketData updateTicket(final TicketData ticketData) {
        ServicesUtil.validateParameterNotNull(ticketData, "Ticket Data can not be null");
        final TicketData updateTicketData = super.updateTicket(ticketData);
        if (updateTicketData == null) {
            return null;
        } else {
            final CsTicketModel ticket = getTicketService().getTicketForTicketId(ticketData.getId());
            try {
                ticket.setPriority(CsTicketPriority.valueOf(ticketData.getTicketPriority().toString()));
                ticket.setCategory(CsTicketCategory.valueOf(ticketData.getTicketCategory().toString()));
                ticket.setHeadline(XSSFilterUtil.filter(ticketData.getSubject()));
                ticket.setReasonCategory(
                        getCategoryModel(determineReasonCategoryForTicket(ticketData.getReasonCategoryMap())));
                getTicketBusinessService().updateTicket(ticket);
            } catch (final Exception e) {
                LOG.error("Exception raised while updating", e);
            }
            return ticketData;
        }
    }

    @Override
    public TicketData updateComplaint(final TicketData complaintData) {
        ServicesUtil.validateParameterNotNull(complaintData, "Complaint Data can not be null");
        final TicketData updateTicketData = updateComplaintBasicData(complaintData);
        if (updateTicketData == null) {
            return null;
        } else {
            final CsTicketModel ticket = getTicketService().getTicketForTicketId(complaintData.getId());
            try {
                ticket.setPriority(CsTicketPriority.valueOf(complaintData.getTicketPriority().toString()));
                ticket.setCategory(CsTicketCategory.valueOf(complaintData.getTicketCategory().toString()));
                ticket.setHeadline(XSSFilterUtil.filter(complaintData.getSubject()));
                ticket.setReasonCategory(
                        getCategoryModel(determineReasonCategoryForTicket(complaintData.getReasonCategoryMap())));
                this.sapComplaintBussinessService.updateComplaint(ticket);
            } catch (final Exception e) {
                LOG.error("Exception while updating", e);
            }
            return complaintData;
        }
    }

    protected TicketData updateComplaintBasicData(TicketData complaintData) {
        CsTicketModel ticket = getTicketService().getTicketForTicketId(complaintData.getId());

        // if with status change
        if (!getStatusMapping().get(ticket.getState().getCode()).getId()
                .equalsIgnoreCase(complaintData.getStatus().getId())) {
            ticket = stateChanges.get(getComplaintStatus(complaintData)).apply(ticket, complaintData);
        }
            List<MediaModel> attachments = null;
            if (CollectionUtils.isNotEmpty(complaintData.getAttachments())) {
                attachments = new ArrayList<>(complaintData.getAttachments().size());
                for (final MultipartFile file : complaintData.getAttachments()) {
                    try {
                        attachments.add(getTicketAttachmentsService().createAttachment(file.getOriginalFilename(),
                                file.getContentType(), file.getBytes(), getUserService().getCurrentUser()));
                    } catch (final IOException e) {
                        LOG.error(e.getMessage(), e);
                        return null;
                    }
                }
            }

            final CsCustomerEventModel customerEventModel = this.sapComplaintBussinessService.addNoteComplaint(ticket,
                    CsInterventionType.IM, CsEventReason.UPDATE, XSSFilterUtil.filter(complaintData.getMessage()),
                    attachments);
            ticket = customerEventModel != null ? ticketService.getTicketForTicketId(ticket.getTicketID()) : null;
        if (ticket == null) {
            return null;
        }

        return complaintData;
    }

    protected String determineReasonCategoryForTicket(final Map<String, String> reasonCategoryMap) {
        String reasonCategory;
        if (null != reasonCategoryMap.get("4")) {
            reasonCategory = reasonCategoryMap.get("4");
        } else if (null != reasonCategoryMap.get("3")) {
            reasonCategory = reasonCategoryMap.get("3");
        } else if (null != reasonCategoryMap.get("2")) {
            reasonCategory = reasonCategoryMap.get("2");
        } else {
            reasonCategory = reasonCategoryMap.get("1");
        }
        return reasonCategory;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.sap.crmcustomerticketingaddon.SapCrmTicketFacade#
     * setQueryKeyParamToRelatedObject(java.util.Map)
     */
    @Override
    public List<TicketRelatedObjectData> setQueryKeyParamToRelatedObject(final Map<String, String> queryMap) {
        final List<TicketRelatedObjectData> tickecketRelatedObjList = new ArrayList<TicketRelatedObjectData>();
        final String installBaseType = (String) configurationService.getConfiguration()
                .getProperty(CrmcustomerticketingfacadesConstants.INSTALL_BASE_ID);

        final String componentType = (String) configurationService.getConfiguration()
                .getProperty(CrmcustomerticketingfacadesConstants.COMPONENT_ID);

        final String serviceOrderType = (String) configurationService.getConfiguration()
                .getProperty(CrmcustomerticketingfacadesConstants.SERVICE_ORDER_ID);

        final String installBaseGuid = (String) configurationService.getConfiguration()
                .getProperty(CrmcustomerticketingfacadesConstants.INSTALL_BASE_GUID);

        final String componentBaseGuid = (String) configurationService.getConfiguration()
                .getProperty(CrmcustomerticketingfacadesConstants.COMPONENT_GUID);

        final String installedBaseId = queryMap.get(installBaseType);
        final String componentId = queryMap.get(componentType);

        final String serviceOrderId = queryMap.get(serviceOrderType);

        final String componentGuid = queryMap.get(componentBaseGuid);
        final String installedBaseGuid = queryMap.get(installBaseGuid);

        if (!StringUtils.isEmpty(installedBaseId)) {
            tickecketRelatedObjList.add(getRelatedObjectData(installedBaseId, installBaseType, installedBaseGuid));
        }
        if (!StringUtils.isEmpty(componentId)) {
            tickecketRelatedObjList.add(getRelatedObjectData(componentId, componentType, componentGuid));
        }
        if (!StringUtils.isEmpty(serviceOrderId)) {
            tickecketRelatedObjList.add(getRelatedObjectData(serviceOrderId, serviceOrderType, null));
        }
        return tickecketRelatedObjList;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.sap.crmcustomerticketingaddon.SapCrmTicketFacade#
     * updateServiceOrderToTicket(de.hybris.platform
     * .customerticketingfacades.data.TicketData)
     */
    @Override
    public TicketData updateServiceOrderToTicket(final TicketData ticketData) {
        final CsTicketModel ticket = getTicketService().getTicketForTicketId(ticketData.getId());
        try {
            final List<CsTicketRelatedObjectModel> csRelObjectModelList = populateTicketRelatedObjectList(
                    ticketData.getRelatedObjects());
            ticket.setCsTicketRelatedObject(csRelObjectModelList);
            getTicketBusinessService().updateTicket(ticket);
            return ticketData;
        } catch (final Exception e) {
            LOG.error("Exception raised while updating : ", e);
            return ticketData;
        }
    }

    @Override
    public TicketData getWSTicket(final String ticketId) {
        final CsTicketModel ticketModel = getTicketService().getTicketForTicketId(ticketId);
        if (ticketModel == null) {
            return null; // preventing NPE below
        }

        return getTicketConverter().convert(ticketModel, new TicketData());
    }

    @Override
    public String getCategoryNameForCode(final String categoryGuid) {
        final CategorizationCategoryModel category = this.sapCategoriesService.getCategoryByGuid(categoryGuid);
        if (null != category) {
            return category.getName();
        }
        return null;
    }

    /**
     * This method created list of Related Object Model from DTO List
     *
     * @param relObjectist
     * @return list of CsTicketRelatedObjectModel
     */
    @Override
    public List<CsTicketRelatedObjectModel> populateTicketRelatedObjectList(
            final List<TicketRelatedObjectData> relObjectist) {
        final List<CsTicketRelatedObjectModel> csTicketRelatedObjectList = new ArrayList<CsTicketRelatedObjectModel>();
        if (relObjectist != null && !relObjectist.isEmpty()) {
            for (final Iterator iterator = relObjectist.iterator(); iterator.hasNext();) {
                final CsTicketRelatedObjectModel csTicketRelatedObj = (CsTicketRelatedObjectModel) this.modelService
                        .create(CsTicketRelatedObjectModel.class);
                final TicketRelatedObjectData ticketRelatedObjectData = (TicketRelatedObjectData) iterator.next();
                csTicketRelatedObj.setObjectId(ticketRelatedObjectData.getObjectId());
                csTicketRelatedObj.setObjectType(ticketRelatedObjectData.getObjectType());
                csTicketRelatedObj.setObjectGuid(ticketRelatedObjectData.getObjectGuid());
                csTicketRelatedObjectList.add(csTicketRelatedObj);
            }
        }
        return csTicketRelatedObjectList;
    }

    @Override
    public List<ComplaintAssociatedOrderData> getAssociatedOrders() {
        return complaintOrderAssociationStrategy.associatedOrders(getUserService().getCurrentUser());
    }

    @Override
    public List<ComplaintAssociatedOrderEntryData> getAssociatedOrderEntries(final String orderCode) {
        return complaintOrderAssociationStrategy.getAssociatedOrderEntries(orderCode);
    }

    protected CsTicketParameter createCsTicketParameter(final TicketData ticketData) {
        final CsTicketParameter ticketParameter = new CsTicketParameter();

        ticketParameter.setPriority(
                getEnumerationService().getEnumerationValue(CsTicketPriority._TYPECODE, getTicketPriority()));
        ticketParameter
                .setReason(getEnumerationService().getEnumerationValue(CsEventReason._TYPECODE, getTicketReason()));
        ticketParameter
                .setAssociatedTo(getTicketService().getAssociatedObject(ticketData.getAssociatedTo(), null, null));
        ticketParameter.setAssignedGroup(getDefaultCsAgentManagerGroup());
        ticketParameter.setCategory(CsTicketCategory.valueOf(ticketData.getTicketCategory().name()));
        ticketParameter.setHeadline(XSSFilterUtil.filter(ticketData.getSubject()));
        ticketParameter.setInterventionType(CsInterventionType.TICKETMESSAGE);
        ticketParameter.setCreationNotes(XSSFilterUtil.filter(ticketData.getMessage()));
        ticketParameter.setCustomer(getUserService().getCurrentUser());
        ticketParameter.setAttachments(ticketData.getAttachments());

        ticketParameter.setReasonCategory(
                getCategoryModel(determineReasonCategoryForTicket(ticketData.getReasonCategoryMap())));
        ticketParameter.setRelatedObjects(populateTicketRelatedObjectList(ticketData.getRelatedObjects()));
        return ticketParameter;
    }

    /**
     * Get category Model by guid
     *
     * @param reasonCategory
     * @return CategoryModel
     */
    protected CategorizationCategoryModel getCategoryModel(final Object reasonCategory) {
        CategorizationCategoryModel category = new CategorizationCategoryModel();
        category.setCategorizationGuid((String) reasonCategory);        
        try {
            category = this.modelService.getByExample(category);
        } catch (ModelNotFoundException e) {
            LOG.error("Exception raised while fetching category : ", e);
            category = null;
        }
        return category;
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

    protected CsTicketState getComplaintStatus(final TicketData data) {
        for (final String key : getStatusMapping().keySet()) {
            if (data.getStatus().getId().equalsIgnoreCase(getStatusMapping().get(key).getId())) {
                LOG.info("matching with: " + key);
                return CsTicketState.valueOf(key);
            }
        }
        LOG.warn("Status key not found");
        return null;
    }

    /**
     * @return the sapTicketBusinessService
     */
    public SapTicketBusinessService getSapTicketBusinessService() {
        return sapTicketBusinessService;
    }

    /**
     * @param sapTicketBusinessService
     *            the sapTicketBusinessService to set
     */
    public void setSapTicketBusinessService(final SapTicketBusinessService sapTicketBusinessService) {
        this.sapTicketBusinessService = sapTicketBusinessService;
    }

    /**
     * @param sapCategoriesService
     *            the sapCategoriesService to set
     */
    public void setSapCategoriesService(final SapCrmCategoriesService sapCategoriesService) {
        this.sapCategoriesService = sapCategoriesService;
    }

    public Converter<CategorizationCategoryModel, CategoryData> getCategorizationCategoryConverter() {
        return categorizationCategoryConverter;
    }

    public void setCategorizationCategoryConverter(
            Converter<CategorizationCategoryModel, CategoryData> categorizationCategoryConverter) {
        this.categorizationCategoryConverter = categorizationCategoryConverter;
    }

    /**
     * Method to find all the related service tickets for an ibase.
     * 
     * @param iBaseID
     *            installed base id
     */
    @Override
    public List<TicketData> getTicketsForIbase(String iBaseID) {
        String objectType = (String) configurationService.getConfiguration()
                .getProperty(CrmcustomerticketingfacadesConstants.INSTALL_BASE_ID);
        List<CsTicketModel> tickets = getTicketService().getTicketsForIbase(getUserService().getCurrentUser(), iBaseID,
                objectType);
        return Converters.convertAll(tickets, getTicketListConverter());
    }

    @Override
    protected SapTicketService getTicketService() {
        return ticketService;
    }
}
