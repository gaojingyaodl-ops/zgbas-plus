package com.spt.bas.server.stock.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.stock.service.IStockVirtualService;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 报价单
 *
 * @Author: gaojy
 * @create 2022/5/9 10:37
 * @version: 1.0
 * @description:
 */
@Component
@Transactional(readOnly = true)
public class StockVirtualServiceImpl extends BaseService<StockVirtual> implements IStockVirtualService {
    @Resource
    private StockVirtualDao stockVirtualDao;
    @Resource
    private CtrContractDao ctrContractDao;
    @Resource
    private ApplyAgreementVirtualDao applyAgreementVirtualDao;
    @Resource
    private StockInventoryDao inventoryStockVirtualDao;
    @Autowired
    private PmApproveDao pmApproveDao;

    @Override
    public BaseDao<StockVirtual> getBaseDao() {
        return stockVirtualDao;
    }

    /**
     * 调整指导价/分配销售业务员
     * @param stockVirtual
     */
    @Override
    @ServerTransactional
    public void updateStockVirtual(StockVirtual stockVirtual) {
        StockVirtual entity = stockVirtualDao.findOne(stockVirtual.getId());
        if (Objects.nonNull(entity)) {
            BigDecimal newMinSellPrice = stockVirtual.getMinSellPrice();
            Long newSellMatchUserId = stockVirtual.getSellMatchUserId();
            String newSellMatchUserName = stockVirtual.getSellMatchUserName();
            if (Objects.nonNull(newMinSellPrice)) {
                entity.setMinSellPrice(newMinSellPrice);
            }
            if (Objects.nonNull(newSellMatchUserId)) {
                entity.setSellMatchUserId(newSellMatchUserId);
            }
            if (Objects.nonNull(newSellMatchUserName)) {
                entity.setSellMatchUserName(newSellMatchUserName);
            }
            stockVirtualDao.save(entity);
        }
    }

    @Override
    @ServerTransactional
    public void invalidStockVirtual(Long stockVirtualId) throws ApplicationException {
        StockVirtual entity = stockVirtualDao.findOne(stockVirtualId);
        if (Objects.isNull(entity)) {
            return;
        }
        String virtualBuyType = entity.getVirtualBuyType();
        Long linkContractId = entity.getLinkContractId();
        if (Objects.nonNull(linkContractId)) {
            CtrContract contract = ctrContractDao.findOne(linkContractId);
            if (Objects.nonNull(contract) && !StringUtils.equals(BasConstants.CONTRACTSTATUS_C, contract.getStatus())) {
                throw new ApplicationException("该报价单已使用，请先作废关联合同" + contract.getContractNo());
            }
        }
        if (StringUtils.equals(BasConstants.STOCK_VIRTUAL_XY, virtualBuyType)) {
            // 作废协议采购
            invalidAgreementVirtual(entity);
        } else {
            // 作废库存采购
            invalidInventoryVirtual(entity);
        }
    }

    /**
     * 作废库存采购
     * @param entity 报价单
     */
    private void invalidInventoryVirtual(StockVirtual entity) {
        BigDecimal dealNumber = entity.getDealNumber();
        StockInventory inventoryVirtual = inventoryStockVirtualDao.findOne(entity.getBizApplyVirtualId());
        inventoryVirtual.setReleaseNumber(inventoryVirtual.getReleaseNumber().subtract(dealNumber));
        inventoryVirtual.setInventoryStatus(BasConstants.STOCK_VIRTUAL_STATUS_F);
        inventoryStockVirtualDao.save(inventoryVirtual);

        entity.setVirtualStatus(BasConstants.STOCK_VIRTUAL_STATUS_C);
        stockVirtualDao.save(entity);
    }

    /**
     * 作废协议采购
     * @param entity 报价单
     */
    private void invalidAgreementVirtual(StockVirtual entity) {
        // 判断该协议采购是否经过拆单
        List<StockVirtual> virtualList = stockVirtualDao.findBizStockVirtual(entity.getBizApplyVirtualId(), BasConstants.STOCK_VIRTUAL_XY);
        if (virtualList.size() == 1) {
            // 未拆单直接作废
            entity.setVirtualStatus(BasConstants.STOCK_VIRTUAL_STATUS_C);
            stockVirtualDao.save(entity);

            // 作废对应协议采购申请
            ApplyAgreementVirtual agreementVirtual = applyAgreementVirtualDao.findOne(entity.getBizApplyVirtualId());
            agreementVirtual.setStatus(BasConstants.APPROVE_STATUS_C);
            applyAgreementVirtualDao.save(agreementVirtual);

            PmApprove agreementApprove = pmApproveDao.findOne(agreementVirtual.getApproveId());
            agreementApprove.setStatus(BasConstants.APPROVE_STATUS_C);
            pmApproveDao.save(agreementApprove);
        } else {
            // 经过拆单
            entity.setVirtualStatus(BasConstants.STOCK_VIRTUAL_STATUS_C);
            stockVirtualDao.save(entity);
        }
    }


    @Override
    @ServerTransactional
    public void updateStockVirtualStatus(Long id, String status) {
        if (Objects.isNull(id)) {
            return;
        }
        StockVirtual stockVirtual = this.getEntity(id);
        if (Objects.isNull(stockVirtual)){
            return;
        }
        Long linkApproveId = stockVirtual.getLinkApproveId();
        if (StringUtils.equals(BasConstants.STOCK_VIRTUAL_STATUS_N, status)) {
            stockVirtual.setLinkApproveId(null);
            stockVirtual.setLinkApproveNo(null);
            stockVirtual.setApplyMatchDetailId(null);
            stockVirtual.setLinkContractId(null);
            stockVirtual.setLinkContractNo(null);
        } else if(StringUtils.equals(BasConstants.STOCK_VIRTUAL_STATUS_A, status) && Objects.nonNull(linkApproveId)) {
            CtrContract buyVirtualContract = ctrContractDao.findByApproveIdAndContractType(linkApproveId, BasConstants.CONTRACT_TYPE_B);
            if (Objects.nonNull(buyVirtualContract)){
                stockVirtual.setLinkContractId(buyVirtualContract.getId());
                stockVirtual.setLinkContractNo(buyVirtualContract.getContractNo());
            }
        }
        stockVirtual.setVirtualStatus(status);
        stockVirtualDao.save(stockVirtual);
    }

