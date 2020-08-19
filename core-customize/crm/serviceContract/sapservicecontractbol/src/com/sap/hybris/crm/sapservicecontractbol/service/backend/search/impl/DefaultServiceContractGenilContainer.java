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
package com.sap.hybris.crm.sapservicecontractbol.service.backend.search.impl;

import java.util.List;

import com.sap.genil.GenilDataContainer;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractGenilContainer;

/**
 * Default implementation for Service Contract GENIL Container
 */
public class DefaultServiceContractGenilContainer implements ServiceContractGenilContainer {
    private GenilDataContainer contractInfo;
    private GenilDataContainer contractHeader;
    private GenilDataContainer contractHeaderStatusSet;
    private GenilDataContainer contractStatusCurrent;
    private GenilDataContainer contractCumulated;
    private GenilDataContainer contractTextSet;
    private List<GenilDataContainer> contractTextAll = null;
    private GenilDataContainer contractItems;
    private List<GenilDataContainer> contractItemsAllList = null;
    private GenilDataContainer contractItemPricingSet;
    private GenilDataContainer contractItemPricingExt;
    private GenilDataContainer contractItemProductExt;
    private GenilDataContainer contractItemServiceExt;
    private GenilDataContainer contractItemStatusSet;
    private GenilDataContainer contractItemStatusCurrent;
    private GenilDataContainer contractItemCummulateSet;
    private GenilDataContainer contractItemCummulateValue;
    private GenilDataContainer contractItemCummulateQuantity;
    private GenilDataContainer contractItemSchedlinExt;
    private GenilDataContainer contractItemSchedlinFirst;
    private GenilDataContainer contractItemDateSet;
    private List<GenilDataContainer> contractItemDatesAll = null;
    private GenilDataContainer contractBillingPlanSet;
    private GenilDataContainer contractBillingDetail;

    /**
     * @return the contractTextAll
     */
    @Override
    public List<GenilDataContainer> getContractTextAll() {
        return contractTextAll;
    }

    /**
     * @param contractTextAll
     *            the contractTextAll to set
     */
    @Override
    public void setContractTextAll(final List<GenilDataContainer> contractTextAll) {
        this.contractTextAll = contractTextAll;
    }

    /**
     * @return the contractInfo
     */
    @Override
    public GenilDataContainer getContractInfo() {
        return contractInfo;
    }

    /**
     * @param contractInfo
     *            the contractInfo to set
     */
    @Override
    public void setContractInfo(final GenilDataContainer contractInfo) {
        this.contractInfo = contractInfo;
    }

    /**
     * @return the contractHeader
     */
    @Override
    public GenilDataContainer getContractHeader() {
        return contractHeader;
    }

    /**
     * @param contractHeader
     *            the contractHeader to set
     */
    @Override
    public void setContractHeader(final GenilDataContainer contractHeader) {
        this.contractHeader = contractHeader;
    }

    /**
     * @return the contractHeaderStatusSet
     */
    @Override
    public GenilDataContainer getContractHeaderStatusSet() {
        return contractHeaderStatusSet;
    }

    /**
     * @param contractHeaderStatusSet
     *            the contractHeaderStatusSet to set
     */
    @Override
    public void setContractHeaderStatusSet(final GenilDataContainer contractHeaderStatusSet) {
        this.contractHeaderStatusSet = contractHeaderStatusSet;
    }

    /**
     * @return the contractStatusCurrent
     */
    @Override
    public GenilDataContainer getContractStatusCurrent() {
        return contractStatusCurrent;
    }

    /**
     * @param contractStatusCurrent
     *            the contractStatusCurrent to set
     */
    @Override
    public void setContractStatusCurrent(final GenilDataContainer contractStatusCurrent) {
        this.contractStatusCurrent = contractStatusCurrent;
    }

    /**
     * @return the contractCumulated
     */
    @Override
    public GenilDataContainer getContractCumulated() {
        return contractCumulated;
    }

