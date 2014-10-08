/*
 *  Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Sonrisa Informatikai Kft. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sonrisa.
 *
 * SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package hu.sonrisa.backend.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Valamilyen művelet eredményét tartalmazó objektum.
 *
 * @param <T> A tárolt célpont objektum típusa
 * @author Dombi Gergely
 */
public abstract class AbsztraktMuveletEredmenye<T> implements Serializable {

    /**
     * Célpont - az objektum amin a művelet állapotváltozást idéz elő
     */
    private T celpont;
    // ------------------------------------------------------------
    // ~ Member fields
    // ------------------------------------------------------------
//    /**
//     * Üzenetek (hibaüzenetek) a feltöltés lépéseiről.
//     */
//    private Map<UzenetTipus, List<String>> uzenetek = new HashMap<UzenetTipus, List<String>>(7);
    
    public static final String MUVELETEREDMENY_BEEGETETT_STRING_PLACEHOLDER = "absztraktEredmeny.placeHolder";
    private EnumMap<UzenetTipus, List<ResourceBasedUzenet>> resourceBasedUzenet = new EnumMap<UzenetTipus, List<ResourceBasedUzenet>>(UzenetTipus.class);
    /**
     * Az import státusza. SIKERES, ha minden fájlt feldolgoztunk és nem kell más
     * fájl az importhoz. HIBA egyébként (akár hiányzó, akár hibás file).
     */
    private MuveletStatusz statusz;

    // ------------------------------------------------------------
    // ~ Constructors
    // ------------------------------------------------------------
    /**
     * Konstruktor
     */
    public AbsztraktMuveletEredmenye() {
    }

    /**
     * Státusz getter
     * @return
     */
    public MuveletStatusz getStatusz() {
        return statusz;
    }

    /**
     * Státusz setter
     * @param statusz
     */
    public void setStatusz(MuveletStatusz statusz) {
        this.statusz = statusz;
    }

    public void addHiba(ResourceBasedUzenet h) {
        addUzenet(UzenetTipus.FELDOLGOZASI_HIBA, h);
    }

    /**
     * resource alapu uzenet hozzaadasa az eredmenyhez
     * 
     * @param uzenetTipus
     * @param resourceKey
     * @param parameters
     */
    public void addUzenet(UzenetTipus uzenetTipus, String resourceKey, Object... parameters) {
        addUzenet(uzenetTipus, new ResourceBasedUzenet(resourceKey, parameters));
    }
    
    /**
     * Uzenet hozzaadasa
     * @param uzenetTipus
     * @param uzenet 
     */
    public void addUzenet(UzenetTipus uzenetTipus, ResourceBasedUzenet uzenet) {
        List<ResourceBasedUzenet> uzenetek = resourceBasedUzenet.get(uzenetTipus);
        if (uzenetek == null) {
            resourceBasedUzenet.put(uzenetTipus, uzenetek = new ArrayList<ResourceBasedUzenet>());
        }
        uzenetek.add(uzenet);
    }
    
   /**
     * Uzenetek hozzaadasa
     * @param uzenetTipus
     * @param toAdd 
     */
    public void addUzenetek(UzenetTipus uzenetTipus, List<ResourceBasedUzenet> toAdd) {
        List<ResourceBasedUzenet> uzenetek = resourceBasedUzenet.get(uzenetTipus);
        if (uzenetek == null) {
            resourceBasedUzenet.put(uzenetTipus, uzenetek = new ArrayList<ResourceBasedUzenet>());
        }
        uzenetek.addAll(toAdd);
    }

    /**
     * resource alapu uzenetek
     *
     * @return
     */
    public Map<UzenetTipus, List<ResourceBasedUzenet>> getResourceUzenetek() {
        return resourceBasedUzenet;
    }

    /**
     * Célpont getter
     * @return
     */
    public T getCelpont() {
        return celpont;
    }

    /**
     * Célpont setter
     * @param celpont
     */
    public void setCelpont(T celpont) {
        this.celpont = celpont;
    }
}
