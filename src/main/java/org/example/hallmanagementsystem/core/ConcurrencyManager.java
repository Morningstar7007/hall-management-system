package org.example.hallmanagementsystem.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages concurrency and background tasks using a Thread Pool.
 * Demonstrates Advanced Concurrency and Thread Pools.
 */
public class ConcurrencyManager {
    
    // Create a Fixed Thread Pool with 4 worker threads to handle background database queries
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(4);

    /**
     * Submits a background task to the thread pool queue.
     * @param task the Runnable task to execute
     */
    public static void execute(Runnable task) {
        threadPool.submit(task);
    }

    /**
     * Gracefully shuts down the thread pool. Should be called when the application closes.
     */
    public static void shutdown() {
        if (!threadPool.isShutdown()) {
            threadPool.shutdown();
        }
    }
}

