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
package hu.sonrisa.backend.model.util;

import hu.sonrisa.backend.versionedobject.VersionedObject;
import java.text.SimpleDateFormat;

/**
 * @author Palesz
 */
public final class VersionedObjectUtil {

    private VersionedObjectUtil() {
    }

    public static String getMegjelenithetoVerzio(VersionedObject versionedObject) {        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd. HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        if (versionedObject.getCreatedAt() != null) {
            sb.append(sdf.format(versionedObject.getCreatedAt()));
        }
        if (versionedObject.getCreatedBy() != null) {
            sb.append(" - ").append(versionedObject.getCreatedBy());
        }
        if (versionedObject.getVerzioNev() != null && !versionedObject.getVerzioNev().isEmpty()) {
            sb.append(": ").append(versionedObject.getVerzioNev());
        }
        return sb.toString();
    }
}
