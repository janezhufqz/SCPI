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
package com.sap.hybris.crm.sapcrmordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoRecord;
import com.sap.tc.logging.Severity;

import de.hybris.platform.sap.core.bol.backend.jco.JCoHelper;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.common.TechKey;

/**
 * Represents a message raised in the SAP back end which was transmitted via RFC
 */
public class BackendMessageCRM {

    /**
     * The logging instance
     */
    protected static final Log4JWrapper LOC = Log4JWrapper.getInstance(BackendMessageCRM.class.getName());

    /**
     * Available fields in a message from back end
     */
    public static final class FIELDS {
        /**
         * Message type (I, W, E) for info, warning, error
         */
        public static final String TYPE = "TYPE";
        /**
         * Message class (application area)
         */
        public static final String CLASS = "ID";
        /**
         * Message number
         */
        public static final String NUMBER = "NUMBER";
        /**
         * Message parameter 1
         */
        public static final String V1 = "MESSAGE_V1";
        /**
         * Message parameter 2
         */
        public static final String V2 = "MESSAGE_V2";
        /**
         * Message parameter 3
         */
        public static final String V3 = "MESSAGE_V3";
        /**
         * Message parameter 4
         */
        public static final String V4 = "MESSAGE_V4";
        /**
         * Message text attributes
         */
        private static final String[] TEXT = { "T_MSG", "MESSAGE" };
        /**
         * Message object reference attributes
         */
        private static final String[] REF_TECH_KEY = { "HANDLE_ITEM", "HANDLE", // structure
                "OBJECT", "EXTNUMBER" // table
        };

        private FIELDS() {

        }
    }

    /**
     * Severity (I, E, W)
     */
    protected String beSeverity;
    /**
     * Message class
     */
    protected String beClass;
    /**
     * Message number
     */
    protected String beNumber;
    /**
     * Message parameters
     */
    protected final String[] vars = new String[4];
    /**
     * Message text
     */
    protected String messageText;
    /**
     * Message reference key
     */
    protected TechKey refTechKey;

    /**
     * Constructs a message from a JCO structure
     *
     * @param struct
     *            Structure the message is based on
     */
    public BackendMessageCRM(final JCoRecord struct) {

        final JCoMetaData meta = struct.getMetaData();

        beSeverity = struct.getString(FIELDS.TYPE);
        beClass = struct.getString(FIELDS.CLASS);
        beNumber = struct.getString(FIELDS.NUMBER);

        if ("A".equals(beSeverity) || "E".equals(beSeverity)) {
            final String beMessage = "Back end error message: ";
            if (meta.hasField("MESSAGE")) {
                LOC.log(Severity.ERROR, LogCategories.APPLICATIONS, beMessage + struct.getString("MESSAGE"));
            } else {
                LOC.log(Severity.ERROR, LogCategories.APPLICATIONS, beMessage + beClass + "," + beNumber);
            }
        }

        vars[0] = struct.getString(FIELDS.V1);
        vars[1] = struct.getString(FIELDS.V2);
        vars[2] = struct.getString(FIELDS.V3);
        vars[3] = struct.getString(FIELDS.V4);

        final String fieldNameMessageText = determineFirstExistingFieldNameInJcoRecord(meta, FIELDS.TEXT);
        if (fieldNameMessageText != null) {
            messageText = struct.getString(fieldNameMessageText);
        } else {
            messageText = null;
        }

        final String notEmptyFieldForRefTechKey = determineFirstExistingFieldNameAndEmptyFieldinJcoRecord(meta, struct,
                FIELDS.REF_TECH_KEY);
        if (notEmptyFieldForRefTechKey != null) {
            refTechKey = JCoHelper.getTechKey(struct, notEmptyFieldForRefTechKey);
        } else {
            refTechKey = null;
        }
    }

    /**
     * Constructs a message based on its attributes
     *
     * @param msgType
     *            Type (I, W, E)
     * @param msgId
     *            ID (application area)
     * @param msgNo
     *            Number
     * @param msgV1
     *            Parameter1
     * @param msgV2
     *            Parameter2
     * @param msgV3
     *            Parameter3
     * @param msgV4
     *            Parameter4
     */
    public BackendMessageCRM(final String msgType, final String msgId, final String msgNo, final String msgV1,
            final String msgV2, final String msgV3, final String msgV4) {

        this.beSeverity = msgType;
        this.beClass = msgId;
        this.beNumber = msgNo;

        vars[0] = msgV1;
        vars[1] = msgV2;
        vars[2] = msgV3;
        vars[3] = msgV4;

    }

    static final String determineFirstExistingFieldNameInJcoRecord(final JCoMetaData meta, final String[] fieldsNames) {
        for (final String fName : fieldsNames) {
            if (meta.hasField(fName)) {
                return fName;
            }
        }
        return null;
    }

    static final String determineFirstExistingFieldNameAndEmptyFieldinJcoRecord(final JCoMetaData meta,
            final JCoRecord struct, final String[] fieldsNames) {
        for (final String fName : fieldsNames) {
            if (meta.hasField(fName) && !struct.getString(fName).isEmpty()) {
                return fName;
            }
        }
        return null;
    }

    /**
     * @return Severity (I, W, E)
     */
    public String getBeSeverity() {
        return beSeverity;
    }

    /**
     * @return Application area
     */
    public String getBeClass() {
        return beClass;
    }

    /**
     * @return Message number
     */
    public String getBeNumber() {
        return beNumber;
    }

    /**
     * @return Object reference
     */
    public TechKey getRefTechKey() {
        return refTechKey;
    }

    /**
     * @return Parameters
     */
    public String[] getVars() {
        return vars;
    }

    /**
     * @return Message text
     */
    public String getMessageText() {
        return messageText;
    }

    /**
     * @return Empty message?
     */
    public boolean isEmpty() {
        return checkBeSeverity() && checkBeClass() && checkBeNumber();
    }

    private boolean checkBeClass() {
        return beClass == null || beClass.isEmpty();
    }

    private boolean checkBeSeverity() {
        return beSeverity == null || beSeverity.isEmpty();
    }

    private boolean checkBeNumber() {
        return beNumber == null || beNumber.isEmpty() || StringUtils.equals(beNumber, "000");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(vars);
        result = prime * result + ((beClass == null) ? 0 : beClass.hashCode());
        result = prime * result + ((beNumber == null) ? 0 : beNumber.hashCode());
        result = prime * result + ((beSeverity == null) ? 0 : beSeverity.hashCode());
        result = prime * result + ((messageText == null) ? 0 : messageText.hashCode());
        result = prime * result + ((refTechKey == null) ? 0 : refTechKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (!(object instanceof BackendMessageCRM)) {
            return false;
        }

        final BackendMessageCRM message = (BackendMessageCRM) object;

        final boolean checkBeDetails = checkBeDetails(message);
        final boolean checkVars = checkVars(message);
        if (checkBeDetails && checkVars) {
            return true;
        }
        return false;
    }

    private boolean checkVars(final BackendMessageCRM message) {
        return this.vars[0].equals(message.vars[0]) && this.vars[1].equals(message.vars[1])
                && this.vars[2].equals(message.vars[2]) && this.vars[3].equals(message.vars[3]);
    }

    private boolean checkBeDetails(final BackendMessageCRM message) {
        return this.beSeverity.equals(message.beSeverity) && this.beNumber.equals(message.beNumber)
                && this.beClass.equals(message.beClass);
    }

}
