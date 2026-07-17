package com.spt.bas.server.rocketmq.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/2/27 11:49
 */
@Slf4j
public class ThreadPoolExecutorEngine {


    /**
     * 系统核心数
     */
    private static final int SYSTEM_CORE_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * 最大线程数
     */
    private static final int MAXI_MUM_POOL_SIZE = 2 * SYSTEM_CORE_COUNT + 1;

    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = 2 * SYSTEM_CORE_COUNT;

    private volatile static ThreadPoolExecutor executor;

    /**
     * 获取线程池实例
     *
     * @return 返回线程池
     */
    public static ThreadPoolExecutor getInstance() {
        if (executor == null) {
            synchronized (ThreadPoolExecutorEngine.class) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(CORE_POOL_SIZE,// 核心线程数
                            MAXI_MUM_POOL_SIZE, // 最大线程数
                            Integer.MAX_VALUE, // 闲置线程存活时间
                            TimeUnit.MILLISECONDS,// 时间单位
                            new LinkedBlockingDeque<Runnable>(40000),// 线程队列
                            new ThreadFactoryBuilder().setNameFormat("sync-data-message-%d").build()
                    );
                }
            }
        }
        return executor;
    }

    public void execute(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        executor.execute(runnable);
    }

    // 从线程队列中移除对象
    public void cancel(Runnable runnable) {
        if (executor != null) {
            executor.getQueue().remove(runnable);
        }
    }

    public static void shutdown() {
        executor.shutdown();
        log.info("数据同步线程池已关闭！");
    }
}
