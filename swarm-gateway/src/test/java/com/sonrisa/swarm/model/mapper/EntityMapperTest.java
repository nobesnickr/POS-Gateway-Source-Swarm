/*
 *   Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of
 *  Sonrisa Informatikai Kft. ("Confidential Information").
 *  You shall not disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Sonrisa.
 * 
 *  SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 *  THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *  TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *  PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 *  ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 *  DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.sonrisa.swarm.model.mapper;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sonrisa.swarm.BaseIntegrationTest;
import com.sonrisa.swarm.model.staging.BaseStageEntity;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.retailpro.RetailProAttr;
import com.sonrisa.swarm.model.staging.BaseStageEntity;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.retailpro.RetailProAttr;
import com.sonrisa.swarm.model.staging.retailpro.converter.DateAndTimePropertyConverter;
import com.sonrisa.swarm.model.staging.retailpro.converter.DatePropertyConverter;
import com.sonrisa.swarm.model.staging.retailpro.converter.FieldConcatenationConverter;
import com.sonrisa.swarm.retailpro.util.mapper.EntityMapper;

/**
 * Test cases for these classes: 
 * {@link EntityMapper},
 *
 * @author joe
 */
public class EntityMapperTest extends BaseIntegrationTest{
    
    @Autowired
    private EntityMapper mapper;
    
    private static final String DELIMITER_TEST = " # ";
    private static final String DATE_PATTERN = "yyyy-MM-dd hh:mm:ss";
    
    public EntityMapperTest() {
    }

    @Test
    public void testEntityMapperFormatting() throws ParseException {
        // creation of a json object
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        final String strValue = "value";
        final String dateValue = "2013-03-03 7:45:00";
        final String freeFormatDateValue = "12/03/2013 06:00:00 PM";
        final String dateAndTimeDateValue = "09/07/2013";
        final String dateAndTimeTimeValue = "04:23:16 PM";
                
        jsonMap.put("stringFieldJson", strValue);
        jsonMap.put("dateFieldJson", dateValue);
        jsonMap.put("freeFormatDateFieldJson", freeFormatDateValue);
        
        jsonMap.put("dateAndTimeDateFieldJson", dateAndTimeDateValue + " 12:00:00 AM");
        jsonMap.put("dateAndTimeTimeFieldJson", "12/30/1899 " + dateAndTimeTimeValue);
        
        jsonMap.put("fieldWithoutMapping", "foo");
        
        // conversion
        MapperTestClass entity = mapper.convertToStageEntity(jsonMap, MapperTestClass.class);
        
        // assertation
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        SimpleDateFormat formatOfFreeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);
        
        assertEquals(strValue, entity.stringField);
        assertEquals(Long.toString(sdf.parse(dateValue).getTime()), entity.dateField);
        assertEquals(
                formatOfFreeFormat.parse(freeFormatDateValue), 
                new Date(Long.parseLong(entity.freeFormatDateField))
        );
        assertEquals(
                dateAndTimeDateValue + " " + dateAndTimeTimeValue, 
                formatOfFreeFormat.format(new Date(Long.parseLong(entity.dateAndTimeField)))
        );
        assertNull(entity.fieldWithoutMapping);
        assertEquals(strValue + DELIMITER_TEST + strValue, entity.composedField);
    }
    
    /**
     * Test class used for testing the {@link RetailProAttr} annotation,
     * and the JSON->entity conversation proccess.
     */
    public static class MapperTestClass extends BaseStageEntity{
    	
        @RetailProAttr
        private String swarmId;

        @RetailProAttr(value="stringFieldJson")
        private String stringField;


        @RetailProAttr(value = "dateFieldJson", converter = DatePropertyConverter.class, params = DATE_PATTERN)
        private String dateField;

        @RetailProAttr(value = "freeFormatDateFieldJson", converter = DatePropertyConverter.class)
        private String freeFormatDateField;
       
        @RetailProAttr(value = "dateAndTimeDateFieldJson", converter = DateAndTimePropertyConverter.class, relatedFields = "dateAndTimeTimeFieldJson")
        private String dateAndTimeField;

        /**
         * This field has no mapping so its value will be null after the transformation.
         */
        private String fieldWithoutMapping;
        
        /**
         * This field would be a concatenation of three fields from the json map.
         */
        @RetailProAttr(value = DELIMITER_TEST, converter = FieldConcatenationConverter.class, 
                params = {"stringFieldJson", "stringFieldJson"})
        private String composedField;

        public MapperTestClass() {
        }

        @StageInsertableAttr(dbColumnName="swarm_id")
        @Override
        public String getSwarmId() {
            return swarmId;
        }

        @Override
        public void setSwarmId(String swarmId) {
            this.swarmId = swarmId;
        }

        @Override
        public Long getId() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Long getStoreId() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getLsStoreNo() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getLsSbsNo() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         * @return the stringField
         */
        @StageInsertableAttr(dbColumnName="stringFieldJson")
        public String getStringField() {
            return stringField;
        }

        /**
         * @return the dateField
         */
        @StageInsertableAttr(dbColumnName="dateFieldJson")
        public String getDateField() {
            return dateField;
        }

        /**
         * @return the fieldWithoutMapping
         */
        public String getFieldWithoutMapping() {
            return fieldWithoutMapping;
        }

        /**
         * @return the composedField
         */
        @StageInsertableAttr(dbColumnName="concatenateFieldJson")
        public String getComposedField() {
            return composedField;
        }

        @StageInsertableAttr(dbColumnName="freeFormatDateFieldJson")
        public String getFreeFormatDateField() {
            return freeFormatDateField;
        }

        @StageInsertableAttr(dbColumnName="dateAndTimeDateFieldJson")
        public String getDateAndTimeField() {
            return dateAndTimeField;
        }
    }
}