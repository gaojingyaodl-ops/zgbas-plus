package com.spt.bas.server.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hsoft.file.sdk.constant.FileConstant;
import com.hsoft.file.sdk.remote.FileRemote;
import com.hsoft.file.sdk.util.Base64Utility;
import com.hsoft.file.sdk.vo.FileRespVo;
import com.hsoft.file.sdk.vo.FileUploadBase64Request;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.ApplyProtocolDocCkhDetailVo;
import com.spt.bas.client.vo.ApplyProtocolDocVo;
import com.spt.bas.client.vo.ApplyProtocolDocZnjDetailVo;
import com.spt.bas.client.vo.protocol.*;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.enums.ProtocolDocumentEnum;
import com.spt.bas.server.service.ApplyChargeSalesService;
import com.spt.bas.server.service.IApplyPayRefundDcsxService;
import com.spt.bas.server.service.IApplyProtocolDocumentService;
import com.spt.bas.server.service.IBsCompanyDcsxService;
import com.spt.bas.server.util.BasBusinessUtil;
import com.spt.bas.server.util.MidstreamUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.sign.client.cfca.CfcaResp;
import com.spt.sign.client.remote.ICfcaSignClient;
import com.spt.sign.client.vo.AxqAutoSignVo;
import com.spt.sign.client.vo.AxqContractVo;
import com.spt.sign.client.vo.AxqUploadVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author MoonLight
 * @Date 2024/5/21 16:18
 * @Version 1.0
 */
@Slf4j
@Transactional
@Component("applyProtocolDocumentService")
public class ApplyProtocolDocumentServiceImpl extends BaseService<ApplyProtocolDocument> implements IApplyProtocolDocumentService, IPmService, IPmApproveListener {
    private static final ScheduledExecutorService SCHEDULED_POOL = Executors.newScheduledThreadPool(10);
    @Resource
    private ApplyProtocolDocumentDao applyProtocolDocumentDao;
    @Resource
    private IBsCompanyDcsxService bsCompanyDcsxService;
    @Resource
    private ICfcaSignClient cfcaSignClient;
    @Resource
    private FileRemote fileRemote;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private PmProcessDao pmProcessDao;
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private ApplyDcsxDao applyDcsxDao;
    @Resource
    private MidstreamUtil midstreamUtil;
    @Autowired
    private ApplyMatchDao applyMatchDao;
    @Autowired
    private ApplyMatchDetailDao applyMatchDetailDao;
    @Autowired
    private ApplyChargeSalesService applyChargeSalesService;
    @Resource
    private ICtrContractUpdateService ctrContractUpdateService;
    @Resource
    private IApplyPayRefundDcsxService applyPayRefundDcsxService;

    @Override
    @ServiceTransactional
    public void updateFileId(Long id, String fileId) {
        applyProtocolDocumentDao.updateFileId(id, fileId);
    }

    @Override
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyProtocolDocument entity = applyProtocolDocumentDao.findOne(approve.getBizId());
        if (StringUtils.isNotBlank(entity.getCfcaContractNo())) {
            return;
        }
        if (StringUtils.isNotBlank(entity.getFileId()) && StringUtils.equals(BasConstants.DICT_DOC_TYPE_SP, entity.getDocType())) {
            entity.setAutoSignFlag(false);
            applyProtocolDocumentDao.save(entity);
            return;
        }
        // 1.验证签署方是否开通安心签
        String signCompanyName = entity.getSignCompanyName();
        Boolean verifySignCompany = bsCompanyDcsxService.verifySignCompany(signCompanyName);
        if (Boolean.FALSE.equals(verifySignCompany)) {
            throw new ApplicationException(signCompanyName + "不具备电子签资格！");
        }
        // 2.生成协议文件文件流
        ByteArrayOutputStream byteArrayOutputStream = ProtocolDocumentEnum.generatePdfStream(entity);
        // 3.生成未签署版本协议文件ID
        ProtocolDocumentEnum documentEnum = ProtocolDocumentEnum.getEnumByDocType(entity.getDocType());
        if (Objects.isNull(documentEnum)) {
            throw new ApplicationException("协议文件类型转换异常！");
        }
        String docFileId = fileUploadBase64(byteArrayOutputStream, documentEnum);
        // 4.上传原文合同至安心签
        List<AxqContractVo> axqContractVoList = new ArrayList<>();
        axqContractVoList.add(generateAxqVo(documentEnum, entity, docFileId));
        this.buildAxqContractVoSupplementaryAgreement(entity, documentEnum, docFileId, axqContractVoList);
        AxqUploadVo axqUploadVo = cfcaSignClient.axqUploadContractSigned(axqContractVoList);
        if (Objects.nonNull(axqUploadVo) && StringUtils.equals("60000000", axqUploadVo.getRetCode())) {
            String cfcaContractNo = axqUploadVo.getContractNo();
            log.info("安心签上传合同签署创建成功，合同编号:{}", cfcaContractNo);
            if (StringUtils.isNotBlank(cfcaContractNo)) {
                entity.setCfcaContractNo(cfcaContractNo);
                entity.setFileId(docFileId + BasConstants.COMMA);
                applyProtocolDocumentDao.save(entity);
            }
        } else {
            throw new ApplicationException("协议文件上传失败!");
        }

