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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Manages message containers<br>
 * </p>
 * 
 * @author SAP
 * @version 1.0
 */
class GenilMessageContainerManager {

    private GenilMessageContainer globalMsgContainer;

    /**
     * The registry of message containers which can be associated to a data
     * container object
     */
    private Map<String, GenilMessageContainer> objMsgContainerRegistry;

    /**
     * <p>
     * Constructor for GenilMessageContainerManager.
     * </p>
     * 
     * @param globalMsgContainer
     */
    GenilMessageContainerManager() {
        this.objMsgContainerRegistry = new HashMap<String, GenilMessageContainer>();
        this.globalMsgContainer = new GenilMessageContainer();
    }

    public GenilMessageContainer getGlobalMessageContainer() {
        return this.globalMsgContainer;
    }

    public GenilMessageContainer getObjectMessageContainer(String dcGuid) {
        return this.objMsgContainerRegistry.get(dcGuid);
    }

    public Map<String, GenilMessageContainer> getObjectMessageContainers() {
        return Collections.unmodifiableMap(objMsgContainerRegistry);
    }

    /**
     * Resets the manager of message containers by reseting the global message
     * container and all the object message containers<br>
     */
    public void reset() {
        this.globalMsgContainer.reset();
        for (GenilMessageContainer msgCont : this.objMsgContainerRegistry.values()) {
            msgCont.reset();
        }
    }

    /**
     * Add a new object message container to the registry
     * 
     * @param dcGuid data container guid
     * @param objMsgContainer the message container
     */
    public void setObjectMessageContainer(String dcGuid, GenilMessageContainer objMsgContainer) {
        this.objMsgContainerRegistry.put(dcGuid, objMsgContainer);
    }

}
