package com.spt.bas.purchase.wx.server.service.impl;

import cn.hutool.json.JSONUtil;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.ApplySource;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IPmApproveClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.client.vo.ApplyConfirmReceiptVo;
import com.spt.bas.client.vo.ApplyDeliveryOutVo;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.entity.WebApplicationMsg;
import com.spt.bas.purchase.wx.server.common.BaseException;
import com.spt.bas.purchase.wx.server.common.Status;
import com.spt.bas.purchase.wx.server.dao.BsCompanyDao;
import com.spt.bas.purchase.wx.server.dao.UserDetailDao;
import com.spt.bas.purchase.wx.server.service.IApplyService;
import com.spt.bas.purchase.wx.server.util.UserHelper;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.core.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 审批 发起审批服务
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-06 22:44
 */
@Component
@Slf4j
public class ApplyServiceImpl implements IApplyService {
    @Autowired
    private IPmProcessClient processClient;
    @Autowired
    private IPmApproveClient approveClient;
    @Autowired
    private UserDetailDao userDetailDao;
    @Autowired
    private BsCompanyDao companyDao;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    /**
     * 中光企业id(固定)
     */
    private static final Long ZG_ENTERPRISE_ID = BasConstants.ZG_ENTERPRISE_ID;

    /**
     * 发起审批
     */
    private void startFlow(String bizEntityJson, String processCode) throws ApplicationException,WebApplicationException {
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setEnterpriseId(ZG_ENTERPRISE_ID);

        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ZG_ENTERPRISE_ID);
        searchVo.setProcessCode(processCode);
        PmProcess process = processClient.findByProcessCode(searchVo);
        if (process == null) {
            throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
        }
        UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(UserHelper.getCurUserId());
        BsCompany company = companyDao.findByIdAndEnableFlgTrue(userDetail.getCompanyId());
        if (company == null || company.getMatchUserId() == null) {
            throw new BaseException(Status.ERROR, "该绑定企业没有业务员，无法发起请求");
        }
        SysUserSdk userById = authOpenFacade.findUserById(company.getMatchUserId());
        startVo.setUserId(userById.getUserId());
        startVo.setDeptId(userById.getDeptId());
        startVo.setUserName(userById.getNickName());
        startVo.setProcessId(process.getId());
        startVo.setApproveId(0L);
        startVo.setBizEntityJson(bizEntityJson);
        approveClient.startFlow(startVo);
    }

    /**
     * 发起审批
     */
    private void startFlowCms(ApplyEntrust entrust, String processCode) throws ApplicationException,WebApplicationException {
        String bizEntityJson = JsonUtil.obj2Json(entrust);
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setEnterpriseId(ZG_ENTERPRISE_ID);

        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ZG_ENTERPRISE_ID);
        searchVo.setProcessCode(processCode);
        PmProcess process = processClient.findByProcessCode(searchVo);
        if (process == null) {
            throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
        }
       
        startVo.setUserId(entrust.getApplyUserId());
        startVo.setDeptId(entrust.getDeptId());
        startVo.setUserName(entrust.getApplyUserName());
        startVo.setProcessId(process.getId());
        startVo.setApproveId(0L);
        startVo.setBizEntityJson(bizEntityJson);
        approveClient.startFlow(startVo);
    }

    /**
     * 发起审批
     */
    private void startFlowAndFinish(String bizEntityJson, Long contractMathUserId) throws ApplicationException,WebApplicationException {
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_D);
        startVo.setEnterpriseId(ZG_ENTERPRISE_ID);

        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ZG_ENTERPRISE_ID);
        searchVo.setProcessCode(BasConstants.PROCESS_APPLY_CONFIRM_RECEIPT);
        PmProcess process = processClient.findByProcessCode(searchVo);
        if (process == null) {
            throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
        }
        SysUserSdk userById = authOpenFacade.findUserById(contractMathUserId);
        startVo.setUserId(userById.getUserId());
        startVo.setDeptId(userById.getDeptId());
        startVo.setUserName(userById.getNickName());
        startVo.setProcessId(process.getId());
        startVo.setApproveId(0L);
        startVo.setAutoStartMessage("客户小程序确认收货，自动发起");
        startVo.setBizEntityJson(bizEntityJson);
        startVo.setAutoStartFlg(true);
        startVo.setAutoStartFlgReal(true);
        approveClient.startFlow(startVo);
    }

    /**
     * 入金验证申请
     *
     * @param deposit
     */
    @Override
    public void applyDeposit(ApplyDeposit deposit) {
        try {
            deposit.setApplySource(ApplySource.PURCHASE.getCode());
            startFlow(JsonUtil.obj2Json(deposit), BasConstants.PROCESS_APPLY_DEPOSIT);
        } catch (Exception e) {
            log.error("applyDeposit,入金验证申请发起错误；", e);
            throw new BaseException(Status.APPLY_FAIL);
        }
    }

    @Override
    public void applyAdmittance(BsCompanyAllowed bsCompanyAllowed) {
        try {
            bsCompanyAllowed.setApplySource(ApplySource.PURCHASE.getCode());
            startFlow(JsonUtil.obj2Json(bsCompanyAllowed), BasConstants.PROCESS_APPLY_CPN_ALLOWED);
        } catch (Exception e) {
            log.error("applyAdmittance,准入申请发起错误；", e);
            throw new BaseException(Status.APPLY_FAIL);
        }
    }

    /**
     * 企业资料审核
     *
     * @param applyCompanyInfo
     */
    @Override
    public void applyCompanyInfo(ApplyCompanyInfo applyCompanyInfo) {
        try {
            applyCompanyInfo.setApplySource(ApplySource.PURCHASE.getCode());
            startFlow(JsonUtil.obj2Json(applyCompanyInfo), BasConstants.PROCESS_APPLY_COMPANY_INFO);
        } catch (Exception e) {
            log.error("applyCompanyInfo,公司信息审批申请发起错误；", e);
            throw new BaseException(Status.APPLY_FAIL);
        }
    }

    /**
     * 委托授权审核
     *
     * @param entrust
     */
    @Override
    public void applyEntrust(ApplyEntrust entrust) {
        try {
            entrust.setApplySource(ApplySource.PURCHASE.getCode());
            startFlow(JsonUtil.obj2Json(entrust), BasConstants.PROCESS_APPLY_ENTRUST);
        } catch (Exception e) {
            log.error("entrust,委托授权审批申请发起错误；", e);
            throw new BaseException(Status.APPLY_FAIL);
        }
    }
    
    /**
     * 委托授权审核
     *
     * @param entrust
     */
    @Override
    public void applyEntrustCms(ApplyEntrust entrust) {
        try {
            startFlowCms(entrust, BasConstants.PROCESS_APPLY_ENTRUST);
        } catch (Exception e) {
            log.error("entrust,委托授权审批申请发起错误；", e);
            throw new BaseException(Status.APPLY_FAIL);
        }
    }

    /**
     * 发起意见反馈审批
     *
     * @param feedback
     */
    @Override
    public void applyFeedback(Feedback feedback) {
        try {
            feedback.setApplySource(ApplySource.PURCHASE.getCode());
            startFlow(JsonUtil.obj2Json(feedback), BasConstants.PROCESS_APPLY_FEEDBACK);
        } catch (Exception e) {
            log.error("feedback,意见反馈审批申请发起错误；", e);
            throw new BaseException(Status.APPLY_FAIL);
        }
    }

    /**
     * 发起申请成为合伙人审批
     *
     * @param partner
     */
    @Override
    public void applyPartner(ApplyPartner partner) {
        try {
            UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(UserHelper.getCurUserId());
            partner.setCompanyId(userDetail.getCompanyId());
            partner.setApplySource(ApplySource.PURCHASE.getCode());
            startFlow(JsonUtil.obj2Json(partner), BasConstants.PROCESS_APPLY_PARTNER);
        } catch (Exception e) {
            log.error("partner,合伙人申请审批申请发起错误；", e);
            throw new BaseException(Status.APPLY_FAIL);
        }
    }

    /**
     * 发起cfca审批
     *
     * @param applyCfca
     */
    @Override
    public void applyCfca(ApplyCfca applyCfca) {
        try {
            UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(UserHelper.getCurUserId());
            applyCfca.setCompanyId(userDetail.getCompanyId());
            BsCompany company = companyDao.findByIdAndEnableFlgTrue(userDetail.getCompanyId());
            applyCfca.setCompanyName(company.getCompanyName());
            applyCfca.setWxUserId(UserHelper.getCurUserId());
            applyCfca.setApproveId(0L);
            applyCfca.setEnterpriseId(ZG_ENTERPRISE_ID);
            applyCfca.setApplySource(ApplySource.PURCHASE.getCode());
            startFlow(JsonUtil.obj2Json(applyCfca), BasConstants.PROCESS_APPLY_CFCA);
        } catch (Exception e) {
            log.error("applyCfca,cfca审批申请发起错误；", e);
            throw new BaseException(Status.APPLY_FAIL);
        }
    }

    /**
     * 发起确认收货审批
     *
     * @param applyConfirmReceiptVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyConfirmReceipt(ApplyConfirmReceiptVo applyConfirmReceiptVo) {
        try {
            applyConfirmReceiptVo.setWxUserId(UserHelper.getCurUserId());
            applyConfirmReceiptVo.setApproveId(0L);
            applyConfirmReceiptVo.setEnterpriseId(ZG_ENTERPRISE_ID);
            applyConfirmReceiptVo.setApplySource(ApplySource.PURCHASE.getCode());
            startFlow(JsonUtil.obj2Json(applyConfirmReceiptVo), BasConstants.PROCESS_APPLY_CONFIRM_RECEIPT);
        } catch (Exception e) {
            log.error("applyConfirmReceipt批申请发起错误；", e);
            throw new BaseException(Status.APPLY_FAIL);
        }
    }

    @Override
    @Transactional
    public void applyConfirmReceiptAndFinish(ApplyConfirmReceiptVo applyConfirmReceiptVo) {
        try {
//            applyConfirmReceiptVo.setWxUserId(UserHelper.getCurUserId());
            applyConfirmReceiptVo.setApproveId(0L);
            applyConfirmReceiptVo.setEnterpriseId(ZG_ENTERPRISE_ID);
            applyConfirmReceiptVo.setApplySource(ApplySource.PURCHASE.getCode());
            startFlowAndFinish(JsonUtil.obj2Json(applyConfirmReceiptVo), applyConfirmReceiptVo.getContractMatchUserId());
        } catch (Exception e) {
            log.error("applyConfirmReceipt批申请发起错误；", e);
            throw new BaseException(Status.APPLY_FAIL);
        }
    }

    /**
     * 发起出库审批
     *
     * @param applyDeliveryOutVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyDeliveryOut(ApplyDeliveryOutVo applyDeliveryOutVo) {
        try {
            applyDeliveryOutVo.setWxUserId(UserHelper.getCurUserId());
            applyDeliveryOutVo.setApproveId(0L);
            applyDeliveryOutVo.setEnterpriseId(ZG_ENTERPRISE_ID);
            applyDeliveryOutVo.setApplySource(ApplySource.PURCHASE.getCode());
            startFlow(JsonUtil.obj2Json(applyDeliveryOutVo), BasConstants.PROCESS_APPLY_DELIVERYOUT);
        } catch (Exception e) {
            log.error("applyDeliveryOut批申请发起错误；", e);
            throw new BaseException(Status.APPLY_FAIL);
        }
    }

    /**
     * 收货款申请
     *
     * @param applyReceive
     */
    @Override
    public void confirmPay(ApplyReceive applyReceive) {
        try {
            applyReceive.setWxUserId(UserHelper.getCurUserId());
            applyReceive.setApproveId(0L);
            applyReceive.setEnterpriseId(ZG_ENTERPRISE_ID);
            applyReceive.setApplySource(ApplySource.PURCHASE.getCode());
            startFlow(JsonUtil.obj2Json(applyReceive), BasConstants.PROCESS_APPLY_RECEIVE);
        }catch (WebApplicationException ex){
            // 有具体返回错误
            log.error("confirmPay批申请发起错误；", ex);
            throw new BaseException(Status.APPLY_FAIL.getCode(), JSONUtil.toBean(ex.getMessage(), WebApplicationMsg.class).getMessage());
        } catch (Exception e) {
            log.error("confirmPay批申请发起错误；", e);
            throw new BaseException(Status.APPLY_FAIL);
        }
    }

    /**
     * 支付服务费申请
     *
     * @param applyServiceReceive
     */
    @Override
    public void confirmServicePay(ApplyServiceReceive applyServiceReceive) {
        try {
            applyServiceReceive.setWxUserId(UserHelper.getCurUserId());
            applyServiceReceive.setApproveId(0L);
            applyServiceReceive.setEnterpriseId(ZG_ENTERPRISE_ID);
            applyServiceReceive.setApplySource(ApplySource.PURCHASE.getCode());
            startFlow(JsonUtil.obj2Json(applyServiceReceive), BasConstants.PROCESS_SERVICE_RECE);
        }catch (WebApplicationException ex){
            // 有具体返回错误
            log.error("confirmServicePay批申请发起错误；", ex);
            throw new BaseException(Status.APPLY_FAIL.getCode(), JSONUtil.toBean(ex.getMessage(), WebApplicationMsg.class).getMessage());
        } catch (Exception e) {
            log.error("confirmServicePay批申请发起错误；", e);
            throw new BaseException(Status.APPLY_FAIL);
        }
    }

    /**
     * 申请开票
     *
     * @param applyInvoice
     */
    @Override
    public void applyBill(ApplyInvoice applyInvoice) {
        try {
            applyInvoice.setWxUserId(UserHelper.getCurUserId());
            applyInvoice.setApproveId(0L);
            applyInvoice.setEnterpriseId(ZG_ENTERPRISE_ID);
            applyInvoice.setApplySource(ApplySource.PURCHASE.getCode());
            startFlow(JsonUtil.obj2Json(applyInvoice), BasConstants.PROCESS_APPLY_INVOICE);
        }catch (WebApplicationException ex){
            // 有具体返回错误
            log.error("applyBill批申请发起错误；", ex);
            throw new BaseException(Status.APPLY_FAIL.getCode(), JSONUtil.toBean(ex.getMessage(), WebApplicationMsg.class).getMessage());
        } catch (Exception e) {
            log.error("applyBill批申请发起错误；", e);
            throw new BaseException(Status.APPLY_FAIL);
        }
    }


    /**
     * 上传资料合并
     * @param
     */
    @Override
    public void ApplyWxCfca(ApplyWxCfca applyWxCfca) {
        try {
            applyWxCfca.setApplySource(ApplySource.PURCHASE.getCode());
            UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrue(UserHelper.getCurUserId());
            applyWxCfca.setCompanyId(userDetail.getCompanyId());
            BsCompany company = companyDao.findByIdAndEnableFlgTrue(userDetail.getCompanyId());
            applyWxCfca.setCompanyName(company.getCompanyName());
            applyWxCfca.setWxUserId(UserHelper.getCurUserId());
            applyWxCfca.setApproveId(0L);
            applyWxCfca.setEnterpriseId(ZG_ENTERPRISE_ID);
            applyWxCfca.setApplySource(ApplySource.PURCHASE.getCode());
            startFlow(JsonUtil.obj2Json(applyWxCfca), BasConstants.ONLINE_ACCOUNT_OPENING);
        } catch (Exception e) {
            log.error("applyCompanyInfo,合并流程错误；", e);
            throw new BaseException(Status.APPLY_FAIL);
        }
    }


}
