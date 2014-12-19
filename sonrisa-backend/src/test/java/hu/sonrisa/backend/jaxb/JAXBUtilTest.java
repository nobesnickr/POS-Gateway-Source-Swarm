/*
 *   Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
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
package hu.sonrisa.backend.jaxb;

import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import hu.sonrisa.backend.model.XMLObject;
import hu.sonrisa.backend.model.util.StringUtil;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.zip.GZIPInputStream;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.xmlbeans.impl.util.Base64;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sonrisa
 */
public class JAXBUtilTest {

    @XmlRootElement(name = "test")
    private static class JaxbObject extends XMLObject {
        
        private String value;

        public JaxbObject() {
            super(0);
        }
        
        @XmlAttribute
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
        
    }

    public JAXBUtilTest() {
    }

    /**
     * Test of marshal method, of class JAXBUtil.
     */
    @Test
    public void testMarshal_Object() throws IOException{
        JaxbObject x = new JaxbObject();
        x.setValue("valami");
        String tst = JAXBUtil.marshal(x);
        assertEquals("<?xml version='1.0' encoding='UTF-8'?><test value=\"valami\" objectVersion=\"0\"/>", tst);
        JaxbObject y = JAXBUtil.unmarshalTyped(new StringReader(tst), JaxbObject.class);
        assertEquals(0, y.getObjectVersion().intValue());
        assertEquals("valami", y.getValue());
        String tst2 = JAXBUtil.gZip(JAXBUtil.marshal(x));
        GZIPInputStream gin = new GZIPInputStream(new ByteArrayInputStream(Base64.decode(tst2.getBytes(StringUtil.UTF8))));
        BufferedReader br = new BufferedReader(new InputStreamReader(gin));
        String l;
        String xs = "";
        while((l = br.readLine()) != null){
            xs += l;
        }
        JaxbObject z = JAXBUtil.unmarshalTyped(new StringReader(xs), JaxbObject.class);
        assertEquals("valami", z.getValue());
    }



    @Test
    public void testZipping() throws IOException{
        JaxbObject x = new JaxbObject();
        x.setValue("valami");
        String line = null;
        String exp = JAXBUtil.marshal(x);
        long time = System.currentTimeMillis();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream zip = new GZIPOutputStream(out);
        zip.write(exp.getBytes(StringUtil.UTF8));
        zip.flush();
        zip.close();
        byte[] b1 = out.toByteArray();
        String encoded = new String(Base64.encode(b1), StringUtil.UTF8);
        String encoded2 = JAXBUtil.gZip(JAXBUtil.marshal(x));
        assertEquals(encoded, encoded2);
        System.out.println("******************** time: " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        byte[] decodedBytes = Base64.decode(encoded.getBytes(StringUtil.UTF8));
        ByteArrayInputStream bin = new ByteArrayInputStream(decodedBytes);
        GZIPInputStream zin = new GZIPInputStream(bin);
        BufferedReader br = new BufferedReader(new InputStreamReader(zin, StringUtil.UTF8));
        line = null;
        String res = "";
        while((line = br.readLine()) != null){
            res += line;
        }
        System.out.println("******************** time2: " + (System.currentTimeMillis() - time));
        assertEquals(exp, res);
        JaxbObject xx = JAXBUtil.unmarshalTyped(new StringReader(res), JaxbObject.class);
        assertEquals("valami", xx.getValue());
    }
}
