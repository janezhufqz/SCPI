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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm.strategy;

import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.core.bol.backend.jco.JCoHelper;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionHelper;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Schedline;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.CustomizingHelper;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang.StringUtils;

import com.sap.conn.jco.JCoTable;
import com.sap.hybris.crm.sapcrmordermgmtbol.constants.SapcrmordermgmtbolConstants;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.BackendState;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.CRMConstants;
import com.sap.tc.logging.Severity;

/**
 * Class is responsible to map item (cart entry, oder entry) information between
 * LO-API and the BOL layer. Used for reading and writing
 */
public class ItemMapper extends BaseMapper {

	/** The product service. */
	@Autowired
	protected ProductService productService;

	/** ID of LO-API item segment. */
	public static final String OBJECT_ID_ITEM = "ITEM";

	/** Logging instance. */
	public static final Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(ItemMapper.class.getName());

	/** Empty string. */
	protected static String EMPTY_STRING = "";

	/** text control constant. */
	public static final String LF = "\n";

	/** The Constant sdf. */
	public static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	/** Factory to access SAP session beans. */
	protected GenericFactory genericFactory = null;

	/** Converter BO. */
	protected Converter converter;

	/**
	 * Sets the converter.
	 *
	 * @param converter
	 *            the new converter
	 */
	public void setConverter(final Converter converter) {
		this.converter = converter;
	}

