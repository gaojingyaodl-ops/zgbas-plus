package com.spt.bas.server.service.impl;

import cn.hutool.core.date.DateUtil;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.LogisticsEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.logistics.service.ICtrLogisticsDeliveryService;
import com.spt.bas.server.service.*;
import com.spt.bas.server.stock.service.StockDetailFacade;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component("applyDeliveryInService")
@Transactional(readOnly = true)
public class ApplyDeliveryInServiceImpl extends BaseService<ApplyDeliveryIn> implements IApplyDeliveryInService, IPmService, IPmApproveListener {
    private static final ScheduledExecutorService SCHEDULED_POOL = Executors.newScheduledThreadPool(10);
    @Autowired
    private ApplyDeliveryInDao applyDeliveryInDao;
    @Autowired
    private IApplyProductDetailService productDetailService;
    @Autowired
    private ICtrContractService contractService;
    @Autowired
    private ICtrContractUpdateService ctrContractUpdateService;
    @Autowired
    private ICtrContractOphisService contractOphisService;
    @Autowired
    private CtrProductDao productDao;
    @Autowired
    private ICtrProductService productService;
    @Autowired
    private StockDetailFacade stockDetailFacade;
    @Autowired
    private IApproveDealService approveDealService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private PmApproveDao pmApproveDao;
    @Autowired
    private ICtrContractApplyService contractApplyService;
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private CtrContractApplyDao ctrContractApplyDao;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private IPmApproveService iPmApproveService;
    @Autowired
    private RtPushServiceImpl rtPushService;
    @Resource
    private ICtrLogisticsDeliveryService logisticsDeliveryService;
    @Autowired
    private CtrOutInLedgerDao ctrOutInLedgerDao;
    @Resource
    private CtrLogisticsDao ctrLogisticsDao;
    @Autowired
    private ApplyProductDetailDao applyProductDetailDao;
    @Autowired
    private PmProcessDao pmProcessDao;
    @Autowired
    private CtrProductDao ctrProductDao;

    @Override
    public BaseDao<ApplyDeliveryIn> getBaseDao() {
        return applyDeliveryInDao;
    }

