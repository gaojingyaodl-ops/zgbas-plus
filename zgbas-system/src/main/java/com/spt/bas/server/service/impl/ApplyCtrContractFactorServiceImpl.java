package com.spt.bas.server.service.impl;


import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrContractFactor;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.dao.ApplyCtrContractFactoDao;
import com.spt.bas.server.dao.ApplyPayDao;
import com.spt.bas.server.service.IApplyCtrContractFactoService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.service.impl.PmApproveServiceImpl;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 *保理扩展
 * </p>
 *
 */
@Component("applyCtrContractFactorService")
public class ApplyCtrContractFactorServiceImpl extends BaseService<ApplyCtrContractFactor>implements IApplyCtrContractFactoService, IPmApproveListener,IPmService {
    private static final ScheduledExecutorService SCHEDULED_POOL = Executors.newScheduledThreadPool(10);

    @Autowired
    private ApplyCtrContractFactoDao applyCtrContractFactoDao;

    @Autowired
    private ICtrContractService ctrContractService;

    @Autowired
    private IPmProcessService iPmProcessService;

    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Autowired
    private PmApproveServiceImpl approveService;

    @Autowired
    private ApplyPayDao applyPayDao;

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {

    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        return null;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess pmProcess) {
        return null;
    }

    @Override
    public BaseDao<ApplyCtrContractFactor> getBaseDao() {
        return null;
    }



    @Override
    public ApplyCtrContractFactor findByApproveId(Long approveid) {
        return applyCtrContractFactoDao.findByApproveId(approveid);
    }

    @Override
    public ApplyCtrContractFactor findByContractNo(String contrcact) {
        return applyCtrContractFactoDao.findByContractNo(contrcact);
    }

    @Override
    public void updateFacto(String status,BigDecimal londamount, Date londDate, String contrcact) {
        applyCtrContractFactoDao.updateFacto(status, londamount, londDate, contrcact);
    }

    @Override
    public void updateStatusByContractNo(String contractNo,String factorStatus) {
        applyCtrContractFactoDao.updateStatusByContractNo(contractNo,factorStatus);
    }

