package com.spt.bas.server.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.BsDictConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.ApplyConfirmReceiptDcsxVo;
import com.spt.bas.client.vo.ApplyProductDetailSaveVo;
import com.spt.bas.client.vo.ApplyProductDetailVo;
import com.spt.bas.client.vo.CtrContractApplyVo;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.*;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.vo.*;
import com.spt.sign.client.remote.ICfcaLogisticsClient;
import com.spt.sign.client.remote.ICfcaSignClient;
import com.spt.sign.client.vo.AxqContractVo;
import com.spt.sign.client.vo.AxqLogisticsVo;
import com.spt.sign.client.vo.CtrProductVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component("applyConfirmReceiptDcsxService")
public class ApplyConfirmReceiptDcsxServiceImpl extends BaseService<ApplyConfirmReceiptDcsx> implements IApplyConfrimReceiptDcsxService, IPmService, IPmApproveListener {
    @Autowired
    private ApplyConfirmReceiptDcsxDao applyConfirmReceiptDcsxDao;
    @Autowired
    private IApplyProductDetailService productDetailService;
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private CtrProductDao productDao;
    @Autowired
    private ICtrContractDcsxApplyService ctrContractDcsxApplyService;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private ApplyDeliveryOutDao applyDeliveryOutDao;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IPmProcessService pmProcessService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Resource
    private ICfcaSignClient cfcaSignClient;
    @Resource
    private ICfcaLogisticsClient cfcaLogisticsClient;
    @Resource
    private ICtrContractOphisService contractOphisService;
    @Resource
    private CtrContractOphisDao ctrContractOphisDao;
    @Resource
    private ApplyDcsxDao applyDcsxDao;
    @Resource
    private IApplyDcsxService applyDcsxService;
    @Autowired
    private PmProcessDao processDao;
    @Autowired
    private IBsCompanyOurService companyOurService;
    @Autowired
    private CtrOutInLedgerDao ctrOutInLedgerDao;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Resource
    private CtrLogisticsDao ctrLogisticsDao;
    /**
     * 发起审批
     *
     * @param approve
     */
    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyConfirmReceiptDcsx entity = applyConfirmReceiptDcsxDao.findOne(approve.getBizId());
        Long contractId = entity.getContractId();
        List<ApplyProductDetail> lstDetail = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_Z);
        CtrContractApplyVo vo = new CtrContractApplyVo();
        BigDecimal curNumber = BigDecimal.ZERO;
        for (ApplyProductDetail apd : lstDetail) {
            CtrProduct product = productDao.findOne(apd.getCtrProductId());
            BigDecimal curConfirmReceiptDcsxNumber = BigDecimal.ZERO;
            if (product.getCurConfirmReceiptNumber() != null) {
                curConfirmReceiptDcsxNumber = product.getCurConfirmReceiptNumber();
            }
            product.setCurConfirmReceiptNumber(curConfirmReceiptDcsxNumber.add(apd.getCurNumber()));
            curNumber = curNumber.add(apd.getCurNumber());
            productDao.save(product);

            // 更新相应批次出库字段
            ApplyDeliveryOut applyDeliveryOut = applyDeliveryOutDao.findOne(apd.getApplyDeliveryOutId());
            applyDeliveryOut.setConfirmReceiptDcsxApplyId(entity.getId());
            applyDeliveryOut.setConfirmDcsxFlg(BasConstants.CONFIRM_FLG_ING);
            BigDecimal confirmDcsxNumber = applyDeliveryOut.getConfirmDcsxNumber();
            if(confirmDcsxNumber == null) {
                confirmDcsxNumber = BigDecimal.ZERO;
            }
            applyDeliveryOut.setConfirmDcsxNumber(confirmDcsxNumber.add(apd.getCurNumber()));
            applyDeliveryOutDao.save(applyDeliveryOut);
        }

        vo.setApplyType(BasConstants.APPLY_TYPE_Z);
        vo.setContractId(contractId);
        vo.setRealDate(entity.getConfirmReceiptDate());
        vo.setDealAmount(curNumber);
        ctrContractDcsxApplyService.updateCtrContractApply(vo);
        ApplyCtrDCSX ctrDcsx = applyDcsxDao.findOne(entity.getContractId());
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, ctrDcsx.getBusinessType())) {
            contractOphisService.addHisDcTp(BasConstants.APPLY_TYPE_Z, ctrDcsx.getStatus(), entity.getContractId(), approve, new Date());
        } else {
            contractOphisService.addHisDcsx(BasConstants.APPLY_TYPE_Z, ctrDcsx.getStatus(), entity.getContractId(), approve, new Date());
        }

        // 生成中游确认收货单
        generateSignature(entity, lstDetail);

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
            ApplyConfirmReceiptDcsx entity = applyConfirmReceiptDcsxDao.findOne(approve.getBizId());
            List<ApplyProductDetail> lstDetail = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_Z);
            // 完成时判断是否上传附件
            if (StringUtils.isEmpty(entity.getFileId())) {
                entity = generateSignature(entity, lstDetail);
                if (StringUtils.isEmpty(entity.getFileId())){
                    throw new ApplicationException("请完成电子签章或上传签章后的文件");
                }
            }
            Long contractId = entity.getContractId();
            ApplyCtrDCSX applyCtrDCSX = applyDcsxDao.findOne(contractId);
            
            if (applyCtrDCSX.getApplyCancelFlg()) {
                throw new ApplicationException("请驳回，该合同处于合同作废阶段!");
            }
            if (entity.getConfirmReceiptDate() == null) {
                throw new ApplicationException("请驳回，实际到货日期不能为空!");
            }
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
                    applyDeliveryOut.setConfirmDcsxFlg(BasConstants.CONFIRM_FLG_YES);

                    applyDeliveryOutDao.save(applyDeliveryOut);
                }
            }
            
            // 修改已确认收货数量
            applyDcsxService.addConfirmReceiptNumber(contractId, curRealConfirmReceiptNumber, confirmReceiptDate, approve.getApproveNo());
            
            // 2.保存合同操作流水记录
            ApplyCtrDCSX ctrDcsx = applyDcsxDao.findOne(entity.getContractId());
            PmApprove pmApprove = pmApproveService.getEntity(entity.getApproveId());

            // 适配历史审批中的审批单添加操作记录：过段时间可以去除
            Date createdDate = approve.getCreatedDate();
            if (createdDate != null) {
                String format = DateUtil.format(createdDate, "yyyy-MM-dd");
                if (BasConstants.NEW_ADD_HIS_START_DATE.compareTo(format) >= 0) {
                    // 添加合同操作记录
                    if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, ctrDcsx.getBusinessType())) {
                        contractOphisService.addHisDcTp(BasConstants.APPLY_TYPE_Z, ctrDcsx.getStatus(), entity.getContractId(), pmApprove, new Date());
                    } else {
                        contractOphisService.addHisDcsx(BasConstants.APPLY_TYPE_Z, ctrDcsx.getStatus(), entity.getContractId(), pmApprove, new Date());
                    }
                }
            }

            // 中游确认收货回调更新
            signatureComplete(entity);
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
        ApplyConfirmReceiptDcsx entity = applyConfirmReceiptDcsxDao.findOne(approve.getBizId());
        List<ApplyProductDetail> lstDetail = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_Z);
        Long contractId = entity.getContractId();
        List<CtrProduct> productList = productDao.findByCtrContractId(contractId);
        for (CtrProduct product : productList) {
            //审批结束，更新当前审批数量为0
            product.setCurConfirmReceiptNumber(BigDecimal.ZERO);
            productDao.save(product);

        }
        BigDecimal curNumber = BigDecimal.ZERO;
        for (ApplyProductDetail apd : lstDetail) {
            // 更新相应批次出库字段
            ApplyDeliveryOut applyDeliveryOut = applyDeliveryOutDao.findOne(apd.getApplyDeliveryOutId());
            applyDeliveryOut.setConfirmDcsxFlg(BasConstants.CONFIRM_FLG_NOT);
            applyDeliveryOut.setConfirmReceiptDcsxApplyId(null);
            curNumber = curNumber.add(apd.getCurNumber());
            BigDecimal confirmDcsxNumber = applyDeliveryOut.getConfirmDcsxNumber();
            if(confirmDcsxNumber != null) {
                applyDeliveryOut.setConfirmDcsxNumber(confirmDcsxNumber.subtract(apd.getCurNumber()));
            } else {
                applyDeliveryOut.setConfirmDcsxNumber(BigDecimal.ZERO);
            }
            applyDeliveryOutDao.save(applyDeliveryOut);
        }
        CtrContractApplyVo vo = new CtrContractApplyVo();
        vo.setApplyType(BasConstants.APPLY_TYPE_Z);
        vo.setContractId(contractId);
        vo.setRealDate(entity.getConfirmReceiptDate());
        vo.setDealAmount(curNumber.multiply(new BigDecimal("-1")));
        ctrContractDcsxApplyService.updateCtrContractApply(vo);
    }

    /** 审批追回 */
    @Override
    @ServerTransactional
    public void  doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {
        ApplyConfirmReceiptDcsx entity = applyConfirmReceiptDcsxDao.findOne(vo.getBizId());
        List<ApplyProductDetail> lstDetail = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_Z);

