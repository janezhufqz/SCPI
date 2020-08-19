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
package com.sap.hybris.crm.sapbusinessagreementfacades.b2b.converters.populator;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.sap.hybris.sapcrmcustomerb2b.model.SAPCCPaymentInfoModel;
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
import com.sap.hybris.sapbusinessagreementfacades.data.CreditCardData;
import com.sap.hybris.sapcrmcustomerb2b.service.DefaultSAPCRMB2BUnitService;

/**
 * populate details of business agreement for B2Bunit
 */
public class DefaultBusinessAgreementB2BDetailPopulator
        implements Populator<BusinessAgreementModel, BusinessAgreementDetailsData> {
    @Resource
    DefaultSAPCRMB2BUnitService b2bUnitService;

    private Converter<AddressModel, AddressData> addressConverter;
    private Converter<SAPCCPaymentInfoModel, CreditCardData> creditCardConverter;

    private Converter<CRMBusinessAgreementPaymentMethodModel, BusinessAgreementPaymentMethod> paymentMethodConverter;

    /**
     * @return the creditCardConverter
     */
    public Converter<SAPCCPaymentInfoModel, CreditCardData> getCreditCardConverter() {
        return creditCardConverter;
    }

    /**
     * @param creditCardConverter
     *            the creditCardConverter to set
     */
    public void setCreditCardConverter(final Converter<SAPCCPaymentInfoModel, CreditCardData> creditCardConverter) {
        this.creditCardConverter = creditCardConverter;
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

    public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter) {
        this.addressConverter = addressConverter;
    }

    @Override
    public void populate(final BusinessAgreementModel source, final BusinessAgreementDetailsData target) {
        target.setBusinessAgreementID(source.getId());
        target.setDescription(XSSFilterUtil.filter(source.getText()));
        final B2BUnitModel b2BUnitModel = source.getPartner();
        target.setPartnerName(b2BUnitModel.getDisplayName());

        final BusinessAgreementHeaderModel businessAgreementHeaderModel = getBusinessAgreementHeaderModel(source);

        if (businessAgreementHeaderModel != null) {
            populateGeneralData(target, b2BUnitModel, businessAgreementHeaderModel);

            final BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel = getBusinessAgreementRuleHeaderModel(
                    businessAgreementHeaderModel);

            if (businessAgreementRuleHeaderModel != null) {
                // populating paymentViaInfo

                BusinessAgreementPaymentMethod paymentViaInfo = null;

                paymentViaInfo = paymentMethodConverter.convert(businessAgreementRuleHeaderModel.getPaymentmethodinc());

                paymentViaInfo = populatePaymentDetails(businessAgreementHeaderModel,
                        SapbusinessagreementfacadesConstants.ALTERNATIVE_PAYER,
                        businessAgreementHeaderModel.getAddresspayerguid(), paymentViaInfo, b2BUnitModel);
                target.setPaymentVia(paymentViaInfo);

                // populating refundViaInfo
                BusinessAgreementPaymentMethod refundViaInfo = null;

                refundViaInfo = paymentMethodConverter.convert(businessAgreementRuleHeaderModel.getPaymentmethodout());

                refundViaInfo = populatePaymentDetails(businessAgreementHeaderModel,
                        SapbusinessagreementfacadesConstants.PAYMENT_RECIPIENT,
                        businessAgreementHeaderModel.getAddressrecipientguid(), refundViaInfo, b2BUnitModel);
                target.setRefundVia(refundViaInfo);

                populateCorrespondenceData(businessAgreementHeaderModel, target, b2BUnitModel,
                        businessAgreementRuleHeaderModel);
                populateDunningData(businessAgreementHeaderModel, target, b2BUnitModel,
                        businessAgreementRuleHeaderModel);
                populateTaxData(businessAgreementHeaderModel, target, b2BUnitModel);
            }
        }
    }

    /**
     * method populate general data of business agreement
     *
     * @param target
     * @param b2BUnitModel
     * @param businessAgreementHeaderModel
     */
    private void populateGeneralData(final BusinessAgreementDetailsData target, final B2BUnitModel b2BUnitModel,
            final BusinessAgreementHeaderModel businessAgreementHeaderModel) {
        target.setRefnumber(XSSFilterUtil.filter(businessAgreementHeaderModel.getRefnumber()));
        target.setIsDefault(businessAgreementHeaderModel.getBusinessagreementdefault());

        final CRMBusinessAgreementClassModel crmBusinessAgreementClassModel = businessAgreementHeaderModel
                .getBusinessagreementclass();
        if (crmBusinessAgreementClassModel != null) {
            target.setCollectivebill(crmBusinessAgreementClassModel.getCollectivebill());
            target.setBusinessAgreementClass(crmBusinessAgreementClassModel.getText());
        }
        final AddressModel bpAddressModel = b2bUnitService
                .findAddressByAddressGUID(businessAgreementHeaderModel.getAddresscorrespondenceguid(), b2BUnitModel);
        if (bpAddressModel != null) {
            target.setAddressData(addressConverter.convert(bpAddressModel));
        }
    }

    /**
     * method return businessAgreementHeaderModel
     *
     * @param source
     * @return businessAgreementHeaderModel
     */
    private BusinessAgreementHeaderModel getBusinessAgreementHeaderModel(final BusinessAgreementModel source) {
        BusinessAgreementHeaderModel businessAgreementHeaderModel = null;
        if (source.getBusinessagreementheader() != null) {
            final Set<BusinessAgreementHeaderModel> businessAgreementHeaderSet = source.getBusinessagreementheader();

            if (!businessAgreementHeaderSet.isEmpty()) {
                businessAgreementHeaderModel = businessAgreementHeaderSet.iterator().next();
            }
        }
        return businessAgreementHeaderModel;
    }

    /**
     * method return businessAgreementRuleHeaderModel
     *
     * @param source
     * @return businessAgreementRuleHeaderModel
     */
    private BusinessAgreementRuleHeaderModel getBusinessAgreementRuleHeaderModel(
            final BusinessAgreementHeaderModel source) {
        BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel = null;
        if (source.getBusinessruleHeader() != null) {
            final Set<BusinessAgreementRuleHeaderModel> businessAgreementRuleHeaderSet = source.getBusinessruleHeader();
            if (!businessAgreementRuleHeaderSet.isEmpty()) {
                businessAgreementRuleHeaderModel = businessAgreementRuleHeaderSet.iterator().next();
            }
        }
        return businessAgreementRuleHeaderModel;
    }

    /**
     * method return b2bUnitModel for Partner function
     *
     * @param businessAgreementHeaderModel
     * @param partnerFunction
     * @return b2bUnitModel
     */
    private B2BUnitModel getB2BUnitForPartnerFunction(final BusinessAgreementHeaderModel businessAgreementHeaderModel,
            final String partnerFunction) {
        B2BUnitModel b2bUnitModel = null;
        final Set<BusinessAgreementPartnersModel> partnersModelSet = businessAgreementHeaderModel
                .getBusinessagreementpartners();
        if (!partnersModelSet.isEmpty()) {
            for (final BusinessAgreementPartnersModel businessAgreementPartnersModel : partnersModelSet) {
                if (StringUtils.equals(businessAgreementPartnersModel.getPartnerfunction().getCode(),
                        partnerFunction)) {
                    b2bUnitModel = businessAgreementPartnersModel.getPartnerguid2();
                    break;
                }
            }
        }
        return b2bUnitModel;
    }

    /**
     * method populate Payment Details of business agreement
     *
     * @param businessAgreementHeaderModel
     * @param partnerFunction
     * @param addressguid
     * @param paymentInfo
     * @param currentB2BUnitModel
     * @return
     */
    private BusinessAgreementPaymentMethod populatePaymentDetails(
            final BusinessAgreementHeaderModel businessAgreementHeaderModel, final String partnerFunction,
            final String addressguid, final BusinessAgreementPaymentMethod paymentInfo,
            final B2BUnitModel currentB2BUnitModel) {

        B2BUnitModel partnerb2bUnitModel = getB2BUnitForPartnerFunction(businessAgreementHeaderModel, partnerFunction);
        if (partnerb2bUnitModel == null) {
            partnerb2bUnitModel = currentB2BUnitModel;
        }

        if (partnerb2bUnitModel != null) {
            paymentInfo.setAlternativePayerDesc(partnerb2bUnitModel.getDisplayName());
            final AddressModel alternativePayerAddress = b2bUnitService.findAddressByAddressGUID(addressguid,
                    partnerb2bUnitModel);
            if (alternativePayerAddress != null) {
                paymentInfo.setAlternativePayerAddress(addressConverter.convert(alternativePayerAddress));
            }
        }
        return paymentInfo;
    }

    /**
     * method populate Correspondence Data of business agreement
     *
     * @param businessAgreementHeaderModel
     * @param target
     * @param currentB2BUnitModel
     * @param businessAgreementRuleHeaderModel
     */
    private void populateCorrespondenceData(final BusinessAgreementHeaderModel businessAgreementHeaderModel,
            final BusinessAgreementDetailsData target, final B2BUnitModel currentB2BUnitModel,
            final BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel) {
        B2BUnitModel partnerb2bUnitModel = getB2BUnitForPartnerFunction(businessAgreementHeaderModel,
                SapbusinessagreementfacadesConstants.CORRESPONDENCE_RECIPIENT);

        if (partnerb2bUnitModel == null) {
            partnerb2bUnitModel = currentB2BUnitModel;
        }
        if (partnerb2bUnitModel != null) {
            target.setCorrespondenceRecipientId(partnerb2bUnitModel.getUid());
            target.setCorrespondenceRecipientDesc(partnerb2bUnitModel.getDisplayName());
        }

        if (businessAgreementRuleHeaderModel.getCorrepondencevariant() != null) {
            target.setCorrespondenceVariant(businessAgreementRuleHeaderModel.getCorrepondencevariant().getText());

        }
        if (businessAgreementRuleHeaderModel.getShippingcontrolbp() != null) {
            target.setCorrespondenceShipControl(businessAgreementRuleHeaderModel.getShippingcontrolbp().getText());
        }
    }

    /**
     * method populate Dunning Data of business agreement
     *
     * @param businessAgreementHeaderModel
     * @param target
     * @param currentB2BUnitModel
     * @param businessAgreementRuleHeaderModel
     */
    private void populateDunningData(final BusinessAgreementHeaderModel businessAgreementHeaderModel,
            final BusinessAgreementDetailsData target, final B2BUnitModel currentB2BUnitModel,
            final BusinessAgreementRuleHeaderModel businessAgreementRuleHeaderModel) {
        B2BUnitModel partnerb2bUnitModel = getB2BUnitForPartnerFunction(businessAgreementHeaderModel,
                SapbusinessagreementfacadesConstants.DUNNING_RECIPIENT);

        if (partnerb2bUnitModel == null) {
            partnerb2bUnitModel = currentB2BUnitModel;
        }
        if (partnerb2bUnitModel != null) {
            target.setDunningRecipientId(partnerb2bUnitModel.getUid());
            target.setDunningDesc(partnerb2bUnitModel.getDisplayName());

            final AddressModel dunningAddressModel = b2bUnitService.findAddressByAddressGUID(
                    businessAgreementHeaderModel.getAddressdunningguid(), partnerb2bUnitModel);

            if (dunningAddressModel != null) {
                target.setDunningAddress(addressConverter.convert(dunningAddressModel));
            }
            if (businessAgreementRuleHeaderModel.getShippingcontroldr() != null) {
                target.setDunningShipControl(businessAgreementRuleHeaderModel.getShippingcontroldr().getText());
            }
        }
    }

    /**
     * method populate tax data of business agreement
     *
     * @param businessAgreementHeaderModel
     * @param target
     * @param b2bUnitModel
     */
    private void populateTaxData(final BusinessAgreementHeaderModel businessAgreementHeaderModel,
            final BusinessAgreementDetailsData target, final B2BUnitModel b2bUnitModel) {
        final AddressModel taxAddressModel = b2bUnitService
                .findAddressByAddressGUID(businessAgreementHeaderModel.getAddresstaxguid(), b2bUnitModel);

        if (taxAddressModel != null) {
            target.setTaxRelevantAddress(addressConverter.convert(taxAddressModel));
        }

        if (businessAgreementHeaderModel.getTaxcategory() != null) {
            target.setTaxType(businessAgreementHeaderModel.getTaxcategory().getText());
        }
        if (businessAgreementHeaderModel.getTaxcode() != null) {
            target.setTaxCode(businessAgreementHeaderModel.getTaxcode().getText());
        }
    }

}
