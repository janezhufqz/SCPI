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
package com.sap.hybris.sapcrmcustomerb2b.outbound;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hybris.datahub.core.rest.DataHubOutboundException;
import com.hybris.datahub.core.services.DataHubOutboundService;


/**
 * @author C5230256
 *
 */
public class B2BContactExporterDataHub implements B2BContactExporter
{
     private static final Logger LOGGER = Logger
               .getLogger(com.sap.hybris.sapcrmcustomerb2b.outbound.B2BContactExporterDataHub.class.getName());

     private String feedName;
     private String relationshipRawType;
     private String b2bContactRawType;

     private DataHubOutboundService dataHubOutboundService;

     /**
      * exports the b2bcontact data to backend
      */
     @Override
     public void exportContact(List<Map<String, Object>> rawData)
     {
          sendToDatahub(rawData, b2bContactRawType);

     }

     /**
      * 
      * Exports the contact and b2b unit relationship data to backend 
      */
     @Override
     public void exportRelations(List<Map<String, Object>> rawData)
     {
          sendToDatahub(rawData, relationshipRawType);
     }

     private void sendToDatahub(List<Map<String, Object>> rawData, String rawType)
     {
          if (rawData != null && !rawData.isEmpty())
          {
               //calls the sendToDatahub method of the datahub adapter to send all the data to datahub as a list of maps, to be posted to given
                 //feed and to the given raw type of datahub
                    try {
                        dataHubOutboundService.sendToDataHub(feedName, rawType, rawData);
                    } catch (DataHubOutboundException  e) {
                          LOGGER.error("Error processing sending data to Data Hub. " + e);
                      }
                    if (LOGGER.isDebugEnabled())
                    {
                         LOGGER.debug("The following values were sent to Data Hub" + rawData + "(to the feed" + feedName
                                   + " into raw model " + rawType + ")");
                    }
          }
          else
          {
               LOGGER.debug("Data not sent to backend because target map is empty");
          }
     }


     /**
      * @return the relationshipFeedName
      */
     public String getFeedName()
     {
          return feedName;
     }

     /**
      * @param feedName
      *           the relationshipFeedName to set
      */
     public void setFeedName(String feedName)
     {
          this.feedName = feedName;
     }

     /**
      * @return the relationshipRawType
      */
     public String getRelationshipRawType()
     {
          return relationshipRawType;
     }

     /**
      * @param relationshipRawType
      *           the relationshipRawType to set
      */
     public void setRelationshipRawType(String relationshipRawType)
     {
          this.relationshipRawType = relationshipRawType;
     }

     /**
      * @return the b2bContactRawType
      */
     public String getB2bContactRawType()
     {
          return b2bContactRawType;
     }

     /**
      * @param b2bContactRawType
      *           the b2bContactRawType to set
      */
     public void setB2bContactRawType(String b2bContactRawType)
     {
          this.b2bContactRawType = b2bContactRawType;
     }

     /**
      * @return the dataHubOutboundService
      */
     public DataHubOutboundService getDataHubOutboundService()
     {
          return dataHubOutboundService;
     }

     /**
      * @param dataHubOutboundService
      *           the dataHubOutboundService to set
      */
     public void setDataHubOutboundService(DataHubOutboundService dataHubOutboundService)
     {
          this.dataHubOutboundService = dataHubOutboundService;
     }



}
