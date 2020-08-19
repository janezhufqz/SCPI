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
package com.sap.hybris.sapcrmcustomerb2c.constants;

import de.hybris.platform.core.model.user.CustomerModel;

/**
 * @author C5230245
 *
 */
public class CreditCardMetaData {

    private String subscriptionId;
    private String cardtype;
    private CustomerModel customerModel;
    private String validToMonth;
    private String ccOwner;
    private String validFromYear;
    private String validToYear;
    private String validFromMonth;
    private boolean duplicate;
    private boolean saved;
    private boolean isDefault;

    /**
     * @return the isDefault
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * @param isDefault
     *            the isDefault to set
     */
    public void setDefault(final boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * @return the cardtype
     */
    public String getCardtype() {
        return cardtype;
    }

    /**
     * @param cardtype
     *            the cardtype to set
     */
    public void setCardtype(final String cardtype) {
        this.cardtype = cardtype;
    }

    /**
     * @return the customerModel
     */
    public CustomerModel getCustomerModel() {
        return customerModel;
    }

    /**
     * @param customerModel
     *            the customerModel to set
     */
    public void setCustomerModel(final CustomerModel customerModel) {
        this.customerModel = customerModel;
    }

    /**
     * @return the validToMonth
     */
    public String getValidToMonth() {
        return validToMonth;
    }

    /**
     * @param validToMonth
     *            the validToMonth to set
     */
    public void setValidToMonth(final String validToMonth) {
        this.validToMonth = validToMonth;
    }

    /**
     * @return the ccOwner
     */
    public String getCcOwner() {
        return ccOwner;
    }

    /**
     * @param ccOwner
     *            the ccOwner to set
     */
    public void setCcOwner(final String ccOwner) {
        this.ccOwner = ccOwner;
    }

    /**
     * @return the validFromYear
     */
    public String getValidFromYear() {
        return validFromYear;
    }

    /**
     * @param validFromYear
     *            the validFromYear to set
     */
    public void setValidFromYear(final String validFromYear) {
        this.validFromYear = validFromYear;
    }

    /**
     * @return the validToYear
     */
    public String getValidToYear() {
        return validToYear;
    }

    /**
     * @param validToYear
     *            the validToYear to set
     */
    public void setValidToYear(final String validToYear) {
        this.validToYear = validToYear;
    }

    /**
     * @return the validFromMonth
     */
    public String getValidFromMonth() {
        return validFromMonth;
    }

    /**
     * @param validFromMonth
     *            the validFromMonth to set
     */
    public void setValidFromMonth(final String validFromMonth) {
        this.validFromMonth = validFromMonth;
    }

    /**
     * @return the duplicate
     */
    public boolean isDuplicate() {
        return duplicate;
    }

    /**
     * @param boolean1
     *            the duplicate to set
     */
    public void setDuplicate(final boolean duplicate) {
        this.duplicate = duplicate;
    }

    /**
     * @return the saved
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * @param saved
     *            the saved to set
     */
    public void setSaved(final boolean saved) {
        this.saved = saved;
    }

    /**
     * @return the subscriptionId
     */
    public String getSubscriptionId() {
        return subscriptionId;
    }

    /**
     * @param subscriptionId
     *            the subscriptionId to set
     */
    public void setSubscriptionId(final String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    /**
     * @param validToMonth2
     */

}
