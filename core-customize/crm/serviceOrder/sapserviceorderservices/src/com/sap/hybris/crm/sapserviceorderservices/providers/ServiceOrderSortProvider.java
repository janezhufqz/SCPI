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

package com.sap.hybris.crm.sapserviceorderservices.providers;

import de.hybris.platform.commerceservices.search.pagedata.SortData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.Assert;

public class ServiceOrderSortProvider {

    private Map<String, String> properties;

    /**
     * This method takes the available shorts from spring configuration for
     * doing mapping with sort field which can come from front end or any other
     * layer with corresponding actual field of Service Order model
     *
     * @param sortCode
     *            sort code that need to set
     *
     * @param sortOrder
     *
     * @return List<SortData> returns the sort data that with current sort
     */

    public List<SortData> getAvailableSortWithSlected(final String sortCode) {

        final List<SortData> availableSort = new ArrayList<SortData>();

        // getting the property mapping present in spring configuration
        for (final Entry<String, String> sortCraiteria : properties.entrySet()) {
            final SortData sort = new SortData();

            if (sortCraiteria.getKey() != null) {
                sort.setCode(sortCraiteria.getKey());
                if (sortCraiteria.getKey().equals(sortCode)) {
                    sort.setSelected(true);
                }
                availableSort.add(sort);
            }

        }

        return availableSort;
    }

    /**
     * @param sortCode
     * @param String
     *            - mapping with table attribute
     */
    public String getTypeAttributeForSortCode(final String sortCode) {
        if (properties.containsKey(sortCode)) {
            return properties.get(sortCode);
        }
        return null;
    }

    /**
     * @return the properties
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * @param properties
     *            the properties to set
     */
    public void setProperties(final Map<String, String> properties) {
        Assert.notEmpty(properties);
        this.properties = properties;
    }

}
