/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.cpi.crm.sapcpicrmorderexchange.service.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PaymentCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.SalesConditionCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiConfig;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiCreditCardPayment;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderAddress;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderCancellation;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderCancellationItem;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderItem;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderPriceComponent;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiPartnerRole;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiTargetSystem;
import de.hybris.platform.sap.sapcpiorderexchange.exceptions.SapCpiOmmOrderConversionServiceException;
import de.hybris.platform.sap.sapcpiorderexchange.service.impl.SapCpiOmmOrderConversionService;
import de.hybris.platform.sap.sapcrmorderexchange.constants.CrmOrderEntryCsvColumns;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class SapCpiCrmOrderConversionService extends SapCpiOmmOrderConversionService
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiCrmOrderConversionService.class);

	private RawItemContributor<OrderModel> sapOrderContributor;
	private RawItemContributor<OrderModel> sapPaymentContributor;
	private RawItemContributor<OrderModel> sapPartnerContributor;
	private RawItemContributor<OrderModel> sapOrderEntryContributor;
	private RawItemContributor<OrderModel> sapSalesConditionsContributor;
	private RawItemContributor<OrderCancelRecordEntryModel> sapOrderCancelRequestContributor;
	private SAPGlobalConfigurationDAO sapCoreSAPGlobalConfigurationDAO;

	@Override
	public SapCpiOrder convertOrderToSapCpiOrder(final OrderModel orderModel)
	{

		final SapCpiOrder sapCpiOrder = new SapCpiOrder();

		sapOrderContributor
				.createRows(orderModel)
				.stream()
				.findFirst()
				.ifPresent(
						row -> {

							sapCpiOrder.setSapCpiConfig(mapOrderConfigInfo(orderModel));

							sapCpiOrder.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
							sapCpiOrder.setBaseStoreUid(mapAttribute(OrderCsvColumns.BASE_STORE, row));
							sapCpiOrder.setCreationDate(mapDateAttribute(OrderCsvColumns.DATE, row));
							sapCpiOrder.setCurrencyIsoCode(mapAttribute(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, row));
							sapCpiOrder.setPaymentMode(mapAttribute(OrderCsvColumns.PAYMENT_MODE, row));
							sapCpiOrder.setDeliveryMode(mapAttribute(OrderCsvColumns.DELIVERY_MODE, row));
							sapCpiOrder.setChannel(mapAttribute(OrderCsvColumns.CHANNEL, row));
							sapCpiOrder.setPurchaseOrderNumber(mapAttribute(OrderCsvColumns.PURCHASE_ORDER_NUMBER, row));

							sapCpiOrder.setTransactionType(orderModel.getStore().getSAPConfiguration().getSapcommon_transactionType());
							sapCpiOrder.setSalesOrganization(orderModel.getStore().getSAPConfiguration()
									.getSapcommon_salesOrganization());
							sapCpiOrder.setDistributionChannel(orderModel.getStore().getSAPConfiguration()
									.getSapcommon_distributionChannel());
							sapCpiOrder.setDivision(orderModel.getStore().getSAPConfiguration().getSapcommon_division());

							orderModel
									.getStore()
									.getSAPConfiguration()
									.getSapDeliveryModes()
									.stream()
									.filter(
											entry -> entry.getDeliveryMode().getCode().contentEquals(orderModel.getDeliveryMode().getCode()))
									.findFirst().ifPresent(entry -> sapCpiOrder.setShippingCondition(entry.getDeliveryValue()));

							sapCpiOrder.setSapCpiOrderItems(mapOrderItems(orderModel));
							sapCpiOrder.setSapCpiPartnerRoles(mapOrderPartners(orderModel));
							sapCpiOrder.setSapCpiOrderAddresses(mapOrderAddresses(orderModel));
							sapCpiOrder.setSapCpiOrderPriceComponents(mapOrderPrices(orderModel));
							sapCpiOrder.setSapCpiCreditCardPayments(mapCreditCards(orderModel));

						});

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("SCPI CRM order object: %n %s",
					ReflectionToStringBuilder.toString(sapCpiOrder, new RecursiveToStringStyle()).toString()));
		}

		return sapCpiOrder;

	}

	@Override
	protected SapCpiConfig mapOrderConfigInfo(final OrderModel orderModel)
	{

		final SapCpiTargetSystem sapCpiTargetSystem = new SapCpiTargetSystem();

		final Optional<SAPLogicalSystemModel> sapLogicalSystemOptional = getSapCoreSAPGlobalConfigurationDAO()
				.getSAPGlobalConfiguration().getSapLogicalSystemGlobalConfig().stream()
				.filter(logSys -> logSys.isDefaultLogicalSystem()).findFirst();

		if (sapLogicalSystemOptional.isPresent())
		{

			final SAPLogicalSystemModel sapLogicalSystem = sapLogicalSystemOptional.get();

			sapCpiTargetSystem.setSenderName(sapLogicalSystem.getSenderName());
			sapCpiTargetSystem.setSenderPort(sapLogicalSystem.getSenderPort());

			sapCpiTargetSystem.setReceiverName(sapLogicalSystem.getSapLogicalSystemName());
			sapCpiTargetSystem.setReceiverPort(sapLogicalSystem.getSapLogicalSystemName());


			if (sapLogicalSystem.getSapHTTPDestination() != null)
			{

				final String targetUrl = sapLogicalSystem.getSapHTTPDestination().getTargetURL();

				sapCpiTargetSystem.setUsername(sapLogicalSystem.getSapHTTPDestination().getUserid());
				sapCpiTargetSystem.setUrl(targetUrl);
				sapCpiTargetSystem.setClient(targetUrl.split("sap-client=")[1].substring(0, 3));


			}
			else
			{

				final String msg = String.format(
						"Error occurs while reading the http destination system information for the order [%s]!", orderModel.getCode());
				LOG.error(msg);
				throw new SapCpiOmmOrderConversionServiceException(msg);
			}


		}
		else
		{

			final String msg = String.format(
					"Error occurs while reading the default logical system information for the order [%s]!", orderModel.getCode());
			LOG.error(msg);
			throw new SapCpiOmmOrderConversionServiceException(msg);

		}

		final SapCpiConfig sapCpiConfig = new SapCpiConfig();
		sapCpiConfig.setSapCpiTargetSystem(sapCpiTargetSystem);

		return sapCpiConfig;

	}

	@Override
	protected List<SapCpiOrderItem> mapOrderItems(final OrderModel orderModel)
	{


		final List<SapCpiOrderItem> sapCpiOrderItems = new ArrayList<>();

		sapOrderEntryContributor.createRows(orderModel).forEach(row -> {

			final SapCpiOrderItem sapCpiOrderItem = new SapCpiOrderItem();

			sapCpiOrderItem.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
			sapCpiOrderItem.setEntryNumber(mapAttribute(OrderEntryCsvColumns.ENTRY_NUMBER, row));
			sapCpiOrderItem.setQuantity(mapAttribute(OrderEntryCsvColumns.QUANTITY, row));
			sapCpiOrderItem.setProductCode(mapAttribute(OrderEntryCsvColumns.PRODUCT_CODE, row));
			sapCpiOrderItem.setUnit(mapAttribute(OrderEntryCsvColumns.ENTRY_UNIT_CODE, row));
			sapCpiOrderItem.setProductName(mapAttribute(OrderEntryCsvColumns.PRODUCT_NAME, row));

			sapCpiOrderItems.add(sapCpiOrderItem);

		});

		return sapCpiOrderItems;

	}

	@Override
	protected List<SapCpiPartnerRole> mapOrderPartners(final OrderModel orderModel)
	{

		final List<SapCpiPartnerRole> sapCpiPartnerRoles = new ArrayList<>();

		sapPartnerContributor.createRows(orderModel).forEach(row -> {

			final SapCpiPartnerRole sapCpiPartnerRole = new SapCpiPartnerRole();

			sapCpiPartnerRole.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
			sapCpiPartnerRole.setDocumentAddressId(mapAttribute(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, row));
			sapCpiPartnerRole.setPartnerId(mapAttribute(PartnerCsvColumns.PARTNER_CODE, row));
			sapCpiPartnerRole.setPartnerRoleCode(mapAttribute(PartnerCsvColumns.PARTNER_ROLE_CODE, row));

			sapCpiPartnerRoles.add(sapCpiPartnerRole);

		});

		return sapCpiPartnerRoles;
	}

	@Override
	protected List<SapCpiOrderAddress> mapOrderAddresses(final OrderModel orderModel)
	{

		final List<SapCpiOrderAddress> sapCpiOrderAddresses = new ArrayList<>();

		sapPartnerContributor.createRows(orderModel).forEach(row -> {

			final SapCpiOrderAddress sapCpiOrderAddress = new SapCpiOrderAddress();

			sapCpiOrderAddress.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
			sapCpiOrderAddress.setApartment(mapAttribute(PartnerCsvColumns.APPARTMENT, row));
			sapCpiOrderAddress.setBuilding(mapAttribute(PartnerCsvColumns.BUILDING, row));
			sapCpiOrderAddress.setCity(mapAttribute(PartnerCsvColumns.CITY, row));
			sapCpiOrderAddress.setCountryIsoCode(mapAttribute(PartnerCsvColumns.COUNTRY_ISO_CODE, row));
			sapCpiOrderAddress.setDistrict(mapAttribute(PartnerCsvColumns.DISTRICT, row));
			sapCpiOrderAddress.setDocumentAddressId(mapAttribute(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, row));
			sapCpiOrderAddress.setEmail(mapAttribute(PartnerCsvColumns.EMAIL, row));
			sapCpiOrderAddress.setFaxNumber(mapAttribute(PartnerCsvColumns.FAX, row));
			sapCpiOrderAddress.setFirstName(mapAttribute(PartnerCsvColumns.FIRST_NAME, row));
			sapCpiOrderAddress.setHouseNumber(mapAttribute(PartnerCsvColumns.HOUSE_NUMBER, row));
			sapCpiOrderAddress.setLanguageIsoCode(mapAttribute(PartnerCsvColumns.LANGUAGE_ISO_CODE, row));
			sapCpiOrderAddress.setLastName(mapAttribute(PartnerCsvColumns.LAST_NAME, row));
			sapCpiOrderAddress.setMiddleName(mapAttribute(PartnerCsvColumns.MIDDLE_NAME, row));
			sapCpiOrderAddress.setMiddleName2(mapAttribute(PartnerCsvColumns.MIDDLE_NAME2, row));
			sapCpiOrderAddress.setPobox(mapAttribute(PartnerCsvColumns.POBOX, row));
			sapCpiOrderAddress.setPostalCode(mapAttribute(PartnerCsvColumns.POSTAL_CODE, row));
			sapCpiOrderAddress.setRegionIsoCode(mapAttribute(PartnerCsvColumns.REGION_ISO_CODE, row));
			sapCpiOrderAddress.setStreet(mapAttribute(PartnerCsvColumns.STREET, row));
			sapCpiOrderAddress.setTelNumber(mapAttribute(PartnerCsvColumns.TEL_NUMBER, row));
			sapCpiOrderAddress.setTitleCode(mapAttribute(PartnerCsvColumns.TITLE, row));

			if (sapCpiOrderAddress.getDocumentAddressId() != null && !sapCpiOrderAddress.getDocumentAddressId().isEmpty())
			{
				sapCpiOrderAddresses.add(sapCpiOrderAddress);
			}
		});

		return sapCpiOrderAddresses;
	}


	@Override
	protected List<SapCpiCreditCardPayment> mapCreditCards(final OrderModel orderModel)
	{

		final List<SapCpiCreditCardPayment> sapCpiCreditCardPayments = new ArrayList<>();

		try
		{

			sapPaymentContributor.createRows(orderModel).forEach(
					row -> {

						final SapCpiCreditCardPayment sapCpiCreditCardPayment = new SapCpiCreditCardPayment();

						sapCpiCreditCardPayment.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
						sapCpiCreditCardPayment.setCcOwner(mapAttribute(PaymentCsvColumns.CC_OWNER, row));
						sapCpiCreditCardPayment.setPaymentProvider(mapAttribute(PaymentCsvColumns.PAYMENT_PROVIDER, row));
						sapCpiCreditCardPayment.setSubscriptionId(mapAttribute(PaymentCsvColumns.SUBSCRIPTION_ID, row));

						String requestId = mapAttribute(PaymentCsvColumns.REQUEST_ID, row);
						requestId = Pattern.compile("^\\d+$").matcher(requestId).matches() ? BigInteger
								.valueOf(Long.parseLong(requestId)).toString(32).toUpperCase() : requestId;
						sapCpiCreditCardPayment.setRequestId(requestId);

						final String month = mapAttribute(PaymentCsvColumns.VALID_TO_MONTH, row);
						sapCpiCreditCardPayment.setValidToMonth(month);

						final String year = mapAttribute(PaymentCsvColumns.VALID_TO_YEAR, row);

						if (year != null && month != null)
						{

							final YearMonth yearMonth = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
							sapCpiCreditCardPayment.setValidToYear(yearMonth.atEndOfMonth().format(
									DateTimeFormatter.ofPattern("yyyyMMdd")));

						}
						else
						{

							sapCpiCreditCardPayment.setValidToYear(null);

						}

						sapCpiCreditCardPayments.add(sapCpiCreditCardPayment);

					});


		}
		catch (final RuntimeException ex)
		{

			final String msg = String.format("Error occurs while setting the payment information for the order [%s]!",
					orderModel.getCode());
			LOG.error(msg.concat(ex.getMessage()));
			throw ex;

		}

		return sapCpiCreditCardPayments;

	}


	@Override
	protected List<SapCpiOrderPriceComponent> mapOrderPrices(final OrderModel orderModel)
	{

		final List<SapCpiOrderPriceComponent> sapCpiOrderPriceComponents = new ArrayList<>();

		sapSalesConditionsContributor.createRows(orderModel).forEach(row -> {

			final SapCpiOrderPriceComponent sapCpiOrderPriceComponent = new SapCpiOrderPriceComponent();

			sapCpiOrderPriceComponent.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
			sapCpiOrderPriceComponent.setEntryNumber(mapAttribute(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, row));
			sapCpiOrderPriceComponent.setConditionCode(mapAttribute(SalesConditionCsvColumns.CONDITION_CODE, row));
			sapCpiOrderPriceComponent.setConditionCounter(mapAttribute(SalesConditionCsvColumns.CONDITION_COUNTER, row));
			sapCpiOrderPriceComponent.setCurrencyIsoCode(mapAttribute(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, row));
			sapCpiOrderPriceComponent.setPriceQuantity(mapAttribute(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY, row));
			sapCpiOrderPriceComponent.setUnit(mapAttribute(SalesConditionCsvColumns.CONDITION_UNIT_CODE, row));
			sapCpiOrderPriceComponent.setValue(mapAttribute(SalesConditionCsvColumns.CONDITION_VALUE, row));
			sapCpiOrderPriceComponent.setAbsolute(mapAttribute(SalesConditionCsvColumns.ABSOLUTE, row));

			sapCpiOrderPriceComponents.add(sapCpiOrderPriceComponent);

		});

		return sapCpiOrderPriceComponents;

	}

	@Override
	public List<SapCpiOrderCancellation> convertCancelOrderToSapCpiCancelOrder(
			final OrderCancelRecordEntryModel orderCancelRecordEntryModel)
	{

		final SapCpiOrderCancellation sapCpiOrderCancellation = new SapCpiOrderCancellation();

		final List<Map<String, Object>> cancellationRows = sapOrderCancelRequestContributor.createRows(orderCancelRecordEntryModel);

		cancellationRows
				.stream()
				.findFirst()
				.ifPresent(
						cancellationHeader -> {

							sapCpiOrderCancellation.setSapCpiConfig(mapOrderConfigInfo(orderCancelRecordEntryModel
									.getModificationRecord().getOrder()));
							sapCpiOrderCancellation.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, cancellationHeader));
							sapCpiOrderCancellation.setRejectionReason(mapAttribute(OrderEntryCsvColumns.REJECTION_REASON,
									cancellationHeader));
							sapCpiOrderCancellation.setIsCancelRequest(mapAttribute(CrmOrderEntryCsvColumns.IsCancelRequest,
									cancellationHeader));
							final List<SapCpiOrderCancellationItem> sapCpiOrderCancellationItems = new ArrayList<>();

							cancellationRows.forEach(cancellationItem -> {
								final SapCpiOrderCancellationItem sapCpiOrderCancellationItem = new SapCpiOrderCancellationItem();
								sapCpiOrderCancellationItem.setEntryNumber(mapAttribute(OrderEntryCsvColumns.ENTRY_NUMBER,
										cancellationItem));
								sapCpiOrderCancellationItem.setProductCode(mapAttribute(OrderEntryCsvColumns.PRODUCT_CODE,
										cancellationItem));
								sapCpiOrderCancellationItems.add(sapCpiOrderCancellationItem);
							});


							sapCpiOrderCancellation.setSapCpiOrderCancellationItems(sapCpiOrderCancellationItems);

						});


		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("SCPI cancel order object: %n %s",
					ReflectionToStringBuilder.toString(sapCpiOrderCancellation, new RecursiveToStringStyle())));
		}

		return Arrays.asList(sapCpiOrderCancellation);
	}

	@Override
	protected String mapAttribute(final String attribute, final Map<String, Object> row)
	{
		return row.get(attribute) != null ? row.get(attribute).toString() : null;
	}

	@Override
	protected String mapDateAttribute(final String attribute, final Map<String, Object> row)
	{

		if (row.get(attribute) != null && row.get(attribute) instanceof Date)
		{
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			return sdf.format((Date) row.get(attribute));
		}

		return null;
	}

	@Override
	protected RawItemContributor<OrderModel> getSapOrderContributor()
	{
		return sapOrderContributor;
	}

	@Override
	@Required
	public void setSapOrderContributor(final RawItemContributor<OrderModel> sapOrderContributor)
	{
		this.sapOrderContributor = sapOrderContributor;
	}

	@Override
	protected RawItemContributor<OrderModel> getSapPaymentContributor()
	{
		return sapPaymentContributor;
	}

	@Override
	@Required
	public void setSapPaymentContributor(final RawItemContributor<OrderModel> sapPaymentContributor)
	{
		this.sapPaymentContributor = sapPaymentContributor;
	}

	@Override
	protected RawItemContributor<OrderModel> getSapPartnerContributor()
	{
		return sapPartnerContributor;
	}

	@Override
	@Required
	public void setSapPartnerContributor(final RawItemContributor<OrderModel> sapPartnerContributor)
	{
		this.sapPartnerContributor = sapPartnerContributor;
	}

	@Override
	protected RawItemContributor<OrderModel> getSapOrderEntryContributor()
	{
		return sapOrderEntryContributor;
	}

	@Override
	@Required
	public void setSapOrderEntryContributor(final RawItemContributor<OrderModel> sapOrderEntryContributor)
	{
		this.sapOrderEntryContributor = sapOrderEntryContributor;
	}

	@Override
	protected RawItemContributor<OrderModel> getSapSalesConditionsContributor()
	{
		return sapSalesConditionsContributor;
	}

	@Override
	@Required
	public void setSapSalesConditionsContributor(final RawItemContributor<OrderModel> sapSalesConditionsContributor)
	{
		this.sapSalesConditionsContributor = sapSalesConditionsContributor;
	}

	protected SAPGlobalConfigurationDAO getSapCoreSAPGlobalConfigurationDAO()
	{
		return sapCoreSAPGlobalConfigurationDAO;
	}

	@Required
	public void setSapCoreSAPGlobalConfigurationDAO(final SAPGlobalConfigurationDAO sapCoreSAPGlobalConfigurationDAO)
	{
		this.sapCoreSAPGlobalConfigurationDAO = sapCoreSAPGlobalConfigurationDAO;
	}

	@Override
	protected RawItemContributor<OrderCancelRecordEntryModel> getSapOrderCancelRequestContributor()
	{
		return sapOrderCancelRequestContributor;
	}

	@Override
	@Required
	public void setSapOrderCancelRequestContributor(
			final RawItemContributor<OrderCancelRecordEntryModel> sapOrderCancelRequestContributor)
	{
		this.sapOrderCancelRequestContributor = sapOrderCancelRequestContributor;
	}

}