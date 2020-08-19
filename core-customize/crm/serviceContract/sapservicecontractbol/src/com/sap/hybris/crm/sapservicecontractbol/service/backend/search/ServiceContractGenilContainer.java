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
package com.sap.hybris.crm.sapservicecontractbol.service.backend.search;

import java.util.List;

import com.sap.genil.GenilDataContainer;

/**
 * GENIL container for Service Contracts
 */
public interface ServiceContractGenilContainer {

    public GenilDataContainer getContractInfo();

    public void setContractInfo(GenilDataContainer contractInfo);

    public GenilDataContainer getContractHeader();

    public void setContractHeader(GenilDataContainer contractHeader);

    public GenilDataContainer getContractHeaderStatusSet();

    public void setContractHeaderStatusSet(GenilDataContainer contractHeaderStatusSet);

    public GenilDataContainer getContractStatusCurrent();

    public void setContractStatusCurrent(GenilDataContainer contractStatusCurrent);

    public GenilDataContainer getContractCumulated();

    public void setContractCumulated(GenilDataContainer contractCumulated);

    public GenilDataContainer getContractTextSet();

    public void setContractTextSet(GenilDataContainer contractTextSet);

    public List<GenilDataContainer> getContractTextAll();

    public void setContractTextAll(final List<GenilDataContainer> contractTextAll);

    public GenilDataContainer getContractItems();

    public void setContractItems(GenilDataContainer contractItems);

    public List<GenilDataContainer> getContractItemsAllList();

    public void setContractItemsAllList(List<GenilDataContainer> contractItemsAllList);

    public GenilDataContainer getContractItemPricingSet();

    public void setContractItemPricingSet(GenilDataContainer contractItemPricingSet);

    public GenilDataContainer getContractItemPricingExt();

    public void setContractItemPricingExt(GenilDataContainer contractItemPricingExt);

    public GenilDataContainer getContractItemProductExt();

    public void setContractItemProductExt(GenilDataContainer contractItemProductExt);

    public GenilDataContainer getContractItemServiceExt();

    public void setContractItemServiceExt(GenilDataContainer contractItemServiceExt);

    public GenilDataContainer getContractItemStatusSet();

    public void setContractItemStatusSet(GenilDataContainer contractItemStatusSet);

    public GenilDataContainer getContractItemStatusCurrent();

    public void setContractItemStatusCurrent(GenilDataContainer contractItemStatusCurrent);

    public GenilDataContainer getContractItemCummulateSet();

    public void setContractItemCummulateSet(GenilDataContainer contractItemCummulateSet);

    public GenilDataContainer getContractItemCummulateValue();

    public void setContractItemCummulateValue(GenilDataContainer contractItemCummulateValue);

    public GenilDataContainer getContractItemCummulateQuantity();

    public void setContractItemCummulateQuantity(GenilDataContainer contractItemCummulateQuantity);

    public GenilDataContainer getContractItemSchedlinExt();

    public void setContractItemSchedlinExt(GenilDataContainer contractItemSchedlinExt);

    public GenilDataContainer getContractItemSchedlinFirst();

    public void setContractItemSchedlinFirst(GenilDataContainer contractItemSchedlinFirst);

    public GenilDataContainer getContractItemDateSet();

    public void setContractItemDateSet(GenilDataContainer contractItemDateSet);

    public List<GenilDataContainer> getContractItemDatesAll();

    public void setContractItemDatesAll(List<GenilDataContainer> contractItemDatesAll);

    public GenilDataContainer getContractBillingPlanSet();

    public void setContractBillingPlanSet(GenilDataContainer contractBillingPlanSet);

    public GenilDataContainer getContractBillingDetail();

    public void setContractBillingDetail(GenilDataContainer contractBillingDetail);

}
