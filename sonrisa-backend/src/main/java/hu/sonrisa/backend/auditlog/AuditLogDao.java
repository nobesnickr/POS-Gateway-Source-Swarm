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
package hu.sonrisa.backend.auditlog;

import hu.sonrisa.backend.auth.SessionCredentialsProvider;
import hu.sonrisa.backend.dao.BaseJpaDao;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Repository;

/**
 * DAO class for AuditLog entities
 *
 * @author cserepj
 */
@Repository()
@Configurable
public class AuditLogDao extends BaseJpaDao<Long, AuditLog> {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuditLogDao.class);
    private final static String UNKNOWN = "ismeretlen";


    public AuditLogDao() {
        super(AuditLog.class);

    }

    /**
     * Saving an audit log entry
     *
     * @param event
     * @param parameter
     * @param creds
     */
    public void log(String event, String parameter, SessionCredentialsProvider creds) {
        AuditLog log = new AuditLog();
        log.setEsemeny(event);
        if (parameter != null) {
            log.setParameter(parameter.length() > 2000 ? parameter.substring(0, 1990) + "\n\n[...]" : parameter);
        }
        try {
            if (creds.getSessionFelhasznalo() != null) {
                log.setFelhasznaloId(creds.getSessionFelhasznalo().getId());
                log.setFelhasznaloNev(creds.getSessionFelhasznalo().getNev());
            } else {
                log.setFelhasznaloId(UNKNOWN);
                log.setFelhasznaloNev(UNKNOWN);
            }
            log.setSzervezet(creds.getSessionSzervezet());
            log.setHost(creds.getRemoteIp());
        } catch (Exception ex) {
            log.setFelhasznaloId(UNKNOWN);
            log.setFelhasznaloNev(UNKNOWN);
            log.setSzervezet("-");
            log.setHost("-");
        }
        log.setIdopont(new Date());
        try {
            persist(log);
        } catch (Exception ex) {
            LOGGER.error("Error while savint audit log: " + event, ex);
        }

    }

}
