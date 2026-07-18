package com.spt.common.core.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.spt.common.constant.HttpStatus;
import com.spt.common.core.domain.AjaxResult;
import com.spt.common.core.page.TableDataInfo;
import com.spt.common.utils.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;

/**
 * web层通用数据处理 — zgbas-quartz 本地化精简版（Phase 6 Task 2）。
 *
 * <p>Source {@code spt-auth/auth-common/.../core/controller/BaseController.java}
 * pulls in mybatis-plus Page / pagehelper PageInfo / LoginUser / PageDomain /
 * TableSupport / DateUtils / PageUtils / SecurityUtils (RuoYi) / SqlUtil — about
 * 13 transitive classes, most of which (PageDomain/TableSupport/PageUtils/SqlUtil)
 * cascade further. The quartz subsystem only uses 5 methods:
 * {@code startPage}, {@code getDataTable(List)}, {@code toAjax(int)},
 * {@code error(String)}, {@code getUsername}.
 *
 * <p>This minimal port retains exactly that surface, using the monolith's own
 * stack ({@code com.github.pagehelper.PageHelper} 5.3.0 is transitively on the
 * classpath; Apache Shiro {@link SecurityUtils} is the monolith's auth framework
 * per Phase 3). See research §Common Pitfalls 3 ("BaseController / AjaxResult /
 * TableDataInfo → 必须本地化").
 *
 * @author ruoyi
 */
public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                // Minimal: store raw text as Date.valueOf is brittle; SysJobController
                // endpoints don't bind Date query params so this path is not on the
                // critical QUARTZ-04 path. Null out on empty.
                if (StringUtils.isEmpty(text)) {
                    setValue(null);
                    return;
                }
                // Best-effort: java.sql.Date.valueOf expects yyyy-mm-dd. For
                // SysJob update-time fields this is unused at the controller layer.
                try {
                    setValue(new Date(Long.parseLong(text)));
                } catch (NumberFormatException ignore) {
                    setValue(null);
                }
            }
        });
    }

    /**
     * 设置请求分页数据 — delegates to {@link PageHelper#startPage(int, int)}.
     * Pulls pageNum / pageSize from request params (RuoYi PageHelper convention).
     */
    protected void startPage() {
        Integer pageNum = null;
        Integer pageSize = null;
        try {
            javax.servlet.http.HttpServletRequest request =
                    ((org.springframework.web.context.request.ServletRequestAttributes)
                            org.springframework.web.context.request.RequestContextHolder
                                    .currentRequestAttributes())
                            .getRequest();
            String pageNumStr = request.getParameter("pageNum");
            String pageSizeStr = request.getParameter("pageSize");
            if (StringUtils.isNotEmpty(pageNumStr)) {
                pageNum = Integer.valueOf(pageNumStr);
            }
            if (StringUtils.isNotEmpty(pageSizeStr)) {
                pageSize = Integer.valueOf(pageSizeStr);
            }
        } catch (IllegalStateException ignored) {
            // No request bound (e.g. unit-test) — fall through with nulls.
        }
        if (pageNum != null && pageSize != null) {
            PageHelper.startPage(pageNum, pageSize);
        }
    }

    /**
     * 响应请求分页数据
     */
    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(new PageInfo<>(list).getTotal());
        return rspData;
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error() {
        return AjaxResult.error();
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error(String message) {
        return AjaxResult.error(message);
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 获取登录用户名 — 走 Apache Shiro Subject principal。
     *
     * <p>Falls back to literal {@code "admin"} when no Shiro session is bound
     * (e.g. dry-run via scheduler on a fresh boot before login). Source uses
     * RuoYi {@code SecurityUtils.getLoginUser()} which is absent in the monolith;
     * Shiro is the equivalent auth chain (Phase 3).
     */
    public String getUsername() {
        try {
            Object principal = SecurityUtils.getSubject().getPrincipal();
            if (principal != null) {
                return principal.toString();
            }
        } catch (Exception e) {
            logger.debug("getUsername: no Shiro subject bound, falling back to 'admin'");
        }
        return "admin";
    }
}