    @Override
    @ServerTransactional
    public void autoDeleteStockVirtual() {
        // 查询所有新录入的库存
        List<StockVirtual> stockVirtualList = stockVirtualDao.findAllByVirtualStatus(BasConstants.STOCK_VIRTUAL_STATUS_N);
        Date nowDate = new Date();
        if (CollectionUtils.isNotEmpty(stockVirtualList)) {
            for (StockVirtual stockVirtual : stockVirtualList) {
                try {
                    // 超过24小时未被使用，状态修改为已失效
                    if (DateUtil.between(stockVirtual.getPublishTime(), nowDate, DateUnit.DAY) >= 1) {
                        updateStockVirtualStatus(stockVirtual.getId(), BasConstants.STOCK_VIRTUAL_STATUS_C);
                        logger.info("库存id：[" + stockVirtual.getId() + "]已置为失效");
                    }
                } catch (Exception e) {
                    logger.error("定时清除24小时未被使用的库存失败：{}", e);
                }

            }
        }
    }

    /**
     * 预算申请绑定虚拟库存
     *
     * @param match
     * @param matchDetailList
     * @return
     */
    @Override
    @ServerTransactional
    public void bindStockVirtual(PmApprove approve, ApplyMatch match, List<ApplyMatchDetail> matchDetailList){
        Long stockVirtualId = match.getStockVirtualId();
        if (Objects.isNull(stockVirtualId) || stockVirtualId == 0L) {
            return;
        }
        StockVirtual stockVirtual = getEntity(stockVirtualId);
        if (Objects.isNull(stockVirtual)) {
            return;
        }
        Long applyMatchDetailId = 0L;
        for (ApplyMatchDetail detail : matchDetailList) {
            String contractType = detail.getContractType();
            String buySource = detail.getBuySource();
            String sellSource = detail.getSellSource();
            if (StringUtils.equals(BasConstants.CONTRACT_TYPE_B, contractType) && (StringUtils.equals(BasConstants.STOCK_VIRTUAL_KC, buySource)
                    || StringUtils.equals(BasConstants.STOCK_VIRTUAL_XY, buySource))) {
                applyMatchDetailId = detail.getId();
                break;
            } else if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, contractType) && StringUtils.equals(BasConstants.SELL_SOURCE_S, sellSource)) {
                applyMatchDetailId = detail.getId();
                break;
            }
        }
        if (applyMatchDetailId == 0L){
            logger.info("未使用报价虚拟库存!");
            return;
        }

        BigDecimal virtualDealNumber = stockVirtual.getDealNumber();
        BigDecimal dealPrice = stockVirtual.getDealPrice();
        BigDecimal matchDealNumber = match.getDealNumber();
        String stockVirtualNo = stockVirtual.getStockVirtualNo();
        if (matchDealNumber.compareTo(virtualDealNumber) != 0) {
            // 拆分虚拟报价库存
            StockVirtual newStockVirtual = new StockVirtual();
            BeanUtils.copyProperties(stockVirtual, newStockVirtual);
            newStockVirtual.setId(0L);
            newStockVirtual.setDealNumber(virtualDealNumber.subtract(matchDealNumber));
            newStockVirtual.setTotalAmount(newStockVirtual.getDealNumber().multiply(dealPrice).setScale(2, RoundingMode.HALF_UP));
            newStockVirtual.setStockVirtualNo(bindNewVirtualNo(stockVirtualNo));
            newStockVirtual.setVirtualStatus(BasConstants.STOCK_VIRTUAL_STATUS_N);
            stockVirtualDao.save(newStockVirtual);

            stockVirtual.setDealNumber(matchDealNumber);
            stockVirtual.setTotalAmount(matchDealNumber.multiply(dealPrice).setScale(2, RoundingMode.HALF_UP));
        }
        stockVirtual.setApplyMatchDetailId(applyMatchDetailId);
        stockVirtual.setLinkApproveId(approve.getId());
        stockVirtual.setLinkApproveNo(approve.getApproveNo());
        stockVirtual.setSellMatchUserId(approve.getCreateUserId());
        stockVirtual.setSellMatchUserName(approve.getCreateUserName());
        stockVirtual.setVirtualStatus(BasConstants.STOCK_VIRTUAL_STATUS_I);
        stockVirtualDao.save(stockVirtual);
    }

    /**
     * 附件上传
     * @param id id
     * @param fileId 附件 id
     */
    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        stockVirtualDao.updateFileId(id, fileId);
    }

    /**
     * 判断是否存在可选择的报价
     * @return
     */
    @Override
    public boolean existEnableVirtual() {
        return stockVirtualDao.findEnableVirtualCount() > 0L;
    }

    private String bindNewVirtualNo(String targetVirtualNo) {
        if (targetVirtualNo.contains("-")) {
            targetVirtualNo = targetVirtualNo.split("-")[0];
        }
        List<StockVirtual> virtualList = stockVirtualDao.findByStockVirtualNoLike(targetVirtualNo);
        int suffix = CollectionUtils.isEmpty(virtualList) ? 1 : virtualList.size() + 1;
        return targetVirtualNo + "-" + suffix;
    }
}
