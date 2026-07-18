package com.spt.common.utils.poi;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Excel 工具类 — zgbas-quartz 本地化 STUB（Phase 6 Task 2，Rule 3）。
 *
 * <p>The source {@code spt-auth/auth-common/.../poi/ExcelUtil.java} is 1158 lines
 * and pulls 35 transitive imports (Excels / SptAuthConfig / Convert / UtilException
 * / DateUtils / DictUtils / FileTypeUtils / FileUtils / ImageUtils / ReflectUtils).
 * Full port would cascade into ~10 additional localizations, all for an admin
 * convenience endpoint ({@code POST /monitor/job/export} and
 * {@code POST /monitor/jobLog/export}) that is not on the QUARTZ-04 critical
 * path (manual trigger + parameter pass-through).
 *
 * <p>Plan Task 2 action D explicitly allows deferral: "不剥 ExcelUtil / @Excel /
 * @Log（先保 SysJobController.export 编译通过，export 功能可后期剥除）". This stub
 * satisfies the compile contract (same constructor + method signature used by
 * SysJobController/SysJobLogController.export) and throws a descriptive
 * UnsupportedOperationException at runtime. Full Excel export implementation
 * is tracked as a deferred item in SUMMARY.md.
 *
 * @author ruoyi
 */
public class ExcelUtil<T> {

    public ExcelUtil(Class<T> clazz) {
        // Stub: clazz retained for signature parity with the source util; not used.
    }

    /**
     * 导出 Excel — STUB. Always throws.
     *
     * @param response HTTP 响应（unused）
     * @param list     数据列表（unused）
     * @param sheetName 工作表名（unused）
     */
    public void exportExcel(HttpServletResponse response, List<T> list, String sheetName) {
        throw new UnsupportedOperationException(
                "ExcelUtil.exportExcel is a stub in zgbas-quartz (Phase 6 Task 2 Rule 3). "
                        + "Full export impl deferred — see 06-01-SUMMARY.md Known Stubs.");
    }
}
