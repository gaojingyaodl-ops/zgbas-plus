package com.spt.common.constant;

/**
 * 返回状态码 — zgbas-quartz 本地化子集（Phase 6 Task 2）。
 * 仅保留 quartz 子系统 AjaxResult / TableDataInfo 实际用到的 SUCCESS/ERROR。
 *
 * @author ruoyi
 */
public class HttpStatus {
    /** ry-ui.js web_status.SUCCESS = 0，必须与前端约定一致。 */
    public static final int SUCCESS = 0;
    public static final int ERROR = 500;
}
