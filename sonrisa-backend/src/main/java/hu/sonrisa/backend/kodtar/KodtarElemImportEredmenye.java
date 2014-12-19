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

package hu.sonrisa.backend.kodtar;

import hu.sonrisa.backend.model.AbsztraktMuveletEredmenye;
import hu.sonrisa.backend.model.util.StringUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Kódtár elemek importjának eredményét tároló osztály.
 *
 * @author Joe
 */
public class KodtarElemImportEredmenye extends AbsztraktMuveletEredmenye<List<KodtarElem>>  {

    /**
     * Attribútum nevekhez tartozó kódtár uuid-eket tárol, az import során
     */
    private Map<String, String> attrKodtarMap = new HashMap<String, String>();
    
    private List<Kodtar> kodtarak = new ArrayList<Kodtar> ();
    
    private boolean fromExportFile = false;
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (KodtarElem de : getCelpont()) {
            b.append(de.getKodtar()).append(" : ").
                    append(de.getId()).append(" - ").
                                append(de.getMegnevezes()).append(StringUtil.newLine());
        }
        return b.toString();
    }
    
    public void addKodtar(String attributum, String uuid) {
        attrKodtarMap.put(attributum, uuid);        
    }

    public String getKodtar(String nev) {
        return attrKodtarMap.get(nev);
    }
    
    public List<Kodtar> getImportaltKodtark(){
        return kodtarak;
    }

    public boolean isFromExportFile() {
        return fromExportFile;
    }

    public void setFromExportFile(boolean fromExportFile) {
        this.fromExportFile = fromExportFile;
    }
}