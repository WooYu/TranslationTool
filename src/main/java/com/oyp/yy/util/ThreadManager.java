package com.oyp.yy.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>date: Created by A18086 on 2019/10/18.</p>
 * <p>desc: </p>
 */
public class ThreadManager {

    private static ExecutorService executorService;
    private static ExecutorService singThread;

    private ThreadManager() {
        throw new RuntimeException("don`t do this");
    }


    public static ExecutorService getCachedThreadPool() {
        if (executorService == null) {
            synchronized (ThreadManager.class) {
                if (executorService == null) {
                    executorService = Executors.newCachedThreadPool();
                }
            }
        }
        return executorService;
    }

    public static ExecutorService getSingleThread() {
        if (singThread == null) {
            synchronized (ThreadManager.class) {
                if (singThread == null) {
                    singThread = Executors.newSingleThreadExecutor();
                }
            }
        }
        return singThread;
    }

}
