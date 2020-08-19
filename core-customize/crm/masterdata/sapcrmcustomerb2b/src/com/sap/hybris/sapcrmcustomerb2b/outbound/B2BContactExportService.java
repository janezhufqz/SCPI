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

import static com.sap.hybris.sapcrmcustomerb2b.constants.Sapcrmcustomerb2bConstants.*;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Class to prepare the customer data and send the data to the Data Hub
 */
/**
 * @author C5230256
 *
 */
public class B2BContactExportService {
    private static final Logger LOGGER = Logger
            .getLogger(com.sap.hybris.sapcrmcustomerb2b.outbound.B2BContactExportService.class.getName());

    private CustomerNameStrategy customerNameStrategy;
    private DefaultStoreSessionFacade storeSessionFacade;
    private String buyerCode = "11";

    private String executiveBoardCode = "01";
    private String headOfPurchasingCode = "02";
    private B2BContactExporter exporter;

    public void sendB2BContactRelations(final List<Map<String, Object>> rawRelations) {
        exporter.exportRelations(rawRelations);
    }

    /**
     * puts B2B customer Model data along with the address data to the target
     * map and sends it to the Data Hub
     *
     * @param changedB2bCustomerModel
     */
    public void prepareAndSendB2BContact(final B2BCustomerModel changedB2bCustomerModel,
            final AddressModel addressModel, final String requestSource) {
        List<Map<String, Object>> rawData = new ArrayList<>();
        if (changedB2bCustomerModel.getDefaultB2BUnit() == null) {
            LOGGER.debug("No Default b2b unit set. Not sending data to backend");
            return;
        }
        final Collection<AddressModel> addresses = new ArrayList<AddressModel>();
        addresses.addAll(changedB2bCustomerModel.getAddresses());
        switch (requestSource) {
        case "addressDeletion":
            rawData = processDeletionRequest(changedB2bCustomerModel, addressModel, addresses);
            break;
        case "addressAdditionUpdation":
            rawData = processAddressAddUpdateRequest(changedB2bCustomerModel, addressModel, addresses);
            break;
        default:
            rawData = addCustomerandAddressesToListofMaps(addresses, changedB2bCustomerModel);
            break;
        }

        // sends the data to Datahub in ONE block to ensure that auto
        // compose and publish cannot occur in between
        getExporter().exportContact(rawData);

    }

    private List<Map<String, Object>> processAddressAddUpdateRequest(final B2BCustomerModel changedB2bCustomerModel,
            final AddressModel addressModel, final Collection<AddressModel> addresses) {
        List<Map<String, Object>> rawData;
        if (addressModel != null && addresses != null && !addresses.contains(addressModel)) {
            addresses.add(addressModel);
        }
        rawData = addCustomerandAddressesToListofMaps(addresses, changedB2bCustomerModel);
        return rawData;
    }

    private List<Map<String, Object>> processDeletionRequest(final B2BCustomerModel changedB2bCustomerModel,
            final AddressModel addressModel, final Collection<AddressModel> addresses) {
        List<Map<String, Object>> rawData = new ArrayList<Map<String, Object>>();
        if (addressModel != null && addresses != null && addresses.contains(addressModel)) {
            addresses.remove(addressModel);
        }
        if (addresses != null && !addresses.isEmpty()) {
            rawData = addCustomerandAddressesToListofMaps(addresses, changedB2bCustomerModel);
        }
        final Map<String, Object> deletedAddressData = addCustomerDatatoMap(changedB2bCustomerModel, addressModel,
                true);
        if (deletedAddressData != null)
            rawData.add(deletedAddressData);
        return rawData;
    }

    /**
     * adds all the addresses of the customer, except for the one that has been
     * sent as parameter(modified or deleted), to the list of Maps. Modified or
     * deleted is handled seperately. one address is added as one map in the
     * list of maps
     *
     * @param customerAddresses
     * @param changedB2bCustomerModel
     * @param newAddressModel
     * @return
     */
    public List<Map<String, Object>> addCustomerandAddressesToListofMaps(
            final Collection<AddressModel> customerAddresses, final B2BCustomerModel changedB2bCustomerModel) {
        final Map<String, Object> customerData = addCustomerDatatoMap(changedB2bCustomerModel, null, false);

        // iterates over all the getddresses of the customer, one by one, puts
        // the address
        // data in the map and adds the map to the list
        final List<Map<String, Object>> rawData = new ArrayList<Map<String, Object>>();
        if (!customerAddresses.isEmpty()) {
            final Iterator<AddressModel> iterator = customerAddresses.iterator();
            while (iterator.hasNext()) {
                final Map<String, Object> targetData = new HashMap<String, Object>();
                final AddressModel nextaddress = iterator.next();
                final Map<String, Object> addressData = addAddressDatatoMap(changedB2bCustomerModel, nextaddress,
                        false);

                if (addressData != null) { // merges the customerdata map with
                                            // address data map
                    targetData.putAll(customerData);
                    targetData.putAll(addressData);
                    // adds this map to the list
                    rawData.add(targetData);
                }
            }
        } else {
            rawData.add(customerData);
        }
        return rawData;
    }

