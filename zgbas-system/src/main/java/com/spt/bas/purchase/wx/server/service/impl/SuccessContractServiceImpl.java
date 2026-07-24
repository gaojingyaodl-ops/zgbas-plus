package com.spt.bas.purchase.wx.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.CtrContractOphisRequest;
import com.spt.bas.purchase.wx.server.service.IContractService;
import com.spt.bas.purchase.wx.server.service.ISuccessContractService;
import com.spt.bas.report.client.entity.RptConfirmReceiptDetail;
import com.spt.bas.report.client.entity.RptConfirmReceiptVo;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.vo.PmApproveStepFlowVo;
import com.spt.sign.client.entity.SignContract;
import com.spt.sign.client.entity.SignInfo;
import com.spt.sign.client.remote.ISignContractClient;
import com.spt.sign.client.remote.ISignInfoClient;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: gaojy
 * @create 2021/12/9 18:05
 * @version: 1.0
 * @description:
 */
@Component
@Transactional(readOnly = true)
public class SuccessContractServiceImpl implements ISuccessContractService {
    private Logger logger = LoggerFactory.getLogger(SuccessContractServiceImpl.class);
    @Autowired
    private ISignContractClient signContractClient;
    @Autowired
    private ISignInfoClient signInfoClient;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;
    @Autowired
    private IPmApproveClient pmApproveClient;
    @Autowired
    private ICtrContractOphisClient contractOphisClient;
    @Autowired
    private IContractService contractService;
    @Autowired
    private IApplyDeliveryOutClient applyDeliveryOutClient;
    @Autowired
    private ISignFileApiClient signFileApiClient;
    @Autowired
    private IApplyFactorSignClient applyFactorSignClient;


    /**
     * 完成Cfca合同签署
     */
    @Override
    @ServiceTransactional
    public void doSuccessContract() {
        // 查询近5分钟更新未签署的电子合同 执行签署合同操作后 返回电子合同列表 （注意：返回的signContractList 合同已执行DownloadContractNo操作）
        List<SignContract> signContractList = signContractClient.getCompleteContract5Minute();
        if (CollectionUtils.isEmpty(signContractList)) {
            logger.info(">>>暂未查询到已签署未完成的合同!");
            return;
        }
        logger.info(">>>查询已完成未签署合同 共查询问题数据{}条,合同号:{}", signContractList.size(), signContractList.stream().map(SignContract::getContractNo).collect(Collectors.joining(",")));
        Map<PmApproveContents, SignContract> signContractMap = new HashMap<>();
        Map<String, CtrContract> contractMap = new HashMap<>();
        signContractList.forEach(signContract -> {
            CtrContract ctrContract = ctrContractClient.findByContractNoV2(signContract.getBizContractId());
            if (Objects.nonNull(ctrContract) && StringUtils.equals(BasConstants.CONTRACTTYPE_SELL, ctrContract.getContractType())) {
                List<PmApproveContents> pmApproveContents = pmApproveContentsClient.findByRealApproveId(ctrContract.getApproveId());
                pmApproveContents.forEach(pmApproveContent -> {
                    if ("SealUsage".equals(pmApproveContent.getApplyName())) {
                        SealUsage sealUsage = JsonUtil.json2Object(SealUsage.class, pmApproveContent.getContents());
                        if (Objects.nonNull(sealUsage) && StringUtils.equals(BasConstants.CONTRACTTYPE_SELL, sealUsage.getBusinessType())) {
                            contractMap.put(signContract.getBizContractId(), ctrContract);
                            signContractMap.put(pmApproveContent, signContract);
                        }
                    }
                });
            }
        });
        signContractMap.forEach((pmApproveContent, signContract) -> {
            String status = pmApproveContent.getStatus();
            String fileId = pmApproveContent.getFileId();
            logger.info("contractNo:{} downLoadPath:{}", signContract.getContractNo(), signContract.getDownloadPath());
            if (!StringUtils.equals(BasConstants.CONTRACTSTATUS_D, status) || !StringUtils.equals(signContract.getDownloadPath() + ",", fileId)) {
                try {
                    CtrContract contract = contractMap.get(signContract.getBizContractId());
                    String ourCompanyName = contract.getOurCompanyName();
                    String contractNo = contract.getContractNo();
                    boolean sgxSpecialFlg = StringUtils.equals("SGX", ourCompanyName) || StringUtils.equals("苏州高新供应链管理有限公司", ourCompanyName);
                    logger.info("contractNo:{}, ourCompanyName:{}, sgxSpecialFlg:{}", contractNo, ourCompanyName, sgxSpecialFlg);
                    if (Boolean.TRUE.equals(sgxSpecialFlg)) {
                        pmApproveContent.setFileId(signContract.getDownloadPath() + ",");
                        pmApproveContentsClient.save(pmApproveContent);
                    } else {
                        // 修改盖章申请单状态
                        pmApproveContent.setFileId(signContract.getDownloadPath() + ",");
                        pmApproveContent.setStatus(BasConstants.CONTRACTSTATUS_D);
                        pmApproveContentsClient.save(pmApproveContent);
                        // 完成盖章审批
                        PmApproveStepFlowVo vo = new PmApproveStepFlowVo();
                        vo.setComplete(true);
                        vo.setApproveId(pmApproveContent.getApproveId());
                        vo.setApproveStepId(0L);
                        vo.setApproveOpinion(BasConstants.APPROVE_OPINION_AGREE);
                        vo.setApproveUserId(999999L);
                        vo.setApproveUserName("anxinsign");
                        vo.setApproveRemark("客户小程序签署");
                        pmApproveClient.doStepFlow(vo);
                    }
                }catch(Exception e){
                    logger.error("successContract doStepFlow error:{}",e.getMessage());
                }
            }
        });
    }

