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

import com.hazelcast.core.DistributedTask;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.core.MultiTask;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Lock;

/**
 * Hazelcast based distributed executor implementation
 * 
 * @author János Cserép <cserepj@sonrisa.hu>
 */
public abstract class HazelcastSonrisaExecutor implements SonrisaExecutor {

    /**
     * Subclasses should return reference to hazelcast based executor service
     * @return
     */
    public abstract ExecutorService getExecutor();

    /**
     * Subclasses should return reference to hazelcast instance
     * @return
     */
    public abstract HazelcastInstance getInstance();

    @Override
    public <T> Collection<T> runEveryWhere(Callable<T> callable) {
        try {
            Set<Member> members = getInstance().getCluster().getMembers();
            MultiTask mt = new MultiTask(callable,
                    members);
            getExecutor().execute(mt);
            return mt.get();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public <T> T runOnHost(Callable<T> callable, String hostId) {
        try {
            Member m = findMember(hostId);
            FutureTask<T> x = new DistributedTask<T>(callable, m);
            getExecutor().execute(x);
            return x.get();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Lock getLock(String string) {
        return getInstance().getLock(string);
    }

    private Member findMember(String hostId) {
        for (Member m : getInstance().getCluster().getMembers()) {
            if (m.getInetSocketAddress().toString().equals(hostId)) {
                return m;
            }
        }
        return null;
    }
}
