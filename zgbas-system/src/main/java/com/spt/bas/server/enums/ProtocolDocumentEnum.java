package com.spt.bas.server.enums;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.hsoft.file.sdk.constant.FileConstant;
import com.hsoft.file.sdk.remote.FileRemote;
import com.hsoft.file.sdk.util.Base64Utility;
import com.hsoft.file.sdk.vo.FileRespVo;
import com.hsoft.file.sdk.vo.FileUploadBase64Request;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyProtocolDocument;
import com.spt.bas.client.entity.BsContractTemplate;
import com.spt.bas.client.vo.ApplyProtocolDocCkhDetailVo;
import com.spt.bas.client.vo.ApplyProtocolDocVo;
import com.spt.bas.client.vo.ApplyProtocolDocZnjDetailVo;
import com.spt.bas.client.vo.protocol.*;
import com.spt.bas.server.service.IBsContractTemplateService;
import com.spt.bas.server.util.HtmlToPdf;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.core.util.SpringContextHolder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 协议文件枚举类
 *
 * @Author MoonLight
 * @Date 2024/5/24 11:11
 * @Version 1.0
 */
@Slf4j
@Getter
public enum ProtocolDocumentEnum {

    REMINDER_LETTER("催款函", "RL", "protocol_doc_ckh", "20", "5", ApplyProtocolDocVo.class) {
        @Override
        public String getSignKeyWord(ApplyProtocolDocument entity) {
            return entity.getSignCompanyName() + "（盖章）";
        }
    },
    LETTER_NOTIFICATION("逾期滞纳金告知函", "LN", "protocol_doc_znj", "20", "5", ApplyProtocolDocVo.class) {
        @Override
        public String getSignKeyWord(ApplyProtocolDocument entity) {
            return entity.getSignCompanyName() + "（盖章）";
        }
    },
    CANCEL_PROTOCOL("合同解除协议", "CP", "rescission_agreement", "40", "5", RescissionAgreement.class) {
        @Override
        public String getSignKeyWord(ApplyProtocolDocument entity) {
            return entity.getSignCompanyName() + "（盖章）";
        }
    },
    REPAYMENT_AGREEMENT("还款协议", "RA", "repayment_agreement", "40", "5", RepaymentAgreement.class) {
        @Override
        public String getSignKeyWord(ApplyProtocolDocument entity) {
            return "（签字或盖章）：" + entity.getSignCompanyName();
        }
    },
    SUPPLEMENTARY_PROTOCOL("合同补充协议", "SP", "supplementary_agreement", "40", "5", SupplementaryAgreement.class) {
        @Override
        public String getSignKeyWord(ApplyProtocolDocument entity) {
            return "（盖章）："+ entity.getSignCompanyName();
        }
    },
    SUPPLEMENTARY_PROTOCOL2("合同补充协议", "SP2", "supplementary_agreement2", "40", "5", SupplementaryAgreement.class) {
        @Override
        public String getSignKeyWord(ApplyProtocolDocument entity) {
            return "（盖章）："+ entity.getSignCompanyName();
        }
    },
    REMINDER_PAYMENT("付款提示函", "RP", "reminder_pay_agreement", "20", "5", ApplyProtocolDocVo.class) {
        @Override
        public String getSignKeyWord(ApplyProtocolDocument entity) {
            return entity.getSignCompanyName() + "（盖章）";
        }
    },
    DZD_AGREEMENT("对账单", "DZ", "dzd_agreement", "40", "5", DzdAgreement.class) {
        @Override
        public String getSignKeyWord(ApplyProtocolDocument entity) {
            return entity.getSignCompanyName() + "（盖章）";
        }
    };

    /**
     * 协议文件名称
     */
    private final String docName;

    /**
     * 协议文件类型
     */
    private final String docType;

    /**
     * 协议文件模板
     */
    private final String docTemplate;

    /**
     * 签署坐标X轴偏移量
     */
    private final String offsetX;

    /**
     * 签署坐标Y轴偏移量
     */
    private final String offsetY;

    /**
     * 协议文件实体Vo
     */
    private final Class<?> analyzeClass;

    /**
     * 签署关键字
     *
     * @param entity
     * @return
     */
    public abstract String getSignKeyWord(ApplyProtocolDocument entity);

