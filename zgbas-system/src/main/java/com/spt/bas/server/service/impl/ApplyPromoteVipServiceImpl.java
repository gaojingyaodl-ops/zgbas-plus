package com.spt.bas.server.service.impl;


import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyPromoteVip;
import com.spt.bas.client.entity.ApplyVipReceive;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.server.dao.ApplyPromoteVipDao;
import com.spt.bas.server.service.IApplyPromoteVipService;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.service.impl.PmApproveServiceImpl;
import com.spt.pm.vo.*;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * VIP提额
 */
@Component("applyPromoteVipService")
@Transactional(readOnly = true)
public class ApplyPromoteVipServiceImpl extends BaseService<ApplyPromoteVip> implements IApplyPromoteVipService, IPmApproveListener {

    @Autowired
    private ApplyPromoteVipDao applyPromoteVipDao;

    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;


    @Resource
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IPmProcessService iPmProcessService;

    @Autowired
    private PmApproveServiceImpl approveService;


    @Autowired
    private IBsCompanyService companyService;
    /**
     * 中光企业id(固定)
     */
    private static final Long ZG_ENTERPRISE_ID = BasConstants.ZG_ENTERPRISE_ID;


    @Override
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyPromoteVip entity = JsonUtil.json2Object(ApplyPromoteVip.class, contents);
        entity.setStatus(BasConstants.APPROVE_STATUS_E);
        BsCompany company = companyService.getEntity(entity.getCompanyId());
        company.setPromoteVipApplyStatus(BasConstants.APPLY_STATUS_REJECT);
        save(entity);
        IPmApproveListener.super.doStepBack(approve, nextStep);
    }

    @Override
    public void doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyPromoteVip entity = JsonUtil.json2Object(ApplyPromoteVip.class, contents);
        entity.setStatus(BasConstants.APPROVE_STATUS_E);
        BsCompany company = companyService.getEntity(entity.getCompanyId());
        company.setPromoteVipApplyStatus(BasConstants.APPLY_STATUS_REJECT);
        save(entity);
        IPmApproveListener.super.doRetrieve(vo);
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            ApplyPromoteVip entity = JsonUtil.json2Object(ApplyPromoteVip.class, contents);
            entity.setStatus(BasConstants.APPROVE_STATUS_A);
            BsCompany company = companyService.getEntity(entity.getCompanyId());
            company.setPromoteVipApplyStatus(BasConstants.APPLY_STATUS_APPLYING);
            entity.setId(0L);
            save(entity);
            doApplyVipReceiveTask(entity, BasConstants.PROCESS_APPLY_VIP_RECEIVE, approve.getCreateUserId());
        }
    }
    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyPromoteVip entity = JsonUtil.json2Object(ApplyPromoteVip.class, contents);
        entity.setStatus(BasConstants.APPROVE_STATUS_E);
        BsCompany company = companyService.getEntity(entity.getCompanyId());
        company.setPromoteVipApplyStatus(BasConstants.APPLY_STATUS_REJECT);
        save(entity);
    }
    @Override
    public BaseDao<ApplyPromoteVip> getBaseDao() {
        return applyPromoteVipDao;
    }

    //发起收款审批
    @Override
    public void doApplyVipReceiveTask(ApplyPromoteVip applyPromoteVip,String processCode,Long createUserId) throws ApplicationException{

        ApplyVipReceive receive=new ApplyVipReceive();
        receive.setCompanyId(applyPromoteVip.getCompanyId());
        receive.setOurCompanyName("青岛中光亿云供应链管理有限公司");
        receive.setCompanyName(applyPromoteVip.getCompanyName());
        receive.setPromoteCreditAmount(applyPromoteVip.getPromoteCreditAmount());
        //全款
        receive.setReceiveType("A");
        //电汇
        receive.setReceiveMode("T");
        receive.setReceiveDate(new Date());
        receive.setReceiveAmount(applyPromoteVip.getPromoteVipAmout());
        String bizEntityJson = JsonUtil.obj2Json(receive);
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setEnterpriseId(ZG_ENTERPRISE_ID);
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ZG_ENTERPRISE_ID);
        searchVo.setProcessCode(processCode);
        PmProcess process = iPmProcessService.findByProcessCode(searchVo);
        if (process == null) {
            throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
        }
        SysUserSdk userById = authOpenFacade.findUserById(createUserId);
        startVo.setUserId(userById.getUserId());
        startVo.setUserName(userById.getNickName());
        startVo.setProcessId(process.getId());
        startVo.setApproveId(0L);
        startVo.setBizEntityJson(bizEntityJson);
        approveService.startFlow(startVo);

    }


}
