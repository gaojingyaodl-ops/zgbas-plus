package com.spt.bas.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.common.ApiResult;
import com.spt.bas.client.constant.ApplySource;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyEntrust;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyQuota;
import com.spt.bas.client.entity.BsEntrust;
import com.spt.bas.client.vo.CmsResultVo;
import com.spt.bas.purchase.wx.client.constant.SaveInfoType;
import com.spt.bas.purchase.wx.client.entity.SaveInfo;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.remote.ISaveTempClient;
import com.spt.bas.purchase.wx.client.remote.IWxUserDetailClient;
import com.spt.bas.purchase.wx.client.vo.EntrustVo;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.dao.ApplyEntrustDao;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.dao.BsEntrustDao;
import com.spt.bas.server.service.IApplyEntrustService;
import com.spt.bas.server.service.IBsCompanyQuotaService;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.bas.server.util.SMSUtils;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.sign.client.entity.EnterpriseAccount;
import com.spt.sign.client.entity.SignTransactor;
import com.spt.sign.client.remote.IEnterpriseAccountClient;
import com.spt.sign.client.remote.ISignTransactorClient;
import com.spt.tools.core.bean.RespVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *     委托授权审批
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-10 14:15
 */
@Component("applyEntrustService")
@Transactional(readOnly = true)
public class ApplyEntrustServiceImpl extends BaseService<ApplyEntrust> implements IApplyEntrustService, IPmApproveListener {
    private ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-communication-entrust-runner-%d").build();
    private ExecutorService executorService = new ThreadPoolExecutor(10, 20, 200L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), namedThreadFactory);


    @Autowired
    private ApplyEntrustDao applyEntrustDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private IBsCompanyQuotaService bsCompanyQuotaService;
    @Autowired
    private IWxUserDetailClient userDetailDao;
    @Autowired
    private IPmProcessService pmProcessService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private ISaveTempClient saveTempClient;
    @Autowired
    private BsCompanyDao bsCompanyDao;
    @Autowired
    private BsEntrustDao bsEntrustDao;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private ISignTransactorClient signTransactorClient;
    @Autowired
    private IEnterpriseAccountClient enterpriseAccountClient;

    @Value("${cms.server.url}")
    private String cmsServerUrl;

    /**
     * 发起审批
     *
     * @param approve
     */
    @Override
    public void doStepIn(PmApprove approve) throws ApplicationException {

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
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            ApplyEntrust entity = JsonUtil.json2Object(ApplyEntrust.class, contents);
            entity.setApplyUserId(approve.getCreateUserId());
            entity.setApplyUserName(approve.getCreateUserName());
            entity.setApproveId(approve.getId());
            entity.setId(0L);
            entity.setStatus(BasConstants.APPROVE_STATUS_D);
            entity.setFileId(pmApproveContents.getFileId());
            logger.info("doStepFlow - " + JsonUtil.obj2Json(entity));
            save(entity);

            String applySource = entity.getApplySource();
            if(!StringUtils.equals(ApplySource.CMS.getCode(),applySource)) {
                // 修改userDetails状态
                UserDetail userDetail = userDetailDao.findByUserId(entity.getWxUserId());
                userDetail.setIsBind(true);
                userDetail.setEntrustApplyStatus(BasConstants.APPLY_STATUS_COMPLETE);
                userDetailDao.save(userDetail);

                // 审核完成后保存临时信息
                saveTempClient.commitTempInfo(SaveInfoType.ENTRUST.getType(), userDetail.getUserId());

                // 同步委托授权信息到bsEntrust表
                BsEntrust bsEntrust = syncEntrustInfo(userDetail);

                // 同步完成线上化标识
                Long companyId = userDetail.getCompanyId();
                BsCompany company = bsCompanyService.getEntity(companyId);
                company.setOnLineFlg(true);
                bsCompanyService.save(company);
//            // 判断入金测试是否完成 且准入 如果完成发起额度审批
//            if (BasConstants.APPLY_STATUS_COMPLETE.equals(userDetail.getDepositStatus())
//                    && BasConstants.APPLY_STATUS_COMPLETE.equals(userDetail.getApplyIouStatus())) {
//                    startQuota(entity, userDetail);
//            }


                //发送 邮件通知开户
                String value = BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.CREATE_ACCOUNT, BasConstants.CREATE_ACCOUNT_USER);
                SMSUtils.sendEmailCreateAccount(company.getCompanyName(),value);


                // 执行自动开通经办人逻辑
                pushTransactorToSign(entity, bsEntrust);
            } else {
                // 调用浙塑网站（cms）接口返回审批状态
                String status = approve.getStatus();
                String companyName = entity.getCompanyName();
                CmsResultVo cmsResultVo = new CmsResultVo(status, companyName);
                cmsDoPost(cmsResultVo);
            }

        }
    }

    /**
     * 同步委托授权信息到bsEntrust表
     * @param userDetail
     */
    private BsEntrust syncEntrustInfo(UserDetail userDetail) {
        if (userDetail != null) {
            SaveInfo infoByType = saveTempClient.getInfoByType(userDetail.getUserId(), SaveInfoType.ENTRUST.getType());
            if (infoByType.getContent() != null) {
                EntrustVo entrustVo = JsonUtil.json2Object(EntrustVo.class, infoByType.getContent());
                BsEntrust bsEntrust = new BsEntrust();
                BeanUtils.copyProperties(entrustVo, bsEntrust);
                bsEntrust.setCompanyId(userDetail.getCompanyId());
                bsEntrust.setWxUserId(userDetail.getUserId());
                bsEntrust.setEnableFlg(true);
                BsEntrust entrust = bsEntrustDao.save(bsEntrust);
                infoByType.setCompanyId(userDetail.getCompanyId());
                saveTempClient.save(infoByType);
                return entrust;
            }
        }
        return null;
    }

    /**
     * 发起额度审批
     * @param entity
     * @param userDetail
     * @throws ApplicationException
     */
    private void startQuota(ApplyEntrust entity,UserDetail userDetail) throws ApplicationException {
        // 发起额度审批
        BsCompanyQuota bsCompanyQuota = new BsCompanyQuota();
        bsCompanyQuota.setCompanyId(entity.getCompanyId());
        bsCompanyQuota.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        bsCompanyQuota.setTotalCreditAmount(userDetail.getTotalCreditAmount());
        bsCompanyQuota.setTotalFuturesAmount(userDetail.getTotalFuturesAmount());
        bsCompanyQuota.setTotalSpotAmount(userDetail.getTotalSpotAmount());
        bsCompanyQuota.setApproveId(0L);
        bsCompanyQuota.setCompanyName(entity.getCompanyName());
        bsCompanyQuota.setWxUserId(userDetail.getUserId());
        bsCompanyQuota.setApplySource(ApplySource.PURCHASE.getCode());
        bsCompanyQuotaService.startFlow(JsonUtil.obj2Json(bsCompanyQuota), entity.getCompanyId());
    }


    /**
     * 审批驳回
     *
     * @param approve
     * @param nextStep
     */
    @Override
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyEntrust entity = JsonUtil.json2Object(ApplyEntrust.class, contents);
        String applySource = entity.getApplySource();
        if(!StringUtils.equals(ApplySource.CMS.getCode(),applySource)) {
            UserDetail userDetail = userDetailDao.findByUserId(entity.getWxUserId());
            userDetail.setIsBind(true);
            userDetail.setEntrustApplyStatus(BasConstants.APPLY_STATUS_REJECT);
            userDetailDao.save(userDetail);
        } else {
            // 调用浙塑网站（cms）接口返回审批状态
            String status = approve.getStatus();
            String companyName = entity.getCompanyName();
            CmsResultVo cmsResultVo = new CmsResultVo(status, companyName);
            cmsDoPost(cmsResultVo);

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
    public BaseDao<ApplyEntrust> getBaseDao() {
        return applyEntrustDao;
    }

    @Override
    public void startFlow(String bizEntityJson,Long companyId) throws ApplicationException {
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        BsCompany company = bsCompanyDao.findOne(companyId);
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        searchVo.setProcessCode("APPLY_ENTRUST");
        PmProcess process = pmProcessService.findByProcessCode(searchVo);
        SysUserSdk userById = authOpenFacade.findUserById(company.getMatchUserId());
        startVo.setUserId(userById.getUserId());
        startVo.setUserName(userById.getNickName());
        startVo.setProcessId(process.getId());
        startVo.setApproveId(0L);
        startVo.setBizEntityJson(bizEntityJson);
        pmApproveService.startFlow(startVo);
    }

    /**
     * 推送委托授权信息至签章系统-执行自动开通经办人
     *
     * @param entity
     */
    private void pushTransactorToSign(ApplyEntrust entity, BsEntrust bsEntrust) {
        if (Objects.isNull(entity) || Objects.isNull(entity.getWxUserId())) {
            return;
        }
        logger.info("pushTransactorToSign wxUserId:{}", entity.getWxUserId());
        executorService.execute(() -> {
            EnterpriseAccount vo = new EnterpriseAccount();
            vo.setEnterpriseName(entity.getCompanyName());
            EnterpriseAccount enterpriseAccount = enterpriseAccountClient.getEnterpriseAccountEntity(vo);
            if (Objects.isNull(enterpriseAccount) || !StringUtils.equals("60000000", enterpriseAccount.getRetCode())) {
                return;
            }
            SignTransactor signTransactor = new SignTransactor();
            signTransactor.setTransactorName(bsEntrust.getTrusteeName());
            signTransactor.setIdentNo(bsEntrust.getIdentityCardNumber());
            signTransactor.setMobilePhone(bsEntrust.getTrusteePhone());
            signTransactor.setIdentTypeCode("0");
            signTransactor.setUserId(enterpriseAccount.getUserId());
            RespVo<?> respVo = signTransactorClient.receiveTransactorSign(signTransactor);
            logger.info("pushTransactorToSign result:{}", JsonUtil.obj2Json(respVo));
        });
    }
    /**
     * 根据公司名称查询是否用经办人
     * 不能根据companyId 查询,历史数据中没有保存companyId
     *
     * @param companyName 公司名词
     * @return true-已经绑定过，false-没有绑定过
     */
    @Override
    public Boolean findIsHaveEntrustUserByCompanyName(String companyName) {
        if(StringUtils.isBlank(companyName)){
            return false;
        }
        List<ApplyEntrust> list = applyEntrustDao.findIsHaveEntrustUserByCompanyName(companyName);
        // 直接判断是否为空即可，不为空，说明之前有人申请过了
        return CollectionUtils.isNotEmpty(list);
    }

    /**
     * 调用浙塑网站回调接口
     * @throws ApplicationException
     */
    public void cmsDoPost(Object obj) throws ApplicationException {
        ApiResult apiResult = new ApiResult();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(cmsServerUrl+BasConstants.CMS_ENTRUST_CALLBACK_URL);
            httpPost.setHeader("Content-Type", "application/json");
            String jsonInputString = JSONObject.toJSONString(obj);
            StringEntity entity = new StringEntity(jsonInputString, Charset.forName("UTF-8"));
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                String responseString = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                ObjectMapper objectMapper = new ObjectMapper();
                apiResult = objectMapper.readValue(responseString, ApiResult.class);
            } else {
                apiResult.setResultCode(500);
                apiResult.setMessage("审批失败，cms接口调用失败!");
            }
            
            if(apiResult.getResultCode().equals(500)){
                throw new ApplicationException("审批失败! " + apiResult.getMessage());    
            }
        } catch (Exception e) {
            logger.error("接口调用失败",e);
            throw new ApplicationException("审批失败，cms接口调用失败!");
        }

    }
}