    /**
     * 模板参数合同，生成PDF文件流
     *
     * @param entity
     * @return
     * @throws ApplicationException
     */
    public static ByteArrayOutputStream generatePdfStream(ApplyProtocolDocument entity) throws ApplicationException {
        ProtocolDocumentEnum documentEnum = getEnumByDocType(entity.getDocType());
        if (Objects.isNull(documentEnum)) {
            throw new ApplicationException("can't recognize docType：【" + entity.getDocType() + "】");
        }
        Object targetParam = getTargetObject(documentEnum, entity);
        if (Objects.isNull(targetParam)) {
            throw new ApplicationException("can't convert docContent");
        }
        IBsContractTemplateService templateService = SpringContextHolder.getBean(IBsContractTemplateService.class);
        BsContractTemplate template = templateService.findByTemplateTagAndEnterpriseId(new BsContractTemplate(entity.getEnterpriseId(), documentEnum.getDocTemplate()));
        if (Objects.isNull(template) || StringUtils.isBlank(template.getContent())) {
            throw new ApplicationException("can't find docTemplate，docTemplate：【" + documentEnum.getDocTemplate() + "】");
        }
        String htmlContent = template.getContent();
        targetParam = handelParam(entity.getDocType(), targetParam, entity);
        return HtmlToPdf.createPdfByHtml(htmlContent, targetParam);
    }

