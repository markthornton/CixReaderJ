package uk.me.mthornton.utility;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CommonScheduler {
    private static final AtomicInteger counter = new AtomicInteger();
    private static final ThreadFactory daemonThreadFactory = runnable -> {
        Thread thread = new Thread(runnable, "Daemon-"+counter.incrementAndGet());
        thread.setContextClassLoader(Thread.currentThread().getContextClassLoader());
        thread.setDaemon(true);
        return thread;
    };

    private static final ScheduledExecutorService scheduledExecutor =  new ScheduledThreadPoolExecutor(2, daemonThreadFactory);

    public static ThreadFactory daemonThreadFactory() {
        return daemonThreadFactory;
    }

    public static ScheduledExecutorService scheduledExecutor() {
        return scheduledExecutor;
    }
}
