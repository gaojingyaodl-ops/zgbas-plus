package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.ApplyBuyVo;
import com.spt.bas.client.vo.ApplyProductDetailSaveVo;
import com.spt.bas.client.vo.ApplyProductDetailVo;
import com.spt.bas.client.vo.ApproveFormPrintVo;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractSaveService;
import com.spt.bas.server.dao.ApplyBuyDao;
import com.spt.bas.server.service.IApplyBuyService;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.bas.server.service.IStockDetailPresellService;
import com.spt.bas.server.util.BasBusinessUtil;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.bas.server.util.TemplateContentUtility;
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
import com.spt.tools.core.constants.CommonErrorId;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component("applyBuyService")
@Transactional(readOnly = true)
public class ApplyBuyServiceImpl extends BaseService<ApplyBuy> implements IApplyBuyService, IPmService, IPmApproveListener {
    private static final ScheduledExecutorService BUY_SCHEDULED_POOL = Executors.newScheduledThreadPool(10);
    @Autowired
    private ApplyBuyDao applyBuyDao;
    @Autowired
    private IApplyProductDetailService productDetailService;
    @Autowired
    private ICtrContractSaveService contractSaveService;
    @Autowired
    private IStockDetailPresellService stockDetailPresellService;
    @Autowired
    private IPmApproveService approveService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private PmProcessDao pmProcessDao;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IPmProcessService pmProcessService;
    @Override
    public BaseDao<ApplyBuy> getBaseDao() {
        return applyBuyDao;
    }

    @Override
    public Class<ApplyBuy> getEntityClazz() {
        return ApplyBuy.class;
    }

    @Override
    @ServerTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyBuy buy = applyBuyDao.findOne(approve.getBizId());
            List<ApplyProductDetail> list = productDetailService.findApplyDetail(buy.getId(), buy.getApplyType());
            //保存合同主表
            CtrContract entity = new CtrContract();
            BeanUtils.copyProperties(buy, entity);
            entity.setTransportAmount(buy.getTransportAmount());
            entity.setWarehouseAmount(buy.getWarehouseAmount());
            entity.setContractType(BasConstants.CONTRACT_TYPE_B);
            entity.setCarrier(buy.getCarrier());
            entity.setContractStatus(BasConstants.CONTRACTSTATUS_S);
            entity.setSource(buy.getApplyType());
            entity.setDeliveryDateFrom(buy.getArrivalTime());
            entity.setDeliveryDateTo(buy.getArrivalTime());//到货时间
            entity.setAttachDeliveryTime(buy.getArrivalTimeExt());
            entity.setPayMode(buy.getPayKind());
            entity.setBreachAmount(BigDecimal.ZERO);
            entity.setReceiveBreachAmount(BigDecimal.ZERO);
            entity.setServiceBilledAmount(BigDecimal.ZERO);
            entity.setServiceAmount(BigDecimal.ZERO);
            entity.setReceiveServiceAmount(BigDecimal.ZERO);
            if (StringUtils.isBlank(entity.getDeliveryAddr())) {
                entity.setDeliveryAddr(buy.getShippingAddr());//配送地址
            }
            entity.setBusinessKind(BasConstants.BusinessKind.DICT_ZYCG);
            entity = contractSaveService.saveContract(entity, list, approve);

