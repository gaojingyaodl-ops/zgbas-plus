package com.spt.bas.server.service.impl;

import cn.hutool.core.thread.ExecutorBuilder;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.logistics.service.ICtrLogisticsDeliveryService;
import com.spt.bas.server.service.*;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SMSUtils;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * <p>
 * 收货审批
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-10 14:34
 */
@Component("applyConfirmReceiptService")
public class ApplyConfirmReceiptServiceImpl extends BaseService<ApplyConfirmReceipt> implements IApplyConfrimReceiptService, IPmService, IPmApproveListener {
    ExecutorService executor = ExecutorBuilder.create()
            .setCorePoolSize(4)
            .setMaxPoolSize(10)
            .setWorkQueue(new LinkedBlockingQueue<>(50))
            .build();
    @Autowired
    private ApplyConfirmReceiptDao applyConfirmReceiptDao;
    @Autowired
    private ApplyConfirmReceiptDcsxDao applyConfirmReceiptDcsxDao;
    @Autowired
    private IApplyProductDetailService productDetailService;
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private CtrProductDao productDao;
    @Autowired
    private ICtrContractApplyService ctrContractApplyService;
    @Autowired
    private ICtrContractUpdateService ctrContractUpdateService;
    @Autowired
    private ICtrContractOphisService contractOphisService;
    @Autowired
    private ApplyDeliveryOutDao applyDeliveryOutDao;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IPmProcessService pmProcessService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private CtrContractOphisDao ctrContractOphisDao;
    @Autowired
    private PmProcessDao processDao;
    @Autowired
    private IApplyDcsxService applyDcsxService;
    @Autowired
    private IBsCompanyOurService companyOurService;
    @Resource
    private ICtrLogisticsDeliveryService logisticsDeliveryService;
    @Autowired
    private PmApproveDao pmApproveDao;

