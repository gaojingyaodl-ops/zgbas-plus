package com.spt.bas.server.service.impl;

import com.hsoft.push.sdk.remote.PushClientHttp;
import com.hsoft.push.sdk.vo.PushRequest;
import com.hsoft.push.sdk.vo.PushTarget;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.SealUsage;
import com.spt.bas.client.entity.SealUsageDCSX;
import com.spt.bas.client.entity.SignFile;
import com.spt.bas.client.entity.SignFileUser;
import com.spt.bas.client.vo.sign.SignFileSearchVo;
import com.spt.bas.server.dao.sign.SignFileDao;
import com.spt.bas.server.dao.sign.SignFileUserDao;
import com.spt.bas.server.service.ISignFileService;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmService;
import com.spt.sign.client.cfca.CfcaCreateContract;
import com.spt.sign.client.cfca.CfcaResp;
import com.spt.sign.client.cfca.CfcaShortUrl3912;
import com.spt.sign.client.entity.SignContract;
import com.spt.sign.client.remote.ICfcaSignClient;
import com.spt.sign.client.vo.AxqAutoSignVo;
import com.spt.sign.client.vo.AxqContractVo;
import com.spt.sign.client.vo.AxqUploadVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.util.SpringContextHolder;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Transactional(readOnly = true)
public class SignFileServiceImpl extends BaseService<SignFile> implements ISignFileService {
    private static final ExecutorService threadPool = Executors.newSingleThreadExecutor();
    @Autowired
    private SignFileDao signFileDao;

    @Autowired
    private SignFileUserDao signFileUserDao;

    @Autowired
    private ICfcaSignClient cfcaSignClient;

    @Autowired
    private PmApproveDao pmApproveDao;

    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;

    @Autowired
    private PmProcessDao pmProcessDao;
    @Resource
    private PushClientHttp pushRemote;

