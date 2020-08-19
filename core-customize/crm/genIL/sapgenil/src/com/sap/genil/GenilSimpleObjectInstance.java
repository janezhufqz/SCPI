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

public class GenilSimpleObjectInstance {

	/**
	 * The name of the GenIL object
	 */
	private String objectName;
	private String rtGuid;
	private boolean idIsHandel;

	protected GenilSimpleObjectInstance(String objectName, String rtGuid,
			boolean idIsHandel) {
		this.objectName = objectName;
		this.rtGuid = rtGuid;
		this.idIsHandel = idIsHandel;
	}

	public String getObjectName() {
		return objectName;
	}

	public String getRtGuid() {
		return rtGuid;
	}

	public boolean isID_IS_HANDLE() {
		return idIsHandel;
	}

}
