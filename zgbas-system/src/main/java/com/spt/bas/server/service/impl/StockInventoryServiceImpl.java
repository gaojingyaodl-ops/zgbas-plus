package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.server.dao.StockInventoryDao;
import com.spt.bas.server.dao.StockVirtualDao;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.bas.server.service.IStockInventoryService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author MoonLight
 * @Date 2024/8/20 13:51
 * @Version 1.0
 */
@Transactional(readOnly = true)
@Component("StockInventoryService")
public class StockInventoryServiceImpl extends BaseService<StockInventory> implements IStockInventoryService {
    @Resource
    private StockVirtualDao stockVirtualDao;
    @Resource
    private StockInventoryDao stockInventoryDao;
    @Resource
    private IBsCompanyService bsCompanyService;


    @Override
    public BaseDao<StockInventory> getBaseDao() {
        return stockInventoryDao;
    }

    @Override
    @ServiceTransactional
    public void invalidInventory(Long id) {

    }

    @Override
    @ServiceTransactional
    public void updateInventory(StockInventory stockVirtual) throws ApplicationException {
        logger.info("updateInventory param:{}", JsonUtil.obj2Json(stockVirtual));
        StockInventory entity = getEntity(stockVirtual.getId());
        if (Objects.isNull(entity)) {
            logger.error("updateInventory error, 查询不到库存采购!");
            return;
        }
        if (Objects.nonNull(stockVirtual.getReleaseNumber()) && stockVirtual.getReleaseNumber().compareTo(BigDecimal.ZERO) > 0) {
            updateReleaseNumber(stockVirtual, entity);
        } else {
            updateMinSellPrice(stockVirtual, entity);
        }
    }

    private void updateMinSellPrice(StockInventory stockVirtual, StockInventory entity){
        entity.setMinSellPrice(stockVirtual.getMinSellPrice());
        stockInventoryDao.save(entity);
    }

    private void updateReleaseNumber(StockInventory stockVirtual, StockInventory entity) throws ApplicationException {
        // 释放数量
        BigDecimal newReleaseNumber = stockVirtual.getReleaseNumber();
        // 销售指导价
        BigDecimal minSellPrice = stockVirtual.getMinSellPrice();
        // 指定销售业务员
        Long sellMatchUserId = stockVirtual.getSellMatchUserId();
        String sellMatchUserName = stockVirtual.getSellMatchUserName();
        BigDecimal dealNumber = entity.getDealNumber();
        BigDecimal releaseNumber = entity.getReleaseNumber();
        BigDecimal remainingNumber = dealNumber.subtract(releaseNumber).subtract(newReleaseNumber);
        if (remainingNumber.compareTo(BigDecimal.ZERO) < 0) {
            throw new ApplicationException("释放数量有误，最多可释放数量：" + dealNumber.subtract(releaseNumber));
        } else {
            List<StockVirtual> virtualList = stockVirtualDao.findAllBizStockVirtual(stockVirtual.getId(), BasConstants.STOCK_VIRTUAL_KC);
            int suffix = CollectionUtils.isNotEmpty(virtualList) ? virtualList.size() + 1 : 1;
            // 拆分虚拟报价库存
            String stockVirtualNo = entity.getStockVirtualNo();
            Long buyOurCompanyId = entity.getBuyOurCompanyId();
            String buyOurCompanyName = entity.getBuyOurCompanyName();
            BsCompany buyCompany = bsCompanyService.getEntity(buyOurCompanyId);
            BigDecimal dealPrice = entity.getDealPrice();
            BigDecimal raisePrice = Objects.isNull(entity.getRaisePrice()) ? BigDecimal.ZERO : entity.getRaisePrice();
            StockVirtual newStockVirtual = new StockVirtual();
            BeanUtils.copyProperties(entity, newStockVirtual);
            newStockVirtual.setId(0L);
            newStockVirtual.setDealNumber(newReleaseNumber);
            newStockVirtual.setVirtualBuyType(BasConstants.STOCK_VIRTUAL_KC);
            newStockVirtual.setDealPrice(StringUtils.isNotBlank(buyOurCompanyName) ? dealPrice.add(raisePrice) : dealPrice);
            newStockVirtual.setCompanyId(Objects.nonNull(buyCompany) ? buyCompany.getId() : entity.getCompanyId() );
            newStockVirtual.setCompanyName(Objects.nonNull(buyCompany) ? buyCompany.getCompanyName() : entity.getCompanyName());
            newStockVirtual.setTotalAmount(newReleaseNumber.multiply(newStockVirtual.getDealPrice()).setScale(2, RoundingMode.HALF_UP));
            newStockVirtual.setStockVirtualNo(stockVirtualNo + "-" + suffix);
            newStockVirtual.setVirtualStatus(BasConstants.STOCK_VIRTUAL_STATUS_N);
            newStockVirtual.setMinSellPrice(minSellPrice);
            newStockVirtual.setSellMatchUserId(sellMatchUserId);
            newStockVirtual.setSellMatchUserName(sellMatchUserName);
            newStockVirtual.setPublishTime(new Date());
            newStockVirtual.setBizApplyVirtualId(stockVirtual.getId());
            stockVirtualDao.save(newStockVirtual);

            boolean fishFlg = remainingNumber.compareTo(BigDecimal.ZERO) == 0;
            entity.setInventoryStatus(fishFlg ? BasConstants.STOCK_VIRTUAL_STATUS_Y : BasConstants.STOCK_VIRTUAL_STATUS_F);
            entity.setStockVirtualNo(stockVirtualNo);
            entity.setReleaseNumber(entity.getReleaseNumber().add(newReleaseNumber));
            stockInventoryDao.save(entity);
        }
    }

    @Override
    public Page<StockInventory> findInventoryVirtualPage(PageSearchVo searchVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "productCd","brandNumber","factoryName");
        Specification<StockInventory> spec = WebUtil.buildSpecification(searchVo.getSearchParams());
        // 判断库存数量大于0 ，采购数量-释放数量
        Specification<StockInventory> specPayN = new Specification<StockInventory>() {
            private static final long serialVersionUID = 1L;
            @Override
            public javax.persistence.criteria.Predicate toPredicate(Root<StockInventory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 计算 dealNumber 和 releaseNumber 的差值
                Expression<BigDecimal> dealNumber = root.get("dealNumber");
                Expression<BigDecimal> releaseNumber = root.get("releaseNumber");
                // 处理 releaseNumber 为 null 的情况
                Expression<BigDecimal> safeReleaseNumber = cb.coalesce(releaseNumber, BigDecimal.ZERO);
                // 计算 dealNumber 和 safeReleaseNumber 的差值
                Expression<BigDecimal> difference = cb.diff(dealNumber, safeReleaseNumber);
                return cb.gt(difference, BigDecimal.ZERO);
            }
        };
        spec = Specification.where(spec).and(specPayN);
        PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows(), sort);
        Page<StockInventory> page = getBaseDao().findAll(spec, pageRequest);
        return page;
    }
}
