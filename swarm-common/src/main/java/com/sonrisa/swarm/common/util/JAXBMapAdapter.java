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
package com.sonrisa.swarm.common.util;

import com.sonrisa.swarm.common.util.JAXBMapAdapter.MapElements;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter class to marshal and unmarshal Map objects to XML and back to Map objects with JAXB.
 *
 * @author joe
 */
public class JAXBMapAdapter extends XmlAdapter<MapElements[], Map<String, Object>> {

    @Override
    public MapElements[] marshal(Map<String, Object> arg0) throws Exception {
        MapElements[] mapElements = new MapElements[arg0.size()];
        int i = 0;
        for (Map.Entry<String, Object> entry : arg0.entrySet()) {
            mapElements[i++] = new MapElements(entry.getKey(), entry.getValue());
        }

        return mapElements;
    }

    @Override
    public Map<String, Object> unmarshal(MapElements[] arg0) throws Exception {
        Map<String, Object> r = new HashMap<String, Object>();
        for (MapElements mapelement : arg0) {
            r.put(mapelement.key, mapelement.value);
        }
        return r;
    }

    /**
     * XML representation of a map entry.
     */
    public static class MapElements {

        @XmlElement
        public String key;
        @XmlElement
        public Object value;

        private MapElements() {
        } //Required by JAXB

        public MapElements(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}
