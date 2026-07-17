package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyVip;
import com.spt.bas.client.entity.ApplyVipMainReceive;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.server.dao.ApplyVipDao;
import com.spt.bas.server.service.IApplyVipService;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.pm.annotation.ServerTransactional;
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
import java.util.Calendar;
import java.util.Date;



/**
 * VIP
 */
@Component("applyVipService")
@Transactional(readOnly = true)
public class ApplyVipServiceImpl extends BaseService<ApplyVip> implements IApplyVipService, IPmApproveListener {
    /**
     * 中光企业id(固定)
     */
    private static final Long ZG_ENTERPRISE_ID = BasConstants.ZG_ENTERPRISE_ID;

    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;

    @Autowired
    private ApplyVipDao applyVipDao;

    @Autowired
    private IBsCompanyService companyService;

    @Resource
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IPmProcessService iPmProcessService;

    @Autowired
    private PmApproveServiceImpl approveService;

    @Override
    public BaseDao<ApplyVip> getBaseDao() {
        return applyVipDao;
    }


    @Override
    @ServerTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            ApplyVip entity = JsonUtil.json2Object(ApplyVip.class, contents);
            entity.setApplyUserId(approve.getCreateUserId());
            entity.setApplyUserName(approve.getCreateUserName());
            entity.setApproveId(approve.getId());
            entity.setId(0L);
            Date date = new Date();
            entity.setStartDate(date);
            //计算到期日
            Integer vipLevel = entity.getVipLevel();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, vipLevel);
            Date time = calendar.getTime();
            entity.setEndDate(time);
            save(entity);
            BsCompany company = companyService.getEntity(entity.getCompanyId());
            company.setVipLevel(entity.getVipLevel());
            company.setRate(entity.getRate());
            company.setInterestRate(entity.getInterestRate());
            company.setVipStartDate(date);
            company.setVipEndDate(entity.getEndDate());
            company.setApplyVipStatus(BasConstants.APPLY_STATUS_APPLYING);
            doApplyVipReceiveTask(entity,BasConstants.PROCESS_APPLY_VIP_MAIN_RECEIVE,approve.getCreateUserId());

        }
    }

    @Override
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyVip entity = JsonUtil.json2Object(ApplyVip.class, contents);
        BsCompany company = companyService.getEntity(entity.getCompanyId());
        company.setApplyVipStatus(BasConstants.APPLY_STATUS_REJECT);
        companyService.save(company);
        IPmApproveListener.super.doStepBack(approve, nextStep);
    }

    @Override
    @ServerTransactional
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyVip entity = JsonUtil.json2Object(ApplyVip.class, contents);
        BsCompany company = companyService.getEntity(entity.getCompanyId());
        company.setApplyVipStatus(BasConstants.APPLY_STATUS_REJECT);
        companyService.save(company);

    }

    @Override
    @ServerTransactional
    public void doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyVip entity = JsonUtil.json2Object(ApplyVip.class, contents);
        BsCompany company = companyService.getEntity(entity.getCompanyId());
        company.setApplyVipStatus(BasConstants.APPROVE_STATUS_E);
        companyService.save(company);
        IPmApproveListener.super.doRetrieve(vo);
    }

    public void doApplyVipReceiveTask(ApplyVip applyPromoteVip, String processCode, Long createUserId) throws ApplicationException{

        ApplyVipMainReceive receive=new ApplyVipMainReceive();
        receive.setCompanyId(applyPromoteVip.getCompanyId());
        receive.setOurCompanyName("青岛中光亿云供应链管理有限公司");
        receive.setCompanyName(applyPromoteVip.getCompanyName());
        //全款
        receive.setReceiveType("A");
        //电汇
        receive.setReceiveMode("T");
        receive.setReceiveDate(new Date());
        receive.setReceiveAmount(applyPromoteVip.getVipAmount());
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
