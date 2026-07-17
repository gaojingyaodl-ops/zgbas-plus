package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.server.dao.ApplyVipDao;
import com.spt.bas.server.dao.ApplyVipMainReceiveDao;
import com.spt.bas.server.dao.BsCompanyAccountDao;
import com.spt.bas.server.service.IApplyVipMainReceiveService;
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
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 *
 * @author old
 */
@Component("applyVipMainReceiveService")
@Transactional(readOnly = true)
public class ApplyVipMainReceiveServiceImpl extends BaseService<ApplyVipMainReceive> implements IApplyVipMainReceiveService, IPmApproveListener  {

    @Resource
    private ApplyVipMainReceiveDao applyVipMainReceiveDao;
    @Resource
    private  ApplyVipDao applyVipDao;

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

    @Autowired
    private IApplyVipService applyVipService;
    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents p = pmApproveContentsDao.findOne(approve.getBizId());
            String content = p.getContents();
            ApplyVipMainReceive applyVipMainReceive = JsonUtil.json2Object(ApplyVipMainReceive.class, content);
            applyVipMainReceive.setStatus(BasConstants.APPROVE_STATUS_D);
            applyVipMainReceive.setId(0L);
            Long companyId = applyVipMainReceive.getCompanyId();
            save(applyVipMainReceive);

            //收款申请通过后开始计算VIP的起始时间
            ApplyVip applyVip = applyVipDao.findvip(companyId);
            applyVip.setStatus(BasConstants.APPROVE_STATUS_D);
            Date ksdate = new Date();
            BsCompany company = companyService.getEntity(applyVipMainReceive.getCompanyId());
            company.setRate(BigDecimal.ZERO);
            Date date = new Date();
            //计算到期日
            Integer vipLevel = applyVip.getVipLevel();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, vipLevel);
            Date time = calendar.getTime();
            applyVip.setEndDate(time);

            Date endDate = applyVip.getEndDate();
            //计算vip剩余天数
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            long startDateTime = 0;
            try {
                startDateTime = dateFormat.parse(dateFormat.format(endDate)).getTime();
            } catch (ParseException e) {
                logger.error("错误" + e.getMessage());
                e.printStackTrace();
            }
            long endDateTime = 0;
            try {
                endDateTime = dateFormat.parse(dateFormat.format(ksdate)).getTime();
            } catch (ParseException e) {
                logger.error("错误" + e.getMessage());
                e.printStackTrace();
            }
            int a = (int) ((startDateTime - endDateTime) / (1000 * 3600 * 24));
            company.setApplyVipStatus(BasConstants.APPLY_STATUS_COMPLETE);
            company.setVipLevel(applyVip.getVipLevel());
            company.setRate(applyVip.getRate());
            company.setVipEndDate(time);
            company.setInterestRate(applyVip.getInterestRate());
            company.setVipStartDate(ksdate);
            company.setVipEndDate(applyVip.getEndDate());
            company.setDaysRemaining(a);
            //修改赊销额度
            BigDecimal b1 = new BigDecimal(Double.valueOf(1.05));
            BigDecimal b2 = company.getTotalCreditAmount();
            company.setTotalCreditAmount(b1.multiply(b2));
            companyService.save(company);

            doapplyVipInvoiceService(applyVipMainReceive.getCompanyId(), applyVipMainReceive, BasConstants.PROCESS_APPLY_VIP_MAIN_INVOICE, approve.getCreateUserId());
        }
    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyVip entity = JsonUtil.json2Object(ApplyVip.class, contents);
        entity.setStatus(BasConstants.APPROVE_STATUS_B);
        BsCompany company = companyService.getEntity(entity.getCompanyId());
        company.setApplyVipStatus(BasConstants.APPLY_STATUS_REJECT);
        company.setInterestRate(entity.getInterestRate());
        companyService.save(company);

        IPmApproveListener.super.doStepBack(approve, nextStep);
    }

    @Override
    public void doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyVip entity = JsonUtil.json2Object(ApplyVip.class, contents);
        entity.setStatus(BasConstants.APPROVE_STATUS_B);
        BsCompany company = companyService.getEntity(entity.getCompanyId());
        company.setApplyVipStatus(BasConstants.APPLY_STATUS_REJECT);
        company.setPromoteVipApplyStatus(BasConstants.APPLY_STATUS_REJECT);
        company.setInterestRate(entity.getInterestRate());
        companyService.save(company);
        IPmApproveListener.super.doRetrieve(vo);
    }

    private void doapplyVipInvoiceService(Long companyid, ApplyVipMainReceive applyVipReceive, String processCode, Long createUserId)  throws ApplicationException{


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
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyVip entity = JsonUtil.json2Object(ApplyVip.class, contents);
        entity.setStatus(BasConstants.APPROVE_STATUS_B);
        BsCompany company = companyService.getEntity(entity.getCompanyId());
        company.setApplyVipStatus(BasConstants.APPLY_STATUS_REJECT);
        company.setPromoteVipApplyStatus(BasConstants.APPLY_STATUS_REJECT);
        company.setInterestRate(entity.getInterestRate());
        companyService.save(company);

    }

    @Override
    public BaseDao<ApplyVipMainReceive> getBaseDao() {
        return applyVipMainReceiveDao;
    }


}
