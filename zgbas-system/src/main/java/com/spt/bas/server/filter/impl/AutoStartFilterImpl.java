package com.spt.bas.server.filter.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.IApplyChargeSalesClient;
import com.spt.bas.client.vo.BsBankVo;
import com.spt.bas.server.dao.ApplyDcsxDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.filter.IAutoStartPayFilter;
import com.spt.bas.server.service.IBsCompanyDcsxService;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 中间链中游合同自动发起代采赊销付款申请
 *
 * @Author: gaojy
 * @create 2022/11/25 11:00
 * @version: 1.0
 * @description:
 */
@Component
public class AutoStartFilterImpl implements IAutoStartPayFilter {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private static final ScheduledExecutorService SCHEDULED_POOL = Executors.newScheduledThreadPool(10);
    @Autowired
    private IBsCompanyDcsxService bsCompanyDcsxService;
    @Autowired
    private ApplyDcsxDao applyDcsxDao;
    @Autowired
    private IPmProcessService processService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private IApplyChargeSalesClient applyChargeSalesClient;

    private static final String AUTO_START_MESSAGE_1_1 = "盖章完成，自动发起代采赊销付全款";
    private static final String AUTO_START_MESSAGE_1_2 = "盖章审批通过，系统自动发起20%预付款";
    private static final String AUTO_START_MESSAGE_2 = "下游回款，自动发起代采赊销付款";
    private static final String AUTO_START_MESSAGE_3 = "约定付款日到期，自动发起代采赊销付款";

    /**
     * 【代采赊销盖章通过后】 自动发起 代采赊销付款申请
     *
     * @param entity 中游合同
     */
    @Override
    @ServerTransactional
    public void doSealUsageFilter(ApplyCtrDCSX entity) {
//        Map<String, BsCompanyDcsx> companyConfigMap = bsCompanyDcsxService.getCompanyConfigMap();
//        BsCompanyDcsx config = companyConfigMap.get(entity.getCompanyName());
//        if (Objects.isNull(config)) {
//            return;
//        }
//        if (!StringUtils.equals(BasConstants.CHAIN_PAY_TYPE_1, config.getChainPayType())) {
//            return;
//        }
//        // 发起代采赊销付款审批单
//        // 暂时保留泛太克 盖章审批通过后发起20%预付款申请逻辑
//        String autoStartMessage = AUTO_START_MESSAGE_1_1;
//        if (StringUtils.equals(BasConstants.COMPANY_NAME_FTK, entity.getCompanyName())) {
//            autoStartMessage = AUTO_START_MESSAGE_1_2;
//        }
//        autoStartPayApprove(entity, autoStartMessage);
    }

    /**
     * 【收到下游合同全款】 自动发起 代采赊销付款申请
     *
     * @param entity 中游合同
     */
    @Override
    @ServerTransactional
    public void doApplyReceiveFilter(ApplyCtrDCSX entity) {
//        Map<String, BsCompanyDcsx> companyConfigMap = bsCompanyDcsxService.getCompanyConfigMap();
//        BsCompanyDcsx config = companyConfigMap.get(entity.getCompanyName());
//        if (Objects.isNull(config)) {
//            return;
//        }
//        if (!StringUtils.equals(BasConstants.CHAIN_PAY_TYPE_2, config.getChainPayType())) {
//            return;
//        }
//
//        List<ApplyCtrDCSX> dcsxList = applyDcsxDao.findByApproveId(entity.getApproveId());
//        dcsxList.forEach(dcsx -> autoStartPayApprove(dcsx,  AUTO_START_MESSAGE_2));
    }

    /**
     * 【到达付款日】定时任务自动发起 代采赊销付款申请
     *
     * @param entity
     */
    @Override
    @ServerTransactional
    public void doPayTaskFilter(ApplyCtrDCSX entity) {
//        Map<String, BsCompanyDcsx> companyConfigMap = bsCompanyDcsxService.getCompanyConfigMap();
//        BsCompanyDcsx config = companyConfigMap.get(entity.getCompanyName());
//        if (Objects.isNull(config)) {
//            return;
//        }
//        if (!StringUtils.equals(BasConstants.CHAIN_PAY_TYPE_3, config.getChainPayType())) {
//            return;
//        }
//        autoStartPayApprove(entity, AUTO_START_MESSAGE_3);
    }

