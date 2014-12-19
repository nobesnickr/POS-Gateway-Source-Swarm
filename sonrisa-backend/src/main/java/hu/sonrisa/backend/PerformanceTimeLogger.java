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
package hu.sonrisa.backend;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performancia mérésre. Taskok szerint méri az időt
 * @author Golyo
 */
public class PerformanceTimeLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceTimeLogger.class);
    private Map<String, Long> taskTimerMap;
    private long start;
    private String actTask;

    /**
     * 
     * @param debugChanges
     */
    public PerformanceTimeLogger(boolean debugChanges) {
        taskTimerMap = new HashMap<String, Long>();
    }

    /**
     * Elindított folyamat befejezése, ha volt ilyen,
     * Megadott folyamat mérésének elindítása
     * @param task
     */
    public void changeTask(String task) {
        finishTask(actTask);
        actTask = task;
        LOGGER.debug("START ----------- " + task);
        start = System.currentTimeMillis();
    }

    /**
     * Elindított folyamat befejezése
     */
    public void skipTask() {
        finishTask(actTask);
        actTask = null;
    }

    /**
     * Összes folyamat befejezése, loggolása
     */
    public void finishAndLog() {
        long summ = 0;
        finishTask(actTask);
        for (Map.Entry<String, Long> e : taskTimerMap.entrySet()) {
            LOGGER.debug(e.getKey() + " = " + e.getValue());
            summ += e.getValue();
        }
        LOGGER.debug("---------------------------");
        LOGGER.debug("OSSZESEN = " + summ);
        taskTimerMap.clear();
    }

    private void finishTask(String task) {
        if (actTask != null) {
            Long end = System.currentTimeMillis();
            Long act = taskTimerMap.get(task);
            if (act == null) {
                act = 0l;
            }
            act += (end - start);
            taskTimerMap.put(task, act);
            LOGGER.debug("FINISH ---------- " + task);
        }
    }
}
