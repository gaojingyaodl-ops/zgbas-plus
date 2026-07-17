package com.spt.bas.server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.BsProductConfigVo;
import com.spt.bas.client.vo.BudgetSettlementVo;
import com.spt.bas.client.vo.ParamByCompanyGrade;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.BudgetSettlementDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.service.*;
import com.spt.bas.server.util.BudgetSettlementUtil;
import com.spt.bas.server.util.CommissionCalculateUtil;
import com.spt.bas.server.util.SptDateUtils;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.service.IBsKeySequenceService;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-30 09:27
 */
@Component
@Transactional(readOnly = true)
@Slf4j
public class BudgetSettlementServiceImpl extends BaseService<BudgetSettlement> implements IBudgetSettlementService {
    @Autowired
    BudgetSettlementDao budgetSettlementDao;
    @Autowired
    private CommissionCalculateUtil commissionCalculateUtil;
    @Autowired
    private IBsProductConfigService bsProductConfigService;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private IBsKeySequenceService bsKeySequenceService;
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private ICtrContractUpdateService ctrContractUpdateService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private ICtrProductService ctrProductService;

    /**
     * 保存销售结算表
     *
     * @param buyProduct
     * @param sellProduct
     */
    @Override
    @ServerTransactional
    public void saveSettlement(CtrProduct buyProduct, CtrProduct sellProduct) {
        //商品配置项参数 有默认
        BsProductConfigVo configVo = bsProductConfigService.getConfigValue(sellProduct.getProductCd(),
                sellProduct.getEnterpriseId());
        if (configVo != null) {
            CtrContract sellContract = ctrContractService.getEntity(sellProduct.getCtrContractId());
            CtrContract buyContract = ctrContractService.getEntity(buyProduct.getCtrContractId());
            BigDecimal sellTransportAmount = sellContract.getTransportAmount();
            BigDecimal buyTransportAmount = buyContract.getTransportAmount();
            BigDecimal buyWarehouseAmount = buyContract.getWarehouseAmount();
            BigDecimal sellWarehouseAmount = sellContract.getWarehouseAmount();
            BigDecimal totalNumber = sellContract.getTotalNumber();

            //上下游运输费平均单价
            BigDecimal transportPrice = BigDecimal.ZERO;
            BigDecimal t_transprotAmount = sellTransportAmount.add(buyTransportAmount);
            if (t_transprotAmount != null && t_transprotAmount.compareTo(BigDecimal.ZERO) > 0) {
                transportPrice = t_transprotAmount.divide(totalNumber, 2, BigDecimal.ROUND_HALF_UP);
            }
            //仓储费
            BigDecimal warehousePrice = BigDecimal.ZERO;
            BigDecimal t_warehouseAmount = sellWarehouseAmount.add(buyWarehouseAmount);

            if (t_warehouseAmount != null && t_warehouseAmount.compareTo(BigDecimal.ZERO) > 0) {
                warehousePrice = t_warehouseAmount.divide(totalNumber, 2, BigDecimal.ROUND_HALF_UP);
            }
            //保存结算记录
            BudgetSettlement settlement = new BudgetSettlement();

            // 销售单价
            BigDecimal sellPrice = sellProduct.getDealPrice();
            // 采购单价
            BigDecimal buyPrice = buyProduct.getDealPrice();
            // 根据sellContract的contractAttr字段 判断是否是托盘业务 合同属性为远期
            if ("F".equals(sellContract.getContractAttr())) {
                settlement.setProcessCode(BasConstants.PROCESS_APPLY_MATCH_PALLET);
                Date sellPayFullTime = sellContract.getPayFullTime();
                Date buyPayFullTime = buyContract.getPayFullTime();
                // 托盘时长 = 销售合同约定收款日 - 采购合同约定付款日
                long compareDays = DateOperator.compareDays(sellPayFullTime, buyPayFullTime) + 1;
                BigDecimal bondRate = sellContract.getBondRate();
                // 托盘费用 = 销售单价 * 数量 * (1 - 定金比例) * 托盘时长
                BigDecimal palletAmount = sellPrice.multiply(totalNumber).multiply(BigDecimal.ONE.subtract(bondRate)).multiply(new BigDecimal(compareDays)).setScale(2, BigDecimal.ROUND_HALF_UP);
                settlement.setPalletAmount(palletAmount);
                settlement.setBuyPayFullTime(buyContract.getPayFullTime());
            }
            settlement.setContractModel(sellContract.getContractModel());
            settlement.setServeAmount(sellContract.getServiceAmount());
            settlement.setSellContractId(sellProduct.getCtrContractId());
            settlement.setSellContractNo(sellContract.getContractNo());
            settlement.setSellProductId(sellProduct.getId());
            settlement.setBuyContractId(buyProduct.getCtrContractId());
            settlement.setBuyContractNo(buyContract.getContractNo());
            BigDecimal transportAmount_buy = buyContract.getTransportAmount() == null ? BigDecimal.ZERO : buyContract.getTransportAmount();
            BigDecimal warehouseAmount_buy = buyContract.getWarehouseAmount() == null ? BigDecimal.ZERO : buyContract.getWarehouseAmount();
            BigDecimal transportAmount_sell = sellContract.getTransportAmount() == null ? BigDecimal.ZERO : sellContract.getTransportAmount();
            BigDecimal warehouseAmount_sell = sellContract.getWarehouseAmount() == null ? BigDecimal.ZERO : sellContract.getWarehouseAmount();
            // 计算利润时，采购和销售的运输费和仓储费都算是成本
            settlement.setBuyTransportAmount(transportAmount_buy.add(transportAmount_sell));
            settlement.setBuyWarehouseAmount(warehouseAmount_buy.add(warehouseAmount_sell));
            settlement.setProductName(sellProduct.getProductName());
            settlement.setBrandNumber(sellProduct.getBrandNumber());
            settlement.setFactoryName(sellProduct.getFactoryName());
            settlement.setContractTime(sellContract.getContractTime());
            settlement.setPayFullTime(sellContract.getPayFullTime());
            settlement.setDeliveryTime(sellContract.getDeliveryDateFrom());
            settlement.setDealNumber(sellProduct.getDealNumber());
            settlement.setSellPrice(sellProduct.getDealPrice());
            settlement.setBuyPrice(buyProduct.getDealPrice());
            settlement.setPremium(sellProduct.getPremium());
            settlement.setTransportPrice(transportPrice);
            settlement.setWarehousePrice(warehousePrice);
            settlement.setSettlementType(sellContract.getSettlementType());
            settlement.setCreditCycle(sellContract.getCreditCycle());
            // 保险费率(代采业务，保险费率为零)
            if (StringUtils.isEmpty(settlement.getSettlementType())) {
                settlement.setInsuranceRate(BigDecimal.ZERO);
            } else {
                Long creditCycle = sellContract.getCreditCycle();
                BigDecimal insuranceRate = creditCycle <= 30L ? new BigDecimal("0.001") : new BigDecimal("0.0012");
                settlement.setInsuranceRate(insuranceRate);
            }
            // BsCompany company = bsCompanyService.getEntity(sellContract.getCompanyId());
            // 根据客户等级动态条件获取服务费率、逾期罚息费率
            ParamByCompanyGrade paramByCompanyGrade = getParamByCompanyGrade(sellContract.getCompanyId(), sellProduct.getProductCd());
            // settlement.setServeRate(company.getRate());
            // settlement.setBreachRate(company.getInterestRate());
            settlement.setServeRate(paramByCompanyGrade.getServeRate());
            settlement.setBreachRate(paramByCompanyGrade.getBreachRate());

            settlement.setBusinessCommissionRate(configVo.getBusinessCommissionRate());
            settlement.setBuyCommissionRate(configVo.getBuyCommissionRate());
            settlement.setSellCommissionRate(configVo.getSellCommissionRate());
            settlement.setManageCommissionRate(configVo.getManageCommissionRate());
            settlement.setCompanyCommissionRate(configVo.getCompanyCommissionRate());
            settlement.setBreachAmount(BigDecimal.ZERO);
            settlement.setBudgetFinishStatus("0");
            settlement.setBudgetStatus("0");

            settlement.setMarginAmount(BudgetSettlementUtil.getMarginAmount(settlement));
            settlement.setPrintAmount(BudgetSettlementUtil.getPrintAmount(settlement));
            settlement.setVatAmount(BudgetSettlementUtil.getVatAmount(settlement));
            settlement.setBuyCommissionAmount(BudgetSettlementUtil.getBuyCommissionAmount(settlement));
            settlement.setSellCommissionAmount(BudgetSettlementUtil.getSellCommissionAmount(settlement));
            settlement.setManageCommissionAmount(BudgetSettlementUtil.getManageCommissionAmount(settlement));
            // 公司提成
            settlement.setCompanyCommissionAmount(BudgetSettlementUtil.getCompanyCommissionAmount(settlement));
            // 利润率
            settlement.setMarginRate(BudgetSettlementUtil.getMarginRate(settlement));
            settlement.setEnterpriseId(sellContract.getEnterpriseId());
            settlement.setCreditFlg(sellContract.getCreditFlg());
            settlement.setMarketingRetentionRate(configVo.getMarketingRate());
            settlement.setCreditCycle(sellContract.getCreditCycle());
            settlement.setMarketingRetention(BudgetSettlementUtil.getMarketingRetention(settlement));
            String settlementCode = bsKeySequenceService.getNextKey(BasConstants.KEY_BUDGET_SETTLEMENT_NO, sellContract.getEnterpriseId());
            settlement.setSettlementCode(settlementCode);

            settlement.setSellDirectorUserId(getDeptLeaderId(sellContract.getMatchUserId()));
            settlement.setBuyDirectorUserId(getDeptLeaderId(buyContract.getMatchUserId()));

            settlement.setSellDirectorCommissionAmount(BudgetSettlementUtil.getSellDirectorCommissionAmount(settlement));
            settlement.setBuyDirectorCommissionAmount(BudgetSettlementUtil.getBuyDirectorCommissionAmount(settlement));

            budgetSettlementDao.save(settlement);

            /**
             * 销售合同保存罚息费率
             */
            sellContract.setBreachRate(settlement.getBreachRate());
            ctrContractDao.save(sellContract);

//            CtrProduct product = ctrProductDao.findOne(sellProduct.getId());
//            if (StringUtils.isBlank(product.getSettlementCode())) {
//                product.setSettlementCode(settlementCode);
//                ctrProductDao.save(product);
//            }
        }
    }

