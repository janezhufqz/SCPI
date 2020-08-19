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
package de.hybris.platform.sap.sapcrmordermgmtservices.converters.populator;




import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapcrmordermgmtservices.inbound.SAPCRMB2BUnitServiceImpl;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultCartPopulator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;


/**
 * @author C5230711
 *
 */
public class DefaultCartPopulatorCRM<SOURCE extends Basket, TARGET extends CartData> extends DefaultCartPopulator<SOURCE, TARGET>
{
	private static final Logger LOG = Logger.getLogger(DefaultCartPopulatorCRM.class);
	private Converter<AddressModel, AddressData> addressConverter;
	private B2BCustomerService b2bCustomerService;
	private B2BUnitService b2bUnitService;
	/** Factory to access SAP session beans. */
	protected GenericFactory genericFactory = null;

	@Autowired
	FlexibleSearchService flexibleSearchService;
	/** The sap crm b2 b unit service. */
	private SAPCRMB2BUnitServiceImpl sapCRMB2BUnitService;

	@Override
	protected void populateDeliveryAddress(final SOURCE source, final TARGET target)
	{

		final ShipTo shipTo = source.getHeader().getShipTo();
		if (shipTo != null)
		{
			final String sapShipAddrNumber = shipTo.getAddress().getId();
			final AddressModel addressModel = getSapShipAddress(sapShipAddrNumber);

			if (addressModel != null)
			{
				final AddressData addressData = addressConverter.convert(addressModel);
				target.setDeliveryAddress(addressData);
			}
		}
		else
		{
			LOG.debug("No shipTo available");
		}
	}

	/**
	 * @param id
	 * @return
	 */
	public AddressModel getSapShipAddress(final String id)
	{
		final B2BCustomerModel b2bCustomer = (B2BCustomerModel) b2bCustomerService.getCurrentB2BCustomer();
		final B2BUnitModel parent = (B2BUnitModel) b2bUnitService.getParent(b2bCustomer);
		final String queryParam = parent.getUid() + "|" + id;
		final String query = "SELECT {pk} FROM {address} where {publicKey} = ?id";

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		fQuery.addQueryParameter("id", queryParam);
		final SearchResult<AddressModel> searchResult = flexibleSearchService.search(fQuery);
		if (searchResult != null && searchResult.getResult() != null)
		{
			final List<AddressModel> resultList = searchResult.getResult();
			if (CollectionUtils.isNotEmpty(resultList))
			{
				final AddressModel sapShipAddrModel = resultList.get(0);
				if (sapShipAddrModel != null)
				{
					return sapShipAddrModel;
				}
			}
		}
		return null;
	}


	@Override
	protected void populateHeader(final SOURCE source, final TARGET target)
	{
		final Header header = source.getHeader();
		final String shipCond = header.getShipCond();
		final String incoTerms1 = header.getIncoTerms1();
		final String incoTerms2 = header.getIncoTerms2();
		Optional<B2BUnitModel> b2bModel = null;
		if (StringUtils.isEmpty(shipCond))
		{
			//Get Shipping Condition from Sales_Area
			b2bModel = Optional.ofNullable(determineB2BUnitOfCurrentB2BCustomer());
			header.setShipCond(StringUtils.isEmpty(b2bModel.get().getShippingconditions()) ? getDefaultAllowedDeliveryType()
					: b2bModel.get().getShippingconditions());
		}
		if (StringUtils.isEmpty(incoTerms1) && StringUtils.isEmpty(incoTerms2))
		{
			if (b2bModel == null)
			{
				b2bModel = Optional.ofNullable(determineB2BUnitOfCurrentB2BCustomer());
			}
			//Fill Incoterms from B2BModel which is sales_area
			header.setIncoTerms1(b2bModel.isPresent() ? b2bModel.get().getIncoterms1() : "");
			header.setIncoTerms2(b2bModel.isPresent() ? b2bModel.get().getIncoterms2() : "");

		}
		source.setHeader(header);
		target.setPurchaseOrderNumber(header.getPurchaseOrderExt());
		super.populateHeader(source, target);

	}

	/**
	 * Gets the default allowed delivery type from list of allowed delivery types.
	 *
	 * @return the default allowed delivery type
	 */
	private String getDefaultAllowedDeliveryType()
	{
		String defaultDeliveryType = null;
		final TransactionConfiguration transactionConfiguration = getConfiguration();
		Map<String, String> allowedDeliveryMap = null;
		try
		{
			allowedDeliveryMap = transactionConfiguration.getAllowedDeliveryTypes(false);
			if (!org.springframework.util.CollectionUtils.isEmpty(allowedDeliveryMap))
			{
				final Map.Entry<String, String> entry = allowedDeliveryMap.entrySet().iterator().next();
				defaultDeliveryType = entry.getKey();

			}
		}
		catch (final CommunicationException e)
		{
			throw new ApplicationBaseRuntimeException("Could not fetch delivery types", e);
		}
		return defaultDeliveryType;
	}


	private B2BUnitModel determineB2BUnitOfCurrentB2BCustomer()
	{
		final B2BCustomerModel b2bCustomer = (B2BCustomerModel) b2bCustomerService.getCurrentB2BCustomer();
		final B2BUnitModel parent = getSapCRMB2BUnitService().getParent(b2bCustomer);
		return parent;
	}

	/**
	 * Gets the configuration.
	 *
	 * @return the configuration
	 */
	TransactionConfiguration getConfiguration()
	{
		return (TransactionConfiguration) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_TRANSACTION_CONFIGURATION);
	}

	/**
	 * @return the addressConverter
	 */
	public Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	/**
	 * @param addressConverter
	 *           the addressConverter to set
	 */
	@Override
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	public void setB2bCustomerService(final B2BCustomerService b2bCustomerService)
	{
		this.b2bCustomerService = b2bCustomerService;
	}


	public void setB2bUnitService(final B2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}






	/**
	 * Injected generic factory.
	 *
	 * @param genericFactory
	 *           the new generic factory
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	/**
	 * @return the sapCRMB2BUnitService
	 */
	public SAPCRMB2BUnitServiceImpl getSapCRMB2BUnitService()
	{
		return sapCRMB2BUnitService;
	}

	/**
	 * @param sapCRMB2BUnitService
	 *           the sapCRMB2BUnitService to set
	 */
	public void setSapCRMB2BUnitService(final SAPCRMB2BUnitServiceImpl sapCRMB2BUnitService)
	{
		this.sapCRMB2BUnitService = sapCRMB2BUnitService;
	}
}
