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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm;

import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.SalesDocumentBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sap.hybris.crm.sapcrmordermgmtbol.constants.SapcrmordermgmtbolConstants;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.basket.backend.impl.crm.BasketCRM;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.order.backend.impl.crm.OrderCRM;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.ItemBuffer;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.strategy.GetAllStrategy;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.strategy.LrdActionsStrategy;
import com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.interf.crm.strategy.SetStrategy;

/**
 * Common base class for ECO CRM sales documents. See {@link OrderCRM},
 * {@link BasketCRM}
 *
 */
@BackendType("CRM")
public abstract class SalesDocumentCRM extends
		IsaBackendBusinessObjectBaseSalesCRM implements SalesDocumentBackend {

	/**
	 * constant naming the key referenced in factory-config.xml where the
	 * strategy class is specified
	 */

	private static final Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(SalesDocumentCRM.class.getName());

	private ItemBuffer itemBuffer;

	private final Map<TechKey, Item> erroneousItems = new HashMap<TechKey, Item>();

	/**
	 * Warning message in case UME is disabled. Then we only have one session in
	 * CRM, and we cannot fully support multiple documents. UME disabling will
	 * not be possible in productive environments.
	 */
	protected boolean isUMEDisabledWarningNecessary = false;

	/**
	 * Strategy pattern to support various LO-API versions
	 */
	protected GetAllStrategy getAllStrategy = null;

	protected LrdActionsStrategy lrdActionsStrategy = null;

	protected SetStrategy setStrategy = null;

	// store variant info for itemSalesDocs
	protected Map<String, String> itemVariantMap = new HashMap<String, String>(
			0);

	// flag if inline config display isd enabled
	protected boolean showInlineConfig = true;

	// The condition type which should be used to determine the freight value
	protected String headerCondTypeFreight = "";

	// flag if incompletion log is requested
	protected boolean isIncompletionLogRequested = false;

	// store delivery Priority coming from UI as CRM Backend does not support it
	// on header level
	protected String deliveryPriority = null;

	// Error state of the document in the backend (ERRKZ)
	protected boolean erroneous = false;

	protected TransactionConfiguration transactionConfiguration;

	protected boolean createProcess;

	private String soldToHandle;

	private String payerHandle;

	private boolean documentInitial;

	private String shippingCondition = null;

	private boolean isRequiredDeliveryDateNeededForInitializing = false;

	private Header erroneousHeader;

	/**
	 * @return the itemBuffer
	 */
	public ItemBuffer getItemBuffer() {
		return itemBuffer;
	}

	/**
	 * @param itemBuffer
	 *            the itemBuffer to set
	 */
	public void setItemBuffer(final ItemBuffer itemBuffer) {
		this.itemBuffer = itemBuffer;
	}

	/**
	 * @return the getAllStrategy
	 */
	public GetAllStrategy getGetAllStrategy() {
		return getAllStrategy;
	}

	/**
	 * @param getAllStrategy
	 *            the getAllStrategy to set
	 */
	public void setGetAllStrategy(final GetAllStrategy getAllStrategy) {
		this.getAllStrategy = getAllStrategy;
	}

	/**
	 * @return the lrdActionsStrategy
	 */
	public LrdActionsStrategy getLrdActionsStrategy() {
		return lrdActionsStrategy;
	}

	/**
	 * @param lrdActionsStrategy
	 *            the lrdActionsStrategy to set
	 */
	public void setLrdActionsStrategy(
			final LrdActionsStrategy lrdActionsStrategy) {
		this.lrdActionsStrategy = lrdActionsStrategy;
	}

	/**
	 * @return the setStrategy
	 */
	public SetStrategy getSetStrategy() {
		return setStrategy;
	}

	/**
	 * @param setStrategy
	 *            the setStrategy to set
	 */
	public void setSetStrategy(final SetStrategy setStrategy) {
		this.setStrategy = setStrategy;
	}

	@Override
	public boolean isDocumentInitial() {
		return documentInitial;
	}

	protected SalesDocumentCRM() {
		super();
	}

	@Override
	public void setSoldToHandle(final String soldTo) {
		soldToHandle = soldTo;

	}

	@Override
	public void setPayerHandle(final String payer) {
		payerHandle = payer;

	}

	@Override
	public String getPayerHandle() {
		return payerHandle;
	}

	@Override
	public String getSoldToHandle() {
		return soldToHandle;
	}

	@Override
	public void setDocumentInitial(final boolean isInitial) {
		documentInitial = isInitial;
	}

	@Override
	public void setShippingCondition(final String currentShippingCondition) {
		shippingCondition = currentShippingCondition;

	}

	@Override
	public String getShippingCondition() {
		return shippingCondition;
	}

	@Override
	public ModuleConfigurationAccess getModuleConfigurationAccess() {
		return getModuleConfigurationAccess();
	}

	/**
	 * Create back end representation of the object
	 *
	 * @param salesDocument
	 *            sales document to be created in back end
	 * @throws BackendException
	 *             case of a back-end error
	 */
	public void createInBackend(final SalesDocument salesDocument)
			throws BackendException {
		createInBackend(null, salesDocument);
	}

	/**
	 * Create back end representation of the object without providing
	 * information about the user. This may happen in the B2C scenario where the
	 * login may be done later.
	 *
	 * @param transactionConfiguration
	 *            The current shop
	 * @param salesDocument
	 *            The document to read the data in
	 */
	@Override
	public void createInBackend(
			final TransactionConfiguration transactionConfiguration,
			final SalesDocument salesDocument) throws BackendException {

		sapLogger.entering("createInBackend()");

		// clear CRM buffer, because we are creating a new document in
		// CRM
		itemBuffer.clearCRMBuffer();

		// Shop needs to be initialized. Other methods rely on this.
		this.transactionConfiguration = transactionConfiguration;

		// get JCOConnection
		final JCoConnection aJCoCon = getDefaultJCoConnection();

		loadState.setLoadOperation(LoadOperation.CREATE);
		final BackendCallResult retVal = lrdActionsStrategy.createBasket(
				salesDocument, this, aJCoCon);

		// Exit here, if there are errors
		if (retVal.isFailure() && (!salesDocument.isInitialized())) {
			salesDocument.setInvalid();
			if (sapLogger.isDebugEnabled()) {
				sapLogger.debug("Errors in R3LrdLoad -> returning");
			}

			return;
		} else if (retVal.isFailure()) {
			// correct error, error is recoverable
			handleInvalidHeaderAfterUpdate(salesDocument);
			restoreErroneousHeaderAfterSet(salesDocument.getHeader());
			isRequiredDeliveryDateNeededForInitializing = true;
		} else {
			setErroneousHeader(null);
		}

		// a subsequent read is not needed
		salesDocument.setDirty(false);

		createProcess = true;

		sapLogger.exiting();
	}

	/**
	 * Document couldn't be created. Now the header needs to be corrected, and
	 * the document is again sent to CRM. <br>
	 *
	 * @throws BackendException
	 */
	protected void handleInvalidHeaderAfterUpdate(
			final SalesDocument salesDocument) throws BackendException {
		final Header header = salesDocument.getHeader();
		final Header copy = (Header) header.clone();
		copy.setReqDeliveryDate(null);
		copy.copyMessages(salesDocument);
		setErroneousHeader(copy);

		fixErroneousHeader(salesDocument);
		setItemText(salesDocument);

		setStrategy.execute(this, salesDocument, itemBuffer.getItemsCRMState(),
				transactionConfiguration, getDefaultJCoConnection(), null,
				false);

	}

	/**
	 * @param salesDocument
	 */
	private void setItemText(final SalesDocument salesDocument) {
		for (final Item item : salesDocument.getItemList()) {
			item.setText(item.createText());
		}
	}

	/**
	 * Restoring header: Initializing delivery date.<br>
	 *
	 * @param header
	 */
	protected void restoreErroneousHeaderAfterSet(final Header header) {
		header.setReqDeliveryDate(SapcrmordermgmtbolConstants.DATE_INITIAL);

	}

	/**
	 * Fixing a header which cannot be digested from CRM. This implementation
	 * sets a delivery date because this is the only error which can be
	 * corrected by means of attributes available in WCEM
	 *
	 */
	protected void fixErroneousHeader(final SalesDocument document) {
		final Header header = document.getHeader();
		header.setReqDeliveryDate(new Date(System.currentTimeMillis()));
		header.setText((Text) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_TEXT));
	}

	/**
	 * Deletes a single item in the backend.
	 */
	@Override
	public void deleteItemInBackend(final SalesDocument salesDocument,
			final TechKey itemToDelete) throws BackendException {

		final String METHOD_NAME = "deleteItemInBackend()";
		sapLogger.entering(METHOD_NAME);

		final TechKey[] itemsToDelete = new TechKey[1];
		itemsToDelete[0] = itemToDelete;
		deleteItemsInBackend(salesDocument, itemsToDelete, null);

		sapLogger.exiting();
	}

	@Override
	public void removeErroneousItems(final TechKey[] itemsToDelete) {
		for (int i = 0; i < itemsToDelete.length; i++) {
			erroneousItems.remove(itemsToDelete[i]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.wec.app.esales.module.transaction.backend.interf.SalesDocumentBackend
	 * #emptyInBackend(com.sap.wec.app.esales.module.transaction.backend.interf.
	 * SalesDocument)
	 */
	@Override
	public void emptyInBackend(final SalesDocument salesDocument)
			throws BackendException {

		// re-set change status
		loadState.setLoadOperation(LoadOperation.DISPLAY);

		itemBuffer.clearCRMBuffer();

	}

	/**
	 * Returns the map, to store item variant information
	 *
	 * @return HashMap, the map to store item varaint information in
	 */
	@Override
	public Map<String, String> getItemVariantMap() {
		return itemVariantMap;
	}

	protected void fixErroneousItem(final Item item) {
		item.setProductId("");
	}

	/**
	 * Initializes this Business Object.
	 */
	@Override
	public void initBackendObject() throws BackendException {

		super.initBackendObject();

		// in case of create, this will be set to try by the create method
		createProcess = false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend
	 * .interf .crm.BackendState#isCreateProcess()
	 */
	@Override
	public boolean isCreateProcess() {
		return createProcess;
	}

	/**
	 * @return true, if error occured
	 */
	public boolean isErroneous() {
		return erroneous;
	}

	@Override
	public boolean isMultipleAddressesSupported() {
		return false;
	}

	/**
	 * Read the complete document from backend and reset the dirty flags.
	 */
	@Override
	public void readForUpdateFromBackend(final SalesDocument salesDocument)
			throws BackendException {

		if (checkForInitializationError(salesDocument)) {
			return;
		}

		setGData(salesDocument, salesDocument.getTransactionConfiguration());

		readFromBackend(salesDocument, transactionConfiguration, false);
		// reset the dirty flags to avoid duplicate rfc calls.
		salesDocument.setDirty(false);
		salesDocument.getHeader().setDirty(false);
	}

	protected boolean checkForInitializationError(
			final SalesDocument salesDocument) {
		if (isInBackend(salesDocument)) {
			salesDocument.setInitialized(true);
			return false;
		}
		if (!salesDocument.isInitialized()) {
			salesDocument.getItemList().clear();
			salesDocument.addMessage(new Message(Message.ERROR,
					"sapsalestransactions.bo.no.create"));
			return true;
		} else {
			return false;
		}
	}

	protected boolean isInBackend(final SalesDocument salesDocument) {
		final String id = salesDocument.getTechKey().getIdAsString();
		return (id != null) && (!id.isEmpty());
	}

	@Override
	public void readFromBackend(final SalesDocument salesDocument,
			final TransactionConfiguration transConf, final boolean directRead)
			throws BackendException {

		transactionConfiguration = transConf;
		final String METHOD_NAME = "readFromBackend()";
		sapLogger.entering(METHOD_NAME);

		if (checkForInitializationError(salesDocument)) {
			sapLogger.exiting();
			return;
		}

		final JCoConnection aJCoCon = getDefaultJCoConnection();

		// clear document data before being read from backend
		salesDocument.clearHeader();

		handleInvalidItems(salesDocument);
		readItemsFromLOAPI(salesDocument, aJCoCon);
		handleInvalidHeaderAfterRead(salesDocument);
		handleInvalidItemsAfterRead(salesDocument);

		adjustFreeGoods(salesDocument, transactionConfiguration);

		if (deliveryPriority != null) {
			salesDocument.getHeader().setDeliveryPriority(deliveryPriority);
		}

		sapLogger.exiting();
	}

	/**
	 * Actions which are needed for the header after a read step. Messages might
	 * need to be restored, because the header has been temporarily adjusted
	 * before. <br>
	 *
	 */
	protected void handleInvalidHeaderAfterRead(
			final SalesDocument salesDocument) {
		if (getErroneousHeader() != null) {
			restoreErroneousHeaderAfterRead(salesDocument);
		}

	}

	/**
	 * Restore invalid header after read step. Restore required delivery date
	 * and messages. <br>
	 *
	 */
	protected void restoreErroneousHeaderAfterRead(
			final SalesDocument salesDocument) {
		final Header header = salesDocument.getHeader();
		header.setReqDeliveryDate(getErroneousHeader().getReqDeliveryDate());
		salesDocument.copyMessages(getErroneousHeader());
	}

	/**
	 * Restores the sales document items after read and adds the information
	 * which needed to be removed before.<br>
	 *
	 * @param salesDocument
	 *            The cart sales document.
	 */
	protected void handleInvalidItemsAfterRead(final SalesDocument salesDocument) {
		final Iterator<Entry<TechKey, Item>> iterator = getErroneousItems()
				.entrySet().iterator();
		while (iterator.hasNext()) {
			final Item itemStateBeforeCorrection = iterator.next().getValue();
			final Item erroneousItem = salesDocument
					.getItem(itemStateBeforeCorrection.getTechKey());
			restoreErroneousItem(itemStateBeforeCorrection, erroneousItem);
		}
	}

	/**
	 * If an item has been fixed so that LO-API can consume it, we later on need
	 * to restore the user inputs, so that the user sees the data he/she has
	 * entered which need to be corrected.<br>
	 *
	 * @param savedState
	 *            The original, saved state of the item
	 * @param errItm
	 *            State of the erroneous item after LO-API can digest it
	 */
	protected void restoreErroneousItem(final Item savedState, final Item errItm) {
		errItm.setProductId(savedState.getProductId());
		errItm.setQuantity(savedState.getQuantity());
		errItm.setUnit(savedState.getUnit());
		errItm.setReqDeliveryDate(savedState.getReqDeliveryDate());
		errItm.copyMessages(savedState);
	}

	@Override
	public Map<TechKey, Item> getErroneousItems() {
		return this.erroneousItems;
	}

	private void readItemsFromLOAPI(final SalesDocument salesDocument,
			final JCoConnection aJCoCon) throws BackendException {
		salesDocument.clearItems();

		getAllStrategy.execute(this, salesDocument, itemBuffer, aJCoCon);

	}

	/**
	 * Checks for invalid items and initialises them, so that they can be
	 * deleted afterwards. This call is done before the LO-API is called for
	 * reading item information. <br>
	 * The items which cannot be digested by LO-API are fixed, the info needed
	 * to restore the document are kept in
	 * {@link SalesDocumentCRM#getErroneousItems()}
	 *
	 * @param salesDocument
	 *            Cart sales document
	 * @return Could we fix the document.
	 * @throws BackendException
	 */
	protected boolean handleInvalidItems(final SalesDocument salesDocument)
			throws BackendException {

		final String METHOD_NAME = "handleInvalidItems()";
		sapLogger.entering(METHOD_NAME);

		final ItemList items = salesDocument.getItemList();
		boolean itemErrorsResolvable = true;

		for (final Item item : items) {
			if ((item != null) && item.isErroneous()) {

				this.addErroneousItem(item);
				fixErroneousItem(item);

				// get JCOConnection
				final JCoConnection aJCoCon = getDefaultJCoConnection();
				setStrategy.execute(this, salesDocument,
						itemBuffer.getItemsCRMState(),
						transactionConfiguration, aJCoCon, null, false);

				// Item is still erroneous
				if (item.isErroneous()) {
					itemErrorsResolvable = false;
					break;
				}

			}

		}
		return itemErrorsResolvable;

	}

	@Override
	public void clearErroneousItems() {
		this.erroneousItems.clear();
	}

	@Override
	public void addErroneousItem(final Item item) {
		final Item clonedItem = (Item) item.clone();
		this.erroneousItems.put(item.getTechKey(), clonedItem);
	}

	@Override
	public boolean recoverFromBackend(final SalesDocument salesDoc,
			final TechKey basketGuid) throws BackendException {

		throw new BackendException(
				"Method recoverFromBackend not supported by this backend.");
	}

	@Override
	public void setErroneous(final boolean bool) {
		erroneous = bool;
	}

	/**
	 * Call the CRM_LORD_LOAD for edit.
	 */
	@Override
	public void setGData(final SalesDocument salesDocument,
			final TransactionConfiguration shop) throws BackendException {

		sapLogger.exiting();
	}

	@Override
	public void setLoadStateCreate() {
		//
	}

	@Override
	public void updateInBackend(final SalesDocument salesDocument)
			throws BackendException {
		updateInBackend(salesDocument, transactionConfiguration);
	}

	/**
	 * Update object in the backend by putting the data into the underlying
	 * storage.
	 *
	 * @param salesDocument
	 *            the document to update
	 * @param shop
	 *            the current shop
	 */

	@Override
	public void updateInBackend(final SalesDocument salesDocument,
			final TransactionConfiguration shop) throws BackendException {

		final String METHOD_NAME = "updateInBackend()";
		sapLogger.entering(METHOD_NAME);

		if (checkForInitializationError(salesDocument)) {
			sapLogger.exiting();
			return;
		}

		// get JCOConnection
		final JCoConnection aJCoCon = getDefaultJCoConnection();

		handleInvalidHeaderBeforeUpdate(salesDocument);
		final BackendCallResult result = setStrategy.execute(this,
				salesDocument, itemBuffer.getItemsCRMState(), shop, aJCoCon,
				null, false);

		if (result.isFailure()) {
			handleInvalidHeaderAfterUpdate(salesDocument);
		} else {
			setErroneousHeader(null);
		}

		handleInvalidItemsAfterUpdate(salesDocument);

		deliveryPriority = salesDocument.getHeader().getDeliveryPriority();
		sapLogger.exiting();

	}

	/**
	 * Actions which are needed before an update call if error situations occur.
	 * Implementation re-initializes the required delivery date in case nothing
	 * was entered<br>
	 *
	 * @param salesDocument
	 *            Sales document
	 */
	protected void handleInvalidHeaderBeforeUpdate(
			final SalesDocument salesDocument) {
		if (isRequiredDeliveryDateNeededForInitializing
				&& salesDocument.getHeader().getReqDeliveryDate() == null) {
			salesDocument.getHeader().setReqDeliveryDate(
					SapcrmordermgmtbolConstants.DATE_INITIAL);
		}
	}

	/**
	 * Clear session storage of erroneous items for those items which are fine
	 * after the set call to SD<br>
	 *
	 * @param salesDocument
	 */
	protected void handleInvalidItemsAfterUpdate(
			final SalesDocument salesDocument) {
		for (final Item item : salesDocument.getItemList()) {
			if (!item.isErroneous()) {
				final TechKey[] itemsToDelete = new TechKey[] { item
						.getTechKey() };
				removeErroneousItems(itemsToDelete);
			}
		}
	}

	@Override
	public void updateInBackend(final SalesDocument salesDocument,
			final TransactionConfiguration transactionConfiguration,
			final List<TechKey> itemsToDelete) throws BackendException {

		// delete items. We need to do this via a distinct RFC call

		updateInBackend(salesDocument, transactionConfiguration);
	}

	@Override
	public void clearItemBuffer() {
		itemBuffer.clearCRMBuffer();
	}

	@Override
	public Header getErroneousHeader() {
		return erroneousHeader;
	}

	@Override
	public void setErroneousHeader(final Header header) {
		erroneousHeader = header;
	}

	@Override
	public boolean isItemBasedAvailability() {
		return true;
	}

	@Override
	public void validate(final SalesDocument salesDocument)
			throws BackendException {
		if (!salesDocument.isInitialized()) {
			return;
		}
		// The standard implementation just re-reads the order from the back
		// end.
		// It's possible to do additional validations or updates at this point
		// to retrieve other messages from the back end, which must be attached
		// to the salesDocument

		// Example:

		readFromBackend(salesDocument, transactionConfiguration, true);
	}

	@Override
	public boolean isBackendDown() {
		try {
			return getDefaultJCoConnection().isBackendOffline();
		} catch (final BackendException e) {
			throw new ApplicationBaseRuntimeException(
					"Cannot determine backend availability", e);

		}
	}

	@Override
	public void closeBackendSession() {
		try {
			getDefaultJCoConnection().isBackendAvailable();
		} catch (final BackendException e) {
			throw new ApplicationBaseRuntimeException(
					"Could not close session", e);
		}
	}

}
