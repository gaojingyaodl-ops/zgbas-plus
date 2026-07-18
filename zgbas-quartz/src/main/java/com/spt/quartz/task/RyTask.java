package com.spt.quartz.task;

import com.spt.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 定时任务调度测试（RuoYi RyTask sample）。
 *
 * <p>Phase 6 (QUARTZ-01) — ported from
 * {@code spt-auth/auth-quartz/src/main/java/com/spt/quartz/task/RyTask.java}.
 * The {@code flushUserRedisCache()} method + {@code RedisUserCache} import from
 * the source are dropped (Rule 3): the monolith has no
 * {@code com.spt.framework.cache.RedisUserCache} type and the three {@code sys_job}
 * demo rows only reference {@code ryNoParams / ryParams / ryMultipleParams}.
 *
 * @author ruoyi
 */
@Component("ryTask")
public class RyTask {
    private static final Logger log = LoggerFactory.getLogger(RyTask.class);

    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i) {
        System.out.println(StringUtils.format("执行多参方法： 字符串类型{}，布尔类型{}，长整型{}，浮点型{}，整形{}", s, b, l, d, i));
    }

    public void ryParams(String params) {
        System.out.println("执行有参方法：" + params);
    }

    public void ryNoParams() {
        log.info("执行无参方法（RyTask.ryNoParams demo）");
    }
}
