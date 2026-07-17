package com.spt.bas.server.filter.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.google.common.base.Splitter;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.BsDictConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IPmApproveContentsClient;
import com.spt.bas.client.vo.SealUsageDcsxVo;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.CtrProductDao;
import com.spt.bas.server.dao.sign.SignFileDao;
import com.spt.bas.server.filter.IAutoSealPdfSignFilter;
import com.spt.bas.server.service.*;
import com.spt.bas.server.service.impl.CtrContractPdfService;
import com.spt.bas.server.service.impl.SignFileServiceImpl;
import com.spt.bas.server.util.FLKPDFUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.service.IPmApproveContentsService;
import com.spt.sign.client.cfca.CfcaResp;
import com.spt.sign.client.entity.EnterpriseAccount;
import com.spt.sign.client.entity.SignTransactor;
import com.spt.sign.client.remote.ICfcaSignClient;
import com.spt.sign.client.remote.IEnterpriseAccountClient;
import com.spt.sign.client.remote.ISignTransactorClient;
import com.spt.sign.client.vo.AxqAutoSignVo;
import com.spt.sign.client.vo.AxqContractVo;
import com.spt.sign.client.vo.AxqUploadVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.annotation.ServiceTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 自动生成PDF文件签署
 *
 * @Author MoonLight
 * @Date 2023/3/30 15:46
 * @Version 1.0
 */
@Slf4j
@Component
public class AutoSealPdfSignFilterImpl implements IAutoSealPdfSignFilter {
    private static final String SEAL_DEFAULT_PREFIX = "公司名称：";
    private static final String purchaseOrderTemplatePath = "templates/PurchaseOrder.pdf";
    private static final String purchaseBuyOrderTemplatePath = "templates/PurchaseBuyOrder.pdf";
    @Resource
    private CtrContractPdfService ctrContractPdfService;
    @Resource
    private IBsCompanyDcsxService bsCompanyDcsxService;
    @Resource
    private SignFileDao signFileDao;
    @Resource
    private ISignUserFileService signUserFileService;
    @Resource
    private IEnterpriseAccountClient enterpriseAccountClient;
    @Resource
    private ISignTransactorClient signTransactorClient;
    @Resource
    private ICfcaSignClient cfcaSignClient;
    @Resource
    private IPmApproveContentsClient appApproveContentsClient;
    @Resource
    private IPmApproveContentsService pmApproveContentsService;
    @Resource
    private ICtrContractService ctrContractService;
    @Resource
    private IApplyDcsxService applyDcsxService;
    @Autowired
    private SignFileServiceImpl signFileServiceImpl;
    @Resource
    private IFileRecordService fileRecordService;
    @Autowired
    private CtrContractDao ctrContractDao;
    @Resource
    private CtrProductDao ctrProductDao;
    @Resource
    private IBizSignService bizSignService;

    /**
     * 生成PDF下游合同附件文件签署
     *
     * @param approve
     * @param contract
     */
    @Override
    @ServiceTransactional
    public void generateSealPDFSign(PmApprove approve, CtrContract contract) {
        // 1. 下游已上传了其它合同则不需要文件签署
        if (Objects.isNull(contract)) {
            log.error("generateSealPDFSign error, contract is null!");
            return;
        }
        String contractType = contract.getContractType();
        String ourCompanyName = contract.getOurCompanyName();
        String buyContentFileId = contract.getBuyContentFileId();
        String sellContentFileId = contract.getSellContentFileId();
        Long virtualContractId = contract.getVirtualContractId();
        // 青岛中光采购合同
        if (StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contractType)
                && StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, ourCompanyName)
                && Objects.isNull(virtualContractId)
                && verifyPDF(buyContentFileId)) {
            saveBusinessBuySignFile(approve, contract, buyContentFileId);
            return;
        }
        if (StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contractType)) {
            log.error("generateSealPDFSign error, contractType is 'B' and virtualContractId is null!");
            return;
        }
        if (StringUtils.isNotBlank(sellContentFileId)) {
            log.error("generateSealPDFSign 已上传合同无需文件签署 sellContractFileId:{}", sellContentFileId);
            return;
        }
        // 2024年2月27日风控部提出需求变动：无须判断客户是否线上化，直接生成文件签署即可