	/**
	 * Bean constructor.
	 * <p>
	 *
	 * @param genericFactory
	 *            Factory to access SAP session beans
	 */
	public void setGenericFactory(final GenericFactory genericFactory) {
		this.genericFactory = genericFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend
	 * .impl.crm.strategy.BaseMapper#init()
	 */
	@Override
	public void init() {
		/* nothing to initialize */
	}

	/**
	 * Reading item information from LO-API. The first 4 parameters are JCO
	 * tables containing item related information
	 *
	 * Error structure
	 *
	 * @param etItem
	 *            the et item
	 * @param etScheduleLines
	 *            the et schedule lines
	 * @param shop
	 *            Customizing
	 *
	 *            Map of item keys
	 *
	 *            Map of BOL items
	 * @param backendState
	 *            the backend state
	 * @param readParams
	 *            the read params
	 * @param salesDoc
	 *            itemMapper.read(etItem,shop,backendState, readParams,
	 *            salesDocument);
	 */
	public void read(final JCoTable etItem, final JCoTable etScheduleLines,
			final TransactionConfiguration shop,
			final BackendState backendState, final SalesDocument salesDoc) {
		final ItemList itemList = salesDoc.getItemList();
		itemList.clear();
		final String currency = salesDoc.getHeader().getCurrency();
		final int numberOfDecimals = CustomizingHelper.getNumberOfDecimals(
				converter, currency);

		for (int i = 0; i < etItem.getNumRows(); i++) {
			etItem.setRow(i);

			if (isItemToBeRead(etItem)) {
				final Item item = salesDoc.createItem();

				BigDecimal netWOFreight = etItem
						.getBigDecimal("NET_WO_FREIGHT");
				netWOFreight = ConversionHelper.adjustCurrencyDecimalPoint(
						netWOFreight, numberOfDecimals);
				item.setNetValueWOFreight(netWOFreight);
				
				BigDecimal netPriceWOFreight = etItem
						.getBigDecimal("NET_WO_FREIGHT");
				netPriceWOFreight = ConversionHelper.adjustCurrencyDecimalPoint(
						netPriceWOFreight, numberOfDecimals);
				item.setNetPriceWOFreight(netPriceWOFreight);

				
				item.setCurrency(currency);
				BigDecimal grossValue = etItem.getBigDecimal("GROSS_VALUE");
				grossValue = ConversionHelper.adjustCurrencyDecimalPoint(
						grossValue, numberOfDecimals);
				item.setGrossValue(grossValue);

				BigDecimal quantity = etItem
						.getBigDecimal(CRMConstants.QUANTITY);
				if (quantity == null) {
					item.setQuantity(BigDecimal.ONE);
				} else {
					quantity = quantity.setScale(2, BigDecimal.ROUND_UP);
					item.setQuantity(quantity);
				}

				final int itemNumberFromLOAPI = etItem
						.getInt(CRMConstants.NUMBER_INT);
				item.setNumberInt(itemNumberFromLOAPI);

				item.setTechKey(new TechKey(JCoHelper.getString(etItem,
						CRMConstants.FIELD_GUID)));

				item.setProductGuid(new TechKey(JCoHelper.getString(etItem,
						CRMConstants.PRODUCT_GUID)));
				item.setProductId(JCoHelper.getString(etItem,
						CRMConstants.PRODUCT));

				// Req Delivery Date Read
				final String reqDelvDate = etItem
						.getString("REQ_DELIVERY_DATE");
				setReqDeliveryDate(item, reqDelvDate);

				readScheduleLines(item, etScheduleLines, etItem);

				itemList.add(item);
			}
		}
	}

	private void setReqDeliveryDate(final Item item, final String reqDelvDate) {
		if (!StringUtils.isEmpty(reqDelvDate)) {

			final Date formatedDate = convertCrmToHybrisDate(reqDelvDate);
			if (formatedDate != null) {
				item.setReqDeliveryDate(formatedDate);
			}

		}
	}

	/**
	 * Checks if is item to be read.
	 *
	 * @param etIte
	 *            the et ite
	 * @return true, if is item to be read
	 */
	protected boolean isItemToBeRead(final JCoTable etIte) {
		// empty but can be used to filter e.g. subItems etc.
		return true;
	}

	/**
	 * Helper to fill item table before a LO-API update is done.
	 *
	 * @param salesDoc
	 *            The sales document
	 * @param itemsToBeChanged
	 *            the items to be changed
	 * @param salesDocR3Lrd
	 *            the sales doc r3 lrd
	 * @param itemComV
	 *            Item data (values)
	 * @param itemComX
	 *            Item data (change flag)
	 * @throws BackendException
	 *             the backend exception
	 */

	public void write(final SalesDocument salesDoc,
			final Set<String> itemsToBeChanged,
			final BackendState salesDocR3Lrd, final JCoTable itemComV,
			final JCoTable itemComX) throws BackendException {

		final Iterator<Item> it = salesDoc.iterator();

		while (it.hasNext()) {
			final Item item = it.next();

			// is itemToBeSent
			if (isItemToBeSent(item, itemsToBeChanged)) {
				writeItemInSalesDoc(item, itemComV, itemsToBeChanged, itemComX,
						salesDocR3Lrd, salesDoc);
			} else {
				sapLogger.debug("We don't send item: "
						+ item.getTechKey().getIdAsString() + " ProductID: "
						+ item.getProductId());
			}
		}
	}

	/**
	 * Checks if is item to be sent.
	 *
	 * @param item
	 *            the item
	 * @param itemsToBeChanged
	 *            the items to be changed
	 * @return true, if is item to be sent
	 */
	protected boolean isItemToBeSent(final Item item,
			final Set<String> itemsToBeChanged) {
		boolean toBeSend = false;

		if (TechKey.isEmpty(item.getTechKey())) {
			// always send new items but do not send subitems
			if (null == item.getParentHandle()
					|| item.getParentHandle().isEmpty()) {
				toBeSend = true;
			}
		} else if (itemsToBeChanged.contains(item.getTechKey().getIdAsString())
				&& TechKey.isEmpty(item.getParentId())) {
			// always send changed items but do not send subitems
			toBeSend = true;
		}
		return toBeSend;

	}

	/**
	 * Helper to fill item table before a LO-API update is done.
	 *
	 * @param item
	 *            The Item to Add or Remove or Update
	 * @param itemComV
	 *            Item data (values)
	 * @param itemsToBeChanged
	 *            the items to be changed
	 * @param itemComX
	 *            Item data (change flag)
	 * @param salesDocR3Lrd
	 *            the sales doc r3 lrd
	 * @param salesDoc
	 *            the sales doc
	 */

	protected void writeItemInSalesDoc(final Item item,
			final JCoTable itemComV, final Set<String> itemsToBeChanged,
			final JCoTable itemComX, final BackendState salesDocR3Lrd,
			final SalesDocument salesDoc) {

		itemComV.appendRow();
		if (TechKey.isEmpty(item.getTechKey())) {
			// handle new item
			writeNewItem(item, itemComV, itemComX);

		}
		// item to be deleted
		else if (item.getProductId() == null || item.getProductId().isEmpty()) {
			// deleted item
			writeDeletedItem(item, itemComV, itemComX);

			// in case of deletion we can leave the further processing for this
			// item
			return;
		} else if (itemsToBeChanged.contains(item.getTechKey().getIdAsString())) {
			// changed item
			writeChangedItem(item, itemComV, itemComX);
		} else {
			// should never happen

		}

		/*
		 * Set item number to force items to stay in SD session even if a
		 * erroneous item has been corrected through initialising its material
		 * number
		 */

		setJcoHelper(item, itemComV, itemComX);

		salesDocR3Lrd.removeMessageFromMessageList(item.getTechKey(),
				"b2b.r3lrd.quantityerror");

		if (null == item.getQuantity()) {
			sapLogger.debug("Given quantity was not valid");

			final Message msg = new Message(Message.ERROR,
					"b2b.r3lrd.quantityerror", null, "");
			salesDocR3Lrd.getOrCreateMessageList(item.getTechKey()).add(msg);
			item.addMessage(msg);
		}

		itemComV.setValue(CRMConstants.QUANTITY, item.getQuantity());

		itemComX.appendRow();
		JCoHelper.setValue(itemComX, item.getTechKey(), CRMConstants.REF_GUID);
		JCoHelper.setValue(itemComX, item.getHandle(), CRMConstants.REF_HANDLE);
		JCoHelper.setValue(itemComX, CRMConstants.QUANTITY,
				CRMConstants.FIELD_NAME);

		writeItemQtyProdUnit(item, itemComV, itemComX);

		// The cancellation of items is transferred as rejection
		// code,
		// which
		// is maintained in the shop
		writeItemRejStatus(item, itemComV, itemComX);

		// Requested Delivery Date
		if (item.getReqDeliveryDate() == null
				&& ((salesDoc.getHeader().getReqDeliveryDate() != null) && (!salesDoc
						.getHeader().getReqDeliveryDate()
						.equals(SapcrmordermgmtbolConstants.DATE_INITIAL)))) {

			item.setReqDeliveryDate(salesDoc.getHeader().getReqDeliveryDate());
		}

		writeReqDlvItemDate(item, itemComV, itemComX);

		printDebugOutput(item, itemComV);

	}

	private void setJcoHelper(final Item item, final JCoTable itemComV,
			final JCoTable itemComX) {
		if (item.getProductId() != null && !item.getProductId().isEmpty()) {
			JCoHelper.setValue(itemComV, item.getProductId(),
					CRMConstants.PRODUCT);

			itemComX.appendRow();
			JCoHelper.setValue(itemComX, item.getTechKey(),
					CRMConstants.REF_GUID);
			JCoHelper.setValue(itemComX, item.getHandle(),
					CRMConstants.REF_HANDLE);
			JCoHelper.setValue(itemComX, CRMConstants.PRODUCT,
					CRMConstants.FIELD_NAME);
		}
	}

	/**
	 * Write deleted item.
	 *
	 * @param item
	 *            the item
	 * @param itemComV
	 *            the item com v
	 * @param itemComX
	 *            the item com x
	 */
	private void writeDeletedItem(final Item item, final JCoTable itemComV,
			final JCoTable itemComX) {
		JCoHelper
				.setValue(itemComV, item.getTechKey(), CRMConstants.FIELD_GUID);
		itemComX.appendRow();
		JCoHelper.setValue(itemComX, item.getTechKey().getIdAsString(),
				CRMConstants.REF_GUID);
		JCoHelper.setValue(itemComX, CRMConstants.FIELD_GUID,
				CRMConstants.FIELD_NAME);

		JCoHelper.setValue(itemComV, "D", "MODE");
		itemComX.appendRow();
		JCoHelper.setValue(itemComX, item.getTechKey(), CRMConstants.REF_GUID);
		JCoHelper.setValue(itemComX, "MODE", CRMConstants.FIELD_NAME);
	}

	/**
	 * Write changed item.
	 *
	 * @param item
	 *            the item
	 * @param itemComV
	 *            the item com v
	 * @param itemComX
	 *            the item com x
	 */
	protected void writeChangedItem(final Item item, final JCoTable itemComV,
			final JCoTable itemComX) {
		JCoHelper.setValue(itemComV, item.getHandle(),
				CRMConstants.FIELD_HANDLE);
		itemComX.appendRow();
		JCoHelper.setValue(itemComX, item.getHandle(), CRMConstants.REF_HANDLE);
		JCoHelper.setValue(itemComX, item.getTechKey().getIdAsString(),
				CRMConstants.REF_GUID);
		JCoHelper.setValue(itemComX, CRMConstants.FIELD_HANDLE,
				CRMConstants.FIELD_NAME);

		JCoHelper.setValue(itemComV, item.getTechKey().getIdAsString(),
				CRMConstants.FIELD_GUID);
		itemComX.appendRow();
		JCoHelper.setValue(itemComX, item.getHandle(), CRMConstants.REF_HANDLE);
		JCoHelper.setValue(itemComX, item.getTechKey().getIdAsString(),
				CRMConstants.REF_GUID);
		JCoHelper.setValue(itemComX, CRMConstants.FIELD_GUID,
				CRMConstants.FIELD_NAME);

		JCoHelper.setValue(itemComV, "B", "MODE");
		itemComX.appendRow();
		JCoHelper.setValue(itemComX, item.getHandle(), CRMConstants.REF_HANDLE);
		JCoHelper.setValue(itemComX, item.getTechKey(), CRMConstants.REF_GUID);
		JCoHelper.setValue(itemComX, "MODE", CRMConstants.FIELD_NAME);
	}

	/**
	 * Write item qty prod unit.
	 *
	 * @param item
	 *            the item
	 * @param itemComV
	 *            the item com v
	 * @param itemComX
	 *            the item com x
	 */
	protected void writeItemQtyProdUnit(final Item item,
			final JCoTable itemComV, final JCoTable itemComX) {
		if (StringUtils.isEmpty(item.getUnit())) {
			if (!StringUtils.isEmpty(item.getProductId())) {

				JCoHelper.setValue(itemComV,
						productService.getProductForCode(item.getProductId())
								.getUnit().getSapCode(),
						CRMConstants.PROCESS_QTY_UNIT);
			}
		} else {
			JCoHelper.setValue(itemComV, item.getUnit(),
					CRMConstants.PROCESS_QTY_UNIT);
		}

		itemComX.appendRow();
		JCoHelper.setValue(itemComX, item.getTechKey(), CRMConstants.REF_GUID);
		JCoHelper.setValue(itemComX, item.getHandle(), CRMConstants.REF_HANDLE);
		JCoHelper.setValue(itemComX, CRMConstants.PROCESS_QTY_UNIT,
				CRMConstants.FIELD_NAME);
	}

	/**
	 * Prints the debug output.
	 *
	 * @param item
	 *            the item
	 * @param itemComV
	 *            the item com v
	 */
	private void printDebugOutput(final Item item, final JCoTable itemComV) {
		if (sapLogger.isDebugEnabled()) {
			final StringBuilder debugOutput = new StringBuilder(
					"\nOBJINST sent to CRM: ");
			debugOutput.append("\nHandle          : "
					+ itemComV.getString(CRMConstants.FIELD_HANDLE));
			debugOutput.append(" \n");
			debugOutput.append("\nHandle   : " + item.getHandle());
			debugOutput.append("\nObject Id: " + "ITEM");
			debugOutput.append(" \n");
			sapLogger.debug(debugOutput);
		}
	}

	/**
	 * Write req dlv item date.
	 *
	 * @param item
	 *            the item
	 * @param itemComV
	 *            the item com v
	 * @param itemComX
	 *            the item com x
	 */
	protected void writeReqDlvItemDate(final Item item,
			final JCoTable itemComV, final JCoTable itemComX) {
		final Date reqDlvDate = item.getReqDeliveryDate();
		if (reqDlvDate != null) {
			final String formatedReqDate = convertHybrisToCrmDate(reqDlvDate);
			if (formatedReqDate != null) {
				JCoHelper.setValue(itemComV, formatedReqDate,
						"REQ_DELIVERY_DATE");
				itemComX.appendRow();
				JCoHelper.setValue(itemComX, item.getTechKey(),
						CRMConstants.REF_GUID);
				JCoHelper.setValue(itemComX, item.getHandle(),
						CRMConstants.REF_HANDLE);
				JCoHelper.setValue(itemComX, "REQ_DELIVERY_DATE",
						CRMConstants.FIELD_NAME);
			}
		}
	}

	/**
	 * Write item rej status.
	 *
	 * @param item
	 *            the item
	 * @param itemComV
	 *            the item com v
	 * @param itemComX
	 *            the item com x
	 */
	protected void writeItemRejStatus(final Item item, final JCoTable itemComV,
			final JCoTable itemComX) {
		if (item.getOverallStatus().isCancelled()) {
			final String rejectionCode = item.getRejectionCode();

			JCoHelper.setValue(itemComV, rejectionCode, "REJECTION");

			itemComX.appendRow();
			JCoHelper.setValue(itemComX, item.getTechKey(),
					CRMConstants.REF_GUID);
			JCoHelper.setValue(itemComX, item.getHandle(),
					CRMConstants.REF_HANDLE);
			JCoHelper.setValue(itemComX, "REJECTION", CRMConstants.FIELD_NAME);
		}
	}

	/**
	 * Write new item.
	 *
	 * @param item
	 *            the item
	 * @param itemComV
	 *            the item com v
	 * @param itemComX
	 *            the item com x
	 */
	protected void writeNewItem(final Item item, final JCoTable itemComV,
			final JCoTable itemComX) {

		if (TechKey.isEmpty(item.getTechKey())) {
			// its a new item
			if (item.getHandle().isEmpty()) {
				item.createUniqueHandle();
			}
			// add item to Jco table
			JCoHelper.setValue(itemComV, item.getHandle(),
					CRMConstants.FIELD_HANDLE);
			itemComX.appendRow();
			JCoHelper.setValue(itemComX, item.getHandle(),
					CRMConstants.REF_HANDLE);
			JCoHelper.setValue(itemComX, CRMConstants.FIELD_HANDLE,
					CRMConstants.FIELD_NAME);

			JCoHelper.setValue(itemComV, "A", "MODE");
			itemComX.appendRow();
			JCoHelper.setValue(itemComX, item.getHandle(),
					CRMConstants.REF_HANDLE);
			JCoHelper.setValue(itemComX, "MODE", CRMConstants.FIELD_NAME);
		}
	}

	/**
	 * Read schedule lines.
	 *
	 * @param item
	 *            the item
	 * @param etScheduleLines
	 *            the et schedule lines
	 * @param etIte
	 *            the et ite
	 */
	protected void readScheduleLines(final Item item,
			final JCoTable etScheduleLines, final JCoTable etIte) {
		final List<Schedline> aList = item.getScheduleLines();
		final String itemGuid = item.getTechKey().getIdAsString();
		for (int j = 0; j < etScheduleLines.getNumRows(); j++) {
			etScheduleLines.setRow(j);
			final String schedLineItemGuid = JCoHelper.getString(
					etScheduleLines, "ITEM_GUID");
			final String eventType = JCoHelper.getString(etScheduleLines,
					"EVENT_TYPE");
			if (itemGuid.equals(schedLineItemGuid)
					&& "CONFIRMED".equals(eventType)) {
				final Schedline sLine = item.createScheduleLine();
				sLine.setTechKey(new TechKey(JCoHelper.getString(etIte, "GUID")));
				sLine.setUnit(JCoHelper.getString(etIte, "NETPR_UOM"));
				final Date date = convertCrmToHybrisDate(etScheduleLines
						.getString("FROM_TIME"));
				if (date != null) {
					sLine.setCommittedDate(date);
				}
				final BigDecimal committedQuantity = new BigDecimal(
						JCoHelper.getString(etScheduleLines,
								CRMConstants.QUANTITY));
				sLine.setCommittedQuantity(committedQuantity);
				aList.add(sLine);
			}
		}
		item.setScheduleLines(aList);
	}

	/**
	 * Convert crm to hybris date.
	 *
	 * @param date
	 *            the date
	 * @return the date
	 */
	protected Date convertCrmToHybrisDate(final String date) {
		Date convertedDate = null;
		if (StringUtils.isNotBlank(date)) {

			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			try {
				convertedDate = sdf.parse(date);
			} catch (final ParseException e) {
				sapLogger.log(Severity.ERROR, LogCategories.APPLICATIONS,
						"Date not parsable Item Mapper");
			}
		}
		return convertedDate;
	}

	/**
	 * Convert hybris to crm date.
	 *
	 * @param date
	 *            the date
	 * @return the string
	 */
	protected String convertHybrisToCrmDate(final Date date) {
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String convertedDate = null;
		convertedDate = sdf.format(date);
		return convertedDate;
	}

}
