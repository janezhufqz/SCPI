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

import java.util.Comparator;

import com.sap.hybris.crm.sapservicecontractbol.service.backend.search.ServiceContractSearchResult;

/**
 * Comparing search results of Service Contracts for sorts on web UI
 *
 *
 */
public class ServiceContractSearchResultComparator implements Comparator<ServiceContractSearchResult> {

    private final String sortCode;

    /**
     * Constructor
     *
     * @param sortCode
     */
    public ServiceContractSearchResultComparator(final String sortCode) {
        this.sortCode = sortCode;
    }

    /**
     * Implementation of compare for Service Contracts sorting
     */
    @Override
    public int compare(final ServiceContractSearchResult o1, final ServiceContractSearchResult o2) {
        int result = 0;

        switch (this.sortCode) {
        case "startDate":
            result = o1.getStartDate().compareTo(o2.getStartDate());
            break;
        case "endDate":
            result = o1.getEndDate().compareTo(o2.getEndDate());
            break;
        case "contractId":
            result = o2.getContractId().compareTo(o1.getContractId());
            break;
        case "status":
            result = o1.getContractConcatStatus().compareToIgnoreCase(o2.getContractConcatStatus());
            break;
        case "netValue":
            final Double val1 = Double.parseDouble(o1.getNetValue());
            final Double val2 = Double.parseDouble(o2.getNetValue());
            result = val1.compareTo(val2);
            break;
        default:
            // contract as default value
            result = o2.getContractId().compareTo(o1.getContractId());
            break;
        }

        if (result == 0) {
            result = o1.getContractId().compareTo(o2.getContractId());
        }

        return result;
    }
}
