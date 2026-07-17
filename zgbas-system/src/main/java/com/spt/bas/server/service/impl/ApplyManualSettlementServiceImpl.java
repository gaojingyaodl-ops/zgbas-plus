package com.spt.bas.server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyManualSettlementDao;
import com.spt.bas.server.service.*;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *    手动决算
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-04-21 15:40
 */
@Component("applyManualSettlementService")
public class ApplyManualSettlementServiceImpl extends BaseService<ApplyManualSettlement> implements IApplyManualSettlementService, IPmService, IPmApproveListener {

    @Autowired
    private ApplyManualSettlementDao applyManualSettlementDao;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private IBudgetSettlementService budgetSettlementService;
    @Autowired
    private IApplyPayService applyPayService;
    @Autowired
    private IApplyDeliveryOutService applyDeliveryOutService;
    @Autowired
    private IApplyInvoiceReceivedService applyInvoiceReceivedService;
    @Autowired
    private IApplyReceiveService applyReceiveService;
    @Autowired
    private IApplyDeliveryInService applyDeliveryInService;
    @Autowired
    private IApplyServiceReceiveService applyServiceReceiveService;
    @Autowired
    private IApplyInvoiceService applyInvoiceService;
    @Autowired
    private ICtrContractOphisService contractOphisService;
    @Autowired
    private IApplyConfrimReceiptService applyConfrimReceiptService;
    @Autowired
    private IBsCompanyClient bsCompanyClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IPmApproveService iPmApproveService;

    @Autowired
    private IApplyDcsxService applyDcsxService;




    /**
     * 发起审批
     *
     * @param approve
     */
    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyManualSettlement applyManualSettlement = applyManualSettlementDao.findOne(approve.getBizId());
        // 校验是否该发的申请都已发起并完成

        CtrContract buyContract = ctrContractService.findByContractNo(applyManualSettlement.getBuyContractNo());
        CtrContract sellContract = ctrContractService.findByContractNo(applyManualSettlement.getSellContractNo());

        // 检验预算是否已经结算
        BudgetSettlement budgetSettlement = budgetSettlementService.getBySellContractId(sellContract.getId());
        if (budgetSettlement == null) {
            throw new ApplicationException("历史遗留单无法手动结束，请联系管理员");
        }
        if ("4".equals(budgetSettlement.getBudgetStatus()) && "1".equals(budgetSettlement.getBudgetFinishStatus())) {
            throw new ApplicationException("已完成结算，无法发起手动结算申请");
        }

        // 校验是否已发起并完成相关审批
        String checkMsg = checkMsg(buyContract, sellContract);
        if (!StrUtil.isEmpty(checkMsg)) {
            throw new ApplicationException(checkMsg);
        }

