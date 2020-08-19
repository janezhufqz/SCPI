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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.crm.strategy;

/**
 * @author I321335
 *
 */
public class MappersDTO {
    private HeaderMapper headerMapper;
    private PartnerMapper partnerMapper;
    private ItemMapper itemMapper;
    public HeaderMapper getHeaderMapper() {
        return headerMapper;
    }
    public void setHeaderMapper(HeaderMapper headerMapper) {
        this.headerMapper = headerMapper;
    }
    public PartnerMapper getPartnerMapper() {
        return partnerMapper;
    }
    public void setPartnerMapper(PartnerMapper partnerMapper) {
        this.partnerMapper = partnerMapper;
    }
    public ItemMapper getItemMapper() {
        return itemMapper;
    }
    public void setItemMapper(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }
}
