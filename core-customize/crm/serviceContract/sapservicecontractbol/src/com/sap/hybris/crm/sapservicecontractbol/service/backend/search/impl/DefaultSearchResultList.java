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

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.SearchResultList;
import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractSearchResult;

/**
 * Default implementation of SearchResultList
 *
 *
 */
public class DefaultSearchResultList implements SearchResultList {

    private PageableData pageableData;
    List<ServiceContractSearchResult> results = new ArrayList<ServiceContractSearchResult>();

    /**
     * @return the results
     */
    @Override
    public List<ServiceContractSearchResult> getResults() {
        return results;
    }

    /**
     * @param results
     *            the results to set
     */
    public void setResults(final List<ServiceContractSearchResult> results) {
        this.results = results;
    }

    PageableData getPageableData() {
        return pageableData;
    }

    @Override
    public void add(final ServiceContractSearchResult result) {
        this.results.add(result);

    }

    @Override
    public int size() {
        return results.size();
    }

    @Override
    public void clear() {
        results.clear();

    }

    @Override
    public void setPageableData(final PageableData pageableData) {
        this.pageableData = pageableData;
    }

    protected void sortResultList(final String sort) {
        final ServiceContractSearchResultComparator comparator = new ServiceContractSearchResultComparator(sort);
        Collections.sort(results, comparator);
    }

    @Override
    public List<ServiceContractSearchResult> getSearchResult() {
        if (results.isEmpty()) {
            return Collections.emptyList();
        }
        if (pageableData == null) {
            throw new ApplicationBaseRuntimeException("No pageable data provided!");
        }

        if (this.pageableData.getSort() == null) {
            this.pageableData.setSort("contractId");
        }

        this.sortResultList(this.pageableData.getSort());

        final int startIndex = determineStartIndex();
        final int endIndex = determineEndIndex(startIndex);
        // return this.results
        return this.results.subList(startIndex, endIndex);
    }

    protected int determineStartIndex() {
        final int startIndex = pageableData.getCurrentPage() * pageableData.getPageSize();
        if (startIndex >= results.size()) {
            throw new ApplicationBaseRuntimeException("Pageable data does not match results size");
        }
        return startIndex;
    }

    protected int determineEndIndex(final int startIndex) {
        int endIndex = startIndex + pageableData.getPageSize();
        if (endIndex > results.size()) {
            endIndex = results.size();
        }
        return endIndex;
    }

}
