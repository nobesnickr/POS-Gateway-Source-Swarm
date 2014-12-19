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

package hu.sonrisa.backend.dao.filter;

import hu.sonrisa.backend.entity.SonrisaJPAEntity;

/**
 * A Wicket-es DataView/DataProvider-hez szükséges filter objektumok interfézse
 * 
 * @param <T> 
 * @author cserepj
 */
public interface SortableProviderJpaFilter<T extends SonrisaJPAEntity<?>> extends ProviderJpaFilter<T> {

    /**
     * A lekérdezés listájának sorrendje növekvő-e
     * @return
     */
    boolean isAscending();

    /**
     * Azon mező neve, ami szerint alakul a sorrend
     * @return
     */
    String getSort();
}
