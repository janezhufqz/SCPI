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
 * This class represents the GenIL relation which belongs to GenIL object. <br>
 * 
 * @author SAP
 * @version 1.0
 */
public class GenilModelRelation {
    public static final char ASSOCIATION = 'A';
    public static final char COMPOSITE = 'B';
    public static final char AGGREGATION = 'C';
    public static final char ZERO2ONE = 'A';
    public static final char ZERO2N = 'B';
    public static final char ONE = 'C';
    public static final char ONE2N = 'D';

    /**
     * Name of GenIL Model relation
     */
    private String relation;

    /**
     * parent to this relation
     */
    private GenilModelObject objectA;

    /**
     * related object
     */
    private GenilModelObject objectB;

    /**
     * Cardinality of object A
     */
    private char cardA;

    /**
     * Cardinality of object B
     */
    private char cardB;

    /**
     * Cardinality of relation
     */
    private char cardRelation;

    protected GenilModelRelation(String relation) {
        super();
        this.relation = relation;
    }

    /**
     * Getter-Method for property {@link #cardA}. <br>
     * 
     * @return Returns the {@link #cardA}.
     */
    public char getCardA() {
        return cardA;
    }

    /**
     * Getter-Method for property {@link #cardB}. <br>
     * 
     * @return Returns the {@link #cardB}.
     */
    public char getCardB() {
        return cardB;
    }

    /**
     * Getter-Method for property {@link #cardRelation}. <br>
     * 
     * @return Returns the {@link #cardRelation}.
     */
    public char getCardRelation() {
        return cardRelation;
    }

    /**
     * Getter-Method for property {@link #objectA}. <br>
     * 
     * @return Returns the {@link #objectA}.
     */
    public GenilModelObject getObjectA() {
        return objectA;
    }

    /**
     * Getter-Method for property {@link #objectB}. <br>
     * 
     * @return Returns the {@link #objectB}.
     */
    public GenilModelObject getObjectB() {
        return objectB;
    }

    /**
     * Getter-Method for property {@link #relation}. <br>
     * 
     * @return Returns the {@link #relation}.
     */public String getRelation() {
        return relation;
    }


     /**
      * Setter-Method for property {@link #cardA}. <br>
      * 
      * @param cardA The {@link #cardA} to set.
      */
     protected void setCardA(char cardA) {
        this.cardA = cardA;
    }

     /**
      * Setter-Method for property {@link #cardB}. <br>
      * 
      * @param cardB The {@link #cardB} to set.
      */
     protected void setCardB(char cardB) {
        this.cardB = cardB;
    }

     /**
      * Setter-Method for property {@link #cardRelation}. <br>
      * 
      * @param cardRelation The {@link #cardRelation} to set.
      */
     protected void setCardRelation(char cardRelation) {
        this.cardRelation = cardRelation;
    }

    /**
     * Setter-Method for property {@link #objectA}. <br>
     * 
     * @param objectA The {@link #objectA} to set.
     */
    protected void setObjectA(GenilModelObject objectA) {
        this.objectA = objectA;
    }

    /**
     * Setter-Method for property {@link #objectB}. <br>
     * 
     * @param objectB The {@link #objectB} to set.
     */
    protected void setObjectB(GenilModelObject objectB) {
        this.objectB = objectB;
    }

    /**
     * Setter-Method for property {@link #relation}. <br>
     * 
     * @param relation The {@link #relation} to set.
     */
    protected void setRelation(String relation) {
        this.relation = relation;
    }
}
