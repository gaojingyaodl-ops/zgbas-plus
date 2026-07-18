package com.spt.quartz.controller;

import com.spt.common.annotation.Log;
import com.spt.common.constant.Constants;
import com.spt.common.core.controller.BaseController;
import com.spt.common.core.domain.AjaxResult;
import com.spt.common.core.page.TableDataInfo;
import com.spt.common.enums.BusinessType;
import com.spt.common.exception.job.TaskException;
import com.spt.common.utils.StringUtils;
import com.spt.common.utils.poi.ExcelUtil;
import com.spt.quartz.domain.SysJob;
import com.spt.quartz.service.ISysJobService;
import com.spt.quartz.util.CronUtils;
import com.spt.quartz.util.ScheduleUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 调度任务信息操作处理
 *
 * <p>Phase 6 (QUARTZ-01 + QUARTZ-04 + D-P6-10) — ported from
 * {@code spt-auth/auth-quartz/src/main/java/com/spt/quartz/controller/SysJobController.java}
 * with three adaptations:
 * <ol>
 *   <li>Pitfall 4 / W5 compensation — every {@code @PreAuthorize("@ss.hasPermi(...)")}
 *       annotation is removed (spring-security is not on the monolith classpath;
 *       auth is handled by the Phase 3 Shiro chain). To preserve method-level
 *       permission granularity (ASVS L1 V4.1.1), Shiro
 *       {@link RequiresPermissions} annotations are added on every write method
 *       with the same {@code monitor:job:*} permission strings
 *       (threat_model T-06-01-02 disposition accept -> mitigated).</li>
 *   <li>D-P6-10 — view-returning {@code @GetMapping} handlers ({@link #view()},
 *       {@link #add()}, {@link #edit(Long, ModelMap)}) are added so the
 *       Thymeleaf pages at {@code templates/monitor/job/} are reachable via
 *       direct URL /monitor/job (and via the external spt-auth sys_menu entry,
 *       see Task 5 checkpoint:human-blocked).</li>
 *   <li>The class is switched from {@code @RestController} to {@code @Controller}
 *       so Thymeleaf view names resolve; AJAX endpoints opt back into JSON via
 *       {@code @ResponseBody}.</li>
 * </ol>
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/monitor/job")
public class SysJobController extends BaseController {

    private String prefix = "monitor/job";

    @Autowired
    private ISysJobService jobService;

    /**
     * 列表页入口（D-P6-10 可视化运维台）。
     */
    @GetMapping()
    public String view() {
        return prefix + "/job";
    }

    /**
     * 新增任务表单页。
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 修改任务表单页（预填 sys_job 记录）。
     */
    @RequiresPermissions("monitor:job:edit")
    @GetMapping("/edit/{jobId}")
    public String edit(@PathVariable("jobId") Long jobId, ModelMap mmap) {
        mmap.put("job", jobService.selectJobById(jobId));
        return prefix + "/edit";
    }

    /**
     * 查询定时任务列表
     */
    @RequiresPermissions("monitor:job:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysJob sysJob) {
        startPage();
        List<SysJob> list = jobService.selectJobList(sysJob);
        return getDataTable(list);
    }

    /**
     * 导出定时任务列表
     */
    @RequiresPermissions("monitor:job:export")
    @Log(title = "定时任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public void export(HttpServletResponse response, SysJob sysJob) {
        List<SysJob> list = jobService.selectJobList(sysJob);
        ExcelUtil<SysJob> util = new ExcelUtil<SysJob>(SysJob.class);
        util.exportExcel(response, list, "定时任务");
    }

    /**
     * 获取定时任务详细信息
     */
    @RequiresPermissions("monitor:job:query")
    @GetMapping(value = "/{jobId}")
    @ResponseBody
    public AjaxResult getInfo(@PathVariable("jobId") Long jobId) {
        return AjaxResult.success(jobService.selectJobById(jobId));
    }

    /**
     * 新增定时任务
     */
    @RequiresPermissions("monitor:job:add")
    @Log(title = "定时任务", businessType = BusinessType.INSERT)
    @PostMapping
    @ResponseBody
    public AjaxResult add(@RequestBody SysJob job) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(job.getCronExpression())) {
            return error("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        } else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI)) {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS})) {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.HTTP, Constants.HTTPS})) {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR)) {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        } else if (!ScheduleUtils.whiteList(job.getInvokeTarget())) {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
        }
        job.setCreateBy(getUsername());
        return toAjax(jobService.insertJob(job));
    }

    /**
     * 修改定时任务
     */
    @RequiresPermissions("monitor:job:edit")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping
    @ResponseBody
    public AjaxResult edit(@RequestBody SysJob job) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(job.getCronExpression())) {
            return error("修改任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        } else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI)) {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS})) {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[]{Constants.HTTP, Constants.HTTPS})) {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR)) {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        } else if (!ScheduleUtils.whiteList(job.getInvokeTarget())) {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
        }
        job.setUpdateBy(getUsername());
        return toAjax(jobService.updateJob(job));
    }

    /**
     * 定时任务状态修改
     */
    @RequiresPermissions("monitor:job:changeStatus")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    @ResponseBody
    public AjaxResult changeStatus(@RequestBody SysJob job) throws SchedulerException {
        SysJob newJob = jobService.selectJobById(job.getJobId());
        newJob.setStatus(job.getStatus());
        return toAjax(jobService.changeStatus(newJob));
    }

    /**
     * 定时任务立即执行一次（D-P6-10 手动触发 UX）。
     */
    @RequiresPermissions("monitor:job:run")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/run")
    @ResponseBody
    public AjaxResult run(@RequestBody SysJob job) throws SchedulerException {
        jobService.run(job);
        return AjaxResult.success();
    }

    /**
     * 删除定时任务
     */
    @RequiresPermissions("monitor:job:remove")
    @Log(title = "定时任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{jobIds}")
    @ResponseBody
    public AjaxResult remove(@PathVariable Long[] jobIds) throws SchedulerException, TaskException {
        jobService.deleteJobByIds(jobIds);
        return AjaxResult.success();
    }
}