    public static Object handelParam(String docType, Object targetParam, ApplyProtocolDocument entity) {
        if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_RL, docType)) {
            ApplyProtocolDocVo ckhVo = (ApplyProtocolDocVo) targetParam;
            ckhVo.setSignDate(DateUtil.format(new Date(), "yyyy-MM-dd"));
            List<ApplyProtocolDocCkhDetailVo> detailList = JSONArray.parseArray(ckhVo.getDetailList(), ApplyProtocolDocCkhDetailVo.class);
            BigDecimal unPayOverdueAmount = BigDecimal.ZERO;
            BigDecimal breachAmount = BigDecimal.ZERO;
            if (CollUtil.isNotEmpty(detailList)) {
                for (ApplyProtocolDocCkhDetailVo detail : detailList) {
                    String payFullDate = detail.getPayFullDate();
                    if (StringUtils.isNotBlank(payFullDate)) {
                        Date dateTime = DateUtil.parseDate(payFullDate);
                        detail.setPayFullDate(DateUtil.format(dateTime, "yyyy年MM月dd日"));
                    }
                    unPayOverdueAmount = unPayOverdueAmount.add(detail.getUnPayOverdueAmount());
                    breachAmount = breachAmount.add(detail.getBreachAmount());
                }
            }
            ckhVo.setUnPayOverdueAmount(unPayOverdueAmount);
            ckhVo.setBreachAmount(breachAmount);
            ckhVo.setCkhDetailList(detailList);
            return ckhVo;
        } else if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_LN, docType)) {
            ApplyProtocolDocVo ckhVo = (ApplyProtocolDocVo) targetParam;
            ckhVo.setSignDate(DateUtil.format(new Date(), "yyyy年MM月dd日"));
            String endDate = ckhVo.getEndDate();
            if (StringUtils.isNotBlank(endDate)) {
                ckhVo.setEndDate(DateUtil.format(DateUtil.parseDate(endDate), "yyyy年MM月dd日"));
            }
            List<ApplyProtocolDocZnjDetailVo> detailList = JSONArray.parseArray(ckhVo.getDetailList(), ApplyProtocolDocZnjDetailVo.class);
            ckhVo.setZnjDetailList(detailList);
            return ckhVo;
        }  else if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_RP, docType)) {
            ApplyProtocolDocVo ckhVo = (ApplyProtocolDocVo) targetParam;
            ckhVo.setSignDate(DateUtil.format(new Date(), "yyyy年MM月dd日"));
            List<ReminderPaymentAgreement> detailList = JSONArray.parseArray(ckhVo.getDetailList(), ReminderPaymentAgreement.class);
            if(!CollectionUtils.isEmpty(detailList)){
                BigDecimal totalAmountPayable = BigDecimal.ZERO;
                for (ReminderPaymentAgreement reminderPaymentAgreement : detailList) {
                    totalAmountPayable=totalAmountPayable.add(reminderPaymentAgreement.getTotalAmount());
                }
                // 应收款合计
                ckhVo.setTotalAmountPayable(totalAmountPayable);
            }
            ckhVo.setRePaymentList(detailList);
            return ckhVo;
        } else if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_DZ, docType)) {
            DzdAgreement dzdAgreement = (DzdAgreement) targetParam;
            List<DzdAgreement.DzdAgreementDetail> detailList = JSONArray.parseArray(dzdAgreement.getDzdDetailListStr(), DzdAgreement.DzdAgreementDetail.class);
            dzdAgreement.setDzdDetailList(detailList);
            dzdAgreement.setSignCompanyName(entity.getSignCompanyName());
            String dzdCompanyContact = dzdAgreement.getDzdCompanyContact();
            if (StringUtils.isBlank(dzdCompanyContact)){
                dzdAgreement.setDzdCompanyContact("-");
            }

            return dzdAgreement;
        }
        return targetParam;
    }

    ProtocolDocumentEnum(String docName, String docType, String docTemplate, String offsetX, String offsetY, Class<?> analyzeClass) {
        this.docName = docName;
        this.docType = docType;
        this.docTemplate = docTemplate;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.analyzeClass = analyzeClass;
    }

    public static ProtocolDocumentEnum getEnumByDocType(String docType) {
        for (ProtocolDocumentEnum target : ProtocolDocumentEnum.values()) {
            if (target.docType.equals(docType)) {
                return target;
            }
        }
        return null;
    }

    private static Object getTargetObject(ProtocolDocumentEnum documentEnum, ApplyProtocolDocument entity) {
        Object targetParam = null;
        try {
            if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_RA, entity.getDocType())) {
                return JSONUtil.toBean(entity.getContent(), documentEnum.getAnalyzeClass());
            }
            if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_SP, entity.getDocType())) {
                SupplementaryAgreement agreement = (SupplementaryAgreement) JsonUtil.json2Object(documentEnum.getAnalyzeClass(), entity.getContent());
                String targetCompanyName = agreement.getTargetCompanyName();
                String ourCompanyName = agreement.getOurCompanyName();
                
                // 只有奥顺宇链条的合同才增加退需方金额条款
                if (StringUtils.equals(BasConstants.COMPANY_NAME_ASY, targetCompanyName) || StringUtils.equals(BasConstants.COMPANY_NAME_ASY, ourCompanyName)) {
                    BigDecimal totalAmount = agreement.getTotalAmount();
                    BigDecimal alterTotalAmount = agreement.getAlterTotalAmount();
                    if (totalAmount != null && alterTotalAmount != null) {
                        if (alterTotalAmount.compareTo(totalAmount) > 0) {
                            BigDecimal subtract = alterTotalAmount.subtract(totalAmount);
                            agreement.setExtraTermBk(SubjectUtil.formatMoney(subtract, RuleUtil.monetaryUnit));
                        } else if (alterTotalAmount.compareTo(totalAmount) < 0) {
                            BigDecimal subtract = totalAmount.subtract(alterTotalAmount);
                            agreement.setExtraTermTk(SubjectUtil.formatMoney(subtract, RuleUtil.monetaryUnit));
                        }
                    }
                }


                return agreement;
            }
            targetParam = JsonUtil.json2Object(documentEnum.getAnalyzeClass(), entity.getContent());
        } catch (Exception e) {
            log.error("getTargetObject error", e);
        }
        return targetParam;
    }

    /**
     * 上传Base64 协议文件至附件服务
     *
     * @param outputStream
     * @param documentEnum
     * @return
     * @throws ApplicationException
     */
    public static String fileUploadBase64(ByteArrayOutputStream outputStream, ProtocolDocumentEnum documentEnum) throws ApplicationException {
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
        FileRemote fileRemote = SpringContextHolder.getBean(FileRemote.class);
        FileRespVo fileRespVo = fileRemote.uploadBase64(fileRequest);
        if (Objects.isNull(fileRespVo) || StringUtils.isBlank(fileRespVo.getFileId())) {
            throw new ApplicationException("协议文件生成失败！");
        }
        return fileRespVo.getFileId();
    }
}
