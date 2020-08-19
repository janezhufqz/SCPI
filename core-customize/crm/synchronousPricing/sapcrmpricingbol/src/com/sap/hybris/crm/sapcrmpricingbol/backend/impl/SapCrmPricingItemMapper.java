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
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogSeverity;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sappricingbol.constants.SappricingbolConstants;
import de.hybris.platform.sap.sappricingbol.converter.ConversionService;
import de.hybris.platform.sap.sappricingbol.enums.PricingProceduresSubtotal;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import com.sap.hybris.crm.sapcrmpricingbol.constants.SapcrmpricingbolConstants;



/**
 * @author C5229090
 *
 *         sets item import parameters to JCo Tables
 */
public class SapCrmPricingItemMapper
{
	static final private Log4JWrapper LOGGER = Log4JWrapper.getInstance(SapCrmPricingItemMapper.class.getName());

	private ModuleConfigurationAccess moduleConfigurationAccess = null;
	private ConversionService conversionService;

	public ConversionService getConversionService()
	{
		return conversionService;
	}

	public void setConversionService(final ConversionService conversionService)
	{
		this.conversionService = conversionService;
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
	 *
	 * @param name
	 * @return property value for name
	 */
	protected String getProperty(final String name)
	{
		final Object propertyValue = getModuleConfigurationAccess().getProperty(name);

		// Some configuration attributes are read as enumeration types, we need to convert them to Strings
		if (propertyValue instanceof PricingProceduresSubtotal)
		{
			return ((PricingProceduresSubtotal) propertyValue).getCode();
		}

		return (String) propertyValue;
	}

	/**
	 * sets catalog item import parameters to JCo Tables
	 *
	 * @param function
	 * @param productModels
	 */
	public void fillImportParameters(final JCoFunction function, final List<ProductModel> productModels)
	{
		LOGGER.entering("fillImportParameters(JCoFunction,ProductModel)");
		LOGGER.trace(LogSeverity.PATH, "SapCrmPricingItemMapper :: fillImportParameters :: Enterted");
		final JCoParameterList tables = function.getTableParameterList();

		final JCoTable itItem = tables.getTable(Config.getParameter(SapcrmpricingbolConstants.JCO_TABLE_PRICE_ITEM));
		fillImportParameters(itItem, productModels);
		tables.setValue(Config.getParameter(SapcrmpricingbolConstants.JCO_TABLE_PRICE_ITEM), itItem);

		final JCoTable itemAttributes = tables
				.getTable(Config.getParameter(SapcrmpricingbolConstants.JCO_TABLE_PRICE_ITEM_ATTRIBUTES));
		fillAttributeParameters(itemAttributes, productModels);
		tables.setValue(Config.getParameter(SapcrmpricingbolConstants.JCO_TABLE_PRICE_ITEM_ATTRIBUTES), itemAttributes);

		final JCoTable timestamps = tables.getTable(Config.getParameter(SapcrmpricingbolConstants.JCO_TABLE_PRICE_TIMESTAMPS));
		fillTimestamps(timestamps, productModels);
		tables.setValue(Config.getParameter(SapcrmpricingbolConstants.JCO_TABLE_PRICE_TIMESTAMPS), timestamps);

		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("crmpricingbol - RFC Item Parameters: ");
			LOGGER.debug(itItem.toString());
		}
		LOGGER.exiting();
	}

	/**
	 * sets the catalog product item data to JCo table
	 *
	 * @param itItem
	 * @param productModels
	 */
	protected void fillImportParameters(final JCoTable itItem, final List<ProductModel> productModels)
	{
		LOGGER.entering("fillImportParameters(JCoTable,ProductModel)");
		LOGGER.trace(LogSeverity.PATH, "SapCrmPricingItemMapper :: fillImportParameters :: Entered");
		for (int i = 0; i < productModels.size(); i++)
		{
			itItem.appendRow();
			itItem.setValue("ITEM_ID", String.valueOf(i + 1));
			itItem.setValue("PRODUCT_ID", productModels.get(i).getGuid());
			itItem.setValue("QUANTITY", productModels.get(i).getPriceQuantity());
			LOGGER.debug("Checking whether sales unit is null : ");
			itItem.setValue("QUANTITY_UNIT",
					productModels.get(i).getUnit() != null
							? getConversionService().getSAPUnitforISO(productModels.get(i).getUnit().getCode())
							: "");

		}
	}