    /**
     * @param contractCumulated
     *            the contractCumulated to set
     */
    @Override
    public void setContractCumulated(final GenilDataContainer contractCumulated) {
        this.contractCumulated = contractCumulated;
    }

    /**
     * @return the contractTextSet
     */
    @Override
    public GenilDataContainer getContractTextSet() {
        return contractTextSet;
    }

    /**
     * @param contractTextSet
     *            the contractTextSet to set
     */
    @Override
    public void setContractTextSet(final GenilDataContainer contractTextSet) {
        this.contractTextSet = contractTextSet;
    }

    /**
     * @return the contractItems
     */
    @Override
    public GenilDataContainer getContractItems() {
        return contractItems;
    }

    /**
     * @param contractItems
     *            the contractItems to set
     */
    @Override
    public void setContractItems(final GenilDataContainer contractItems) {
        this.contractItems = contractItems;
    }

    /**
     * @return the contractItemsAllList
     */
    @Override
    public List<GenilDataContainer> getContractItemsAllList() {
        return contractItemsAllList;
    }

    /**
     * @param contractItemsAllList
     *            the contractItemsAllList to set
     */
    @Override
    public void setContractItemsAllList(final List<GenilDataContainer> contractItemsAllList) {
        this.contractItemsAllList = contractItemsAllList;
    }

    /**
     * @return the contractItemPricingSet
     */
    @Override
    public GenilDataContainer getContractItemPricingSet() {
        return contractItemPricingSet;
    }

    /**
     * @param contractItemPricingSet
     *            the contractItemPricingSet to set
     */
    @Override
    public void setContractItemPricingSet(final GenilDataContainer contractItemPricingSet) {
        this.contractItemPricingSet = contractItemPricingSet;
    }

    /**
     * @return the contractItemPricingExt
     */
    @Override
    public GenilDataContainer getContractItemPricingExt() {
        return contractItemPricingExt;
    }

    /**
     * @param contractItemPricingExt
     *            the contractItemPricingExt to set
     */
    @Override
    public void setContractItemPricingExt(final GenilDataContainer contractItemPricingExt) {
        this.contractItemPricingExt = contractItemPricingExt;
    }

    /**
     * @return the contractItemProductExt
     */
    @Override
    public GenilDataContainer getContractItemProductExt() {
        return contractItemProductExt;
    }

    /**
     * @param contractItemProductExt
     *            the contractItemProductExt to set
     */
    @Override
    public void setContractItemProductExt(final GenilDataContainer contractItemProductExt) {
        this.contractItemProductExt = contractItemProductExt;
    }

    /**
     * @return the contractItemServiceExt
     */
    @Override
    public GenilDataContainer getContractItemServiceExt() {
        return contractItemServiceExt;
    }

    /**
     * @param contractItemServiceExt
     *            the contractItemServiceExt to set
     */
    @Override
    public void setContractItemServiceExt(final GenilDataContainer contractItemServiceExt) {
        this.contractItemServiceExt = contractItemServiceExt;
    }

    /**
     * @return the contractItemStatusSet
     */
    @Override
    public GenilDataContainer getContractItemStatusSet() {
        return contractItemStatusSet;
    }

    /**
     * @param contractItemStatusSet
     *            the contractItemStatusSet to set
     */
    @Override
    public void setContractItemStatusSet(final GenilDataContainer contractItemStatusSet) {
        this.contractItemStatusSet = contractItemStatusSet;
    }

    /**
     * @return the contractItemStatusCurrent
     */
    @Override
    public GenilDataContainer getContractItemStatusCurrent() {
        return contractItemStatusCurrent;
    }

    /**
     * @param contractItemStatusCurrent
     *            the contractItemStatusCurrent to set
     */
    @Override
    public void setContractItemStatusCurrent(final GenilDataContainer contractItemStatusCurrent) {
        this.contractItemStatusCurrent = contractItemStatusCurrent;
    }

    /**
     * @return the contractItemCummulateSet
     */
    @Override
    public GenilDataContainer getContractItemCummulateSet() {
        return contractItemCummulateSet;
    }

