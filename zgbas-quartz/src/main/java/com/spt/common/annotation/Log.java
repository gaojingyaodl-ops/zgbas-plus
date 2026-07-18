package com.spt.common.annotation;

import com.spt.common.enums.BusinessType;
import com.spt.common.enums.OperatorType;

import java.lang.annotation.*;

/**
 * 自定义操作日志记录注解（Phase 6 Task 2 verbatim）。
 *
 * <p>Note: monolith does not have the corresponding AOP aspect to process this
 * annotation. It is retained for source-compatibility with the SysJobController
 * / SysJobLogController port; methods annotated with {@code @Log} will behave
 * as no-ops at runtime in the monolith (full audit logging is out of Phase 6
 * scope — sys_job_log captures the job-execution audit trail instead).
 *
 * @author ruoyi
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 模块
     */
    String title() default "";

    /**
     * 功能
     */
    BusinessType businessType() default BusinessType.OTHER;

    /**
     * 操作人类别
     */
    OperatorType operatorType() default OperatorType.MANAGE;

    /**
     * 是否保存请求的参数
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    boolean isSaveResponseData() default true;
}
