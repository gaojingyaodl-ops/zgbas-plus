package com.spt.quartz.task;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.server.dao.ApplyDcsxDao;
import com.spt.bas.server.filter.IAutoStartPayFilter;
import com.spt.bas.server.service.IApplyDcsxReceiveService;
import com.spt.bas.server.service.IApplyPayService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;


/**
 * Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.ApplyPayTask}
 * (source: zgbas/feat-系统重构v5.0). Package changed to {@code com.spt.quartz.task}
 * per D-P6-09; service iface package names preserved.
 *
 * <p>Bean name {@code "applyPayTask"} aligns with {@code sys_job.invoke_target}
 * short names such as {@code applyPayTask.autoStartPayProcess} /
 * {@code applyPayTask.autoPay} / {@code applyPayTask.autoPayDcsx} /
 * {@code applyPayTask.autoReceive}. Multiple xxl-job handler methods (now
 * plain public methods after Pattern 3 translation) share one bean; each
 * {@code sys_job} row points to a different method on the same bean.
 */
@Component("applyPayTask")
public class ApplyPayTask {
    private Logger logger = LoggerFactory.getLogger(BudgetSettlementTask.class);
    @Autowired
    private ApplyDcsxDao applyDcsxDao;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private IAutoStartPayFilter autoStartPayFilter;
    @Autowired
    private IApplyPayService applyPayService;
    @Autowired
    private  IApplyDcsxReceiveService applyDcsxReceiveService;

    @SneakyThrows
    public void autoStartPayProcess() {
//        logger.info("中游合同付尾款定时任务开始======>");
//        List<ApplyCtrDCSX> ctrDCSXList = applyDcsxDao.findNeedPayList();
//        if (CollectionUtils.isEmpty(ctrDCSXList)) {
//            logger.info("未查询出需要付尾款的中游合同!");
//            logger.info("未查询出需要付尾款的中游合同!");
//            return;
//        }
//        ctrDCSXList.forEach(dcsx -> {
//            try {
//                // 判断中游合同关联的上下游合同是否作废
//                List<CtrContract> ctrContractList = ctrContractService.findByApproveId(dcsx.getApproveId());
//                boolean existInvalid = ctrContractList.stream().anyMatch(c -> StringUtils.equals(BasConstants.CONTRACTSTATUS_C, c.getStatus()));
//                if (Boolean.TRUE.equals(existInvalid)){
//                    logger.info("定时任务-到达付款日自动发起代采赊销付款审批单，上下游合同已作废跳过：contractNo:{} ", dcsx.getContractNo());
//                    return;
//                }
//                autoStartPayFilter.doPayTaskFilter(dcsx);
//            } catch (Exception e) {
//                logger.info("定时任务执行异常：contractNo:{}", dcsx.getContractNo());
//                logger.error("autoStartPayProcess error:{}", e);
//            }
//            logger.info("定时任务-到达付款日自动发起代采赊销付款审批单：contractNo:{}", dcsx.getContractNo());
//        });
//        logger.info("中游合同付尾款定时任务 执行成功!");
    }




    public void autoPay() {
//        logger.info("到约定付款日自动发起付款");
//        //付全款
//        List<CtrContract> ctrContractAutoVo = ctrContractService.autoPayAmount();
//        if(ctrContractAutoVo.size()>0){
//            ctrContractAutoVo.stream().forEach(s->{
//                try {
//                    applyPayService.autoApplyPayTask(s.getContractNo(),1);
//                }catch (Exception e){
//                    logger.info("定时任务执行异常：contractNo:{}", s.getContractNo());
//                    logger.error("异常：autoPay",e);
//                }
//            });
//        }
//
//        logger.info("到约定付款日自动发起付定金款");
//        //付定金
//        List<CtrContract> ctrContractAutoVos = ctrContractService.autoPayBondAmount();
//        if(ctrContractAutoVos.size()>0){
//            ctrContractAutoVos.stream().forEach(v->{
//                try {
//                    applyPayService.autoApplyPayTask(v.getContractNo(),2);
//                }catch (Exception e){
//                    logger.info("定时任务执行异常：contractNo:{}", v.getContractNo());
//                    logger.error("异常：autoPay",e);
//                }
//            });
//        }

    }

    @SneakyThrows
    public void autoPayDcsx() {
//        logger.info("代采赊销约定付款日自动发起付款");
//        //付全款
//        List<ApplyCtrDCSX> amount = applyDcsxDao.autoDcsxPayAmount();
//        if(amount.size()>0){
//            amount.stream().forEach(s->{
//                try {
//                    autoStartPayFilter.doSealUsageFilterAutoTask(s,1);
//                }catch (Exception e){
//                    logger.info("定时任务执行异常：contractNo:{}", s.getContractNo());
//                    logger.error("异常：autoPayDcsx",e);
//                }
//            });
//        }
//        logger.info("代采赊销约定付款日自动发起付定金款");
//        //付定金
//        List<ApplyCtrDCSX> bondAmount = applyDcsxDao.autoDcsxPayBondAmount();
//        if(bondAmount.size()>0){
//            bondAmount.stream().forEach(t->{
//                try {
//                    autoStartPayFilter.doSealUsageFilterAutoTask(t,2);
//                }catch (Exception e){
//                    logger.info("定时任务执行异常：contractNo:{}", t.getContractNo());
//                    logger.error("异常：autoPayDcsx",e);
//                }
//            });
//        }
    }

    @SneakyThrows
    public void autoReceive() {
        logger.info("定时任务自动收定金款");
        //定金
        List<ApplyCtrDCSX> amountBond = applyDcsxDao.autoDcsxBondReceiveAmount();
        if(amountBond.size()>0){
            amountBond.stream().forEach(t->{
                try {
                    applyDcsxReceiveService.ApplyDcsxReceiveTask(t,1);
                } catch (ApplicationException e) {
                    logger.info("定时任务执行异常：contractNo:{}", t.getContractNo());
                    logger.error("异常：autoReceive",e);
                }
            });
        }
        logger.info("定时任务自动收全款");
        //全款
        List<ApplyCtrDCSX> amount = applyDcsxDao.autoDcsxReceiveAmount();
        if(amount.size()>0){
            amount.stream().forEach(s->{
                try {
                    applyDcsxReceiveService.ApplyDcsxReceiveTask(s,2);
                } catch (ApplicationException e) {
                    logger.info("定时任务执行异常：contractNo:{}", s.getContractNo());
                    logger.error("异常：autoReceive",e);
                }
            });
        }
    }

}
