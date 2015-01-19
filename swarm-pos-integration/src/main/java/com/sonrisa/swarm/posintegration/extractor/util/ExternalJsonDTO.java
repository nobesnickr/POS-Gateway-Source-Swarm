package com.sonrisa.swarm.posintegration.extractor.util;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTOPath;
import com.sonrisa.swarm.posintegration.util.ISO8061DateTimeConverter;

/**
 * Implementation of the ExternalDTO interface in a way, that this class 
 * wraps a JsonNode object together 
 */
public class ExternalJsonDTO implements ExternalDTO {
    
    /**
     * Content reflected through the interface is extracted
     * from this JsonNode
     */
    private JsonNode node;
    
    /**
     * Additional parameters 
     */
    private Map<String, String> addParameters = new HashMap<String, String>();
    
    /**
     * Initilizes an instance of the ExternalJsonDTO class.
     * @param node Node to be used to access data
     */
    public ExternalJsonDTO(JsonNode node){
        if(node == null){
            throw new IllegalArgumentException("Node can't be null.");
        }
        this.node = node;
    }

    /**
     * {@inheritDoc}
     * 
     * Note that if value is found, but can't be parsed as int, 0 will be returned
     * and no exception is thrown.
     */
    @Override
    public int getInt(String key) throws ExternalExtractorException {
        JsonNode item = node.get(key);
        if(item != null){
            return item.asInt();
        }
        throw new ExternalExtractorException("Missing value of: " + key);
    }

    /**
     * {@inheritDoc}
     * 
     * Note that if value is found, but can't be parsed as long, 0 will be returned
     * and no exception is thrown.
     */
    @Override
    public long getLong(String key) throws ExternalExtractorException {
        JsonNode item = node.get(key);
        if(item != null){
            return item.asLong();
        }
        throw new ExternalExtractorException("Missing value of: " + key);
    }

    /**
     * {@inheritDoc}
     * 
     * Note that if value is found, but can't be parsed as int, 0.0 will be returned
     * and no exception is thrown.
     */
    @Override
    public double getDouble(String key) throws ExternalExtractorException {
        JsonNode item = node.get(key);
        if(item != null){
            return item.asDouble();
        }
        throw new ExternalExtractorException("Missing value of: " + key);
    }

	/**
	 * {@inheritDoc}
	 * 
	 * Note that if value is found, but can't be parsed as short, 0.0 will be returned
	 * and no exception is thrown.
	 */
	@Override
	public short getShort(String key) throws ExternalExtractorException {
		JsonNode item = node.get(key);
		if (item != null) {
			try {
				return Short.valueOf(item.asText());
			} catch (NumberFormatException nfe) {
				return 0;
			}
		}
		throw new ExternalExtractorException("Missing value of: " + key);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText(String key) {
        JsonNode item = node.get(key);
        if(item != null){
            return item.asText();
        }
        return "";
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public Timestamp getTimeStampISO8061(String key) throws ExternalExtractorException {
		String stringVal = getText(key);
		return new Timestamp(ISO8061DateTimeConverter.stringToDate(stringVal).getTime());
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasKey(String key) {
        return node.get(key) != null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getNestedItemSize(ExternalDTOPath path){
        final JsonNode nestedNode = getJsonNodeAtPath(path);
        if(nestedNode != null){
            return nestedNode.size();
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<ExternalDTO> getNestedItems(final String key) throws ExternalExtractorException {
        final JsonNode nestedNode = node.get(key);
        if(nestedNode == null){
            throw new ExternalExtractorException("Nested items can't be found using key: " + key);
        }
        return new Iterable<ExternalDTO>(){
            @Override
            public Iterator<ExternalDTO> iterator() {
                return new Iterator<ExternalDTO>(){
                    private int index = 0;

                    @Override
                    public boolean hasNext() {
                        return nestedNode.size() > index;
                    }

                    @Override
                    public ExternalDTO next() {
                        return new ExternalJsonDTO(nestedNode.get(index++));
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Removal from external object is unsupported");
                    }
                };
            }
        };
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalDTO getNestedItem(ExternalDTOPath path) throws ExternalExtractorException {
        JsonNode retval = getJsonNodeAtPath(path);
        if(retval == null){
            throw new ExternalExtractorException("Nested object not found:" + path);
        }
        return new ExternalJsonDTO(retval);
    }
        
    /**
     * Returns the nested {@link JsonNode} at a location
     */
    public JsonNode getJsonNodeAtPath(ExternalDTOPath path){
        JsonNode retval = node; 
        for(String fieldKey : path.getFields()){
            retval = retval.get(fieldKey);

        if(retval == null) {
                return null;
        
    }
        }
        return retval;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalDTO getNestedArrayItem(int index) throws ExternalExtractorException {
        JsonNode retval = node.get(index);
        
        if(retval == null) {
            throw new ExternalExtractorException("Nested array item not found: " + index);
        }
        
        return new ExternalJsonDTO(retval);
    }
    
    /**
     * Stringfication calls the JsonNode's toString
     */
    @Override
    public String toString(){
        return node.toString();
    }
    
}
