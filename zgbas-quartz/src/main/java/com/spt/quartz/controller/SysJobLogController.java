package com.spt.quartz.controller;

import com.spt.common.annotation.Log;
import com.spt.common.core.controller.BaseController;
import com.spt.common.core.domain.AjaxResult;
import com.spt.common.core.page.TableDataInfo;
import com.spt.common.enums.BusinessType;
import com.spt.common.utils.poi.ExcelUtil;
import cn.hutool.core.convert.Convert;
import com.spt.quartz.domain.SysJobLog;
import com.spt.quartz.service.ISysJobLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 调度日志操作处理
 */
@Controller
@RequestMapping("/monitor/jobLog")
public class SysJobLogController extends BaseController {

    private String prefix = "monitor/jobLog";

    @Autowired
    private ISysJobLogService jobLogService;

    @GetMapping()
    public String view() {
        return prefix + "/jobLog";
    }

    /**
     * 查询定时任务调度日志列表
     */
    @RequiresPermissions("monitor:job:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysJobLog sysJobLog) {
        startPage();
        List<SysJobLog> list = jobLogService.selectJobLogList(sysJobLog);
        return getDataTable(list);
    }

    /**
     * 导出定时任务调度日志列表
     */
    @RequiresPermissions("monitor:job:export")
    @Log(title = "任务调度日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public void export(HttpServletResponse response, SysJobLog sysJobLog) {
        List<SysJobLog> list = jobLogService.selectJobLogList(sysJobLog);
        ExcelUtil<SysJobLog> util = new ExcelUtil<SysJobLog>(SysJobLog.class);
        util.exportExcel(response, list, "调度日志");
    }

    /**
     * 根据调度编号获取详细信息
     */
    @RequiresPermissions("monitor:job:query")
    @GetMapping(value = "/{jobLogId}")
    @ResponseBody
    public AjaxResult getInfo(@PathVariable Long jobLogId) {
        return AjaxResult.success(jobLogService.selectJobLogById(jobLogId));
    }

    /**
     * 删除定时任务调度日志（ids 为逗号分隔）
     */
    @RequiresPermissions("monitor:job:remove")
    @Log(title = "定时任务调度日志", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(jobLogService.deleteJobLogByIds(Convert.toLongArray(ids)));
    }

    /**
     * 清空定时任务调度日志
     */
    @RequiresPermissions("monitor:job:remove")
    @Log(title = "调度日志", businessType = BusinessType.CLEAN)
    @PostMapping("/clean")
    @ResponseBody
    public AjaxResult clean() {
        jobLogService.cleanJobLog();
        return AjaxResult.success();
    }
}
