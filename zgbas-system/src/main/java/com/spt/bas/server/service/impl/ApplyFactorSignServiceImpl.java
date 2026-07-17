package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyFactorSign;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyFactorSignDao;
import com.spt.bas.server.service.IApplyFactorSignService;
import com.spt.bas.server.service.ICtrContractOphisService;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.bas.server.util.RuleUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 签署保理申请
 */
@Component("applyFactorSignService")
@Transactional(readOnly = true)
public class ApplyFactorSignServiceImpl extends BaseService<ApplyFactorSign> implements IApplyFactorSignService, IPmService, IPmApproveListener {
    @Autowired
    private ApplyFactorSignDao applyFactorSignDao;
    @Autowired
    private IPmProcessService pmProcessService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IPmApproveService pmApproveService;

    @Autowired
    private ICtrContractOphisService contractOphisService;

    @Override
    public BaseDao<ApplyFactorSign> getBaseDao() {
        return applyFactorSignDao;
    }

    @Override
    public Class<ApplyFactorSign> getEntityClazz() {
        return ApplyFactorSign.class;
    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        ApplyFactorSign entity = null;
        if (pmEntity instanceof ApplyFactorSign) {
            entity = (ApplyFactorSign) pmEntity;
            applyFactorSignDao.save(entity);
        }
        return entity;
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {

    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    /**
     * 自动发起签署保理申请
     * @param factorSign
     */
    @Override
    @ServerTransactional
    public void applyFactorSign(ApplyFactorSign factorSign) throws ApplicationException {
        logger.info("applyFactorSign ----");
        //获取审批对象
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_D);
        startVo.setEnterpriseId(factorSign.getEnterpriseId());

        //获取流程对象
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        searchVo.setProcessCode(BasConstants.PROCESS_APPLY_FACTOR_SIGN);

        //根据流程对象获取流程主表
        PmProcess process = pmProcessService.findByProcessCode(searchVo);
        if (process == null) {
            throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录！");
        }

        //根据业务员id获取用户信息
        SysUserSdk sysUserSdk = authOpenFacade.findUserById(factorSign.getApplyUserId());
        if (Objects.nonNull(sysUserSdk)) {
            //审批对象获取当前操作人信息 和流程id
            startVo.setUserId(sysUserSdk.getUserId());
            startVo.setUserName(sysUserSdk.getNickName());
            startVo.setProcessId(process.getId());
            startVo.setApproveId(0L);
        }
        startVo.setBizEntityJson(JsonUtil.obj2Json(factorSign));
        startVo.setAutoStartFlg(true);
        startVo.setAutoStartFlgReal(true);
        startVo.setAutoStartMessage("债权凭证签署完成自动发起");
        //审批发起方法
        PmApprove pmApprove = pmApproveService.startFlow(startVo);

        // 添加合同历史状态记录
        contractOphisService.addHis(BasConstants.APPLY_TYPE_FC, factorSign.getContractId(), pmApprove, new Date());
    }

    /**
     * 生成申请单标题
     * 合同编号，货名，客户名称，我方，合同总额元
     *
     * @param pmEntity
     * @param pmProcess
     * @return
     */
    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess pmProcess) {
        if (pmEntity instanceof ApplyFactorSign) {
            ApplyFactorSign entity = (ApplyFactorSign) pmEntity;
            String contractNo = entity.getContractNo();
            String productNames = entity.getProductNames();
            BigDecimal totalAmount = entity.getTotalAmount();
            String totalAmount2 = NumberUtil.formatNumber(totalAmount, "#.##");
            String  companyName = RuleUtil.companyNameSubString(entity.getCompanyName());
            String  ourCompanyName = RuleUtil.companyNameSubString(entity.getOurCompanyName());
            String company="";
            if(StringUtils.isNotBlank(companyName)&&StringUtils.isNotBlank(ourCompanyName)){
                company=companyName+"-"+ourCompanyName;
            }
            return  SubjectUtil.formatSubject(contractNo, productNames,company, SubjectUtil.formatMoney(totalAmount , RuleUtil.monetaryUnit));
        }
        return null;
    }
}