        // 合同补充协议
        if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_SP, entity.getDocType())) {
            SupplementaryAgreement supplementaryAgreement = JsonUtil.json2Object(SupplementaryAgreement.class, entity.getContent());
            String contractNo = supplementaryAgreement.getContractNo();
            if (supplementaryAgreement.getAutoDcsxSupAgreementFlg() != null && supplementaryAgreement.getAutoDcsxSupAgreementFlg()
                    && (isBuyContract(contractNo) || isSellContract(contractNo))) {
                // 自动发起中游合同补充协议 且 数量变更或单价变更
                BigDecimal totalNumber = supplementaryAgreement.getTotalNumber();
                BigDecimal alterTotalNumber = supplementaryAgreement.getAlterTotalNumber();

                BigDecimal dealPrice = supplementaryAgreement.getDealPrice();
                BigDecimal alterDealPrice = supplementaryAgreement.getAlterDealPrice();

                String brandNumber = supplementaryAgreement.getBrandNumber();

                Boolean autoDcsxFlg = false;
                Boolean totalNumberUpdateFlg = false;
                Boolean dealPriceUpdateFlg = false;
                Boolean brandNumberUpdateFlg = false;
                if (totalNumber != null && alterTotalNumber != null && totalNumber.compareTo(alterTotalNumber) != 0) {
                    autoDcsxFlg = true;
                    totalNumberUpdateFlg = true;
                }
                if (dealPrice != null && alterDealPrice != null && dealPrice.compareTo(alterDealPrice) != 0) {
                    autoDcsxFlg = true;
                    dealPriceUpdateFlg = true;
                }
                if (StringUtils.isNotBlank(brandNumber)) {
                    autoDcsxFlg = true;
                    brandNumberUpdateFlg = true;
                }

                if (autoDcsxFlg) {
                    // 自动发起
                    this.autoDcsxSupAgreement(contractNo, approve, supplementaryAgreement, totalNumberUpdateFlg, dealPriceUpdateFlg, brandNumberUpdateFlg);
                }


            }
        }


    }

    /**
     * 自动发起中游合同补充协议申请
     *
     * @param contractNo
     * @param approve
     */
    public void autoDcsxSupAgreement(String contractNo, PmApprove approve, SupplementaryAgreement entity, Boolean totalNumberUpdateFlg, Boolean dealPriceUpdateFlg, Boolean brandNumberUpdateFlg) {
        SCHEDULED_POOL.schedule(() -> {
            try {
                PmProcess sealUsageProcess = pmProcessDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_APPLY_PROTOCOL_DOC, approve.getEnterpriseId());
                CtrContract contract = ctrContractDao.findByContractNo(contractNo);
                if (Objects.isNull(contract)) {
                    logger.info("根据合同号未找到合同信息，不发起中游合同补充协议申请");
                    return;
                }
                ApplyCtrDCSX dcsxContract = applyDcsxDao.findByDCSXApproveId(contract.getApproveId());
                if (Objects.isNull(dcsxContract)) {
                    logger.info("根据合同号未找到中游合同信息，不发起中游合同补充协议申请");
                    return;
                }

                SupplementaryAgreement dcsxSupplementaryAgreement = new SupplementaryAgreement();
                dcsxSupplementaryAgreement.setProtocolNo(contractNo.replaceAll("[^0-9]", ""));
                dcsxSupplementaryAgreement.setTargetCompanyName(dcsxContract.getCompanyName());
                dcsxSupplementaryAgreement.setOurCompanyName(dcsxContract.getOurCompanyName());
                dcsxSupplementaryAgreement.setContractDate(dcsxContract.getContractTime());
                dcsxSupplementaryAgreement.setContractNo(dcsxContract.getContractNo());
                dcsxSupplementaryAgreement.setProductName(removeLastSegment(contract.getProductsName()));
                dcsxSupplementaryAgreement.setTotalNumber(dcsxContract.getTotalNumber());
                BigDecimal totalNumber = dcsxContract.getTotalNumber();
                if (totalNumberUpdateFlg) {
                    totalNumber = entity.getAlterTotalNumber();
                    dcsxSupplementaryAgreement.setAlterTotalNumber(totalNumber);
                }
                dcsxSupplementaryAgreement.setDeliveryMode(entity.getDeliveryMode());
                dcsxSupplementaryAgreement.setDealPrice(dcsxContract.getDealPrice());
                dcsxSupplementaryAgreement.setTotalAmount(dcsxContract.getTotalAmount());

                if (brandNumberUpdateFlg) {
                    dcsxSupplementaryAgreement.setBrandNumber(entity.getBrandNumber());
                }
                if (dealPriceUpdateFlg) {
                    ApplyMatchDetail buyDetail = new ApplyMatchDetail();
                    ApplyMatchDetail sellDetail = new ApplyMatchDetail();
                    ApplyMatch match = applyMatchDao.findByApproveId(contract.getApproveId());
                    List<ApplyMatchDetail> detailList = applyMatchDetailDao.findByApplyMatchId(match.getId());
                    if (CollectionUtils.isNotEmpty(detailList)) {
                        for (ApplyMatchDetail detail : detailList) {
                            if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, detail.getContractType())) {
                                BeanUtils.copyProperties(detail, sellDetail);
                            } else if (StringUtils.equals(BasConstants.CONTRACT_TYPE_B, detail.getContractType())) {
                                BeanUtils.copyProperties(detail, buyDetail);
                            }
                        }
                    }
                    if (isBuyContract(contractNo)) {
                        buyDetail.setDealPrice(entity.getAlterDealPrice().setScale(3, RoundingMode.HALF_UP));
                        buyDetail.setTotalAmount(totalNumber.multiply(entity.getAlterDealPrice()).setScale(3, RoundingMode.HALF_UP));
                        match.setBuyAmount(totalNumber.multiply(entity.getAlterDealPrice()).setScale(3, RoundingMode.HALF_UP));
                    }
                    if (isSellContract(contractNo)) {
                        sellDetail.setDealPrice(entity.getAlterDealPrice().setScale(3, RoundingMode.HALF_UP));
                        sellDetail.setTotalAmount(totalNumber.multiply(entity.getAlterDealPrice()).setScale(3, RoundingMode.HALF_UP));
                        match.setSellAmount(totalNumber.multiply(entity.getAlterDealPrice()).setScale(3, RoundingMode.HALF_UP));
                    }
                    Map<String, BsCompanyDcsx> companyConfigMap = bsCompanyDcsxService.getCompanyConfigMap();
                    BigDecimal dealPrice = midstreamUtil.generateMidstream(dcsxContract, match, buyDetail, sellDetail);
                    if (Objects.isNull(dealPrice) || dealPrice.compareTo(BigDecimal.ZERO) == 0) {
                        log.info("generateMidstream warn result 0");
                        dealPrice = applyChargeSalesService.calculatePrice(match, dcsxContract.getCreditDays(), companyConfigMap);
                    }
                    dcsxSupplementaryAgreement.setAlterDealPrice(dealPrice);
                    dcsxSupplementaryAgreement.setAlterTotalAmount(totalNumber.multiply(dealPrice).setScale(3, RoundingMode.HALF_UP));

                }
                if (totalNumberUpdateFlg) {
                    dcsxSupplementaryAgreement.setAlterTotalAmount(totalNumber.multiply(dcsxContract.getDealPrice()).setScale(3, RoundingMode.HALF_UP));
                }
                dcsxSupplementaryAgreement.setProtocolDate(entity.getProtocolDate());
                dcsxSupplementaryAgreement.setAutoDcsxSupAgreementFlg(false);
                dcsxSupplementaryAgreement.setAutoRefreshContractFlg(entity.getAutoRefreshContractFlg());
                // 主表
                ApplyProtocolDocument protocolDocument = new ApplyProtocolDocument();
                protocolDocument.setDocType(BasConstants.DICT_DOC_TYPE_SP);
                protocolDocument.setSignCompanyName(dcsxContract.getOurCompanyName());
                protocolDocument.setEnterpriseId(approve.getEnterpriseId());
                protocolDocument.setContent(JsonUtil.obj2Json(dcsxSupplementaryAgreement));

                String entityJson = JsonUtil.obj2Json(protocolDocument);
                PmApproveSaveVo startVo = new PmApproveSaveVo();
                startVo.setBizEntityJson(entityJson);
                startVo.setProcessId(sealUsageProcess.getId());
                startVo.setDeptId(approve.getDeptId());
                startVo.setMode("A");
                startVo.setStatus(BasConstants.APPROVE_STATUS_A);
                startVo.setApproveId(0L);
                startVo.setUserId(approve.getCreateUserId());
                startVo.setUserName(approve.getCreateUserName());
                startVo.setEnterpriseId(approve.getEnterpriseId());
                startVo.setAutoStartMessage("自动发起中游合同补充协议申请，原审批编号：" + approve.getApproveNo());
                startVo.setAutoStartFlgReal(true);
                pmApproveService.startFlow(startVo);
            } catch (Exception e) {
                logger.error("applyBuy autoDcsxSupAgreement error:{}", e.getMessage());
            }
        }, 5, TimeUnit.SECONDS);


    }

    public Boolean isBuyContract(String contractNo) {
        return contractNo.contains("SPTB") || contractNo.contains("KCB") || contractNo.contains("XYB");
    }

    public Boolean isSellContract(String contractNo) {
        return contractNo.contains("SPTS") || contractNo.contains("KCS") || contractNo.contains("XYS");
    }

    public static String removeLastSegment(String str) {
        int lastSlashIndex = str.lastIndexOf("/");
        if (lastSlashIndex != -1) {
            return str.substring(0, lastSlashIndex);
        } else {
            return str;  // 如果字符串中没有 "/", 则返回原字符串
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
        if (StringUtils.equals(BasConstants.APPROVE_STATUS_D, approve.getStatus())) {
            ApplyProtocolDocument entity = applyProtocolDocumentDao.findOne(approve.getBizId());
            if (BooleanUtils.isTrue(entity.getAutoSignFlag())){
                String successFileId = this.successAutoSign(entity);
                if (StringUtils.isBlank(successFileId)) {
                    throw new ApplicationException("协议文件自动签署失败!");
                }
                logger.info("原附件ID:{}", entity.getFileId());
                logger.info("自动签署成功 fileId:{}", successFileId);
                entity.setFileId(successFileId);
                applyProtocolDocumentDao.save(entity);
            }

            this.autoRefreshContract(entity, approve);
        }
    }

    /**
     * 审批撤回
     *
     * @param vo
     */
    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    @Override
    public Map<String, Object> buildConditionDefaultMap(IPmEntity pmEntity) {
        try {
            ApplyProtocolDocument entity = (ApplyProtocolDocument) pmEntity;
            String contractNo = BasBusinessUtil.buildMiddleToSell(parseContractNo(entity));
            CtrContract ctrContract = ctrContractDao.findByContractNo(contractNo);
            return BasBusinessUtil.buildConditionDefaultMap(ctrContract);
        } catch (Exception e) {
            logger.error("buildConditionDefaultMap error", e);
        }
        return new HashMap<>();
    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyProtocolDocument entity = (ApplyProtocolDocument) pmEntity;
            return save(entity);
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
        ApplyProtocolDocument entity = (ApplyProtocolDocument) pmEntity;
        String docTypeName = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_DOC_TYPE, entity.getDocType());
        String contractNo = parseContractNo(entity);
        return SubjectUtil.formatSubject(docTypeName, entity.getSignCompanyName(), contractNo);
    }

    @Override
    public BaseDao<ApplyProtocolDocument> getBaseDao() {
        return applyProtocolDocumentDao;
    }

    private String parseContractNo(ApplyProtocolDocument entity) {
        String contractNo = "";
        String docType = entity.getDocType();
        ApplyProtocolDocVo ckhVo = JSONObject.parseObject(entity.getContent(), ApplyProtocolDocVo.class);
        if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_RL, docType)) {
            List<ApplyProtocolDocCkhDetailVo> detailList = JSONArray.parseArray(ckhVo.getDetailList(), ApplyProtocolDocCkhDetailVo.class);
            for (ApplyProtocolDocCkhDetailVo detail : detailList) {
                contractNo = SubjectUtil.formatSubject(contractNo, detail.getContractNo());
            }
        } else if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_LN, docType)) {
            List<ApplyProtocolDocZnjDetailVo> detailList = JSONArray.parseArray(ckhVo.getDetailList(), ApplyProtocolDocZnjDetailVo.class);
            for (ApplyProtocolDocZnjDetailVo detail : detailList) {
                contractNo = SubjectUtil.formatSubject(contractNo, detail.getContractNo());
            }
        } else if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_RP, docType)) {
            List<ReminderPaymentAgreement> detailList = JSONArray.parseArray(ckhVo.getDetailList(), ReminderPaymentAgreement.class);
            for (ReminderPaymentAgreement detail : detailList) {
                contractNo = SubjectUtil.formatSubject(contractNo, detail.getContractNo());
            }
        } else if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_CP, docType)) {
            RescissionAgreement rescissionAgreement = JsonUtil.json2Object(RescissionAgreement.class, entity.getContent());
            contractNo = rescissionAgreement.getContractNo();
        } else if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_RA, docType)) {
            RepaymentAgreement repaymentAgreement = JSONUtil.toBean(entity.getContent(), RepaymentAgreement.class);
            List<RepaymentAgreement.RepaymentAgreementDetail> repaymentDetailList = JSONUtil.toList(repaymentAgreement.getRepaymentDetailListStr(), RepaymentAgreement.RepaymentAgreementDetail.class);
            for (RepaymentAgreement.RepaymentAgreementDetail detail : repaymentDetailList) {
                contractNo = SubjectUtil.formatSubject(contractNo, detail.getContractNo());
            }
        } else if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_SP, docType)) {
            SupplementaryAgreement supplementaryAgreement = JsonUtil.json2Object(SupplementaryAgreement.class, entity.getContent());
            contractNo = supplementaryAgreement.getContractNo();
        } else if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_DZ, docType)) {
            DzdAgreement dzdAgreement = JsonUtil.json2Object(DzdAgreement.class, entity.getContent());
            contractNo = dzdAgreement.getDzdCompanyName();
        }
        return contractNo;
    }

    /**
     * 上传Base64 协议文件至附件服务
     *
     * @param outputStream
     * @param documentEnum
     * @return
     * @throws ApplicationException
     */
    private String fileUploadBase64(ByteArrayOutputStream outputStream, ProtocolDocumentEnum documentEnum) throws ApplicationException {
        FileUploadBase64Request fileRequest = new FileUploadBase64Request();
        fileRequest.setFilePath(BasConstants.APP_CODE + "/");
        fileRequest.setServerName(BasConstants.APP_CODE);
        fileRequest.setAppCode(PropertiesUtil.getProperty(FileConstant.FILE_BUCKET));
        List<FileUploadBase64Request.Base64DataVo> dataList = new ArrayList<>();
        FileUploadBase64Request.Base64DataVo dataVo = new FileUploadBase64Request.Base64DataVo();
        String fileName = Objects.nonNull(documentEnum) ? documentEnum.getDocName() : "TEST";
        dataVo.setFileName(fileName + ".pdf");
        dataVo.setBase64Data(Base64Utility.base64Encode(outputStream.toByteArray()));
        dataList.add(dataVo);
        fileRequest.setDataList(dataList);
        // 3. 获取合同PDF附件ID
        FileRespVo fileRespVo = fileRemote.uploadBase64(fileRequest);
        if (Objects.isNull(fileRespVo) || StringUtils.isBlank(fileRespVo.getFileId())) {
            throw new ApplicationException("协议文件生成失败！");
        }
        return fileRespVo.getFileId();
    }

    /**
     * 组装上传文件签署接口参数
     *
     * @param documentEnum
     * @param entity
     * @param docFileId
     * @return
     */
    private AxqContractVo generateAxqVo(ProtocolDocumentEnum documentEnum, ApplyProtocolDocument entity, String docFileId) {
        AxqContractVo vo = new AxqContractVo();
        vo.setSignKeyword(documentEnum.getSignKeyWord(entity));
        vo.setCfcaTemplateName(documentEnum.getDocName());
        vo.setBuyerCompanyName(entity.getSignCompanyName());
        vo.setSignType("CTR");
        vo.setFileId(docFileId);
        vo.setGenerateShortUrlFlg(false);
        if (StringUtils.isNotBlank(docFileId) && docFileId.endsWith(",")) {
            vo.setFileId(docFileId.replaceAll(",", ""));
        }
        vo.setProjectCode("003");
        return vo;
    }

    /**
     * 组装自动签署接口参数
     *
     * @param entity
     * @return
     */
    private AxqAutoSignVo convertAutoSign(ApplyProtocolDocument entity) throws ApplicationException {
        AxqAutoSignVo axqAutoSignVo = new AxqAutoSignVo();
        axqAutoSignVo.setCfcaContractNo(entity.getCfcaContractNo());
        ProtocolDocumentEnum documentEnum = ProtocolDocumentEnum.getEnumByDocType(entity.getDocType());
        if (Objects.isNull(documentEnum)) {
            throw new ApplicationException("协议文件类型转换异常!");
        }
        List<AxqAutoSignVo.Signatorie> signatorieList = new ArrayList<>();
        AxqAutoSignVo.Signatorie signatorie = new AxqAutoSignVo.Signatorie();
        signatorie.setCompanyName(entity.getSignCompanyName());
        signatorie.setKeyWord(documentEnum.getSignKeyWord(entity));
        signatorie.setSealType("CTR");
        signatorie.setImageHeight("150");
        signatorie.setImageWidth("150");
        signatorie.setOffsetCoordX(documentEnum.getOffsetX());
        signatorie.setOffsetCoordY(documentEnum.getOffsetY());
        signatorieList.add(signatorie);
        this.buildSupplementaryAgreementSign(entity, documentEnum, signatorieList);
        axqAutoSignVo.setSignatorieList(signatorieList);
        return axqAutoSignVo;
    }

    private void buildAxqContractVoSupplementaryAgreement(ApplyProtocolDocument entity, ProtocolDocumentEnum documentEnum, String docFileId, List<AxqContractVo> axqContractVoList) {
        if (!StringUtils.equals(ProtocolDocumentEnum.SUPPLEMENTARY_PROTOCOL.getDocType(), entity.getDocType())) {
            return;
        }
        SupplementaryAgreement agreement = JsonUtil.json2Object(SupplementaryAgreement.class, entity.getContent());
        if (Objects.isNull(agreement) || StringUtils.isBlank(agreement.getContractNo())) {
            return;
        }
        ApplyCtrDCSX ctrDCSX = applyDcsxDao.findByContractNo(agreement.getContractNo());
        if (Objects.isNull(ctrDCSX)) {
            return;
        }
        AxqContractVo vo = new AxqContractVo();
        vo.setSignKeyword("（盖章）：" + agreement.getTargetCompanyName());
        vo.setCfcaTemplateName(documentEnum.getDocName());
        vo.setBuyerCompanyName(agreement.getTargetCompanyName());
        vo.setSignType("CTR");
        vo.setFileId(docFileId);
        vo.setGenerateShortUrlFlg(false);
        if (StringUtils.isNotBlank(docFileId) && docFileId.endsWith(",")) {
            vo.setFileId(docFileId.replaceAll(",", ""));
        }
        vo.setProjectCode("003");
        axqContractVoList.add(vo);
    }

    private void buildSupplementaryAgreementSign(ApplyProtocolDocument entity, ProtocolDocumentEnum documentEnum, List<AxqAutoSignVo.Signatorie> signatorieList) {
        if (!StringUtils.equals(ProtocolDocumentEnum.SUPPLEMENTARY_PROTOCOL.getDocType(), entity.getDocType())) {
            return;
        }
        SupplementaryAgreement agreement = JsonUtil.json2Object(SupplementaryAgreement.class, entity.getContent());
        if (Objects.isNull(agreement) || StringUtils.isBlank(agreement.getContractNo())) {
            return;
        }
        ApplyCtrDCSX ctrDCSX = applyDcsxDao.findByContractNo(agreement.getContractNo());
        if (Objects.isNull(ctrDCSX)) {
            return;
        }
        AxqAutoSignVo.Signatorie signatorie1 = new AxqAutoSignVo.Signatorie();
        signatorie1.setCompanyName(agreement.getTargetCompanyName());
        signatorie1.setKeyWord("（盖章）：" + agreement.getTargetCompanyName());
        signatorie1.setSealType("CTR");
        signatorie1.setImageHeight("150");
        signatorie1.setImageWidth("150");
        signatorie1.setOffsetCoordX(documentEnum.getOffsetX());
        signatorie1.setOffsetCoordY(documentEnum.getOffsetY());
        signatorieList.add(signatorie1);
    }

    /**
     * 执行安心签自动签署逻辑
     *
     * @param entity
     * @return
     */
    private String successAutoSign(ApplyProtocolDocument entity) throws ApplicationException {
        String cfcaContractNo = entity.getCfcaContractNo();
        if (StringUtils.isBlank(cfcaContractNo)) {
            return "";
        }
        AxqAutoSignVo axqAutoSignVo = convertAutoSign(entity);
        CfcaResp<String> cfcaResp = cfcaSignClient.autoSignContract(axqAutoSignVo);
        if (Objects.nonNull(cfcaResp) && cfcaResp.isSuccess()) {
            return cfcaResp.getData();
        }
        return "";
    }

    private void autoRefreshContract(ApplyProtocolDocument entity, PmApprove approve) {
        String docType = entity.getDocType();
        String protocolFileId = entity.getFileId();
        if (!StringUtils.equals(ProtocolDocumentEnum.SUPPLEMENTARY_PROTOCOL.getDocType(), docType)) {
            logger.info("autoRefreshContract stop approveId:{}, docType:{}", entity.getApproveId(), docType);
            return;
        }
        ProtocolDocumentEnum documentEnum = ProtocolDocumentEnum.getEnumByDocType(docType);
        if (Objects.isNull(documentEnum)) {
            logger.info("autoRefreshContract stop documentEnum is empty");
            return;
        }
        SupplementaryAgreement agreement = JsonUtil.json2Object(SupplementaryAgreement.class, entity.getContent());
        if (Objects.isNull(agreement)) {
            logger.info("autoRefreshContract stop agreement parse null");
            return;
        }
        if (!Boolean.TRUE.equals(agreement.getAutoRefreshContractFlg())) {
            logger.info("autoRefreshContract stop autoRefreshContractFlg is :{}", agreement.getAutoRefreshContractFlg());
            return;
        }
        SCHEDULED_POOL.schedule(() -> {
            ctrContractUpdateService.refreshContractWithProtocolDocument(agreement, protocolFileId);

            applyPayRefundDcsxService.autoStartRefundWithProtocolDocument(agreement, approve);
        }, 5, TimeUnit.SECONDS);
    }
}
