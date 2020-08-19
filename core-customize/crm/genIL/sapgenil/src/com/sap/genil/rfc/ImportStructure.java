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

package com.sap.genil.rfc;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author SAP
 * @version 1.0
 */
public class ImportStructure {
    private String name;
    private List<NameValuePair> values;

    public ImportStructure(String name) {
        super();
        this.name = name;
        this.values = new ArrayList<NameValuePair>();
    }

    public String getName() {
        return name;
    }

    public List<NameValuePair> getValues() {
        return values;
    }

    public void setValue(NameValuePair nvp) {
        values.add(nvp);
    }

    public void setValue(String name, Object value) {
        setValue(new NameValuePair(name, value));
    }
}