    /**
     * 查询直属部门id
     * @param userId
     * @return
     */
    private Long getDeptLeaderId(Long userId) {
        DeptSearchVo sysDeptSearchVo = new DeptSearchVo();
        sysDeptSearchVo.setUserId(userId);
        return authOpenFacade.findDeptLeader(sysDeptSearchVo);
    }

    /**
     * 更新销售结算表
     *
     * @param budgetSettlement
     * @param buyContract
     * @param sellContract
     */
    @Override
    @ServerTransactional
    public void updateSettlement(BudgetSettlement budgetSettlement, CtrContract buyContract, CtrContract sellContract) {
        if (budgetSettlement == null) {
            return;
        }

        List<CtrProduct> byContractId = ctrProductService.findByContractId(sellContract.getId());

        if (byContractId.isEmpty()) {
            return;
        }
        CtrProduct sellProduct = byContractId.get(0);

        //商品配置项参数 有默认
        BsProductConfigVo configVo = bsProductConfigService.getConfigValue(sellProduct.getProductCd(),
                BasConstants.ZG_ENTERPRISE_ID);

        log.info("buyContract:合同号{},内容：{}", buyContract.getContractNo(), JsonUtil.obj2Json(buyContract));
        BigDecimal transportAmount = sellContract.getTransportAmount();
        log.info("下游运输费：{}================================", transportAmount);
        BigDecimal warehouseAmount = sellContract.getWarehouseAmount();
        log.info("下游仓储费：{}================================", warehouseAmount);
        BigDecimal totalNumber = sellContract.getTotalNumber();
        log.info("数量：{}================================", totalNumber);

        //运输费
        BigDecimal transportPrice = BigDecimal.ZERO;
        if (transportAmount != null && transportAmount.compareTo(BigDecimal.ZERO) > 0) {
            transportPrice = transportAmount.divide(totalNumber, 2, BigDecimal.ROUND_HALF_UP);
        }
        log.info("下游运输单价：{}================================", transportPrice);
        //仓储费
        BigDecimal warehousePrice = BigDecimal.ZERO;
        if (warehouseAmount != null && warehouseAmount.compareTo(BigDecimal.ZERO) > 0) {
            warehousePrice = warehouseAmount.divide(totalNumber, 2, BigDecimal.ROUND_HALF_UP);
        }
        log.info("下游仓储单价：{}================================", warehousePrice);

        // 根据sellContract的contractAttr字段 判断是否是托盘业务 合同属性为远期
        if (StrUtil.isEmpty(sellContract.getSettlementType()) && "F".equals(sellContract.getContractAttr())) {
            log.info("合同：{}是托盘业务================================", sellContract.getContractNo());
            budgetSettlement.setProcessCode(BasConstants.PROCESS_APPLY_MATCH_PALLET);
            Date sellPayFullTime = sellContract.getPayFullTime();
            log.info("销售合同收全款日期：{}", sellPayFullTime);
            Date buyPayFullTime = buyContract.getPayFullTime();
            log.info("采购合同付全款日期：{}", buyPayFullTime);
            // 托盘时长 = 销售合同约定收款日 - 采购合同约定付款日
            long compareDays = DateOperator.compareDays(sellPayFullTime, buyPayFullTime) + 1;
            log.info("托盘时长：{}", compareDays);
            BigDecimal bondRate = sellContract.getBondRate();
            log.info("定金比例：{}", bondRate);

            // 销售单价
            BigDecimal totalAmount = sellContract.getTotalAmount();
            BigDecimal sellUnitPrice = totalAmount.divide(totalNumber, 4, BigDecimal.ROUND_HALF_UP);

            // 托盘费用 = 销售单价 * 数量 * (1 - 定金比例) * 托盘时长
            BigDecimal palletAmount = sellUnitPrice.multiply(sellContract.getTotalNumber()).multiply(BigDecimal.ONE.subtract(bondRate)).multiply(new BigDecimal(compareDays)).setScale(2, BigDecimal.ROUND_HALF_UP);
            log.info("托盘费用：{}", palletAmount);
            budgetSettlement.setPalletAmount(palletAmount);
            budgetSettlement.setBuyPayFullTime(buyContract.getPayFullTime());
        }

        budgetSettlement.setServeAmount(sellContract.getServiceAmount());
        BigDecimal transportAmount_buy = buyContract.getTransportAmount() == null ? BigDecimal.ZERO : buyContract.getTransportAmount();
        BigDecimal warehouseAmount_buy = buyContract.getWarehouseAmount() == null ? BigDecimal.ZERO : buyContract.getWarehouseAmount();
        BigDecimal transportAmount_sell = sellContract.getTransportAmount() == null ? BigDecimal.ZERO : sellContract.getTransportAmount();
        BigDecimal warehouseAmount_sell = sellContract.getWarehouseAmount() == null ? BigDecimal.ZERO : sellContract.getWarehouseAmount();
        // 计算利润时，采购和销售的运输费和仓储费都算是成本
        budgetSettlement.setBuyTransportAmount(transportAmount_buy.add(transportAmount_sell));
        budgetSettlement.setBuyWarehouseAmount(warehouseAmount_buy.add(warehouseAmount_sell));
        budgetSettlement.setPayFullTime(sellContract.getPayFullTime());
        budgetSettlement.setDeliveryTime(sellContract.getDeliveryDateFrom());
        budgetSettlement.setTransportPrice(transportPrice);
        budgetSettlement.setWarehousePrice(warehousePrice);
        // 保险费率(代采业务，保险费率为零)
        if (StringUtils.isEmpty(budgetSettlement.getSettlementType())) {
            budgetSettlement.setInsuranceRate(BigDecimal.ZERO);
        } else {
            budgetSettlement.setInsuranceRate(configVo.getInsuranceRate());
        }
        // 根据客户等级动态条件获取服务费率、逾期罚息费率
        ParamByCompanyGrade paramByCompanyGrade = getParamByCompanyGrade(sellContract.getCompanyId(), sellProduct.getProductCd());
        // budgetSettlement.setServeRate(configVo.getServeRate());
        // budgetSettlement.setBreachRate(configVo.getBreachRate());
        budgetSettlement.setServeRate(paramByCompanyGrade.getServeRate());
        budgetSettlement.setBreachRate(paramByCompanyGrade.getBreachRate());

        budgetSettlement.setBusinessCommissionRate(configVo.getBusinessCommissionRate());
        budgetSettlement.setBuyCommissionRate(configVo.getBuyCommissionRate());
        budgetSettlement.setSellCommissionRate(configVo.getSellCommissionRate());
        budgetSettlement.setManageCommissionRate(configVo.getManageCommissionRate());
        budgetSettlement.setCompanyCommissionRate(configVo.getCompanyCommissionRate());
        budgetSettlement.setBreachAmount(BigDecimal.ZERO);
        budgetSettlement.setBudgetFinishStatus("0");
        budgetSettlement.setBudgetStatus("0");
        budgetSettlement.setMarginAmount(BudgetSettlementUtil.getMarginAmount(budgetSettlement));
        budgetSettlement.setPrintAmount(BudgetSettlementUtil.getPrintAmount(budgetSettlement));
        budgetSettlement.setVatAmount(BudgetSettlementUtil.getVatAmount(budgetSettlement));
        budgetSettlement.setBuyCommissionAmount(BudgetSettlementUtil.getBuyCommissionAmount(budgetSettlement));
        budgetSettlement.setSellCommissionAmount(BudgetSettlementUtil.getSellCommissionAmount(budgetSettlement));
        budgetSettlement.setManageCommissionAmount(BudgetSettlementUtil.getManageCommissionAmount(budgetSettlement));
        // 公司提成
        budgetSettlement.setCompanyCommissionAmount(BudgetSettlementUtil.getCompanyCommissionAmount(budgetSettlement));
        // 毛利
        budgetSettlement.setGrossProfit(BudgetSettlementUtil.getGrossProfit(budgetSettlement));
        // 毛利率
        budgetSettlement.setGrossProfitRate(BudgetSettlementUtil.getGrossProfitRate(budgetSettlement));
        budgetSettlement.setEnterpriseId(sellContract.getEnterpriseId());
        budgetSettlement.setCreditFlg(sellContract.getCreditFlg());
        budgetSettlement.setSettlementType(sellContract.getSettlementType());
        budgetSettlement.setMarketingRetentionRate(configVo.getMarketingRate());
        budgetSettlement.setCreditCycle(sellContract.getCreditCycle());
        budgetSettlement.setMarketingRetention(BudgetSettlementUtil.getMarketingRetention(budgetSettlement));
        budgetSettlementDao.save(budgetSettlement);

    }

