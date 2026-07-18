package com.spt.quartz.task;

import com.spt.bas.client.vo.WeChatWorkVo;
import com.spt.bas.report.server.service.IRptWeChatWorkService;
import com.spt.tools.core.cache.LocalCacheManager;
import com.spt.tools.core.cmd.ICommand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * Report command executor.
 *
 * <p>Phase 6 (06-04) — ported from {@code com.spt.bas.report.server.command.ReportCommandExecutor}.
 * Bean name {@code "reportCommandExecutor"} aligns with {@code sys_job.invoke_target} short name
 * {@code reportCommandExecutor.executeCommand('${cmd}')}. Per D-P6-07/D-P6-09, command executors
 * land in {@code com.spt.quartz.task}; {@code IRptWeChatWorkService} stays in its Phase 5 package
 * {@code com.spt.bas.report.server.service} (D-P6-09: only handler self-package changes).
 *
 * <p>Pitfall 6: the source {@code @XxlJob(value = "executeCommand")} was shared across 3 executors;
 * sys_job rows now distinguish them by bean name. The {@code XxlJobHelper.getJobParam()} fallback
 * is deleted because {@code commandline} is now passed directly by {@code JobInvokeUtil.invokeMethod}
 * reflection.
 *
 * <p>Sub-commands (for 06-05 D-P6-02 sys_job translation):
 * <ul>
 *   <li>{@code cache} — LocalCacheManager.refreshAll()</li>
 *   <li>{@code pushWeChatWorkLeaderboard} — IRptWeChatWorkService.pushWeChantWorkLeaderboardForCustomerDevelop(vo)</li>
 * </ul>
 *
 * @author wlddh
 */
@Component("reportCommandExecutor")
public class ReportCommandExecutor implements ICommand {
	@Resource
	private IRptWeChatWorkService weChatWorkService;

	@Override
	public boolean executeCommand(String commandline) throws Exception {
		if(StringUtils.isNotBlank(commandline)){
			if (commandline.equalsIgnoreCase("cache")){
				// 刷新缓存
				LocalCacheManager.refreshAll();
				return true;
			} else if (commandline.equalsIgnoreCase("pushWeChatWorkLeaderboard")){
				WeChatWorkVo vo = new WeChatWorkVo();
				ArrayList<Long> deptIdList	 = new ArrayList<>();
				deptIdList.add(67957L);
				vo.setDeptIdList(deptIdList);
				weChatWorkService.pushWeChantWorkLeaderboardForCustomerDevelop(vo);
				return true;
			}
		}
		return false;
	}

}
