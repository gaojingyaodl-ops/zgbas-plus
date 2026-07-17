package com.spt.bas.server.service.impl;

import cn.hutool.core.date.DateUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.SealUsageDcsxVo;
import com.spt.bas.server.dao.BsLogDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.SealUsageDCSXDao;
import com.spt.bas.server.filter.IAutoSealPdfSignFilter;
import com.spt.bas.server.service.IApplyDcsxService;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.bas.server.service.ICtrContractOphisService;
import com.spt.bas.server.service.ISealUsageDCSXService;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.service.impl.PmApproveServiceImpl;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.util.SpringContextHolder;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component("sealUsageDCSXService")
@Transactional(readOnly = true)
public class SealUsageDCSXServiceImpl extends BaseService<SealUsageDCSX> implements ISealUsageDCSXService, IPmApproveListener {
    @Autowired
    private IPmProcessService iPmProcessService;
    @Autowired
    private PmApproveServiceImpl approveService;
    @Autowired
    private SealUsageDCSXDao sealUsageDcsxDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private PmApproveDao pmApproveDao;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private IApplyDcsxService applyDcsxService;
    @Autowired
    private ICtrContractOphisService contractOphisService;
    @Autowired
    private PmProcessDao pmProcessDao;
    @Autowired
    private BsLogDao bsLogDao;

    @Autowired
    private IAutoSealPdfSignFilter autoSealPdfSignFilter;

