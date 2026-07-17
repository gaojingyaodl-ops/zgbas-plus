package com.spt.bas.server.service.impl;

import com.google.common.base.Splitter;
import com.hsoft.file.sdk.constant.FileConstant;
import com.hsoft.file.sdk.remote.FileRemote;
import com.hsoft.file.sdk.util.Base64Utility;
import com.hsoft.file.sdk.vo.FileRespVo;
import com.hsoft.file.sdk.vo.FileUploadBase64Request;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.protocol.SupplementaryAgreement;
import com.spt.bas.server.dao.BsContractTemplateDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.CtrContractTextDao;
import com.spt.bas.server.enums.ProtocolDocumentEnum;
import com.spt.bas.server.service.IApplyDcsxService;
import com.spt.bas.server.service.IFileRecordService;
import com.spt.bas.server.util.HtmlToPdf;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.prop.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * PDF合同处理生成
 *
 * @Author MoonLight
 * @Date 2023/3/29 15:33
 * @Version 1.0
 */
@Slf4j
@Component
public class CtrContractPdfService {
    private static final String ITEXT_BODY_HTML = "<body>";
    private static final String ITEXT_CSS_TEMPLATE = "itext_css_template";

    private static final String PDF_FILE_SUFFIX = ".pdf";
    @Resource
    private BsContractTemplateDao bsContractTemplateDao;
    @Resource
    private CtrContractTextDao ctrContractTextDao;
    @Resource
    private IApplyDcsxService appplyDcsxService;
    @Resource
    private CtrContractDao ctrContractDao;
    @Resource
    private FileRemote fileRemote;
    @Resource
    private IFileRecordService fileRecordService;

    /**
     * 根据合同ID生成PDF合同附件-下游
     *
     * @param ctrContractId
     * @return
     */
    public String generateContractPdf(Long ctrContractId) {
        CtrContract contract = ctrContractDao.findOne(ctrContractId);
        if (Objects.nonNull(contract)){
            return generateContractPdf(contract, null);
        }
        return "";
    }

    /**
     * 根据合同ID生成PDF合同附件-下游
     *
     * @param contract
     * @return
     */
    public String generateContractPdf(CtrContract contract, CtrContract targetContract) {
        if (Objects.nonNull(targetContract)){
            return generateVirtualProtocolFileId(targetContract, contract);
        }
        CtrContractText contractText = ctrContractTextDao.findByCtrContractId(contract.getId());
        if (Objects.isNull(contractText) || StringUtils.isBlank(contractText.getContent())) {
            log.info("generateContractPdf error, contractText is null ctrContractId:{}", contract.getId());
            return null;
        }
        return getContractPdfFileId(contractText, null, contract.getContractNo(), contract.getEnterpriseId());
    }

    /**
     * 根据合同ID生成PDF合同附件-下游
     *
     * @return
     */
    public String generateProtocolPdf(CtrContract sellContract,CtrContract buyContract) {
        if (Objects.nonNull(sellContract) && Objects.nonNull(buyContract)){
            return generateProtocolFileId(sellContract, buyContract);
        }
        return "";
    }

    private String generateProtocolFileId(CtrContract sellContract, CtrContract buyContract) {
        String virtualProtocolFileId = "";
        try {
            ApplyProtocolDocument document = convertProtocol(sellContract, buyContract);
            ByteArrayOutputStream outputStream = ProtocolDocumentEnum.generatePdfStream(document);
            virtualProtocolFileId = ProtocolDocumentEnum.fileUploadBase64(outputStream, ProtocolDocumentEnum.getEnumByDocType(document.getDocType()));
        } catch (Exception e) {
            log.error("generateVirtualProtocolFileId error", e);
        }
        return virtualProtocolFileId;
    }

