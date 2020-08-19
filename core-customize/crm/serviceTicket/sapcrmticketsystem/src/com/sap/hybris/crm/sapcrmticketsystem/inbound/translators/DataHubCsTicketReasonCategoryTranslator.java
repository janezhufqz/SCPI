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

package com.sap.hybris.crm.sapcrmticketsystem.inbound.translators;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.crm.sapcrmticketsystem.constants.SapcrmticketsystemConstants;
import com.sap.hybris.crm.sapcrmticketsystem.inbound.DataHubInboundReasonCatHelper;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloInvalidParameterException;

/**
 * This class translates the reason category attribute .
 *
 * @author C5229484
 *
 */
public class DataHubCsTicketReasonCategoryTranslator extends DataHubTranslator<DataHubInboundReasonCatHelper> {

    /**
     * Logger for class
     */
    private static final Logger LOG = LoggerFactory.getLogger(DataHubCsTicketReasonCategoryTranslator.class);

    @SuppressWarnings("javadoc")
    public static final String HELPER_BEAN = "sapDataHubInboundCsTicketReasonCatHelper";

    @SuppressWarnings("javadoc")
    public DataHubCsTicketReasonCategoryTranslator() {
        super(HELPER_BEAN);
    }

    /**
     * This method intercepts the ticket import by getting the ticket attributes
     * to fetch the ticket reason category based on the catalog type and schema
     * id in Hybris.
     */
    @Override
    public void performImport(final String reasonCategory, final Item processedItem) throws ImpExException {
        LOG.debug("DataHubCsTicketEventTranslator: Invoked translator to create a new change event for ticket ");

		if (reasonCategory != null && !reasonCategory.equals(SapcrmticketsystemConstants.IGNORE))
		{
			try
			{
				final List<String> categoryData = Arrays.asList(StringUtils.split(reasonCategory, '|'));
				final String categoryCode = categoryData.get(0);
				final String catalogType = categoryData.get(1);
				final String catalogName = categoryData.get(2);
				if (categoryCode != null && catalogType != null && catalogName != null)
				{
					processedItem.setAttribute("reasonCategory",
							getInboundHelper().getCategoryFromSchema(categoryCode, catalogType, catalogName));
				}
			}
			catch (final JaloInvalidParameterException | JaloBusinessException e)
			{
				LOG.error("Exception occured while translating reason Category details.Reason", e);

			}
		}
	}

}