    /**
     * 发起审批
     *
     * @param approve
     */
    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyConfirmReceipt entity = applyConfirmReceiptDao.findOne(approve.getBizId());
        Long contractId = entity.getContractId();
        List<ApplyProductDetail> lstDetail = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_G);
        logger.info("applyConfirmReceiptService lstDetail:{}", JsonUtil.obj2Json(lstDetail));
        // 销售合同下游已确认收货数量
        CtrContractApplyVo vo = new CtrContractApplyVo();
        for (ApplyProductDetail apd : lstDetail) {
            CtrProduct product = productDao.findOne(apd.getCtrProductId());
            BigDecimal curConfirmReceiptNumber = BigDecimal.ZERO;
            if (product.getCurConfirmReceiptNumber() != null) {
                curConfirmReceiptNumber = product.getCurConfirmReceiptNumber();
            }
            product.setCurConfirmReceiptNumber(curConfirmReceiptNumber.add(apd.getCurNumber()));
            productDao.save(product);

            // 更新相应批次出库字段
            ApplyDeliveryOut applyDeliveryOut = applyDeliveryOutDao.findOne(apd.getApplyDeliveryOutId());
            applyDeliveryOut.setConfirmReceiptApplyId(entity.getId());
            applyDeliveryOut.setConfirmFlg(BasConstants.CONFIRM_FLG_ING);
            applyDeliveryOutDao.save(applyDeliveryOut);
        }

        vo.setApplyType(BasConstants.APPLY_TYPE_G);
        vo.setContractId(contractId);
        ctrContractApplyService.updateCtrContractApply(vo);
        contractOphisService.addHis(BasConstants.APPLY_TYPE_G, contractId, approve, entity.getDeliveryDateTo());

    }

    /**
     * 执行审批步骤
     *
     * @param approve
     * @param nextStep
     */
    @Override
    @ServerTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyConfirmReceipt entity = applyConfirmReceiptDao.findOne(approve.getBizId());
            List<ApplyProductDetail> lstDetail = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_G);
            Long contractId = entity.getContractId();
            CtrContract contract = ctrContractDao.findOne(contractId);
            //当前实际确认收货数量
            BigDecimal curRealConfirmReceiptNumber = BigDecimal.ZERO;
            Date confirmReceiptDate = entity.getConfirmReceiptDate();
            for (ApplyProductDetail apd : lstDetail) {
                if (apd.getCurNumber().compareTo(BigDecimal.ZERO) > 0) {
                    CtrProduct product = productDao.findOne(apd.getCtrProductId());
                    BigDecimal confirmReceiptNumber = product.getConfirmReceiptNumber() == null ? BigDecimal.ZERO : product.getConfirmReceiptNumber();
                    product.setConfirmReceiptNumber(confirmReceiptNumber.add(apd.getCurNumber()));
                    curRealConfirmReceiptNumber = apd.getCurNumber();
                    // 审批结束，更新当前审批数量为0
                    product.setCurConfirmReceiptNumber(BigDecimal.ZERO);
                    productDao.save(product);

                    // 更新相应批次出库字段
                    ApplyDeliveryOut applyDeliveryOut = applyDeliveryOutDao.findOne(apd.getApplyDeliveryOutId());
                    applyDeliveryOut.setConfirmFlg(BasConstants.CONFIRM_FLG_YES);

                    applyDeliveryOutDao.save(applyDeliveryOut);
                }
            }
            // 更新确认收货数量
            ctrContractUpdateService.addConfirmReceiptNumber(contractId, curRealConfirmReceiptNumber, confirmReceiptDate, approve.getApproveNo());

            // 更新损耗数据，更新整个链条
            ctrContractUpdateService.refreshContractWithLossNumber(contract, entity.getLossNumber(), entity.getLossType());

            // 计算约定付款日期
            Date targetTime = ctrContractUpdateService.refreshAppointPayFullTimeWithReceipt(contract, entity);

            // 更新该批对应的约定收货日期
            entity.setActualContractPayFullTime(targetTime);
            this.save(entity);

            // 自动发起中游确认收货
            this.autoStartReceiptDcsx(contract, entity, lstDetail);
            
            // 更新合同保理资料收集状态
            ctrContractUpdateService.refreshFactorStatus(contractId);

            // 签署出库单据
            this.signApplyConfirmReceipt(entity, contract, approve);

            // 确认收货邮件通知
            this.notifyEmail(approve, contract);
        }
    }

    /**
     * 自动发起中游确认收货
     * @param contract
     * @param entity
     * @param lstDetail
     * @throws ApplicationException
     */
    private void autoStartReceiptDcsx(CtrContract contract, ApplyConfirmReceipt entity, List<ApplyProductDetail> lstDetail) throws ApplicationException {
        if (getConfirmReceiptFlg(contract.getApproveId())) {
            ApplyDeliveryOut deliveryOut = applyDeliveryOutDao.findByConfirmReceiptApplyId(entity.getId());
            if (StringUtils.equals(BasConstants.CONFIRM_FLG_NOT, deliveryOut.getConfirmDcsxFlg()) || StringUtils.isBlank(deliveryOut.getConfirmDcsxFlg())) {
                for (ApplyProductDetail productDetail : lstDetail) {
                    productDetail.setApplyDeliveryOutId(deliveryOut.getId());
                }
                autoApplyConfirmReceiptDcsx(contract, deliveryOut, lstDetail, entity.getConfirmReceiptDate());
            }
        }
    }

    /**
     * 确认收货邮件通知
     * @param approve
     * @param ctrContract
     */
    private void notifyEmail(PmApprove approve, CtrContract ctrContract) {
        executor.execute(() -> {
            try {
                SysUserSdk sysUser = authOpenFacade.findUserById(approve.getCreateUserId());
                String email = sysUser.getEmail();
                String contractNo = ctrContract.getContractNo();
                String companyName = ctrContract.getCompanyName();
                String createUserName = approve.getCreateUserName();
                SMSUtils.sendConfirmReceiptEmailNotification(contractNo, createUserName, companyName, email);
            } catch (Exception e) {
                logger.error("notifyEmail error", e);
            }
        });
    }

    /**
     *  是否发起中游自动确认收货
     * @param approveId
     * @return
     */
    public Boolean getConfirmReceiptFlg(Long approveId) {
        ApplyCtrDCSX applyCtrDCSX = applyDcsxService.findByDCSXApproveId(approveId);
        BigDecimal confirmReceiveNumber = null;
        if(Objects.nonNull(applyCtrDCSX)) {
            // 是否已手动发起
            // 中游已确认收货数量
            confirmReceiveNumber = applyCtrDCSX.getConfirmReceiveNumber();
            BigDecimal totalNumber = applyCtrDCSX.getTotalNumber();
            //  已确认收货数量小于合同数量
            if(confirmReceiveNumber != null && totalNumber != null) {
                if(confirmReceiveNumber.compareTo(totalNumber) >= 0){
                    return false;
                }
            }
            // 判断我方是否处于中游
            BsCompanyOur companyOur = companyOurService.findByCompanyName(applyCtrDCSX.getCompanyName());
            if(Objects.nonNull(companyOur) && !companyOur.getOurCompanyFlag()) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 自动发起中游确认收货申请
     * @param contract
     * @param entity
     * @param lstDetail
     * @throws ApplicationException
     */
    public void autoApplyConfirmReceiptDcsx(CtrContract contract,ApplyDeliveryOut entity,List<ApplyProductDetail> lstDetail, Date confirmReceiptDate) throws ApplicationException {
        ApplyCtrDCSX applyCtrDCSX = applyDcsxService.findByDCSXApproveId(contract.getApproveId());
        PmApprove pmApprove = pmApproveService.findApproveNoByApproveId(entity.getApproveId());
        // 自动发起中游确认收货
        PmProcess process = processDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_APPLY_CONFIRM_RECEIPT_DCSX, contract.getEnterpriseId());
        ApplyConfirmReceiptDcsxVo applyConfirmReceiptDcsxVo = new ApplyConfirmReceiptDcsxVo();
        lstDetail.forEach(d -> {
            d.setDealNumber(applyCtrDCSX.getTotalNumber());
            d.setCurNumber(applyCtrDCSX.getTotalNumber());
            d.setTotalPrice(d.getDealNumber().multiply(d.getDealPrice()).setScale(2, RoundingMode.HALF_UP));
        });
        applyConfirmReceiptDcsxVo.setLstInsert(lstDetail);
        List<ApplyProductDetail> lstList = new ArrayList<>();
        applyConfirmReceiptDcsxVo.setLstUpdate(lstList);
        applyConfirmReceiptDcsxVo.setLstDelete(lstList);

        applyConfirmReceiptDcsxVo.setId(0L);
        applyConfirmReceiptDcsxVo.setDeliveryOutId(entity.getId());
        applyConfirmReceiptDcsxVo.setContractId(applyCtrDCSX.getId());
        applyConfirmReceiptDcsxVo.setContractNo(applyCtrDCSX.getContractNo());
        applyConfirmReceiptDcsxVo.setOurCompanyName(applyCtrDCSX.getCompanyName());
        applyConfirmReceiptDcsxVo.setCompanyId(applyCtrDCSX.getCompanyId());
        applyConfirmReceiptDcsxVo.setCompanyName(applyCtrDCSX.getOurCompanyName());
        applyConfirmReceiptDcsxVo.setConfirmReceiptDate(confirmReceiptDate);
        applyConfirmReceiptDcsxVo.setLogisticsCosts("0");
        applyConfirmReceiptDcsxVo.setDeliveryAddr(entity.getDeliveryAddr());
        applyConfirmReceiptDcsxVo.setPlateNumber(entity.getPlateNumber());
        applyConfirmReceiptDcsxVo.setContactName(entity.getContactName());
        applyConfirmReceiptDcsxVo.setContactPhone(entity.getContactPhone());
        applyConfirmReceiptDcsxVo.setDriverName(entity.getDriverName());
        applyConfirmReceiptDcsxVo.setDriverCardNo(entity.getDriverCardNo());
        applyConfirmReceiptDcsxVo.setRemark(entity.getRemark());

        String templateCd = BsDictUtil.getValue(entity.getEnterpriseId(), BasConstants.GOOD_RECEIVE_TEMPLATE, applyConfirmReceiptDcsxVo.getOurCompanyName());
        if (StringUtils.isBlank(templateCd)) {
            logger.error(applyConfirmReceiptDcsxVo.getOurCompanyName() + "签收单模板缺失!");
        } else {
            String entityJson = JsonUtil.obj2Json(applyConfirmReceiptDcsxVo);
            PmApproveSaveVo startVo = new PmApproveSaveVo();
            startVo.setBizEntityJson(entityJson);
            startVo.setProcessId(process.getId());
            startVo.setDeptId(pmApprove.getDeptId());
            startVo.setMode(BasConstants.APPROVE_STATUS_A);
            startVo.setStatus(BasConstants.APPROVE_STATUS_A);
            startVo.setApproveId(0L);
            startVo.setUserId(pmApprove.getCreateUserId());
            startVo.setUserName(pmApprove.getCreateUserName());
            startVo.setEnterpriseId(pmApprove.getEnterpriseId());
            startVo.setAutoStartMessage("自动发起中游确认收货");
            pmApproveService.startFlow(startVo);
        }

    }

    /**
     * 审批驳回
     *
     * @param approve
     * @param nextStep
     */
    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyConfirmReceipt entity = applyConfirmReceiptDao.findOne(approve.getBizId());
        List<ApplyProductDetail> lstDetail = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_G);
        Long contractId = entity.getContractId();
        List<CtrProduct> productList = productDao.findByCtrContractId(contractId);
        for (CtrProduct product : productList) {
            //审批结束，更新当前审批数量为0
            product.setCurConfirmReceiptNumber(BigDecimal.ZERO);
            productDao.save(product);

        }
        for (ApplyProductDetail apd : lstDetail) {
            // 更新相应批次出库字段
            ApplyDeliveryOut applyDeliveryOut = applyDeliveryOutDao.findOne(apd.getApplyDeliveryOutId());
            applyDeliveryOut.setConfirmFlg(BasConstants.CONFIRM_FLG_NOT);
            applyDeliveryOut.setConfirmReceiptApplyId(null);
            applyDeliveryOutDao.save(applyDeliveryOut);
        }
    }

    /**
     * 审批撤回
     *
     * @param vo
     */
    @Override
    @ServerTransactional
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        ApplyConfirmReceipt entity = applyConfirmReceiptDao.findOne(vo.getBizId());
        List<ApplyProductDetail> lstDetail = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_G);
        CtrContract ctrContract = ctrContractDao.findOne(entity.getContractId());

        // 1.更新合同表确认收货数据
        BigDecimal confirmReceiveNum = lstDetail.stream().map(ApplyProductDetail::getCurNumber).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal realConfirmReceiveNumber = ctrContract.getConfirmReceiveNumber().subtract(confirmReceiveNum);
        ctrContract.setConfirmReceiptFlg(false);
        ctrContract.setConfirmReceiveNumber(realConfirmReceiveNumber.compareTo(BigDecimal.ZERO) > 0 ? realConfirmReceiveNumber : BigDecimal.ZERO);
        ctrContract.setContractStatusWx(BasConstants.CONTRACT_STATUS_W);
        ctrContract.setConfirmDate(null);
        ctrContractDao.save(ctrContract);

        // 3.合同操作记录里面把对应的收货确认记录删掉
        ctrContractOphisDao.deleteContractOphis(entity.getApproveId(), entity.getContractId());

        ApplyDeliveryOut applyDeliveryOut = applyDeliveryOutDao.findByConfirmReceiptApplyId(entity.getId());
        if(Objects.nonNull(applyDeliveryOut)) {
            // 2.更新出库申请表数据
            applyDeliveryOutDao.invalidConfirmReceive(applyDeliveryOut.getId());

            ApplyConfirmReceiptDcsx confirmReceiptDcsx = applyConfirmReceiptDcsxDao.findOne(applyDeliveryOut.getConfirmReceiptDcsxApplyId());

            if(Objects.nonNull(confirmReceiptDcsx)) {
                // 同步作废代采赊销确认收货
                pmApproveService.doWithdraw(assembleWithdrawVo(vo, confirmReceiptDcsx.getApproveId()));
            }
        }
        ctrContractUpdateService.refreshContractWithLossNumber(ctrContract, entity.getLossNumber().negate(), entity.getLossType());
    }

    private PmApproveWithdrawVo assembleWithdrawVo(PmApproveWithdrawVo withdrawVo, Long invalidApproveId) {
        PmApproveWithdrawVo vo = new PmApproveWithdrawVo();
        PmApprove invalidApprove = pmApproveDao.findOne(invalidApproveId);
        vo.setBizId(invalidApprove.getBizId());
        vo.setUserId(withdrawVo.getUserId());
        vo.setUserName(withdrawVo.getUserName());
        vo.setApproveId(invalidApprove.getId());
        return vo;
    }

    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        ApplyConfirmReceipt applyConfirmReceipt;
        ApplyProductDetailSaveVo saveVo = new ApplyProductDetailSaveVo();
        saveVo.setApplyType(BasConstants.APPLY_TYPE_G);
        if (pmEntity instanceof ApplyConfirmReceiptVo) {
            ApplyConfirmReceiptVo vo = (ApplyConfirmReceiptVo) pmEntity;
            applyConfirmReceipt = new ApplyConfirmReceipt();
            BeanUtils.copyProperties(vo, applyConfirmReceipt);

            if (applyConfirmReceipt.getId() == 0) {
                //生成合同号
                String applyNo = composeContractNo(applyConfirmReceipt.getContractNo());
                applyConfirmReceipt.setApplyNo(applyNo);
            }
            ///收货确认申请选着货品时没有可选着库存信息
            for (ApplyProductDetail detail : vo.getLstInsert()) {
                if (!checkIsExistDelivery(detail.getApplyDeliveryOutId())) {
                    throw new ApplicationException("出库校验出错，请重新选择可确认收货库存信息");
                }
            }

            if(applyConfirmReceipt.getContractId()==null){
                 CtrContract byContractNo = ctrContractService.findByContractNo(applyConfirmReceipt.getContractNo());
                applyConfirmReceipt.setContractId(byContractNo.getId());
            }

             CtrContract contract = ctrContractService.getEntity(applyConfirmReceipt.getContractId());
            // 付款单设置businessType 作为流程条件内容
            if (StringUtils.equals(contract.getBusinessTypeDcsx(), BasConstants.BUSINESS_TYPE_DCSX)) {
                applyConfirmReceipt.setBusinessType(BasConstants.BUSINESS_TYPE_DCSX);
            }

            applyConfirmReceipt = applyConfirmReceiptDao.save(applyConfirmReceipt);
            saveVo.setApplyId(applyConfirmReceipt.getId());
            productDetailService.saveDetailBatch(vo.getLstInsert(), vo.getLstUpdate(), vo.getLstDelete(), saveVo);
        } else {
            ApplyConfirmReceipt entity = (ApplyConfirmReceipt) pmEntity;
            CtrContract contract = ctrContractService.getEntity(entity.getContractId());
            // 付款单设置businessType 作为流程条件内容
            if (StringUtils.equals(contract.getBusinessTypeDcsx(), BasConstants.BUSINESS_TYPE_DCSX)) {
                entity.setBusinessType(BasConstants.BUSINESS_TYPE_DCSX);
            }
            applyConfirmReceipt = applyConfirmReceiptDao.save(entity);
            saveVo.setApplyId(entity.getId());
            productDetailService.saveBatchEnterpriseId(saveVo);
        }
        return applyConfirmReceipt;
    }

    /**
     * 校验是否出库是否正常
     *
     * @param applyDeliveryOutId
     *
     * @return false 不正常 true 正常
     */
    private Boolean checkIsExistDelivery(Long applyDeliveryOutId) {
        ApplyDeliveryOut entity = applyDeliveryOutDao.findOne(applyDeliveryOutId);
        if (entity == null
                || !BasConstants.CONFIRM_FLG_NOT.equals(entity.getConfirmFlg())
                || !BasConstants.APPROVE_STATUS_D.equals(entity.getStatus())) {
            return false;
        }
        return true;
    }

    private String composeContractNo(String contractNo) {
        List<ApplyConfirmReceipt> list = applyConfirmReceiptDao.findByContractNo(contractNo);
        String fmt = String.format("%02d", list.size() + 1);
        return contractNo + BasConstants.APPLY_TYPE_G + fmt;
    }

    /**
     * 标题
     *
     * @param pmEntity
     */
    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyConfirmReceipt vo = (ApplyConfirmReceipt) pmEntity;
            List<ApplyProductDetail> list = productDetailService.findApplyDetail(vo.getId(), BasConstants.APPLY_TYPE_G);
            StringBuffer productNameAndBrand = new StringBuffer("");
            BigDecimal sumNumber = BigDecimal.ZERO;
            for (ApplyProductDetail applyProductDetail : list) {
                String realOutNumber = NumberUtil.formatNumber(applyProductDetail.getCurNumber(), "#.###");
                String[] title = applyProductDetail.getProductCd().split("_");
                if (title[0].equals("SL")) {
                    productNameAndBrand.append(applyProductDetail.getProductName() + "/" + applyProductDetail.getBrandNumber() + "/" + realOutNumber + RuleUtil.weightUnit);
                } else {
                    productNameAndBrand.append(applyProductDetail.getProductName() + "/" + realOutNumber + RuleUtil.weightUnit);
                }
                sumNumber = sumNumber.add(applyProductDetail.getDealNumber());
            }
            String productNameAndBrandStr = productNameAndBrand.toString();
            if (productNameAndBrand.length() > 0) {
                productNameAndBrandStr = productNameAndBrand.substring(0, productNameAndBrand.length() - 1);
            }
            String companyName = RuleUtil.companyNameSubString(vo.getCompanyName());
            String sumNumberStr = NumberUtil.formatNumber(sumNumber, "#.###");
            String subject = SubjectUtil.formatSubject(vo.getContractNo(),productNameAndBrandStr,companyName,sumNumberStr+ RuleUtil.weightUnit);
            return subject;
        }
        return null;
    }

    /**
     * 获取业务员id
     *
     * @param pmEntity
     */
    @Override
    public Long getMatchUserId(IPmEntity pmEntity) {
        return null;
    }

    @Override
    public BaseDao<ApplyConfirmReceipt> getBaseDao() {
        return applyConfirmReceiptDao;
    }


    @Override
    @ServerTransactional
    public void applyConfirmReceipt(ApplyConfirmReceiptVo confirmReceiptVo) throws ApplicationException {
        try {
            if (confirmReceiptVo.getConfirmReceiptDate() == null) {
                throw new ApplicationException("实际到货日期不能为NULL!");
            }
            //获取审批对象
            PmApproveSaveVo startVo = new PmApproveSaveVo();
            startVo.setMode(BasConstants.APPROVE_STATUS_A);
            startVo.setStatus(BasConstants.APPROVE_STATUS_A);
            startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            confirmReceiptVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);

            //获取表单字段
            List<ApplyProductDetailVo> contentJSON = confirmReceiptVo.getProductJSON();

            //封装
            confirmReceiptVo.setBatchSub(contentJSON, confirmReceiptVo.getLstUpdate(), confirmReceiptVo.getLstDelete());

            //获取流程对象
            PmProcessSearchVo searchVo = new PmProcessSearchVo();
            searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            searchVo.setProcessCode(BasConstants.PROCESS_APPLY_CONFIRM_RECEIPT);

            //根据流程对象获取流程主表
            PmProcess process = pmProcessService.findByProcessCode(searchVo);
            if (process == null) {
                throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录！");
            }

            //根据业务员id获取用户信息
            //SysUser userById = adminOpenFacade.findUserById(confirmReceiptVo.getApplyUserId());
            SysUserSdk userById = authOpenFacade.findUserById(confirmReceiptVo.getApplyUserId());
            if (userById != null) {
                //审批对象获取当前操作人信息 和流程id
                startVo.setUserId(userById.getUserId());
                startVo.setUserName(userById.getNickName());
                startVo.setProcessId(process.getId());
                startVo.setApproveId(0L);
                confirmReceiptVo.setApproveId(0L);
            }
            startVo.setBizEntityJson(JsonUtil.obj2Json(confirmReceiptVo));
            //审批发起方法
            pmApproveService.startFlow(startVo);
        } catch (ApplicationException e) {
            throw new ApplicationException(e);
        }
    }

    /**
     * 签署出库单据
     *
     * @param entity
     * @param contract
     * @param approve
     * @return
     */
    private void signApplyConfirmReceipt(ApplyConfirmReceipt entity, CtrContract contract, PmApprove approve) throws ApplicationException {
        Long logisticsFileId = entity.getLogisticsFileId();
        if (Objects.isNull(logisticsFileId)) {
            return;
        }
        String ourCompanyName = contract.getOurCompanyName();
        String sealType = BasConstants.CFCA_SEAL_TYPE.SEAL_TYPE_LGS;
        Long approveId = approve.getId();
        String approveNo = approve.getApproveNo();
        CtrLogisticsFile logisticsFile = logisticsDeliveryService.successLogisticsPdfFile(ourCompanyName, sealType, logisticsFileId, approveId, approveNo);
        if (Objects.nonNull(logisticsFile) && Boolean.TRUE.equals(logisticsFile.getSignFlg())) {
            String fileId = entity.getFileId();
            entity.setFileId(fileId.replace(logisticsFile.getOldFileId(), logisticsFile.getFileId()));
            this.saveEntity(entity);
        }
    }

    @Override
    public List<ApplyConfirmReceipt> findByContractId(Long contractId) {
        return applyConfirmReceiptDao.findByContractId(contractId);
    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        applyConfirmReceiptDao.updateFileId(id, fileId);
    }

    @Override
    public List<Long> findContractIdByDate(Date beginDate, Date endDate) {
        if (Objects.nonNull(beginDate) && Objects.nonNull(endDate)) {
            return applyConfirmReceiptDao.findContractIdByDate(beginDate, endDate);
        }
        if (Objects.nonNull(beginDate)) {
            return applyConfirmReceiptDao.findContractIdByBeginDate(beginDate);
        }
        if (Objects.nonNull(endDate)) {
            return applyConfirmReceiptDao.findContractIdByEndDate(endDate);
        }
        return null;
    }

    @Override
    @ServerTransactional
    public void doSignLogistics() {
        List<ApplyConfirmReceipt> unSignLogistics = applyConfirmReceiptDao.findUnSignLogistics();
        if (CollectionUtils.isEmpty(unSignLogistics)) {
            return;
        }
        logger.info("confirm doSignLogistics size:{}", unSignLogistics.size());
        Map<Long, CtrContract> contractMap = ctrContractDao.findByIds(unSignLogistics.stream()
                        .map(ApplyConfirmReceipt::getContractId)
                        .collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(CtrContract::getId, c -> c));
        Map<Long, PmApprove> approveMap = pmApproveDao.findByIds(unSignLogistics.stream()
                        .map(ApplyConfirmReceipt::getApproveId)
                        .collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(PmApprove::getId, p -> p));
        for (ApplyConfirmReceipt entity : unSignLogistics) {
            try {
                this.signApplyConfirmReceipt(entity, contractMap.get(entity.getContractId()), approveMap.get(entity.getApproveId()));
            } catch (Exception e) {
                logger.error("doSignLogistics error", e);
            }
        }
        logger.info("confirm doSignLogistics success!");
    }

    @Override
    public Date findMaxConfirmDate(Long contractId) {
        return applyConfirmReceiptDao.findMaxConfirmDate(contractId);
    }
}
