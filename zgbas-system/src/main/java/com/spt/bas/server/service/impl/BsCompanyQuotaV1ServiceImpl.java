package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IApplyCompanyCreditClient;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.IBsCompanyCreditClient;
import com.spt.bas.client.vo.ApplyQuotaVo;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.remote.IWxUserDetailClient;
import com.spt.bas.server.dao.BsCompanyQuotaV1Dao;
import com.spt.bas.server.service.IBsCompanyQuotaV1Service;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.vo.*;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 额度审批
 */
@Component("bsCompanyQuotaV1Service")
@Transactional(readOnly = true)
public class BsCompanyQuotaV1ServiceImpl extends BaseService<BsCompanyQuotaV1> implements IBsCompanyQuotaV1Service, IPmApproveListener {

    private static final Long ZG_ENTERPRISE_ID = BasConstants.ZG_ENTERPRISE_ID;

    @Autowired
    private BsCompanyQuotaV1Dao bsCompanyQuotaDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private IPmProcessService pmProcessService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IWxUserDetailClient userDetailDao;
    @Autowired
    private IBsCompanyCreditClient bsCompanyCreditClient;
    @Autowired
    private IApplyCompanyCreditClient applyCompanyCreditClient;

    @Override
    @ServiceTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            ApplyQuotaVo entity = JsonUtil.json2Object(ApplyQuotaVo.class, contents);
            // 修改额度审批状态
            ApplyCompanyCredit applyCompanyCredit = applyCompanyCreditClient.findByCompanyIdAndType(entity.getType(),entity.getCompanyId(),entity.getCreditType());
            applyCompanyCredit.setStatus(BasConstants.APPLY_STATUS_COMPLETE);
            applyCompanyCredit.setApproveId(pmApproveContents.getApproveId());
            applyCompanyCreditClient.save(applyCompanyCredit);
            BsCompanyCredit byCompanyIdAndType = bsCompanyCreditClient.findByCompanyIdAndType(entity.getCompanyId(), entity.getCreditType());
            // 额度审批
            if(entity.getType().equals(BasConstants.APPLY_QUOTA_TYPE_Q)){
                BsCompanyCredit bsCompanyCredit;
                if(byCompanyIdAndType!=null){
                    bsCompanyCredit = byCompanyIdAndType;
                    bsCompanyCredit.setCreditAmount(entity.getCreditAmount());
                    bsCompanyCredit.setRiskAmount(entity.getRiskAmount());
                } else {
                    bsCompanyCredit = new BsCompanyCredit();
                    bsCompanyCredit.setCompanyId(entity.getCompanyId());
                    bsCompanyCredit.setCreditType(entity.getCreditType());
                    bsCompanyCredit.setEnableFlg(true);
                    bsCompanyCredit.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
                    bsCompanyCredit.setCreatedUserId(entity.getUserId());
                    bsCompanyCredit.setCreatedUserName(entity.getNickName());
                    bsCompanyCredit.setCreditAmount(entity.getCreditAmount());
                    bsCompanyCredit.setRiskAmount(entity.getRiskAmount());
                }
                bsCompanyCreditClient.save(bsCompanyCredit);
                // 修改额度审批状态
                BsCompany bsCompany = bsCompanyService.getEntity(entity.getCompanyId());
                // 修改企业授信类别，逗号拼接
                String creditCategory = bsCompany.getCreditCategory();
                String creditType = entity.getCreditType();
                String newCreditCategory = getNewCreditCategory(creditCategory, creditType);
                bsCompany.setCreditCategory(newCreditCategory);
                bsCompanyService.save(bsCompany);
            } else {
                // 临时提额
                byCompanyIdAndType.setTemporaryAmount(entity.getTemporaryAmount());
                // 临时额度失效日为 当前日期+有效天数
                Integer validDays = entity.getValidDays();
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_MONTH, validDays);
                Date expiryDate = calendar.getTime();
                byCompanyIdAndType.setTemporaryExpiryDate(expiryDate);
                bsCompanyCreditClient.save(byCompanyIdAndType);
            }
        }
    }
    public String getNewCreditCategory(String creditCategory, String newCategory) {
        if (StringUtils.isNotBlank(creditCategory)) {
            // 将字符串分割为字符串列表
            List<String> categories = Arrays.asList(creditCategory.split(","));

            // 添加新的字符串类别，确保不重复
            if (!categories.contains(newCategory)) {
                categories = categories.stream().collect(Collectors.toList()); // 复制列表以防止不变性问题
                categories.add(newCategory);
            }

            // 对列表进行字典顺序排序
            categories = categories.stream().sorted().collect(Collectors.toList());

            // 将列表重新拼接成逗号分割的字符串
            return String.join(",", categories);
        } else {
            return BasConstants.CREDIT_TYPE_9;
        }

    }
    /**
     * 计算最终额度
     * 最终额度 = MAX ( 基本额度，MIN ( 基本额度 * ( 1 + 上浮比例 )，最高限额 ) )
     *
     * @return
     */
    private BigDecimal evalTotalCreditAmount(BsCompanyQuota quota, BigDecimal baseQuota) {
        baseQuota = baseQuota == null ? BigDecimal.ZERO : baseQuota;
        BigDecimal r;
        // 基本额度 * ( 1 + 上浮比例 )
        BigDecimal baseUp = quota.getFloatingRate().add(BigDecimal.ONE).multiply(baseQuota);
        // MIN ( 基本额度 * ( 1 + 上浮比例 )，最高限额 )
        if (baseUp.compareTo(quota.getFloatingMaxAmount()) <= 0) {
            r = baseUp;
        }else {
            r = quota.getFloatingMaxAmount();
        }

        // MAX ( 基本额度，r )
        if (baseQuota.compareTo(r) >= 0) {
            r = baseQuota;
        }
        return r;
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
        ApplyQuotaVo entity = JsonUtil.json2Object(ApplyQuotaVo.class, contents);
        ApplyCompanyCredit applyCompanyCredit = applyCompanyCreditClient.findByCompanyIdAndType(entity.getType(),entity.getCompanyId(),entity.getCreditType());
        applyCompanyCredit.setStatus(BasConstants.APPLY_STATUS_REJECT);
        applyCompanyCredit.setApproveId(pmApproveContents.getApproveId());
        applyCompanyCreditClient.save(applyCompanyCredit);
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyQuotaVo entity = JsonUtil.json2Object(ApplyQuotaVo.class, contents);
        ApplyCompanyCredit applyCompanyCredit = applyCompanyCreditClient.findByCompanyIdAndType(entity.getType(),entity.getCompanyId(),entity.getCreditType());
        applyCompanyCredit.setStatus(BasConstants.APPLY_STATUS_NO_START);
        applyCompanyCredit.setApproveId(pmApproveContents.getApproveId());
        applyCompanyCreditClient.save(applyCompanyCredit);
    }

    @Override
    public BaseDao<BsCompanyQuotaV1> getBaseDao() {
        return bsCompanyQuotaDao;
    }

    @Override
    public Class<BsCompanyQuotaV1> getEntityClazz() {
        return BsCompanyQuotaV1.class;
    }

    @Override
    public void startFlow(String bizEntityJson,Long companyId) throws ApplicationException {
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setEnterpriseId(ZG_ENTERPRISE_ID);
        BsCompany company = bsCompanyService.getEntity(companyId);
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ZG_ENTERPRISE_ID);
        searchVo.setProcessCode("APPLY_CPN_QUOTA");
        PmProcess process = pmProcessService.findByProcessCode(searchVo);
        SysUserSdk userById = authOpenFacade.findUserById(company.getMatchUserId());
        startVo.setUserId(userById.getUserId());
        startVo.setUserName(userById.getNickName());
        startVo.setProcessId(process.getId());
        startVo.setApproveId(0L);
        startVo.setBizEntityJson(bizEntityJson);
        pmApproveService.startFlow(startVo);
    }

}