    @Override
    public BaseDao<SealUsageDCSX> getBaseDao() {
        return sealUsageDcsxDao;
    }

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        String contents = pmApproveContents.getContents();
        SealUsage entity = JsonUtil.json2Object(SealUsage.class, contents);
        Long contractId = entity.getContractId();
        ApplyCtrDCSX ctrDcsx = applyDcsxService.getEntity(contractId);
        //保存业务盖章操作记录
        if (contractId != null) {
            if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, ctrDcsx.getBusinessType())) {
                contractOphisService.addHisDcTp(BasConstants.SN, "S", contractId, approve, new Date());
            } else {
                contractOphisService.addHisDcsx(BasConstants.SN, "S", contractId, approve, new Date());
            }
        }

    }
    @Override
    @ServerTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            //完成时判断是否上传附件
            if (StringUtils.isEmpty(pmApproveContents.getFileId())) {
                throw new ApplicationException("请上传盖章附件");
            }
            String contents = pmApproveContents.getContents();
            SealUsageDCSX entity = JsonUtil.json2Object(SealUsageDCSX.class, contents);
            entity.setApproveId(approve.getId());
            entity.setApplyUserId(approve.getCreateUserId());
            entity.setApplyUserName(approve.getCreateUserName());
            entity.setId(0L);
            entity.setFileId(pmApproveContents.getFileId());
            this.successSign(approve, pmApproveContents, entity);
            sealUsageDcsxDao.save(entity);
            this.initSaveDcsx(entity, approve);
        }
    }

    private void successSign(PmApprove approve, PmApproveContents pmApproveContents, SealUsageDCSX entity) {
        try {
            logger.info("预算编号:{} 代采赊销盖章审批完成后自动执行签署逻辑", approve.getApproveNo());
            String resultFileId = autoSealPdfSignFilter.successSignContractByKeywordDCSX(pmApproveContents.getCfcaContractNo(), approve.getContractId());
            if (StringUtils.isNotBlank(resultFileId)) {
                logger.info("原附件ID:{}", pmApproveContents.getFileId());
                logger.info("自动签署成功 fileId:{}", resultFileId);
                entity.setFileId(resultFileId);
                pmApproveContents.setFileId(resultFileId);
                pmApproveContentsDao.save(pmApproveContents);
            }
        } catch (Exception e) {
            logger.info("代采赊销盖章审批完成后自动执行签署逻辑 error", e);
        }
    }

    private void initSaveDcsx(SealUsageDCSX entity, PmApprove approve) throws ApplicationException {
        ApplyCtrDCSX byContractNo = applyDcsxService.findByContractNo(entity.getContractNo());
        byContractNo.setDealPrice(entity.getDealPrice());
        byContractNo.setCreditDays(entity.getCreditDays());
        byContractNo.setTotalAmount(entity.getTotalAmount());
        byContractNo.setPayFullTime(entity.getLastPayDate());
        byContractNo.setBuyPayFullTime(entity.getBuyPayFullTime());
        byContractNo.setDeliveryDateTo(entity.getDeliveryDate());
        byContractNo.setSellDeliveryDate(entity.getSellDeliveryDate());
        byContractNo.setDeliveryType(entity.getDeliveryType());
        byContractNo.setExtraTerm(entity.getExtraTerm());
        byContractNo.setFileId(entity.getFileId());
        byContractNo.setDeliveryAddr(entity.getDeliveryAddr());
        byContractNo.setStatus("S");
        byContractNo.setSealFlg(true);
        if (StringUtils.isNotBlank(entity.getOurBankName())) {
            byContractNo.setOurBankName(entity.getOurBankName());
        }
        if (StringUtils.isNotBlank(entity.getOurBankAccount())) {
            byContractNo.setOurBankAccount(entity.getOurBankAccount());
        }
        applyDcsxService.save(byContractNo);
        // 适配历史审批中的审批单添加操作记录：过段时间可以去除
        Date createdDate = approve.getCreatedDate();
        if (createdDate != null) {
            String format = DateUtil.format(createdDate, "yyyy-MM-dd");
            if (BasConstants.NEW_ADD_HIS_START_DATE.compareTo(format) >= 0) {
                //2.添加合同操作记录
                if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, byContractNo.getBusinessType())) {
                    contractOphisService.addHisDcTp(BasConstants.SN, "S", byContractNo.getId(), approve, new Date());
                } else {
                    contractOphisService.addHisDcsx(BasConstants.SN, "S", byContractNo.getId(), approve, new Date());
                }
            }
        }

    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        String contents = pmApproveContents.getContents();
        SealUsageDCSX sealUsageDCSX = JsonUtil.json2Object(SealUsageDCSX.class, contents);
        sealUsageDCSX.setApproveId(vo.getApproveId());
        sealUsageDCSX.setId(0L);
        sealUsageDCSX.setFileId(pmApproveContents.getFileId());
        save(sealUsageDCSX);
    }

    @Override
    public void updateFileId(Long id, String fileId) {
        sealUsageDcsxDao.updateFileId(id, fileId);
    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        logger.info("doStepBack--------------------");
        Long bizId = approve.getBizId();
        PmApproveContents approveContents = pmApproveContentsDao.findOne(bizId);
        if (approveContents != null) {
            String contents = approveContents.getContents();
            SealUsageDCSX sealUsage = JsonUtil.json2Object(SealUsageDCSX.class, contents);
            Long approveId = sealUsage.getApproveId();
            if (sealUsage != null && StringUtils.isNotBlank(sealUsage.getBusinessType())) {
                List<CtrContract> contracts = ctrContractDao.findByApproveId(approveId);
                CtrContract sellContract = null;
                CtrContract buyContract = null;
                for (CtrContract contract : contracts) {
                    if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, contract.getContractType())) {
                        sellContract = contract;
                    } else if (StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contract.getContractType())) {
                        buyContract = contract;
                    }
                }
                pmApproveDao.updateStatus(approveId, BasConstants.APPROVE_STATUS_B);
                ctrContractDao.updateStatus(approveId, BasConstants.APPROVE_STATUS_C);
                ctrContractDao.updateDCSXStatus(approveId, BasConstants.APPROVE_STATUS_C);
                // 赊销 销售合同作废修正额度
                if (sellContract != null && sellContract.getSettlementType() != null) {
                    BsCompany company = bsCompanyService.getEntity(sellContract.getCompanyId());
                    company.setUsedCreditAmount(company.getUsedCreditAmount().subtract(sellContract.getTotalAmount()));
                    bsCompanyService.save(company);

                    BsCompany buyCompany = bsCompanyService.getEntity(buyContract.getCompanyId());
                    // 采购额度修正--采购额度
                    BigDecimal subtract = buyCompany.getUsedSupplierPurchaseAmount().subtract(buyContract.getTotalAmount());
                    buyCompany.setUsedSupplierPurchaseAmount(subtract.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : subtract);
                    // 采购额度修正--预付款额度
                    BigDecimal subtract1 = buyCompany.getUsedSupplierPrepayAmount().subtract(buyContract.getTotalAmount());
                    buyCompany.setUsedSupplierPrepayAmount(subtract1.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : subtract1);
                    bsCompanyService.save(buyCompany);
                }
                //作废其余业务用印申请
                List<PmApproveContents> approveContentsList = pmApproveContentsDao.findByRealApproveId(approveId);

                for (PmApproveContents acs : approveContentsList) {
                    Long id = acs.getApproveId();
                    pmApproveDao.updateStatus(id, BasConstants.APPROVE_STATUS_B);

                    // 处理另外一个盖章申请 设置处理人。。
                    if (BasConstants.SEAL_APPLY_NAME_USAGE.equals(acs.getApplyName()) || BasConstants.APPLY_SEAL_USAGE_DCSX.equals(acs.getApplyName())
                            || BasConstants.APPLY_SEAL_USAGE_DCTP.equals(acs.getApplyName())) {
                        PmApprove approve1 = pmApproveDao.findOne(id);
                        approve1.setCurrApproverUserId(null);
                        approve1.setCurrApproveStepId(null);
                        approve1.setCurrStepName(null);
                        approve1.setStatus(PmConstants.APPROVE_STATUS_B);// 驳回
                        pmApproveDao.save(approve1);
                    }
                }

            }

            // 赊销预算、代采赊销预算 盖章申请驳回，一并作废申请单
            Long realApproveId = approveContents.getRealApproveId();
            PmApprove realApprove = approveService.getEntity(realApproveId);
            if (Objects.nonNull(realApprove)) {
                PmProcess process = iPmProcessService.getEntity(realApprove.getProcessId());
                String processCode = process.getProcessCode();
                IPmService pmService = SpringContextHolder.getBean(process.getEntityService());
                IPmApproveListener listener = (IPmApproveListener) pmService;
                if (BasConstants.BS_CONFIG_FILTER_PROCESS_LIST.contains(processCode)) {
                    PmApproveCurrVo currVo = new PmApproveCurrVo();
                    currVo.setBizId(realApprove.getBizId());
                    currVo.setApproveNo(realApprove.getApproveNo());
                    currVo.setId(realApprove.getId());
                    currVo.setCompleteDismissalFlg(true);
                    listener.doStepBack(currVo, null);
                }
            }

        }
    }

    /**
     * 增加代采赊销盖章申请操作日志
     *
     * @param startVo
     */
    @Override
    @ServerTransactional
    public void addSealUsageUpdateHis(PmApproveSaveVo startVo) {
        boolean updateFlg = false;
        StringBuilder sbr = new StringBuilder("代采赊销盖章 ");
        try {
            PmProcess process = pmProcessDao.findOne(startVo.getProcessId());
            IPmEntity bizEntity = null;
            String entityName = process.getEntityName();
            if (StringUtils.isNotBlank(entityName)) {
                try {
                    bizEntity = (IPmEntity) JsonUtil.json2Object(Class.forName(entityName), startVo.getBizEntityJson());
                } catch (Exception e) {
                    logger.error("getBizEntity", e);
                }
            }
            if (bizEntity instanceof SealUsageDCSX) {
                SealUsageDCSX entity = (SealUsageDCSX) bizEntity;
                PmApproveContents approveContents = pmApproveContentsDao.findByApproveId(startVo.getApproveId());
                if (Objects.nonNull(approveContents)) {
                    SealUsageDCSX sealUsage = JsonUtil.json2Object(SealUsageDCSX.class, approveContents.getContents());
                    sbr.append(approveContents.getId()).append(" [");
                    if (!StringUtils.equals(sealUsage.getProductBrand(), entity.getProductBrand())) {
                        updateFlg = true;
                        sbr.append("品种:").append(sealUsage.getProductBrand()).append("->").append(entity.getProductBrand()).append(",");
                    }
                    if (!StringUtils.equals(sealUsage.getProductNum(), entity.getProductNum())) {
                        updateFlg = true;
                        sbr.append("牌号:").append(sealUsage.getProductNum()).append("->").append(entity.getProductNum()).append(",");
                    }
                    if (!StringUtils.equals(sealUsage.getFactoryName(), entity.getFactoryName())) {
                        updateFlg = true;
                        sbr.append("厂商:").append(sealUsage.getFactoryName()).append("->").append(entity.getFactoryName()).append(",");
                    }
                    if (!StringUtils.equals(sealUsage.getWrapSpecs(), entity.getWrapSpecs())) {
                        updateFlg = true;
                        sbr.append("包装规格:").append(sealUsage.getWrapSpecs()).append("->").append(entity.getWrapSpecs()).append(",");
                    }
                    if (sealUsage.getDealPrice().compareTo(entity.getDealPrice()) != 0) {
                        updateFlg = true;
                        sbr.append("采购单价:").append(sealUsage.getDealPrice()).append("->").append(entity.getDealPrice()).append(",");
                    }
                    if (sealUsage.getTotalAmount().compareTo(entity.getTotalAmount()) != 0) {
                        updateFlg = true;
                        sbr.append("合同总额:").append(sealUsage.getTotalAmount()).append("->").append(entity.getTotalAmount()).append(",");
                    }
                    if (!Objects.equals(sealUsage.getCreditDays(), entity.getCreditDays())) {
                        updateFlg = true;
                        sbr.append("回款周期:").append(sealUsage.getCreditDays()).append("->").append(entity.getCreditDays()).append(",");
                    }
                    if (!Objects.equals(sealUsage.getPayFullTime(), entity.getLastPayDate())) {
                        updateFlg = true;
                        sbr.append("付款日期:").append(formatDate(sealUsage.getPayFullTime())).append("->").append(formatDate(entity.getLastPayDate())).append(",");
                    }
                    if (!Objects.equals(sealUsage.getDeliveryDate(), entity.getDeliveryDate())) {
                        updateFlg = true;
                        sbr.append("交货日期:").append(formatDate(sealUsage.getDeliveryDate())).append("->").append(formatDate(entity.getDeliveryDate())).append(",");
                    }
                    if (!StringUtils.equals(sealUsage.getDeliveryType(), entity.getDeliveryType())) {
                        updateFlg = true;
                        sbr.append("运输方式:").append(sealUsage.getDeliveryType()).append("->").append(entity.getDeliveryType()).append(",");
                    }
                    if (StringUtils.isNotBlank(entity.getDeliveryAddr()) && !StringUtils.equals(entity.getDeliveryAddr(), sealUsage.getDeliveryAddr())) {
                        updateFlg = true;
                        sbr.append("交货地点:").append(sealUsage.getDeliveryAddr()).append("->").append(entity.getDeliveryAddr()).append(",");
                    }
                    if (StringUtils.isNotBlank(entity.getExtraTerm()) && !StringUtils.equals(entity.getExtraTerm(), sealUsage.getExtraTerm())) {
                        updateFlg = true;
                        sbr.append("补充条款:").append(sealUsage.getExtraTerm()).append("->").append(entity.getExtraTerm()).append(",");
                    }
                }
            }
        } catch (Exception e) {
            updateFlg = false;
            logger.error("addSealUsageUpdateHis error", e);
        }
        if (Boolean.TRUE.equals(updateFlg)) {
            sbr.append("]");
            BsLog bsLog = new BsLog();
            bsLog.setOperation("2");
            bsLog.setOperatorId(startVo.getUserId());
            bsLog.setOperatorName(startVo.getUserName());
            bsLog.setRemark(sbr.toString());
            bsLog.setTargetName("代采赊销盖章");
            bsLogDao.save(bsLog);
        }
    }

    @Override
    public void updateCfcaContractNo(SealUsageDcsxVo entity) {
        autoSealPdfSignFilter.updateCfcaContractNo(entity);
    }

    @Override
    public List<SealUsageDCSX> findSealUsageDcsxByContractNo(String contractNo) {
        return sealUsageDcsxDao.findSealUsageDcsxByContractNo(contractNo);
    }

    private String formatDate(Date date) {
        if (Objects.isNull(date)) {
            return "null";
        }
        return DateOperator.formatDate(date);
    }
}