	/**
	 * sets the catalog product item attributes data to Jco table
	 *
	 * @param itemAttributes
	 * @param productModels
	 */
	protected void fillAttributeParameters(final JCoTable itemAttributes, final List<ProductModel> productModels)
	{
		LOGGER.entering("fillAttributeParameters(JCoTable,ProductModel)");
		LOGGER.trace(LogSeverity.PATH, "SapCrmPricingItemMapper :: fillAttributeParameters :: Entered");
		for (int i = 0; i < productModels.size(); i++)
		{
			itemAttributes.appendRows(2);
			itemAttributes.setValue("ITEM_ID", String.valueOf(i + 1));
			itemAttributes.setValue("FIELDNAME", Config.getParameter(SapcrmpricingbolConstants.JCO_PRODUCT));
			itemAttributes.setValue("FIELDVALUE", productModels.get(i).getGuid());

			itemAttributes.nextRow();
			itemAttributes.setValue("ITEM_ID", String.valueOf(i + 1));
			itemAttributes.setValue("FIELDNAME", Config.getParameter(SapcrmpricingbolConstants.JCO_PRICE_INDICATOR_KEY));
			itemAttributes.setValue("FIELDVALUE", Config.getParameter(SapcrmpricingbolConstants.JCO_PRICE_INDICATOR_VALUE));

		}
		LOGGER.exiting();

	}

	/**
	 * sets the catalog product timestamps data to Jco table
	 *
	 * @param timestamps
	 */
	protected void fillTimestamps(final JCoTable timestamps, final List<ProductModel> productModels)
	{
		LOGGER.entering("fillTimestamps(JCoTable, ProductModel)");
		LOGGER.trace(LogSeverity.PATH, "SapCrmPricingItemMapper :: fillTimestamps :: Entered");
		final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
		final String currentDate = format.format(new Date());
		for (int i = 0; i < productModels.size(); i++)
		{
			timestamps.appendRows(4);
			timestamps.setValue("ITEM_ID", String.valueOf(i + 1));
			timestamps.setValue("FIELDNAME", Config.getParameter(SapcrmpricingbolConstants.JCO_PRT_DELIVERYDATE));
			timestamps.setValue("TIMESTAMP", currentDate);

			timestamps.nextRow();
			timestamps.setValue("ITEM_ID", String.valueOf(i + 1));
			timestamps.setValue("FIELDNAME", Config.getParameter(SapcrmpricingbolConstants.JCO_PRT_ORDERDATE));
			timestamps.setValue("TIMESTAMP", currentDate);

			timestamps.nextRow();
			timestamps.setValue("ITEM_ID", String.valueOf(i + 1));
			timestamps.setValue("FIELDNAME", Config.getParameter(SapcrmpricingbolConstants.JCO_PRICE_DATE));
			timestamps.setValue("TIMESTAMP", currentDate);

			timestamps.nextRow();
			timestamps.setValue("ITEM_ID", String.valueOf(i + 1));
			timestamps.setValue("FIELDNAME", Config.getParameter(SapcrmpricingbolConstants.JCO_TAX_DATE));
			timestamps.setValue("TIMESTAMP", currentDate);

		}
		LOGGER.exiting();
	}

	/**
	 * sets cart item imports to JCo tables
	 *
	 * @param order
	 * @param function
	 */
	public void fillImportParameters(final AbstractOrderModel order, final JCoFunction function)
	{
		LOGGER.entering("fillImportParameters(AbstractOrderModel, JCoFunction, ConversionService)");
		LOGGER.trace(LogSeverity.PATH, "SapCrmPricingItemMapper :: fillImportParameters :: Entered");
		final JCoParameterList tables = function.getTableParameterList();
		final JCoTable itItem = tables.getTable(Config.getParameter(SapcrmpricingbolConstants.JCO_TABLE_PRICE_ITEM));

		final JCoTable itemAttributes = tables
				.getTable(Config.getParameter(SapcrmpricingbolConstants.JCO_TABLE_PRICE_ITEM_ATTRIBUTES));



		final JCoTable timestamps = tables.getTable(Config.getParameter(SapcrmpricingbolConstants.JCO_TABLE_PRICE_TIMESTAMPS));


		for (final AbstractOrderEntryModel orderEntryModel : order.getEntries())
		{
			fillImportParameters(itItem, orderEntryModel);
			fillAttributeParameters(itemAttributes, orderEntryModel);
			fillTimestamps(timestamps, orderEntryModel);
		}
		tables.setValue(Config.getParameter(SapcrmpricingbolConstants.JCO_TABLE_PRICE_ITEM), itItem);
		tables.setValue(Config.getParameter(SapcrmpricingbolConstants.JCO_TABLE_PRICE_ITEM_ATTRIBUTES), itemAttributes);
		tables.setValue(Config.getParameter(SapcrmpricingbolConstants.JCO_TABLE_PRICE_TIMESTAMPS), timestamps);
		LOGGER.exiting();
	}

