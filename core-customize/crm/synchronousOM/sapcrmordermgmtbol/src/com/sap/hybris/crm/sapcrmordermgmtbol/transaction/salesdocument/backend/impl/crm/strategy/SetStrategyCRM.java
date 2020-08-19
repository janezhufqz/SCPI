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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.util.StringUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.hybris.crm.sapcrmordermgmtbol.constants.SapcrmordermgmtbolConstants;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.BackendState;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.CRMConstants;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.strategy.SetStrategy;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult.Result;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * Strategy for function module <br>
 * CRM_LORD_SET</br> in the CRM. This class consists only of static method. Each
 * of theses methods wraps the function module. The purpose of this class is to
 * maintain only one implementation of the logic necessary to call this function
 * module via jco using data provided by Java objects. <br>
 *
 * @version 1.0
 */
public class SetStrategyCRM extends BaseStrategyCRM implements SetStrategy,
		CRMConstants {

	/**
     *
     */
	private static final String ES_HEADER = "ES_HEADER";

	private static final Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(SetStrategyCRM.class.getName());

	@Resource
	private CommonI18NService commonI18NService;

	/**
	 * Strategy for CRM_LORD_SET, all values found in the provided sales
	 * document are written to the backend system. If you want a field to be
	 * ignored set the corresponding value to <code>null</code>.
	 *
	 * @param backendState
	 *            The SalesDocumentR3Lrd object instance
	 * @param salesDoc
	 *            The sales document
	 * @param transactionConfig
	 *            the shop object
	 * @param jcoConnection
	 *            Connection to use
	 * @param itemNewShipTos
	 *            The list of item tech keys, for which a new ship to should be
	 *            created
	 * @param onlyUpdateHeader
	 *            boolean, which indicates, if only header of the object should
	 *            be updated
	 * @return call result.
	 */
	@Override
	public BackendCallResult execute(final BackendState backendState,
			final SalesDocument salesDoc,
			final Map<String, Item> itemsCRMStatus,
			final TransactionConfiguration transactionConfig,
			final JCoConnection jcoConnection,
			final List<String> itemNewShipTos, final boolean onlyUpdateHeader)
			throws BackendException {
		final String methodName = "execute()";

		final HeaderMapper headerMapper = (HeaderMapper) genericFactory
				.getBean(getHeaderMapperBean());
		final ItemMapper itemMapper = (ItemMapper) genericFactory
				.getBean(getItemMapperBean());
		final PartnerMapper partnerMapper = (PartnerMapper) genericFactory
				.getBean(getPartnerMapperBean());

		sapLogger.entering(methodName);

		try {
			final JCoFunction jcoFunction = jcoConnection
					.getFunction(getOrderSetFunctionModule());

			populateHeaderImportParams(jcoFunction, headerMapper, salesDoc,
					backendState, transactionConfig);
			if (itemNewShipTos == null && !onlyUpdateHeader) {
				populateItemMapper(backendState, salesDoc, itemsCRMStatus,
						transactionConfig, itemMapper, jcoFunction);
			}

			setTableParams(jcoFunction);

			execute(jcoConnection, jcoFunction);
			final JCoParameterList tableParams = jcoFunction
					.getTableParameterList();
			final JCoTable etMessages = tableParams
					.getTable(getErrorMessagesTable());
			final JCoStructure esHeader = jcoFunction.getExportParameterList()
					.getStructure(ES_HEADER);

			// Clear Message First If Any...
			salesDoc.clearMessages();
			salesDoc.getHeader().clearMessages();
			final boolean isError = checkError(etMessages, salesDoc, esHeader);

			if (isError) {
				return new BackendCallResult(Result.failure);
			}
			final MappersDTO mappersDto = new MappersDTO();
			mappersDto.setHeaderMapper(headerMapper);
			mappersDto.setItemMapper(itemMapper);
			mappersDto.setPartnerMapper(partnerMapper);
			return processResult(jcoFunction, salesDoc, mappersDto,
					backendState, transactionConfig, onlyUpdateHeader,
					itemsCRMStatus);

		} catch (final BackendException e) {
			invalidateSalesDocument(salesDoc);
			throw e;
		} finally {
			sapLogger.exiting();
		}
	}

	/**
	 * @return
	 */
	protected String getErrorMessagesTable() {
		return "ET_MESSAGES";
	}

	/**
	 * @param jcoFunction
	 * @param salesDoc
	 * @param headerMapper
	 * @param partnerMapper
	 * @param itemMapper
	 * @param salesDocR3Lrd
	 * @param transactionConfig
	 * @param onlyUpdateHeader
	 */
	protected BackendCallResult processResult(final JCoFunction jcoFunction,
			final SalesDocument salesDoc, final MappersDTO mappersDto,
			final BackendState salesDocR3Lrd,
			final TransactionConfiguration transactionConfig,
			final boolean onlyUpdateHeader,
			final Map<String, Item> itemsCRMStatus) {
		// get export parameter list
		final JCoParameterList exportParams = jcoFunction
				.getExportParameterList();
		final HeaderMapper headerMapper = mappersDto.getHeaderMapper();
		final PartnerMapper partnerMapper = mappersDto.getPartnerMapper();
		final ItemMapper itemMapper = mappersDto.getItemMapper();
		if (!onlyUpdateHeader) {
			salesDoc.setTechKey(new TechKey(exportParams
					.getStructure(ES_HEADER).getString("GUID")));

			final JCoParameterList tableParams = jcoFunction
					.getTableParameterList();
			final Header header = salesDoc.getHeader();
			headerMapper.read(exportParams.getStructure("ES_HEADER"),
					salesDocR3Lrd, salesDoc, header);
			partnerMapper.read(tableParams.getTable("ET_PARTNER"), salesDoc,
					salesDocR3Lrd, header);

			itemMapper.read(tableParams.getTable("ET_ITEM"),
					tableParams.getTable("ET_SCHEDULELINES"),
					transactionConfig, salesDocR3Lrd, salesDoc);
			rePopulateItemBuffer(itemsCRMStatus, salesDoc);
		}

		salesDocR3Lrd.setDocumentInitial(false);

		return new BackendCallResult(Result.success);
	}

	/**
	 * @return
	 */
	protected String getPartnerMapperBean() {
		return SapcrmordermgmtbolConstants.ALIAS_BEAN_PARTNER_MAPPER;
	}

	/**
	 * @return
	 */
	protected String getItemMapperBean() {
		return SapcrmordermgmtbolConstants.ALIAS_BEAN_ITEM_MAPPER;
	}

	/**
	 * @return
	 */
	protected String getHeaderMapperBean() {
		return SapcrmordermgmtbolConstants.ALIAS_BEAN_HEADER_MAPPER;
	}

	/**
	 * @param jcoConnection
	 * @param jcoFunction
	 * @throws BackendException
	 */
	public void execute(final JCoConnection jcoConnection,
			final JCoFunction jcoFunction) throws BackendException {
		jcoConnection.execute(jcoFunction);
	}

	/**
	 * @param jcoFunction
	 */
	public void setTableParams(final JCoFunction jcoFunction) {
		final JCoParameterList tableParams = jcoFunction
				.getTableParameterList();
		final JCoTable table = tableParams.getTable("IT_PARAMETERS");
		table.appendRow();
		table.setValue("PARAMETER", "ES_HEADER");
		table.appendRow();
		table.setValue("PARAMETER", "ET_ITEM");
		table.appendRow();
		table.setValue("PARAMETER", "ET_PARTNER");
		table.appendRow();
		table.setValue("PARAMETER", "ET_STATUS_SYST");
		table.appendRow();
		table.setValue("PARAMETER", "ET_STATUS_USER");
		table.appendRow();
		table.setValue("PARAMETER", "ET_SCHEDULELINES");
		tableParams.setValue("IT_PARAMETERS", table);
	}

	/**
	 * @param salesDocR3Lrd
	 * @param salesDoc
	 * @param itemsCRMStatus
	 * @param shop
	 * @param itemNewShipTos
	 * @param onlyUpdateHeader
	 * @param itemMapper
	 * @param function
	 * @throws BackendException
	 */
	protected void populateItemMapper(final BackendState salesDocR3Lrd,
			final SalesDocument salesDoc,
			final Map<String, Item> itemsCRMStatus,
			final TransactionConfiguration shop, final ItemMapper itemMapper,
			final JCoFunction function) throws BackendException {
		Set<String> itemsToBeChanged;
		// setting the import table basket_item

		final JCoTable itemComV = function.getTableParameterList().getTable(
				"IT_ITEM");
		final JCoTable itemComX = function.getTableParameterList().getTable(
				"IT_FIELDS_ITEM_SET");

		// This Method will be used when next time same item will be added
		// from PDP/PLP
		if (shop.isMergeIdenticalProducts()) {
			mergeIdentialItem(salesDoc);
		}
		// fill items
		// first check which items we need to change
		itemsToBeChanged = findItemsToChange(itemsCRMStatus, salesDoc, shop);

		final List<Item> items = salesDoc.getItemList();
		// remove Item from cart,so need to clear sales doc with tricky
		// logic
		// Preparing sales doc to remove item from backend
		prepareItemToRemove(items, salesDoc);

		itemMapper.write(salesDoc, itemsToBeChanged, salesDocR3Lrd, itemComV,
				itemComX);

	}

	/**
	 * @param jcoFunction
	 * @param headerMapper
	 * @param salesDoc
	 * @param salesDocR3Lrd
	 * @param transactionConfig
	 * @throws BackendException
	 */
	protected void populateHeaderImportParams(final JCoFunction jcoFunction,
			final HeaderMapper headerMapper, final SalesDocument salesDoc,
			final BackendState salesDocR3Lrd,
			final TransactionConfiguration transactionConfig)
			throws BackendException {

		final JCoParameterList importParams = jcoFunction
				.getImportParameterList();
		final JCoParameterList tableParams = jcoFunction
				.getTableParameterList();

		final JCoStructure isHeader = importParams.getStructure("IS_HEADER");

		final JCoTable itFieldsHeader = tableParams
				.getTable("IT_FIELDS_HEADER_SET");

		final JCoStructure isControl = importParams.getStructure("IS_CONTROL");

		isControl.setValue("LANGUAGE", commonI18NService.getCurrentLanguage()
				.getIsocode().toString());
		isControl.setValue("MODE", CRMConstants.BASKET_UPDATE_MODE);

		headerMapper.write(salesDoc.getHeader(), salesDocR3Lrd, isHeader,
				itFieldsHeader, transactionConfig);
		if (sapLogger.isDebugEnabled()) {
			logCall(getOrderSetFunctionModule(), importParams, null);
		}
	}

	/**
	 * prepare Item to remove from backend
	 *
	 * @param items
	 * @param salesDoc
	 */
	protected void prepareItemToRemove(final List<Item> items,
			final SalesDocument salesDoc) {
		for (final Item item : items) {
			if (StringUtils.isEmpty(item.getProductId())) {
				salesDoc.getItemList().clear();
				salesDoc.getItemList().add(item);
				break;
			}
		}

	}

	/**
	 * @param salesDoc
	 */
	protected void mergeIdentialItem(final SalesDocument salesDoc) {
		int i = 0;
		final ItemList items = salesDoc.getItemList();
		final Multimap<String, Integer> multiMap = HashMultimap.create();
		for (final Item item : items) {

			if (!StringUtils.isEmpty(item.getProductId())) {
				multiMap.put(item.getProductId(), Integer.valueOf(i++));
			}

		}

		for (final Entry<String, Collection<Integer>> entry : multiMap.asMap()
				.entrySet()) {
			if (entry.getValue().size() > 1) {
				final List<Integer> list = new ArrayList<Integer>(
						entry.getValue());
				final Item item1 = items.get(list.get(0).intValue());
				final Item item2 = items.get(list.get(1).intValue());
				if (StringUtils.isEmpty(item1.getTechKey())) {
					final BigDecimal updatedQuantity = item2.getQuantity().add(
							item1.getQuantity());
					item2.setQuantity(updatedQuantity);
					salesDoc.getItemList().remove(list.get(0).intValue());

				}

				else {
					final BigDecimal updatedQuantity = item2.getQuantity().add(
							item1.getQuantity());
					item1.setQuantity(updatedQuantity);
					salesDoc.getItemList().remove(list.get(1).intValue());
				}
			}
		}

	}

	/**
	 * Determine items which need to be changed. Compares actual sales document
	 * data with the buffered CRM status.
	 *
	 * @param itemsCRMStatus
	 *            List of items which reflect the CRM status (when the document
	 *            has been read previously
	 * @param salesDoc
	 *            Current sales transaction. Contains items which we need to
	 *            compare with the CRM status
	 * @param transConf
	 *            Holds the sales customizing in general
	 * @return Set of item handles which represent the items to be changed.
	 */
	protected Set<String> findItemsToChange(final Map<String, //
			Item> itemsCRMStatus, //
			final SalesDocument salesDoc, //
			final TransactionConfiguration transConf) {

		// loop all items and compare data
		final Set<String> itemsToBeChanged = new HashSet<String>();
		final boolean tracing = sapLogger.isDebugEnabled();
		StringBuilder output = null;
		String traceString = null;
		if (tracing) {
			output = new StringBuilder(LF + "findItemsToChange, Results:");
		}

		final int size = salesDoc.getItemList().size();
		for (int i = 0; i < size; i++) {
			final Item currentItem = salesDoc.getItemList().get(i);
			// try to find corresponding item in existing buffer. If no such
			// item exists,
			// we need to mark the item as to be changed
			Item existingItem = null;
			if (itemsCRMStatus != null) {
				existingItem = itemsCRMStatus.get(currentItem.getTechKey()
						.getIdAsString());

			}
			if (existingItem != null) {
				// first compare attributes on item level

				final boolean doNotAddToChangedItems = isItemToBeSent(
						currentItem, existingItem);
				if (tracing) {
					traceString = LF + currentItem.getTechKey().getIdAsString()
							+ SEPARATOR + currentItem.getProductId()
							+ SEPARATOR;
					output.append(traceString + "item attributes equal: "
							+ doNotAddToChangedItems);
				}
				appendConfigItems(itemsToBeChanged, tracing, output,
						traceString, currentItem, doNotAddToChangedItems);
			} else {
				itemsToBeChanged.add(currentItem.getTechKey().getIdAsString());
				if (tracing) {
					output.append(traceString
							+ "new or not known yet or customizing tells to send all items");
				}
			}
		}
		if (tracing) {
			sapLogger.debug(output);
		}

		return itemsToBeChanged;
	}

	private void appendConfigItems(final Set<String> itemsToBeChanged,
			final boolean tracing, final StringBuilder output,
			final String traceString, final Item currentItem,
			final boolean doNotAddToChangedItems) {
		if (doNotAddToChangedItems) {
			// now take configuration into account

			if (currentItem.isConfigurable()) {
				// we always need to sent configurable items
				itemsToBeChanged.add(currentItem.getTechKey().getIdAsString());
				if (tracing) {
					output.append(traceString + "is configurable");
				}
			}

		} else {
			itemsToBeChanged.add(currentItem.getTechKey().getIdAsString());
			if (tracing) {
				output.append(traceString + "changed");
			}
		}
	}

	protected boolean isItemToBeSent(final Item currentItem,
			final Item existingItem) {
		if (!checkProduct(currentItem, existingItem)) {
			return false;
		}
		if (!checkQuantity(currentItem, existingItem)) {
			return false;
		}

		final boolean equalDeliverDate = checkDeliveryDate(currentItem,
				existingItem);
		if (!equalDeliverDate) {
			return false;
		}

		final boolean isEqualTexts = isEqualTexts(currentItem, existingItem);
		if (!isEqualTexts) {
			return false;
		}
		return true;
	}

	protected boolean checkProduct(final Item currentItem,
			final Item existingItem) {
		final boolean isProductIdIdentical = currentItem.getProductId().equals(
				existingItem.getProductId());
		if (!isProductIdIdentical) {
			return false;
		}
		final String currentUnit = currentItem.getUnit();
		final String existingUnit = existingItem.getUnit();

		final boolean isUnitIdentical = (currentUnit != null && existingUnit != null) ? currentUnit
				.equals(existingUnit)
				: (currentUnit == null && existingUnit == null);
		if (!isUnitIdentical) {
			return false;
		}
		return true;
	}

	protected boolean checkQuantity(final Item currentItem,
			final Item existingItem) {
		final boolean isQuantityIdentical = currentItem.getQuantity()
				.compareTo(existingItem.getQuantity()) == 0;
		if (!isQuantityIdentical) {
			return false;
		}

		if (!StringUtils.isEmpty(currentItem.getFreeQuantity())) {
			final boolean noFreeQuantity = currentItem.getFreeQuantity()
					.compareTo(BigDecimal.ZERO) == 0;
			if (!noFreeQuantity) {
				return false;
			}
		}
		final boolean isCancelStatusIdentical = currentItem.getOverallStatus()
				.isCancelled() == existingItem.getOverallStatus().isCancelled();
		if (!isCancelStatusIdentical) {
			return false;
		}
		return true;
	}

	protected boolean checkDeliveryDate(final Item currentItem,
			final Item existingItem) {
		final Date currentItemReqDelDate = currentItem.getReqDeliveryDate();
		final Date existingItemReqDelDate = existingItem.getReqDeliveryDate();

		return (existingItemReqDelDate != null && currentItemReqDelDate != null) ? currentItemReqDelDate
				.equals(existingItemReqDelDate)
				: (existingItemReqDelDate == null && currentItemReqDelDate == null);
	}

	/**
	 * Compare 2 item texts
	 */
	protected boolean isEqualTexts(final Item item1, final Item item2) {
		if ((item1.getText() == null) && (item2.getText() == null)) {
			return true;
		}
		if ((item1.getText() == null) || (item2.getText() == null)) {
			return false;
		}
		return item1.getText().getText().equals(item2.getText().getText());
	}

	/**
	 * @param salesDoc
	 * @param esError
	 * @throws BackendException
	 */
	protected void markInvalidItems(final SalesDocument salesDoc,
			final JCoStructure esError) throws BackendException {
		final ItemList items = salesDoc.getItemList();
		if (items == null || items.isEmpty()) {
			return;
		}

		// reset the invalidity of all items
		final Iterator<Item> it = items.iterator();
		Item item;

		while (it.hasNext()) {
			item = it.next();
			item.setErroneous(false);
		}

		// Return if the error is not related to an item
		if (!"ITEM".equals(esError.getString("OBJECT"))) {
			return;
		}

		// set item invalid
		item = items.get(new TechKey(esError.getString(FIELD_HANDLE)));
		if (item != null) {
			item.setErroneous(true);
			dispatchMessages(item, null, esError);
		} else {
			if (sapLogger.isDebugEnabled()) {
				sapLogger.debug("Item to be marked erroneouos not found");
			}
		}
	}

	protected void rePopulateItemBuffer(final Map<String, Item> itemsCRMStatus,
			final SalesDocument salesDoc) {

		// now re-create buffer
		itemsCRMStatus.clear();
		final ItemList itemList = salesDoc.getItemList();
		final Iterator<Item> currentItemState = itemList.iterator();
		while (currentItemState.hasNext()) {
			final Item currentItem = currentItemState.next();
			if (isItemToBeBuffered(currentItem)) {
				itemsCRMStatus.put(currentItem.getTechKey().getIdAsString(),
						createItemCopy(currentItem));
			}
		}
	}

	/**
	 * @param currentItem
	 * @return
	 */
	protected boolean isItemToBeBuffered(final Item currentItem) {
		boolean toBeBuffered = false;
		final boolean isSubItem = !TechKey.isEmpty(currentItem.getParentId());
		if (!isSubItem) {
			toBeBuffered = true;
		}
		return toBeBuffered;
	}

	protected Item createItemCopy(final Item currentItem) {
		return (Item) currentItem.clone();
	}

}
