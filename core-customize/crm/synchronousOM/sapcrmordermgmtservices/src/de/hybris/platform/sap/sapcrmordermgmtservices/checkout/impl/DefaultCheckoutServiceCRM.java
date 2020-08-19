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
package de.hybris.platform.sap.sapcrmordermgmtservices.checkout.impl;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;
import de.hybris.platform.sap.sapcommonbol.constants.SapcommonbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtservices.checkout.impl.DefaultCheckoutService;
import de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Collection;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;


/**
 * @author SAP
 *
 */
public class DefaultCheckoutServiceCRM extends DefaultCheckoutService
{
	private SapPartnerService sapPartnerService;
	protected GenericFactory genericFactory = null;
	private ConfigurationService configurationService;

	public static final String ADDRESS_PUBLICKEY_DELIMITER = "sapcrmordermgmtservices.address.publickey.delimiter";
	public static final String ADDRESSID_POSITION = "sapcrmordermgmtservices.address.addressid.position";
	public static final Log4JWrapper LOGGER = Log4JWrapper.getInstance(DefaultCheckoutServiceCRM.class.getName());

	@Override
	public CartData updateCheckoutCart(final CartData cartData)
	{

		LOGGER.getLogger().info("Inside DefaultCheckoutServiceCRM of updateCheckoutCart method ");
		final Basket currentCart = getBolCartFacade().getCart();
		synchronized (currentCart)
		{

			if (cartData.getPurchaseOrderNumber() != null)
			{
				currentCart.getHeader().setPurchaseOrderExt(cartData.getPurchaseOrderNumber());
			}

			if (cartData.getDeliveryAddress() != null)
			{

				final Collection<AddressModel> allowedDeliveryAddresses = sapPartnerService.getAllowedDeliveryAddresses();
				AddressModel deliveryAddress = null;

				for (final AddressModel address : allowedDeliveryAddresses)
				{
					if (cartData.getDeliveryAddress().getId().equals(address.getPk().toString()))
					{
						deliveryAddress = address;
						break;
					}
				}

				if (deliveryAddress != null)
				{
					this.setDeliveryAddress(deliveryAddress.getSapCustomerID(), deliveryAddress);
				}

			}

			getBolCartFacade().updateCart();


			return getSessionCart();


		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtservices.checkout.CheckoutService#setDeliveryAddress(java.lang.String)
	 */
	private boolean setDeliveryAddress(final String sapCustomerId, final AddressModel deliveryAddress)
	{
		final Basket cart = getBolCartFacade().getCart();
		synchronized (cart)
		{
			final String publicKey = deliveryAddress.getPublicKey();
			cart.getHeader().getShipTo().setId(sapCustomerId);
			if (!StringUtils.isEmpty(publicKey))
			{
				final String addressID = getAddressID(publicKey);
				final Address address = (Address) genericFactory.getBean(SapcommonbolConstants.ALIAS_BO_ADDRESS);
				address.setId(addressID);
				cart.getHeader().getShipTo().setAddress(address);
			}
			return true;
		}
	}

	/**
	 * @param publicKey
	 * @return addressId
	 */
	private String getAddressID(final String publicKey)
	{
		final String delimiter = getConfigurationService().getConfiguration().getString(ADDRESS_PUBLICKEY_DELIMITER);
		final int position = Integer.parseInt(getConfigurationService().getConfiguration().getString(ADDRESSID_POSITION));
		final String[] address = publicKey.split(Pattern.quote(delimiter));
		return address.length == position ? address[position - 1] : null;

	}


	/**
	 * @return the sapPartnerService
	 */
	@Override
	public SapPartnerService getSapPartnerService()
	{
		return sapPartnerService;
	}

	/**
	 * @param sapPartnerService
	 *           the sapPartnerService to set
	 */
	@Override
	public void setSapPartnerService(final SapPartnerService sapPartnerService)
	{
		this.sapPartnerService = sapPartnerService;
	}

	/**
	 * Injected generic factory.
	 *
	 * @param genericFactory
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Injected ConfigurationService.
	 *
	 * @param configurationService
	 */

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;

	}


	/**
	 * @param code
	 * @return
	 */
	public Object getAddressModel(final String code)
	{
		// YTODO Auto-generated method stub
		return null;
	}


	/**
	 * @param addressModel
	 */
	public void setDeliveryAddressForBackEnd(final Object addressModel)
	{
		// YTODO Auto-generated method stub

	}
}
