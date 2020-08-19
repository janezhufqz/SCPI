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

/**
 *
 */
package com.sap.hybris.crm.sapcrmpricingbol.backend.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogSeverity;
import de.hybris.platform.sap.core.configuration.enums.BackendType;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction;
import de.hybris.platform.servicelayer.user.UserService;

import org.springframework.beans.factory.annotation.Required;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;


/**
 * @author C5229090
 *
 *         sets header import parameters to JCo Structures
 */
public class SapCrmPricingHeaderMapper
{
	protected static final String TAX_GROUP_P = "TAX_GROUP_P";
	protected static final String TAX_GROUP_BP_01 = "TAX_GROUP_BP_01";
	protected static final String TAX_DEST_CTY = "TAX_DEST_CTY";
	protected static final String TAX_DEPART_CTY = "TAX_DEPART_CTY";
	protected static final String INCOTERMS2 = "INCOTERMS2";
	protected static final String INCOTERMS1 = "INCOTERMS1";
	protected static final String SOLD_TO_PARTY = "SOLD_TO_PARTY";
	protected static final String HEADER_DIVISION = "HEADER_DIVISION";
	protected static final String DIS_CHANNEL = "DIS_CHANNEL";
	protected static final String SALES_ORG = "SALES_ORG";
	protected static final String VALUES = "VALUES";
	protected static final String FIELDNAME = "FIELDNAME";
	protected static final String ATTRIBUTES = "ATTRIBUTES";
	protected static final String LOCAL_CURRENCY_UNIT = "LOCAL_CURRENCY_UNIT";
	protected static final String DOCUMENT_CURRENCY_UNIT = "DOCUMENT_CURRENCY_UNIT";
	protected static final String PRC_PROCEDURE_NAME = "PRC_PROCEDURE_NAME";
	protected static final String APPLICATION = "APPLICATION";
	protected static final String IS_HEADER_INPUT = "IS_HEADER_INPUT";

	static final private Log4JWrapper LOGGER = Log4JWrapper.getInstance(SapCrmPricingHeaderMapper.class.getName());

	private ModuleConfigurationAccess moduleConfigurationAccess = null;
	private UserService userService;

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 *
	 * @return ModuleConfigurationAccess
	 */
	public ModuleConfigurationAccess getModuleConfigurationAccess()
	{
		return moduleConfigurationAccess;
	}

	/**
	 * @param moduleConfigurationAccess
	 */
	@Required
	public void setModuleConfigurationAccess(final ModuleConfigurationAccess moduleConfigurationAccess)
	{
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}


	/**
	 * sets header import parameters to JCo Structures
	 *
	 * @param function
	 * @param partnerFunction
	 */
	public void fillImportParameters(final JCoFunction function, final SapPricingPartnerFunction partnerFunction)
	{
		LOGGER.entering("fillImportParameters(JCoFunction,SapPricingPartnerFunction)");
		LOGGER.trace(LogSeverity.PATH, "SapCrmPricingHeaderMapper :: fillImportParameters :: Enterted");
		partnerFunction.setLanguage("EN");
		final JCoStructure importHeader = function.getImportParameterList().getStructure(IS_HEADER_INPUT);

		importHeader.setValue(APPLICATION, BackendType.CRM.getCode());
		importHeader.setValue(PRC_PROCEDURE_NAME, getProperty("sapcrmpricingprocedure"));
		LOGGER.debug("pricing procedure : #########" + getProperty("sapcrmpricingprocedure"));
		importHeader.setValue(DOCUMENT_CURRENCY_UNIT, partnerFunction.getCurrency());
		importHeader.setValue(LOCAL_CURRENCY_UNIT, partnerFunction.getCurrency());
		final JCoTable attributes = importHeader.getTable(ATTRIBUTES);

		attributes.appendRow();
		attributes.setValue(FIELDNAME, SALES_ORG);
		final JCoTable salesOrg = attributes.getTable(VALUES);
		salesOrg.appendRow();
		salesOrg.setValue(0, getProperty("sapcommon_salesOrganization"));

		attributes.appendRow();
		attributes.setValue(FIELDNAME, DIS_CHANNEL);
		final JCoTable disChannel = attributes.getTable(VALUES);
		disChannel.appendRow();
		disChannel.setValue(0, getProperty("sapcommon_distributionChannel"));

		attributes.appendRow();
		attributes.setValue(FIELDNAME, HEADER_DIVISION);
		final JCoTable division = attributes.getTable(VALUES);
		division.appendRow();
		division.setValue(0, getProperty("sapcommon_division"));

		final UserModel currentCustomer = getUserService().getCurrentUser();

		fillIncotermsParameters(currentCustomer, attributes, partnerFunction);

	}