    /**
     * @param contractItemCummulateSet
     *            the contractItemCummulateSet to set
     */
    @Override
    public void setContractItemCummulateSet(final GenilDataContainer contractItemCummulateSet) {
        this.contractItemCummulateSet = contractItemCummulateSet;
    }

    /**
     * @return the contractItemCummulateValue
     */
    @Override
    public GenilDataContainer getContractItemCummulateValue() {
        return contractItemCummulateValue;
    }

    /**
     * @param contractItemCummulateValue
     *            the contractItemCummulateValue to set
     */
    @Override
    public void setContractItemCummulateValue(final GenilDataContainer contractItemCummulateValue) {
        this.contractItemCummulateValue = contractItemCummulateValue;
    }

    /**
     * @return the contractItemCummulateQuantity
     */
    @Override
    public GenilDataContainer getContractItemCummulateQuantity() {
        return contractItemCummulateQuantity;
    }

    /**
     * @param contractItemCummulateQuantity
     *            the contractItemCummulateQuantity to set
     */
    @Override
    public void setContractItemCummulateQuantity(final GenilDataContainer contractItemCummulateQuantity) {
        this.contractItemCummulateQuantity = contractItemCummulateQuantity;
    }

    /**
     * @return the contractItemSchedlinExt
     */
    @Override
    public GenilDataContainer getContractItemSchedlinExt() {
        return contractItemSchedlinExt;
    }

    /**
     * @param contractItemSchedlinExt
     *            the contractItemSchedlinExt to set
     */
    @Override
    public void setContractItemSchedlinExt(final GenilDataContainer contractItemSchedlinExt) {
        this.contractItemSchedlinExt = contractItemSchedlinExt;
    }

    /**
     * @return the contractItemSchedlinFirst
     */
    @Override
    public GenilDataContainer getContractItemSchedlinFirst() {
        return contractItemSchedlinFirst;
    }

    /**
     * @param contractItemSchedlinFirst
     *            the contractItemSchedlinFirst to set
     */
    @Override
    public void setContractItemSchedlinFirst(final GenilDataContainer contractItemSchedlinFirst) {
        this.contractItemSchedlinFirst = contractItemSchedlinFirst;
    }

    /**
     * @return the contractItemDateSet
     */
    @Override
    public GenilDataContainer getContractItemDateSet() {
        return contractItemDateSet;
    }

    /**
     * @param contractItemDateSet
     *            the contractItemDateSet to set
     */
    @Override
    public void setContractItemDateSet(final GenilDataContainer contractItemDateSet) {
        this.contractItemDateSet = contractItemDateSet;
    }

    /**
     * @return the contractItemDatesAll
     */
    @Override
    public List<GenilDataContainer> getContractItemDatesAll() {
        return contractItemDatesAll;
    }

    /**
     * @param contractItemDatesAll
     *            the contractItemDatesAll to set
     */
    @Override
    public void setContractItemDatesAll(final List<GenilDataContainer> contractItemDatesAll) {
        this.contractItemDatesAll = contractItemDatesAll;
    }

    /**
     * @return the contractBillingPlanSet
     */
    @Override
    public GenilDataContainer getContractBillingPlanSet() {
        return contractBillingPlanSet;
    }

    /**
     * @param contractBillingPlanSet
     *            the contractBillingPlanSet to set
     */
    @Override
    public void setContractBillingPlanSet(final GenilDataContainer contractBillingPlanSet) {
        this.contractBillingPlanSet = contractBillingPlanSet;
    }

    /**
     * @return the contractBillingDetail
     */
    @Override
    public GenilDataContainer getContractBillingDetail() {
        return contractBillingDetail;
    }

    /**
     * @param contractBillingDetail
     *            the contractBillingDetail to set
     */
    @Override
    public void setContractBillingDetail(final GenilDataContainer contractBillingDetail) {
        this.contractBillingDetail = contractBillingDetail;
    }

}
