package com.spt.bas.server.service.impl;

import cn.hutool.core.date.DateUtil;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IPmApproveClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.client.vo.BsInvoiceConfig;
import com.spt.bas.client.vo.SealUsageSearchVo;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.client.vo.protocol.RescissionAgreement;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.ctr.service.impl.CtrContractDataRefService;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.event.CtrContractEvent;
import com.spt.bas.server.filter.IAutoSealPdfSignFilter;
import com.spt.bas.server.logistics.service.ICtrLogisticsDeliveryService;
import com.spt.bas.server.service.*;
import com.spt.bas.server.stock.service.IStockVirtualService;
import com.spt.bas.server.util.SMSUtils;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.*;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.service.IPmProcessNodeService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.core.util.SpringContextHolder;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component("sealUsageService")
@Transactional(readOnly = true)
public class SealUsageServiceImpl extends BaseService<SealUsage> implements ISealUsageService, IPmApproveListener {
    private static final ExecutorService THREAD_POOL = Executors.newSingleThreadExecutor();
    private static final ScheduledExecutorService SCHEDULED_POOL = Executors.newScheduledThreadPool(10);
    private static final List<String> AUTO_SIGN_PROCESS_NODE_CODE = new ArrayList<String>(2) {
        private static final long serialVersionUID = -1;
        {
            add("sgx_caiwu_ljl");
            add("asy_caiwu_hq");
        }
    };

    @Autowired
    private SealUsageDao sealUsageDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private IBsProductConfigService bsProductConfigService;
    @Autowired
    private ICtrProductService ctrProductService;
    @Autowired
    private ApplyMatchDao applyMatchDao;
    @Autowired
    private ApplySellDao applySellDao;
    @Autowired
    private ApplyBuyDao applyBuyDao;
    @Autowired
    private IApplyMatchDetailService applyMatchDetailService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private PmProcessDao pmProcessDao;
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private ApplyDcsxDao applyDcsxDao;
    @Autowired
    private PmApproveDao pmApproveDao;
    @Autowired
    private IPmProcessService pmProcessService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private IBsProductTypeService productTypeService;
    @Autowired
    private IStockVirtualService stockVirtualService;
    @Autowired
    private ICtrContractOphisService ctrContractOphisService;
    @Autowired
    private CtrContractDataRefService ctrContractDataRefService;
    @Autowired
    private  CtrContractChainDao ctrContractChainDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private ICtrLogisticsService ctrLogisticsService;
    @Resource
    private ICtrLogisticsDeliveryService logisticsDeliveryService;
    @Autowired
    private CtrOutInLedgerDao ctrOutInLedgerDao;
    @Resource
    private IAutoSealPdfSignFilter autoSealPdfSignFilter;
    @Resource
    private IPmProcessNodeService pmProcessNodeService;
    @Autowired
    private ICtrContractProfitService ctrContractProfitService;
    @Autowired
    private IPmProcessClient processClient;
    @Autowired
    private IPmApproveClient approveClient;
    @Resource
    private IBsCompanyDcsxService bsCompanyDcsxService;
    @Autowired
    private IBsCompanyAccountService bsCompanyAccountService;
    @Override
    public BaseDao<SealUsage> getBaseDao() {
        return sealUsageDao;
    }

    @Override
    public Class<SealUsage> getEntityClazz() {
        return SealUsage.class;
    }

    @Override
    public void doStepIn(PmApprove approve) throws ApplicationException {
        Long processId = approve.getProcessId();
        PmProcess process = pmProcessService.getEntity(processId);
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        if (Objects.nonNull(process) && StringUtils.equals(BasConstants.PROCESS_APPLY_SEAL_USAGE, process.getProcessCode())) {
            if (StringUtils.isBlank(pmApproveContents.getFileId())) {
                throw new ApplicationException("请上传盖章审批附件!");
            }
        }
        String contents = pmApproveContents.getContents();
        SealUsage entity = JsonUtil.json2Object(SealUsage.class, contents);
        Long contractId = entity.getContractId();
        //保存业务盖章操作记录
        if (contractId != null) {
            ctrContractOphisService.addHis(BasConstants.SN, contractId, approve, new Date());
        }
    }

