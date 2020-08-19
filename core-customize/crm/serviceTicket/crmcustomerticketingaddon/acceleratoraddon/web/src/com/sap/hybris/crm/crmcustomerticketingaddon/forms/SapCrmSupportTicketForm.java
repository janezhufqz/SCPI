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
package com.sap.hybris.crm.crmcustomerticketingaddon.forms;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.customerticketingaddon.forms.SupportTicketForm;
import de.hybris.platform.customerticketingfacades.data.TicketPriority;
import de.hybris.platform.customerticketingfacades.data.TicketRelatedObjectData;
import de.hybris.platform.validation.annotations.NotEmpty;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * @author C5230711
 *
 */
public class SapCrmSupportTicketForm extends SupportTicketForm {

    private TicketPriority ticketPriority;

    private List<TicketRelatedObjectData> ticketRelatedObjectData;

    private CategoryData subjectCategory1;
    private CategoryData subjectCategory2;
    private CategoryData subjectCategory3;
    private CategoryData subjectCategory4;
    private String reasonCategory1;
    private String reasonCategory2;
    private String reasonCategory3;
    private String reasonCategory4;

    /**
     * @return the ticketPriority
     */
    public TicketPriority getTicketPriority() {
        return ticketPriority;
    }

    /**
     * @param ticketPriority
     *            the ticketPriority to set
     */
    public void setTicketPriority(final TicketPriority ticketPriority) {
        this.ticketPriority = ticketPriority;
    }

    /**
     * @return the ticketRelatedObjectData
     */
    public List<TicketRelatedObjectData> getTicketRelatedObjectData() {
        return ticketRelatedObjectData;
    }

    /**
     * @param ticketRelatedObjectData
     *            the ticketRelatedObjectData to set
     */
    public void setTicketRelatedObjectData(final List<TicketRelatedObjectData> ticketRelatedObjectData) {
        this.ticketRelatedObjectData = ticketRelatedObjectData;
    }

    /**
     * @return the subjectCategory1
     */
    public CategoryData getSubjectCategory1() {
        return subjectCategory1;
    }

    /**
     * @param subjectCategory1
     *            the subjectCategory1 to set
     */
    public void setSubjectCategory1(final CategoryData subjectCategory1) {
        this.subjectCategory1 = subjectCategory1;
    }

    /**
     * @return the subjectCategory2
     */
    public CategoryData getSubjectCategory2() {
        return subjectCategory2;
    }

    /**
     * @param subjectCategory2
     *            the subjectCategory2 to set
     */
    public void setSubjectCategory2(final CategoryData subjectCategory2) {
        this.subjectCategory2 = subjectCategory2;
    }

    /**
     * @return the subjectCategory3
     */
    public CategoryData getSubjectCategory3() {
        return subjectCategory3;
    }

    /**
     * @param subjectCategory3
     *            the subjectCategory3 to set
     */
    public void setSubjectCategory3(final CategoryData subjectCategory3) {
        this.subjectCategory3 = subjectCategory3;
    }

    /**
     * @return the subjectCategory4
     */
    public CategoryData getSubjectCategory4() {
        return subjectCategory4;
    }

    /**
     * @param subjectCategory4
     *            the subjectCategory4 to set
     */
    public void setSubjectCategory4(final CategoryData subjectCategory4) {
        this.subjectCategory4 = subjectCategory4;
    }

    /**
     * @return the reasonCategory1
     */
    public String getReasonCategory1() {
        return reasonCategory1;
    }

    /**
     * @param reasonCategory1
     *            the reasonCategory1 to set
     */
    public void setReasonCategory1(final String reasonCategory1) {
        this.reasonCategory1 = reasonCategory1;
    }

    /**
     * @return the reasonCategory2
     */
    public String getReasonCategory2() {
        return reasonCategory2;
    }

    /**
     * @param reasonCategory2
     *            the reasonCategory2 to set
     */
    public void setReasonCategory2(final String reasonCategory2) {
        this.reasonCategory2 = reasonCategory2;
    }

    /**
     * @return the reasonCategory3
     */
    public String getReasonCategory3() {
        return reasonCategory3;
    }

    /**
     * @param reasonCategory3
     *            the reasonCategory3 to set
     */
    public void setReasonCategory3(final String reasonCategory3) {
        this.reasonCategory3 = reasonCategory3;
    }

    /**
     * @return the reasonCategory4
     */
    public String getReasonCategory4() {
        return reasonCategory4;
    }

    /**
     * @param reasonCategory4
     *            the reasonCategory4 to set
     */
    public void setReasonCategory4(final String reasonCategory4) {
        this.reasonCategory4 = reasonCategory4;
    }

}