//        BsCompany bsCompany = bsCompanyService.getEntity(contract.getCompanyId());
//        // 2. 已线上化的客户不需要生成文件签署
//        if (Boolean.TRUE.equals(bsCompany.getOnLineFlg())) {
//            log.error("generateSealPDFSign 已线上化的客户:{}", contract.getCompanyName());
//            return;
//        }
        // 3. 下游我方-浙江网塑 不需要生成文件签署
        if (StringUtils.equals(BasConstants.ZJWS_COMPANY_NAME, ourCompanyName)) {
            log.error("generateSealPDFSign 我方-浙江网塑 不需要生成文件签署");
            return;
        }
        // 4. 判断我方是否具备电子签资格
        Boolean verifySignCompany = bsCompanyDcsxService.verifySignCompany(ourCompanyName);
        if (Boolean.FALSE.equals(verifySignCompany)) {
            log.error("generateSealPDFSign 我方不具备电子签资格:{}", ourCompanyName);
            return;
        }
        // 5. 生成需要签署的合同PDF附件ID
        Boolean virtualFlg = Objects.nonNull(contract.getVirtualContractId());
        CtrContract targetContract = virtualFlg ? ctrContractService.getEntity(contract.getVirtualContractId()) : null;
        String contractPdfFileId = ctrContractPdfService.generateContractPdf(contract, targetContract);
        if (StringUtils.isBlank(contractPdfFileId)) {
            log.error("generateSealPDFSign generateContractPdf error,contractNo:{}", contract.getContractNo());
            return;
        }
        List<String> signCompanyNameList = new ArrayList<>();
        if (virtualFlg && Objects.nonNull(targetContract)) {
            signCompanyNameList.add(targetContract.getCompanyName());
            signCompanyNameList.add(targetContract.getOurCompanyName());
        } else {
            signCompanyNameList.add(contract.getOurCompanyName());
        }
        BsCompanyDcsx dcsxConfig = bsCompanyDcsxService.findByCompanyName(contract.getOurCompanyName());
        if (Boolean.TRUE.equals(dcsxConfig.getAutoSignFlg()) || virtualFlg) {
            // 5.1. 上传原文合同至安心签
            List<AxqContractVo> axqContractVoList = new ArrayList<>();
            for (String name : signCompanyNameList) {
                axqContractVoList.add(convertSignFileDcsxV2(name, contractPdfFileId, contract.getContractNo(), virtualFlg));
            }
            AxqUploadVo axqUploadVo = cfcaSignClient.axqUploadContractSigned(axqContractVoList);
            if (Objects.nonNull(axqUploadVo) && StringUtils.equals("60000000", axqUploadVo.getRetCode())) {
                String cfcaContractNo = axqUploadVo.getContractNo();
                log.info("安心签上传合同签署创建成功，合同编号:{}", cfcaContractNo);
                if (StringUtils.isNotBlank(cfcaContractNo)) {
                    PmApproveContents contents = appApproveContentsClient.findByApproveId(approve.getId());
                    if (Objects.nonNull(contents)) {
                        contents.setCfcaContractNo(cfcaContractNo);
                        contents.setFileId(contractPdfFileId + BasConstants.COMMA);
                        appApproveContentsClient.save(contents);
                    }
                }
                return;
            } else {
                log.error("安心签上传合同签署创建失败 result:{}", axqUploadVo);
            }
        }
        // 6. 保存文件签署
        saveSignFile(approve, contractPdfFileId, contract.getContractNo(), contract.getOwningRegion(), signCompanyNameList);
    }

    /**
     * 库存业务生成PDF文件合同
     *
     * @param approve
     * @param contract
     */
    @Override
    @ServiceTransactional
    public void generateVirtualSealPDFSign(PmApprove approve, CtrContract contract) {
        if (Objects.isNull(contract)) {
            log.error("generateVirtualSealPDFSign error, contract is null!");
            return;
        }
        if (!StringUtils.equals(BasConstants.STOCK_VIRTUAL_KC, contract.getVirtualType())) {
            return;
        }
        String contractNo = contract.getContractNo();
        String prefixVirtual = contractNo.replaceAll("\\d", "");
        if (!StringUtils.equals("KUX", prefixVirtual)) {
            return;
        }
        String contractPdfFileId = ctrContractPdfService.generateContractPdf(contract, null);
        if (StringUtils.isBlank(contractPdfFileId)) {
            log.error("generateVirtualSealPDFSign error, contractNo:{}", contractNo);
            return;
        }
        String companyName = contract.getCompanyName();
        String ourCompanyName = contract.getOurCompanyName();

        Boolean verifySign = bsCompanyDcsxService.verifySignCompany(companyName);
        Boolean verifyOurSign = bsCompanyDcsxService.verifySignCompany(ourCompanyName);

        List<AxqContractVo> axqContractVoList = new ArrayList<>();
        if (verifySign) {
            axqContractVoList.add(convertSignFileWithPrefix(companyName, contractPdfFileId, contract.getContractNo(), SEAL_DEFAULT_PREFIX));
        }
        if (verifyOurSign) {
            axqContractVoList.add(convertSignFileWithPrefix(ourCompanyName, contractPdfFileId, contract.getContractNo(), SEAL_DEFAULT_PREFIX));
        }
        AxqUploadVo axqUploadVo = cfcaSignClient.axqUploadContractSigned(axqContractVoList);
        if (Objects.nonNull(axqUploadVo) && StringUtils.equals("60000000", axqUploadVo.getRetCode())) {
            String cfcaContractNo = axqUploadVo.getContractNo();
            log.info("generateVirtualSealPDFSign 安心签上传合同签署创建成功，合同编号:{}", cfcaContractNo);
            if (StringUtils.isNotBlank(cfcaContractNo)) {
                PmApproveContents contents = appApproveContentsClient.findByApproveId(approve.getId());
                if (Objects.nonNull(contents)) {
                    log.info("generateVirtualSealPDFSign pmApproveContents id is :{}", contents.getId());
                    contents.setCfcaContractNo(cfcaContractNo);
                    contents.setFileId(contractPdfFileId + BasConstants.COMMA);
                    appApproveContentsClient.save(contents);
                } else {
                    log.info("generateVirtualSealPDFSign pmApproveContents error, can't find contents");
                }
            }
        } else {
            log.error("安心签上传合同签署创建失败 result:{}", axqUploadVo);
        }
    }

    /**
     * 生成范伦克采购合同
     *
     * @param approve
     * @param contract
     */
    @Override
    @ServiceTransactional
    public void generateFLKSealPDFSign(PmApprove approve, CtrContract contract) {
        if (!verifyFLK(approve, contract)){
            return;
        }
        String contractPdfFileId = ctrContractPdfService.generateContractPdf(contract.getId());
        log.info("generateFLKSealPDFSign contractPdfFileId:{}", contractPdfFileId);
        if (StringUtils.isBlank(contractPdfFileId)) {
            log.error("generateFLKSealPDFSign generateContractPdf error,contractNo:{}", contract.getContractNo());
            return;
        }
        // 上传原文合同至安心签
        List<AxqContractVo> axqContractVoList = new ArrayList<>();
        axqContractVoList.add(convertSignFileWithPrefix(contract.getCompanyName(), contractPdfFileId, contract.getContractNo(), "公司名称："));
        axqContractVoList.add(convertSignFileWithPrefix(contract.getOurCompanyName(), contractPdfFileId, contract.getContractNo(), "公司名称："));
        AxqUploadVo axqUploadVo = cfcaSignClient.axqUploadContractSigned(axqContractVoList);
        if (Objects.nonNull(axqUploadVo) && StringUtils.equals("60000000", axqUploadVo.getRetCode())) {
            String cfcaContractNo = axqUploadVo.getContractNo();
            log.info("generateFLKSealPDFSign 安心签上传补充协议签署创建成功，合同编号:{}", cfcaContractNo);
            if (StringUtils.isNotBlank(cfcaContractNo)) {
                PmApproveContents contents = appApproveContentsClient.findByApproveId(approve.getId());
                if (Objects.nonNull(contents)) {
                    contents.setCfcaContractNo(cfcaContractNo);
                    contents.setFileId(contractPdfFileId + BasConstants.COMMA);
                    appApproveContentsClient.save(contents);
                }
            }
        } else {
            log.error("generateFLKSealPDFSign 安心签上传补充协议签署创建失败 result:{}", axqUploadVo);
        }
    }

    /**
     * 生成范伦克化工产品采购委托书、化工产品采购订单
     *
     * @param approve
     * @param contract
     */
    @Override
    @ServiceTransactional
    public void generateFLKPurchaseOrder(PmApprove approve, CtrContract contract) {
        if (!verifyFLK(approve, contract)){
            return;
        }
        if (!StringUtils.equals(BasConstants.COMPANY_NAME_FLK, contract.getCompanyName())) {
            log.info("generateFLKPurchaseOrder stop companyName:{}", contract.getCompanyName());
            return;
        }
        try {
            Map<String, String> paramMap = this.buildFLKPurchaseMap(contract);
            String purchaseOrderFileId = FLKPDFUtil.generateFLKFileId(paramMap, purchaseOrderTemplatePath, "化工产品采购委托书");
            this.savePurchaseOrderBizSign(approve, contract, purchaseOrderFileId);

            String purchaseBuyOrderFileId = FLKPDFUtil.generateFLKFileId(paramMap, purchaseBuyOrderTemplatePath, "化工产品采购订单");
            this.savePurchaseBuyOrderBizSign(approve, contract, purchaseBuyOrderFileId);
        } catch (Exception e) {
            log.error("generateFLKPurchaseOrder error", e);
        }
    }

    private void savePurchaseOrderBizSign(PmApprove approve, CtrContract contract, String purchaseOrderFileId) throws ApplicationException {
        log.info("savePurchaseOrderBizSign contractNo:{}, purchaseOrderFileId:{}", contract.getContractNo(), purchaseOrderFileId);
        BizSign bizSign = new BizSign();
        bizSign.setApproveId(approve.getId());
        bizSign.setContractId(contract.getId());
        bizSign.setSignFileId(purchaseOrderFileId);
        bizSign.setSignFileName("化工产品采购委托书");
        bizSign.setSignStatus("N");

        List<BizSignDetail> bizSignDetailList = new ArrayList<>();
        BizSignDetail bizSignDetail = new BizSignDetail();
        bizSignDetail.setSignCompanyName(contract.getOurCompanyName());
        bizSignDetail.setSignKeyWord("委托人：");
        bizSignDetail.setSignOffsetCoordX("100");
        bizSignDetailList.add(bizSignDetail);
        bizSign.setBizSignDetailList(bizSignDetailList);

        bizSignService.generateSign(bizSign);
    }

    private void savePurchaseBuyOrderBizSign(PmApprove approve, CtrContract contract, String purchaseBuyOrderFileId) throws ApplicationException {
        log.info("savePurchaseBuyOrderBizSign contractNo:{}, purchaseBuyOrderFileId:{}", contract.getContractNo(), purchaseBuyOrderFileId);
        BizSign bizSign = new BizSign();
        bizSign.setApproveId(approve.getId());
        bizSign.setContractId(contract.getId());
        bizSign.setSignFileId(purchaseBuyOrderFileId);
        bizSign.setSignFileName("化工产品采购订单");
        bizSign.setSignStatus("N");

        List<BizSignDetail> bizSignDetailList = new ArrayList<>();
        BizSignDetail bizSignDetail = new BizSignDetail();
        bizSignDetail.setSignCompanyName(contract.getCompanyName());
        bizSignDetail.setSignKeyWord(contract.getCompanyName() + "（章）");
        bizSignDetail.setSignOffsetCoordY("-10");
        bizSignDetailList.add(bizSignDetail);

        BizSignDetail bizSignDetail2 = new BizSignDetail();
        bizSignDetail2.setSignCompanyName(contract.getOurCompanyName());
        bizSignDetail2.setSignKeyWord(contract.getOurCompanyName() + "（章）");
        bizSignDetail2.setSignOffsetCoordY("-10");
        bizSignDetailList.add(bizSignDetail2);

        bizSign.setBizSignDetailList(bizSignDetailList);

        bizSignService.generateSign(bizSign);
    }

    private Map<String, String> buildFLKPurchaseMap(CtrContract contract) {
        Map<String, String> paramMap = new HashMap<>();
        CtrProduct ctrProduct = ctrProductDao.findOneByCtrContractId(contract.getId());
        CtrContract targetContract = ctrContractDao.findByApproveIdAndContractType(contract.getApproveId(), BasConstants.CONTRACT_TYPE_B);
        paramMap.put("contractNo", contract.getContractNo());
        paramMap.put("productName", ctrProduct.getProductName());
        paramMap.put("companyName", targetContract.getCompanyName());
        paramMap.put("brandNumber", ctrProduct.getBrandNumber());
        paramMap.put("factoryName", ctrProduct.getFactoryName());
        paramMap.put("targetCompanyName", contract.getOurCompanyName());
        paramMap.put("wrapSpecs", DictUtil.getValue(BasConstants.DICT_TYPE_PACKINGSPECIFICATEXT, ctrProduct.getWrapSpecs()));
        paramMap.put("dealNumber", NumberUtil.decimalFormat("#,##0.0000", contract.getTotalNumber()));
        paramMap.put("dealPrice", NumberUtil.decimalFormat("#,##0.00", contract.getDealPrice()));
        paramMap.put("totalAmount", NumberUtil.decimalFormat("#,##0.00", contract.getTotalAmount()));
        paramMap.put("totalAmountStr", String.format("【%s】元", NumberUtil.decimalFormat("#,##0.00", contract.getTotalAmount())));
        paramMap.put("orderDate", DateUtil.format(new Date(), DatePattern.CHINESE_DATE_FORMAT));
        return paramMap;
    }

    /**
     * 生成PDF补充协议附件文件签署
     *
     * @param approve
     * @param contract
     */
    @Override
    @ServiceTransactional
    public void generateProtocolSealPDFSign(PmApprove approve, CtrContract contract, CtrContract buyContract) {
        // 1. 下游已上传了其它合同则不需要文件签署
        if (Objects.isNull(contract)) {
            return;
        }
        // 4. 判断我方是否具备电子签资格
        Boolean verifySignCompany = bsCompanyDcsxService.verifySignCompany(contract.getOurCompanyName());
        if (Boolean.FALSE.equals(verifySignCompany)) {
            log.error("generateProtocolSealPDFSign 我方不具备电子签资格:{}", contract.getOurCompanyName());
            return;
        }
        // 5. 生成需要签署的补充协议PDF附件ID
        String contractPdfFileId = ctrContractPdfService.generateProtocolPdf(contract, buyContract);
        if (StringUtils.isBlank(contractPdfFileId)) {
            log.error("generateProtocolSealPDFSign generateContractPdf error,contractNo:{}", contract.getContractNo());
            return;
        }
        List<String> signCompanyNameList = new ArrayList<>();
        signCompanyNameList.add(contract.getCompanyName());
        signCompanyNameList.add(contract.getOurCompanyName());
        BsCompanyDcsx dcsxConfig = bsCompanyDcsxService.findByCompanyName(contract.getOurCompanyName());
        if (Boolean.TRUE.equals(dcsxConfig.getAutoSignFlg())) {
            // 5.1. 上传原文合同至安心签
            List<AxqContractVo> axqContractVoList = new ArrayList<>();
            for (String name : signCompanyNameList) {
                axqContractVoList.add(convertSignFileDcsxV2(name, contractPdfFileId, contract.getContractNo(), true));
            }
            AxqUploadVo axqUploadVo = cfcaSignClient.axqUploadContractSigned(axqContractVoList);
            if (Objects.nonNull(axqUploadVo) && StringUtils.equals("60000000", axqUploadVo.getRetCode())) {
                String cfcaContractNo = axqUploadVo.getContractNo();
                log.info("安心签上传补充协议签署创建成功，合同编号:{}", cfcaContractNo);
                if (StringUtils.isNotBlank(cfcaContractNo)) {
                    contract.setCfcaProtocolFileNo(cfcaContractNo);
                    contract.setProtocolFileId(contractPdfFileId + BasConstants.COMMA);
                }
                return;
            } else {
                log.error("安心签上传补充协议签署创建失败 result:{}", axqUploadVo);
            }
        }
        // 6. 保存文件签署
        saveSignFile(approve, contractPdfFileId, contract.getContractNo(), contract.getOwningRegion(), signCompanyNameList);
    }

    /**
     * 生成PDF中游合同附件文件签署
     *
     * @param approve
     * @param entity
     */
    @Override
    @ServiceTransactional
    public void generateSealPDFSignDCSX(PmApprove approve, ApplyCtrDCSX entity) {
        String companyName = entity.getCompanyName();
        String ourCompanyName = entity.getOurCompanyName();
        // 1. 判断双方是否具备电子签资格
        Boolean ourCompanyFlg = bsCompanyDcsxService.verifySignCompany(ourCompanyName);
        Boolean companyFlg = bsCompanyDcsxService.verifySignCompany(companyName);
        List<String> signCompanyNameList = new ArrayList<>();
        if (Boolean.TRUE.equals(ourCompanyFlg)) {
            signCompanyNameList.add(ourCompanyName);
        }
        if (Boolean.TRUE.equals(companyFlg)) {
            signCompanyNameList.add(companyName);
        }
        if (CollectionUtils.isEmpty(signCompanyNameList)) {
            log.error("generateSealPDFSignDCSX 双方都不具备电子签");
            return;
        }
        // 2. 生成需要签署的合同PDF附件ID
        String contractPdfFileId = ctrContractPdfService.generateContractPdfDcsx(entity);
        // 3. 保存文件签署
        saveSignFile(approve, contractPdfFileId, entity.getContractNo(), "", signCompanyNameList);
    }

    /**
     * 生成PDF中游补充协议附件文件签署
     *
     * @param approve
     * @param entity
     */
    @Override
    @ServiceTransactional
    public void generateProtocolSealPDFSignDCSX(PmApprove approve, ApplyCtrDCSX entity) {
        String companyName = entity.getCompanyName();
        String ourCompanyName = entity.getOurCompanyName();
        // 1. 判断双方是否具备电子签资格
        Boolean ourCompanyFlg = bsCompanyDcsxService.verifySignCompany(ourCompanyName);
        Boolean companyFlg = bsCompanyDcsxService.verifySignCompany(companyName);
        List<String> signCompanyNameList = new ArrayList<>();
        if (Boolean.TRUE.equals(ourCompanyFlg)) {
            signCompanyNameList.add(ourCompanyName);
        }
        if (Boolean.TRUE.equals(companyFlg)) {
            signCompanyNameList.add(companyName);
        }
        if (CollectionUtils.isEmpty(signCompanyNameList)) {
            log.error("生成PDF中游补充协议附件文件签署 双方都不具备电子签");
            return;
        }
        // 2. 生成需要签署的补充协议PDF附件ID
        String contractPdfFileId = ctrContractPdfService.generateDcsxProtocolPdf(entity);
        if (StringUtils.isBlank(contractPdfFileId)) {
            log.error("generateProtocolSealPDFSign generateContractPdf error,contractNo:{}", entity.getContractNo());
            return;
        }
        // 5.1. 上传原文合同至安心签
        List<AxqContractVo> axqContractVoList = new ArrayList<>();
        for (String name : signCompanyNameList) {
            axqContractVoList.add(convertSignFileDcsxV2(name, contractPdfFileId, entity.getContractNo(), true));
        }
        AxqUploadVo axqUploadVo = cfcaSignClient.axqUploadContractSigned(axqContractVoList);
        if (Objects.nonNull(axqUploadVo) && StringUtils.equals("60000000", axqUploadVo.getRetCode())) {
            String cfcaContractNo = axqUploadVo.getContractNo();
            log.info("安心签上传补充协议签署创建成功，合同编号:{}", cfcaContractNo);
            if (StringUtils.isNotBlank(cfcaContractNo)) {
                entity.setCfcaProtocolFileNo(cfcaContractNo);
                entity.setProtocolFileId(contractPdfFileId + BasConstants.COMMA);
            }
            return;
        } else {
            log.error("安心签上传补充协议签署创建失败 result:{}", axqUploadVo);
        }
        // 3. 保存文件签署
        saveSignFile(approve, contractPdfFileId, entity.getContractNo(), "", signCompanyNameList);
    }

    /**
     * 生成PDF中游合同附件文件签署
     *
     * @param approve
     * @param entity
     */
    @Override
    @ServiceTransactional
    public void generateSealPDFSignDCSXV2(PmApprove approve, ApplyCtrDCSX entity) {
        if (Objects.isNull(entity)) {
            return;
        }
        String companyName = entity.getCompanyName();
        String ourCompanyName = entity.getOurCompanyName();
        // 1. 判断双方是否具备电子签资格
        Boolean ourCompanyFlg = bsCompanyDcsxService.verifySignCompany(ourCompanyName);
        Boolean companyFlg = bsCompanyDcsxService.verifySignCompany(companyName);
        List<String> signCompanyNameList = new ArrayList<>();
        if (Boolean.TRUE.equals(ourCompanyFlg)) {
            signCompanyNameList.add(ourCompanyName);
        }
        if (Boolean.TRUE.equals(companyFlg)) {
            signCompanyNameList.add(companyName);
        }
        if (CollectionUtils.isEmpty(signCompanyNameList)) {
            log.error("generateSealPDFSignDCSX 双方都不具备电子签");
            return;
        }
        // 2. 生成需要签署的合同PDF附件ID
        String contractPdfFileId = ctrContractPdfService.generateContractPdfDcsx(entity);
        log.info("审批编号:{}, 自动生成签署合同附件ID:{}", approve.getApproveNo(), contractPdfFileId);
        // 3. 上传原文合同至安心签
        List<AxqContractVo> axqContractVoList = new ArrayList<>();
        for (String name : signCompanyNameList) {
            AxqContractVo axqContractVo = convertSignFileDcsxV2(name, contractPdfFileId, entity.getContractNo(), false);
            axqContractVoList.add(axqContractVo);
        }
        AxqUploadVo axqUploadVo = cfcaSignClient.axqUploadContractSigned(axqContractVoList);
        if (Objects.nonNull(axqUploadVo) && StringUtils.equals("60000000", axqUploadVo.getRetCode())) {
            String cfcaContractNo = axqUploadVo.getContractNo();
            log.info("安心签上传合同签署创建成功，合同编号:{}", cfcaContractNo);
            if (StringUtils.isNotBlank(cfcaContractNo)) {
                PmApproveContents contents = appApproveContentsClient.findByApproveId(approve.getId());
                if (Objects.nonNull(contents)) {
                    contents.setCfcaContractNo(cfcaContractNo);
                    contents.setFileId(contractPdfFileId + BasConstants.COMMA);
                    appApproveContentsClient.save(contents);
                }
            }
        } else {
            log.error("安心签上传合同签署创建失败 result:{}", axqUploadVo);
        }
    }

    /**
     * 更新中游合同文件签署附件
     *
     * @param entity
     */
    @Override
    @ServiceTransactional
    public void updateCfcaContractNo(SealUsageDcsxVo entity) {
        String contractNo = entity.getContractNo();
        List<String> signCompanyNameList = new ArrayList<>();
        CtrContract contract = ctrContractService.findByContractNo(contractNo);
        String companyName = Objects.nonNull(contract) ? contract.getCompanyName() : entity.getCompanyName();
        String ourCompanyName = Objects.nonNull(contract) ? contract.getOurCompanyName() : entity.getOurCompanyName();
        // 1. 判断双方是否具备电子签资格
        Boolean ourCompanyFlg = bsCompanyDcsxService.verifySignCompany(ourCompanyName);
        if (Boolean.TRUE.equals(ourCompanyFlg)) {
            signCompanyNameList.add(ourCompanyName);
        }
        if (Objects.isNull(contract)) {
            Boolean companyFlg = bsCompanyDcsxService.verifySignCompany(companyName);
            if (Boolean.TRUE.equals(companyFlg)) {
                signCompanyNameList.add(companyName);
            }
            if (CollectionUtils.isEmpty(signCompanyNameList)) {
                log.error("generateSealPDFSignDCSX 双方都不具备电子签");
                return;
            }
        }
        // 2. 生成需要签署的合同PDF附件ID
        String contractPdfFileId = entity.getFileId();
        // 3. 上传原文合同至安心签
        List<AxqContractVo> axqContractVoList = new ArrayList<>();
        for (String name : signCompanyNameList) {
            AxqContractVo axqContractVo = convertSignFileDcsxV2(name, contractPdfFileId, entity);
            axqContractVoList.add(axqContractVo);
        }
        AxqUploadVo axqUploadVo = cfcaSignClient.axqUploadContractSigned(axqContractVoList);
        if (Objects.nonNull(axqUploadVo) && StringUtils.equals("60000000", axqUploadVo.getRetCode())) {
            String cfcaContractNo = axqUploadVo.getContractNo();
            log.info("安心签上传合同签署创建成功，合同编号:{}", cfcaContractNo);
            if (StringUtils.isNotBlank(cfcaContractNo)) {
                PmApproveContents contents = appApproveContentsClient.findByApproveId(entity.getApproveId());
                if (Objects.nonNull(contents)) {
                    contents.setCfcaContractNo(cfcaContractNo);
                    contents.setFileId(contractPdfFileId + BasConstants.COMMA);
                    appApproveContentsClient.save(contents);
                }
            }
        } else {
            log.error("安心签上传合同签署创建失败 result:{}", axqUploadVo);
        }
    }

    /**
     * 代采赊销盖章申请，审批完成执行自动签署逻辑
     *
     * @param cfcaContractNo
     * @return
     */
    @Override
    public String successSignContractByKeyword(String cfcaContractNo) {
        if (StringUtils.isBlank(cfcaContractNo)) {
            return null;
        }
        CfcaResp<String> cfcaResp = cfcaSignClient.successSignContractByKeyword(cfcaContractNo);
        if (Objects.nonNull(cfcaResp) && cfcaResp.isSuccess()) {
            String fileIdStr = cfcaResp.getData();
            if (StringUtils.isNotBlank(fileIdStr)) {
                return fileIdStr.endsWith(BasConstants.COMMA) ? fileIdStr : fileIdStr + BasConstants.COMMA;
            }
        }
        return null;
    }

    @Override
    public String successFLKSignContractByKeyword(String cfcaContractNo, String contractNo) {
        if (StringUtils.isBlank(cfcaContractNo)) {
            return "";
        }
        String fileIdStr = "";
        CtrContract contract = ctrContractService.findByContractNo(contractNo);
        AxqAutoSignVo axqAutoSignVo = new AxqAutoSignVo();
        axqAutoSignVo.setCfcaContractNo(cfcaContractNo);
        List<AxqAutoSignVo.Signatorie> signatorieList = new ArrayList<>();
        signatorieList.add(generateAxqAutoSignVoParam(contract.getOurCompanyName(), "公司名称：", "20"));
        signatorieList.add(generateAxqAutoSignVoParam(contract.getCompanyName(), "公司名称：", "20"));
        axqAutoSignVo.setSignatorieList(signatorieList);
        CfcaResp<String> cfcaResp = cfcaSignClient.autoSignContract(axqAutoSignVo);
        if (Objects.nonNull(cfcaResp) && cfcaResp.isSuccess()) {
            fileIdStr = cfcaResp.getData();
            if (StringUtils.isNotBlank(fileIdStr)) {
                fileIdStr = fileIdStr.endsWith(BasConstants.COMMA) ? fileIdStr : fileIdStr + BasConstants.COMMA;
            }
        }
        return fileIdStr;
    }

    @Override
    public String successSignContractByKeywordDCSX(String cfcaContractNo, Long dcsxId) {
        if (StringUtils.isBlank(cfcaContractNo)) {
            return "";
        }
        if (Objects.isNull(dcsxId)) {
            return successSignContractByKeyword(cfcaContractNo);
        }
        ApplyCtrDCSX entity = applyDcsxService.getEntity(dcsxId);
        if (Objects.isNull(entity)) {
            return successSignContractByKeyword(cfcaContractNo);
        }
        CtrContract sellContract = ctrContractDao.findByApproveIdAndContractType(entity.getApproveId(), BasConstants.CONTRACT_TYPE_S);
        String companyName = entity.getCompanyName();
        String ourCompanyName = entity.getOurCompanyName();
        if (Objects.nonNull(sellContract)
                && sellContract.getCompanyName().contains("远东")
                && (!companyName.contains("奥顺宇")) && !ourCompanyName.contains("奥顺宇")){
            String fileIdStr = "";
            AxqAutoSignVo axqAutoSignVo = new AxqAutoSignVo();
            axqAutoSignVo.setCfcaContractNo(cfcaContractNo);
            List<AxqAutoSignVo.Signatorie> signatorieList = new ArrayList<>();
            signatorieList.add(generateAxqAutoSignVoParam(entity.getOurCompanyName(), "需方：", "20"));
            signatorieList.add(generateAxqAutoSignVoParam(entity.getCompanyName(), "供方：", "20"));
            axqAutoSignVo.setSignatorieList(signatorieList);
            CfcaResp<String> cfcaResp = cfcaSignClient.autoSignContract(axqAutoSignVo);
            if (Objects.nonNull(cfcaResp) && cfcaResp.isSuccess()) {
                fileIdStr = cfcaResp.getData();
                if (StringUtils.isNotBlank(fileIdStr)) {
                    fileIdStr = fileIdStr.endsWith(BasConstants.COMMA) ? fileIdStr : fileIdStr + BasConstants.COMMA;
                }
            }
            return fileIdStr;
        }
        return successSignContractByKeyword(cfcaContractNo);
    }

    @Override
    public String successSignContractByKeyword(String cfcaContractNo, String contractNo) {
        if (StringUtils.isBlank(cfcaContractNo)) {
            return "";
        }
        CtrContract contract = ctrContractService.findByContractNo(contractNo);
        if (Objects.nonNull(contract) && Objects.nonNull(contract.getVirtualContractId())) {
            CtrContract targetContract = ctrContractService.getEntity(contract.getVirtualContractId());
            if (Objects.isNull(targetContract)) {
                return "";
            }
            AxqAutoSignVo axqAutoSignVo = convertVirtualSign(cfcaContractNo, targetContract);
            CfcaResp<String> cfcaResp = cfcaSignClient.autoSignContract(axqAutoSignVo);
            if (Objects.nonNull(cfcaResp) && cfcaResp.isSuccess()) {
                String fileIdStr = cfcaResp.getData();
                if (StringUtils.isNotBlank(fileIdStr)) {
                    return fileIdStr.endsWith(BasConstants.COMMA) ? fileIdStr : fileIdStr + BasConstants.COMMA;
                }
            }
        }
        return successSignContractByKeyword(cfcaContractNo);
    }

    @Override
    public String successSignContractByKeywordSpecial(String cfcaContractNo, String contractNo) {
        if (StringUtils.isBlank(cfcaContractNo)) {
            log.info("successSignContractByKeywordSpecial error,contractNo:{} cfcaContractNo is null!", contractNo);
            return "";
        }
        CtrContract contract = ctrContractService.findByContractNo(contractNo);
        if (contractNo.startsWith("KUX") && StringUtils.equals(BasConstants.STOCK_VIRTUAL_KC, contract.getVirtualType())) {
            return successSignContractVirtual(cfcaContractNo, contractNo);
        }
        if (Objects.nonNull(contract) && Objects.nonNull(contract.getVirtualContractId())) {
            CtrContract targetContract = ctrContractService.getEntity(contract.getVirtualContractId());
            if (Objects.isNull(targetContract)) {
                log.info("successSignContractByKeywordSpecial error, contractNo:{} targetContract is null!", contractNo);
                return "";
            }
            AxqAutoSignVo axqAutoSignVo = convertVirtualSign(cfcaContractNo, targetContract);
            CfcaResp<String> cfcaResp = cfcaSignClient.autoSignContract(axqAutoSignVo);
            if (Objects.nonNull(cfcaResp) && cfcaResp.isSuccess()) {
                String fileIdStr = cfcaResp.getData();
                if (StringUtils.isNotBlank(fileIdStr)) {
                    return fileIdStr.endsWith(BasConstants.COMMA) ? fileIdStr : fileIdStr + BasConstants.COMMA;
                }
            }
        }
        AxqAutoSignVo axqAutoSignVo = new AxqAutoSignVo();
        axqAutoSignVo.setCfcaContractNo(cfcaContractNo);
        List<AxqAutoSignVo.Signatorie> signatorieList = new ArrayList<>();
        AxqAutoSignVo.Signatorie signatorie = new AxqAutoSignVo.Signatorie();
        signatorie.setCompanyName(contract.getOurCompanyName());
        signatorie.setKeyWord("补充条款");
        signatorie.setSealType("CTR");
        signatorie.setImageHeight("150");
        signatorie.setImageWidth("150");
        signatorie.setOffsetCoordX("55");
        signatorie.setOffsetCoordY("-75");
        signatorieList.add(signatorie);
        axqAutoSignVo.setSignatorieList(signatorieList);
        CfcaResp<String> cfcaResp = cfcaSignClient.autoSignContract(axqAutoSignVo);
        if (Objects.nonNull(cfcaResp) && cfcaResp.isSuccess()) {
            String fileIdStr = cfcaResp.getData();
            if (StringUtils.isNotBlank(fileIdStr)) {
                return fileIdStr.endsWith(BasConstants.COMMA) ? fileIdStr : fileIdStr + BasConstants.COMMA;
            }
        }
        return "";
    }

    private String successSignContractVirtual(String cfcaContractNo, String contractNo) {
        try {
            CtrContract contract = ctrContractService.findByContractNo(contractNo);
            AxqAutoSignVo axqAutoSignVo = new AxqAutoSignVo();
            axqAutoSignVo.setCfcaContractNo(cfcaContractNo);
            List<AxqAutoSignVo.Signatorie> signatorieList = new ArrayList<>();
            AxqAutoSignVo.Signatorie signatorie = new AxqAutoSignVo.Signatorie();
            signatorie.setCompanyName(contract.getOurCompanyName());
            signatorie.setKeyWord(SEAL_DEFAULT_PREFIX + contract.getOurCompanyName());
            signatorie.setSealType("CTR");
            signatorie.setImageHeight("140");
            signatorie.setImageWidth("140");
            signatorie.setOffsetCoordX("20");
            signatorie.setOffsetCoordY("-50");
            signatorieList.add(signatorie);

            AxqAutoSignVo.Signatorie signatorie2 = new AxqAutoSignVo.Signatorie();
            signatorie2.setCompanyName(contract.getCompanyName());
            signatorie2.setKeyWord(SEAL_DEFAULT_PREFIX + contract.getCompanyName());
            signatorie2.setSealType("CTR");
            signatorie2.setImageHeight("140");
            signatorie2.setImageWidth("140");
            signatorie2.setOffsetCoordX("20");
            signatorie2.setOffsetCoordY("-50");
            signatorieList.add(signatorie2);

            axqAutoSignVo.setSignatorieList(signatorieList);
            CfcaResp<String> cfcaResp = cfcaSignClient.autoSignContract(axqAutoSignVo);
            if (Objects.nonNull(cfcaResp) && cfcaResp.isSuccess()) {
                String fileIdStr = cfcaResp.getData();
                if (StringUtils.isNotBlank(fileIdStr)) {
                    return fileIdStr.endsWith(BasConstants.COMMA) ? fileIdStr : fileIdStr + BasConstants.COMMA;
                }
            }
        } catch (Exception e) {
            log.error("successSignContractVirtual error", e);
        }
        return "";
    }

    @Override
    public String successSignProtocolFileByKeyword(String cfcaContractNo, String contractNo) {
        if (StringUtils.isBlank(cfcaContractNo)) {
            return "";
        }
        CtrContract contract = ctrContractService.findByContractNo(contractNo);
        if (Objects.nonNull(contract)) {
            if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, contract.getBusinessType())) {
                AxqAutoSignVo axqAutoSignVo = convertVirtualSign(cfcaContractNo, contract);
                CfcaResp<String> cfcaResp = cfcaSignClient.autoSignContract(axqAutoSignVo);
                if (Objects.nonNull(cfcaResp) && cfcaResp.isSuccess()) {
                    String fileIdStr = cfcaResp.getData();
                    if (StringUtils.isNotBlank(fileIdStr)) {
                        return fileIdStr.endsWith(BasConstants.COMMA) ? fileIdStr : fileIdStr + BasConstants.COMMA;
                    }
                }
            } else if (Objects.nonNull(contract.getVirtualContractId())) {
                CtrContract targetContract = ctrContractService.getEntity(contract.getVirtualContractId());
                if (Objects.isNull(targetContract)) {
                    return "";
                }
                AxqAutoSignVo axqAutoSignVo = convertVirtualSign(cfcaContractNo, targetContract);
                CfcaResp<String> cfcaResp = cfcaSignClient.autoSignContract(axqAutoSignVo);
                if (Objects.nonNull(cfcaResp) && cfcaResp.isSuccess()) {
                    String fileIdStr = cfcaResp.getData();
                    if (StringUtils.isNotBlank(fileIdStr)) {
                        return fileIdStr.endsWith(BasConstants.COMMA) ? fileIdStr : fileIdStr + BasConstants.COMMA;
                    }
                }
            }
        }
        return successSignContractByKeyword(cfcaContractNo);
    }

    @Override
    public String successSignDcsxProtocolFileByKeyword(String cfcaContractNo, String contractNo) {
        if (StringUtils.isBlank(cfcaContractNo)) {
            return "";
        }
        ApplyCtrDCSX ctrDCSX = applyDcsxService.findByContractNo(contractNo);
        if (Objects.nonNull(ctrDCSX)) {
            if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, ctrDCSX.getBusinessType())) {
                AxqAutoSignVo axqAutoSignVo = convertVirtualDcsxSign(cfcaContractNo, ctrDCSX);
                CfcaResp<String> cfcaResp = cfcaSignClient.autoSignContract(axqAutoSignVo);
                if (Objects.nonNull(cfcaResp) && cfcaResp.isSuccess()) {
                    String fileIdStr = cfcaResp.getData();
                    if (StringUtils.isNotBlank(fileIdStr)) {
                        return fileIdStr.endsWith(BasConstants.COMMA) ? fileIdStr : fileIdStr + BasConstants.COMMA;
                    }
                }
            }
        }
        return successSignContractByKeyword(cfcaContractNo);
    }

    @Override
    @ServiceTransactional
    public void successFLKPurchaseOrder(Long approveId) {
        bizSignService.successBizSign(approveId);
    }

    /**
     * 组装自动签署接口参数
     *
     * @param targetContract
     * @return
     */
    private AxqAutoSignVo convertVirtualSign(String cfcaContractNo, CtrContract targetContract) {
        AxqAutoSignVo axqAutoSignVo = new AxqAutoSignVo();
        axqAutoSignVo.setCfcaContractNo(cfcaContractNo);
        List<AxqAutoSignVo.Signatorie> signatorieList = new ArrayList<>();
        signatorieList.add(generateAxqAutoSignVoParam(targetContract.getOurCompanyName(), "-20"));
        signatorieList.add(generateAxqAutoSignVoParam(targetContract.getCompanyName(), "80"));
        axqAutoSignVo.setSignatorieList(signatorieList);
        return axqAutoSignVo;
    }

    /**
     * 组装自动签署接口参数
     *
     * @param ctrDCSX
     * @return
     */
    private AxqAutoSignVo convertVirtualDcsxSign(String cfcaContractNo, ApplyCtrDCSX ctrDCSX) {
        AxqAutoSignVo axqAutoSignVo = new AxqAutoSignVo();
        axqAutoSignVo.setCfcaContractNo(cfcaContractNo);
        List<AxqAutoSignVo.Signatorie> signatorieList = new ArrayList<>();
        signatorieList.add(generateAxqAutoSignVoParam(ctrDCSX.getOurCompanyName(), "-20"));
        signatorieList.add(generateAxqAutoSignVoParam(ctrDCSX.getCompanyName(), "80"));
        axqAutoSignVo.setSignatorieList(signatorieList);
        return axqAutoSignVo;
    }

    private AxqAutoSignVo.Signatorie generateAxqAutoSignVoParam(String targetName, String offsetX) {
        AxqAutoSignVo.Signatorie signatorie = new AxqAutoSignVo.Signatorie();
        signatorie.setCompanyName(targetName);
        signatorie.setKeyWord("（盖章）：" + targetName);
        signatorie.setSealType("CTR");
        signatorie.setImageHeight("140");
        signatorie.setImageWidth("140");
        signatorie.setOffsetCoordX(offsetX);
        signatorie.setOffsetCoordY("5");
        return signatorie;
    }

    private AxqAutoSignVo.Signatorie generateAxqAutoSignVoParam(String targetName, String keyWordPrefix, String offsetX) {
        AxqAutoSignVo.Signatorie signatorie = new AxqAutoSignVo.Signatorie();
        signatorie.setCompanyName(targetName);
        signatorie.setKeyWord(keyWordPrefix + targetName);
        signatorie.setSealType("CTR");
        signatorie.setImageHeight("140");
        signatorie.setImageWidth("140");
        signatorie.setOffsetCoordX(offsetX);
        signatorie.setOffsetCoordY("-35");
        return signatorie;
    }

    private AxqContractVo convertSignFileDcsxV2(String companyName, String contractPdfFileId, String contractNo, Boolean virtualFlg) {
        AxqContractVo vo = new AxqContractVo();
        String signKeyWord = Boolean.TRUE.equals(virtualFlg) ? "（盖章）： " + companyName : companyName;
        vo.setSignKeyword(signKeyWord);
        vo.setCfcaTemplateName(contractNo);
        vo.setBuyerCompanyName(companyName);
        vo.setSignType("CTR");
        vo.setFileId(contractPdfFileId);
        vo.setGenerateShortUrlFlg(false);
        if (StringUtils.isNotBlank(contractPdfFileId) && contractPdfFileId.endsWith(",")) {
            vo.setFileId(contractPdfFileId.replaceAll(",", ""));
        }
        vo.setProjectCode("003");
        return vo;
    }

    private AxqContractVo convertSignFileDcsxV2(String companyName, String contractPdfFileId, SealUsageDcsxVo entity) {
        AxqContractVo vo = new AxqContractVo();
        String signKeyWord = Boolean.TRUE.equals(entity.getVirtualFlg()) ? "（盖章）： " + companyName : companyName;
        vo.setSignKeyword(signKeyWord);
        vo.setCfcaTemplateName(entity.getContractNo());
        vo.setBuyerCompanyName(companyName);
        vo.setSignType("CTR");
        vo.setFileId(contractPdfFileId);
        vo.setGenerateShortUrlFlg(false);
        if (StringUtils.isNotBlank(contractPdfFileId) && contractPdfFileId.endsWith(",")) {
            vo.setFileId(contractPdfFileId.replaceAll(",", ""));
        }
        vo.setProjectCode("003");
        return vo;
    }

    private AxqContractVo convertSignFileWithPrefix(String companyName, String contractPdfFileId, String contractNo, String prefix) {
        AxqContractVo vo = new AxqContractVo();
        String signKeyWord = StringUtils.isNotBlank(prefix) ? prefix + companyName : companyName;
        vo.setSignKeyword(signKeyWord);
        vo.setCfcaTemplateName(contractNo);
        vo.setBuyerCompanyName(companyName);
        vo.setSignType("CTR");
        vo.setFileId(contractPdfFileId);
        vo.setGenerateShortUrlFlg(false);
        if (StringUtils.isNotBlank(contractPdfFileId) && contractPdfFileId.endsWith(",")) {
            vo.setFileId(contractPdfFileId.replaceAll(",", ""));
        }
        vo.setProjectCode("003");
        return vo;
    }

    /**
     * 保存文件签署
     *
     * @param approve
     * @param contractPdfFileId
     * @param contractNo
     * @param signCompanyNameList
     */
    private void saveSignFile(PmApprove approve, String contractPdfFileId, String contractNo, String owningRegion, List<String> signCompanyNameList) {
        Long enterpriseId = approve.getEnterpriseId();
        SignFile signFile = new SignFile();
        signFile.setSignType("1");
        signFile.setFileId(contractPdfFileId);
        signFile.setFile(contractNo + ".pdf");
        signFile.setFileName(contractNo + "[系统][" + (contractNo.contains("X") ? "中游" : "下游") + "]");
        owningRegion = StringUtils.isBlank(owningRegion) ? BsDictConstants.DICT_TYPE_CREATOR : owningRegion;
        String creator = BsDictUtil.getValue(enterpriseId, BsDictConstants.DICT_TYPE_SIGN_PDF_CREATOR, owningRegion);
        signFile.setCreator(StringUtils.isBlank(creator) ? "孙宏丽" : creator);
        signFile.setSignStatus("N");
        signFile.setCompanyNames(String.join(BasConstants.SEPARATE, signCompanyNameList));
        signFile.setSealUsageApproveId(approve.getId());
        signFile.setSealUsageApproveNo(approve.getApproveNo());
        signFile = signFileDao.save(signFile);

        SignFileUser signFileUser = new SignFileUser();
        for (String companyName : signCompanyNameList) {
            EnterpriseAccount enterpriseAccount = enterpriseAccountClient.getByCompanyName(companyName);
            List<SignTransactor> signTransactorList = signTransactorClient.findSignTransactorByuserId(enterpriseAccount.getUserId());
            SignTransactor signTransactor = signTransactorList.get(0);
            List<SignFileUser> signFileUserList = new ArrayList<>();
            signFileUser.setId(0L);
            signFileUser.setSignFileId(signFile.getId());
            signFileUser.setCompanyName(companyName);
            signFileUser.setSignType("CTR");
            signFileUser.setSignName(signTransactor.getTransactorName());
            signFileUser.setSignPhone(signTransactor.getMobilePhone());
            signFileUser.setSignEmail(signTransactor.getEmail());
            signFileUserList.add(signFileUser);
            signUserFileService.saveDatas(signFileUserList, null, null, signFile.getId());
        }
    }

    /**
     * 【电子签】青岛中光上游业务盖章申请，流程发起时，自动生成文件签署记录
     *
     * @param approve  审批单
     * @param contract 上游合同
     */
    private void saveBusinessBuySignFile(PmApprove approve, CtrContract contract, String contractPdfFileId) {
        try {
            String ourCompanyName = contract.getOurCompanyName();
            Long enterpriseId = approve.getEnterpriseId();
            SignFile signFile = new SignFile();
            signFile.setSignType("1");
            signFile.setFileId(contractPdfFileId);
            signFile.setFile(contract.getContractNo() + ".pdf");
            signFile.setFileName(contract.getContractNo() + "[系统][青光][上游]");
            String owningRegion = StringUtils.isBlank(contract.getOwningRegion()) ? BsDictConstants.DICT_TYPE_CREATOR : contract.getOwningRegion();
            String creator = BsDictUtil.getValue(enterpriseId, BsDictConstants.DICT_TYPE_SIGN_PDF_CREATOR, owningRegion);
            signFile.setCreator(StringUtils.isBlank(creator) ? approve.getCreateUserName() : creator);
            signFile.setSignStatus("N");
            signFile.setCompanyNames(ourCompanyName);
            signFile.setSealUsageApproveId(approve.getId());
            signFile.setSealUsageApproveNo(approve.getApproveNo());
            signFile = signFileDao.save(signFile);

            SignFileUser signFileUser = new SignFileUser();
            EnterpriseAccount enterpriseAccount = enterpriseAccountClient.getByCompanyName(ourCompanyName);
            List<SignTransactor> signTransactorList = signTransactorClient.findSignTransactorByuserId(enterpriseAccount.getUserId());
            SignTransactor signTransactor = signTransactorList.stream().filter(s -> StringUtils.equals("肖君怡", s.getTransactorName())).findAny().orElse(signTransactorList.get(0));
            List<SignFileUser> signFileUserList = new ArrayList<>();
            signFileUser.setId(0L);
            signFileUser.setSignFileId(signFile.getId());
            signFileUser.setCompanyName(ourCompanyName);
            signFileUser.setSignType("CTR");
            signFileUser.setSignName(signTransactor.getTransactorName());
            signFileUser.setSignPhone(signTransactor.getMobilePhone());
            signFileUser.setSignEmail(signTransactor.getEmail());
            signFileUserList.add(signFileUser);
            signUserFileService.saveDatas(signFileUserList, null, null, signFile.getId());
            signFileServiceImpl.generateSignature(signFile.getId(), 1);

            PmApproveContents contents = pmApproveContentsService.findByApproveId(approve.getId());
            if (Objects.nonNull(contents)) {

                signFileUserList = signUserFileService.findSignFileUserBySignId(signFile.getId());

                contents.setCfcaContractNo(signFile.getCfcaContractNo());
                contents.setSignShortUrl(signFileUserList.get(0).getShortUrl());
                contents.setFileId(contractPdfFileId + BasConstants.COMMA);
                pmApproveContentsService.save(contents);
            }
        } catch (Exception e) {
            log.info("saveBusinessBuySignFile generateSignature error", e);
        }
    }

    private boolean verifyPDF(String fileId) {
        if (StringUtils.isBlank(fileId)) {
            return false;
        }
        List<String> fileList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(fileId);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(fileList)) {
            return false;
        }
        fileId = fileList.get(0);
        FileRecord fileRecord = fileRecordService.findByFileId(fileId);
        return Objects.nonNull(fileRecord) && (fileRecord.getFileName().contains(".pdf") || fileRecord.getFileName().contains(".PDF"));
    }

    private boolean verifyFLK(PmApprove approve, CtrContract contract){
        log.info("verifyFLK contractNo:{}", contract.getContractNo());
        // 范伦克采购合同
        String contractType = contract.getContractType();
        String businessType = contract.getBusinessType();
        String companyName = contract.getCompanyName();
        if (!StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contractType)) {
            log.info("verifyFLK stop contractType:{}", contractType);
            return false;
        }
        if (!StringUtils.equals(BasConstants.COMPANY_NAME_FLK, companyName)
                && !StringUtils.equals(BasConstants.COMPANY_NAME_ZJKR, companyName)
                && !StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, companyName)
                && !StringUtils.equals(BasConstants.COMPANY_NAME_SHZG, companyName)
                && !StringUtils.equals(BasConstants.COMPANY_NAME_ZJWS, companyName)) {
            log.info("verifyFLK stop companyName:{}", companyName);
            return false;
        }
        if (!StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType)) {
            log.info("verifyFLK stop businessType:{}", businessType);
            return false;
        }
        // 判断我方是否具备电子签资格
        Boolean verifySignCompany = bsCompanyDcsxService.verifySignCompany(contract.getOurCompanyName());
        if (Boolean.FALSE.equals(verifySignCompany)) {
            log.error("verifyFLK 我方不具备电子签资格:{}", contract.getOurCompanyName());
            return false;
        }
        return true;
    }
}