    @Override
    @ServerTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        if (Objects.nonNull(nextStep) && Objects.nonNull(nextStep.getNodeId())) {
            PmProcessNode node = pmProcessNodeService.getEntity(nextStep.getNodeId());
            if (Objects.nonNull(node) && AUTO_SIGN_PROCESS_NODE_CODE.contains(node.getNodeCode())) {
                SealUsage entity = JsonUtil.json2Object(SealUsage.class, pmApproveContents.getContents());
                String resultFileId = autoSealPdfSignFilter.successSignContractByKeyword(pmApproveContents.getCfcaContractNo(), entity.getContractNo());
                if (StringUtils.isNotBlank(resultFileId)) {
                    logger.info("原附件ID:{}", pmApproveContents.getFileId());
                    logger.info("自动签署成功 fileId:{}", resultFileId);
                    pmApproveContents.setFileId(resultFileId);
                    pmApproveContentsDao.save(pmApproveContents);
                }
            }
        }
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            // 完成时判断是否上传附件
            if (StringUtils.isEmpty(pmApproveContents.getFileId())) {
                throw new ApplicationException("请上传盖章附件");
            }
            String contents = pmApproveContents.getContents();
            SealUsage entity = JsonUtil.json2Object(SealUsage.class, contents);
            entity.setApproveId(approve.getId());
            entity.setApplyUserId(approve.getCreateUserId());
            entity.setApplyUserName(approve.getCreateUserName());
            entity.setId(0L);
            entity.setFileId(pmApproveContents.getFileId());
            save(entity);
            String fileId = entity.getFileId();
            String businessType = entity.getBusinessType();
            Long contractId = entity.getContractId();
            if (StringUtils.isNotBlank(pmApproveContents.getCfcaContractNo()) && (StringUtils.equals(BasConstants.COMPANY_NAME_FLK, entity.getCustomerName())
                    || StringUtils.equals(BasConstants.COMPANY_NAME_ZJKR, entity.getCustomerName())
                    || StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, entity.getCustomerName()))
                    || StringUtils.equals(BasConstants.COMPANY_NAME_SHZG, entity.getCustomerName())
                    || StringUtils.equals(BasConstants.COMPANY_NAME_ZJWS, entity.getCustomerName())) {
                fileId = successSign(approve, pmApproveContents, entity);
            }
            if (StringUtils.isNotBlank(businessType) && contractId != null) {
                CtrContract contract = null;
                switch (businessType) {
                    case BasConstants.CONTRACT_TYPE_B:
                        contract = ctrContractDao.findOne(contractId);
                        contract.setSealFlg(true);
                        contract.setSealDate(new Date());
                        contract.setContractStatus(BasConstants.CONTRACTSTATUS_S);
                        contract.setBuyContentFileId(fileId);
                        ctrContractDao.save(contract);
                        this.addSellCtrOutInLedger(contract);
                        // 判断是否自动发起业务付款申请
                        BsDictData bsDictData = BsDictUtil.getBsDictData(BasConstants.AUTO_APPLY_PAY_SWITCH, BasConstants.SWITCH);
                        if (bsDictData != null && StringUtils.equals("1", bsDictData.getDictName())) {
                            this.addAutoApplyPay(contract);
                        }
                        break;
                    case BasConstants.BUSINESS_TYPE_ZY_CG:
                        //自营采购
                        contract = ctrContractDao.findOne(contractId);
                        contract.setSealFlg(true);
                        contract.setSealDate(new Date());
                        contract.setContractStatus(BasConstants.CONTRACTSTATUS_S);
                        contract.setBuyContentFileId(fileId);
                        contract.setFileId(fileId);
                        ctrContractDao.save(contract);
                        break;
                    case BasConstants.BUSINESS_TYPE_ZY_XS:
                        //自营销售
                        contract = ctrContractDao.findOne(contractId);
                        contract.setSealFlg(true);
                        contract.setSealDate(new Date());
                        contract.setContractStatus(BasConstants.CONTRACTSTATUS_S);
                        contract.setSellContentFileId(fileId);
                        contract.setFileId(fileId);
                        boolean wxFlg = !StringUtils.equals(BasConstants.DELIVERY_MODE_SX, contract.getDeliveryMode());
                        contract.setContractStatusWx(wxFlg ? BasConstants.CONTRACT_STATUS_P : BasConstants.CONTRACT_STATUS_W);
                        ctrContractDao.save(contract);
                        break;
                    case BasConstants.CONTRACT_TYPE_S:
                        contract = ctrContractDao.findOne(contractId);
                        contract.setSealFlg(true);
                        contract.setSealDate(new Date());
                        contract.setContractStatus(BasConstants.CONTRACTSTATUS_S);
                        contract.setSellContentFileId(fileId);
                        boolean wxsFlg = !StringUtils.equals(BasConstants.DELIVERY_MODE_SX, contract.getDeliveryMode());
                        contract.setContractStatusWx(wxsFlg ? BasConstants.CONTRACT_STATUS_P : BasConstants.CONTRACT_STATUS_W);
                        ctrContractDao.save(contract);
                        this.addBuyCtrOutInLedger(contract);
                        break;
                    default:
                        break;
                }

                // 更新报价库存状态为已签约
                this.updateStockVirtualStatus(contract);

                // 自营/代采 乙二醇合同盖章完成后自动发起并完成开票/收票审批
                this.autoStartBill(contract);
            }
            // 适配历史审批中的审批单添加操作记录：过段时间可以去除
            Date createdDate = approve.getCreatedDate();
            if (createdDate != null) {
                String format = DateUtil.format(createdDate, "yyyy-MM-dd");
                if (BasConstants.NEW_ADD_HIS_START_DATE.compareTo(format) >= 0) {
                    //2.添加合同操作记录
                    if (contractId != null) {
                        ctrContractOphisService.addHis(BasConstants.SN, contractId, approve, new Date());
                    }
                }
            }

            if (Objects.nonNull(entity.getContractId())) {
                CtrContractEvent contractEvent = new CtrContractEvent(entity.getContractId());
                // 发布事务事件
                applicationEventPublisher.publishEvent(contractEvent);
            }
            // 签署物流单据
            this.signLogistics(entity, approve);

            //合同盖章完成通知
            this.sendNotifyEmail(entity, approve);
        }
    }

    /**
     * 乙二醇自营/代采业务盖章完成后根据配置开关自动发起收开票
     *
     * @param contract
     */
    private void autoStartBill(CtrContract contract) {
        if (Objects.isNull(contract)) {
            return;
        }
        // 非自营、代采业务 不需要自动生成开收票
        Boolean matchCreditFlg = contract.getMatchCreditFlg();
        if (Boolean.TRUE.equals(matchCreditFlg)) {
            return;
        }
        // 查询出合同商品代码判断是否为乙二醇合同
        List<CtrProduct> productList = ctrProductService.findByContractId(contract.getId());
        if (CollectionUtils.isNotEmpty(productList)) {
            String productCd = productList.get(0).getProductCd();
            // 查询乙二醇自动开收票配置开关
            BsInvoiceConfig config = bsProductConfigService.getBsInvoiceConfig(contract.getEnterpriseId());
            if (Objects.isNull(config)) {
                logger.error("BsInvoiceConfig is null！");
                return;
            }
            String contractType = contract.getContractType();
            Boolean autoReceiveInvoice = config.getAutoReceiveInvoice();
            Boolean autoPayInvoice = config.getAutoPayInvoice();
            List<String> autoReceiveProductCdList = config.getAutoReceiveProductCdList();
            List<String> autoPayProductCdList = config.getAutoPayProductCdList();
            if (StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contractType) &&
                    Boolean.TRUE.equals(autoReceiveInvoice) &&
                    CollectionUtils.isNotEmpty(autoReceiveProductCdList) &&
                    autoReceiveProductCdList.contains(productCd)) {
                // 采购自动发起并完成收票审批
                ctrContractDataRefService.refreshBuyBilledAmount(contract);
            } else if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, contractType)
                    && Boolean.TRUE.equals(autoPayInvoice) &&
                    CollectionUtils.isNotEmpty(autoPayProductCdList) &&
                    autoPayProductCdList.contains(productCd)) {
                // 销售自动发起并完成开票审批 (代采托盘不自动发起开启)
                ctrContractDataRefService.refreshSellBilledAmount(contract);
            }
        }
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        // TODO Auto-generated method stub
        logger.info("doWithdraw--------------------");
    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        logger.info("doStepBack--------------------");
        Long bizId = approve.getBizId();
        PmApproveContents approveContents = pmApproveContentsDao.findOne(bizId);
        if (approveContents != null) {
            String contents = approveContents.getContents();
            SealUsage sealUsage = JsonUtil.json2Object(SealUsage.class, contents);
            if(sealUsage.getChainDc()==true){
                CtrContractChain one=ctrContractChainDao.findOne(sealUsage.getContractId());
                one.setStatus(BasConstants.APPROVE_STATUS_B);
                ctrContractChainDao.save(one);
            } else {
                Long approveId = null;
                ApplyCtrDCSX applyCtrDCSX = null;
                if (sealUsage != null && StringUtils.isNotBlank(sealUsage.getBusinessType())) {
                    //作废代采预算单
                    Long contractId = sealUsage.getContractId();
                    if (contractId != null && contractId != 0L) {
                        CtrContract contract = ctrContractDao.findOne(contractId);
                        approveId = contract.getApproveId();
                        pmApproveDao.updateStatus(approveId, BasConstants.APPROVE_STATUS_B);
                        ctrContractDao.updateStatus(approveId, BasConstants.APPROVE_STATUS_C);
                        ctrContractDao.updateDCSXStatus(approveId, BasConstants.APPROVE_STATUS_C);
                    }
                    //作废其余业务用印申请
                    applyCtrDCSX = applyDcsxDao.findByDCSXApproveId(approveId);
                    List<PmApprove> businessApproveList = pmApproveService.getAllBusinessApprove(approveId);
                    for (PmApprove businessApprove : businessApproveList) {
                        businessApprove.setCurrApproverUserId(null);
                        businessApprove.setCurrApproveStepId(null);
                        businessApprove.setCurrStepName(null);
                        businessApprove.setStatus(approve.getStatus());
                        pmApproveDao.save(businessApprove);

                        PmApproveContents approveContent = pmApproveContentsDao.findByApproveId(businessApprove.getId());

                        if (Objects.nonNull(approveContent)){
                            approveContent.setStatus(BasConstants.APPROVE_STATUS_N);
                            pmApproveContentsDao.save(approveContent);
                        }
                        // 合同操作历史表添加追回记录
                        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_E)) {
                            CtrContract contracts = ctrContractDao.findOne(businessApprove.getContractId());
                            ctrContractOphisService.addRollBackHis(contracts, businessApprove);
                        }
                        // 合同操作历史表 添加驳回记录
                        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_B)) {
                            CtrContract contracts = ctrContractDao.findOne(businessApprove.getContractId());
                            ctrContractOphisService.addRejectHis(contracts, businessApprove);
                        }
                    }
                } else {
                    // 合同操作历史表添加追回记录
                    if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_E)) {
                        Long contractIds = approve.getContractId();
                        CtrContract contracts = ctrContractDao.findOne(contractIds);
                        ctrContractOphisService.addRollBackHis(contracts, approve);
                    }
                    // 合同操作历史表 添加驳回记录
                    if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_B)) {
                        Long contractIds = approve.getContractId();
                        CtrContract contracts = ctrContractDao.findOne(contractIds);
                        ctrContractOphisService.addRejectHis(contracts, approve);
                    }
                }

                // 赊销预算、代采赊销预算 盖章申请驳回，一并作废申请单
                Long realApproveId = approveContents.getRealApproveId();
                PmApprove realApprove = pmApproveService.getEntity(realApproveId);
                if (Objects.nonNull(realApprove)) {
                    PmProcess process = pmProcessDao.findOne(realApprove.getProcessId());
                    String processCode = process.getProcessCode();
                    IPmService pmService = SpringContextHolder.getBean(process.getEntityService());
                    IPmApproveListener listener = (IPmApproveListener) pmService;
                    if (BasConstants.BS_CONFIG_FILTER_PROCESS_LIST.contains(processCode)
                            || BasConstants.VIRTUAL_PROCESS_LIST.contains(processCode)) {
                        PmApproveCurrVo currVo = new PmApproveCurrVo();
                        currVo.setBizId(realApprove.getBizId());
                        currVo.setApproveNo(realApprove.getApproveNo());
                        currVo.setId(realApprove.getId());
                        currVo.setCompleteDismissalFlg(true);
                        listener.doStepBack(currVo, null);
                    }
                }

                // 中游合同已双签的情况下自动发起取消协议
                autoStartCancelProtocol(applyCtrDCSX);
            }
        }
    }

    private void autoStartCancelProtocol(ApplyCtrDCSX applyCtrDCSX) {
        if (Objects.isNull(applyCtrDCSX) || !BooleanUtils.isTrue(applyCtrDCSX.getSealFlg())) {
            return;
        }
        SCHEDULED_POOL.schedule(() -> {
            try {
                PmProcess sealUsageProcess = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_APPLY_PROTOCOL_DOC, applyCtrDCSX.getEnterpriseId());
                RescissionAgreement rescissionAgreement = new RescissionAgreement();
                rescissionAgreement.setProtocolNo(applyCtrDCSX.getContractNo().replaceAll("\\D", "") + "-1");
                rescissionAgreement.setContractNo(applyCtrDCSX.getContractNo());
                rescissionAgreement.setTargetCompanyName(applyCtrDCSX.getCompanyName());
                rescissionAgreement.setOurCompanyName(applyCtrDCSX.getOurCompanyName());
                rescissionAgreement.setContractDate(applyCtrDCSX.getContractTime());
                rescissionAgreement.setContractDateStr(DateOperator.formatDate(applyCtrDCSX.getContractTime(), "yyyy年MM月dd日"));
                rescissionAgreement.setTotalNumber(NumberUtil.formatNumber(applyCtrDCSX.getTotalNumber(), "#.###"));
                rescissionAgreement.setTotalAmount(NumberUtil.formatNumber(applyCtrDCSX.getTotalAmount(), "#.###"));
                rescissionAgreement.setDealPrice(NumberUtil.formatNumber(applyCtrDCSX.getDealPrice(), "#.###"));
                rescissionAgreement.setFactoryName(applyCtrDCSX.getFactoryName());
                rescissionAgreement.setProtocolDate(new Date());
                rescissionAgreement.setProtocolDateStr(DateOperator.formatDate(new Date(), "yyyy年MM月dd日"));
                BsProductType productTypeCode = productTypeService.findProductTypeCode(applyCtrDCSX.getProductBrand());
                if (Objects.nonNull(productTypeCode)) {
                    rescissionAgreement.setProductName(productTypeCode.getTypeName() + "/" + applyCtrDCSX.getProductNum());
                }
                rescissionAgreement.setRefundAmount(NumberUtil.formatNumber(applyCtrDCSX.getDealedAmount(), "#.###"));
                // 主表
                ApplyProtocolDocument protocolDocument = new ApplyProtocolDocument();
                protocolDocument.setDocType(BasConstants.DICT_DOC_TYPE_CP);
                protocolDocument.setSignCompanyName(applyCtrDCSX.getOurCompanyName());
                protocolDocument.setEnterpriseId(applyCtrDCSX.getEnterpriseId());
                protocolDocument.setContent(JsonUtil.obj2Json(rescissionAgreement));

                String entityJson = JsonUtil.obj2Json(protocolDocument);
                PmApproveSaveVo startVo = new PmApproveSaveVo();
                startVo.setBizEntityJson(entityJson);
                startVo.setProcessId(sealUsageProcess.getId());
                startVo.setDeptId(applyCtrDCSX.getDeptId());
                startVo.setMode("A");
                startVo.setStatus(BasConstants.APPROVE_STATUS_A);
                startVo.setApproveId(0L);
                startVo.setUserId(applyCtrDCSX.getMatchUserId());
                startVo.setUserName(applyCtrDCSX.getMatchUserName());
                startVo.setEnterpriseId(applyCtrDCSX.getEnterpriseId());
                startVo.setAutoStartMessage("链条业务盖章驳回，自动发起取消协议！");
                startVo.setAutoStartFlgReal(true);
                pmApproveService.startFlow(startVo);
            } catch (Exception e) {
                logger.error("autoStartCancelProtocol error:{}", e.getMessage());
            }
        }, 5, TimeUnit.SECONDS);
    }

    @Override
    public Page<SealUsage> findUsagePage(SealUsageSearchVo searchVo) {
        Sort sort = Sort.by(Direction.DESC, "id");
        Map<String, Object> searchParams = searchVo.getSearchParams();
        Specification<SealUsage> spec = WebUtil.buildSpecification(searchParams);
        PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows(), sort);
        Page<SealUsage> page = getBaseDao().findAll(spec, pageRequest);
        if (page != null && page.getContent() != null && page.getContent().size() > 0) {
            for (SealUsage sealUsage : page.getContent()) {
                String sealType = sealUsage.getSealType();
                String companyName = sealUsage.getCompanyName();
                if (sealType != null) {
                    String sealTypeNameStr = "";
                    if (sealType.contains(",")) {
                        String[] splitItem = sealType.split(",");
                        for (String item : splitItem) {
                            sealTypeNameStr += DictUtil.getValue(BasConstants.DICT_TYPE_SEAL_TYPE, item) + " ";
                        }
                        if (sealTypeNameStr.length() > 1) {
                            sealTypeNameStr = sealTypeNameStr.substring(0, sealTypeNameStr.length() - 1);
                        }
                    } else {
                        sealTypeNameStr = DictUtil.getValue(BasConstants.DICT_TYPE_SEAL_TYPE, sealType);
                    }
                    sealUsage.setSealType(sealTypeNameStr);
                }
                if (companyName != null) {
                    String companyNameStr = "";
                    if (companyName.contains(",")) {
                        String[] splitCompanyName = companyName.split(",");
                        for (String name : splitCompanyName) {
                            companyNameStr += DictUtil.getValue(BasConstants.DICT_TYPE_CUSTOMER_NAME, name) + " ";
                        }
                        if (companyNameStr.length() > 1) {
                            companyNameStr = companyNameStr.substring(0, companyNameStr.length() - 1);
                        }
                    } else {
                        companyNameStr = DictUtil.getValue(BasConstants.DICT_TYPE_CUSTOMER_NAME, companyName);
                    }
                    sealUsage.setCompanyName(companyNameStr);
                }
            }
        }

        PageRequest pageRequest_new = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<SealUsage> pageVo = new PageImpl<>(page.getContent(), pageRequest_new, page.getTotalElements());
        return pageVo;
    }

    @Override
    @ServerTransactional
    public void startSealUsage(PmApprove approve) {
        THREAD_POOL.execute(() -> parseStartFlowVo(approve));
    }

    private void parseStartFlowVo(PmApprove approve) {

        SCHEDULED_POOL.schedule(() -> {

            try {
                PmProcess pmProcess = pmProcessDao.findOne(approve.getProcessId());
                PmProcess sealUsageProcess = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_APPLY_SEAL_USAGE_BUSINESS, approve.getEnterpriseId());
                String processCode = pmProcess.getProcessCode();
                List<String> processList = new ArrayList<>();
                List<String> processMatch = Arrays.asList(BasConstants.PROCESS_GROUP_DCBTYS);
                processList.add(BasConstants.PROCESS_APPLY_BUY);
                processList.add(BasConstants.PROCESS_APPLY_SELL);
                if (processMatch.contains(processCode)) {
                    List<ApplyMatchDetail> matchDetailList = applyMatchDetailService.findByApproveId(approve.getId());
                    ApplyMatch match = applyMatchDao.findByApproveId(approve.getId());

                    String dictCd = BsCompanyOurUtil.getKey(approve.getEnterpriseId(), match.getOurCompanyName());
                    ApplyMatchDetail sellDetail = null;
                    for (ApplyMatchDetail detail : matchDetailList) {
                        if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, detail.getContractType())) {
                            sellDetail = detail;
                        }
                        try {
                            //合同盖章签署通知
                            if(detail.getContractType().equals(BasConstants.APPLY_TYPE_S)) {
                                BsCompany company = bsCompanyService.getEntity(detail.getCompanyId());
                                if (Boolean.TRUE.equals(company.getOpenCfcaFlg())) {
                                    SMSUtils.sendContractNo(company.getCompanyPhone(),detail.getContractNo());
                                }
                            }

                            PmApproveSaveVo startVo = new PmApproveSaveVo();
                            SealUsage usage = new SealUsage();
                            usage.setEnterpriseId(detail.getEnterpriseId());
                            usage.setSealType(BasConstants.DICT_TYPE_SEAL_TYPE_TS);
                            usage.setSealDate(new Date());
                            usage.setCompanyName(dictCd);
                            usage.setContractNo(detail.getContractNo());
                            usage.setCustomerName(detail.getCompanyName());
                            usage.setTotalAmount(detail.getTotalAmount());
                            usage.setFileType(BasConstants.DICT_TYPE_FILE_TYPE_BS);
                            usage.setApplyUserId(approve.getCreateUserId());
                            usage.setApplyUserName(approve.getCreateUserName());
                            usage.setContractId(detail.getContractId());
                            usage.setBusinessType(detail.getContractType());
                            usage.setRealApproveId(approve.getId());
                            usage.setBusinessFlg(true);
                            String entityJson = JsonUtil.obj2Json(usage);

                            startVo.setBizEntityJson(entityJson);
                            startVo.setProcessId(sealUsageProcess.getId());
                            startVo.setDeptId(approve.getDeptId());
                            startVo.setMode("A");
                            startVo.setStatus("A");
                            startVo.setApproveId(0L);
                            startVo.setUserId(approve.getCreateUserId());
                            startVo.setUserName(approve.getCreateUserName());
                            startVo.setEnterpriseId(approve.getEnterpriseId());
                            pmApproveService.startFlow(startVo);
                        } catch (Exception e) {
                            logger.info("startSealUsage error !!!", e);
                        }
                    }
                    //宁圣合同盖章
                    ApplyCtrDCSX applyCtrDCSX = applyDcsxDao.findByDCSXApproveId(approve.getId());
                    if (applyCtrDCSX != null) {
                        PmProcess process;
                        if (StringUtils.equals(BasConstants.APPLY_SEAL_USAGE_DCTP, applyCtrDCSX.getBusinessType())) {
                            process = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.APPLY_SEAL_USAGE_DCTP, approve.getEnterpriseId());
                        } else {
                            process = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.APPLY_SEAL_USAGE_DCSX, approve.getEnterpriseId());
                        }

                        if (StringUtils.equals(BasConstants.PROCESS_APPLY_CHARGE_SALES, processCode)) {
                            String applyCompanyDict = BsCompanyOurUtil.getKey(approve.getEnterpriseId(), applyCtrDCSX.getOurCompanyName());
                            PmApproveSaveVo startVo = new PmApproveSaveVo();
                            SealUsageDCSX usage = new SealUsageDCSX();
                            //品种
                            BsProductType productTypeCode = productTypeService.findProductTypeCode(applyCtrDCSX.getProductBrand());
                            usage.setApplyUserId(approve.getCreateUserId());
                            usage.setProductBrand(productTypeCode.getTypeName());
                            //牌号
                            usage.setProductNum(applyCtrDCSX.getProductNum());
                            //数量
                            usage.setTotalNumber(applyCtrDCSX.getTotalNumber());
                            //厂商
                            usage.setFactoryName(applyCtrDCSX.getFactoryName());
                            //包装规格
                            String wrapSpecs = DictUtil.getValue(BasConstants.DICT_TYPE_IMPORTBUYPACKING, applyCtrDCSX.getWrapSpecs());
                            usage.setWrapSpecs(wrapSpecs);
                            //质量标准
                            String qualityStandard = DictUtil.getValue(BasConstants.DICT_TYPE_QUALITYSTANDDARD, applyCtrDCSX.getQualityStandard());
                            usage.setQualityStandard(qualityStandard);
                            //我方
                            usage.setOurCompanyName(applyCtrDCSX.getOurCompanyName());
                            //代采购方
                            usage.setCompanyName(applyCtrDCSX.getCompanyName());
                            //采购单价
                            usage.setBondAmount(applyCtrDCSX.getDealPrice());
                            //合同总额
                            usage.setTotalAmount(applyCtrDCSX.getTotalAmount());
                            //回款周期
                            usage.setCreditDays(applyCtrDCSX.getCreditDays());
                            //付款日期
                            usage.setPayBondTime(applyCtrDCSX.getPayBondTime());

                            usage.setPayFullTime(applyCtrDCSX.getPayFullTime());
                            usage.setDealPrice(applyCtrDCSX.getDealPrice());

                            // 关联审批id
                            usage.setRealApproveId(approve.getId());

                            usage.setEnterpriseId(applyCtrDCSX.getEnterpriseId());
                            usage.setSealType(BasConstants.DICT_TYPE_SEAL_TYPE_TS);
                            usage.setSealDate(new Date());
                            usage.setApproveId(applyCtrDCSX.getApproveId());
                            usage.setContractNo(applyCtrDCSX.getContractNo());
                            usage.setTotalAmount(applyCtrDCSX.getTotalAmount());
                            usage.setContractId(applyCtrDCSX.getId());
                            usage.setFileType(BasConstants.DICT_TYPE_FILE_TYPE_DCSX);
                            usage.setApplyUserId(approve.getCreateUserId());
                            usage.setApplyUserName(approve.getCreateUserName());
                            usage.setBusinessType(BasConstants.CONTRACT_TYPE_X);
                            usage.setBusinessFlg(true);
                            usage.setBuyPrice(applyCtrDCSX.getBuyPrice());
                            usage.setDeliveryType(applyCtrDCSX.getDeliveryType());
                            usage.setDeliveryDate(applyCtrDCSX.getDeliveryDateTo());
                            usage.setDeliveryAddr(applyCtrDCSX.getDeliveryAddr());
                            String entityJson = JsonUtil.obj2Json(usage);

                            startVo.setBizEntityJson(entityJson);
                            startVo.setProcessId(process.getId());
                            startVo.setDeptId(approve.getDeptId());
                            startVo.setApproveId(0L);
                            startVo.setMode("A");
                            startVo.setStatus("A");
                            startVo.setUserId(approve.getCreateUserId());
                            startVo.setUserName(approve.getCreateUserName());
                            startVo.setEnterpriseId(approve.getEnterpriseId());
                            pmApproveService.startFlow(startVo);
                        }
                    }
                } else if (StringUtils.equals(processCode, BasConstants.PROCESS_APPLY_SELL)) {
                    ApplySell applySell = applySellDao.findByApproveId(approve.getId());
                    String dictCd = BsCompanyOurUtil.getKey(approve.getEnterpriseId(), applySell.getOurCompanyName());
                    try {
                        PmApproveSaveVo startVo = new PmApproveSaveVo();
                        SealUsage usage = new SealUsage();
                        usage.setEnterpriseId(applySell.getEnterpriseId());
                        usage.setSealType(BasConstants.DICT_TYPE_SEAL_TYPE_TS);
                        usage.setSealDate(new Date());
                        usage.setCompanyName(dictCd);
                        usage.setContractNo(applySell.getContractNo());
                        usage.setCustomerName(applySell.getCompanyName());
                        usage.setTotalAmount(applySell.getTotalAmount());
                        usage.setFileType(BasConstants.DICT_TYPE_FILE_TYPE_BS);
                        usage.setApplyUserId(approve.getCreateUserId());
                        usage.setApplyUserName(approve.getCreateUserName());
                        usage.setContractId(applySell.getContractId());
                        usage.setBusinessType(applySell.getBusinessType());
                        usage.setRealApproveId(approve.getId());
                        usage.setBusinessFlg(true);
                        usage.setFileId(applySell.getFileId());
                        String entityJson = JsonUtil.obj2Json(usage);
                        startVo.setBizEntityJson(entityJson);
                        startVo.setProcessId(sealUsageProcess.getId());
                        startVo.setDeptId(approve.getDeptId());
                        startVo.setMode("A");
                        startVo.setStatus("A");
                        startVo.setApproveId(0L);
                        startVo.setUserId(approve.getCreateUserId());
                        startVo.setUserName(approve.getCreateUserName());
                        startVo.setEnterpriseId(approve.getEnterpriseId());
                        pmApproveService.startFlow(startVo);
                    } catch (Exception e) {
                        logger.info("startSealUsage error !!!", e);
                    }
                } else if (StringUtils.equals(processCode, BasConstants.PROCESS_APPLY_BUY) || StringUtils.equals(processCode, BasConstants.PROCESS_APPLY_PRESELL_BUY)) {
                    ApplyBuy applyBuy = applyBuyDao.findByApproveId(approve.getId());
                    String dictCd = BsCompanyOurUtil.getKey(approve.getEnterpriseId(), applyBuy.getOurCompanyName());
                    try {
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
                        startVo.setStatus("A");
                        startVo.setApproveId(0L);
                        startVo.setUserId(approve.getCreateUserId());
                        startVo.setUserName(approve.getCreateUserName());
                        startVo.setEnterpriseId(approve.getEnterpriseId());
                        pmApproveService.startFlow(startVo);
                    } catch (Exception e) {
                        logger.info("startSealUsage error !!!", e);
                    }
                }
            } catch (Exception e) {
                logger.info("parseStartFlowVo errow", e);
            }
        }, 5, TimeUnit.SECONDS);
    }

    /**
     * 盖章申请完成，签署物流单据
     * @param entity
     * @param approve
     */
    @Override
    @ServerTransactional
    public void signLogistics(SealUsage entity, PmApprove approve){
        SCHEDULED_POOL.schedule(() -> {
            Long logisticsFileId = entity.getLogisticsFileId();
            if (Objects.isNull(logisticsFileId)){
                return;
            }
            String ourCompanyName = entity.getCompanyName();
            String sealType = BasConstants.CFCA_SEAL_TYPE.SEAL_TYPE_LGS;
            Long approveId = approve.getId();
            String approveNo = approve.getApproveNo();
            logger.info("approveNo:{},logisticsFileId:{}", approveNo, logisticsFileId);
            CtrLogisticsFile logisticsFile = logisticsDeliveryService.successLogisticsPdfFile(ourCompanyName, sealType, logisticsFileId, approveId, approveNo);
            if (Objects.nonNull(logisticsFile) && Boolean.TRUE.equals(logisticsFile.getSignFlg())) {
                String fileId = entity.getFileId();
                logger.info("oldFileId:{},newFileId:{}", logisticsFile.getOldFileId(), logisticsFile.getFileId());
                entity.setFileId(fileId.replace(logisticsFile.getOldFileId(), logisticsFile.getFileId()));
                PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
                String contentsFileId = pmApproveContents.getFileId();
                logger.info("contentsFileId:{}", contentsFileId);
                pmApproveContents.setFileId(contentsFileId.replace(logisticsFile.getOldFileId(), logisticsFile.getFileId()));
                pmApproveContentsDao.save(pmApproveContents);
                sealUsageDao.save(entity);
            }
        }, 5, TimeUnit.SECONDS);
    }

    @Override
    public SealUsage findById(Long id) {
       return  getBaseDao().findOne(id);
    }

    private void updateStockVirtualStatus(CtrContract contract){
        SCHEDULED_POOL.schedule(() -> {
            if (Objects.nonNull(contract)) {
                ApplyMatch virtualMatch = applyMatchDao.findVirtualMatch(contract.getContractNo());
                if (Objects.nonNull(virtualMatch)) {
                    StockVirtual stockVirtual = stockVirtualService.getEntity(virtualMatch.getStockVirtualId());
                    if (Objects.nonNull(stockVirtual) && StringUtils.equals(contract.getContractType(), BasConstants.CONTRACT_TYPE_B)) {
                        stockVirtualService.updateStockVirtualStatus(virtualMatch.getStockVirtualId(), BasConstants.STOCK_VIRTUAL_STATUS_S);
                    }
                }
            }
        }, 6, TimeUnit.SECONDS);
    }

    private void sendNotifyEmail(SealUsage entity, PmApprove approve){
        SCHEDULED_POOL.schedule(() -> {
            Long contractId1 = entity.getContractId();
            SysUserSdk sysUser = authOpenFacade.findUserById(approve.getCreateUserId());
            String email = sysUser.getEmail();
            CtrContract ctrContract = ctrContractDao.findOne(contractId1);
            //合同编号
            if (ctrContract != null) {
                String contractNo = ctrContract.getContractNo();
                String companyName = ctrContract.getCompanyName();
                //创建者姓名
                String createUserName = approve.getCreateUserName();
                SMSUtils.sendEmailNotification(contractNo, createUserName, companyName, email);
            }
        }, 10, TimeUnit.SECONDS);
    }

    private void addSellCtrOutInLedger(CtrContract contract) {
        SCHEDULED_POOL.schedule(() -> {
            try {
                CtrContract ctrContractSell = ctrContractDao.findByApproveIdAndContractType(contract.getApproveId(), BasConstants.CONTRACT_TYPE_S);
                if (ctrContractSell != null && Objects.equals(ctrContractSell.getSealFlg(), true)) {
                    List<CtrOutInLedger> ctrOutInLedgerList = ctrOutInLedgerDao.findByOperationAndContractNo(BasConstants.DICT_OUT_IN_LEDGER_TYPE_1, contract.getContractNo());
                    if (CollectionUtils.isEmpty(ctrOutInLedgerList)) {
                        CtrOutInLedger ctrOutInLedger = new CtrOutInLedger();
                        BeanUtils.copyProperties(contract, ctrOutInLedger);
                        ctrOutInLedger.setId(null);
                        ctrOutInLedger.setOperTime(new Date());
                        ctrOutInLedger.setOperation(BasConstants.DICT_OUT_IN_LEDGER_TYPE_1);
                        ctrOutInLedger.setPrice(contract.getDealPrice());
                        ctrOutInLedger.setSourceId(contract.getId());
                        // 获取公司传真
                        BsCompany bsCompany = bsCompanyService.getEntity(ctrContractSell.getCompanyId());
                        if (Objects.nonNull(bsCompany)) {
                            ctrOutInLedger.setCompanyFax(bsCompany.getCompanyFax());
                        }
                        // 获取实际合同单号
                        List<CtrLogistics> ctrLogisticsList = ctrLogisticsService.getByBuyContractNo(ctrOutInLedger.getContractNo());
                        if (CollectionUtils.isNotEmpty(ctrLogisticsList)) {
                            ctrOutInLedger.setRealContractNo(ctrLogisticsList.get(0).getSupplierNo());
                        }
                        ctrOutInLedgerDao.save(ctrOutInLedger);
                    }
                }
            } catch (Exception e) {
                logger.error("addSellCtrOutInLedger error");
            }
        }, 8, TimeUnit.SECONDS);
    }

    private void addBuyCtrOutInLedger(CtrContract contract) {
        SCHEDULED_POOL.schedule(() -> {
            try {
                CtrContract ctrContractBuy = ctrContractDao.findByApproveIdAndContractType(contract.getApproveId(), BasConstants.CONTRACT_TYPE_B);
                if (ctrContractBuy != null && Objects.equals(ctrContractBuy.getSealFlg(), true)) {
                    List<CtrOutInLedger> ctrOutInLedgerList = ctrOutInLedgerDao.findByOperationAndContractNo(BasConstants.DICT_OUT_IN_LEDGER_TYPE_1, ctrContractBuy.getContractNo());
                    if (CollectionUtils.isEmpty(ctrOutInLedgerList)) {
                        CtrOutInLedger ctrOutInLedger = new CtrOutInLedger();
                        BeanUtils.copyProperties(ctrContractBuy, ctrOutInLedger);
                        ctrOutInLedger.setId(null);
                        ctrOutInLedger.setSourceId(ctrContractBuy.getId());
                        ctrOutInLedger.setOperTime(new Date());
                        ctrOutInLedger.setOperation(BasConstants.DICT_OUT_IN_LEDGER_TYPE_1);
                        ctrOutInLedger.setPrice(ctrContractBuy.getDealPrice());
                        // 获取公司传真
                        BsCompany bsCompany = bsCompanyService.getEntity(ctrContractBuy.getCompanyId());
                        if (Objects.nonNull(bsCompany)) {
                            ctrOutInLedger.setCompanyFax(bsCompany.getCompanyFax());
                        }
                        // 获取实际合同单号
                        List<CtrLogistics> ctrLogisticsList = ctrLogisticsService.getByBuyContractNo(ctrOutInLedger.getContractNo());
                        if (CollectionUtils.isNotEmpty(ctrLogisticsList)) {
                            ctrOutInLedger.setRealContractNo(ctrLogisticsList.get(0).getSupplierNo());
                        }
                        ctrOutInLedgerDao.save(ctrOutInLedger);
                    }
                }
            } catch (Exception e) {
                logger.error("addCtrOutInLedger error", e);
            }
        }, 10, TimeUnit.SECONDS);
    }

    @Override
    public List<SealUsage> findByContractId(Long contractId) {
        Specification<SealUsage> spec = (root, query, cb) -> {
            // 创建一个 Predicate 条件，用于根据 contractId 进行过滤
            return cb.equal(root.get("contractId"), contractId);
        };
        return getBaseDao().findAll(spec);
    }

    private String successSign(PmApprove approve, PmApproveContents pmApproveContents, SealUsage entity) {
        String resultFileId = entity.getFileId();
        try {
            logger.info("预算编号:{} 业务盖章审批完成后自动执行签署逻辑", approve.getApproveNo());
            resultFileId = autoSealPdfSignFilter.successFLKSignContractByKeyword(pmApproveContents.getCfcaContractNo(), entity.getContractNo());
            if (StringUtils.isNotBlank(resultFileId)) {
                logger.info("原附件ID:{}", pmApproveContents.getFileId());
                logger.info("自动签署成功 fileId:{}", resultFileId);
                entity.setFileId(resultFileId);
                pmApproveContents.setFileId(resultFileId);
                pmApproveContentsDao.save(pmApproveContents);
            }
            autoSealPdfSignFilter.successFLKPurchaseOrder(approve.getId());
        } catch (Exception e) {
            logger.info("业务盖章审批完成后自动执行签署逻辑 error", e);
        }
        return  resultFileId;
    }

    /**
     * 发起业务付款申请
     * @param contract
     */
    private void addAutoApplyPay(CtrContract contract) {
        SCHEDULED_POOL.schedule(() -> {
            try {
                // 获取所有关联的合同数据
                List<CtrContractProfit> ctrContractProfitList = ctrContractProfitService.findByAndApproveId(contract.getApproveId());
                if (CollectionUtils.isNotEmpty(ctrContractProfitList)) {
                    // 转化为map,key 为 buycontractNo
                    Map<String, CtrContractProfit> profitMap = ctrContractProfitList.stream()
                            .collect(Collectors.toMap(CtrContractProfit::getBuyContractNo, it -> it));
                    CtrContractProfit ctrContractProfit = profitMap.get(contract.getContractNo());
                    // 判断是否是特殊链条
                    List<CtrContract> ctrContractList = ctrContractDao.findByApproveId(contract.getApproveId());
                    boolean specialChainFlg = ctrContractList.stream().anyMatch(CtrContract::getSpecialChainFlag);
                    // 获取自动发起付款申请我方名称集合
                    List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.AUTO_APPLY_PAY_OUR_COMPANY);
                    List<String> ourCompanyName = listByCategory.stream().map(BsDictData::getDictName).collect(Collectors.toList());
                    if (ObjectUtils.isNotEmpty(ctrContractProfit)) {
                        boolean bondFlg=false;
                        BigDecimal bondRate= BigDecimal.ZERO;
                        /**
                         * 根据采购合同的定金，来判断是发起全款，还是定金及尾款
                         * 如果有定金，要发起定金、及尾款两个自动付款流程
                         */
                        if(contract.getBondAmount().compareTo(BigDecimal.ZERO)>0){
                            bondFlg = true;
                            bondRate=contract.getBondAmount().divide(contract.getTotalAmount(),12, RoundingMode.HALF_UP);
                        }
                        String sellContractNo = ctrContractProfit.getSellContractNo();
                        // 普通链条，并判断销售合同我方是不是上海中光
                        if (!specialChainFlg) {
                            CtrContractProfit ctrContractProfit1 = profitMap.get(sellContractNo);
                            String contractNoS = ctrContractProfit1.getSellContractNo();
                            CtrContract sptsContract = ctrContractDao.findByContractNo(contractNoS);
                            if (ourCompanyName.contains(sptsContract.getOurCompanyName())) {
                                CtrContract sptbContract = ctrContractDao.findByContractNo(contract.getContractNo());
                                if(bondFlg){
                                    ApplyPay sptbApplyPayB = buildApplyPay(sptbContract,"B",bondRate);
                                    ApplyPay sptbApplyPayR = buildApplyPay(sptbContract,"R",bondRate);
                                    // 自动发起付款流程
                                    startFlow(JsonUtil.obj2Json(sptbApplyPayB), BasConstants.PROCESS_CODE_PAY, sptbContract.getMatchUserId());
                                    startFlow(JsonUtil.obj2Json(sptbApplyPayR), BasConstants.PROCESS_CODE_PAY, sptbContract.getMatchUserId());
                                } else {
                                    ApplyPay sptbApplyPay = buildApplyPay(sptbContract,"A",BigDecimal.ZERO);
                                    // 自动发起付款流程
                                    startFlow(JsonUtil.obj2Json(sptbApplyPay), BasConstants.PROCESS_CODE_PAY, sptbContract.getMatchUserId());
                                }
                            } else {
                                CtrContract sptbContract = ctrContractDao.findByContractNo(contract.getContractNo());
                                ApplyCtrDCSX sptxContract  = applyDcsxDao.findByContractNo(sellContractNo);
                                // 原阳鸿博链条标识
                                Boolean hbFlg = false;
                                if (Objects.nonNull(sptxContract) && ( StringUtils.equals(BasConstants.COMPANY_NAME_YYHB, sptxContract.getCompanyName())
                                        || StringUtils.equals(BasConstants.COMPANY_NAME_YYHB, sptxContract.getOurCompanyName()) )) {
                                    hbFlg = true;
                                }
                                if(bondFlg){
                                    // 发起定金付款
                                    ApplyPay sptbApplyPayB = buildApplyPay(sptbContract,"B",bondRate);
                                    ApplyPay sptxApplyPayB = buildApplyDcsxPay(sptxContract,"B",bondRate);
                                    startFlow(JsonUtil.obj2Json(sptbApplyPayB), BasConstants.PROCESS_CODE_PAY, sptbContract.getMatchUserId());
                                    if (!hbFlg) {
                                        // 鸿博链条迁移至下游收款完成自动发起，或下游未收款且超过上游约定付款日期60天后自动发起
                                        startFlow(JsonUtil.obj2Json(sptxApplyPayB), BasConstants.PROCESS_CODE_DCSX_PAY, sptxContract.getMatchUserId());
                                    }
                                    // 发起尾款
                                    ApplyPay sptbApplyPayR = buildApplyPay(sptbContract,"R",bondRate);
                                    ApplyPay sptxApplyPayR = buildApplyDcsxPay(sptxContract,"R",bondRate);
                                    startFlow(JsonUtil.obj2Json(sptbApplyPayR), BasConstants.PROCESS_CODE_PAY, sptbContract.getMatchUserId());
                                    if (!hbFlg) {
                                        // 鸿博链条迁移至下游收款完成自动发起，或下游未收款且超过上游约定付款日期60天后自动发起
                                        startFlow(JsonUtil.obj2Json(sptxApplyPayR), BasConstants.PROCESS_CODE_DCSX_PAY, sptxContract.getMatchUserId());
                                    }
                                } else {
                                    ApplyPay sptbApplyPay = buildApplyPay(sptbContract,"A",BigDecimal.ZERO);
                                    ApplyPay sptxApplyPay = buildApplyDcsxPay(sptxContract,"A",BigDecimal.ZERO);
                                    // 自动发起付款流程
                                    startFlow(JsonUtil.obj2Json(sptbApplyPay), BasConstants.PROCESS_CODE_PAY, sptbContract.getMatchUserId());
                                    if (!hbFlg) {
                                        // 鸿博链条迁移至下游收款完成自动发起，或下游未收款且超过上游约定付款日期60天后自动发起
                                        startFlow(JsonUtil.obj2Json(sptxApplyPay), BasConstants.PROCESS_CODE_DCSX_PAY, sptxContract.getMatchUserId());
                                    }
                                }
                            }
                        } else {
                            CtrContractProfit ctrContractProfit1 = profitMap.get(ctrContractProfit.getSellContractNo());
                            CtrContractProfit ctrContractProfit2 = profitMap.get(ctrContractProfit1.getSellContractNo());
                            String contractNoS = ctrContractProfit2.getSellContractNo();
                            // 新链条
                            CtrContract sptsContract = ctrContractDao.findByContractNo(contractNoS);
                            if (ourCompanyName.contains(sptsContract.getOurCompanyName())) {
                                CtrContract sptbContract = ctrContractDao.findByContractNo(contract.getContractNo());
                                CtrContract spt1Contract = ctrContractDao.findByContractNo(sellContractNo);
                                if(bondFlg){
                                    ApplyPay sptbApplyPayB = buildApplyPay(sptbContract,"B",bondRate);
                                    ApplyPay spt1ApplyPayB = buildApplyPay(spt1Contract,"B",bondRate);
                                    // 自动发起付款流程
                                    startFlow(JsonUtil.obj2Json(sptbApplyPayB), BasConstants.PROCESS_CODE_PAY, sptbContract.getMatchUserId());
                                    startFlow(JsonUtil.obj2Json(spt1ApplyPayB), BasConstants.PROCESS_CODE_PAY, spt1Contract.getMatchUserId());
                                    ApplyPay sptbApplyPayR = buildApplyPay(sptbContract,"R",bondRate);
                                    ApplyPay spt1ApplyPayR = buildApplyPay(spt1Contract,"R",bondRate);
                                    // 自动发起付款流程
                                    startFlow(JsonUtil.obj2Json(sptbApplyPayR), BasConstants.PROCESS_CODE_PAY, sptbContract.getMatchUserId());
                                    startFlow(JsonUtil.obj2Json(spt1ApplyPayR), BasConstants.PROCESS_CODE_PAY, spt1Contract.getMatchUserId());
                                } else {
                                    ApplyPay sptbApplyPay = buildApplyPay(sptbContract,"A",BigDecimal.ZERO);
                                    ApplyPay spt1ApplyPay = buildApplyPay(spt1Contract,"A",BigDecimal.ZERO);
                                    // 自动发起付款流程
                                    startFlow(JsonUtil.obj2Json(sptbApplyPay), BasConstants.PROCESS_CODE_PAY, sptbContract.getMatchUserId());
                                    startFlow(JsonUtil.obj2Json(spt1ApplyPay), BasConstants.PROCESS_CODE_PAY, spt1Contract.getMatchUserId());
                                }

                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("addAutoApplyPay error");
            }
        }, 13, TimeUnit.SECONDS);
    }

    private ApplyPay buildApplyPay(CtrContract contract,String payType,BigDecimal bondRate) {
        ApplyPay applyPay = new ApplyPay();
        applyPay.setContractId(contract.getId());
        applyPay.setContractNo(contract.getContractNo());
        applyPay.setTotalAmount(contract.getTotalAmount());
        BigDecimal dealedAmount = contract.getDealedAmount() == null ? contract.getDealedAmount() : BigDecimal.ZERO;
        applyPay.setPayedAmount(dealedAmount);
        applyPay.setOurCompanyName(contract.getOurCompanyName());
        applyPay.setCompanyName(contract.getCompanyName());
        applyPay.setCompanyId(contract.getCompanyId());
        // 获取企业银行信息
        List<BsCompanyAccount> companyAccounts = bsCompanyAccountService.findByCompanyId(contract.getCompanyId());
        BsCompanyAccount companyAccount = null;
        if(companyAccounts != null && companyAccounts.size() > 0) {
            for (BsCompanyAccount companyAccountDB : companyAccounts) {
                if(companyAccountDB.getDefaultFlg()){
                    companyAccount = companyAccountDB;
                    break;
                }
            }
            if(companyAccount == null){
                companyAccount = companyAccounts.get(0);
            }
        }
        if(companyAccount!=null){
            applyPay.setBankName(companyAccount.getBankName());
            applyPay.setBankAccount(companyAccount.getBankAccount());
        } else {
            BsCompanyDcsx companyConfig = bsCompanyDcsxService.findByCompanyName(contract.getCompanyName());
            if (Objects.nonNull(companyConfig)) {
                applyPay.setBankName(companyConfig.getCompanyBankName());
                applyPay.setBankAccount(companyConfig.getCompanyCardId());
            } else {
                applyPay.setBankName(null);
                applyPay.setBankAccount(null);
            }
        }
        switch (payType){
            case "A":
                // 全款
                applyPay.setPayAmount(contract.getTotalAmount());
                break;
            case "B":
                // 定金
                BigDecimal bondAmount = contract.getTotalAmount().multiply(bondRate).setScale(2, RoundingMode.HALF_UP);
                if (contract.getBondAmount().compareTo(BigDecimal.ZERO) > 0){
                    bondAmount = contract.getBondAmount();
                }
                applyPay.setPayAmount(bondAmount);
                break;
            case "R":
                // 尾款
                BigDecimal bondAmount1 = contract.getTotalAmount().multiply(bondRate).setScale(2, RoundingMode.HALF_UP);
                if (contract.getBondAmount().compareTo(BigDecimal.ZERO) > 0){
                    bondAmount1 = contract.getBondAmount();
                }
                BigDecimal finalAmount  = contract.getTotalAmount().subtract(bondAmount1);
                applyPay.setPayAmount(finalAmount);
                break;
        }
        applyPay.setPayType(payType);
        // 判断我方是苏高新，收款方是青光、网速，付款类型为账户余额
        if(contract.getOurCompanyName().equals(BasConstants.COMPANY_NAME_SUGX)){
            if(contract.getCompanyName().equals(BasConstants.COMPANY_NAME_QDZG)||contract.getCompanyName().equals(BasConstants.COMPANY_NAME_WSNB)){
                applyPay.setPayMode("F");
            } else {
                applyPay.setPayMode("T");
            }
        } else {
            applyPay.setPayMode("T");
        }
        applyPay.setPayDate(new Date());
        applyPay.setDeptId(contract.getDeptId());
        applyPay.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        return applyPay;
    }

    private ApplyPay buildApplyDcsxPay(ApplyCtrDCSX contract,String payType,BigDecimal bondRate) {
        ApplyPay applyPay = new ApplyPay();
        applyPay.setContractId(contract.getId());
        applyPay.setContractNo(contract.getContractNo());
        applyPay.setTotalAmount(contract.getTotalAmount());
        BigDecimal dealedAmount = contract.getDealedAmount() == null ? contract.getDealedAmount() : BigDecimal.ZERO;
        applyPay.setPayedAmount(dealedAmount);
        applyPay.setOurCompanyName(contract.getOurCompanyName());
        applyPay.setCompanyName(contract.getCompanyName());
        applyPay.setCompanyId(contract.getCompanyId());
        applyPay.setBankName(contract.getBankName());
        applyPay.setBankAccount(contract.getBankAccount());
        switch (payType){
            case "A":
                // 全款
                applyPay.setPayAmount(contract.getTotalAmount());
                break;
            case "B":
                // 定金
                BigDecimal bondAmount = contract.getTotalAmount().multiply(bondRate).setScale(2, RoundingMode.HALF_UP);
                applyPay.setPayAmount(bondAmount);
                break;
            case "R":
                // 尾款
                BigDecimal bondAmount1 = contract.getTotalAmount().multiply(bondRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal finalAmount  = contract.getTotalAmount().subtract(bondAmount1);
                applyPay.setPayAmount(finalAmount);
                break;
        }
        applyPay.setPayType(payType);
        // 判断我方是苏高新，收款方是青光、网速，付款类型为账户余额
        if(contract.getOurCompanyName().equals(BasConstants.COMPANY_NAME_SUGX)){
            if(contract.getCompanyName().equals(BasConstants.COMPANY_NAME_QDZG)||contract.getCompanyName().equals(BasConstants.COMPANY_NAME_WSNB)){
                applyPay.setPayMode("F");
            } else {
                applyPay.setPayMode("T");
            }
        } else {
            applyPay.setPayMode("T");
        }
        applyPay.setPayDate(new Date());
        applyPay.setDeptId(contract.getDeptId());
        applyPay.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        return applyPay;
    }
    /**
     * 发起审批
     */
    private void startFlow(String bizEntityJson, String processCode,Long applyUserId) throws ApplicationException, WebApplicationException {
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        searchVo.setProcessCode(processCode);
        PmProcess process = processClient.findByProcessCode(searchVo);
        if (process == null) {
            throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
        }
        SysUserSdk userById = authOpenFacade.findUserById(applyUserId);
        startVo.setUserId(userById.getUserId());
        startVo.setDeptId(userById.getDeptId());
        startVo.setUserName(userById.getNickName());
        startVo.setProcessId(process.getId());
        startVo.setApproveId(0L);
        startVo.setBizEntityJson(bizEntityJson);
        startVo.setAutoStartFlg(true);
        approveClient.startFlow(startVo);
    }
}