    /**
     * 更新已违约结算表
     */
    @Override
    @ServerTransactional
    public void doTask() throws ApplicationException {
        // 1.进行中的结算单若已超期则更改标识为已违约
        // 2.以违约结算单更新逾期罚息等计算项
        List<BudgetSettlement> list = budgetSettlementDao.findUnFinishSettlementListV3();
        logger.info("预算决算定时任务开始；list.size = {}", list.size());
        for (int i = 0; i < list.size(); i++) {
            BudgetSettlement settlement = list.get(i);
            try{
                this.doSettle(settlement);
                logger.info("第{}条===============================结束", i + 1);
            }catch (Exception e){
                logger.error("更新失败,settlement:{}", JsonUtil.obj2Json(settlement), e);
            }
        }
        logger.info("预算决算定时任务结束；===================================================");
    }

    /**
     * 根据合同编号更新已违约结算表
     */
    @Override
    @ServerTransactional
    public void doTaskByContractNo(String contractNo) throws ApplicationException {
        // 1.进行中的结算单若已超期则更改标识为已违约
        // 2.以违约结算单更新逾期罚息等计算项
        List<BudgetSettlement> list = budgetSettlementDao.findUnFinishSettlementListV3ByContractNo(contractNo);
        if(!CollectionUtils.isEmpty(list)) {
            logger.info("预算决算定时任务开始；list.size = {}", list.size());
            for (int i = 0; i < list.size(); i++) {
                BudgetSettlement settlement = list.get(i);
                try{
                    this.doSettle(settlement);
                    logger.info("第{}条===============================结束", i + 1);
                }catch (Exception e){
                    logger.error("更新失败,settlement:{}", JsonUtil.obj2Json(settlement), e);
                }
            }
        } else {
            logger.info("根据合同号未查询到未完成预算结算单；===================================================");
        }
        logger.info("预算决算定时任务结束；===================================================");

    }