//        // 1.更新合同表确认收货数据
//        BigDecimal confirmReceiveNum = lstDetail.stream().map(ApplyProductDetail::getCurNumber).reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        // 2.更新出库申请表数据
//        applyDeliveryOutDao.invalidConfirmReceiveDcsx(entity.getId());

        BigDecimal curNumber = BigDecimal.ZERO;
        for (ApplyProductDetail apd : lstDetail) {
            // 更新相应批次出库字段
            ApplyDeliveryOut applyDeliveryOut = applyDeliveryOutDao.findOne(apd.getApplyDeliveryOutId());
            applyDeliveryOut.setConfirmDcsxFlg(BasConstants.CONFIRM_FLG_NOT);
            applyDeliveryOut.setConfirmReceiptDcsxApplyId(null);
            curNumber = curNumber.add(apd.getCurNumber());
            BigDecimal confirmDcsxNumber = applyDeliveryOut.getConfirmDcsxNumber();
            if(confirmDcsxNumber != null) {
                applyDeliveryOut.setConfirmDcsxNumber(confirmDcsxNumber.subtract(apd.getCurNumber()));
            } else {
                applyDeliveryOut.setConfirmDcsxNumber(BigDecimal.ZERO);
            }
            applyDeliveryOutDao.save(applyDeliveryOut);
        }

        CtrContractApplyVo applyVo = new CtrContractApplyVo();
        applyVo.setApplyType(BasConstants.APPLY_TYPE_Z);
        applyVo.setContractId(entity.getContractId());
        applyVo.setRealDate(entity.getConfirmReceiptDate());
        applyVo.setDealAmount(curNumber.multiply(new BigDecimal("-1")));
        ctrContractDcsxApplyService.updateCtrContractApply(applyVo);
    };

    /**
     * 审批撤回
     *
     * @param vo
     */
    @Override
    @ServerTransactional
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        ApplyConfirmReceiptDcsx entity = applyConfirmReceiptDcsxDao.findOne(vo.getBizId());
        List<ApplyProductDetail> lstDetail = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_Z);
        BigDecimal curNumber = BigDecimal.ZERO;
        for (ApplyProductDetail apd : lstDetail) {
            // 更新相应批次出库字段
            ApplyDeliveryOut applyDeliveryOut = applyDeliveryOutDao.findOne(apd.getApplyDeliveryOutId());
            curNumber = curNumber.add(apd.getCurNumber());
            BigDecimal confirmDcsxNumber = applyDeliveryOut.getConfirmDcsxNumber();
            if(confirmDcsxNumber != null) {
                applyDeliveryOut.setConfirmDcsxNumber(confirmDcsxNumber.subtract(apd.getCurNumber()));
            } else {
                applyDeliveryOut.setConfirmDcsxNumber(BigDecimal.ZERO);
            }
            applyDeliveryOutDao.invalidConfirmReceive(applyDeliveryOut.getId());
        }
        
        CtrContractApplyVo applyVo = new CtrContractApplyVo();
        applyVo.setApplyType(BasConstants.APPLY_TYPE_Z);
        applyVo.setContractId(entity.getContractId());
        applyVo.setRealDate(entity.getConfirmReceiptDate());
        applyVo.setDealAmount(curNumber.multiply(new BigDecimal("-1")));
        ctrContractDcsxApplyService.updateCtrContractApply(applyVo);

        if(StringUtils.equals(BasConstants.APPROVE_STATUS_D,entity.getStatus())) {
            Long contractId = entity.getContractId();
            applyDcsxService.addConfirmReceiptNumber(contractId, curNumber.multiply(new BigDecimal("-1")), null, null);
        }
        // 3.合同操作记录里面把对应的收货确认记录删掉
        ctrContractOphisDao.deleteContractOphis(entity.getApproveId(), entity.getContractId());
    }

    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        ApplyConfirmReceiptDcsx applyConfirmReceiptDcsx;
        ApplyProductDetailSaveVo saveVo = new ApplyProductDetailSaveVo();
        saveVo.setApplyType(BasConstants.APPLY_TYPE_Z);
        if (pmEntity instanceof ApplyConfirmReceiptDcsxVo) {
            ApplyConfirmReceiptDcsxVo vo = (ApplyConfirmReceiptDcsxVo) pmEntity;
            applyConfirmReceiptDcsx = new ApplyConfirmReceiptDcsx();
            BeanUtils.copyProperties(vo, applyConfirmReceiptDcsx);

            if (applyConfirmReceiptDcsx.getId() == 0) {
                //生成合同号
                String applyNo = composeContractNo(applyConfirmReceiptDcsx.getContractNo());
                applyConfirmReceiptDcsx.setApplyNo(applyNo);
                vo.setApplyNo(applyNo);
            }
            if (applyConfirmReceiptDcsx.getContractId() == null) {
                CtrContract byContractNo = ctrContractService.findByContractNo(applyConfirmReceiptDcsx.getContractNo());
                applyConfirmReceiptDcsx.setContractId(byContractNo.getId());
            }

            applyConfirmReceiptDcsx = applyConfirmReceiptDcsxDao.save(applyConfirmReceiptDcsx);
            saveVo.setApplyId(applyConfirmReceiptDcsx.getId());
            List<ApplyProductDetail> lstInsert = vo.getLstInsert();
            for (ApplyProductDetail productDetail : lstInsert) {
                productDetail.setId(null);
            }
            productDetailService.saveDetailBatch(vo.getLstInsert(), vo.getLstUpdate(), vo.getLstDelete(), saveVo);
            vo.setId(applyConfirmReceiptDcsx.getId());
        } else {
            ApplyConfirmReceiptDcsx entity = (ApplyConfirmReceiptDcsx) pmEntity;
            CtrContract contract = ctrContractService.getEntity(entity.getContractId());
            logger.info("applyConfirmReceipt.saveEntity:{}", JsonUtil.obj2Json(contract));
            applyConfirmReceiptDcsx = applyConfirmReceiptDcsxDao.save(entity);
            saveVo.setApplyId(entity.getId());
            productDetailService.saveBatchEnterpriseId(saveVo);
        }

        // 中游确认收货时增加出入库台账报表
        if (StringUtils.equals(applyConfirmReceiptDcsx.getStatus(), BasConstants.APPROVE_STATUS_D)) {
            ApplyCtrDCSX applyCtrDCSX = applyDcsxDao.findOne(applyConfirmReceiptDcsx.getContractId());
            if (Objects.nonNull(applyCtrDCSX)) {
                List<CtrOutInLedger> ctrOutInLedgerList = ctrOutInLedgerDao.findByOperationAndContractNo(BasConstants.DICT_OUT_IN_LEDGER_TYPE_4, applyCtrDCSX.getContractNo());
                if (CollectionUtils.isEmpty(ctrOutInLedgerList)) {
                    CtrOutInLedger ctrOutInLedger = new CtrOutInLedger();
                    BeanUtils.copyProperties(applyCtrDCSX, ctrOutInLedger);
                    ctrOutInLedger.setId(null);
                    ctrOutInLedger.setOperTime(new Date());
                    ctrOutInLedger.setOperation(BasConstants.DICT_OUT_IN_LEDGER_TYPE_4);
                    ctrOutInLedger.setPrice(applyCtrDCSX.getDealPrice());
                    ctrOutInLedger.setSourceId(applyConfirmReceiptDcsx.getId());
                    ctrOutInLedger.setCompanyName(applyConfirmReceiptDcsx.getCompanyName());
                    ctrOutInLedger.setDeliveryAddr(applyConfirmReceiptDcsx.getDeliveryAddr());

                    List<CtrContract> ctrContractList = ctrContractDao.findByApproveId(applyCtrDCSX.getApproveId());
                    if (CollectionUtils.isNotEmpty(ctrContractList)) {
                        ctrOutInLedger.setProductsName(ctrContractList.get(0).getProductsName());
                        ctrOutInLedger.setDeptId(ctrContractList.get(0).getDeptId());
                    }
                    // 获取公司传真
                    BsCompany bsCompany = bsCompanyService.getEntity(applyCtrDCSX.getCompanyId());
                    if (Objects.nonNull(bsCompany)) {
                        ctrOutInLedger.setCompanyFax(bsCompany.getCompanyFax());
                    }
                    // 获取实际合同单号
                    List<CtrLogistics> ctrLogisticsList = ctrLogisticsDao.findByLogisticsNo(ctrOutInLedger.getContractNo().substring(4));
                    if (CollectionUtils.isNotEmpty(ctrLogisticsList)) {
                        ctrOutInLedger.setRealContractNo(ctrLogisticsList.get(0).getSupplierNo());
                    }
                    ctrOutInLedgerDao.save(ctrOutInLedger);
                }
            }
        }
        return applyConfirmReceiptDcsx;
    }

    /**
     * 校验是否出库是否正常
     *
     * @param applyDeliveryOutId
     * @return false 不正常 true 正常
     */
    private Boolean checkIsExistDelivery(Long applyDeliveryOutId) {
        ApplyDeliveryOut entity = applyDeliveryOutDao.findOne(applyDeliveryOutId);
        if (entity == null
                || !BasConstants.CONFIRM_FLG_NOT.equals(entity.getConfirmDcsxFlg())
                || !BasConstants.APPROVE_STATUS_D.equals(entity.getStatus())) {
            return false;
        }
        return true;
    }

    private String composeContractNo(String contractNo) {
        List<ApplyConfirmReceiptDcsx> list = applyConfirmReceiptDcsxDao.findByContractNo(contractNo);
        String fmt = String.format("%02d", list.size() + 1);
        return contractNo + BasConstants.APPLY_TYPE_Z + fmt;
    }

    /**
     * 标题
     *
     * @param pmEntity
     */
    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyConfirmReceiptDcsx vo = (ApplyConfirmReceiptDcsx) pmEntity;
            List<ApplyProductDetail> list = productDetailService.findApplyDetail(vo.getId(), BasConstants.APPLY_TYPE_Z);
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
    public BaseDao<ApplyConfirmReceiptDcsx> getBaseDao() {
        return applyConfirmReceiptDcsxDao;
    }

    @Override
    @ServerTransactional
    public void applyConfirmReceiptDcsx(ApplyConfirmReceiptDcsxVo confirmReceiptDcsxVo) throws ApplicationException {
        try {
            if (confirmReceiptDcsxVo.getConfirmReceiptDate() == null) {
                throw new ApplicationException("实际到货日期不能为NULL!");
            }
            //获取审批对象
            PmApproveSaveVo startVo = new PmApproveSaveVo();
            startVo.setMode(BasConstants.APPROVE_STATUS_A);
            startVo.setStatus(BasConstants.APPROVE_STATUS_A);
            startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            confirmReceiptDcsxVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);

            //获取表单字段
            List<ApplyProductDetailVo> contentJSON = confirmReceiptDcsxVo.getProductJSON();

            //封装
            confirmReceiptDcsxVo.setBatchSub(contentJSON, confirmReceiptDcsxVo.getLstUpdate(), confirmReceiptDcsxVo.getLstDelete());

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
            SysUserSdk userById = authOpenFacade.findUserById(confirmReceiptDcsxVo.getApplyUserId());
            if (userById != null) {
                //审批对象获取当前操作人信息 和流程id
                startVo.setUserId(userById.getUserId());
                startVo.setUserName(userById.getNickName());
                startVo.setProcessId(process.getId());
                startVo.setApproveId(0L);
                confirmReceiptDcsxVo.setApproveId(0L);
            }
            startVo.setBizEntityJson(JsonUtil.obj2Json(confirmReceiptDcsxVo));
            //审批发起方法
            pmApproveService.startFlow(startVo);
        } catch (ApplicationException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public List<ApplyConfirmReceiptDcsx> findByContractId(Long contractId) {
        return applyConfirmReceiptDcsxDao.findByContractId(contractId);
    }

    /**
     * 生成中游确认收货单
     *
     * @param entity
     * @param lstDetail
     * @return
     * @throws ApplicationException
     */
    @Override
    @ServerTransactional
    public ApplyConfirmReceiptDcsx generateSignature(ApplyConfirmReceiptDcsx entity, List<ApplyProductDetail> lstDetail) {
        try {
            AxqContractVo axqContractVo = buildGoodReceiveParam(entity, lstDetail);
            axqContractVo.setGenerateShortUrlFlg(false);
            axqContractVo.setGainSignFileFlg(true);
            logger.info("ApplyConfirmReceiptDcsx generateSignature param:{}", axqContractVo);
            AxqContractVo res = cfcaSignClient.axqGoodReceive(axqContractVo);
            logger.info("ApplyConfirmReceiptDcsx generateSignature result:{}", res);
            if (Objects.isNull(res)) {
                throw new ApplicationException("中游确认收货单生成失败!");
            }
            if (StringUtils.equals("60000000", res.getRetCode())) {
                String fileId = StringUtils.isBlank(entity.getFileId()) ? "" : entity.getFileId();
                String signFileId = StringUtils.isBlank(res.getFileId()) ? "" : res.getFileId();
                signFileId = fileId + signFileId;
                signFileId = (StringUtils.isNotBlank(signFileId) && signFileId.endsWith(BasConstants.COMMA)) ? signFileId : signFileId + BasConstants.COMMA;
                entity.setSignCfcaContractNo(res.getContractNo());
                entity.setFileId(signFileId);

                this.save(entity);
            }
        }catch (Exception e){
            logger.error("generateSignature error", e);
        }
        return entity;
    }

    /**
     * 中游确认收货回调更新
     *
     * @param entity
     */
    @Override
    @ServerTransactional
    public void signatureComplete(ApplyConfirmReceiptDcsx entity) {
        if (StringUtils.isBlank(entity.getSignCfcaContractNo())){
            return;
        }
        // 1.更新已签署电子签附件
        AxqLogisticsVo axqLogisticsVo = new AxqLogisticsVo();
        axqLogisticsVo.setCfcaContractNo(entity.getSignCfcaContractNo());
        axqLogisticsVo.setOurCompanyName(entity.getCompanyName());
        axqLogisticsVo.setSignType("LGS");
        axqLogisticsVo.setSignLocation("buyerSignLocation");
        axqLogisticsVo.setProjectCode("003");
        axqLogisticsVo = cfcaLogisticsClient.axqSignLogistics(axqLogisticsVo);
        if (Boolean.TRUE.equals(axqLogisticsVo.getSuccessFlg())){
            String cfcaFileId = axqLogisticsVo.getCfcaFileId();
            String oldCfcaFileId = axqLogisticsVo.getOldCfcaFileId();
            String fileId = entity.getFileId();
            fileId = fileId.replace(oldCfcaFileId, cfcaFileId);
            entity.setFileId(fileId);
            entity.setSignFlg(true);
            applyConfirmReceiptDcsxDao.save(entity);
        }
    }

    @Override
    public void updateFileId(Long id, String fileId) {
        applyConfirmReceiptDcsxDao.updateFileId(id,fileId);
    }

    /**
     * 构建确认收货电子合同所属属性
     *
     * @param entity
     * @return
     */
    private AxqContractVo buildGoodReceiveParam(ApplyConfirmReceiptDcsx entity, List<ApplyProductDetail> lstDetail) throws ApplicationException {
        AxqContractVo axqContractVo = new AxqContractVo();
        String companyName = entity.getCompanyName();
        String ourCompanyName = entity.getOurCompanyName();
        logger.info("签收单抬头:{}", ourCompanyName);
        String templateCd = BsDictUtil.getValue(entity.getEnterpriseId(), BasConstants.GOOD_RECEIVE_TEMPLATE, ourCompanyName);
        if (StringUtils.isBlank(templateCd)) {
            throw new ApplicationException(ourCompanyName + "签收单模板缺失!");
        }
        List<ApplyProductDetail> productDetails = productDetailService.findApplyDetail(entity.getDeliveryOutId(), BasConstants.APPLY_TYPE_O);
        if (CollectionUtils.isEmpty(productDetails)) {
            if(CollectionUtils.isEmpty(lstDetail)){
                throw new ApplicationException("确认收货商品明细缺失!");
            } else {
                productDetails.addAll(lstDetail);
            }
        }
        ApplyProductDetail productDetail = productDetails.get(0);
        List<ApplyProductDetail> pds = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_Z);
        ApplyProductDetail confirmReceiptProductDetail = pds.get(0);
        // 货物签收单模板CD
        axqContractVo.setTemplateId(templateCd);
        axqContractVo.setBuyerCompanyName(companyName);
        axqContractVo.setContractNo(entity.getContractNo());
        axqContractVo.setContractId(entity.getContractNo());
        axqContractVo.setBizContractId(entity.getApplyNo());
        axqContractVo.setConfirmReceiptDate(entity.getConfirmReceiptDate());
        axqContractVo.setConfrimNumber(confirmReceiptProductDetail.getCurNumber() + "");
        axqContractVo.setProductName(productDetail.getProductName());
        axqContractVo.setMatchUserPhone(entity.getSignUserPhone());
        //产地
        axqContractVo.setProductPlace(productDetail.getFactoryName());
        //规格
        axqContractVo.setPackageSpec(productDetail.getBrandNumber());
        //单位
        axqContractVo.setNumberUnit(productDetail.getNumberUnit());
        //数量
        axqContractVo.setDealNumber(String.valueOf(productDetail.getDealNumber()));
        //送货单号
        axqContractVo.setApplyNo(entity.getApplyNo());
        //车号
        axqContractVo.setPlateNumber(entity.getPlateNumber());
        //司机姓名
        axqContractVo.setDriverName(entity.getDriverName());
        //身份证号
        axqContractVo.setDriverCardNo(entity.getDriverCardNo());
        //配送地址
        axqContractVo.setDeliveryAddr(entity.getDeliveryAddr());
        //联系人
        axqContractVo.setContactName(entity.getContactName());
        //联系人电话
        axqContractVo.setContactPhone(entity.getContactPhone());
        //备注
        axqContractVo.setMemo(entity.getRemark());
        axqContractVo.setPriceTotalAll(String.valueOf(productDetail.getTotalPrice()));
        List<CtrProductVo> ctrProductVos = new ArrayList<>();
        productDetails.forEach(product -> {
            CtrProductVo ctrVo = new CtrProductVo();
            ctrVo.setBrandNumber(product.getBrandNumber());
            ctrVo.setDealNumber(product.getDealNumber() + "");
            ctrVo.setDealPrice(product.getDealPrice() + "");
            ctrVo.setFactoryName(companyName);
            ctrVo.setProductName(product.getProductName());
            ctrVo.setTotalPrice(product.getTotalPrice() + "");
            ctrProductVos.add(ctrVo);
        });
        axqContractVo.setProductList(ctrProductVos);
        return mustAxq(axqContractVo);
    }

    /**
     * 安心签必填字段
     *
     * @param s
     * @return
     */
    private AxqContractVo mustAxq(AxqContractVo s) {
        s.setAppCode(BasConstants.APP_CODE);
        // 默认
        s.setBuyerSignType(BasConstants.CONTRACT_SEAL_TYPE_UK_SIGN);
        s.setSellerSignType(BasConstants.CONTRACT_SEAL_TYPE_UK_SIGN);
        s.setBuyerIsCheckProjectCode(BasConstants.CONTRACT_SEAL_NOT_SEND_PWD);
        // 默认
        s.setSellerIsCheckProjectCode(BasConstants.CONTRACT_SEAL_NOT_SEND_PWD);
        // 默认
        s.setSignLocation(BasConstants.CONTRACT_SIGN_LOCATION);
        s.setBuyerSignLocation(BasConstants.CONTRACT_BUYER_SIGN_LOCATION);
        // 默认
        s.setSellerSignLocation(BasConstants.CONTRACT_SELLER_SIGN_LOCATION);
        // 默认
        s.setBuyerLocation("10.2.2.3");
        s.setSellerLocation("11.22.33.66");
        return s;
    }

    private static String generateQrCodeBase64(String url) {
        QrConfig config = new QrConfig(250, 250);
        // 设置边距，既二维码和背景之间的边距
        config.setMargin(1);
        // 高纠错级别
        config.setErrorCorrection(ErrorCorrectionLevel.H);
        return QrCodeUtil.generateAsBase64(url, config, "PNG", BsDictConstants.LOGO_IMAGE_BASE_64);
    }

    /**
     * 自动发起中游确认收货申请
     * @param contract
     * @param entity
     * @param lstDetail
     * @throws ApplicationException
     */
    @Override
    public void autoApplyConfirmReceiptDcsx(CtrContract contract,ApplyDeliveryOut entity,List<ApplyProductDetail> lstDetail) {
        try {
            ApplyCtrDCSX applyCtrDCSX = applyDcsxService.findByDCSXApproveId(contract.getApproveId());
            PmApprove pmApprove = pmApproveService.findApproveNoByApproveId(entity.getApproveId());
            // 自动发起中游确认收货
            PmProcess process = processDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_APPLY_CONFIRM_RECEIPT_DCSX, contract.getEnterpriseId());
            ApplyConfirmReceiptDcsxVo applyConfirmReceiptDcsxVo = new ApplyConfirmReceiptDcsxVo();
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
            applyConfirmReceiptDcsxVo.setConfirmReceiptDate(new Date());
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
                startVo.setAutoStartMessage("系统生成历史中游确认收货");
                pmApproveService.startFlow(startVo);
            }
        } catch (ApplicationException e) {
            logger.error("系统生成历史中游确认收货失败",e);
        }
    }

    @Override
    public void initHistoryConfirmReceiptDcsx() {
        logger.info("生成历史数据中游确认收货审批单-开始");
        List<ApplyCtrDCSX> applyCtrDCSXList = applyDcsxService.findAll();
        if(!CollectionUtils.isEmpty(applyCtrDCSXList)) {
            // 查询所有代采赊销预算合同
            for (ApplyCtrDCSX applyCtrDCSX : applyCtrDCSXList) {
                // 判断是否符合发起中游确认收货条件
                if(getConfirmReceiptFlg(applyCtrDCSX)){
                    
                    CtrContract contract = ctrContractDao.findByApproveIdAndContractType(applyCtrDCSX.getApproveId(), BasConstants.CONTRACT_TYPE_S);
                    if(Objects.nonNull(contract)) {
                        
                        List<ApplyDeliveryOut> deliveryOutList = applyDeliveryOutDao.findByContractNo(contract.getContractNo());
                        if(!CollectionUtils.isEmpty(deliveryOutList)) {
                            for (ApplyDeliveryOut deliveryOut : deliveryOutList) {
                                String confirmDcsxFlg = deliveryOut.getConfirmDcsxFlg();// 中游确认收货状态
                                String confirmFlg = deliveryOut.getConfirmFlg();// 下游确认收货状态
                                if( StringUtils.equals(BasConstants.CONFIRM_FLG_YES,confirmFlg) && (StringUtils.isBlank(confirmDcsxFlg) || StringUtils.equals(BasConstants.CONFIRM_FLG_NOT,confirmDcsxFlg)) ){
                                    logger.info("ourCompanyName:{},companyName:{}",applyCtrDCSX.getOurCompanyName(),applyCtrDCSX.getCompanyName());
                                    List<ApplyProductDetail> lstDetail = productDetailService.findApplyDetail(deliveryOut.getId(), BasConstants.APPLY_TYPE_O);
                                    for (ApplyProductDetail productDetail : lstDetail) {
                                        productDetail.setApplyDeliveryOutId(deliveryOut.getId());
                                        productDetail.setId(null);
                                    }
                                    // 是否判断司机信息
                                    String isCheckDriver = com.spt.bas.server.cache.BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DICT_DRIVER_CONFIG,BasConstants.DICT_IS_CHECK_DRIVER);
                                    if (StringUtils.isNotBlank(isCheckDriver) && !"Y".equals(isCheckDriver)) {
                                        String driverName = deliveryOut.getDriverName();
                                        if(StringUtils.isNotBlank(driverName)) {
                                            autoApplyConfirmReceiptDcsx(contract,deliveryOut,lstDetail);
                                        }
                                    } else {
                                        autoApplyConfirmReceiptDcsx(contract,deliveryOut,lstDetail);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        logger.info("生成历史数据中游确认收货审批单-结束");
    }
    /**
     *  是否需要补充中游确认收货审批单
     * @return
     */
    public Boolean getConfirmReceiptFlg(ApplyCtrDCSX applyCtrDCSX) {
        BigDecimal confirmReceiveNumber = null;
        if(Objects.nonNull(applyCtrDCSX)) {
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
}
