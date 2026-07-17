package com.spt.bas.server.service.impl;

import com.google.common.base.Splitter;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.StockContract;
import com.spt.bas.client.vo.CtrProductSearchVo;
import com.spt.bas.client.vo.CtrProductVo;
import com.spt.bas.server.dao.CtrProductDao;
import com.spt.bas.server.dao.StockContractDao;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.service.ICtrProductService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class CtrProductServiceImpl extends BaseService<CtrProduct> implements ICtrProductService {
    @Autowired
    private CtrProductDao ctrProductDao;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private StockContractDao stockContractDao;

    @Override
    public BaseDao<CtrProduct> getBaseDao() {
        return ctrProductDao;
    }

    @Override
    public Class<CtrProduct> getEntityClazz() {
        return CtrProduct.class;
    }

	/*@Override
	public void updateNumberOfIn(ApplyDeliveryInVo vo) {
		List<CtrProduct> lstUpdate = vo.getLstUpdate();
		for (CtrProduct ctrProduct : lstUpdate) {
			ctrProduct.setRealInNumber(ctrProduct.getRealInNumber().add(ctrProduct.getCurInNumber()));
			ctrProduct.setCurInNumber(BigDecimal.ZERO);
			ctrProductDao.save(ctrProduct);
		}

	}*/


    public List<CtrProduct> findByContractId(Long contractId) {
        return ctrProductDao.findByCtrContractId(contractId);
    }

    @Override
    public List<CtrProduct> findByOutCtrContractId(Long ctrContractId) {
        return ctrProductDao.findByOutCtrContractId(ctrContractId);
    }

    @Override
    public List<CtrProduct> findEntityByParam(Map<String, Object> queryParams) {
        Specification<CtrProduct> spec = WebUtil.buildSpecification(queryParams);
        List<CtrProduct> productDetailList = this.ctrProductDao.findAll(spec);
        return productDetailList;
    }

    @Override
    public Page<CtrProductVo> findProductList(CtrProductSearchVo searchVo) {
        Long ctrContractId = searchVo.getCtrContractId();
        if (ctrContractId != null) {
            List<CtrProductVo> newList = new ArrayList<>();
            List<CtrProduct> productList = ctrProductDao.findByCtrContractId(ctrContractId);
            CtrContract entity = ctrContractService.getEntity(ctrContractId);
            BigDecimal dealedAmount = entity.getDealedAmount();
            //根据收款金额按比例计算最大可出库数量
            for (CtrProduct ctrProduct : productList) {
                BigDecimal dealNumber = ctrProduct.getDealNumber();
                BigDecimal curApproveNumber = ctrProduct.getCurApproveNumber();
                BigDecimal warehouseNumber = ctrProduct.getWarehouseNumber();
                BigDecimal dealPrice = ctrProduct.getDealPrice();
                BigDecimal maxCurNumber = BigDecimal.ZERO;
                CtrProductVo vo = new CtrProductVo();
                BeanUtils.copyProperties(ctrProduct, vo);
//                if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP,entity.getBusinessType())) {
////                    maxCurNumber = dealNumber.subtract(curApproveNumber).subtract(warehouseNumber);
//                    BigDecimal realTotalAmount = entity.getTotalAmount().subtract(entity.getApproveTpInterest());
//                    if (dealedAmount.compareTo(BigDecimal.ZERO) > 0 && dealedAmount.compareTo(entity.getTotalAmount().subtract(entity.getApproveTpInterest())) < 0) {
//                        //本条明细货物总值 总数量*单价
//                        BigDecimal proTotalAmount = dealNumber.multiply(ctrProduct.getDealPrice()).subtract(entity.getApproveTpInterest());
//                        // 10%定金
//                        BigDecimal bondAmount = proTotalAmount.multiply(new BigDecimal("0.1"));
//                        // 可用提货货款 （已收）
//                        BigDecimal amount = dealedAmount.subtract(bondAmount).divide(new BigDecimal("0.9"), 6, BigDecimal.ROUND_DOWN);
//                        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
//                            maxCurNumber = BigDecimal.ZERO;
//                        } else {
//                            // 已付金额/本条明细货物总值 * 总数量 - 当前审批数量 - 已出库数量
//                            maxCurNumber = amount.divide(realTotalAmount, 4, BigDecimal.ROUND_DOWN)
//                                    .multiply(dealNumber).setScale(2, BigDecimal.ROUND_DOWN)
//                                    .subtract(curApproveNumber)
//                                    .subtract(warehouseNumber);
//                        }
//                        //2.全部付款
//                    } else if (dealedAmount.compareTo(entity.getTotalAmount().subtract(entity.getApproveTpInterest())) >= 0) {
//                        maxCurNumber = dealNumber.subtract(curApproveNumber).subtract(warehouseNumber);
//                    }
//                } else {
//                    maxCurNumber = dealNumber.subtract(curApproveNumber).subtract(warehouseNumber);
//                }
                maxCurNumber = dealNumber.subtract(curApproveNumber).subtract(warehouseNumber);
                vo.setMaxCurNumber(maxCurNumber);
                //查询对应采购合同对方企业ID
                String linkContractId = entity.getLinkContractId();
                if (StringUtils.isNotBlank(linkContractId)) {
                    List<String> sellIdList = Splitter.on(",").omitEmptyStrings().splitToList(linkContractId);
                    List<Long> buyContractList = sellIdList.stream().map(a -> Long.valueOf(a)).collect(Collectors.toList());
                    if (buyContractList != null && buyContractList.size() > 0) {
                        Long buyContractId = buyContractList.get(0);
                        CtrContract buyContract = ctrContractService.getEntity(buyContractId);
                        vo.setBuyCompanyId(buyContract.getCompanyId());
                    }
                }
                newList.add(vo);
            }
            PageRequest page = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
            Page<CtrProductVo> pageVo = new PageImpl<>(newList, page, newList.size());
            return pageVo;
        }
        return null;
    }

    /**
     * 确认收货商品详细
     *
     * @param searchVo
     * @return
     */
    @Override
    public Page<CtrProductVo> findConfirmProductList(CtrProductSearchVo searchVo) {
        Long ctrContractId = searchVo.getCtrContractId();
        if (ctrContractId != null) {
            List<CtrProductVo> newList = new ArrayList<>();
            List<CtrProduct> productList = ctrProductDao.findByCtrContractId(ctrContractId);
            CtrContract entity = ctrContractService.getEntity(ctrContractId);
            //根据收款金额按比例计算最大可出库数量
            for (CtrProduct ctrProduct : productList) {
                // 已确认收货数量
                BigDecimal confirmReceiptNumber = ctrProduct.getConfirmReceiptNumber() == null ? BigDecimal.ZERO : ctrProduct.getConfirmReceiptNumber();
                // 当前审批中的带确认收货数量
                BigDecimal curConfirmReceiptNumber = ctrProduct.getCurConfirmReceiptNumber() == null ? BigDecimal.ZERO : ctrProduct.getCurConfirmReceiptNumber();

                BigDecimal warehouseNumber = ctrProduct.getWarehouseNumber();
                BigDecimal maxCurNumber;
                CtrProductVo vo = new CtrProductVo();
                BeanUtils.copyProperties(ctrProduct, vo);
                maxCurNumber = warehouseNumber.subtract(confirmReceiptNumber).subtract(curConfirmReceiptNumber);
                vo.setMaxCurNumber(maxCurNumber);
                newList.add(vo);
            }
            PageRequest page = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
            Page<CtrProductVo> pageVo = new PageImpl<>(newList, page, newList.size());
            return pageVo;
        }
        return null;
    }

    @Override
    public List<CtrProductVo> findList(PageSearchVo pageSearchVo) throws Exception {
        Page<CtrProduct> page = this.findPage(pageSearchVo);
        List<CtrProductVo> voList = new ArrayList<>();
        if (page != null) {
            List<CtrProduct> ctrProducts = page.getContent();
            for (CtrProduct ctrProduct : ctrProducts) {
                CtrProductVo vo = new CtrProductVo();
                BeanUtils.copyProperties(ctrProduct, vo);
                StockContract stockContract = stockContractDao.findByBuyProductId(ctrProduct.getId());
                vo.setStockContractId(Objects.nonNull(stockContract) ? stockContract.getId() : null);
                voList.add(vo);
            }
        }
        return voList;
    }

    /**
     * 入库时 无库存情况
     *
     * @param pageSearchVo
     * @return
     * @throws Exception
     */
    @Override
    public List<CtrProductVo> findListWithNoStock(PageSearchVo pageSearchVo) throws Exception {
        Page<CtrProduct> page = this.findPage(pageSearchVo);
        List<CtrProductVo> voList = new ArrayList<>();
        if (page != null) {
            List<CtrProduct> ctrProducts = page.getContent();
            for (CtrProduct ctrProduct : ctrProducts) {
                CtrProductVo vo = new CtrProductVo();
                BeanUtils.copyProperties(ctrProduct, vo);
                voList.add(vo);
            }
        }
        return voList;
    }

    @Override
    public Date findMinDeliveryDateByProductId(List<Long> productList) {
        if (productList != null && !productList.isEmpty()) {
            Object result = ctrProductDao.findMinDeliveryDateByProductId(productList);
            if (result != null) {
                Date maxDeliveryDate = (Date) result;
                return maxDeliveryDate;
            }
        }
        return null;
    }

    @Override
    public BigDecimal getNearPrice(String productCd, Long enterpriseId, String contractType) {
        List<CtrProduct> list = ctrProductDao.findByProductCd(productCd, enterpriseId, contractType);
        if (list.isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            CtrProduct ctrProduct = list.get(0);
            return ctrProduct.getDealPrice();
        }
    }

}
