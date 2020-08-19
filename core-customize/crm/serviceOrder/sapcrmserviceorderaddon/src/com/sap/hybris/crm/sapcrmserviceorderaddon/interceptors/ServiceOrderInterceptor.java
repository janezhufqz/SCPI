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
package com.sap.hybris.crm.sapcrmserviceorderaddon.interceptors;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.sap.hybris.crm.sapserviceorderservices.model.ServiceOrderEntryModel;
import com.sap.hybris.crm.sapserviceorderservices.model.ServiceOrderModel;

/**
 * 
 * @author C5230256
 *
 */
public class ServiceOrderInterceptor implements ValidateInterceptor<ServiceOrderModel> {
    private static final Logger LOGGER = Logger
            .getLogger(com.sap.hybris.crm.sapcrmserviceorderaddon.interceptors.ServiceOrderInterceptor.class.getName());

    private FlexibleSearchService flexibleSearchService;

    @Resource
    ModelService modelService;

    /**
     * This method checks first whether deleteOrderEntries is true or not. If
     * true it means that the request is from backend. It then checks whether it
     * is a new model or old one. For an old model this method deletes the
     * addresses owned by the order and also deletes all the order entries. This
     * has been done to take care of deletion of line items and deletion of
     * scheduling from backend.
     * 
     * @param serviceOrderModel
     * @param ctx
     * @throws InterceptorException
     */
    @Override
    public void onValidate(final ServiceOrderModel serviceOrderModel, final InterceptorContext ctx)
            throws InterceptorException {
        if (serviceOrderModel.isDeleteOrderEntries()) {
            List<AbstractOrderEntryModel> list = serviceOrderModel.getEntries();
            AddressModel paymentAddress = serviceOrderModel.getPaymentAddress();
            AddressModel deliveryAddress = serviceOrderModel.getDeliveryAddress();
            Iterator<AbstractOrderEntryModel> iterator = null;
            if (list != null) {
                iterator = list.iterator();
                while (iterator.hasNext()) {
                    ServiceOrderEntryModel model = (ServiceOrderEntryModel) iterator.next();
                    modelService.remove(model);
                }
            }
            String orderPk = null;
            if (!ctx.isNew(serviceOrderModel)) {
                orderPk = serviceOrderModel.getPk().toString();
            }

            if (paymentAddress != null) {
                String addressPublicKey = paymentAddress.getPublicKey();
                deleteAddressofOrder(addressPublicKey, orderPk);
            }
            if (deliveryAddress != null) {
                String addressPublicKey = deliveryAddress.getPublicKey();
                deleteAddressofOrder(addressPublicKey, orderPk);
            }
            serviceOrderModel.setEntries(null);
            serviceOrderModel.setDeleteOrderEntries(false);

        }
    }

    /**
     * This method deletes the address with the given public key whose owner is
     * the given order
     * 
     * @param addressPublicKey
     * @param orderPk
     */
    private void deleteAddressofOrder(final String addressPublicKey, final String orderPk) {
        Object returnedObject = getAddressToBeDeleted(addressPublicKey, orderPk);
        if (returnedObject instanceof AddressModel) {
            AddressModel address = (AddressModel) returnedObject;
            modelService.remove(address);
        }
    }

    public Object getAddressToBeDeleted(final String publickey, String orderPk) {
        final FlexibleSearchQuery flexibleSearchQuery = getAddressFlexibleSearchQuery(publickey, orderPk);
        try {
            if (flexibleSearchQuery != null) {
                return this.flexibleSearchService.searchUnique(flexibleSearchQuery);
            }
        } catch (final Exception e) {
            LOGGER.debug("Some exception occured in executing the search query. Address could not be deleted" + e);
            return null;
        }
        return null;
    }

    protected FlexibleSearchQuery getAddressFlexibleSearchQuery(final String publicKey, final String orderPk) {
        if (publicKey == null || orderPk == null)
            return null;
        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
                "SELECT {c:pk} FROM {ADDRESS AS c} WHERE  {c.PUBLICKEY} = ?publickey AND {c.OWNER}=?orderpk AND {c.duplicate}=FALSE");
        flexibleSearchQuery.addQueryParameter("publickey", publicKey);
        flexibleSearchQuery.addQueryParameter("orderpk", orderPk);
        return flexibleSearchQuery;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    /**
     * @param flexibleSearchService
     */
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
