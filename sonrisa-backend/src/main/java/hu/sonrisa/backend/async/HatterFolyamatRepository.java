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
package hu.sonrisa.backend.async;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Palesz
 */
public final class HatterFolyamatRepository {

    private static final Map<String, HatterFolyamat> folyamatMap =
            new ConcurrentHashMap<String, HatterFolyamat>();

    /**
     *
     */
    public HatterFolyamatRepository() {
    }

    /**
     * Hozzáadja a folyamatot a nyilvántartáshoz, és addig a nyilvántartásban
     * tartja, amíg már csak weakreference-ek vannak rá.
     *
     * @param folyamat
     */
    public void add(HatterFolyamat folyamat) {
        synchronized (HatterFolyamatRepository.class) {
            addFolyamat(folyamat);
        }
    }

    private void addFolyamat(HatterFolyamat folyamat) {
        folyamatMap.put(folyamat.getId(), folyamat);
    }

    /**
     *
     * @param id
     * @return
     */
    public HatterFolyamat get(String id) {
        return folyamatMap.get(id);
    }

    void remove(String id) {
        folyamatMap.remove(id);
    }
}
