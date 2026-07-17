package com.spt.bas.server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractSaveService;
import com.spt.bas.server.dao.ApplyInventoryVirtualDao;
import com.spt.bas.server.dao.StockInventoryDao;
import com.spt.bas.server.dao.StockVirtualDao;
import com.spt.bas.server.filter.IAutoSealPdfSignFilter;
import com.spt.bas.server.service.IApplyInventoryVirtualService;
import com.spt.bas.server.service.IBsCompanyDcsxService;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.bas.server.util.BasBusinessUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IBsKeySequenceService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 库存采购申请
 * @Author MoonLight
 * @Date 2024/8/20 11:06
 * @Version 1.0
 */
@Slf4j
@Transactional(readOnly = true)
@Component("applyInventoryVirtualService")
public class ApplyInventoryVirtualServiceImpl extends BaseService<ApplyInventoryVirtual> implements IApplyInventoryVirtualService, IPmService, IPmApproveListener {
    private static final ScheduledExecutorService SCHEDULED_POOL = Executors.newScheduledThreadPool(10);
    @Resource
    private ApplyInventoryVirtualDao applyInventoryVirtualDao;
    @Resource
    private StockVirtualDao stockVirtualDao;
    @Resource
    private StockInventoryDao stockInventoryDao;
    @Resource
    private IBsKeySequenceService bsKeySequenceService;
    @Resource
    private ICtrContractSaveService contractSaveService;
    @Resource
    private PmProcessDao pmProcessDao;
    @Resource
    private IPmApproveService pmApproveService;
    @Resource
    private IAutoSealPdfSignFilter autoSealPdfSignFilter;
    @Resource
    private IBsCompanyDcsxService bsCompanyDcsxService;
    @Resource
    private IBsCompanyService bsCompanyService;

    // 链条-0  [A]->B->[C] => [A-C]
    private static final Integer CHAIN_TYPE_0 = 0;
    // 链条-1  [A->B]->C => [A-B]
    private static final Integer CHAIN_TYPE_1 = 1;
    // 链条-2  A->[B->C] => [B-C]
    private static final Integer CHAIN_TYPE_2 = 2;
    private static final String PREFIX_KUB = "KUB";
    private static final String PREFIX_KUX = "KUX";
    private static final String PREFIX_KLB = "KLB";

    @Override
    public BaseDao<ApplyInventoryVirtual> getBaseDao() {
        return applyInventoryVirtualDao;
    }

    @Override
    public void doStepIn(PmApprove approve) throws ApplicationException {
        log.info("applyInventoryVirtualService doStepIn approveNo:{}", approve.getApproveNo());
        ApplyInventoryVirtual entity = applyInventoryVirtualDao.findOne(approve.getBizId());
        Long templateId = entity.getTemplateId();
        String contentTemplateId = entity.getContentTemplateId();
        if ((Objects.isNull(templateId) || templateId == -1) && StrUtil.isBlank(contentTemplateId)){
            throw new ApplicationException("请选择合同模板或自行上传合同附件!");
        }
    }

    /**
     * 执行审批步骤
     *
     * @param approve
     * @param nextStep
     */
    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyInventoryVirtual entity = applyInventoryVirtualDao.findOne(approve.getBizId());

            // 保存采购合同
            String pairCode = bsKeySequenceService.getNextKey(BasConstants.KEY_PAIR_CODE, approve.getEnterpriseId());
            CtrContract linkContract;
            CtrContract chainContract = null;
            if (StringUtils.isNotBlank(entity.getBuyOurCompanyName())) {
                linkContract = saveVirtualContract(entity, approve, CHAIN_TYPE_2, pairCode);
                chainContract = saveVirtualContract(entity, approve, CHAIN_TYPE_1, pairCode);
            } else {
                linkContract = saveVirtualContract(entity, approve, CHAIN_TYPE_0, pairCode);
            }

            // 发起盖章申请
            autoInitiatedSealUsage(linkContract, approve);
            autoInitiatedSealUsage(chainContract, approve);

            StockInventory stockVirtual = stockInventoryDao.findByStockVirtualNo(entity.getStockVirtualNo());
            stockVirtual = Objects.isNull(stockVirtual) ? new StockInventory() : stockVirtual;
            BeanUtils.copyProperties(entity, stockVirtual,"id");
            stockVirtual.setVirtualContractId(linkContract.getId());
            stockVirtual.setVirtualContractNo(linkContract.getContractNo());
            stockVirtual.setInventoryStatus(BasConstants.STOCK_VIRTUAL_STATUS_F);
            stockInventoryDao.save(stockVirtual);
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
        ApplyInventoryVirtual entity = applyInventoryVirtualDao.findOne(vo.getBizId());
        if (Objects.isNull(entity)) {
            return;
        }
        StockInventory stockVirtual = stockInventoryDao.findByStockVirtualNo(entity.getStockVirtualNo());
        if (Objects.isNull(stockVirtual)) {
            return;
        }
        stockVirtual.setInventoryStatus(BasConstants.STOCK_VIRTUAL_STATUS_C);
        stockInventoryDao.save(stockVirtual);

