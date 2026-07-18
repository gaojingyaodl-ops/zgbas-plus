package com.spt.quartz.task;

import com.spt.bas.server.service.IWeChatWorkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.WeChatWorkTask}.
 * Bean name {@code "weChatWorkTask"} aligns with {@code sys_job.invoke_target}
 * short name {@code weChatWorkTask.pushWeChatWorkLeaderboard}.
 */
@Component("weChatWorkTask")
@Slf4j
public class WeChatWorkTask {

    @Autowired
    private IWeChatWorkService weChatWorkService;

    /**
     * 企业微信机器人推送消息任务
     */
    public void pushWeChatWorkLeaderboard(){
        log.info("企业微信机器人推送消息任务开始======>");
         weChatWorkService.pushWeChatWorkLeaderboard();

        weChatWorkService.pushWeChantWorkLeaderboardForCustomerDevelop();
        log.info("企业微信机器人推送消息任务结束<======");
    }
}
