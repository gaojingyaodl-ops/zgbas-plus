package com.spt.bas.server.service;

/**
 * MQ data-sync orchestration service.
 *
 * <p>Phase 6 (06-04) — D-P6-11 direct-service-call refactor target for {@code MQApi} HTTP
 * endpoints. The 8 business methods encapsulate the same pagination + RocketMQ async-send
 * orchestration that the {@code Synchronized*Task} handlers (sys_job-scheduled entry points)
 * perform — yielding two coexisting entry points (sys_job async cron + MQApi HTTP sync) that
 * execute equivalent business logic. {@code testSendMessage} is a 1:1 dev/test endpoint
 * (hardcoded topic {@code "yyc-data"}).
 *
 * <p>Design decision (06-04): the 8 methods are aggregated into a single new service iface
 * rather than scattered across per-domain service ifaces (ICtrContractService / IBsCompanyService
 * etc.). Rationale: (a) the Synchronized*Task bodies use DAOs + RocketMQ infrastructure directly
 * (not per-domain service orchestration), so per-domain placement would pollute those impls with
 * raw DAO access; (b) the 8 methods form a cohesive "MQ data sync" concern; (c) 06-03-SUMMARY
 * explicitly lists this aggregator approach as a viable alternative to the per-domain path.
 *
 * @author Phase 6 (06-04)
 */
public interface IMqSyncService {

    /**
     * 全量同步数据中台的合同数据 (source: SynchronizedCtrContractTask).
     */
    void synchronizedAllCtrContract();

    /**
     * 全量同步数据中台合同产品详情 (source: SynchronizedCtrProductTask).
     */
    void synchronizedAllCtrProduct();

    /**
     * 全量同步 WorkTarget 目标 (source: SynchronizedWorkTargetTask).
     */
    void synchronizedAllWorkTarget();

    /**
     * 全量同步合同历史数据 (source: SynchronizedCtrContractOphisTask).
     */
    void synchronizedAllCtrContractOphis();

    /**
     * 全量同步 t_bs_company 数据 (source: SynchronizedBsCompanyTask).
     */
    void synchronizedAllBsCompany();

    /**
     * 全量同步合同撮合表 (source: SynchronizedApplyMatchTask).
     */
    void synchronizedAllApplyMatch();

    /**
     * 全量同步合同撮合表详情表 (source: SynchronizedApplyMatchDetailTask).
     */
    void synchronizedAllApplyMatchDetail();

    /**
     * 全量同步审批表 (source: SynchronizedPmApproveTask).
     */
    void synchronizedAllPmApprove();

    /**
     * Dev/test endpoint — sends a fixed payload to topic {@code "yyc-data"}
     * (source: SynchronizedWorkTargetTask.testSendMessage).
     */
    void testSendMessage();
}
