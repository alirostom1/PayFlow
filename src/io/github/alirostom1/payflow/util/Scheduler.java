package io.github.alirostom1.payflow.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void schedule(Runnable task, LocalDateTime runAt) {
        long delay = Duration.between(LocalDateTime.now(), runAt).toMillis();
        scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
    }
    
}
