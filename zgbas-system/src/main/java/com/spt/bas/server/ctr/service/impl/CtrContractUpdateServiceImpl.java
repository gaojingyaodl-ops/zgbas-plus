package com.spt.bas.server.ctr.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.google.common.base.Splitter;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IBsCompanyDcsxClient;
import com.spt.bas.client.remote.IInsuranceAmountFlowClient;
import com.spt.bas.client.vo.*;
import com.spt.bas.client.vo.protocol.SupplementaryAgreement;
import com.spt.bas.report.client.remote.IRptCtrContractReceiveDetailClient;
import com.spt.bas.report.client.vo.RptContractDateSearchVo;
import com.spt.bas.report.client.vo.RptContractDateVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractSaveService;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.*;
import com.spt.bas.server.util.BasBusinessUtil;
import com.spt.bas.server.util.FileUtil;
import com.spt.bas.server.util.SptDateUtils;
import com.spt.pm.service.IBsKeySequenceService;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class CtrContractUpdateServiceImpl implements ICtrContractUpdateService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final ScheduledExecutorService SCHEDULED_POOL = Executors.newScheduledThreadPool(10);
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private ApplyDcsxDao applyDcsxDao;
    @Autowired
    private CtrProductDao ctrProductDao;
    @Autowired
    private IApplyInvoiceService applyInvoiceService;
    @Autowired
    private ICtrContractOphisService contractHisService;
    @Autowired
    private ICtrContractSaveService ctrContractSaveService;
    @Autowired
    private ICtrContractApplyService ctrContractApplyService;
    @Autowired
    private CtrContractApplyDao ctrContractApplyDao;
    @Autowired
    private IBsKeySequenceService bsKeySequenceService;
    @Autowired
    private CtrServiceContractDao ctrServiceContractDao;
    @Autowired
    private BudgetSettlementDao budgetSettlementDao;
    @Autowired
    private IApplyReceiveService applyReceiveService;
    @Autowired
    private ApplyConfirmReceiptDao applyConfirmReceiptDao;
    @Autowired
    private IBsProductConfigService bsProductConfigService;
    @Autowired
    private IApplyProductDetailService applyProductDetailService;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private CtrContractSettlementDao settlementDao;
    @Autowired
    private IRptCtrContractReceiveDetailClient contractReceiveDetailClient;
    @Autowired
    private IBudgetSettlementService budgetSettlementService;
    @Value("${file.server.url}")
    private String fileServerUrl;
    @Autowired
    private IInsuranceAmountFlowClient iInsuranceAmountFlowClient;
    @Resource
    private IBsCompanyDcsxClient bsCompanyDcsxClient;
    @Resource
    private CtrLogisticsDao ctrLogisticsDao;
    @Autowired
    private ApplyDeliveryOutDao applyDeliveryOutDao;
    @Resource
    private BsCompanyCreditDao bsCompanyCreditDao;

    @Override
    @ServerTransactional
    public void refreshProdutsName() {
        List<CtrContract> lstCtr = (List<CtrContract>) ctrContractDao.findAll();
        for (CtrContract ctr : lstCtr) {
            List<CtrProduct> lstProd = ctrProductDao.findByCtrContractId(ctr.getId());
            StringBuffer productsName = new StringBuffer();
            for (Iterator<CtrProduct> it = lstProd.iterator(); it.hasNext(); ) {
                CtrProduct prod = (CtrProduct) it.next();
                productsName.append(prod.getProductName()).append("/").append(prod.getBrandNumber()).append("/")
                        .append(prod.getFactoryName());
                if (it.hasNext()) {
                    productsName.append(",");
                }
            }
            ctr.setProductsName(productsName.toString());
            ctrContractDao.save(ctr);
        }

    }


    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        ctrContractDao.updateFileId(id, fileId);
    }

    @Override
    @ServerTransactional
    public void updateDebtCertificateFileId(Long id, String debtCertificateFileId) {
        if (StringUtils.isBlank(debtCertificateFileId)){
            return;
        }
        ctrContractDao.updateDebtCertificateFileId(id, debtCertificateFileId);
        refreshFactorStatus(id);
    }

    @Override
    @ServerTransactional
    public void updateCtrFileId(Long id, String fileId) {
        CtrContract entity = ctrContractService.getEntity(id);
        if (BasConstants.CONTRACT_STATUS_S.equals(entity.getContractType())) {
            ctrContractDao.updateSellFileId(id,fileId);
        } else if (BasConstants.CONTRACT_STATUS_B.equals(entity.getContractType())) {
            ctrContractDao.updateBuyFileId(id,fileId);
        }else if(BasConstants.BUSINESS_TYPE_BL.equals(entity.getBusinessTypeDcsx())){

        }
    }

    @Override
    @ServerTransactional
    public void updateInvoiceFileId(Long id, String fileId) {
        try {
            ctrContractDao.updateInvoiceFileId(id, fileId);
            CtrContract contract = ctrContractDao.findOne(id);
            ContractStatusResponseVo vo = new ContractStatusResponseVo();
            vo.setContractNo(contract.getContractNo());
            vo.setFileId(fileId);
            vo.setType(BasConstants.APPLY_TYPE_N);
            vo.setEnterpriseId(contract.getEnterpriseId());
            vo.setOnLineFlg(contract.getOnLineFlg());
            vo.setCreditFlg(contract.getCreditFlg());
            vo.setContractStatus(contract.getContractStatus());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    @ServerTransactional
    public void updateWarehouseFileId(Long id, String fileId) {
        try {
            ctrContractDao.updateWarehouseFileId(id, fileId);
            CtrContract contract = ctrContractDao.findOne(id);
            ContractStatusResponseVo vo = new ContractStatusResponseVo();
            vo.setContractNo(contract.getContractNo());
            vo.setFileId(fileId);
            vo.setType(BasConstants.APPLY_TYPE_G);
            vo.setEnterpriseId(contract.getEnterpriseId());
            vo.setOnLineFlg(contract.getOnLineFlg());
            vo.setCreditFlg(contract.getCreditFlg());
            vo.setContractStatus(contract.getContractStatus());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    @ServerTransactional
    public void updateDoubleCheckFileId(Long id, String fileId) {
        CtrContract contract = ctrContractDao.findOne(id);
        if (contract != null) {
            String contractFileId = contract.getFileId();
            if (StringUtils.isBlank(contractFileId)) {
                contract.setFileId(fileId);
            } else {
                contract.setFileId(contractFileId + fileId);
            }
            contract.setDoubleCheckFileId(fileId);
            ctrContractDao.save(contract);
        }
        //ctrContractDao.updateDoubleCheckFileId(id, fileId);
    }

    /**
     * 生成合同状态
     */
    @Override
    public CtrContract setContractStatus(CtrContract contract) {
        String contractStatus = BasConstants.CONTRACTSTATUS_S;
        if (!contract.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            // 审批未完成
            return contract;
        }
        if (contract.getContractStatus().equals(BasConstants.CONTRACTSTATUS_C)) {
            // 合同作废
            return contract;
        }
        BigDecimal totalNumber = contract.getTotalNumber();
        BigDecimal totalAmount = contract.getTotalAmount();
        BigDecimal interestAmount = contract.getInterestAmount();
        interestAmount = interestAmount == null ? BigDecimal.ZERO : interestAmount;
        //实际合同总价 = 合同总价 + 合同罚息
        totalAmount = totalAmount.add(interestAmount);
        BigDecimal dealedAmount = contract.findRealDealedAmount();// 已收付款金额
        BigDecimal billedAmount = contract.getBilledAmount();// 已开票已收票金额
        BigDecimal warehouseNumber = contract.getWarehouseNumber();// 已出库已入库数量
        if (contract.getContractType().equals(BasConstants.CONTRACT_TYPE_B)) {
            // 采购合同
            if (totalAmount.compareTo(billedAmount) <= 0) {
                contractStatus = BasConstants.CONTRACTSTATUS_V1;// 已收票
            } else if (totalNumber.compareTo(warehouseNumber) <= 0) {
                contractStatus = BasConstants.CONTRACTSTATUS_G1;// 已收货
            } else if (totalAmount.compareTo(dealedAmount) <= 0) {
                contractStatus = BasConstants.CONTRACTSTATUS_F1;// 已付款
            }
        } else {
            // 销售合同
            if (totalAmount.compareTo(billedAmount) <= 0) {
                contractStatus = BasConstants.CONTRACTSTATUS_V2;// 已开票
            } else if (totalNumber.compareTo(warehouseNumber) <= 0) {
                contractStatus = BasConstants.CONTRACTSTATUS_G2;// 已发货
            } else if (totalAmount.compareTo(dealedAmount) <= 0) {
                contractStatus = BasConstants.CONTRACTSTATUS_F2;// 已收款
            }
        }
        contract.setContractStatus(contractStatus);
        return contract;
    }

    /**
     * 已退款
     */
    @Override
    @ServerTransactional
    public void addrefundAmount(Long contractId, BigDecimal dealAmount, String approveNo, String refundType) throws ApplicationException {
        CtrContract contract = ctrContractDao.findOne(contractId);
        //退款金额
        BigDecimal refundAmount = dealAmount.add(contract.getRefundAmount());
        contract.setRefundAmount(refundAmount);
        contract.setDealedAmount(contract.getDealedAmount().subtract(dealAmount));
        ctrContractDao.save(contract);
    }

    @Override
    @ServerTransactional
    public void addServiceAmount(Long contractId, BigDecimal dealAmount, String approveNo) {
        CtrContract contract = ctrContractDao.findOne(contractId);
        BigDecimal receiveServiceAmount = contract.getReceiveServiceAmount();
        receiveServiceAmount = receiveServiceAmount.add(dealAmount).add(contract.getReceiveBreachAmount());

        // 修改服务合同收款字段（与合同表数据同步）
        CtrServiceContract serviceContract = ctrServiceContractDao.findByCtrContractId(contractId);

        if (receiveServiceAmount.compareTo(contract.getServiceAmount()) >= 0) {
            // 付清货款 在抵逾期罚息
            serviceContract.setDealedAmount(contract.getServiceAmount());
            contract.setReceiveServiceAmount(contract.getServiceAmount());
            BigDecimal subtract = receiveServiceAmount.subtract(contract.getServiceAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);
            contract.setReceiveBreachAmount(subtract);
        } else {
            // 先付货款
            serviceContract.setDealedAmount(receiveServiceAmount);
            contract.setReceiveServiceAmount(receiveServiceAmount);
        }

        // 结清服务费 修改状态
        BigDecimal breachAmount = contract.getBreachAmount();
        BigDecimal receiveBreachAmount = contract.getReceiveBreachAmount();
        // 两票制只有 已收服务费用+已收逾期罚息 大于等于 服务合同总价+逾期罚息，才会更新
        if ((receiveServiceAmount.add(receiveBreachAmount)).compareTo(contract.getServiceAmount().add(breachAmount)) >= 0) {
            // 更新服务合同结束时间
            serviceContract.setContractEndTime(new Date());
        }
        refreshStatus(contract);

        ctrServiceContractDao.save(serviceContract);

        ctrContractDao.save(contract);
    }

    /**
     * 更新销售合同逾期罚息
     * 2021-04-14 修改
     * 如果违约，根据收款单实际收款日期，分批次计算逾期罚息后汇总
     * 2021-05-14 修改
     * 如果违约，根据收款单实际收款日期和确认收货单上实际确认收货日期，分批次计算逾期罚息后汇总
     */
    @Override
    @ServerTransactional
    public void updateSellContractInterest(CtrContract sellContract, BudgetSettlement settlement) throws ApplicationException {
        // 1.获取所有收款单(按实际收款日期升序排序)
        List<ApplyReceive> applyReceives = this.getApplyReceives(sellContract.getId());

        // 记录已发起的确认收货金额
        BigDecimal allConfirmReceivedAmount = BigDecimal.ZERO;

        // 2.获取所有确认收货单(按实际收款日期升序排序)
        Map r = this.getApplyConfirmReceipts(sellContract, allConfirmReceivedAmount);

        // 所有确认收货单
        List<ActualConfirmReceive> actualConfirmReceives = (List<ActualConfirmReceive>) r.get("actualConfirmReceives");

        // 已发起的确认收货金额
        allConfirmReceivedAmount = (BigDecimal) r.get("allConfirmReceivedAmount");

        logger.info("allConfirmReceivedAmount:{}", allConfirmReceivedAmount);

        // 总逾期罚息
        BigDecimal sumBreachAmount = BigDecimal.ZERO;
        // 总收款
        BigDecimal sumReceiveAmount = BigDecimal.ZERO;

        // 最近的逾期日期
        long overDay = 0;

        // 逾期罚息信息记录
        BreachInfoRecord breachInfoRecord = new BreachInfoRecord(sumReceiveAmount, sumBreachAmount, overDay);

        // 3.计算有确认收货单的情况
        breachInfoRecord = this.calConfirmReceives(breachInfoRecord, actualConfirmReceives, applyReceives, settlement);

        // 4.计算未做确认收货单的情况
        // 未做确认收货单，约定收款日期为合同上的收款日期
        if (actualConfirmReceives.isEmpty()) {
            logger.info("未确认收货=================");
            breachInfoRecord = this.calNoConfirmReceives(breachInfoRecord, sellContract, applyReceives, settlement);
        }

        // 5.总收款小于总金额 计算余下逾期罚息
        BigDecimal sumReceive = breachInfoRecord.getSumReceiveAmount();
        if (Objects.nonNull(sumReceive) && sumReceive.compareTo(sellContract.getTotalAmount()) < 0) {
            breachInfoRecord = this.calOtherBreachAmount(breachInfoRecord,sellContract, actualConfirmReceives, settlement, allConfirmReceivedAmount);
        }

        BigDecimal breachAmount = breachInfoRecord.getSumBreachAmount();
        BigDecimal receiveBreachAmount = sellContract.getReceiveBreachAmount();
        logger.info("contractNo:{},breachAmount:{},receiveBreachAmount:{}", sellContract.getContractNo(), breachAmount, receiveBreachAmount);
        if (breachAmount.compareTo(BigDecimal.ZERO) == 0 && receiveBreachAmount.compareTo(BigDecimal.ZERO) > 0) {
            breachAmount = receiveBreachAmount;
        }

        sellContract.setBreachAmount(breachAmount);
        sellContract.setBreachDays(breachInfoRecord.getOverDay());
        sellContract.setOrverdurFlg(breachInfoRecord.getOverDay() > 0);
        Optional<Date> maxReceiveDate = applyReceives.stream()
                .map(ApplyReceive::getReceiveDate)
                .max(Date::compareTo);
        if (maxReceiveDate.isPresent()) {
            Date maxDate = maxReceiveDate.get();
            sellContract.setRealPayFullTime(maxDate);
        }
        ctrContractDao.save(sellContract);

    }

    /**
     * 总收款小于总金额 计算余下逾期罚息
     *
     * @param breachInfoRecord             逾期罚息信息记录
     * @param sellContract             销售合同
     * @param actualConfirmReceives    确认收货单
     * @param settlement
     * @param allConfirmReceivedAmount 已发起的确认收货金额
     * @return
     */
    private BreachInfoRecord calOtherBreachAmount(
            BreachInfoRecord breachInfoRecord,
            CtrContract sellContract,
            List<ActualConfirmReceive> actualConfirmReceives,
            BudgetSettlement settlement,
            BigDecimal allConfirmReceivedAmount) {

        BigDecimal sumReceiveAmount = breachInfoRecord.getSumReceiveAmount();
        BigDecimal sumBreachAmount = breachInfoRecord.getSumBreachAmount();
        long overDay = breachInfoRecord.getOverDay();

        Date now = new Date();
        // =========如果没有确认收货审批按合同上约定的收款日期计算

        // 合同收付款日期
        Date payFullTime = sellContract.getPayFullTime();
        // 约定收付全款日期
        Date appointPayFullTime = sellContract.getAppointPayFullTime();
        // 更新违约信息时，优先根据约定收付全款日期计算
        Date compareDate = Objects.nonNull(appointPayFullTime) ? appointPayFullTime : payFullTime;
        logger.info("合同约定收款日期：{}", compareDate);
        if (actualConfirmReceives.isEmpty()) {
            if (now.after(compareDate)) {
                // 本次收款
                try {
                    BigDecimal noPayed = sellContract.getTotalAmount().subtract(sumReceiveAmount);
                    Long compareDays = DateOperator.compareDays(SptDateUtils.formatterDate(compareDate), SptDateUtils.formatterDate(now));
                    overDay = Math.max(overDay, compareDays);
                    logger.info("compareDays:{}", compareDays);
                    BigDecimal breachAmount = noPayed.multiply(new BigDecimal(compareDays)).multiply(settlement.getBreachRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    sumBreachAmount = sumBreachAmount.add(breachAmount);
                }catch (Exception e){
                    logger.error("逾期罚息计算异常:{}", e);
                }
            }
        } else {
            // 遍历收货确认单
            // 分批次收货对应应收总计
            BigDecimal sumReceiveCost = BigDecimal.ZERO;
            for (int i = 0; i < actualConfirmReceives.size(); i++) {
                logger.info("分批次收货对应应收总计:{}", sumReceiveCost);
                Date confirmReceiveTime = actualConfirmReceives.get(i).getReceiveTime();
                BigDecimal receiveCost = actualConfirmReceives.get(i).getReceiveCost();
                sumReceiveCost = sumReceiveCost.add(receiveCost);
                logger.info("分批次收货对应应收总计:{}", sumReceiveCost);
                logger.info("第{}笔收货确认单,收款日期:{},应收款金额:{}", i + 1, confirmReceiveTime, receiveCost);
                if (sumReceiveAmount.compareTo(sumReceiveCost) < 0) {
                    if (now.after(confirmReceiveTime)) {
                        // 本次收款
                        BigDecimal noPayed = sumReceiveCost.subtract(sumReceiveAmount);
                        logger.info("第{}笔收货对应未收款:{}", i + 1, noPayed);
                        Long compareDays = DateOperator.compareDays(SptDateUtils.formatterDate(confirmReceiveTime), SptDateUtils.formatterDate(now));
                        overDay = Math.max(overDay, compareDays);
                        logger.info("compareDays:{}", compareDays);
                        BigDecimal breachAmount = noPayed.multiply(new BigDecimal(compareDays)).multiply(settlement.getBreachRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
                        logger.info("第{}笔收货确认单,对应产生逾期罚息:{}", i + 1, breachAmount);
                        sumBreachAmount = sumBreachAmount.add(breachAmount);
                    }
                }
            }
        }

        // 3======================================================================================

        // 剩余未确认收货的货物按原合同预定收全款日期计算
        logger.info("记录已发起的确认收货金额:{}", allConfirmReceivedAmount);
        if (allConfirmReceivedAmount.compareTo(BigDecimal.ZERO) > 0 && allConfirmReceivedAmount.compareTo(sellContract.getTotalAmount()) < 0) {
            if (now.after(compareDate)) {
                // 剩余未确认收货金额
                BigDecimal subtract = sellContract.getTotalAmount().subtract(allConfirmReceivedAmount);
                Long compareDays = DateOperator.compareDays(SptDateUtils.formatterDate(compareDate), SptDateUtils.formatterDate(now));
                overDay = Math.max(overDay, compareDays);
                logger.info("compareDays:{}", compareDays);
                BigDecimal breachAmount = subtract.multiply(new BigDecimal(compareDays)).multiply(settlement.getBreachRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
                logger.info("未确认收货订单,对应产生逾期罚息:{}", breachAmount);
                sumBreachAmount = sumBreachAmount.add(breachAmount);
            }
        }
        breachInfoRecord.setOverDay(overDay);
        breachInfoRecord.setSumBreachAmount(sumBreachAmount);
        breachInfoRecord.setSumReceiveAmount(sumReceiveAmount);
        return breachInfoRecord;
    }

    /**
     * 计算没有收货确认单的情况
     * 未做确认收货单，约定收款日期为合同上的收款日期
     *
     * @param breachInfoRecord  逾期罚息信息记录
     * @param sellContract     销售合同
     * @param applyReceives    收款列表
     * @param settlement
     * @return
     */
    private BreachInfoRecord calNoConfirmReceives(BreachInfoRecord breachInfoRecord,
                                                  CtrContract sellContract,
                                                  List<ApplyReceive> applyReceives,
                                                  BudgetSettlement settlement) {

        BigDecimal sumReceiveAmount = breachInfoRecord.getSumReceiveAmount();
        BigDecimal sumBreachAmount = breachInfoRecord.getSumBreachAmount();
        long overDay = breachInfoRecord.getOverDay();

        // 合同收付全款日期
        Date payFullTime = sellContract.getPayFullTime();
        // 约定收付全款日期
        Date appointPayFullTime = sellContract.getAppointPayFullTime();
        // 更新违约信息时，优先根据约定收付全款日期计算
        Date compareDate = Objects.nonNull(appointPayFullTime) ? appointPayFullTime : payFullTime;

        BigDecimal shouldReceiveCost = sellContract.getTotalAmount();
        for (int i = 0; i < applyReceives.size(); i++) {
            logger.info("开始遍历收款单:{}", i + 1);
            // 本次收款
            BigDecimal receiveAmount = applyReceives.get(i).getReceiveAmount();
            Date receiveDate = applyReceives.get(i).getReceiveDate();
            logger.info("第{}次收款,收款金额:{},收款日期:{}", i + 1, receiveAmount, receiveDate);
            sumReceiveAmount = sumReceiveAmount.add(receiveAmount);
            logger.info("加上本次收款金额，共收款:{}", sumReceiveAmount);
            logger.info("第{}次收款金额:{},收款日期:{}", i + 1, receiveAmount, receiveDate);
            // 判断本次收款是否逾期
            if (receiveDate.after(compareDate)) {
                logger.info("第{}笔收款逾期了,收款日期:{},应收款金额:{}", i + 1, compareDate, shouldReceiveCost);
                Long compareDays = DateOperator.compareDays(SptDateUtils.formatterDate(compareDate), SptDateUtils.formatterDate(receiveDate));
                overDay = Math.max(overDay, compareDays);
                logger.info("逾期天数:{}", compareDays);
                // 应计算罚金本金 （合计应收 - 合计实收）
                BigDecimal thisTimeBreach = BigDecimal.ZERO;
                logger.info("总实收:{}", sumReceiveAmount);
                logger.info("总应收:{}", shouldReceiveCost);
                thisTimeBreach = receiveAmount;
                logger.info("应计算罚金金额thisTimeBreach:{}", thisTimeBreach);
                // 应计算罚金本金金额大于0
                if (thisTimeBreach.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal breachAmount = thisTimeBreach.multiply(new BigDecimal(compareDays)).multiply(settlement.getBreachRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    logger.info("本次所收逾期罚息：{}", breachAmount);
                    sumBreachAmount = sumBreachAmount.add(breachAmount);
                }
            }
        }
        breachInfoRecord.setOverDay(overDay);
        breachInfoRecord.setSumBreachAmount(sumBreachAmount);
        breachInfoRecord.setSumReceiveAmount(sumReceiveAmount);
        return breachInfoRecord;
    }

    /**
     * 计算有收货确认单情况下的逾期
     * @param actualConfirmReceives 收货单列表
     * @param applyReceives  收款单列表
     * @param settlement
     * @return
     */
    private BreachInfoRecord calConfirmReceives(BreachInfoRecord breachInfoRecord,
                                    List<ActualConfirmReceive> actualConfirmReceives,
                                    List<ApplyReceive> applyReceives,
                                    BudgetSettlement settlement) {
        BigDecimal sumReceiveAmount = breachInfoRecord.getSumReceiveAmount();
        BigDecimal sumBreachAmount = breachInfoRecord.getSumBreachAmount();
        long overDay = breachInfoRecord.getOverDay();
        // 总应收
        BigDecimal confirmReceivedCost = BigDecimal.ZERO;
        for (int i = 0; i < actualConfirmReceives.size(); i++) {
            // 总收款
            sumReceiveAmount = BigDecimal.ZERO;

            // 每批次待收
            BigDecimal thisConfrimCost = BigDecimal.ZERO;
            logger.info("开始遍历收货单:{}", i + 1);
            // 每批次应收款日期
            Date confirmReceiveTime = actualConfirmReceives.get(i).getReceiveTime();
            // 每批次应收款金额
            BigDecimal receiveCost = actualConfirmReceives.get(i).getReceiveCost();

            thisConfrimCost = receiveCost;
            logger.info("第{}次收货单,待收金额:{}", i + 1, thisConfrimCost);

            confirmReceivedCost = confirmReceivedCost.add(receiveCost);
            logger.info("约定收货日期:{},应收款:{}", confirmReceiveTime, receiveCost);
            for (int i1 = 0; i1 < applyReceives.size(); i1++) {
                logger.info("开始遍历收款单:{}", i1 + 1);
                // 本次收款
                BigDecimal receiveAmount = applyReceives.get(i1).getReceiveAmount();
                Date receiveDate = applyReceives.get(i1).getReceiveDate();
                logger.info("第{}次收款,收款金额:{},收款日期:{}", i1+1, receiveAmount, receiveDate);
                sumReceiveAmount = sumReceiveAmount.add(receiveAmount);
                logger.info("加上本次收款金额，共收款:{}", sumReceiveAmount);
                logger.info("第{}次收货单,第{}次收款金额:{},收款日期:{}", i + 1, i1 + 1, receiveAmount,receiveDate);
                // 判断本次收款是否逾期
                if (receiveDate.after(confirmReceiveTime)) {
                    logger.info("第{}次收货单,第{}笔收款逾期了,收款日期:{},应收款金额:{}", i + 1, i1 + 1, confirmReceiveTime, receiveCost);
                    Long compareDays = DateOperator.compareDays(SptDateUtils.formatterDate(confirmReceiveTime), SptDateUtils.formatterDate(receiveDate));
                    overDay = Math.max(overDay, compareDays);
                    logger.info("逾期天数:{}", compareDays);
                    // 应计算罚金本金 （合计应收 - 合计实收）
                    BigDecimal thisTimeBreach = BigDecimal.ZERO;
                    logger.info("总实收:{}", sumReceiveAmount);
                    logger.info("对应应收:{}", receiveCost);
                    logger.info("总应收:{}", confirmReceivedCost);

                    if (thisConfrimCost.compareTo(BigDecimal.ZERO)<=0) {
                        logger.info("待收小于等于0，跳过");
                        continue;
                    }

                    // 判断除本次收款是否已完成截止当前批次的已收款 跳过
                    if (sumReceiveAmount.subtract(receiveAmount).compareTo(confirmReceivedCost) >= 0) {
                        logger.info("第{}次收货单,第{}次收款因条件一跳过", i + 1, i1 + 1);
                        continue;
                    }

                    // 总应收减去本批次应收大于等于总实收 跳过
                    if (confirmReceivedCost.subtract(receiveCost).compareTo(sumReceiveAmount) >= 0) {
                        logger.info("第{}次收货单,第{}次收款因条件二跳过", i + 1, i1 + 1);
                        continue;
                    }

                    // 总收款 - (总应收 - 本批次应收款) >= 0
                    if (sumReceiveAmount.subtract(confirmReceivedCost.subtract(receiveCost)).compareTo(BigDecimal.ZERO) >= 0) {
                        // 第一个批次，逾期本金按每次收款金额计算
                        if (i == 0) {
                            // 罚金金额 = 本批收款金额
                            thisTimeBreach = receiveAmount;
                            // 本批次待收金额 > 总收款 - (总应收-本批次应收款) && 只有一个批次
                        } else if (thisConfrimCost.compareTo(sumReceiveAmount.subtract(confirmReceivedCost.subtract(receiveCost))) > 0) {
                            // 罚金金额 = 总收款 - (总应收 - 本批次应收款)
                            thisTimeBreach = sumReceiveAmount.subtract(confirmReceivedCost.subtract(receiveCost));
                        } else {
                            // 罚金金额 = 本批次待收金额
                            thisTimeBreach = thisConfrimCost;
                        }
                    }

                    thisConfrimCost = thisConfrimCost.subtract(thisTimeBreach);
                    logger.info("第{}次收货单,待收金额:{}", i + 1, thisConfrimCost);
                    logger.info("应计算罚金金额thisTimeBreach:{}", thisTimeBreach);

                    // 应计算罚金本金金额大于0
                    if (thisTimeBreach.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal breachAmount = thisTimeBreach.multiply(new BigDecimal(compareDays)).multiply(settlement.getBreachRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
                        logger.info("本次所收逾期罚息：{}", breachAmount);
                        sumBreachAmount = sumBreachAmount.add(breachAmount);
                    }
                }else {
                    // 本次收款时没有违约
                    thisConfrimCost = thisConfrimCost.subtract(receiveAmount);
                }
            }
        }
        breachInfoRecord.setOverDay(overDay);
        breachInfoRecord.setSumBreachAmount(sumBreachAmount);
        breachInfoRecord.setSumReceiveAmount(sumReceiveAmount);
        return breachInfoRecord;
    }

    /**
     * 获取所有收款单并按实际收款日期升序排序
     * @param sellContractId
     * @return
     */
    private List<ApplyReceive> getApplyReceives(Long sellContractId) {
        // 获取所有收款单
        List<ApplyReceive> applyReceives = applyReceiveService.findListByContractIdAndStatus(sellContractId, BasConstants.APPROVE_STATUS_D);
        if (CollUtil.isNotEmpty(applyReceives)) {
            applyReceives.forEach(r -> {
                if (StringUtils.equals(BasConstants.PAY_MODE_H, r.getReceiveMode()) && Objects.nonNull(r.getBillDueTime())) {
                    r.setReceiveDate(r.getBillDueTime());
                }
            });

            // 收款单按实际收款日期升序排序
            applyReceives.sort(Comparator.comparing(ApplyReceive::getReceiveDate));
        }
        return applyReceives;
    }

    /**
     * 获取所有确认收货单并按实际交货日期升序排序
     * @param sellContract           销售合同
     * @param allConfirmReceivedAmount 已发起的确认收货金额
     * @return
     */
    private Map getApplyConfirmReceipts(CtrContract sellContract, BigDecimal allConfirmReceivedAmount) {
        Map r = new HashMap(2);
        // 合同回款周期
        Long creditCycle = sellContract.getCreditCycle();
        logger.info("合同回款周期:{}", creditCycle);
        // 获取所有确认收货单
        List<ApplyConfirmReceipt> applyConfirmReceipts = applyConfirmReceiptDao.findByContractId(sellContract.getId());
        // 记录每一批次对应的应收日期和应收款
        List<ActualConfirmReceive> actualConfirmReceives = new ArrayList<>(applyConfirmReceipts.size());
        Iterator<ApplyConfirmReceipt> it = applyConfirmReceipts.iterator();
        while (it.hasNext()) {
            ApplyConfirmReceipt confirmReceipt = it.next();
            if (!BasConstants.APPROVE_STATUS_D.equals(confirmReceipt.getStatus())) {
                it.remove();
                continue;
            }
            // 查询明细
            ApplyDeliveryApplyIdVo applyVo = new ApplyDeliveryApplyIdVo();
            applyVo.setApplyId(confirmReceipt.getId());
            applyVo.setApplyType(BasConstants.APPLY_TYPE_G);
            // 兼容多品种
            List<ApplyProductDetail> products = applyProductDetailService.findApplyId(applyVo);
            // 本次收货对应应收金额
            BigDecimal shouldPay = BigDecimal.ZERO;
            for (ApplyProductDetail product : products) {
                BigDecimal curTotalAmount = product.getCurNumber().multiply(product.getDealPrice());
                shouldPay = shouldPay.add(curTotalAmount);
            }
            allConfirmReceivedAmount = allConfirmReceivedAmount.add(shouldPay);
            if (confirmReceipt.getActualContractPayFullTime() == null && confirmReceipt.getConfirmReceiptDate() == null) {
                logger.info("confirmReceipt的ActualContractPayFullTime和ConfirmReceiptDate为空,跳过");
                continue;
            }
            Date actualContractPayFullTime = confirmReceipt.getActualContractPayFullTime() == null ? DateUtil.offsetDay(confirmReceipt.getConfirmReceiptDate(), (int) (creditCycle - 1L)) : confirmReceipt.getActualContractPayFullTime();
            ActualConfirmReceive actualConfirmReceive = new ActualConfirmReceive(actualContractPayFullTime, shouldPay);
            actualConfirmReceives.add(actualConfirmReceive);
        }

        // 按预定收款日期升序
        actualConfirmReceives.sort(Comparator.comparing(ActualConfirmReceive::getReceiveTime));
        r.put("actualConfirmReceives", actualConfirmReceives);
        r.put("allConfirmReceivedAmount", allConfirmReceivedAmount);
        return r;
    }

    /**
     * 逾期罚息信息记录
     */
    class BreachInfoRecord{
        private BigDecimal sumReceiveAmount;
        private BigDecimal sumBreachAmount;
        private long overDay;

        public BreachInfoRecord(BigDecimal sumReceiveAmount, BigDecimal sumBreachAmount, long overDay) {
            this.sumReceiveAmount = sumReceiveAmount;
            this.sumBreachAmount = sumBreachAmount;
            this.overDay = overDay;
        }

        public BigDecimal getSumReceiveAmount() {
            return sumReceiveAmount;
        }

        public void setSumReceiveAmount(BigDecimal sumReceiveAmount) {
            this.sumReceiveAmount = sumReceiveAmount;
        }

        public BigDecimal getSumBreachAmount() {
            return sumBreachAmount;
        }

        public void setSumBreachAmount(BigDecimal sumBreachAmount) {
            this.sumBreachAmount = sumBreachAmount;
        }

        public long getOverDay() {
            return overDay;
        }

        public void setOverDay(long overDay) {
            this.overDay = overDay;
        }
    }

    /**
     * 记录收货单上实际收货时间和对应应收金额
     */
    class ActualConfirmReceive {
        private Date receiveTime;
        private BigDecimal receiveCost;

        public Date getReceiveTime() {
            return receiveTime;
        }

        public void setReceiveTime(Date receiveTime) {
            this.receiveTime = receiveTime;
        }

        public BigDecimal getReceiveCost() {
            return receiveCost;
        }

        public void setReceiveCost(BigDecimal receiveCost) {
            this.receiveCost = receiveCost;
        }

        public ActualConfirmReceive(Date receiveTime, BigDecimal receiveCost) {
            this.receiveTime = receiveTime;
            this.receiveCost = receiveCost;
        }
    }


    /**
     * 已收付款金额
     *
     * @throws ApplicationException
     */
    @Override
    @ServerTransactional
    public void addDealedAmount(Long contractId, BigDecimal dealAmount, String approveNo, String payType, Date receiveDate) throws ApplicationException {
        CtrContract contract = ctrContractDao.findOne(contractId);
        // 已收/付款金额
        BigDecimal payed = dealAmount.add(contract.getDealedAmount()).add(contract.getReceiveBreachAmount());
        Boolean tpFlg = false;
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP,contract.getBusinessType())) {
            tpFlg = true;
            payed = dealAmount.add(contract.getDealedAmount());
        }
        // 采购合同 付款逻辑
        if (BasConstants.CONTRACT_TYPE_B.equals(contract.getContractType())) {
            // 采购合同 付款逻辑
            addDealedAmountWithContractTypeBuy(contract, payed);
        } else if (BasConstants.CONTRACT_TYPE_S.equals(contract.getContractType())) {
            BigDecimal dealedAmount = contract.getDealedAmount();

            // 销售合同 收款逻辑
            addDealedAmountWithContractTypeSell(contract, payed);
            boolean receiveFlg;
            if (payed.compareTo(contract.getTotalAmount()) >= 0 && !tpFlg) {

                Boolean updateRealPayDateFlg = true;
                if(dealAmount != null && dealedAmount.compareTo(contract.getTotalAmount()) >= 0) {
                    // 已收全款，当前收款为逾期罚息
                    updateRealPayDateFlg = false;
                } else {
                    // 查询收款日期最大值
                    RptContractDateSearchVo search = new RptContractDateSearchVo();
                    List<String> sellContractNoList = new ArrayList<>();
                    sellContractNoList.add(contract.getContractNo());
                    search.setSellContractNoList(sellContractNoList);
                    List<RptContractDateVo> sellReceiveDateList = contractReceiveDetailClient.selectSellReceiveDateList(search);
                    Map<String, Date> sellReceiveDateMap = sellReceiveDateList.stream()
                            .collect(Collectors.toMap(RptContractDateVo::getContractNo, RptContractDateVo::getSellReceiveDate));

                    Date sellReceiveDate = sellReceiveDateMap.get(contract.getContractNo());
                    // 收款日期最大值是否在当前收款日期之后
                    if(sellReceiveDate != null && sellReceiveDate.after(receiveDate)){
                        receiveDate = sellReceiveDate;
                    }
                }
                if(updateRealPayDateFlg) {
                    contract.setRealPayFullTime(receiveDate);
                }
                receiveFlg = true;
            } else {
                contract.setRealPayFullTime(null);
                receiveFlg = false;
            }
            settlementDao.updateSettlementReceiveFlg(contractId, receiveFlg);
        }
        BigDecimal breachAmount = contract.getBreachAmount();
        BigDecimal receiveBreachAmount = contract.getReceiveBreachAmount();
        // 更新fondFlg字段
        if (payed.compareTo(contract.getTotalAmount()) >= 0 && !tpFlg) {
            // 一票制
            if ("0".equals(contract.getSettlementType())) {
                // 已付 大于 合同总金额 ==> 不计算逾期
                // 更新结算表字段
                BudgetSettlement settlement = budgetSettlementDao.findBySellContractIdAndBudgetFinishStatus(contract.getId(), "0");
                if (settlement != null && settlement.getRealPayFullTime() == null) {
                    settlement.setRealPayFullTime(new Date());
                    // 逾期中
                    if ("1".equals(settlement.getBudgetStatus())) {
                        settlement.setBudgetStatus("4");
                        budgetSettlementDao.save(settlement);
                    }
                }
                // 已付+已付逾期罚息 大于等于 合同总金额+逾期罚息 ==>完成支付
                if (contract.getDealedAmount().add(receiveBreachAmount).compareTo(contract.getTotalAmount().add(breachAmount)) >= 0) {
                    contract.setFondFlg(true);
                    // 完成支付货款
                    contract.setDealedFlg(true);
                }
            } else {
                // 两票制
                contract.setFondFlg(true);
                // 完成支付货款
                contract.setDealedFlg(true);
                // 更新结算表字段
                BudgetSettlement settlement = budgetSettlementDao.findBySellContractIdAndBudgetFinishStatus(contract.getId(), "0");
                if (settlement != null && settlement.getRealPayFullTime() == null) {
                    settlement.setRealPayFullTime(new Date());
                    // 逾期中
                    if ("1".equals(settlement.getBudgetStatus())) {
                        settlement.setBudgetStatus("4");
                        budgetSettlementDao.save(settlement);
                    }
                }
            }
        } else {
            // 更新结算单状态为未完成
            BudgetSettlement budgetSettlement = budgetSettlementDao.findBySellContractId(contract.getId());
            if (Objects.nonNull(budgetSettlement) && !StringUtils.equals("0", budgetSettlement.getBudgetFinishStatus())) {
                budgetSettlement.setBudgetFinishStatus("0");
                budgetSettlement.setBudgetStatus("0");
                budgetSettlementDao.save(budgetSettlement);
                logger.info("合同收款金额更新后，更新结算单状态，contractNo:{}", contract.getContractNo());
            }

            contract.setFondFlg(false);
            contract.setDealedFlg(false);
            // 更新 销售合同前台显示状态字段
            rollbackContractStatusWx(contract);
        }

        refreshStatus(contract);
        ctrContractDao.save(contract);

        refreshVirtualContract(contract);
        this.refreshBreachAmount(contract);
    }

    @Override
    @ServerTransactional
    public void addReceiveAmount(ApplyReceive entity, String approveNo, Boolean withdrawFlg) throws ApplicationException {
        Long contractId = entity.getContractId();
        String receiveType = entity.getReceiveType();
        String receiveMode = entity.getReceiveMode();
        BigDecimal receiveAmount = entity.getReceiveAmount();
        BigDecimal discountAmount = entity.getDiscountAmount();
        Date receiveDate = entity.getReceiveDate();
        CtrContract ctrContract = ctrContractDao.findByContractNo(entity.getContractNo());
        BigDecimal currReceiveAmount = Boolean.TRUE.equals(withdrawFlg) ? receiveAmount : receiveAmount.negate();
        BigDecimal currDiscountAmount = Boolean.TRUE.equals(withdrawFlg) ? discountAmount : discountAmount.negate();
        if (StringUtils.equals(BasConstants.PAY_TYPE_T, receiveType)) {
            // 贴现费用
            ctrContract.setDiscountReceiveAmount(ctrContract.getDiscountReceiveAmount().add(currReceiveAmount));
            ctrContractDao.save(ctrContract);
        } else if (StringUtils.equals(BasConstants.PAY_MODE_H, receiveMode)) {
            // 票汇
            ctrContract.setDiscountChargeTarget(entity.getDiscountTarget());
            ctrContract.setDiscountChargeAmount(ctrContract.getDiscountChargeAmount().add(currDiscountAmount));
            ctrContractDao.save(ctrContract);

            this.addDealedAmount(contractId, currReceiveAmount, approveNo, receiveType, receiveDate);
        } else {
            this.addDealedAmount(contractId, currReceiveAmount, approveNo, receiveType, receiveDate);
        }
    }

    /**
     * 回滚 背靠背业务 销售合同 付款完成状态
     *
     * @param entity
     * @return
     */
    private CtrContract rollbackContractStatusWx(CtrContract entity) {
        refreshStatus(entity);
        return entity;
    }

    /**
     * 采购合同 付款逻辑
     *
     * @param contract 采购合同
     * @param payed    加上本次付款金额的总金额
     */
    private void addDealedAmountWithContractTypeBuy(CtrContract contract, BigDecimal payed) throws ApplicationException {
        // todo 付款逻辑校验 总付款金额不能大于总收款金额
        if (checkPayedMoney(contract)) {
            throw new ApplicationException("总付款金额不能大于总收款金额");
        }
        contract.setDealedAmount(payed);
    }

    /**
     * 付款逻辑校验 总付款金额不能大于总收款金额
     *
     * @param contract
     * @return
     */
    private boolean checkPayedMoney(CtrContract contract) {
        // todo
        return false;
    }

    /**
     * 销售合同 收款逻辑
     *
     * @param contract   销售合同
     * @param payed      加上本次收款金额的总金额
     */
    private void addDealedAmountWithContractTypeSell(CtrContract contract, BigDecimal payed) {
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP,contract.getBusinessType())) {
            contract.setDealedAmount(payed);
        } else {
            // 白条 一票制 销售合同 考虑逾期罚息
            String settlementType = contract.getSettlementType();
            if (StringUtils.equals(BasConstants.SETTLEMENT_TYPE_ONE, settlementType)) {
                // 合同总价
                BigDecimal totalAmount = contract.getTotalAmount();
                // 先扣货款
                if (payed.compareTo(BigDecimal.ZERO) <= 0) {
                    contract.setDealedAmount(BigDecimal.ZERO);
                    contract.setReceiveBreachAmount(BigDecimal.ZERO);
                } else if (payed.compareTo(totalAmount) >= 0) {
                    // 已收金额
                    contract.setDealedAmount(totalAmount);
                    // 多出金额加到已付逾期罚息
                    BigDecimal subtract = payed.subtract(totalAmount).setScale(2, RoundingMode.HALF_UP);
                    contract.setReceiveBreachAmount(subtract);
                } else {
                    contract.setDealedAmount(payed);
                }
            } else if (StringUtils.equals(BasConstants.SETTLEMENT_TYPE_TWO, settlementType)) {
                // 白条 两票制 销售合同不考虑逾期罚息 算在服务合同上
                contract.setDealedAmount(payed);
            } else {
                // 代采
                contract.setDealedAmount(payed);
            }
        }
        setContractStatus(contract);
    }

    /**
     * 已开票已收票金额
     *
     * @throws ApplicationException
     */
    @Override
    @ServerTransactional
    public void addBilledAmount(Long contractId, BigDecimal dealAmount,Date inInvoiceDate, String approveNo) throws ApplicationException {
        CtrContract contract = ctrContractDao.findOne(contractId);
        // 已收票
        BigDecimal billedAmount = contract.getBilledAmount().add(dealAmount);

        contract.setBilledAmount(billedAmount);
        contract.setInvoiceDate(inInvoiceDate.toString());
        contract = setContractStatus(contract);
        if (billedAmount.compareTo(contract.getTotalAmount()) >= 0) {
            contract.setBillFlg(true);
            contract.setBilledFlg(true);
        } else {
            contract.setBillFlg(false);
            contract.setBilledFlg(false);
        }
        refreshStatus(contract);
        ctrContractDao.save(contract);
        refreshVirtualContract(contract);
    }


    /**
     * 已入\出库数量
     *
     * @throws ApplicationException
     */
    @Override
    @ServerTransactional
    public void addWarehouseNumber(Long contractId, BigDecimal dealAmount, String approveNo, Date warehouseDate) throws ApplicationException {
        CtrContract contract = ctrContractDao.findOne(contractId);
        // 已收货
        BigDecimal curRealNumber = contract.getWarehouseNumber().add(dealAmount);
        if (contract.getContractType().equals(BasConstants.CONTRACT_TYPE_B)) {
            if (curRealNumber.compareTo(contract.getTotalNumber()) >= 0) {
                contract.setProductStatus(BasConstants.PRODUCT_STATUS_I);// 全部入库
            } else {
                contract.setProductStatus(BasConstants.PRODUCT_STATUS_PI);// 部分入库
            }

        } else {
            if (curRealNumber.compareTo(contract.getTotalNumber()) >= 0) {
                contract.setProductStatus(BasConstants.PRODUCT_STATUS_O);// 全部出库
            } else {
                contract.setProductStatus(BasConstants.PRODUCT_STATUS_PO);// 部分出库
            }
        }

        contract.setWarehouseNumber(curRealNumber);
        contract = setContractStatus(contract);
        if (curRealNumber.compareTo(contract.getTotalNumber()) >= 0) {
            contract.setWarehouseFlg(true);
            // 更新 发货时间
            contract.setDeliveryDateFrom(warehouseDate);
        } else {
            contract.setWarehouseFlg(false);
        }
        // 刷新合同最晚开票日期
        refreshLatestBillDate(contract, warehouseDate);
        ctrContractDao.save(contract);
        refreshVirtualContract(contract);
    }

    /**
     * 已确认收货数量
     *
     * @throws ApplicationException
     */
    @Override
    @ServerTransactional
    public void addConfirmReceiptNumber(Long contractId, BigDecimal dealAmount,Date confirmReceiptDate, String approveNo) throws ApplicationException {
        CtrContract contract = ctrContractDao.findOne(contractId);
        BigDecimal confirmReceiveNumber = BigDecimal.ZERO;
        if (contract.getConfirmReceiveNumber() != null) {
            confirmReceiveNumber = contract.getConfirmReceiveNumber();
        }
        // 已收货
        BigDecimal curRealNumber = confirmReceiveNumber.add(dealAmount);
        if (contract.getContractType().equals(BasConstants.CONTRACT_TYPE_S)) {
            contract.setConfirmReceiveNumber(curRealNumber);
            contract = setContractStatus(contract);
            boolean confirmFlg;
            if (curRealNumber.compareTo(contract.getTotalNumber()) >= 0) {
                contract.setConfirmReceiptFlg(true);
                contract.setConfirmDate(confirmReceiptDate);
                confirmFlg = true;
                // 首次全部确认收货生成保费扣费流水
                insuranceAmountDeduct(contract);
            } else {
                contract.setConfirmReceiptFlg(false);
                contract.setConfirmDate(null);
                confirmFlg = false;
            }
            refreshStatus(contract);
            ctrContractDao.save(contract);
            settlementDao.updateSettlementConfirmFlg(contractId, confirmFlg);
        }

    }



    /**
     * 1更新合同确认收货数量
     * 2添加合同操作流水
     * 3更新付全款时间
     *
     * @throws ApplicationException
     */
    @Override
    @ServerTransactional
    public void updateConfirmReceiveNumber(CtrContractOphisRequest request) throws ApplicationException {
        if (request != null) {
            CtrContract contract = ctrContractDao.findOne(request.getCtrContractId());
            //修改合同确认收货数量
            BigDecimal confirmReceiveNumber = request.getDealNumber();
            String fileId = request.getFileId();
            BigDecimal confirmNumber = contract.getConfirmReceiveNumber() == null ? BigDecimal.ZERO : contract.getConfirmReceiveNumber();
            String warehouseFileId = contract.getWarehouseFileId() == null ? "" : contract.getWarehouseFileId();
            BigDecimal totalNumber = contract.getTotalNumber();
            confirmNumber = confirmNumber.add(confirmReceiveNumber);
            if (confirmNumber.compareTo(totalNumber) >= 0) {
                confirmNumber = totalNumber;
            }
            if (StringUtils.isBlank(warehouseFileId)) {
                warehouseFileId = fileId;
            } else {
                warehouseFileId = warehouseFileId + fileId;
            }
            contract.setConfirmReceiveNumber(confirmNumber);
            contract.setConfirmDate(new Date());
            if (null != request.getDeliveryInTime()) {
                contract.setConfirmDate(request.getDeliveryInTime());
            }
            contract.setWarehouseFileId(warehouseFileId);
            contract = ctrContractDao.save(contract);

            List<CtrProduct> ctrProductList = ctrProductDao.findByCtrContractId(contract.getId());
            StringBuffer productNameAndBrand = new StringBuffer("");
            String deliveryType = DictUtil.getValue(BasConstants.DICT_TYPE_BUYDELIVERY, contract.getDeliveryType());
            String confrim = NumberUtil.formatNumber(confirmReceiveNumber, "#.###");
            for (CtrProduct ctrProduct : ctrProductList) {
                String[] title = ctrProduct.getProductCd().split("_");
                if (title[0].equals("SL")) {
                    productNameAndBrand.append(ctrProduct.getProductName() + "/" + ctrProduct.getBrandNumber() + ",");
                } else {
                    productNameAndBrand.append(ctrProduct.getProductName() + ",");
                }
            }
            String productNameAndBrandStr = productNameAndBrand.toString();
            if (productNameAndBrand.length() > 0) {
                productNameAndBrandStr = productNameAndBrand.substring(0, productNameAndBrand.length() - 1);
            }
            productNameAndBrandStr = productNameAndBrandStr + "/" + deliveryType + "/" + confrim;

            String companyName = contract.getCompanyName();
            String subject = "";
            if (null != request.getDeliveryInTime()) {
                String deliveryInTimeStr = new SimpleDateFormat("yyyy-MM-dd").format(request.getDeliveryInTime());
                subject = String.format("%s %s %s", "[" + productNameAndBrandStr + "]", deliveryInTimeStr, companyName, confrim);
            } else {
                subject = String.format("%s %s %s", "[" + productNameAndBrandStr + "]", companyName, confrim);
            }


            //添加合同操作流水
            CtrContractOphisRequest ophis = new CtrContractOphisRequest();
            ophis.setApplyType(BasConstants.APPLY_TYPE_G);
            ophis.setCtrContractId(contract.getId());
            ophis.setCancel(false);
            ophis.setRemark(subject);
            ophis.setCreateUserId(request.getCreateUserId());
            ophis.setCreateUserName(request.getCreateUserName());
            ophis.setHappenDate(request.getHappenDate());
            contractHisService.addHis(ophis);

            ContractStatusResponseVo vo = new ContractStatusResponseVo();
            vo.setContractNo(contract.getContractNo());
            vo.setFileId(warehouseFileId);
            vo.setType(BasConstants.APPLY_TYPE_G);
            vo.setEnterpriseId(contract.getEnterpriseId());
            vo.setOnLineFlg(contract.getOnLineFlg());
            vo.setContractStatus(contract.getContractStatus());

            //更新付全款时间
            if (contract.getPayFullTime() == null) {
                ctrContractSaveService.refrshPayFullTime(contract.getId());
            }
        }

    }

    @Override
    @ServerTransactional
    public void doSigning(CtrContractSignRequest req) throws ApplicationException {
        Long contractId = req.getCtrContractId();
        ctrContractDao.updateContractStatusSign(contractId);

        CtrContractOphisRequest request = new CtrContractOphisRequest();
        request.setApplyType(BasConstants.APPLY_TYPE_SN);
        request.setCtrContractId(contractId);
        request.setRemark("合同签约");
        request.setCreateUserId(req.getUserId());
        request.setCreateUserName(req.getUserName());
        contractHisService.addHis(request);
    }

    /**
     * 修改合同预估运费仓储费和罚息并生成合同操作记录
     */
    @Override
    @ServerTransactional
    public void updateContractAmount(CtrContractUpdateVo updateVo) {
        Boolean updateFlg = false;
        CtrContract contract = ctrContractDao.findOne(updateVo.getId());
        BigDecimal contractInterestAmount = contract.getInterestAmount();
        contractInterestAmount = contractInterestAmount == null ? BigDecimal.ZERO : contractInterestAmount;
        BigDecimal transportAmount = updateVo.getTransportAmount();
        BigDecimal warehouseAmount = updateVo.getWarehouseAmount();
        BigDecimal interestAmount = updateVo.getInterestAmount();
        Long bizUserId = updateVo.getBizUserId();
        String bizUserName = updateVo.getBizUserName();
        StringBuilder oldAmountStr = new StringBuilder("[");
        StringBuilder newAmountStr = new StringBuilder("[");
        if (contract.getTransportAmount().compareTo(transportAmount) != 0) {
            updateFlg = true;
            oldAmountStr.append(String.format("%s %s", "预估运费:" + contract.getTransportAmount(), "\n"));
            newAmountStr.append(String.format("%s %s", "预估运费:" + transportAmount, "\n"));
        }
        if (contract.getWarehouseAmount().compareTo(warehouseAmount) != 0) {
            updateFlg = true;
            oldAmountStr.append(String.format("%s %s", "预估仓储费:" + contract.getWarehouseAmount(), "\n"));
            newAmountStr.append(String.format("%s %s", "预估仓储费:" + warehouseAmount, "\n"));
        }
        if (contractInterestAmount.compareTo(interestAmount) != 0) {
            updateFlg = true;
            oldAmountStr.append(String.format("%s", "罚息:" + contract.getInterestAmount()));
            newAmountStr.append(String.format("%s", "罚息:" + interestAmount));
        }
        oldAmountStr.append("]");
        newAmountStr.append("]");
        String remark = oldAmountStr + "-" + newAmountStr + bizUserName;

        if (updateFlg) {
            //1修改合同费用
            contract.setTransportAmount(transportAmount);
            contract.setWarehouseAmount(warehouseAmount);
            contract.setInterestAmount(interestAmount);
            ctrContractDao.save(contract);
            //2生成合同操作记录
            CtrContractOphisRequest request = new CtrContractOphisRequest();
            request.setCtrContractId(contract.getId());
            request.setRemark(remark);
            request.setCreateUserId(bizUserId);
            request.setCreateUserName(bizUserName);
            contractHisService.addHis(request);
        }

    }

    /**
     * 刷新代采合同排序号pairCode
     */
    @Override
    @ServerTransactional
    public void makePairCodeForMatch(Long enterpriseId) {
        //1.查询所有的代采-销售合同
        List<CtrContract> list = ctrContractDao.findBySourceAndEnterprise(BasConstants.APPLY_TYPE_MS, enterpriseId);
        for (CtrContract contract : list) {
            //2.获取代采-销售合同所对应的代采-采购合同
            String linkContractId = contract.getLinkContractId();
            if (StringUtils.isNotBlank(linkContractId)) {
                String pairCode = bsKeySequenceService.getNextKey(BasConstants.KEY_PAIR_CODE, enterpriseId);
                List<String> sellIdList = Splitter.on(",").omitEmptyStrings().splitToList(linkContractId);
                List<Long> buyContractList = sellIdList.stream().map(a -> Long.valueOf(a)).collect(Collectors.toList());
                //3.保存pairCode排序号
                for (Long buyContractId : buyContractList) {
                    CtrContract buyContract = ctrContractDao.findOne(buyContractId);
                    buyContract.setPairCode(pairCode);
                    ctrContractDao.save(buyContract);
                }
                contract.setPairCode(pairCode);
                ctrContractDao.save(contract);
            }
        }
    }


    /**
     * 更新合同-出/入库费用
     * @param deliveryOptionFee
     * @param contractId
     */
    @Override
    @ServerTransactional
    public void updateContractDeliveryFee(BigDecimal deliveryOptionFee, Long contractId) {
        // 若出/入库费用为空或0,则无需更新
        if (Objects.isNull(deliveryOptionFee) || BigDecimal.ZERO.compareTo(deliveryOptionFee) == 0){
            return;
        }
        CtrContract contract = ctrContractDao.findOne(contractId);
        BigDecimal deliveryFee = Objects.isNull(contract.getDeliveryFee()) ? BigDecimal.ZERO : contract.getDeliveryFee();
        contract.setDeliveryFee(deliveryFee.add(deliveryOptionFee));
        ctrContractDao.save(contract);
    }

    @Override
    public void updateGoodsFileId(Long id, String fileId) {
        ctrContractDao.updateGoodsFileId(id, fileId);
    }
    /* 更新违约标识 */
    @Override
    public void violateFlgUpdate(Long id) {
        ctrContractDao.violateFlgUpdate(id);
    }

    /**
     * 更新合同保理资料收集状态并自动发起20%保证金付款
     * @param contractId
     */
    @Override
    @ServerTransactional
    public CtrContract refreshFactorStatus(Long contractId) {
        CtrContract entity = ctrContractDao.findOne(contractId);
        if (Objects.isNull(entity)) {
            return entity;
        }
        String businessTypeDcsx = entity.getBusinessTypeDcsx();
        if (!BasConstants.BL_BUSINESS_CODE.contains(businessTypeDcsx)) {
            return entity;
        }
        String factorStatus = entity.getFactorStatus();
        if (!StringUtils.equals(BasConstants.FACTOR_STATUS_N, factorStatus)) {
            return entity;
        }
        if (StringUtils.isBlank(entity.getInvoiceFileId())) {
            List<ApplyInvoice> applyInvoiceList = applyInvoiceService.findByContractId(entity.getId());
            String invoiceFileIds = applyInvoiceList.stream().filter(s -> StringUtils.equals(BasConstants.APPROVE_STATUS_D, s.getStatus()) &&
                    StringUtils.isNotBlank(s.getFileId())).map(ApplyInvoice::getFileId).collect(Collectors.joining(""));
            entity.setInvoiceFileId(removeInvalidFileId(invoiceFileIds));
        }
        if (StringUtils.isBlank(entity.getGoodsFileId())) {
            List<ApplyConfirmReceipt> applyConfirmReceiptList = applyConfirmReceiptDao.findByContractId(entity.getId());
            String goodsFileIds = applyConfirmReceiptList.stream().filter(s -> StringUtils.equals(BasConstants.APPROVE_STATUS_D, s.getStatus()) &&
                    StringUtils.isNotBlank(s.getFileId())).map(ApplyConfirmReceipt::getFileId).collect(Collectors.joining(""));
            entity.setGoodsFileId(removeInvalidFileId(goodsFileIds));
        }
        if (StringUtils.isNotBlank(entity.getDebtCertificateFileId())){
            entity.setDebtCertificateFileId(removeInvalidFileId(entity.getDebtCertificateFileId()));
        }
        boolean judgeFileId = StringUtils.isNotBlank(entity.getInvoiceFileId()) && StringUtils.isNotBlank(entity.getGoodsFileId()) && StringUtils.isNotBlank(entity.getDebtCertificateFileId());
        if (judgeFileId) {
            entity.setFactorStatus(BasConstants.FACTOR_STATUS_Z);
            // 自动发起20%保证金付款
            // contractFactoService.autoLaunchApplyPay(entity.getContractNo());
        }
        entity = ctrContractDao.save(entity);
        return entity;
    }

    private String removeInvalidFileId(String fileId) {
        if (StringUtils.isBlank(fileId)) {
            return fileId;
        }
        List<String> realFileIdList = new ArrayList<>();
        List<String> fileIdList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(fileId);
        for (String id : fileIdList) {
            boolean existFileFlg = FileUtil.existFileFlg(fileServerUrl + "/view/download/" + id);
            if (Boolean.TRUE.equals(existFileFlg)) {
                realFileIdList.add(id);
            }
        }
        fileId = String.join(BasConstants.COMMA, realFileIdList);
        return StringUtils.isNotBlank(fileId) ? fileId + BasConstants.COMMA : "";
    }


    @Override
    public void updateDeliveryStaus(Long id, String staus) {
        ctrContractDao.updateDeliveryStaus(id, staus);
    }

    /**
     * 更新履约状态
     * @param id
     * @param status
     */
    @Override
    public void updatePerformanceStatus(Long id, String status) {
        ctrContractDao.updatePerformanceStaus(id,status);
    }

    @Override
    @ServerTransactional
    public void updateDeliveryAmount(Long contractId, BigDecimal warehouseAmount, BigDecimal transportAmount, BigDecimal deliveryFee) {
        CtrContract contract = ctrContractDao.findOne(contractId);
        warehouseAmount = Objects.isNull(warehouseAmount) ? BigDecimal.ZERO : warehouseAmount;
        transportAmount = Objects.isNull(transportAmount) ? BigDecimal.ZERO : transportAmount;
        deliveryFee = Objects.isNull(deliveryFee) ? BigDecimal.ZERO : deliveryFee;

        BigDecimal contractWarehouseAmount = Objects.isNull(contract.getWarehouseAmount()) ? BigDecimal.ZERO : contract.getWarehouseAmount();
        BigDecimal contractTransportAmount = Objects.isNull(contract.getTransportAmount()) ? BigDecimal.ZERO : contract.getTransportAmount();
        BigDecimal contractDeliveryFee = Objects.isNull(contract.getDeliveryFee()) ? BigDecimal.ZERO : contract.getDeliveryFee();

        contract.setWarehouseAmount(contractWarehouseAmount.add(warehouseAmount));
        contract.setTransportAmount(contractTransportAmount.add(transportAmount));
        contract.setDeliveryFee(contractDeliveryFee.add(deliveryFee));
        ctrContractDao.save(contract);
    }

    /**
     * 清除合同罚金
     *
     * @param id
     */
    @Override
    @ServerTransactional
    public void clearPenalty(Long id) {
        CtrContract entity = ctrContractDao.findOne(id);
        if (Objects.isNull(entity)) {
            return;
        }
        logger.info("清除罚金 contractNo：{}", entity.getContractNo());
        entity.setOrverdurFlg(false);
        entity.setBreachAmount(entity.getReceiveBreachAmount());

        refreshStatus(entity);
        ctrContractDao.save(entity);
    }

    /**
     * 更新合同状态与小程序合同状态
     *
     * @param contract
     */
    private void refreshStatus(CtrContract contract) {
        refreshContractStatus(contract);
        refreshContractStatusWx(contract);
    }

    /**
     * 更新采购管家小程序状态
     *
     * @param contract
     */
    @Override
    public void refreshContractStatusWx(CtrContract contract) {
        String contractStatusWx = contract.getContractStatusWx();
        logger.info("refreshContractStatusWx contractNo:{},before contractStatusWx:{}", contract.getContractNo(), contractStatusWx);
        String businessType = contract.getBusinessType();
        String contractType = contract.getContractType();
        BigDecimal totalAmount = contract.getTotalAmount();
        BigDecimal dealedAmount = contract.getDealedAmount();
        BigDecimal billedAmount = contract.getBilledAmount();
        Boolean confirmReceiptFlg = contract.getConfirmReceiptFlg();
        BigDecimal breachAmount = contract.getBreachAmount();
        BigDecimal receiveBreachAmount = contract.getReceiveBreachAmount();
        // 非[背靠背]业务或[销售]合同 无须更新小程序状态
        if (!StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) || StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contractType)) {
            return;
        }
        if (StringUtils.equals(BasConstants.CONTRACT_STATUS_T, contract.getContractStatusWx())) {
            // 已标记违约的合同无须更新状态
            return;
        }
        if (Boolean.FALSE.equals(contract.getSealFlg())) {
            // 待盖章
            contractStatusWx = BasConstants.CONTRACT_STATUS_N;
        } else if (totalAmount.compareTo(dealedAmount) > 0) {
            // 待付款
            contractStatusWx = BasConstants.CONTRACT_STATUS_P;
        } else if (Boolean.FALSE.equals(confirmReceiptFlg)) {
            // 待收货
            contractStatusWx = BasConstants.CONTRACT_STATUS_W;
        } else if (totalAmount.compareTo(billedAmount) > 0) {
            // 待收票
            contractStatusWx = BasConstants.CONTRACT_STATUS_B;
        } else if (breachAmount.compareTo(receiveBreachAmount) > 0) {
            // 逾期
            contractStatusWx = BasConstants.CONTRACT_STATUS_L;
        } else {
            // 已完成
            contractStatusWx = BasConstants.CONTRACT_STATUS_O;
            contract.setContractStatus(BasConstants.CONTRACTSTATUS_D);
        }
        contract.setContractStatusWx(contractStatusWx);
        logger.info("refreshContractStatusWx contractNo:{},contractStatusWx:{}", contract.getContractNo(), contractStatusWx);
    }

    /**
     * 更新合同状态
     *
     * @param contract
     */
    @Override
    public void refreshContractStatus(CtrContract contract) {
        String contractType = contract.getContractType();
        String contractStatus = contract.getContractStatus();
        BigDecimal totalAmount = contract.getTotalAmount();
        BigDecimal totalNumber = contract.getTotalNumber();
        BigDecimal dealedAmount = contract.getDealedAmount();
        BigDecimal billedAmount = contract.getBilledAmount();
        BigDecimal warehouseNumber = contract.getWarehouseNumber();
        Boolean confirmReceiptFlg = contract.getConfirmReceiptFlg();
        BigDecimal breachAmount = contract.getBreachAmount();
        BigDecimal receiveBreachAmount = contract.getReceiveBreachAmount();
        logger.info("refreshContractStatus contractNo:{}, before contractStatus:{}", contract.getContractNo(), contractStatus);
        // 合同已作废无须更新合同状态
        if (StringUtils.equals(BasConstants.CONTRACTSTATUS_C, contractStatus)) {
            return;
        }
        if (Boolean.FALSE.equals(contract.getSealFlg())) {
            contractStatus = BasConstants.CONTRACTSTATUS_B;
        } else if (StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contractType) &&
                billedAmount.compareTo(totalAmount) >= 0 &&
                warehouseNumber.compareTo(totalNumber) >= 0 &&
                dealedAmount.compareTo(totalAmount) >= 0) {
            contractStatus = BasConstants.CONTRACTSTATUS_D;
        } else if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, contractType) &&
                billedAmount.compareTo(totalAmount) >= 0 &&
                warehouseNumber.compareTo(totalNumber) >= 0 &&
                dealedAmount.compareTo(totalAmount) >= 0 &&
                Boolean.TRUE.equals(confirmReceiptFlg &&
                        receiveBreachAmount.compareTo(breachAmount) >= 0)) {
            contractStatus = BasConstants.CONTRACTSTATUS_D;
        } else if (billedAmount.compareTo(totalAmount) >= 0) {
            contractStatus = StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contractType) ? BasConstants.CONTRACTSTATUS_V1 : BasConstants.CONTRACTSTATUS_V2;
        } else if (warehouseNumber.compareTo(totalNumber) >= 0) {
            contractStatus = StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contractType) ? BasConstants.CONTRACTSTATUS_G1 : BasConstants.CONTRACTSTATUS_G2;
        } else if (dealedAmount.compareTo(totalAmount) >= 0) {
            contractStatus = StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contractType) ? BasConstants.CONTRACTSTATUS_F1 : BasConstants.CONTRACTSTATUS_F2;
        }

        contract.setContractStatus(contractStatus);
        logger.info("refreshContractStatus contractNo:{},contractStatus:{}", contract.getContractNo(), contractStatus);
    }

    /**
     * 更新最晚开票日期
     * 1.使用人保额度：出库日期 + 30天 - 1
     * 2.使用大地额度，出库日期 + 60天 - 1
     * 3.其他情况：出库日期 + 30天 - 1
     * @param targetContract 合同
     * @param warehouseDate 出库日期
     */
    private void refreshLatestBillDate(CtrContract targetContract, Date warehouseDate){
        Date compareDate = null;
        Date maxWarehouseOutDate = applyDeliveryOutDao.findLastDelivery(targetContract.getId());
        if (Objects.isNull(warehouseDate)) {
            compareDate = maxWarehouseOutDate;
        } else if (Objects.nonNull(maxWarehouseOutDate) && maxWarehouseOutDate.after(warehouseDate)) {
            compareDate = maxWarehouseOutDate;
        }
        int compareDay = StringUtils.equals(BasConstants.CREDIT_TYPE_1, targetContract.getCreditType()) ? 59 : 29;
        targetContract.setLatestBillDate(Objects.isNull(compareDate) ? null : DateOperator.addDays(compareDate, compareDay));
    }

    /**
     * 更新库存采购合同关联的采购合同
     *
     * @param targetContract 库存采购合同
     */
    @Override
    @ServerTransactional
    public void refreshVirtualContract(CtrContract targetContract) {
        if (Objects.isNull(targetContract) || !StringUtils.equals(BasConstants.STOCK_VIRTUAL_KC, targetContract.getVirtualType())) {
            return;
        }
        Long virtualContractId = targetContract.getId();
        List<CtrContract> resultContractList = ctrContractDao.findCtrContractByVirtualContractId(virtualContractId);
        if (CollectionUtils.isEmpty(resultContractList)) {
            return;
        }
        CtrContractApply targetApply = ctrContractApplyService.findByContractId(targetContract.getId());

        // 付款
        dealWithVirtualPay(targetContract, resultContractList);

        // 收票
        dealWithVirtualInvoice(targetContract, resultContractList);

        // 入库
        dealWithVirtualDeliveryIn(targetContract, resultContractList);

        // 商品明细入库数量
        dealWithVirtualProduct(targetContract, resultContractList);

        // 处理库存采购关联合同的决算合同总金额
        dealWithVirtualFinalAmount(targetContract, resultContractList);
        ctrContractDao.saveAll(resultContractList);
        updateVirtualContractApply(resultContractList, targetApply);
    }

    /**
     * 更新链条合同损耗
     *
     * @param sellContract 销售合同
     * @param lossNumber   损耗数量
     * @param lossType     损耗类型
     */
    @Override
    @ServerTransactional
    public void refreshContractWithLossNumber(CtrContract sellContract, BigDecimal lossNumber, String lossType) {
        if (Objects.isNull(lossNumber) || lossNumber.compareTo(BigDecimal.ZERO) == 0){
            return;
        }
        logger.info("refreshChainContractWithLossNumber contractNo:{} lossNumber:{} lossType:{}", sellContract.getContractNo(), lossNumber, lossType);
        sellContract.setTotalNumber(sellContract.getTotalNumber().subtract(lossNumber));
        sellContract.setTotalAmount(sellContract.getTotalNumber().multiply(sellContract.getDealPrice()).setScale(2, RoundingMode.HALF_UP));
        sellContract.setLossNumber(sellContract.getLossNumber().add(lossNumber));
        ctrContractDao.save(sellContract);
        ctrProductDao.save(refreshProductParam(sellContract));
        CtrContract buyContract = ctrContractDao.findByApproveIdAndContractType(sellContract.getApproveId(), BasConstants.CONTRACT_TYPE_B);
        if (Objects.nonNull(buyContract) && StringUtils.equals("1", lossType)){
            buyContract.setTotalNumber(buyContract.getTotalNumber().subtract(lossNumber));
            buyContract.setTotalAmount(buyContract.getTotalNumber().multiply(buyContract.getDealPrice()).setScale(2, RoundingMode.HALF_UP));
            buyContract.setLossNumber(buyContract.getLossNumber().add(lossNumber));
            ctrContractDao.save(buyContract);
            ctrProductDao.save(refreshProductParam(buyContract));
        }
        ApplyCtrDCSX dcsxContract = applyDcsxDao.findByDCSXApproveId(sellContract.getApproveId());
        if (Objects.nonNull(dcsxContract)){
            dcsxContract.setTotalNumber(dcsxContract.getTotalNumber().subtract(lossNumber));
            dcsxContract.setTotalAmount(dcsxContract.getTotalNumber().multiply(dcsxContract.getDealPrice()).setScale(2, RoundingMode.HALF_UP));
            dcsxContract.setLossNumber(dcsxContract.getLossNumber().add(lossNumber));
            applyDcsxDao.save(dcsxContract);
        }
    }

    /**
     * 确认收货触发更新约定付款日期
     *
     * @param sellContract 销售合同
     * @param entity       确认收货实体
     */
    @Override
    @ServerTransactional
    public Date refreshAppointPayFullTimeWithReceipt(CtrContract sellContract, ApplyConfirmReceipt entity) throws ApplicationException {
        Date targetTime;
        if (Boolean.FALSE.equals(sellContract.getMatchCreditFlg())) {
            return null;
        }
        BigDecimal totalAmount = sellContract.getTotalAmount();
        BigDecimal billedAmount = sellContract.getBilledAmount();
        Date currApprovePayFullTime = sellContract.getAppointPayFullTime();
        Date compareDate = entity.getConfirmReceiptDate();
        // 货到票到：货送到且我司开票给客户，再开始计算账期
        if (Boolean.TRUE.equals(sellContract.getReceiptArrivedFlg()) && billedAmount.compareTo(totalAmount) >= 0) {
            Date maxBillDate = applyInvoiceService.findMaxBillDate(sellContract.getId());
            if (Objects.nonNull(maxBillDate) && compareDate.before(maxBillDate)) {
                compareDate = maxBillDate;
            }
        }
        int addDays = Objects.nonNull(sellContract.getCreditCycle()) ? Integer.parseInt(sellContract.getCreditCycle().toString()) : 1;
        int realCreditCycle = Math.max(addDays - 1, 1);
        targetTime = BasBusinessUtil.offsetDayOneSeconds(compareDate, realCreditCycle);
        targetTime = targetTime.before(currApprovePayFullTime) ? currApprovePayFullTime : targetTime;
        sellContract.setAppointPayFullTime(targetTime);
        logger.info("refreshAppointPayFullTimeWithReceipt contractNo:{}, appointPayFullTime:{}", sellContract.getContractNo(), targetTime);
        ctrContractDao.save(sellContract);
        updateBudgetSettlement(sellContract.getId(), targetTime);
        this.refreshBreachAmount(sellContract);
        return targetTime;
    }

    /**
     * 更新约定付款日期
     * @param sellContract 销售合同
     * @param invoice 收票
     */
    @Override
    @ServerTransactional
    public void refreshAppointPayFullTimeWithInvoice(CtrContract sellContract, ApplyInvoice invoice) throws ApplicationException {
        if (Boolean.FALSE.equals(sellContract.getMatchCreditFlg())){
            return;
        }
        Date appointPayFullTime = sellContract.getAppointPayFullTime();
        Date specialTime = dealWithSpecialConfigCompany(sellContract, invoice);
        Date targetTime = dealWithReceiptArrived(sellContract, invoice);
        Date complateDate = BasBusinessUtil.getMaxDate(specialTime, targetTime);
        if (Objects.nonNull(complateDate) && complateDate.after(appointPayFullTime)){
            sellContract.setAppointPayFullTime(complateDate);
            ctrContractDao.save(sellContract);

            List<ApplyConfirmReceipt> confirmReceiptList = applyConfirmReceiptDao.findByContractId(sellContract.getId());
            if (CollectionUtils.isNotEmpty(confirmReceiptList)){
                confirmReceiptList.forEach(c -> c.setActualContractPayFullTime(complateDate));
                applyConfirmReceiptDao.saveAll(confirmReceiptList);
            }
            updateBudgetSettlement(sellContract.getId(), complateDate);
            this.refreshBreachAmount(sellContract);
        }
    }

    /**
     * 根据补充协议更新合同数据
     *
     * @param agreement      补充协议数据
     * @param protocolFileId 补充协议双签附件
     */
    @Override
    @ServerTransactional
    public void refreshContractWithProtocolDocument(SupplementaryAgreement agreement, String protocolFileId) {
        String contractNo = agreement.getContractNo();
        // 变更牌号
        String brandNumber = agreement.getBrandNumber();
        // 变更合同数量
        BigDecimal alterTotalNumber = agreement.getAlterTotalNumber();
        // 变更单价
        BigDecimal alterDealPrice = agreement.getAlterDealPrice();
        // 变更合同金额
        BigDecimal alterTotalAmount = agreement.getAlterTotalAmount();
        // 变更交货方式
        String deliveryMode = agreement.getDeliveryMode();

        CtrContract contract = ctrContractDao.findByContractNo(contractNo);
        if (Objects.isNull(contract)) {
            ApplyCtrDCSX ctrDCSX = applyDcsxDao.findByContractNo(contractNo);
            if (Objects.isNull(ctrDCSX)) {
                logger.error("refreshContractWithProtocolDocument stop can't find any contract contractNo:{}", contractNo);
                return;
            }
            if (Objects.nonNull(alterTotalNumber)) {
                ctrDCSX.setTotalNumber(alterTotalNumber);
            }
            if (Objects.nonNull(alterDealPrice)) {
                ctrDCSX.setDealPrice(alterDealPrice);
            }
            if (Objects.nonNull(alterTotalAmount)) {
                ctrDCSX.setTotalAmount(alterTotalAmount);
            }
            if (StringUtils.isNotBlank(brandNumber)) {
                ctrDCSX.setProductNum(brandNumber);
            }
            if (StringUtils.isNotBlank(deliveryMode)) {
                ctrDCSX.setDeliveryMode(deliveryMode);
            }
            ctrDCSX.setFileId(ctrDCSX.getFileId() + protocolFileId);
            applyDcsxDao.save(ctrDCSX);
            return;
        }
        CtrProduct ctrProduct = ctrProductDao.findOneByCtrContractId(contract.getId());
        List<CtrLogistics> ctrLogisticList = ctrLogisticsDao.findByLogisticsNo(contractNo.replaceAll("\\D", ""));
        CtrLogistics ctrLogistics = ctrLogisticList.stream().findFirst().orElse(null);
        if (Objects.nonNull(alterTotalNumber)) {
            contract.setTotalNumber(alterTotalNumber);
            ctrProduct.setDealNumber(alterTotalNumber);
            ctrProduct.setRemainNumber(alterTotalNumber);
            if (Objects.nonNull(ctrLogistics)){
                ctrLogistics.setDealNumber(alterTotalNumber);
            }
        }
        if (Objects.nonNull(alterDealPrice)) {
            contract.setDealPrice(alterDealPrice);
            ctrProduct.setDealPrice(alterDealPrice);
        }
        if (Objects.nonNull(alterTotalAmount)) {
            contract.setTotalAmount(alterTotalAmount);
            ctrProduct.setTotalPrice(alterTotalAmount);
        }
        if (StringUtils.isNotBlank(brandNumber)) {
            ctrProduct.setBrandNumber(brandNumber);
        }
        if (StringUtils.isNotBlank(deliveryMode)) {
            contract.setDeliveryMode(deliveryMode);
        }
        if (!StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, contract.getBusinessType())) {
            contract.setFileId(contract.getFileId() + protocolFileId);
        } else if (StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contract.getContractType())) {
            contract.setBuyContentFileId(contract.getBuyContentFileId() + protocolFileId);
            if (Objects.nonNull(ctrLogistics)){
                ctrLogistics.setBuyDealPrice(contract.getDealPrice());
                ctrLogistics.setBuyTotalAmount(contract.getTotalAmount());
            }
        } else {
            contract.setSellContentFileId(contract.getSellContentFileId() + protocolFileId);
            if (Objects.nonNull(ctrLogistics)){
                ctrLogistics.setSellDealPrice(contract.getDealPrice());
                ctrLogistics.setSellTotalAmount(contract.getTotalAmount());
            }
        }
        ctrContractDao.save(contract);
        ctrProductDao.save(ctrProduct);
        if (Objects.nonNull(ctrLogistics)){
            ctrLogisticsDao.save(ctrLogistics);
        }
    }

    private void updateBudgetSettlement(Long contractId, Date targetTime) throws ApplicationException {
        BudgetSettlement budgetSettlement = budgetSettlementService.getBySellContractId(contractId);
        if (Objects.nonNull(budgetSettlement)) {
            budgetSettlement.setPayFullTime(targetTime);
            budgetSettlementService.save(budgetSettlement);
        }
    }

    private void refreshBreachAmount(CtrContract sellContract) {
        SCHEDULED_POOL.schedule(() -> {
            try {
                if (Boolean.TRUE.equals(sellContract.getMatchCreditFlg())) {
                    budgetSettlementService.doTaskByContractNo(sellContract.getContractNo());
                }
            } catch (Exception e) {
                logger.error("refreshBreachAmount error", e);
            }
        }, 5, TimeUnit.SECONDS);
    }

    /**
     * 货送到且我司开票给客户，再开始计算账期
     * @param sellContract
     * @param invoice
     * @return
     */
    private Date dealWithReceiptArrived(CtrContract sellContract, ApplyInvoice invoice){
        if (Boolean.TRUE.equals(sellContract.getReceiptArrivedFlg())){
            int addDays = Objects.nonNull(sellContract.getCreditCycle()) ? Integer.parseInt(sellContract.getCreditCycle().toString()) : 1;
            int realCreditCycle = Math.max(addDays - 1, 1);
            Date compareDate = invoice.getInvoiceDate();
            Date maxConfirmDate = applyConfirmReceiptDao.findMaxConfirmDate(sellContract.getId());
            if (Objects.nonNull(maxConfirmDate) && compareDate.before(maxConfirmDate)) {
                compareDate = maxConfirmDate;
            }
            Date targetTime = BasBusinessUtil.offsetDayOneSeconds(compareDate, realCreditCycle);
            logger.info("货送到且我司开票给客户，再开始计算账期 contractNo:{}, appointPayFullTime:{}", sellContract.getContractNo(), targetTime);
            return targetTime;
        }
        return null;
    }

    /**
     * 如果有配置付款日期规则的客户，只在开票完成后维护，确认收货不维护
     *
     * @param sellContract 销售合同
     * @param invoice      收票
     */
    private Date dealWithSpecialConfigCompany(CtrContract sellContract, ApplyInvoice invoice) {
        // 有配置付款日期规则的客户，全部开票后，更新时间付全款日期 = 开票日期 + 配置额外天数
        BigDecimal billedAmount = sellContract.getBilledAmount();
        BigDecimal totalAmount = sellContract.getTotalAmount();
        if (invoice.getDealAmount().add(billedAmount).compareTo(totalAmount) >= 0) {
            BsPayFullRuleVo payFullRule = bsProductConfigService.getPayFullRule(sellContract.getCompanyId(), sellContract.getEnterpriseId());
            if (Objects.nonNull(payFullRule) && Objects.nonNull(payFullRule.getAddPayFullDate())) {
                int addDays = payFullRule.getAddPayFullDate();
                if (addDays <= 0) {
                    addDays = Objects.nonNull(sellContract.getCreditCycle()) ? Integer.parseInt(sellContract.getCreditCycle().toString()) : 0;
                }
                Date appointPayFullTime = BasBusinessUtil.offsetDayOneSeconds(invoice.getInvoiceDate(), addDays);
                sellContract.setAppointPayFullTime(appointPayFullTime);
                logger.info("已配置付款日期规则，执行更新实际付全款日期 contractNo:{},companyName:{},appointPayFullTime:{}",
                        sellContract.getContractNo(), sellContract.getCompanyName(), DateOperator.formatDate(appointPayFullTime));
                return appointPayFullTime;
            }
        }
        return null;
    }

    /**
     * 处理库存采购关联合同的付款数据
     *
     * @param targetContract     库存采购合同
     * @param resultContractList 关联采购合同
     */
    private void dealWithVirtualPay(CtrContract targetContract, List<CtrContract> resultContractList) {
        BigDecimal targetAmount = targetContract.getDealedAmount();
        for (CtrContract contract : resultContractList) {
            if (StringUtils.equals(BasConstants.APPLY_STATUS_APPLYING, contract.getHideOut())){
                continue;
            }
            if (targetAmount.compareTo(contract.getTotalAmount()) >= 0) {
                contract.setDealedAmount(contract.getTotalAmount());
                targetAmount = targetAmount.subtract(contract.getTotalAmount());
            } else {
                contract.setDealedAmount(targetAmount);
                targetAmount = BigDecimal.ZERO;
            }
        }
    }

    /**
     * 处理库存采购关联合同的入库数据
     *
     * @param targetContract     库存采购合同
     * @param resultContractList 关联采购合同
     */
    private void dealWithVirtualDeliveryIn(CtrContract targetContract, List<CtrContract> resultContractList) {
        BigDecimal targetNumber = targetContract.getWarehouseNumber();
        for (CtrContract contract : resultContractList) {
            if (StringUtils.equals(BasConstants.APPLY_STATUS_APPLYING, contract.getHideOut())){
                continue;
            }
            if (targetNumber.compareTo(contract.getTotalNumber()) >= 0) {
                contract.setWarehouseNumber(contract.getTotalNumber());
                targetNumber = targetNumber.subtract(contract.getTotalNumber());
            } else {
                contract.setWarehouseNumber(targetNumber);
                targetNumber = BigDecimal.ZERO;
            }
            logger.info("contractNo:{}, warehouseNumber:{}", contract.getContractNo(), contract.getWarehouseNumber());
        }
    }

    /**
     * 处理库存采购关联合同的收票数据
     *
     * @param targetContract     库存采购合同
     * @param resultContractList 关联采购合同
     */
    private void dealWithVirtualInvoice(CtrContract targetContract, List<CtrContract> resultContractList) {
        BigDecimal targetBilledAmount = targetContract.getBilledAmount();
        for (CtrContract contract : resultContractList) {
            if (StringUtils.equals(BasConstants.APPLY_STATUS_APPLYING, contract.getHideOut())){
                continue;
            }
            if (targetBilledAmount.compareTo(contract.getTotalAmount()) >= 0) {
                contract.setBilledAmount(contract.getTotalAmount());
                targetBilledAmount = targetBilledAmount.subtract(contract.getTotalAmount());
            } else {
                contract.setBilledAmount(targetBilledAmount);
                targetBilledAmount = BigDecimal.ZERO;
            }
        }
    }

    /**
     * 处理库存采购关联合同的商品数据
     *
     * @param targetContract     库存采购合同
     * @param resultContractList 关联采购合同
     */
    private void dealWithVirtualProduct(CtrContract targetContract, List<CtrContract> resultContractList) {
        for (CtrContract contract : resultContractList) {
            if (StringUtils.equals(BasConstants.APPLY_STATUS_APPLYING, contract.getHideOut())){
                continue;
            }
            List<CtrProduct> productList = ctrProductDao.findByCtrContractId(contract.getId());
            for (CtrProduct ctrProduct : productList) {
                ctrProduct.setRemainNumber(contract.getWarehouseNumber());
                ctrProduct.setWarehouseNumber(contract.getWarehouseNumber());
            }
            ctrProductDao.saveAll(productList);
            contract.setSealFlg(true);
            contract.setBuyContentFileId(targetContract.getBuyContentFileId());
            refreshContractStatus(contract);
        }
    }

    /**
     * 处理库存采购关联合同的决算合同总金额
     *
     * @param targetContract     库存采购合同
     * @param resultContractList 关联采购合同
     */
    private void dealWithVirtualFinalAmount(CtrContract targetContract, List<CtrContract> resultContractList) {
        BigDecimal totalVirtualNumber = resultContractList.stream().map(CtrContract::getTotalNumber).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalVirtualNumber.compareTo(targetContract.getTotalNumber()) >= 0){
            BigDecimal realFinalAmount = resultContractList.stream()
                    .filter(c -> Objects.nonNull(c.getAgreementDealPrice()))
                    .map(c -> c.getTotalNumber().multiply(c.getAgreementDealPrice()).setScale(2, RoundingMode.HALF_UP))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            ctrContractDao.updatefinalTotalAmount(targetContract.getId(), realFinalAmount);
        }
    }

    private void updateVirtualContractApply(List<CtrContract> resultContractList, CtrContractApply targetApply){
        if (Objects.isNull(targetApply) || CollectionUtils.isEmpty(resultContractList)){
            return;
        }
        List<CtrContractApply> contractApplyList = new ArrayList<>();
        for (CtrContract contract : resultContractList) {
            CtrContractApply apply = ctrContractApplyService.findByContractId(contract.getId());
            apply.setApplyBillAmount(contract.getBilledAmount());
            apply.setRealBillDate(targetApply.getRealBillDate());

            apply.setApplyWarehouseNumber(contract.getWarehouseNumber());
            apply.setRealWarehoseDate(targetApply.getRealWarehoseDate());

            apply.setApplyPayAmount(contract.getDealedAmount());
            apply.setRealPayDate(contract.getRealPayFullTime());

            contractApplyList.add(apply);
        }
        ctrContractApplyDao.saveAll(contractApplyList);
    }

    private CtrProduct refreshProductParam(CtrContract ctrContract){
        CtrProduct ctrProduct = ctrProductDao.findOneByCtrContractId(ctrContract.getId());
        ctrProduct.setDealNumber(ctrContract.getTotalNumber());
        ctrProduct.setDealPrice(ctrContract.getDealPrice());
        ctrProduct.setTotalPrice(ctrContract.getTotalAmount());
        ctrProduct.setRemainNumber(ctrContract.getTotalNumber());
        return ctrProduct;
    }
    // 保费扣款流水
    private void insuranceAmountDeduct(CtrContract contract) {
        try {
            // insuranceFlag 为false 代表首次
            if(!contract.getInsuranceFlag()){
                String ourCompanyName = contract.getOurCompanyName();
                // 获取资金代采方信息
                BsCompanyDcsx bsCompanyDcsx = bsCompanyDcsxClient.findByCompanyName(ourCompanyName);
                // 期初余额
                BigDecimal insuranceAmount = bsCompanyDcsx.getInsuranceAmount()==null?BigDecimal.ZERO:bsCompanyDcsx.getInsuranceAmount();
                // 合同保费
                BigDecimal contractInsuranceAmount = contract.getInsuranceAmount()==null?BigDecimal.ZERO:contract.getInsuranceAmount();
                // 期末余额（期初余额-合同保费）
                BigDecimal ultimateAmount = insuranceAmount.subtract(contractInsuranceAmount);
                // 增加保费扣款流水
                InsuranceAmountFlow insuranceAmountFlow = new InsuranceAmountFlow();
                insuranceAmountFlow.setFundCompanyId(bsCompanyDcsx.getId());
                insuranceAmountFlow.setContractId(contract.getId());
                insuranceAmountFlow.setFlowType(BasConstants.DICT_TYPE_INSURANCE_AMFL_S);
                // 取负数，流水金额（等于保费金额）肯定是负的,被减
                insuranceAmountFlow.setFlowAmount(contractInsuranceAmount.negate());
                insuranceAmountFlow.setInitialAmount(insuranceAmount);
                insuranceAmountFlow.setUltimateAmount(ultimateAmount);
                String subject = contract.getContractNo()+","+contract.getTotalAmount()+"元,";
                if(contract.getCreditCycle()!=null&&contract.getCreditCycle()>0){
                    subject+=contract.getCreditCycle()+"天";
                }
                insuranceAmountFlow.setSubject(subject);
                insuranceAmountFlow.setLinkApproveId(contract.getApproveId());
                iInsuranceAmountFlowClient.save(insuranceAmountFlow);
                // 修改资金方保费余额
                bsCompanyDcsx.setInsuranceAmount(ultimateAmount);
                bsCompanyDcsxClient.save(bsCompanyDcsx);
                // 销售合同设置已参保
                contract.setInsuranceFlag(true);
            }
        } catch (Exception e) {
            logger.error("保费扣款流水错误===》",e);
        }
    }
}
