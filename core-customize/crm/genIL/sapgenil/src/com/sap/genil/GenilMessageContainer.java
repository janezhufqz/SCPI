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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This class models a GenIL message container<br>
 * 
 * @author SAP
 * @version 1.0
 */
public class GenilMessageContainer {

    /**
     * <p>
     * Messages belonging to the current container
     * </p>
     */
    private Collection<GenilMessage> messages;

    /**
     * <p>
     * The filter which can be applied to the container
     * </p>
     */
    private GenilMessageFilter filter;

    /**
     * Constructor for GenilMesageContainer
     */
    GenilMessageContainer() {
        messages = new ArrayList<GenilMessage>();
    }

    /**
     * Constructor for GenilMesageContainer
     * 
     * @param filter
     */
    GenilMessageContainer(GenilMessageFilter filter) {
        this();
        this.filter = filter;
    }

    /**
     * Constructor for GenilMesageContainer
     * 
     * @param messages
     */
    GenilMessageContainer(List<GenilMessage> messages) {
        this.messages = messages;
    }

    
    /**
     * Add message to the Message container
     * 
     * @param msg
     */
    public void addMessage(GenilMessage msg) {
        this.messages.add(msg);
    }

    /**
     * Cumulates the messages of the current container with the messages of
     * another message container
     * 
     * @param otherMsgContainer
     * @return
     */
    public Collection<GenilMessage> getCumulatedMessages() {
        Collection<GenilMessage> cumMsgs = new ArrayList<GenilMessage>();
        cumMsgs.addAll(this.getMessages());
        cumMsgs.addAll(this.getMessages());

        return cumMsgs;
    }

    /**
     * Getter-Method for property {@link #filter}. <br>
     * 
     * @return Gets the {@link #filter}.
     */
    public GenilMessageFilter getFilter() {
        return this.filter;
    }

    /**
     * Getter-Method for property {@link #messages}. <br>
     * 
     * @return Gets the {@link #messages}.
     */
    public Collection<GenilMessage> getMessages() {
        Collection<GenilMessage> fCol = new ArrayList<GenilMessage>();
        if (filter != null) {
            for (Iterator<GenilMessage> iterator = filter.getFilterIterator(messages.iterator()); iterator.hasNext();) {
                GenilMessage msg = iterator.next();
                fCol.add(msg);
            }
        }
        else {
            fCol = new ArrayList<GenilMessage>(this.messages);
        }

        return fCol;
    }

    /**
     * Clears a message container
     */
    public void reset() {
        this.messages.clear();
        this.filter = null;
    }

    /**
     * Setter-Method for property {@link #filter}. <br>
     * 
     * @param filter Sets the {@link #filter}.
     */    
    public void setFilter(GenilMessageFilter filter) {
        this.filter = filter;
    }

    /**
     * Setter-Method for property {@link #messages}. <br>
     * 
     * @param messages Sets the {@link #messages}.
     */    public void setMessages(Collection<GenilMessage> messages) {
        this.messages = messages;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder msgCont = new StringBuilder("MsgCont: ");
        for (GenilMessage msg : this.getMessages()) {
            msgCont.append("\n" + msg.toString());
        }
        return msgCont.toString();
    }

}
