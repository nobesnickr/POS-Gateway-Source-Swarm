/*
 *   Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.
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
package com.sonrisa.swarm.staging.dao.jdbc;

import com.sonrisa.swarm.model.StageBatchInsertable;
import java.util.List;


/**
 * Interface of DAOs that uses jdbcTemplate and is capable of batch insert.
 *
 * @author joe
 */
public interface JdbcTemplateBasedDao<T extends StageBatchInsertable> {

    /**
     * JDBCTemplate batch insert. The parameter list comes from the Spring Batch
     * Writer phase.
     *
     * @param entities
     */
    void create(List<? extends T> entities);
    
    /**
     * Get the table name which is used by the insert script.
     *
     * @return
     */
     String getTableName();
}
