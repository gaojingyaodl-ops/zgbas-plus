package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyDispute;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.server.service.IApplyDisputeService;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.bas.server.service.ICtrContractOphisService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveContentsService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *  争议申请
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-21 10:24
 */
@Component("applyDisputeService")
@Transactional
public class ApplyDisputeServiceImpl implements IApplyDisputeService, IPmApproveListener, IPmService {

    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;

    @Autowired
    private ICtrContractService contractService;

    @Autowired
    private IBsCompanyService bsCompanyService;

    @Autowired
    private IPmApproveContentsService pmApproveContentsService;

    @Autowired
    private ICtrContractOphisService contractOphisService;

    @Autowired
    private IPmApproveService pmApproveService;

    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyDispute applyDispute = getApplyDispute(approve);
        // 发起后 预算状态变为'争议'状态
        CtrContract buyContract = contractService.getEntity(applyDispute.getBuyContractId());
        CtrContract sellContract = contractService.getEntity(applyDispute.getSellContractId());
        buyContract.setContractStatus(BasConstants.CONTRACT_STATUS_Z);
        sellContract.setContractStatus(BasConstants.CONTRACT_STATUS_Z);
        contractService.save(buyContract);
        contractService.save(sellContract);
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
            ApplyDispute applyDispute = getApplyDispute(approve);
            // 争议审批通过以后，系统自动将预算作废处理，预算状态显示为作废。
            CtrContract buyContract = contractService.getEntity(applyDispute.getBuyContractId());
            CtrContract sellContract = contractService.getEntity(applyDispute.getSellContractId());
            sellContract.setContractStatus(BasConstants.CONTRACTSTATUS_C);
            buyContract.setContractStatus(BasConstants.CONTRACTSTATUS_C);
            contractService.save(sellContract);
            contractService.save(buyContract);
            // 如果是赊销 销售合同作废修正额度
            if (sellContract.getSettlementType() != null){
                BsCompany company = bsCompanyService.getEntity(sellContract.getCompanyId());
                company.setUsedCreditAmount(company.getUsedCreditAmount().subtract(sellContract.getTotalAmount()));
                bsCompanyService.save(company);
                BsCompany buyCompany = bsCompanyService.getEntity(buyContract.getCompanyId());
                // 采购额度修正--采购额度
                BigDecimal subtract = buyCompany.getUsedSupplierPurchaseAmount().subtract(buyContract.getTotalAmount());
                buyCompany.setUsedSupplierPurchaseAmount(subtract.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : subtract);
                // 采购额度修正--预付款额度
                BigDecimal subtract1 = buyCompany.getUsedSupplierPrepayAmount().subtract(buyContract.getTotalAmount());
                buyCompany.setUsedSupplierPrepayAmount(subtract1.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : subtract1);
                bsCompanyService.save(buyCompany);
            }
            // 添加历史
            contractOphisService.addHis(BasConstants.APPLY_TYPE_ZY, sellContract.getId(), approve, new Date());

        }
    }

    /**
     * 审批撤回
     *
     * @param vo
     */
    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        ApplyDispute entity = getApplyDispute(vo.getBizId());
        updateContractStatus(entity);
    }


    /**
     * 审批驳回
     *
     * @param approve
     * @param nextStep
     */
    @Override
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyDispute applyDispute = getApplyDispute(approve);
        updateContractStatus(applyDispute);
    }

    /**
     * 争议审批被驳回，则该预算恢复到原状。
     * @param applyDispute
     */
    private void updateContractStatus( ApplyDispute applyDispute) throws ApplicationException {
        // 争议审批被驳回，则该预算恢复到原状。
        CtrContract buyContract = contractService.getEntity(applyDispute.getBuyContractId());
        CtrContract sellContract = contractService.getEntity(applyDispute.getSellContractId());
        sellContract.setContractStatus(applyDispute.getCurSellContractStatus());
        buyContract.setContractStatus(applyDispute.getCurBuyContractStatus());
        contractService.save(sellContract);
        contractService.save(buyContract);
    }

    /**
     * 获取内容信息
     * @param approve
     * @return
     */
    private ApplyDispute getApplyDispute(PmApprove approve) {
        return getApplyDispute(approve.getBizId());
    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        PmApproveContents entity = new PmApproveContents();
        if (pmEntity instanceof ApplyDispute) {
            ApplyDispute applyDispute = (ApplyDispute) pmEntity;
            Long approveId = applyDispute.getApproveId();
            // 根据approveId判断并替换entity
//        replaceIfExistPmApproveContent(approveId, entity);
            applyDispute.setApproveId(approveId);
            String contents = JsonUtil.obj2Json(applyDispute);
            entity.setContents(contents);

            //生成标题
            entity.setSubject("[争议审批]" + "" + applyDispute.getSellContractNo());
            entity.setApproveId(approveId);
            PmApprove entity1 = pmApproveService.getEntity(applyDispute.getApproveId());
            if(entity1 != null){
                SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(entity1.getCreateUserId());
                applyDispute.setDeptId(deptByUserId.getDeptId());
            }
            entity.setFileId(applyDispute.getFileId());
            entity.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            entity.setApplyName(BasConstants.PROCESS_APPLY_DISPUTE);
            pmApproveContentsDao.save(entity);
            return entity;
        }else {
            PmApproveContents pmApproveContents = (PmApproveContents) pmEntity;
            ApplyDispute applyDispute = JsonUtil.json2Object(ApplyDispute.class, pmApproveContents.getContents());
            applyDispute.setId(pmApproveContents.getId());
            applyDispute.setApproveId(pmApproveContents.getApproveId());
            pmApproveContents.setContents(JsonUtil.obj2Json(applyDispute));
            pmApproveContentsDao.save(pmApproveContents);
            return pmApproveContents;
        }
    }

    @Override
    public IPmEntity getEntity(Long entityId) {
        return pmApproveContentsService.getEntity(entityId);
    }


    /**
     * 获取内容信息
     * @param bizId
     * @return
     */
    private ApplyDispute getApplyDispute(Long bizId) {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(bizId);
        return JsonUtil.json2Object(ApplyDispute.class, pmApproveContents.getContents());
    }

    @Override
    public void delete(Long entityId) {

    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            if (pmEntity instanceof PmApproveContents) {
                PmApproveContents entity = (PmApproveContents) pmEntity;
                ApplyDispute applyDispute = JsonUtil.json2Object(ApplyDispute.class, entity.getContents());
                //生成标题
                return ("[争议审批]" + "" + applyDispute.getSellContractNo());
            }
        }
        return null;
    }



}
