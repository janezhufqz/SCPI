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

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.backend.jco.BackendBusinessObjectBaseJCo;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.bol.logging.LogSeverity;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionFactory;
import de.hybris.platform.sap.core.jco.connection.JCoStateful;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.exceptions.BackendOfflineException;
import de.hybris.platform.sap.sappricingbol.backend.impl.SapPricingCachedBackendERP;
import de.hybris.platform.sap.sappricingbol.backend.interf.SapPricingBackend;
import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction;
import de.hybris.platform.sap.sappricingbol.converter.ConversionService;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Required;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import com.sap.hybris.crm.sapcrmpricingbol.constants.SapcrmpricingbolConstants;



/**
 * @author C5229090
 *
 *
 *         connects to the Backendsystem and retrieves the pricing information
 */
@BackendType("CRM")
//public class SapPricingBackendCRM extends BackendBusinessObjectBaseJCo implements SapCrmPricingBackend
public class SapPricingBackendCRM extends BackendBusinessObjectBaseJCo implements SapPricingBackend
{

	static final private Log4JWrapper Logger = Log4JWrapper.getInstance(SapPricingBackendCRM.class.getName());

	@Resource(name = "sapCoreJCoManagedConnectionFactory")
	protected JCoManagedConnectionFactory managedConnectionFactory; //NOPMD

	private SapCrmPricingMapper pricingMapper;
	private SapPricingCachedBackendERP cacheAccess;

	@Override
	public List<PriceInformation> readPriceInformationForProducts(final List<ProductModel> productModels,
			final SapPricingPartnerFunction partnerFunction, final ConversionService conversionService) throws BackendException
	{

		Logger.entering("readPriceInformationForProducts(ProductModel, SapPricingPartnerFunction, ConversionService)");

		List<PriceInformation> priceInformationList = cacheAccess.readCachedPriceInformationForProducts(productModels,
				partnerFunction);
		JCoConnection connection = null;

		// Check if the catalog price is cached
		if (null != priceInformationList && !priceInformationList.isEmpty())
		{
			return priceInformationList;
		}
		try
		{
			// Get JCo connection

			connection = managedConnectionFactory.getManagedConnection(getDefaultConnectionName(), this.getClass().getName());
			if (connection != null && connection.isBackendOffline())
			{
				//Since RFC is disabled, price can not be retrieved from Backend system
				Logger.log(LogSeverity.ERROR, LogCategories.APPS_COMMON_RESOURCES,
						"Backend RFC is disabled. Hence price cannot be retrieved... reverting to default Cached price");
				return priceInformationList;
			}

			// Get function module
			final JCoFunction function = connection.getFunction(SapcrmpricingbolConstants.SPE_CALCULATE_PRICE);

			// Get import parameters

			final JCoParameterList tables = function.getTableParameterList();

			// Fill import parameters
			pricingMapper.fillImportParameters(function, productModels, partnerFunction);
			Logger.entering("SapPricingBackendCRM : readPriceInformationForProducts(...) :: Now Executing..." + function.getName());
			// Execute
			connection.execute(function);

			// Read back-end messages
			final JCoTable etMessage = tables.getTable("ET_MESSAGES");
			logMesages(etMessage);

			// Read catalog price
			final JCoTable resultTable = tables.getTable("ET_ITEM_CALC_MAIN_RESLT");

			//Read result Structure
			priceInformationList = pricingMapper.readResultExport(resultTable);

			// Cache the price
			cacheAccess.cachePriceInformationForProducts(productModels, partnerFunction, priceInformationList);
		}
		catch (final BackendOfflineException e)
		{
			Logger.log(LogSeverity.ERROR, LogCategories.APPS_COMMON_RESOURCES, "Unable to connect to Backend System" + e);
		}
		finally
		{
			if (!connection.isBackendOffline())
			{
				closeConnection(connection);
			}
		}
		Logger.exiting();

		return priceInformationList;
	}

	@Override
	public void readPricesForCart(final AbstractOrderModel order, final SapPricingPartnerFunction partnerFunction,
			final ConversionService conversionService) throws BackendException, CommunicationException
	{
		Logger.entering("readPricesForCart(AbstractOrderModel,SapPricingPartnerFunction, ConversionService)");

		JCoConnection connection = null;


		try
		{
			// Get JCo connection
			connection = managedConnectionFactory.getManagedConnection(getDefaultConnectionName(), this.getClass().getName());

			// Get function module
			final JCoFunction function = connection.getFunction(SapcrmpricingbolConstants.SPE_CALCULATE_PRICE);

			// Get table parameters
			final JCoParameterList tables = function.getTableParameterList();

			// Fill import parameters
			pricingMapper.fillImportParameters(function, order, partnerFunction);

			// Execute
			connection.execute(function);

			// Read back-end messages
			final JCoTable etMessage = tables.getTable("ET_MESSAGES");
			logMesages(etMessage);

			// Read catalog price
			final JCoTable resultTable = tables.getTable("ET_ITEM_CALC_MAIN_RESLT");

			//Read result Structure
			pricingMapper.readResultExport(order, resultTable);

		}
		catch (final BackendOfflineException e)
		{
			Logger.log(LogSeverity.ERROR, LogCategories.APPS_COMMON_RESOURCES, "Unable to connect to Backend System" + e);
		}
		finally
		{
			if (!connection.isBackendOffline())
			{
				closeConnection(connection);
			}
		}
		Logger.exiting();
	}

	protected void closeConnection(final JCoConnection connection)
	{
		try
		{
			if (connection != null)
			{
				((JCoStateful) connection).destroy();
			}
		}
		catch (final BackendException ex)
		{
			Logger.log(LogSeverity.ERROR, LogCategories.APPS_COMMON_RESOURCES, "Error during JCoStateful connection close! " + ex);

		}
	}

	protected void logMesages(final JCoTable etMessage)
	{
		if (!etMessage.isEmpty())
		{
			for (int i = 0; i < etMessage.getNumRows(); i++)
			{
				etMessage.setRow(i);
				if (etMessage.getString("TYPE").contentEquals("E") || etMessage.getString("TYPE").contentEquals("W")
						|| etMessage.getString("TYPE").contentEquals("I"))
				{
					Logger.log(LogSeverity.ERROR, LogCategories.APPLICATIONS, etMessage.getString("MESSAGE"));
				}
			}
		}
	}

	/**
	 * @return CacheAccess
	 */
	public SapPricingCachedBackendERP getCacheAccess()
	{
		return cacheAccess;
	}

	/**
	 * @param cacheAccess
	 */
	@Required
	public void setCacheAccess(final SapPricingCachedBackendERP cacheAccess)
	{
		this.cacheAccess = cacheAccess;
	}

	public void setManagedConnectionFactory(final JCoManagedConnectionFactory managedConnectionFactory)
	{
		this.managedConnectionFactory = managedConnectionFactory;
	}

	/**
	 * @return the pricingMapper
	 */
	public SapCrmPricingMapper getPricingMapper()
	{
		return pricingMapper;
	}

	/**
	 * @param pricingMapper
	 *           the pricingMapper to set
	 */
	public void setPricingMapper(final SapCrmPricingMapper pricingMapper)
	{
		this.pricingMapper = pricingMapper;
	}

}