	public void fillIncotermsParameters(final UserModel currentCustomer, final JCoTable attributes,
			final SapPricingPartnerFunction partnerFunction)
	{
		if (getUserService().isAnonymousUser(currentCustomer))
		{
			//Retrieve details for Anonymous Customer
			attributes.appendRow();
			attributes.setValue(FIELDNAME, SOLD_TO_PARTY);
			final JCoTable soldToParty = attributes.getTable(VALUES);
			soldToParty.appendRow();
			soldToParty.setValue(0, getProperty(SAPConfigurationModel.REFERENCECUSTOMERGUID));

			attributes.appendRow();
			attributes.setValue(FIELDNAME, INCOTERMS1);
			final JCoTable incoTerms1 = attributes.getTable(VALUES);
			incoTerms1.appendRow();
			incoTerms1.setValue(0, getProperty(SAPConfigurationModel.INCOTERMS1));

			attributes.appendRow();
			attributes.setValue(FIELDNAME, INCOTERMS2);
			final JCoTable incoTerms2 = attributes.getTable(VALUES);
			incoTerms2.appendRow();
			incoTerms2.setValue(0, getProperty(SAPConfigurationModel.INCOTERMS2));
		}
		else
		{
			if (currentCustomer instanceof CustomerModel)
			{
				final CustomerModel currentB2CCustomer = (CustomerModel) currentCustomer;
				//B2C Logged on Customer
				attributes.appendRow();
				attributes.setValue(FIELDNAME, SOLD_TO_PARTY);
				final JCoTable soldToParty = attributes.getTable(VALUES);
				soldToParty.appendRow();
				soldToParty.setValue(0, currentB2CCustomer.getCrmGuid());

				attributes.appendRow();
				attributes.setValue(FIELDNAME, INCOTERMS1);
				final JCoTable incoTerms1 = attributes.getTable(VALUES);
				incoTerms1.appendRow();
				incoTerms1.setValue(0, currentB2CCustomer.getIncoterms1());

				attributes.appendRow();
				attributes.setValue(FIELDNAME, INCOTERMS2);
				final JCoTable incoTerms2 = attributes.getTable(VALUES);
				incoTerms2.appendRow();
				incoTerms2.setValue(0, currentB2CCustomer.getIncoterms2());
			}
		}
	}

	/**
	 * Returns property value for the property name
	 *
	 * @param name
	 *
	 * @return property value for the property name
	 */

	protected String getProperty(final String name)
	{
		final Object propertyValue = getModuleConfigurationAccess().getProperty(name);

		return (String) propertyValue;
	}


	public void fillImportTaxParameters(final JCoFunction function, final SapPricingPartnerFunction partnerFunction,
			final AbstractOrderModel order)
	{
		final JCoStructure importHeader = function.getImportParameterList().getStructure(IS_HEADER_INPUT);
		for (final AbstractOrderEntryModel orderEntryModel : order.getEntries())
		{
			fillTaxParameters(importHeader, partnerFunction, orderEntryModel);
		}
		function.getImportParameterList().setValue(IS_HEADER_INPUT, importHeader);
	}