    /**
     * 应收账款债权完成签署
     */
    @Override
    @ServiceTransactional
    public void doSuccessDebtCertificate() {
        // 查询近5分钟更新未签署的应收账款债权 执行签署合同操作后 返回电子合同列表 （注意：返回的signContractList 合同已执行DownloadContractNo操作）
        List<SignContract> signContractList = signContractClient.getCompleteDebtCertificate5Minute();
        if (CollectionUtils.isEmpty(signContractList)) {
            logger.info(">>>暂未查询到已签署未完成的应收账款债权合同 !");
            return;
        }
        signContractList.forEach(signContract -> {
            String downloadPath = signContract.getDownloadPath();
            String contractNo = signContract.getAttachmentName();
            CtrContract ctrContract = ctrContractClient.findByContractNoV2(contractNo);
            if (Objects.nonNull(ctrContract) && StringUtils.equals(BasConstants.CONTRACTTYPE_SELL, ctrContract.getContractType()) && StringUtils.isNotBlank(downloadPath)) {
                ctrContract.setDebtCertificateFileId(downloadPath+",");
                ctrContractClient.save(ctrContract);
                ctrContractClient.refreshFactorStatus(ctrContract.getId());
                CtrContractOphisRequest request = new CtrContractOphisRequest();
                request.setCtrContractId(ctrContract.getId());
                request.setCancel(false);
                request.setCreateUserId(0L);
                request.setCreateUserName("系统任务");
                request.setRemark("同步债权凭证附件，附件ID："+downloadPath);
                request.setProcessName("债权凭证");
                request.setHappenDate(new Date());
                request.setContractGroup("CTR");
                contractOphisClient.addHis(request);
                logger.info("doSuccessDebtCertificate contractNo:{},debtCertificateFileId:{}", contractNo, downloadPath);

                // 债权凭证签署完成后自动发起已审批完成的签署保理申请单
                this.autoStartFactorSign(ctrContract);
            }
        });
    }

