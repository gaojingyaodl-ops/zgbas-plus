package com.spt.quartz.task;


import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrContractFactor;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.service.IApplyCtrContractFactoService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.service.impl.PmApproveServiceImpl;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.DcsxRepaymentdTask}.
 * Bean name {@code "dcsxRepaymentdTask"} aligns with {@code sys_job.invoke_target}
 * short name {@code dcsxRepaymentdTask.run}.
 */
@Component("dcsxRepaymentdTask")
public class DcsxRepaymentdTask {

    private Logger logger = LoggerFactory.getLogger(BudgetSettlementTask.class);

    /**
     * 中光企业id(固定)
     */
    private static final Long ZG_ENTERPRISE_ID = BasConstants.ZG_ENTERPRISE_ID;
    @Autowired
    private ICtrContractService ctrContractService;

    @Autowired
    private IApplyCtrContractFactoService applyCtrContractFactoService;

    @Autowired
    private IPmProcessService iPmProcessService;

    @Autowired
    private PmApproveServiceImpl approveService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    /**
     * 每天凌晨两点执行自动发起 隔一天的银行还款
     * @throws ParseException
     * @throws ApplicationException
     */
    @SneakyThrows
    public void run() throws ParseException, ApplicationException {
        logger.info("保理预算还款定时任务启动");
        List<CtrContract> contractsList = ctrContractService.findByContractTypeDCSXBl();
        contractsList.removeIf(s -> s.getPayFullTime() == null );
        for (CtrContract c : contractsList) {
            SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd ");
            Date begin = dfs.parse(dfs.format(c.getPayFullTime()));
            Date end =dfs.parse(dfs.format(new Date()));
            long between = (end.getTime()-begin.getTime())  / 1000;
            long day = between / (24 * 3600);
            if (day==-1) {
                logger.info("保理预算还款定时任务发起", c.getContractNo(), c.getPayFullTime());
                ApplyCtrContractFactor applyCtrContractFactor = applyCtrContractFactoService.findByApproveId(c.getApproveId());
                if (applyCtrContractFactor.getBackAmount()!=null && applyCtrContractFactor.getLoanAmount()!=null &&applyCtrContractFactor.getBackAmount().compareTo(applyCtrContractFactor.getLoanAmount()) == 0) {
                    logger.info("{}已完成付款跳過", c.getContractNo());
                    continue;
                }
                if(applyCtrContractFactor.getRepaymentApplyStatus().equals("B")||applyCtrContractFactor.equals("D")){
                    logger.info("已有审批不能重复发起", c.getContractNo());
                    continue;
                }else{
                    if(applyCtrContractFactor.getLoanAmount()!=null){
                        applyPayAuto(applyCtrContractFactor, BasConstants.APPLY_DCSXBL_PAY, c.getMatchUserId());
                    }
                }
            }
        }
    }

    public void applyPayAuto(ApplyCtrContractFactor applyCtrContractFactor, String processCode, Long createUserId) throws ApplicationException {
        ApplyPay pay = new ApplyPay();
        pay.setContractId(applyCtrContractFactor.getContractId());
        pay.setContractNo(applyCtrContractFactor.getContractNo());
        pay.setTotalAmount(applyCtrContractFactor.getLoanAmount());
        pay.setPayAmount(applyCtrContractFactor.getLoanAmount());
        pay.setCompanyName("放款银行");
        pay.setEnterpriseId(ZG_ENTERPRISE_ID);
        pay.setApproveId(applyCtrContractFactor.getApproveId());
        pay.setOurCompanyName(applyCtrContractFactor.getOurCompanyName());
        pay.setPayDate(new Date());
        pay.setPayMode("T");
        pay.setPayType("A");
//        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BsDictConstants.DICT_TYPE_BANKINFO);
//        String bankname = listByCategory.get(0).getDictName();
        String bizEntityJson = JsonUtil.obj2Json(pay);
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setEnterpriseId(ZG_ENTERPRISE_ID);
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ZG_ENTERPRISE_ID);
        searchVo.setProcessCode(processCode);
        PmProcess process = iPmProcessService.findByProcessCode(searchVo);
        if (process == null) {
            throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
        }
        String value = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.Factoring_Repayment_Originator, BasConstants.Factoring_Repayment_Originator_User);
        SysUserSdk userById = authOpenFacade.findUserById(new Long(value));
        startVo.setUserId(userById.getUserId());
        startVo.setUserName(userById.getNickName());
        startVo.setProcessId(process.getId());
        startVo.setApproveId(0L);
        startVo.setBizEntityJson(bizEntityJson);
        approveService.startFlow(startVo);
    }


}
