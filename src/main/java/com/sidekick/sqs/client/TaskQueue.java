package com.sidekick.sqs.client;

import java.util.concurrent.*;

class TaskQueue {

    private ExecutorService pool;
    private int poolSize;

    public TaskQueue() {
        super();
        this.setPoolSize(10);
    }

    public TaskQueue(int poolSize) {
        super();
        this.setPoolSize(poolSize);
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public void submit(Callable<Object> task) {
        pool.submit(task);
    }

    public void submit(Runnable task) {
        pool.submit(task);
    }

    public void init() {
        if (this.getPoolSize() <= 0) {
            throw new IllegalArgumentException("Pool size must be greater than 0");
        }
        pool = Executors.newFixedThreadPool(this.getPoolSize());
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void shutdown() {
        try {
            if (!pool.isShutdown()) {
                pool.shutdown();
                pool.awaitTermination(10, TimeUnit.SECONDS);
            }
        } catch (InterruptedException ex) {
            // Swallow this exception
        }
    }
}
