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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class gives the opportunity to set a filter on GenilMessageContainer.
 * This way, only messages which comply to the filter will be read from the
 * message container <br>
 * 
 * @author SAP
 * @version 1.0
 */

public abstract class GenilMessageFilter {

    private class FilterIterator implements Iterator<GenilMessage> {

        private Iterator<GenilMessage> iterator;

        private GenilMessage next;

        private FilterIterator(Iterator<GenilMessage> iterator) {
            this.iterator = iterator;
            toNext();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            return next != null;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        @Override
        public GenilMessage next() {
            if (next == null) {
                throw new NoSuchElementException();
            }
            GenilMessage returnValue = next;
            toNext();
            return returnValue;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /**
         * Sets the Iterator to the next non empty object
         */
        private void toNext() {
            next = null;
            while (iterator.hasNext()) {
                GenilMessage item = iterator.next();
                if (item != null && passes(item)) {
                    next = item;
                    break;
                }
            }
        }
    }

    /**
     * Returns a Iterator for the MessageObject
     * 
     * @param iterator
     * @return
     */
    public Iterator<GenilMessage> getFilterIterator(Iterator<GenilMessage> iterator) {
        return new FilterIterator(iterator);
    }

    public abstract boolean passes(GenilMessage object);
}
