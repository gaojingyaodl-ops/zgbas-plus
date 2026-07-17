package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyVipInvoice;
import com.spt.bas.client.entity.ApplyVipReceive;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyAccount;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.server.dao.ApplyVipReceiveDao;
import com.spt.bas.server.dao.BsCompanyAccountDao;
import com.spt.bas.server.service.IApplyVipReceiveService;
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
import java.util.Date;
import java.util.List;

/**
 * vip提额差价申请
 *
 * @author old
 */
@Component("applyVipReceiveService")
@Transactional(readOnly = true)
public class ApplyVipReceiveServiceImpl extends BaseService<ApplyVipReceive> implements IApplyVipReceiveService, IPmApproveListener,Runnable  {


@Resource
    private  ApplyVipReceiveDao receiveDao;



    @Autowired
    private BsCompanyAccountDao bsCompanyAccountDao;

    @Autowired
    private IBsCompanyService companyService;

    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;



    @Resource
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IPmProcessService iPmProcessService;

    @Autowired
    private PmApproveServiceImpl approveService;




    /**
     * 中光企业id(固定)
     */
    private static final Long ZG_ENTERPRISE_ID = BasConstants.ZG_ENTERPRISE_ID;

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents p = pmApproveContentsDao.findOne(approve.getBizId());
            String content = p.getContents();
            ApplyVipReceive applyVipReceive = JsonUtil.json2Object(ApplyVipReceive.class, content);
            applyVipReceive.setStatus(BasConstants.APPROVE_STATUS_D);
            applyVipReceive.setId(0L);
            save(applyVipReceive);
            //收款完成 修改公司表赊销金额
            BsCompany company = companyService.getEntity(applyVipReceive.getCompanyId());
            company.setTotalCreditAmount(applyVipReceive.getPromoteCreditAmount());
            company.setPromoteVipApplyStatus(BasConstants.APPLY_STATUS_COMPLETE);
            companyService.save(company);

            //收款成功发起vip收款
            doapplyVipInvoiceService(applyVipReceive.getCompanyId(), applyVipReceive, BasConstants.PROCESS_APPLY_VIP_INVOICE, approve.getCreateUserId());
        }
    }

    @Override
    @ServerTransactional
    public void doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {
        PmApproveContents p = pmApproveContentsDao.findOne(vo.getBizId());
        String content = p.getContents();
        ApplyVipReceive applyVipReceive= JsonUtil.json2Object(ApplyVipReceive.class, content);
        BsCompany company = companyService.getEntity(applyVipReceive.getCompanyId());
        company.setPromoteVipApplyStatus(BasConstants.APPLY_STATUS_REJECT);
        IPmApproveListener.super.doRetrieve(vo);
    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        PmApproveContents p = pmApproveContentsDao.findOne(approve.getBizId());
        String content = p.getContents();
        ApplyVipReceive applyVipReceive= JsonUtil.json2Object(ApplyVipReceive.class, content);
        BsCompany company = companyService.getEntity(applyVipReceive.getCompanyId());
        company.setPromoteVipApplyStatus(BasConstants.APPLY_STATUS_REJECT);
        IPmApproveListener.super.doStepBack(approve, nextStep);
    }


    private void doapplyVipInvoiceService(Long companyid,ApplyVipReceive applyVipReceive,String processCode,Long createUserId)  throws ApplicationException{
        List<BsCompanyAccount> list = bsCompanyAccountDao.findByCompanyId(companyid);
        ApplyVipInvoice applyVipInvoice=new ApplyVipInvoice();
        if (list.size()!=0){
            BsCompanyAccount bsCompanyAccount = list.get(0);
            applyVipInvoice.setBankName(bsCompanyAccount.getBankName());
            applyVipInvoice.setBankAccount(bsCompanyAccount.getBankAccount());
            applyVipInvoice.setAddress(bsCompanyAccount.getContactAddress());
            applyVipInvoice.setTaxNo(bsCompanyAccount.getTaxNo());
            applyVipInvoice.setCompanyPhone(bsCompanyAccount.getContactPhone());
        }

        applyVipInvoice.setCompanyId(applyVipReceive.getCompanyId());
        applyVipInvoice.setOurCompanyName("青岛中光亿云供应链管理有限公司");
        applyVipInvoice.setCompanyName(applyVipReceive.getCompanyName());
        applyVipInvoice.setTotalAmount(applyVipReceive.getReceiveAmount());
        applyVipInvoice.setCompanyId(applyVipReceive.getCompanyId());
        applyVipInvoice.setReceiveAmount(applyVipReceive.getReceiveAmount());
        applyVipInvoice.setBilledAmount(applyVipReceive.getReceiveAmount());
        applyVipInvoice.setDealAmount(applyVipReceive.getReceiveAmount());
        applyVipInvoice.setInvoiceDate(new Date());


        String bizEntityJson = JsonUtil.obj2Json(applyVipInvoice);

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

    @Override
    @ServerTransactional
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        PmApproveContents p = pmApproveContentsDao.findOne(vo.getBizId());
        String content = p.getContents();
        ApplyVipReceive applyVipReceive= JsonUtil.json2Object(ApplyVipReceive.class, content);
        BsCompany company = companyService.getEntity(applyVipReceive.getCompanyId());
        company.setPromoteVipApplyStatus(BasConstants.APPLY_STATUS_REJECT);
    }

    @Override
    public BaseDao<ApplyVipReceive> getBaseDao() {
        return receiveDao;
    }


    @Override
    public void run() {

    }
}