    @Override
    public BaseDao<SignFile> getBaseDao(){
        return signFileDao;
    }
    @Override
    public Page<SignFile> findPageSignFile(SignFileSearchVo searchVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows(), sort);
        Specification<SignFile> spec = WebUtil.buildSpecification(searchVo.getSearchParams());
        return getBaseDao().findAll(spec, pageRequest);
    }

    @Override
    public SignFile findByCfcaContractNo(String cfcaContractNo) {
        return signFileDao.findByCfcaContractNo(cfcaContractNo);
    }

    @Override
    @ServiceTransactional
    public SignFile generateSignature(Long signId, Integer signAuthType) throws ApplicationException {
        if (Objects.isNull(signId) || signId == 0L) {
            throw new ApplicationException("signId error,signId:"+ signId);
        }
        SignFile signFile = signFileDao.findOne(signId);
        if (Objects.isNull(signFile)){
            throw new ApplicationException("无效的signId !");
        }
        String companyNames = signFile.getCompanyNames();
        boolean autoSignFlg = StringUtils.equals("2", signFile.getSignType());
        if (Boolean.FALSE.equals(autoSignFlg) && StringUtils.isNotBlank(companyNames) && !companyNames.contains(BasConstants.COMPANY_NAME_FTK)){
            String sealUsageApproveNo = signFile.getSealUsageApproveNo();
            if (StringUtils.isBlank(sealUsageApproveNo)){
                throw new ApplicationException("请先补充关联盖章审批单号!");
            }
            PmApprove pmApprove = pmApproveDao.findByApproveNo(sealUsageApproveNo);
            if (Objects.isNull(pmApprove)){
                throw new ApplicationException("无效的盖章审批单号!");
            }
        }
        List<SignFileUser> signFileUserList = signFileUserDao.findBySignId(signId);
        if (CollectionUtils.isEmpty(signFileUserList)){
            throw new ApplicationException("文件签署方不可为空 !");
        }
        // 组装签署参数
        List<AxqContractVo> axqContractVoList = convertSignFile(signFile, signFileUserList, autoSignFlg, signAuthType);

        // 上传合同签署
        AxqUploadVo axqUploadVo = cfcaSignClient.axqUploadContractSigned(axqContractVoList);

        if (Boolean.TRUE.equals(autoSignFlg)) {
            // 上传文件，自动签署
            generateAutoSign(axqUploadVo, signFile, signFileUserList);
        } else {
            // 上传文件，生成短链接认证签署
            generateShortUrl(axqUploadVo, signFile, signFileUserList);
        }
        return signFile;
    }

    private void generateAutoSign(AxqUploadVo axqUploadVo, SignFile signFile, List<SignFileUser> signFileUserList) throws ApplicationException {
        if (Objects.nonNull(axqUploadVo) && StringUtils.equals("60000000", axqUploadVo.getRetCode())) {
            String cfcaContractNo = axqUploadVo.getContractNo();
            signFile.setCfcaContractNo(cfcaContractNo);
            logger.info("generateAutoSign cfcaContractNo:{}", cfcaContractNo);
            AxqAutoSignVo axqAutoSignVo = convertAutoSign(signFile, signFileUserList);
            CfcaResp<String> cfcaResp = cfcaSignClient.autoSignContract(axqAutoSignVo);
            if (Objects.nonNull(cfcaResp) && cfcaResp.isSuccess()){
                String fileIdStr = cfcaResp.getData();
                if (StringUtils.isNotBlank(fileIdStr)){
                    String fileId = fileIdStr.endsWith(BasConstants.COMMA) ? fileIdStr : fileIdStr + BasConstants.COMMA;
                    signFile.setSignStatus("D");
                    signFile.setFileId(fileId);
                    signFile.setSignDate(new Date());
                    signFileUserDao.updateSignFileUserStatus(signFile.getId(), "D");
                }
            }
        } else {
            logger.info("axqUploadVo :{}", JsonUtil.obj2Json(axqUploadVo));
            String retMessage = axqUploadVo.getRetMessage();
            if (StringUtils.isBlank(retMessage)){
                retMessage = "自动签署失败!";
            }
            throw new ApplicationException(retMessage);
        }
    }

    private void generateShortUrl(AxqUploadVo axqUploadVo, SignFile signFile, List<SignFileUser> signFileUserList) throws ApplicationException {
        if (Objects.nonNull(axqUploadVo) && StringUtils.equals("60000000", axqUploadVo.getRetCode())) {
            signFile.setCfcaContractNo(axqUploadVo.getContractNo());

            List<CfcaShortUrl3912> cfcaShortUrlList = axqUploadVo.getCfcaShortUrlList();
            signFileUserList.forEach(signFileUser -> {
                CfcaShortUrl3912 cfcaShortUrl3912 = cfcaShortUrlList.stream()
                        .filter(c-> StringUtils.equals(signFileUser.getCompanyName(), c.getCompanyName()))
                        .filter(c -> StringUtils.equals(c.getMobilePhone(), signFileUser.getSignPhone()))
                        .findFirst().orElse(null);
                signFileUser.setShortUrl(Objects.nonNull(cfcaShortUrl3912) ? cfcaShortUrl3912.getShortUrl() : "");
            });
            signFileDao.save(signFile);
            signFileUserDao.saveAll(signFileUserList);

            // 发送文件签署邮件通知
            sendSignEmailSms(signFile, signFileUserList);
        } else {
            logger.info("axqUploadVo :{}", JsonUtil.obj2Json(axqUploadVo));
            String retMessage = axqUploadVo.getRetMessage();
            if (StringUtils.isBlank(retMessage)){
                retMessage = "生成短链接失败!";
            }
            throw new ApplicationException(retMessage);
        }
    }

    /**
     * 发送文件签署电子签通知邮件
     * @param signFile
     * @param signFileUserList
     */
    private void sendSignEmailSms(SignFile signFile, List<SignFileUser> signFileUserList){
        threadPool.execute(()->{
            try {
                PushRequest req = new PushRequest();
                req.setBusinessId(signFile.getSealUsageApproveNo());
                req.setModule("S");
                req.setPushType("basSignatureFile");
                req.setSubmitUserId("sys");
                Map<String, Object> param = new HashMap<>();
                param.put("signFileName", signFile.getFileName());
                param.put("signApproveNo", signFile.getSealUsageApproveNo());
                signFileUserList.forEach(user->{
                    List<PushTarget> lst = new ArrayList<>();
                    lst.add(new PushTarget(user.getSignName(), user.getSignPhone(),  user.getSignEmail()));
                    req.setTargets(lst);
                    param.put("signCompanyName", abbreviationCompanyName(user.getCompanyName()));
                    param.put("signShortUrl", user.getShortUrl());
                    param.put("signCreateDate", DateOperator.formatDate(user.getUpdatedDate(), true));
                    param.put("signUserPhone", user.getSignPhone());
                    param.put("signUserName", user.getSignName());
                    param.put("type", "");
                    param.put("companyNameLabel", "");
                    param.put("companyName", "");
                    setEmailParam(param, signFile.getSealUsageApproveNo(), user.getCompanyName());
                    req.setParam(param);
                    pushRemote.send(req);
                });
            }catch (Exception e) {
                logger.error("sendSignEmailSms error", e);
            }
        });
    }

    /**
     * 刷新电子签状态
     * @param cfcaContractNo
     * @return
     */
    @Override
    @ServiceTransactional
    public SignFile refreshSignFile(String cfcaContractNo) throws ApplicationException {
        StringBuilder message = new StringBuilder();
        SignFile signFile = signFileDao.findByCfcaContractNo(cfcaContractNo);
        try {
            Map map = cfcaSignClient.successContract(cfcaContractNo);
            logger.info("refreshSignFile, retMap:{}, cfcaContractNo:{}", map, cfcaContractNo);
            String retCode = (String) map.get("retCode");
            if (!StringUtils.equals("60000000", retCode)) {
                return signFile;
            }
            SignContract signContract = cfcaSignClient.findByContractNo(cfcaContractNo);
            logger.info("refreshSignFile, signContract:{}", JsonUtil.obj2Json(signContract));
            if (Objects.nonNull(signContract) && StringUtils.equals("1", signContract.getContractState())) {
                String fileId = signContract.getDownloadPath();
                if (Objects.isNull(fileId)) {
                    Map contractFileId = cfcaSignClient.getContractFileId(cfcaContractNo);
                    logger.info("refreshSignFile, getContractFileId:{}", JsonUtil.obj2Json(contractFileId));
                    fileId = (String) contractFileId.get("fileId");
                }
                if (StringUtils.isNotBlank(fileId)) {
                    fileId = fileId.endsWith(",") ? fileId : fileId + ",";
                    signFile.setSignStatus("D");
                    signFile.setFileId(fileId);
                    signFile.setSignDate(signContract.getUpdatedDate());
                    signFileUserDao.updateSignFileUserStatus(signFile.getId(), "D");
                    // 文件签署成功后更新对应审批单附件及合同附件
                    updateSealUsageApprove(signFile, fileId);
                    logger.info("签署完成更新审批单附件，审批单编号：{}，附件ID:{}", signFile.getSealUsageApproveNo(), fileId);
                    return signFileDao.save(signFile);
                }
            } else {
                CfcaCreateContract queryContract = cfcaSignClient.queryContract(cfcaContractNo);
                if (Objects.nonNull(queryContract) && Objects.nonNull(queryContract.getContract())) {
                    List<CfcaCreateContract.Signatorie> signatorieList = queryContract.getContract().getSignatories();
                    for (CfcaCreateContract.Signatorie signatorie : signatorieList) {
                        if (!Objects.equals(1, signatorie.getSignmentState())) {
                            message.append(signatorie.getUserName()).append("尚未签署；");
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("refreshSignFile error", e);
        }
        if (StringUtils.isNotBlank(message.toString())) {
            throw new ApplicationException(message.toString());
        }
        return signFile;
    }

    /**
     * 文件签署完成，更新盖章审批附件及合同附件
     *
     * @param signFile
     * @param fileId
     */
    private void updateSealUsageApprove(SignFile signFile, String fileId) {
        try {
            PmApproveContents approveContents = pmApproveContentsDao.findByApproveId(signFile.getSealUsageApproveId());
            if (Objects.nonNull(approveContents)) {
                // 1.更新盖章审批单附件
                String contentFileId = approveContents.getFileId();
                if (StringUtils.isBlank(contentFileId)) {
                    approveContents.setFileId(fileId);
                } else {
                    approveContents.setFileId(contentFileId + fileId);
                }
                approveContents.setSignShortUrl(null);
                pmApproveContentsDao.save(approveContents);

//                // 2.审批中的盖章审批直接审批通过
//                PmApprove pmApprove = pmApproveDao.findByApproveNo(signFile.getSealUsageApproveNo());
//                PmProcess process = pmProcessDao.findOne(pmApprove.getProcessId());
//                String processCode = process.getProcessCode();
//
//                // 【代采赊销盖章】不自动完成，因为存在审批人调整中游合同单价总计等数据的情况，直接完成会有问题，故在此判断
//                if (!StringUtils.equals(BasConstants.APPROVE_STATUS_D, pmApprove.getStatus()) &&
//                        !StringUtils.equals(BasConstants.APPLY_SEAL_USAGE_DCSX, processCode)) {
//                    // 完成盖章审批
//                    PmApproveStepFlowVo vo = new PmApproveStepFlowVo();
//                    vo.setComplete(true);
//                    vo.setApproveId(approveContents.getApproveId());
//                    vo.setApproveStepId(0L);
//                    vo.setApproveOpinion(BasConstants.APPROVE_OPINION_AGREE);
//                    vo.setApproveUserId(999999L);
//                    vo.setApproveUserName("文件签署");
//                    vo.setApproveRemark("文件签署自动完成");
//                    pmApproveService.doStepFlow(vo);
//                }

//                // 3.将文件同步至合同双签附件中
//                String contractNo = null;
//                String contractNoDcsx = null;
//                String entityName = process.getEntityName();
//                IPmEntity bizEntity = (IPmEntity) JsonUtil.json2Object(Class.forName(entityName), approveContents.getContents());
//                if (bizEntity instanceof SealUsage) {
//                    SealUsage sealUsage = (SealUsage) bizEntity;
//                    contractNo = sealUsage.getContractNo();
//                } else if (bizEntity instanceof SealUsageDCSX) {
//                    SealUsageDCSX sealUsageDCSX = (SealUsageDCSX) bizEntity;
//                    contractNoDcsx = sealUsageDCSX.getContractNo();
//                }
//
//                if (StringUtils.isNotBlank(contractNo)) {
//                    CtrContract contract = ctrContractDao.findByContractNo(contractNo);
//                    if (Objects.nonNull(contract)) {
//                        contract.setContractStatus(BasConstants.CONTRACT_STATUS_S);
//                        contract.setSealFlg(true);
//                        if (StringUtils.equalsIgnoreCase(BasConstants.CONTRACT_TYPE_S, contract.getContractType())) {
//                            String sellContentFileId = StringUtils.isNotBlank(contract.getSellContentFileId()) ? contract.getSellContentFileId() : "";
//                            contract.setSellContentFileId(sellContentFileId + fileId);
//                        } else {
//                            String buyContentFileId = StringUtils.isNotBlank(contract.getBuyContentFileId()) ? contract.getBuyContentFileId() : "";
//                            contract.setBuyContentFileId(buyContentFileId + fileId);
//                        }
//                        ctrContractDao.save(contract);
//                    }
//                }
//                if (StringUtils.isNotBlank(contractNoDcsx)) {
//                    ApplyCtrDCSX applyCtrDCSX = applyDcsxDao.findByContractNo(contractNoDcsx);
//                    if (Objects.nonNull(applyCtrDCSX)) {
//                        String contractFileId = StringUtils.isNotBlank(applyCtrDCSX.getFileId()) ? applyCtrDCSX.getFileId() : "";
//                        applyCtrDCSX.setFileId(contractFileId + fileId);
//                        applyCtrDCSX.setStatus(BasConstants.CONTRACT_STATUS_B);
//                        applyCtrDCSX.setSealFlg(true);
//                        applyDcsxDao.save(applyCtrDCSX);
//                    }
//                }
            }
        } catch (Exception e) {
            logger.error("updateSealUsageApprove error", e);
        }
    }

    //获取审批监听
    public IPmApproveListener getListenerService(PmProcess process,IPmService pmService) {
        IPmApproveListener listener = null;
        if (pmService instanceof IPmApproveListener) {
            listener = (IPmApproveListener) pmService;
        }
        if (StringUtils.isNotBlank(process.getListenerService())) {
            listener = SpringContextHolder.getBean(process.getListenerService());
        }
        return listener;
    }

    /**
     * 组装文件签署所需参数
     * @param signFile
     * @param signFileUserList
     * @return
     */
    private List<AxqContractVo> convertSignFile(SignFile signFile, List<SignFileUser> signFileUserList, Boolean autoSignFlg, Integer signAuthType){
        List<AxqContractVo> axqContractVoList = new ArrayList<>();
        signFileUserList.forEach(signFileUser -> {
            AxqContractVo vo = new AxqContractVo();
            vo.setSignAuthType(signAuthType);
            vo.setSignKeyword(signFileUser.getKeyWord());
            vo.setCfcaTemplateName(signFile.getFileName());
            vo.setBuyerCompanyName(signFileUser.getCompanyName());
            vo.setSignType(signFileUser.getSignType());
            vo.setFileId(signFile.getFileId());
            if (signFile.getFileId().endsWith(",")){
                vo.setFileId(signFile.getFileId().replaceAll(",",""));
            }
            vo.setGenerateShortUrlFlg(!autoSignFlg);
            vo.setPhoneNumber(signFileUser.getSignPhone());
            vo.setProjectCode("004");
            axqContractVoList.add(vo);
        });
        return axqContractVoList;
    }

    private AxqAutoSignVo convertAutoSign(SignFile signFile, List<SignFileUser> signFileUserList){
        AxqAutoSignVo axqAutoSignVo = new AxqAutoSignVo();
        axqAutoSignVo.setCfcaContractNo(signFile.getCfcaContractNo());
        List<AxqAutoSignVo.Signatorie> signatorieList = new ArrayList<>();
        signFileUserList.forEach(sign->{
            AxqAutoSignVo.Signatorie signatorie = new AxqAutoSignVo.Signatorie();
            signatorie.setCompanyName(sign.getCompanyName());
            signatorie.setKeyWord(sign.getKeyWord());
            signatorie.setSealType(sign.getSignType());
            signatorie.setImageHeight(sign.getImageHeight());
            signatorie.setImageWidth(sign.getImageWidth());
            signatorie.setOffsetCoordX(sign.getOffsetCoordX());
            signatorie.setOffsetCoordY(sign.getOffsetCoordY());

            signatorieList.add(signatorie);
        });

        axqAutoSignVo.setSignatorieList(signatorieList);
        return axqAutoSignVo;
    }

    @Override
    public SignFile findByAllLimit() {
        return signFileDao.findByAllLimit();
    }

    private Map<String, Object> setEmailParam(Map<String, Object> param, String approveNo, String signCompanyName) {
        if (StringUtils.isBlank(approveNo)) {
            return param;
        }
        PmApprove approve = pmApproveDao.findByApproveNo(approveNo);
        if (Objects.isNull(approve)) {
            return param;
        }
        PmProcess process = pmProcessDao.findOne(approve.getProcessId());
        if (Objects.isNull(process)) {
            return param;
        }
        PmApproveContents approveContents = pmApproveContentsDao.findByApproveId(approve.getId());
        if (Objects.isNull(approveContents)) {
            return param;
        }
        if (StringUtils.equals(BasConstants.PROCESS_APPLY_SEAL_USAGE_BUSINESS, process.getProcessCode())) {
            SealUsage sealUsage = JsonUtil.json2Object(SealUsage.class, approveContents.getContents());
            if (Objects.nonNull(sealUsage) && sealUsage.getContractNo().contains("S")) {
                param.put("type", "【下游】");
                param.put("companyNameLabel", "客户名称：");
                param.put("companyName", sealUsage.getCustomerName());
                return param;
            }
            if (Objects.nonNull(sealUsage) && sealUsage.getContractNo().contains("B")) {
                param.put("type", "【上游】");
                param.put("companyNameLabel", "客户名称：");
                param.put("companyName", sealUsage.getCustomerName());
                return param;
            }
        } else if (StringUtils.equals(BasConstants.APPLY_SEAL_USAGE_DCSX, process.getProcessCode())) {
            SealUsageDCSX sealUsage = JsonUtil.json2Object(SealUsageDCSX.class, approveContents.getContents());
            param.put("type", "【中游】");
            param.put("companyNameLabel", "代采购方：");
            String companyName = sealUsage.getCompanyName();
            String ourCompanyName = sealUsage.getOurCompanyName();
            param.put("companyName", StringUtils.equals(signCompanyName, companyName) ? ourCompanyName : companyName);
            return param;
        } else if (StringUtils.equals(BasConstants.APPLY_SEAL_USAGE_DCTP, process.getProcessCode())) {
            SealUsageDCSX sealUsage = JsonUtil.json2Object(SealUsageDCSX.class, approveContents.getContents());
            param.put("type", "【中游】");
            param.put("companyNameLabel", "代采购方：");
            String companyName = sealUsage.getCompanyName();
            String ourCompanyName = sealUsage.getOurCompanyName();
            param.put("companyName", StringUtils.equals(signCompanyName, companyName) ? ourCompanyName : companyName);
            return param;
        }
        return param;
    }

    private String  abbreviationCompanyName(String name) {
        if (StringUtils.isBlank(name)){
            return "";
        }
        name = name.replace("供应链管理有限公司","");
        name = name.replace("国际贸易有限公司","");
        name = name.replace("科技有限公司","");
        name = name.replace("贸易有限公司","");
        name = name.replace("供应链集团有限公司","");
        name = name.replace("科技股份有限公司","");
        name = name.replace("企业管理合伙企业（有限合伙）","");
        name = name.replace("有限公司","");
        return name;
    }
}
