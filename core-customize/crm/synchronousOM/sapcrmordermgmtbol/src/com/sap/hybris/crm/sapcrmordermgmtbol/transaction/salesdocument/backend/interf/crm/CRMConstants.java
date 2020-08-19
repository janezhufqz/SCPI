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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm;




/**
 * Interface containing names of function modules and UI Elements, RFC Constants, field names <br>
 *
 * @version 1.0
 */
public interface CRMConstants
{
	public static String CRM_WEC_CUST_F4_UNITS = "CRM_HYB_CUST_F4_UNITS";

	public static String FM_LO_API_WEC_ORDER_ORDER_HISTORY = "CRM_ISA_SALESDOC_GETLIST";

	/** text control constant */
	public static String LF = "\n";
	/** text control constant */
	public static String SEPARATOR = "/";

	/** RFC constant. */
	public static String BAPI_RETURN_ERROR = "E";

	/** RFC constant. */
	public static String BAPI_RETURN_WARNING = "W";

	/** RFC constant. */
	public static String BAPI_RETURN_INFO = "I";

	/** RFC constant. */
	public static String BAPI_RETURN_ABORT = "A";

	/** RFC constant. */
	public static String ROLE_CONTACT = "00000015";

	/** RFC constant. */
	public static String ROLE_SOLDTO = "00000001";

	/** RFC constant. */
	public static String ROLE_SHIPTO = "00000002";

	/** RFC constant. */
	public static String ROLE_BILLPARTY = "00000003";

	/** RFC constant. */
	public static String ROLE_PAYER = "00000004";

	/** RFC constant. */
	public static String ROLE_PERSONRESP = "00000014";

	/** RFC constant. */
	public static String PARTNER_FCT = "PARTNER_FCT";

	/** RFC constant. */
	public static String PARTNER_PFT = "PARTNER_PFT";

	/** RFC constant. */
	public static String PARTNER_ID = "PARTNER_ID";



	/**
	 * The scenario ID for the LO-API call. Will not be persistet, but can be used to control LO-API processing.
	 */
	public static String scenario_LO_API_WEC = "CRM_WEC";

	/** ABAP constant */
	public static String ABAP_TRUE = "X";

	/*
	 * A list of commonly used RFC constants, to save memory via reducing the number of static strings
	 */
	/** field name */
	public static String FIELD_HANDLE = "HANDLE";

	public static String FIELD_GUID = "GUID";

	public static String REF_HANDLE = "REF_HANDLE";

	public static String REF_GUID = "REF_GUID";

	public static String REF_KIND = "REF_KIND";

	public static String FIELD_NAME = "FIELDNAME";

	public static String NUMBER_INT = "NUMBER_INT";

	public static String QUANTITY = "QUANTITY";

	public static String PRODUCT = "PRODUCT";

	public static String PRODUCT_GUID = "PRODUCT_GUID";

	public static String PROCESS_QTY_UNIT = "PROCESS_QTY_UNIT";




	/**
	 * Represents item number in SD
	 */
	public static String FIELD_POSNR = "POSNR";
	/** field name */
	public static String FIELD_HANDLE_PARENT = "HANDLE_PARENT";
	/** field name */
	public static String FIELD_OBJECT_ID = "OBJECT_ID";
	/** field name */
	public static String FIELD_ID = "ID";
	/** field name */
	public static String FIELD_SPRAS_ISO = "SPRAS_ISO";
	/** field name */
	public static String FIELD_TEXT_STRING = "TEXT_STRING";
	/** field name */
	public static String FIELD_HANDLE_ITEM = "HANDLE_ITEM";

	public static String BASKET_CREATE_MODE = "A";

	public static String BASKET_UPDATE_MODE = "B";

	public static String ORDER_READ_MODE = "C";

	public static final String FIRST_HEAD_HANDLE = "1";


	public static String ROLE_SOLDTO_PFT = "0001";

	public static String ROLE_SHIPTO_PFT = "0002";

	public static String ROLE_BILLPARTY_PFT = "0003";

	public static String ROLE_CONTACT_PFT = "0007";


}