        // 校验修改完成后是否符合结算条件
        String checkCanSettleMsg = checkCanSettleMsg(buyContract, sellContract, applyManualSettlement);
        if (!StrUtil.isEmpty(checkCanSettleMsg)) {
            throw new ApplicationException(checkCanSettleMsg);
        }
    }

    /**
     * 校验是否已发起并完成相关审批
     * @param buyContract
     * @param sellContract
     * @return msg 提示信息
     */
    private String checkMsg(CtrContract buyContract,CtrContract sellContract) {
        StringBuilder msg = new StringBuilder();

        // 是否都已盖章
        if (!buyContract.getSealFlg()) {
            msg.append("采购合同还未盖章;");
        }
        if (!sellContract.getSealFlg()) {
            msg.append("销售合同还未盖章;");
        }
        // 是否有付款申请
        List<ApplyPay> applyPays = applyPayService.findByContractId(buyContract.getId());
        if(buyContract.getBusinessTypeDcsx()==null){
            if (applyPays.isEmpty()) {
                msg.append("还未发起付款申请;");
            }else{
                if (applyPays.stream().anyMatch(a -> !BasConstants.APPROVE_STATUS_D.equals(a.getStatus()))) {
                    msg.append("还有未完成的付款申请;");
                }
            }
        }


        // 入库
        List<ApplyDeliveryIn> applyDeliveryIns = applyDeliveryInService.findByContractId(buyContract.getId());
        if (applyDeliveryIns.isEmpty()) {
            msg.append("还未发起入库申请;");
        }else {
            if (applyDeliveryIns.stream().anyMatch(a -> !BasConstants.APPROVE_STATUS_D.equals(a.getStatus()) && !BasConstants.APPROVE_STATUS_D.equals(a.getStatus()))) {
                msg.append("还有未完成的入库申请;");
            }
        }

        // 收票
        List<ApplyInvoiceReceived> invoiceReceiveds = applyInvoiceReceivedService.findByContractId(buyContract.getId());
        if (invoiceReceiveds.isEmpty()) {
            msg.append("还未发起收票申请;");
        }else {
            if (invoiceReceiveds.stream().anyMatch(a -> !BasConstants.APPROVE_STATUS_D.equals(a.getStatus()))) {
                msg.append("还有未完成的收票申请;");
            }
        }

        // 收货款
        List<ApplyReceive> receives = applyReceiveService.findByContractId(sellContract.getId());
        if (receives.isEmpty()) {
            msg.append("还未发起收款申请;");
        }else {
            if (receives.stream().anyMatch(a -> !BasConstants.APPROVE_STATUS_D.equals(a.getStatus()))) {
                msg.append("还有未完成的收款申请;");
            }
        }

        // 出库
        List<ApplyDeliveryOut> deliveryOuts = applyDeliveryOutService.findByContractId(sellContract.getId());
        if (deliveryOuts.isEmpty()) {
            msg.append("还未发起出库申请;");
        }else {
            if (deliveryOuts.stream().anyMatch(a -> !BasConstants.APPROVE_STATUS_D.equals(a.getStatus()))) {
                msg.append("还有未完成的出库申请;");
            }
        }

        // 开票
        List<ApplyInvoice> applyInvoices = applyInvoiceService.findByContractId(sellContract.getId());
        if (applyInvoices.isEmpty()) {
            msg.append("还未发起开票申请;");
        }else {
            if (applyInvoices.stream().anyMatch(a -> !BasConstants.APPROVE_STATUS_D.equals(a.getStatus()))) {
                msg.append("还有未完成的开票申请;");
            }
        }

        // 确认收货
        List<ApplyConfirmReceipt> applyConfirmReceipts = applyConfrimReceiptService.findByContractId(sellContract.getId());
        if (applyConfirmReceipts.isEmpty()) {
            msg.append("还未发起确认收货申请;");
        }else {
            if (applyConfirmReceipts.stream().anyMatch(a -> !BasConstants.APPROVE_STATUS_D.equals(a.getStatus()))) {
                msg.append("还有未完成的确认收货申请;");
            }
        }

        // 如果两票制
        if (BasConstants.SETTLEMENT_TYPE_TWO.equals(sellContract.getSettlementType())) {
            // 收服务费
            List<ApplyServiceReceive> applyServiceReceives = applyServiceReceiveService.findByContractId(sellContract.getId());
            if (applyServiceReceives.isEmpty()) {
                msg.append("还未发起收服务费申请;");
            }else {
                if (applyServiceReceives.stream().anyMatch(a -> !BasConstants.APPROVE_STATUS_D.equals(a.getStatus()))) {
                    msg.append("还有未完成的收服务费申请;");
                }
            }
        }
        String msgString = msg.toString();
        if (!StrUtil.isEmpty(msgString)) {
            return msgString.substring(0, msgString.length() - 1);
        }

        return null;
    }

    /**
     * 校验是否可以发起结算
     * @param buyContract
     * @param sellContract
     * @param applyManualSettlement
     * @return msg 提示信息
     */
    private String checkCanSettleMsg(CtrContract buyContract,CtrContract sellContract,ApplyManualSettlement applyManualSettlement) {
        StringBuilder sb = new StringBuilder();
        // 是否是赊销合同
        boolean isSX = !StringUtils.isEmpty(sellContract.getSettlementType());

        if( buyContract.getBusinessTypeDcsx() == null ){
            // 校验采购合同
            Boolean buyCheck = checkBuyContract(buyContract, applyManualSettlement);
            if (!buyCheck) {
                sb.append("校验调整后采购合同不满足决算条件!");
            }
        }
        Boolean sellCheck;
        // 校验销售合同
        if (!isSX) {
            // 代采
            sellCheck = checkDcSellContract(sellContract, applyManualSettlement);
        }else{
            // 赊销
            sellCheck = checkSellContract(sellContract, applyManualSettlement);
        }
        if (!sellCheck) {
            sb.append("校验调整后销售合同不满足决算条件!");
        }
        return sb.toString();
    }

    /**
     * 检查采购合同状态
     *
     * @return
     */
    private Boolean checkBuyContract(CtrContract buyContract,ApplyManualSettlement applyManualSettlement) {
        if (buyContract == null) {
            return false;
        }
        // 是否完成收票
        Boolean billedFlg = applyManualSettlement.getReceiveInvoiceAmountB().compareTo(buyContract.getTotalAmount()) >= 0;
        // 是否完成付款
        Boolean dealedFlg = applyManualSettlement.getPayAmountB().compareTo(buyContract.getTotalAmount()) >= 0;
        // 是否完成盖章
        Boolean sealFlg = buyContract.getSealFlg();
        String contractNo = buyContract.getContractNo();
        logger.error("buyContractNo:{};checkBuyContract,billedFlg:{}", contractNo, billedFlg);
        logger.error("buyContractNo:{};checkBuyContract,dealedFlg:{}", contractNo, dealedFlg);
        logger.error("buyContractNo:{};checkBuyContract,sealFlg:{}", contractNo, sealFlg);

        if (billedFlg && dealedFlg && sealFlg) {
            return true;
        }
        return false;
    }

    /**
     * 校验代采销售合同状态
     *
     * @param sellContract
     * @return
     */
    private Boolean checkDcSellContract(CtrContract sellContract,ApplyManualSettlement applyManualSettlement) {
        if (sellContract == null) {
            return false;
        }
        // 是否完成开票
        Boolean billedFlg = applyManualSettlement.getInvoiceAmountB().compareTo(sellContract.getTotalAmount()) >= 0;
        // 是否完成收款
        Boolean dealedFlg = applyManualSettlement.getReceiveAmountB().compareTo(sellContract.getTotalAmount()) >= 0;
        // 是否完成盖章
        Boolean sealFlg = sellContract.getSealFlg();
        // 是否完成确认收货
        Boolean confirmReceiptFlg = applyManualSettlement.getConfirmReceivedGoodsB().compareTo(applyManualSettlement.getDeliveryOutNumberB()) >= 0;

        String contractNo = sellContract.getContractNo();

        logger.error("sellContractNo:{};checkSellContract,billedFlg:{}", contractNo, billedFlg);
        logger.error("sellContractNo:{};checkSellContract,dealedFlg:{}", contractNo, dealedFlg);
        logger.error("sellContractNo:{};checkSellContract,sealFlg:{}", contractNo, sealFlg);
        logger.error("sellContractNo:{};checkSellContract,confirmReceiptFlg:{}", contractNo, confirmReceiptFlg);

        if (billedFlg && dealedFlg && sealFlg && confirmReceiptFlg) {
            return true;
        }
        return false;
    }

    /**
     * 检查销售和服务合同状态
     *
     * @param sellContract
     * @return
     */
    private Boolean checkSellContract(CtrContract sellContract,ApplyManualSettlement applyManualSettlement) {
        if (sellContract == null) {
            return false;
        }

        // 应收款 = 合同金额 - 损耗金额
        BigDecimal receivables = sellContract.getTotalAmount().subtract(applyManualSettlement.getLossAmount());

        // 是否完成开票
        Boolean billedFlg = applyManualSettlement.getInvoiceAmountB().compareTo(receivables) >= 0;
        // 是否完成付款
//        Boolean dealedFlg = sellContract.getDealedFlg();
        // 是否完成付款(收货款金额大于等于合同金额)
        Boolean dealedFlg = applyManualSettlement.getReceiveAmountB().compareTo(receivables) >= 0;

        // 是否完成盖章
        Boolean sealFlg = sellContract.getSealFlg();
        // 是否完成确认收货
        Boolean confirmReceiptFlg = applyManualSettlement.getConfirmReceivedGoodsB().compareTo(applyManualSettlement.getDeliveryOutNumberB()) >= 0;

        // 服务费合同总价
        BigDecimal serviceAmount = sellContract.getServiceAmount();

        // 已收服务费金额
        BigDecimal receiveServiceAmount = applyManualSettlement.getReceiveServiceAmountB();

        // 服务合同是否已收款(服务费收款金额大于等于服务合同金额)
        boolean receiveServiceFlg = receiveServiceAmount.compareTo(serviceAmount) >= 0;

        // 服务合同开票是否完成(服务合同开票金额大于等于服务合同收款金额)
        boolean serviceBilledFlg = sellContract.getServiceBilledAmount().compareTo(serviceAmount) >= 0;

        String contractNo = sellContract.getContractNo();

        logger.error("sellContractNo:{};checkSellContract,billedFlg:{}", contractNo, billedFlg);
        logger.error("sellContractNo:{};checkSellContract,dealedFlg:{}", contractNo, dealedFlg);
        logger.error("sellContractNo:{};checkSellContract,sealFlg:{}", contractNo, sealFlg);
        logger.error("sellContractNo:{};checkSellContract,confirmReceiptFlg:{}", contractNo, confirmReceiptFlg);
        logger.error("sellContractNo:{};checkSellContract,serviceAmount:{}", contractNo, serviceAmount);
        logger.error("sellContractNo:{};checkSellContract,receiveServiceAmount:{}", contractNo, receiveServiceAmount);
        logger.error("sellContractNo:{};checkSellContract,receiveServiceFlg:{}", contractNo, receiveServiceFlg);
        logger.error("sellContractNo:{};checkSellContract,serviceBilledFlg:{}", contractNo, serviceBilledFlg);

        if (billedFlg && dealedFlg && sealFlg && confirmReceiptFlg && receiveServiceFlg && serviceBilledFlg) {
            return true;
        }
        return false;
    }

    /**
     * 执行审批步骤
     *
     * @param approve
     * @param nextStep
     */
    @Override
    @ServerTransactional
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyManualSettlement applyManualSettlement = applyManualSettlementDao.findOne(approve.getBizId());
            // 校验是否该发的申请都已发起并完成
            CtrContract buyContract = ctrContractService.findByContractNo(applyManualSettlement.getBuyContractNo());
            CtrContract sellContract = ctrContractService.findByContractNo(applyManualSettlement.getSellContractNo());

            // 校验是否已发起并完成相关审批
            String checkMsg = checkMsg(buyContract, sellContract);
            if (!StrUtil.isEmpty(checkMsg)) {
                throw new ApplicationException(checkMsg);
            }

            // 校验修改完成后是否符合结算条件
            String checkCanSettleMsg = checkCanSettleMsg(buyContract, sellContract,applyManualSettlement);
            if (!StrUtil.isEmpty(checkCanSettleMsg)) {
                throw new ApplicationException(checkCanSettleMsg);
            }

            // 更新调整后字段到CtrContract表中
            // 入库数量
            buyContract.setWarehouseNumber(applyManualSettlement.getDeliveryInNumberB());
            // 付款
            buyContract.setDealedAmount(applyManualSettlement.getPayAmountB());
            // 收票
            buyContract.setBilledAmount(applyManualSettlement.getReceiveInvoiceAmountB());

            // 收款
            sellContract.setDealedAmount(applyManualSettlement.getReceiveAmountB());
            // 出库
            sellContract.setWarehouseNumber(applyManualSettlement.getDeliveryOutNumberB());
            // 开票
            sellContract.setBilledAmount(applyManualSettlement.getInvoiceAmountB());
            // 确认收货
            sellContract.setConfirmReceiveNumber(applyManualSettlement.getConfirmReceivedGoodsB());
            // 服务费收款
            sellContract.setReceiveServiceAmount(applyManualSettlement.getReceiveServiceAmountB());
            /// 服务费开票
            sellContract.setServiceBilledAmount(applyManualSettlement.getServiceInvoiceAmountB());


            // 损耗处理====================================================
            // 实际物流费用
            sellContract.setTransportAmount(applyManualSettlement.getLossAmountByActual());
            // 损耗金额
            sellContract.setLossAmount(sellContract.getLossAmount().add(applyManualSettlement.getLossAmount()));
            // 损耗数量
            sellContract.setLossNumber(sellContract.getLossNumber().add(applyManualSettlement.getLossNumber()));
            // 物流方承担损耗金额
            sellContract.setLossAmountByLogistics(sellContract.getLossAmountByLogistics().add(applyManualSettlement.getLossAmountByLogistics()));
            // 供应商承担损耗金额
            sellContract.setLossAmountBySupplier(sellContract.getLossAmountBySupplier().add(applyManualSettlement.getLossAmountBySupplier()));
            // 我方承担损耗金额
            sellContract.setLossAmountByOur(sellContract.getLossAmountByOur().add(applyManualSettlement.getLossAmountByOur()));
            // 计入供应商 上游应付金额应该做相应的扣减
            buyContract.setTotalAmount(buyContract.getTotalAmount().subtract(applyManualSettlement.getLossAmountBySupplier()));
            // 上游数量
            buyContract.setTotalNumber(buyContract.getTotalNumber().subtract(applyManualSettlement.getLossNumber()));
            // 下游扣减 按实际填写的损耗数量
            BigDecimal sellDealPrice = sellContract.getDealPrice();
            // 下游扣减金额
            BigDecimal sellLossAmount = sellDealPrice.multiply(applyManualSettlement.getLossNumber());
            sellContract.setTotalNumber(sellContract.getTotalNumber().subtract(sellContract.getLossNumber()));
            // 下游数量
            sellContract.setTotalAmount(sellContract.getTotalAmount().subtract(sellLossAmount));

            ctrContractService.save(sellContract);

            // 物流承担费用大于0 代表物流费用变更了
            if (applyManualSettlement.getLossAmountByLogistics().compareTo(BigDecimal.ZERO) > 0) {
                budgetSettlementService.updateTransformAndWarehouse(sellContract.getId());
            }

            ctrContractService.save(buyContract);

            // 如果修改了运输费仓储费则更新
            if (applyManualSettlement.getSellTransAmount().compareTo(applyManualSettlement.getSellTransAmountB()) != 0 ||
                    applyManualSettlement.getSellWarehouseAmount().compareTo(applyManualSettlement.getSellWarehouseAmountB()) != 0) {
                sellContract.setTransportAmount(applyManualSettlement.getSellTransAmountB());
                sellContract.setWarehouseAmount(applyManualSettlement.getSellWarehouseAmountB());
                ctrContractService.save(sellContract);
                // 计算利润时，采购和销售的运输费和仓储费都算是成本
                budgetSettlementService.updateTransformAndWarehouse(sellContract.getId());
            }else{
                ctrContractService.save(sellContract);
            }

            ctrContractService.save(buyContract);

            // 添加历史
            contractOphisService.addHis(BasConstants.APPLY_TYPE_MUSE, sellContract.getId(), approve, new Date());

            // 决算
            BudgetSettlement settlement = budgetSettlementService.getBySellContractId(sellContract.getId());
            budgetSettlementService.doSettle(settlement);
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
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyManualSettlement entity = (ApplyManualSettlement) pmEntity;
            Long approveId = entity.getApproveId();
            PmApprove pmApprove = iPmApproveService.getEntity(approveId);
            if(pmApprove != null) {
                SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(pmApprove.getCreateUserId());
                entity.setDeptId(deptByUserId.getDeptId());
            }
            return save(entity);

        }
        return null;
    }

    /**
     * 标题
     *
     * @param pmEntity
     */
    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        ApplyManualSettlement applyManualSettlement = (ApplyManualSettlement) pmEntity;
        return "[" + applyManualSettlement.getSellContractNo() + "]手动结算";
    }

    @Override
    public BaseDao<ApplyManualSettlement> getBaseDao() {
        return applyManualSettlementDao;
    }

    @Override
    public void updateFileId(Long id, String fileId) {
        applyManualSettlementDao.updateFileId(id, fileId);
    }
}
