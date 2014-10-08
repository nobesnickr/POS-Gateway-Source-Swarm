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

import hu.sonrisa.backend.exception.BackendException;
import hu.sonrisa.backend.model.XmlVerzioValto;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author cserepj
 */
public class XmlVerzioValtasDescriptor {
    private List<XmlVerzioValto> verzioValtasok;

    /**
     *
     * @param clazz
     */
    public XmlVerzioValtasDescriptor(Class<?> clazz) {
        int cnt = 1;
        boolean lastFound = false;
        List<XmlVerzioValto> vv = new ArrayList<XmlVerzioValto>();
        while (!lastFound) {
            Class<?> cl;
            try {
                String packagename = clazz.getPackage().getName();
                String clname = packagename + ".xmlchange." + clazz.getSimpleName() + "XmlVerzio" + cnt + "Valtas";
                cl = Class.forName(clname);
                XmlVerzioValto xvv = (XmlVerzioValto) cl.newInstance();
                vv.add(xvv);
                cnt++;
            } catch (ClassNotFoundException cnfex) {
                lastFound = true;
            } catch (Exception ex) {
                throw new BackendException(ex);
            }
        }
        verzioValtasok = Collections.unmodifiableList(vv);
    }

    /**
     * Lefuttatja a verzióváltást az átadott XML reprezentáción a JDOM
     * api segítségével
     *
     * @param text
     * @param verzio
     * @return
     */
    public CharSequence transform(CharSequence text, int verzio) {
        try {
            int all = verzioValtasok.size();
            if (all <= verzio) {
                return text;
            }
            SAXBuilder s = new SAXBuilder(false);
            Document d = s.build(new StringReader(text.toString()));
            for (XmlVerzioValto xvv : verzioValtasok.subList(verzio, all)) {
                d = xvv.transform(d);
            }
            return new XMLOutputter().outputString(d);
        } catch (JDOMException ex) {
            throw new BackendException(ex);
        } catch (IOException ioex){
            throw new BackendException(ioex);
        }
    }

    public boolean isTransformable(int verzio) {
        int all = verzioValtasok.size();
        if (all <= verzio) {
            return false;
        }
        return true;
    }
}
