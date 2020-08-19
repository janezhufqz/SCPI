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
package com.sap.hybris.sapcrmcustomerb2b.outbound;

import static com.sap.hybris.sapcrmcustomerb2b.constants.Sapcrmcustomerb2bConstants.B2BGROUP;
import static com.sap.hybris.sapcrmcustomerb2b.constants.Sapcrmcustomerb2bConstants.CONTACT_ID;
import static com.sap.hybris.sapcrmcustomerb2b.constants.Sapcrmcustomerb2bConstants.CUSTOMERID;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Triggers export of customer to Data Hub whenever a new b2b customer is created or an existing one is edited
 *
 * @author C5230256
 *
 */
public class DefaultB2BCustomerInterceptor implements ValidateInterceptor<B2BCustomerModel>
{
    private static final Logger LOGGER = Logger
            .getLogger(com.sap.hybris.sapcrmcustomerb2b.outbound.DefaultB2BCustomerInterceptor.class.getName());
    private B2BContactExportService b2bContactExportService;
    private CommonUtility commonutil;

    public B2BContactExportService getB2bContactExportService()
    {
        return b2bContactExportService;
    }

    public void setB2bContactExportService(final B2BContactExportService b2bContactExportService)
    {
        this.b2bContactExportService = b2bContactExportService;
    }

    /**
     * @param customerModel
     * @param ctx
     * @throws InterceptorException
     */
    @Override
    public void onValidate(final B2BCustomerModel customerModel, final InterceptorContext ctx) throws InterceptorException
    {

        final String requestSource = detectRequestSource(customerModel, ctx);
        switch (requestSource)
        {
            case "newmodelreplicationfrombackend":
                customerModel.setB2bUnitRelationSent(true);
                break;
            case "notification":
                processNotification(customerModel);
                break;
            case "modelupdationfromfrontend":
                replicateContactData(ctx, customerModel);
                break;
            default:
                break;
        }

    }

    /**
     * @param customerModel
     */
    private void processNotification(final B2BCustomerModel customerModel)
    {
        LOGGER.debug("Processing the notification");
        replicateRelationshipData(customerModel);
        customerModel.setB2bUnitRelationSent(true);
        customerModel.setLoginDisabled(false);
    }

    public String detectRequestSource(final B2BCustomerModel customerModel, final InterceptorContext ctx)
    {
        String requestType = "other";
        if (ctx.isNew(customerModel))
        {
            if (customerModel.getCrmGuid() != null)
            {
                requestType = "newmodelreplicationfrombackend";
            }
        }
        else
        {

            if (ctx.isModified(customerModel, "sapIsReplicated") && !customerModel.getB2bUnitRelationSent())
            {
                requestType = "notification";
            }
            else if (!ctx.isModified(customerModel, "sapIsReplicated"))
            {
                requestType = "modelupdationfromfrontend";
            }
        }
        return requestType;
    }

    /**
     * sends the b2b contact and b2b unit relationship data to datahub after when the notification of the replication of
     * b2b contact at CRM is recieved.
     */
    private void replicateRelationshipData(final B2BCustomerModel b2bCustomerModel)
    {
        final Map<String, Object> target = new HashMap<String, Object>();
        final B2BUnitModel parentB2BUnit = b2bCustomerModel.getDefaultB2BUnit();
        final List<Map<String, Object>> rawData = new ArrayList<>();
        final String parentB2BUnitUid = (parentB2BUnit != null) ? parentB2BUnit.getUid() : null;
        // for relationship replication three informations are sent
        if (parentB2BUnitUid != null)
        {
            target.put(CONTACT_ID, b2bCustomerModel.getCustomerID());
            target.put(CUSTOMERID, parentB2BUnitUid);
            target.put(B2BGROUP, b2bContactExportService.getB2BContactFunction(b2bCustomerModel.getGroups()));
        }

        rawData.add(target);
        // sends the relationship data to datahub, which in turn will trigger
        // relationship idoc to be sent to backend
        b2bContactExportService.sendB2BContactRelations(rawData);
        LOGGER.debug("Relationship Data has been sent to backend");
    }

    private void replicateContactData(final InterceptorContext ctx, final B2BCustomerModel customerModel)
    {
        final boolean replicateb2bContact = commonutil.shallB2BCustomerBeReplicatedToBackend();
        // only if replication of user is requested start publishing to Data Hub
        // this also prevents the data coming from datahub to be sent again to
        // datahub, since whenever something comes from datahub the base store
        // will be null
        // and hence replicateb2bcontact will be false
        if (replicateb2bContact && (customerModel instanceof B2BCustomerModel))
        {
            LOGGER.debug("Sending Modified contact details to datahub...");

            if (ctx.isModified(customerModel, CustomerModel.NAME) || ctx.isModified(customerModel, CustomerModel.TITLE)
                    || ctx.isModified(customerModel, CustomerModel.UID)
                    || ctx.isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS))
            {
                b2bContactExportService.prepareAndSendB2BContact(customerModel, null,"b2bcustomerinterceptor");
            }
            if(ctx.isModified(customerModel,CustomerModel.GROUPS)){
            	replicateRelationshipData(customerModel);
            }
        }
        else if (!replicateb2bContact && LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Customer " + customerModel.getUid()
                    + " was not sent to Data Hub. Either ReplicateRegisteredUser field is not active in SAP Global Configuration associated with the current base store OR The data is already there in the backend");
            LOGGER.debug("Customer Default shipment address modified =  "
                    + ctx.isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS));
            LOGGER.debug("Customer name modified = " + ctx.isModified(customerModel, CustomerModel.NAME));
            LOGGER.debug("Customer title modified = " + ctx.isModified(customerModel, CustomerModel.TITLE));
            LOGGER.debug("Customer sapContactId =  " + customerModel.getSapContactID());
        }
    }

    /**
     * @return the commonutil
     */
    public CommonUtility getCommonutil()
    {
        return commonutil;
    }

    /**
     * @param commonutil
     *           the commonutil to set
     */
    public void setCommonutil(final CommonUtility commonutil)
    {
        this.commonutil = commonutil;
    }

}
