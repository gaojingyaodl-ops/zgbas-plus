-- =============================================================================
-- Phase 6 (QUARTZ-04 / D-P6-10) — External spt-auth sys_menu INSERT
-- =============================================================================
-- **checkpoint:human-blocked** — this file is NOT applied automatically.
-- A human operator must execute it against the *external* spt-auth DB, then
-- refresh the zgbas-plus menu cache (auth-sdk) so "定时任务" shows up in
-- the "系统监控" menu group.
--
-- Why external: per D-P6-10 LOCKED + 06-CONTEXT.md, zgbas-plus fetches menus
-- dynamically from the external spt-auth sys_menu table (auth-sdk HTTP call).
-- The monolith itself has no sys_menu table.
--
-- Scope: 1 parent menu row (C type) + 6 button permission rows (F type).
-- Permission strings align 1:1 with the @RequiresPermissions annotations
-- added to SysJobController / SysJobLogController in Task 1 (W5 compensation,
-- threat_model T-06-01-02).
--
-- Adaptation from RuoYi seed (ry_20210908.sql:175, 1049-1054): component path
-- changed from 'monitor/job/index' -> 'monitor/job/job' because zgbas-plus's
-- Thymeleaf list page is `templates/monitor/job/job.html` (Task 4), not
-- `index.html`. The GET /monitor/job handler in SysJobController returns the
-- view name "monitor/job/job" which SpringBoot ViewResolver resolves to
-- `classpath:/templates/monitor/job/job.html`.
--
-- Idempotency: use INSERT IGNORE so re-running won't fail on pre-existing
-- menu_id 110 (RuoYi seed row) — operator can also run as-is if the row was
-- already inserted by the RuoYi base seed. The WHERE clause on the optional
-- DELETE block lets you cleanly back out if needed.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- (A) Parent menu row — '定时任务' under '系统监控' (parent_id=2 by RuoYi seed)
-- -----------------------------------------------------------------------------
-- menu_id=110 is the RuoYi seed id; if it already exists (spt-auth was seeded
-- from ry_20210908.sql), INSERT IGNORE will skip and only the component path
-- needs manual review. If spt-auth skipped the seed, this row creates it.
-- -----------------------------------------------------------------------------
INSERT IGNORE INTO sys_menu (
    menu_id, menu_name, parent_id, order_num, path, component, query,
    is_frame, is_cache, menu_type, visible, status, perms, icon,
    create_by, create_time, update_by, update_time, remark
) VALUES (
    110, '定时任务', 2, 2, 'job', 'monitor/job/job', '',
    1, 0, 'C', '0', '0', 'monitor:job:list', 'job',
    'admin', sysdate(), '', null, '定时任务菜单（zgbas-plus Phase 6 D-P6-10）'
);

-- If menu_id=110 already exists with component='monitor/job/index', optionally
-- update the component path to match zgbas-plus's job.html template:
-- UPDATE sys_menu SET component = 'monitor/job/job', remark = 'zgbas-plus Phase 6 D-P6-10' WHERE menu_id = 110;


-- -----------------------------------------------------------------------------
-- (B) Button-type permission rows (F type) under parent menu_id=110
-- -----------------------------------------------------------------------------
-- RuoYi standard button ids: 1049-1054. INSERT IGNORE lets this file run
-- idempotently whether or not the base seed was applied.
-- -----------------------------------------------------------------------------

-- 任务查询
INSERT IGNORE INTO sys_menu VALUES (
    1049, '任务查询', 110, 1, '#', '', '', 1, 0, 'F', '0', '0',
    'monitor:job:query', '#', 'admin', sysdate(), '', null, ''
);

-- 任务新增
INSERT IGNORE INTO sys_menu VALUES (
    1050, '任务新增', 110, 2, '#', '', '', 1, 0, 'F', '0', '0',
    'monitor:job:add', '#', 'admin', sysdate(), '', null, ''
);

-- 任务修改
INSERT IGNORE INTO sys_menu VALUES (
    1051, '任务修改', 110, 3, '#', '', '', 1, 0, 'F', '0', '0',
    'monitor:job:edit', '#', 'admin', sysdate(), '', null, ''
);

-- 任务删除
INSERT IGNORE INTO sys_menu VALUES (
    1052, '任务删除', 110, 4, '#', '', '', 1, 0, 'F', '0', '0',
    'monitor:job:remove', '#', 'admin', sysdate(), '', null, ''
);

-- 状态修改（启停 + 立即执行）
INSERT IGNORE INTO sys_menu VALUES (
    1053, '状态修改', 110, 5, '#', '', '', 1, 0, 'F', '0', '0',
    'monitor:job:changeStatus', '#', 'admin', sysdate(), '', null, ''
);

-- 立即执行（D-P6-10 手动触发 UX — maps to @RequiresPermissions("monitor:job:run"))
INSERT IGNORE INTO sys_menu VALUES (
    1055, '立即执行', 110, 6, '#', '', '', 1, 0, 'F', '0', '0',
    'monitor:job:run', '#', 'admin', sysdate(), '', null,
    'zgbas-plus Phase 6 D-P6-10 — manual trigger button'
);


-- =============================================================================
-- Operator runbook
-- =============================================================================
-- 1. Apply this SQL to the external spt-auth MySQL DB (e.g. mysql -h <spt-auth-db-host>
--    -u <user> -p <spt-auth-db-name> < 06-01-MENU-INSERT.sql).
-- 2. Restart or refresh zgbas-plus so auth-sdk pulls the updated sys_menu cache
--    (mechanism: depends on auth-sdk cache strategy — see Phase 3 D-P3-03
--    cache init).
-- 3. Login to zgbas-plus as admin -> expand "系统监控" in the left sidebar ->
--    "定时任务" entry should appear. Click it -> renders the Task 4 job.html
--    list page (GET /monitor/job).
--
-- Fallback (if you can't apply SQL right now): open
-- http://<zgbas-plus-host>/monitor/job directly in a browser with an
-- authenticated Shiro session. The page will render via the Task 1
-- SysJobController @GetMapping view handler. (Menu INSERT still needs to be
-- applied eventually for full D-P6-10 compliance.)
--
-- Resume signal for the GSD orchestrator: type one of
--   - "menu-applied"           (SQL applied + menu visible in sidebar)
--   - "direct-link-verified"   (fallback URL renders the list page)
--   - "menu-deferred-to-p7"    (defer menu apply to Phase 7 UAT)
-- =============================================================================
