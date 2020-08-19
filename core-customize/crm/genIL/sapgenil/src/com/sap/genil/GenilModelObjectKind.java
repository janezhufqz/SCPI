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

package com.sap.genil;

/**
 * This class represents the different kinds of an genil model object
 * 
 * @author SAP
 * @version 1.0
 */
public enum GenilModelObjectKind {
    ROOT('A'),
    ACCESS('B'),
    DEPENDENT('C'),
    SEARCH('D'),
    SEARCH_RESULT('E'),
    VIEW('F'),
    DYNAMIC_SEARCH('G'),
    ABSTRACT('X'),
    INVALID();

    /**
     * 
     * 
     * @param kind
     * @return
     */
    public static GenilModelObjectKind get(char kind) {
        for (GenilModelObjectKind mok : GenilModelObjectKind.values()) {
            if (kind == mok.getKind()) {
                return mok;
            }
        }
        return null;
    }

    /**
     * Kind of the Object
     */
    private char kind;

    /**
     * Standard constructor. <br>
     */
    GenilModelObjectKind() {
        this.kind = '\0';
    }

    /**
     * Standard constructor. <br>
     * 
     * @param kind
     */
    GenilModelObjectKind(char kind) {
        this.kind = kind;
    }

    /**
     * Getter-Method for property {@link #kind}. <br>
     * 
     * @return Returns the {@link #kind}.
     */
    private char getKind() {
        return kind;
    }
}