    @Override
    public void doSealUsageFilterAutoTask(ApplyCtrDCSX entity, int i) {
//        Map<String, BsCompanyDcsx> companyConfigMap = bsCompanyDcsxService.getCompanyConfigMap();
//        BsCompanyDcsx config = companyConfigMap.get(entity.getCompanyName());
//        if (Objects.isNull(config)) {
//            return;
//        }
////        if (!StringUtils.equals(BasConstants.CHAIN_PAY_TYPE_3, config.getChainPayType())) {
////            return;
////        }
//        autoStartPayApproveAuto(entity, AUTO_START_MESSAGE_3, i);
    }

    /**
     * 发起代采赊销付款审批单
     *
     * @param entity
     * @param autoStartMessage
     */
    private void autoStartPayApprove(ApplyCtrDCSX entity, String autoStartMessage) {
//        SCHEDULED_POOL.schedule(() -> {
//            try {
//                PmProcessSearchVo searchVo = new PmProcessSearchVo(BasConstants.PROCESS_CODE_DCSX_PAY, entity.getEnterpriseId());
//                PmProcess process = processService.findByProcessCode(searchVo);
//
//                // 1.构建代采赊销付款申请单所需参数
//                ApplyPay applyPay = buildPayParam(entity);
//
//                if (Objects.isNull(applyPay)) {
//                    return;
//                }
//                String message=autoStartMessage;
//                if(StringUtils.equals(applyPay.getPayType(),"B")){
//                    message="盖章完成，自动发起代采赊销付定金";
//                }
//                // 2.构建代采赊销付款审批单发起所需参数
//                PmApproveSaveVo startVo = buildPmApproveStartVo(applyPay, process, entity, message);
//
//                // 3.发起代采赊销付款审批单
//                pmApproveService.startFlow(startVo);
//            } catch (Exception e) {
//                logger.error("doApplyPayTask error:{}", e.getLocalizedMessage());
//            }
//        }, 3, TimeUnit.SECONDS);
    }

    /**
     * 构建代采赊销付款审批单发起所需参数
     *
     * @param applyPay
     * @param process
     * @return
     */
    private PmApproveSaveVo buildPmApproveStartVo(ApplyPay applyPay, PmProcess process, ApplyCtrDCSX entity, String autoStartMessage) {
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        String bizEntityJson = JsonUtil.obj2Json(applyPay);
        startVo.setApproveId(0L);
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setEnterpriseId(entity.getEnterpriseId());
        startVo.setUserId(entity.getMatchUserId());
        startVo.setUserName(entity.getMatchUserName());
        startVo.setProcessId(process.getId());
        startVo.setBizEntityJson(bizEntityJson);
        startVo.setAutoStartMessage(autoStartMessage);
        return startVo;
    }

