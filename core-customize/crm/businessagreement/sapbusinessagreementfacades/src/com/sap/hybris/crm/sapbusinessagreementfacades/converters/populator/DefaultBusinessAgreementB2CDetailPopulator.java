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
package com.sap.hybris.crm.sapbusinessagreementfacades.converters.populator;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.sap.hybris.crm.sapcrmmodel.util.XSSFilterUtil;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementHeaderModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementPartnersModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementRuleHeaderModel;
import com.sap.hybris.sapbusinessagreement.model.CRMBusinessAgreementClassModel;
import com.sap.hybris.sapbusinessagreement.model.CRMBusinessAgreementPaymentMethodModel;
import com.sap.hybris.sapbusinessagreementfacades.constants.SapbusinessagreementfacadesConstants;
import com.sap.hybris.sapbusinessagreementfacades.data.BusinessAgreementDetailsData;
import com.sap.hybris.sapbusinessagreementfacades.data.BusinessAgreementPaymentMethod;

/**
 * populate details of business agreement for B2C Customer
 */
public class DefaultBusinessAgreementB2CDetailPopulator implements
        Populator<BusinessAgreementModel, BusinessAgreementDetailsData> {
    @Resource
    CustomerAccountService customerAccountService;
    private Converter<AddressModel, AddressData> addressConverter;

    private Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;
    private Converter<CRMBusinessAgreementPaymentMethodModel, BusinessAgreementPaymentMethod> paymentMethodConverter;

    public Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> getCreditCardPaymentInfoConverter() {
        return creditCardPaymentInfoConverter;
    }

    public void setCreditCardPaymentInfoConverter(
            final Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter) {
        this.creditCardPaymentInfoConverter = creditCardPaymentInfoConverter;
    }


    public Converter<CRMBusinessAgreementPaymentMethodModel, BusinessAgreementPaymentMethod> getPaymentMethodConverter() {
        return paymentMethodConverter;
    }

    public void setPaymentMethodConverter(
            final Converter<CRMBusinessAgreementPaymentMethodModel, BusinessAgreementPaymentMethod> paymentMethodConverter) {
        this.paymentMethodConverter = paymentMethodConverter;
    }

    public Converter<AddressModel, AddressData> getAddressConverter() {
        return addressConverter;
    }

    public void setAddressConverter(
            final Converter<AddressModel, AddressData> addressConverter) {
        this.addressConverter = addressConverter;
    }

    @Override
    public void populate(final BusinessAgreementModel source,
            final BusinessAgreementDetailsData target) {
        target.setBusinessAgreementID(source.getId());
        target.setDescription(XSSFilterUtil.filter(source.getText()));

        final CustomerModel customerModel = source.getCustomer();
        target.setPartnerName(customerModel.getDisplayName());
        final BusinessAgreementHeaderModel businessAgreementHeaderModel = getBusinessAgreementHeaderModel(source);

        if (businessAgreementHeaderModel != null) {
            target.setRefnumber(XSSFilterUtil
                    .filter(businessAgreementHeaderModel.getRefnumber()));
            target.setIsDefault(businessAgreementHeaderModel
                    .getBusinessagreementdefault());

            final CRMBusinessAgreementClassModel crmBusinessAgreementClassModel = businessAgreementHeaderModel
                    .getBusinessagreementclass();
            if (crmBusinessAgreementClassModel != null) {
                target.setCollectivebill(crmBusinessAgreementClassModel
                        .getCollectivebill());
                target.setBusinessAgreementClass(crmBusinessAgreementClassModel
                        .getText());
            }
            // customer address
            target.setAddressData(getAddressData(source.getCustomer(),
                    businessAgreementHeaderModel.getAddresscorrespondenceguid()));

            final BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel = getBusinessAgreementRuleHeaderModel(businessAgreementHeaderModel);

            if (businessAgreementRuleHeaderModel != null) {
                // populating paymentViaInfo
                BusinessAgreementPaymentMethod paymentViaInfo = paymentMethodConverter
                        .convert(businessAgreementRuleHeaderModel
                                .getPaymentmethodinc());

                paymentViaInfo = populatePaymentDetails(
                        businessAgreementHeaderModel, paymentViaInfo,
                        SapbusinessagreementfacadesConstants.ALTERNATIVE_PAYER,
                        businessAgreementHeaderModel.getAddresspayerguid(),
                        source.getCustomer());
                target.setPaymentVia(paymentViaInfo);

                // populating refundViaInfo
                BusinessAgreementPaymentMethod refundViaInfo = paymentMethodConverter
                        .convert(businessAgreementRuleHeaderModel
                                .getPaymentmethodout());

                refundViaInfo = populatePaymentDetails(
                        businessAgreementHeaderModel, refundViaInfo,
                        SapbusinessagreementfacadesConstants.PAYMENT_RECIPIENT,
                        businessAgreementHeaderModel.getAddressrecipientguid(),
                        source.getCustomer());
                target.setRefundVia(refundViaInfo);

                populateCorrespondenceData(businessAgreementHeaderModel,
                        source, target, businessAgreementRuleHeaderModel);
                populateDunningData(businessAgreementHeaderModel, source,
                        target, businessAgreementRuleHeaderModel);
                populateTaxData(businessAgreementHeaderModel, source, target);
            }
        }
    }

    /**
     *
     * @param businessAgreementHeaderModel
     * @param paymentInfo
     * @param partnerFunction
     * @param addressguid
     * @param currentCustomerModel
     * @return paymentInfo
     */
    private BusinessAgreementPaymentMethod populatePaymentDetails(
            final BusinessAgreementHeaderModel businessAgreementHeaderModel,
            final BusinessAgreementPaymentMethod paymentInfo,
            final String partnerFunction, final String addressguid,
            final CustomerModel currentCustomerModel) {
        CustomerModel customerModel = getCustomerPartnerFunction(
                businessAgreementHeaderModel, partnerFunction);

        if (customerModel == null) {
            customerModel = currentCustomerModel;
        }

        paymentInfo.setAlternativePayerId(customerModel.getCustomerID());
        paymentInfo.setAlternativePayerDesc(customerModel.getDisplayName());
        paymentInfo.setAlternativePayerAddress(getAddressData(customerModel,
                addressguid));

        return paymentInfo;
    }

    /**
     *
     * @param source
     * @return businessAgreementRuleHeaderModel
     */
    private BusinessAgreementRuleHeaderModel getBusinessAgreementRuleHeaderModel(
            final BusinessAgreementHeaderModel source) {
        BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel = null;
        if (source.getBusinessruleHeader() != null) {
            final Set<BusinessAgreementRuleHeaderModel> businessAgreementRuleHeaderSet = source
                    .getBusinessruleHeader();
            if (!businessAgreementRuleHeaderSet.isEmpty()) {
                businessAgreementRuleHeaderModel = businessAgreementRuleHeaderSet
                        .iterator().next();
            }
        }
        return businessAgreementRuleHeaderModel;
    }

    /**
     *
     * @param addresses
     * @param addressCorrespondenceGuid
     * @return addressData
     */
    private AddressData getAddressData(final CustomerModel customerModel,
            final String addressCorrespondenceGuid) {
        final Collection<AddressModel> addresses = customerModel.getAddresses();
        AddressData addressData = null;
        /*
         * TO DO: Below logic need to be changed after handling default flag on
         * addresses
         */
        if (addresses != null && !addresses.isEmpty()) {
            if (addressCorrespondenceGuid == null) {
                addressData = addressConverter.convert(customerModel
                        .getDefaultShipmentAddress());
            } else {
                addressData = convertAddressModel(addresses,
                        addressCorrespondenceGuid);
            }
        }
        return addressData;
    }

    /**
     *
     * @param addresses
     * @param addressCorrespondenceGuid
     * @return addressData
     */
    private AddressData convertAddressModel(
            final Collection<AddressModel> addresses,
            final String addressCorrespondenceGuid) {
        AddressModel addressModel = null;
        AddressData addressData = null;
        for (final AddressModel address : addresses) {
            if (StringUtils.isNotBlank(address.getPublicKey())
                    && address.getPublicKey().contains(
                            addressCorrespondenceGuid)) {
                addressModel = address;
                break;
            }
        }

        if (addressModel != null) {
            addressData = addressConverter.convert(addressModel);
        }
        return addressData;
    }

    /**
     * method populate tax data of business agreement
     *
     * @param businessAgreementHeaderModel
     * @param source
     * @param target
     */
    private void populateTaxData(
            final BusinessAgreementHeaderModel businessAgreementHeaderModel,
            final BusinessAgreementModel source,
            final BusinessAgreementDetailsData target) {
        final AddressData addressData = getAddressData(source.getCustomer(),
                businessAgreementHeaderModel.getAddresstaxguid());

        // Tax Relevant Address
        target.setTaxRelevantAddress(addressData);
        // tax type
        if (businessAgreementHeaderModel.getTaxcategory() != null) {
            target.setTaxType(businessAgreementHeaderModel.getTaxcategory()
                    .getText());
        }
        // tax code
        if (businessAgreementHeaderModel.getTaxcode() != null) {
            target.setTaxCode(businessAgreementHeaderModel.getTaxcode()
                    .getText());
        }
    }

    /**
     * method populate Dunning Data of business agreement
     *
     * @param businessAgreementHeaderModel
     * @param source
     * @param target
     */
    private void populateDunningData(
            final BusinessAgreementHeaderModel businessAgreementHeaderModel,
            final BusinessAgreementModel source,
            final BusinessAgreementDetailsData target,
            final BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel) {
        CustomerModel customerModel = getCustomerPartnerFunction(
                businessAgreementHeaderModel,
                SapbusinessagreementfacadesConstants.DUNNING_RECIPIENT);

        if (customerModel == null) {
            customerModel = source.getCustomer();
        }

        target.setDunningRecipientId(customerModel.getUid());
        target.setDunningDesc(customerModel.getDisplayName());

        final AddressData addressData = getAddressData(source.getCustomer(),
                businessAgreementHeaderModel.getAddressdunningguid());
        target.setDunningAddress(addressData);

        if (businessAgreementRuleHeaderModel.getShippingcontroldr() != null) {
            target.setDunningShipControl(businessAgreementRuleHeaderModel
                    .getShippingcontroldr().getText());
        }

    }

    /**
     * method populate Correspondence Data of business agreement
     *
     * @param businessAgreementHeaderModel
     * @param target
     */
    private void populateCorrespondenceData(
            final BusinessAgreementHeaderModel businessAgreementHeaderModel,
            final BusinessAgreementModel source,
            final BusinessAgreementDetailsData target,
            final BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel) {
        CustomerModel customerModel = getCustomerPartnerFunction(
                businessAgreementHeaderModel,
                SapbusinessagreementfacadesConstants.CORRESPONDENCE_RECIPIENT);

        if (customerModel == null) {
            customerModel = source.getCustomer();

        }
        target.setCorrespondenceRecipientId(customerModel.getUid());
        target.setCorrespondenceRecipientDesc(customerModel.getDisplayName());

        if (businessAgreementRuleHeaderModel.getCorrepondencevariant() != null) {
            target.setCorrespondenceVariant(businessAgreementRuleHeaderModel
                    .getCorrepondencevariant().getText());
        }
        if (businessAgreementRuleHeaderModel.getShippingcontrolbp() != null) {
            target.setCorrespondenceShipControl(businessAgreementRuleHeaderModel
                    .getShippingcontrolbp().getText());
        }

    }

    /**
     * method return customerModel for Parter function
     *
     * @param businessAgreementHeaderModel
     * @param partnerFunction
     * @return customerModel
     */
    private CustomerModel getCustomerPartnerFunction(
            final BusinessAgreementHeaderModel businessAgreementHeaderModel,
            final String partnerFunction) {
        CustomerModel customerModel = null;
        final Set<BusinessAgreementPartnersModel> partnersModelSet = businessAgreementHeaderModel
                .getBusinessagreementpartners();

        for (final BusinessAgreementPartnersModel businessAgreementPartnersModel : partnersModelSet) {
            if (StringUtils.equals(businessAgreementPartnersModel
                    .getPartnerfunction().getCode(), partnerFunction)) {
                customerModel = businessAgreementPartnersModel
                        .getCustomerguid();
                break;
            }
        }
        return customerModel;
    }

    /**
     * method return businessAgreementHeaderModel
     *
     * @param source
     * @return businessAgreementHeaderModel
     */
    private BusinessAgreementHeaderModel getBusinessAgreementHeaderModel(
            final BusinessAgreementModel source) {
        BusinessAgreementHeaderModel businessAgreementHeaderModel = null;
        if (source.getBusinessagreementheader() != null) {
            final Set<BusinessAgreementHeaderModel> businessAgreementHeaderSet = source
                    .getBusinessagreementheader();

            if (!businessAgreementHeaderSet.isEmpty()) {
                businessAgreementHeaderModel = businessAgreementHeaderSet
                        .iterator().next();
            }
        }
        return businessAgreementHeaderModel;
    }
}
