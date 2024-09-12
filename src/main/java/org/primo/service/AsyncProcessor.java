package org.primo.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncProcessor {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static Future<?> submitTask(Runnable task) {
        return executorService.submit(task);
    }

    public static void shutdown() {
        executorService.shutdown();
    }
}

