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
package com.sonrisa.posintegration.extractor.util;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.posintegration.extractor.util.ExternalDTOTransformer;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;


/**
 * This class test the behaviour of the ExternalDTO transformer class
 */
public class ExternalDTOTransformerTest {
    
    /** 
     * Object being tested
     */
    ExternalDTOTransformer dtoTransformer;
    
    /**
     * Mocks an InternalDTO with all kinds of annotated fields
     */
    public static class InternalDTO implements DWTransferable {
        
        private int intProperty;
        
        private int nonRequiredIntProperty = 0;
        
        private long longProperty;
        
        private double doubleProperty;
        
        private short shortProperty;
        
        private Timestamp iso8061Property;
        
        private String stringProperty;
        
        private String embededStringProperty;
        
        private List<String> iterableProperty;
        
        private BigDecimal bigDecimalProperty;
        
        public long getRemoteId(){
            return 12345L;
        }
        
        public int getIntProperty() {
            return intProperty;
        }

        public long getLongProperty() {
            return longProperty;
        }

        public double getDoubleProperty() {
            return doubleProperty;
        }

        public short getShortProperty() {
			return shortProperty;
		}

		public Timestamp getIso8061Property() {
			return iso8061Property;
		}

		public String getStringProperty() {
            return stringProperty;
        }

        public String getEmbededStringProperty() {
            return embededStringProperty;
        }

        public List<String> getIterableProperty() {
            return iterableProperty;
        }

        public int getNonRequiredIntProperty() {
            return nonRequiredIntProperty;
        }
        
        public BigDecimal getBigDecimalProperty() {
            return bigDecimalProperty;
        }

        public InternalDTO(){
            
        }

        @ExternalField(value = "intProperty", required = true)
        public void setIntProperty(int intProperty) {
            this.intProperty = intProperty;
        }

        @ExternalField("nonRequiredIntProperty")
        public void setNonRequiredIntProperty(int nonRequiredIntProperty) {
            this.nonRequiredIntProperty = nonRequiredIntProperty;
        }

        @ExternalField("longProperty")
        public void setLongProperty(long longProperty) {
            this.longProperty = longProperty;
        }

        @ExternalField("doubleProperty")
        public void setDoubleProperty(double doubleProperty) {
            this.doubleProperty = doubleProperty;
        }
        
        @ExternalField("shortProperty")
        public void setShortProperty(short shortProperty) {
			this.shortProperty = shortProperty;
		}

        @ExternalField("iso8061Property")
		public void setIso8061Property(Timestamp iso8061Property) {
			this.iso8061Property = iso8061Property;
		}

		@ExternalField("stringProperty")
        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }

        @ExternalField("embededProperty")
        public void setEmbededStringProperty(ExternalDTO embededDTO) {
            this.embededStringProperty = embededDTO.getText("stringProperty");
        }

        @ExternalField("iterableProperty")
        public void setIterableProperty(Iterable<ExternalDTO> iterableProperty) {
            this.iterableProperty = new ArrayList<String>();
            for(ExternalDTO dto : iterableProperty) {
                this.iterableProperty.add(dto.getText("stringProperty"));
            }
        }

        @ExternalField("bigDecimalProperty")
        public void setBigDecimalProperty(BigDecimal bigDecimalProperty) {
            this.bigDecimalProperty = bigDecimalProperty;
        }

