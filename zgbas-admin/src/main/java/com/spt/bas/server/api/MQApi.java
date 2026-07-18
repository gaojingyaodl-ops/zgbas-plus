package com.spt.bas.server.api;

import com.spt.bas.server.service.IMqSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MQ 同步触发 HTTP 门面 (D-P6-11).
 *
 * <p>Phase 6 (06-04) — ported from {@code com.spt.bas.server.api.MQApi} (source:
 * zgbas/basCore/basServer). Per D-P6-11, the HTTP endpoint contract is preserved verbatim
 * (frontend zero-change): {@code @RequestMapping("/mq/api")} + all 8 {@code @GetMapping} paths
 * unchanged. Internally, the {@code @Autowired Synchronized*Task} handler beans are replaced by
 * a single {@link IMqSyncService} direct-service-call dependency — decoupling the HTTP sync
 * entry from the quartz handler layer (the {@code Synchronized*Task} beans continue to serve as
 * sys_job-scheduled cron entry points).
 *
 * <p>End-point → IMqSyncService method mapping (1:1 with source Synchronized*Task methods):
 * <ul>
 *   <li>{@code GET /mq/api/ctrContractTask} → {@link IMqSyncService#synchronizedAllCtrContract()}</li>
 *   <li>{@code GET /mq/api/ctrProductTask}  → {@link IMqSyncService#synchronizedAllCtrProduct()}</li>
 *   <li>{@code GET /mq/api/workTargetTask}  → {@link IMqSyncService#synchronizedAllWorkTarget()}</li>
 *   <li>{@code GET /mq/api/ophisTask}       → {@link IMqSyncService#synchronizedAllCtrContractOphis()}</li>
 *   <li>{@code GET /mq/api/companyTask}      → {@link IMqSyncService#synchronizedAllBsCompany()}</li>
 *   <li>{@code GET /mq/api} (root)           → {@link IMqSyncService#testSendMessage()} (dev/test)</li>
 *   <li>{@code GET /mq/api/applyMatchTask}   → {@link IMqSyncService#synchronizedAllApplyMatch()}</li>
 *   <li>{@code GET /mq/api/applyMatchDetailTask} → {@link IMqSyncService#synchronizedAllApplyMatchDetail()}</li>
 *   <li>{@code GET /mq/api/pmApproveTask}    → {@link IMqSyncService#synchronizedAllPmApprove()}</li>
 * </ul>
 *
 * @author 杨英承 (source); Phase 6 (06-04) D-P6-11 refactor
 */
@RestController
@RequestMapping(value = "/mq/api")
public class MQApi {

    @Autowired
    private IMqSyncService mqSyncService;

    @GetMapping("/ctrContractTask")
    public void ctrContractTask() {
        mqSyncService.synchronizedAllCtrContract();
    }

    @GetMapping("/ctrProductTask")
    public void ctrProductTask() {
        mqSyncService.synchronizedAllCtrProduct();
    }

    @GetMapping("/workTargetTask")
    public void workTargetTask() {
        mqSyncService.synchronizedAllWorkTarget();
    }

    @GetMapping("/ophisTask")
    public void ophisTask() {
        mqSyncService.synchronizedAllCtrContractOphis();
    }

    @GetMapping("/companyTask")
    public void companyTask() {
        mqSyncService.synchronizedAllBsCompany();
    }

    @GetMapping
    public void testSendMessage() {
        mqSyncService.testSendMessage();
    }

    @GetMapping("/applyMatchTask")
    public void applyMatchTask() {
        mqSyncService.synchronizedAllApplyMatch();
    }

    @GetMapping("/applyMatchDetailTask")
    public void applyMatchDetailTask() {
        mqSyncService.synchronizedAllApplyMatchDetail();
    }

    @GetMapping("/pmApproveTask")
    public void pmApproveTask() {
        mqSyncService.synchronizedAllPmApprove();
    }

}
