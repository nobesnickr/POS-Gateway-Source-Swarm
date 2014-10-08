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
package hu.sonrisa.backend.auditlog;

import hu.sonrisa.backend.auth.SessionCredentialsProvider;
import hu.sonrisa.backend.service.GenericServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Audit logging service implementation
 *
 * @author Joe
 */
@Service()
@Configurable
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class AuditLogServiceImpl extends GenericServiceImpl<Long, AuditLog, AuditLogDao> implements AuditLogService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuditLogServiceImpl.class);
    private AuditLogDao dao;
    @Autowired(required = false)
    private SessionCredentialsProvider sessionCredentialsProvider;

    @Autowired
    public AuditLogServiceImpl(AuditLogDao dao) {
        super(dao);
        this.dao = dao;
    }

    /**
     *
     * @param esemeny
     * @param parameter
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void log(String esemeny, String parameter) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("AuditLog: " + esemeny);
        }
        dao.log(esemeny, parameter, sessionCredentialsProvider);
    }
}
