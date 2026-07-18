package com.spt.quartz.task;

import org.apache.commons.lang3.StringUtils;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.entity.ApplyMatchDetail;
import com.spt.bas.server.ctr.service.impl.CtrContractDataRefService;
import com.spt.bas.server.dao.ApplyMatchDao;
import com.spt.bas.server.dao.ApplyMatchDetailDao;
import com.spt.bas.server.service.*;
import com.spt.bas.server.service.impl.OverdueInterestProcessor;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.service.IPmApproveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.CtrContractScheduleTask}.
 * Bean name {@code "ctrContractScheduleTask"} aligns with {@code sys_job.invoke_target}
 * short names {@code ctrContractScheduleTask.updateRiskScheduleTask},
 * {@code ctrContractScheduleTask.doUpdatePerformanceStatusTask},
 * {@code ctrContractScheduleTask.refreshContractStatusTask},
 * {@code ctrContractScheduleTask.refreshBuyBilledAmount},
 * {@code ctrContractScheduleTask.initLogistics('${contractNo}')},
 * {@code ctrContractScheduleTask.refreshOverdueInterest('${contractNo}')}, etc.
 */
@Component("ctrContractScheduleTask")
public class CtrContractScheduleTask {
    private static final Logger log = LoggerFactory.getLogger(CtrContractScheduleTask.class);

    @Autowired
    private ICtrContractScheduleService ctrContractScheduleService;

    @Autowired
    private CtrContractDataRefService ctrContractDataRefService;

    @Autowired
    private OverdueInterestProcessor overdueInterestProcessor;

    @Resource
    private ICtrLogisticsService ctrLogisticsService;

    @Resource
    private IApplyDeliveryInService deliveryInService;

    @Resource
    private IApplyConfrimReceiptService confrimReceiptService;

    @Resource
    private ApplyChargeSalesService applyChargeSalesService;

    @Resource
    private IPmApproveService pmApproveService;

    @Resource
    private ApplyMatchDao applyMatchDao;

    @Resource
    private ApplyMatchDetailDao applyMatchDetailDao;

    @Resource
    private IApplyDcsxService applyDcsxService;

    @Resource
    private IApplyDeliveryOutService deliveryOutService;

    /**
     * 定时任务每日凌晨3点更新风控待办事项
     */
    public void updateRiskScheduleTask() {
        log.info("更新入库日期超过7日待办事项======>");
        ctrContractScheduleService.doWarehouseScheduleTask();

        log.info("更新预售超过7日未回补待办事项======>");
        ctrContractScheduleService.doPreSellScheduleTask();

        log.info("更新月末3日内未收到进项发票待办事项======>");
        Calendar calendar = Calendar.getInstance();
        if ((calendar.getActualMaximum(Calendar.DATE) - calendar.get(Calendar.DATE)) <= 3) {
            ctrContractScheduleService.doBilledScheduleTask();
        }
    }

    public void doUpdatePerformanceStatusTask() {
        log.info("更新合同履约状态任务开始======>");
        ctrContractScheduleService.doUpdatePerformanceStatusTask();
        log.info("更新合同履约状态任务结束<======");
    }

    public void refreshContractStatusTask() {
        log.info("更新合同状态任务开始======>");
        ctrContractScheduleService.refreshContractStatus();
        log.info("更新合同状态任务结束<======");
    }


    public void doUnDelieryNotifyTask() {
        log.info("发货预警通知任务开始======>");
        ctrContractScheduleService.doUnDelieryNotifyTask();
        log.info("发货预警通知任务开始<======");
    }

    public void refreshBuyBilledAmount() throws Exception {
        log.info("刷新自营乙二醇采购收票数据任务开始======>");
        ctrContractDataRefService.refreshBuyBilledAmount();
        log.info("刷新自营乙二醇采购收票数据任务结束<======");
    }


    public void refreshSellBilledAmount() throws Exception {
        log.info("刷新自营乙二醇销售开票数据任务开始======>");
        ctrContractDataRefService.refreshSellBilledAmount();
        log.info("刷新自营乙二醇销售开票数据任务结束<======");
    }

    /**
     * 初始化物流单据
     */
    public void initLogistics(String contractNo) {
        log.info("contractNo:{}", contractNo);
        log.info("初始化物流单据任务开始======>");
        ctrLogisticsService.initLogistics(contractNo);
        log.info("初始化物流单据任务结束<======");
    }

    /**
     * 更新中游逾期罚息
     */
    public void refreshOverdueInterest(String contractNo) throws Exception {
        log.info("contractNo:{}", contractNo);
        log.info("更新中游逾期罚息任务开始======>");
        overdueInterestProcessor.refreshOverdueInterest(contractNo);
        log.info("更新中游逾期罚息任务结束<======");
    }


    /**
     * 入库单、出库单 物流单价签署补偿任务
     */
    public void doSignLogistics() throws Exception {
        log.info("入库单物流单价签署补偿任务开始======>");
        deliveryInService.doSignLogistics();
        log.info("入库单物流单价签署补偿任务结束<======");
        log.info("=================================");
        log.info("出库单物流单价签署补偿任务开始======>");
        confrimReceiptService.doSignLogistics();
        log.info("出库单 物流单价签署补偿任务结束<======");
    }

    /**
     * 业务盖章审批流程补偿任务
     */
    public void autoInitiatedSealUsage(String approveNo) throws Exception {
        if (StringUtils.isBlank(approveNo)) {
            throw new RuntimeException("审批单号不可为空!");
        }
        PmApprove approve = pmApproveService.findByApproveNo(approveNo);
        if (Objects.isNull(approve)) {
            throw new RuntimeException("未查询到审批单!");
        }
        log.info("业务盖章审批流程补偿任务开始======>");
        Long approveId = approve.getId();
        ApplyCtrDCSX applyCtrDCSX = applyDcsxService.findByDCSXApproveId(approveId);
        ApplyMatch match = applyMatchDao.findByApproveId(approveId);
        List<ApplyMatchDetail> matchDetailList = applyMatchDetailDao.findByApplyMatchId(match.getId());
        applyChargeSalesService.autoInitiatedSealUsage(applyCtrDCSX, match, matchDetailList, approve);
        log.info("业务盖章审批流程补偿任务结束<======");
    }

    /**
     * 使用大地额度的下游赊销订单，在发货45天后自动发起开票申请
     */
    public void autoStartDaDiInvoiceApply() throws Exception {
        log.info("使用大地额度的下游赊销订单，在发货45天后自动发起开票申请任务开始======>");
        ctrContractScheduleService.startDaDiInvoiceApply();
        log.info("使用大地额度的下游赊销订单，在发货45天后自动发起开票申请任务结束<======");
    }

    /**
     * 刷新发货单据任务
     */
    public void refreshShippingFile(String contractNo) throws Exception {
        log.info("刷新发货单据任务任务开始======>");
        deliveryOutService.refreshShippingFile(contractNo);
        log.info("刷新发货单据任务任务结束<======");
    }
}