    @Override
    public Class<ApplyDeliveryIn> getEntityClazz() {
        return ApplyDeliveryIn.class;
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyDeliveryIn entity = applyDeliveryInDao.findOne(approve.getBizId());
            Long contractId = entity.getContractId();
            CtrContract contract = ctrContractDao.findOne(contractId);
            contract.setCarrier(entity.getCarrier());
            ctrContractDao.save(contract);
            if (contract.getApplyCancelFlg()) {
                throw new ApplicationException("请驳回，该合同处于合同作废阶段!");
            }
            List<ApplyProductDetail> productList = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_I);
            // 是否背靠背业务
            boolean isBB = false;
            String businessType = contract.getBusinessType();
            if (BasConstants.BUSINESS_TYPE_ZY_BB.equals(businessType) || BasConstants.BUSINESS_TYPE_KC_CG.equals(businessType) || BasConstants.BUSINESS_TYPE_ZY_TP.equals(businessType)) {
                isBB = true;
            }
            //当前实际入库的数量
            BigDecimal curRealInNumber = BigDecimal.ZERO;
            for (ApplyProductDetail apd : productList) {
                if (apd.getCurNumber().compareTo(BigDecimal.ZERO) > 0) {
                    //更新合同明细的已入库数量
                    CtrProduct product = productService.getEntity(apd.getCtrProductId());
                    product.setWarehouseNumber(product.getWarehouseNumber().add(apd.getCurNumber()));
                    product.setWarehouseAddr(apd.getWarehouseAddr());
                    curRealInNumber = curRealInNumber.add(apd.getCurNumber());
                    product.setCurApproveNumber(BigDecimal.ZERO);
                    product.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_N);
                    productDao.save(product);
                    if (!isBB) { // 如果不是背靠背业务且不是代采托盘 不修改原逻辑
                        StockDetailRequest request = StockDetailRequest.build(product, apd);
                        request.setApplyType(BasConstants.APPLY_TYPE_I);
                        request.setCtrContractId(contractId);
                        request.setApplyNo(entity.getApplyNo());
                        request.setWarehousePosition(entity.getWarehousePosition());
                        request.setWarehouseBatchNo(entity.getWarehouseBatchNo());
                        request.setApplyId(entity.getId());
                        request.setApproveId(approve.getId());
                        request.setWrapSpecs(apd.getWrapSpecs());
                        request.setWarehousePos(apd.getWarehousePos());
                        request.setWarehouseAddr(apd.getWarehouseAddr());
                        request.setDealNumber(apd.getCurNumber());
                        request.setStockType(entity.getStockType());
                        request.setSpotType(entity.getSpotType());
                        stockDetailFacade.saveDeliveryIn(request);
                    }
                }
            }
            // 修改合同库存数量
            ctrContractUpdateService.addWarehouseNumber(contractId, curRealInNumber, approve.getApproveNo(), entity.getWarehouseInDate());
            // 适配历史审批中的审批单添加操作记录：过段时间可以去除
            Date createdDate = approve.getCreatedDate();
            if (createdDate != null) {
                String format = DateUtil.format(createdDate, "yyyy-MM-dd");
                if (BasConstants.NEW_ADD_HIS_START_DATE.compareTo(format) >= 0) {
                    // 添加合同历史状态记录
                    contractOphisService.addHis(BasConstants.APPLY_TYPE_I, contractId, approve, entity.getWarehouseInDate());
                }
            }

            //更新合同入库时间
            CtrContractApply contractApply = contractApplyService.findByContractId(contractId);
            Date realWarehoseDate = contractApply.getRealWarehoseDate();
            Date warehouseInDate = entity.getWarehouseInDate();
            if (realWarehoseDate == null || warehouseInDate.after(realWarehoseDate)) {
                contractApply.setRealWarehoseDate(warehouseInDate);
                contractApplyService.save(contractApply);
            }

            logger.info("入库完成释放:合同总数量:{},入库数量:{},本次入库数量:{}", contract.getTotalNumber(), contract.getWarehouseNumber(), curRealInNumber);
            // 入库完成释放 采购额度、预付款额度
            if (contract.getCreditFlg() != null && contract.getCreditFlg() && contract.getTotalNumber().compareTo(contract.getWarehouseNumber()) <= 0) {
                logger.info("{} {} 入库完成，释放采购额度、预付款额度 {}", contract.getContractNo(), contract.getCompanyName(), contract.getTotalAmount());
                Long companyId = contract.getCompanyId();
                BsCompany company = bsCompanyService.getEntity(companyId);
                //释放 已使用的采购额度
                company.setUsedSupplierPurchaseAmount(company.getUsedSupplierPurchaseAmount().subtract(contract.getTotalAmount()));
                //释放 已使用的预付款额度
                company.setUsedSupplierPrepayAmount(company.getUsedSupplierPrepayAmount().subtract(contract.getTotalAmount()));
                bsCompanyService.save(company);
            }

            // doZyDeliveryIn(entity, contract);

            // 推送入库信息至融拓
            rtPushService.pushDeliveryInToRt(entity, approve, productList);

            // 签署入库单据
            this.signDeliveryIn(entity, contract, approve);

            this.autoStartVirtualDeliveryIn(entity, contract, approve);
        }

    }

    /**
     * 发起审批，更新当前审批数量字段
     */
    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyDeliveryIn entity = applyDeliveryInDao.findOne(approve.getBizId());
        CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(entity.getContractId());
        CtrContract ctrContract = ctrContractDao.findOne(entity.getContractId());
        Long contractId = entity.getContractId();
        Long processId = approve.getProcessId();
        CtrContractApplyVo vo = new CtrContractApplyVo();
        BigDecimal curNumber = BigDecimal.ZERO;
        BigDecimal curApplyNumberTotal = BigDecimal.ZERO;
        List<ApplyProductDetail> productList = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_I);
        for (ApplyProductDetail apd : productList) {
            CtrProduct product = productService.getEntity(apd.getCtrProductId());
            BigDecimal remainNumber = product.getDealNumber().subtract(product.getWarehouseNumber().add(product.getCurApproveNumber()));
            if (remainNumber.compareTo(BigDecimal.ZERO) > 0) {
                //更新合同明细的已入库数量
                curNumber = product.getCurApproveNumber().add(apd.getCurNumber());
                if (curNumber.compareTo(BigDecimal.ZERO) < 0) {
                    curNumber = BigDecimal.ZERO;
                }
                product.setCurApproveNumber(curNumber);
                productDao.save(product);
            }
            curApplyNumberTotal = curApplyNumberTotal.add(apd.getCurNumber());
        }
        vo.setDealNumber(curApplyNumberTotal);
        // amount = 当前入库数量 + 已入库数量
        BigDecimal amount = vo.getDealNumber().add(contractApply.getApplyWarehouseNumber());
        // subtract = 总数量 - （当前入库数量 + 已入库数量）
        BigDecimal subtract = ctrContract.getTotalNumber().subtract(amount);
        if (subtract.compareTo(BigDecimal.ZERO) < 0) {
            throw new ApplicationException("申请数量有误！");
        }
        vo.setApplyType(BasConstants.APPLY_TYPE_I);
        vo.setContractId(contractId);
        contractApplyService.updateCtrContractApply(vo);

        //获得合同信息
        CtrContract contract = contractService.getEntity(contractId);
        //获得该合同的数量
        BigDecimal totalNumber = contract.getTotalNumber();
        //获得实际入库数量
        BigDecimal warehouseNumber = contract.getWarehouseNumber();

        // 修改审批代办事项表
        //合同总数量等于 实际入库数量
        //已经全部入库
        if (totalNumber.equals(warehouseNumber)) {
            //删除对应的入库通知
            approveDealService.removeApproveDeal(processId, String.valueOf(contractId));
        } else {
            BigDecimal inNumber = warehouseNumber.add(curNumber);
            if (inNumber.equals(totalNumber)) {
                //删除对应的入库通知
                approveDealService.removeApproveDeal(processId, String.valueOf(contractId));
            } else {
                //仍有剩余入库数量时 需修改 摘要上的入库商量
                BigDecimal dealNumber = totalNumber.subtract(inNumber);
                //获得摘要
                ApproveDealRequest request = new ApproveDealRequest();
                request.setContractId(contractId);
                request.setDealType(BasConstants.APPLY_TYPE_I);
                request.setProcessId(processId);
                request.setTotalAmount(null);
                request.setTotalNumber(dealNumber);
                //修改摘要
                approveDealService.updateSubject(request);
            }
        }
        // 添加合同历史状态记录
        contractOphisService.addHis(BasConstants.APPLY_TYPE_I, contractId, approve, entity.getWarehouseInDate());
    }

    /**
     * 驳回，将当前审批数量字段设为0
     */
    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyDeliveryIn entity = applyDeliveryInDao.findOne(approve.getBizId());
        List<ApplyProductDetail> productList = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_I);
        BigDecimal totalNumber = BigDecimal.ZERO;
        for (ApplyProductDetail apd : productList) {
            //根据合同明细找到与其对应的申请明细
            //出入库数量
            totalNumber = totalNumber.add(apd.getCurNumber());
            CtrProduct product = productService.getEntity(apd.getCtrProductId());
            BigDecimal subtractNumber = product.getCurApproveNumber().subtract(apd.getCurNumber());
            if (subtractNumber.compareTo(BigDecimal.ZERO) < 0) {
                subtractNumber = BigDecimal.ZERO;
            }
            product.setCurApproveNumber(subtractNumber);
            productDao.save(product);
        }
        ApplyDeliveryIn deliveryIn = applyDeliveryInDao.findOne(approve.getBizId());
        //更新CtrContractApply中数据
        CtrContractApplyVo vo = new CtrContractApplyVo();
        Date deliveryInDate = applyDeliveryInDao.findLastDelivery(entity.getContractId());
        vo.setDealNumber(totalNumber.negate());
        vo.setContractId(deliveryIn.getContractId());
        vo.setApplyType(BasConstants.APPLY_TYPE_I);
        vo.setRealDate(deliveryInDate);
        contractApplyService.updateCtrContractApply(vo);
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        //申请单作废，还原入库数据，合同状态改为已签约
        ApplyDeliveryIn entity = applyDeliveryInDao.findOne(vo.getBizId());
        //当前实际入库的数量
        BigDecimal curRealInNumber = BigDecimal.ZERO;
        Long contractId = entity.getContractId();
        List<ApplyProductDetail> list = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_I);
        for (ApplyProductDetail apd : list) {
            CtrProduct product = productService.getEntity(apd.getCtrProductId());
            product.setWarehouseNumber(product.getWarehouseNumber().subtract(apd.getCurNumber()));
            productDao.save(product);
            curRealInNumber = curRealInNumber.add(apd.getCurNumber());

//            StockDetailRequest request = StockDetailRequest.build(product, apd);
//            request.setApplyType(BasConstants.APPLY_TYPE_I);
//            request.setCtrContractId(contractId);
//            request.setApplyId(entity.getId());
//            request.setBack(true);
//            request.setApproveId(vo.getApproveId());
//            request.setDealNumber(apd.getCurNumber());
//            request.setStockType(entity.getStockType());
//            request.setSpotType(entity.getSpotType());
//            stockDetailFacade.saveDeliveryIn(request);
        }

        //合同状态改成已收货
        Date deliveryInDate = applyDeliveryInDao.findLastDelivery(entity.getContractId());
        PmApprove approve = pmApproveService.getEntity(entity.getApproveId());
        if (StringUtils.equals(BasConstants.APPROVE_STATUS_D, entity.getStatus())) {
            ctrContractUpdateService.addWarehouseNumber(contractId, curRealInNumber.negate(), approve.getApproveNo(), deliveryInDate);
        }
