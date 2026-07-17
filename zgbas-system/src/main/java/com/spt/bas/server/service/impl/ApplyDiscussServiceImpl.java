package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.server.dao.ApplyDiscussDao;
import com.spt.bas.server.service.*;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.bas.server.util.RuleUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component("applyDiscussService")
@Transactional(readOnly = true)
public class ApplyDiscussServiceImpl extends BaseService<ApplyDiscuss> implements IApplyDiscussService, IPmService, IPmApproveListener {
    @Autowired
    private ApplyDiscussDao applyDiscussDao;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private IBudgetSettlementService budgetSettlementService;
    @Autowired
    private ICtrContractOphisService contractOphisService;
    @Autowired
    private ICtrProductService productService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IApplyDcsxService applyDcsxService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Override
    public BaseDao<ApplyDiscuss> getBaseDao() {
        return applyDiscussDao;
    }

    @Override
    public Class<ApplyDiscuss> getEntityClazz() {
        return ApplyDiscuss.class;
    }

    @Override
    @ServiceTransactional
    public void updateFileId(Long id, String fileId) {
        applyDiscussDao.updateFileId(id, fileId);
    }

    @Override
    @ServiceTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyDiscuss entity = applyDiscussDao.findOne(approve.getBizId());
            CtrContract buyContract = ctrContractService.getEntity(entity.getBuyContractId());
            CtrContract sellContract = ctrContractService.getEntity(entity.getSellContractId());


            //更新中游合同
            ApplyCtrDCSX byDCSXApproveId = applyDcsxService.findByDCSXApproveId(buyContract.getApproveId());
            if (byDCSXApproveId!=null){
                byDCSXApproveId.setTotalNumber(entity.getDealNumberB());
                BigDecimal totalNumber = entity.getDealNumberB();
                BigDecimal dealPrice = byDCSXApproveId.getDealPrice();
                BigDecimal sum = totalNumber.multiply(dealPrice);
                byDCSXApproveId.setTotalAmount(sum);
            }


            // 更新合同
            buyContract.setTotalNumber(entity.getDealNumberB());
            buyContract.setTotalAmount(entity.getBuyTotalAmount());
            sellContract.setTotalNumber(entity.getDealNumberB());
            sellContract.setTotalAmount(entity.getSellTotalAmount());
            sellContract.setServiceAmount(entity.getServiceAmount());
            ctrContractService.save(buyContract);
            ctrContractService.save(sellContract);

            // 更新合同商品表
            List<CtrProduct> buyProducts = productService.findByContractId(buyContract.getId());
            List<CtrProduct> sellProducts = productService.findByContractId(sellContract.getId());
            // 当前只有一个商品 直接保存
            for (CtrProduct buyProduct : buyProducts) {
                buyProduct.setDealNumber(entity.getDealNumberB());
                productService.save(buyProduct);
            }

            // 当前只有一个商品 直接保存
            for (CtrProduct sellProduct : sellProducts) {
                sellProduct.setDealNumber(entity.getDealNumberB());
                productService.save(sellProduct);
            }

            // 更新结算表
            BudgetSettlement bySellContractId = budgetSettlementService.getBySellContractId(sellContract.getId());
            budgetSettlementService.updateSettlement(bySellContractId, buyContract, sellContract);

            // 添加历史
            contractOphisService.addHis(BasConstants.APPLY_TYPE_DICUSS, sellContract.getId(), approve, new Date());

        }

    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        // TODO Auto-generated method stub

    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyDiscuss entity = (ApplyDiscuss) pmEntity;
            PmApprove entity1 = pmApproveService.getEntity(entity.getApproveId());
            if(entity1 != null){
                SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(entity1.getCreateUserId());
                entity.setDeptId(deptByUserId.getDeptId());
            }
            return save(entity);
        }
        return null;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyDiscuss entity = (ApplyDiscuss) pmEntity;
            String buyContractNo = entity.getBuyContractNo();
            String dealNumber = NumberUtil.formatNumber(entity.getDealNumber(), "#.##");
            String buyTotalAmount = NumberUtil.formatNumber(entity.getBuyTotalAmount(), "#.##");
            String sellTotalAmount = NumberUtil.formatNumber(entity.getSellTotalAmount(), "#.##");
            String subject = SubjectUtil.formatSubject(buyContractNo,dealNumber+ RuleUtil.weightUnit,entity.getDealNumberB()+ RuleUtil.weightUnit,SubjectUtil.formatMoney(entity.getBuyTotalAmount() , RuleUtil.monetaryUnit),SubjectUtil.formatMoney(entity.getSellTotalAmount() , RuleUtil.monetaryUnit));
            return subject;
        }
        return null;
    }
}

