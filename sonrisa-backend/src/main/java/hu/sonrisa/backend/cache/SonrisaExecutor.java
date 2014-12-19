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
package hu.sonrisa.backend.cache;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

/**
 *  Execution interface abstraction
 * 
 * @author János Cserép <cserepj@sonrisa.hu>
 */
public interface SonrisaExecutor {

    /**
     * Runs the given callable on all nodes where the application is running
     * 
     * @param <T>
     * @param callable
     * @return
     */
    <T> Collection<T> runEveryWhere(Callable<T> callable);

    /**
     * Runs the given callable on the specified host only
     * 
     * @param <T>
     * @param callable
     * @param hostId
     * @return
     */
    <T> T runOnHost(Callable<T> callable, String hostId);

    /**
     * returns a named lock
     * 
     * @param string
     * @return
     */
    Lock getLock(String string);
}