    private ApplyProtocolDocument convertProtocol(CtrContract sellContract, CtrContract buyContract){
        ApplyProtocolDocument document = new ApplyProtocolDocument();
        SupplementaryAgreement agreement = new SupplementaryAgreement();
        agreement.setProtocolNo(sellContract.getContractNo());
        agreement.setTargetCompanyName(sellContract.getOurCompanyName());
        agreement.setOurCompanyName(sellContract.getCompanyName());
        agreement.setContractDate(sellContract.getContractTime());
        agreement.setContractDateStr(DateOperator.formatDate(sellContract.getContractTime(),"yyyy年MM月dd日"));
        agreement.setContractNo(sellContract.getContractNo());
        agreement.setTotalNumber(sellContract.getTotalNumber());
        agreement.setDealPrice(sellContract.getDealPrice());
        agreement.setTotalAmount(sellContract.getTotalAmount());
        List<String> productNames = Splitter.on(sellContract.getProductsName()).omitEmptyStrings().splitToList(BasConstants.OBL);
        agreement.setProductName(sellContract.getProductsName());
        if (CollectionUtils.isNotEmpty(productNames) && productNames.size() >= 2){
            agreement.setProductName(productNames.get(0) + BasConstants.OBL + productNames.get(1));
        }
        if (sellContract.getTotalNumber().compareTo(sellContract.getTotalNumber()) != 0){
            agreement.setAlterTotalNumber(sellContract.getTotalNumber());
        }
        agreement.setAlterDealPrice(sellContract.getSettlementDealPrice());
        agreement.setAlterTotalAmount(sellContract.getSettlementTotalAmount());
        agreement.setProtocolDate(new Date());
        agreement.setProtocolDateStr(DateOperator.formatDate(new Date(), "yyyy年MM月dd日"));
        document.setContent(JsonUtil.obj2Json(agreement));
        document.setEnterpriseId(sellContract.getEnterpriseId());
        document.setDocType(ProtocolDocumentEnum.SUPPLEMENTARY_PROTOCOL2.getDocType());
        return document;
    }

    /**
     * 根据合同ID生成PDF合同附件-中游
     *
     * @return
     */
    public String generateDcsxProtocolPdf(ApplyCtrDCSX entity) {
        if (Objects.nonNull(entity) ){
            return generateDcsxProtocolFileId(entity);
        }
        return "";
    }

    private String generateDcsxProtocolFileId(ApplyCtrDCSX entity) {
        String virtualProtocolFileId = "";
        try {
            ApplyProtocolDocument document = convertDcsxProtocol(entity);
            ByteArrayOutputStream outputStream = ProtocolDocumentEnum.generatePdfStream(document);
            virtualProtocolFileId = ProtocolDocumentEnum.fileUploadBase64(outputStream, ProtocolDocumentEnum.getEnumByDocType(document.getDocType()));
        } catch (Exception e) {
            log.error("generateVirtualProtocolFileId error", e);
        }
        return virtualProtocolFileId;
    }
    private ApplyProtocolDocument convertDcsxProtocol(ApplyCtrDCSX entity){
        ApplyProtocolDocument document = new ApplyProtocolDocument();
        SupplementaryAgreement agreement = new SupplementaryAgreement();
        agreement.setProtocolNo(entity.getContractNo());
        agreement.setTargetCompanyName(entity.getCompanyName());
        agreement.setOurCompanyName(entity.getOurCompanyName());
        agreement.setContractDate(entity.getContractTime());
        agreement.setContractDateStr(DateOperator.formatDate(entity.getContractTime(),"yyyy年MM月dd日"));
        agreement.setContractNo(entity.getContractNo());
        agreement.setTotalNumber(entity.getTotalNumber());
        agreement.setDealPrice(entity.getDealPrice());
        agreement.setTotalAmount(entity.getTotalAmount());
        CtrContract contract = ctrContractDao.findByApproveIdAndContractType(entity.getApproveId(), BasConstants.CONTRACT_TYPE_B);
        List<String> productNames = Splitter.on(contract.getProductsName()).omitEmptyStrings().splitToList(BasConstants.OBL);
        agreement.setProductName(contract.getProductsName());
        if (CollectionUtils.isNotEmpty(productNames) && productNames.size() >= 2){
            agreement.setProductName(productNames.get(0) + BasConstants.OBL + productNames.get(1));
        }
        if (entity.getTotalNumber().compareTo(entity.getTotalNumber()) != 0){
            agreement.setAlterTotalNumber(entity.getTotalNumber());
        }
        agreement.setAlterDealPrice(entity.getSettlementDealPrice());
        agreement.setAlterTotalAmount(entity.getSettlementTotalAmount());
        agreement.setProtocolDate(new Date());
        agreement.setProtocolDateStr(DateOperator.formatDate(new Date(), "yyyy年MM月dd日"));
        document.setContent(JsonUtil.obj2Json(agreement));
        document.setEnterpriseId(entity.getEnterpriseId());
        document.setDocType(ProtocolDocumentEnum.SUPPLEMENTARY_PROTOCOL2.getDocType());
        return document;
    }
    /**
     * 根据合同ID生成PDF合同附件-中游
     *
     * @param entity
     * @return
     */
    public String generateContractPdfDcsx(ApplyCtrDCSX entity) {
        String htmlContent = appplyDcsxService.getDcsxTemplateContract(entity);
        return getContractPdfFileId(null, htmlContent, entity.getContractNo(), entity.getEnterpriseId());
    }

