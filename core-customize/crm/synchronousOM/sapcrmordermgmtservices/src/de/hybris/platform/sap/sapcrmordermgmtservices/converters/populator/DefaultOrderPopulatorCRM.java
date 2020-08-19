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
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultOrderPopulator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;


/**
 * @author C5230711
 *
 */
public class DefaultOrderPopulatorCRM<SOURCE extends Order, TARGET extends OrderData> extends
		DefaultOrderPopulator<SOURCE, TARGET>
{

	private static final Logger LOG = Logger.getLogger(DefaultOrderPopulatorCRM.class);
	private Converter<AddressModel, AddressData> addressConverter;
	private B2BCustomerService b2bCustomerService;
	private B2BUnitService b2bUnitService;
	@Autowired
	private SessionService sessionService;
	@Autowired
	FlexibleSearchService flexibleSearchService;

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		super.populate(source, target);
		target.setCode(getExternalFormat(source.getHeader().getSalesDocNumber()));

		Map<String, String> billingDocNumbers = null;

		if (source.getHeader() != null && source.getHeader().getSuccessorList() != null)
		{
			billingDocNumbers = new HashMap<String, String>();
			final List<ConnectedDocument> connectedDocuments = source.getHeader().getSuccessorList();

			for (final ConnectedDocument connectedDocument : connectedDocuments)
			{
				if (null != connectedDocument && StringUtils.isNotBlank(connectedDocument.getDocNumber()))
				{
					billingDocNumbers.put(connectedDocument.getDocNumber(), connectedDocument.getBusObjectType());
					sessionService.setAttribute(connectedDocument.getDocNumber() + "_data", getExternalFormat(source.getTechKey()
							.getIdAsString()));
				}
			}
			target.setBillingDocNumbers(billingDocNumbers);
		}

	}

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
			if (!CollectionUtils.isEmpty(resultList))
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

}