    /**
     * 债权凭证签署完成后自动发起已审批完成的签署保理申请单
     * @param ctrContract
     */
    @Override
    public void autoStartFactorSign(CtrContract ctrContract){
        logger.info("begin autoStartFactorSign ---");
        try {
            ApplyFactorSign entity = new ApplyFactorSign();
            entity.setContractId(ctrContract.getId());
            entity.setContractNo(ctrContract.getContractNo());
            entity.setProductNames(ctrContract.getProductsName());
            entity.setContractTime(ctrContract.getContractTime());
            entity.setCompanyId(ctrContract.getCompanyId());
            entity.setCompanyName(ctrContract.getCompanyName());
            entity.setOurCompanyName(ctrContract.getOurCompanyName());
            entity.setDealNumber(ctrContract.getTotalNumber());
            entity.setDealPrice(ctrContract.getDealPrice());
            entity.setTotalAmount(ctrContract.getTotalAmount());
            entity.setPayFullDate(ctrContract.getPayFullTime());
            entity.setDeliveryDate(ctrContract.getDeliveryDateTo());
            entity.setFileId(ctrContract.getDebtCertificateFileId());
            entity.setStatus(BasConstants.APPROVE_STATUS_D);
            entity.setEnterpriseId(ctrContract.getEnterpriseId());
            entity.setApplyUserId(ctrContract.getMatchUserId());

            applyFactorSignClient.applyFactorSign(entity);
        }catch (Exception e){
            logger.error("autoStartFactorSign error", e);
        }
    }

    /**
     * 确认收货签收单完成签署
     */
    @Override
    @ServiceTransactional
    public void doReceiveGood() {
        // 查询近5分钟更新未签署的签收单 执行签署合同操作后 返回电子合同列表 （注意：返回的signContractList 合同已执行DownloadContractNo操作）
        List<SignContract> signContractList = signContractClient.getCompleteReceiveGood5Minute();
        if (CollectionUtils.isEmpty(signContractList)) {
            logger.info(">>>暂未查询到已签署未完成的收货确认签收单!");
            return;
        }

        signContractList.forEach(signContract -> {
            SignInfo signInfo = new SignInfo();
            signInfo.setContractNo(signContract.getContractNo());
            List<SignInfo> signInfols = signInfoClient.getSignInfols(signInfo);
            
            String bizContractNo = signInfols.get(0).getBizContractId();
            String fileId = signContract.getDownloadPath();
            ApplyDeliveryOut deliveryOut = applyDeliveryOutClient.findByApplyNo(bizContractNo);
            RptConfirmReceiptVo confirmReceiptVo = new RptConfirmReceiptVo();
            confirmReceiptVo.setContractNo(deliveryOut.getContractNo());
            confirmReceiptVo.setDeliveryId(bizContractNo);
            List<RptConfirmReceiptDetail> confirmReceiptList = new ArrayList<>();
            RptConfirmReceiptDetail confirmReceiptDetail = new RptConfirmReceiptDetail();
            confirmReceiptDetail.setDeliveryId(bizContractNo);
            confirmReceiptDetail.setFileId(fileId);
            confirmReceiptList.add(confirmReceiptDetail);
            confirmReceiptVo.setConfirmReceiptList(confirmReceiptList);
            contractService.confirmReceiptV2(confirmReceiptVo);
            logger.info("doReceiveGood contractNo:{},fileId:{}", bizContractNo, fileId);
        });
    }

    @Override
    @ServiceTransactional
    public void doUploadContractSigned() {
        List<SignContract> signContractList = signContractClient.getUploadContractSignedUpdate();
        if (CollectionUtils.isEmpty(signContractList)) {
            logger.info(">>>暂未查询到自定义上传合同完成签署!");
            return;
        }
        signContractList.forEach(signContract -> {
            String downloadPath = signContract.getDownloadPath();
            String contractNo = signContract.getContractNo();
            String contractStatus = signContract.getContractState();
            SignFile byCfcaContractNo = signFileApiClient.findByCfcaContractNo(contractNo);
            if (Objects.nonNull(byCfcaContractNo) && StringUtils.isNotBlank(downloadPath)) {
                byCfcaContractNo.setFileId(downloadPath);
                byCfcaContractNo.setSignStatus(contractStatus);
                signFileApiClient.save(byCfcaContractNo);
                 logger.info("", contractNo, downloadPath);
                 PmApproveContents byApproveId = pmApproveContentsClient.findByApproveId(byCfcaContractNo.getSealUsageApproveId());
                 byApproveId.setFileId(byApproveId.getFileId()+","+downloadPath);
                 pmApproveContentsClient.save(byApproveId);
                 logger.info("对应盖章审批添加附件", byCfcaContractNo.getSealUsageApproveNo(), downloadPath);
            }
        });
    }

    }