    /**
     *   计算方式调整2021 原：
     *             销售合同30万以下，合同金额 * 85%后，得到保理金额，金额保留万位（向下取整），然后乘以20%
     *             销售合同30万及以上，合同金额 * 90%后，得到保理金额，金额保留万位，然后乘以20%
     *             计算公式：保证金额 = (合同金额 X 0.85) 向下取整到万位 X 0.2
     *   计算方式调整2022-09-28 现：
     *            计算公式：保证金额 = (合同金额 X 0.9) 向下取整到万位 X 0.1
     * @param contractNo
     */
    @Override
    public void autoLaunchApplyPay(String contractNo) {
        if (Objects.isNull(contractNo)) {
            return;
        }
        logger.info("autoLaunchApplyPay contract:{}",contractNo);
        SCHEDULED_POOL.schedule(()->{
            CtrContract contract = ctrContractService.findByContractNo(contractNo);
            Long factorPayCount = applyPayDao.getFactorPayCount(contractNo);
            if (factorPayCount > 0L){
                logger.warn("autoLaunchApplyPay exist!");
                return;
            }
            BigDecimal totalAmount = contract.getTotalAmount();
//          BigDecimal rate = new BigDecimal("0.85");
            BigDecimal bigDecimal = new BigDecimal("0.9");
            BigDecimal realFactorAmount;
            BigDecimal factorAmount = totalAmount.multiply(bigDecimal);
            BigDecimal divide = factorAmount.divide(new BigDecimal(10000),2, RoundingMode.HALF_UP);
            BigDecimal decimalAmount = divide.setScale(0, RoundingMode.DOWN).multiply(new BigDecimal(10000)).multiply(new BigDecimal("0.1"));
            realFactorAmount = decimalAmount.setScale(0, RoundingMode.HALF_DOWN);

//            if (BasConstants.FACTOR_CONTRACT_AMOUNT.compareTo(totalAmount) > 0) {
//                //保理金额
//                BigDecimal factorAmount = totalAmount.multiply(rate);
//                BigDecimal divide = factorAmount.divide(new BigDecimal(10000),2, RoundingMode.HALF_UP);
//                BigDecimal decimalAmount = divide.setScale(0, RoundingMode.DOWN).multiply(new BigDecimal(10000)).multiply(new BigDecimal("0.2"));
//                realFactorAmount = decimalAmount.setScale(0, RoundingMode.HALF_DOWN);
//            } else {
//                BigDecimal factorAmount = totalAmount.multiply(bigDecimal);
//                BigDecimal divide = factorAmount.divide(new BigDecimal(10000),2, RoundingMode.HALF_UP);
//                BigDecimal decimalAmount = divide.setScale(0, RoundingMode.DOWN).multiply(new BigDecimal(10000)).multiply(new BigDecimal("0.2"));
//                realFactorAmount = decimalAmount.setScale(0, RoundingMode.HALF_DOWN);
//            }

            if (judgeFileId(contract) && realFactorAmount.compareTo(BigDecimal.ZERO) > 0 && BasConstants.FACTOR_STATUS_Z.equals(contract.getFactorStatus())) {
                try {
                    PmApproveSaveVo startVo = new PmApproveSaveVo();
                    ApplyPay pay = new ApplyPay();
                    pay.setId(contract.getId());
                    pay.setPayAmount(realFactorAmount);
                    pay.setFactorAmount(realFactorAmount);
                    pay.setContractId(contract.getId());
                    pay.setContractNo(contract.getContractNo());
                    pay.setTotalAmount(contract.getTotalAmount());
                    pay.setCompanyName("余姚市应收账款债权管理有限公司");
                    pay.setBankName("宁波余姚农村商业银行营业部");
                    pay.setBankAccount("201000189769286");
                    pay.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
                    pay.setApproveId(contract.getApproveId());
                    pay.setOurCompanyName(contract.getOurCompanyName());
                    pay.setPayDate(new Date());
                    pay.setPayMode("T");
                    pay.setPayType("A");
                    String bizEntityJson = JsonUtil.obj2Json(pay);
                    startVo.setMode(BasConstants.APPROVE_STATUS_A);
                    startVo.setStatus(BasConstants.APPROVE_STATUS_A);
                    startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
                    PmProcessSearchVo searchVo = new PmProcessSearchVo();
                    searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
                    searchVo.setProcessCode(BasConstants.DEPOSIT_PAYMENT_DCSXBL);
                    PmProcess process = iPmProcessService.findByProcessCode(searchVo);
                    String value = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.Factoring_Repayment_Originator, BasConstants.Factoring_Repayment_Originator_User);
                    //SysUser userById = adminOpenFacade.findUserById(new Long(value));
                    SysUserSdk userById = authOpenFacade.findUserById(new Long(value));
                    startVo.setUserId(userById.getUserId());
                    startVo.setUserName(userById.getNickName());
                    startVo.setProcessId(process.getId());
                    startVo.setApproveId(0L);
                    startVo.setAutoStartMessage("资料已收集，自动发起20%保证金付款");
                    startVo.setBizEntityJson(bizEntityJson);

                    approveService.startFlow(startVo);
                } catch (ApplicationException e) {
                    logger.error("保理预算自动发起 20%保证金付款失败!", e);
                }
            }
        },2, TimeUnit.SECONDS);
    }

    private Boolean judgeFileId(CtrContract contract){
        if(Objects.isNull(contract)){
            return false;
        }
        boolean invoiceFileId = StringUtils.isNotBlank(contract.getInvoiceFileId());
        boolean debtCertificateFileId = StringUtils.isNotBlank(contract.getDebtCertificateFileId());
        boolean goodsFileId = StringUtils.isNotBlank(contract.getGoodsFileId());
        return invoiceFileId && debtCertificateFileId && goodsFileId;
    }


}
