/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package sap.sapcpicrmreturnorder.service.impl;

import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.crm.model.SAPReturnOrderReasonModel;

import sap.sapcpicrmcomplaint.model.SAPCpiOutboundReturnOrderEntryConditionModel;
import sap.sapcpicrmcomplaint.model.SAPCpiOutboundReturnOrderEntryModel;
import sap.sapcpicrmreturnorder.model.SAPCpiOutboundReturnOrderModel;
import sap.sapcpicrmreturnorder.service.SapCpiReturnOrderConversionService;
import sap.sapcpicrmticketingsystem.model.SAPCpiOutboundReturnOrderConfigModel;

/**
 *
 */
public class SapCpiReturnOrderConversionServiceImpl implements SapCpiReturnOrderConversionService
{
	/**
	 * 
	 */
	private static final String PRODUCT_CODE = "productCode";

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiReturnOrderConversionServiceImpl.class);

	private ModelService modelService;
	private SAPGlobalConfigurationDAO globalConfigurationDAO;
	private FlexibleSearchService flexibleSearchService;

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public SAPGlobalConfigurationDAO getGlobalConfigurationDAO()
	{
		return globalConfigurationDAO;
	}

	public void setGlobalConfigurationDAO(final SAPGlobalConfigurationDAO globalConfigurationDAO)
	{
		this.globalConfigurationDAO = globalConfigurationDAO;
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@Override
	public SAPCpiOutboundReturnOrderModel convertReturnOrderSapCpiReturnOrder(final List<Map<String, Object>> rowsAsNameValuePairs,
			final ReturnRequestModel pReturnOrder)
	{

		final SAPCpiOutboundReturnOrderModel sapCpiReturnOrderModel = getModelService()
				.create(SAPCpiOutboundReturnOrderModel.class);

		sapCpiReturnOrderModel.setPrecedingDocumentId(getProceedingDocumentId(rowsAsNameValuePairs));
		sapCpiReturnOrderModel.setOrderId(getOrderId(rowsAsNameValuePairs));
		sapCpiReturnOrderModel.setCustomerId(getCustomerId(rowsAsNameValuePairs));

		final List<Map<String, Object>> productsInfo = getProductsInfo(rowsAsNameValuePairs);

		final Set<SAPCpiOutboundReturnOrderEntryModel> returnOrderEntries = new HashSet<>();


		productsInfo.stream().forEach(product -> {


			final SAPCpiOutboundReturnOrderEntryModel returnOrderEntry = new SAPCpiOutboundReturnOrderEntryModel();
			returnOrderEntry.setItemNumber(Integer.toString(((Integer) product.get("entryNumber")) + 1));
			returnOrderEntry.setOrderedProduct(product.get(PRODUCT_CODE).toString());
			returnOrderEntry.setProductDescription(product.get("productName").toString());
			returnOrderEntry.setProductId(product.get(PRODUCT_CODE).toString());
			returnOrderEntry.setProcessQtyInitIso(product.get("entryUnitCode").toString());
			returnOrderEntry.setDefectQuantity(((Long) product.get("quantity")).toString());
			returnOrderEntry.setRejectionReason(getRejectionCode(product.get("rejectionReason").toString()));

			returnOrderEntries.add(returnOrderEntry);

			final List<Map<String, Object>> productConditions = getProductConditions(rowsAsNameValuePairs,
					(Integer) product.get("entryNumber"));

			final Set<SAPCpiOutboundReturnOrderEntryConditionModel> returnOrderEntryConditions = new HashSet<>();

			productConditions.forEach(productCondition -> {

				final SAPCpiOutboundReturnOrderEntryConditionModel returnOrderEntryCondition = new SAPCpiOutboundReturnOrderEntryConditionModel();
				returnOrderEntryCondition.setConditionCode(productCondition.get("conditionCode").toString());
				returnOrderEntryCondition.setConditionValue(productCondition.get("conditionValue").toString());
				returnOrderEntryCondition.setConditionCurrencyIsoCode(productCondition.get("conditionCurrencyIsoCode").toString());

				returnOrderEntryConditions.add(returnOrderEntryCondition);

			});

			returnOrderEntry.setSapCpiOutboundReturnOrderEntryConditions(returnOrderEntryConditions);

		});

		sapCpiReturnOrderModel.setSapCpiOutboundReturnOrderEntries(returnOrderEntries);

		final SAPCpiOutboundReturnOrderConfigModel config = getModelService()
				.create(SAPCpiOutboundReturnOrderConfigModel.class);


		final Set<SAPLogicalSystemModel> logicalSystems = getGlobalConfigurationDAO().getSAPGlobalConfiguration()
				.getSapLogicalSystemGlobalConfig();

		if (logicalSystems == null || logicalSystems.isEmpty())
		{
			LOG.error("No Logical system is maintained in back-office");
			return null;
		}
		else
		{

			final SAPLogicalSystemModel logicalSystem = logicalSystems.stream().filter(ls -> ls.isDefaultLogicalSystem()).findFirst()
					.orElse(null);

			if (logicalSystem != null)
			{
				config.setSenderName(logicalSystem.getSenderName());
				config.setSenderPort(logicalSystem.getSenderPort());
				config.setReceiverName(logicalSystem.getSenderName());
				config.setReceiverPort(logicalSystem.getSenderName());
				config.setUrl(logicalSystem.getSapHTTPDestination().getTargetURL());
				config.setClient(logicalSystem.getSapHTTPDestination().getTargetURL().split("sap-client=")[1].substring(0, 3));
				config.setUserName(logicalSystem.getSapHTTPDestination().getUserid());

				final SAPConfigurationModel sapConfiguration = pReturnOrder.getOrder().getStore().getSAPConfiguration();

				config.setSalesOrg(sapConfiguration.getSapcommon_salesOrganization());
				config.setDistributionChannel(sapConfiguration.getSapcommon_distributionChannel());
				config.setSalesOrgResp(sapConfiguration.getSapcommon_salesOrgResponsible());
				config.setProcessType(sapConfiguration.getReturnOrderProcesstype());

				sapCpiReturnOrderModel.setSapCpiConfig(config);
			}
			else
			{
				return null;
			}
		}

		return sapCpiReturnOrderModel;
	}

	/**
	 * @return
	 *
	 */
	private List<Map<String, Object>> getProductConditions(final List<Map<String, Object>> rowsAsNameValuePairs,
			final Integer pEntryNumber)
	{
		return rowsAsNameValuePairs.stream()
				.filter(item -> ((!item.get("conditionCode").toString().equalsIgnoreCase(""))
						&& (((Integer) item.get("conditionEntryNumber")).equals(pEntryNumber))))
				.collect(Collectors.toList());

	}

	/**
	 *
	 */
	private String getRejectionCode(final String pRefundReason)
	{

		String rejectionCode = "";

		final String rejectionCodeQuery = "SELECT {ror.pk} from {" + SAPReturnOrderReasonModel._TYPECODE + " as ror JOIN "
				+ de.hybris.platform.basecommerce.enums.RefundReason._TYPECODE + " as rr ON {ror."
				+ SAPReturnOrderReasonModel.REFUNDREASON + "} = {rr.pk}} where {rr.code}=?pRefundReason";
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(rejectionCodeQuery);
		fQuery.addQueryParameter("pRefundReason", pRefundReason);
		final SearchResult<SAPReturnOrderReasonModel> lReturnOrderReasonResults = flexibleSearchService.search(fQuery);

		if (!lReturnOrderReasonResults.getResult().isEmpty())
		{
			final SAPReturnOrderReasonModel lReturnReasonModel = lReturnOrderReasonResults.getResult().get(0);
			rejectionCode = lReturnReasonModel.getSapReturnReasonCode();
		}

		return rejectionCode;
	}

	/**
	 * @return
	 *
	 */
	private List<Map<String, Object>> getProductsInfo(final List<Map<String, Object>> rowsAsNameValuePairs)
	{
		return rowsAsNameValuePairs.stream()
				.filter(item -> !item.get(PRODUCT_CODE).toString().equalsIgnoreCase(""))
				.collect(Collectors.toList());
	}



	/**
	 *
	 */
	private String getCustomerId(final List<Map<String, Object>> rowsAsNameValuePairs)
	{
		String customerId = null;

		final Optional<Map<String, Object>> partnerCodeRow = rowsAsNameValuePairs.stream()
				.filter(eachMap -> !(((String) eachMap.get("partnerCode")).equalsIgnoreCase(""))).findFirst();

		if (partnerCodeRow.isPresent())
		{
			final Map<String, Object> map = partnerCodeRow.get();
			customerId = map.get("partnerCode").toString();
		}

		return customerId;
	}

	/**
	 *
	 */
	private String getOrderId(final List<Map<String, Object>> rowsAsNameValuePairs)
	{
		String orderId = null;

		final Optional<Map<String, Object>> orderIdRow = rowsAsNameValuePairs.stream()
				.filter(eachMap -> !(((String) eachMap.get("precedingDocumentId")).equalsIgnoreCase(""))).findFirst();

		if (orderIdRow.isPresent())
		{
			final Map<String, Object> map = orderIdRow.get();
			orderId = map.get("precedingDocumentId").toString();
		}

		return orderId;
	}

	/**
	 *
	 */
	private String getProceedingDocumentId(final List<Map<String, Object>> rowsAsNameValuePairs)
	{
		String precedingDocumentId = null;

		final Optional<Map<String, Object>> precedingDocumentIdRow = rowsAsNameValuePairs.stream()
				.filter(eachMap -> !(((String) eachMap.get("orderId")).equalsIgnoreCase(""))).findFirst();

		if (precedingDocumentIdRow.isPresent())
		{
			final Map<String, Object> map = precedingDocumentIdRow.get();
			precedingDocumentId = map.get("orderId").toString();
		}

		return precedingDocumentId;
	}
}
