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

package com.sap.genil;

import de.hybris.platform.sap.core.jco.connection.JCoConnection;

import com.sap.conn.jco.JCoException;
import com.sap.genil.GenilConst.GenilFeature;
import com.sap.genil.exception.GenilBackendException;
import com.sap.genil.rfc.RFCInput;


/**
 * Defines a stateless proxy. <br>
 *
 * @author SAP
 * @version 1.0
 * 
 *  @author C5230248 --Modified by Removing Genil JCoConnection and added Hybris JCoConnection
 * 													
 */
class GenilProxyStateless extends GenilProxy
{

	/**
	 * Standard constructor. <br>
	 * 
	 * @param connection
	 * @param componentSet
	 * @param ignoreModelError
	 * @throws GenilBackendException
	 */
	GenilProxyStateless(final JCoConnection connection, final String componentSet, final boolean ignoreModelError)
			throws GenilBackendException, JCoException
	{
		super(connection, componentSet, ignoreModelError, GenilConst.COMPONENT_SET_INIT_AND_LOAD);
	}

	@Override
	protected RFCInput getRFCInput(final GenilFeature feature)
	{
		return new RFCInput(feature.getRFCStateLess());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.wec.tc.core.backend.genil.GenilProxy#prepareReadRequest(com.sap
	 * .wec.tc.core.backend.genil.GenilReadRootRequestContainer) in addition to the default parameters, the component set
	 * is required for stateless read
	 */
	@Override
	protected RFCInput prepareReadRequest(final GenilReadRootRequestContainer rootReqCont)
	{

		final RFCInput rfcInput = super.prepareReadRequest(rootReqCont);

		rfcInput.addImportParameter("IV_COMPONENT_SET", this.componentSet);

		return rfcInput;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.wec.tc.core.backend.genil.GenilProxy#prepareDQueryRequest(com
	 * .sap.wec.tc.core.backend.genil.GenilQueryRequestContainer) in addition to the default parameters, the component
	 * set is required for stateless read
	 */
	@Override
	protected RFCInput prepareDQueryRequest(final GenilQueryRequestContainer queryReqCont)
	{
		final RFCInput rfcInput = super.prepareDQueryRequest(queryReqCont);
		rfcInput.addImportParameter("IV_COMPONENT_SET", this.componentSet);
		return rfcInput;
	}

	@Override
	protected void prepareQueryRequest(final GenilQueryRequestContainer queryReqCont, final RFCInput rfcInput)
	{

		super.prepareQueryRequest(queryReqCont, rfcInput);
		rfcInput.addImportParameter("IV_COMPONENT_SET", this.componentSet);
	}

	@Override
	protected boolean synchronizeAbapModel(final char componentSetLoadLevel) throws GenilBackendException
	{
		isInitialized = true;
		return isInitialized;
	}

	@Override
	protected void synchronizeJavaModel(final boolean ignoreModelErrors) throws GenilBackendException
	{
		if (isInitialized)
		{
			readModel(ignoreModelErrors);
		}
	}

}