    /**
     * populates the map with the customer data first and then adds the address
     * data and returns the map
     *
     * @param b2bCustomerModel
     * @param addressModel
     * @return map
     */
    protected Map<String, Object> addCustomerDatatoMap(final B2BCustomerModel b2bCustomerModel,
            final AddressModel addressModel, final boolean deleteAddress) {
        LOGGER.debug("Preparing Customer data for the customer with ID: " + b2bCustomerModel.getCustomerID()
                + " to be sent to backend");
        // splits the customer name into firstname and lastname
        final String[] names = customerNameStrategy.splitName(b2bCustomerModel.getName());

        final String sessionLanguage = getSessionLanguage();
        final String titleCode = b2bCustomerModel.getTitle() != null ? b2bCustomerModel.getTitle().getCode() : null;

        final B2BUnitModel parentB2BUnit = b2bCustomerModel.getDefaultB2BUnit();
        final String parentB2BUnitUid = (parentB2BUnit != null) ? parentB2BUnit.getUid() : null;

        // adds the customer data in the map as key value pair
        final Map<String, Object> target = new HashMap<String, Object>();
        if (parentB2BUnitUid != null) {
            target.put(UID, b2bCustomerModel.getUid());
            target.put(CONTACT_ID, b2bCustomerModel.getCustomerID());
            target.put(FIRSTNAME, names[0]);
            target.put(LASTNAME, names[1]);
            target.put(SESSION_LANGUAGE, sessionLanguage);
            target.put(TITLE, titleCode);
            target.put(CUSTOMERID, parentB2BUnitUid);
            target.put(PARTYGUID, b2bCustomerModel.getCrmGuid());
            target.put(B2BGROUP, getB2BContactFunction(b2bCustomerModel.getGroups()));
            // after adding customer data, adds the address data to map

            final Map<String, Object> prepareAddressData = addAddressDatatoMap(b2bCustomerModel, addressModel,
                    deleteAddress);
            if (prepareAddressData != null) {
                target.putAll(prepareAddressData);
            }
            return target;
        }
        return null;
    }

    protected Map<String, Object> addAddressDatatoMap(final B2BCustomerModel b2bCustomerModel,
            final AddressModel addressModel, final boolean deleteAddress) {
        if (addressModel == null || b2bCustomerModel == null) {
            return null;
        }

        final Map<String, Object> target = new HashMap<String, Object>();

        String countryIsoCode = null;
        if (addressModel.getCountry() != null) {
            countryIsoCode = addressModel.getCountry().getIsocode();
        }

        final boolean isDefaultAddress = isDefaultAddress(b2bCustomerModel, addressModel);
        final String addressGuid = addressModel.getPublicKey().split("\\|")[1];
        LOGGER.debug("Preparing Address data for the address with GUID : " + addressGuid + " to be sent to backend");

        target.put(COUNTRY, countryIsoCode);
        target.put(STREET, addressModel.getStreetname());
        target.put(PHONE, addressModel.getPhone1());
        target.put(FAX, addressModel.getFax());
        target.put(TOWN, addressModel.getTown());
        target.put(POSTALCODE, addressModel.getPostalcode());
        target.put(STREETNUMBER, addressModel.getStreetnumber());
        target.put(ISDEFAULT, isDefaultAddress);
        target.put(ADDRESSGUID, addressGuid);
        target.put(DELETEADDRESS, deleteAddress);
        return target;
    }

    /**
     * @param b2bCustomerModel
     * @param addressModel
     * @return
     */
    private boolean isDefaultAddress(final B2BCustomerModel b2bCustomerModel, final AddressModel addressModel) {
        boolean isDefaultAddress = false;
        final AddressModel defaultShipmentAddress = b2bCustomerModel.getDefaultShipmentAddress();

        if (defaultShipmentAddress != null && defaultShipmentAddress.equals(addressModel)) {
            isDefaultAddress = true;
        }
        return isDefaultAddress;
    }

    /**
     * @return the exporter
     */
    public B2BContactExporter getExporter() {
        return exporter;
    }

    /**
     * @param exporter
     *            the exporter to set
     */
    public void setExporter(final B2BContactExporter exporter) {
        this.exporter = exporter;
    }

    /**
     * Gets the Contact Function value for the corresponding b2bgroup
     */
    protected String getB2BContactFunction(final Set<PrincipalGroupModel> groups) {
        final StringBuilder b2bGroups = new StringBuilder("");
        for (final PrincipalGroupModel group : groups) {
            b2bGroups.append(group.getUid() + ",");
        }
        if (b2bGroups.toString().contains(B2BADMINGROUP) && b2bGroups.toString().contains(B2BCUSTOMERGROUP)) {
            return headOfPurchasingCode;
        } else if (b2bGroups.toString().contains(B2BADMINGROUP)) {
            return executiveBoardCode;
        } else if (b2bGroups.toString().contains(B2BCUSTOMERGROUP)) {
            return buyerCode;
        }
        return " ";
    }

    /**
     * @return
     */
    private String getSessionLanguage() {
        return getStoreSessionFacade().getCurrentLanguage() != null
                ? getStoreSessionFacade().getCurrentLanguage().getIsocode() : null;
    }

    public DefaultStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    public void setStoreSessionFacade(final DefaultStoreSessionFacade storeSessionFacade) {
        this.storeSessionFacade = storeSessionFacade;
    }

    /**
     * populates the map with the address data of the customer
     *
     * @param addressModel
     * @param b2bCustomerModel
     * @param target
     * @param isDefaultAddress
     */

    /**
     * @return customerNameStrategy
     */
    public CustomerNameStrategy getCustomerNameStrategy() {
        return customerNameStrategy;
    }

    /**
     * @param customerNameStrategy
     */
    public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy) {
        this.customerNameStrategy = customerNameStrategy;
    }

    public void setBuyerCode(String buyerCode) {
        this.buyerCode = buyerCode;
    }

    public void setExecutiveBoardCode(String executiveBoardCode) {
        this.executiveBoardCode = executiveBoardCode;
    }

    public void setHeadOfPurchasingCode(String headOfPurchasingCode) {
        this.headOfPurchasingCode = headOfPurchasingCode;
    }

}