            //保存采购申请表中的合同Id
            buy.setContractId(entity.getId());
            buy = applyBuyDao.save(buy);
            //审批完成自动生成盖章申请
            this.autoInitiatedSealUsage(buy, approve);
        }

    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) {
    }

    @Override
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyBuy buy = applyBuyDao.findOne(approve.getBizId());
        //预售发起的采购申请驳回
        if (BasConstants.APPLY_TYPE_A.equals(buy.getApplyType())) {
            List<ApplyProductDetail> list = productDetailService.findApplyDetail(buy.getId(), BasConstants.APPLY_TYPE_A);
            for (ApplyProductDetail apd : list) {
                StockDetailPresell presell = stockDetailPresellService.findByCtrProductId(apd.getCtrProductId());
                presell.setApproveBuyNumber(presell.getApproveBuyNumber().subtract(apd.getDealNumber()));
                stockDetailPresellService.save(presell);
            }
        }
    }

    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        ApplyBuy buy;
        ApplyProductDetailSaveVo vo = new ApplyProductDetailSaveVo();
        vo.setApplyType(BasConstants.APPLY_TYPE_B);
        if (pmEntity instanceof ApplyBuyVo) {
            ApplyBuyVo buyVo = (ApplyBuyVo) pmEntity;
            //新增采购申请
            buy = new ApplyBuy();
            BeanUtils.copyProperties(buyVo, buy);
            vo.setApplyType(buy.getApplyType());
            //生成合同号
            if (buy.getId() == null || buy.getId() == 0) {
                String contractNo = BasBusinessUtil.composeContractNoZy(buyVo.getEnterpriseId(), vo.getApplyType());
                buy.setContractNo(contractNo);
            }
            buy = applyBuyDao.save(buy);
            //新增商品明细
            vo.setApplyId(buy.getId());
            productDetailService.saveDetailBatch(buyVo.getLstInsert(), buyVo.getLstUpdate(), buyVo.getLstDelete(), vo);
            //计算合同总价及定金比率
            List<ApplyProductDetail> productList = productDetailService.findApplyDetail(buy.getId(), BasConstants.APPLY_TYPE_B);
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (ApplyProductDetail product : productList) {
                totalAmount = totalAmount.add(product.getTotalPrice());
            }
            BigDecimal bondRate = Objects.isNull(buy.getBondRate()) ? BigDecimal.ZERO : buy.getBondRate();
            BigDecimal bondAmount = totalAmount.multiply(bondRate);
            buy.setTotalAmount(totalAmount);
            buy.setBondRate(bondRate);
            buy.setBondAmount(bondAmount);
            if (StringUtils.equals(BasConstants.ATTACH_DELIVERY_TIME_K, buy.getArrivalTimeExt())) {
                buy.setArrivalTime(null);
            }
            applyBuyDao.save(buy);
        } else {
            ApplyBuy entity = (ApplyBuy) pmEntity;
            buy = applyBuyDao.save(entity);
            //保存商品明细中企业id
            vo.setApplyId(buy.getId());
            vo.setEnterpriseId(buy.getEnterpriseId());
            vo.setApplyType(buy.getApplyType());
            productDetailService.saveBatchEnterpriseId(vo);
        }
        return buy;
    }

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyBuy buy = applyBuyDao.findOne(approve.getBizId());
        if (buy.getApplyType().equals(BasConstants.APPLY_TYPE_A)) {
            List<ApplyProductDetail> list = productDetailService.findApplyDetail(buy.getId(), BasConstants.APPLY_TYPE_A);
            for (ApplyProductDetail apd : list) {
                StockDetailPresell presell = stockDetailPresellService.findByCtrProductId(apd.getCtrProductId());
                presell.setApproveBuyNumber(presell.getApproveBuyNumber().add(apd.getDealNumber()));
                stockDetailPresellService.save(presell);
            }
        }

    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyBuy vo = (ApplyBuy) pmEntity;
            List<ApplyProductDetail> list = productDetailService.findApplyDetail(vo.getId(), vo.getApplyType());
            StringBuffer productNameAndBrand = new StringBuffer("");
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (ApplyProductDetail product : list) {
                String dealNumber = NumberUtil.formatNumber(product.getDealNumber(), "#.###");
                String[] title = product.getProductCd().split("_");
                if (title[0].equals("SL")) {
                    productNameAndBrand.append(product.getProductName() + "/" + product.getBrandNumber() + "/" + dealNumber + RuleUtil.weightUnit);
                } else {
                    productNameAndBrand.append(product.getProductName() + "/" + dealNumber + RuleUtil.weightUnit);
                }
                totalPrice = totalPrice.add(product.getTotalPrice());
            }
            String productNameAndBrandStr = productNameAndBrand.toString();
            //合同编号，品名/牌号/数量，供方，总价
            String companyName = RuleUtil.companyNameSubString(vo.getCompanyName());
            final String subject = SubjectUtil.formatSubject(vo.getContractNo(),productNameAndBrandStr,companyName,SubjectUtil.formatMoney(totalPrice,RuleUtil.monetaryUnit));
            return subject;
        }
        return null;
    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        applyBuyDao.updateFileId(id, fileId);
    }

    @Override
    @ServerTransactional
    public void updateApplyStatus(Long contractId) {
        applyBuyDao.updateApplyStatus(contractId);
    }

    @Override
    public ApplyBuy findByContractId(Long contractId) {
        return applyBuyDao.findByContractId(contractId);
    }

    @Override
    public ApproveFormPrintVo printApplyBuy(Long applyId) {
        ApproveFormPrintVo vo = new ApproveFormPrintVo();
        ApplyBuy buy = applyBuyDao.findOne(applyId);
        List<ApplyProductDetail> list = productDetailService.findApplyDetail(applyId, buy.getApplyType());
        //处理模板合并的参数
        for (ApplyProductDetail detail : list) {
            String productName = detail.getProductName() + "/" + detail.getBrandNumber() + "/" + detail.getFactoryName();
            detail.setProductName(productName);
        }
        vo.setDetailList(list);

        //格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

        //期货显示到货时间
        if (StringUtils.equals(buy.getContractAttr(), BasConstants.DICT_TYPE_CONTRACTATTR_F)) {
            vo.setArrivalTimeStr(sdf.format(buy.getArrivalTime()));
        } else {
            vo.setArrivalTimeStr("");
        }
        if (buy.getTransportAmount() != null && buy.getTransportAmount().compareTo(BigDecimal.ZERO) > 0) {
            vo.setTransportAmount(buy.getTransportAmount());
        } else {
            vo.setTransportAmount(BigDecimal.ZERO);
        }
        vo.setBondAmount(buy.getBondAmount());
        vo.setDeliveryType(DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, buy.getDeliveryType()));
        vo.setContractAttr(DictUtil.getValue(BasConstants.DICT_TYPE_CONTRACTATTR, buy.getContractAttr()));
        vo.setPayTimeStr(sdf.format(buy.getPayFullTime()));
        vo.setWarehouseName(list.get(0).getWarehouseName());
        vo.setRemark(buy.getRemark());
        vo.setCompanyName(buy.getCompanyName());
        vo.setContractNo(buy.getContractNo());
        //获取审批单的创建人
        PmApprove approve = approveService.getEntity(buy.getApproveId());
        vo.setMatchUserName(approve.getCreateUserName());

        BsTemplateConfig template = TemplateContentUtility.getTemplate("matchApplyPrint", "FMC_APPLY_BUY", "CH", buy.getEnterpriseId());
        try {
            String content = contentMerge(template.getContent(), vo);
            vo.setContent(content);
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return vo;
    }

    //将审批内容填充至模板
    @SuppressWarnings("deprecation")
    private String contentMerge(String content, ApproveFormPrintVo entity) throws ApplicationException {
        Configuration cfg = new Configuration();
        StringWriter sw = new StringWriter();
        try {
            Template t = new Template("", new StringReader(content), cfg);
            t.process(entity, sw);
            content = sw.toString();
        } catch (Exception e) {
            throw new ApplicationException(CommonErrorId.ERROR_DATA_EXCHANGE, "合并模板异常", e);
        }
        return content;
    }

    @Override
    @ServerTransactional
    public void applyBuy(ApplyBuyVo applyBuyVo) throws ApplicationException {
        try {

            for (ApplyProductDetailVo applyProductDetailVo : applyBuyVo.getProductJSON()) {
                if (StringUtils.isEmpty(applyProductDetailVo.getProductName())) {
                    throw new ApplicationException("货品信息不能为NULL");
                }
                if (StringUtils.isEmpty(applyProductDetailVo.getWarehouseName())) {
                    throw new ApplicationException("提货/配送仓库不能为NULL");
                }
                if (applyProductDetailVo.getDealNumber()==null) {
                    throw new ApplicationException("数量不能为NULL");
                }
                if (applyProductDetailVo.getDealPrice()==null) {
                    throw new ApplicationException("单价不能为NULL");
                }
                if (applyProductDetailVo.getTaxPrice()==null) {
                    throw new ApplicationException("不含税单价不能为NULL");
                }
                if (applyProductDetailVo.getTotalPrice()==null) {
                    throw new ApplicationException("总价不能为NULL");
                }
            }
            if(StringUtils.isEmpty(applyBuyVo.getCompanyName())){
                throw new ApplicationException("供方不能为NULL");
            }
            if(StringUtils.isEmpty(applyBuyVo.getInvoiceDate())){
                throw new ApplicationException("开票日期不能为NULL");
            }
            if(StringUtils.isEmpty(applyBuyVo.getPayType())){
                throw new ApplicationException("支付方式不能为NULL");
            }
            if(StringUtils.isEmpty(applyBuyVo.getReceiveBank())){
                throw new ApplicationException("收款银行不能为NULL");
            }
            if(StringUtils.isEmpty(applyBuyVo.getReceiveAccount())){
                throw new ApplicationException("收款账号不能为NULL");
            }
            if(StringUtils.isEmpty(applyBuyVo.getDeliveryAddr())){
                throw new ApplicationException("交货地点不能为NULL");
            }

            PmApproveSaveVo startVo = new PmApproveSaveVo();
            startVo.setMode(BasConstants.APPROVE_STATUS_A);
            startVo.setStatus(BasConstants.APPROVE_STATUS_A);
            startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);//中光亿云企业id
            applyBuyVo.setEnterpriseId(startVo.getEnterpriseId());//获取企业id

            // 接收货品的信息 //ApplyProductDetailVo
            List<ApplyProductDetailVo> applyBuyVoList = applyBuyVo.getProductJSON();//获取货品信息
            for (ApplyProductDetail detail : applyBuyVoList) {
                detail.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            }
            //原生态,不能为null get值
            applyBuyVo.setBatchSub(applyBuyVo.getLstInsert(), applyBuyVoList, applyBuyVo.getLstDelete());

            PmProcessSearchVo searchVo = new PmProcessSearchVo();
            searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            searchVo.setProcessCode(BasConstants.PROCESS_APPLY_BUY); //流程自营采购

            //根据对象参数获取流程主表
            PmProcess process = pmProcessService.findByProcessCode(searchVo);
            if (process == null) {
                throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
            }

            //通过当前登录人Id 获取申请人信息
            //SysUser userById = adminOpenFacade.findUserById(applyBuyVo.getApplyUserId());
            SysUserSdk userById = authOpenFacade.findUserById(applyBuyVo.getApplyUserId());
            if (userById != null) {
                startVo.setUserId(userById.getUserId());
                startVo.setUserName(userById.getNickName());
                startVo.setProcessId(process.getId());
                startVo.setApproveId(0L);
                applyBuyVo.setApproveId(0L);//代表新增
            }
            startVo.setBizEntityJson(JsonUtil.obj2Json(applyBuyVo));
            pmApproveService.startFlow(startVo);
            logger.info("---自营采购预算申请---");
        } catch (ApplicationException e) {
            throw new ApplicationException(e);
        }
    }

    /**
     * 自动发起盖章申请-自营采购预算
     * @param applyBuy
     * @param approve
     */
    @Override
    @ServerTransactional
    public void autoInitiatedSealUsage(ApplyBuy applyBuy, PmApprove approve) {
        BUY_SCHEDULED_POOL.schedule(() -> {
            try {
                PmProcess sealUsageProcess = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_APPLY_SEAL_USAGE_BUSINESS, approve.getEnterpriseId());
                String dictCd = BsCompanyOurUtil.getKey(approve.getEnterpriseId(), applyBuy.getOurCompanyName());

                PmApproveSaveVo startVo = new PmApproveSaveVo();
                SealUsage usage = new SealUsage();
                usage.setEnterpriseId(applyBuy.getEnterpriseId());
                usage.setSealType(BasConstants.DICT_TYPE_SEAL_TYPE_TS);
                usage.setSealDate(new Date());
                usage.setCompanyName(dictCd);
                usage.setContractNo(applyBuy.getContractNo());
                usage.setCustomerName(applyBuy.getCompanyName());
                usage.setTotalAmount(applyBuy.getTotalAmount());
                usage.setFileType(BasConstants.DICT_TYPE_FILE_TYPE_BS);
                usage.setApplyUserId(approve.getCreateUserId());
                usage.setApplyUserName(approve.getCreateUserName());
                usage.setContractId(applyBuy.getContractId());
                usage.setBusinessType(applyBuy.getBusinessType());
                usage.setRealApproveId(approve.getId());
                usage.setBusinessFlg(true);
                usage.setFileId(applyBuy.getFileId());
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
                pmApproveService.startFlow(startVo);
            } catch (Exception e) {
                logger.error("applyBuy autoInitiatedSealUsage error:{}", e.getMessage());
            }
        },5, TimeUnit.SECONDS);
    }
}