    private String generateVirtualProtocolFileId(CtrContract targetContract, CtrContract entity) {
        String virtualProtocolFileId = "";
        try {
            ApplyProtocolDocument document = convertVirtualProtocol(targetContract, entity);
            ByteArrayOutputStream outputStream = ProtocolDocumentEnum.generatePdfStream(document);
            virtualProtocolFileId = ProtocolDocumentEnum.fileUploadBase64(outputStream, ProtocolDocumentEnum.getEnumByDocType(document.getDocType()));
        } catch (Exception e) {
            log.error("generateVirtualProtocolFileId error", e);
        }
        return virtualProtocolFileId;
    }

    private ApplyProtocolDocument convertVirtualProtocol(CtrContract targetContract, CtrContract entity){
        ApplyProtocolDocument document = new ApplyProtocolDocument();
        SupplementaryAgreement agreement = new SupplementaryAgreement();
        agreement.setProtocolNo(entity.getContractNo());
        agreement.setTargetCompanyName(targetContract.getCompanyName());
        agreement.setOurCompanyName(targetContract.getOurCompanyName());
        agreement.setContractDate(entity.getContractTime());
        agreement.setContractDateStr(DateOperator.formatDate(entity.getContractTime(),"yyyy年MM月dd日"));
        agreement.setContractNo(targetContract.getContractNo());
        agreement.setTotalNumber(targetContract.getTotalNumber());
        agreement.setDealPrice(targetContract.getDealPrice());
        agreement.setTotalAmount(targetContract.getTotalAmount());
        List<String> productNames = Splitter.on(targetContract.getProductsName()).omitEmptyStrings().splitToList(BasConstants.OBL);
        agreement.setProductName(targetContract.getProductsName());
        if (CollectionUtils.isNotEmpty(productNames) && productNames.size() >= 2){
            agreement.setProductName(productNames.get(0) + BasConstants.OBL + productNames.get(1));
        }
        if (targetContract.getTotalNumber().compareTo(entity.getTotalNumber()) != 0){
            agreement.setAlterTotalNumber(entity.getTotalNumber());
        }
        BigDecimal newDealPrice = calculateVirtualPrice(entity, targetContract);
        if (targetContract.getDealPrice().compareTo(newDealPrice) != 0){
            agreement.setAlterDealPrice(newDealPrice);
        }
        BigDecimal newTotalAmount = newDealPrice.multiply(entity.getTotalNumber()).setScale(2, RoundingMode.HALF_UP);
        if (targetContract.getTotalAmount().compareTo(newTotalAmount) != 0){
            agreement.setAlterTotalAmount(newTotalAmount);
        }
        agreement.setProtocolDate(new Date());
        agreement.setProtocolDateStr(DateOperator.formatDate(new Date(), "yyyy年MM月dd日"));
        document.setContent(JsonUtil.obj2Json(agreement));
        document.setEnterpriseId(entity.getEnterpriseId());
        document.setDocType(ProtocolDocumentEnum.SUPPLEMENTARY_PROTOCOL2.getDocType());
        return document;
    }