//        CtrContractOphisRequest request = new CtrContractOphisRequest();
//        request.setApplyType(BasConstants.APPLY_TYPE_I);
//        request.setCtrContractId(contractId);
//        request.setCancel(true);
//        request.setRemark(approve.getSubject());
//        request.setCreateUserId(vo.getUserId());
//        request.setCreateUserName(vo.getUserName());
//        request.setApproveId(vo.getApproveId());
//        request.setContractGroup("CTR");
//        contractOphisService.addHis(request);
        //更新CtrContractApply中数据

        CtrContractApplyVo applyVo = new CtrContractApplyVo();
        applyVo.setDealNumber(curRealInNumber.negate());
        applyVo.setContractId(entity.getContractId());
        applyVo.setApplyType(BasConstants.APPLY_TYPE_I);
        applyVo.setRealDate(deliveryInDate);
        contractApplyService.updateCtrContractApply(applyVo);
    }

    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        ApplyDeliveryIn deliveryIn = null;
        ApplyProductDetailSaveVo vo = new ApplyProductDetailSaveVo();
        vo.setApplyType(BasConstants.APPLY_TYPE_I);
        if (pmEntity instanceof ApplyDeliveryInVo) {
            ApplyDeliveryInVo inVo = (ApplyDeliveryInVo) pmEntity;
            //新增入库信息
            deliveryIn = new ApplyDeliveryIn();
            BeanUtils.copyProperties(inVo, deliveryIn);
            PmApprove entity = iPmApproveService.getEntity(inVo.getApproveId());
            if (entity != null) {
                SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(entity.getCreateUserId());
                deliveryIn.setDeptId(deptByUserId.getDeptId());
            }
            if (deliveryIn.getId() == 0) {
                // 生成入库编号
                String contractNo = composeApplyNo(deliveryIn.getContractNo());
                deliveryIn.setApplyNo(contractNo);
            }
            CtrContract contract = contractService.getEntity(deliveryIn.getContractId());
            logger.info("applyDeliveryIn.saveEntity:{}", JsonUtil.obj2Json(contract));
            // 付款单设置businessType 作为流程条件内容
            if (StringUtils.equals(contract.getBusinessTypeDcsx(), BasConstants.BUSINESS_TYPE_DCSX)) {
                deliveryIn.setBusinessType(BasConstants.BUSINESS_TYPE_DCSX);
            }
            deliveryIn = applyDeliveryInDao.save(deliveryIn);

            //新增商品明细
            vo.setApplyId(deliveryIn.getId());
            productDetailService.saveDetailBatch(inVo.getLstInsert(), inVo.getLstUpdate(), inVo.getLstDelete(), vo);
        } else {
            ApplyDeliveryIn entity = (ApplyDeliveryIn) pmEntity;
            deliveryIn = applyDeliveryInDao.save(entity);
            //保存商品明细中企业id
            vo.setApplyId(deliveryIn.getId());
            vo.setEnterpriseId(deliveryIn.getEnterpriseId());
            productDetailService.saveBatchEnterpriseId(vo);
        }
        // 入库完成时增加出入库台账报表
        if (StringUtils.equals(deliveryIn.getStatus(), PmConstants.APPROVE_STATUS_D)) {
            CtrContract ctrContractBuy = ctrContractDao.findOne(deliveryIn.getContractId());
            if (Objects.nonNull(ctrContractBuy)) {
                CtrContract ctrContractSell = ctrContractDao.findByApproveIdAndContractType(ctrContractBuy.getApproveId(), BasConstants.CONTRACT_TYPE_S);
                if (Objects.nonNull(ctrContractSell)) {
                    CtrOutInLedger ctrOutInLedger = new CtrOutInLedger();
                    BeanUtils.copyProperties(ctrContractBuy, ctrOutInLedger);
                    ctrOutInLedger.setId(null);
                    ctrOutInLedger.setOperTime(new Date());
                    ctrOutInLedger.setOperation(BasConstants.DICT_OUT_IN_LEDGER_TYPE_2);
                    ctrOutInLedger.setPrice(ctrContractBuy.getDealPrice());
                    ctrOutInLedger.setSourceId(deliveryIn.getId());
                    // 获取当初入库数量
                    ApplyProductDetail applyProductDetail =  applyProductDetailDao.findByApplyIdAndApplyType(deliveryIn.getId(), BasConstants.APPLY_TYPE_I);
                    if (Objects.nonNull(applyProductDetail)) {
                        ctrOutInLedger.setWarehouseNumber(applyProductDetail.getCurNumber());
                    }
                    // 计算结余数量（结余数量 = 合同数量 - 已入库数量）
                    List<CtrOutInLedger> ctrInLedgerList = ctrOutInLedgerDao.findByOperationAndContractNo(BasConstants.DICT_OUT_IN_LEDGER_TYPE_2, ctrContractBuy.getContractNo());
                    BigDecimal surplusNumber = ctrContractBuy.getTotalNumber().subtract(ctrOutInLedger.getWarehouseNumber());
                    if (CollectionUtils.isNotEmpty(ctrInLedgerList)) {
                        for (CtrOutInLedger inLedger : ctrInLedgerList) {
                            surplusNumber = surplusNumber.subtract(inLedger.getWarehouseNumber());
                        }
                    }
                    ctrOutInLedger.setSurplusNumber(surplusNumber);
                    // 获取公司传真
                    BsCompany bsCompany = bsCompanyService.getEntity(ctrContractBuy.getCompanyId());
                    if (Objects.nonNull(bsCompany)) {
                        ctrOutInLedger.setCompanyFax(bsCompany.getCompanyFax());
                    }
                    // 获取实际合同单号
                    List<CtrLogistics> ctrLogisticsList = ctrLogisticsDao.findByBuyContractNo(ctrOutInLedger.getContractNo());
                    if (CollectionUtils.isNotEmpty(ctrLogisticsList)) {
                        ctrOutInLedger.setRealContractNo(ctrLogisticsList.get(0).getSupplierNo());
                    }
                    ctrOutInLedgerDao.save(ctrOutInLedger);
                }
            }
        }
        return deliveryIn;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyDeliveryIn vo = (ApplyDeliveryIn) pmEntity;
            List<ApplyProductDetail> list = productDetailService.findApplyDetail(vo.getId(), BasConstants.APPLY_TYPE_I);
            StringBuffer productNameAndBrand = new StringBuffer("");
            BigDecimal sumNumber = BigDecimal.ZERO;
            for (ApplyProductDetail applyProductDetail : list) {
                String realInNumber = NumberUtil.formatNumber(applyProductDetail.getCurNumber(), "#.###");
                String[] title = applyProductDetail.getProductCd().split("_");
                if ("SL".equals(title[0])) {
                    if (StringUtils.isEmpty(applyProductDetail.getWarehouseName())) {
                        productNameAndBrand.append(applyProductDetail.getProductName() + "/" + applyProductDetail.getBrandNumber() + "/" + realInNumber + RuleUtil.weightUnit);
                    } else {
                        productNameAndBrand.append(applyProductDetail.getProductName() + "/" + applyProductDetail.getBrandNumber() + "/" + applyProductDetail.getWarehouseName() + "/" + realInNumber + RuleUtil.weightUnit);
                    }
                } else {
                    if (StringUtils.isEmpty(applyProductDetail.getWarehouseName())) {
                        productNameAndBrand.append(applyProductDetail.getProductName() + "/" + realInNumber + RuleUtil.weightUnit);
                    } else {
                        productNameAndBrand.append(applyProductDetail.getProductName() + "/" + applyProductDetail.getWarehouseName() + "/" + realInNumber + RuleUtil.weightUnit);
                    }
                }
                sumNumber = sumNumber.add(applyProductDetail.getDealNumber());
            }
            String companyName = vo.getCompanyName();
            String productNameAndBrandStr = productNameAndBrand.toString();
            if (productNameAndBrand.length() > 0) {
                productNameAndBrandStr = productNameAndBrand.substring(0, productNameAndBrand.length() - 1);
            }
            String deliveryType = DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, vo.getDeliveryType());
            String companyName1 = RuleUtil.companyNameSubString(companyName);
            final CtrContractChooseVo byContractId = contractService.findByContractId(vo.getContractId());
            String companyName2 = RuleUtil.companyNameSubString(byContractId.getOurCompanyName());
            String company = "";
            if (StringUtils.isNotBlank(companyName1) && StringUtils.isNotBlank(companyName2)) {
                company = companyName1 + "-" + companyName2;
            }
            return SubjectUtil.formatSubject(vo.getContractNo(), productNameAndBrandStr, company, deliveryType);
        }
        return null;
    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        applyDeliveryInDao.updateFileId(id, fileId);
    }

    @Override
    public List<ApplyDeliveryIn> findByContractId(Long contractId) {
        return applyDeliveryInDao.findByContractId(contractId);
    }

    @Override
    @ServerTransactional
    public void updateApplyStatus(Long contractId) {
        applyDeliveryInDao.updateApplyStatus(contractId);
    }

    @Override
    public List<ApplyDeliveryIn> findDeliveryInContractId(Long contractId) {
        return applyDeliveryInDao.findDeliveryInContractId(contractId);
    }

    @Override
    public ApplyDeliveryIn generateApplyNo(Long contractId) {
        ApplyDeliveryIn entity = new ApplyDeliveryIn();
        CtrContract contract = ctrContractDao.findOne(contractId);
        String contractNo = contract.getContractNo();
        if (Objects.nonNull(contract)) {
            entity.setApplyNo(composeApplyNo(contractNo));
            entity.setBillNoPre(composeBillNoPre(contractNo));
        }
        return entity;
    }

    @Override
    @ServerTransactional
    public void doSignLogistics() {
        List<ApplyDeliveryIn> unSignLogistics = applyDeliveryInDao.findUnSignLogistics();
        if (org.springframework.util.CollectionUtils.isEmpty(unSignLogistics)) {
            return;
        }
        logger.info("deliveryIn doSignLogistics size:{}", unSignLogistics.size());
        Map<Long, CtrContract> contractMap = ctrContractDao.findByIds(unSignLogistics.stream()
                        .map(ApplyDeliveryIn::getContractId)
                        .collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(CtrContract::getId, c -> c));
        Map<Long, PmApprove> approveMap = pmApproveDao.findByIds(unSignLogistics.stream()
                        .map(ApplyDeliveryIn::getApproveId)
                        .collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(PmApprove::getId, p -> p));
        for (ApplyDeliveryIn entity : unSignLogistics) {
            try {
                this.signDeliveryIn(entity, contractMap.get(entity.getContractId()), approveMap.get(entity.getApproveId()));
            } catch (Exception e) {
                logger.error("doSignLogistics error", e);
            }
        }
        logger.info("deliveryIn doSignLogistics success!");
    }

    private String composeApplyNo(String contractNo) {
        return LogisticsEnum.DELIVERY_IN.getLogisticsPrefix() + composeBillNoPre(contractNo);
    }

    private String composeBillNoPre(String contractNo) {
        List<ApplyDeliveryIn> deliveryIn = applyDeliveryInDao.findByContractNo(contractNo);
        String fmt = String.format("-%d", deliveryIn.size() + 1);
        contractNo = contractNo.replaceAll("\\D", "");
        return contractNo + fmt;
    }

    /**
     * 签署入库单据
     *
     * @param entity
     * @param contract
     * @param approve
     * @return
     */
    private void signDeliveryIn(ApplyDeliveryIn entity, CtrContract contract, PmApprove approve) throws ApplicationException {
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

    /**
     * 库存采购业务
     * 供应商入库到代采方（青光），审批完成后，自动发起代采方-我方（苏高新）的入库申请
     *
     * @param deliveryIn
     * @param contract
     * @param approve
     */
    private void autoStartVirtualDeliveryIn(ApplyDeliveryIn deliveryIn, CtrContract contract, PmApprove approve) {
        Long virtualId = contract.getVirtualId();
        String virtualType = contract.getVirtualType();
        if (Objects.nonNull(virtualId) && StringUtils.equals(BasConstants.STOCK_VIRTUAL_KC, virtualType)) {
            SCHEDULED_POOL.schedule(() -> {
                List<CtrContract> contractList = ctrContractDao.findCtrContractByVirtualId(virtualId);
                CtrContract otherVirtualContract = contractList.stream()
                        .filter(c -> !Objects.equals(contract.getId(), c.getId()))
                        .filter(c -> c.getWarehouseNumber().compareTo(BigDecimal.ZERO) == 0)
                        .findAny().orElse(null);
                if (Objects.nonNull(otherVirtualContract)) {
                    CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(otherVirtualContract.getId());
                    if (Objects.nonNull(contractApply) && contractApply.getApplyWarehouseNumber().compareTo(BigDecimal.ZERO) == 0) {
                        startDeliveryInApply(otherVirtualContract, deliveryIn, approve);
                    }
                }
            }, 5, TimeUnit.SECONDS);
        }
    }

    private void startDeliveryInApply(CtrContract targetContract, ApplyDeliveryIn deliveryIn, PmApprove approve) {
        try {
            PmProcess deliveryOutProcess = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_CODE_IN, targetContract.getEnterpriseId());
            PmApproveSaveVo startVo = new PmApproveSaveVo();
            ApplyDeliveryInVo inVo = new ApplyDeliveryInVo();
            List<ApplyProductDetail> lstInsert = new ArrayList<>();
            BeanUtils.copyProperties(deliveryIn, inVo);
            inVo.setId(0L);
            inVo.setContractId(targetContract.getId());
            inVo.setContractNo(targetContract.getContractNo());
            inVo.setApproveId(null);
            inVo.setApplyNo(null);
            inVo.setStatus(BasConstants.APPLY_TYPE_A);
            inVo.setCompanyId(targetContract.getCompanyId());
            inVo.setCompanyName(targetContract.getCompanyName());
            inVo.setBusinessNo(targetContract.getContractNo());
            ApplyProductDetail productDetail = productDetailService.findApplyDetail(deliveryIn.getId(), BasConstants.APPLY_TYPE_I).stream().findFirst().orElse(null);
            CtrProduct product = ctrProductDao.findByCtrContractId(targetContract.getId()).stream().findFirst().orElse(null);
            if (Objects.nonNull(productDetail) && Objects.nonNull(product)) {
                ApplyProductDetail detail = new ApplyProductDetail();
                BeanUtils.copyProperties(productDetail, detail);
                detail.setId(0L);
                detail.setDealNumber(product.getDealNumber());
                detail.setDealPrice(product.getDealPrice());
                detail.setTotalPrice(product.getTotalPrice());
                detail.setApplyId(null);
                detail.setCurNumber(product.getDealNumber());
                detail.setApplyDeliveryOutId(null);
                detail.setCtrProductId(product.getId());
                lstInsert.add(detail);
            }
            inVo.setLstInsert(lstInsert);
            inVo.setLstUpdate(new ArrayList<>());
            inVo.setLstDelete(new ArrayList<>());
            String entityJson = JsonUtil.obj2Json(inVo);
            startVo.setBizEntityJson(entityJson);
            startVo.setProcessId(deliveryOutProcess.getId());
            startVo.setDeptId(targetContract.getDeptId());
            startVo.setMode(BasConstants.APPROVE_STATUS_A);
            startVo.setStatus(BasConstants.APPROVE_STATUS_A);
            startVo.setApproveId(0L);
            startVo.setUserId(approve.getCreateUserId());
            startVo.setUserName(approve.getCreateUserName());
            startVo.setEnterpriseId(approve.getEnterpriseId());
            startVo.setAutoStartMessage("自动发起入库申请");
            startVo.setAutoStartFlgReal(true);
            pmApproveService.startFlow(startVo);
        } catch (Exception e) {
            logger.error("startDeliveryInApply error", e);
        }
    }
}