    /**
     * 单预算结算
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doSettle(BudgetSettlement settlement) throws ApplicationException {
        CtrContract sellContract = ctrContractService.getEntity(settlement.getSellContractId());
        CtrContract buyContract = ctrContractService.getEntity(settlement.getBuyContractId());
        boolean updateFlg = false;
        // 是否是赊销合同
        boolean isSX = !StringUtils.isEmpty(sellContract.getSettlementType());
        // 付全款日期前 没有违约
        logger.info("sellContract:{},是否赊销:{},payFullTime:{};now:{}", sellContract.getContractNo(), isSX, settlement.getPayFullTime(), new Date());
        // 如果是代采，没有逾期违约的规则
        if (!isSX) {
            if (checkBuyContract(buyContract) && checkDcSellContract(sellContract)) {
                logger.info("合同所有步骤都已完成，决算完成================================");
                settlement.setBudgetStatus("4");
                settlement.setBudgetFinishStatus("1");
                updateContractFlgTrue(buyContract, sellContract);
                updateFlg = true;
            } else {
                logger.info("合同还有步骤没有完成，决算未完成================================");
            }
        } else {
            if (settlement.getPayFullTime().after(new Date())) {
                logger.info("付款期限还没有到，没有违约");
                // 校验采购和销售合同(加服务合同)是否全部完成
                if (checkBuyContract(buyContract)
                        && checkSellContract(sellContract)) {
                    logger.info("同时所有合同所有步骤都已完成，决算完成================================");
                    settlement.setBudgetStatus("4");
                    settlement.setBudgetFinishStatus("1");
                    // 正常流程没有违约，逾期天数为0
                    settlement.setOverdueDays(0L);
                    updateContractFlgTrue(buyContract, sellContract);
                    //updateFlg = true;
                }
            } else if (sellContract.getDealedAmount().compareTo(sellContract.getTotalAmount().subtract(sellContract.getLossAmount())) >= 0
                    && sellContract.getReceiveServiceAmount().compareTo(sellContract.getServiceAmount()) >= 0) {
                logger.info("期限已过，销售合同和服务合同费用已付清，但是预算没有结束，没有违约");
                // 再次校验
                // 校验采购和销售合同(加服务合同)是否全部完成
                if (checkBuyContract(buyContract)
                        && checkSellContract(sellContract)) {
                    logger.info("同时所有合同所有步骤都已完成，决算完成================================");
                    settlement.setBudgetStatus("4");
                    settlement.setBudgetFinishStatus("1");
                    // 正常流程没有违约，逾期天数为0
                    //updateFlg = true;
                    settlement.setOverdueDays(0L);
                    updateContractFlgTrue(buyContract, sellContract);
                }
            } else {
                // 逾期天数
                settlement.setOverdueDays(DateOperator.compareDays(SptDateUtils.formatterDate(settlement.getPayFullTime()), SptDateUtils.formatterDate(settlement.getRealPayFullTime())));
                // 已超过7天 违约
                if (DateUtils.addDays(settlement.getPayFullTime(), getBreachLimitDay(settlement)).before(new Date())) {
                    settlement.setOverdueDays(DateOperator.compareDays(SptDateUtils.formatterDate(settlement.getPayFullTime()), SptDateUtils.formatterDate(new Date())));
                    // 更新合同违约
                    updateBreach(sellContract);
                    logger.info("已违约。。。");
                    // 违约中
                    settlement.setBudgetStatus("3");
                    //updateFlg = true;
                } else {
                    // 超过付全款日期 已逾期
                    // 逾期中
                    settlement.setBudgetStatus("1");
                    logger.info("逾期中。。。");
                    // 更新合同逾期
                    updateOverdue(sellContract);
                    //updateFlg = true;
                }
            }
            // 更新逾期罚息
            updateFlg = true;
            ctrContractUpdateService.updateSellContractInterest(sellContract, settlement);
        }
        if (updateFlg) {
            settlement.setSellRefund(sellContract.getRefundAmount());
            settlement.setBuyRefund(buyContract.getRefundAmount());
            settlement.setLogisticsRefund(sellContract.getLossAmountByLogistics());
            // 利润
            settlement.setMarginAmount(BudgetSettlementUtil.getMarginAmount(settlement).subtract(sellContract.getSellInterestAmount()));
            // 利润率
            settlement.setMarginRate(BudgetSettlementUtil.getMarginRate(settlement));
            // 增值税
            settlement.setVatAmount(BudgetSettlementUtil.getVatAmount(settlement));
            // 附加税
            settlement.setSurtax(BudgetSettlementUtil.getSurtax(settlement));
            // 印花税
            settlement.setPrintAmount(BudgetSettlementUtil.getPrintAmount(settlement));
            // 逾期天数
            settlement.setOverdueDays(sellContract.getBreachDays());
            // 逾期罚息
            settlement.setBreachAmount(sellContract.getBreachAmount());
            // 采购提成
            settlement.setBuyCommissionAmount(BudgetSettlementUtil.getBuyCommissionAmount(settlement));
            // 销售提成
            settlement.setSellCommissionAmount(BudgetSettlementUtil.getSellCommissionAmount(settlement));
            // 公司提成
            settlement.setCompanyCommissionAmount(BudgetSettlementUtil.getCompanyCommissionAmount(settlement));
            // 毛利
            settlement.setGrossProfit(BudgetSettlementUtil.getGrossProfit(settlement));
            // 毛利率
            settlement.setGrossProfitRate(BudgetSettlementUtil.getGrossProfitRate(settlement));
            // 资金成本
            settlement.setCapitalCost(BudgetSettlementUtil.getCapitalCost(settlement));
            // 保险成本
            settlement.setInsuranceCost(BudgetSettlementUtil.getInsuranceCost(settlement));
            // 营销留存
            settlement.setMarketingRetention(BudgetSettlementUtil.getMarketingRetention(settlement));
            if ("BL".equals(settlement.getContractModel())) {
                settlement.setSellDirectorCommissionAmount(BudgetSettlementUtil.getBlsellDirectorCommissionAmount(settlement));
                settlement.setBuyDirectorCommissionAmount(BudgetSettlementUtil.getBlbuyDirectorCommissionAmount(settlement));
            } else {
                settlement.setSellDirectorCommissionAmount(BudgetSettlementUtil.getSellDirectorCommissionAmount(settlement));
                settlement.setBuyDirectorCommissionAmount(BudgetSettlementUtil.getBuyDirectorCommissionAmount(settlement));
            }

            budgetSettlementDao.save(settlement);
        }
    }

    /**
     * 决算完成后将所有flag设为true
     * @param buyContract
     * @param sellContract
     */
    private void updateContractFlgTrue(CtrContract buyContract,CtrContract sellContract) throws ApplicationException {
        // 是否付款
        buyContract.setDealedFlg(true);
        // 是否出库
        buyContract.setWarehouseFlg(true);
        // 是否收票
        buyContract.setBilledFlg(true);
        buyContract.setBillFlg(true);

        buyContract.setContractStatus(BasConstants.CONTRACTSTATUS_D);

        // 是否收货款
        sellContract.setDealedFlg(true);
        // 是否出库
        sellContract.setWarehouseFlg(true);
        // 是否确认收货
        sellContract.setConfirmReceiptFlg(true);
        // 是否开票
        sellContract.setBillFlg(true);
        sellContract.setBilledFlg(true);

        sellContract.setContractStatus(BasConstants.CONTRACTSTATUS_D);

        ctrContractService.save(buyContract);
        ctrContractService.save(sellContract);
    }