        List<StockVirtual> virtualList = stockVirtualDao.findBizStockVirtual(stockVirtual.getId(), BasConstants.STOCK_VIRTUAL_KC);
        if (CollectionUtils.isEmpty(virtualList)) {
            return;
        }
        virtualList.forEach(v -> v.setVirtualStatus(BasConstants.STOCK_VIRTUAL_STATUS_C));
        stockVirtualDao.saveAll(virtualList);
    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyInventoryVirtual entity = applyInventoryVirtualDao.findOne(approve.getBizId());
        if (Objects.isNull(entity)) {
            return;
        }
        StockInventory stockVirtual = stockInventoryDao.findByStockVirtualNo(entity.getStockVirtualNo());
        if (Objects.isNull(stockVirtual)) {
            return;
        }
        stockVirtual.setInventoryStatus(BasConstants.STOCK_VIRTUAL_STATUS_C);
        stockInventoryDao.save(stockVirtual);

        List<StockVirtual> virtualList = stockVirtualDao.findBizStockVirtual(stockVirtual.getId(), BasConstants.STOCK_VIRTUAL_KC);
        if (CollectionUtils.isEmpty(virtualList)) {
            return;
        }
        virtualList.forEach(v -> v.setVirtualStatus(BasConstants.STOCK_VIRTUAL_STATUS_C));
        stockVirtualDao.saveAll(virtualList);
    }

    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity instanceof ApplyInventoryVirtual) {
            ApplyInventoryVirtual entity = (ApplyInventoryVirtual) pmEntity;
            if (Objects.isNull(entity.getId())
                    || entity.getId() == 0L
                    || StringUtils.equals(BasConstants.APPROVE_STATUS_C, entity.getStatus())) {
                String suffix = BasBusinessUtil.composeContractNoSuffix(entity.getEnterpriseId());
                entity.setStockVirtualNo(PREFIX_KLB + suffix);
            }
            if (StringUtils.isNotBlank(entity.getBuyOurCompanyName())){
                String buyOurCompanyName = parseOurCompanyName(entity.getEnterpriseId(), entity.getBuyOurCompanyName());
                BsCompany buyOurCompany = bsCompanyService.findByCompanyName(buyOurCompanyName);
                entity.setBuyOurCompanyId(Objects.nonNull(buyOurCompany) ? buyOurCompany.getId() : 0L);
            }
            entity = applyInventoryVirtualDao.save(entity);
            return entity;
        }
        return null;
    }

    /**
     * 标题
     *
     * @param pmEntity
     * @param pmProcess
     */
    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess pmProcess) {
        if (pmEntity instanceof ApplyInventoryVirtual) {
            ApplyInventoryVirtual entity = (ApplyInventoryVirtual) pmEntity;
            String stockVirtualNo = entity.getStockVirtualNo();
            String productName = entity.getProductName();
            String factoryName = entity.getFactoryName();
            String companyName = entity.getCompanyName();
            String matchUserName = entity.getMatchUserName();
            String dealPriceStr = NumberUtil.formatNumber(entity.getDealPrice(), "#.##");
            String minSellPriceStr = NumberUtil.formatNumber(entity.getMinSellPrice(), "#.##");
            String dealNumberStr = NumberUtil.formatNumber(entity.getDealNumber(), "#.##");
            String totalAmountStr = NumberUtil.formatNumber(entity.getTotalAmount(), "#.##");
            return SubjectUtil.formatSubject("[库存采购]", stockVirtualNo, productName, factoryName,
                    companyName, "单价" + dealPriceStr, "指导价" + minSellPriceStr, "数量" + dealNumberStr, "采购总价" + totalAmountStr, matchUserName);
        }
        return "";
    }

    private String parseOurCompanyName(Long enterpriseId, String companyCode) {
        if (StringUtils.isBlank(companyCode)) {
            return "";
        }
        String ourCompanyName = BsCompanyOurUtil.getValue(enterpriseId, companyCode);
        if (StringUtils.isNotBlank(ourCompanyName)) {
            return ourCompanyName;
        } else {
            BsCompanyDcsx fundCompany = bsCompanyDcsxService.findByCompanyCd(companyCode);
            return Objects.nonNull(fundCompany) ? fundCompany.getCompanyName() : "";
        }
    }

    @Override
    @ServiceTransactional
    public void updateFileId(Long id, String fileId) {
        applyInventoryVirtualDao.updateContentTemplateId(id, fileId);
    }

    /**
     * 生成库存采购合同
     *
     * @param entity
     * @param approve
     * @throws ApplicationException
     */
    private CtrContract saveVirtualContract(ApplyInventoryVirtual entity, PmApprove approve, Integer chainType, String pairCode) throws ApplicationException {
        CtrContract virtualContract = assembleContract(entity, approve, chainType, pairCode);
        List<ApplyProductDetail> productDetails = assembleProductDetail(entity, virtualContract);
        return contractSaveService.saveContract(virtualContract, productDetails, approve);
    }

    /**
     * 组装采购合同参数
     *
     * @param virtual   库存采购申请
     * @param approve   审批单
     * @param chainType 链条类型
     * @return 合同
     */
    private CtrContract assembleContract(ApplyInventoryVirtual virtual, PmApprove approve, Integer chainType, String pairCode) {
        CtrContract entity = new CtrContract();
        entity.setTotalNumber(virtual.getDealNumber());
        entity.setTotalAmount(virtual.getTotalAmount());
        entity.setDealPrice(virtual.getDealPrice());
        entity.setVirtualId(virtual.getId());
        entity.setVirtualType(BasConstants.STOCK_VIRTUAL_KC);
        entity.setApproveTransportAmount(virtual.getTransportCost());
        entity.setApproveWarehouseAmount(virtual.getWarehouseCost());
        entity.setApproveStevedorage(virtual.getStevedorage());
        entity.setBsTemplateContractId(virtual.getTemplateId());
        entity.setBuyContentFileId(virtual.getContentTemplateId());
        entity.setContractType(BasConstants.CONTRACT_TYPE_B);
        entity.setDeliveryDateTo(virtual.getDeliveryDate());
        entity.setDeliveryDateFrom(virtual.getDeliveryDate());
        entity.setAttachDeliveryTime(virtual.getArrivalTimeExt());
        entity.setRemark(virtual.getRemark());
        entity.setPayType(virtual.getPayType());
        entity.setSource(BasConstants.APPLY_TYPE_B);
        entity.setPayFullTime(virtual.getPayFullTime());
        entity.setPayBondTime(virtual.getPayBondTime());
        entity.setAppointPayFullTime(virtual.getPayFullTime());
        entity.setBondRate(virtual.getPayRate());
        entity.setBondAmount(virtual.getPayBondAmount());
        entity.setBusinessType(BasConstants.BUSINESS_TYPE_KC_CG);
        entity.setBusinessKind(BasConstants.BUSINESS_TYPE_KC_CG);
        entity.setCompanyPiccFlg(false);
        entity.setMatchCreditFlg(false);
        entity.setPremium(virtual.getRaisePrice());
        entity.setApproveId(approve.getId());
        entity.setEnterpriseId(approve.getEnterpriseId());
        entity.setContractTime(new Date());
        entity.setDeliveryType(virtual.getDeliveryType());
        entity.setDeliveryMode(virtual.getDeliveryMode());
        entity.setPayType(virtual.getPayType());
        entity.setProductStatus(BasConstants.PRODUCT_STATUS_P);
        entity.setStatus(BasConstants.CONTRACTSTATUS_B);
        entity.setMatchUserId(virtual.getMatchUserId());
        entity.setMatchUserName(virtual.getMatchUserName());
        entity.setDeliveryAddr(virtual.getDeliveryAddr());
        entity.setApplySource(BasConstants.APP_CODE);
        entity.setQualityStandard(virtual.getQualityStandard());
        entity.setPairCode(pairCode);
        String contractNoSuffix = virtual.getStockVirtualNo().replaceAll("\\D", "");
        entity.setContractNo(PREFIX_KUB + contractNoSuffix);
        if (Objects.equals(CHAIN_TYPE_1, chainType)) {
            // 链条-1  [A->B]->C => [A-B]
            entity.setCompanyId(virtual.getCompanyId());
            entity.setCompanyName(virtual.getCompanyName());
            entity.setOurCompanyName(parseOurCompanyName(approve.getEnterpriseId(), virtual.getBuyOurCompanyName()));
        } else if (Objects.equals(CHAIN_TYPE_2, chainType)) {
            // 链条-2  A->[B->C] => [B-C]
            entity.setContractNo(PREFIX_KUX + contractNoSuffix);
            String buyOurCompanyName = parseOurCompanyName(approve.getEnterpriseId(), virtual.getBuyOurCompanyName());
            BsCompany buyOurCompany = bsCompanyService.findByCompanyName(buyOurCompanyName);
            entity.setCompanyId(Objects.nonNull(buyOurCompany) ? buyOurCompany.getId() : 0L);
            entity.setCompanyName(buyOurCompanyName);
            entity.setOurCompanyName(parseOurCompanyName(approve.getEnterpriseId(), virtual.getOurCompanyName()));
            BigDecimal raisePrice = Objects.isNull(virtual.getRaisePrice()) ? BigDecimal.ZERO : virtual.getRaisePrice();
            entity.setDealPrice(virtual.getDealPrice().add(raisePrice));
            entity.setTotalAmount(virtual.getDealNumber().multiply(entity.getDealPrice()).setScale(2, RoundingMode.HALF_UP));
        } else {
            // 链条-0  [A]->B->[C] => [A-C]
            entity.setCompanyId(virtual.getCompanyId());
            entity.setCompanyName(virtual.getCompanyName());
            entity.setOurCompanyName(parseOurCompanyName(approve.getEnterpriseId(), virtual.getOurCompanyName()));
        }
        return entity;
    }

    /**
     * 组装商品明细
     *
     * @param virtual         库存采购申请
     * @param virtualContract 合同
     * @return 商品申请明细
     */
    private List<ApplyProductDetail> assembleProductDetail(ApplyInventoryVirtual virtual, CtrContract virtualContract) {
        List<ApplyProductDetail> productDetails = new ArrayList<>();
        ApplyProductDetail detail = new ApplyProductDetail();
        detail.setProductCd(virtual.getProductCd());
        detail.setProductName(virtual.getProductName());
        detail.setBrandNumber(virtual.getBrandNumber());
        detail.setFactoryName(virtual.getFactoryName());
        detail.setDealNumber(virtual.getDealNumber());
        detail.setDealPrice(virtualContract.getDealPrice());
        detail.setTotalPrice(virtualContract.getTotalAmount());
        detail.setEnterpriseId(virtual.getEnterpriseId());
        detail.setApplyId(virtual.getId());
        detail.setWrapSpecs(virtual.getWrapSpecs());
        detail.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_N);
        productDetails.add(detail);
        return productDetails;
    }

    private void autoInitiatedSealUsage(CtrContract contract, PmApprove approve) {
        if (Objects.isNull(contract) || Objects.isNull(approve)) {
            return;
        }
        SCHEDULED_POOL.schedule(() -> {
            try {
                //合同盖章签署通知
                PmProcess sealUsageProcess = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_APPLY_SEAL_USAGE_BUSINESS, approve.getEnterpriseId());
                PmApproveSaveVo startVo = new PmApproveSaveVo();
                SealUsage usage = new SealUsage();
                usage.setFileId(contract.getBuyContentFileId());
                usage.setEnterpriseId(approve.getEnterpriseId());
                usage.setSealType(BasConstants.DICT_TYPE_SEAL_TYPE_TS);
                usage.setSealDate(new Date());
                String companyCd = BsCompanyOurUtil.getKey(approve.getEnterpriseId(), contract.getOurCompanyName());
                if (StringUtils.isNotBlank(companyCd)) {
                    usage.setCompanyName(companyCd);
                } else {
                    usage.setCompanyName(contract.getOurCompanyName());
                }
                usage.setContractNo(contract.getContractNo());
                usage.setCustomerName(contract.getCompanyName());
                usage.setTotalAmount(contract.getTotalAmount());
                usage.setFileType(BasConstants.DICT_TYPE_FILE_TYPE_BS);
                usage.setApplyUserId(approve.getCreateUserId());
                usage.setApplyUserName(approve.getCreateUserName());
                usage.setContractId(contract.getId());
                usage.setBusinessType(contract.getContractType());
                usage.setRealApproveId(approve.getId());
                usage.setBusinessFlg(true);
                usage.setVirtualType(contract.getContractNo().replaceAll("\\d", ""));
                String entityJson = JsonUtil.obj2Json(usage);
                startVo.setBizEntityJson(entityJson);
                startVo.setProcessId(sealUsageProcess.getId());
                startVo.setDeptId(approve.getDeptId());
                startVo.setMode("A");
                startVo.setStatus(BasConstants.APPROVE_STATUS_A);
                startVo.setApproveId(0L);
                startVo.setUserId(approve.getCreateUserId());
                startVo.setUserName(approve.getCreateUserName());
                startVo.setEnterpriseId(approve.getEnterpriseId());
                startVo.setAutoStartMessage("预算审批完成，自动发起盖章申请");
                startVo.setAutoStartFlgReal(true);
                PmApprove pmApprove = pmApproveService.startFlow(startVo);
                autoSealPdfSignFilter.generateVirtualSealPDFSign(pmApprove, contract);
            } catch (Exception e) {
                logger.info("autoInitiatedSealUsage error:{}", e.getMessage());
            }
        }, 5, TimeUnit.SECONDS);
    }
}
