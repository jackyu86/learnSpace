/*
Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License"). You may
not use this file except in compliance with the License. A copy of the
License is located at

    http://aws.amazon.com/apache2.0/

or in the "license" file accompanying this file. This file is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.
 */

package com.amazon.merchants.executor;

import java.text.MessageFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.amazon.merchants.Messages;
import com.amazon.merchants.logger.TransportLogger;
import com.amazon.merchants.services.TransportService;

public class TransportScheduler {
    private static TransportScheduler instance = new TransportScheduler();
    private final int INITALDELAY = 0;
    private int corePoolSize;
    private ScheduledThreadPoolExecutor exec;


    private TransportScheduler() {
    }


    /**
     * Get an TransportScheduler instance
     *
     * @return the singleton instance
     */
    public static TransportScheduler getInstance() {
        return instance;
    }


    /**
     * Load the scheduler's executor
     *
     * @param corePoolSize
     *            number of worker threads, usually equals to the number of AMTU
     *            services
     */
    public void start(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        exec = (ScheduledThreadPoolExecutor) Executors
            .newScheduledThreadPool(corePoolSize);
        TransportLogger.getSysAuditLogger().debug(
            Messages.TransportScheduler_0.toString() + corePoolSize);
    }


    public int getCorePoolSize() {
        return corePoolSize;
    }


    /**
     * Register a repeating task to the executor
     *
     * @param service
     *            an AMTU service
     * @param period
     *            recurring period in @unit
     * @param unit
     *            time unit, i.e. DAYS, HOURS, MUNITES, SECONDS
     */
    public void schedule(TransportService service, long period, TimeUnit unit) {
        exec.scheduleAtFixedRate(service, INITALDELAY, period, unit);

        Object[] messageArguments = { service.toString(), new Long(period),
            unit.toString() };
        MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
        formatter.applyPattern(Messages.TransportScheduler_1.toString());
        TransportLogger.getSysAuditLogger().debug(
            formatter.format(messageArguments));
    }


    public void scheduleWithInitialDelay(TransportService service,
        long initialDelay, long period, TimeUnit unit) {
        exec.scheduleAtFixedRate(service, initialDelay, period, unit);

        Object[] messageArguments = { service.toString(), new Long(period),
            unit.toString() };
        MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
        formatter.applyPattern(Messages.TransportScheduler_1.toString());
        TransportLogger.getSysAuditLogger().debug(
            formatter.format(messageArguments));
    }


    /**
     * Re-register a repeating task to the executor
     *
     * @param service
     *            an AMTU service
     * @param period
     *            recurring period in @unit
     * @param unit
     *            time unit, i.e. DAYS, HOURS, MUNITES, SECONDS
     */
    public void reschedule(TransportService service, long period, TimeUnit unit) {
        // Should be able to reschedule
        exec.remove(service);
        exec.scheduleAtFixedRate(service, INITALDELAY, period, unit);
    }


    /**
     * Re-register a repeating task to the executor to be started after the
     * given delay
     *
     * @param service
     *            an AMTU service
     * @param initialDelay
     *            delay before the first start
     * @param period
     *            recurring period in @unit
     * @param unit
     *            time unit, i.e. DAYS, HOURS, MUNITES, SECONDS
     */
    public void rescheduleWithInitialDelay(TransportService service,
        long initialDelay, long period, TimeUnit unit) {
        // Should be able to reschedule
        exec.remove(service);
        exec.scheduleAtFixedRate(service, initialDelay, period, unit);
    }


    /**
     * Shutdown the scheduler's executor. The executor will attempt to finish
     * all unfinished tasks and not accept any new tasks
     */
    public void shutdown() {
        exec.shutdown();
        TransportLogger.getSysAuditLogger().debug(
            Messages.TransportScheduler_2.toString());
    }
}