    /**
     * 构造代采赊销付款审批单参数
     *
     * @return
     */
    private ApplyPay buildPayParam(ApplyCtrDCSX dcsx) throws ApplicationException {
        ApplyPay pay = new ApplyPay();
        pay.setCompanyId(dcsx.getCompanyId());
        pay.setContractId(dcsx.getId());
        pay.setContractNo(dcsx.getContractNo());
        pay.setTotalAmount(dcsx.getTotalAmount());

        BigDecimal payAmount = dcsx.getTotalAmount();
        BigDecimal applyPayAmount = dcsx.getApplyPayAmount();
        applyPayAmount = Objects.isNull(applyPayAmount) ? BigDecimal.ZERO : applyPayAmount;
        BigDecimal amount = dcsx.getTotalAmount().subtract(dcsx.getDealedAmount()).subtract(applyPayAmount);
        // 剩余可付金额小于等于0 终止发起动作
        if (payAmount.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("contractNo:{},剩余可付金额小于等于0 终止发起动作", dcsx.getContractNo());
            return null;
        }
        if (StringUtils.equals(BasConstants.COMPANY_NAME_FTK, dcsx.getCompanyName())) {
            pay.setPayType("B");
            payAmount = dcsx.getTotalAmount().multiply(new BigDecimal("0.2"));
            pay.setPayDate(new Date());
        }
        if (payAmount.compareTo(amount) > 0) {
            payAmount = amount;
        }
        BigDecimal bondAmount = dcsx.getBondAmount();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
        final String payFullTime = sdf.format(dcsx.getPayFullTime());
        String payBondTime=null;
        if(dcsx.getPayBondTime()!=null){
            payBondTime= sdf.format(dcsx.getPayBondTime());
        }
        final String date = sdf.format(new Date());
        if (Objects.nonNull(payFullTime) && payFullTime.equals(date)) {
            pay.setPayDate(dcsx.getPayFullTime());
            pay.setPayAmount(payAmount);
            pay.setPayType("A");
        }
        if (Objects.nonNull(payBondTime) && payBondTime.equals(date)) {
            if(bondAmount!=null&&bondAmount.compareTo(BigDecimal.ZERO) > 0){
                pay.setPayDate(dcsx.getPayBondTime());
                pay.setPayAmount(bondAmount);
                pay.setPayType("B");
            }
        }
        pay.setPayedAmount(Objects.nonNull(dcsx.getDealedAmount()) ? BigDecimal.ZERO : dcsx.getDealedAmount());
        pay.setCompanyName(dcsx.getCompanyName());
        pay.setStatus(dcsx.getStatus());
        pay.setEnterpriseId(dcsx.getEnterpriseId());
        pay.setApproveId(dcsx.getApproveId());
        pay.setOurCompanyName(dcsx.getOurCompanyName());
        pay.setBusinessType(dcsx.getBusinessType());
        pay.setPayMode("T");
        String ourCompanyName = dcsx.getOurCompanyName();
        if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, ourCompanyName)) {
            BsBankVo specialBank = applyChargeSalesClient.getSpecialBank(dcsx.getEnterpriseId());
            if (Objects.nonNull(specialBank)) {
                pay.setBankName(specialBank.getBankName());
                pay.setBankAccount(specialBank.getBankNum());
            }
        } else {
            BsCompanyDcsx companyDcsx = bsCompanyDcsxService.findByCompanyName(dcsx.getCompanyName());
            if (Objects.nonNull(companyDcsx)) {
                pay.setBankName(companyDcsx.getCompanyBankName());
                pay.setBankAccount(companyDcsx.getCompanyCardId());
            }
        }
        pay.setUnpayedAmount(dcsx.getUnpayedAmount());
        return pay;
    }




    /**
     * 构造代采赊销付款审批单参数
     *
     * @return
     */
    private ApplyPay buildPayParamAuto(ApplyCtrDCSX dcsx,int i) throws ApplicationException {
        ApplyPay pay = new ApplyPay();
        pay.setCompanyId(dcsx.getCompanyId());
        pay.setContractId(dcsx.getId());
        pay.setContractNo(dcsx.getContractNo());
        pay.setTotalAmount(dcsx.getTotalAmount());

        BigDecimal payAmount = dcsx.getTotalAmount();
        BigDecimal applyPayAmount = dcsx.getApplyPayAmount();
        applyPayAmount = Objects.isNull(applyPayAmount) ? BigDecimal.ZERO : applyPayAmount;
        BigDecimal amount = dcsx.getTotalAmount().subtract(dcsx.getDealedAmount()).subtract(applyPayAmount);
        // 剩余可付金额小于等于0 终止发起动作
        if (payAmount.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("contractNo:{},剩余可付金额小于等于0 终止发起动作", dcsx.getContractNo());
            return null;
        }
        if (StringUtils.equals(BasConstants.COMPANY_NAME_FTK, dcsx.getCompanyName())) {
            pay.setPayType("B");
            payAmount = dcsx.getTotalAmount().multiply(new BigDecimal("0.2"));
            pay.setPayDate(new Date());
        }
        if (payAmount.compareTo(amount) > 0) {
            payAmount = amount;
        }
        BigDecimal bondAmount = dcsx.getBondAmount();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
        String payFullTime=null;
        if(dcsx.getPayFullTime()!=null){
              payFullTime = sdf.format(dcsx.getPayFullTime());
        }
        String payBondTime=null;
        if(dcsx.getPayBondTime()!=null){
            payBondTime= sdf.format(dcsx.getPayBondTime());
        }
        if (Objects.nonNull(payFullTime) && i==1) {
            pay.setPayDate(dcsx.getPayFullTime());
            pay.setPayAmount(payAmount);
            if(payAmount.compareTo(dcsx.getTotalAmount())==0){
                pay.setPayType("A");
            }else{
                pay.setPayType("R");
            }
        }
        if (Objects.nonNull(payBondTime) && i==2) {
            if(bondAmount!=null&&bondAmount.compareTo(BigDecimal.ZERO) > 0){
                pay.setPayDate(dcsx.getPayBondTime());
                pay.setPayAmount(bondAmount);
                pay.setPayType("B");
            }
        }
        pay.setPayedAmount(Objects.nonNull(dcsx.getDealedAmount()) ? BigDecimal.ZERO : dcsx.getDealedAmount());
        pay.setCompanyName(dcsx.getCompanyName());
        pay.setStatus(dcsx.getStatus());
        pay.setEnterpriseId(dcsx.getEnterpriseId());
        pay.setApproveId(dcsx.getApproveId());
        pay.setOurCompanyName(dcsx.getOurCompanyName());
        pay.setBusinessType(dcsx.getBusinessType());
        pay.setPayMode("T");
        String ourCompanyName = dcsx.getOurCompanyName();
        if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, ourCompanyName)) {
            BsBankVo specialBank = applyChargeSalesClient.getSpecialBank(dcsx.getEnterpriseId());
            if (Objects.nonNull(specialBank)) {
                pay.setBankName(specialBank.getBankName());
                pay.setBankAccount(specialBank.getBankNum());
            }
        } else {
            BsCompanyDcsx companyDcsx = bsCompanyDcsxService.findByCompanyName(dcsx.getCompanyName());
            if (Objects.nonNull(companyDcsx)) {
                pay.setBankName(companyDcsx.getCompanyBankName());
                pay.setBankAccount(companyDcsx.getCompanyCardId());
            }
        }
        pay.setUnpayedAmount(dcsx.getUnpayedAmount());
        return pay;
    }

    /**
     * 发起代采赊销付款审批单
     *
     * @param entity
     * @param autoStartMessage
     */
    private void autoStartPayApproveAuto(ApplyCtrDCSX entity, String autoStartMessage ,int i) {
//        SCHEDULED_POOL.schedule(() -> {
//            try {
//                PmProcessSearchVo searchVo = new PmProcessSearchVo(BasConstants.PROCESS_CODE_DCSX_PAY, entity.getEnterpriseId());
//                PmProcess process = processService.findByProcessCode(searchVo);
//                // 1.构建代采赊销付款申请单所需参数
//                ApplyPay applyPay = buildPayParamAuto(entity,i);
//                if (Objects.isNull(applyPay)) {
//                    return;
//                }
//                // 2.构建代采赊销付款审批单发起所需参数
//                PmApproveSaveVo startVo = buildPmApproveStartVo(applyPay, process, entity, autoStartMessage);
//                // 3.发起代采赊销付款审批单
//                pmApproveService.startFlow(startVo);
//            } catch (Exception e) {
//                logger.error("doApplyPayTask error:{}", e.getLocalizedMessage());
//            }
//        }, 3, TimeUnit.SECONDS);
    }
}