	/**
	 * sets the cart products item data into JCo table
	 *
	 * @param itItem
	 * @param orderEntryModel
	 */
	protected void fillImportParameters(final JCoTable itItem, final AbstractOrderEntryModel orderEntryModel)
	{
		LOGGER.entering("fillImportParameters(JCoTable, AbstractOrderEntryModel)");
		LOGGER.trace(LogSeverity.PATH, "SapCrmPricingItemMapper :: fillImportParameters :: Entered");
		final String itemId = StringUtils.leftPad(String.valueOf(orderEntryModel.getEntryNumber().intValue() + 1), 32,
				"0"); // left padded to fill double byte RAW data type
		itItem.appendRow();
		itItem.setValue("ITEM_ID", itemId);
		itItem.setValue("PRODUCT_ID", orderEntryModel.getProduct().getGuid());
		itItem.setValue("QUANTITY", orderEntryModel.getQuantity());
		LOGGER.debug("Checking whether sales unit is null : ");
		itItem.setValue("QUANTITY_UNIT",
				orderEntryModel.getUnit() != null ? getConversionService().getSAPUnitforISO(orderEntryModel.getUnit().getCode())
						: "");
		LOGGER.exiting();
	}

	/**
	 * sets the cart products item attributes data to Jco table
	 *
	 * @param itemAttributes
	 * @param orderEntryModel
	 */
	protected void fillAttributeParameters(final JCoTable itemAttributes, final AbstractOrderEntryModel orderEntryModel)
	{
		LOGGER.entering("fillAttributeParameters(JCoTable, AbstractOrderEntryModel)");
		LOGGER.trace(LogSeverity.PATH, "SapCrmPricingItemMapper :: fillAttributeParameters :: Entered");
		final String itemId = StringUtils.leftPad(String.valueOf(orderEntryModel.getEntryNumber().intValue() + 1), 32,
				"0"); // left padded to fill double byte RAW data type
		itemAttributes.appendRows(2);
		itemAttributes.setValue("ITEM_ID", itemId);
		itemAttributes.setValue("FIELDNAME", Config.getParameter(SapcrmpricingbolConstants.JCO_PRODUCT));
		itemAttributes.setValue("FIELDVALUE", orderEntryModel.getProduct().getGuid());

		itemAttributes.nextRow();
		itemAttributes.setValue("ITEM_ID", itemId);
		itemAttributes.setValue("FIELDNAME", Config.getParameter(SapcrmpricingbolConstants.JCO_PRICE_INDICATOR_KEY));
		itemAttributes.setValue("FIELDVALUE", Config.getParameter(SapcrmpricingbolConstants.JCO_PRICE_INDICATOR_VALUE));
		LOGGER.exiting();
	}

	/**
	 * sets the cart product timestamps data to Jco table
	 *
	 * @param timestamps
	 */
	protected void fillTimestamps(final JCoTable timestamps, final AbstractOrderEntryModel orderEntryModel)
	{
		LOGGER.entering("fillTimestamps(JCoTable, AbstractOrderEntryModel)");
		LOGGER.trace(LogSeverity.PATH, "SapCrmPricingItemMapper :: fillTimestamps :: Entered");
		final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
		final String currentDate = format.format(new Date());
		final String itemId = StringUtils.leftPad(String.valueOf(orderEntryModel.getEntryNumber().intValue() + 1), 32,
				"0"); // left padded to fill double byte RAW data type
		timestamps.appendRows(4);
		timestamps.setValue("ITEM_ID", itemId);
		timestamps.setValue("FIELDNAME", Config.getParameter(SapcrmpricingbolConstants.JCO_PRT_DELIVERYDATE));
		timestamps.setValue("TIMESTAMP", currentDate);

		timestamps.nextRow();
		timestamps.setValue("ITEM_ID", itemId);
		timestamps.setValue("FIELDNAME", Config.getParameter(SapcrmpricingbolConstants.JCO_PRT_ORDERDATE));
		timestamps.setValue("TIMESTAMP", currentDate);

		timestamps.nextRow();
		timestamps.setValue("ITEM_ID", itemId);
		timestamps.setValue("FIELDNAME", Config.getParameter(SapcrmpricingbolConstants.JCO_PRICE_DATE));
		timestamps.setValue("TIMESTAMP", currentDate);

		timestamps.nextRow();
		timestamps.setValue("ITEM_ID", itemId);
		timestamps.setValue("FIELDNAME", Config.getParameter(SapcrmpricingbolConstants.JCO_TAX_DATE));
		timestamps.setValue("TIMESTAMP", currentDate);

		LOGGER.exiting();
	}

