package com.spt.quartz.task;

import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.ws.IndexWebSocketServer;
import com.spt.tools.core.cache.LocalCacheManager;
import com.spt.tools.core.cmd.ICommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Web command executor.
 *
 * <p>Phase 6 (06-04) — ported from {@code com.spt.bas.web.config.BasWebCommand}. Bean name
 * {@code "basWebCommand"} aligns with {@code sys_job.invoke_target} short name
 * {@code basWebCommand.executeCommand('${cmd}')}. Per D-P6-07/D-P6-09, command executors land in
 * {@code com.spt.quartz.task}; {@link ShiroUtil} and {@link IndexWebSocketServer} stay in their
 * Phase 2/3 packages (D-P6-09: only handler self-package changes).
 *
 * <p>Pitfall 6: the source {@code @XxlJob(value = "executeCommand")} was shared across 3 executors;
 * sys_job rows now distinguish them by bean name. The {@code XxlJobHelper.getJobParam()} fallback
 * is deleted because {@code commandline} is now passed directly by {@code JobInvokeUtil.invokeMethod}
 * reflection.
 *
 * <p>Sub-commands (for 06-05 D-P6-02 sys_job translation):
 * <ul>
 *   <li>{@code clean} — ShiroUtil.clean()</li>
 *   <li>{@code cache} — LocalCacheManager.refreshAll()</li>
 *   <li>{@code fundSocket} — IndexWebSocketServer.broadcast(fundCompanyMessage)</li>
 * </ul>
 *
 * @author wlddh
 */
@Component("basWebCommand")
@Slf4j
public class BasWebCommand implements ICommand {
    @Resource
    private IndexWebSocketServer indexWebSocketServer;

    @Override
    public boolean executeCommand(String commandline) throws Exception {
        log.info("executeCommand {}", commandline);
        if (StringUtils.isNotBlank(commandline)) {
            if (commandline.trim().equalsIgnoreCase("clean")) // 刷新shiro
            {
                ShiroUtil.clean();
                return true;
            } else if (commandline.equalsIgnoreCase("cache")) {
                // 刷新缓存
                LocalCacheManager.refreshAll();
                log.info("executeCommand {} done", commandline);
                return true;
            } else if (commandline.equalsIgnoreCase("fundSocket")){
				String message = "{\"fundCompanyId\":\"21\",\"fundCompanyName\":\"苏州高新供应链管理有限公司\",\"fundAmount\":"+ RandomUtils.nextLong(500000L, 1000000000L) +"}";
				indexWebSocketServer.broadcast(message);
			}
        }

        return false;
    }

}
