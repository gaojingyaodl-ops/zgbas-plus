package com.spt.common.constant;

/**
 * 通用常量信息 — zgbas-quartz 本地化子集（Phase 6 Task 2，Pitfall 2/3）。
 *
 * <p>仅保留 quartz 子系统实际引用的常量。源
 * {@code spt-auth/auth-common/src/main/java/com/spt/common/constant/Constants.java}
 * 含 ~290 行（含 JWT/SYS_CONFIG_KEY/BIS_COMPANY_* 等大量非 quartz 域常量 +
 * {@code io.jsonwebtoken.Claims} import），全量照搬会引入 JWT 依赖与跨域语义噪音。
 *
 * <p><b>Pitfall 3（D-P6-01 LOCKED）</b>：{@link #JOB_WHITELIST_STR} 由源
 * {@code {"com.ruoyi"}}} 改为 {@code {"com.spt"}}，让 {@code com.spt.quartz.task.*}
 * 的 bean invokeTarget 通过 {@code ScheduleUtils.whiteList} 校验。
 *
 * @author ruoyi
 */
public class Constants {
    /**
     * 通用成功标识（sys_job_log.status = 0）
     */
    public static final String SUCCESS = "0";

    /**
     * 通用失败标识（sys_job_log.status = 1）
     */
    public static final String FAIL = "1";

    /**
     * http请求（invokeTarget 黑名单）
     */
    public static final String HTTP = "http://";

    /**
     * https请求（invokeTarget 黑名单）
     */
    public static final String HTTPS = "https://";

    /**
     * RMI 远程方法调用（invokeTarget 黑名单）
     */
    public static final String LOOKUP_RMI = "rmi:";

    /**
     * LDAP 远程方法调用（invokeTarget 黑名单）
     */
    public static final String LOOKUP_LDAP = "ldap:";

    /**
     * LDAPS 远程方法调用（invokeTarget 黑名单）
     */
    public static final String LOOKUP_LDAPS = "ldaps:";

    /**
     * 定时任务白名单配置（仅允许访问的包名）。
     * <p>Pitfall 3：源为 {@code {"com.ruoyi"}}，本期改为 {@code {"com.spt"}}
     * 覆盖所有 {@code com.spt.quartz.task.*} handler bean（research §Open
     * Questions Q5 RESOLVED）。
     */
    public static final String[] JOB_WHITELIST_STR = {"com.spt"};

    /**
     * 定时任务违规的字符（invokeTarget 黑名单 — defense-in-depth on top of whitelist）
     */
    public static final String[] JOB_ERROR_STR = {"java.net.URL", "javax.naming.InitialContext", "org.yaml.snakeyaml",
            "org.springframework", "org.apache", "com.ruoyi.common.utils.file"};
}