	/**
	 * Read catalog result prices
	 *
	 * @param resultTable
	 * @return price for the catalog
	 */
	public List<PriceInformation> readPrices(final JCoTable resultTable)
	{
		LOGGER.entering("readPrices(JCoTable)");
		LOGGER.trace(LogSeverity.PATH, "SapCrmPricingItemMapper :: readPrices :: Entered");
		if (!resultTable.isEmpty())
		{

			final List<PriceInformation> result = new ArrayList<PriceInformation>(resultTable.getNumRows());

			for (int i = 0; i < resultTable.getNumRows(); i++)
			{
				resultTable.setRow(i);
				final double priceBeforeDiscount = Math.abs(readPrice(resultTable));

				final double discount = 0;


				final Double quantity = Double.valueOf(Double.parseDouble("1"));

				// calculate discounted price
				final double price = (priceBeforeDiscount - discount) / quantity.doubleValue();


				final PriceValue priceValue = new PriceValue((String) resultTable.getValue("DOCUMENT_CURRENCY_UNIT"), price, true);


				// Add qualifiers
				final Map<String, Long> qualifiers = new HashMap<String, Long>();
				qualifiers.put(PriceRow.MINQTD, Long.valueOf(quantity.intValue()));

				final PriceInformation priceInformation = new PriceInformation(qualifiers, priceValue);
				result.add(priceInformation);

			}

			return result;
		}

		return Collections.emptyList();
	}

	/**
	 * sets price for the products in cart based on their itemId.
	 *
	 * @param order
	 * @param resultTable
	 */
	public void readPrices(final AbstractOrderModel order, final JCoTable resultTable)
	{
		LOGGER.trace(LogSeverity.PATH, "SapCrmPricingItemMapper :: readPrices :: Entered");
		LOGGER.trace(LogSeverity.PATH, "SapCrmPricingItemMapper :: fillImportParameters :: Entered");
		if (resultTable != null && !resultTable.isEmpty())
		{
			final String currencyIsoCode = resultTable.getString("DOCUMENT_CURRENCY_UNIT");
			LOGGER.debug("currency code from crm : " + currencyIsoCode);
			/**
			 * Implementation added to rectify price swaping in hybris cart when pricing request is made to CRM.
			 */

			double deliveryCosts = 0.00;
			for (int i = 0; i < resultTable.getNumRows(); i++)
			{
				resultTable.setRow(i);
				final byte[] itemId = resultTable.getByteArray("ITEM_ID");
				int orderEntry = 0;
				if (null != itemId)
				{
					orderEntry = computeOrderEntry(itemId) - 1;
				}
				final long quantity = order.getEntries().get(orderEntry).getQuantity().longValue();
				final double entryPrice = readPrice(resultTable);
				order.getEntries().get(orderEntry).setBasePrice(Double.valueOf(entryPrice / quantity));

				final double entryTax = readTax(resultTable);
				final double tax = entryTax / quantity;
				final TaxValue taxValue = new TaxValue(generateCode("TAX", order.getEntries().get(orderEntry), order), tax, true, tax,
						currencyIsoCode);
				order.getEntries().get(orderEntry).setTaxValues(Arrays.asList(taxValue));

				// retrieve delivery costs
				final double deliveryCost = readDeliveryCost(resultTable);
				// sum all entries delivery costs
				deliveryCosts += deliveryCost;
				if (order.getDeliveryMode() != null)
				{
					order.setDeliveryCost(Double.valueOf(deliveryCosts));
				}
			}
		}
	}

	/**
	 * @param itemId
	 * @return orderentry for the itemId
	 */
	protected int computeOrderEntry(final byte[] itemId)
	{
		final int orderEntry = new BigInteger(DatatypeConverter.printHexBinary(itemId)).intValue();
		LOGGER.debug("Computed OrderEntry value = " + orderEntry);
		return orderEntry;
	}


	/**
	 * generate a code string from order number and order entry number
	 *
	 * @return
	 */
	protected String generateCode(final String prefix, final AbstractOrderEntryModel entry, final AbstractOrderModel order)
	{
		return prefix + entry.getEntryNumber() + order.getCode();

	}

	/**
	 *
	 * @param itemTable
	 * @return the tax value
	 */
	protected double readTax(final JCoTable itemTable)
	{
		final double entryTax = itemTable.getDouble("TAX_VALUE");
		return entryTax;
	}

	/**
	 *
	 * @param itemTable
	 * @return gross value
	 */
	protected double readPrice(final JCoTable itemTable)
	{
		final String priceSetting = getProperty(SappricingbolConstants.CONF_PROP_PRICE_SUBTOTAL);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("SAP CRM Price settings :: "
					+ (priceSetting == null ? "Not maintained. Defaulting to NET PRICE FREIGHTLESS" : priceSetting));
		}
		if (priceSetting == null || "NETWR".equalsIgnoreCase(priceSetting))
		{
			return itemTable.getDouble("NET_VALUE_FREIGHTLESS");
		}
		return itemTable.getDouble("NET_VALUE");
	}


	protected double readDeliveryCost(final JCoTable itemTable)
	{
		final double deliveryCost = itemTable.getDouble("FREIGHT");
		return deliveryCost;
	}


}
