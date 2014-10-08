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

import java.util.HashSet;
import java.util.Set;

/**
 * Általános állapotgép megengedett és tiltott állapotátmenetek kezelésére.
 *
 * A megengedett átmeneteket egy Stringeket tartalmazó Set-ben tárolja, az
 * állapotok egy enum értékei lehetnek.
 *
 * @param <T> az enum a konkrét állapotokkal
 * @author cserepj
 */
public class StateMachine<T extends Enum<T>> {

    /**
     * A megengedett állapotátmenetek String reprezentációja
     */
    private Set<String> validTransitions = new HashSet<String>(10);

    /**
     * Megmondja, hogy az átadott állapotok közötti átmenet megengedett-e, vagy
     * sem
     *
     * @param oldStatus a kiindulási állapot
     * @param newStatus az új állapot
     * @return boole érték
     */
    public boolean isValidStateTransition(T oldStatus, T newStatus) {
        return validTransitions.contains(getTransitionString(oldStatus, newStatus));
    }

    /**
     * Megpróbálja átvezetni az átadott objektumot az új állapotba
     *
     * @param object egy állapottal rendelkező objektum
     * @param newStatus az új állapot
     * @return igaz, ha sikerült
     */
    public boolean transitionToState(StatusAware<T> object, T newStatus) {
        if (isValidStateTransition(object.getStatusz(), newStatus)) {
            object.setStatusz(newStatus);
            return true;
        }
        return false;
    }

    /**
     * A megadott státuszátmenetet felveszi a megengedett átmenetek közé
     *
     * @param oldStatus kiindulási állapot
     * @param newStatus cél állapot
     */
    public void addValidTransition(T oldStatus, T newStatus) {
        String key = getTransitionString(oldStatus, newStatus);
        validTransitions.add(key);
    }

    /**
     * Előállítja a két állapot átmenetének string reprezentációját
     *
     * @param oldStatus kiindulási állapot
     * @param newStatus cél állapot
     * @return
     */
    private String getTransitionString(T oldStatus,
            T newStatus) {
        return oldStatus.name() + "->" + newStatus.name();
    }
}
