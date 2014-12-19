/*
 *  *  Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
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

/**
 * Az import során felmerülő üzenetek típusait jelző enum
 */
public enum UzenetTipus {

    HIANYZO_FILE("error"), HIANYZO_KEZELO("error"), FELDOLGOZASI_HIBA("error"),
    FIGYELMEZTETES("warning"), INFORMACIO("info"), NEM_IMPORTALHATO("error");
    /**
     * Milyen szintű üzenet (error / warning  / info)
     */
    private String level;

    /**
     * Konsruktor
     * @param level
     */
    private UzenetTipus(String level) {
        this.level = level;
    }

    /**
     * Szint lekérdezése
     * @return
     */
    public String getLevel() {
        return level;
    }
}
