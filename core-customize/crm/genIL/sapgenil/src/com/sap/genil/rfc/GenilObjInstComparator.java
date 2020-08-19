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

import java.util.Comparator;

/**
 * Compares to object instances based on their Name, ID and Handle flag
 * 
 * @author SAP
 */
public class GenilObjInstComparator implements Comparator<GenilObjInstance> {

    /*
     * (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */

    /**
     * Two Genil object instances are the same when they have the same
     * OnjectName, the same ObjectID and the same idIsHandle flag
     * 
     * @param o1 Object 1 to compare
     * @param o2 Object 2 to compare
     */
    public int compare(GenilObjInstance o1, GenilObjInstance o2) {
        String s1 = o1.getObjectName() + o1.getObjectId() + o1.idIsHandle();
        String s2 = o2.getObjectName() + o2.getObjectId() + o2.idIsHandle();

        return s1.compareTo(s2);
    }

}
