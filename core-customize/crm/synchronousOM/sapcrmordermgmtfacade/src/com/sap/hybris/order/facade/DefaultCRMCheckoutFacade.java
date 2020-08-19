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
package com.sap.hybris.order.facade;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.sap.sapcrmordermgmtservices.checkout.impl.DefaultCheckoutServiceCRM;
import de.hybris.platform.sap.sapordermgmtb2bfacades.order.impl.SapOrdermgmtB2BAcceleratorCheckoutFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;


/**
 * @author C5230727
 *
 */
public class DefaultCRMCheckoutFacade extends SapOrdermgmtB2BAcceleratorCheckoutFacade
{
	private DefaultCheckoutServiceCRM defaultCheckoutServiceCRM;
	private Converter<CartModel, CartData> cartConverter;


	/**
	 * @return the cartConverter
	 */
	public Converter<CartModel, CartData> getCartConverter()
	{
		return cartConverter;
	}



	/**
	 * @param cartConverter
	 *           the cartConverter to set
	 */
	@Required
	public void setCartConverter(final Converter<CartModel, CartData> cartConverter)
	{
		this.cartConverter = cartConverter;
	}



	/**
	 * @return the defaultCheckoutServiceCRM
	 */
	public DefaultCheckoutServiceCRM getDefaultCheckoutServiceCRM()
	{
		return defaultCheckoutServiceCRM;
	}



	/**
	 * @param defaultCheckoutServiceCRM
	 *           the defaultCheckoutServiceCRM to set
	 */
	@Required
	public void setDefaultCheckoutServiceCRM(final DefaultCheckoutServiceCRM defaultCheckoutServiceCRM)
	{
		this.defaultCheckoutServiceCRM = defaultCheckoutServiceCRM;
	}



	@Override
	public boolean setDeliveryAddress(final AddressData addressData)
	{

		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			AddressModel addressModel = null;
			if (addressData != null)
			{
				if (addressData.getId() != null)
				{
					addressModel = getDeliveryAddressModelForCode(addressData.getId());
				}
				else
				{
					addressModel = createDeliveryAddressModel(addressData, cartModel);
				}
			}

			final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cartModel);
			parameter.setAddress(addressModel);
			parameter.setIsDeliveryAddress(true);

			getCommerceCheckoutService().setDeliveryAddress(parameter);
			updateCartToBackend(getCart());
		}
		return false;

	}



	/**
	 * @param cart
	 */
	private void updateCartToBackend(final CartModel cart)
	{
		updateCheckoutCart(getCartConverter().convert(cart));

	}


}
