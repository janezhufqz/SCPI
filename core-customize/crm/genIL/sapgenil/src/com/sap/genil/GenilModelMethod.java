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
 * This class describes a GenIL method which belongs to GenIL object. <br> 
 * 
 * @author SAP
 * @version 1.0
 */
public class GenilModelMethod {
    private String methodName;
    private String paramStructure;
    private String returnType;
    private GenilModelObject object;

    protected GenilModelMethod(GenilModelObject object, String methodName) {
        super();
        this.object = object;
        this.methodName = methodName;
    }

    /**
     * Getter-Method for property {@link #methodName}. <br>
     * 
     * @return Returns the {@link #methodName}.
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Getter-Method for property {@link #object}. <br>
     * 
     * @return Returns the {@link #object}.
     */
    public GenilModelObject getObject() {
        return object;
    }

    /**
     * Getter-Method for property {@link #paramStructure}. <br>
     * 
     * @return Returns the {@link #paramStructure}.
     */
    public String getParamStructure() {
        return paramStructure;
    }

    /**
     * Getter-Method for property {@link #returnType}. <br>
     * 
     * @return Returns the {@link #returnType}.
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * Setter-Method for property {@link #paramStructure}. <br>
     * 
     * @param paramStructure The {@link #paramStructure} to set.
     */
    protected void setParamStructure(String paramStructure) {
        this.paramStructure = paramStructure;
    }

    /**
     * Setter-Method for property {@link #returnType}. <br>
     * 
     * @param returnType The {@link #returnType} to set.
     */
    protected void setReturnType(String returnType) {
        this.returnType = returnType;
    }

}