    /**
     * 检查采购合同状态
     *
     * @return
     */
    private Boolean checkBuyContract(CtrContract buyContract) {
        if (buyContract == null) {
            return false;
        }
        // 是否完成开票
        Boolean billedFlg = buyContract.getBilledAmount().compareTo(buyContract.getTotalAmount()) >= 0;
        // 是否完成付款
        Boolean dealedFlg = true;
        if (buyContract.getBusinessTypeDcsx()!=null&&!buyContract.getBusinessTypeDcsx().equals("DCSX") ) {
            dealedFlg = buyContract.getDealedAmount().compareTo(buyContract.getTotalAmount()) >= 0;
            logger.info("代采赊销合同不进入付款校验");
        }

        // 是否完成盖章
        Boolean sealFlg = buyContract.getSealFlg();
        // 是否完成入库
        Boolean warehouseFlg = buyContract.getWarehouseNumber().compareTo(buyContract.getTotalNumber()) >= 0;
        if (billedFlg && dealedFlg && sealFlg && warehouseFlg) {
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
    private Boolean checkDcSellContract(CtrContract sellContract) {
        if (sellContract == null) {
            return false;
        }
        // 是否完成开票
        Boolean billedFlg = sellContract.getBilledAmount().compareTo(sellContract.getTotalAmount()) >= 0;
        // 是否完成付款
        Boolean dealedFlg = sellContract.getDealedAmount().compareTo(sellContract.getTotalAmount()) >= 0;
        // 是否完成盖章
        Boolean sealFlg = sellContract.getSealFlg();
        // 是否完成入库
        Boolean warehouseFlg = sellContract.getWarehouseNumber().compareTo(sellContract.getTotalNumber()) >= 0;
        // 是否完成确认收货
        Boolean confirmReceiptFlg = sellContract.getConfirmReceiveNumber().compareTo(sellContract.getTotalNumber()) >= 0;

        String contractNo = sellContract.getContractNo();

        if (billedFlg && dealedFlg && sealFlg && warehouseFlg && confirmReceiptFlg) {
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
    private Boolean checkSellContract(CtrContract sellContract) {
        if (sellContract == null) {
            return false;
        }
        // 应收款 = 合同金额 - 损耗金额
        BigDecimal receivables = sellContract.getTotalAmount().subtract(sellContract.getLossAmount());
        // 是否完成开票
        Boolean billedFlg = sellContract.getBilledAmount().compareTo(receivables) >= 0;
        // 是否完成付款
//        Boolean dealedFlg = sellContract.getDealedFlg();
        // 是否完成付款(收货款金额大于等于合同金额)
        Boolean dealedFlg = sellContract.getDealedAmount().compareTo(receivables) >= 0;

        // 是否完成盖章
        Boolean sealFlg = sellContract.getSealFlg();
        // 是否完成入库
        Boolean warehouseFlg = sellContract.getWarehouseNumber().compareTo(sellContract.getTotalNumber()) >= 0;
        // 是否完成确认收货
        Boolean confirmReceiptFlg = sellContract.getConfirmReceiveNumber().compareTo(sellContract.getTotalNumber()) >= 0;

        // 服务费合同总价
        BigDecimal serviceAmount = sellContract.getServiceAmount();

        // 服务费已开开票金额
        BigDecimal serviceBilledAmount = sellContract.getServiceBilledAmount() == null ? BigDecimal.ZERO : sellContract.getServiceBilledAmount();

        // 已收服务费金额
        BigDecimal receiveServiceAmount = sellContract.getReceiveServiceAmount();

        // 服务合同是否已收款(服务费收款金额大于等于服务合同金额)
        boolean receiveServiceFlg = receiveServiceAmount.compareTo(serviceAmount) >= 0;

        // 服务合同开票是否完成(服务合同开票金额大于等于服务合同收款金额)
        boolean serviceBilledFlg = serviceBilledAmount.compareTo(receiveServiceAmount) >= 0;

        String contractNo = sellContract.getContractNo();

        if (billedFlg && dealedFlg && sealFlg && warehouseFlg && confirmReceiptFlg && receiveServiceFlg && serviceBilledFlg) {
            return true;
        }
        return false;
    }


    /**
     * 更新合同逾期
     *
     * @param sellContract
     */
    private void updateOverdue(CtrContract sellContract) throws ApplicationException {
        sellContract.setOrverdurFlg(Objects.nonNull(sellContract.getBreachDays()) && sellContract.getBreachDays() > 0);
        sellContract.setContractStatusWx(BasConstants.CONTRACT_STATUS_L);
        ctrContractService.save(sellContract);
    }

    /**
     * 更新合同违约
     *
     * @param sellContract
     */
    public void updateBreach(CtrContract sellContract) throws ApplicationException {
        sellContract.setContractStatusWx(BasConstants.CONTRACT_STATUS_T);
        ctrContractService.save(sellContract);
    }

    /**
     * 获取违约限制日期
     *
     * @return
     */
    private Integer getBreachLimitDay(BudgetSettlement settlement) {
        int limitDay = BasConstants.BREACH_LIMIT_DAY;
        BsDictData bsDictData;
        // 托盘业务7后违约
        if (BasConstants.PROCESS_APPLY_MATCH_PALLET.equals(settlement.getProcessCode())) {
            bsDictData = BsDictUtil.getBsDictData("breachLimitDay", "sevenDay");
        }else {
            bsDictData = BsDictUtil.getBsDictData("breachLimitDay", "configDay");
        }
        if (bsDictData != null && !StringUtils.isEmpty(bsDictData.getDictName())) {
            try {
                limitDay = Integer.parseInt(bsDictData.getDictName());
            } catch (Exception exception) {
                logger.info("limitDay:{},配置错误,使用默认配置", limitDay);
            }
        }
        logger.info("limitDay:{}", limitDay);
        return limitDay;
    }

    /**
     * 通过销售合同id查询
     *
     * @param sellContractId
     * @return
     */
    @Override
    public BudgetSettlementVo findBySellContractId(Long sellContractId) {
        BudgetSettlement settlement = budgetSettlementDao.findBySellContractIdAndBudgetFinishStatus(sellContractId, "1");
        BudgetSettlementVo budgetSettlementVo = null;
        if (settlement != null) {
            budgetSettlementVo = new BudgetSettlementVo();
            BeanUtils.copyProperties(settlement, budgetSettlementVo);
            CtrContract sellContract = ctrContractService.getEntity(settlement.getSellContractId());
            // 已收逾期服务费 = 实际收款 - 合同金额
            BigDecimal receiveOverdueAmount = sellContract.getReceiveBreachAmount();
            budgetSettlementVo.setBreachAmount(sellContract.getBreachAmount());
            budgetSettlementVo.setOverdueDays(sellContract.getBreachDays());
            budgetSettlementVo.setReceiveOverdueAmount(receiveOverdueAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
            // 业务员罚金 = 逾期罚金 - 已收逾期服务费
            budgetSettlementVo.setFineOfSalesman(
                    (sellContract.getBreachAmount().subtract(budgetSettlementVo.getReceiveOverdueAmount()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        return budgetSettlementVo;
    }

    @Override
    public BudgetSettlement findBySellContractIdWithAnyStatus(Long sellContractId) {
        BudgetSettlement settlement = budgetSettlementDao.findBySellContractId(sellContractId);
        BudgetSettlementVo budgetSettlementVo = null;
        if (settlement != null) {
            budgetSettlementVo = new BudgetSettlementVo();
            BeanUtils.copyProperties(settlement, budgetSettlementVo);
            CtrContract sellContract = ctrContractService.getEntity(settlement.getSellContractId());
            // 已收逾期服务费 = 实际收款 - 合同金额
            BigDecimal receiveOverdueAmount = sellContract.getReceiveBreachAmount();
            budgetSettlementVo.setBreachAmount(sellContract.getBreachAmount());
            budgetSettlementVo.setOverdueDays(sellContract.getBreachDays());
            budgetSettlementVo.setReceiveOverdueAmount(receiveOverdueAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
            // 业务员罚金 = 逾期罚金 - 已收逾期服务费
            budgetSettlementVo.setFineOfSalesman(
                    (sellContract.getBreachAmount().subtract(budgetSettlementVo.getReceiveOverdueAmount()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        return budgetSettlementVo;
    }



    /**
     * BudgetSettlement
     *
     * @param sellContractId
     * @return
     */
    @Override
    public BudgetSettlement getBySellContractId(Long sellContractId) {
        return budgetSettlementDao.findBySellContractId(sellContractId);
    }

    @Override
    public BaseDao<BudgetSettlement> getBaseDao() {
        return budgetSettlementDao;
    }

    /**
     * 更新上下游仓储费运输费
     *
     * @param sellContractId
     * @return
     */
    @Override
    public BudgetSettlement updateTransformAndWarehouse(Long sellContractId) {
        BudgetSettlement budgetSettlement = this.getBySellContractId(sellContractId);
        if (Objects.isNull(budgetSettlement)){
            return null;
        }
        CtrContract sellContract = ctrContractService.getEntity(budgetSettlement.getSellContractId());
        CtrContract buyContract = ctrContractService.getEntity(budgetSettlement.getBuyContractId());
        BigDecimal sellTransportAmount = sellContract.getTransportAmount();
        BigDecimal buyTransportAmount = buyContract.getTransportAmount();
        log.info("上游运输费：{}================================", buyTransportAmount);
        log.info("下游运输费：{}================================", sellTransportAmount);
        BigDecimal buyWarehouseAmount = buyContract.getWarehouseAmount();
        BigDecimal sellWarehouseAmount = sellContract.getWarehouseAmount();
        log.info("上游仓储费：{}================================", buyWarehouseAmount);
        log.info("下游仓储费：{}================================", sellWarehouseAmount);
        BigDecimal totalNumber = sellContract.getTotalNumber();
        log.info("数量：{}================================", totalNumber);

        //上下游运输费平均单价
        BigDecimal transportPrice = BigDecimal.ZERO;
        BigDecimal t_transprotAmount = sellTransportAmount.add(buyTransportAmount);
        if (t_transprotAmount != null && t_transprotAmount.compareTo(BigDecimal.ZERO) > 0) {
            transportPrice = t_transprotAmount.divide(totalNumber, 2, BigDecimal.ROUND_HALF_UP);
        }
        log.info("上下游运输单价：{}================================", transportPrice);
        //仓储费
        BigDecimal warehousePrice = BigDecimal.ZERO;
        BigDecimal t_warehouseAmount = sellWarehouseAmount.add(buyWarehouseAmount);

        if (t_warehouseAmount != null && t_warehouseAmount.compareTo(BigDecimal.ZERO) > 0) {
            warehousePrice = t_warehouseAmount.divide(totalNumber, 2, BigDecimal.ROUND_HALF_UP);
        }
        budgetSettlement.setWarehousePrice(warehousePrice);
        budgetSettlement.setTransportPrice(transportPrice);
        budgetSettlement.setBuyWarehouseAmount(t_warehouseAmount);
        budgetSettlement.setBuyTransportAmount(t_transprotAmount);
        return budgetSettlementDao.save(budgetSettlement);
    }

    /**
     * 根据企业等级获取服务费率、违约费率
     * @param companyId
     * @return
     */
    @Override
    public ParamByCompanyGrade getParamByCompanyGrade(Long companyId, String productCd) {
        ParamByCompanyGrade param = new ParamByCompanyGrade();
        BsCompany entity = bsCompanyService.getEntity(companyId);
        if (Objects.nonNull(entity) && StringUtils.isNotBlank(entity.getCompanyGrade())) {
            logger.info("getParamByCompanyGrade plan1 companyName:{},companyGrade:{}", entity.getCompanyName(), entity.getCompanyGrade());
            Map<String, Object> mapDefault = new HashMap<>();
            mapDefault.put("companyGrade", entity.getCompanyGrade());
            List<ParamByCompanyGrade> companyGradeList = bsProductConfigService.getParamByCompanyGrade(entity.getEnterpriseId());
            param = commissionCalculateUtil.getParamByCompanyGrand(mapDefault, companyGradeList);
        } else {
            logger.info("getParamByCompanyGrade plan2 productCd:{}", productCd);
            BsProductConfigVo configVo = bsProductConfigService.getConfigValue(productCd, BasConstants.ZG_ENTERPRISE_ID);
            param.setServeRate(configVo.getServeRate());
            param.setBreachRate(configVo.getBreachRate());
        }
        logger.info("根据企业等级获取服务费率、逾期罚息费率 comapnyId:{},productCd:{},serverRate:{},breachRate:{}",
                companyId, productCd, param.getServeRate(), param.getBreachRate());
        return param;
    }

}