    private BigDecimal calculateVirtualPrice(CtrContract buyContract, CtrContract targetContract) {
        BigDecimal agreementDealPrice = buyContract.getAgreementDealPrice();
        if (Objects.nonNull(agreementDealPrice) && agreementDealPrice.compareTo(BigDecimal.ZERO) > 0) {
            return agreementDealPrice;
        }
        BigDecimal param_00019 = new BigDecimal("0.00019");
        CtrContract sellContract = ctrContractDao.findByApproveIdAndContractType(buyContract.getApproveId(), BasConstants.CONTRACT_TYPE_S);
        BigDecimal sellDealPrice = sellContract.getDealPrice();
        BigDecimal buyDealPrice = targetContract.getDealPrice();
        log.info("targetContract ourCompanyName:{}", targetContract.getOurCompanyName());
        // 销售单价- 采购价*（下游回款时间-中游回款时间+1）* 0.00019
        long days = DateOperator.compareDays(buyContract.getPayFullTime(), sellContract.getPayFullTime()) + 1L;
        days = Math.max(days, 1);
        log.info("销售价:{}，采购价：{}，下游回款时间：{}，上游回款时间：{}, 账期:{}", sellDealPrice, buyDealPrice, sellContract.getPayFullTime(), buyContract.getPayFullTime(), days);
        BigDecimal param = buyDealPrice.multiply(new BigDecimal(days)).multiply(param_00019).setScale(2, RoundingMode.HALF_UP);
        agreementDealPrice = sellDealPrice.subtract(param);
        ctrContractDao.updateAgreementDealPrice(buyContract.getId(), agreementDealPrice);
        return agreementDealPrice;
    }

    /**
     * HTML字符串转PDF文件并上传至附件系统，返回附件ID
     *
     * @param contractText
     * @param htmlContent
     * @param contractNo
     * @return
     */
    private String getContractPdfFileId(CtrContractText contractText, String htmlContent, String contractNo, Long enterpriseId) {
        String contractPdfFileId = "";
        try {
            BsContractTemplate template = bsContractTemplateDao.findByTemplateTagAndEnterpriseId(ITEXT_CSS_TEMPLATE, enterpriseId);
            if (Objects.isNull(template) || StringUtils.isBlank(template.getContent())) {
                log.info("generateContractPdf error, template is null");
                return null;
            }

            // 1. Html数据格式处理合并
            StringBuilder content = new StringBuilder(template.getContent());
            String contentText = StringUtils.isBlank(htmlContent) ? contractText.getContent() : htmlContent;
            int insertIndex = content.indexOf(ITEXT_BODY_HTML) + ITEXT_BODY_HTML.length();
            String contractHtml = content.insert(insertIndex, contentText).toString();
            // 2. 生成PDF合同文件流
            ByteArrayOutputStream outputStream = HtmlToPdf.createPdfByHtml(contractHtml);

            // 3. 将PDF文件上传至附件系统
            FileUploadBase64Request fileRequest = new FileUploadBase64Request();
            fileRequest.setFilePath(BasConstants.APP_CODE + "/");
            fileRequest.setServerName(BasConstants.APP_CODE);
            fileRequest.setAppCode(PropertiesUtil.getProperty(FileConstant.FILE_BUCKET));
            List<FileUploadBase64Request.Base64DataVo> dataList = new ArrayList<>();
            FileUploadBase64Request.Base64DataVo dataVo = new FileUploadBase64Request.Base64DataVo();
            dataVo.setFileName(contractNo + PDF_FILE_SUFFIX);
            dataVo.setBase64Data(Base64Utility.base64Encode(outputStream.toByteArray()));
            dataList.add(dataVo);
            fileRequest.setDataList(dataList);
            // 3. 获取合同PDF附件ID
            FileRespVo fileRespVo = fileRemote.uploadBase64(fileRequest);
            contractPdfFileId = fileRespVo.getFileId();
            saveFileRecord(fileRespVo, dataVo.getFileName());
        } catch (Exception e) {
            log.info("getContractPdfFileId error", e);
        }
        log.info("getContractPdfFileId contractNo:{}, contractPdfFileId:{}", contractNo, contractPdfFileId);
        return contractPdfFileId;
    }

    /**
     * 附件上传，保存fileRecord
     *
     * @param fileRespVo
     */
    private void saveFileRecord(FileRespVo fileRespVo, String fileName) throws ApplicationException {
        FileRecord fileRecord = new FileRecord();
        String fileId = fileRespVo.getFileId();
        if (StringUtils.isEmpty(fileId)) {
            return;
        }
        // 去除","
        if (fileId.indexOf(",") > 0) {
            fileId = fileId.split(",")[0];
        }
        fileRecord.setFileId(fileId);
        fileRecord.setFileName(fileName);
        fileRecordService.save(fileRecord);
    }
}