	public void fillTaxParameters(final JCoStructure importHeader, final SapPricingPartnerFunction partnerFunction,
			final AbstractOrderEntryModel orderEntryModel)
	{

		importHeader.setValue(APPLICATION, BackendType.CRM.getCode());
		importHeader.setValue(PRC_PROCEDURE_NAME, getProperty("sapcrmpricingprocedure"));
		LOGGER.debug("pricing procedure : #########" + getProperty("sapcrmpricingprocedure"));
		importHeader.setValue(DOCUMENT_CURRENCY_UNIT, partnerFunction.getCurrency());
		importHeader.setValue(LOCAL_CURRENCY_UNIT, partnerFunction.getCurrency());
		final JCoTable attributes = importHeader.getTable(ATTRIBUTES);

		attributes.appendRow();
		attributes.setValue(FIELDNAME, SALES_ORG);
		final JCoTable salesOrg = attributes.getTable(VALUES);
		salesOrg.appendRow();
		salesOrg.setValue(0, getProperty("sapcommon_salesOrganization"));

		attributes.appendRow();
		attributes.setValue(FIELDNAME, DIS_CHANNEL);
		final JCoTable disChannel = attributes.getTable(VALUES);
		disChannel.appendRow();
		disChannel.setValue(0, getProperty("sapcommon_distributionChannel"));

		attributes.appendRow();
		attributes.setValue(FIELDNAME, HEADER_DIVISION);
		final JCoTable division = attributes.getTable(VALUES);
		division.appendRow();
		division.setValue(0, getProperty("sapcommon_division"));

		final UserModel currentCustomer = getUserService().getCurrentUser();

		fillTaxIncotermsParameters(currentCustomer, attributes, partnerFunction);


		if (orderEntryModel.getProduct().getEurope1PriceFactory_PTG() != null)
		{
			final String[] taxClass = orderEntryModel.getProduct().getEurope1PriceFactory_PTG().getCode().split("-");
			if (taxClass.length > 0)
			{

				attributes.appendRow();
				attributes.setValue(FIELDNAME, TAX_DEPART_CTY);
				final JCoTable taxDepartCountry = attributes.getTable(VALUES);
				taxDepartCountry.appendRow();
				taxDepartCountry.setValue(0, taxClass[0]);

				attributes.appendRow();
				attributes.setValue(FIELDNAME, TAX_DEST_CTY);
				final JCoTable taxDestCountry = attributes.getTable(VALUES);
				taxDestCountry.appendRow();
				taxDestCountry.setValue(0, taxClass[0]);

				attributes.appendRow();
				attributes.setValue(FIELDNAME, TAX_GROUP_BP_01);
				final JCoTable taxGroupBp = attributes.getTable(VALUES);
				taxGroupBp.appendRow();
				taxGroupBp.setValue(0, taxClass[taxClass.length - 1]);


				attributes.appendRow();
				attributes.setValue(FIELDNAME, TAX_GROUP_P);
				final JCoTable taxGroupP = attributes.getTable(VALUES);
				taxGroupP.appendRow();
				taxGroupP.setValue(0, taxClass[taxClass.length - 1]);

			}
		}

	}

	public void fillTaxIncotermsParameters(final UserModel currentCustomer, final JCoTable attributes,
			final SapPricingPartnerFunction partnerFunction)
	{
		if (getUserService().isAnonymousUser(currentCustomer))
		{
			//Retrieve details for Anonymous Customer
			attributes.appendRow();
			attributes.setValue(FIELDNAME, SOLD_TO_PARTY);
			final JCoTable soldToParty = attributes.getTable(VALUES);
			soldToParty.appendRow();
			soldToParty.setValue(0, getProperty(SAPConfigurationModel.REFERENCECUSTOMERGUID));

			attributes.appendRow();
			attributes.setValue(FIELDNAME, INCOTERMS1);
			final JCoTable incoTerms1 = attributes.getTable(VALUES);
			incoTerms1.appendRow();
			incoTerms1.setValue(0, getProperty(SAPConfigurationModel.INCOTERMS1));

			attributes.appendRow();
			attributes.setValue(FIELDNAME, INCOTERMS2);
			final JCoTable incoTerms2 = attributes.getTable(VALUES);
			incoTerms2.appendRow();
			incoTerms2.setValue(0, getProperty(SAPConfigurationModel.INCOTERMS2));
		}
		else
		{
			if (currentCustomer instanceof CustomerModel)
			{
				final CustomerModel currentB2CCustomer = (CustomerModel) currentCustomer;
				//B2C Logged on Customer
				attributes.appendRow();
				attributes.setValue(FIELDNAME, SOLD_TO_PARTY);
				final JCoTable soldToParty = attributes.getTable(VALUES);
				soldToParty.appendRow();
				soldToParty.setValue(0, currentB2CCustomer.getCrmGuid());

				attributes.appendRow();
				attributes.setValue(FIELDNAME, INCOTERMS1);
				final JCoTable incoTerms1 = attributes.getTable(VALUES);
				incoTerms1.appendRow();
				incoTerms1.setValue(0, currentB2CCustomer.getIncoterms1());

				attributes.appendRow();
				attributes.setValue(FIELDNAME, INCOTERMS2);
				final JCoTable incoTerms2 = attributes.getTable(VALUES);
				incoTerms2.appendRow();
				incoTerms2.setValue(0, currentB2CCustomer.getIncoterms2());
			}

		}
	}
}
