-- Phase 6 (QUARTZ-04 / D-P6-01 / D-P6-02): sys_job_data.sql
-- Translation of xxl_job_info (zg_prod) → sys_job INSERT rows.
--
-- Source export: .planning/phases/06-quartz-migration/06-05-RAW-EXPORT.sql (88 rows)
-- Translation rules: 06-RESEARCH.md §Pattern 2 (invoke_target) + §Pattern 3 (xxl-job→quartz) +
--                    §Code Examples §1 (handler) + §Code Examples §2 (command executor)
-- Decisions applied: D-P6-01 (export), D-P6-02 (translate + verify), D-P6-03 (status 3-tier),
--                    D-P6-12 (concurrent from SERIAL_EXECUTION block strategy)
--
-- TRANSLATION SUMMARY (see 06-05-TRANSLATION-WORKSHEET.md for full row-by-row mapping)
--   Total xxl_job_info rows in export:             88
--   Matched (direct @XxlJob value match):          49   → INSERTed below
--   executeCommand unambiguously matched:           1   → INSERTed below
--   -----------------------------------------------------
--   Total INSERT rows in this file:                50   (job_id 102-190)
--
--   Unmatched (excluded — see worksheet §3):       38
--     - 28 non-migrated handlers (no matching @XxlJob in 06-02/03/04 manifests)
--     -  7 ambiguous executeCommand entries (no/inert param, stopped in prod, never triggered)
--     -  1 BasCommandExecutor stale sub-command ('getReceive' — not in current source branches)
--     -  1 multi-token executeCommand ('clean cache' — no branch match)
--     -  1 source-deprecated: id=12 overdueTask (OrverdurTask @XxlJob line commented out — 06-02)
--     -  1 test stub: id=69 testXxlJob (TestJob not on QUARTZ-03 path — 06-02)
--
--   ⚠ Plan's verify expected ≥60 INSERT rows (assumed ~55 executeCommand cron entries in
--     production). Reality: production operators created only ~10 executeCommand admin entries
--     (most status=0, trigger_last/next_time=0 = never triggered). Of those, only 1 has an
--     unambiguous sub-command matching a current executor branch (id=27 'clean' → basWebCommand).
--     See SUMMARY "Deviations from Plan" for the full breakdown. User decision needed in
--     Task 3 checkpoint: accept 50-row reality OR authorize expanding ~55 source-code
--     sub-commands as additional sys_job rows (sacrificing fidelity to source admin DB).
--
-- FIELD MAPPING (xxl_job_info → sys_job)
--   job_desc                → job_name        (Chinese description preserved verbatim)
--   job_group (synthetic)   → job_group       ('DEFAULT' for direct @XxlJob handlers;
--                                              'COMMAND' for executeCommand rows)
--   executor_handler        → invoke_target   (Pattern 2: beanName.methodName([args]))
--     + executor_param      →                 (args baked into invoke_target per type rules)
--   schedule_type=CRON +
--     schedule_conf         → cron_expression (as-is, trimmed; quartz 6-field format compatible)
--   schedule_type=NONE      → cron_expression (placeholder '0 0 0 1 1 ? 2099' — never fires)
--   executor_block_strategy → concurrent      (SERIAL_EXECUTION='1' DisallowConcurrent;
--                                              DISCARD_LATER='0'. All 88 rows = SERIAL_EXECUTION)
--   trigger_status          → status          (1=running → '0' NORMAL; 0=stopped → '1' PAUSED)
--   (fixed)                 → misfire_policy  '3' (default — do nothing on misfire)
--   (fixed)                 → create_by       'admin'
--   (fixed)                 → create_time     sysdate()
--   (fixed)                 → update_by       ''
--   (fixed)                 → update_time     null
--   (fixed)                 → remark          '迁自 xxl-job <executor_handler>'
--
-- IGNORED xxl_job_info FIELDS (per D-P6-12 — single-node monolith; RuoYi has no native equivalents)
--   - executor_route_strategy (FIRST/ROUND/etc. — multi-node routing, N/A for single node)
--   - executor_timeout        (RuoYi has no per-job timeout)
--   - executor_fail_retry_count (RuoYi has no built-in retry — D-P6-12 permanent out-of-scope)
--   - misfire_strategy        (RuoYi uses sys_job.misfire_policy field instead)
--   - alarm_email             (RuoYi has no alerting — D-P6-12 out-of-scope)
--   - glue_*                  (all rows are glue_type=BEAN; no GLUE jobs to migrate)
--   - child_jobid             (sub-task chaining — not in production export)
--   - trigger_last/next_time  (transient scheduler state, not configuration)
--
-- job_id RANGE / TRACEABILITY
--   06-01 sys_job.sql already uses job_id 1-3 for RyTask demos; auto_increment=100.
--   This file uses job_id = xxl_job_info.id + 100 (range 102-190) so that:
--     (a) no collision with RyTask demos or future SysJobController.add auto-IDs (≥191);
--     (b) xxl_job_info.id = job_id - 100 (traceability preserved).
--   sys_job PK is (job_id, job_name, job_group); all rows satisfy uniqueness via job_id.
--
-- APPLY INSTRUCTIONS (operator — apply is OUT of this plan's scope; 06-06 owns startup validation)
--   Pre-req: 06-01 sys_job.sql + quartz.sql already applied to sptbasdb_pd
--            (sys_job / sys_job_log / QRTZ_* tables exist; RyTask demos at job_id 1-3).
--   Apply:   mysql -u <user> -p sptbasdb_pd < sys_job_data.sql
--   Verify:  SELECT COUNT(*) FROM sys_job WHERE job_id BETWEEN 100 AND 200;  -- expect 50
--   Restart: SysJobServiceImpl.@PostConstruct init() loads all sys_job rows
--            (D-P6-06 fail-fast — any invalid cron or missing bean.method aborts startup).

-- =============================================================================
-- MATCHED @XxlJob HANDLERS (06-02 basServer/task + 06-03 rocketmq/task Synchronized*)
-- =============================================================================

-- xxl_job_info.id=2 (running, CRON 0 5 9 ? * *)
INSERT INTO sys_job VALUES (102, '自动发起付款',                                'DEFAULT', 'applyPayTask.autoStartPayProcess',                                    '0 5 9 ? * *',    '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job autoStartPayProcess');

-- xxl_job_info.id=3 (running, CRON 0 40 23 * * ?)
INSERT INTO sys_job VALUES (103, '更新私海客户没有成交单则划入公海',            'DEFAULT', 'bsCompanyTask.updateCompanyGrey',                                    '0 40 23 * * ?',  '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job updateCompanyGrey');

-- xxl_job_info.id=4 (running, CRON 0 30 23 * * ?)
INSERT INTO sys_job VALUES (104, '开户人id 刷新历史数据',                       'DEFAULT', 'bsCompanyTask.refreshOwnerOfAccountId',                              '0 30 23 * * ?',  '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job refreshOwnerOfAccountId');

-- xxl_job_info.id=6 (running, CRON 0 30 0 * * ?)
INSERT INTO sys_job VALUES (106, '凌晨零点30分更新结算单',                      'DEFAULT', 'budgetSettlementTask.updateBudgetSettlement',                        '0 30 0 * * ?',   '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job updateBudgetSettlement');

-- xxl_job_info.id=7 (running, CRON 0 0 0 * * ?)
INSERT INTO sys_job VALUES (107, '更新vip剩余时长',                             'DEFAULT', 'budgetSettlementTask.updateVipRemainingTime',                        '0 0 0 * * ?',    '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job updateVipRemainingTime');

-- xxl_job_info.id=8 (stopped, CRON 0 0 9 * * ?)
INSERT INTO sys_job VALUES (108, '白条到期日9点自动发起收款审批',               'DEFAULT', 'budgetSettlementTask.applyReceive',                                   '0 0 9 * * ?',    '3', '1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job applyReceive (源 trigger_status=0)');

-- xxl_job_info.id=9 (stopped, CRON 0 0 3 ? * *)
INSERT INTO sys_job VALUES (109, '【疑似无效】每日凌晨3点更新风控待办事项',     'DEFAULT', 'ctrContractScheduleTask.updateRiskScheduleTask',                     '0 0 3 ? * *',    '3', '1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job updateRiskScheduleTask (源 trigger_status=0)');

-- xxl_job_info.id=10 (stopped, CRON '0 0 23 * * ? ' — trailing space trimmed)
INSERT INTO sys_job VALUES (110, '【疑似无效】违约企业重置赊销额度及准入',      'DEFAULT', 'defaultingEnterpriseTask.defaultingEnterpriseTask',                  '0 0 23 * * ?',   '3', '1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job defaultingEnterpriseTask (源 trigger_status=0)');

-- xxl_job_info.id=11 (stopped, CRON 0 0 23 ? * *)
INSERT INTO sys_job VALUES (111, '【疑似无效】当天内部采购的库存若未销售则原路退回', 'DEFAULT', 'internalBuyTask.internalBuyTask',                                 '0 0 23 ? * *',   '3', '1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job internalBuyTask (源 trigger_status=0)');

-- xxl_job_info.id=13 (running, CRON 0 0 2 ? * *)
INSERT INTO sys_job VALUES (113, '更新逾期印章外借状态',                        'DEFAULT', 'sealBorrowTask.updateSealBorrow',                                    '0 0 2 ? * *',    '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job updateSealBorrow');

-- xxl_job_info.id=14 (running, CRON 0 0 3 ? * *)
INSERT INTO sys_job VALUES (114, '更新销售结算表',                              'DEFAULT', 'settlementTask.updateSettlementTask',                                '0 0 3 ? * *',    '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job updateSettlementTask');

-- xxl_job_info.id=21 (running, CRON 0 0/5 * * * ?)
INSERT INTO sys_job VALUES (121, '超保额度到期自动恢复',                        'DEFAULT', 'bsCompanyTask.recoverTotalCreditAmount',                             '0 0/5 * * * ?',  '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job recoverTotalCreditAmount');

-- xxl_job_info.id=32 (running, CRON 0 0 7 * * ?)
INSERT INTO sys_job VALUES (132, '每天更新合同履约状态',                        'DEFAULT', 'ctrContractScheduleTask.doUpdatePerformanceStatusTask',              '0 0 7 * * ?',    '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job doUpdatePerformanceStatusTask');

-- xxl_job_info.id=33 (running, CRON 0 0 8 ? * *)
INSERT INTO sys_job VALUES (133, '发货预警通知任务',                            'DEFAULT', 'ctrContractScheduleTask.doUnDelieryNotifyTask',                       '0 0 8 ? * *',    '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job doUnDelieryNotifyTask');

-- xxl_job_info.id=36 (stopped, CRON 0 0 0 * * ?)
INSERT INTO sys_job VALUES (136, '定时清除24小时未被使用的库存',                'DEFAULT', 'stockVirtualTask.autoDeleteStockVirtual',                            '0 0 0 * * ?',    '3', '1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job autoDeleteStockVirtual (源 trigger_status=0)');

-- xxl_job_info.id=37 (stopped, NONE — placeholder cron, method sig wants String param → empty arg)
INSERT INTO sys_job VALUES (137, '根据合同编号更新计算违约金',                  'DEFAULT', 'budgetSettlementTask.updateBudgetSettlementByContractNo(\'\')',      '0 0 0 1 1 ? 2099','3','1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job updateBudgetSettlementByContractNo (源 NONE + trigger_status=0; P7-03 operator review: PAUSE — method wants String param, empty arg)');

-- xxl_job_info.id=38 (running, CRON 0 0 5 ? * *)
INSERT INTO sys_job VALUES (138, '每天更新合同状态',                            'DEFAULT', 'ctrContractScheduleTask.refreshContractStatusTask',                   '0 0 5 ? * *',    '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job refreshContractStatusTask');

-- xxl_job_info.id=39 (running, CRON 0 0 22 ? * *)
INSERT INTO sys_job VALUES (139, '离职员工名下客户自动释放至公海',              'DEFAULT', 'bsCompanyTask.leaveReleasePublic',                                   '0 0 22 ? * *',   '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job leaveReleasePublic');

-- xxl_job_info.id=41 (stopped, CRON '* * 1 ? * 7' — P7-03 PAUSED: every-second frequency at 1am Sat)
INSERT INTO sys_job VALUES (141, '全量同步t_bs_company',                        'DEFAULT', 'synchronizedBsCompanyTask.synchronizedAllBsCompany',                 '* * 1 ? * 7',    '3', '1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job synchronizedAllBsCompany (源 trigger_status=0; P7-03 operator review: PAUSE — cron extreme frequency)');

-- xxl_job_info.id=42 (P7-03 PAUSED, CRON 0 0 3 * * ?, param empty — method refreshProfitData(String) wants approveNo)
INSERT INTO sys_job VALUES (142, '刷新风控利润统计数据',                        'DEFAULT', 'ctrContractProfitTask.refreshProfitData(\'\')',                      '0 0 3 * * ?',    '3', '1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job refreshProfitData (P7-03 operator review: PAUSE — method wants String approveNo, empty arg)');

-- xxl_job_info.id=43 (running, CRON 0 0 1 * * ?)
INSERT INTO sys_job VALUES (143, 'zgbas全量同步数据中台t_ctr_contract',         'DEFAULT', 'synchronizedCtrContractTask.synchronizedAllCtrContract',             '0 0 1 * * ?',    '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job synchronizedAllCtrContract');

-- xxl_job_info.id=44 (P7-03 PAUSED, CRON '0 0 1 ? * 1' — quartz dow=1=Sunday; needs operator confirm before enabling)
INSERT INTO sys_job VALUES (144, 'zgbase全量同步数据中台work_target',           'DEFAULT', 'synchronizedWorkTargetTask.synchronizedAllWorkTarget',               '0 0 1 ? * 1',    '3', '1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job synchronizedAllWorkTarget (P7-03 operator review: PAUSE — dow=1=Sunday in quartz, confirm semantics before enabling)');

-- xxl_job_info.id=45 (running, CRON 0 0 2 * * ?)
INSERT INTO sys_job VALUES (145, 'zgbas全量同步数据中台t_ctr_contract_ophis',   'DEFAULT', 'synchronizedCtrContractOphisTask.synchronizedAllCtrContractOphis',   '0 0 2 * * ?',    '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job synchronizedAllCtrContractOphis');

-- xxl_job_info.id=46 (running, CRON '0 0 1 ? * 1' — duplicate invoke_target of id=41, different cron+name; both kept)
INSERT INTO sys_job VALUES (146, 'zgbas全量同步数据中台t_bs_company',           'DEFAULT', 'synchronizedBsCompanyTask.synchronizedAllBsCompany',                 '0 0 1 ? * 1',    '3', '1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job synchronizedAllBsCompany (与 id=141 同 handler, 不同 cron/name; P7-03 operator review: PAUSE — dow=1=Sunday)');

-- xxl_job_info.id=47 (stopped, NONE — placeholder cron)
INSERT INTO sys_job VALUES (147, '生成历史数据中游确认收货审批单',              'DEFAULT', 'confirmReceiptDcsxTask.initHistoryConfirmReceiptDcsx',                '0 0 0 1 1 ? 2099','3','1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job initHistoryConfirmReceiptDcsx (源 NONE + trigger_status=0)');

-- xxl_job_info.id=48 (running, CRON 0 10 1 * * ?)
INSERT INTO sys_job VALUES (148, 'zgbas全量同步数据中台t_ctr_product',          'DEFAULT', 'synchronizedCtrProductTask.synchronizedAllCtrProduct',               '0 10 1 * * ?',   '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job synchronizedAllCtrProduct');

-- xxl_job_info.id=49 (running, CRON 0 15 1 * * ?)
INSERT INTO sys_job VALUES (149, 'zgbas全量同步数据中台t_pm_approve',           'DEFAULT', 'synchronizedPmApproveTask.synchronizedAllPmApprove',                 '0 15 1 * * ?',   '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job synchronizedAllPmApprove');

-- xxl_job_info.id=50 (running, CRON 0 20 1 * * ?)
INSERT INTO sys_job VALUES (150, 'zgbas全量同步数据中台t_apply_match',          'DEFAULT', 'synchronizedApplyMatchTask.synchronizedAllApplyMatch',               '0 20 1 * * ?',   '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job synchronizedAllApplyMatch');

-- xxl_job_info.id=51 (running, CRON 0 25 1 * * ?)
INSERT INTO sys_job VALUES (151, 'zgbas全量同步数据中台t_apply_match_detail',   'DEFAULT', 'synchronizedApplyMatchDetailTask.synchronizedAllApplyMatchDetail',   '0 25 1 * * ?',   '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job synchronizedAllApplyMatchDetail');

-- xxl_job_info.id=52 (stopped, CRON 0 35 1 * * ?)
INSERT INTO sys_job VALUES (152, '发起代采赊销预算全款日期/定金日期是当天的收款申请', 'DEFAULT', 'applyPayTask.autoReceive',                                       '0 35 1 * * ?',   '3', '1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job autoReceive (源 trigger_status=0)');

-- xxl_job_info.id=53 (stopped, CRON 0 27 1 * * ?)
INSERT INTO sys_job VALUES (153, '发起代采赊销预算付款日期/定金日期是当天的付款申请', 'DEFAULT', 'applyPayTask.autoPayDcsx',                                       '0 27 1 * * ?',   '3', '1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job autoPayDcsx (源 trigger_status=0)');

-- xxl_job_info.id=54 (stopped, CRON 0 30 1 * * ?)
INSERT INTO sys_job VALUES (154, '发起普通预算付款日期/定金日期是当天的付款申请', 'DEFAULT', 'applyPayTask.autoPay',                                             '0 30 1 * * ?',   '3', '1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job autoPay (源 trigger_status=0)');

-- xxl_job_info.id=55 (stopped, NONE — method wants String contractNo, empty arg)
INSERT INTO sys_job VALUES (155, '补偿结算单收违约金提成',                      'DEFAULT', 'settlementTask.refreshBreachCommission(\'\')',                       '0 0 0 1 1 ? 2099','3','1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job refreshBreachCommission (源 NONE + trigger_status=0; P7-03 operator review: PAUSE — method wants String contractNo, empty arg)');

-- xxl_job_info.id=58 (running, CRON '10 1/2 * * * ?' — sec=10, min=every 2 min starting at 1)
INSERT INTO sys_job VALUES (158, '定时自动审批',                                'DEFAULT', 'pmApproveTask.doAutoSign',                                           '10 1/2 * * * ?', '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job doAutoSign');

-- xxl_job_info.id=59 (stopped, NONE — method wants String contractNo, empty arg)
INSERT INTO sys_job VALUES (159, '初始化物流单据',                              'DEFAULT', 'ctrContractScheduleTask.initLogistics(\'\')',                        '0 0 0 1 1 ? 2099','3','1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job initLogistics (源 NONE + trigger_status=0; P7-03 operator review: PAUSE — method wants String contractNo, empty arg)');

-- xxl_job_info.id=60 (stopped, CRON 0 20 1 * * ?)
INSERT INTO sys_job VALUES (160, '更新物流单据表历史数据',                      'DEFAULT', 'pmApproveTask.updateCtrLogistics',                                   '0 20 1 * * ?',   '3', '1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job updateCtrLogistics (源 trigger_status=0)');

-- xxl_job_info.id=61 (running, CRON 0 0 1 * * ?)
INSERT INTO sys_job VALUES (161, '企业业务扩展表数据同步',                      'DEFAULT', 'bsCompanyTask.syncCompanyBusinessExpansion',                         '0 0 1 * * ?',    '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job syncCompanyBusinessExpansion');

-- xxl_job_info.id=63 (stopped, NONE, param='approveNo,contractNo' — literal looks like param NAME list, kept as-is)
INSERT INTO sys_job VALUES (163, '代采赊销盖章申请，附件生成异常补偿任务',      'DEFAULT', 'autoSealPdfTask.generateSealPDFSignDCSX(\'approveNo,contractNo\')',  '0 0 0 1 1 ? 2099','3','1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job generateSealPDFSignDCSX (源 NONE + trigger_status=0; P7-03 operator review: PAUSE — executor_param looks like placeholder name list)');

-- xxl_job_info.id=64 (stopped, NONE — method wants String param, empty arg)
INSERT INTO sys_job VALUES (164, '代采赊销盖章审批完成后自动执行签署逻辑补偿任务', 'DEFAULT', 'autoSealPdfTask.successSignContractByKeyword(\'\')',                '0 0 0 1 1 ? 2099','3','1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job successSignContractByKeyword (源 NONE + trigger_status=0; P7-03 operator review: PAUSE — method wants String param, empty arg)');

-- xxl_job_info.id=65 (running, CRON 0 0 23 ? * *, method wants String contractNo, empty arg)
INSERT INTO sys_job VALUES (165, '更新中游逾期利息',                            'DEFAULT', 'ctrContractScheduleTask.refreshOverdueInterest(\'\')',                '0 0 23 ? * *',   '3', '1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job refreshOverdueInterest (P7-03 operator review: PAUSE — method wants String contractNo, empty arg)');

-- xxl_job_info.id=67 (stopped, CRON 0 0 23 ? * *)
INSERT INTO sys_job VALUES (167, '恢复企业授信额度为人保批复额度',              'DEFAULT', 'bsCompanyTask.recoverCompanyCreditAmount',                           '0 0 23 ? * *',   '3', '1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job recoverCompanyCreditAmount (源 trigger_status=0)');

-- xxl_job_info.id=68 (running, CRON 0 0/5 * * * ?)
INSERT INTO sys_job VALUES (168, '物流单据签署补偿任务',                        'DEFAULT', 'ctrContractScheduleTask.doSignLogistics',                            '0 0/5 * * * ?',  '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job doSignLogistics');

-- xxl_job_info.id=83 (running, CRON 0 30 17 * * ?)
INSERT INTO sys_job VALUES (183, '企业微信机器人推送业绩排名',                  'DEFAULT', 'weChatWorkTask.pushWeChatWorkLeaderboard',                           '0 30 17 * * ?',  '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job pushWeChatWorkLeaderboard');

-- xxl_job_info.id=85 (running, CRON '0 0 0 * * ? ' — trailing space trimmed)
INSERT INTO sys_job VALUES (185, '临时额度到期自动恢复',                        'DEFAULT', 'bsCompanyCreditTask.recoverTemporaryAmount',                         '0 0 0 * * ?',    '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job recoverTemporaryAmount');

-- xxl_job_info.id=86 (stopped, NONE — method wants String approveNo, empty arg)
INSERT INTO sys_job VALUES (186, '业务盖章异常生成补偿任务',                    'DEFAULT', 'ctrContractScheduleTask.autoInitiatedSealUsage(\'\')',               '0 0 0 1 1 ? 2099','3','1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job autoInitiatedSealUsage (源 NONE + trigger_status=0; P7-03 operator review: PAUSE — method wants String approveNo, empty arg)');

-- xxl_job_info.id=87 (running, CRON 0 0 7 ? * *)
INSERT INTO sys_job VALUES (187, '发货45天后自动发起开票',                      'DEFAULT', 'ctrContractScheduleTask.autoStartDaDiInvoiceApply',                  '0 0 7 ? * *',    '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job autoStartDaDiInvoiceApply');

-- xxl_job_info.id=88 (running, CRON '0 0 1 * * ? ' — trailing space trimmed)
INSERT INTO sys_job VALUES (188, '业务限制解除次数重置为0',                     'DEFAULT', 'businessRestrictRelieveTask.resetUsableCount',                      '0 0 1 * * ?',    '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job resetUsableCount');

-- xxl_job_info.id=89 (stopped, CRON '0 0 1 * * ? ' — trimmed; param='contractNo' literal looks like placeholder name)
INSERT INTO sys_job VALUES (189, '刷新发货文件',                                'DEFAULT', 'ctrContractScheduleTask.refreshShippingFile(\'contractNo\')',        '0 0 1 * * ?',    '3', '1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job refreshShippingFile (源 trigger_status=0; P7-03 operator review: PAUSE — executor_param \'contractNo\' looks like placeholder name)');

-- xxl_job_info.id=90 (running, CRON '0 0 1 * * ? ' — trailing space trimmed)
INSERT INTO sys_job VALUES (190, '鸿博业务60后自动发起中游付款申请',            'DEFAULT', 'dcsxAutoApplyPayTask.autoHb60DayNotApplyDcsxPay',                    '0 0 1 * * ?',    '3', '1', '0', 'admin', sysdate(), '', null, '迁自 xxl-job autoHb60DayNotApplyDcsxPay');

-- =============================================================================
-- MATCHED executeCommand SUB-COMMAND (06-04 BasCommandExecutor / ReportCommandExecutor / BasWebCommand)
-- =============================================================================
-- Production xxl-job admin had ~10 executeCommand entries (mostly status=0, never triggered,
--   trigger_last/next_time=0). Of those, only id=27 has an unambiguous sub-command matching
--   a current executor branch. See worksheet §3.B for the 7 ambiguous/inert entries (excluded).

-- xxl_job_info.id=27 (stopped, NONE, executor_param='clean' → BasWebCommand.clean → ShiroUtil.clean())
INSERT INTO sys_job VALUES (127, 'executeCommand(clean) — 刷新 Shiro 缓存',     'COMMAND', 'basWebCommand.executeCommand(\'clean\')',                            '0 0 0 1 1 ? 2099','3','1', '1', 'admin', sysdate(), '', null, '迁自 xxl-job executeCommand (源 param=clean, job_group=9; bean disambiguated via 06-04 BasWebCommand.clean branch)');

-- =============================================================================
-- EXCLUDED — see 06-05-TRANSLATION-WORKSHEET.md §3 (Unmatched Handlers) for full decision log
-- =============================================================================
-- Excluded xxl_job_info.ids:
--   Source-deprecated (D-P6-03 ③ 跳过): 12 (overdueTask — 06-02 OrverdurTask @XxlJob commented)
--   Test stub (not on QUARTZ-03 path):  69 (testXxlJob — 06-02 TestJob excluded)
--   Non-migrated handlers (no @XxlJob match in 06-02/03/04):
--     5 (updateCompanyBasicInfo), 15 (doSuccessContract), 16 (doSuccessDebtCertificate),
--     17 (loadInfoAll), 18 (calcAll), 19 (verifyContract), 20 (loadWfqDataInfo),
--     31 (doReceiveGood), 34 (loadPiccAll), 35 (supplierCalcAll),
--     56 (querySeal), 57 (deleteSeal), 62 (syncWechatToAdmin), 66 (addEvaHumanCostHandler),
--     70 (syncAllContract), 71 (piccQueryCreditStatus), 72 (getGuTuCompanyBaseInfo),
--     73 (getCompanyHolderListCount), 74 (getCompanyEquityCount), 75 (getCompanyEquityFreeze),
--     76 (getCompanyInvestment), 77 (getJudicialSale), 78 (getOwnTaxCount),
--     79 (getEnvironmentalPenaltiesCount), 80 (getCompanyAbnormalCount),
--     81 (getMergePunishCount), 82 (getMergeLicenseCount), 84 (syncAllContractSettlement)
--   Stale test entry:                    40 (initHistoryProfit11111 — name suffix "11111" indicates test stub)
--   Ambiguous executeCommand (no/inert/unrecognized param + stopped + never triggered):
--     22, 23, 24, 28, 29 (empty param — indeterminate executor bean)
--     26 (param='getReceive' — not a current BasCommandExecutor branch)
--     30 (param='clean cache' — multi-token, no executor branch matches)
