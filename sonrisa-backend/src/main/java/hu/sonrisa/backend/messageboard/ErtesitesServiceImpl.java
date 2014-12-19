/*
 *   Copyright (c) 2012 Sonrisa Informatikai Kft. All Rights Reserved.
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

package hu.sonrisa.backend.messageboard;

import hu.sonrisa.backend.auditlog.AuditLogServiceImpl;
import hu.sonrisa.backend.auth.SessionCredentialsProvider;
import hu.sonrisa.backend.service.GenericServiceImpl;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author joe
 */
@Service()
@Configurable
public class ErtesitesServiceImpl extends GenericServiceImpl<Long, Ertesites, ErtesitesDao> implements ErtesitesService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ErtesitesServiceImpl.class);
    
    private ErtesitesDao dao;
    
    @Autowired
    private SessionCredentialsProvider credentialsProvider;

    @Autowired
    public ErtesitesServiceImpl(ErtesitesDao dao) {
        super(dao);
        this.dao = dao;
    }
    
    
    @Override
    @Transactional
    public void save(Ertesites ertesites) {        
         if (ertesites.getId() == null) {
            ertesites.setDate(new Date());
            ertesites.setFelhasznalo(credentialsProvider.getSessionFelhasznalo().getId());
            ertesites.setFelhasznaloNev(credentialsProvider.getSessionFelhasznalo().getNev());
            dao.persist(ertesites);            
        } else {
            dao.merge(ertesites);
        }
        
    }
    
    @Override
    @Transactional 
    public void remove(long id){
        final Ertesites ertesites = dao.findById(id);
        if (ertesites != null){            
            dao.remove(ertesites);        
            LOGGER.debug("Ertesites torolve, id: " + id);
        }else{
            LOGGER.debug("Ertesites torlese sikertelen, nem letezo id: " + id);
        }
        
    }

}
