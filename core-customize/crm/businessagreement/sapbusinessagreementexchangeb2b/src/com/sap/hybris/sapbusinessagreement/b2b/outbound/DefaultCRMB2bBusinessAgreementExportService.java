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
package com.sap.hybris.sapbusinessagreement.b2b.outbound;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.hybris.crm.sapcrmmodel.util.XSSFilterUtil;
import com.sap.hybris.sapbusinessagreement.constants.BusinessAgreemenPartnerRelationCsvColumns;
import com.sap.hybris.sapbusinessagreement.constants.BusinessAgreementCsvColumns;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementHeaderModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementModel;
import com.sap.hybris.sapbusinessagreement.model.BusinessAgreementPartnersModel;
import com.sap.hybris.sapbusinessagreement.outbound.DefaultCRMBusinessAgreementExportService;

/**
 *
 */
public class DefaultCRMB2bBusinessAgreementExportService extends DefaultCRMBusinessAgreementExportService {

    @Override
    public void createRawBusinessAgreement(final BusinessAgreementHeaderModel headermodel,
            final BusinessAgreementModel model) {

        final Map<String, Object> rawHybrisBusinessAgreement = new HashMap<>();
        if (model.getId() != null) {
            rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.BUAGID, model.getId());
        } else {
            rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.BUAGID, "");
        }
        if (model.getGuid() != null) {
            rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.BUAGUUID, model.getGuid());
        } else {
            rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.BUAGUUID, "");
        }

        if (model.getPartner() != null) {
            rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.PARENTUNIT, model.getPartner().getUid());
            rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.PARENTUNITGUID,
                    model.getPartner().getB2bunitguid());
        } else {
            if (model.getCustomer() != null) {
                rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.PARENTUNIT,
                        model.getCustomer().getCustomerID());
                rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.PARENTUNITGUID,
                        model.getCustomer().getCrmGuid());
            }
        }
        if (model.getText() != null || ("").equals(model.getText())) {
            rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.TEXT, XSSFilterUtil.filter(model.getText()));
        } else {
            rawHybrisBusinessAgreement.put(BusinessAgreementCsvColumns.TEXT, "");
        }

        createBusinessAgreementHeader(rawHybrisBusinessAgreement, headermodel);

        sendRawBusinessItemsToDataHub(RAW_HYBRIS_BUSINESS_AGREEMENT,
                Collections.singletonList(rawHybrisBusinessAgreement));
    }

    @Override
    public void createPartnersRelation(final Map<String, Object> rawHybrisBusinessAgreementpertners,
            final List<Map<String, Object>> rawHybrisBusinessAgreementpertnerslist,
            final BusinessAgreementPartnersModel partnermodel) {
        if (partnermodel.getBusinessagreementheader().getBusinessagreement() != null) {
            rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.BUAGID,
                    partnermodel.getBusinessagreementheader().getBusinessagreement().getId());
        } else {
            rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.BUAGID, "");
        }
        if (partnermodel.getBusinessagreementpartnercat() != null) {
            rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.PARTNERFUNCTIONCATEGORY,
                    partnermodel.getBusinessagreementpartnercat());
        } else {
            rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.PARTNERFUNCTIONCATEGORY,
                    "");
        }
        if (partnermodel.getPartnerguid1() != null) {
            rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.PRIMARYPARTNERGUID,
                    partnermodel.getPartnerguid1());
        } else {
            rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.PRIMARYPARTNERGUID, "");
        }
        if (partnermodel.getPartnerguid2() != null) {
            rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.SECONDARYPARTNERGUID,
                    partnermodel.getPartnerguid2().getB2bunitguid());
        } else {
            if (partnermodel.getCustomerguid() != null) {
                rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.SECONDARYPARTNERGUID,
                        partnermodel.getCustomerguid().getCrmGuid());
            }
        }

        rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.BPRELATIONSHIPNUMBER, "");
        if (partnermodel.getPartnerfunction() != null) {
            rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.PARTNERFUNCTION,
                    partnermodel.getPartnerfunction().getCode());
        } else {
            rawHybrisBusinessAgreementpertners.put(BusinessAgreemenPartnerRelationCsvColumns.PARTNERFUNCTION, "");
        }
        rawHybrisBusinessAgreementpertnerslist.add(rawHybrisBusinessAgreementpertners);
    }

}
