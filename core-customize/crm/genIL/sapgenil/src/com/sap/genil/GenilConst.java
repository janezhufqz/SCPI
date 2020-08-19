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

import com.sap.tc.logging.Category;

/**
 * Provides main Genil constants and mappings for stateless and statefull acess
 * 
 * @author SAP
 * @version 1.0
 */
public class GenilConst {

    /**
     * Enumeration with all available Genil functions and the statefull and
     * statelsee representations
     * 
     * @author SAP
     */
    public enum GenilFeature {
        COMMIT,
        CREATE,
        DELETE,
        EXEC_BO_METHOD,
        GET_DQUERY_RESULT,
        GET_MESSAGES,
        GET_MODEL,
        GET_QUERY_RESULT,
        INIT,
        INITIALIZE,
        LOAD,
        LOCK,
        MODIFY,
        READ,
        RESET,
        ROLLBACK,
        SAVE;

        @java.lang.SuppressWarnings("squid:MethodCyclomaticComplexity")
        public GenilRFC getRFCStateFul() {
            switch (this.ordinal()) {
                case 0:
                    return GenilRFC.GENIL_COMMIT;
                case 1:
                    return GenilRFC.GENIL_CREATE;
                case 2:
                    return GenilRFC.GENIL_DELETE;
                case 3:
                    return GenilRFC.GENIL_EXEC_BO_METHOD;
                case 4:
                    return GenilRFC.GENIL_GET_DQUERY_RESULT;
                case 5:
                    return GenilRFC.GENIL_GET_MESSAGES;
                case 6:
                    return GenilRFC.GENIL_GET_MODEL;
                case 7:
                    return GenilRFC.GENIL_GET_QUERY_RESULT;
                case 8:
                    return GenilRFC.GENIL_INIT;
                case 9:
                    return GenilRFC.GENIL_INITIALIZE;
                case 10:
                    return GenilRFC.GENIL_LOAD;
                case 11:
                    return GenilRFC.GENIL_LOCK;
                case 12:
                    return GenilRFC.GENIL_MODIFY;
                case 13:
                    return GenilRFC.GENIL_READ;
                case 14:
                    return GenilRFC.GENIL_RESET;
                case 15:
                    return GenilRFC.GENIL_ROLLBACK;
                case 16:
                    return GenilRFC.GENIL_SAVE;

                default:
                    return null;
            }
        }

@java.lang.SuppressWarnings("squid:MethodCyclomaticComplexity")
        public GenilRFC getRFCStateLess() {
            switch (this.ordinal()) {
                case 0:
                    return null;
                case 1:
                    return null;
                case 2:
                    return null;
                case 3:
                    return null;
                case 4:
                    return GenilRFC.GENIL_GET_DQUERY_RESULT_SL;
                case 5:
                    return null;
                case 6:
                    return GenilRFC.GENIL_GET_MODEL_SL;
                case 7:
                    return GenilRFC.GENIL_GET_QUERY_RESULT_SL;
                case 8:
                    return null;
                case 9:
                    return null;
                case 10:
                    return null;
                case 11:
                    return null;
                case 12:
                    return null;
                case 13:
                    return GenilRFC.GENIL_READ_SL;
                case 14:
                    return null;
                case 15:
                    return null;
                case 16:
                    return null;
                default:
                    return null;
            }
        }
    }

    /**
     * Enumeration with representations of possible error levels in Genil layer
     * 
     * @author SAP
     */
    public enum GenilMessageType {
        ALL('A'), ERROR('E'), WARNING('W'), INFO('I'), SUCCESS('S'), ABORT('C'), EXIT('X');

        @java.lang.SuppressWarnings("squid:MethodCyclomaticComplexity") 
        public static GenilMessageType getMsgType(char msgType) {
            switch (msgType) {
                case 'A':
                    return ALL;
                case 'E':
                    return ERROR;
                case 'W':
                    return WARNING;
                case 'I':
                    return INFO;
                case 'S':
                    return SUCCESS;
                case 'C':
                    return ABORT;
                case 'X':
                    return EXIT;
                default:
                    return ERROR;
            }
        }

        private char msgType;

        GenilMessageType(char msgType) {
            this.msgType = msgType;
        }

        public char getMsgTypeAsChar() {
            return this.msgType;
        }

    }

    /**
     * Enumeration with all RFC functions provided by Genil RFC layer
     * 
     * @author SAP
     */
    public enum GenilRFC {
        GENIL_COMMIT,
        GENIL_CREATE,
        GENIL_DELETE,
        GENIL_EXEC_BO_METHOD,
        GENIL_GET_DQUERY_RESULT,
        GENIL_GET_MESSAGES,
        GENIL_GET_MODEL,
        GENIL_GET_QUERY_RESULT,
        GENIL_INIT,
        GENIL_INITIALIZE,
        GENIL_LOAD,
        GENIL_LOCK,
        GENIL_MODIFY,
        GENIL_READ,
        GENIL_RESET,
        GENIL_ROLLBACK,
        GENIL_SAVE,
        GENIL_GET_DQUERY_RESULT_SL,
        GENIL_GET_QUERY_RESULT_SL,
        GENIL_READ_SL,
        GENIL_GET_MODEL_SL;
    }

    public static final String ABAP_FALSE = "";

    public static final String ABAP_TRUE = "X";
    public static final char MESSAGE_LEVEL_CUSTOMER = '1';
    public static final char MESSAGE_LEVEL_EMPLOYEE = '3';
    public static final char MESSAGE_LEVEL_PROFESSIONAL = '6';
    public static final char MESSAGE_LEVEL_ADMINISTRATOR = '8';

    public static final char COMPONENT_SET_INIT_AND_LOAD = '1';
    public static final char COMPONENT_SET_LOAD = '2';
    public static final char COMPONENT_SET_ALREADY_LOADED = '4';

    public static final char MESSAGE_LEVEL_TRACE = '9';
    public static final Category GENIL_MODEL_ERROR = Category.getCategory(Category.APPS_COMMON,
            "MODEL_ERROR");

    public static final String ZERO_GUID = "00000000000000000000000000000000";
}
