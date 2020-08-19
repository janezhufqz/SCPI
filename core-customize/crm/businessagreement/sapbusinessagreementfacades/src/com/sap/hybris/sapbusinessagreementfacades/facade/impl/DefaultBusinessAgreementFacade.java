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
package com.sap.hybris.sapbusinessagreementfacades.facade.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import com.sap.hybris.crm.sapbusinessagreementservices.service.BusinessAgreementService;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;
import com.sap.hybris.sapbusinessagreementfacades.data.BusinessAgreementData;
import com.sap.hybris.sapbusinessagreementfacades.data.BusinessAgreementDetailsData;
import com.sap.hybris.sapbusinessagreementfacades.facade.BusinessAgreementFacade;

/**
 *
 */
public class DefaultBusinessAgreementFacade implements BusinessAgreementFacade {

    private BusinessAgreementService businessAgreementService;

    private UserService userService;

    private Converter<BusinessAgreementModel, BusinessAgreementDetailsData> businessAgreementB2CConverter;

    private Converter<BusinessAgreementModel, BusinessAgreementData> agreementHistoryConverter;

    /**
     * @return the businessAgreementB2CConverter
     */
    public Converter<BusinessAgreementModel, BusinessAgreementDetailsData> getBusinessAgreementB2CConverter() {
        return businessAgreementB2CConverter;
    }

    /**
     * @param businessAgreementB2CConverter
     *            the businessAgreementB2CConverter to set
     */
    public void setBusinessAgreementB2CConverter(
            final Converter<BusinessAgreementModel, BusinessAgreementDetailsData> businessAgreementB2CConverter) {
        this.businessAgreementB2CConverter = businessAgreementB2CConverter;
    }

    /**
     * @return the userService
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * @param userService
     *            the userService to set
     */
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    /**
     * @return the businessAgreementService
     */
    public BusinessAgreementService getBusinessAgreementService() {
        return businessAgreementService;
    }

    /**
     * @param businessAgreementService
     *            the businessAgreementService to set
     */
    public void setBusinessAgreementService(final BusinessAgreementService businessAgreementService) {
        this.businessAgreementService = businessAgreementService;
    }

    /**
     * @return the agreementHistoryConverter
     */
    public Converter<BusinessAgreementModel, BusinessAgreementData> getAgreementHistoryConverter() {
        return agreementHistoryConverter;
    }

    /**
     * @param agreementHistoryConverter
     *            the agreementHistoryConverter to set
     */
    public void setAgreementHistoryConverter(
            final Converter<BusinessAgreementModel, BusinessAgreementData> agreementHistoryConverter) {
        this.agreementHistoryConverter = agreementHistoryConverter;
    }

    @Override
    public String addBusinessAgreement(final BusinessAgreementData businessAgreementData) {
        return null;
    }

    @Override
    public SearchPageData<BusinessAgreementData> getPagedBusinessAgreementData(final PageableData pageableData) {
        final CustomerModel currentCustomerModel = (CustomerModel) getUserService().getCurrentUser();
        final SearchPageData<BusinessAgreementModel> agreementResults = this.getBusinessAgreementService()
                .getBusinessAgreementList(currentCustomerModel, pageableData);
        return convertBusinessAgreementPageData(agreementResults, getAgreementHistoryConverter());
    }

    /**
     * convert SearchPageData<BusinessAgreementModel> into
     * SearchPageData<BusinessAgreementData>
     *
     * @param source
     * @param converter
     * @return SearchPageData<BusinessAgreementData>
     */
    protected <S, T> SearchPageData<T> convertBusinessAgreementPageData(final SearchPageData<S> source,
            final Converter<S, T> converter) {
        final SearchPageData<T> result = new SearchPageData<T>();
        result.setPagination(source.getPagination());
        result.setSorts(source.getSorts());
        result.setResults(Converters.convertAll(source.getResults(), converter));
        return result;
    }

    @Override
    public BusinessAgreementDetailsData getBusinessAggreementDetails(final String businessAgreementId) {
        final BusinessAgreementModel businessAgreementModel = getBusinessAgreementService()
                .getBusinessAggreementDetails(businessAgreementId);

        BusinessAgreementDetailsData businessAgreementDetailsData = null;

        if (businessAgreementModel.getCustomer() != null) {
            businessAgreementDetailsData = businessAgreementB2CConverter.convert(businessAgreementModel);
        }

        return businessAgreementDetailsData;
    }

}
