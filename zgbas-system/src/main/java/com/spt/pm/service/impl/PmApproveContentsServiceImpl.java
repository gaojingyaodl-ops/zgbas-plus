package com.spt.pm.service.impl;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.ApplySource;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.ApplyCancel2Vo;
import com.spt.bas.client.vo.ApplyQuotaVo;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveContentsService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.util.RuleUtils;
import com.spt.pm.util.SubjectPmUtil;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Component("pmApproveContentsService")
@Transactional(readOnly = true)
public class PmApproveContentsServiceImpl extends BaseService<PmApproveContents>
        implements IPmApproveContentsService, IPmService {
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IApplyWxCfcaClient applyWxCfcaClient;
    @Autowired
    private IBsCompanyClient bsCompanyClient;
    @Autowired
    private IBsProductTypeClient bsProductTypeClient;
    @Autowired
    private IApplyEntrustClient applyEntrustClient;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private ICtrProductClient ctrProductClient;

    @Override
    public BaseDao<PmApproveContents> getBaseDao() {
        return pmApproveContentsDao;
    }

    @Override
    public Class<PmApproveContents> getEntityClazz() {
        return PmApproveContents.class;
    }

    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        PmApproveContents entity = new PmApproveContents();
        if (pmEntity instanceof SealUsage) {
            //印章使用申请
            parseSealUsage(pmEntity, entity);
            entity.setApplyName(BasConstants.SEAL_APPLY_NAME_USAGE);
        } else if (pmEntity instanceof SealBorrow) {
            //印章外借申请
            parseSealBorrow(pmEntity, entity);
            entity.setApplyName(BasConstants.SEAL_APPLY_NAME_BORROW);
        } else if (pmEntity instanceof BsCompanyAllowed) {
            //公司准入申请
            parseBsCompanyAllowed(pmEntity, entity);
            entity.setApplyName(BasConstants.COMPANY_APPLY_ALLOWED);
        } else if (pmEntity instanceof BsCompanyQuota) {
            //公司额度浮动申请
            parseBsCompanyQuota(pmEntity, entity);
            entity.setApplyName(BasConstants.COMPANY_APPLY_QUOTA);
        } else if (pmEntity instanceof ApplyQuotaVo) {
            //公司额度申请
            parseBsCompanyQuotaV1(pmEntity, entity);
            entity.setApplyName(BasConstants.COMPANY_QUOTA);
        } else if (pmEntity instanceof ApplyCompanyInfo) {
            // 公司信息审批
            parseApplyCompanyInfo(pmEntity, entity);
            entity.setApplyName(BasConstants.APPLY_COMPANY_INFO);
        } else if (pmEntity instanceof ApplyDeposit) {
            // 入金验证审批
            parseApplyDeposit(pmEntity, entity);
            entity.setApplyName(BasConstants.APPLY_DEPOSIT);
        } else if (pmEntity instanceof ApplyEntrust) {
            // 委托授权审批
            parseApplyEntrust(pmEntity, entity);
            entity.setApplyName(BasConstants.APPLY_ENTRUST);
        } else if (pmEntity instanceof ApplyPartner) {
            // 合伙人申请
            parseApplyPartner(pmEntity, entity);
        } else if (pmEntity instanceof Feedback) {
            // 意见反馈
            parseApplyFeedback(pmEntity, entity);
        } else if (pmEntity instanceof ApplyCfca) {
            // cfca
            parseApplyCfca(pmEntity, entity);
        } else if (pmEntity instanceof ApplyRate) {
            // 服务费率审批
            parseApplyRate(pmEntity, entity);
        } else if (pmEntity instanceof ApplyCreditCycle) {
            // 回款周期审批
            parseApplyCreditCycle(pmEntity, entity);
        } else if (pmEntity instanceof ApplyCancel2Vo) {
            // 作废
            parseApplyCancel(pmEntity, entity);
        } else if (pmEntity instanceof ApplyVip) {
            // vip审批
            parseApplyVip(pmEntity, entity);
        } else if (pmEntity instanceof ApplyDispute) {
            // 争议审批
            parseDispute(pmEntity, entity);
        } else if (pmEntity instanceof PmApproveContents) {
            //通用审批单修改
            entity = save((PmApproveContents) pmEntity);
        }else if (pmEntity instanceof ApplyPromoteVip) {
            //vip提额审批
            applyPromoteVip(pmEntity, entity);
        }else if (pmEntity instanceof ApplyVipReceive) {
            //vip提额收款审批
            applyVipReceive(pmEntity, entity);
        }
        else if (pmEntity instanceof ApplyVipInvoice) {
            //vip提额开票审批
            applyVipInvoice(pmEntity, entity);
        }else if (pmEntity instanceof ApplyVipMainReceive) {
            //vip收款审批
            applyVipMainReceive(pmEntity, entity);
        }
        else if (pmEntity instanceof ApplyVipMainInvoice) {
            //vip开票审批
            applyVipMinInvoice(pmEntity, entity);
        }
        else if (pmEntity instanceof ApplySupplierAllowed) {
            //供应商准入审批
            parseApplySupplierAllowed(pmEntity, entity);
            entity.setApplyName(BasConstants.PROCESS_APPLY_SUPPLIER_ALLOWED);
        }
        else if (pmEntity instanceof ApplySupplierQuota) {
            //供应商准入审批
            parseApplySupplierQuota(pmEntity, entity);
        }
        else if (pmEntity instanceof ApplySupplierDelivery) {
            //供应商准入审批
            parseApplySupplierDelivery(pmEntity, entity);
        }
        else if (pmEntity instanceof ApplySupplierFuture) {
            //供应商准入审批
            parseApplySupplierFuture(pmEntity, entity);
        } else if (pmEntity instanceof ApplyTerminalPick){
           //终端工厂自提审批
            ApplyTerminalPick(pmEntity, entity);
        } else if(pmEntity instanceof SealUsageDCSX){
           //代采赊销盖章审批
            sealUsageDCSX(pmEntity,entity);
        }else if(pmEntity instanceof ApplyInternalTransferMoney){
            //内部资金拆借申请
            applyInternalTransferMoney(pmEntity,entity);
        } else if(pmEntity instanceof ApplyCompanyOnline){
            //企业线上化审批
            applyOnline(pmEntity,entity);
        }else if (pmEntity instanceof ApplyWxCfca) {
            // cfca开户二合一
            onlineAccountOpening(pmEntity, entity);
            entity.setApplyName(BasConstants.ONLINE_ACCOUNT_OPENING);
        }else if (pmEntity instanceof VehicleUse){
            // 车辆使用申请
            parseVehicleUse(pmEntity, entity);
            entity.setApplyName(BasConstants.VEHICLE_APPLY_NAME_USE);
        }else if(pmEntity instanceof BasBrand){
            parseApplyBrand(pmEntity, entity);
            entity.setApplyName(BasConstants.APPLY_BRAND);
        }else if(pmEntity instanceof ApplyPay){
            parseApplyPay(pmEntity, entity);
            entity.setApplyName(BasConstants.FACTOR_AMOUNT);
        }
        return entity;

    }

    @Override
    public Map<String, Object> buildConditionDefaultMap(IPmEntity pmEntity) {
        HashMap<String, Object> resultMap = new HashMap<>();
        try {
            PmApproveContents entity = (PmApproveContents) pmEntity;
            SealUsage sealUsages = JsonUtil.json2Object(SealUsage.class, entity.getContents());
            if (Objects.isNull(sealUsages) || Objects.isNull(sealUsages.getContractId())){
                return resultMap;
            }
            CtrContract contract = ctrContractClient.getEntity(sealUsages.getContractId());
            if (Objects.isNull(contract)){
                return resultMap;
            }
            List<CtrProduct> productList = ctrProductClient.findByOutCtrContractId(contract.getId());
            if (CollectionUtils.isNotEmpty(productList)) {
                // 化工标识
                resultMap.put("hgFlag", productList.stream().anyMatch(p -> p.getProductCd().startsWith("HG_")));
                // 塑料标识
                resultMap.put("slFlag", productList.stream().anyMatch(p -> p.getProductCd().startsWith("SL_")));
            }
            String contractNo = contract.getContractNo();
            Boolean matchCreditFlg = contract.getMatchCreditFlg();
            String businessType = contract.getBusinessType();
            // 合同类型
            resultMap.put("contractType", contract.getContractType());
            // 自营标识
            resultMap.put("zyFlag", StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_CG, businessType)
                    || StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_XS, businessType));
            // 代采标识
            resultMap.put("dcFlag", StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) && Boolean.FALSE.equals(matchCreditFlg));
            // 赊销标识
            resultMap.put("sxFlag", StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) && Boolean.TRUE.equals(matchCreditFlg));
            // 托盘标识
            resultMap.put("tpFlag", StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, businessType));
            // 协议合同标识
            resultMap.put("xyFlag", contractNo.startsWith(BasConstants.STOCK_VIRTUAL_XY));
            // 库存合同标识
            resultMap.put("kcFlag", contractNo.startsWith(BasConstants.STOCK_VIRTUAL_KC));
            // KUB库存采购合同标识
            resultMap.put("kubFlag", contractNo.startsWith("KUB"));
            // KUX库存采购合同标识
            resultMap.put("kuxFlag", contractNo.startsWith("KUX"));
            // 合同业务员部门ID
            resultMap.put("matchDeptId", contract.getDeptId());
        } catch (Exception e) {
            logger.error("buildConditionDefaultMap", e);
        }
        return resultMap;
    }

    /**
     * 供应商远期申请
     * @param pmEntity
     * @param entity
     * @throws ApplicationException
     */
    private void parseApplySupplierFuture(IPmEntity pmEntity, PmApproveContents entity) throws ApplicationException {
        ApplySupplierFuture applySupplierFuture = (ApplySupplierFuture) pmEntity;
        Long approveId = applySupplierFuture.getApproveId();

        replaceIfExistPmApproveContent(approveId, entity);
        String supplierFuture = applySupplierFuture.getSupplierFuture();
        String supplierFutureStr = supplierFuture.equals("0") ? "否" : "是";

        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            applySupplierFuture.setApplyUserId(pmApprove.getCreateUserId());
            applySupplierFuture.setApplyUserName(pmApprove.getCreateUserName());
        }

        String contents = JsonUtil.obj2Json(applySupplierFuture);
        entity.setContents(contents);
        String companyName = RuleUtils.companyNameSubString(applySupplierFuture.getCompanyName());
        String subject = SubjectPmUtil.formatSubject(companyName,"允许供应商远期："+supplierFutureStr);
        entity.setSubject(subject);
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(applySupplierFuture.getApplyUserId());
        entity.setDeptId(deptByUserId.getDeptId());
        entity.setEnterpriseId(applySupplierFuture.getEnterpriseId());
        entity = save(entity);
        logger.info("parseApplySupplierFuture : " + JsonUtil.obj2Json(entity));
    }

    /**
     * 供应商配送申请
     * @param pmEntity
     * @param entity
     * @throws ApplicationException
     */
    private void parseApplySupplierDelivery(IPmEntity pmEntity, PmApproveContents entity) throws ApplicationException {
        ApplySupplierDelivery applySupplierDelivery = (ApplySupplierDelivery) pmEntity;
        Long approveId = applySupplierDelivery.getApproveId();

        replaceIfExistPmApproveContent(approveId, entity);
        String supplierDelivery = applySupplierDelivery.getSupplierDelivery();
        String supplierDeliveryStr = supplierDelivery.equals("0") ? "否" : "是";

        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            applySupplierDelivery.setApplyUserId(pmApprove.getCreateUserId());
            applySupplierDelivery.setApplyUserName(pmApprove.getCreateUserName());
        }

        String contents = JsonUtil.obj2Json(applySupplierDelivery);
        entity.setContents(contents);
        String subject = "";
        String companyName = RuleUtils.companyNameSubString(applySupplierDelivery.getCompanyName());
        subject = SubjectPmUtil.formatSubject(companyName,"允许供应商配送："+supplierDeliveryStr);
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(applySupplierDelivery.getApplyUserId());
        entity.setDeptId(deptByUserId.getDeptId());
        entity.setSubject(subject);
        entity.setEnterpriseId(applySupplierDelivery.getEnterpriseId());
        entity = save(entity);
        logger.info("parseApplySupplierDelivery : " + JsonUtil.obj2Json(entity));
    }

    /**
     * 供应商额度申请
     * @param pmEntity
     * @param entity
     * @throws ApplicationException
     */
    private void parseApplySupplierQuota(IPmEntity pmEntity, PmApproveContents entity) throws ApplicationException {
        ApplySupplierQuota applySupplierQuota = (ApplySupplierQuota) pmEntity;
        Long approveId = applySupplierQuota.getApproveId();

        replaceIfExistPmApproveContent(approveId, entity);

        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            applySupplierQuota.setApplyUserId(pmApprove.getCreateUserId());
            applySupplierQuota.setApplyUserName(pmApprove.getCreateUserName());
            SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(applySupplierQuota.getApplyUserId());
            applySupplierQuota.setDeptId(deptByUserId.getDeptId());
        }

        String contents = JsonUtil.obj2Json(applySupplierQuota);
        entity.setContents(contents);
        String companyName = RuleUtils.companyNameSubString(applySupplierQuota.getCompanyName());
        String subject = "";
        subject = SubjectPmUtil.formatSubject(companyName,"采购："+applySupplierQuota.getSupplierPurchaseAmount()+ RuleUtils.monetaryUnit,"预付款:"+applySupplierQuota.getSupplierPrepayAmount()+ RuleUtils.monetaryUnit);
        entity.setSubject(subject);
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(applySupplierQuota.getApplyUserId());
        entity.setDeptId(deptByUserId.getDeptId());
        entity.setEnterpriseId(applySupplierQuota.getEnterpriseId());
        entity = save(entity);
        logger.info("parseApplySupplierQuota : " + JsonUtil.obj2Json(entity));
    }

    /**
     * 解析供应商准入申请
     * @param pmEntity
     * @param entity
     */
    private void parseApplySupplierAllowed(IPmEntity pmEntity, PmApproveContents entity) throws ApplicationException {
        ApplySupplierAllowed applySupplierAllowed = (ApplySupplierAllowed) pmEntity;
        Long approveId = applySupplierAllowed.getApproveId();

        replaceIfExistPmApproveContent(approveId, entity);
        String supplierRating = applySupplierAllowed.getSupplierRating();
        String supplierRatingStr = "";
        if (StringUtils.equals(supplierRating, BasConstants.DICT_TYPE_CREDITRATING_W)) {
            supplierRatingStr = BasConstants.DICT_TYPE_CREDITRATING_W_TEXT;
        } else if (StringUtils.equals(supplierRating, BasConstants.DICT_TYPE_CREDITRATING_G)) {
            supplierRatingStr = BasConstants.DICT_TYPE_CREDITRATING_G_TEXT;
        } else if (StringUtils.equals(supplierRating, BasConstants.DICT_TYPE_CREDITRATING_B)) {
            supplierRatingStr = BasConstants.DICT_TYPE_CREDITRATING_B_TEXT;
        }

        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            applySupplierAllowed.setApplyUserId(pmApprove.getCreateUserId());
            applySupplierAllowed.setApplyUserName(pmApprove.getCreateUserName());
        }
        Long count = ctrContractClient.countByCompanyId(applySupplierAllowed.getCompanyId());
        applySupplierAllowed.setCooperationFlg(Objects.nonNull(count) && count > 0);

        String contents = JsonUtil.obj2Json(applySupplierAllowed);
        entity.setContents(contents);
        String subject = "";
        String companyName = RuleUtils.companyNameSubString(applySupplierAllowed.getCompanyName());
        subject =SubjectPmUtil.formatSubject(companyName,supplierRatingStr,BasConstants.DICT_TYPE_SUPPLIER_ALLOWED);
        entity.setSubject(subject);
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(applySupplierAllowed.getApplyUserId());
        entity.setDeptId(deptByUserId.getDeptId());
        entity.setEnterpriseId(applySupplierAllowed.getEnterpriseId());

        entity = save(entity);
        logger.info("parseApplySupplierAllowed : " + JsonUtil.obj2Json(entity));
    }


    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            if (pmEntity instanceof PmApproveContents) {
                PmApproveContents pmApproveContents = (PmApproveContents) pmEntity;
                return pmApproveContents.getSubject();
            }
        }
        return null;
    }

    @Override
    public Long getMatchUserId(IPmEntity pmEntity) {
        return IPmService.super.getMatchUserId(pmEntity);
    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        pmApproveContentsDao.updateFileId(id, fileId);
    }


    @Override
    public PmApproveContents findByApproveId(Long approveId) {
        return pmApproveContentsDao.findByApproveId(approveId);
    }

    @Override
    public List<PmApproveContents> findByRealApproveId(Long approveId) {
        return pmApproveContentsDao.findByRealApproveId(approveId);
    }

    /**
     * 解析公司准入申请
     *
     * @param pmEntity
     * @param entity
     */
    private void parseBsCompanyAllowed(IPmEntity pmEntity, PmApproveContents entity) throws ApplicationException {

        BsCompanyAllowed bsCompanyAllowed = (BsCompanyAllowed) pmEntity;
        Long approveId = ((BsCompanyAllowed) pmEntity).getApproveId();

        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);

        bsCompanyAllowed.setApproveId(approveId);
        String companyName = bsCompanyAllowed.getCompanyName();
        String allowed = bsCompanyAllowed.getAllowed();
        String allowedStr = "";
        if (StringUtils.equals(allowed, BasConstants.DICT_TYPE_ALLOWED_Y)) {
            allowedStr = BasConstants.DICT_TYPE_ALLOWED_Y_TEXT;
        } else if (StringUtils.equals(allowed, BasConstants.DICT_TYPE_ALLOWED_N)) {
            allowedStr = BasConstants.DICT_TYPE_ALLOWED_N_TEXT;
        } else if (StringUtils.equals(allowed, BasConstants.DICT_TYPE_ALLOWED_NEW)) {
            allowedStr = BasConstants.DICT_TYPE_ALLOWED_NEW_TEXT;
        }

        String creditRating = bsCompanyAllowed.getCreditRating();
        String creditRatingStr = "";
        if (StringUtils.equals(creditRating, BasConstants.DICT_TYPE_CREDITRATING_W)) {
            creditRatingStr = BasConstants.DICT_TYPE_CREDITRATING_W_TEXT;
        } else if (StringUtils.equals(creditRating, BasConstants.DICT_TYPE_CREDITRATING_G)) {
            creditRatingStr = BasConstants.DICT_TYPE_CREDITRATING_G_TEXT;
        } else if (StringUtils.equals(creditRating, BasConstants.DICT_TYPE_CREDITRATING_B)) {
            creditRatingStr = BasConstants.DICT_TYPE_CREDITRATING_B_TEXT;
        }

        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            bsCompanyAllowed.setApplyUserId(pmApprove.getCreateUserId());
            bsCompanyAllowed.setApplyUserName(pmApprove.getCreateUserName());
        }

        String contents = JsonUtil.obj2Json(bsCompanyAllowed);

        entity.setContents(contents);
        //生成标题
        String subject = "";
        String companyNameStr = RuleUtils.companyNameSubString(companyName);
        //格式 : 公司名称 信用等级 是否准入
        subject = SubjectPmUtil.formatSubject(companyNameStr,creditRatingStr,allowedStr);
        entity.setSubject(subject);
        entity.setEnterpriseId(bsCompanyAllowed.getEnterpriseId());
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(bsCompanyAllowed.getApplyUserId());
        entity.setDeptId(deptByUserId.getDeptId());
        entity = save(entity);
        logger.info("parseBsCompanyAllowed : " + JsonUtil.obj2Json(entity));
    }

    /**
     * 解析公司额度申请
     *
     * @param pmEntity
     * @param entity
     */
    private void parseBsCompanyQuota(IPmEntity pmEntity, PmApproveContents entity) throws ApplicationException {
        BsCompanyQuota bsCompanyQuota = (BsCompanyQuota) pmEntity;
        Long approveId = ((BsCompanyQuota) pmEntity).getApproveId();

        // 根据approveId判断并替换entity 并将applysource和wxUserid字段带入
        replaceIfExistPmApproveContent(approveId, entity);

        bsCompanyQuota.setApproveId(approveId);
        String companyName = bsCompanyQuota.getCompanyName();

        // 申请表单带过来的字段 applySource，wxUserId
        if (entity != null && !StringUtils.isEmpty(entity.getContents())) {
            BsCompanyQuota bsCompanyQuota1 = JsonUtil.json2Object(BsCompanyQuota.class, entity.getContents());
            bsCompanyQuota.setApplySource(bsCompanyQuota1.getApplySource());
            bsCompanyQuota.setWxUserId(bsCompanyQuota1.getWxUserId());
        }

        BigDecimal floatingRate = bsCompanyQuota.getFloatingRate();
        String floatingRateStr = floatingRate.multiply(new BigDecimal("100")) + "%";
        BigDecimal floatingMaxAmount = bsCompanyQuota.getFloatingMaxAmount();

        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            bsCompanyQuota.setApplyUserId(pmApprove.getCreateUserId());
            bsCompanyQuota.setApplyUserName(pmApprove.getCreateUserName());
        }

        String contents = JsonUtil.obj2Json(bsCompanyQuota);
        entity.setContents(contents);
        //生成标题
        String subject = "";

        //格式 : 公司名称 信用等级 是否准入
        String companyName1 = RuleUtils.companyNameSubString(companyName);
        subject =SubjectPmUtil.formatSubject(companyName1,floatingRateStr,SubjectPmUtil.formatMoney(floatingMaxAmount , RuleUtils.monetaryUnit));
        entity.setSubject(subject);
        entity.setEnterpriseId(bsCompanyQuota.getEnterpriseId());
        entity = save(entity);
        logger.info("parseBsCompanyQuota : " + JsonUtil.obj2Json(entity));
    }

    /**
     * 解析公司额度申请
     *
     * @param pmEntity
     * @param entity
     */
    private void parseBsCompanyQuotaV1(IPmEntity pmEntity, PmApproveContents entity) throws ApplicationException {
        ApplyQuotaVo applyQuotaVo = (ApplyQuotaVo) pmEntity;
        Long approveId = ((ApplyQuotaVo) pmEntity).getApproveId();
        // 根据approveId判断并替换entity 并将applysource和wxUserid字段带入
        replaceIfExistPmApproveContent(approveId, entity);
        applyQuotaVo.setApproveId(approveId);
        String companyName = applyQuotaVo.getCompanyName();
        String contents = JsonUtil.obj2Json(applyQuotaVo);
        entity.setContents(contents);
        //生成标题
        String subject = "";
        // 额度审批
        if(applyQuotaVo.getType().equals(BasConstants.APPLY_QUOTA_TYPE_Q)){
            subject = SubjectPmUtil.formatSubject(companyName,applyQuotaVo.getCreditTypeName()+"授信",SubjectPmUtil.formatMoney(applyQuotaVo.getCreditAmount() , RuleUtils.monetaryUnit),"风控额度",SubjectPmUtil.formatMoney(applyQuotaVo.getRiskAmount() , RuleUtils.monetaryUnit));
        } else {
            // 临时提额
            subject = SubjectPmUtil.formatSubject(companyName,applyQuotaVo.getCreditTypeName()+"提额",SubjectPmUtil.formatMoney(applyQuotaVo.getTemporaryAmount() , RuleUtils.monetaryUnit),"有效"+applyQuotaVo.getValidDays()+ RuleUtils.dateUnit);
        }
        //格式 : 公司名称 信用等级 是否准入
        entity.setSubject(subject);
        entity.setEnterpriseId(applyQuotaVo.getEnterpriseId());
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(applyQuotaVo.getUserId());
        entity.setDeptId(deptByUserId.getDeptId());
        entity = save(entity);
        logger.info("parseBsCompanyQuotaV1 : " + JsonUtil.obj2Json(entity));
    }

    /**
     * 解析公司信息申请
     *
     * @param pmEntity
     * @param entity
     */
    private void parseApplyCompanyInfo(IPmEntity pmEntity, PmApproveContents entity) throws ApplicationException {
        ApplyCompanyInfo applyCompanyInfo = (ApplyCompanyInfo) pmEntity;
        Long approveId = ((ApplyCompanyInfo) pmEntity).getApproveId();

        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);

        applyCompanyInfo.setApproveId(approveId);
        String companyName = applyCompanyInfo.getCompanyName();

        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            applyCompanyInfo.setApplyUserId(pmApprove.getCreateUserId());
            applyCompanyInfo.setApplyUserName(pmApprove.getCreateUserName());
        }

        String contents = JsonUtil.obj2Json(applyCompanyInfo);
        entity.setContents(contents);
        //生成标题
        String subject = "";


        //格式 : 公司名称
        subject = String.format("%s", "[" + companyName + "]");
        entity.setSubject(subject);
        entity.setEnterpriseId(applyCompanyInfo.getEnterpriseId());
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(applyCompanyInfo.getApplyUserId());
        entity.setDeptId(deptByUserId.getDeptId());
        entity.setFileId(applyCompanyInfo.getFileId());
        entity = save(entity);
        logger.info("parseApplyCompanyInfo : " + JsonUtil.obj2Json(entity));
    }

    /**
     * 解析入金验证申请
     *
     * @param pmEntity
     * @param entity
     */
    private void parseApplyDeposit(IPmEntity pmEntity, PmApproveContents entity) throws ApplicationException {
        ApplyDeposit applyDeposit = (ApplyDeposit) pmEntity;
        Long approveId = ((ApplyDeposit) pmEntity).getApproveId();

        applyDeposit.setApproveId(approveId);

        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);

        String companyName = applyDeposit.getCompanyName();
        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            applyDeposit.setApplyUserId(pmApprove.getCreateUserId());
            applyDeposit.setApplyUserName(pmApprove.getCreateUserName());
        }

        String contents = JsonUtil.obj2Json(applyDeposit);
        entity.setContents(contents);
        //生成标题
        String subject = "";

        //格式 : 公司名称
        subject = String.format("%s", "[" + companyName + "]");
        entity.setSubject(subject);
        entity.setEnterpriseId(applyDeposit.getEnterpriseId());
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(applyDeposit.getApplyUserId());
        entity.setDeptId(deptByUserId.getDeptId());
        entity = save(entity);
        logger.info("parseApplyDeposit : " + JsonUtil.obj2Json(entity));
    }

    /**
     * 解析委托授权申请
     *
     * @param pmEntity
     * @param entity
     */
    private void parseApplyEntrust(IPmEntity pmEntity, PmApproveContents entity) throws ApplicationException {
        ApplyEntrust applyEntrust = (ApplyEntrust) pmEntity;
        Long approveId = ((ApplyEntrust) pmEntity).getApproveId();
        applyEntrust.setApproveId(approveId);

        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);

        String companyName = applyEntrust.getCompanyName();

        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            applyEntrust.setApplyUserId(pmApprove.getCreateUserId());
            applyEntrust.setApplyUserName(pmApprove.getCreateUserName());
        }

        String applySource = applyEntrust.getApplySource();
        String additionalManager = "";
        if(!StringUtils.equals(ApplySource.CMS.getCode(),applySource)) {
            // 判断是否有申请过绑定委托授权，有就说明是追加经办人
            Boolean isHaveEntrustUser = applyEntrustClient.findIsHaveEntrustUserByCompanyName(applyEntrust.getCompanyName());
            // 如果有多个的话，说明是
            if(isHaveEntrustUser){
                additionalManager = "[追加经办人]";
            }

        }

        String contents = JsonUtil.obj2Json(applyEntrust);
        entity.setContents(contents);
        //生成标题
        String subject = "";

        //格式 : 公司名称
        String companyNameStr = RuleUtils.companyNameSubString(companyName);
        subject = SubjectPmUtil.formatSubject(companyNameStr,additionalManager);
        entity.setSubject(subject);
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(applyEntrust.getApplyUserId());
        entity.setDeptId(deptByUserId.getDeptId());
        entity.setEnterpriseId(applyEntrust.getEnterpriseId());
        entity.setFileId(applyEntrust.getFileId());
        entity = save(entity);
        logger.info("parseApplyDeposit : " + JsonUtil.obj2Json(entity));
    }


    /**
     * 印章使用审批内容Json处理
     */
    private void parseSealUsage(IPmEntity pmEntity, PmApproveContents entity) throws ApplicationException {
        SealUsage sealUsage = (SealUsage) pmEntity;
        Long id = sealUsage.getId();
        Long approveId = ((SealUsage) pmEntity).getApproveId();
        sealUsage.setApproveId(approveId);
        String sealType = sealUsage.getSealType();
        String companyName = sealUsage.getCompanyName();
        if (StringUtils.isNotBlank(sealUsage.getContractNo())){
            List<CtrContract> contractList = ctrContractClient.findByContractNoLikes(sealUsage.getContractNo());
            if (CollectionUtils.isNotEmpty(contractList)){
                CtrContract contract = contractList.get(0);
                sealUsage.setOwnRegion(contract.getOwningRegion());
            }
        }
        String contractNo = "";
        // 对方企业名称
        String customerName = sealUsage.getCustomerName();
        entity.setRealApproveId(sealUsage.getRealApproveId());
        if (approveId != null) {
            PmApprove pmApprove = pmApproveService.getEntity(approveId);
            if (pmApprove != null) {
                sealUsage.setApplyUserId(pmApprove.getCreateUserId());
                sealUsage.setApplyUserName(pmApprove.getCreateUserName());
            }
        }
        if (sealType != null && sealType.contains(BasConstants.DICT_TYPE_SEAL_FS)) {
            sealUsage.setFinSealFlg(true);
        }
        String contents = JsonUtil.obj2Json(sealUsage);
        if (id != null && id != 0L) {
            entity.setId(id);
        }
        entity.setContents(contents);
        //生成标题
        String subject = "";
        String sealTypeNameStr = "";
        if (sealType.contains(",")) {
            String[] splitItem = sealType.split(",");
            for (String item : splitItem) {
                String value = DictUtil.getValue(BasConstants.DICT_TYPE_SEAL_TYPE, item);
                if (StringUtils.isNotBlank(value)){
                    sealTypeNameStr = sealTypeNameStr + value + ",";
                }
            }
            if (sealTypeNameStr.length() > 1) {
                sealTypeNameStr = sealTypeNameStr.substring(0, sealTypeNameStr.length() - 1);
            }
        } else {
            sealTypeNameStr = DictUtil.getValue(BasConstants.DICT_TYPE_SEAL_TYPE, sealType);
        }

        String companyNameStr = "";
        // 业务盖章 摘要显示对方企业
        if (sealUsage.getBusinessFlg()) {
            companyNameStr = customerName;
            contractNo = sealUsage.getContractNo();
        }else {
            if (StringUtils.isNotBlank(companyName) && companyName.contains(",")) {
                String[] splitCompanyName = companyName.split(",");
                for (String name : splitCompanyName) {
                    String value = DictUtil.getValue(BasConstants.DICT_TYPE_CUSTOMER_NAME, name);
                    if (StringUtils.isNotBlank(value)){
                        companyNameStr = companyNameStr + value + ",";
                    }
                }
                if (companyNameStr.length() > 1) {
                    companyNameStr = companyNameStr.substring(0, companyNameStr.length() - 1);
                }
            } else {
                companyNameStr = DictUtil.getValue(BasConstants.DICT_TYPE_CUSTOMER_NAME, companyName);
            }
        }

        //格式 : 印章类型 公司名称 合同类型 印章日期
        Boolean onLineFlg = false;
        if (StringUtils.isNotBlank(customerName)){
            BsCompany bsCompany = bsCompanyClient.findByCompanyName(customerName);
            if (Objects.nonNull(bsCompany)){
                onLineFlg = bsCompany.getOnLineFlg();
            }
        }
        String companyName1 = RuleUtils.companyNameSubString(companyNameStr);
        String value = DictUtil.getValue(BasConstants.DICT_TYPE_SEAL_FILE_TYPE, sealUsage.getFileType());
        String businessType = sealUsage.getBusinessType();
        String contractType = "";
        if(StringUtils.equals("B",businessType)) {
            contractType = "采购";
        } else if(StringUtils.equals("S",businessType)) {
            contractType = "销售";
        } else if(StringUtils.equals("ZY-XS",businessType)) {
            contractType = "自营销售";
        } else if(StringUtils.equals("ZY-CG",businessType)) {
            contractType = "自营采购";
        }
        if (Boolean.TRUE.equals(onLineFlg) && Boolean.TRUE.equals(sealUsage.getBusinessFlg())) {
            CtrContract entity1 = ctrContractClient.getEntity(sealUsage.getContractId());
            String ourCompanyName = RuleUtils.companyNameSubString(entity1.getOurCompanyName());
            subject =  SubjectPmUtil.formatSubject("[线上化]"+sealTypeNameStr,contractNo,companyName1+"-"+ourCompanyName,contractType);
        } else {
           // 摘要存储印章类型时存空字符串，不存入 null
           sealTypeNameStr =  sealTypeNameStr == null ? "" : sealTypeNameStr;
            subject =SubjectPmUtil.formatSubject(sealTypeNameStr,contractNo,companyName1,sealUsage.getFileName(),value);
            Long contractId = sealUsage.getContractId();
            String ourCompanyName = "";
            if(contractId != null) {
                // 业务盖章
                CtrContract entity1 = ctrContractClient.getEntity(sealUsage.getContractId());
                if(Objects.nonNull(entity1)) {
                    ourCompanyName = "-" + RuleUtils.companyNameSubString(entity1.getOurCompanyName());
                    subject =  SubjectPmUtil.formatSubject(sealTypeNameStr,contractNo,companyName1 + ourCompanyName,contractType);
                }
            } else {
                // 普通盖章
                ourCompanyName = StringUtils.isNotBlank(customerName) ? "-" + sealUsage.getCustomerName() : "";
                contractNo = sealUsage.getContractNo();
                subject =  SubjectPmUtil.formatSubject(sealTypeNameStr,contractNo,companyName1 + ourCompanyName,sealUsage.getFileName(),value);
            }
        }
        entity.setSubject(subject);
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(sealUsage.getApplyUserId());
        if (deptByUserId!=null) {
            entity.setDeptId(deptByUserId.getDeptId());
        }
        entity.setFileId(sealUsage.getFileId());
        entity.setEnterpriseId(sealUsage.getEnterpriseId());
        save(entity);
    }

    /**
     * 印章外借审批内容Json处理
     */
    private void parseSealBorrow(IPmEntity pmEntity, PmApproveContents entity) throws ApplicationException {
        SealBorrow sealBorrow = (SealBorrow) pmEntity;
        Long id = sealBorrow.getId();
        Long approveId = ((SealBorrow) pmEntity).getApproveId();
        sealBorrow.setApproveId(approveId);
        String itemType = sealBorrow.getItemType();
        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            sealBorrow.setApplyUserId(pmApprove.getCreateUserId());
            sealBorrow.setApplyUserName(pmApprove.getCreateUserName());
        }
        if (itemType != null && itemType.contains(BasConstants.DICT_TYPE_SEAL_FS)) {
            sealBorrow.setFinSealFlg(true);
        }
        String contents = JsonUtil.obj2Json(sealBorrow);
        if (id != null && id != 0L) {
            entity.setId(id);
        }
        entity.setContents(contents);
        //生成标题
        String subject = "";
        String startDateStr = DateOperator.formatDate(sealBorrow.getStartDate(), false);
        String endDateStr = DateOperator.formatDate(sealBorrow.getEndDate(), false);
        String itemTypeNameStr = "";
        if (itemType.contains(",")) {
            String[] splitItem = itemType.split(",");
            for (String item : splitItem) {
                itemTypeNameStr += DictUtil.getValue(BasConstants.DICT_TYPE_ITEM_TYPE, item) + ",";
            }
            if (itemTypeNameStr.length() > 1) {
                itemTypeNameStr = itemTypeNameStr.substring(0, itemTypeNameStr.length() - 1);
            }
        } else {
            itemTypeNameStr = DictUtil.getValue(BasConstants.DICT_TYPE_ITEM_TYPE, sealBorrow.getItemType());
        }
        String companyNameStr = DictUtil.getValue(BasConstants.DICT_TYPE_CUSTOMER_NAME, sealBorrow.getCompanyName());
        String companyName= RuleUtils.companyNameSubString(companyNameStr);
        subject = SubjectPmUtil.formatSubject(itemTypeNameStr,companyName,sealBorrow.getAddress(),sealBorrow.getReason(),startDateStr + "至" + endDateStr);
        entity.setSubject(subject);
        entity.setFileId(sealBorrow.getFileId());
        PmApprove entity1 = pmApproveService.getEntity(sealBorrow.getApproveId());
        if (entity1 != null) {
            SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(sealBorrow.getApplyUserId());
            entity.setDeptId(deptByUserId.getDeptId());
        }
        entity.setEnterpriseId(sealBorrow.getEnterpriseId());
        entity = save(entity);
    }

    /**
     * 车辆使用审批内容处理
     * @param pmEntity
     * @param entity
     * @throws ApplicationException
     */
    private void parseVehicleUse(IPmEntity pmEntity, PmApproveContents entity) throws ApplicationException {
        VehicleUse vehicleUse = (VehicleUse) pmEntity;
        Long id = vehicleUse.getId();//编号
        Long approveId = ((VehicleUse) pmEntity).getApproveId();//审批编号
        vehicleUse.setApproveId(approveId);
        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            vehicleUse.setApplyUserId(pmApprove.getCreateUserId());
            vehicleUse.setApplyUserName(pmApprove.getCreateUserName());
        }
        String contents = JsonUtil.obj2Json(vehicleUse);
        if (id != null && id != 0L) {
            entity.setId(id);
        }
        entity.setContents(contents);
        //生成标题
        String startDateStr = DateOperator.formatDate(new Date(), false);
        String endDateStr = DateOperator.formatDate(vehicleUse.getDepartDate(), false);
        String PlatNumberValue = vehicleUse.getPlateNumber();
        String subject = String.format("%s %s", PlatNumberValue, "[" + startDateStr + "至" + endDateStr + "]");
        entity.setSubject(subject);
        entity.setFileId(vehicleUse.getFileId());
        PmApprove entity1 = pmApproveService.getEntity(vehicleUse.getApproveId());
        if (entity1 != null) {
            SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(vehicleUse.getApplyUserId());
            entity.setDeptId(deptByUserId.getDeptId());
        }
        entity.setEnterpriseId(vehicleUse.getEnterpriseId());
        save(entity);
    }

    /**
     * 申请牌号内容处理
     * @param pmEntity
     * @param entity
     */
    private void parseApplyBrand(IPmEntity pmEntity, PmApproveContents entity) {
        BasBrand bsBrand = (BasBrand) pmEntity;
        Long approveId = bsBrand.getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);
        bsBrand.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(bsBrand);
        entity.setContents(contents);
        //生成标题
        boolean b = Objects.isNull(bsBrand.getProductCd());
        BsProductType productType = null;
        if(Boolean.FALSE.equals(b)){
            productType  = bsProductTypeClient.findProductTypeCode(bsBrand.getProductCd());
        }
        String s = SubjectPmUtil.formatSubject(productType.getTypeName(), bsBrand.getBrandNumber());
        entity.setSubject(s);
        entity.setApproveId(approveId);
        pmApproveContentsDao.save(entity);
    }

    /**
     * 保证金付款处理
     * @param pmEntity
     * @param entity
     */
    private void parseApplyPay(IPmEntity pmEntity, PmApproveContents entity) {
        ApplyPay applyPay = (ApplyPay) pmEntity;
        Long approveId = applyPay.getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);
        applyPay.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyPay);
        entity.setContents(contents);
        //生成标题
        entity.setSubject("[保证金付款]"+"/"+applyPay.getContractNo()+"/"+applyPay.getOurCompanyName());
        entity.setApproveId(approveId);
        pmApproveContentsDao.save(entity);
    }
    /**
     * 成为合伙人审批内容处理
     *
     * @param pmEntity
     * @param entity
     */
    private void parseApplyPartner(IPmEntity pmEntity, PmApproveContents entity) {
        ApplyPartner applyPartner = (ApplyPartner) pmEntity;
        Long approveId = ((ApplyPartner) pmEntity).getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);
        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            applyPartner.setApplyUserId(pmApprove.getCreateUserId());
            applyPartner.setApplyUserName(pmApprove.getCreateUserName());
        }
        applyPartner.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyPartner);
        entity.setContents(contents);
        //生成标题
        entity.setSubject("申请成为合伙人");
        entity.setApproveId(approveId);
        entity.setEnterpriseId(applyPartner.getEnterpriseId());
        pmApproveContentsDao.save(entity);
        entity.setApplyName(BasConstants.APPLY_PARTNER);
    }

    /**
     * 意见反馈审批内容处理
     *
     * @param pmEntity
     * @param entity
     */
    private void parseApplyFeedback(IPmEntity pmEntity, PmApproveContents entity) {
        Feedback feedback = (Feedback) pmEntity;
        Long approveId = ((Feedback) pmEntity).getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);
        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            feedback.setApplyUserId(pmApprove.getCreateUserId());
            feedback.setApplyUserName(pmApprove.getCreateUserName());
        }
        feedback.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(feedback);
        entity.setContents(contents);
        //生成标题
        entity.setSubject("意见反馈");
        entity.setApproveId(approveId);
        entity.setEnterpriseId(feedback.getEnterpriseId());
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(feedback.getApplyUserId());
        entity.setDeptId(deptByUserId.getDeptId());
        pmApproveContentsDao.save(entity);
        entity.setApplyName(BasConstants.APPLY_FEEDBACK);
    }

    /**
     * cfca内容处理
     *
     * @param pmEntity
     * @param entity
     */
    private void parseApplyCfca(IPmEntity pmEntity, PmApproveContents entity) {
        ApplyCfca applyCfca = (ApplyCfca) pmEntity;
        logger.info("parseApplyCfca:{}", JsonUtil.obj2Json(applyCfca));
        Long approveId = ((ApplyCfca) pmEntity).getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);
        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            applyCfca.setApplyUserId(pmApprove.getCreateUserId());
            applyCfca.setApplyUserName(pmApprove.getCreateUserName());
        }
        applyCfca.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyCfca);
        entity.setContents(contents);
        //生成标题
        entity.setSubject(applyCfca.getCompanyName());
        entity.setApproveId(approveId);
        entity.setEnterpriseId(applyCfca.getEnterpriseId());
        entity.setFileId(applyCfca.getFileId());
        logger.info("parseApplyCfca---1:{}", JsonUtil.obj2Json(applyCfca));
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(applyCfca.getApplyUserId());
        logger.info("parseApplyCfca----deptByUserId:{}", JsonUtil.obj2Json(deptByUserId));
        if (deptByUserId != null) {
            entity.setDeptId(deptByUserId.getDeptId());
        }
        pmApproveContentsDao.save(entity);
        entity.setApplyName(BasConstants.APPLY_CFCA);
    }

    /**
     * 服务费率内容处理
     *
     * @param pmEntity
     * @param entity
     */
    private void parseApplyRate(IPmEntity pmEntity, PmApproveContents entity) {
        ApplyRate applyRate = (ApplyRate) pmEntity;
        Long approveId = applyRate.getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);
        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            applyRate.setApplyUserId(pmApprove.getCreateUserId());
            applyRate.setApplyUserName(pmApprove.getCreateUserName());
        }
        applyRate.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyRate);
        entity.setContents(contents);
        //生成标题
        entity.setSubject("服务费率申请");
        entity.setApproveId(approveId);
        entity.setEnterpriseId(applyRate.getEnterpriseId());
        entity.setFileId(applyRate.getFileId());
        pmApproveContentsDao.save(entity);
        entity.setApplyName(BasConstants.APPLY_RATE);
    }

    /**
     * 回款周期审批
     *
     * @param pmEntity
     * @param entity
     */
    private void parseApplyCreditCycle(IPmEntity pmEntity, PmApproveContents entity) {
        ApplyCreditCycle applyCreditCycle = (ApplyCreditCycle) pmEntity;
        Long approveId = applyCreditCycle.getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);
        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            applyCreditCycle.setApplyUserId(pmApprove.getCreateUserId());
            applyCreditCycle.setApplyUserName(pmApprove.getCreateUserName());
        }
        applyCreditCycle.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyCreditCycle);
        entity.setContents(contents);
        //生成标题
        entity.setSubject("回款周期申请");
        entity.setApproveId(approveId);
        entity.setEnterpriseId(applyCreditCycle.getEnterpriseId());
        entity.setFileId(applyCreditCycle.getFileId());
        pmApproveContentsDao.save(entity);
        entity.setApplyName(BasConstants.APPLY_CREDIT_CYCLE);
    }

    private void parseApplyCancel(IPmEntity pmEntity, PmApproveContents entity) {
        ApplyCancel2Vo applyCancel = (ApplyCancel2Vo) pmEntity;
        Long approveId = applyCancel.getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);

        applyCancel.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyCancel);
        entity.setContents(contents);
        //生成标题
        entity.setSubject("作废申请");
        entity.setApproveId(approveId);
        entity.setFileId(applyCancel.getFileId());
        pmApproveContentsDao.save(entity);
        entity.setApplyName(BasConstants.APPLY_CANCEL);
    }


    private void ApplyTerminalPick(IPmEntity pmEntity, PmApproveContents entity) {
        ApplyTerminalPick applyPick = (ApplyTerminalPick) pmEntity;
        Long approveId = applyPick.getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);

        applyPick.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyPick);
        entity.setContents(contents);
        //生成标题
        String companyName1 = RuleUtils.companyNameSubString(applyPick.getCompanyName());
        entity.setSubject(companyName1);
        entity.setApproveId(approveId);
        entity.setFileId(applyPick.getFileId());
        entity.setDeptId(applyPick.getDeptId());
        pmApproveContentsDao.save(entity);
        entity.setApplyName(BasConstants.APPLY_CANCEL);
    }

    private void applyInternalTransferMoney(IPmEntity pmEntity, PmApproveContents entity){
        ApplyInternalTransferMoney applyInternalTransferMoney = (ApplyInternalTransferMoney) pmEntity;
        Long approveId = applyInternalTransferMoney.getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);

        applyInternalTransferMoney.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyInternalTransferMoney);
        entity.setContents(contents);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sealDate = sdf.format(applyInternalTransferMoney.getApplyDate());
        //生成标题
        entity.setSubject("["+"内部资金拆借申请"+""+""+applyInternalTransferMoney.getRepaymentAmount()+ RuleUtils.monetaryUnit+""+""+sealDate+"]");
        entity.setApproveId(approveId);
        entity.setFileId(applyInternalTransferMoney.getFileId());
        entity.setFileId(applyInternalTransferMoney.getFileId());
        pmApproveContentsDao.save(entity);
        entity.setApplyName(BasConstants.PROCESS_APPLY_INTERAL_TRANSFER_MONEY);

    }

    private void sealUsageDCSX(IPmEntity pmEntity, PmApproveContents entity){
        SealUsageDCSX sealUsageDCSX = (SealUsageDCSX) pmEntity;
        Long approveId = sealUsageDCSX.getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);

        sealUsageDCSX.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(sealUsageDCSX);
        entity.setContents(contents);
        //生成标题
        String companyName1 = RuleUtils.companyNameSubString(sealUsageDCSX.getCompanyName());
        String companyName2 = RuleUtils.companyNameSubString(sealUsageDCSX.getOurCompanyName());
        String company="";
        if(StringUtils.isNotBlank(companyName1)&&StringUtils.isNotBlank(companyName2)){
            company=companyName1+"-"+companyName2;
        }
        String s = SubjectPmUtil.formatSubject(sealUsageDCSX.getContractNo(),company,SubjectPmUtil.formatMoney(sealUsageDCSX.getTotalAmount() , RuleUtils.monetaryUnit));
        entity.setSubject(s);
        entity.setApproveId(approveId);
        entity.setFileId(sealUsageDCSX.getFileId());
        entity.setRealApproveId(sealUsageDCSX.getRealApproveId());
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(sealUsageDCSX.getApplyUserId());
        if (deptByUserId != null) {
            entity.setDeptId(deptByUserId.getDeptId());
        }
        pmApproveContentsDao.save(entity);
        CtrContract ctrContract = new CtrContract();
        ctrContract.setContractNo(sealUsageDCSX.getContractNo());
        CtrContract contract = ctrContractClient.findByContractNo(ctrContract);
        if (Objects.nonNull(contract) && StringUtils.equals(BasConstants.APPLY_SEAL_USAGE_DCTP, contract.getBusinessType())) {
            entity.setApplyName(BasConstants.APPLY_SEAL_USAGE_DCTP);
        } else {
            entity.setApplyName(BasConstants.APPLY_SEAL_USAGE_DCSX);
        }
    }


    private void parseApplyVip(IPmEntity pmEntity, PmApproveContents entity) {
        ApplyVip applyVip = (ApplyVip) pmEntity;
        Long approveId = applyVip.getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);

        applyVip.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyVip);
        entity.setContents(contents);

        //生成标题
        entity.setSubject("[Vip申请]" + "" + applyVip.getCompanyName());
        entity.setApproveId(approveId);
        entity.setFileId(applyVip.getFileId());
        pmApproveContentsDao.save(entity);
        entity.setApplyName(BasConstants.PROCESS_APPLY_VIP);
    }


    private void applyPromoteVip(IPmEntity pmEntity, PmApproveContents entity) {
        ApplyPromoteVip applyPromoteVip = (ApplyPromoteVip) pmEntity;
        Long approveId = applyPromoteVip.getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);

        applyPromoteVip.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyPromoteVip);
        entity.setContents(contents);
        BsCompany company = bsCompanyClient.findCompany(applyPromoteVip.getCompanyId());
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(company.getMatchUserId());
        entity.setDeptId(deptByUserId.getDeptId());
        //生成标题
        entity.setSubject("[Vip提额申请]" + "" + applyPromoteVip.getCompanyName());
        entity.setApproveId(approveId);
        pmApproveContentsDao.save(entity);
        entity.setApplyName(BasConstants.PROCESS_APPLY_VIP_PROMOTE);
    }

    private void applyVipReceive(IPmEntity pmEntity, PmApproveContents entity) {
        ApplyVipReceive applyVipReceive = (ApplyVipReceive) pmEntity;
        Long approveId = applyVipReceive.getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);

        applyVipReceive.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyVipReceive);
        entity.setContents(contents);
        //生成标题
        entity.setSubject("[Vip提额收款申请]" + "" + applyVipReceive.getCompanyName());
        BsCompany company = bsCompanyClient.findCompany(applyVipReceive.getCompanyId());
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(company.getMatchUserId());
        entity.setDeptId(deptByUserId.getDeptId());
        entity.setApproveId(approveId);
        pmApproveContentsDao.save(entity);
        entity.setApplyName(BasConstants.PROCESS_APPLY_VIP_RECEIVE);
    }


    private void applyVipInvoice(IPmEntity pmEntity, PmApproveContents entity) {
        ApplyVipInvoice applyVipInvoice = (ApplyVipInvoice) pmEntity;
        Long approveId = applyVipInvoice.getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);

        applyVipInvoice.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyVipInvoice);
        entity.setContents(contents);
        //生成标题
        entity.setSubject("[Vip提额开票申请]" + "" + applyVipInvoice.getCompanyName());
        entity.setApproveId(approveId);
        pmApproveContentsDao.save(entity);
        entity.setApplyName(BasConstants.PROCESS_APPLY_VIP_RECEIVE);
    }


    private void applyVipMainReceive(IPmEntity pmEntity, PmApproveContents entity) {
        ApplyVipMainReceive applyVipMainReceive = (ApplyVipMainReceive) pmEntity;
        Long approveId = applyVipMainReceive.getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);

        applyVipMainReceive.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyVipMainReceive);
        entity.setContents(contents);
        //生成标题
        entity.setSubject("[Vip费用收款申请]" + "" + applyVipMainReceive.getCompanyName());
        BsCompany company = bsCompanyClient.findCompany(applyVipMainReceive.getCompanyId());
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(company.getMatchUserId());
        applyVipMainReceive.setDeptId(deptByUserId.getDeptId());
        entity.setApproveId(approveId);
        pmApproveContentsDao.save(entity);
        entity.setApplyName(BasConstants.PROCESS_APPLY_VIP_MAIN_RECEIVE);
    }


    private void applyVipMinInvoice(IPmEntity pmEntity, PmApproveContents entity) {
        ApplyVipMainInvoice applyVipMainInvoice = (ApplyVipMainInvoice) pmEntity;
        Long approveId = applyVipMainInvoice.getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);

        applyVipMainInvoice.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyVipMainInvoice);
        entity.setContents(contents);
        //生成标题
        entity.setSubject("[Vip费用开票申请]" + "" + applyVipMainInvoice.getCompanyName());
        BsCompany company = bsCompanyClient.findCompany(applyVipMainInvoice.getCompanyId());
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(company.getMatchUserId());
        entity.setDeptId(deptByUserId.getDeptId());
        entity.setApproveId(approveId);
        pmApproveContentsDao.save(entity);
        entity.setApplyName(BasConstants.PROCESS_APPLY_VIP_MAIN_INVOICE);
    }


    /**
     * 争议审批
     * @param pmEntity
     * @param entity
     */
    private void parseDispute(IPmEntity pmEntity, PmApproveContents entity) {
        ApplyDispute applyDispute = (ApplyDispute) pmEntity;
        Long approveId = applyDispute.getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);
        applyDispute.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyDispute);
        entity.setContents(contents);

        //生成标题
        entity.setSubject("[争议审批]" + "" + applyDispute.getSellContractNo());
        entity.setApproveId(approveId);
        entity.setFileId(applyDispute.getFileId());
        entity.setApplyName(BasConstants.PROCESS_APPLY_DISPUTE);
        pmApproveContentsDao.save(entity);

    }

    /**
     * 线上化申请
     * @param pmEntity
     * @param entity
     */
    private void applyOnline(IPmEntity pmEntity, PmApproveContents entity) {
        ApplyCompanyOnline applyCompanyOnline = (ApplyCompanyOnline) pmEntity;
        Long approveId = applyCompanyOnline.getApproveId();
        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);
        applyCompanyOnline.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyCompanyOnline);
        entity.setContents(contents);

        //生成标题
        entity.setSubject("[企业线上化审批]" + "" + applyCompanyOnline.getCompanyName());
        entity.setApproveId(approveId);
        entity.setApplyName(BasConstants.PROCESS_APPLY_ONLINE);
        pmApproveContentsDao.save(entity);

    }

    /**
     * cfca开户合并
     *
     * @param pmEntity
     * @param entity
     */
    private void onlineAccountOpening(IPmEntity pmEntity, PmApproveContents entity) throws ApplicationException {
        ApplyWxCfca applyCompanyInfo = (ApplyWxCfca) pmEntity;
        Long approveId = ((ApplyWxCfca) pmEntity).getApproveId();

        // 根据approveId判断并替换entity
        replaceIfExistPmApproveContent(approveId, entity);

        applyCompanyInfo.setApproveId(approveId);
        String companyName = applyCompanyInfo.getCompanyName();

        PmApprove pmApprove = pmApproveService.getEntity(approveId);
        if (pmApprove != null) {
            applyCompanyInfo.setApplyUserId(pmApprove.getCreateUserId());
            applyCompanyInfo.setApplyUserName(pmApprove.getCreateUserName());
        }
        // 判断是否有申请过绑定委托授权，有就说明是追加经办人
        Boolean isHaveEntrustUser = applyEntrustClient.findIsHaveEntrustUserByCompanyName(applyCompanyInfo.getCompanyName());
        String additionalManager = "";
        // 如果有多个的话，说明是
        if(isHaveEntrustUser){
            additionalManager = "[追加经办人]";
        }
        //生成标题
        String subject = "";
        //格式 : 公司名称
        String companyName1 = RuleUtils.companyNameSubString(companyName);
        subject =  SubjectPmUtil.formatSubject(companyName1,additionalManager);
        entity.setSubject(subject);
        entity.setEnterpriseId(applyCompanyInfo.getEnterpriseId());

        //--------------------------------------------
        ApplyWxCfca applyCfca = (ApplyWxCfca) pmEntity;
        logger.info("parseApplyCfca:{}", JsonUtil.obj2Json(applyCfca));
        // 根据approveId判断并替换entity

        applyCompanyInfo.setApproveId(approveId);
        String contents = JsonUtil.obj2Json(applyCompanyInfo);
        entity.setContents(contents);
        entity.setApproveId(approveId);
        entity.setEnterpriseId(applyCompanyInfo.getEnterpriseId());
        applyWxCfcaClient.save(applyCompanyInfo);
        logger.info("parseApplyCfca---1:{}", JsonUtil.obj2Json(applyCompanyInfo));
        SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(applyCompanyInfo.getApplyUserId());
        logger.info("parseApplyCfca----deptByUserId:{}", JsonUtil.obj2Json(deptByUserId));
        if (deptByUserId != null) {
            entity.setDeptId(deptByUserId.getDeptId());
        }
        pmApproveContentsDao.save(entity);
        entity.setApplyName(BasConstants.ONLINE_ACCOUNT_OPENING);
        entity = save(entity);
        logger.info("parseApplyCompanyInfo : " + JsonUtil.obj2Json(entity));
    }



    /**
     * 驳回或撤回的时候，重新发起审批 pmApproveContent会生成2条数据，
     * 所以保存的时候先用approveId检查一下pmApproveContent是否已存在，存在的话取原来的数据复制，
     * 更新数据
     *
     * @param approveId
     * @param entity
     */
    private void replaceIfExistPmApproveContent(Long approveId, PmApproveContents entity) {
        PmApproveContents byApproveId = pmApproveContentsDao.findByApproveId(approveId);
        if (byApproveId != null) {
            BeanUtils.copyProperties(byApproveId, entity);
        }
    }
}