        @Override
        public Timestamp getLastModified() {
            return new Timestamp(0L);
        }
    }
    
    /**
     * Mocks an ExternalDTO
     * @author sonrisa
     *
     */
    private static class MockExternalDTO implements ExternalDTO {
        
        private int intProperty;
        
        private long longProperty;
        
        private double doubleProperty;
        
        private short shortProperty;

        private String stringProperty;

        private String bigDecimalProperty;
        
        private ExternalDTO embededProperty;
        
        private List<ExternalDTO> iterableProperty;
        
        private String iso8061Property;
        
        @Override
        public int getInt(String key) throws ExternalExtractorException {
            if(key.equals("intProperty")){
                return intProperty;
            }
            throw new ExternalExtractorException("Error!");
        }

        @Override
        public long getLong(String key) throws ExternalExtractorException {
            if(key.equals("longProperty")){
                return longProperty;
            }
            throw new ExternalExtractorException("Error!");
        }

        @Override
        public double getDouble(String key) throws ExternalExtractorException {
            if(key.equals("doubleProperty")){
                return doubleProperty;
            }
            throw new ExternalExtractorException("Error!");
        }
        
        @Override
		public short getShort(String key) throws ExternalExtractorException {
        	if (key.equals("shortProperty")) {
        		return shortProperty;
        	}
			return 0;
		}

		@Override
		public Timestamp getTimeStampISO8061(String key) throws ExternalExtractorException {
			if (key.equals("iso8061Property")) {
				return new Timestamp(ISO8061DateTimeConverter.stringToDate(iso8061Property).getTime());
			}
			throw new ExternalExtractorException("Error!");
		}

        @Override
        public String getText(String key) {
            if(key.equals("stringProperty")){
                return stringProperty;
            } else if(key.equals("bigDecimalProperty")){
                return bigDecimalProperty;
            } else {
                return "";
            }
        }

        @Override
        public Iterable<ExternalDTO> getNestedItems(String key) throws ExternalExtractorException {
            if(key.equals("iterableProperty")){
                return iterableProperty;
            }
            throw new ExternalExtractorException("Error!");
        }

        @Override
        public ExternalDTO getNestedItem(ExternalDTOPath key) throws ExternalExtractorException {
            if(key.getFields().length == 1 && key.getFields()[0].equals("embededProperty")){
                return embededProperty;
            }
            throw new ExternalExtractorException("Error!");
        }

        @Override
		public boolean hasKey(String key) {
			if (key.equals("embededProperty") ||
				key.equals("iterableProperty") ||
				key.equals("stringProperty") ||
				key.equals("doubleProperty") ||
				key.equals("longProperty") ||
				key.equals("bigDecimalProperty") ||
				key.equals("intProperty") ||
				key.equals("iso8061Property") ||
				key.equals("shortProperty"))
			{
				return true;
			}
			return false;
		}

        @Override
        public int getNestedItemSize(ExternalDTOPath key) {
            return 0;
        }

        @Override
        public ExternalDTO getNestedArrayItem(int index) throws ExternalExtractorException {
            if(index == 0){
                return embededProperty;
            }
            throw new ExternalExtractorException("Error!");
        }

        public void setIntProperty(int intProperty) {
            this.intProperty = intProperty;
        }

        public void setLongProperty(long longProperty) {
            this.longProperty = longProperty;
        }

        public void setDoubleProperty(double doubleProperty) {
            this.doubleProperty = doubleProperty;
        }

        public void setShortProperty(short shortProperty) {
			this.shortProperty = shortProperty;
		}

		public void setIso8061(String iso8061) {
			this.iso8061Property = iso8061;
		}

		public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }

        public void setEmbededStringProperty(ExternalDTO embededProperty) {
            this.embededProperty = embededProperty;
        }

        public void setIterableProperty(List<ExternalDTO> iterableProperty) {
            this.iterableProperty = iterableProperty;
        }

        public void setBigDecimalProperty(String bigDecimalProperty) {
            this.bigDecimalProperty = bigDecimalProperty;
        }
    }

    /**
     * Setup test context
     */
    @Before
    public void setupTestContext(){
        this.dtoTransformer = new ExternalDTOTransformer();
    }
    
    
    /**
     * Test mapping from ExternalDTO to InternalDTO
     * @throws ExternalExtractorException
     */
    @Test
    public void testMappingOnItems() throws ExternalExtractorException {
        
        final int intValue = 555;
        final long longValue = 744L + (1L << 42);
        final double doubleValue = 64.0;
        final short shortValue = (short)16;
        final String stringValue = "QQQQxxxxxxx!!!!!   && |";
        final String embededString = "EMBBBBBEEEDDDDDEEEEDDDD!!!";
        final int iterableCount = 31;
        final BigDecimal bigDecimalValue = new BigDecimal("100000.000001");
        final String iso8061Value = "2014-05-02T19:19:12.793";
        
        MockExternalDTO embededDTO = new MockExternalDTO();
        embededDTO.setStringProperty(embededString);
        
        MockExternalDTO externalDTO = new MockExternalDTO();
        externalDTO.setDoubleProperty(doubleValue);
        externalDTO.setShortProperty(shortValue);
        externalDTO.setIntProperty(intValue);
        externalDTO.setLongProperty(longValue);
        externalDTO.setStringProperty(stringValue);
        externalDTO.setEmbededStringProperty(embededDTO);
        externalDTO.setBigDecimalProperty(bigDecimalValue.toPlainString());
        externalDTO.setIso8061(iso8061Value);
        
        ArrayList<ExternalDTO> iterableItems = new ArrayList<ExternalDTO>();
        for(int i = 0; i < iterableCount; i++){
            iterableItems.add(embededDTO);
        }
        
        externalDTO.setIterableProperty(iterableItems);
        
        InternalDTO dto = dtoTransformer.transformDTO(externalDTO, InternalDTO.class);
        
        assertEquals(intValue, dto.getIntProperty());
        assertEquals(0, dto.getNonRequiredIntProperty());
        assertEquals(longValue, dto.getLongProperty());
        assertEquals(doubleValue, dto.getDoubleProperty(),0.0001);
        assertEquals(shortValue, dto.getShortProperty());
        assertEquals(stringValue, dto.getStringProperty());
        assertEquals(bigDecimalValue, dto.getBigDecimalProperty());
        assertEquals(embededString, dto.getEmbededStringProperty());        			 
        assertEquals(iterableCount, dto.getIterableProperty().size());
        assertEquals(embededString, dto.getIterableProperty().get(0));
    }
}
